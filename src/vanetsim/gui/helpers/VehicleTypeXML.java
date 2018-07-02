package vanetsim.gui.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
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
import vanetsim.localization.Messages;

public class VehicleTypeXML {

    private String xmlPath_;

    //private String defaultPath_ = "C:\\2015 ADHOC\\VanetSimulator\\VS\\vehicleTypes.xml";
    private String defaultPath_ = "//Users//saliha-benkerdagh//Desktop//VanetSimulator//VS//vehicleTypes.xml";

    public ArrayList<VehicleType> types_;

    public VehicleTypeXML(String path) {
        if (path == null)
            xmlPath_ = defaultPath_;
        else
            xmlPath_ = path;
    }

    public ArrayList<VehicleType> getVehicleTypes() {
        types_ = new ArrayList<VehicleType>();
        try {
            SMInputCursor childCrsr, typCrsr;
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            InputStream filestream;
            
            filestream = new FileInputStream(xmlPath_);
            XMLStreamReader sr = factory.createXMLStreamReader(filestream);
            SMInputCursor rootCrsr = SMInputFactory.rootElementCursor(sr);
            rootCrsr.getNext();
            
            
            if (rootCrsr.getLocalName().toLowerCase().equals("vehicletypes")) 
            {
                childCrsr = rootCrsr.childElementCursor();
                while (childCrsr.getNext() != null) {
                    if (childCrsr.getLocalName().toLowerCase().equals("vehicle")) {
                        typCrsr = childCrsr.childElementCursor();
                        String tmpName = "";
                        int vehicleLength = 0, tmpMaxSpeed = 0, tmpMinSpeed = 0, tmpMaxCommDist = 0, tmpMinCommDist = 0, tmpMaxWaittime = 0, tmpMinWaittime = 0, tmpMaxBraking_rate = 0, tmpMinBraking_rate = 0, tmpMaxAcceleration_rate = 0, tmpMinAcceleration_rate = 0, tmpMinTimeDistance = 0, tmpMaxTimeDistance = 0, tmpMinPoliteness = 0, tmpMaxPoliteness = 0, tmpVehiclesDeviatingMaxSpeed = 0, tmpDeviationFromSpeedLimit = 0, color = 0;
                        boolean wifi = false, emergencyVehicle = false;
                        while (typCrsr.getNext() != null) {
                            
                            if (typCrsr.getLocalName().toLowerCase().equals("name")) {
                                tmpName = typCrsr.getElemStringValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("vehiclelength")) {
                                vehicleLength = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxspeed")) {
                                tmpMaxSpeed = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("minspeed")) {
                                tmpMinSpeed = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxcommdist")) {
                                tmpMaxCommDist = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("mincommdist")) {
                                tmpMinCommDist = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxbraking_rate")) {
                                tmpMaxBraking_rate = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("minbraking_rate")) {
                                tmpMinBraking_rate = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxacceleration_rate")) {
                                tmpMaxAcceleration_rate = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("minacceleration_rate")) {
                                tmpMinAcceleration_rate = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("mintimedistance")) {
                                tmpMinTimeDistance = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxtimedistance")) {
                                tmpMaxTimeDistance = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("minpoliteness")) {
                                tmpMinPoliteness = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxpoliteness")) {
                                tmpMaxPoliteness = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("vehiclesdeviatingmaxspeed")) {
                                tmpVehiclesDeviatingMaxSpeed = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("deviationfromspeedlimit")) {
                                tmpDeviationFromSpeedLimit = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("maxwaittime")) {
                                tmpMaxWaittime = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("minwaittime")) {
                                tmpMinWaittime = typCrsr.getElemIntValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("wifi")) {
                                wifi = typCrsr.getElemBooleanValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("emergencyvehicle")) {
                                emergencyVehicle = typCrsr.getElemBooleanValue();
                            } else if (typCrsr.getLocalName().toLowerCase().equals("color")) {
                                color = typCrsr.getElemIntValue();
                            }
                        }
                        types_.add(new VehicleType(tmpName, vehicleLength, tmpMaxSpeed, tmpMinSpeed, tmpMaxCommDist, tmpMinCommDist, tmpMaxBraking_rate, tmpMinBraking_rate, tmpMaxAcceleration_rate, tmpMinAcceleration_rate, tmpMinTimeDistance, tmpMaxTimeDistance, tmpMinPoliteness, tmpMaxPoliteness, tmpVehiclesDeviatingMaxSpeed, tmpDeviationFromSpeedLimit, tmpMaxWaittime, tmpMinWaittime, wifi, emergencyVehicle, color));
                    }
                }
            }
            
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("ErrorLog.loadType"), 6, getClass().getName(), "load", e);
            e.printStackTrace();
        }
        return types_;
    }

    public void saveVehicleTypes(ArrayList<VehicleType> tmpList) {
        try {
            File file = new File(xmlPath_);
            OutputStream filestream = new FileOutputStream(file);
            XMLStreamWriter xw = XMLOutputFactory.newInstance().createXMLStreamWriter(filestream);
            SMOutputDocument doc = SMOutputFactory.createOutputDocument(xw);
            doc.setIndentation("\n\t\t\t\t\t\t\t\t", 2, 1);
            ;
            doc.addComment("Generated on " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
            SMOutputElement root = doc.addElement("VehicleTypes");
            for (VehicleType type : tmpList) {
                SMOutputElement vehicle = root.addElement("Vehicle");
                vehicle.addElement("Name").addCharacters(type.getName());
                vehicle.addElement("VehicleLength").addValue(type.getVehicleLength());
                vehicle.addElement("MinSpeed").addValue(type.getMinSpeed());
                vehicle.addElement("MaxSpeed").addValue(type.getMaxSpeed());
                vehicle.addElement("MinCommDist").addValue(type.getMinCommDist());
                vehicle.addElement("MaxCommDist").addValue(type.getMaxCommDist());
                vehicle.addElement("MinBraking_Rate").addValue(type.getMinBrakingRate());
                vehicle.addElement("MaxBraking_Rate").addValue(type.getMaxBrakingRate());
                vehicle.addElement("MinAcceleration_Rate").addValue(type.getMinAccelerationRate());
                vehicle.addElement("MaxAcceleration_Rate").addValue(type.getMaxAccelerationRate());
                vehicle.addElement("MinTimeDistance").addValue(type.getMinTimeDistance());
                vehicle.addElement("MaxTimeDistance").addValue(type.getMaxTimeDistance());
                vehicle.addElement("MinPoliteness").addValue(type.getMinPoliteness());
                vehicle.addElement("MaxPoliteness").addValue(type.getMaxPoliteness());
                vehicle.addElement("vehiclesDeviatingMaxSpeed").addValue(type.getVehiclesDeviatingMaxSpeed_());
                vehicle.addElement("deviationFromSpeedLimit").addValue(type.getDeviationFromSpeedLimit_());
                vehicle.addElement("MinWaitTime").addValue(type.getMinWaittime());
                vehicle.addElement("MaxWaitTime").addValue(type.getMaxWaittime());
                vehicle.addElement("Wifi").addValue(type.isWifi());
                vehicle.addElement("EmergencyVehicle").addValue(type.isEmergencyVehicle());
                vehicle.addElement("Color").addValue(type.getColor());
            }
            doc.closeRoot();
            xw.close();
            filestream.close();
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("ErrorLog.saveType"), 6, getClass().getName(), "save", e);
        }
    }

    public String getDefaultPath() {
        return defaultPath_;
    }
}
