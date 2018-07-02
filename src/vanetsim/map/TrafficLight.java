package vanetsim.map;

public class TrafficLight {

    private static final double[] DEFAULT_SWITCH_INTERVALS = new double[] { 5000, 1000, 5000 };

    private static final double JUNCTION_FREE_TIME = 2000;

    private double redPhaseLength_;

    private double yellowPhaseLength_;

    private double greenPhaseLength_;

    private int state = 0;

    private Street[] streets_;

    private boolean[] streetIsPriority_;

    private double timer_;

    private Junction junction_;

    private boolean switcher = true;

    public TrafficLight(Junction junction) {
        junction_ = junction;
        junction_.getNode().setTrafficLight_(this);
        redPhaseLength_ = DEFAULT_SWITCH_INTERVALS[0];
        yellowPhaseLength_ = DEFAULT_SWITCH_INTERVALS[1];
        greenPhaseLength_ = DEFAULT_SWITCH_INTERVALS[2];
        initialiseTrafficLight();
    }

    public TrafficLight(double redPhaseLength, double yellowPhaseLength, double greenPhaseLength, Junction junction) {
        junction_ = junction;
        junction_.getNode().setTrafficLight_(this);
        redPhaseLength_ = redPhaseLength;
        yellowPhaseLength_ = yellowPhaseLength;
        greenPhaseLength_ = greenPhaseLength;
        initialiseTrafficLight();
    }

    private void initialiseTrafficLight() {
        streetIsPriority_ = new boolean[junction_.getNode().getCrossingStreetsCount()];
        if (!junction_.getNode().hasNonDefaultSettings()) {
            junction_.getNode().setStreetHasException_(new int[junction_.getNode().getCrossingStreetsCount()]);
            for (int m = 0; m < junction_.getNode().getStreetHasException_().length; m++) junction_.getNode().getStreetHasException_()[m] = 1;
        }
        streets_ = junction_.getNode().getCrossingStreets();
        Street[] tmpPriorityStreets = junction_.getPriorityStreets();
        boolean isOneway = false;
        for (int i = 0; i < streets_.length; i++) {
            streetIsPriority_[i] = false;
            for (int j = 0; j < tmpPriorityStreets.length; j++) {
                if (streets_[i] == tmpPriorityStreets[j])
                    streetIsPriority_[i] = true;
            }
            if (streets_[i].isOneway() && streets_[i].getStartNode() == junction_.getNode())
                isOneway = true;
            if (!isOneway) {
                if (streetIsPriority_[i]) {
                    if (junction_.getNode() == streets_[i].getStartNode()) {
                        streets_[i].setStartNodeTrafficLightState(0);
                        if (junction_.getNode().hasNonDefaultSettings()) {
                            streets_[i].setStartNodeTrafficLightState(junction_.getNode().getStreetHasException_()[i]);
                        }
                        streets_[i].setPriorityOnStartNode(true);
                    } else {
                        streets_[i].setEndNodeTrafficLightState(0);
                        if (junction_.getNode().hasNonDefaultSettings()) {
                            streets_[i].setEndNodeTrafficLightState(junction_.getNode().getStreetHasException_()[i]);
                        }
                        streets_[i].setPriorityOnEndNode(true);
                    }
                } else {
                    if (junction_.getNode() == streets_[i].getStartNode()) {
                        streets_[i].setStartNodeTrafficLightState(4);
                        if (junction_.getNode().hasNonDefaultSettings()) {
                            streets_[i].setStartNodeTrafficLightState(junction_.getNode().getStreetHasException_()[i]);
                        }
                    } else {
                        streets_[i].setEndNodeTrafficLightState(4);
                        if (junction_.getNode().hasNonDefaultSettings()) {
                            streets_[i].setEndNodeTrafficLightState(junction_.getNode().getStreetHasException_()[i]);
                        }
                    }
                }
            }
            isOneway = false;
            calculateTrafficLightPosition(streets_[i]);
        }
        timer_ = greenPhaseLength_;
        junction_.getNode().setHasTrafficSignal_(true);
    }

