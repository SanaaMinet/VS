package vanetsim.scenario;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
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
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.EventLogWriter;
import vanetsim.gui.helpers.IDSLogWriter;
import vanetsim.gui.helpers.MouseClickManager;
import vanetsim.gui.helpers.PrivacyLogWriter;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.routing.WayPoint;
import vanetsim.scenario.events.Event;
import vanetsim.scenario.events.EventList;
import vanetsim.scenario.events.EventSpot;
import vanetsim.scenario.events.EventSpotList;
import vanetsim.simulation.WorkerThread;

public final class Scenario {

    private static final Scenario INSTANCE = new Scenario();

    private boolean ready_ = true;

    private String scenarioName = "";

    private Scenario() {
    }

    public static Scenario getInstance() {
        return INSTANCE;
    }

    public void initNewScenario() {
        if (ready_ == true) {
            ready_ = false;
            if (!Renderer.getInstance().isConsoleStart())
                VanetSimStart.getSimulationMaster().stopThread();
            if (!Renderer.getInstance().isConsoleStart())
                VanetSimStart.getMainControlPanel().getSimulatePanel().setSimulationStop();
            KnownVehiclesList.setTimePassed(0);
           
        
            Renderer.getInstance().setTimePassed(0);
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().setShowVehicles(false);
            
            Renderer.getInstance().setShowMixZones(false);
            Renderer.getInstance().setAttackedVehicle(null);
            Renderer.getInstance().setAttackerVehicle(null);
            Renderer.getInstance().setShowAttackers(false);
            Vehicle.setMaximumCommunicationDistance(0);
            Vehicle.resetGlobalRandomGenerator();
            Vehicle.setMinTravelTimeForRecycling(60000);
    
            Vehicle.setAttackedVehicleID_(0);
            if (!Renderer.getInstance().isConsoleStart())
                MouseClickManager.getInstance().cleanMarkings();
            Region[][] Regions = Map.getInstance().getRegions();
            int Region_max_x = Map.getInstance().getRegionCountX();
            int Region_max_y = Map.getInstance().getRegionCountY();
            int i, j;
            for (i = 0; i < Region_max_x; ++i) {
                for (j = 0; j < Region_max_y; ++j) {
                    Regions[i][j].cleanVehicles();
                }
            }
            EventList.getInstance().clearEvents();
            if (!Renderer.getInstance().isConsoleStart())
                VanetSimStart.getMainControlPanel().getEditPanel().getEditEventPanel().updateList();
        }
    }

