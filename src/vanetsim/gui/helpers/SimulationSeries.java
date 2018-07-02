package vanetsim.gui.helpers;

import java.util.ArrayList;

public class SimulationSeries {

    private final String name_;

    private ArrayList<VehicleSet> vehicleSetList_ = new ArrayList<VehicleSet>();

    private ArrayList<SimulationProperty> propertyList_ = new ArrayList<SimulationProperty>();

    public SimulationSeries(String name) {
        name_ = name;
    }

    public void removeProperty(String key) {
        int index = -1;
        for (int i = 0; i < propertyList_.size(); i++) {
            if (propertyList_.get(i).getPropertyKey_().equals(key)) {
                index = i;
                break;
            }
        }
        if (index != -1)
            propertyList_.remove(index);
    }

    public void removeVehicleSet(String name) 
    {
        int index = -1;
        for (int i = 0; i < vehicleSetList_.size(); i++) {
            if (vehicleSetList_.get(i).getName_().equals(name)) {
                index = i;
                break;
            }
        }
        if (index != -1)
            vehicleSetList_.remove(index);
    }

    public ArrayList<VehicleSet> getVehicleSetList_() {
        return vehicleSetList_;
    }

    public void setVehicleSetList_(ArrayList<VehicleSet> vehicleSetList_) {
        this.vehicleSetList_ = vehicleSetList_;
    }

    public ArrayList<SimulationProperty> getPropertyList_() {
        return propertyList_;
    }

    public void setPropertyList_(ArrayList<SimulationProperty> propertyList_) {
        this.propertyList_ = propertyList_;
    }

    public String getName_() {
        return name_;
    }

    public String toString() {
        return name_;
    }
}
