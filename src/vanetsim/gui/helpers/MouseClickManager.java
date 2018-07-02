package vanetsim.gui.helpers;

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import vanetsim.VanetSimStart;
import vanetsim.gui.DrawingArea;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.EditControlPanel;
import vanetsim.gui.controlpanels.ReportingControlPanel;
import vanetsim.gui.controlpanels.SimulateControlPanel;
import vanetsim.localization.Messages;
import vanetsim.map.MapHelper;
import vanetsim.map.Node;
import vanetsim.map.Street;
import vanetsim.routing.WayPoint;
import vanetsim.scenario.Vehicle;

public final class MouseClickManager extends Thread {

    private static final MouseClickManager INSTANCE = new MouseClickManager();

    private static final int INFORMATION_REFRESH_INTERVAL = 800;

    private static final int DRAG_ACTIVATION_INTERVAL = 140;

    private static final DecimalFormat INTEGER_FORMAT_FRACTION = new DecimalFormat(",##0.00");

    private final EditControlPanel editPanel_ = VanetSimStart.getMainControlPanel().getEditPanel();

    private final ReportingControlPanel reportPanel_ = VanetSimStart.getMainControlPanel().getReportingPanel();

    private final StringBuilder informationText_ = new StringBuilder();

    private Cursor defaultCursor_ = new Cursor(Cursor.DEFAULT_CURSOR);

    private Cursor moveCursor_ = new Cursor(Cursor.MOVE_CURSOR);

    private DrawingArea drawArea_ = null;

    public boolean active_ = false;

    private long pressTime_ = 0;

    private int pressedX_ = -1;

    private int pressedY_ = -1;

    private long releaseTime_ = 0;

    private int releasedX_ = -1;

    private int releasedY_ = -1;

    private int waitingTime_ = -1;

    private Node markedNode_ = null;

    private Street markedStreet_ = null;

    private Vehicle markedVehicle_ = null;

    private String cachedStreetInformation_ = "";

    private Street cachedStreet_ = null;

    public static MouseClickManager getInstance() {
        return INSTANCE;
    }

    private MouseClickManager() {
    }

    public void setActive(boolean active) {
        active_ = active;
        if (active_ == false && drawArea_ != null) {
            waitingTime_ = -1;
            drawArea_.setCursor(defaultCursor_);
        }
    }

    public void setDrawArea(DrawingArea drawArea) {
        drawArea_ = drawArea;
    }

    public synchronized void signalPressed(int x, int y) {
        try {
            Point2D.Double mapposition_source = new Point2D.Double(0, 0);
            Renderer.getInstance().getTransform().inverseTransform(new Point2D.Double(x, y), mapposition_source);
            boolean onEditingTab;
            if (VanetSimStart.getMainControlPanel().getSelectedTabComponent() instanceof EditControlPanel)
                onEditingTab = true;
            else
                onEditingTab = false;
            if (editPanel_.getEditMode() && onEditingTab) {
                editPanel_.receiveMouseEvent((int) Math.round(mapposition_source.getX()), (int) Math.round(mapposition_source.getY()));
            } else if (reportPanel_.isInMonitoredMixZoneEditMode()) {
                reportPanel_.receiveMouseEvent((int) Math.round(mapposition_source.getX()), (int) Math.round(mapposition_source.getY()));
            } else {
                waitingTime_ = 0;
                pressedX_ = (int) StrictMath.floor(0.5 + mapposition_source.getX());
                pressedY_ = (int) StrictMath.floor(0.5 + mapposition_source.getY());
                pressTime_ = System.currentTimeMillis();
                releaseTime_ = pressTime_;
            }
        } catch (Exception e) {
        }
    }

    public synchronized void signalReleased(int x, int y) {
        boolean onEditingTab;
        if (VanetSimStart.getMainControlPanel().getSelectedTabComponent() instanceof EditControlPanel)
            onEditingTab = true;
        else
            onEditingTab = false;
        if ((!editPanel_.getEditMode() || !onEditingTab) && !reportPanel_.isInMonitoredMixZoneEditMode()) {
            try {
                Point2D.Double mapposition_source = new Point2D.Double(0, 0);
                Renderer.getInstance().getTransform().inverseTransform(new Point2D.Double(x, y), mapposition_source);
                waitingTime_ = -1;
                releasedX_ = (int) StrictMath.floor(0.5 + mapposition_source.getX());
                releasedY_ = (int) StrictMath.floor(0.5 + mapposition_source.getY());
                releaseTime_ = System.currentTimeMillis();
                if (drawArea_ != null)
                    drawArea_.setCursor(defaultCursor_);
            } catch (Exception e) {
            }
        }
    }

