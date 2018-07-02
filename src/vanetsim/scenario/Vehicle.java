package vanetsim.scenario;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.ReportingControlPanel;
import vanetsim.gui.helpers.GeneralLogWriter;
import vanetsim.gui.helpers.PrivacyLogWriter;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.map.Street;
import vanetsim.routing.A_Star.A_Star_Algorithm;
import vanetsim.routing.RoutingAlgorithm;
import vanetsim.routing.WayPoint;
import vanetsim.scenario.events.BlockingObject;
import vanetsim.scenario.messages.Message;
 

public class Vehicle extends LaneObject {
    
   public static  Vehicule TV[];   //Fclustering
    //public static vanetsim.scenario.Vehicle  TV1[];  //Aprove
    public static  Vehicule  TV2[];  //Wca
    public static  Vehicule  TV3[];  //Vwca
    
    public static long TR[][];
    

    private static final Map MAP = Map.getInstance();

    private static final ReportingControlPanel REPORT_PANEL = getReportingPanel();

    private static final int KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL = 5000;

    private static final int KNOWN_RSUS_TIMEOUT_CHECKINTERVAL = 5000;

    private static final int KNOWN_PENALTIES_TIMEOUT_CHECKINTERVAL = 30000;

    private static final int MESSAGE_INTERVAL = 5000;

    private static final int LANE_CHANGE_INTERVAL = 5000;

    private static final int TIME_FOR_JAM = 5000;

    private static final int PENALTY_MESSAGE_RADIUS = 50000;

    private static final int PENALTY_FAKE_MESSAGE_RADIUS = 50000;

    private static final int PENALTY_EVA_MESSAGE_RADIUS = 50000;

    private static final int PENALTY_MESSAGE_VALID = 10000;

    private static final int PENALTY_MESSAGE_VALUE = 2000000;

    private static final int PENALTY_VALID = 5000;

    private static final int MIX_CHECK_INTERVAL = 1000;

    private static final int ATTACKER_INTERVAL = 50;

    private static final int SPEED_FLUCTUATION_CHECKINTERVAL = 5000;

    private static final int SPEED_NO_FLUCTUATION_CHECKINTERVAL = 10000;

    private static final int SPEED_FLUCTUATION_MAX = 6;

    private static final Random RANDOM = new Random(1L);

    private static final RoutingAlgorithm ROUTING_ALGO = new A_Star_Algorithm();
    
    private int vehId_ = 0;
    
    private static String vehicleStat_ = "Inactif";
   
    //private static int arrivalTime_ = 0;
    private int arrivalTime_ = 0;

    private static int routingMode_ = 1;

    private static int minTravelTimeForRecycling_ = 60000;

    private static boolean communicationEnabled_ = true;

    private static boolean beaconsEnabled_ = true;

    private static boolean mixZonesEnabled_ = true;

    private static boolean mixZonesFallbackEnabled_ = true;

    private static boolean mixZonesFallbackFloodingOnly_ = true;

    private static int mixZoneRadius_ = 10000;

    private static int maxMixZoneRadius_ = 0;

    private static int communicationInterval_ = 160;

    private static int beaconInterval_ = 240;

    private static int maximumCommunicationDistance_ = 0;

    private static Region[][] regions_;

    private static boolean beaconMonitorEnabled_ = false;

    private static int beaconMonitorMinX_ = -1;

    private static int beaconMonitorMaxX_ = -1;

    private static int beaconMonitorMinY_ = -1;

    private static int beaconMonitorMaxY_ = -1;

    private static boolean recyclingEnabled_ = true;

 
    private static boolean attackerDataLogged_ = false;

    private static boolean attackerEncryptedDataLogged_ = false;

    private static boolean privacyDataLogged_ = false;

    private static long attackedVehicleID_ = 0;

    private static int reRouteTime_ = -1;

    private static boolean encryptedBeaconsInMix_ = false;

    private static int steadyIDCounter = 0;

    private static int TIME_BETWEEN_SILENT_PERIODS = 10000;

    private static int TIME_OF_SILENT_PERIODS = 2000;

    private static boolean silent_period = false;

    private static boolean silentPeriodsOn = false;

    private static int TIME_TO_PSEUDONYM_CHANGE = 3000;

    private static int SLOW_SPEED_LIMIT = (int) (30 * 100000.0 / 3600);

    private static boolean slowOn = false;

    private static boolean idsActivated = false;

    private static int fakeMessagesInterval_ = 10000;

    private static boolean directCommunicationMode_ = true;

    private static int WAIT_TO_SEND_RHCN_ = 4;

    private int waitToSendRHCNCounter_ = -1;

    public ArrayDeque<WayPoint> originalDestinations_;

    private final WayPoint startingWayPoint_;

    private int vehicleLength_;

    private int maxSpeed_;

    private Color color_;

    private int brakingRate_;

    private int accelerationRate_;

    private boolean emergencyVehicle_;

    private final int maxBrakingDistance_;

    private int speedDeviation_;

    private final KnownMessages knownMessages_ = new KnownMessages(this);

    private final KnownVehiclesList knownVehiclesList_ = new KnownVehiclesList();

    
    private final KnownEventSourcesList knownEventSourcesList_ = new KnownEventSourcesList(this.getID());

 
    private boolean wiFiEnabled_;

    private final Random ownRandom_;

    private long ID_;
    
    public double predictedPosition;

    public int steadyID_;

    private ArrayDeque<WayPoint> destinations_;

    private double newSpeed_;

    private int newLane_ = 1;

    private boolean active_ = false;

    private Street[] routeStreets_;

    private boolean[] routeDirections_;

    private int routePosition_;

    private int curBrakingDistance_;

    private double speedAtLastBrakingDistanceCalculation_ = 0;

    private boolean isInMixZone_ = false;

    private Node junctionAllowed_ = null;

    private int maxCommDistance_;

    private Region curRegion_;

    private int curWaitTime_;

    private int totalTravelTime_;

    private long totalTravelDistance_;

    private boolean brakeForDestination_ = false;

    private int brakeForDestinationCountdown_ = Integer.MAX_VALUE;

    private int destinationCheckCountdown_ = 0;

    private int laneChangeCountdown = 0;

    private int communicationCountdown_;

    private int beaconCountdown_;

    private int mixCheckCountdown_;

    private int knownVehiclesTimeoutCountdown_;

    private int knownRSUsTimeoutCountdown_;

    private int knownPenaltiesTimeoutCountdown_;

    private int speedFluctuationCountdown_;

    private boolean isBraking_ = false;

    private double fluctuation_ = 0;

    private int lastRHCNMessageCreated = 0;

    private int lastPCNMessageCreated = 0;

    private int lastPCNFORWARDMessageCreated = 0;

    private int lastEVAMessageCreated = 0;

    private int stopTime_ = 0;

    private int messagesCounter_ = 0;

    private int pcnMessagesCreated_ = 0;

    private int pcnForwardMessagesCreated_ = 0;

    private int evaMessagesCreated_ = 0;

    private int evaForwardMessagesCreated_ = 0;

    private int rhcnMessagesCreated_ = 0;

    private int eeblMessagesCreated_ = 0;

    private int fakeMessagesCreated_ = 0;

    private int IDsChanged_ = 0;

    private boolean mayBeRecycled_ = false;

    private boolean doNotRecycle_ = false;

    private Boolean attackerWasInMix = false;

    private Boolean attackedWasInMix = false;

    private Boolean firstContact = false;

    private Node curMixNode_ = null;

    private boolean waitingForSignal_ = false;

    private int timeDistance_ = 1000;

    private int politeness_ = 0;

    private boolean silentPeriod = false;

    private String savedBeacon1 = "";

    private String savedBeacon2 = "";

    private int logNextBeacons = 0;

    private boolean isInSlow = false;

    private boolean changedPseudonymInSlow = false;

    private int slowTimestamp = 0;

    private boolean slowBeaconsLogged = false;

    private boolean vehicleJustStartedInSlow = true;

    private boolean moveOutOfTheWay_ = false;

    private boolean forwardMessage_ = false;

    private boolean fakingMessages_ = false;

    private String fakeMessageType_ = "";

    private int fakeMessageCountdown_ = 0;

    private int fakeMessageCounter_ = 0;

 
    private static int emergencyBrakingInterval_ = 600000;

    private boolean emergencyBraking_ = false;

    private int emergencyBrakingDuration_ = 3000;

    private int emergencyBrakingCountdown_ = -1;

    private boolean EEBLmessageIsCreated_ = false;

    private int emergencyBeacons = -1;

    private boolean drivingOnTheSide_ = false;

    private Vehicle waitingForVehicle_ = null;

    private boolean passingBlocking_ = false;

    private boolean inTrafficJam_ = false;

    private boolean checkIDSProcessors_ = false;

    private int spamCounter_ = 0;

    private int EVAMessageDelay_ = 3;

    private static int minEVAMessageDelay_ = 1;

    private static int maxEVAMessageDelay_ = 10;

    private boolean logBeaconsAfterEvent_ = false;

    private String beaconString_ = "";

    private int amountOfLoggedBeacons_ = 0;

    private boolean logJunctionFrequency_ = false;

    private boolean probeDataActive_ = false;

    private int PROBE_DATA_TIME_INTERVAL = 80;

    private int amountOfProbeData_ = 3;

    private int startAfterTime_ = 60000;

    private int curProbeDataInterval_ = 0;
    


