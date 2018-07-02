package vanetsim.map;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CyclicBarrier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.MapSizeDialog;
import vanetsim.gui.helpers.MouseClickManager;
import vanetsim.localization.Messages;
import vanetsim.routing.A_Star.A_Star_LookupTableFactory;
import vanetsim.scenario.Scenario;
import vanetsim.scenario.Vehicle;
 
import vanetsim.scenario.events.EventSpot;

public final class Map {

    private static final Map INSTANCE = new Map();

    public static final int LANE_WIDTH = 300;

    private int width_ = 0;

    private int height_ = 0;

    private int regionWidth_ = 0;

    private int regionHeight_ = 0;

    private int regionCountX_ = 0;

    private int regionCountY_ = 0;

    private Region[][] regions_ = null;

    private boolean ready_ = true;

    private ArrayList<Node> amenityList_ = new ArrayList<Node>();

    private String mapName_ = "";

    private Map() {
    }

    public static Map getInstance() {
        return INSTANCE;
    }

    public void initNewMap(int width, int height, int regionWidth, int regionHeight) {
        int i, j;
        if (ready_ == true) {
            ready_ = false;
            if (!Renderer.getInstance().isConsoleStart()) {
                Scenario.getInstance().initNewScenario();
                Scenario.getInstance().setReadyState(true);
            }
            A_Star_LookupTableFactory.clear();
            Node.resetNodeID();
            width_ = width;
            height_ = height;
            regionWidth_ = regionWidth;
            regionHeight_ = regionHeight;
            Renderer.getInstance().setMarkedStreet(null);
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().setAttackerVehicle(null);
            Renderer.getInstance().setAttackedVehicle(null);
            if (!Renderer.getInstance().isConsoleStart())
                MouseClickManager.getInstance().cleanMarkings();
            regionCountX_ = width_ / regionWidth_;
            if (width_ % regionWidth_ > 0)
                ++regionCountX_;
            regionCountY_ = height_ / regionHeight_;
            if (height_ % regionHeight_ > 0)
                ++regionCountY_;
            regions_ = new Region[regionCountX_][regionCountY_];
            int upperboundary = 0, leftboundary = 0;
            for (i = 0; i < regionCountX_; ++i) {
                for (j = 0; j < regionCountY_; ++j) {
                    regions_[i][j] = new Region(i, j, leftboundary, leftboundary + regionWidth_ - 1, upperboundary, upperboundary + regionHeight_ - 1);
                    upperboundary += regionHeight_;
                }
                leftboundary += regionWidth_;
                upperboundary = 0;
            }
            Vehicle.setRegions(regions_);
            EventSpot.setRegions_(regions_);
           
        } else {
            ErrorLog.log(Messages.getString("Map.mapLocked"), 7, getClass().getName(), "initNewMap", null);
        }
    }