    public void cleanMarkings() {
        markedVehicle_ = null;
        markedStreet_ = null;
        markedNode_ = null;
    }

    private final synchronized String createInformationString() {
        double dx, dy;
        WayPoint tmpWayPoint;
        informationText_.setLength(0);
        if (markedStreet_ == cachedStreet_) {
            informationText_.append(cachedStreetInformation_);
        } else {
            if (markedStreet_ != null) {
                informationText_.append(Messages.getString("MouseClickManager.streetInformation"));
                informationText_.append(Messages.getString("MouseClickManager.streetName"));
                informationText_.append(markedStreet_.getName());
                informationText_.append("\n");
                informationText_.append(Messages.getString("MouseClickManager.streetLength"));
                informationText_.append(INTEGER_FORMAT_FRACTION.format(markedStreet_.getLength() / 100));
                informationText_.append(" m\n");
                informationText_.append(Messages.getString("MouseClickManager.streetSpeed"));
                informationText_.append(INTEGER_FORMAT_FRACTION.format(markedStreet_.getSpeed() / 100000.0 * 3600));
                informationText_.append(" km/h\n");
                informationText_.append(Messages.getString("MouseClickManager.lanesPerDirection"));
                informationText_.append(markedStreet_.getLanesCount());
                informationText_.append("\n");
                informationText_.append(Messages.getString("MouseClickManager.streetStart"));
                informationText_.append(markedStreet_.getStartNode().getX());
                informationText_.append(" (x), ");
                informationText_.append(markedStreet_.getStartNode().getY());
                informationText_.append(" (y)");
                if (markedStreet_.getStartNode().getJunction() != null)
                    informationText_.append(Messages.getString("MouseClickManager.junction"));
                informationText_.append("\n");
                informationText_.append(Messages.getString("MouseClickManager.crossingsOutgoings"));
                informationText_.append(markedStreet_.getStartNode().getCrossingStreetsCount());
                informationText_.append("/");
                informationText_.append(markedStreet_.getStartNode().getOutgoingStreetsCount());
                informationText_.append("\n");
                informationText_.append(Messages.getString("MouseClickManager.streetEnd"));
                informationText_.append(markedStreet_.getEndNode().getX());
                informationText_.append(" (x),");
                informationText_.append(markedStreet_.getEndNode().getY());
                informationText_.append(" (y)");
                if (markedStreet_.getEndNode().getJunction() != null)
                    informationText_.append(Messages.getString("MouseClickManager.junction"));
                informationText_.append("\n");
                informationText_.append(Messages.getString("MouseClickManager.crossingsOutgoings"));
                informationText_.append(markedStreet_.getEndNode().getCrossingStreetsCount());
                informationText_.append("/");
                informationText_.append(markedStreet_.getEndNode().getOutgoingStreetsCount());
                informationText_.append("\n");
            }
            cachedStreet_ = markedStreet_;
            cachedStreetInformation_ = informationText_.toString();
        }
        if (markedVehicle_ != null) {
            if (informationText_.length() != 0)
                informationText_.append("\n");
            informationText_.append(Messages.getString("MouseClickManager.vehicleInformation"));
            informationText_.append(Messages.getString("MouseClickManager.vehicleID"));
            informationText_.append(markedVehicle_.getHexID());
            informationText_.append("\n");
            informationText_.append(Messages.getString("MouseClickManager.vehicleStart"));
            informationText_.append(markedVehicle_.getStartPoint().getX());
            informationText_.append(" (x), ");
            informationText_.append(markedVehicle_.getStartPoint().getY());
            informationText_.append(" (y)\n");
            tmpWayPoint = markedVehicle_.getDestinations().peekFirst();
            if (tmpWayPoint != null) {
                informationText_.append(Messages.getString("MouseClickManager.vehicleNextDestination"));
                informationText_.append(tmpWayPoint.getX());
                informationText_.append(" (x), ");
                informationText_.append(tmpWayPoint.getY());
                informationText_.append(" (y)\n");
                dx = markedVehicle_.getX() - tmpWayPoint.getX();
                dy = markedVehicle_.getY() - tmpWayPoint.getY();
                informationText_.append(Messages.getString("MouseClickManager.linearDistance"));
                informationText_.append(INTEGER_FORMAT_FRACTION.format(Math.sqrt(dx * dx + dy * dy) / 100));
                informationText_.append(" m\n");
            }
            informationText_.append(Messages.getString("MouseClickManager.vehiclesCurrentSpeed"));
            informationText_.append(INTEGER_FORMAT_FRACTION.format(markedVehicle_.getCurSpeed() / 100000.0 * 3600));
            informationText_.append(" km/h\n");
            informationText_.append(Messages.getString("MouseClickManager.travelTime"));
            informationText_.append(INTEGER_FORMAT_FRACTION.format(markedVehicle_.getTotalTravelTime() / 1000.0));
            informationText_.append(" s\n");
            informationText_.append(Messages.getString("MouseClickManager.travelDistance"));
            informationText_.append(INTEGER_FORMAT_FRACTION.format(markedVehicle_.getTotalTravelDistance() / 100));
            informationText_.append(" m\n");
            informationText_.append(Messages.getString("MouseClickManager.knownVehicles"));
            informationText_.append(markedVehicle_.getKnownVehiclesList().getSize());
            informationText_.append("\n");
            informationText_.append(Messages.getString("MouseClickManager.knownMessages"));
            informationText_.append(markedVehicle_.getKnownMessages().getSize());
            informationText_.append(" (+ ");
            informationText_.append(markedVehicle_.getKnownMessages().getOldMessagesSize());
            informationText_.append(Messages.getString("MouseClickManager.old"));
            informationText_.append("\n");
            informationText_.append(Messages.getString("MouseClickManager.failedForwardMessages"));
            informationText_.append(markedVehicle_.getKnownMessages().getFailedForwardCount());
            informationText_.append("\n");
            informationText_.append(Messages.getString("MouseClickManager.knownPenalties"));
    
            informationText_.append("\n");
        }
        if (markedNode_ != null) {
        }
        return informationText_.toString();
    }