    public void load(File file, boolean zip) {
        scenarioName = file.getName();
        Map.getInstance().clearMixZones();
        
        try {
            if (!Renderer.getInstance().isConsoleStart()) VanetSimStart.setProgressBar(true);
            
            
            initNewScenario();
            String type, penaltyType, fakeMessageType, eventSpotType;
            int x=0, y=0, frequency, radius, time, maxSpeed, numOfVehicle=0,vehicleID=-1, arrivalTime, vehicleLength=0, currentLane=-1, startSpeed=0, maxCommDistance, direction, lanes, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, color, mixX, mixY, mixRadius, wifiX, wifiY, wifiRadius;
            boolean tmpBoolean, wifi, emergencyVehicle, tmpAttacker, tmpAttacked, isEncrypted, mixHasRSU, isFake, fakingMessages;
            long seed;
            ArrayDeque<WayPoint> destinations;
            WayPoint tmpWayPoint;
            Vehicle tmpVehicle;
            Node[] tmpNodes;
            Node tmpNode;
            
            
            SMInputCursor childCrsr, vehicleCrsr, vehiclesCrsr, mixNodeCrsr, mixNodesCrsr, settingsCrsr, eventCrsr, eventsCrsr, eventSpotCrsr, eventSpotsCrsr, destinationsCrsr, waypointCrsr, rsuCrsr, rsusCrsr, aRsuCrsr, aRsusCrsr;
            XMLInputFactory factory = XMLInputFactory.newInstance();
         //   JOptionPane.showConfirmDialog(null, "1");
            ErrorLog.log(Messages.getString("Scenario.loadingScenario") + file.getName(), 3, getClass().getName(), "load", null);
        //    JOptionPane.showConfirmDialog(null, "File : " + "Scenario.loadingScenario" + file.getName());
            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            Vehicle.setEncryptedBeaconsInMix_(false);
         //   JOptionPane.showConfirmDialog(null, "2");
            InputStream filestream;
            if (zip) {
                filestream = new ZipInputStream(new FileInputStream(file));
                ((ZipInputStream) filestream).getNextEntry();
            } else
                filestream = new FileInputStream(file);
            XMLStreamReader sr = factory.createXMLStreamReader(filestream);
            SMInputCursor rootCrsr = SMInputFactory.rootElementCursor(sr);
            rootCrsr.getNext();
            if (rootCrsr.getLocalName().toLowerCase().equals("scenario")) {
                childCrsr = rootCrsr.childElementCursor();
                while (childCrsr.getNext() != null) {
                    if (childCrsr.getLocalName().toLowerCase().equals("settings")) {
                        settingsCrsr = childCrsr.childElementCursor();
                        while (settingsCrsr.getNext() != null) {
                            if (settingsCrsr.getLocalName().toLowerCase().equals("communicationenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                if (!Renderer.getInstance().isConsoleStart())
                                    VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setCommunication(tmpBoolean);
                                Vehicle.setCommunicationEnabled(tmpBoolean);
                               
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconsenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                if (!Renderer.getInstance().isConsoleStart())
                                    VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setBeacons(tmpBoolean);
                                Vehicle.setBeaconsEnabled(tmpBoolean);
                               
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fallbackinmixzonesenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                if (!Renderer.getInstance().isConsoleStart())
                                    //VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setMixZonesFallbackEnabled(tmpBoolean);
                                Vehicle.setMixZonesFallbackEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fallbackinmixzonesfloodingonly")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;

                                Vehicle.setMixZonesFallbackFloodingOnly(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("globalInfrastructureenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                if (!Renderer.getInstance().isConsoleStart())
                                    VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setGlobalInfrastructure(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mixzonesenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                
                                Vehicle.setMixZonesEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("vehiclerecyclingenabled")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                if (!Renderer.getInstance().isConsoleStart())
                                    VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setRecyclingEnabled(tmpBoolean);
                                Vehicle.setRecyclingEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("communicationinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setCommunicationInterval(tmp);
                                    Vehicle.setCommunicationInterval(tmp);
                                   
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconsinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setBeaconInterval(tmp);
                                    Vehicle.setBeaconInterval(tmp);
                                    
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mixzoneradius")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));

                                    Vehicle.setMixZoneRadius(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("autoaddmixzones")) {
                                if (settingsCrsr.collectDescendantText(false).equals("true"))
                                    tmpBoolean = true;
                                else
                                    tmpBoolean = false;
                                   Renderer.getInstance().setAutoAddMixZones(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("routingmode")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    if (tmp > 1)
                                        tmp = 1;
                                    else if (tmp < 0)
                                        tmp = 0;
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditSettingsPanel().setRoutingMode(tmp);
                                    Vehicle.setRoutingMode(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mintraveltimeforrecycling")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setMinTravelTimeForRecycling(tmp);
                                } catch (Exception e) {
                                }
                            }  else if (settingsCrsr.getLocalName().toLowerCase().equals("arsuloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogAttackerCheckBox_().setSelected(true);
                                        Vehicle.setAttackerDataLogged_(true);
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogAttackerCheckBox_().setSelected(false);
                                        Vehicle.setAttackerDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("arsuencryptedloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getEncryptedLogging_().setSelected(true);
                                        Vehicle.setAttackerEncryptedDataLogged_(true);
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getEncryptedLogging_().setSelected(false);
                                        Vehicle.setAttackerEncryptedDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("privacyloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogPrivacyCheckBox_().setSelected(true);
                                        Vehicle.setPrivacyDataLogged_(true);
                                        PrivacyLogWriter.setLogPath(System.getProperty("user.dir"));
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogPrivacyCheckBox_().setSelected(false);
                                        Vehicle.setPrivacyDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idslog")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
 
                                    if (!Renderer.getInstance().isConsoleStart() && !tmp.equals(""))
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogIDSPath_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idsloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogIDSCheckBox_().setSelected(true);
                                        
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogIDSCheckBox_().setSelected(false);
                                         
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("eventlog")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
 
                                    if (!Renderer.getInstance().isConsoleStart() && !tmp.equals(""))
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogEventPath_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("eventloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogEventCheckBox_().setSelected(true);
                                     
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditLogControlPanel_().getLogEventCheckBox_().setSelected(false);
                                     
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("showencryptedcomminmix")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                     
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodsenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setSilentPeriodsOn(true);
                                    } else {
                                        Vehicle.setSilentPeriodsOn(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodduration")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_OF_SILENT_PERIODS(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodfrequency")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_BETWEEN_SILENT_PERIODS(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setSlowOn(true);
                                    } else {
                                        Vehicle.setSlowOn(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowtimetochangepseudonym")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_TO_PSEUDONYM_CHANGE(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowspeedlimit")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setSLOW_SPEED_LIMIT(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idsactivated")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setIdsActivated(true);
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getActivateIDSCheckBox_().setSelected(true);
                                    } else {
                                        Vehicle.setIdsActivated(false);
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getActivateIDSCheckBox_().setSelected(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("advancedidsrules")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getActivateAdvancedIDSCheckBox_().setSelected(true);
                                         
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getActivateAdvancedIDSCheckBox_().setSelected(false);
                                         
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("activerules")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    
 
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconslogged")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownVehicle.setAmountOfSavedBeacons(tmp);
                                    if (!Renderer.getInstance().isConsoleStart())
                                        if (tmp > 0)
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getBeaconsLogged_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fakemessageinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setFakeMessagesInterval_(tmp);
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getFakeMessageInterval_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("pcnthreshold")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getPCNThreshold_().setValue(Math.sqrt(tmp));
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("pcnforwardthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                     
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getPCNFORWARDThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("evaforwardthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                     
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getEVAFORWARDThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("rhcnthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                     
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getRHCNThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("eeblthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                    
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getEEBLThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("evabeacontimethreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                     
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getEVABeaconTimeThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("evabeaconthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                    
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getEVABeaconThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("evamessagedelay")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setMaxEVAMessageDelay_(tmp);
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getEVAMessageDelay_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spamdetection")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getSpamDetectionCheckBox_().setSelected(true);
                                        KnownEventSource.setSpamCheck_(true);
                                         
                                    } else {
                                        if (!Renderer.getInstance().isConsoleStart())
                                            VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getSpamDetectionCheckBox_().setSelected(false);
                                        KnownEventSource.setSpamCheck_(false);
                                         
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spammessagethreshold")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownEventSource.setSpammingThreshold_(tmp);
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getSpamMessageAmountThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spamtimethreshold")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownEventSource.setSpammingTimeThreshold_(tmp);
                                    if (!Renderer.getInstance().isConsoleStart())
                                        VanetSimStart.getMainControlPanel().getEditPanel().getEditIDSControlPanel_().getSpamTimeThreshold_().setValue(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("trafficmodel")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    WorkerThread.setSimulationMode_(tmp);
                                    System.out.println("Here Pass");
                                } catch (Exception e) {
                                }
                            }else if (settingsCrsr.getLocalName().toLowerCase().equals("numberofgeneratedvehicles")) {
                                System.out.println("Here pass 2");
                                try {
                                    
                                    //int nbv = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    vanetsim.gui.controlpanels.ClusteringControlPanel.NbreDeVehicule = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    int NbVehLoaded =vanetsim.gui.controlpanels.ClusteringControlPanel.NbreDeVehicule;
                                    vanetsim.gui.controlpanels.ClusteringControlPanel.S.setRowCount(NbVehLoaded);//return here 27/05
                                    adhoc.Adhoc.S = new String[NbVehLoaded][11];
                                    vanetsim.scenario.Vehicle.TV = new vanetsim.scenario.Vehicle.Vehicule[NbVehLoaded];
                                     
                                    System.out.println("Nombre de véhicules collectés === "+vanetsim.gui.controlpanels.ClusteringControlPanel.NbreDeVehicule);
                                } catch (Exception e) {
                                }
                            }else if (settingsCrsr.getLocalName().toLowerCase().equals("lambdapoisson")) {
                                System.out.println("Here pass 3");
                                try {
                                    
                                    //int nbv = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    vanetsim.gui.controlpanels.ClusteringControlPanel.lambda = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                  
                                     
                                    System.out.println("lambda  === "+vanetsim.gui.controlpanels.ClusteringControlPanel.lambda);
                                } catch (Exception e) {
                                }
                            }else if (settingsCrsr.getLocalName().toLowerCase().equals("timesimulation")) {
                                System.out.println("Here pass 4");
                                try {
                                    
                                    //int nbv = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    vanetsim.gui.controlpanels.ClusteringControlPanel.tsim = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                  
                                     
                                    System.out.println("lambda  === "+vanetsim.gui.controlpanels.ClusteringControlPanel.tsim);
                                } catch (Exception e) {
                                }
                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("vehicles")) {
                        vehiclesCrsr = childCrsr.childElementCursor();
                        while (vehiclesCrsr.getNext() != null) {
                            if (vehiclesCrsr.getLocalName().toLowerCase().equals("vehicle")) {
                               // JOptionPane.showConfirmDialog(null, "Count : " + vehiclesCrsr.getElementCount());
                                maxCommDistance = 10000;
                                vehicleID = 0;
                                arrivalTime = 0;
                                vehicleLength = 2500;
                                maxSpeed = 10000;
                                wifi = true;
                                emergencyVehicle = false;
                                braking_rate = 100;
                                acceleration_rate = 200;
                                timeDistance = 100;
                                politeness = 50;
                                speedDeviation = 0;
                                color = 0;
                                destinations = new ArrayDeque<WayPoint>(1);
                                vehicleCrsr = vehiclesCrsr.childElementCursor();
                                tmpAttacker = false;
                                tmpAttacked = false;
                                fakingMessages = false;
                                fakeMessageType = "";
                                
                                while (vehicleCrsr.getNext() != null) 
                                {
                                    if (vehicleCrsr.getLocalName().toLowerCase().equals("numofvehicle")) 
                                    {
                                        try {
                                            
                                          //  tmpVehicle = new Vehicle(destinations, vehicleLength, maxSpeed, maxCommDistance, wifi, emergencyVehicle, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, new Color(color), fakingMessages, fakeMessageType, arrivalTime);
                                            numOfVehicle = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                            //System.out.println("Vehicle num === "+(numOfVehicle+1));
                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+numOfVehicle, numOfVehicle, 0);
                                            adhoc.Adhoc.S[numOfVehicle][0] = ""+numOfVehicle;
                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt("Inactif", numOfVehicle, 7);
                                            adhoc.Adhoc.S[numOfVehicle][7] = "Inactif";
                                             vanetsim.scenario.Vehicle.TV[numOfVehicle].etat ="Inactif";
                                            //999999
                                        } catch (Exception e) {
                                        }
                                    }
                                    else if (vehicleCrsr.getLocalName().toLowerCase().equals("vehicleid")) 
                                    {
                                        try {

                                          //  tmpVehicle = new Vehicle(destinations, vehicleLength, maxSpeed, maxCommDistance, wifi, emergencyVehicle, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, new Color(color), fakingMessages, fakeMessageType, arrivalTime);
                                            vehicleID = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+vehicleID, numOfVehicle, 1);
                                            adhoc.Adhoc.S[numOfVehicle][1] = ""+vehicleID;
                                            //999999
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("arrivaltime")) {
                                        try {
                                            arrivalTime = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+arrivalTime, numOfVehicle, 2);
                                            //System.out.println("TIME_PER_STEP == "+vanetsim.simulation.SimulationMaster.TIME_PER_STEP);
                                            adhoc.Adhoc.S[numOfVehicle][2] = ""+arrivalTime/vanetsim.simulation.SimulationMaster.TIME_PER_STEP;
                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+arrivalTime/vanetsim.simulation.SimulationMaster.TIME_PER_STEP, numOfVehicle, 2);
                                            
                                        } catch (Exception e) {
                                        }
                                    } else
                                    if (vehicleCrsr.getLocalName().toLowerCase().equals("vehiclelength")) {
                                        try {
                                            vehicleLength = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    }else
                                    if (vehicleCrsr.getLocalName().toLowerCase().equals("currentstartlane")) {
                                        try {
                                           currentLane = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    }else
                                    if (vehicleCrsr.getLocalName().toLowerCase().equals("startspeed")) {
                                        try {
                                            startSpeed = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("maxspeed")) {
                                        try {
                                            maxSpeed = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("maxcommdist")) {
                                        try {
                                            maxCommDistance = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("wifi")) {
                                        try {
                                            wifi = Boolean.parseBoolean(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("emergencyvehicle")) {
                                        try {
                                            emergencyVehicle = Boolean.parseBoolean(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("braking_rate")) {
                                        try {
                                            braking_rate = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("acceleration_rate")) {
                                        try {
                                            acceleration_rate = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("timedistance")) {
                                        try {
                                            timeDistance = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("politeness")) {
                                        try {
                                            politeness = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("speeddeviation")) {
                                        try {
                                            speedDeviation = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("color")) {
                                        try {
                                            color = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isattacker")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                tmpAttacker = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isattacked")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                tmpAttacked = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isfakingmessages")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                fakingMessages = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("fakingmessagetype")) {
                                        try {
                                            fakeMessageType = vehicleCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("destinations")) {
                                        destinationsCrsr = vehicleCrsr.childElementCursor();
                                        int levelX = 1, levelY = 1;
                                        while (destinationsCrsr.getNext() != null) {
                                            if (destinationsCrsr.getLocalName().toLowerCase().equals("waypoint")) {
                                                x = -1;
                                                y = -1;
                                                time = -1;
                                                waypointCrsr = destinationsCrsr.childElementCursor();
                                                while (waypointCrsr.getNext() != null) {
                                                    if (waypointCrsr.getLocalName().toLowerCase().equals("x")) {
                                                        try {
                                                            x = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                            if (levelX == 1)
                                                            {
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+x, numOfVehicle, 3);
                                                            adhoc.Adhoc.S[numOfVehicle][3] = ""+x;
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+x, numOfVehicle, 9);
                                                            adhoc.Adhoc.S[numOfVehicle][9] = ""+x;
                                                            //System.out.println("levelX == "+levelX+" x == "+x);
                                                            levelX++;
                                                            }
                                                            else
                                                            {
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+x, numOfVehicle, 5);
                                                            adhoc.Adhoc.S[numOfVehicle][5] = ""+x;
                                                                //System.out.println("levelX == "+levelX+" x == "+x);
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (waypointCrsr.getLocalName().toLowerCase().equals("y")) {
                                                        try {
                                                            y = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                            
                                                            if (levelY == 1)
                                                            {
                                                            //System.out.println("levelY == "+levelY+" y == "+y);
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+y, numOfVehicle, 4);
                                                            adhoc.Adhoc.S[numOfVehicle][4] = ""+y;
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+y, numOfVehicle, 10);
                                                            adhoc.Adhoc.S[numOfVehicle][10] = ""+y;
                                                            levelY++;
                                                            }
                                                            else
                                                            {
                                                            //System.out.println("levelY == "+levelY+" y == "+y);
                                                            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+y, numOfVehicle, 6);
                                                            adhoc.Adhoc.S[numOfVehicle][6] = ""+y;
                                                            
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (waypointCrsr.getLocalName().toLowerCase().equals("wait")) {
                                                        try {
                                                            time = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                        } catch (Exception e) {
                                                        }
                                                    } else
                                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileWayPoint") + waypointCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                                }
                                                try {
                                                    tmpWayPoint = new WayPoint(x, y, time);
                                                    destinations.add(tmpWayPoint);
                                                    //JOptionPane.showConfirmDialog(null, "tmpWayPoint Scénario X"+tmpWayPoint.getX());
                                                    //JOptionPane.showConfirmDialog(null, "tmpWayPoint Scénario Y"+tmpWayPoint.getY());
                                                } catch (ParseException e) {
                                                    ErrorLog.log(Messages.getString("Scenario.snappingFailed"), 5, getClass().getName(), "load", null);
                                                }
                                            }
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileVehicle") + vehicleCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
                                if (maxCommDistance != -1 && maxSpeed != -1 && destinations.size() > 1) 
                                {
                                    try {
                                        tmpVehicle = new Vehicle(destinations, vehicleLength, maxSpeed, maxCommDistance, wifi, emergencyVehicle, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, new Color(color), fakingMessages, fakeMessageType, arrivalTime);//vehicleID to be added as a param
                                // System.out.println(" Arrival time "+arrivalTime+" vs "+tmpVehicle.getArrivalTime());
                                        int idv = vanetsim.scenario.Vehicle.GetIndice(vanetsim.gui.controlpanels.ClusteringControlPanel.S,tmpVehicle.steadyID_ );
                                       /*if(vanetsim.scenario.Vehicle.TV!=null)           
                                        vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(idv,tmpVehicle.getX(), tmpVehicle.getY(), tmpVehicle.getCurLane(), tmpVehicle.getVehicleLength(), 200,(double)tmpVehicle.getCurSpeed()) ;
                                        */
                                            //System.out.println("vehicleID récupéré : "+vehicleID);
                                            tmpVehicle.steadyID_ = vehicleID;
                                            tmpVehicle.setArrivalTime(arrivalTime);
                                            //System.out.println("=====> steadyID récupéré : "+tmpVehicle.steadyID_ );
                                        
                                            /* for(int k=0;k<nbv;k++)
            {
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+(k+1), k, 0);
                adhoc.Adhoc.S[k][0] = ""+(k+1);
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+((int)(listofdatas[k][1]*100)), k, 2);
                adhoc.Adhoc.S[k][2] = ""+((int)(listofdatas[k][1]*100));
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt("Inactif", k, 7);
                adhoc.Adhoc.S[k][7] = "Inactif";
            }
            *///NaNo
                                        
                                        Map.getInstance().addVehicle(tmpVehicle);
                                        //System.out.println(" tmpVehicle.getID(): "+tmpVehicle.getID()+"  tmpVehicle.getVehicleID(): "+tmpVehicle.getVehicleID());//Ajout de ID de veh dans la méthode  Vehicle(...)
                                        if (tmpAttacker)
                                            Renderer.getInstance().setAttackerVehicle(tmpVehicle);
                                        if (tmpAttacked) {
                                            Renderer.getInstance().setAttackedVehicle(tmpVehicle);
                                            Vehicle.setAttackedVehicleID_(tmpVehicle.getID());
                                        }
                                        tmpAttacker = false;
                                        tmpAttacked = false;
                                    } catch (Exception e) {
                                    }
                                } else
                                    ErrorLog.log(Messages.getString("Scenario.notAllFieldsForVehicle"), 5, getClass().getName(), "load", null);
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileVehicles") + vehiclesCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                //System.out.println("    DEBUT CREATION DES VEHICULES     ");
                                if(vanetsim.scenario.Vehicle.TV!=null) 
                                {
                                vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(numOfVehicle,x, y, currentLane, vehicleLength, 200,(double)startSpeed) ;
                                //System.out.println("Creation du véhicule n°: "+z+" SteadyID : "+vehicle.steadyID_+" Temps d'arrivée : "+vehicle.getArrivalTime()+" PosX : "+vehicle.getX()+" PosY : "+vehicle.getY());
                                } 
                                 //System.out.println("    FIN CREATION DES VEHICULES     "); 
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("mixzones")) {
                        mixNodesCrsr = childCrsr.childElementCursor();
                        int maxMixRadius = 0;
                        while (mixNodesCrsr.getNext() != null) {
                            if (mixNodesCrsr.getLocalName().toLowerCase().equals("mixnode")) {
                                mixX = -1;
                                mixY = -1;
                                mixRadius = -1;
                                 
                                mixNodeCrsr = mixNodesCrsr.childElementCursor();
                                while (mixNodeCrsr.getNext() != null) {
                                    if (mixNodeCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            mixX = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (mixNodeCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            mixY = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (mixNodeCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            mixRadius = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else  
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileMixNode") + mixNodeCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
                                if (mixRadius > 0) {
                                    try {
                                        int Region_cnt_x = Map.getInstance().getRegionCountX();
                                        int Region_cnt_y = Map.getInstance().getRegionCountY();
                                        Region[][] Regions = Map.getInstance().getRegions();
                                        for (int i = 0; i < Region_cnt_x; ++i) {
                                            for (int j = 0; j < Region_cnt_y; ++j) {
                                                tmpNodes = Regions[i][j].getNodes();
                                                for (int k = 0; k < tmpNodes.length; k++) {
                                                    tmpNode = tmpNodes[k];
                                                    if (tmpNode.getX() == mixX && tmpNode.getY() == mixY) {
                                                         
                                                        Regions[i][j].addMixZone(tmpNode, mixRadius);
                                                        if (maxMixRadius < mixRadius)
                                                            maxMixRadius = mixRadius;
                                                        Vehicle.setEncryptedBeaconsInMix_(false);
 
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                } else
                                    ErrorLog.log(Messages.getString("Scenario.notAllFieldsForMixNode"), 5, getClass().getName(), "load", null);
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileMixNodes") + mixNodesCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                        }
                        Vehicle.setMaxMixZoneRadius(maxMixRadius);
                    } else if (childCrsr.getLocalName().toLowerCase().equals("rsus")) {
                        rsusCrsr = childCrsr.childElementCursor();
                        while (rsusCrsr.getNext() != null) {
                            if (rsusCrsr.getLocalName().toLowerCase().equals("rsu")) {
                                wifiX = -1;
                                wifiY = -1;
                                wifiRadius = -1;
                                isEncrypted = false;
                                rsuCrsr = rsusCrsr.childElementCursor();
                                while (rsuCrsr.getNext() != null) {
                                    if (rsuCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            wifiX = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            wifiY = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            wifiRadius = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("isencrypted")) {
                                        try {
                                            if (rsuCrsr.collectDescendantText(false).equals("true"))
                                                isEncrypted = true;
                                        } catch (Exception e) {
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileRSU") + rsuCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
 
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileRSUs") + rsusCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("arsus")) {
                        aRsusCrsr = childCrsr.childElementCursor();
                        while (aRsusCrsr.getNext() != null) {
                            if (aRsusCrsr.getLocalName().toLowerCase().equals("arsu")) {
                                wifiX = -1;
                                wifiY = -1;
                                wifiRadius = -1;
                                aRsuCrsr = aRsusCrsr.childElementCursor();
                                while (aRsuCrsr.getNext() != null) {
                                    if (aRsuCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            wifiX = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (aRsuCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            wifiY = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (aRsuCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            wifiRadius = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileARSU") + aRsuCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
 
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileARSUs") + aRsusCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("events")) {
                        eventsCrsr = childCrsr.childElementCursor();
                        while (eventsCrsr.getNext() != null) {
                            if (eventsCrsr.getLocalName().toLowerCase().equals("event")) {
                                time = -1;
                                x = -1;
                                y = -1;
                                direction = 0;
                                lanes = Integer.MAX_VALUE;
                                type = "";
                                penaltyType = "";
                                isFake = false;
                                eventCrsr = eventsCrsr.childElementCursor();
                                while (eventCrsr.getNext() != null) {
                                    if (eventCrsr.getLocalName().toLowerCase().equals("time")) {
                                        try {
                                            time = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("type")) {
                                        type = eventCrsr.collectDescendantText(false).toLowerCase();
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            x = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            y = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("direction")) {
                                        try {
                                            direction = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("lanes")) {
                                        try {
                                            lanes = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("penaltytype")) {
                                        try {
                                            penaltyType = eventCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("isfake")) {
                                        try {
                                            if (eventCrsr.collectDescendantText(false).equals("true"))
                                                isFake = true;
                                        } catch (Exception e) {
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileEvent") + childCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
 
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileEvents") + childCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("eventspots")) {
                        eventSpotsCrsr = childCrsr.childElementCursor();
                        while (eventSpotsCrsr.getNext() != null) {
                            if (eventSpotsCrsr.getLocalName().toLowerCase().equals("eventspot")) {
                                x = -1;
                                y = -1;
                                seed = -1;
                                frequency = -1;
                                radius = -1;
                                eventSpotType = "";
                                eventSpotCrsr = eventSpotsCrsr.childElementCursor();
                                while (eventSpotCrsr.getNext() != null) {
                                    if (eventSpotCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            x = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            y = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("seed")) {
                                        try {
                                            seed = Long.parseLong(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("frequency")) {
                                        try {
                                            frequency = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            radius = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("eventspottype")) {
                                        try {
                                            eventSpotType = eventSpotCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    } else
                                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileEventSpot") + childCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                                }
                                if (frequency != -1) {
                                    try {
                                        EventSpotList.getInstance().addEventSpot(new EventSpot(x, y, frequency, radius, eventSpotType, seed));
                                    } catch (Exception e) {
                                    }
                                }
                            } else
                                ErrorLog.log(Messages.getString("Scenario.unknownElementWhileEventSpots") + childCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                        }
                    } else
                        ErrorLog.log(Messages.getString("Scenario.unknownElementWhileScenario") + childCrsr.getLocalName(), 5, getClass().getName(), "load", null);
                }
            } else
                ErrorLog.log(Messages.getString("Scenario.wrongRoot"), 7, getClass().getName(), "load", null);
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("Scenario.errorLoading"), 7, getClass().getName(), "load", e);
        }
        if (!Renderer.getInstance().isConsoleStart())
            VanetSimStart.setProgressBar(false);
        ready_ = true;
        Renderer.getInstance().ReRender(false, false);
        if (!Renderer.getInstance().isConsoleStart())
            VanetSimStart.getMainControlPanel().getEditPanel().getEditEventPanel().updateList();
        ErrorLog.log(Messages.getString("Scenario.finishedLoading"), 3, getClass().getName(), "load", null);
    }
//fin load()
    public void setReadyState(boolean ready) {
        ready_ = ready;
    }

    public void save(File file, boolean zip) {
        try {
            VanetSimStart.setProgressBar(true);
            //JOptionPane.showConfirmDialog(null, "a");
            ErrorLog.log(Messages.getString("Scenario.savingScenario") + file.getName(), 3, getClass().getName(), "save", null);
       //    JOptionPane.showConfirmDialog(null, "1");
            int i, j, k;
            Vehicle[] vehiclesArray;
            Node[] mixZoneArray;
            ArrayDeque<WayPoint> destinations;
            Iterator<WayPoint> wayPointIterator;
            Vehicle vehicle;
            Node mixNode;
            WayPoint wayPoint;
//  JOptionPane.showConfirmDialog(null, "2");
            Event event;
            SMOutputElement level1, level2, level3;
            int Region_cnt_x = Map.getInstance().getRegionCountX();
            int Region_cnt_y = Map.getInstance().getRegionCountY();
            Region[][] Regions = Map.getInstance().getRegions();
            OutputStream filestream;
            if (zip) {
                filestream = new ZipOutputStream(new FileOutputStream(file + ".zip"));
                ((ZipOutputStream) filestream).putNextEntry(new ZipEntry(file.getName()));
            } else
                filestream = new FileOutputStream(file);
          //   JOptionPane.showConfirmDialog(null, "3");
           XMLStreamWriter xw = XMLOutputFactory.newInstance().createXMLStreamWriter(filestream);
        //    JOptionPane.showConfirmDialog(null, "31");
           SMOutputDocument doc = SMOutputFactory.createOutputDocument(xw);
        //    JOptionPane.showConfirmDialog(null, "32");
            doc.setIndentation("\n\t\t\t\t\t\t\t\t", 2, 1);
         //   JOptionPane.showConfirmDialog(null, "33");
            doc.addComment(scenarioName);
           doc.addComment("Generated on " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
         //   JOptionPane.showConfirmDialog(null, "34");
            SMOutputElement root = doc.addElement("Scenario");
           // JOptionPane.showConfirmDialog(null, "35");
            SMOutputElement settings = root.addElement("Settings");
            
            //JOptionPane.showConfirmDialog(null, "4");
            settings.addElement("CommunicationEnabled").addValue(Vehicle.getCommunicationEnabled());
            settings.addElement("BeaconsEnabled").addValue(Vehicle.getBeaconsEnabled());
            settings.addElement("GlobalInfrastructureEnabled").addValue(true);
            settings.addElement("CommunicationInterval").addValue(Vehicle.getCommunicationInterval());
            settings.addElement("BeaconsInterval").addValue(Vehicle.getBeaconInterval());
            settings.addElement("MixZonesEnabled").addValue(Vehicle.getMixZonesEnabled());
            settings.addElement("MixZoneRadius").addValue(Vehicle.getMixZoneRadius());
            settings.addElement("AutoAddMixZones").addValue(Renderer.getInstance().isAutoAddMixZones());
            settings.addElement("RoutingMode").addValue(Vehicle.getRoutingMode());
            settings.addElement("VehicleRecyclingEnabled").addValue(Vehicle.getRecyclingEnabled());
            settings.addElement("FallBackInMixZonesEnabled").addValue(Vehicle.getMixZonesFallbackEnabled());
            settings.addElement("FallBackInMixZonesFloodingOnly").addValue(Vehicle.getMixZonesFallbackFloodingOnly());
            settings.addElement("MinTravelTimeForRecycling").addValue(Vehicle.getMinTravelTimeForRecycling());
            settings.addElement("ARSULoggingEnabled").addValue(Vehicle.isAttackerDataLogged_());
 
            settings.addElement("ARSUEncryptedLoggingEnabled").addValue(Vehicle.isAttackerEncryptedDataLogged_());
            settings.addElement("privacyLoggingEnabled").addValue(Vehicle.isPrivacyDataLogged_());
             
            settings.addElement("IDSLog").addCharacters(IDSLogWriter.getLogPath());
             
            settings.addElement("EventLog").addCharacters(EventLogWriter.getLogPath());
            settings.addElement("SilentPeriodsEnabled").addValue(Vehicle.isSilentPeriodsOn());
            settings.addElement("SilentPeriodDuration").addValue(Vehicle.getTIME_OF_SILENT_PERIODS());
            settings.addElement("SilentPeriodFrequency").addValue(Vehicle.getTIME_BETWEEN_SILENT_PERIODS());
            settings.addElement("SlowEnabled").addValue(Vehicle.isSlowOn());
            settings.addElement("SlowTimeToChangePseudonym").addValue(Vehicle.getTIME_TO_PSEUDONYM_CHANGE());
            settings.addElement("SlowSpeedLimit").addValue(Vehicle.getSLOW_SPEED_LIMIT());
            settings.addElement("idsActivated").addValue(Vehicle.isIdsActivated());
            settings.addElement("beaconsLogged").addValue(KnownVehicle.getAmountOfSavedBeacons_());
            settings.addElement("fakeMessageInterval").addValue(Vehicle.getFakeMessagesInterval_());
             
 
            settings.addElement("EVAMessageDelay").addCharacters(Vehicle.getMaxEVAMessageDelay_() + "");
            
            settings.addElement("SpamDetection").addValue(KnownEventSource.isSpamcheck());
            settings.addElement("SpamMessageThreshold").addCharacters(KnownEventSource.getSpammingthreshold() + "");
            settings.addElement("SpamTimeThreshold").addCharacters(KnownEventSource.getSpammingtimethreshold() + "");
            settings.addElement("TrafficModel").addValue(WorkerThread.getSimulationMode_());
            
            settings.addElement("NumberOfGeneratedVehicles").addValue(vanetsim.gui.controlpanels.ClusteringControlPanel.NbreDeVehicule);
            
            
            settings.addElement("LambdaPoisson").addValue((int)vanetsim.gui.controlpanels.ClusteringControlPanel.lambda);
            settings.addElement("TimeSimulation").addValue(vanetsim.gui.controlpanels.ClusteringControlPanel.tsim);
            
            
            String activatedRules = "";
            
 //JOptionPane.showConfirmDialog(null, "b");
 
            SMOutputElement vehicles = root.addElement("Vehicles");
            int z=0;
            for (i = 0; i < Region_cnt_x; ++i) {
                for (j = 0; j < Region_cnt_y; ++j) {
                    vehiclesArray = Regions[i][j].getVehicleArray();
                    for (k = 0; k < vehiclesArray.length; ++k,z++) {
                        vehicle = vehiclesArray[k];
                        level1 = vehicles.addElement("Vehicle");
                        //*******/addValue
                        level1.addElement("NumOfVehicle").addValue(z);
                        level1.addElement("VehicleID").addValue(vehicle.steadyID_);/****bricole*/
                        level1.addElement("ArrivalTime").addValue(vehicle.getArrivalTime());
                        level1.addElement("VehicleLength").addValue(vehicle.getVehicleLength());
                        level1.addElement("CurrentStartLane").addValue(vehicle.getCurLane());
                        level1.addElement("StartSpeed").addValue(vehicle.getCurSpeed());
                        level1.addElement("MaxSpeed").addValue(vehicle.getMaxSpeed());
                        level1.addElement("MaxCommDist").addValue(vehicle.getMaxCommDistance());
                        level1.addElement("Wifi").addValue(vehicle.isWiFiEnabled());
                        level1.addElement("emergencyVehicle").addValue(vehicle.isEmergencyVehicle());
                        level1.addElement("braking_rate").addValue(vehicle.getBrakingRate());
                        level1.addElement("acceleration_rate").addValue(vehicle.getAccelerationRate());
                        level1.addElement("timeDistance").addValue(vehicle.getTimeDistance());
                        level1.addElement("politeness").addValue(vehicle.getPoliteness());
                        level1.addElement("speeddeviation").addValue(vehicle.getSpeedDeviation_());
                        level1.addElement("Color").addValue(vehicle.getColor().getRGB());
                        level1.addElement("isFakingMessages").addValue(vehicle.isFakingMessages());
                        level1.addElement("fakingMessageType").addCharacters(vehicle.getFakeMessageType());
                        if (Renderer.getInstance().getAttackerVehicle() == vehicle)
                            level1.addElement("isAttacker").addValue(true);
                        else
                            level1.addElement("isAttacker").addValue(false);
                        if (Renderer.getInstance().getAttackedVehicle() == vehicle)
                            level1.addElement("isAttacked").addValue(true);
                        else
                            level1.addElement("isAttacked").addValue(false);
                        level2 = level1.addElement("Destinations");
                        level3 = level2.addElement("WayPoint");
                        level3.addElement("x").addValue(vehicle.getX());
                        level3.addElement("y").addValue(vehicle.getY());
                        level3.addElement("wait").addValue(vehicle.getWaittime());
                        destinations = vehicle.getDestinations();
                        wayPointIterator = destinations.iterator();
                        while (wayPointIterator.hasNext()) {
                            wayPoint = wayPointIterator.next();
                            level3 = level2.addElement("WayPoint");
                            level3.addElement("x").addValue(wayPoint.getX());
                            level3.addElement("y").addValue(wayPoint.getY());
                            level3.addElement("wait").addValue(wayPoint.getWaittime());
                        }
                    }
                }
            }
         //  JOptionPane.showConfirmDialog(null, "c");
            SMOutputElement mixZones = root.addElement("MixZones");
            for (i = 0; i < Region_cnt_x; ++i) {
                for (j = 0; j < Region_cnt_y; ++j) {
                    mixZoneArray = Regions[i][j].getMixZoneNodes();
                    for (k = 0; k < mixZoneArray.length; ++k) {
                        mixNode = mixZoneArray[k];
                        level1 = mixZones.addElement("MixNode");
                        level1.addElement("x").addValue(mixNode.getX());
                        level1.addElement("y").addValue(mixNode.getY());
                        level1.addElement("radius").addValue(mixNode.getMixZoneRadius());
 
                    }
                }
            }
// JOptionPane.showConfirmDialog(null, "d");
 
            SMOutputElement events = root.addElement("Events");
            Iterator<Event> eventIterator = EventList.getInstance().getIterator();
            while (eventIterator.hasNext()) {
                event = eventIterator.next();
                level1 = events.addElement("Event");
                level1.addElement("Time").addValue(event.getTime());
                 
            }
 // JOptionPane.showConfirmDialog(null, "e");          
            SMOutputElement eventspots = root.addElement("EventSpots");
            EventSpot tmpSpot = EventSpotList.getInstance().getHead_();
            while (tmpSpot != null) {
                level1 = eventspots.addElement("EventSpot");
                level1.addElement("x").addValue(tmpSpot.getX_());
                level1.addElement("y").addValue(tmpSpot.getY_());
                level1.addElement("seed").addValue(tmpSpot.getSeed_());
                level1.addElement("frequency").addValue(tmpSpot.getFrequency_());
                level1.addElement("radius").addValue(tmpSpot.getRadius_());
                level1.addElement("eventSpotType").addCharacters(tmpSpot.getEventSpotType_());
                tmpSpot = tmpSpot.getNext_();
            }
      //      JOptionPane.showConfirmDialog(null, "f");
             doc.closeRoot();
            xw.close();
            filestream.close();
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("Scenario.errorWhileSaving"), 6, getClass().getName(), "save", e);
        }
        VanetSimStart.setProgressBar(false);
    }

    public boolean getReadyState() {
        return ready_;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }
}
