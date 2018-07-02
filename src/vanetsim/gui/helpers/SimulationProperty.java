package vanetsim.gui.helpers;

public class SimulationProperty {

    private final String propertyKey_;

    private final double startValue_;

    private final double stepValue_;

    private final int stepAmount_;

    public SimulationProperty(String propertyKey, double startValue, double stepValue, int stepAmount) {
        propertyKey_ = propertyKey;
        startValue_ = startValue;
        stepValue_ = stepValue;
        stepAmount_ = stepAmount;
    }

    public String getPropertyKey_() {
        return propertyKey_;
    }

    public double getStartValue_() {
        return startValue_;
    }

    public double getStepValue_() {
        return stepValue_;
    }

    public int getStepAmount_() {
        return stepAmount_;
    }

    public String toString() 
    {
        return "Key:" + propertyKey_ + " :Start:" + startValue_ + " :Step:" + stepValue_ + " :Amount:" + stepAmount_;
    }
}
