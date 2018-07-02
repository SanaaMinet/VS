package vanetsim.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.CyclicBarrier;
import java.awt.geom.Path2D;
import java.util.ArrayDeque;
import javax.imageio.ImageIO;
import vanetsim.ErrorLog;
import vanetsim.localization.Messages;
import vanetsim.map.Junction;
import vanetsim.map.Map;
import vanetsim.map.MapHelper;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.map.Street;
import vanetsim.routing.WayPoint;
 
 
import vanetsim.scenario.KnownVehicle;
import vanetsim.scenario.Vehicle;
 
 
import vanetsim.scenario.events.EventList;
import vanetsim.scenario.events.EventSpot;
import vanetsim.scenario.events.EventSpotList;

public final class Renderer {

    private static final Renderer INSTANCE = new Renderer();

    public static Renderer getInstance() {
        return INSTANCE;
    }

    private static final int VEHICLE_SIZE = 250;

    private static final DecimalFormat FORMATTER = new DecimalFormat(",###");

    private final Map map_ = Map.getInstance();

    private final Font timeFont_ = new Font("Default", Font.PLAIN, 11);

    private final Font silentPeriodFont_ = new Font("Default", Font.BOLD, 20);

    private final int vehicleIDFontSize_ = 150;

    private final Font vehicleIDFont_ = new Font("SansSerif", Font.PLAIN, vehicleIDFontSize_);

