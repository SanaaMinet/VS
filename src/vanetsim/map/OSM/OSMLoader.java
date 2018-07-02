package vanetsim.map.OSM;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayDeque;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.controlpanels.MapSizeDialog;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
import vanetsim.map.Street;

public final class OSMLoader {

    private static final OSMLoader INSTANCE = new OSMLoader();

    private static final double E_RADIUS = 6378137.0;

    private static final double ECC_SQUARED = 0.00669437999014;

    private static final double ECC2_SQUARED = 0.00673949674228;

    private OSMLoader() {
    }

    public static OSMLoader getInstance() {
        return INSTANCE;
    }

    public void loadOSM(File file) {
        Map map = Map.getInstance();
        try {
            VanetSimStart.setProgressBar(true);
            String childtype, waytype, nodetype, key, value, streetName, streetType;
            int i = 0, id, maxspeed, isOneway, lanes;
            double latitude, longitude, minLatitude = Double.MAX_VALUE, maxLatitude = -Double.MAX_VALUE, minLongitude = Double.MAX_VALUE, maxLongitude = -Double.MAX_VALUE;
            boolean maxspeedSet, onewaySet, isRoundabout, laneSet, correctionSet = false, hasTrafficSignal;
            Node lastNode, node;
            OSMNode tmpNode;
            HashMap<Integer, OSMNode> OSMNodes = new HashMap<Integer, OSMNode>();
            ArrayList<OSMNode> amenityNodes = new ArrayList<OSMNode>();
            ArrayDeque<Integer> wayPoints = new ArrayDeque<Integer>();
            Iterator<Integer> wayPointsIterator;
            Color displayColor;
            SMInputCursor wayCursor, nodeCursor;
            XMLInputFactory factory = XMLInputFactory.newInstance();
            ErrorLog.log(Messages.getString("OSM_Loader.loading") + file.getName(), 3, OSMLoader.class.getName(), "loadOSM", null);
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            FileInputStream filestream = new FileInputStream(file);
            XMLStreamReader sr = factory.createXMLStreamReader(filestream);
            SMInputCursor rootCrsr = SMInputFactory.rootElementCursor(sr);
            rootCrsr.getNext();
            if (rootCrsr.getLocalName().toLowerCase().equals("osm") && (rootCrsr.getAttrValue("version").equals("0.5") || rootCrsr.getAttrValue("version").equals("0.6"))) {
                SMInputCursor childCrsr = rootCrsr.childElementCursor();
                while (childCrsr.getNext() != null) {
                    childtype = childCrsr.getLocalName().toLowerCase();
                    if (childtype.equals("node")) {
                        if (correctionSet)
                            ErrorLog.log(Messages.getString("OSM_Loader.nodeAfterWay"), 7, Map.class.getName(), "loadOSM", null);
                        if (childCrsr.getAttrValue("visible") == null || !childCrsr.getAttrValue("visible").equals("false")) {
                            try {
                                id = Integer.parseInt(childCrsr.getAttrValue("id"));
                                latitude = Double.parseDouble(childCrsr.getAttrValue("lat"));
                                longitude = Double.parseDouble(childCrsr.getAttrValue("lon"));
                                if (latitude < minLatitude) {
                                    minLatitude = latitude;
                                }
                                if (latitude > maxLatitude) {
                                    maxLatitude = latitude;
                                }
                                if (longitude < minLongitude)
                                    minLongitude = longitude;
                                if (longitude > maxLongitude)
                                    maxLongitude = longitude;
                                hasTrafficSignal = false;
                                nodeCursor = childCrsr.childElementCursor();
                                while (nodeCursor.getNext() != null) {
                                    nodetype = nodeCursor.getLocalName().toLowerCase();
                                    if (nodetype.equals("tag")) {
                                        key = nodeCursor.getAttrValue("k").toLowerCase();
                                        value = nodeCursor.getAttrValue("v");
                                        if (key.equals("highway")) {
                                            if (value.equals("traffic_signals"))
                                                hasTrafficSignal = true;
                                        }
                                        if (key.equals("amenity")) {
                                            if (value.equals("school") || value.equals("hospital") || value.equals("police") || value.equals("fire_station") || value.equals("kindergarten"))
                                                amenityNodes.add(new OSMNode(latitude, longitude, value));
                                        }
                                    }
                                }
                                OSMNodes.put(id, new OSMNode(latitude, longitude, hasTrafficSignal));
                            } catch (Exception e) {
                                ErrorLog.log(Messages.getString("OSM_Loader.errorParsingNode"), 5, Map.class.getName(), "loadOSM", e);
                            }
                        }
                    } else if (childtype.equals("way")) {
                        if (childCrsr.getAttrValue("visible") == null || !childCrsr.getAttrValue("visible").equals("false")) {
                            try {
                                if (correctionSet == false) {
                                    correctionSet = true;
                                    double[] result1 = new double[2], result2 = new double[2], result3 = new double[2], result4 = new double[2], result5 = new double[2], result6 = new double[2];
                                    double longitudeMiddle = minLongitude + (maxLongitude - minLongitude) / 2;
                                    WGS84toUTM(result1, maxLongitude, minLatitude, false, longitudeMiddle, false);
                                    WGS84toUTM(result2, minLongitude, maxLatitude, false, longitudeMiddle, false);
                                    WGS84toUTM(result3, minLongitude, minLatitude, false, longitudeMiddle, false);
                                    WGS84toUTM(result4, maxLongitude, maxLatitude, false, longitudeMiddle, false);
                                    WGS84toUTM(result5, longitudeMiddle, minLatitude, false, longitudeMiddle, false);
                                    WGS84toUTM(result6, longitudeMiddle, maxLatitude, false, longitudeMiddle, false);
                                    double leftBound = Math.min(result2[0], result3[0]);
                                    double rightBound = Math.max(result1[0], result4[0]);
                                    double upperBound = Math.max(result2[1], result6[1]);
                                    double lowerBound = Math.min(result1[1], result5[1]);
                                    int width = (int) Math.round((rightBound - leftBound + 1000) * 100);
                                    int height = (int) Math.round((upperBound - lowerBound + 1000) * 100);
                                    VanetSimStart.setProgressBar(false);
                                    CyclicBarrier barrier = new CyclicBarrier(2);
                                    new MapSizeDialog(width, height, 100000, 100000, barrier);
                                    try {
                                        barrier.await();
                                    } catch (Exception e2) {
                                    }
                                    VanetSimStart.setProgressBar(true);
                                    int correctionX = (int) Math.round(leftBound - (Map.getInstance().getMapWidth() - width) / 200) - 500;
                                    int correctionY = (int) Math.round(upperBound + (Map.getInstance().getMapHeight() - height) / 200) + 500;
                                    OSMNode.setCorrections(longitudeMiddle, correctionX, correctionY);
                                }
                                streetName = "";
                                streetType = "unknown";
                                maxspeedSet = false;
                                onewaySet = false;
                                laneSet = false;
                                isOneway = 0;
                                isRoundabout = false;
                                lanes = 1;
                                displayColor = Color.WHITE;
                                maxspeed = -1;
                                wayPoints.clear();
                                id = Integer.parseInt(childCrsr.getAttrValue("id"));
                                wayCursor = childCrsr.childElementCursor();
                                while (wayCursor.getNext() != null) {
                                    waytype = wayCursor.getLocalName().toLowerCase();
                                    if (waytype.equals("nd")) {
                                        try {
                                            id = Integer.parseInt(wayCursor.getAttrValue("ref"));
                                            wayPoints.add(id);
                                        } catch (Exception e) {
                                        }
                                    } else if (waytype.equals("tag")) {
                                        key = wayCursor.getAttrValue("k").toLowerCase();
                                        value = wayCursor.getAttrValue("v");
                                        if ((streetName.equals("") && key.equals("ref")) || key.equals("name"))
                                            streetName = value;
                                        else if (key.equals("highway")) {
                                            streetType = value;
                                            if (value.equals("motorway")) {
                                                if (onewaySet == false)
                                                    isOneway = 1;
                                                if (laneSet == false)
                                                    lanes = 2;
                                                displayColor = new Color(117, 146, 185);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 130 * 100000 / 3600;
                                                }
                                            } else if (value.equals("motorway_link")) {
                                                displayColor = new Color(117, 146, 185);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 70 * 100000 / 3600;
                                                }
                                            } else if (value.equals("trunk")) {
                                                displayColor = new Color(116, 194, 116);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 110 * 100000 / 3600;
                                                }
                                            } else if (value.equals("trunk_link")) {
                                                displayColor = new Color(116, 194, 116);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 70 * 100000 / 3600;
                                                }
                                            } else if (value.equals("primary")) {
                                                displayColor = new Color(225, 98, 102);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 100 * 100000 / 3600;
                                                }
                                            } else if (value.equals("primary_link")) {
                                                displayColor = new Color(225, 98, 102);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 70 * 100000 / 3600;
                                                }
                                            } else if (value.equals("secondary")) {
                                                displayColor = new Color(253, 184, 100);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 100 * 100000 / 3600;
                                                }
                                            } else if (value.equals("tertiary")) {
                                                displayColor = new Color(252, 249, 105);
                                                if (maxspeedSet == false) {
                                                    maxspeed = 90 * 100000 / 3600;
                                                }
                                            } else if (value.equals("road")) {
                                                if (maxspeedSet == false) {
                                                    maxspeed = 70 * 100000 / 3600;
                                                }
                                            } else if (value.equals("unclassified")) {
                                                if (maxspeedSet == false) {
                                                    maxspeed = 70 * 100000 / 3600;
                                                }
                                            } else if (value.equals("residential")) {
                                                if (maxspeedSet == false) {
                                                    maxspeed = 30 * 100000 / 3600;
                                                }
                                            } else if (value.equals("living_street") || value.equals("service")) {
                                                if (maxspeedSet == false) {
                                                    maxspeed = 10 * 100000 / 3600;
                                                }
                                            } else if (value.equals("unsurfaced") || value.equals("track")) {
                                                if (maxspeedSet == false) {
                                                    maxspeed = 2 * 100000 / 3600;
                                                }
                                            } else {
                                                if (maxspeedSet == false) {
                                                    maxspeed = -1;
                                                }
                                            }
                                        } else if (key.equals("network") && value.equals("BAB")) {
                                            if (onewaySet == false)
                                                isOneway = 1;
                                            if (laneSet == false)
                                                lanes = 2;
                                            displayColor = Color.BLUE;
                                            if (maxspeedSet == false) {
                                                maxspeed = 120 * 100000 / 3600;
                                                maxspeedSet = true;
                                            }
                                        } else if (key.equals("tracktype") && maxspeedSet == false) {
                                            if (value.equals("grade1") && maxspeed < 10 * 100000 / 3600) {
                                                maxspeed = 10 * 100000 / 3600;
                                                maxspeedSet = true;
                                            } else if (value.equals("grade2") && maxspeed < 5 * 100000 / 3600) {
                                                maxspeed = 5 * 100000 / 3600;
                                                maxspeedSet = true;
                                            }
                                        } else if (key.equals("maxspeed")) {
                                            try {
                                                maxspeed = Integer.parseInt(value) * 100000 / 3600;
                                                maxspeedSet = true;
                                            } catch (Exception e) {
                                            }
                                        } else if (key.equals("oneway")) {
                                            onewaySet = true;
                                            if (value.equals("yes") || value.equals("true") || value.equals("1"))
                                                isOneway = 1;
                                            else if (value.equals("-1"))
                                                isOneway = -1;
                                            else
                                                isOneway = 0;
                                        } else if (key.equals("lanes")) {
                                            try {
                                                lanes = Integer.parseInt(value);
                                                if (lanes == 0)
                                                    lanes = 1;
                                                laneSet = true;
                                            } catch (Exception e) {
                                            }
                                        } else if (key.equals("junction")) {
                                            if (value.equals("roundabout")) {
                                                isRoundabout = true;
                                            }
                                        }
                                    }
                                }
                                if (maxspeed > 0) {
                                    wayPointsIterator = wayPoints.iterator();
                                    if (streetName.length() == 0) {
                                        streetName = "S " + i;
                                        ++i;
                                    }
                                    node = null;
                                    lastNode = null;
                                    while (wayPointsIterator.hasNext()) {
                                        id = wayPointsIterator.next();
                                        tmpNode = OSMNodes.get(id);
                                        if (tmpNode != null) {
                                            node = tmpNode.getRealNode();
                                            node = map.addNode(node);
                                            if (lastNode != null) {
                                                map.addStreet(new Street(streetName, lastNode, node, streetType, isOneway, lanes, displayColor, map.getRegionOfPoint(node.getX(), node.getY()), maxspeed));
                                            }
                                            lastNode = node;
                                        }
                                    }
                                    if (isRoundabout) {
                                        wayPointsIterator = wayPoints.iterator();
                                        if (wayPointsIterator.hasNext()) {
                                            id = wayPointsIterator.next();
                                            tmpNode = OSMNodes.get(id);
                                            if (tmpNode != null) {
                                                node = tmpNode.getRealNode();
                                                node = map.addNode(node);
                                                if (lastNode != null) {
                                                    map.addStreet(new Street(streetName, node, lastNode, streetType, isOneway, lanes, displayColor, map.getRegionOfPoint(lastNode.getX(), lastNode.getY()), maxspeed));
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                ErrorLog.log(Messages.getString("OSM_Loader.errorParsingWay"), 5, Map.class.getName(), "loadOSM", e);
                            }
                        }
                    } else if (childtype.equals("area") || childtype.equals("relation") || childtype.equals("bounds")) {
                    } else
                        ErrorLog.log(Messages.getString("OSM_Loader.unknownElement"), 3, Map.class.getName(), "loadOSM", null);
                }
            } else
                ErrorLog.log(Messages.getString("OSM_Loader.wrongFileFormat"), 6, Map.class.getName(), "loadOSM", null);
            for (OSMNode n : amenityNodes) {
                Map.getInstance().addAmenityNode(n.getRealNode(), n.getAmenity_());
            }
            sr.close();
            filestream.close();
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("OSM_Loader.errorLoading"), 7, Map.class.getName(), "loadOSM", e);
        }
        map.signalMapLoaded();
        VanetSimStart.setProgressBar(false);
        ErrorLog.log(Messages.getString("OSM_Loader.loadingFinished"), 3, OSMLoader.class.getName(), "loadOSM", null);
    }

    public boolean WGS84toUTM(double[] result, double longitude, double latitude, boolean calculateZone, double longitudeMiddle, boolean highPrecision) {
        if (result.length > 1) {
            if (latitude < -80 || latitude > 84)
                return false;
            if (longitude == 180.0)
                longitude = -180.0;
            else if (longitude > 180.0 || longitude < -180)
                return false;
            double longitudeMiddleRad;
            if (calculateZone) {
                int longitudeZone = (int) Math.floor((longitude + 180.0) / 6.0) + 1;
                if (latitude >= 56.0 && latitude < 64.0 && longitude >= 3.0 && longitude < 12.0)
                    longitudeZone = 32;
                if (latitude >= 72.0 && latitude < 84.0) {
                    if (longitude >= 0.0 && longitude < 9.0)
                        longitudeZone = 31;
                    else if (longitude >= 9.0 && longitude < 21.0)
                        longitudeZone = 33;
                    else if (longitude >= 21.0 && longitude < 33.0)
                        longitudeZone = 35;
                    else if (longitude >= 33.0 && longitude < 42.0)
                        longitudeZone = 37;
                }
                longitudeMiddleRad = ((longitudeZone - 1) * 6 - 177) * (Math.PI / 180.0);
            } else {
                longitudeMiddleRad = longitudeMiddle * (Math.PI / 180.0);
            }
            double latitudeRad = latitude * (Math.PI / 180.0);
            double longitudeRad = longitude * (Math.PI / 180.0);
            double m = 6367449.145960818 * latitudeRad - 16038.508333152433 * Math.sin(2 * latitudeRad) + 16.832200728062137 * Math.sin(4 * latitudeRad) - 0.021800766212608933 * Math.sin(6 * latitudeRad);
            double latitudeSinus = Math.sin(latitudeRad);
            double latitudeTangens = Math.tan(latitudeRad);
            double latitudeCosinus = Math.cos(latitudeRad);
            double n = E_RADIUS / Math.sqrt(1 - ECC_SQUARED * latitudeSinus * latitudeSinus);
            double t = latitudeTangens * latitudeTangens;
            double c = ECC2_SQUARED * latitudeCosinus * latitudeCosinus;
            double a = latitudeCosinus * (longitudeRad - longitudeMiddleRad);
            double g1 = 0;
            double g2 = 0;
            double g3 = 0;
            double g4 = 0;
            if (highPrecision) {
                g1 = 13 * c * c + 4 * Math.pow(c, 3.0) - 64 * c * c - 24 * Math.pow(c, 3.0) * t;
                g2 = (61 - 479 * t + 179 * t * t - Math.pow(t, 3.0)) * Math.pow(a, 7.0) / 5040;
                g3 = 445 * c * c + 324 * Math.pow(c, 3.0) - 680 * c * c * t + 88 * Math.pow(c, 4.0) - 600 * Math.pow(c, 3.0) * t - 192 * Math.pow(c, 4.0) * t;
                g4 = (1385 - 3111 * t + 543 * t * t - Math.pow(t, 3.0)) * Math.pow(a, 8.0) / 40320;
            }
            double term1 = 1 - t + c;
            double term2 = 5 - 18 * t + t * t + 72 * c - 58 * ECC2_SQUARED + g1;
            result[0] = 0.9996 * n * (a + term1 * Math.pow(a, 3.0) / 6 + term2 * Math.pow(a, 5.0) / 120 + g2) + 500000.0;
            term1 = 5 - t + 9 * c + 4 * c * c;
            term2 = 61 - (58 * t) + (t * t) + (600 * c) - (330 * ECC2_SQUARED) + g3;
            result[1] = 0.9996 * (m + n * latitudeTangens * (a * a / 2 + term1 * Math.pow(a, 4.0) / 24 + term2 * Math.pow(a, 6.0) / 720) + g4);
            if (latitude < 0)
                result[1] = result[1] + 10000000.0;
            return true;
        } else
            return false;
    }
}