    public void changePhases(int timePerStep) {
        if (timer_ < timePerStep) {
            state = (state + 1) % 4;
            if (state == 1)
                timer_ = yellowPhaseLength_;
            else if (state == 2)
                timer_ = JUNCTION_FREE_TIME;
            else if (state == 0 && switcher)
                timer_ = greenPhaseLength_;
            else if (state == 0 && !switcher)
                timer_ = redPhaseLength_;
            else if (state == 3)
                timer_ = yellowPhaseLength_;
            switcher = !switcher;
            for (int i = 0; i < streets_.length; i++) {
                if (streets_[i].getStartNode() == junction_.getNode() && streets_[i].getStartNodeTrafficLightState() != -1) {
                    streets_[i].updateStartNodeTrafficLightState();
                } else if (streets_[i].getEndNode() == junction_.getNode() && streets_[i].getEndNodeTrafficLightState() != -1) {
                    streets_[i].updateEndNodeTrafficLightState();
                }
            }
        } else
            timer_ = timer_ - timePerStep;
    }

    public void calculateTrafficLightPosition(Street tmpStreet) {
        double junctionX = junction_.getNode().getX();
        double junctionY = junction_.getNode().getY();
        double nodeX;
        double nodeY;
        if (junction_.getNode().equals(tmpStreet.getStartNode())) {
            nodeX = tmpStreet.getEndNode().getX();
            nodeY = tmpStreet.getEndNode().getY();
        } else {
            nodeX = tmpStreet.getStartNode().getX();
            nodeY = tmpStreet.getStartNode().getY();
        }
        double m = (nodeY - junctionY) / (nodeX - junctionX);
        double n = nodeY - m * nodeX;
        double a = 1 + (m * m);
        double b = (2 * m * n) - (2 * m * junctionY) - (2 * junctionX);
        double c = (n * n) - (2 * n * junctionY) + (junctionY * junctionY) - (700 * 700) + (junctionX * junctionX);
        if (junction_.getNode().equals(tmpStreet.getStartNode())) {
            if (nodeX < junctionX) {
                tmpStreet.setTrafficLightStartX_((int) Math.round((-b - Math.sqrt((b * b) - (4 * a * c))) / (2 * a)));
                tmpStreet.setTrafficLightStartY_((int) Math.round((m * tmpStreet.getTrafficLightStartX_()) + n));
            } else {
                tmpStreet.setTrafficLightStartX_((int) Math.round((-b + Math.sqrt((b * b) - (4 * a * c))) / (2 * a)));
                tmpStreet.setTrafficLightStartY_((int) Math.round((m * tmpStreet.getTrafficLightStartX_()) + n));
            }
        } else {
            if (nodeX < junctionX) {
                tmpStreet.setTrafficLightEndX_((int) Math.round((-b - Math.sqrt((b * b) - (4 * a * c))) / (2 * a)));
                tmpStreet.setTrafficLightEndY_((int) Math.round((m * tmpStreet.getTrafficLightEndX_()) + n));
            } else {
                tmpStreet.setTrafficLightEndX_((int) Math.round((-b + Math.sqrt((b * b) - (4 * a * c))) / (2 * a)));
                tmpStreet.setTrafficLightEndY_((int) Math.round((m * tmpStreet.getTrafficLightEndX_()) + n));
            }
        }
    }

    public double getGreenPhaseLength() {
        return greenPhaseLength_;
    }

    public void setGreenPhaseLength(double greenPhaseLength) {
        this.greenPhaseLength_ = greenPhaseLength;
    }

    public void setYellowPhaseLength(double yellowPhaseLength) {
        this.yellowPhaseLength_ = yellowPhaseLength;
    }

    public double getYellowPhaseLength() {
        return yellowPhaseLength_;
    }

    public void setRedPhaseLength(double redPhaseLength) {
        this.redPhaseLength_ = redPhaseLength;
    }

    public double getRedPhaseLength() {
        return redPhaseLength_;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public Street[] getStreets_() {
        return streets_;
    }

    public void setStreets_(Street[] streets_) {
        this.streets_ = streets_;
    }
}