    public Vehicle(ArrayDeque<WayPoint> destinations, int vehicleLength, int maxSpeed, int maxCommDist, boolean wiFiEnabled, boolean emergencyVehicle, int brakingRate, int accelerationRate, int timeDistance, int politeness, int speedDeviation, Color color, boolean fakingMessages, String fakeMessageType, int arrivalTime) throws ParseException {
        if (destinations != null && destinations.size() > 1) 
        {
            originalDestinations_ = destinations;
            //JOptionPane.showConfirmDialog(null, "destinations avant X"+destinations.getFirst().getX());
            //JOptionPane.showConfirmDialog(null, "destinations avant Y"+destinations.getFirst().getY());
            
            destinations_ = originalDestinations_.clone();
            
            ID_ = RANDOM.nextLong();
            steadyID_ = steadyIDCounter++;           
            arrivalTime_ = arrivalTime;
            vehicleLength_ = vehicleLength;
            maxSpeed_ = maxSpeed;
            emergencyVehicle_ = emergencyVehicle;
            color_ = color;
            brakingRate_ = brakingRate;
            accelerationRate_ = accelerationRate;
            timeDistance_ = timeDistance;
            politeness_ = politeness;
            maxBrakingDistance_ = maxSpeed_ + maxSpeed_ * maxSpeed_ / (2 * brakingRate_);
            startingWayPoint_ = destinations_.pollFirst();
            wiFiEnabled_ = wiFiEnabled;
            speedDeviation_ = speedDeviation;
            ownRandom_ = new Random(RANDOM.nextLong());
            curX_ = startingWayPoint_.getX();
            curY_ = startingWayPoint_.getY();
            curPosition_ = startingWayPoint_.getPositionOnStreet();
            //System.err.println("startingWayPoint_ X "+startingWayPoint_.getX()+" -- startingWayPoint_ Y "+startingWayPoint_.getY());
            curStreet_ = startingWayPoint_.getStreet();
            curWaitTime_ = startingWayPoint_.getWaittime();
            fakingMessages_ = fakingMessages;
            fakeMessageType_ = fakeMessageType;
            curRegion_ = Map.getInstance().getRegionOfPoint(curX_, curY_);
            maxCommDistance_ = maxCommDist;
            curSpeed_ = brakingRate_ / 2;
            newSpeed_ = curSpeed_;
            int pos;
            if (curStreet_.isOneway()) {
                while (!destinations_.isEmpty() && (destinations_.peekFirst().getStreet() == curStreet_ || !calculateRoute(true, false))) {
                    curWaitTime_ = destinations_.pollFirst().getWaittime();
                }
            } else {
                while (!destinations_.isEmpty() && (destinations_.peekFirst().getStreet() == curStreet_ || !calculateRoute(false, false))) {
                    curWaitTime_ = destinations_.pollFirst().getWaittime();
                }
            }
            if (destinations_.size() == 0)
                throw new ParseException(Messages.getString("Vehicle.errorNotEnoughDestinations"), 0);
            if (curWaitTime_ == 0) { // "Here important"
                active_ = true;
                /*bricol*/
                vehicleStat_ = "Actif";
                pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
                vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
                
                curStreet_.addLaneObject(this, curDirection_);
            }
            calculatePosition();
                                //----------------------------------------------------------

            
            beaconCountdown_ = (int) Math.round(curPosition_) % beaconInterval_;
            communicationCountdown_ = (int) Math.round(curPosition_) % communicationInterval_;
            mixCheckCountdown_ = (int) Math.round(curPosition_) % MIX_CHECK_INTERVAL;
            knownVehiclesTimeoutCountdown_ = (int) Math.round(curPosition_) % KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL;
            knownPenaltiesTimeoutCountdown_ = (int) Math.round(curPosition_) % KNOWN_PENALTIES_TIMEOUT_CHECKINTERVAL;
            knownRSUsTimeoutCountdown_ = (int) Math.round(curPosition_) % KNOWN_RSUS_TIMEOUT_CHECKINTERVAL;
            speedFluctuationCountdown_ = (int) Math.round(curPosition_) % SPEED_FLUCTUATION_CHECKINTERVAL;
            fakeMessageCountdown_ = (int) Math.round(curPosition_) % fakeMessagesInterval_;
            emergencyBrakingCountdown_ = ownRandom_.nextInt(emergencyBrakingInterval_) + 1;
            EVAMessageDelay_ = minEVAMessageDelay_ + ownRandom_.nextInt(maxEVAMessageDelay_);
        } else
            throw new ParseException(Messages.getString("Vehicle.errorNotEnoughDestinations"), 0);
    }
//------------------------------------------------------------------------------
    public static int GetIndice(DefaultTableModel T,int idfix)//Saliha à ajouter cette fonction
    {
        int id=-1,i=0;
        boolean IndexFound=false;
        //System.err.println("idfix   "+idfix);
                    //for(int i=0;i<T.getRowCount();i++)
                    while ((i<T.getRowCount())&&(IndexFound==false))
                    {
                   
                        //System.err.println("i= "+i+"    Ident S : "+Integer.parseInt(T.getValueAt(i,1).toString())+" --  idfix"+idfix);
                        if(Integer.parseInt(T.getValueAt(i,1).toString()) == idfix) 
                        {
                        id =i;
                        IndexFound = true;
                        }
                        else i++;
                    }
                    
        
        return id;
    }
//------------------------------------------------------------------------------
    public boolean calculateRoute(boolean careAboutDirection, boolean isReroute) {
        try {
            WayPoint nextPoint = destinations_.peekFirst();
            if (curStreet_ == nextPoint.getStreet()) {
                boolean neededDirection;
                if (curPosition_ < nextPoint.getPositionOnStreet())
                    neededDirection = true;
                else
                    neededDirection = false;
                if (!careAboutDirection || neededDirection == curDirection_) {
                    routeStreets_ = new Street[1];
                    routeStreets_[0] = curStreet_;
                    routeDirections_ = new boolean[1];
                    routeDirections_[0] = true;
                    routePosition_ = 0;
                    return true;
                }
            }
            int direction;
            if (!careAboutDirection)
                direction = 0;
            else if (curDirection_)
                direction = -1;
            else
                direction = 1;
            ArrayDeque<Node> routing = ROUTING_ALGO.getRouting(routingMode_, direction, curX_, curY_, curStreet_, curPosition_, nextPoint.getX(), nextPoint.getY(), nextPoint.getStreet(), nextPoint.getPositionOnStreet(), maxSpeed_);
            if (routing.size() > 0) {
                if (routing.size() == 1) {
                    routeStreets_ = new Street[2];
                    routeStreets_[0] = curStreet_;
                    routeStreets_[1] = nextPoint.getStreet();
                    routeDirections_ = new boolean[2];
                    if (routing.peekFirst() == curStreet_.getEndNode())
                        routeDirections_[0] = true;
                    else
                        routeDirections_[0] = false;
                    if (routing.peekFirst() == nextPoint.getStreet().getStartNode())
                        routeDirections_[1] = true;
                    else
                        routeDirections_[1] = false;
                    routePosition_ = 0;
                    return true;
                } else {
                    Node nextNode;
                    int i;
                    boolean usedDestination = false;
                    Street[] outgoingStreets;
                    Street tmpStreet = curStreet_, tmpStreet2;
                    routeStreets_ = new Street[routing.size() + 1];
                    routeDirections_ = new boolean[routing.size() + 1];
                    Iterator<Node> routeIterator = routing.iterator();
                    if (routing.peekFirst() == curStreet_.getEndNode())
                        curDirection_ = true;
                    else
                        curDirection_ = false;
                    routeStreets_[0] = curStreet_;
                    boolean tmpDirection;
                    if (routeIterator.next() == curStreet_.getEndNode())
                        tmpDirection = true;
                    else
                        tmpDirection = false;
                    routeDirections_[0] = tmpDirection;
                    routePosition_ = 1;
                    while (true) {
                        if (routeIterator.hasNext())
                            nextNode = routeIterator.next();
                        else if (!usedDestination) {
                            usedDestination = true;
                            if (nextPoint.getStreet() != tmpStreet) {
                                nextNode = nextPoint.getStreet().getStartNode();
                                if ((!tmpDirection && nextNode == tmpStreet.getStartNode()) || (tmpDirection && nextNode == tmpStreet.getEndNode()))
                                    nextNode = nextPoint.getStreet().getEndNode();
                            } else
                                break;
                        } else
                            break;
                        if (tmpDirection)
                            outgoingStreets = tmpStreet.getEndNode().getOutgoingStreets();
                        else
                            outgoingStreets = tmpStreet.getStartNode().getOutgoingStreets();
                        for (i = 0; i < outgoingStreets.length; ++i) {
                            tmpStreet2 = outgoingStreets[i];
                            if (tmpStreet2.getStartNode() == nextNode) {
                                tmpStreet = tmpStreet2;
                                tmpDirection = false;
                                break;
                            } else if (tmpStreet2.getEndNode() == nextNode) {
                                tmpStreet = tmpStreet2;
                                tmpDirection = true;
                                break;
                            }
                        }
                        routeStreets_[routePosition_] = tmpStreet;
                        routeDirections_[routePosition_] = tmpDirection;
                        ++routePosition_;
                    }
                    routePosition_ = 0;
                    destinationCheckCountdown_ = 0;
                    return true;
                }
            } else {
                if (!isReroute && destinations_.size() < 2) {
                    active_ = false;
                    /*bricol*/
                    vehicleStat_ = "HorsSystem";
                    curWaitTime_ = Integer.MIN_VALUE;
                    if (totalTravelTime_ >= minTravelTimeForRecycling_)
                        mayBeRecycled_ = true;
                }
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void adjustSpeedWithIDM(int timePerStep) {
        int pos;
        if (curWaitTime_ != 0 && curWaitTime_ != Integer.MIN_VALUE) {
            if (curWaitTime_ <= timePerStep) {
                curWaitTime_ = 0;
                active_ = true;
                /*bricol*/
                vehicleStat_ = "Actif";
                pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
                vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
                brakeForDestination_ = false;
                curStreet_.addLaneObject(this, curDirection_);
            } else
                curWaitTime_ -= timePerStep;
        }
        if (active_) {
            if (curWaitTime_ == 0 && curStreet_ != null) {
                newSpeed_ = 270;
                if (this.getCurStreet().getName().contains("Mittelweg"))
                    newSpeed_ = 27000;
            }
        }
    }

    public boolean checkLaneFreeMOBIL(int lane) {
        return true;
    }

    public void adjustSpeedWithSJTUTraceFiles(int timePerStep) {
        active_ = true;
        /*bricol*/
        vehicleStat_ = "Actif";
        int  pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
        vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
        curWaitTime_ = 0;
        curStreet_.addLaneObject(this, curDirection_);
        newSpeed_ = 1800;
    }

    public void adjustSpeedWithSanFranciscoTraceFiles(int timePerStep) {
        active_ = true;
        /*bricol*/
        vehicleStat_ = "Actif";
        int  pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
        vanetsim.scenario.Vehicle.TV[pos].etat="Actif";
        curWaitTime_ = 0;
        curStreet_.addLaneObject(this, curDirection_);
        newSpeed_ = 1800;
    }

    public void adjustSpeed(int timePerStep) {
        waitingForSignal_ = false;
        if (curWaitTime_ != 0 && curWaitTime_ != Integer.MIN_VALUE) {
            if (curWaitTime_ <= timePerStep) {
                curWaitTime_ = 0;
                active_ = true;
                /*bricol*/
                vehicleStat_ = "Actif";
                int  pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
                vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
                brakeForDestination_ = false;
                curStreet_.addLaneObject(this, curDirection_);
            } else
                curWaitTime_ -= timePerStep;
        }
        if (active_) {
            if (curWaitTime_ == 0 && curStreet_ != null) {
                if (curSpeed_ != speedAtLastBrakingDistanceCalculation_) {
                    speedAtLastBrakingDistanceCalculation_ = curSpeed_;
                    curBrakingDistance_ = (int) StrictMath.floor(0.5d + curSpeed_ + curSpeed_ * curSpeed_ / (2 * brakingRate_));
                    if (curBrakingDistance_ < 500)
                        curBrakingDistance_ = 500;
                }
                if (destinationCheckCountdown_ <= 0 && !brakeForDestination_) {
                    WayPoint destinationWayPoint = destinations_.peekFirst();
                    long dx = destinationWayPoint.getX() - curX_;
                    long dy = destinationWayPoint.getY() - curY_;
                    

                    
                    long distanceSquared = dx * dx + dy * dy;
                    if (distanceSquared < (long) maxBrakingDistance_ * maxBrakingDistance_ * 2) {
                        if (destinationWayPoint.getStreet() == curStreet_) {
                            if (distanceSquared <= (long) curBrakingDistance_ * curBrakingDistance_) {
                                if (brakeForDestinationCountdown_ > 1000)
                                    brakeForDestinationCountdown_ = 1000;
                                brakeForDestination_ = true;
                            } else
                                destinationCheckCountdown_ = (int) StrictMath.floor(0.5d + ((StrictMath.sqrt(distanceSquared) - maxBrakingDistance_) / maxSpeed_) * 1000);
                        } else {
                            double distance = 0, tmpPosition = curPosition_;
                            Street tmpStreet = curStreet_;
                            boolean tmpDirection = curDirection_;
                            int i;
                            int j = routeStreets_.length - 1;
                            for (i = routePosition_; i < j; ) {
                                if (tmpDirection)
                                    distance += tmpStreet.getLength() - tmpPosition;
                                else
                                    distance += tmpPosition;
                                ++i;
                                tmpDirection = routeDirections_[i];
                                tmpStreet = routeStreets_[i];
                                if (tmpDirection)
                                    tmpPosition = 0;
                                else
                                    tmpPosition = tmpStreet.getLength();
                            }
                            if (tmpDirection)
                                distance += destinations_.getFirst().getPositionOnStreet() - tmpPosition;
                            else
                                distance += tmpPosition - destinations_.getFirst().getPositionOnStreet();
                            if (distance <= curBrakingDistance_) {
                                if (brakeForDestinationCountdown_ > 1000)
                                    brakeForDestinationCountdown_ = 1000;
                                brakeForDestination_ = true;
                            } else if (distance > maxBrakingDistance_) {
                                destinationCheckCountdown_ = (int) StrictMath.floor(0.5d + (distance - maxBrakingDistance_) / maxSpeed_ * 1000);
                            }
                        }
                    } else
                        destinationCheckCountdown_ = (int) StrictMath.floor(0.5d + ((StrictMath.sqrt(distanceSquared) - maxBrakingDistance_) / maxSpeed_) * 1000);
                } else
                    destinationCheckCountdown_ -= timePerStep;
                int result = checkCurrentBraking(curLane_);
                boolean changedLane = false;
                laneChangeCountdown -= timePerStep;
                if (laneChangeCountdown < 0 && curLane_ == 0)
                    newLane_ = 1;
                if (laneChangeCountdown < 0 && result == 1) {
                    if (curLane_ > 1) {
                        curBrakingDistance_ += 2000;
                        int result2 = checkCurrentBraking(curLane_ - 1);
                        curBrakingDistance_ -= 2000;
                        if (result2 == 0 && checkLaneFree(curLane_ + 1)) {
                            newLane_ = curLane_ - 1;
                            changedLane = true;
                            laneChangeCountdown = LANE_CHANGE_INTERVAL;
                            result = 0;
                            moveOutOfTheWay_ = false;
                            drivingOnTheSide_ = false;
                        }
                    }
                    if (result == 1 && curStreet_.getLanesCount() > curLane_) {
                        curBrakingDistance_ += 2000;
                        int result2 = checkCurrentBraking(curLane_ + 1);
                        curBrakingDistance_ -= 2000;
                        if (result2 == 0 && checkLaneFree(curLane_ + 1)) {
                            newLane_ = curLane_ + 1;
                            changedLane = true;
                            laneChangeCountdown = LANE_CHANGE_INTERVAL;
                            result = 0;
                        }
                    }
                }
                boolean brakeOnce = false;
                if (result > 0) {
                    brakeOnce = true;
                }
                if (laneChangeCountdown < 0 && curLane_ > 1 && !changedLane && result == 0) {
                    if (checkLaneFree(curLane_ - 1)) {
                        newLane_ = curLane_ - 1;
                        changedLane = true;
                        laneChangeCountdown = LANE_CHANGE_INTERVAL;
                        moveOutOfTheWay_ = false;
                        drivingOnTheSide_ = false;
                    }
                }
                if (drivingOnTheSide_) {
                    if (next_ != null && next_.getClass().equals(Vehicle.class) && waitingForVehicle_ != null && ((Vehicle) next_).getID() == waitingForVehicle_.getID()) {
                        drivingOnTheSide_ = false;
                        waitingForVehicle_ = null;
                        laneChangeCountdown = 1000;
                    }
                }
                if (emergencyVehicle_)
                    newLane_ = curStreet_.getLanesCount();
                if (moveOutOfTheWay_ && !emergencyVehicle_) {
                    if (forwardMessage_ && waitingForVehicle_ != null) {
                        forwardMessage_ = false;
                        boolean tmpDirection2 = !curDirection_;
                        Street tmpStreet2 = curStreet_;
                        Street[] crossingStreets;
                        Node tmpNode;
                        int k, l = 0, destX = -1, destY = -1;
                        do {
                            ++l;
                            if (tmpDirection2) {
                                tmpNode = tmpStreet2.getStartNode();
                            } else {
                                tmpNode = tmpStreet2.getEndNode();
                            }
                            if (tmpNode.getJunction() != null) {
                                destX = tmpNode.getX();
                                destY = tmpNode.getY();
                                break;
                            }
                            crossingStreets = tmpNode.getCrossingStreets();
                            if (crossingStreets.length != 2) {
                                destX = tmpNode.getX();
                                destY = tmpNode.getY();
                                break;
                            }
                            for (k = 0; k < crossingStreets.length; ++k) {
                                if (crossingStreets[k] != tmpStreet2) {
                                    tmpStreet2 = crossingStreets[k];
                                    if (tmpStreet2.getStartNode() == tmpNode)
                                        tmpDirection2 = false;
                                    else
                                        tmpDirection2 = true;
                                    break;
                                }
                            }
                        } while (tmpStreet2 != curStreet_ && l < 10000);
                        if (destX != -1 && destY != -1) {
                            int direction = -1;
                            if (!curDirection_)
                                direction = 1;
                            int time = Renderer.getInstance().getTimePassed();
 
                            ++evaForwardMessagesCreated_;
                        }
                    }
                    if (newLane_ == curStreet_.getLanesCount() && ownRandom_.nextInt(100) == 0) {
                        drivingOnTheSide_ = true;
                        newLane_ = curLane_ - 1;
                        changedLane = true;
                        laneChangeCountdown = LANE_CHANGE_INTERVAL + 20000;
                        moveOutOfTheWay_ = false;
                    }
                }
                if ((curLane_ == 0 && curSpeed_ > 277) || emergencyBraking_) {
                    brakeOnce = true;
                }
                if (brakeForDestinationCountdown_ > 0 && brakeForDestination_) {
                    brakeForDestinationCountdown_ -= timePerStep;
                }
                if ((brakeForDestinationCountdown_ <= 0 && brakeForDestination_) || brakeOnce || isBraking_) {
                    if (isBraking_ && !(brakeOnce || (brakeForDestinationCountdown_ <= 0 && brakeForDestination_)))
                        newSpeed_ = curSpeed_ - (fluctuation_ * (double) timePerStep / 1000);
                    else
                        newSpeed_ = curSpeed_ - (brakingRate_ * (double) timePerStep / 1000);
                    if (!brakeOnce && newSpeed_ < brakingRate_ / 2)
                        newSpeed_ = brakingRate_ / 2;
                }
                if (!brakeForDestination_ && !brakeOnce && !isBraking_) {
                    if (curSpeed_ < (curStreet_.getSpeed() + speedDeviation_)) {
                        newSpeed_ = curSpeed_ + (accelerationRate_ * (double) timePerStep / 1000);
                    }
                }
                if (newSpeed_ > (maxSpeed_ + speedDeviation_))
                    newSpeed_ = (maxSpeed_ + speedDeviation_);
                else if (newSpeed_ < 0)
                    newSpeed_ = 0;
                if ((curStreet_.getSpeed() + speedDeviation_) > 0 && newSpeed_ > (curStreet_.getSpeed() + speedDeviation_) && this != Renderer.getInstance().getAttackerVehicle() && !emergencyVehicle_)
                    newSpeed_ = (curStreet_.getSpeed() + speedDeviation_);
            }
            if (speedFluctuationCountdown_ < 1) {
                isBraking_ = !isBraking_;
                if (isBraking_) {
                    fluctuation_ = ownRandom_.nextInt(SPEED_FLUCTUATION_MAX);
                    speedFluctuationCountdown_ += SPEED_FLUCTUATION_CHECKINTERVAL;
                } else {
                    speedFluctuationCountdown_ += SPEED_NO_FLUCTUATION_CHECKINTERVAL;
                    fluctuation_ = 0;
                }
            } else
                speedFluctuationCountdown_ -= timePerStep;
            if (EEBLmessageIsCreated_) {
                emergencyBrakingCountdown_ -= timePerStep;
                if (emergencyBrakingCountdown_ < 1) {
                    emergencyBraking_ = false;
                    EEBLmessageIsCreated_ = false;
                }
            }
            if (isWiFiEnabled() && communicationEnabled_) {
                if (knownMessages_.hasNewMessages()) {
                    knownMessages_.processMessages();
                }
                communicationCountdown_ -= timePerStep;
                if (communicationCountdown_ < 1)
                    knownMessages_.checkOutdatedMessages(true);
                knownPenaltiesTimeoutCountdown_ -= timePerStep;
 
                if (beaconsEnabled_) {
                    beaconCountdown_ -= timePerStep;
                    if (knownVehiclesTimeoutCountdown_ < 1) {
                        knownVehiclesList_.checkOutdatedVehicles();
                         
                        knownVehiclesTimeoutCountdown_ += KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL;
                    } else
                        knownVehiclesTimeoutCountdown_ -= timePerStep;
 
                }
                lastRHCNMessageCreated += timePerStep;
                lastPCNMessageCreated += timePerStep;
                lastPCNFORWARDMessageCreated += timePerStep;
                lastEVAMessageCreated += timePerStep;
                if (newSpeed_ == 0) {
                    stopTime_ += timePerStep;
                    if (stopTime_ > TIME_FOR_JAM && !waitingForSignal_) {
                        inTrafficJam_ = true;
                        if (lastPCNMessageCreated >= MESSAGE_INTERVAL) {
                            lastPCNMessageCreated = 0;
                            boolean tmpDirection = curDirection_;
                            Street tmpStreet = curStreet_;
                            Street[] crossingStreets;
                            Node tmpNode;
                            int i, j = 0, destX = -1, destY = -1;
                            do {
                                ++j;
                                if (tmpDirection) {
                                    tmpNode = tmpStreet.getStartNode();
                                } else {
                                    tmpNode = tmpStreet.getEndNode();
                                }
                                if (tmpNode.getJunction() != null) {
                                    destX = tmpNode.getX();
                                    destY = tmpNode.getY();
                                    break;
                                }
                                crossingStreets = tmpNode.getCrossingStreets();
                                if (crossingStreets.length != 2) {
                                    destX = tmpNode.getX();
                                    destY = tmpNode.getY();
                                    break;
                                }
                                for (i = 0; i < crossingStreets.length; ++i) {
                                    if (crossingStreets[i] != tmpStreet) {
                                        tmpStreet = crossingStreets[i];
                                        if (tmpStreet.getStartNode() == tmpNode)
                                            tmpDirection = false;
                                        else
                                            tmpDirection = true;
                                        break;
                                    }
                                }
                            } while (tmpStreet != curStreet_ && j < 10000);
                            if (destX != -1 && destY != -1) {
                                int direction = -1;
                                if (!curDirection_)
                                    direction = 1;
                                int time = Renderer.getInstance().getTimePassed();

                                ++pcnMessagesCreated_;
                            }
                        }
                    }
                } else {
                    inTrafficJam_ = false;
                    stopTime_ = 0;
                }
                if (mixZonesEnabled_) {
                    mixCheckCountdown_ -= MIX_CHECK_INTERVAL;
                    if (mixCheckCountdown_ <= 0) {
                        int MapMinX, MapMinY, MapMaxX, MapMaxY, RegionMinX, RegionMinY, RegionMaxX, RegionMaxY;
                        int i, j, k, size;
                        Node node;
                        long dx, dy, mixDistanceSquared = (long) getMaxMixZoneRadius() * getMaxMixZoneRadius();
                        boolean needsToMix = false;
                        long tmp = curX_ - getMaxMixZoneRadius();
                        if (tmp < 0)
                            MapMinX = 0;
                        else if (tmp < Integer.MAX_VALUE)
                            MapMinX = (int) tmp;
                        else
                            MapMinX = Integer.MAX_VALUE;
                        tmp = curX_ + getMaxMixZoneRadius();
                        if (tmp < 0)
                            MapMaxX = 0;
                        else if (tmp < Integer.MAX_VALUE)
                            MapMaxX = (int) tmp;
                        else
                            MapMaxX = Integer.MAX_VALUE;
                        tmp = curY_ - getMaxMixZoneRadius();
                        if (tmp < 0)
                            MapMinY = 0;
                        else if (tmp < Integer.MAX_VALUE)
                            MapMinY = (int) tmp;
                        else
                            MapMinY = Integer.MAX_VALUE;
                        tmp = curY_ + getMaxMixZoneRadius();
                        if (tmp < 0)
                            MapMaxY = 0;
                        else if (tmp < Integer.MAX_VALUE)
                            MapMaxY = (int) tmp;
                        else
                            MapMaxY = Integer.MAX_VALUE;
                        Region tmpregion = MAP.getRegionOfPoint(MapMinX, MapMinY);
                        RegionMinX = tmpregion.getX();
                        RegionMinY = tmpregion.getY();
                        tmpregion = MAP.getRegionOfPoint(MapMaxX, MapMaxY);
                        RegionMaxX = tmpregion.getX();
                        RegionMaxY = tmpregion.getY();
                        for (i = RegionMinX; i <= RegionMaxX; ++i) {
                            for (j = RegionMinY; j <= RegionMaxY; ++j) {
                                Node[] mixNodes = regions_[i][j].getMixZoneNodes();
                                size = mixNodes.length;
                                for (k = 0; k < size; ++k) {
                                    node = mixNodes[k];
                                    if (node.getX() >= curX_ - node.getMixZoneRadius() && node.getX() <= curX_ + node.getMixZoneRadius() && node.getY() >= curY_ - node.getMixZoneRadius() && node.getY() <= curY_ + node.getMixZoneRadius()) {
                                        dx = node.getX() - curX_;
                                        dy = node.getY() - curY_;
                                        mixDistanceSquared = node.getMixZoneRadius() * node.getMixZoneRadius();
                                        if ((dx * dx + dy * dy) <= mixDistanceSquared) {
                                            needsToMix = true;
                                            curMixNode_ = node;
                                            i = RegionMaxX;
                                            j = RegionMaxY;
                                            k = mixNodes.length - 1;
                                        }
                                    }
                                }
                            }
                        }
                        if (needsToMix != isInMixZone_) {
                            if (privacyDataLogged_) {
                                if (needsToMix)
                                    PrivacyLogWriter.log(Renderer.getInstance().getTimePassed() + ":Steady ID:" + this.steadyID_ + ":Pseudonym:" + Long.toHexString(this.ID_) + ":TraveledDistance:" + totalTravelDistance_ + ":TraveledTime:" + totalTravelTime_ + ":Node ID:" + curMixNode_.getNodeID() + ":Direction:IN" + ":Street:" + this.getCurStreet().getName() + ":StreetSpeed:" + this.getCurStreet().getSpeed() + ":VehicleSpeed:" + this.getCurSpeed() + ":x:" + this.curX_ + ":y:" + this.curY_);
                                else
                                    PrivacyLogWriter.log(Renderer.getInstance().getTimePassed() + ":Steady ID:" + this.steadyID_ + ":Pseudonym:" + Long.toHexString(this.ID_) + ":TraveledDistance:" + totalTravelDistance_ + ":TraveledTime:" + totalTravelTime_ + ":Node ID:" + curMixNode_.getNodeID() + ":Direction:OUT" + ":Street:" + this.getCurStreet().getName() + ":StreetSpeed:" + this.getCurStreet().getSpeed() + ":VehicleSpeed:" + this.getCurSpeed() + ":x:" + this.curX_ + ":y:" + this.curY_);
                            }
                            if (needsToMix) {
                                ++IDsChanged_;
                                ID_ = ownRandom_.nextLong();
                            }
                            isInMixZone_ = needsToMix;
                        }
                        if (!needsToMix)
                            curMixNode_ = null;
                    }
                }
                if (fakingMessages_) {
                    fakeMessageCountdown_ -= timePerStep;
                    if (fakeMessageCountdown_ < 0) {
                        fakeMessageCountdown_ = fakeMessagesInterval_;
                        String messageType = fakeMessageType_;
 
                        boolean tmpDirection2 = curDirection_;
                        if (messageType.equals("HUANG_PCN"))
                            tmpDirection2 = !curDirection_;
                        Street tmpStreet2 = curStreet_;
                        Street[] crossingStreets;
                        Node tmpNode;
                        int k, l = 0, destX = -1, destY = -1;
                        do {
                            ++l;
                            if (tmpDirection2) {
                                tmpNode = tmpStreet2.getStartNode();
                            } else {
                                tmpNode = tmpStreet2.getEndNode();
                            }
                            if (tmpNode.getJunction() != null) {
                                destX = tmpNode.getX();
                                destY = tmpNode.getY();
                                break;
                            }
                            crossingStreets = tmpNode.getCrossingStreets();
                            if (crossingStreets.length != 2) {
                                destX = tmpNode.getX();
                                destY = tmpNode.getY();
                                break;
                            }
                            for (k = 0; k < crossingStreets.length; ++k) {
                                if (crossingStreets[k] != tmpStreet2) {
                                    tmpStreet2 = crossingStreets[k];
                                    if (tmpStreet2.getStartNode() == tmpNode)
                                        tmpDirection2 = false;
                                    else
                                        tmpDirection2 = true;
                                    break;
                                }
                            }
                        } while (tmpStreet2 != curStreet_ && l < 10000);
 
                        ++fakeMessagesCreated_;
                       
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private final boolean checkPolitness(int lane) {
        boolean vehicleBehind2 = false;
        Vehicle b2 = null;
        double distance = 0;
        if (previous_ != null) {
            if (previous_.getCurLane() == lane + 1) {
                vehicleBehind2 = true;
                if (this.equals(Renderer.getInstance().getMarkedVehicle()))
                    ((Vehicle) previous_).setColor(Color.cyan);
                b2 = (Vehicle) previous_;
            } else {
                LaneObject tmpLaneObject = previous_.getPrevious();
                while (tmpLaneObject != null) {
                    if (tmpLaneObject.getCurLane() == lane + 1) {
                        vehicleBehind2 = true;
                        if (this.equals(Renderer.getInstance().getMarkedVehicle()))
                            ((Vehicle) tmpLaneObject).setColor(Color.cyan);
                        b2 = (Vehicle) tmpLaneObject;
                        break;
                    }
                    tmpLaneObject = tmpLaneObject.getPrevious();
                }
            }
        }
        if (vehicleBehind2) {
            distance = Math.abs(curPosition_ - b2.curPosition_);
        } else {
            if (curDirection_)
                distance = curPosition_;
            else
                distance = curStreet_.getLength() - curPosition_;
        }
        if (!vehicleBehind2) {
            Street[] outgoingStreets;
            Street tmpStreet = curStreet_, tmpStreet2;
            LaneObject tmpLaneObject;
            Node nextNode = null;
            boolean tmpDirection = curDirection_;
            int i;
            int counter = 0;
            while (counter < 3) {
                if (tmpDirection)
                    nextNode = tmpStreet.getStartNode();
                else
                    nextNode = tmpStreet.getEndNode();
                if (nextNode.getCrossingStreetsCount() != 2)
                    break;
                else
                    outgoingStreets = nextNode.getCrossingStreets();
                for (i = 0; i < outgoingStreets.length; ++i) {
                    tmpStreet2 = outgoingStreets[i];
                    if (tmpStreet2 != tmpStreet) {
                        tmpStreet = tmpStreet2;
                        if (lane + 1 > tmpStreet.getLanesCount())
                            break;
                        if (tmpStreet2.getStartNode() == nextNode) {
                            tmpDirection = true;
                            break;
                        } else {
                            tmpDirection = false;
                            break;
                        }
                    }
                }
                if (!vehicleBehind2) {
                    tmpLaneObject = tmpStreet.getFirstLaneObject(!tmpDirection);
                    while (tmpLaneObject != null) {
                        if (tmpLaneObject.getCurLane() == lane + 1 && !tmpLaneObject.equals(this)) {
                            counter = 3;
                            if (tmpDirection)
                                distance = tmpStreet.getLength() - tmpLaneObject.getCurPosition() + distance;
                            else
                                distance = tmpLaneObject.getCurPosition() + distance;
                            if (this.equals(Renderer.getInstance().getMarkedVehicle()))
                                ((Vehicle) tmpLaneObject).setColor(Color.cyan);
                            b2 = (Vehicle) tmpLaneObject;
                            break;
                        }
                        tmpLaneObject = tmpLaneObject.getNext();
                    }
                }
                counter++;
                if (counter < 3)
                    distance += tmpStreet.getLength();
            }
        }
        if (b2 != null) {
            if (curSpeed_ >= b2.curSpeed_) {
                return true;
            }
            float t = (float) ((b2.curSpeed_ - curSpeed_) / accelerationRate_);
            if ((distance - b2.curBrakingDistance_) > ((politeness_ / 100) * (b2.curSpeed_ * t)))
                return true;
            else
                return false;
        } else
            return true;
    }

    private final int checkCurrentBraking(int lane) {
        boolean foundNextVehicle = false;
        if (next_ != null) {
            if (next_.getCurLane() == lane) {
                foundNextVehicle = true;
                if ((curDirection_ && next_.getCurPosition() - curPosition_ < curBrakingDistance_) || (!curDirection_ && curPosition_ - next_.getCurPosition() < curBrakingDistance_)) {
                    if (curSpeed_ > next_.getCurSpeed() - brakingRate_) {
                        if (!next_.getClass().equals(BlockingObject.class)) {
                            if (emergencyVehicle_) {
                                if (lastEVAMessageCreated >= MESSAGE_INTERVAL) {
                                    lastEVAMessageCreated = 0;
                                    boolean tmpDirection2 = !curDirection_;
                                    Street tmpStreet2 = curStreet_;
                                    Street[] crossingStreets;
                                    Node tmpNode;
                                    int k, l = 0, destX = -1, destY = -1;
                                    do {
                                        ++l;
                                        if (tmpDirection2) {
                                            tmpNode = tmpStreet2.getStartNode();
                                        } else {
                                            tmpNode = tmpStreet2.getEndNode();
                                        }
                                        if (tmpNode.getJunction() != null) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        crossingStreets = tmpNode.getCrossingStreets();
                                        if (crossingStreets.length != 2) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        for (k = 0; k < crossingStreets.length; ++k) {
                                            if (crossingStreets[k] != tmpStreet2) {
                                                tmpStreet2 = crossingStreets[k];
                                                if (tmpStreet2.getStartNode() == tmpNode)
                                                    tmpDirection2 = false;
                                                else
                                                    tmpDirection2 = true;
                                                break;
                                            }
                                        }
                                    } while (tmpStreet2 != curStreet_ && l < 10000);
                                    if (destX != -1 && destY != -1) {
                                        int direction = -1;
                                        if (!curDirection_)
                                            direction = 1;
                                        int time = Renderer.getInstance().getTimePassed();
 
                                        ++evaMessagesCreated_;
                                    }
                                }
                            } else if (((Vehicle) next_).isInTrafficJam_() && !((Vehicle) next_).waitingForSignal_ && curSpeed_ > 0) {
                                if (lastPCNFORWARDMessageCreated >= MESSAGE_INTERVAL) {
                                    lastPCNFORWARDMessageCreated = 0;
                                    boolean tmpDirection = curDirection_;
                                    Street tmpStreet = curStreet_;
                                    Street[] crossingStreets;
                                    Node tmpNode;
                                    int i, j = 0, destX = -1, destY = -1;
                                    do {
                                        ++j;
                                        if (tmpDirection) {
                                            tmpNode = tmpStreet.getStartNode();
                                        } else {
                                            tmpNode = tmpStreet.getEndNode();
                                        }
                                        if (tmpNode.getJunction() != null) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        crossingStreets = tmpNode.getCrossingStreets();
                                        if (crossingStreets.length != 2) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        for (i = 0; i < crossingStreets.length; ++i) {
                                            if (crossingStreets[i] != tmpStreet) {
                                                tmpStreet = crossingStreets[i];
                                                if (tmpStreet.getStartNode() == tmpNode)
                                                    tmpDirection = false;
                                                else
                                                    tmpDirection = true;
                                                break;
                                            }
                                        }
                                    } while (tmpStreet != curStreet_ && j < 10000);
                                    if (destX != -1 && destY != -1) {
                                        int direction = -1;
                                        if (!curDirection_)
                                            direction = 1;
                                        int time = Renderer.getInstance().getTimePassed();
 
                                        ++pcnForwardMessagesCreated_;
                                    }
                                }
                            }
                            return 1;
                        } else if (((BlockingObject) next_).getPenaltyType_().equals("HUANG_RHCN") && curSpeed_ < 360)
                            return 0;
                        else {
                            passingBlocking_ = true;
                            if (((BlockingObject) next_).getPenaltyType_().equals("HUANG_RHCN")) {
                                if (lastRHCNMessageCreated >= MESSAGE_INTERVAL) {
                                    if (waitToSendRHCNCounter_ < 0)
                                        waitToSendRHCNCounter_ = WAIT_TO_SEND_RHCN_;
                                    else if (waitToSendRHCNCounter_ > 0)
                                        waitToSendRHCNCounter_--;
                                    else {
                                        waitToSendRHCNCounter_ = -1;
                                        lastRHCNMessageCreated = 0;
                                        boolean tmpDirection2 = curDirection_;
                                        Street tmpStreet2 = curStreet_;
                                        Street[] crossingStreets;
                                        Node tmpNode;
                                        int k, l = 0, destX = -1, destY = -1;
                                        do {
                                            ++l;
                                            if (tmpDirection2) {
                                                tmpNode = tmpStreet2.getStartNode();
                                            } else {
                                                tmpNode = tmpStreet2.getEndNode();
                                            }
                                            if (tmpNode.getJunction() != null) {
                                                destX = tmpNode.getX();
                                                destY = tmpNode.getY();
                                                break;
                                            }
                                            crossingStreets = tmpNode.getCrossingStreets();
                                            if (crossingStreets.length != 2) {
                                                destX = tmpNode.getX();
                                                destY = tmpNode.getY();
                                                break;
                                            }
                                            for (k = 0; k < crossingStreets.length; ++k) {
                                                if (crossingStreets[k] != tmpStreet2) {
                                                    tmpStreet2 = crossingStreets[k];
                                                    if (tmpStreet2.getStartNode() == tmpNode)
                                                        tmpDirection2 = false;
                                                    else
                                                        tmpDirection2 = true;
                                                    break;
                                                }
                                            }
                                        } while (tmpStreet2 != curStreet_ && l < 10000);
                                        if (destX != -1 && destY != -1) {
                                            int direction = -1;
                                            if (!curDirection_)
                                                direction = 1;
                                            int time = Renderer.getInstance().getTimePassed();
 
                                            ++rhcnMessagesCreated_;
                                        }
                                    }
                                }
                            }
                            return 1;
                        }
                    }
                }
            } else {
                LaneObject tmpLaneObject = next_.getNext();
                while (tmpLaneObject != null) {
                    if (tmpLaneObject.getCurLane() == lane) {
                        foundNextVehicle = true;
                        if ((curDirection_ && tmpLaneObject.getCurPosition() - curPosition_ < curBrakingDistance_) || (!curDirection_ && curPosition_ - tmpLaneObject.getCurPosition() < curBrakingDistance_)) {
                            if (curSpeed_ > next_.getCurSpeed() - brakingRate_) {
                                if (!next_.getClass().equals(BlockingObject.class)) {
                                    if (emergencyVehicle_) {
                                        if (lastEVAMessageCreated >= MESSAGE_INTERVAL) {
                                            lastEVAMessageCreated = 0;
                                            boolean tmpDirection2 = !curDirection_;
                                            Street tmpStreet2 = curStreet_;
                                            Street[] crossingStreets;
                                            Node tmpNode;
                                            int k, l = 0, destX = -1, destY = -1;
                                            do {
                                                ++l;
                                                if (tmpDirection2) {
                                                    tmpNode = tmpStreet2.getStartNode();
                                                } else {
                                                    tmpNode = tmpStreet2.getEndNode();
                                                }
                                                if (tmpNode.getJunction() != null) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                crossingStreets = tmpNode.getCrossingStreets();
                                                if (crossingStreets.length != 2) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                for (k = 0; k < crossingStreets.length; ++k) {
                                                    if (crossingStreets[k] != tmpStreet2) {
                                                        tmpStreet2 = crossingStreets[k];
                                                        if (tmpStreet2.getStartNode() == tmpNode)
                                                            tmpDirection2 = false;
                                                        else
                                                            tmpDirection2 = true;
                                                        break;
                                                    }
                                                }
                                            } while (tmpStreet2 != curStreet_ && l < 10000);
                                            if (destX != -1 && destY != -1) {
                                                int direction = -1;
                                                if (!curDirection_)
                                                    direction = 1;
                                                int time = Renderer.getInstance().getTimePassed();
 
                                                ++evaMessagesCreated_;
                                            }
                                        }
                                    } else if (((Vehicle) next_).isInTrafficJam_() && !((Vehicle) next_).waitingForSignal_ && curSpeed_ > 0) {
                                        if (lastPCNFORWARDMessageCreated >= MESSAGE_INTERVAL) {
                                            lastPCNFORWARDMessageCreated = 0;
                                            boolean tmpDirection = curDirection_;
                                            Street tmpStreet = curStreet_;
                                            Street[] crossingStreets;
                                            Node tmpNode;
                                            int i, j = 0, destX = -1, destY = -1;
                                            do {
                                                ++j;
                                                if (tmpDirection) {
                                                    tmpNode = tmpStreet.getStartNode();
                                                } else {
                                                    tmpNode = tmpStreet.getEndNode();
                                                }
                                                if (tmpNode.getJunction() != null) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                crossingStreets = tmpNode.getCrossingStreets();
                                                if (crossingStreets.length != 2) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                for (i = 0; i < crossingStreets.length; ++i) {
                                                    if (crossingStreets[i] != tmpStreet) {
                                                        tmpStreet = crossingStreets[i];
                                                        if (tmpStreet.getStartNode() == tmpNode)
                                                            tmpDirection = false;
                                                        else
                                                            tmpDirection = true;
                                                        break;
                                                    }
                                                }
                                            } while (tmpStreet != curStreet_ && j < 10000);
                                            if (destX != -1 && destY != -1) {
                                                int direction = -1;
                                                if (!curDirection_)
                                                    direction = 1;
                                                int time = Renderer.getInstance().getTimePassed();
 
                                                ++pcnForwardMessagesCreated_;
                                            }
                                        }
                                    }
                                    return 1;
                                } else if (((BlockingObject) next_).getPenaltyType_().equals("HUANG_RHCN") && curSpeed_ < 360)
                                    return 0;
                                else {
                                    passingBlocking_ = true;
                                    if (((BlockingObject) next_).getPenaltyType_().equals("HUANG_RHCN")) {
                                        if (lastRHCNMessageCreated >= MESSAGE_INTERVAL) {
                                            lastRHCNMessageCreated = 0;
                                            boolean tmpDirection2 = curDirection_;
                                            Street tmpStreet2 = curStreet_;
                                            Street[] crossingStreets;
                                            Node tmpNode;
                                            int k, l = 0, destX = -1, destY = -1;
                                            do {
                                                ++l;
                                                if (tmpDirection2) {
                                                    tmpNode = tmpStreet2.getStartNode();
                                                } else {
                                                    tmpNode = tmpStreet2.getEndNode();
                                                }
                                                if (tmpNode.getJunction() != null) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                crossingStreets = tmpNode.getCrossingStreets();
                                                if (crossingStreets.length != 2) {
                                                    destX = tmpNode.getX();
                                                    destY = tmpNode.getY();
                                                    break;
                                                }
                                                for (k = 0; k < crossingStreets.length; ++k) {
                                                    if (crossingStreets[k] != tmpStreet2) {
                                                        tmpStreet2 = crossingStreets[k];
                                                        if (tmpStreet2.getStartNode() == tmpNode)
                                                            tmpDirection2 = false;
                                                        else
                                                            tmpDirection2 = true;
                                                        break;
                                                    }
                                                }
                                            } while (tmpStreet2 != curStreet_ && l < 10000);
                                            if (destX != -1 && destY != -1) {
                                                int direction = -1;
                                                if (!curDirection_)
                                                    direction = 1;
                                                int time = Renderer.getInstance().getTimePassed();
 
                                                ++rhcnMessagesCreated_;
                                            }
                                        }
                                    }
                                    return 1;
                                }
                            }
                        }
                        break;
                    }
                    tmpLaneObject = tmpLaneObject.getNext();
                }
            }
        }
        double distance;
        if (curDirection_)
            distance = curStreet_.getLength() - curPosition_;
        else
            distance = curPosition_;
        if (distance < curBrakingDistance_) {
            Street tmpStreet = curStreet_;
            LaneObject tmpLaneObject;
            Node junctionNode, nextNode;
            boolean tmpDirection = curDirection_;
            boolean gotJunctionPermission = false;
            int tmpLane = lane;
            int i;
            int j = routeStreets_.length - 1;
            for (i = routePosition_; i < j; ) {
                if (tmpDirection)
                    junctionNode = tmpStreet.getEndNode();
                else
                    junctionNode = tmpStreet.getStartNode();
                if (junctionNode.getJunction() != null) {
                    if (junctionAllowed_ != junctionNode) {
                        if (routeDirections_[i + 1])
                            nextNode = routeStreets_[i + 1].getEndNode();
                        else
                            nextNode = routeStreets_[i + 1].getStartNode();
                        if (junctionNode.isHasTrafficSignal_()) {
                            if (junctionNode.getJunction().canPassTrafficLight(this, tmpStreet, nextNode)) {
                                junctionAllowed_ = junctionNode;
                                gotJunctionPermission = true;
                            } else {
                                waitingForSignal_ = true;
                                return 2;
                            }
                        } else {
                            int priority;
                            if (tmpDirection)
                                priority = junctionNode.getJunction().getJunctionPriority(tmpStreet.getStartNode(), nextNode);
                            else
                                priority = junctionNode.getJunction().getJunctionPriority(tmpStreet.getEndNode(), nextNode);
                            if (priority != 1) {
                                if (curSpeed_ > 1000) {
                                    return 2;
                                } else if (priority > 2) {
                                    junctionNode.getJunction().addWaitingVehicle(this, priority);
                                    if (!emergencyVehicle_ && !fakingMessages_ && !junctionNode.getJunction().canPassJunction(this, priority, nextNode))
                                        return 2;
                                    else {
                                        junctionAllowed_ = junctionNode;
                                        gotJunctionPermission = true;
                                    }
                                }
                            }
                        }
                    }
                }
                ++i;
                tmpDirection = routeDirections_[i];
                tmpStreet = routeStreets_[i];
                if (tmpLane > tmpStreet.getLanesCount())
                    tmpLane = tmpStreet.getLanesCount();
                if (tmpStreet.getSpeed() < curSpeed_) {
                    if (gotJunctionPermission) {
                        junctionAllowed_.getJunction().allowOtherVehicle();
                        junctionAllowed_ = null;
                    }
                    return 2;
                }
                if (!foundNextVehicle) {
                    tmpLaneObject = tmpStreet.getFirstLaneObject(tmpDirection);
                    while (tmpLaneObject != null) {
                        if (tmpLaneObject.getCurLane() == tmpLane) {
                            foundNextVehicle = true;
                            if ((tmpDirection && tmpLaneObject.getCurPosition() + distance < curBrakingDistance_) || (!tmpDirection && tmpStreet.getLength() - tmpLaneObject.getCurPosition() + distance < curBrakingDistance_)) {
                                if (curSpeed_ > tmpLaneObject.getCurSpeed() - brakingRate_) {
                                    if (gotJunctionPermission) {
                                        junctionAllowed_.getJunction().allowOtherVehicle();
                                        junctionAllowed_ = null;
                                    }
                                    return 1;
                                }
                            }
                            break;
                        }
                        tmpLaneObject = tmpLaneObject.getNext();
                    }
                }
                distance += tmpStreet.getLength();
                if (distance > curBrakingDistance_)
                    break;
                if (tmpStreet == destinations_.peekFirst().getStreet())
                    break;
            }
        }
        return 0;
    }

    private final boolean checkLaneFree(int lane) {
        boolean foundNextVehicle = false;
        int neededFreeDistance = curBrakingDistance_ / 2;
        if (next_ != null) {
            if (next_.getCurLane() == lane) {
                foundNextVehicle = true;
                if ((curDirection_ && next_.getCurPosition() - curPosition_ < neededFreeDistance) || (!curDirection_ && curPosition_ - next_.getCurPosition() < neededFreeDistance)) {
                    if (curSpeed_ > next_.getCurSpeed() - brakingRate_)
                        return false;
                }
            } else {
                LaneObject tmpLaneObject = next_.getNext();
                while (tmpLaneObject != null) {
                    if (tmpLaneObject.getCurLane() == lane) {
                        foundNextVehicle = true;
                        if ((curDirection_ && tmpLaneObject.getCurPosition() - curPosition_ < neededFreeDistance) || (!curDirection_ && curPosition_ - tmpLaneObject.getCurPosition() < neededFreeDistance)) {
                            if (curSpeed_ > next_.getCurSpeed() - brakingRate_)
                                return false;
                        }
                        break;
                    }
                    tmpLaneObject = tmpLaneObject.getNext();
                }
            }
        }
        double distance;
        if (curDirection_)
            distance = curStreet_.getLength() - curPosition_;
        else
            distance = curPosition_;
        if (!foundNextVehicle && distance < neededFreeDistance) {
            Street tmpStreet = curStreet_;
            LaneObject tmpLaneObject;
            boolean tmpDirection = curDirection_;
            for (int i = routePosition_ + 1; i < routeStreets_.length; ++i) {
                tmpDirection = routeDirections_[i];
                tmpStreet = routeStreets_[i];
                if (!foundNextVehicle) {
                    tmpLaneObject = tmpStreet.getFirstLaneObject(tmpDirection);
                    while (tmpLaneObject != null) {
                        if (tmpLaneObject.getCurLane() == lane) {
                            foundNextVehicle = true;
                            if ((tmpDirection && tmpLaneObject.getCurPosition() + distance < neededFreeDistance) || (!tmpDirection && tmpStreet.getLength() - tmpLaneObject.getCurPosition() + distance < neededFreeDistance)) {
                                if (curSpeed_ > tmpLaneObject.getCurSpeed() - brakingRate_)
                                    return false;
                            }
                            break;
                        }
                        tmpLaneObject = tmpLaneObject.getNext();
                    }
                }
                distance += tmpStreet.getLength();
                if (distance > neededFreeDistance)
                    break;
            }
        }
        neededFreeDistance = curBrakingDistance_;
        boolean foundPreviousVehicle = false;
        if (previous_ != null) {
            if (previous_.getCurLane() == lane) {
                foundPreviousVehicle = true;
                if ((curDirection_ && curPosition_ - previous_.getCurPosition() < neededFreeDistance) || (!curDirection_ && previous_.getCurPosition() - curPosition_ < neededFreeDistance)) {
                    if (curSpeed_ > previous_.getCurSpeed() - brakingRate_)
                        return false;
                }
            } else {
                LaneObject tmpLaneObject = previous_.getPrevious();
                while (tmpLaneObject != null) {
                    if (tmpLaneObject.getCurLane() == lane) {
                        foundPreviousVehicle = true;
                        if ((curDirection_ && curPosition_ - tmpLaneObject.getCurPosition() < neededFreeDistance) || (!curDirection_ && tmpLaneObject.getCurPosition() - curPosition_ < neededFreeDistance)) {
                            if (curSpeed_ > previous_.getCurSpeed() - brakingRate_)
                                return false;
                        }
                        break;
                    }
                    tmpLaneObject = tmpLaneObject.getPrevious();
                }
            }
        }
        if (curDirection_)
            distance = curPosition_;
        else
            distance = curStreet_.getLength() - curPosition_;
        if (!foundPreviousVehicle && distance < neededFreeDistance) {
            Street[] outgoingStreets;
            Street tmpStreet = curStreet_, tmpStreet2;
            LaneObject tmpLaneObject;
            Node nextNode = null;
            boolean tmpDirection = curDirection_;
            int i;
            while (true) {
                if (tmpDirection)
                    nextNode = tmpStreet.getStartNode();
                else
                    nextNode = tmpStreet.getEndNode();
                if (nextNode.getCrossingStreetsCount() != 2)
                    return false;
                else
                    outgoingStreets = nextNode.getCrossingStreets();
                for (i = 0; i < outgoingStreets.length; ++i) {
                    tmpStreet2 = outgoingStreets[i];
                    if (tmpStreet2 != tmpStreet) {
                        tmpStreet = tmpStreet2;
                        if (lane > tmpStreet.getLanesCount())
                            return false;
                        if (tmpStreet2.getStartNode() == nextNode) {
                            tmpDirection = true;
                            break;
                        } else {
                            tmpDirection = false;
                            break;
                        }
                    }
                }
                if (!foundPreviousVehicle) {
                    tmpLaneObject = tmpStreet.getFirstLaneObject(tmpDirection);
                    while (tmpLaneObject != null) {
                        if (tmpLaneObject.getCurLane() == lane) {
                            foundNextVehicle = true;
                            if ((tmpDirection && tmpStreet.getLength() - tmpLaneObject.getCurPosition() + distance < neededFreeDistance) || (!tmpDirection && tmpLaneObject.getCurPosition() + distance < neededFreeDistance)) {
                                if (curSpeed_ > tmpLaneObject.getCurSpeed() - brakingRate_)
                                    return false;
                            }
                            break;
                        }
                        tmpLaneObject = tmpLaneObject.getNext();
                    }
                }
                distance += tmpStreet.getLength();
                if (distance > neededFreeDistance)
                    break;
            }
        }
        return true;
    }

    public void sendMessages() {
        communicationCountdown_ += communicationInterval_;
        if (beaconsEnabled_ && !isInMixZone_) {
            Message[] messages = knownMessages_.getForwardMessages();
            int size = knownMessages_.getSize();
            Vehicle nearestVehicle;
           
            for (int i = size - 1; i > -1; --i) {
                
                int sendCount = 0;
            
                long dx, dy, maxCommSquared = (long) maxCommDistance_ * maxCommDistance_;
 
                if (messages[i].getFloodingMode()) {
                    KnownVehicle[] heads = knownVehiclesList_.getFirstKnownVehicle();
                    KnownVehicle next;
                    for (int j = 0; j < heads.length; ++j) {
                        next = heads[j];
                        while (next != null) {
                            ++sendCount;
                            nearestVehicle = next.getVehicle();
                            dx = nearestVehicle.getX() - curX_;
                            dy = nearestVehicle.getY() - curY_;
                            if ((dx * dx + dy * dy) < maxCommSquared) {
                                nearestVehicle.receiveMessage(curX_, curY_, messages[i]);
                            }
                            next = next.getNext();
                        }
                    }
                    if (sendCount > 0)
                        knownMessages_.deleteForwardMessage(i, true);
                } else {
                    nearestVehicle = knownVehiclesList_.findNearestVehicle(curX_, curY_, messages[i].getDestinationX_(), messages[i].getDestinationY_(), maxCommDistance_);
                    if (nearestVehicle != null) {
                        nearestVehicle.receiveMessage(curX_, curY_, messages[i]);
                        knownMessages_.deleteForwardMessage(i, true);
                    }
                }
            }
        } else if (!isInMixZone_ || mixZonesFallbackEnabled_) {
            Message[] messages = knownMessages_.getForwardMessages();
            int messageSize = knownMessages_.getSize();
            if (messageSize > 0) {
                int MapMinX, MapMinY, MapMaxX, MapMaxY, RegionMinX, RegionMinY, RegionMaxX, RegionMaxY;
                long tmp = curX_ - maxCommDistance_;
                if (tmp < 0)
                    MapMinX = 0;
                else if (tmp < Integer.MAX_VALUE)
                    MapMinX = (int) tmp;
                else
                    MapMinX = Integer.MAX_VALUE;
                tmp = curX_ + (long) maxCommDistance_;
                if (tmp < 0)
                    MapMaxX = 0;
                else if (tmp < Integer.MAX_VALUE)
                    MapMaxX = (int) tmp;
                else
                    MapMaxX = Integer.MAX_VALUE;
                tmp = curY_ - maxCommDistance_;
                if (tmp < 0)
                    MapMinY = 0;
                else if (tmp < Integer.MAX_VALUE)
                    MapMinY = (int) tmp;
                else
                    MapMinY = Integer.MAX_VALUE;
                tmp = curY_ + (long) maxCommDistance_;
                if (tmp < 0)
                    MapMaxY = 0;
                else if (tmp < Integer.MAX_VALUE)
                    MapMaxY = (int) tmp;
                else
                    MapMaxY = Integer.MAX_VALUE;
                Region tmpregion = MAP.getRegionOfPoint(MapMinX, MapMinY);
                RegionMinX = tmpregion.getX();
                RegionMinY = tmpregion.getY();
                tmpregion = MAP.getRegionOfPoint(MapMaxX, MapMaxY);
                RegionMaxX = tmpregion.getX();
                RegionMaxY = tmpregion.getY();
                long maxCommDistance_square = (long) maxCommDistance_ * maxCommDistance_;
                long dx, dy, distance = 0;
                int i, j, k, l, size;
                Vehicle[] vehicles = null;
                Vehicle vehicle = null;
 
                for (i = RegionMinX; i <= RegionMaxX; ++i) {
                    for (j = RegionMinY; j <= RegionMaxY; ++j) {
                        vehicles = regions_[i][j].getVehicleArray();
                        size = vehicles.length;
                        for (k = 0; k < size; ++k) {
                            vehicle = vehicles[k];
                            if (vehicle.isWiFiEnabled() && vehicle.isActive() && vehicle != this && vehicle.getX() >= MapMinX && vehicle.getX() <= MapMaxX && vehicle.getY() >= MapMinY && vehicle.getY() <= MapMaxY) {
                                dx = vehicle.getX() - curX_;
                                dy = vehicle.getY() - curY_;
                                distance = dx * dx + dy * dy;
                                if (distance <= maxCommDistance_square) {
                                    if (!isInMixZone_ || !mixZonesFallbackFloodingOnly_) {
                                        for (l = 0; l < messageSize; ++l) {
                                            vehicle.receiveMessage(curX_, curY_, messages[l]);
                                            vehicle.setColor(Color.red);
                                        }
                                    } else {
                                        for (l = 0; l < messageSize; ++l) {
                                            if (messages[l].getFloodingMode()) {
                                                vehicle.receiveMessage(curX_, curY_, messages[l]);
                                                vehicle.setColor(Color.cyan);
                                            }
                                        }
                                    }
                                }
                            }
                        }
 
                    }
                }
                if (!isInMixZone_ || !mixZonesFallbackFloodingOnly_)
                    knownMessages_.deleteAllForwardMessages(true);
                else
                    knownMessages_.deleteAllFloodingForwardMessages(true);
            }
        }
    }

    public final void receiveMessage(int sourceX, int sourceY, Message message) {
        long dx = message.getDestinationX_() - curX_;
        long dy = message.getDestinationY_() - curY_;
        long distanceToDestinationSquared = dx * dx + dy * dy;
        if (message.getFloodingMode()) {
            if ((message.getDestinationRadiusSquared() >= distanceToDestinationSquared) && !directCommunicationMode_) {
                knownMessages_.addMessage(message, true, true);
            } else
                knownMessages_.addMessage(message, true, false);
        } else {
            if (message.getDestinationRadiusSquared() >= distanceToDestinationSquared) {
                message.setFloodingMode(true);
            }
            if (beaconsEnabled_) {
                if (directCommunicationMode_)
                    knownMessages_.addMessage(message, true, false);
                else
                    knownMessages_.addMessage(message, true, true);
            } else {
                dx = message.getDestinationX_() - sourceX;
                dy = message.getDestinationY_() - sourceY;
                if (((dx * dx + dy * dy) > distanceToDestinationSquared) && !directCommunicationMode_) {
                    knownMessages_.addMessage(message, true, true);
                } else
                    knownMessages_.addMessage(message, true, false);
            }
        }
    }

    public void sendBeacons() {
        beaconCountdown_ += beaconInterval_;
        if (probeDataActive_ && Renderer.getInstance().getTimePassed() > startAfterTime_ && amountOfProbeData_ > 0) {
            if (curProbeDataInterval_ <= 1) {
                GeneralLogWriter.log(Renderer.getInstance().getTimePassed() + ":" + curX_ + ":" + curY_ + ":" + curSpeed_ + ":" + ID_);
                curProbeDataInterval_ = PROBE_DATA_TIME_INTERVAL;
                amountOfProbeData_--;
            } else
                curProbeDataInterval_--;
        }
        if (isInSlow && !changedPseudonymInSlow && Renderer.getInstance().getTimePassed() >= (slowTimestamp + TIME_TO_PSEUDONYM_CHANGE - (2 * beaconInterval_))) {
            changedPseudonymInSlow = true;
            ++IDsChanged_;
            ID_ = ownRandom_.nextLong();
        }
        if (slowOn) {
            if (privacyDataLogged_ && isInSlow && !slowBeaconsLogged) {
                slowBeaconsLogged = true;
                if (!vehicleJustStartedInSlow)
                    PrivacyLogWriter.log(savedBeacon2.replace("%0%aa%0%", "IN") + "\n" + savedBeacon1.replace("%0%aa%0%", "IN"));
            } else if (privacyDataLogged_ && !isInSlow && slowBeaconsLogged) {
                slowBeaconsLogged = false;
                logNextBeacons = 2;
            }
        }
        if (slowOn) {
            if (!isInSlow && this.curSpeed_ <= SLOW_SPEED_LIMIT && logNextBeacons == 0) {
                isInSlow = true;
                slowTimestamp = Renderer.getInstance().getTimePassed();
                changedPseudonymInSlow = false;
            } else if (isInSlow && this.curSpeed_ > SLOW_SPEED_LIMIT && (Renderer.getInstance().getTimePassed() - slowTimestamp) > (2 * beaconInterval_)) {
                isInSlow = false;
            }
        }
        if (silent_period != silentPeriod) {
            silentPeriod = silent_period;
            if (!silent_period)
                logNextBeacons = 2;
            if (silentPeriod && privacyDataLogged_ && isSilentPeriodsOn())
                PrivacyLogWriter.log(savedBeacon2 + "\n" + savedBeacon1);
        }
        if (!silent_period && !isInSlow) {
            int i, j, k, size = 0, MapMinX, MapMinY, MapMaxX, MapMaxY, RegionMinX, RegionMinY, RegionMaxX, RegionMaxY;
            Vehicle[] vehicles = null;
            Vehicle vehicle = null;
            long tmp = curX_ - maxCommDistance_;
            if (tmp < 0)
                MapMinX = 0;
            else if (tmp < Integer.MAX_VALUE)
                MapMinX = (int) tmp;
            else
                MapMinX = Integer.MAX_VALUE;
            tmp = curX_ + (long) maxCommDistance_;
            if (tmp < 0)
                MapMaxX = 0;
            else if (tmp < Integer.MAX_VALUE)
                MapMaxX = (int) tmp;
            else
                MapMaxX = Integer.MAX_VALUE;
            tmp = curY_ - maxCommDistance_;
            if (tmp < 0)
                MapMinY = 0;
            else if (tmp < Integer.MAX_VALUE)
                MapMinY = (int) tmp;
            else
                MapMinY = Integer.MAX_VALUE;
            tmp = curY_ + (long) maxCommDistance_;
            if (tmp < 0)
                MapMaxY = 0;
            else if (tmp < Integer.MAX_VALUE)
                MapMaxY = (int) tmp;
            else
                MapMaxY = Integer.MAX_VALUE;
            Region tmpregion = MAP.getRegionOfPoint(MapMinX, MapMinY);
            RegionMinX = tmpregion.getX();
            RegionMinY = tmpregion.getY();
            tmpregion = MAP.getRegionOfPoint(MapMaxX, MapMaxY);
            RegionMaxX = tmpregion.getX();
            RegionMaxY = tmpregion.getY();
            long maxCommDistanceSquared = (long) maxCommDistance_ * maxCommDistance_;
            long dx, dy;
            for (i = RegionMinX; i <= RegionMaxX; ++i) {
                for (j = RegionMinY; j <= RegionMaxY; ++j) {
                    vehicles = regions_[i][j].getVehicleArray();
                    size = vehicles.length;
                    for (k = 0; k < size; ++k) {
                        vehicle = vehicles[k];
                        if (vehicle.isWiFiEnabled() && vehicle.isActive() && vehicle != this && vehicle.getX() >= MapMinX && vehicle.getX() <= MapMaxX && vehicle.getY() >= MapMinY && vehicle.getY() <= MapMaxY) {
                            dx = vehicle.getX() - curX_;
                            dy = vehicle.getY() - curY_;
                            if ((dx * dx + dy * dy) <= maxCommDistanceSquared) {
                                if (emergencyBeacons > 0) {
 
                                    vehicle.getKnownVehiclesList().updateVehicle(this, (ID_ - 1), curX_, curY_, curSpeed_, vehicle.getID(), false, false);
                                } else if (emergencyBeacons == 0) {
                                    boolean tmpDirection2 = curDirection_;
                                    Street tmpStreet2 = curStreet_;
                                    Street[] crossingStreets;
                                    Node tmpNode;
                                    int k1, l = 0, destX = -1, destY = -1;
                                    do {
                                        ++l;
                                        if (tmpDirection2) {
                                            tmpNode = tmpStreet2.getStartNode();
                                        } else {
                                            tmpNode = tmpStreet2.getEndNode();
                                        }
                                        if (tmpNode.getJunction() != null) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        crossingStreets = tmpNode.getCrossingStreets();
                                        if (crossingStreets.length != 2) {
                                            destX = tmpNode.getX();
                                            destY = tmpNode.getY();
                                            break;
                                        }
                                        for (k1 = 0; k1 < crossingStreets.length; ++k1) {
                                            if (crossingStreets[k1] != tmpStreet2) {
                                                tmpStreet2 = crossingStreets[k1];
                                                if (tmpStreet2.getStartNode() == tmpNode)
                                                    tmpDirection2 = false;
                                                else
                                                    tmpDirection2 = true;
                                                break;
                                            }
                                        }
                                    } while (tmpStreet2 != curStreet_ && l < 10000);
                                    if (destX != -1 && destY != -1) {
                                        int direction = -1;
                                        int time = Renderer.getInstance().getTimePassed();
 
                                        emergencyBeacons = -1;
                                    }
                                    ++fakeMessagesCreated_;
                              
                                }
                                vehicle.getKnownVehiclesList().updateVehicle(this, ID_, curX_, curY_, curSpeed_, vehicle.getID(), false, false);
                                 
                            }
                        }
                    }
                }
            }
            if (emergencyBeacons >= 0)
                emergencyBeacons--;
            if (beaconMonitorEnabled_) {
                if (curX_ >= beaconMonitorMinX_ && curX_ <= beaconMonitorMaxX_ && curY_ >= beaconMonitorMinY_ && curY_ <= beaconMonitorMaxY_) {
                    REPORT_PANEL.addBeacon(this, ID_, curX_, curY_, curSpeed_, false);
                }
            }
            if (logBeaconsAfterEvent_) {
                amountOfLoggedBeacons_++;
                if (amountOfLoggedBeacons_ == 10) {
                    beaconString_ += "," + curX_ + "," + curY_ + "," + curSpeed_;
                    if (!beaconString_.equals(","))
                        GeneralLogWriter.log(beaconString_);
                    logBeaconsAfterEvent_ = false;
                } else {
                    beaconString_ += "," + curX_ + "," + curY_ + "," + curSpeed_;
                }
            }
 
            if (privacyDataLogged_ && (silentPeriodsOn || slowOn)) {
                savedBeacon2 = savedBeacon1;
                savedBeacon1 = Renderer.getInstance().getTimePassed() + ":Steady ID:" + this.steadyID_ + ":Pseudonym:" + Long.toHexString(this.ID_) + ":TraveledDistance:" + totalTravelDistance_ + ":TraveledTime:" + totalTravelTime_ + ":Node ID:None" + ":Direction:%0%aa%0%" + ":Street:" + this.getCurStreet().getName() + ":StreetSpeed:" + this.getCurStreet().getSpeed() + ":VehicleSpeed:" + this.getCurSpeed() + ":x:" + this.curX_ + ":y:" + this.curY_;
                if (logNextBeacons == 1) {
                    logNextBeacons = 0;
                    if (!slowOn || !vehicleJustStartedInSlow)
                        PrivacyLogWriter.log(savedBeacon2.replace("%0%aa%0%", "OUT") + ":TimeInSlow:" + (Renderer.getInstance().getTimePassed() - slowTimestamp) + "\n" + savedBeacon1.replace("%0%aa%0%", "OUT"));
                    if (vehicleJustStartedInSlow)
                        vehicleJustStartedInSlow = false;
                } else if (logNextBeacons == 2) {
                    logNextBeacons--;
                }
            }
        }
    }

    public void sendEncryptedBeacons() {
        if (!silentPeriod) {
            beaconCountdown_ += beaconInterval_;
 
        }
        if (silent_period != silentPeriod) {
            silentPeriod = silent_period;
            if (silentPeriod) {
                if (privacyDataLogged_)
                    PrivacyLogWriter.log(Renderer.getInstance().getTimePassed() + ":Steady ID:" + this.steadyID_ + ":Pseudonym:" + Long.toHexString(this.ID_) + ":TraveledDistance:" + totalTravelDistance_ + ":TraveledTime:" + totalTravelTime_ + ":Node ID:none" + ":Direction:IN" + ":x:" + this.curX_ + ":y:" + this.curY_);
            } else {
                ID_ = ownRandom_.nextLong();
                if (privacyDataLogged_)
                    PrivacyLogWriter.log(Renderer.getInstance().getTimePassed() + ":Steady ID:" + this.steadyID_ + ":Pseudonym:" + Long.toHexString(this.ID_) + ":TraveledDistance:" + totalTravelDistance_ + ":TraveledTime:" + totalTravelTime_ + ":Node ID:none" + ":Direction:OUT" + ":x:" + this.curX_ + ":y:" + this.curY_);
            }
        }
    }

    public void move(int timePerStep) {
        if (curWaitTime_ == 0 && curStreet_ != null) {
            curLane_ = newLane_;
            curSpeed_ = newSpeed_;
            double tmpPosition, newPosition = curPosition_, movement;
            WayPoint nextTarget;
            movement = curSpeed_ * (timePerStep / 1000.0);
            totalTravelTime_ += timePerStep;
            totalTravelDistance_ += movement;
            Street oldStreet = curStreet_;
            boolean oldDirection = curDirection_;
            while (movement > 0) {
                if (curDirection_)
                    tmpPosition = newPosition + movement;
                else
                    tmpPosition = newPosition - movement;
                if (routePosition_ == routeStreets_.length - 1 && destinations_.peekFirst().getStreet() == curStreet_) {
                    nextTarget = destinations_.peekFirst();
                    if ((curDirection_ && nextTarget.getPositionOnStreet() < tmpPosition) || (!curDirection_ && nextTarget.getPositionOnStreet() > tmpPosition)) {
                        movement = tmpPosition - nextTarget.getPositionOnStreet();
                        newPosition = nextTarget.getPositionOnStreet();
                        do {
                            destinations_.poll();
                            if (destinations_.isEmpty())
                                break;
                            curWaitTime_ = destinations_.peekFirst().getWaittime();
                        } while (!calculateRoute(true, false));
                        if (destinations_.isEmpty()) {
                            if (slowOn)
                                PrivacyLogWriter.log("VehicleReachedDestination:" + this.steadyID_ + ":" + Long.toHexString(this.ID_));
                            active_ = false;
                            /*bricol*/
                            vehicleStat_ = "HorsSystem";
                            curWaitTime_ = Integer.MIN_VALUE;
                            if (totalTravelTime_ >= minTravelTimeForRecycling_)
                                mayBeRecycled_ = true;
                            break;
                        } else
                            brakeForDestinationCountdown_ = Integer.MAX_VALUE;
                        if (curWaitTime_ > 0) {
                            curSpeed_ = 0;
                            break;
                        } else
                            brakeForDestination_ = false;
                    } else {
                        newPosition = tmpPosition;
                        movement = 0;
                    }
                } else if ((curDirection_ && tmpPosition > curStreet_.getLength()) || (!curDirection_ && tmpPosition < 0)) {
                    if (curDirection_)
                        movement = tmpPosition - curStreet_.getLength();
                    else
                        movement = -tmpPosition;
                    ++routePosition_;
                    if (routePosition_ >= routeStreets_.length) {
                        if (curDirection_)
                            newPosition = curStreet_.getLength();
                        else
                            newPosition = 0;
                        do {
                            destinations_.poll();
                            if (destinations_.isEmpty())
                                break;
                            curWaitTime_ = destinations_.peekFirst().getWaittime();
                        } while (!calculateRoute(true, false));
                        if (destinations_.isEmpty()) {
                            active_ = false;
                            /*bricol*/
                            vehicleStat_ = "HorsSystem";
                            curWaitTime_ = Integer.MIN_VALUE;
                            if (totalTravelTime_ >= minTravelTimeForRecycling_)
                                mayBeRecycled_ = true;
                            break;
                        } else
                            brakeForDestinationCountdown_ = Integer.MAX_VALUE;
                        if (curWaitTime_ > 0) {
                            curSpeed_ = 0;
                            break;
                        } else
                            brakeForDestination_ = false;
                    }
                    curDirection_ = routeDirections_[routePosition_];
                    if (logJunctionFrequency_) {
                        Node tmpNode = null;
                        @SuppressWarnings("unused") int street1 = -1;
                        @SuppressWarnings("unused") int street2 = -1;
                        if (curDirection_ && routeStreets_[routePosition_].getStartNode().getCrossingStreetsCount() > 2) {
                            tmpNode = routeStreets_[routePosition_].getStartNode();
                            for (int b = 0; b < tmpNode.getCrossingStreetsCount(); b++) {
                                if (tmpNode.getCrossingStreets()[b].equals(curStreet_))
                                    street1 = b;
                                if (tmpNode.getCrossingStreets()[b].equals(routeStreets_[routePosition_]))
                                    street2 = b;
                            }
                        } else if (!curDirection_ && routeStreets_[routePosition_].getEndNode().getCrossingStreetsCount() > 2) {
                            tmpNode = routeStreets_[routePosition_].getEndNode();
                            for (int b = 0; b < tmpNode.getCrossingStreetsCount(); b++) {
                                if (tmpNode.getCrossingStreets()[b].equals(curStreet_))
                                    street1 = b;
                                if (tmpNode.getCrossingStreets()[b].equals(routeStreets_[routePosition_]))
                                    street2 = b;
                            }
                        }
                    }
                    curStreet_ = routeStreets_[routePosition_];
                    if (curDirection_) {
                        if (curStreet_.getStartNode() == junctionAllowed_) {
                            junctionAllowed_.getJunction().allowOtherVehicle();
                            junctionAllowed_ = null;
                        }
                        newPosition = 0;
                    } else {
                        if (curStreet_.getEndNode() == junctionAllowed_) {
                            junctionAllowed_.getJunction().allowOtherVehicle();
                            junctionAllowed_ = null;
                        }
                        newPosition = curStreet_.getLength();
                    }
                } else {
                    newPosition = tmpPosition;
                    movement = 0;
                }
                
            }
            if (!active_ || curWaitTime_ != 0) {
                oldStreet.delLaneObject(this, oldDirection);
                curPosition_ = newPosition;
            } else if (curStreet_ != oldStreet || curDirection_ != oldDirection) {
                if (curStreet_.getLanesCount() < curLane_) {
                    curLane_ = curStreet_.getLanesCount();
                    newLane_ = curLane_;
                }
                oldStreet.delLaneObject(this, oldDirection);
                curPosition_ = newPosition;
                curStreet_.addLaneObject(this, curDirection_);
            } else if (curLane_ > 1 || drivingOnTheSide_ || passingBlocking_) {
                passingBlocking_ = false;
                curStreet_.updateLaneObject(this, curDirection_, newPosition);
            } else {
                curPosition_ = newPosition;
            }
            if (curStreet_ != null) 
            {
                calculatePosition();
 
                
            }
            if (curX_ < curRegion_.getLeftBoundary() || curX_ > curRegion_.getRightBoundary() || curY_ < curRegion_.getUpperBoundary() || curY_ > curRegion_.getLowerBoundary()) {
                curRegion_.delVehicle(this);
                curRegion_ = MAP.getRegionOfPoint(curX_, curY_);
                curRegion_.addVehicle(this, false);
            }
        }
    }
    
    
    
    public void PredictPosition(int timePerStep) {
        if (curWaitTime_ == 0 && curStreet_ != null) {
            curLane_ = newLane_;
            curSpeed_ = newSpeed_;
            double tmpPosition, newPosition = curPosition_, movement;
            WayPoint nextTarget;
            movement = curSpeed_ * (timePerStep / 1000.0);
            totalTravelTime_ += timePerStep;
            totalTravelDistance_ += movement;
            Street oldStreet = curStreet_;
            boolean oldDirection = curDirection_;
            while (movement > 0) {
                if (curDirection_)
                    tmpPosition = newPosition + movement;
                else
                    tmpPosition = newPosition - movement;
                if (routePosition_ == routeStreets_.length - 1 && destinations_.peekFirst().getStreet() == curStreet_) {
                    nextTarget = destinations_.peekFirst();
                    if ((curDirection_ && nextTarget.getPositionOnStreet() < tmpPosition) || (!curDirection_ && nextTarget.getPositionOnStreet() > tmpPosition)) {
                        movement = tmpPosition - nextTarget.getPositionOnStreet();
                        newPosition = nextTarget.getPositionOnStreet();
                        do {
                            destinations_.poll();
                            if (destinations_.isEmpty())
                                break;
                            curWaitTime_ = destinations_.peekFirst().getWaittime();
                        } while (!calculateRoute(true, false));
                        if (destinations_.isEmpty()) {
                            if (slowOn)
                                PrivacyLogWriter.log("VehicleReachedDestination:" + this.steadyID_ + ":" + Long.toHexString(this.ID_));
                            active_ = false;
                            /*bricol*/
                            vehicleStat_ = "HorsSystem";
                            curWaitTime_ = Integer.MIN_VALUE;
                            if (totalTravelTime_ >= minTravelTimeForRecycling_)
                                mayBeRecycled_ = true;
                            break;
                        } else
                            brakeForDestinationCountdown_ = Integer.MAX_VALUE;
                        if (curWaitTime_ > 0) {
                            curSpeed_ = 0;
                            break;
                        } else
                            brakeForDestination_ = false;
                    } else {
                        newPosition = tmpPosition;
                        movement = 0;
                    }
                } else if ((curDirection_ && tmpPosition > curStreet_.getLength()) || (!curDirection_ && tmpPosition < 0)) {
                    if (curDirection_)
                        movement = tmpPosition - curStreet_.getLength();
                    else
                        movement = -tmpPosition;
                    ++routePosition_;
                    if (routePosition_ >= routeStreets_.length) {
                        if (curDirection_)
                            newPosition = curStreet_.getLength();
                        else
                            newPosition = 0;
                        do {
                            destinations_.poll();
                            if (destinations_.isEmpty())
                                break;
                            curWaitTime_ = destinations_.peekFirst().getWaittime();
                        } while (!calculateRoute(true, false));
                        if (destinations_.isEmpty()) {
                            active_ = false;
                            /*bricol*/
                            vehicleStat_ = "HorsSystem";
                            curWaitTime_ = Integer.MIN_VALUE;
                            if (totalTravelTime_ >= minTravelTimeForRecycling_)
                                mayBeRecycled_ = true;
                            break;
                        } else
                            brakeForDestinationCountdown_ = Integer.MAX_VALUE;
                        if (curWaitTime_ > 0) {
                            curSpeed_ = 0;
                            break;
                        } else
                            brakeForDestination_ = false;
                    }
                    curDirection_ = routeDirections_[routePosition_];
                    if (logJunctionFrequency_) {
                        Node tmpNode = null;
                        @SuppressWarnings("unused") int street1 = -1;
                        @SuppressWarnings("unused") int street2 = -1;
                        if (curDirection_ && routeStreets_[routePosition_].getStartNode().getCrossingStreetsCount() > 2) {
                            tmpNode = routeStreets_[routePosition_].getStartNode();
                            for (int b = 0; b < tmpNode.getCrossingStreetsCount(); b++) {
                                if (tmpNode.getCrossingStreets()[b].equals(curStreet_))
                                    street1 = b;
                                if (tmpNode.getCrossingStreets()[b].equals(routeStreets_[routePosition_]))
                                    street2 = b;
                            }
                        } else if (!curDirection_ && routeStreets_[routePosition_].getEndNode().getCrossingStreetsCount() > 2) {
                            tmpNode = routeStreets_[routePosition_].getEndNode();
                            for (int b = 0; b < tmpNode.getCrossingStreetsCount(); b++) {
                                if (tmpNode.getCrossingStreets()[b].equals(curStreet_))
                                    street1 = b;
                                if (tmpNode.getCrossingStreets()[b].equals(routeStreets_[routePosition_]))
                                    street2 = b;
                            }
                        }
                    }
                    curStreet_ = routeStreets_[routePosition_];
                    if (curDirection_) {
                        if (curStreet_.getStartNode() == junctionAllowed_) {
                            junctionAllowed_.getJunction().allowOtherVehicle();
                            junctionAllowed_ = null;
                        }
                        newPosition = 0;
                    } else {
                        if (curStreet_.getEndNode() == junctionAllowed_) {
                            junctionAllowed_.getJunction().allowOtherVehicle();
                            junctionAllowed_ = null;
                        }
                        newPosition = curStreet_.getLength();
                    }
                } else {
                    newPosition = tmpPosition;
                    movement = 0;
                }
                
            }
        this.predictedPosition = newPosition;
        }
        
    }


    public final void moveAttacker() {
        Vehicle tmpAttacked = Renderer.getInstance().getAttackedVehicle();
        if (isInMixZone_ && firstContact)
            attackerWasInMix = true;
        if (tmpAttacked != null && tmpAttacked.isInMixZone_ && firstContact) {
            Renderer.getInstance().setAttackedVehicle(null);
            Vehicle.setAttackedVehicleID_(0);
            attackedWasInMix = true;
            newSpeed_ = curStreet_.getSpeed();
            searchAttackedVehicle_();
        }
        if (attackedWasInMix && attackerWasInMix && !isInMixZone_ && firstContact) {
            if (getKnownVehiclesList().findNearestVehicle(0, 0, curX_, curY_, 10000000) != null) {
                Vehicle.setAttackedVehicleID_(getKnownVehiclesList().findNearestVehicle(0, 0, curX_, curY_, 10000000).getID());
                Renderer.getInstance().setAttackedVehicle(getKnownVehiclesList().findNearestVehicle(0, 0, curX_, curY_, 10000000));
                attackedWasInMix = false;
            }
        }
        if (attackedVehicleID_ != 0) {
            reRouteTime_--;
            if (reRouteTime_ < 0) {
                reRouteTime_ = ATTACKER_INTERVAL;
                long dx, dy, dg;
                KnownVehicle[] heads = knownVehiclesList_.getFirstKnownVehicle();
                KnownVehicle next;
                for (int l = 0; l < heads.length; ++l) {
                    next = heads[l];
                    while (next != null) {
                        if (next.getVehicle().getID() == attackedVehicleID_) {
                            firstContact = true;
                            dx = next.getVehicle().getX() - curX_;
                            dy = next.getVehicle().getY() - curY_;
                            dg = (dx * dx + dy * dy);
                            if (dg > 60000000)
                                newSpeed_ = maxSpeed_;
                            else if (dg > 20000000 && dg < 60000000)
                                newSpeed_ = Renderer.getInstance().getAttackedVehicle().getCurSpeed();
                            else if (dg < 20000000)
                                newSpeed_ = 0;
                            if (dg > 10000000) {
                                getDestinations().clear();
                                try {
                                    getDestinations().add(new WayPoint(next.getX(), next.getY(), 0));
                                    getDestinations().add(new WayPoint(next.getX(), next.getY(), 0));
                                    calculateRoute(false, true);
                                    brakeForDestination_ = false;
                                    brakeForDestinationCountdown_ = 1000;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            next = null;
                        } else
                            next = next.getNext();
                    }
                }
            }
        }
    }

    public void searchAttackedVehicle_() {
        boolean NodeFound = false;
        Node tempNode = curStreet_.getStartNode();
        if (!curDirection_)
            tempNode = curStreet_.getEndNode();
        int guessX = curX_ - destinations_.peekFirst().getX();
        int guessY = curY_ - destinations_.peekFirst().getY();
        for (int i = 0; i < tempNode.getCrossingStreets().length; i++) {
            Node tempNode2;
            if (tempNode.equals(tempNode.getCrossingStreets()[i].getEndNode()))
                tempNode2 = tempNode.getCrossingStreets()[i].getStartNode();
            else
                tempNode2 = tempNode.getCrossingStreets()[i].getEndNode();
            if (guessX < 0 && guessY < 0)
                if (curX_ < tempNode2.getX() && curY_ < tempNode2.getY()) {
                    NodeFound = true;
                    tempNode = tempNode2;
                    i = 10;
                } else if (guessX < 0 && guessY > 0)
                    if (curX_ < tempNode2.getX() && curY_ > tempNode2.getY()) {
                        NodeFound = true;
                        tempNode = tempNode2;
                        i = 10;
                    } else if (guessX > 0 && guessY > 0)
                        if (curX_ > tempNode2.getX() && curY_ > tempNode2.getY()) {
                            NodeFound = true;
                            tempNode = tempNode2;
                            i = 10;
                        } else if (guessX > 0 && guessY < 0)
                            if (curX_ > tempNode2.getX() && curY_ < tempNode2.getY()) {
                                NodeFound = true;
                                tempNode = tempNode2;
                                i = 10;
                            }
        }
        if (!NodeFound) {
            for (int i = 0; i < tempNode.getCrossingStreets().length; i++) {
                Node tempNode2;
                if (tempNode.equals(tempNode.getCrossingStreets()[i].getEndNode()))
                    tempNode2 = tempNode.getCrossingStreets()[i].getStartNode();
                else
                    tempNode2 = tempNode.getCrossingStreets()[i].getEndNode();
                if (Math.abs(guessX) > Math.abs(guessY)) {
                    if (guessX < 0) {
                        if (curX_ < tempNode2.getX()) {
                            tempNode = tempNode2;
                            i = 10;
                        }
                    } else if (guessX > 0) {
                        if (curX_ > tempNode2.getX()) {
                            tempNode = tempNode2;
                            i = 10;
                        }
                    }
                } else {
                    if (guessY < 0) {
                        if (curY_ < tempNode2.getY()) {
                            tempNode = tempNode2;
                            i = 10;
                        }
                    } else if (guessY > 0) {
                        if (curY_ > tempNode2.getY()) {
                            tempNode = tempNode2;
                            i = 10;
                        }
                    }
                }
            }
        }
        for (int j = 0; j < 30; j++) {
            NodeFound = false;
            for (int i = 0; i < tempNode.getCrossingStreets().length; i++) {
                Node tempNode2;
                if (tempNode.equals(tempNode.getCrossingStreets()[i].getEndNode()))
                    tempNode2 = tempNode.getCrossingStreets()[i].getStartNode();
                else
                    tempNode2 = tempNode.getCrossingStreets()[i].getEndNode();
                if (guessX < 0 && guessY < 0)
                    if (tempNode.getX() < tempNode2.getX() && tempNode.getY() < tempNode2.getY()) {
                        NodeFound = true;
                        tempNode = tempNode2;
                        i = 10;
                    } else if (guessX < 0 && guessY > 0)
                        if (tempNode.getX() < tempNode2.getX() && tempNode.getY() > tempNode2.getY()) {
                            NodeFound = true;
                            tempNode = tempNode2;
                            i = 10;
                        } else if (guessX > 0 && guessY > 0)
                            if (tempNode.getX() > tempNode2.getX() && tempNode.getY() > tempNode2.getY()) {
                                NodeFound = true;
                                tempNode = tempNode2;
                                i = 10;
                            } else if (guessX > 0 && guessY < 0)
                                if (tempNode.getX() > tempNode2.getX() && tempNode.getY() < tempNode2.getY()) {
                                    NodeFound = true;
                                    tempNode = tempNode2;
                                    i = 10;
                                }
            }
            if (!NodeFound) {
                for (int i = 0; i < tempNode.getCrossingStreets().length; i++) {
                    Node tempNode2;
                    if (tempNode.equals(tempNode.getCrossingStreets()[i].getEndNode()))
                        tempNode2 = tempNode.getCrossingStreets()[i].getStartNode();
                    else
                        tempNode2 = tempNode.getCrossingStreets()[i].getEndNode();
                    if (Math.abs(guessX) > Math.abs(guessY)) {
                        if (guessX < 0) {
                            if (tempNode.getX() < tempNode2.getX()) {
                                tempNode = tempNode2;
                                i = 10;
                            }
                        } else if (guessX > 0) {
                            if (tempNode.getX() > tempNode2.getX()) {
                                tempNode = tempNode2;
                                i = 10;
                            }
                        }
                    } else {
                        if (guessY < 0) {
                            if (tempNode.getY() < tempNode2.getY()) {
                                tempNode = tempNode2;
                                i = 10;
                            }
                        } else if (guessY > 0) {
                            if (tempNode.getY() > tempNode2.getY()) {
                                tempNode = tempNode2;
                                i = 10;
                            }
                        }
                    }
                }
            }
        }
        getDestinations().clear();
        try {
            getDestinations().add(new WayPoint(tempNode.getX(), tempNode.getY(), 0));
            getDestinations().add(new WayPoint(tempNode.getX(), tempNode.getY(), 0));
            calculateRoute(false, true);
            brakeForDestination_ = false;
            brakeForDestinationCountdown_ = 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        ID_ = ownRandom_.nextLong();
        steadyID_ = steadyIDCounter++;
        curSpeed_ = brakingRate_ / 2;
        newSpeed_ = curSpeed_;
        totalTravelTime_ = 0;
        totalTravelDistance_ = 0;
        newLane_ = 1;
        active_ = false;
        /*bricol*/
        vehicleStat_ = "Inactif";
        speedAtLastBrakingDistanceCalculation_ = 0;
        isInMixZone_ = false;
        junctionAllowed_ = null;
        brakeForDestination_ = false;
        brakeForDestinationCountdown_ = Integer.MAX_VALUE;
        destinationCheckCountdown_ = 0;
        laneChangeCountdown = 0;
        communicationCountdown_ = 0;
        knownVehiclesTimeoutCountdown_ = 0;
        knownPenaltiesTimeoutCountdown_ = 0;
        beaconCountdown_ = (int) Math.round(curPosition_) % beaconInterval_;
        communicationCountdown_ = (int) Math.round(curPosition_) % communicationInterval_;
        mixCheckCountdown_ = (int) Math.round(curPosition_) % MIX_CHECK_INTERVAL;
        emergencyBrakingCountdown_ = ownRandom_.nextInt(emergencyBrakingInterval_) + 1;
        lastRHCNMessageCreated = 0;
        lastPCNMessageCreated = 0;
        lastPCNFORWARDMessageCreated = 0;
        lastEVAMessageCreated = 0;
        stopTime_ = 0;
        passingBlocking_ = false;
        IDsChanged_ = 0;
        isInSlow = false;
        changedPseudonymInSlow = false;
        slowBeaconsLogged = false;
        vehicleJustStartedInSlow = true;
        knownVehiclesList_.clear();
       
        knownMessages_.clear();
        
     
        knownRSUsTimeoutCountdown_ = 0;
        speedFluctuationCountdown_ = (int) Math.round(curPosition_) % SPEED_FLUCTUATION_CHECKINTERVAL;
        knownEventSourcesList_.clear();
        curX_ = startingWayPoint_.getX();
        curY_ = startingWayPoint_.getY();
        curPosition_ = startingWayPoint_.getPositionOnStreet();
        curStreet_ = startingWayPoint_.getStreet();
        curWaitTime_ = startingWayPoint_.getWaittime();
        destinations_ = originalDestinations_.clone();
        if (curStreet_.isOneway()) {
            while (!destinations_.isEmpty() && (!calculateRoute(false, false) || destinations_.peekFirst().getStreet() == curStreet_)) {
                curWaitTime_ = destinations_.pollFirst().getWaittime();
            }
        } else {
            while (!destinations_.isEmpty() && (!calculateRoute(false, false) || destinations_.peekFirst().getStreet() == curStreet_)) {
                curWaitTime_ = destinations_.pollFirst().getWaittime();
            }
        }
        if (curWaitTime_ == 0) {
            active_ = true;
            /*bricol*/
            vehicleStat_ = "Actif";
            int  pos = Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,steadyID_);
            vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
            curStreet_.addLaneObject(this, curDirection_);
        }
        calculatePosition();
                    

        curRegion_.delVehicle(this);
        curRegion_ = MAP.getRegionOfPoint(curX_, curY_);
        curRegion_.addVehicle(this, false);
        mayBeRecycled_ = false;
    }

    public static void resetGlobalRandomGenerator() {
        RANDOM.setSeed(1L);
    }

    public String toString() {
        return Long.toHexString(ID_).substring(0, 5);
    }

    public boolean getMayBeRecycled() {
        return mayBeRecycled_;
    }

    public Street[] getRouteStreets() {
        return routeStreets_;
    }

    public boolean[] getRouteDirections() {
        return routeDirections_;
    }

    public int getRoutePosition() {
        return routePosition_;
    }

    public int getVehicleID() {
        return vehId_;
    }

    public int getCommunicationCountdown() {
        return communicationCountdown_;
    }

    public int getBeaconCountdown() {
        return beaconCountdown_;
    }

    public WayPoint getStartPoint() {
        return startingWayPoint_;
    }

    public int getRegionX() {
        return curRegion_.getX();
    }

    public int getRegionY() {
        return curRegion_.getY();
    }

    public int getMaxSpeed() {
        return maxSpeed_;
    }

    public int getWaittime() {
        if (curWaitTime_ < 0)
            return 0;
        else
            return curWaitTime_;
    }

    public ArrayDeque<WayPoint> getDestinations() {
        return destinations_;
    }

    public int getMaxCommDistance() {
        return maxCommDistance_;
    }

    public boolean isActive() {
        return active_;
    }

    public boolean isInMixZone() {
        return isInMixZone_;
    }

    public boolean isWiFiEnabled() {
        return wiFiEnabled_;
    }

    public KnownMessages getKnownMessages() {
        return knownMessages_;
    }

   

    public KnownVehiclesList getKnownVehiclesList() {
        return knownVehiclesList_;
    }

 

    public int getIDsChanged() {
        return IDsChanged_;
    }

    public int getTotalTravelTime() {
        return totalTravelTime_;
    }

    public long getTotalTravelDistance() {
        return totalTravelDistance_;
    }

    public void setRegion(Region region) {
        curRegion_ = region;
    }
    
    public void setVehicleID(int id) {
        vehId_ = id;
    }

    public String getHexID() {
        if (wiFiEnabled_)
            return Long.toHexString(ID_);
        else
            return "(" + Long.toHexString(ID_) + ")";
    }

    public static int getCommunicationInterval() {
        return communicationInterval_;
    }

    public static int getBeaconInterval() {
        return beaconInterval_;
    }

    public static boolean getCommunicationEnabled() {
        return communicationEnabled_;
    }

    public static boolean getRecyclingEnabled() {
        return recyclingEnabled_;
    }

    public static boolean getBeaconsEnabled() {
        return beaconsEnabled_;
    }

    public static boolean getMixZonesEnabled() {
        return mixZonesEnabled_;
    }

    public static boolean getMixZonesFallbackEnabled() {
        return mixZonesFallbackEnabled_;
    }

    public static boolean getMixZonesFallbackFloodingOnly() {
        return mixZonesFallbackFloodingOnly_;
    }

    public static int getRoutingMode() {
        return routingMode_;
    }

    public static int getMaximumCommunicationDistance() {
        return maximumCommunicationDistance_;
    }

    public static int getMinTravelTimeForRecycling() {
        return minTravelTimeForRecycling_;
    }

    public static int getMixZoneRadius() {
        return mixZoneRadius_;
    }

    public static void setMaxMixZoneRadius(int maxMixZoneRadius) {
        maxMixZoneRadius_ = maxMixZoneRadius;
    }

    public static int getMaxMixZoneRadius() {
        return maxMixZoneRadius_;
    }

    public static void setMixZoneRadius(int mixZoneRadius) {
        mixZoneRadius_ = mixZoneRadius;
    }

    public static void setMinTravelTimeForRecycling(int minTravelTimeForRecycling) {
        minTravelTimeForRecycling_ = minTravelTimeForRecycling;
    }

    public static void setMaximumCommunicationDistance(int maximumCommunicationDistance) {
        maximumCommunicationDistance_ = maximumCommunicationDistance;
    }

    public static void setRegions(Region[][] regions) {
        regions_ = regions;
    }

    public static void setCommunicationInterval(int communicationInterval) {
        communicationInterval_ = communicationInterval;
    }

    public static void setBeaconInterval(int beaconInterval) {
        beaconInterval_ = beaconInterval;
    }

    public static void setCommunicationEnabled(boolean state) {
         
        communicationEnabled_ = state;
    }

    public static void setRecyclingEnabled(boolean state) {
        recyclingEnabled_ = state;
    }

    public static void setBeaconsEnabled(boolean state) {
       
        beaconsEnabled_ = state;
    }

    public static void setMixZonesEnabled(boolean state) {
        mixZonesEnabled_ = state;
    }

    public static void setMixZonesFallbackEnabled(boolean state) {
        mixZonesFallbackEnabled_ = state;
    }

    public static void setMixZonesFallbackFloodingOnly(boolean state) {
        mixZonesFallbackFloodingOnly_ = state;
    }

    public static void setBeaconMonitorZoneEnabled(boolean beaconMonitorEnabled) {
        beaconMonitorEnabled_ = beaconMonitorEnabled;
      
    }

    public static boolean getbeaconMonitorEnabled() {
        return beaconMonitorEnabled_;
    }

    public static void setMonitoredMixZoneVariables(int beaconMonitorMinX, int beaconMonitorMaxX, int beaconMonitorMinY, int beaconMonitorMaxY) {
        beaconMonitorMinX_ = beaconMonitorMinX;
        beaconMonitorMaxX_ = beaconMonitorMaxX;
        beaconMonitorMinY_ = beaconMonitorMinY;
        beaconMonitorMaxY_ = beaconMonitorMaxY;
 
    }

    public static int getbeaconMonitorMinX() {
        return beaconMonitorMinX_;
    }

    public static int getbeaconMonitorMaxX() {
        return beaconMonitorMaxX_;
    }

    public static int getbeaconMonitorMinY() {
        return beaconMonitorMinX_;
    }

    public static int getbeaconMonitorMaxY() {
        return beaconMonitorMaxY_;
    }

    public static ReportingControlPanel getREPORT_PANEL() {
        return REPORT_PANEL;
    }

    public static void setRoutingMode(int mode) {
        routingMode_ = mode;
    }

    public long getID() {
        return ID_;
    }

    public void setWiFiEnabled(boolean wiFiEnabled) {
        wiFiEnabled_ = wiFiEnabled;
    }

    public void setMaxSpeed(int maxSpeed) {
        maxSpeed_ = maxSpeed;
    }

    public void setMaxCommDistance(int maxCommDistance) {
        maxCommDistance_ = maxCommDistance;
    }

    public void setCurWaitTime(int curWaitTime) {
        curWaitTime_ = curWaitTime;
    }

    public int getCurWaitTime() {
        return curWaitTime_;
    }

    public void setColor(Color color) {
        color_ = color;
    }

    public Color getColor() {
        return color_;
    }

    public void setBrakingRate(int brakingRate) {
        if (brakingRate <= 0)
            brakingRate_ = 300;
        else
            brakingRate_ = brakingRate;
    }

    public int getBrakingRate() {
        return brakingRate_;
    }

    public void setAccelerationRate(int accelerationRate) {
        if (accelerationRate <= 0)
            accelerationRate_ = 800;
        else
            accelerationRate_ = accelerationRate;
    }

    public int getAccelerationRate() {
        return accelerationRate_;
    }

    public void setEmergencyVehicle(boolean emergencyVehicle) {
        emergencyVehicle_ = emergencyVehicle;
    }

    public boolean isEmergencyVehicle() {
        return emergencyVehicle_;
    }

    public void setVehicleLength(int vehicleLength) {
        vehicleLength_ = vehicleLength;
    }

    public int getVehicleLength() {
        return vehicleLength_;
    }
 

    public static boolean isAttackerDataLogged_() {
        return attackerDataLogged_;
    }

    public static void setAttackerDataLogged_(boolean attackerDataLogged_) {
        Vehicle.attackerDataLogged_ = attackerDataLogged_;
    }

    public static long getAttackedVehicleID_() {
        return attackedVehicleID_;
    }

    public static void setAttackedVehicleID_(long attackedVehicleID_) {
        Vehicle.attackedVehicleID_ = attackedVehicleID_;
    }

    public static boolean isEncryptedBeaconsInMix_() {
        return encryptedBeaconsInMix_;
    }

    public static void setEncryptedBeaconsInMix_(boolean encryptedBeaconsInMix_) {
        Vehicle.encryptedBeaconsInMix_ = encryptedBeaconsInMix_;
    }

    public static boolean isAttackerEncryptedDataLogged_() {
        return attackerEncryptedDataLogged_;
    }

    public static void setAttackerEncryptedDataLogged_(boolean attackerEncryptedDataLogged_) {
        Vehicle.attackerEncryptedDataLogged_ = attackerEncryptedDataLogged_;
    }

    public Node getCurMixNode_() {
        return curMixNode_;
    }

    public void setCurMixNode_(Node curMixNode_) {
        this.curMixNode_ = curMixNode_;
    }

    public static ReportingControlPanel getReportingPanel() {
        if (Renderer.getInstance().isConsoleStart())
            return null;
        else
            return VanetSimStart.getMainControlPanel().getReportingPanel();
    }

    public boolean isWaitingForSignal_() {
        return waitingForSignal_;
    }

    public void setWaitingForSignal_(boolean waitingForSignal_) {
        this.waitingForSignal_ = waitingForSignal_;
    }

    public static boolean isPrivacyDataLogged_() {
        return privacyDataLogged_;
    }

    public static void setPrivacyDataLogged_(boolean privacyDataLogged_) {
        Vehicle.privacyDataLogged_ = privacyDataLogged_;
    }

    public void setTimeDistance(int timeDistance) {
        timeDistance_ = timeDistance;
    }

    public int getTimeDistance() {
        return timeDistance_;
    }

    public void setPoliteness(int politeness_) {
        this.politeness_ = politeness_;
    }

    public int getPoliteness() {
        return politeness_;
    }

    public static int getTIME_BETWEEN_SILENT_PERIODS() {
        return TIME_BETWEEN_SILENT_PERIODS;
    }

    public static void setTIME_BETWEEN_SILENT_PERIODS(int i) {
        TIME_BETWEEN_SILENT_PERIODS = i;
    }

    public static int getTIME_OF_SILENT_PERIODS() {
        return TIME_OF_SILENT_PERIODS;
    }

    public static void setTIME_OF_SILENT_PERIODS(int i) {
        TIME_OF_SILENT_PERIODS = i;
    }

    public static boolean isSilent_period() {
        return silent_period;
    }

    public static void setSilent_period(boolean silent_period) {
        Vehicle.silent_period = silent_period;
    }

    public static boolean isSilentPeriodsOn() {
        return silentPeriodsOn;
    }

    public static void setSilentPeriodsOn(boolean silentPeriodsOn) {
        Vehicle.silentPeriodsOn = silentPeriodsOn;
    }

    public static int getTIME_TO_PSEUDONYM_CHANGE() {
        return TIME_TO_PSEUDONYM_CHANGE;
    }

    public static void setTIME_TO_PSEUDONYM_CHANGE(int tIME_TO_PSEUDONYM_CHANGE) {
        TIME_TO_PSEUDONYM_CHANGE = tIME_TO_PSEUDONYM_CHANGE;
    }

    public static int getSLOW_SPEED_LIMIT() {
        return SLOW_SPEED_LIMIT;
    }

    public static void setSLOW_SPEED_LIMIT(int sLOW_SPEED_LIMIT) {
        SLOW_SPEED_LIMIT = sLOW_SPEED_LIMIT;
    }

    public static boolean isSlowOn() {
        return slowOn;
    }

    public static void setSlowOn(boolean slowOn) {
        Vehicle.slowOn = slowOn;
    }

    public boolean isInSlow() {
        return isInSlow;
    }

    public void setInSlow(boolean isInSlow) {
        this.isInSlow = isInSlow;
    }

    public boolean isChangedPseudonymInSlow() {
        return changedPseudonymInSlow;
    }

    public void setChangedPseudonymInSlow(boolean changedPseudonymInSlow) {
        this.changedPseudonymInSlow = changedPseudonymInSlow;
    }

    public int getSlowTimestamp() {
        return slowTimestamp;
    }

    public void setSlowTimestamp(int slowTimestamp) {
        this.slowTimestamp = slowTimestamp;
    }

    public int getSpeedDeviation_() {
        return speedDeviation_;
    }

    public void setSpeedDeviation_(int speedDeviation_) {
        this.speedDeviation_ = speedDeviation_;
    }

    public static boolean isIdsActivated() {
        return idsActivated;
    }

    public static void setIdsActivated(boolean idsActivated) {
        Vehicle.idsActivated = idsActivated;
    }

    public boolean isMoveOutOfTheWay_() {
        return moveOutOfTheWay_;
    }

    public void setMoveOutOfTheWay_(boolean moveOutOfTheWay) {
        moveOutOfTheWay_ = moveOutOfTheWay;
    }

    public boolean isFakingMessages() {
        return fakingMessages_;
    }

    public void setFakingMessages(boolean fakingMessages) {
        fakingMessages_ = fakingMessages;
    }

    public String getFakeMessageType() {
        return fakeMessageType_;
    }

    public void setFakeMessageType(String fakeMessageType) {
        fakeMessageType_ = fakeMessageType;
    }

    public static int getFakeMessagesInterval_() {
        return fakeMessagesInterval_;
    }

    public static void setFakeMessagesInterval_(int fakeMessagesInterval_) {
        Vehicle.fakeMessagesInterval_ = fakeMessagesInterval_;
    }

    public long getEmergencyBrakingCountdown_() {
        return emergencyBrakingCountdown_;
    }

    public void setEmergencyBrakingCountdown_(int emergencyBrakingCountdown_) {
        this.emergencyBrakingCountdown_ = emergencyBrakingCountdown_;
    }

    public int getEmergencyBrakingDuration_() {
        return emergencyBrakingDuration_;
    }

    public void setEmergencyBrakingDuration_(int emergencyBrakingDuration_) {
        this.emergencyBrakingDuration_ = emergencyBrakingDuration_;
    }

    public int getStopTime_() {
        return stopTime_;
    }

    public void setForwardMessage_(boolean forwardMessage_) {
        this.forwardMessage_ = forwardMessage_;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public boolean isDoNotRecycle_() {
        return doNotRecycle_;
    }

    public void setDoNotRecycle_(boolean doNotRecycle_) {
        this.doNotRecycle_ = doNotRecycle_;
    }

    public int getEmergencyBeacons() {
        return emergencyBeacons;
    }

    public void setEmergencyBeacons(int emergencyBeacons) {
        this.emergencyBeacons = emergencyBeacons;
    }

    public Vehicle getWaitingForVehicle_() {
        return waitingForVehicle_;
    }

    public void setWaitingForVehicle_(Vehicle waitingForVehicle_) {
        this.waitingForVehicle_ = waitingForVehicle_;
    }

    public boolean isDrivingOnTheSide_() {
        return drivingOnTheSide_;
    }

 

    public boolean isPassingBlocking_() {
        return passingBlocking_;
    }

    public void setPassingBlocking_(boolean passingBlocking_) {
        this.passingBlocking_ = passingBlocking_;
    }

    public int getPcnMessagesCreated_() {
        return pcnMessagesCreated_;
    }

    public int getPcnForwardMessagesCreated_() {
        return pcnForwardMessagesCreated_;
    }

    public int getEvaMessagesCreated_() {
        return evaMessagesCreated_;
    }

    public int getEvaForwardMessagesCreated_() {
        return evaForwardMessagesCreated_;
    }

    public int getRhcnMessagesCreated_() {
        return rhcnMessagesCreated_;
    }

    public int getEeblMessagesCreated_() {
        return eeblMessagesCreated_;
    }

    public int getFakeMessagesCreated_() {
        return fakeMessagesCreated_;
    }

    public boolean isInTrafficJam_() {
        return inTrafficJam_;
    }

    public void setInTrafficJam_(boolean inTrafficJam_) {
        this.inTrafficJam_ = inTrafficJam_;
    }

    public boolean isCheckIDSProcessors_() {
        return checkIDSProcessors_;
    }

    public void setCheckIDSProcessors_(boolean checkIDSProcessors_) {
        this.checkIDSProcessors_ = checkIDSProcessors_;
    }

    public void setEmergencyBraking_(boolean emergencyBraking_) {
        this.emergencyBraking_ = emergencyBraking_;
    }

    public boolean isEmergencyBraking_() {
        return emergencyBraking_;
    }

    public boolean isEEBLmessageIsCreated_() {
        return EEBLmessageIsCreated_;
    }

    public void setEEBLmessageIsCreated_(boolean eEBLmessageIsCreated_) {
        EEBLmessageIsCreated_ = eEBLmessageIsCreated_;
    }

    public KnownEventSourcesList getKnownEventSourcesList_() {
        return knownEventSourcesList_;
    }

    public int getSpamCounter_() {
        return spamCounter_;
    }

    public void setSpamCounter_(int spamCounter_) {
        this.spamCounter_ = spamCounter_;
    }

    public int getMessagesCounter_() {
        return messagesCounter_;
    }

    public void setMessagesCounter_(int messagesCounter_) {
        this.messagesCounter_ = messagesCounter_;
    }

    public Random getOwnRandom_() {
        return ownRandom_;
    }

    public int getEVAMessageDelay_() {
        return EVAMessageDelay_;
    }

    public void setEVAMessageDelay_(int eVAMessageDelay_) {
        EVAMessageDelay_ = eVAMessageDelay_;
    }

    public static int getMaxEVAMessageDelay_() {
        return maxEVAMessageDelay_;
    }

    public static void setMaxEVAMessageDelay_(int theMaxEVAMessageDelay_) {
        maxEVAMessageDelay_ = theMaxEVAMessageDelay_;
    }

    public boolean isLogBeaconsAfterEvent_() {
        return logBeaconsAfterEvent_;
    }

    public void setLogBeaconsAfterEvent_(boolean logBeaconsAfterEvent_) {
        this.logBeaconsAfterEvent_ = logBeaconsAfterEvent_;
    }

    public int getAmountOfLoggedBeacons_() {
        return amountOfLoggedBeacons_;
    }

    public void setAmountOfLoggedBeacons_(int amountOfLoggedBeacons_) {
        this.amountOfLoggedBeacons_ = amountOfLoggedBeacons_;
    }

    public String getBeaconString_() {
        return beaconString_;
    }

    public void setBeaconString_(String beaconString_) {
        this.beaconString_ = beaconString_;
    }
    
    public String getVehicleStat() {
        return vehicleStat_;
    }
     
    public void setVehicleStat(String vehicleStat) {
        vehicleStat_ = vehicleStat;
    }
    
    public int getArrivalTime() {
        return arrivalTime_;
    }
     
    public void setArrivalTime(int arrivalTime) {
        arrivalTime_ = arrivalTime;
    }
    
//==============================================================================
    public static class Vehicule
   {
        //-----------------------Saliha parameters----------------------------------
    
   // static double Tvit[] = new double[3];//Table des vitesses   
    static Random rnd = new Random();
    public int idVeh; //l'identifiant du véhicule
    
    public int posx;  //la longitude du véhicule
    public int posy;  //la latitude du véhicule

    public double positionPredite;
    
    public int posPx;  //la longitude du véhicule prédite
    public int posPy;  //la latitude du véhicule prédite
    public double ActuelPosition; //position actuelle dans le map

    public int Xdt;
    public int Ydt;
    public int largeur;
    public int longueur;

    public int idVoie;
    public int lifeTime;
    public String etat;// 1:actif,-1:inactif,0:hors syst
    
    Thread th;
    long transdeb;
    long transfin;
   public boolean demarrer;
  
    public int INST[][];
    public int NBS[];//nbr de saut du nd i par rapport à tout les autres nds;
    public String CHM[];//chemin du nd i vers tout le reste des nds ;
    //----------------------------Propriétés en commun --> adapté pour chaque algo --------------------------
    public boolean est_init_FC;
    public boolean est_ch_FC;
    public boolean est_sch_FC;
    public boolean est_mn_FC;
    public boolean est_sm_FC;
    
    public boolean est_init_APV;
    public boolean est_ch_APV;
    public boolean est_mn_APV;

    public boolean est_init_VWCA;
    public boolean est_ch_VWCA;
    public boolean est_mn_VWCA;
    
    public boolean est_WCA_init;
    public boolean est_WCA_ch;
    public boolean est_WCA_mn;
    
    public boolean est_init_MOZO;
    public boolean est_ch_MOZO;
    public boolean est_mn_MOZO;
    
    public String type_noeud_FC;
    public String type_noeud_APV;
    public String type_noeud_VWCA;
    public String type_WCA_noeud;
    public String type_noeud_MOZO;
    
    
    public   Vector<String>  ETAT_FC;
    public   Vector<String>  ETAT_APV;
    public   Vector<String>  ETAT_VWCA;
    public   Vector<String>  ETAT_MOZO;
    
    public   Vector<Long> DUREE_FC;
    public   Vector<Long> DUREE_APV;
    public   Vector<Long> DUREE_VWCA;
    public   Vector<Long> DUREE_MOZO;
    

    public long transdeb_FC;
    public long transfin_FC;  
          
    public long transdeb_APV;
    public long transfin_APV; 
          
    public long transdeb_VWCA;
    public long transfin_VWCA; 
    
    public long transdeb_MOZO;
    public long transfin_MOZO;
    
    

    //-----------------------------FitnessClustering----------------------------

    public long TC; //a voir
    public long TS;
 //   public long TR[][];

    public double vitesse;
    public int nbvoisin;
    public String Dpacket[]; //saliha
    public double TReceivedPkt[][]; //saliha
    public int direction;      //saliha
    public double LinkValidity;//saliha
    public double RelativeVelocity;//saliha
    public double Fitness;
    public double RecFit[][];
    public int myCH_FC, myCH_APV, myCH_VWCA, myCH_MOZO;
    public double FitnessMyCH;
    public double Lcouv[][];//Liste de couverture
    public int IndCouv;
    //public int myCHapv;

    Packet TP[];        //doesn't match

    public int nbPacket;
 
    public DefaultTableModel CHDuration;
    public DefaultTableModel SCHDuration;
    public DefaultTableModel MNDuration;
    public DefaultTableModel SMDuration;
    //--------------------------------------------------------------------------
    public int TempsCH;
    public int TempsSCH;
    public int TempsMN;
    public int TempsSM ;
    
    //-----------------------------WCA------------------------------------------
    public DefaultListModel VS;  //Current neighbour
    public DefaultListModel OVS; //Old neighbour

    public int Delta;
    public double Dv;
    public double Mv;
    public double Drelative;
    public double Mrelative;
    public double Wi;
    public boolean situation;
    //------------------------
    public int lcav[]; //à l'instant ti
    public int lcap[]; //à l'instant ti+1
    public int Lcon;  //liens connctée
    public int Lrep;   //liens en reprture
    public double instant;
    //------------------------
    public int vtp[]; //à l'instant t1
    public int vts[];  //à l'instant t2

    public double T;

    //----------------------------APROVE----------------------------------------
    public double Th;
    public double Cvg;
    //----------------------------VWCA------------------------------------------
    public double DistrustValue;
    public DefaultListModel VerifL;
    //public DefaultListModel WL;
    public double Weight;
    public double Entropy;
    public double Dmoy;

    public int priority;
    public int myTrans;
    public double Abnormality;
    public int Ta;
    public double Tr1;
    public double Tr2;
    public double To;
    public double TpsPro;
    public double Rmin;
    public double Rnew;
    public double Rmax;
    //-----------------------------Fin Param Saliha-----------------------------
    //=================== ADHOC METHODE INTEGRATION =====================
    //----------------------------APROVE----------------------------------------
    public static void affichage_distance(int vact[], int portee) {
        if (vact.length > 0) {

            adhoc.GlobalParameters.DIST = new double[vact.length][vact.length];
            adhoc.GlobalParameters.LIEN = new int[vact.length][vact.length];
            adhoc.GlobalParameters.SAUT = new int[vact.length][vact.length];

            for (int i = 0; i < vact.length; i++) {
                for (int j = 0; j < vact.length; j++) 
                {
                    adhoc.GlobalParameters.DIST[i][j] = -1;
                    adhoc.GlobalParameters.LIEN[i][j] = 0;
                }
            }

            /*
             //-------------------------------
             D->ColCount = 1 + nbact;
             D->RowCount = 1 + nbact;
             //---------------------------------

             for(int i=0;i<D->RowCount;i++)
             for(int j=0;j<D->ColCount;j++)
             D->Cells[j][i] =" ";
             //---------------------------------

             for(int i=0;i<nbact;i++)
             {
             D->Cells[0][i+1] = "N"+IntToStr(vact[i]+1);
             D->Cells[i+1][0] = " N"+IntToStr(vact[i]+1);
             }
             */
	//----------------------------------------------
            for (int i = 0; i < vact.length; i++) {
                if ( TV[vact[i]].etat.equals("Actif")) 
                {
                    //System.out.println("Noeuds voisin de "+vact[i]+" sont :");
                    for (int j = 0; j < vact.length; j++) 
                    {

                        if (TV[vact[j]].etat.equals("Actif")) 
                        {
                            if (vact[i] != vact[j]) 
                            {
                                double dist = distance_euclidienne(vact[i], vact[j]);
                                
                                //System.out.println("distance entre "+vact[i]+" et "+vact[j]+" === "+dist+"  : portee : "+portee);
                                if (dist <= portee) 
                                {
                                    dist = Math.ceil(dist * 100) / 100;
                                    adhoc.GlobalParameters.DIST[i][j] = dist;
                                    adhoc.GlobalParameters.LIEN[i][j] = 1;
                                    //System.out.println(vact[j]+", distance ---> "+dist);
                                    //	D->Cells[j+1][i+1]= FloatToStr(dist);
                                } else 
                                {
                                    adhoc.GlobalParameters.LIEN[i][j] = 0;
                                }
                            } else {adhoc.GlobalParameters.LIEN[i][j] = 0;}
                        }
                        //System.out.println("--------------------------");
                    }
                }
            }

            //----------------------------------------------
        }

    }

    public static double distance_euclidienne(int idi, int idj) {

//        return Math.sqrt(Math.pow((TV[idi].posx + (TV[idi].largeur / 2)) - (TV[idj].posx + (TV[idj].largeur / 2)), 2)
//                + Math.pow((TV[idi].posy + (TV[idi].longueur / 2)) - (TV[idj].posy + (TV[idj].longueur / 2)), 2));
        return Math.sqrt(Math.pow((TV[idi].posx) - (TV[idj].posx), 2)
                + Math.pow((TV[idi].posy) - (TV[idj].posy), 2));

    }

    public static int[] ListVoisinFormation(int nd, int vact[], int nbv) {
        int L[];
           //System.out.println("Taille vect : "+vact.length+"             Taille Distold : "+adhoc.GlobalParameters.DISTold.length);

        if (nbv > 0) {

            L = new int[nbv];

            int n = 0;
            if (nd < adhoc.GlobalParameters.DISTold.length) {
                for (int i = 0; i < adhoc.GlobalParameters.DISTold.length/*vact.length*/; i++) //if(i<adhoc.GlobalParameters.DISTold.length)
                {
                    if (adhoc.GlobalParameters.DISTold[nd][i] > -1) {
                        if (n < nbv) {
                            L[n] = vact[i];//here
                            n++;
                        }
                    }
                }
            }

            return L;
        } else {
            return null;
        }

        /* 
         if(nbv<=adhoc.GlobalParameters.DISTold.length)
         {
         L = new int[nbv];   
                    
         if(nd<adhoc.GlobalParameters.DISTold.length)
         for(int i=0;i<nbv;i++)
         if(adhoc.GlobalParameters.DISTold[nd][i]>-1)
         {
         if(i<vact.length)
         {
         L[n] = vact[i];
         n++;
         }
         }
         }else
         {
         L = new int[adhoc.GlobalParameters.DISTold.length];   
         if(nd<adhoc.GlobalParameters.DISTold.length)
         for(int i=0;i<adhoc.GlobalParameters.DISTold.length;i++)
         if(i<adhoc.GlobalParameters.DISTold.length)
         if(adhoc.GlobalParameters.DISTold[nd][i]>-1)
         {
         if(i<vact.length)
         {
         L[n] = vact[i];
         n++;
         }
         }
         }
         */
    }

    public static int[] ListVoisin(int nd, int vact[], int nbv) {
        int L[];
        if (nbv > 0) {

            L = new int[nbv];
            int n = 0;
            if (nd < adhoc.GlobalParameters.DIST.length) {
                for (int i = 0; i < vact.length; i++) {
                    if (i < adhoc.GlobalParameters.DIST.length) {
                        if (adhoc.GlobalParameters.DIST[nd][i] > -1) {
                            // if(i<vact.length)
                            if (n < nbv) {
                                L[n] = vact[i];
                                n++;
                            }
                        }
                    }
                }
            }

            return L;
        } else {
            return null;
        }

    }

    //====================CREATION DES VEHICULES==================================
    public static void CreationVehicule(int newVeh,int X, int Y, int voie, int larg, int long_,double vitesse) 
    {
        /*
        TV = new Vehicule[nbv];
        //TV1 = new Vehicule[nbv];
        TV2 = new Vehicule[nbv];
        TV3 = new Vehicule[nbv];
        */
         /*
        for (int i = 0; i < 3; i++) {
            Tvit[i] = 40 + rnd.nextInt(10); //Contrôle de vitesse
            
        }*/

   //System.out.println("======================== newVeh "+newVeh+"/"+TV.length);
            Vehicule vh = new Vehicule();
            vh.idVeh = newVeh;
            
            

            vh.demarrer = false;
            vh.etat = "Inactif";
         
            vh.idVoie = voie;
            vh.posx = X;
            vh.posy = Y;
            
            
            
            vh.posPx = X;
            vh.posPy = Y;

            vh.th = null;
          
            vh.largeur = larg;
            vh.longueur = long_;
            
            vh.lifeTime =0;

            vh.CHM = null;
            vh.INST = null;
            vh.NBS = null;

            vh.vitesse = vitesse;
            
           // System.out.println("Creation de vehicule --> Vehicle ID "+vh.idVeh+" posx "+vh.posx+" posy "+vh.posy/*+" voie "+vh.idVoie+" vitesse "+vh.vitesse*/);
            vh.nbvoisin = 0;
            vh.TC = 0;
            vh.TS = 0;


            vh.Dpacket = null;
            vh.TReceivedPkt = null;

            vh.direction = 0;      //saliha
            vh.LinkValidity = 0;//saliha

            vh.RelativeVelocity = 0;//saliha

            vh.Fitness = 0.0;
            vh.RecFit = null;
            
            vh.myCH_FC = 0;
            vh.myCH_APV = 0;
            vh.myCH_VWCA = 0;
            vh.myCH_MOZO = 0;
            
            vh.FitnessMyCH = 0.0;
            vh.Lcouv = null;
            vh.IndCouv = 0;
            vh.TP = null;

            vh.nbPacket = 0;

            vh.est_init_FC = true;
            vh.est_ch_FC = false;
            vh.est_sch_FC = false;
            vh.est_mn_FC = false;
            vh.est_sm_FC = false;
            
            vh.est_init_APV = true;
            vh.est_ch_APV = false;
            vh.est_mn_APV = false;

            vh.est_init_VWCA = true;
            vh.est_ch_VWCA = false;
            vh.est_mn_VWCA= false;
            
            vh.est_init_MOZO = true;
            vh.est_ch_MOZO = false;
            vh.est_mn_MOZO = false;
            
            vh.type_noeud_FC = "Init";
            vh.type_noeud_APV = "Init";
            vh.type_noeud_VWCA = "Init";
            vh.type_noeud_MOZO = "Init";            
            
            
            vh.CHDuration = new DefaultTableModel();
            vh.SCHDuration = new DefaultTableModel();
            vh.MNDuration = new DefaultTableModel();
            vh.SMDuration = new DefaultTableModel();
    //-----------------------------WCA------------------------------------------
            vh.instant = 0;
            vh.Lcon = 0;
            vh.Lrep = 0;
            vh.Delta = 0;
            vh.Dv = 0.0;
            vh.Mv = 0.0;
            vh.Drelative = 0.0;
            vh.Mrelative = 0.0;
            vh.Wi = 0.0;
            vh.Lcon = 0;
            vh.Lrep = 0;
            vh.vtp = null; //à l'instant t1
            vh.vts = null;  //à l'instant t2
            vh.T = 0.0;

            vh.VS = new DefaultListModel();

            vh.Xdt = 0;
            vh.Ydt = 0;
            vh.situation = false;
            vh.lcav = null;
            vh.lcap = null;

            vh.Cvg = 0;
            //vh.myCHapv = 0;
            vh.OVS = new DefaultListModel();
            //--------------------VWCA-----------------------------------------------------     
            vh.DistrustValue = 1.0;

            vh.Weight = 0.0;
            vh.Entropy = 0.0;
            vh.Dmoy = 0.0;

            vh.priority = -1;
            vh.myTrans = adhoc.Adhoc.portee;
            vh.VerifL = new DefaultListModel();
            vh.Abnormality = 0.0;
            vh.Ta = 0;
            vh.Tr1 = 0.0;
            vh.Tr2 = 0.0;
            vh.To = 0.0;
            vh.TpsPro = 0.0;
            vh.Rmin = 100;
            vh.Rnew = 200;
            vh.Rmax = adhoc.Adhoc.rtMax;
    //-------------------------------------------------------------------------     

          vh.ETAT_FC  = new Vector<String>(); 
          vh.DUREE_FC  = new Vector<Long>();  
          vh.transdeb_FC = System.currentTimeMillis();
          vh.transfin_FC =0;  
          
          vh.ETAT_APV  = new Vector<String>(); 
          vh.DUREE_APV  = new Vector<Long>();  
          vh.transdeb_APV = System.currentTimeMillis();
          vh.transfin_APV =0; 
          
          vh.ETAT_VWCA  = new Vector<String>(); 
          vh.DUREE_VWCA = new Vector<Long>();  
          vh.transdeb_VWCA = System.currentTimeMillis();
          vh.transfin_VWCA =0; 
          
          vh.ETAT_MOZO  = new Vector<String>(); 
          vh.DUREE_MOZO = new Vector<Long>();  
          vh.transdeb_MOZO = System.currentTimeMillis();
          vh.transfin_MOZO =0; 
            
          TV[newVeh] = vh;
            
          TV[newVeh].ETAT_FC.add("Init");
          TV[newVeh].ETAT_APV.add("Init");
          TV[newVeh].ETAT_VWCA.add("Init");
          TV[newVeh].ETAT_MOZO.add("Init");
            
            
            //TV1[i] = vh;
            //TV2[newVeh] = vh;
            //TV3[newVeh] = vh;
            //------------------------------------------------------
            TV[newVeh].CHDuration.setColumnCount(0);
            TV[newVeh].CHDuration.addColumn("TdebutCH");
            TV[newVeh].CHDuration.addColumn("TfinCH");
            TV[newVeh].CHDuration.setRowCount(1);
            TV[newVeh].CHDuration.setValueAt("-1",TV[newVeh].CHDuration.getRowCount()-1 , 0);
            TV[newVeh].CHDuration.setValueAt("-1",TV[newVeh].CHDuration.getRowCount()-1 , 1);
            //------------------------------------------------------
            TV[newVeh].SCHDuration.setColumnCount(0);
            TV[newVeh].SCHDuration.addColumn("TdebutSCH");
            TV[newVeh].SCHDuration.addColumn("TfinSCH");
            TV[newVeh].SCHDuration.setRowCount(1);
            TV[newVeh].SCHDuration.setValueAt("-1",TV[newVeh].SCHDuration.getRowCount()-1 , 0);
            TV[newVeh].SCHDuration.setValueAt("-1",TV[newVeh].SCHDuration.getRowCount()-1 , 1);
            //------------------------------------------------------
            TV[newVeh].MNDuration.setColumnCount(0);
            TV[newVeh].MNDuration.addColumn("TdebutMN");
            TV[newVeh].MNDuration.addColumn("TfinMN");
            TV[newVeh].MNDuration.setRowCount(1);
            TV[newVeh].MNDuration.setValueAt("-1",TV[newVeh].MNDuration.getRowCount()-1 , 0);
            TV[newVeh].MNDuration.setValueAt("-1",TV[newVeh].MNDuration.getRowCount()-1 , 1);
            //------------------------------------------------------
            TV[newVeh].SMDuration.setColumnCount(0);
            TV[newVeh].SMDuration.addColumn("TdebutSM");
            TV[newVeh].SMDuration.addColumn("TfinSM");
            TV[newVeh].SMDuration.setRowCount(1);
            TV[newVeh].SMDuration.setValueAt("-1",TV[newVeh].SMDuration.getRowCount()-1 , 0);
            TV[newVeh].SMDuration.setValueAt("-1",TV[newVeh].SMDuration.getRowCount()-1 , 1);
            
            
            
            
            TV[newVeh].Dpacket = new String[9];
            //------------------------------------
            TV[newVeh].TReceivedPkt = new double[1][11];

            //------------------------------------
            /*
            if (TV[newVeh].idVoie == 1) {
                TV[newVeh].vitesse = Tvit[0];
            }
            if (TV[newVeh].idVoie == 2) {
                TV[newVeh].vitesse = Tvit[1];
            }
            if (TV[newVeh].idVoie == 3) {
                TV[newVeh].vitesse = Tvit[2];
            }
            */
            //-------------------------------------
            //TV1[i].vitesse = TV[newVeh].vitesse;
          //  TV2[newVeh].vitesse = TV[newVeh].vitesse;
          //  TV3[newVeh].vitesse = TV[newVeh].vitesse;
            //-------------------------------------
            TV[newVeh].NBS = new int[TV.length];
            TV[newVeh].CHM = new String[TV.length];
            TV[newVeh].INST = new int[2][TV.length];
                                //---------------------------

            //---------------------------
            for (int j = 0; j < TV.length; j++) {
                TV[newVeh].INST[0][j] = 0;
                TV[newVeh].INST[1][j] = 0;
            }
            //------------------------------------------------------
             
      
    }
     //----------------------------------------------------

 
    //-------------------------------------------------------------------------

    public static void Simulation(int idv, String S[][]) {
        TV[idv].th = new Thread() {

            public void run() {
                while (TV[idv].demarrer == true) {
                    if (TV[idv].etat.equals("Actif")) 
                    {
                        //DetruireVehicule(idv);
                        TV[idv].posx = TV[idv].posx - (int) (TV[idv].vitesse * 0.104875);

                        //ConstruireVehicule(idv);
                        S[idv][2] = "" + TV[idv].posx;
                        S[idv][3] = "" + TV[idv].posy;
                        S[idv][4] = "" + TV[idv].etat;

                        if (TV[idv].posx < -TV[idv].largeur) 
                        {
                            TV[idv].posx = 0;
                            TV[idv].demarrer = false;
                            TV[idv].etat = "Hors systéme";
                            //--------------------------------------------------
                            Algorithms.FitnessClustering.CloseStateDuration(idv, adhoc.Adhoc.compteur / 100);
                            
                            System.out.println(" Compteur === "+adhoc.Adhoc.compteur+" Temps darrivée === "+Integer.parseInt(adhoc.Adhoc.S[idv][1])+" Lifetime de "+S[idv][0]+" === "+TV[idv].lifeTime);
                            TV[idv].lifeTime = adhoc.Adhoc.compteur - Integer.parseInt(adhoc.Adhoc.S[idv][2]);
                            //--------------------------------------------------
                            S[idv][2] = "" + TV[idv].posx;
                            S[idv][3] = "" + TV[idv].posy;
                            S[idv][4] = "" + TV[idv].etat;
                        }
//----------------------------------------------------------------------------
                        
                        
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }
        };
        TV[idv].th.start();
    }

    public static void affichage_param_fitness(int nbn, DefaultTableModel Ttrp, DefaultTableModel Tvr, DefaultTableModel Tlv) //saliha
    {
        /*
         //-------------------------------
         Ttrp.setColumnCount(0);Ttrp.addColumn("N° Veh");
         for(int i=0;i<nbn;i++) Ttrp.addColumn("V"+(i+1));
         Ttrp.setRowCount(nbn);
         //-------------------------------
         Tvr.setColumnCount(0);Tvr.addColumn("N° Veh");
         for(int i=0;i<nbn;i++) Tvr.addColumn("V"+(i+1));
         Tvr.setRowCount(nbn);         
         //-------------------------------
         Tlv.setColumnCount(0);Tlv.addColumn("N° Veh");
         for(int i=0;i<nbn;i++) Tlv.addColumn("V"+(i+1));
         Tlv.setRowCount(nbn);
         
 
         for(int i=0;i<Ttrp.getRowCount();i++)
         {
         Ttrp.setValueAt("V"+(i+1), i, 0); 
         Tvr.setValueAt("V"+(i+1), i, 0); 
         Tlv.setValueAt("V"+(i+1), i, 0);
         for(int j=1;j<Ttrp.getColumnCount();j++)
         {
         Ttrp.setValueAt(" ", i, j); 
         Tvr.setValueAt(" ", i, j); 
         Tlv.setValueAt(" ", i, j); 
         }
         }
         */
    }
//------------------------------------------------------------------------------

    public static int NbrVoisinFormation(int nd, int nbn) //saliha
    {
        int n = 0;//adhoc.GlobalParameters.DIST
        //System.out.println("nd : "+nd+"   nbn : "+nbn); 
        for (int j = 0; j </*adhoc.GlobalParameters.DIST.length*/ nbn; j++) //if(j<nbn/*adhoc.GlobalParameters.DIST.length*/)
        //if(adhoc.GlobalParameters.DIST.length<nbn/*adhoc.GlobalParameters.DIST.length*/)    
        {
            if (adhoc.GlobalParameters.DISTold[nd][j] > -1) {
                n++;
            }
        }
        //System.out.println("Nbre de voisin trouvé : "+n+" du noeud "+nd+"/"+adhoc.GlobalParameters.DIST.length); 
        return n;
    }
//------------------------------------------------------------------------------

    public static int NbrVoisin(int nd, int nbn) //saliha
    {
        int n = 0;//adhoc.GlobalParameters.DIST
        //System.out.println("nd : "+nd+"   nbn : "+nbn); 
        for (int j = 0; j </*adhoc.GlobalParameters.DIST.length*/ nbn; j++) {
            if (nd < adhoc.GlobalParameters.DIST.length) {
                if (j < adhoc.GlobalParameters.DIST[nd].length) {
                    if (adhoc.GlobalParameters.DIST[nd][j] > -1) {
                        n++;
                    }
                }
            }
        }

        //System.out.println("Nbre de voisin trouvé : "+n+" du noeud "+nd+"/"+adhoc.GlobalParameters.DIST.length); 
        return n;
    }
  //----------------------------------------------------------------------------
    //-----------------------------------------------------------------------------
    //--------------------------DecouvertVoisinage----------------------------------

    public static void DecouvertVoisinage(int idn, int portee, int vact[]) {
        TV[idn].nbvoisin = 0;
        TV[idn].VS.removeAllElements();

        for (int i = 0; i < vact.length; i++) {
            if (idn != vact[i]) {
                if (distance_euclidienne(idn, vact[i]) <= portee) {
                    TV[idn].nbvoisin++;
                    TV[idn].VS.addElement("" + vact[i]);
                }
            }
        }
    }

    //-----------------------------------------------------------------------------

    public static boolean VerifVoisin(int nd, int mbr) {
        boolean b = false;
        for (int i = 0; i < TV[nd].OVS.getSize(); i++) {
            if (TV[nd].OVS.getElementAt(i).equals("" + mbr)) {
                b = true;
            }
        }
        return b;
    }

    //-----------------------------------------------------------------------------

    public static int GetPositionElement(int elt, int vact[]) 
    {
        int pos = -1;
        for (int i = 0; i < vact.length; i++) {
            if (vact[i] == elt) {
                pos = i;
            }
        }
        return pos;

    }
//==============================================================================

    public static int GetPositionElementTableau(int elt, int vect[][], int col) {
        int pos = 0;
        for (int i = 0; i < vect.length; i++) {
            if (vect[i][col] == elt) {
                pos = i;
            }
        }
        return pos;
    }
//==============================================================================  

    class Packet {

        int idNode;
        int Xposition;
        int Yposition;
        double Velocity;
        int lane;
        int direction;
        int ConnectivityDegree;
        double TimeStamp;
        int avi;
    };

    //==========================================================================        
            
   }
   
}