    public void run() {
        setName("MouseClickManager");
        Renderer renderer = Renderer.getInstance();
        SimulateControlPanel simulatePanel = VanetSimStart.getMainControlPanel().getSimulatePanel();
        int lastInformationRefresh = 0;
        int sleepTime;
        long time = 0;
        setPriority(Thread.MIN_PRIORITY);
        while (true) {
            time = System.nanoTime();
            if (active_ && drawArea_ != null) {
                synchronized (this) {
                    if (waitingTime_ > -1) {
                        if (waitingTime_ > DRAG_ACTIVATION_INTERVAL) {
                            drawArea_.setCursor(moveCursor_);
                            waitingTime_ = -1;
                        } else
                            waitingTime_ += 2;
                    }
                    if (releaseTime_ - pressTime_ > DRAG_ACTIVATION_INTERVAL) {
                        renderer.pan((pressedX_ - releasedX_) * 2, (pressedY_ - releasedY_) * 2);
                        ReRenderManager.getInstance().doReRender();
                        releaseTime_ = pressTime_;
                    } else if (releaseTime_ != pressTime_) {
                        int distance = (int) Math.round(Math.min(80 / renderer.getMapZoom(), 1000000));
                        if (distance < 3000)
                            distance = 3000;
                        markedStreet_ = MapHelper.findNearestStreet(pressedX_, pressedY_, distance, new double[2], new int[2]);
                        renderer.setMarkedStreet(markedStreet_);
                        markedVehicle_ = MapHelper.findNearestVehicle(pressedX_, pressedY_, distance, new long[1]);
                        renderer.setMarkedVehicle(markedVehicle_);
                        markedNode_ = MapHelper.findNearestNode(pressedX_, pressedY_, distance, new long[1]);
                        renderer.ReRender(false, false);
                        releaseTime_ = pressTime_;
                    }
                }
            }
            if (lastInformationRefresh < 0) {
                lastInformationRefresh = INFORMATION_REFRESH_INTERVAL;
                simulatePanel.setInformation(createInformationString());
            }
            if (active_)
                sleepTime = 2;
            else
                sleepTime = 50;
            lastInformationRefresh -= sleepTime;
            time = ((System.nanoTime() - time) / 1000000);
            if (time > 0)
                time = sleepTime - time;
            else
                time = sleepTime + time;
            if (time > 0 && time <= sleepTime) {
                try {
                    sleep(time);
                } catch (Exception e) {
                }
                ;
            }
        }
    }
}