    private final BasicStroke lineBackground_ = new BasicStroke((Map.LANE_WIDTH + 45) * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    private final BasicStroke lineMain_ = new BasicStroke(Map.LANE_WIDTH * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    private final AffineTransform transform_ = new AffineTransform();

    private final Point2D blockingImageTransformSource_ = new Point2D.Double(0, 0);

    private final Point2D blockingImageTransformDestination_ = new Point2D.Double(0, 0);

    private boolean simulationRunning_ = false;

    private int timePassed_ = 0;

    private boolean scheduleFullRender_ = false;

    private boolean doPaintInitializedBySimulation_ = false;

    private DrawingArea drawArea_;

    private int drawWidth_ = 0;

    private int drawHeight_ = 0;

    private double middleX_ = 0;

    private double middleY_ = 0;

    private int mapMinX_ = 0;

    private int mapMinY_ = 0;

    private int mapMaxX_ = 0;

    private int mapMaxY_ = 0;

    private boolean currentOverdrawn_ = true;

    private boolean lastOverdrawn_ = true;

    private double zoom_ = Math.exp(1 / 100.0) / 1000;

    private int panCountX_ = 0;

    private int panCountY_ = 0;

    private int regionMinX_ = 0;

    private int regionMinY_ = 0;

    private int regionMaxX_ = 0;

    private int regionMaxY_ = 0;

    private AffineTransform tmpAffine_ = new AffineTransform();

    private double maxTranslationX_ = 0;

    private double maxTranslationY_ = 0;

    private Street markedStreet_ = null;

    private Vehicle markedVehicle_ = null;

    private Vehicle attackerVehicle_ = null;

    private Vehicle attackedVehicle_ = null;

    private boolean highlightCommunication_ = false;

    private boolean highlightAllNodes_ = false;

    private boolean hideMixZones_ = false;

    private boolean displayVehicleIDs_ = false;

    private boolean showAllBlockings_;

    private boolean showBeaconMonitorZone_ = false;

    private boolean showVehicles_ = false;

    private boolean showMixZones_ = false;

 

    private boolean showAttackers_ = false;

    private boolean autoAddMixZones_ = false;

    private int beaconMonitorMinX_ = -1;

    private int beaconMonitorMaxX_ = -1;

    private int beaconMonitorMinY_ = -1;

    private int beaconMonitorMaxY_ = -1;

    

  

    private CyclicBarrier barrierForSimulationMaster_;

    private BufferedImage blockingImage_;

    private BufferedImage slipperyImage_;

    private BufferedImage scaledBlockingImage_;

    private BufferedImage scaledSlipperyImage_;

    private boolean consoleStart = false;

    private Junction markedJunction_ = null;

    private ArrayList<String> locationInformationMix_ = null;

    private ArrayList<String> locationInformationSilentPeriod_ = null;

    private ArrayList<String> locationInformationMDS_ = null;

    private boolean MDSMode_ = true;

    private int mixZoneAmount = 0;

    private boolean debugMode = false;

    private boolean showAmenities = false;

    private int[][] grid = null;

    private int minGridValue = 0;

    private int maxGridValue = 0;

    private int gridSize_ = 0;

    private boolean showPenaltyConnections_ = false;

    private boolean showKnownVehiclesConnections_ = false;

    private ArrayList<Double> centroidsX = null;

    private ArrayList<Double> centroidsY = null;

 

    private boolean showAllClusters = false;

    private final Font clusterIDFont_ = new Font("SansSerif", Font.PLAIN, 1000);

    private Renderer() {
        try {
            BufferedImage tmpImage;
            URL url = ClassLoader.getSystemResource("vanetsim/images/blocking.png");
            tmpImage = ImageIO.read(url);
            blockingImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(tmpImage.getWidth(), tmpImage.getHeight(), 3);
            blockingImage_.getGraphics().drawImage(tmpImage, 0, 0, null);
            url = ClassLoader.getSystemResource("vanetsim/images/slippery_road.png");
            tmpImage = ImageIO.read(url);
            slipperyImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(tmpImage.getWidth(), tmpImage.getHeight(), 3);
            slipperyImage_.getGraphics().drawImage(tmpImage, 0, 0, null);
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("Renderer.noBlockingImage"), 7, Renderer.class.getName(), "Constructor", e);
            blockingImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1, 3);
            slipperyImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1, 3);
        }
    }

    public void drawMovingObjects(Graphics2D g2d) {
        Region[][] regions = map_.getRegions();
        if (regions != null && map_.getReadyState() && (!simulationRunning_ || doPaintInitializedBySimulation_)) {
            int i, j, k, size;
            Vehicle vehicle;
            Vehicle[] vehicles;
            AffineTransform g2dAffine = g2d.getTransform();
            if (g2dAffine.getTranslateX() > maxTranslationX_)
                maxTranslationX_ = g2dAffine.getTranslateX();
            if (g2dAffine.getTranslateY() > maxTranslationY_)
                maxTranslationY_ = g2dAffine.getTranslateY();
            if (g2dAffine.getTranslateX() < 0 || g2dAffine.getTranslateY() < 0)
                tmpAffine_ = (AffineTransform) g2dAffine.clone();
            else if (g2dAffine.getTranslateX() < maxTranslationX_ || g2dAffine.getTranslateY() < maxTranslationY_)
                tmpAffine_.setToIdentity();
            else
                tmpAffine_.setToTranslation(maxTranslationX_, maxTranslationY_);
            tmpAffine_.concatenate(transform_);
            AffineTransform tmpAffine2 = g2d.getTransform();
            g2d.setTransform(tmpAffine_);
            if (showBeaconMonitorZone_) {
                g2d.setColor(Color.orange);
                g2d.drawRect(beaconMonitorMinX_, beaconMonitorMinY_, beaconMonitorMaxX_ - beaconMonitorMinX_, beaconMonitorMaxY_ - beaconMonitorMinY_);
            }
            if (markedStreet_ != null) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int totalLanes;
                if (markedStreet_.isOneway())
                    totalLanes = markedStreet_.getLanesCount();
                else
                    totalLanes = 2 * markedStreet_.getLanesCount();
                int x0 = markedStreet_.getStartNode().getX();
                int y0 = markedStreet_.getStartNode().getY();
                int x1 = markedStreet_.getEndNode().getX();
                int y1 = markedStreet_.getEndNode().getY();
                g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * totalLanes + 45, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                g2d.setPaint(Color.BLACK);
                g2d.drawLine(x0, y0, x1, y1);
                int deltaX = x1 - x0;
                int deltaY = y1 - y0;
                double frac = 0.2;
                if (markedStreet_.getLength() < 1000 && markedStreet_.isOneway())
                    frac = 0.9;
                g2d.drawLine(x0 + (int) ((1 - frac) * deltaX + frac * deltaY), y0 + (int) ((1 - frac) * deltaY - frac * deltaX), x1, y1);
                g2d.drawLine(x0 + (int) ((1 - frac) * deltaX - frac * deltaY), y0 + (int) ((1 - frac) * deltaY + frac * deltaX), x1, y1);
                if (!markedStreet_.isOneway()) {
                    deltaX = -deltaX;
                    deltaY = -deltaY;
                    g2d.drawLine(x1 + (int) ((1 - frac) * deltaX + frac * deltaY), y1 + (int) ((1 - frac) * deltaY - frac * deltaX), x0, y0);
                    g2d.drawLine(x1 + (int) ((1 - frac) * deltaX - frac * deltaY), y1 + (int) ((1 - frac) * deltaY + frac * deltaX), x0, y0);
                    deltaX = -deltaX;
                    deltaY = -deltaY;
                }
                g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * totalLanes, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                g2d.setPaint(Color.CYAN);
                g2d.drawLine(x0, y0, x1, y1);
                g2d.drawLine(x0 + (int) ((1 - frac) * deltaX + frac * deltaY), y0 + (int) ((1 - frac) * deltaY - frac * deltaX), x1, y1);
                g2d.drawLine(x0 + (int) ((1 - frac) * deltaX - frac * deltaY), y0 + (int) ((1 - frac) * deltaY + frac * deltaX), x1, y1);
                if (!markedStreet_.isOneway()) {
                    deltaX = -deltaX;
                    deltaY = -deltaY;
                    g2d.drawLine(x1 + (int) ((1 - frac) * deltaX + frac * deltaY), y1 + (int) ((1 - frac) * deltaY - frac * deltaX), x0, y0);
                    g2d.drawLine(x1 + (int) ((1 - frac) * deltaX - frac * deltaY), y1 + (int) ((1 - frac) * deltaY + frac * deltaX), x0, y0);
                }
            }
            g2d.setTransform(tmpAffine2);
      
            g2d.setTransform(tmpAffine_);
            Street[] streets;
            Street street;
            for (i = regionMinX_; i <= regionMaxX_; ++i) {
                for (j = regionMinY_; j <= regionMaxY_; ++j) {
                    streets = regions[i][j].getStreets();
                    for (k = 0; k < streets.length; ++k) {
                        street = streets[k];
                        if (street.getEndNodeTrafficLightState() != -1) {
                            if (markedJunction_ != null && markedJunction_.getNode().equals(street.getEndNode())) {
                                g2d.setPaint(Color.orange);
                                g2d.fillOval(street.getTrafficLightEndX_() - (Map.LANE_WIDTH + 45), street.getTrafficLightEndY_() - (Map.LANE_WIDTH + 45), (Map.LANE_WIDTH + 45) * 2, (Map.LANE_WIDTH + 45) * 2);
                            }
                            if (street.getEndNodeTrafficLightState() == 0)
                                g2d.setPaint(Color.green);
                            else if (street.getEndNodeTrafficLightState() == 1 || street.getEndNodeTrafficLightState() == 7)
                                g2d.setPaint(Color.yellow);
                            else
                                g2d.setPaint(Color.red);
                            g2d.fillOval(street.getTrafficLightEndX_() - (Map.LANE_WIDTH), street.getTrafficLightEndY_() - (Map.LANE_WIDTH), (Map.LANE_WIDTH) * 2, (Map.LANE_WIDTH) * 2);
                        }
                        if (street.getStartNodeTrafficLightState() != -1) {
                            if (markedJunction_ != null && markedJunction_.getNode().equals(street.getStartNode())) {
                                g2d.setPaint(Color.orange);
                                g2d.fillOval(street.getTrafficLightStartX_() - (Map.LANE_WIDTH + 45), street.getTrafficLightStartY_() - (Map.LANE_WIDTH + 45), (Map.LANE_WIDTH + 45) * 2, (Map.LANE_WIDTH + 45) * 2);
                            }
                            if (street.getStartNodeTrafficLightState() == 0)
                                g2d.setPaint(Color.green);
                            else if (street.getStartNodeTrafficLightState() == 1 || street.getStartNodeTrafficLightState() == 7)
                                g2d.setPaint(Color.yellow);
                            else
                                g2d.setPaint(Color.red);
                            g2d.fillOval(street.getTrafficLightStartX_() - (Map.LANE_WIDTH), street.getTrafficLightStartY_() - (Map.LANE_WIDTH), (Map.LANE_WIDTH) * 2, (Map.LANE_WIDTH) * 2);
                        }
                    }
                }
            }
            if (zoom_ > 0.0018) {
                if (displayVehicleIDs_)
                    g2d.setFont(vehicleIDFont_);
                g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                g2d.setPaint(Color.black);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    for (i = regionMinX_; i <= regionMaxX_; ++i) {
                        for (j = regionMinY_; j <= regionMaxY_; ++j) {
                            vehicles = regions[i][j].getVehicleArray();
                            size = vehicles.length;
                            for (k = 0; k < size; ++k) {
                                vehicle = vehicles[k];
                                g2d.setPaint(vehicle.getColor());
                                if (vehicle.isInSlow())
                                    g2d.setPaint(Color.pink);
                                if ((isShowVehicles() || vehicle.isActive()) && vehicle.getX() >= mapMinX_ && vehicle.getX() <= mapMaxX_ && vehicle.getY() >= mapMinY_ && vehicle.getY() <= mapMaxY_) {
                                    if (highlightCommunication_) {
                                        if (vehicle.isWiFiEnabled() && (!vehicle.isInMixZone() || Vehicle.getMixZonesFallbackEnabled())) {
                                            g2d.setPaint(Color.blue);
                                            if (vehicle != markedVehicle_)
                                                g2d.drawOval(vehicle.getX() - vehicle.getMaxCommDistance(), vehicle.getY() - vehicle.getMaxCommDistance(), vehicle.getMaxCommDistance() * 2, vehicle.getMaxCommDistance() * 2);
                                        } else
                                            g2d.setPaint(Color.black);
                                    }
                                    g2d.fillOval(vehicle.getX() - VEHICLE_SIZE / 2, vehicle.getY() - VEHICLE_SIZE / 2, VEHICLE_SIZE, VEHICLE_SIZE);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                if (displayVehicleIDs_) {
                    g2d.setPaint(new Color(153, 102, 100));
                    try {
                        for (i = regionMinX_; i <= regionMaxX_; ++i) {
                            for (j = regionMinY_; j <= regionMaxY_; ++j) {
                                vehicles = regions[i][j].getVehicleArray();
                                size = vehicles.length;
                                for (k = 0; k < size; ++k) {
                                    vehicle = vehicles[k];
                                    if (vehicle.isActive() && vehicle.getX() >= mapMinX_ && vehicle.getX() <= mapMaxX_ && vehicle.getY() >= mapMinY_ && vehicle.getY() <= mapMaxY_) {
                                        g2d.drawString(vehicle.getHexID(), vehicle.getX() - vehicleIDFontSize_ * 4, vehicle.getY() - VEHICLE_SIZE / 2);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                if (showKnownVehiclesConnections_) {
                    g2d.setPaint(new Color(153, 102, 100));
                    try {
                        for (i = regionMinX_; i <= regionMaxX_; ++i) {
                            for (j = regionMinY_; j <= regionMaxY_; ++j) {
                                vehicles = regions[i][j].getVehicleArray();
                                size = vehicles.length;
                                for (k = 0; k < size; ++k) {
                                    vehicle = vehicles[k];
                                    if (vehicle.isActive() && vehicle.getX() >= mapMinX_ && vehicle.getX() <= mapMaxX_ && vehicle.getY() >= mapMinY_ && vehicle.getY() <= mapMaxY_) {
                                        KnownVehicle[] heads = vehicle.getKnownVehiclesList().getFirstKnownVehicle();
                                        KnownVehicle next;
                                        for (int l = 0; l < heads.length; ++l) {
                                            next = heads[l];
                                            while (next != null) {
                                                g2d.drawLine(vehicle.getX(), vehicle.getY(), next.getX(), next.getY());
                                                next = next.getNext();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (showPenaltyConnections_) {
                    try {
                        for (i = regionMinX_; i <= regionMaxX_; ++i) {
                            for (j = regionMinY_; j <= regionMaxY_; ++j) {
                                vehicles = regions[i][j].getVehicleArray();
                                size = vehicles.length;
                                for (k = 0; k < size; ++k) {
                                    vehicle = vehicles[k];
 
                                    }
                                }
                            }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (markedVehicle_ != null) {
                    g2d.setPaint(Color.RED);
                    g2d.fillOval(markedVehicle_.getX() - VEHICLE_SIZE / 2 + 35, markedVehicle_.getY() - VEHICLE_SIZE / 2 + 35, VEHICLE_SIZE - 70, VEHICLE_SIZE - 70);
                    if (markedVehicle_.isWiFiEnabled() && (!markedVehicle_.isInMixZone() || Vehicle.getMixZonesFallbackEnabled()))
                        g2d.drawOval(markedVehicle_.getX() - markedVehicle_.getMaxCommDistance(), markedVehicle_.getY() - markedVehicle_.getMaxCommDistance(), markedVehicle_.getMaxCommDistance() * 2, markedVehicle_.getMaxCommDistance() * 2);
                    WayPoint nextDestination = markedVehicle_.getDestinations().peekFirst();
                    if (nextDestination != null) {
                        g2d.drawLine(markedVehicle_.getX(), markedVehicle_.getY(), nextDestination.getX(), nextDestination.getY());
                        g2d.fillOval(nextDestination.getX() - VEHICLE_SIZE, nextDestination.getY() - VEHICLE_SIZE, VEHICLE_SIZE * 2, VEHICLE_SIZE * 2);
                        Street[] routestreets = markedVehicle_.getRouteStreets();
                        if (routestreets.length > 1) {
                            g2d.setPaint(Color.blue);
                            if (markedVehicle_.getCurDirection())
                                g2d.drawLine(markedVehicle_.getX(), markedVehicle_.getY(), markedVehicle_.getCurStreet().getEndNode().getX(), markedVehicle_.getCurStreet().getEndNode().getY());
                            else
                                g2d.drawLine(markedVehicle_.getX(), markedVehicle_.getY(), markedVehicle_.getCurStreet().getStartNode().getX(), markedVehicle_.getCurStreet().getStartNode().getY());
                            for (i = markedVehicle_.getRoutePosition() + 1; i < routestreets.length - 1; ++i) {
                                g2d.drawLine(routestreets[i].getStartNode().getX(), routestreets[i].getStartNode().getY(), routestreets[i].getEndNode().getX(), routestreets[i].getEndNode().getY());
                            }
                            if (!markedVehicle_.getRouteDirections()[routestreets.length - 1]) {
                                g2d.drawLine(nextDestination.getX(), nextDestination.getY(), routestreets[routestreets.length - 1].getEndNode().getX(), routestreets[routestreets.length - 1].getEndNode().getY());
                            } else {
                                g2d.drawLine(nextDestination.getX(), nextDestination.getY(), routestreets[routestreets.length - 1].getStartNode().getX(), routestreets[routestreets.length - 1].getStartNode().getY());
                            }
                        }
                    }
                    if (isShowVehicles()) {
                        ArrayDeque<WayPoint> tmpDestinations = markedVehicle_.getDestinations();
                        WayPoint oldDestination = null;
                        for (WayPoint destination : tmpDestinations) {
                            if (oldDestination != null) {
                                g2d.setPaint(Color.blue);
                                g2d.fillOval(oldDestination.getX() - VEHICLE_SIZE, oldDestination.getY() - VEHICLE_SIZE, VEHICLE_SIZE * 2, VEHICLE_SIZE * 2);
                                g2d.setPaint(Color.red);
                                g2d.drawLine(oldDestination.getX(), oldDestination.getY(), destination.getX(), destination.getY());
                            }
                            g2d.setPaint(Color.red);
                            g2d.fillOval(destination.getX() - VEHICLE_SIZE, destination.getY() - VEHICLE_SIZE, VEHICLE_SIZE * 2, VEHICLE_SIZE * 2);
                            oldDestination = destination;
                        }
                    }
                }
                if (showAttackers_ || simulationRunning_) {
                    if (attackerVehicle_ != null) {
                        g2d.setPaint(Color.LIGHT_GRAY);
                        g2d.fillOval(attackerVehicle_.getX() - VEHICLE_SIZE / 2 + 35, attackerVehicle_.getY() - VEHICLE_SIZE / 2 + 35, VEHICLE_SIZE - 70, VEHICLE_SIZE - 70);
                    }
                    if (attackedVehicle_ != null) {
                        g2d.setPaint(Color.GREEN);
                        g2d.fillOval(attackedVehicle_.getX() - VEHICLE_SIZE / 2 + 35, attackedVehicle_.getY() - VEHICLE_SIZE / 2 + 35, VEHICLE_SIZE - 70, VEHICLE_SIZE - 70);
                    }
                }
            }
            if (highlightAllNodes_ && zoom_ > 0.0012) {
                Node[] nodes;
                Node node;
                for (i = regionMinX_; i <= regionMaxX_; ++i) {
                    for (j = regionMinY_; j <= regionMaxY_; ++j) {
                        nodes = regions[i][j].getNodes();
                        for (k = 0; k < nodes.length; ++k) {
                            node = nodes[k];
                            g2d.setPaint(Color.black);
                            g2d.fillOval(node.getX() - (Map.LANE_WIDTH + 45), node.getY() - (Map.LANE_WIDTH + 45), (Map.LANE_WIDTH + 45) * 2, (Map.LANE_WIDTH + 45) * 2);
                            g2d.setPaint(Color.pink);
                            if (markedJunction_ != null && markedJunction_.getNode().equals(node))
                                g2d.setPaint(Color.red);
                            g2d.fillOval(node.getX() - Map.LANE_WIDTH, node.getY() - Map.LANE_WIDTH, Map.LANE_WIDTH * 2, Map.LANE_WIDTH * 2);
                        }
                    }
                }
            }
            if (g2dAffine.getTranslateX() < 0 || g2dAffine.getTranslateY() < 0)
                tmpAffine_ = g2dAffine;
            else if (g2dAffine.getTranslateX() < maxTranslationX_ || g2dAffine.getTranslateY() < maxTranslationY_)
                tmpAffine_.setToIdentity();
            else
                tmpAffine_.setToTranslation(maxTranslationX_, maxTranslationY_);
            g2d.setTransform(tmpAffine_);
            g2d.setPaint(Color.black);
            g2d.setFont(timeFont_);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.drawString(FORMATTER.format(timePassed_/40) + " ms ", 5, 10);//Here printed simulation Time
            if (Vehicle.isSilent_period()) {
                g2d.setPaint(Color.red);
                g2d.setFont(silentPeriodFont_);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2d.drawString("SILENT PERIOD", 5, this.drawHeight_ - 10);
            }
            if (simulationRunning_ && doPaintInitializedBySimulation_) {
                doPaintInitializedBySimulation_ = false;
                try {
                    barrierForSimulationMaster_.await();
                    barrierForSimulationMaster_.reset();
                } catch (Exception e) {
                }
            }
        }
    }

    public void drawScale(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.white);
        g2d.fillRect(1, 1, 98, 28);
        g2d.setColor(Color.black);
        try {
            Point2D.Double point1 = new Point2D.Double();
            Point2D.Double point2 = new Point2D.Double();
            transform_.inverseTransform(new Point2D.Double(0, 0), point1);
            transform_.inverseTransform(new Point2D.Double(0, 70), point2);
            int len = (int) Math.round(point2.getY() - point1.getY());
            if (len < 1) {
                double len3 = (point2.getY() - point1.getY()) * 10;
                DecimalFormat df = new DecimalFormat(",###.###");
                g2d.drawString(df.format(len3) + " mm", 30, 15);
                g2d.drawLine(15, 20, 85, 20);
                g2d.drawLine(15, 18, 15, 22);
                g2d.drawLine(85, 18, 85, 22);
            } else {
                transform_.transform(new Point2D.Double(0, 0), point1);
                if (len > 10000000) {
                    len = len / 10000000;
                    transform_.transform(new Point2D.Double(0, len * 10000000), point2);
                    g2d.drawString(len + "00 km", 25, 15);
                } else if (len > 1000000) {
                    len = len / 1000000;
                    transform_.transform(new Point2D.Double(0, len * 1000000), point2);
                    g2d.drawString(len + "0 km", 30, 15);
                } else if (len > 100000) {
                    len = len / 100000;
                    transform_.transform(new Point2D.Double(0, len * 100000), point2);
                    g2d.drawString(len + " km", 35, 15);
                } else if (len > 10000) {
                    len = len / 10000;
                    transform_.transform(new Point2D.Double(0, len * 10000), point2);
                    g2d.drawString(len + "00 m", 35, 15);
                } else if (len > 1000) {
                    len = len / 1000;
                    transform_.transform(new Point2D.Double(0, len * 1000), point2);
                    g2d.drawString(len + "0 m", 35, 15);
                } else if (len > 100) {
                    len = len / 100;
                    transform_.transform(new Point2D.Double(0, len * 100), point2);
                    g2d.drawString(len + " m", 35, 15);
                } else if (len > 10) {
                    len = len / 10;
                    transform_.transform(new Point2D.Double(0, len * 10), point2);
                    g2d.drawString(len + "0 cm", 35, 15);
                } else {
                    transform_.transform(new Point2D.Double(0, len), point2);
                    g2d.drawString(len + " cm", 35, 15);
                }
                len = (int) Math.round(point2.getY() - point1.getY());
                g2d.drawLine(15, 20, 15 + len, 20);
                g2d.drawLine(15, 18, 15, 22);
                g2d.drawLine(15 + len, 18, 15 + len, 22);
            }
        } catch (Exception e) {
        }
    }

    public synchronized void drawStaticObjects(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        Region[][] regions = map_.getRegions();
        if (regions != null && map_.getReadyState()) {
            int savedRegionMinX = regionMinX_;
            int savedRegionMaxX = regionMaxX_;
            int savedRegionMinY = regionMinY_;
            int savedRegionMaxY = regionMaxY_;
            if (!lastOverdrawn_ && (((panCountX_ == 1 || panCountX_ == -1) && panCountY_ == 0) || ((panCountY_ == 1 || panCountY_ == -1) && panCountX_ == 0))) {
                g2d.drawImage(image, (int) Math.round(panCountX_ * drawWidth_ / 2), (int) Math.round(panCountY_ * drawHeight_ / 2), null, drawArea_);
                int clipX = 0, clipY = 0, clipWidth, clipHeight;
                if (panCountX_ != 0)
                    clipWidth = drawWidth_ / 2;
                else
                    clipWidth = drawWidth_;
                if (panCountY_ != 0)
                    clipHeight = drawHeight_ / 2;
                else
                    clipHeight = drawHeight_;
                if (panCountX_ < 0)
                    clipX = drawWidth_ / 2;
                else if (panCountY_ < 0)
                    clipY = drawHeight_ / 2;
                g2d.setClip(clipX, clipY, clipWidth, clipHeight);
                int newMinX = mapMinX_, newMaxX = mapMaxX_, newMinY = mapMinY_, newMaxY = mapMaxY_;
                if (panCountX_ > 0)
                    newMaxX = mapMaxX_ - ((mapMaxX_ - mapMinX_) / 2);
                else if (panCountX_ < 0)
                    newMinX = mapMinX_ + ((mapMaxX_ - mapMinX_) / 2);
                if (panCountY_ > 0)
                    newMaxY = mapMaxY_ - ((mapMaxY_ - mapMinY_) / 2);
                else if (panCountY_ < 0)
                    newMinY = mapMinY_ + ((mapMaxY_ - mapMinY_) / 2);
                Region tmpRegion = map_.getRegionOfPoint(newMinX, newMinY);
                savedRegionMinX = tmpRegion.getX();
                savedRegionMinY = tmpRegion.getY();
                tmpRegion = map_.getRegionOfPoint(newMaxX, newMaxY);
                savedRegionMaxX = tmpRegion.getX();
                savedRegionMaxY = tmpRegion.getY();
            }
            panCountX_ = 0;
            panCountY_ = 0;
            g2d.setColor(new Color(230, 230, 230));
            g2d.fillRect(0, 0, drawWidth_, drawHeight_);
            boolean antialias = true;
            if (zoom_ > 0.0018)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            else {
                antialias = false;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            g2d.setTransform(transform_);
            if (!antialias)
                g2d.setPaint(Color.gray);
            else
                g2d.setPaint(Color.black);
            g2d.drawRect(0, 0, map_.getMapWidth(), map_.getMapHeight());
            int i, j, k, totalLanes, lastEntry = -1;
            Street street;
            Region streetRegion;
            Node startNode, endNode;
            Street[] streets;
            TreeMap<Integer, Path2D.Float> layers = new TreeMap<Integer, Path2D.Float>();
            Path2D.Float currentPath = null;
            if (showMixZones_ || (hideMixZones_ && Vehicle.getCommunicationEnabled() && Vehicle.getMixZonesEnabled())) {
                g2d.setPaint(Color.LIGHT_GRAY);
                for (i = regionMinX_; i <= regionMaxX_; ++i) {
                    for (j = regionMinY_; j <= regionMaxY_; ++j) {
                        Node[] nodes = regions[i][j].getMixZoneNodes();
                        for (int l = 0; l < nodes.length; ++l) {
                            g2d.setPaint(Color.LIGHT_GRAY);
                            g2d.fillOval(nodes[l].getX() - nodes[l].getMixZoneRadius(), nodes[l].getY() - nodes[l].getMixZoneRadius(), nodes[l].getMixZoneRadius() * 2, nodes[l].getMixZoneRadius() * 2);
                            g2d.setPaint(Color.RED);
 
                        }
                    }
                }
            }
            if (locationInformationSilentPeriod_ != null) {
                showMixZones_ = true;
                String[] data;
                for (String location : locationInformationSilentPeriod_) {
                    data = location.split(":");
                    if (data[0].equals("true"))
                        g2d.setPaint(Color.RED);
                    else if (data[0].equals("slow"))
                        g2d.setPaint(Color.BLUE);
                    else
                        g2d.setPaint(Color.GREEN);
                    g2d.drawOval(Integer.parseInt(data[1]) - 2500, Integer.parseInt(data[2]) - 2500, 5000, 5000);
                }
            }
            if (locationInformationMDS_ != null) {
                showMixZones_ = true;
                String[] data;
                for (String location : locationInformationMDS_) {
                    data = location.split(":");
                    if (MDSMode_) {
                        if (data[8].equals("true") && data[10].equals("true"))
                            g2d.setPaint(Color.LIGHT_GRAY);
                        else if (data[8].equals("false") && data[10].equals("false"))
                            g2d.setPaint(Color.BLACK);
                    } else {
                        if (data[8].equals("false") && data[10].equals("true"))
                            g2d.setPaint(Color.LIGHT_GRAY);
                        else if (data[8].equals("true") && data[10].equals("false"))
                            g2d.setPaint(Color.BLACK);
                    }
                    g2d.drawOval(Integer.parseInt(data[5]) - 2500, Integer.parseInt(data[6]) - 2500, 5000, 5000);
                }
            }
            if (locationInformationMix_ != null) {
                String[] data;
                int size = 0;
                for (String location : locationInformationMix_) {
                    data = location.split(":");
                    if (Float.parseFloat(data[4]) != 0)
                        size = (int) (1000 + (Float.parseFloat(data[4]) / mixZoneAmount) * 100000);
                    else
                        size = 0;
                    g2d.setPaint(new Color((int) (Float.parseFloat(data[2]) * 200) + 55, (int) (Float.parseFloat(data[3]) * 200) + 55, 0));
                    g2d.fillOval(Integer.parseInt(data[0]) - size, Integer.parseInt(data[1]) - size, size * 2, size * 2);
                }
            }
            if (antialias) {
                for (i = savedRegionMinX; i <= savedRegionMaxX; ++i) {
                    for (j = savedRegionMinY; j <= savedRegionMaxY; ++j) {
                        streets = regions[i][j].getStreets();
                        for (k = 0; k < streets.length; ++k) {
                            street = streets[k];
                            streetRegion = street.getMainRegion();
                            if (streetRegion == regions[i][j] || streetRegion.getX() < savedRegionMinX || streetRegion.getX() > savedRegionMaxX || streetRegion.getY() < savedRegionMinY || streetRegion.getY() > savedRegionMaxY) {
                                if (street.isOneway())
                                    totalLanes = street.getLanesCount();
                                else
                                    totalLanes = 2 * street.getLanesCount();
                                if (lastEntry != totalLanes) {
                                    lastEntry = totalLanes;
                                    currentPath = layers.get(totalLanes);
                                    if (currentPath == null) {
                                        currentPath = new Path2D.Float(Path2D.WIND_NON_ZERO, 5000);
                                        layers.put(lastEntry, currentPath);
                                    }
                                }
                                startNode = street.getStartNode();
                                endNode = street.getEndNode();
                                currentPath.moveTo(startNode.getX(), startNode.getY());
                                currentPath.lineTo(endNode.getX(), endNode.getY());
                            }
                        }
                    }
                }
                Iterator<Integer> iterator = layers.keySet().iterator();
                int key;
                g2d.setPaint(Color.black);
                g2d.setStroke(lineBackground_);
                while (iterator.hasNext()) {
                    key = iterator.next();
                    if (key == 2)
                        g2d.setStroke(lineBackground_);
                    else
                        g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * key + 90, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                    g2d.draw(layers.get(key));
                }
            }
            layers.clear();
            lastEntry = 1;
            boolean detailedView;
            if (zoom_ > 0.0001)
                detailedView = true;
            else
                detailedView = false;
            for (i = savedRegionMinX; i <= savedRegionMaxX; ++i) {
                for (j = savedRegionMinY; j <= savedRegionMaxY; ++j) {
                    streets = regions[i][j].getStreets();
                    for (k = 0; k < streets.length; ++k) {
                        street = streets[k];
                        streetRegion = street.getMainRegion();
                        if (streetRegion == regions[i][j] || streetRegion.getX() < savedRegionMinX || streetRegion.getX() > savedRegionMaxX || streetRegion.getY() < savedRegionMinY || streetRegion.getY() > savedRegionMaxY) {
                            if (detailedView || street.getSpeed() > 2000) {
                                startNode = street.getStartNode();
                                endNode = street.getEndNode();
                                if (antialias) {
                                    if (street.isOneway())
                                        totalLanes = street.getDisplayColor().getRGB() * 100 - street.getLanesCount();
                                    else
                                        totalLanes = street.getDisplayColor().getRGB() * 100 - (2 * street.getLanesCount());
                                } else
                                    totalLanes = street.getDisplayColor().getRGB();
                                if (lastEntry != totalLanes) {
                                    lastEntry = totalLanes;
                                    currentPath = layers.get(totalLanes);
                                    if (currentPath == null) {
                                        currentPath = new Path2D.Float(Path2D.WIND_NON_ZERO, 5000);
                                        layers.put(lastEntry, currentPath);
                                    }
                                }
                                currentPath.moveTo(startNode.getX(), startNode.getY());
                                currentPath.lineTo(endNode.getX(), endNode.getY());
                            }
                        }
                    }
                }
            }
            Color drawColor;
            Iterator<Integer> coloriterator = layers.keySet().iterator();
            int key, lanes;
            while (coloriterator.hasNext()) {
                key = coloriterator.next();
                if (antialias) {
                    drawColor = new Color(key / 100);
                    lanes = -key + (key / 100 * 100);
                    if (lanes == 2)
                        g2d.setStroke(lineMain_);
                    else
                        g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * lanes, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                } else {
                    g2d.setStroke(lineMain_);
                    drawColor = new Color(Math.max((int) (((key >> 16) & 0xFF) * 0.8), 0), Math.max((int) (((key >> 8) & 0xFF) * 0.8), 0), Math.max((int) (((key >> 0) & 0xFF) * 0.8), 0));
                }
                g2d.setPaint(drawColor);
                g2d.draw(layers.get(key));
            }
            if (antialias) {
                int l, correction;
                int[] start = new int[2];
                int[] end = new int[2];
                boolean correctStart, correctEnd;
                ArrayList<Point2D.Double> paintArrayList;
                for (i = savedRegionMinX; i <= savedRegionMaxX; ++i) {
                    for (j = savedRegionMinY; j <= savedRegionMaxY; ++j) {
                        streets = regions[i][j].getStreets();
                        for (k = 0; k < streets.length; ++k) {
                            street = streets[k];
                            streetRegion = street.getMainRegion();
                            if (streetRegion == regions[i][j] || streetRegion.getX() < savedRegionMinX || streetRegion.getX() > savedRegionMaxX || streetRegion.getY() < savedRegionMinY || streetRegion.getY() > savedRegionMaxY) {
                                if (street.getBridgePaintLines() != null || street.getBridgePaintPolygons() != null) {
                                    if (street.isOneway())
                                        totalLanes = street.getLanesCount();
                                    else
                                        totalLanes = 2 * street.getLanesCount();
                                    correction = totalLanes * Map.LANE_WIDTH * 2;
                                    correctStart = false;
                                    correctEnd = false;
                                    start[0] = street.getStartNode().getX();
                                    start[1] = street.getStartNode().getY();
                                    end[0] = street.getEndNode().getX();
                                    end[1] = street.getEndNode().getY();
                                    if (street.getStartNode().getCrossingStreetsCount() > 2)
                                        correctStart = true;
                                    if (street.getEndNode().getCrossingStreetsCount() > 2)
                                        correctEnd = true;
                                    if (street.getLength() > correction * 2) {
                                        MapHelper.calculateResizedLine(start, end, correction, correctStart, correctEnd);
                                        g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * totalLanes + 90, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                                        g2d.setColor(Color.white);
                                        g2d.drawLine(start[0], start[1], end[0], end[1]);
                                        g2d.setStroke(new BasicStroke(Map.LANE_WIDTH * totalLanes, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                                        g2d.setColor(street.getDisplayColor());
                                        g2d.drawLine(start[0], start[1], end[0], end[1]);
                                    }
                                }
                            }
                        }
                    }
                }
                for (i = savedRegionMinX; i <= savedRegionMaxX; ++i) {
                    for (j = savedRegionMinY; j <= savedRegionMaxY; ++j) {
                        streets = regions[i][j].getStreets();
                        for (k = 0; k < streets.length; ++k) {
                            street = streets[k];
                            streetRegion = street.getMainRegion();
                            if (streetRegion == regions[i][j] || streetRegion.getX() < savedRegionMinX || streetRegion.getX() > savedRegionMaxX || streetRegion.getY() < savedRegionMinY || streetRegion.getY() > savedRegionMaxY) {
                                paintArrayList = street.getBridgePaintPolygons();
                                if (paintArrayList != null) {
                                    g2d.setColor(street.getDisplayColor());
                                    for (l = 3; l < paintArrayList.size(); l = l + 4) {
                                        int[] xPoints = new int[4];
                                        int[] yPoints = new int[4];
                                        xPoints[0] = (int) Math.round(paintArrayList.get(l - 3).x);
                                        yPoints[0] = (int) Math.round(paintArrayList.get(l - 3).y);
                                        xPoints[1] = (int) Math.round(paintArrayList.get(l - 2).x);
                                        yPoints[1] = (int) Math.round(paintArrayList.get(l - 2).y);
                                        xPoints[2] = (int) Math.round(paintArrayList.get(l).x);
                                        yPoints[2] = (int) Math.round(paintArrayList.get(l).y);
                                        xPoints[3] = (int) Math.round(paintArrayList.get(l - 1).x);
                                        yPoints[3] = (int) Math.round(paintArrayList.get(l - 1).y);
                                        start[0] = xPoints[0];
                                        start[1] = yPoints[0];
                                        end[0] = xPoints[3];
                                        end[1] = yPoints[3];
                                        MapHelper.calculateResizedLine(start, end, 50, true, true);
                                        xPoints[0] = start[0];
                                        yPoints[0] = start[1];
                                        xPoints[3] = end[0];
                                        yPoints[3] = end[1];
                                        start[0] = xPoints[1];
                                        start[1] = yPoints[1];
                                        end[0] = xPoints[2];
                                        end[1] = yPoints[2];
                                        MapHelper.calculateResizedLine(start, end, 50, true, true);
                                        xPoints[1] = start[0];
                                        yPoints[1] = start[1];
                                        xPoints[2] = end[0];
                                        yPoints[2] = end[1];
                                        g2d.fillPolygon(xPoints, yPoints, 4);
                                    }
                                    g2d.setStroke(new BasicStroke(45, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                                    g2d.setColor(Color.white);
                                    for (l = 1; l < paintArrayList.size(); l = l + 2) {
                                        int x1 = (int) Math.round(paintArrayList.get(l - 1).x);
                                        int y1 = (int) Math.round(paintArrayList.get(l - 1).y);
                                        int x2 = (int) Math.round(paintArrayList.get(l).x);
                                        int y2 = (int) Math.round(paintArrayList.get(l).y);
                                        g2d.drawLine(x1, y1, x2, y2);
                                    }
                                }
                                paintArrayList = street.getBridgePaintLines();
                                if (paintArrayList != null) {
                                    g2d.setStroke(new BasicStroke(45, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                                    g2d.setColor(Color.white);
                                    for (l = 1; l < paintArrayList.size(); l = l + 2) {
                                        int x1 = (int) Math.round(paintArrayList.get(l - 1).x);
                                        int y1 = (int) Math.round(paintArrayList.get(l - 1).y);
                                        int x2 = (int) Math.round(paintArrayList.get(l).x);
                                        int y2 = (int) Math.round(paintArrayList.get(l).y);
                                        g2d.drawLine(x1, y1, x2, y2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (  simulationRunning_) {
                g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                
 
            }
            if (debugMode) {
                g2d.setPaint(Color.PINK);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 400));
                g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                for (i = regionMinX_; i <= regionMaxX_; ++i) {
                    for (j = regionMinY_; j <= regionMaxY_; ++j) {
                        ArrayList<String> xxx = regions[i][j].xxx;
                        ArrayList<String> yyy = regions[i][j].yyy;
                        ArrayList<String> nnn = regions[i][j].nnn;
                        if (xxx != null) {
                            for (k = 0; k < xxx.size(); k++) {
                                g2d.drawOval(Integer.parseInt(xxx.get(k)) - 300, Integer.parseInt(yyy.get(k)) - 300, 300 * 2, 300 * 2);
                                g2d.drawString(nnn.get(k), Integer.parseInt(xxx.get(k)), Integer.parseInt(yyy.get(k)));
                            }
                        }
                    }
                }
            }
            if ( simulationRunning_) {
                g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                
 
            }
            g2d.setStroke(new BasicStroke(300, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            EventSpot tmpEventSpot = EventSpotList.getInstance().getHead_();
            while (tmpEventSpot != null) {
                g2d.setPaint(tmpEventSpot.getEventSpotColor_());
                g2d.drawOval(tmpEventSpot.getX_() - tmpEventSpot.getRadius_(), tmpEventSpot.getY_() - tmpEventSpot.getRadius_(), tmpEventSpot.getRadius_() * 2, tmpEventSpot.getRadius_() * 2);
                g2d.fillOval(tmpEventSpot.getX_() - 500, tmpEventSpot.getY_() - 500, 1000, 1000);
                tmpEventSpot = tmpEventSpot.getNext_();
            }
            g2d.setStroke(new BasicStroke(300, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2d.setPaint(Color.red);
            if (centroidsX != null && centroidsY != null) {
                for (int r = 0; r < centroidsX.size(); r++) {
                    g2d.drawOval(centroidsX.get(r).intValue() - 500, centroidsY.get(r).intValue() - 500, 1000, 1000);
                }
            }
            g2d.setStroke(new BasicStroke(300, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
 
            g2d.setStroke(new BasicStroke(25, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
 
            if (showAmenities) {
                g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                for (Node amenity : Map.getInstance().getAmenityList()) {
                    g2d.setPaint(amenity.getNodeColor());
                    g2d.drawOval(amenity.getX() - 200000, amenity.getY() - 200000, 400000, 400000);
                    g2d.fillOval(amenity.getX() - 500, amenity.getY() - 500, 1000, 1000);
                }
            }
            if (grid != null) {
                int maxMinusmin = maxGridValue - minGridValue;
                int basicStroke = 100;
                double probability = 0;
                int strokeSize = 0;
                for (int a = 0; a < grid.length; a++) {
                    for (int b = 0; b < grid[0].length; b++) {
                        if (grid[a][b] > 0)
                            g2d.setPaint(new Color(255, 0, 0));
                        else
                            g2d.setPaint(new Color(0, 255, 0));
                        probability = ((double) grid[a][b] - minGridValue) / maxMinusmin;
                        strokeSize = (int) (basicStroke + probability * (gridSize_ - basicStroke));
                        g2d.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        g2d.drawRect(a * gridSize_ - (gridSize_ - strokeSize) / 2, b * gridSize_ - (gridSize_ - strokeSize) / 2, gridSize_ - strokeSize, gridSize_ - strokeSize);
                    }
                }
            }
        } else {
            g2d.setColor(new Color(230, 230, 230));
            g2d.fillRect(0, 0, drawWidth_, drawHeight_);
        }
        g2d.dispose();
    }

    public synchronized void pan(char direction) {
        if (direction == 'u') {
            panCountY_ += 1;
            middleY_ -= (drawHeight_ / 2 / zoom_);
        } else if (direction == 'd') {
            panCountY_ -= 1;
            middleY_ += (drawHeight_ / 2 / zoom_);
        } else if (direction == 'l') {
            panCountX_ += 1;
            middleX_ -= (drawWidth_ / 2 / zoom_);
        } else {
            panCountX_ -= 1;
            middleX_ += (drawWidth_ / 2 / zoom_);
        }
        updateParams();
    }

    public synchronized void pan(double x, double y) {
        middleX_ += x;
        middleY_ += y;
        updateParams();
    }

    public synchronized void setMapZoom(double zoom) {
        if (zoom > 0.0000045 && zoom < 0.5) {
            zoom_ = zoom;
            updateParams();
            int size = (int) Math.round(1000 * zoom_);
            if (size < 4)
                size = 4;
            size = size - (size % 2);
            Graphics2D g2;
            BufferedImage tmp;
            BufferedImage tmp2;
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            int oldSize, curSize = blockingImage_.getWidth();
            if (size < blockingImage_.getWidth() / 2) {
                float weight = 1.0f / 9.0f;
                float[] elements = new float[9];
                for (int i = 0; i < 9; i++) {
                    elements[i] = weight;
                }
                Kernel blurKernel = new Kernel(3, 3, elements);
                scaledBlockingImage_ = gc.createCompatibleImage(blockingImage_.getWidth(), blockingImage_.getHeight(), Transparency.TRANSLUCENT);
                new ConvolveOp(blurKernel).filter(blockingImage_, scaledBlockingImage_);
                scaledSlipperyImage_ = gc.createCompatibleImage(slipperyImage_.getWidth(), slipperyImage_.getHeight(), Transparency.TRANSLUCENT);
                new ConvolveOp(blurKernel).filter(slipperyImage_, scaledSlipperyImage_);
                do {
                    oldSize = curSize;
                    if (curSize > size) {
                        curSize /= 2;
                        if (curSize < size)
                            curSize = size;
                    }
                    tmp = gc.createCompatibleImage(curSize, curSize, Transparency.TRANSLUCENT);
                    g2 = tmp.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(scaledBlockingImage_, 0, 0, curSize, curSize, 0, 0, oldSize, oldSize, null);
                    g2.dispose();
                    scaledBlockingImage_ = tmp;
                    tmp2 = gc.createCompatibleImage(curSize, curSize, Transparency.TRANSLUCENT);
                    g2 = tmp2.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(scaledSlipperyImage_, 0, 0, curSize, curSize, 0, 0, oldSize, oldSize, null);
                    g2.dispose();
                    scaledSlipperyImage_ = tmp2;
                } while (curSize > size);
            } else {
                scaledBlockingImage_ = gc.createCompatibleImage(size, size, Transparency.TRANSLUCENT);
                g2 = scaledBlockingImage_.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(blockingImage_, 0, 0, size, size, null);
                g2.dispose();
                scaledSlipperyImage_ = gc.createCompatibleImage(size, size, Transparency.TRANSLUCENT);
                g2 = scaledSlipperyImage_.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(slipperyImage_, 0, 0, size, size, null);
                g2.dispose();
            }
        }
    }

    public synchronized void updateParams() {
        if (map_.getReadyState() == true) {
            transform_.setToScale(zoom_, zoom_);
            transform_.translate(-middleX_ + (drawWidth_ / (zoom_ * 2)), -middleY_ + (drawHeight_ / (zoom_ * 2)));
            lastOverdrawn_ = currentOverdrawn_;
            currentOverdrawn_ = false;
            int addValue = Vehicle.getMaximumCommunicationDistance();
            if (Vehicle.getMixZoneRadius() > addValue)
                addValue = Vehicle.getMixZoneRadius();
            long tmp = (long) StrictMath.floor(middleX_ - (drawWidth_ / (zoom_ * 2))) - addValue;
            if (tmp < 0) {
                mapMinX_ = 0;
                currentOverdrawn_ = true;
            } else if (tmp < Integer.MAX_VALUE)
                mapMinX_ = (int) tmp;
            else
                mapMinX_ = Integer.MAX_VALUE;
            tmp = (long) StrictMath.ceil(middleX_ + (drawWidth_ / (zoom_ * 2))) + addValue;
            if (tmp < 0)
                mapMaxX_ = 0;
            else {
                if (tmp > map_.getMapWidth())
                    currentOverdrawn_ = true;
                if (tmp < Integer.MAX_VALUE)
                    mapMaxX_ = (int) tmp;
                else
                    mapMaxX_ = Integer.MAX_VALUE;
            }
            tmp = (long) StrictMath.floor(middleY_ - (drawHeight_ / (zoom_ * 2))) - addValue;
            if (tmp < 0) {
                mapMinY_ = 0;
                currentOverdrawn_ = true;
            } else if (tmp < Integer.MAX_VALUE)
                mapMinY_ = (int) tmp;
            else
                mapMinY_ = Integer.MAX_VALUE;
            tmp = (long) StrictMath.ceil(middleY_ + (drawHeight_ / (zoom_ * 2))) + addValue;
            if (tmp < 0)
                mapMaxY_ = 0;
            else {
                if (tmp > map_.getMapHeight())
                    currentOverdrawn_ = true;
                if (tmp < Integer.MAX_VALUE)
                    mapMaxY_ = (int) tmp;
                else
                    mapMaxY_ = Integer.MAX_VALUE;
            }
            Region tmpregion = map_.getRegionOfPoint(mapMinX_, mapMinY_);
            regionMinX_ = tmpregion.getX();
            regionMinY_ = tmpregion.getY();
            tmpregion = map_.getRegionOfPoint(mapMaxX_, mapMaxY_);
            regionMaxX_ = tmpregion.getX();
            regionMaxY_ = tmpregion.getY();
        }
    }

    public void ReRender(boolean fullRender, boolean forceRenderNow) {
        if (fullRender)
            scheduleFullRender_ = true;
        if (drawArea_ != null) {
            if (!simulationRunning_ || forceRenderNow) {
                if (scheduleFullRender_) {
                    scheduleFullRender_ = false;
                    drawArea_.prepareBufferedImages();
                }
                doPaintInitializedBySimulation_ = true;
                drawArea_.repaint();
            }
        }
    }

    public void moveCamera(double middleX, double middleY, double zoom) {
        middleX_ = middleX;
        middleY_ = middleY;
        setMapZoom(zoom);
    }

    public double getMiddleX() {
        return middleX_;
    }

    public double getMiddleY() {
        return middleY_;
    }

    public double getMapZoom() {
        return zoom_;
    }

    public int getTimePassed() {
        return timePassed_;
    }

    public AffineTransform getTransform() {
        return transform_;
    }

    public void notifySimulationRunning(boolean running) {
        simulationRunning_ = running;
        //System.err.println("simulationRunning_ ==== "+simulationRunning_);
    }

    public synchronized void setMarkedStreet(Street markedStreet) {
        markedStreet_ = markedStreet;
    }

    public synchronized void setMarkedVehicle(Vehicle markedVehicle) {
        markedVehicle_ = markedVehicle;
    }

    public synchronized Vehicle getMarkedVehicle() {
        return markedVehicle_;
    }

    public synchronized void setAttackerVehicle(Vehicle attackerVehicle) {
        attackerVehicle_ = attackerVehicle;
    }

    public synchronized Vehicle getAttackerVehicle() {
        return attackerVehicle_;
    }

    public synchronized void setMiddle(int x, int y) {
        middleX_ = x;
        middleY_ = y;
        updateParams();
    }

    public void setDrawArea(DrawingArea drawArea) {
        drawArea_ = drawArea;
    }

    public void setDrawHeight(int drawHeight) {
        drawHeight_ = drawHeight;
    }

    public void setDrawWidth(int drawWidth) {
        drawWidth_ = drawWidth;
    }

    public void setTimePassed(int timePassed) {
        timePassed_ = timePassed;
    }

    public void setBarrierForSimulationMaster(CyclicBarrier barrier) {
        barrierForSimulationMaster_ = barrier;
    }

    public void setHighlightNodes(boolean highlightNodes) {
        highlightAllNodes_ = highlightNodes;
    }

    public void setHighlightCommunication(boolean highlightCommunication) {
        highlightCommunication_ = highlightCommunication;
        System.out.println("highlightCommunication_ === "+highlightCommunication_);
    }

    public void setHideMixZones(boolean hideMixZones) {
        hideMixZones_ = hideMixZones;
    }

    public void setDisplayVehicleIDs(boolean displayVehicleIDs) {
        displayVehicleIDs_ = displayVehicleIDs;
    }

    public void setShowAllBlockings(boolean showAllBlockings) {
        showAllBlockings_ = showAllBlockings;
    }

    public void setShowBeaconMonitorZone(boolean showBeaconMonitorZone) {
        showBeaconMonitorZone_ = showBeaconMonitorZone;
    }

    public void setMonitoredBeaconZoneVariables(int beaconMonitorMinX, int beaconMonitorMaxX, int beaconMonitorMinY, int beaconMonitorMaxY) {
        beaconMonitorMinX_ = beaconMonitorMinX;
        beaconMonitorMaxX_ = beaconMonitorMaxX;
        beaconMonitorMinY_ = beaconMonitorMinY;
        beaconMonitorMaxY_ = beaconMonitorMaxY;
    }

    public void setShowVehicles(boolean showVehicles) {
        showVehicles_ = showVehicles;
    }

    public boolean isShowVehicles() {
        return showVehicles_;
    }

    public void setShowMixZones(boolean showMixZones) {
        showMixZones_ = showMixZones;
    }

    public boolean isShowMixZones() {
        return showMixZones_;
    }

    public void setAutoAddMixZones(boolean autoAddMixZones) {
        autoAddMixZones_ = autoAddMixZones;
    }

    public boolean isAutoAddMixZones() {
        return autoAddMixZones_;
    }

 

    public Vehicle getAttackedVehicle() {
        return attackedVehicle_;
    }

    public void setAttackedVehicle(Vehicle attackedVehicle_) {
        this.attackedVehicle_ = attackedVehicle_;
    }

    public boolean isShowAttackers() {
        return showAttackers_;
    }

    public void setShowAttackers(boolean showAttackers_) {
        this.showAttackers_ = showAttackers_;
    }

    public boolean isConsoleStart() {
        return consoleStart;
    }

    public void setConsoleStart(boolean consoleStart) {
        this.consoleStart = consoleStart;
    }

    public void setMarkedJunction_(Junction markedJunction_) {
        this.markedJunction_ = markedJunction_;
    }

    public Junction getMarkedJunction_() {
        return markedJunction_;
    }

    public ArrayList<String> getLocationInformationMix() {
        return locationInformationMix_;
    }

    public void setLocationInformationMix(ArrayList<String> locationInformation) {
        this.locationInformationMix_ = locationInformation;
    }

    public ArrayList<String> getLocationInformationSilentPeriod_() {
        return locationInformationSilentPeriod_;
    }

    public void setLocationInformationSilentPeriod_(ArrayList<String> locationInformationSilentPeriod_) {
        this.locationInformationSilentPeriod_ = locationInformationSilentPeriod_;
    }

    public int getMixZoneAmount() {
        return mixZoneAmount;
    }

    public void setMixZoneAmount(int mixZoneAmount) {
        this.mixZoneAmount = mixZoneAmount;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public int getMinGridValue() {
        return minGridValue;
    }

    public void setMinGridValue(int minGridValue) {
        this.minGridValue = minGridValue;
    }

    public int getMaxGridValue() {
        return maxGridValue;
    }

    public void setMaxGridValue(int maxGridValue) {
        this.maxGridValue = maxGridValue;
    }

    public int getGridSize_() {
        return gridSize_;
    }

    public void setGridSize_(int gridSize_) {
        this.gridSize_ = gridSize_;
    }

    public boolean isShowPenaltyConnections_() {
        return showPenaltyConnections_;
    }

    public void setShowPenaltyConnections_(boolean showPenaltyConnections_) {
        this.showPenaltyConnections_ = showPenaltyConnections_;
    }

    public boolean isShowKnownVehiclesConnections_() {
        return showKnownVehiclesConnections_;
    }

    public void setShowKnownVehiclesConnections_(boolean showKnownVehiclesConnections_) {
        this.showKnownVehiclesConnections_ = showKnownVehiclesConnections_;
    }

    public ArrayList<Double> getCentroidsX() {
        return centroidsX;
    }

    public void setCentroidsX(ArrayList<Double> centroidsX) {
        this.centroidsX = centroidsX;
    }

    public ArrayList<Double> getCentroidsY() {
        return centroidsY;
    }

    public void setCentroidsY(ArrayList<Double> centroidsY) {
        this.centroidsY = centroidsY;
    }
 

    public void setShowAllClusters(boolean showAllClusters) {
        this.showAllClusters = showAllClusters;
    }

    public boolean getMDSMode_() {
        return MDSMode_;
    }

    public void setMDSMode_(boolean mDSMode_) {
        MDSMode_ = mDSMode_;
    }

    public ArrayList<String> getLocationInformationMDS_() {
        return locationInformationMDS_;
    }

    public void setLocationInformationMDS_(ArrayList<String> locationInformationMDS_) {
        this.locationInformationMDS_ = locationInformationMDS_;
    }

    public int getDrawWidth_() {
        return drawWidth_;
    }

    public void setDrawWidth_(int drawWidth_) {
        this.drawWidth_ = drawWidth_;
    }

    public int getDrawHeight_() {
        return drawHeight_;
    }

    public void setDrawHeight_(int drawHeight_) {
        this.drawHeight_ = drawHeight_;
    }
}
