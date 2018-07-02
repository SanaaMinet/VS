package vanetsim.gui.helpers;

public class VehicleType {

    private final String name_;

    private int vehicleLength_;

    private int maxSpeed_;

    private int minSpeed_;

    private int maxCommDist_;

    private int minCommDist_;

    private int maxBrakingRate_;

    private int minBrakingRate_;

    private int maxAccelerationRate_;

    private int minAccelerationRate_;

    private int minTimeDistance_;

    private int maxTimeDistance_;

    private int minPoliteness_;

    private int maxPoliteness_;

    private int vehiclesDeviatingMaxSpeed_;

    private int deviationFromSpeedLimit_;

    private int maxWaittime_;

    private int minWaittime_;

    private boolean wifi_;

    private boolean emergencyVehicle_;

    private int color_;

    public VehicleType(String name, int vehicleLength, int maxSpeed, int minSpeed, int maxCommDist, int minCommDist, int maxBrakingRate, int minBrakingRate, int maxAccelerationRate, int minAccelerationRate, int minTimeDistance, int maxTimeDistance, int minPoliteness, int maxPoliteness, int vehiclesDeviatingMaxSpeed, int deviationFromSpeedLimit, int maxWaittime, int minWaittime, boolean wifi, boolean emergencyVehicle, int color) {
        name_ = name;
        vehicleLength_ = vehicleLength;
        maxSpeed_ = maxSpeed;
        minSpeed_ = minSpeed;
        maxCommDist_ = maxCommDist;
        minCommDist_ = minCommDist;
        maxWaittime_ = maxWaittime;
        minWaittime_ = minWaittime;
        maxBrakingRate_ = maxBrakingRate;
        minBrakingRate_ = minBrakingRate;
        maxAccelerationRate_ = maxAccelerationRate;
        minAccelerationRate_ = minAccelerationRate;
        minTimeDistance_ = minTimeDistance;
        maxTimeDistance_ = maxTimeDistance;
        minPoliteness_ = minPoliteness;
        maxPoliteness_ = maxPoliteness;
        vehiclesDeviatingMaxSpeed_ = vehiclesDeviatingMaxSpeed;
        deviationFromSpeedLimit_ = deviationFromSpeedLimit;
        wifi_ = wifi;
        emergencyVehicle_ = emergencyVehicle;
        color_ = color;
    }

    public String getName() {
        return name_;
    }

    public int getMaxSpeed() {
        return maxSpeed_;
    }

    public void setMaxSpeed(int maxSpeed) {
        maxSpeed_ = maxSpeed;
    }

    public int getMinSpeed() {
        return minSpeed_;
    }

    public void setMinSpeed(int minSpeed) {
        minSpeed_ = minSpeed;
    }

    public int getMaxCommDist() {
        return maxCommDist_;
    }

    public void setMaxCommDist(int maxCommDist) {
        maxCommDist_ = maxCommDist;
    }

    public int getMinCommDist() {
        return minCommDist_;
    }

    public void setMinCommDist(int minCommDist) {
        minCommDist_ = minCommDist;
    }

    public int getMaxWaittime() {
        return maxWaittime_;
    }

    public void setMaxWaittime(int maxWaittime) {
        maxWaittime_ = maxWaittime;
    }

    public int getMinWaittime() {
        return minWaittime_;
    }

    public void setMinWaittime(int minWaittime) {
        minWaittime_ = minWaittime;
    }

    public int getColor() {
        return color_;
    }

    public void setColor(int color) {
        color_ = color;
    }

    public int getMaxBrakingRate() {
        return maxBrakingRate_;
    }

    public void setMaxBrakingRate(int maxBrakingRate) {
        maxBrakingRate_ = maxBrakingRate;
    }

    public int getMinBrakingRate() {
        return minBrakingRate_;
    }

    public void setMinBrakingRate(int minBrakingRate) {
        minBrakingRate_ = minBrakingRate;
    }

    public int getMaxAccelerationRate() {
        return maxAccelerationRate_;
    }

    public void setMaxAccelerationRate(int maxAccelerationRate) {
        maxAccelerationRate_ = maxAccelerationRate;
    }

    public int getMinAccelerationRate() {
        return minAccelerationRate_;
    }

    public void setMinAccelerationRate(int minAccelerationRate) {
        minAccelerationRate_ = minAccelerationRate;
    }

    public boolean isWifi() {
        return wifi_;
    }

    public void setWifi(boolean wifi) {
        wifi_ = wifi;
    }

    public boolean isEmergencyVehicle() {
        return emergencyVehicle_;
    }

    public void setEmergencyVehicle(boolean emergencyVehicle) {
        emergencyVehicle_ = emergencyVehicle;
    }

    public int getVehicleLength() {
        return vehicleLength_;
    }

    public void setVehicleLength(int vehicleLength) {
        vehicleLength_ = vehicleLength;
    }

    public void setMinTimeDistance(int minTimeDistance_) {
        this.minTimeDistance_ = minTimeDistance_;
    }

    public int getMinTimeDistance() {
        return minTimeDistance_;
    }

    public void setMaxTimeDistance(int maxTimeDistance_) {
        this.maxTimeDistance_ = maxTimeDistance_;
    }

    public int getMaxTimeDistance() {
        return maxTimeDistance_;
    }

    public void setMinPoliteness(int minPoliteness_) {
        this.minPoliteness_ = minPoliteness_;
    }

    public int getMinPoliteness() {
        return minPoliteness_;
    }

    public void setMaxPoliteness(int maxPoliteness_) {
        this.maxPoliteness_ = maxPoliteness_;
    }

    public int getMaxPoliteness() {
        return maxPoliteness_;
    }

    public String toString() {
        return (String) name_;
    }

    public int getVehiclesDeviatingMaxSpeed_() {
        return vehiclesDeviatingMaxSpeed_;
    }

    public void setVehiclesDeviatingMaxSpeed_(int vehiclesDeviatingMaxSpeed_) {
        this.vehiclesDeviatingMaxSpeed_ = vehiclesDeviatingMaxSpeed_;
    }

    public int getDeviationFromSpeedLimit_() {
        return deviationFromSpeedLimit_;
    }

    public void setDeviationFromSpeedLimit_(int deviationFromSpeedLimit_) {
        this.deviationFromSpeedLimit_ = deviationFromSpeedLimit_;
    }
}