    public void signalMapLoaded() {
        for (int i = 0; i < regionCountX_; ++i) {
            for (int j = 0; j < regionCountY_; ++j) {
                regions_[i][j].calculateJunctions();
            }
        }
        ready_ = true;
        if (!Renderer.getInstance().isConsoleStart()) {
            Renderer.getInstance().setMiddle(width_ / 2, height_ / 2);
            Renderer.getInstance().setMapZoom(Math.exp(5 / 100.0) / 1000);
            Renderer.getInstance().ReRender(true, false);
        }
        Runnable job = new Runnable() {

            public void run() {
                for (int i = 0; i < regionCountX_; ++i) {
                    for (int j = 0; j < regionCountY_; ++j) {
                        regions_[i][j].checkStreetsForBridges();
                    }
                }
            }
        };
        Thread t = new Thread(job);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public void load(File file, boolean zip) {
        try {
            if (!Renderer.getInstance().isConsoleStart())
                VanetSimStart.setProgressBar(true);
            String childtype, setting, streetName, streetType, trafficSignalException, amenity = "";
            int x = 0, y = 0, maxSpeed, isOneway, lanes, newMapWidth, newMapHeight, newRegionWidth, newRegionHeight;
            Color displayColor;
            boolean xSet, ySet, trafficSignal;
            Node startNode, endNode;
            SMInputCursor childCrsr, nodeCrsr, settingsCrsr, streetCrsr, streetCrsr2, amenityCrsr, amenityCrsr2;
            XMLInputFactory factory = XMLInputFactory.newInstance();
            mapName_ = file.getName();
            ErrorLog.log(Messages.getString("Map.loadingMap") + file.getName(), 3, getClass().getName(), "loadMap", null);
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            InputStream filestream;
            if (zip) {
                filestream = new ZipInputStream(new FileInputStream(file));
                ((ZipInputStream) filestream).getNextEntry();
            } else
                filestream = new FileInputStream(file);
            XMLStreamReader sr = factory.createXMLStreamReader(filestream);
            SMInputCursor rootCrsr = SMInputFactory.rootElementCursor(sr);
            rootCrsr.getNext();
            if (rootCrsr.getLocalName().toLowerCase().equals("map")) {
                childCrsr = rootCrsr.childElementCursor();
                childCrsr.getNext();
                if (childCrsr.getLocalName().toLowerCase().equals("settings")) {
                    newMapWidth = 0;
                    newMapHeight = 0;
                    newRegionWidth = 0;
                    newRegionHeight = 0;
                    settingsCrsr = childCrsr.childElementCursor();
                    while (settingsCrsr.getNext() != null) {
                        setting = settingsCrsr.getLocalName().toLowerCase();
                        if (setting.equals("map_height")) {
                            try {
                                newMapHeight = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                            } catch (Exception e) {
                            }
                        } else if (setting.equals("map_width")) {
                            try {
                                newMapWidth = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                            } catch (Exception e) {
                            }
                        } else if (setting.equals("region_height")) {
                            try {
                                newRegionWidth = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                            } catch (Exception e) {
                            }
                        } else if (setting.equals("region_width")) {
                            try {
                                newRegionHeight = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (newMapWidth > 0 && newMapHeight > 0 && newRegionWidth > 0 && newRegionHeight > 0) {
                        if (!Renderer.getInstance().isConsoleStart())
                            VanetSimStart.setProgressBar(false);
                        CyclicBarrier barrier = new CyclicBarrier(2);
                        if (!Renderer.getInstance().isConsoleStart()) {
                            new MapSizeDialog(newMapWidth, newMapHeight, newRegionWidth, newRegionHeight, barrier);
                        } else
                            Map.getInstance().initNewMap(newMapWidth, newMapHeight, newRegionWidth, newRegionHeight);
                        int addX = (width_ - newMapWidth) / 2;
                        int addY = (height_ - newMapHeight) / 2;
                        if (!Renderer.getInstance().isConsoleStart())
                            VanetSimStart.setProgressBar(true);
                        while (childCrsr.getNext() != null) {
                            if (childCrsr.getLocalName().toLowerCase().equals("streets")) {
                                streetCrsr = childCrsr.childElementCursor();
                                while (streetCrsr.getNext() != null) {
                                    if (streetCrsr.getLocalName().toLowerCase().equals("street")) {
                                        streetName = "";
                                        startNode = null;
                                        endNode = null;
                                        trafficSignal = false;
                                        streetType = "unkown";
                                        lanes = 0;
                                        isOneway = 0;
                                        maxSpeed = 0;
                                        trafficSignalException = "";
                                        displayColor = null;
                                        streetCrsr2 = streetCrsr.childElementCursor();
                                        while (streetCrsr2.getNext() != null) {
                                            childtype = streetCrsr2.getLocalName().toLowerCase();
                                            if (childtype.equals("name")) {
                                                streetName = streetCrsr2.collectDescendantText(true);
                                            } else if (childtype.equals("startnode") || childtype.equals("endnode")) {
                                                xSet = false;
                                                ySet = false;
                                                nodeCrsr = streetCrsr2.childElementCursor();
                                                while (nodeCrsr.getNext() != null) {
                                                    if (nodeCrsr.getLocalName().toLowerCase().equals("x")) {
                                                        try {
                                                            x = Integer.parseInt(nodeCrsr.collectDescendantText(false)) + addX;
                                                            xSet = true;
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (nodeCrsr.getLocalName().toLowerCase().equals("y")) {
                                                        try {
                                                            y = Integer.parseInt(nodeCrsr.collectDescendantText(false)) + addY;
                                                            ySet = true;
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (nodeCrsr.getLocalName().toLowerCase().equals("trafficsignal")) {
                                                        if (nodeCrsr.collectDescendantText(false).toLowerCase().equals("true"))
                                                            trafficSignal = true;
                                                        else
                                                            trafficSignal = false;
                                                    } else if (nodeCrsr.getLocalName().toLowerCase().equals("trafficsignalexceptions")) {
                                                        trafficSignalException = nodeCrsr.collectDescendantText(true);
                                                    }
                                                }
                                                if (xSet && ySet) {
                                                    if (childtype.equals("startnode")) {
                                                        startNode = new Node(x, y, trafficSignal);
                                                        if (!trafficSignalException.equals(""))
                                                            startNode.addSignalExceptionsOfString(trafficSignalException);
                                                    } else {
                                                        endNode = new Node(x, y, trafficSignal);
                                                        if (!trafficSignalException.equals(""))
                                                            endNode.addSignalExceptionsOfString(trafficSignalException);
                                                    }
                                                }
                                            } else if (childtype.equals("oneway")) {
                                                if (streetCrsr2.collectDescendantText(false).toLowerCase().equals("true"))
                                                    isOneway = 1;
                                                else
                                                    isOneway = 0;
                                            } else if (childtype.equals("streettype")) {
                                                streetType = streetCrsr2.collectDescendantText(true);
                                            } else if (childtype.equals("lanes")) {
                                                try {
                                                    lanes = Integer.parseInt(streetCrsr2.collectDescendantText(false));
                                                } catch (Exception e) {
                                                }
                                            } else if (childtype.equals("speed")) {
                                                try {
                                                    maxSpeed = Integer.parseInt(streetCrsr2.collectDescendantText(false));
                                                } catch (Exception e) {
                                                }
                                            } else if (childtype.equals("color")) {
                                                try {
                                                    displayColor = new Color(Integer.parseInt(streetCrsr2.collectDescendantText(false)));
                                                } catch (Exception e) {
                                                }
                                            } else
                                                ErrorLog.log(Messages.getString("Map.unknownElement"), 3, getClass().getName(), "load", null);
                                        }
                                        if (maxSpeed > 0 && startNode != null && endNode != null && displayColor != null && !streetName.equals("")) {
                                            startNode = addNode(startNode);
                                            endNode = addNode(endNode);
                                            addStreet(new Street(streetName, startNode, endNode, streetType, isOneway, lanes, displayColor, getRegionOfPoint(startNode.getX(), startNode.getY()), maxSpeed));
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Map.unknownElementOnlyStreet"), 3, getClass().getName(), "load", null);
                                }
                            } else if (childCrsr.getLocalName().toLowerCase().equals("amenities")) {
                                amenityCrsr = childCrsr.childElementCursor();
                                while (amenityCrsr.getNext() != null) {
                                    if (amenityCrsr.getLocalName().toLowerCase().equals("amenity")) {
                                        x = 0;
                                        y = 0;
                                        amenity = "";
                                        amenityCrsr2 = amenityCrsr.childElementCursor();
                                        while (amenityCrsr2.getNext() != null) {
                                            childtype = amenityCrsr2.getLocalName().toLowerCase();
                                            if (childtype.equals("x")) {
                                                x = Integer.parseInt(amenityCrsr2.collectDescendantText(false));
                                            } else if (childtype.equals("y")) {
                                                y = Integer.parseInt(amenityCrsr2.collectDescendantText(false));
                                            } else if (childtype.equals("amenity")) {
                                                amenity = amenityCrsr2.collectDescendantText(true);
                                            } else
                                                ErrorLog.log(Messages.getString("Map.unknownElement"), 3, getClass().getName(), "load", null);
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Map.unknownElementOnlyAmenity"), 3, getClass().getName(), "load", null);
                                    addAmenityNode(new Node(x, y, false), amenity);
                                }
                            } else
                                ErrorLog.log(Messages.getString("Map.unknownElementOnlyAmenitiesOrStreets"), 3, getClass().getName(), "load", null);
                        }
                    } else
                        ErrorLog.log(Messages.getString("Map.settingsIncomplete"), 7, getClass().getName(), "load", null);
                } else
                    ErrorLog.log(Messages.getString("Map.settingsMissing"), 7, getClass().getName(), "load", null);
            } else
                ErrorLog.log(Messages.getString("Map.wrongRoot"), 7, getClass().getName(), "load", null);
            sr.close();
            filestream.close();
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("Map.errorLoading"), 7, getClass().getName(), "load", e);
        }
        if (!Renderer.getInstance().isConsoleStart())
            VanetSimStart.setProgressBar(false);
        signalMapLoaded();
        ErrorLog.log(Messages.getString("Map.loadingFinished"), 3, getClass().getName(), "load", null);
    }

    public void save(File file, boolean zip) {
        try {
            if (!Renderer.getInstance().isConsoleStart())
                VanetSimStart.setProgressBar(true);
            ErrorLog.log(Messages.getString("Map.savingMap") + file.getName(), 3, getClass().getName(), "save", null);
            int i, j, k;
            Street[] streetsArray;
            Street street;
            SMOutputElement level1, level2;
            OutputStream filestream;
            if (zip) {
                filestream = new ZipOutputStream(new FileOutputStream(file + ".zip"));
                ((ZipOutputStream) filestream).putNextEntry(new ZipEntry(file.getName()));
            } else
                filestream = new FileOutputStream(file);
            XMLStreamWriter xw = XMLOutputFactory.newInstance().createXMLStreamWriter(filestream);
            SMOutputDocument doc = SMOutputFactory.createOutputDocument(xw);
            doc.setIndentation("\n\t\t\t\t\t\t\t\t", 2, 1);
            ;
            doc.addComment("Generated on " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
            doc.addComment("This file may contain data from the OpenStreetMap project which is licensed under the Creative Commons Attribution-ShareAlike 2.0 license.");
            SMOutputElement root = doc.addElement("Map");
            level1 = root.addElement("Settings");
            level2 = level1.addElement("Map_height");
            level2.addValue(height_);
            level2 = level1.addElement("Map_width");
            level2.addValue(width_);
            level2 = level1.addElement("Region_height");
            level2.addValue(regionHeight_);
            level2 = level1.addElement("Region_width");
            level2.addValue(regionWidth_);
            SMOutputElement streets = root.addElement("Streets");
            for (i = 0; i < regionCountX_; ++i) {
                for (j = 0; j < regionCountY_; ++j) {
                    streetsArray = regions_[i][j].getStreets();
                    for (k = 0; k < streetsArray.length; ++k) {
                        street = streetsArray[k];
                        if (street.getMainRegion() == regions_[i][j]) {
                            level1 = streets.addElement("Street");
                            level1.addElement("Name").addCharacters(street.getName());
                            level2 = level1.addElement("StartNode");
                            level2.addElement("x").addValue(street.getStartNode().getX());
                            ;
                            level2.addElement("y").addValue(street.getStartNode().getY());
                            if (street.getStartNode().isHasTrafficSignal_()) {
                                level2.addElement("trafficSignal").addCharacters("true");
                                if (street.getStartNode().hasNonDefaultSettings())
                                    level2.addElement("TrafficSignalExceptions").addCharacters(street.getStartNode().getSignalExceptionsInString());
                            } else
                                level2.addElement("trafficSignal").addCharacters("false");
                            level2 = level1.addElement("EndNode");
                            level2.addElement("x").addValue(street.getEndNode().getX());
                            level2.addElement("y").addValue(street.getEndNode().getY());
                            if (street.getEndNode().isHasTrafficSignal_()) {
                                level2.addElement("trafficSignal").addCharacters("true");
                                if (street.getEndNode().hasNonDefaultSettings())
                                    level2.addElement("TrafficSignalExceptions").addCharacters(street.getEndNode().getSignalExceptionsInString());
                            } else
                                level2.addElement("trafficSignal").addCharacters("false");
                            if (street.isOneway())
                                level1.addElement("Oneway").addCharacters("true");
                            else
                                level1.addElement("Oneway").addCharacters("false");
                            level1.addElement("StreetType").addCharacters(street.getStreetType_());
                            level1.addElement("Lanes").addValue(street.getLanesCount());
                            level1.addElement("Speed").addValue(street.getSpeed());
                            level1.addElement("Color").addValue(street.getDisplayColor().getRGB());
                        }
                    }
                }
            }
            SMOutputElement amenities = root.addElement("Amenities");
            for (Node n : amenityList_) {
                level1 = amenities.addElement("Amenity");
                level1.addElement("x").addValue(n.getX());
                level1.addElement("y").addValue(n.getY());
                level1.addElement("amenity").addCharacters(n.getAmenity_());
            }
            doc.closeRoot();
            xw.close();
            filestream.close();
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("Map.errorSavingMap"), 6, getClass().getName(), "save", e);
        }
        if (!Renderer.getInstance().isConsoleStart())
            VanetSimStart.setProgressBar(false);
    }

    public Node addNode(Node node) {
        int regionX = node.getX() / regionWidth_;
        int regionY = node.getY() / regionHeight_;
        if (regionX >= regionCountX_)
            regionX = regionCountX_ - 1;
        else if (regionX < 0)
            regionX = 0;
        if (regionY >= regionCountY_)
            regionY = regionCountY_ - 1;
        else if (regionY < 0)
            regionY = 0;
        node.setRegion(regions_[regionX][regionY]);
        return regions_[regionX][regionY].addNode(node, true);
    }

    public void delNode(Node node) {
        node.getRegion().delNode(node);
    }

 

 

    public void addVehicle(Vehicle vehicle) {
        int regionX = vehicle.getX() / regionWidth_;
        int regionY = vehicle.getY() / regionHeight_;
        if (regionX >= regionCountX_)
            regionX = regionCountX_ - 1;
        else if (regionX < 0)
            regionX = 0;
        if (regionY >= regionCountY_)
            regionY = regionCountY_ - 1;
        else if (regionY < 0)
            regionY = 0;
        vehicle.setRegion(regions_[regionX][regionY]);
        regions_[regionX][regionY].addVehicle(vehicle, false);
    }

    public void delVehicle(Vehicle vehicle) {
        int regionX = vehicle.getRegionX();
        int regionY = vehicle.getRegionY();
        regions_[regionX][regionY].delVehicle(vehicle);
    }

    public void addStreet(Street street) {
        int startRegionX = street.getStartNode().getRegion().getX();
        int startRegionY = street.getStartNode().getRegion().getY();
        int endRegionX = street.getEndNode().getRegion().getX();
        int endRegionY = street.getEndNode().getRegion().getY();
        int i;
        if (startRegionX == endRegionX) {
            if (startRegionY == endRegionY)
                regions_[startRegionX][startRegionY].addStreet(street, true);
            else {
                if (startRegionY < endRegionY) {
                    for (i = startRegionY; i <= endRegionY; ++i) regions_[startRegionX][i].addStreet(street, true);
                } else {
                    for (i = endRegionY; i <= startRegionY; ++i) regions_[startRegionX][i].addStreet(street, true);
                }
            }
        } else if (startRegionY == endRegionY) {
            if (startRegionX < endRegionX) {
                for (i = startRegionX; i <= endRegionX; ++i) regions_[i][startRegionY].addStreet(street, true);
            } else {
                for (i = endRegionX; i <= startRegionX; ++i) regions_[i][startRegionY].addStreet(street, true);
            }
        } else {
            int start_x = street.getStartNode().getX();
            int start_y = street.getStartNode().getY();
            int end_x = street.getEndNode().getX();
            int end_y = street.getEndNode().getY();
            regions_[startRegionX][startRegionY].addStreet(street, true);
            regions_[endRegionX][endRegionY].addStreet(street, true);
            double a = ((double) start_y - end_y) / ((double) start_x - end_x);
            double b = start_y - a * start_x;
            double x, y;
            long tmp;
            int max_x = Math.max(endRegionX, startRegionX);
            int max_y = Math.max(startRegionY, endRegionY);
            for (i = Math.min(startRegionX, endRegionX); i < max_x; ++i) {
                y = a * (i * regionWidth_) + b;
                tmp = Math.round(y) / regionHeight_;
                if (tmp > -1 && tmp < regionCountY_)
                    regions_[i][(int) tmp].addStreet(street, true);
                y = a * ((i * regionWidth_) + regionWidth_ - 1) + b;
                tmp = Math.round(y) / regionHeight_;
                if (tmp > -1 && tmp < regionCountY_)
                    regions_[i][(int) tmp].addStreet(street, true);
            }
            for (i = Math.min(startRegionY, endRegionY); i < max_y; ++i) {
                x = ((i * regionHeight_) - b) / a;
                tmp = Math.round(x) / regionWidth_;
                if (tmp > -1 && tmp < regionCountX_)
                    regions_[(int) tmp][i].addStreet(street, true);
                x = (((i * regionHeight_) + regionHeight_ - 1) - b) / a;
                tmp = Math.round(x) / regionWidth_;
                if (tmp > -1 && tmp < regionCountX_)
                    regions_[(int) tmp][i].addStreet(street, true);
            }
        }
    }

    public void delStreet(Street street) {
        int startRegionX = street.getStartNode().getRegion().getX();
        int startRegionY = street.getStartNode().getRegion().getY();
        int endRegionX = street.getEndNode().getRegion().getX();
        int endRegionY = street.getEndNode().getRegion().getY();
        int i;
        if (startRegionX == endRegionX) {
            if (startRegionY == endRegionY)
                regions_[startRegionX][startRegionY].delStreet(street);
            else {
                if (startRegionY < endRegionY) {
                    for (i = startRegionY; i <= endRegionY; ++i) regions_[startRegionX][i].delStreet(street);
                } else {
                    for (i = endRegionY; i <= startRegionY; ++i) regions_[startRegionX][i].delStreet(street);
                }
            }
        } else if (startRegionY == endRegionY) {
            if (startRegionX < endRegionX) {
                for (i = startRegionX; i <= endRegionX; ++i) regions_[i][startRegionY].delStreet(street);
            } else {
                for (i = endRegionX; i <= startRegionX; ++i) regions_[i][startRegionY].delStreet(street);
            }
        } else {
            int start_x = street.getStartNode().getX();
            int start_y = street.getStartNode().getY();
            int end_x = street.getEndNode().getX();
            int end_y = street.getEndNode().getY();
            regions_[startRegionX][startRegionY].delStreet(street);
            regions_[endRegionX][endRegionY].delStreet(street);
            double a = ((double) start_y - end_y) / ((double) start_x - end_x);
            double b = start_y - a * start_x;
            double x, y;
            long tmp;
            int max_x = Math.max(endRegionX, startRegionX);
            int max_y = Math.max(startRegionY, endRegionY);
            for (i = Math.min(startRegionX, endRegionX); i < max_x; ++i) {
                y = a * (i * regionWidth_) + b;
                tmp = Math.round(y) / regionHeight_;
                if (tmp > -1 && tmp < regionCountY_)
                    regions_[i][(int) tmp].delStreet(street);
                y = a * ((i * regionWidth_) + regionWidth_ - 1) + b;
                tmp = Math.round(y) / regionHeight_;
                if (tmp > -1 && tmp < regionCountY_)
                    regions_[i][(int) tmp].delStreet(street);
            }
            for (i = Math.min(startRegionY, endRegionY); i < max_y; ++i) {
                x = ((i * regionHeight_) - b) / a;
                tmp = Math.round(x) / regionWidth_;
                if (tmp > -1 && tmp < regionCountX_)
                    regions_[(int) tmp][i].delStreet(street);
                x = (((i * regionHeight_) + regionHeight_ - 1) - b) / a;
                tmp = Math.round(x) / regionWidth_;
                if (tmp > -1 && tmp < regionCountX_)
                    regions_[(int) tmp][i].delStreet(street);
            }
        }
    }

    public void addMixZone(Node node, int radius) {
        int regionX = node.getX() / regionWidth_;
        int regionY = node.getY() / regionHeight_;
        regions_[regionX][regionY].addMixZone(node, radius);
    }

    public void deleteMixZone(Node node) {
        int regionX = node.getX() / regionWidth_;
        int regionY = node.getY() / regionHeight_;
        regions_[regionX][regionY].deleteMixZone(node);
    }

    public void clearMixZones() {
        for (int i = 0; i < regionCountX_; ++i) for (int j = 0; j < regionCountY_; ++j) regions_[i][j].clearMixZones();
    }

 

    public void clearVehicles() {
        Renderer.getInstance().setMarkedVehicle(null);
        for (int i = 0; i < regionCountX_; ++i) for (int j = 0; j < regionCountY_; ++j) regions_[i][j].cleanVehicles();
    }

    @SuppressWarnings("unchecked")
    public void autoTrimMap(int minX, int minY, int maxX, int maxY) {
        Node[] tmpNodes = null;
        Street[] tmpStreets = null;
        boolean autoTrim = false;
        if (minX == -1 && minY == -1 && maxX == -1 && maxY == -1)
            autoTrim = true;
        if (autoTrim) {
            minX = width_;
            minY = height_;
            maxX = 0;
            maxY = 0;
            int tmpX = 0;
            int tmpY = 0;
            for (int i = 0; i < regions_.length; i++) {
                for (int j = 0; j < regions_[i].length; j++) {
                    tmpNodes = regions_[i][j].getNodes();
                    for (int k = 0; k < tmpNodes.length; k++) {
                        tmpX = tmpNodes[k].getX();
                        tmpY = tmpNodes[k].getY();
                        if (tmpX < minX)
                            minX = tmpX;
                        if (tmpY < minY)
                            minY = tmpY;
                        if (tmpX > maxX)
                            maxX = tmpX;
                        if (tmpY > maxY)
                            maxY = tmpY;
                    }
                }
            }
        } else {
            Node tmpStartNode = null;
            Node tmpEndNode = null;
            for (int i = 0; i < regions_.length; i++) {
                for (int j = 0; j < regions_[i].length; j++) {
                    tmpStreets = regions_[i][j].getStreets();
                    for (int k = 0; k < tmpStreets.length; k++) {
                        tmpStartNode = tmpStreets[k].getStartNode();
                        tmpEndNode = tmpStreets[k].getEndNode();
                        if (((tmpStartNode.getX() < minX || tmpStartNode.getY() < minY) && (tmpEndNode.getX() < minX || tmpEndNode.getY() < minY)) || ((tmpStartNode.getX() > maxX || tmpStartNode.getY() > maxY) && (tmpEndNode.getX() > maxX || tmpEndNode.getY() > maxY))) {
                            tmpStreets[k].getStartNode().delOutgoingStreet(tmpStreets[k]);
                            tmpStreets[k].getStartNode().delCrossingStreet(tmpStreets[k]);
                            if (tmpStreets[k].getStartNode().getCrossingStreetsCount() == 0)
                                Map.getInstance().delNode(tmpStreets[k].getStartNode());
                            tmpStreets[k].getEndNode().delOutgoingStreet(tmpStreets[k]);
                            tmpStreets[k].getEndNode().delCrossingStreet(tmpStreets[k]);
                            if (tmpStreets[k].getEndNode().getCrossingStreetsCount() == 0)
                                Map.getInstance().delNode(tmpStreets[k].getEndNode());
                            Map.getInstance().delStreet(tmpStreets[k]);
                        } else if (tmpStartNode.getY() < minY || tmpStartNode.getY() > maxY || tmpEndNode.getY() < minY || tmpEndNode.getY() > maxY || tmpStartNode.getX() < minX || tmpEndNode.getX() < minX || tmpStartNode.getX() > maxX || tmpEndNode.getX() > maxX) {
                            int tmpBorder;
                            Node tmp = null;
                            if (tmpStartNode.getY() < minY || tmpEndNode.getY() < minY)
                                tmpBorder = minY;
                            else if (tmpStartNode.getY() > maxY || tmpEndNode.getY() > maxY)
                                tmpBorder = maxY;
                            else if (tmpStartNode.getX() < minX || tmpEndNode.getX() < minX)
                                tmpBorder = minX;
                            else
                                tmpBorder = maxX;
                            if (tmpEndNode.getY() < minY || tmpEndNode.getY() > maxY || tmpEndNode.getX() < minX || tmpEndNode.getX() > maxX) {
                                tmp = tmpStartNode;
                                tmpStartNode = tmpEndNode;
                                tmpEndNode = tmp;
                            }
                            Node tmpNode = new Node(tmpStartNode.getX(), tmpStartNode.getY());
                            tmpNode.addOutgoingStreet(tmpStreets[k]);
                            tmpNode.addCrossingStreet(tmpStreets[k]);
                            tmpStartNode.delCrossingStreet(tmpStreets[k]);
                            tmpStartNode.delOutgoingStreet(tmpStreets[k]);
                            regions_[i][j].delNode(tmpStartNode);
                            regions_[i][j].addNode(tmpNode, false);
                            tmpStartNode = tmpNode;
                            if (tmp == null)
                                tmpStreets[k].setStartNode(tmpStartNode);
                            else
                                tmpStreets[k].setEndNode(tmpStartNode);
                            if (tmpStartNode.getX() < minX || tmpEndNode.getX() < minX || tmpStartNode.getX() > maxX || tmpEndNode.getX() > maxX) {
                                tmpStartNode.setY(trimStreet(tmpEndNode.getY(), tmpEndNode.getX(), tmpStartNode.getY(), tmpStartNode.getX(), tmpBorder));
                                tmpStartNode.setX(tmpBorder);
                            } else {
                                tmpStartNode.setX(trimStreet(tmpEndNode.getX(), tmpEndNode.getY(), tmpStartNode.getX(), tmpStartNode.getY(), tmpBorder));
                                tmpStartNode.setY(tmpBorder);
                            }
                        }
                    }
                }
            }
        }
        width_ = maxX - minX;
        height_ = maxY - minY;
        for (int i = 0; i < regions_.length; i++) {
            for (int j = 0; j < regions_[i].length; j++) {
                tmpNodes = regions_[i][j].getNodes();
                for (int k = 0; k < tmpNodes.length; k++) {
                    tmpNodes[k].setX(tmpNodes[k].getX() - minX);
                    tmpNodes[k].setY(tmpNodes[k].getY() - minY);
                }
            }
        }
        ArrayList<Node> tmpList = (ArrayList<Node>) Map.getInstance().getAmenityList().clone();
        for (Node n : tmpList) {
            if ((n.getX() < minX || n.getY() < minY) || (n.getX() > maxX || n.getY() > maxY))
                Map.getInstance().getAmenityList().remove(n);
            else {
                n.setX(n.getX() - minX);
                n.setY(n.getY() - minY);
            }
        }
        Renderer.getInstance().ReRender(true, true);
        saveReloadMap();
    }

    public int trimStreet(int streetXInside, int streetYInside, int streetXOutside, int streetYOutside, int border) {
        int y, a, b, x;
        double f, fakt, c;
        if (streetXInside > streetXOutside)
            y = streetXInside - streetXOutside;
        else
            y = streetXOutside - streetXInside;
        if (streetYOutside > border) {
            a = border - streetYInside;
            b = streetYOutside - border;
        } else {
            a = streetYInside - border;
            b = border - streetYOutside;
        }
        f = Math.sqrt(y * y + (a + b) * (a + b));
        fakt = f / (a + b);
        c = b * fakt;
        x = (int) Math.round(Math.sqrt(c * c - b * b));
        if (streetXOutside > streetXInside)
            return streetXOutside - x;
        else
            return streetXOutside + x;
    }

    public void saveReloadMap() {
        VanetSimStart.getMainControlPanel().changeFileChooser(false, true, false);
        final JFileChooser filechooser = VanetSimStart.getMainControlPanel().getFileChooser();
        int returnVal = filechooser.showSaveDialog(VanetSimStart.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Runnable job = new Runnable() {

                public void run() {
                    File file = filechooser.getSelectedFile();
                    if (filechooser.getAcceptAllFileFilter() != filechooser.getFileFilter() && !file.getName().toLowerCase().endsWith(".xml"))
                        file = new File(file.getAbsolutePath() + ".xml");
                    
                    Map.getInstance().save(file, false);
                    Map.getInstance().load(file, false);
                }
            };
            new Thread(job).start();
        }
    }

    public void clearTrafficLights() {
        for (int i = 0; i < regionCountX_; ++i) for (int j = 0; j < regionCountY_; ++j) regions_[i][j].clearTrafficLights();
    }

    public void addAmenityNode(Node n, String value) {
        if (value.equals("school"))
            n.setNodeColor(Color.yellow);
        else if (value.equals("kindergarten"))
            n.setNodeColor(Color.green);
        else if (value.equals("hospital"))
            n.setNodeColor(Color.magenta);
        else if (value.equals("police"))
            n.setNodeColor(Color.blue);
        else if (value.equals("fire_station"))
            n.setNodeColor(Color.red);
        n.setAmenity_(value);
        amenityList_.add(n);
    }

    public int getMapWidth() {
        return width_;
    }

    public int getMapHeight() {
        return height_;
    }

    public Region getRegionOfPoint(int x, int y) {
        if (regionWidth_ > 0 && regionHeight_ > 0) {
            int region_x = x / regionWidth_;
            int region_y = y / regionHeight_;
            if (region_x < 0)
                region_x = 0;
            else if (region_x >= regionCountX_)
                region_x = regionCountX_ - 1;
            if (region_y < 0)
                region_y = 0;
            else if (region_y >= regionCountY_)
                region_y = regionCountY_ - 1;
            return regions_[region_x][region_y];
        } else
            return null;
    }

    public void writeSilentPeriodHeader() {
    }

    public Region[][] getRegions() {
        return regions_;
    }

    public int getRegionCountX() {
        return regionCountX_;
    }

    public int getRegionCountY() {
        return regionCountY_;
    }

    public boolean getReadyState() {
        return ready_;
    }

    public ArrayList<Node> getAmenityList() {
        return amenityList_;
    }

    public void setAmenityList(ArrayList<Node> amenityList) {
        amenityList_ = amenityList;
    }

    public String getMapName_() {
        return mapName_;
    }

    public void setMapName_(String mapName_) {
        this.mapName_ = mapName_;
    }

    public void printVehiclesPerRegion() {
        int counter = 0;
        for (int i = 0; i < regionCountY_; ++i) {
            for (int j = 0; j < regionCountX_; ++j) {
                System.out.print(regions_[j][i].getVehicleArrayList().size() + ",");
                counter++;
            }
            System.out.println();
        }
        System.out.println("Counter: " + counter);
    }

    public void saveVehicles() {
        FileWriter fstream;
        try {
            fstream = new FileWriter("vehicleCoords.log", true);
            BufferedWriter out = new BufferedWriter(fstream);
            for (int i = 0; i < regionCountY_; ++i) {
                for (int j = 0; j < regionCountX_; ++j) {
                    for (Vehicle vehicle : regions_[j][i].getVehicleArrayList()) out.write(vehicle.getX() + " " + vehicle.getY() + "\n");
                }
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
