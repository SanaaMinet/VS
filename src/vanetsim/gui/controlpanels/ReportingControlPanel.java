package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.map.Street;
 
import vanetsim.scenario.Vehicle;
 

public final class ReportingControlPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

    private static final long serialVersionUID = 5121974914528330821L;

    private static final int STATISTICS_ACTUALIZATION_INTERVAL = 500;

    private static final int BEACONINFO_ACTUALIZATION_INTERVAL = 500;

    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat(",##0");

    private static final DecimalFormat INTEGER_FORMAT_FRACTION = new DecimalFormat(",##0.00");

    private final JTextArea statisticsTextArea_;

    private final JCheckBox autoUpdateStatisticsCheckBox_;

    private final JTextArea beaconInfoTextArea_;

    private final JScrollBar beaconInfoVerticalScrollBar_;

    private final JCheckBox doMonitorBeaconsCheckBox_;

    private final JCheckBox monitoredBeaconZoneEditCheckBox_;

    private final JCheckBox monitoredBeaconZoneShowCheckBox_;

    private final StringBuilder statisticsText_ = new StringBuilder();

    private final StringBuilder beaconInfoText_ = new StringBuilder();

    private boolean active_ = false;

    private boolean updateStatistics_ = false;

    private boolean updateBeaconInfo_ = false;

    private boolean monitoredBeaconZoneEdit_ = false;

    private int lastPressedX_ = -1;

    private int lastPressedY_ = -1;

    private int statisticsCountdown_ = 0;

    private int beaconInfoCountdown_ = 0;

    private final JButton privacyLogCleaner_;

    private final JButton privacyLogAnalyzer_;

    private FileFilter logFileFilter_;

    private JList<String> availableScripts_;

    private DefaultListModel<String> availableScriptsModel_ = new DefaultListModel<String>();

    public ReportingControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 3;
        JLabel jLabel1 = new JLabel("<html><b>" + Messages.getString("ReportingControlPanel.statistics") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);
        c.gridwidth = 2;
        autoUpdateStatisticsCheckBox_ = new JCheckBox(Messages.getString("ReportingControlPanel.autoupdateStatistics"), false);
        autoUpdateStatisticsCheckBox_.addItemListener(this);
        ++c.gridy;
        add(autoUpdateStatisticsCheckBox_, c);
        statisticsTextArea_ = new JTextArea(11, 1);
        statisticsTextArea_.setEditable(false);
        statisticsTextArea_.setLineWrap(true);
        JScrollPane scrolltext = new JScrollPane(statisticsTextArea_);
        scrolltext.setMinimumSize(new Dimension(180, 300));
        c.fill = GridBagConstraints.HORIZONTAL;
        ++c.gridy;
        add(scrolltext, c);
        c.gridwidth = 1;
        c.gridx = 1;
        ++c.gridy;
        c.fill = GridBagConstraints.NONE;
        JPanel tmpPanel = new JPanel();
        tmpPanel.add(ButtonCreator.getJButton("refresh.png", "refresh", Messages.getString("ReportingControlPanel.refresh"), this));
        tmpPanel.add(ButtonCreator.getJButton("clipboard.png", "copyStatisticsInfo", Messages.getString("ReportingControlPanel.copyStatisticsToClipboard"), this));
        add(tmpPanel, c);
        c.gridwidth = 3;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        jLabel1 = new JLabel("<html><b>" + Messages.getString("ReportingControlPanel.monitoredBeaconZoneInfo") + "</b></html>");
        ++c.gridy;
        c.insets = new Insets(25, 5, 5, 5);
        add(jLabel1, c);
        c.insets = new Insets(5, 5, 0, 5);
        doMonitorBeaconsCheckBox_ = new JCheckBox(Messages.getString("ReportingControlPanel.enableMonitoring"), false);
        doMonitorBeaconsCheckBox_.addItemListener(this);
        ++c.gridy;
        add(doMonitorBeaconsCheckBox_, c);
        monitoredBeaconZoneShowCheckBox_ = new JCheckBox(Messages.getString("ReportingControlPanel.showMonitoredZone"), false);
        monitoredBeaconZoneShowCheckBox_.addItemListener(this);
        ++c.gridy;
        add(monitoredBeaconZoneShowCheckBox_, c);
        monitoredBeaconZoneEditCheckBox_ = new JCheckBox(Messages.getString("ReportingControlPanel.editMonitoredZone"), false);
        monitoredBeaconZoneEditCheckBox_.addItemListener(this);
        ++c.gridy;
        add(monitoredBeaconZoneEditCheckBox_, c);
        c.insets = new Insets(5, 5, 5, 5);
        beaconInfoTextArea_ = new JTextArea(25, 1);
        beaconInfoTextArea_.setEditable(false);
        beaconInfoTextArea_.setText(Messages.getString("ReportingControlPanel.legend"));
        beaconInfoTextArea_.setLineWrap(true);
        scrolltext = new JScrollPane(beaconInfoTextArea_);
        scrolltext.setMinimumSize(new Dimension(180, 300));
        scrolltext.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        beaconInfoVerticalScrollBar_ = scrolltext.getVerticalScrollBar();
        ++c.gridy;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(scrolltext, c);
        c.gridwidth = 1;
        c.gridx = 1;
        ++c.gridy;
        c.fill = GridBagConstraints.NONE;
        tmpPanel = new JPanel();
        tmpPanel.add(ButtonCreator.getJButton("delete.png", "deleteBeaconInfo", Messages.getString("ReportingControlPanel.deleteBeaconInfo"), this));
        tmpPanel.add(ButtonCreator.getJButton("clipboard.png", "copyBeaconInfo", Messages.getString("ReportingControlPanel.copyBeaconToClipBoard"), this));
        add(tmpPanel, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        ++c.gridy;
        c.insets = new Insets(25, 5, 5, 5);
        add(new JLabel("<html><b>" + Messages.getString("ReportingControlPanel.analyseLog") + "</b></html>"), c);
        c.insets = new Insets(5, 5, 0, 5);
        ++c.gridy;
        c.gridx = 0;
        privacyLogCleaner_ = new JButton(Messages.getString("EditLogControlPanel.privacyLogCleanerButton"));
        privacyLogCleaner_.setActionCommand("cleanLog");
        privacyLogCleaner_.setPreferredSize(new Dimension(200, 20));
        privacyLogCleaner_.addActionListener(this);
        add(privacyLogCleaner_, c);
        ++c.gridy;
        c.gridx = 0;
        privacyLogAnalyzer_ = new JButton(Messages.getString("EditLogControlPanel.privacyLogAnalyzerButton"));
        privacyLogAnalyzer_.setActionCommand("openAnalyzer");
        privacyLogAnalyzer_.setPreferredSize(new Dimension(200, 20));
        privacyLogAnalyzer_.addActionListener(this);
        add(privacyLogAnalyzer_, c);
        ++c.gridy;
        c.gridx = 0;
        JButton calculateStreetLengthButton = new JButton(Messages.getString("ReportingControlPanel.calculateStreetLength"));
        calculateStreetLengthButton.setActionCommand("openStreetLengthCalculator");
        calculateStreetLengthButton.setPreferredSize(new Dimension(200, 20));
        calculateStreetLengthButton.addActionListener(this);
        add(calculateStreetLengthButton, c);
        ++c.gridy;
        c.gridx = 0;
        JButton advancedLocationInformation = new JButton(Messages.getString("ReportingControlPanel.showAdvancedLocationInformation"));
        advancedLocationInformation.setActionCommand("showAdvancedLocationInformation");
        advancedLocationInformation.setPreferredSize(new Dimension(200, 20));
        advancedLocationInformation.addActionListener(this);
        add(advancedLocationInformation, c);
        ++c.gridy;
        c.gridx = 0;
        JButton correctLogFiles = new JButton(Messages.getString("ReportingControlPanel.correctLogFiles"));
        correctLogFiles.setActionCommand("correctLogFiles");
        correctLogFiles.setPreferredSize(new Dimension(200, 20));
        correctLogFiles.addActionListener(this);
        add(correctLogFiles, c);
        ++c.gridy;
        c.gridx = 0;
        JButton accuSimpleFiles = new JButton(Messages.getString("ReportingControlPanel.accumulateSimpleLogFiles"));
        accuSimpleFiles.setActionCommand("accumulateSimple");
        accuSimpleFiles.setPreferredSize(new Dimension(200, 20));
        accuSimpleFiles.addActionListener(this);
        add(accuSimpleFiles, c);
        ++c.gridy;
        c.gridx = 0;
        JButton accuDetailFiles = new JButton(Messages.getString("ReportingControlPanel.accumulateDetailedLogFiles"));
        accuDetailFiles.setActionCommand("accumulateDetail");
        accuDetailFiles.setPreferredSize(new Dimension(200, 20));
        accuDetailFiles.addActionListener(this);
        add(accuDetailFiles, c);
        ++c.gridy;
        c.gridx = 0;
        JButton accuIDSFiles = new JButton(Messages.getString("ReportingControlPanel.accumulateIDSLogFiles"));
        accuIDSFiles.setActionCommand("accumulateIDSFiles");
        accuIDSFiles.setPreferredSize(new Dimension(200, 20));
        accuIDSFiles.addActionListener(this);
        add(accuIDSFiles, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        ++c.gridy;
        c.insets = new Insets(25, 5, 5, 5);
        add(new JLabel("<html><b>" + Messages.getString("ReportingControlPanel.sciptCollection") + "</b></html>"), c);
        c.insets = new Insets(5, 5, 0, 5);
        ++c.gridy;
        availableScripts_ = new JList<String>(availableScriptsModel_);
        availableScriptsModel_.add(0, Messages.getString("ReportingControlPanel.accumulateSpammerFiles"));
        availableScriptsModel_.add(1, Messages.getString("ReportingControlPanel.accumulateVehicleIDSResults"));
        availableScriptsModel_.add(2, Messages.getString("ReportingControlPanel.perturbationLog"));
        availableScriptsModel_.add(3, Messages.getString("ReportingControlPanel.makejobs"));
        availableScriptsModel_.add(4, Messages.getString("ReportingControlPanel.accumulateKnownVehiclesTimeFiles"));
        availableScriptsModel_.add(5, Messages.getString("ReportingControlPanel.calculateAngles"));
        availableScripts_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableScripts_.removeListSelectionListener(this);
        availableScripts_.addListSelectionListener(this);
        add(availableScripts_, c);
        ++c.gridy;
        ++c.gridy;
        ++c.gridy;
        c.gridx = 0;
        JButton makejobs = new JButton("makejobs");
        makejobs.setActionCommand("makejobs");
        makejobs.setPreferredSize(new Dimension(200, 20));
        makejobs.addActionListener(this);
        ++c.gridy;
        c.gridx = 0;
        JButton createScenarios = new JButton("Create Scenarios");
        createScenarios.setActionCommand("createscenarios");
        createScenarios.setPreferredSize(new Dimension(200, 20));
        createScenarios.addActionListener(this);
        logFileFilter_ = new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".log");
            }

            public String getDescription() {
                return Messages.getString("EditLogControlPanel.logFiles") + " (*.log)";
            }
        };
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
    }

    public void checkUpdates(int timePerStep) {
        if (updateStatistics_ || statisticsCountdown_ == -1) {
            statisticsCountdown_ -= timePerStep;
            if (statisticsCountdown_ < 1) {
                statisticsCountdown_ += STATISTICS_ACTUALIZATION_INTERVAL;
                updateStatistics();
            }
        }
        if (updateBeaconInfo_) {
            beaconInfoCountdown_ -= timePerStep;
            if (beaconInfoCountdown_ < 1) {
                beaconInfoCountdown_ += BEACONINFO_ACTUALIZATION_INTERVAL;
                updateBeaconInfo();
            }
        }
    }

    private final void updateStatistics() {
        statisticsText_.setLength(0);
        Region[][] regions = Map.getInstance().getRegions();
        Vehicle[] vehicles;
        Vehicle vehicle;
        int i, j, k;
        int activeVehicles = 0;
        int travelledVehicles = 0;
        int wifiVehicles = 0;
        long pcnMessagesCreated = 0;
        long pcnForwardMessagesCreated = 0;
        long evaMessagesCreated = 0;
        long evaForwardMessagesCreated = 0;
        long rhcnMessagesCreated = 0;
        long eeblMessagesCreated = 0;
        long fakeMessagesCreated = 0;
        long IDsChanged = 0;
        double messageForwardFailed = 0;
        double travelDistance = 0;
        double travelTime = 0;
        double speed = 0;
        double knownVehicles = 0;
        for (i = 0; i < regions.length; ++i) {
            for (j = 0; j < regions[i].length; ++j) {
                vehicles = regions[i][j].getVehicleArray();
                for (k = 0; k < vehicles.length; ++k) {
                    vehicle = vehicles[k];
                    if (vehicle.getTotalTravelTime() > 0) {
                        ++travelledVehicles;
                        travelDistance += vehicle.getTotalTravelDistance();
                        travelTime += vehicle.getTotalTravelTime();
                    }
                    if (vehicle.isActive()) {
                        ++activeVehicles;
                        speed += vehicle.getCurSpeed();
                        if (vehicle.isWiFiEnabled()) {
                            ++wifiVehicles;
                            messageForwardFailed += vehicle.getKnownMessages().getFailedForwardCount();
                            knownVehicles += vehicle.getKnownVehiclesList().getSize();
                            IDsChanged += vehicle.getIDsChanged();
                            pcnMessagesCreated += vehicle.getPcnMessagesCreated_();
                            pcnForwardMessagesCreated += vehicle.getPcnForwardMessagesCreated_();
                            evaMessagesCreated += vehicle.getEvaMessagesCreated_();
                            evaForwardMessagesCreated += vehicle.getEvaForwardMessagesCreated_();
                            rhcnMessagesCreated += vehicle.getRhcnMessagesCreated_();
                            eeblMessagesCreated += vehicle.getEeblMessagesCreated_();
                            fakeMessagesCreated += vehicle.getFakeMessagesCreated_();
                        }
                    }
                }
            }
        }
        statisticsText_.append(Messages.getString("ReportingControlPanel.currentTime"));
        statisticsText_.append(INTEGER_FORMAT.format(Renderer.getInstance().getTimePassed()));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.activeVehicles"));
        statisticsText_.append(INTEGER_FORMAT.format(activeVehicles));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.averageSpeed"));
        if (activeVehicles > 0)
            statisticsText_.append(INTEGER_FORMAT_FRACTION.format(speed / activeVehicles / 100000 * 3600));
        else
            statisticsText_.append("0");
        statisticsText_.append(" km/h\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.averageTravelDistance"));
        if (travelledVehicles > 0)
            statisticsText_.append(INTEGER_FORMAT_FRACTION.format(travelDistance / travelledVehicles / 100));
        else
            statisticsText_.append("0");
        statisticsText_.append(" m\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.averageTravelTime"));
        if (travelledVehicles > 0)
            statisticsText_.append(INTEGER_FORMAT_FRACTION.format(travelTime / travelledVehicles / 1000));
        else
            statisticsText_.append("0");
        statisticsText_.append(" s\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.wifiVehicles"));
        statisticsText_.append(INTEGER_FORMAT.format(wifiVehicles));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.averageKnownVehicles"));
        if (wifiVehicles > 0)
            statisticsText_.append(INTEGER_FORMAT_FRACTION.format(knownVehicles / wifiVehicles));
        else
            statisticsText_.append("0");
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniquePCNMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(pcnMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniquePCNFORWARDMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(pcnForwardMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniqueEVAMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(evaMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniqueEVAFORWARDMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(evaForwardMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniqueRHCNMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(rhcnMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniqueEEBLMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(eeblMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.uniqueFAKEMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(fakeMessagesCreated));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.failedMessages"));
        statisticsText_.append(INTEGER_FORMAT.format(messageForwardFailed));
        statisticsText_.append("\n");
        statisticsText_.append(Messages.getString("ReportingControlPanel.totalIDchanges"));
        statisticsText_.append(INTEGER_FORMAT.format(IDsChanged));
        statisticsText_.append("\n");
    
        statisticsTextArea_.setText(statisticsText_.toString());
    }

    public void updateBeaconInfo() {
        boolean autoScroll;
        int scrollDiff = beaconInfoVerticalScrollBar_.getValue() + beaconInfoVerticalScrollBar_.getVisibleAmount() - beaconInfoVerticalScrollBar_.getMaximum();
        if (scrollDiff > -10)
            autoScroll = true;
        else
            autoScroll = false;
        beaconInfoTextArea_.append(beaconInfoText_.toString());
        if (autoScroll)
            beaconInfoTextArea_.setCaretPosition(beaconInfoTextArea_.getDocument().getLength());
        beaconInfoText_.setLength(0);
    }

    public synchronized void addBeacon(Vehicle vehicle, long ID, long x, long y, double speed, boolean isEncrypted) {
        beaconInfoText_.append("\n\nVehicle\n");
        beaconInfoText_.append(Renderer.getInstance().getTimePassed());
        beaconInfoText_.append("ms\n");
        beaconInfoText_.append(Long.toHexString(ID));
        beaconInfoText_.append(",");
        beaconInfoText_.append(speed);
        beaconInfoText_.append("\n");
        beaconInfoText_.append(x);
        beaconInfoText_.append(",");
        beaconInfoText_.append(y);
        beaconInfoText_.append(", encrypted:");
        beaconInfoText_.append(isEncrypted);
    }

    public synchronized void addBeacon(Vehicle vehicle, long ID, long x, long y, double speed, boolean isEncrypted, boolean isForwared) {
        beaconInfoText_.append("\n\nVehicle (forwarded by RSU) \n");
        beaconInfoText_.append(Renderer.getInstance().getTimePassed());
        beaconInfoText_.append("ms\n");
        beaconInfoText_.append(Long.toHexString(ID));
        beaconInfoText_.append(",");
        beaconInfoText_.append(speed);
        beaconInfoText_.append("\n");
        beaconInfoText_.append(x);
        beaconInfoText_.append(",");
        beaconInfoText_.append(y);
        beaconInfoText_.append(", encrypted:");
        beaconInfoText_.append(isEncrypted);
    }

    public synchronized void addBeacon(  long ID, long x, long y, boolean isEncrypted) {
        beaconInfoText_.append("\n\nRSU\n");
        beaconInfoText_.append(Renderer.getInstance().getTimePassed());
        beaconInfoText_.append("ms\n");
        beaconInfoText_.append(ID);
        beaconInfoText_.append("\n");
        beaconInfoText_.append(x);
        beaconInfoText_.append(",");
        beaconInfoText_.append(y);
        beaconInfoText_.append(", encrypted:");
        beaconInfoText_.append(isEncrypted);
    }

    public void receiveMouseEvent(int x, int y) {
        if (x < 0)
            x = 0;
        else if (x > Map.getInstance().getMapWidth())
            x = Map.getInstance().getMapWidth();
        if (y < 0)
            y = 0;
        else if (y > Map.getInstance().getMapHeight())
            y = Map.getInstance().getMapHeight();
        if (lastPressedX_ == -1 && lastPressedY_ == -1) {
            lastPressedX_ = x;
            lastPressedY_ = y;
        } else {
            int minX = Math.min(lastPressedX_, x);
            int maxX = Math.max(lastPressedX_, x);
            int minY = Math.min(lastPressedY_, y);
            int maxY = Math.max(lastPressedY_, y);
            Vehicle.setMonitoredMixZoneVariables(minX, maxX, minY, maxY);
            Renderer.getInstance().setMonitoredBeaconZoneVariables(minX, maxX, minY, maxY);
            if (monitoredBeaconZoneShowCheckBox_.isSelected())
                Renderer.getInstance().ReRender(false, false);
            lastPressedX_ = -1;
            lastPressedY_ = -1;
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("refresh".equals(command)) {
            if (VanetSimStart.getSimulationMaster().isSimulationRunning())
                statisticsCountdown_ = -1;
            else
                updateStatistics();
        } else if ("deleteBeaconInfo".equals(command)) {
            StringSelection ss = new StringSelection(beaconInfoTextArea_.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            beaconInfoTextArea_.setText(Messages.getString("ReportingControlPanel.legend"));
        } else if ("copyStatisticsInfo".equals(command)) {
            StringSelection ss = new StringSelection(statisticsTextArea_.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        } else if ("copyBeaconInfo".equals(command)) {
            StringSelection ss = new StringSelection(beaconInfoTextArea_.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        } else if ("cleanLog".equals(command)) {
            try {
                cleanLogFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
         
        } else if ("openStreetLengthCalculator".equals(command)) {
            calculateStreetLength();
        } else if ("showAdvancedLocationInformation".equals(command)) {
            showAdvancedLocationInformation();
        } else if ("correctLogFiles".equals(command)) {
            editDetailLogFiles();
        } else if ("accumulateSimple".equals(command)) {
            accumulateSimpleLogFiles("MIX");
        } else if ("accumulateDetail".equals(command)) {
            accumulateDetailedLogFiles();
        } else if ("accumulateIDSFiles".equals(command)) {
            accumulateVehicleIDSResults();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        boolean state;
        if (e.getStateChange() == ItemEvent.SELECTED)
            state = true;
        else
            state = false;
        if (e.getSource() == monitoredBeaconZoneEditCheckBox_) {
            monitoredBeaconZoneEdit_ = state;
        } else if (e.getSource() == doMonitorBeaconsCheckBox_) {
            Vehicle.setBeaconMonitorZoneEnabled(state);
            updateBeaconInfo_ = state;
        } else if (e.getSource() == monitoredBeaconZoneShowCheckBox_) {
            Renderer.getInstance().setShowBeaconMonitorZone(state);
            Renderer.getInstance().ReRender(false, false);
        } else if (e.getSource() == autoUpdateStatisticsCheckBox_) {
            updateStatistics_ = state;
        }
    }

    public boolean isActive() {
        return active_;
    }

    public boolean isInMonitoredMixZoneEditMode() {
        if (active_ && monitoredBeaconZoneEdit_)
            return true;
        else
            return false;
    }

    public void setActive(boolean active) {
        active_ = active;
    }

    public void cleanLogFile() throws IOException {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        class MixZoneCoordinate {

            public int xCoordinate = -1;

            public int yCoordinate = -1;

            public int port = -1;

            public MixZoneCoordinate(int x, int y, int p) {
                xCoordinate = x;
                yCoordinate = y;
                port = p;
            }
        }
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            for (File file : files) {
                ArrayList<MixZoneCoordinate> mixZoneCoordinateList = new ArrayList<MixZoneCoordinate>();
                BufferedReader reader;
                FileWriter fstream = new FileWriter(file.getPath().substring(0, file.getPath().length() - 4) + "_cleaned.log");
                BufferedWriter out = new BufferedWriter(fstream);
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    boolean headerSeperator = true;
                    ArrayList<String> savedLines = new ArrayList<String>();
                    while (line != null) {
                        if (line.length() >= 8 && line.substring(0, 8).equals("Mix-Zone")) {
                            ArrayList<String> savedLines2 = new ArrayList<String>();
                            boolean saved = false;
                            String[] lineSplitted = line.split(":");
                            if (savedLines.size() < 1) {
                                savedLines2.add(line);
                                saved = true;
                            }
                            for (String savedLine : savedLines) {
                                if (!saved && Integer.parseInt(lineSplitted[2]) < Integer.parseInt(savedLine.split(":")[2])) {
                                    saved = true;
                                    savedLines2.add(line);
                                }
                                savedLines2.add(savedLine);
                            }
                            if (!saved)
                                savedLines2.add(line);
                            savedLines = savedLines2;
                        } else {
                            if (headerSeperator) {
                                String xArray[];
                                String yArray[];
                                String writeLine = "";
                                for (int j = 0; j < savedLines.size(); j = j + 2) {
                                    xArray = savedLines.get(j).split(":");
                                    yArray = savedLines.get(j + 1).split(":");
                                    writeLine = "Mix-Zone:" + xArray[1] + ":" + xArray[2] + ":" + xArray[3] + ":" + xArray[4];
                                    for (int i = 5; i < xArray.length; i++) {
                                        writeLine += ":" + (i - 4) + "=" + xArray[i] + "/" + yArray[i];
                                        mixZoneCoordinateList.add(new MixZoneCoordinate(Integer.parseInt(xArray[i]), Integer.parseInt(yArray[i]), i - 4));
                                    }
                                    out.write(writeLine + "\n");
                                }
                                out.write("*******************" + "\n");
                                headerSeperator = false;
                            }
                            int xCoord = -1;
                            int yCoord = -1;
                            String[] lineSplit = line.split(":");
                            if (lineSplit.length == 23) {
                                xCoord = Integer.parseInt(lineSplit[20]);
                                yCoord = Integer.parseInt(lineSplit[22]);
                            } else
                                break;
                            for (MixZoneCoordinate c : mixZoneCoordinateList) {
                                long dx = c.xCoordinate - xCoord;
                                long dy = c.yCoordinate - yCoord;
                                long distanceSquared = dx * dx + dy * dy;
                                if (distanceSquared < 500000) {
                                    out.write(lineSplit[0] + ":" + lineSplit[1] + ":" + lineSplit[2] + ":" + lineSplit[3] + ":" + lineSplit[4] + ":" + lineSplit[5] + ":" + lineSplit[6] + ":" + lineSplit[7] + ":" + lineSplit[8] + ":" + lineSplit[9] + ":" + lineSplit[10] + ":" + lineSplit[11] + ":" + lineSplit[12] + ":" + lineSplit[13] + ":" + lineSplit[14] + ":" + lineSplit[15] + ":" + lineSplit[16] + ":" + lineSplit[17] + ":" + lineSplit[18] + ":port:" + c.port + "\n");
                                    break;
                                }
                            }
                        }
                        line = reader.readLine();
                    }
                    out.close();
                } catch (FileNotFoundException e) {
                    System.err.println("FileNotFoundException: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }
            }
        }
    }

    public void calculateStreetLength() {
        BigDecimal streetLength = BigDecimal.ZERO;
        BigDecimal streetLength_old = BigDecimal.ZERO;
        Region[][] regions = Map.getInstance().getRegions();
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[i].length; j++) {
                Street[] tmpStreets = regions[i][j].getStreets();
                for (int k = 0; k < tmpStreets.length; k++) {
                    streetLength_old = new BigDecimal(streetLength.toString());
                    streetLength = streetLength_old.add(BigDecimal.valueOf(tmpStreets[k].getLength()));
                }
            }
        }
        streetLength_old = new BigDecimal(streetLength.toString());
        streetLength = streetLength_old.divide(new BigDecimal(100000));
        JOptionPane.showMessageDialog(null, streetLength.toPlainString() + "km", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void calculateAngles() {
        Region[][] regions = Map.getInstance().getRegions();
        Node[] tmpNodes = null;
        Street[] tmpCrossingStreets = null;
        Street street1 = null;
        Street street2 = null;
        Node nodeA = null;
        Node nodeB = null;
        Node nodeC = null;
        double a = 0;
        double b = 0;
        double c = 0;
        int[] angleCounter = new int[181];
        int degreeCounter = 0;
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[i].length; j++) {
                tmpNodes = regions[i][j].getNodes();
                for (int k = 0; k < tmpNodes.length; k++) {
                    if (tmpNodes[k].getJunction() != null) {
                        nodeA = tmpNodes[k];
                        tmpCrossingStreets = tmpNodes[k].getCrossingStreets();
                        if (tmpCrossingStreets.length > 2) {
                            for (int l = 0; l < tmpCrossingStreets.length; l++) {
                                street1 = tmpCrossingStreets[l];
                                if (!nodeA.equals(street1.getStartNode()))
                                    nodeB = street1.getStartNode();
                                else
                                    nodeB = street1.getEndNode();
                                for (int m = 0; m < tmpCrossingStreets.length; m++) {
                                    street2 = tmpCrossingStreets[m];
                                    if (!street1.equals(street2)) {
                                        if (!nodeA.equals(street2.getStartNode()))
                                            nodeC = street1.getStartNode();
                                        else
                                            nodeC = street2.getEndNode();
                                        degreeCounter++;
                                        c = Math.sqrt((nodeB.getX() - nodeA.getX()) * (nodeB.getX() - nodeA.getX()) + (nodeB.getY() - nodeA.getY()) * (nodeB.getY() - nodeA.getY()));
                                        b = Math.sqrt((nodeC.getX() - nodeA.getX()) * (nodeC.getX() - nodeA.getX()) + (nodeC.getY() - nodeA.getY()) * (nodeC.getY() - nodeA.getY()));
                                        a = Math.sqrt((nodeB.getX() - nodeC.getX()) * (nodeB.getX() - nodeC.getX()) + (nodeB.getY() - nodeC.getY()) * (nodeB.getY() - nodeC.getY()));
                                        if ((-2 * b * c) != 0)
                                            angleCounter[(int) Math.round(Math.toDegrees(Math.acos((a * a - b * b - c * c) / (-2 * b * c))))] = angleCounter[(int) Math.round(Math.toDegrees(Math.acos((a * a - b * b - c * c) / (-2 * b * c))))] + 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        @SuppressWarnings("unused") double entropie = 0;
        double degree = 0;
        for (int n = 0; n < angleCounter.length; n++) {
            degree = ((double) angleCounter[n] / degreeCounter * 100);
            if (degree != 0)
                entropie += (degree * Math.log(degree));
        }
    }

    public void showAdvancedLocationInformation() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        HashMap<String, String> mixHashMap = new HashMap<String, String>();
        HashMap<String, Integer> colors = new HashMap<String, Integer>();
        ArrayList<String> locationInformation = new ArrayList<String>();
        String logType = "";
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                boolean headerSeperator = true;
                String data[];
                while (line != null) {
                    data = line.split(":");
                    if (logType.equals("")) {
                        if (line.length() >= 8 && line.substring(0, 8).equals("Mix-Zone"))
                            logType = "Mix Zone";
                        else
                            logType = "Silent Period";
                    }
                    if (logType.equals("Mix Zone")) {
                        if (line.length() >= 8 && line.substring(0, 8).equals("Mix-Zone")) {
                            for (int i = 5; i < data.length; i++) {
                                mixHashMap.put(data[2] + "-" + (i - 4), data[i].split("=")[1].split("/")[0] + ":" + data[i].split("=")[1].split("/")[1]);
                            }
                        } else {
                            if (headerSeperator) {
                                headerSeperator = false;
                                line = reader.readLine();
                                data = line.split(":");
                            }
                            if (colors.get(data[1] + "-" + data[2] + "-" + data[0]) == null)
                                colors.put(data[1] + "-" + data[2] + "-" + data[0], new Integer(0));
                            colors.put(data[1] + "-" + data[2] + "-" + data[0], new Integer(colors.get(data[1] + "-" + data[2] + "-" + data[0]).intValue() + 1));
                        }
                    } else {
                        if (headerSeperator) {
                            headerSeperator = false;
                            line = reader.readLine();
                            line = reader.readLine();
                        }
                        locationInformation.add(line);
                    }
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
            if (logType.equals("Mix Zone")) {
                int red = 0;
                int green = 0;
                int counter = 0;
                for (Entry<String, String> e : mixHashMap.entrySet()) {
                    red = 0;
                    green = 0;
                    if (colors.get(e.getKey() + "-false") == null)
                        green = 0;
                    else
                        green = colors.get(e.getKey() + "-false").intValue();
                    if (colors.get(e.getKey() + "-true") == null)
                        red = 0;
                    else
                        red = colors.get(e.getKey() + "-true").intValue();
                    counter = counter + red + green;
                    if ((red + green) == 0)
                        locationInformation.add(e.getValue() + ":" + 0 + ":" + 0 + ":" + 0);
                    else
                        locationInformation.add(e.getValue() + ":" + ((float) red / (red + green)) + ":" + ((float) green / (red + green)) + ":" + (red + green));
                }
                Renderer.getInstance().setLocationInformationMix(locationInformation);
                Renderer.getInstance().setMixZoneAmount(counter);
                Renderer.getInstance().ReRender(true, true);
            } else {
                Renderer.getInstance().setLocationInformationSilentPeriod_(locationInformation);
                Renderer.getInstance().ReRender(true, true);
            }
        }
    }

    public float[] accumulateSimpleMixFiles(ArrayList<File> files) {
        float[] returnValue = new float[10];
        float[] values = new float[10];
        for (File file : files) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    if (line.substring(0, 5).equals("Total")) {
                        String[] values2 = line.split(" ");
                        for (int i = 2; i < values2.length; i++) values[i - 2] += Float.valueOf(values2[i]);
                    }
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
        for (int j = 0; j < values.length; j++) returnValue[j] = (float) values[j] / files.size();
        boolean foundMixZone = false;
        for (int i = 1; i < 100; i++) {
            values = new float[10];
            foundMixZone = false;
            for (File file : files) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    while (line != null) {
                        String[] values2 = line.split(" ");
                        if (values2[0].equals("Mix-Zone" + i)) {
                            foundMixZone = true;
                            for (int j = 2; j < values2.length; j++) if (!values2[j].equals("NaN"))
                                values[j - 2] += Float.valueOf(values2[j]);
                        }
                        line = reader.readLine();
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("FileNotFoundException: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }
            }
            if (foundMixZone) {
                System.out.print("Mix-Zone" + i);
                for (int j = 0; j < values.length; j++) System.out.print(" " + (float) values[j] / files.size());
                System.out.println("");
            }
        }
        return returnValue;
    }

    public float[] accumulateSimpleSilentPeriodFiles(ArrayList<File> files) {
        float[] values = new float[10];
        float[] returnValue = new float[10];
        for (File file : files) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    if (!line.substring(0, 1).equals("#")) {
                        String[] values2 = line.split(" ");
                        System.out.println(file.getAbsolutePath());
                        for (int i = 1; i < values2.length; i++) {
                            System.out.println("+ " + values2[i]);
                            values[i - 1] += Float.valueOf(values2[i]);
                        }
                    }
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
        for (int j = 0; j < values.length; j++) if (values[j] != 0)
            returnValue[j] = (float) values[j] / files.size();
        return returnValue;
    }

    public float[] accumulateDetailFiles(ArrayList<File> files) {
        float[] values = new float[200];
        int counter = 0;
        for (File file : files) {
            System.out.println(file.getName());
            counter = 0;
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    if (!line.substring(0, 1).equals("#")) {
                        String[] values2 = line.split(" ");
                        values[counter] += Float.valueOf(values2[values2.length - 1]);
                        counter++;
                    }
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
        float[] returnValues = new float[counter];
        for (int j = 0; j < returnValues.length; j++) returnValues[j] = ((float) values[j] / files.size());
        return returnValues;
    }

    public ArrayList<File> getFileList(File file) {
        File dir = new File(file.getParent());
        String filename = file.getName();
        int index = filename.indexOf(".0_v");
        String stringStart = filename.substring(0, index - 1);
        ArrayList<File> files = new ArrayList<File>();
        for (File f : dir.listFiles()) {
            if (f.getName().contains(stringStart))
                files.add(f);
        }
        return files;
    }

    public void editDetailLogFiles() {
        String filter1 = "_advanced_detail_analyzed";
        String filter2 = "_standard_detail_analyzed";
        JFileChooser fc = new JFileChooser();
        fc.setSize(2560, 1300);
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            for (File file : tmpFiles) {
                ArrayList<File> files = getFileList(file);
                for (File f : files) {
                    if (f.getName().contains(filter1) || f.getName().contains(filter2)) {
                        FileWriter fstream;
                        try {
                            if (!(new File(f.getParent() + "/corrected/")).exists())
                                (new File(f.getParent() + "/corrected/")).mkdir();
                            fstream = new FileWriter(f.getParent() + "/corrected/" + f.getName(), false);
                            BufferedWriter out = new BufferedWriter(fstream);
                            int vehiclesTotal = 0;
                            String[] data;
                            float[] tmpData = new float[200];
                            float[][] tmpData2 = new float[200][200];
                            int counter = 0;
                            BufferedReader reader;
                            try {
                                reader = new BufferedReader(new FileReader(f));
                                String line = reader.readLine();
                                while (line != null) {
                                    if (!line.substring(0, 1).equals("#") && !line.equals("")) {
                                        data = line.split(" ");
                                        for (int i = 1; i < data.length - 1; i++) {
                                            tmpData[counter] += Integer.parseInt(data[i]);
                                            vehiclesTotal += Integer.parseInt(data[i]);
                                            tmpData2[counter][i - 1] = Integer.parseInt(data[i]);
                                        }
                                        counter++;
                                    } else
                                        out.write(line + "\n");
                                    line = reader.readLine();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(f.getAbsolutePath());
                                System.err.println("FileNotFoundException: " + e.getMessage());
                            }
                            float tmpSum = 0;
                            float vehiclesTotal2 = 0;
                            for (int l = 0; l < counter; l++) {
                                out.write("" + l);
                                for (int j = 0; j < counter - 1; j++) out.write(" " + (int) tmpData2[l][j]);
                                if (l == 0) {
                                    out.write(" " + (float) (tmpData[l] / vehiclesTotal) + "\n");
                                } else if (l == 1) {
                                    tmpSum = 0;
                                    for (int i = l; i < counter; i++) tmpSum += tmpData[i];
                                    out.write(" " + (float) (tmpSum / vehiclesTotal) + "\n");
                                } else {
                                    tmpSum = 0;
                                    for (int i = l; i < counter; i++) tmpSum += tmpData[i];
                                    vehiclesTotal2 = vehiclesTotal;
                                    for (int n = 0; n < (l - 1); n++) {
                                        for (int o = 0; o < l; o++) {
                                            vehiclesTotal2 -= tmpData2[o][n];
                                        }
                                    }
                                    out.write(" " + (float) (tmpSum / vehiclesTotal2) + "\n");
                                }
                            }
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void accumulateDetailedLogFiles() {
        String filter2 = "_advanced_detail_analyzed";
        String filter1 = "_standard_detail_analyzed";
        ArrayList<File> files1 = new ArrayList<File>();
        ArrayList<File> files2 = new ArrayList<File>();
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            FileWriter fstream1;
            FileWriter fstream2;
            try {
                if (!(new File(tmpFiles[0].getParent() + "/diagramms/")).exists())
                    (new File(tmpFiles[0].getParent() + "/diagramms/")).mkdir();
                fstream1 = new FileWriter(tmpFiles[0].getParent() + "/diagramms/standardAttackedDetailedDiagramm.txt", false);
                BufferedWriter out1 = new BufferedWriter(fstream1);
                fstream2 = new FileWriter(tmpFiles[0].getParent() + "/diagramms/advancedAttackedDetailedDiagramm.txt", false);
                BufferedWriter out2 = new BufferedWriter(fstream2);
                float[][] standardArray = new float[200][200];
                float[][] advancedArray = new float[200][200];
                for (int i = 0; i < standardArray.length; i++) {
                    for (int j = 0; j < standardArray[0].length; j++) {
                        standardArray[i][j] = -1;
                        advancedArray[i][j] = -1;
                    }
                }
                int counter = 0;
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileList(file);
                    files1 = new ArrayList<File>();
                    files2 = new ArrayList<File>();
                    for (File f : files) {
                        if (f.getName().contains(filter1))
                            files1.add(f);
                        else if (f.getName().contains(filter2))
                            files2.add(f);
                    }
                    float[] accu1 = accumulateDetailFiles(files1);
                    float[] accu2 = accumulateDetailFiles(files2);
                    for (int i = 0; i < accu1.length; i++) {
                        standardArray[i][counter] = accu1[i];
                    }
                    for (int i = 0; i < accu2.length; i++) {
                        advancedArray[i][counter] = accu2[i];
                    }
                    counter++;
                }
                for (int j = 0; j < tmpFiles.length; j++) {
                    out1.write("1 ");
                    out2.write("1 ");
                }
                out1.write("\n");
                out2.write("\n");
                for (int i = 1; i < standardArray.length; i++) {
                    if (standardArray[i][0] == -1)
                        break;
                    for (int j = 0; j < tmpFiles.length; j++) {
                        if (standardArray[i][j] == -1)
                            out1.write("0 ");
                        else
                            out1.write(standardArray[i][j] + " ");
                    }
                    out1.write("\n");
                }
                for (int i = 1; i < advancedArray.length; i++) {
                    if (advancedArray[i][0] == -1)
                        break;
                    for (int j = 0; j < tmpFiles.length; j++) {
                        if (advancedArray[i][j] == -1)
                            out2.write("0 ");
                        else
                            out2.write(advancedArray[i][j] + " ");
                    }
                    out2.write("\n");
                }
                out1.close();
                out2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void makejobs() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            for (File file : tmpFiles) {
                System.out.println(file.getName() + ":slow:standard:0.0:2.2:0.9:0.85:0.9:0.9:0.1:170.0:true:0:10000.0");
            }
        }
    }

    public void accumulateSimpleLogFiles(String mode) {
        String filter2 = "_advanced_simple_analyzed";
        String filter1 = "_standard_simple_analyzed";
        ArrayList<File> files1 = new ArrayList<File>();
        ArrayList<File> files2 = new ArrayList<File>();
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            FileWriter fstream1;
            try {
                if (!(new File(tmpFiles[0].getParent() + "/diagramms/")).exists())
                    (new File(tmpFiles[0].getParent() + "/diagramms/")).mkdir();
                fstream1 = new FileWriter(tmpFiles[0].getParent() + "/diagramms/simpleDiagramm.txt", false);
                BufferedWriter out = new BufferedWriter(fstream1);
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileList(file);
                    files1 = new ArrayList<File>();
                    files2 = new ArrayList<File>();
                    for (File f : files) {
                        if (f.getName().contains(filter1))
                            files1.add(f);
                        else if (f.getName().contains(filter2))
                            files2.add(f);
                    }
                    float[] accu2;
                    if (mode.equals("SP")) {
                        accu2 = accumulateSimpleSilentPeriodFiles(files2);
                    } else {
                        accu2 = accumulateSimpleMixFiles(files2);
                    }
                    for (int i = 0; i < accu2.length; i++) {
                        if (accu2[i] != 0)
                            out.write(" " + accu2[i]);
                    }
                    out.write("\n");
                    out.write("#");
                    out.write("\n");
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void accumulateSimpleLogFilesForSlow(String mode) {
        String filter1 = "_standard_simple_analyzed";
        ArrayList<File> files1 = new ArrayList<File>();
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            FileWriter fstream1;
            try {
                if (!(new File(tmpFiles[0].getParent() + "/diagramms/")).exists())
                    (new File(tmpFiles[0].getParent() + "/diagramms/")).mkdir();
                fstream1 = new FileWriter(tmpFiles[0].getParent() + "/diagramms/simpleDiagramm.txt", false);
                BufferedWriter out = new BufferedWriter(fstream1);
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileList(file);
                    files1 = new ArrayList<File>();
                    for (File f : files) {
                        if (f.getName().contains(filter1))
                            files1.add(f);
                    }
                    float[] accu1;
                    if (mode.equals("SP")) {
                        accu1 = accumulateSimpleSilentPeriodFiles(files1);
                    } else {
                        accu1 = accumulateSimpleMixFiles(files1);
                    }
                    out.write("" + accu1[0]);
                    for (int i = 1; i < accu1.length; i++) {
                        if (accu1[i] != 0)
                            out.write(" " + accu1[i]);
                    }
                    out.write("\n");
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int compare(String[] string1, String[] string2) {
        int returnValue = 0;
        int int1 = 0;
        int int2 = 0;
        for (int i = 0; i < string1.length; i++) {
            try {
                int1 = Integer.parseInt(string1[i]);
                int2 = Integer.parseInt(string2[i]);
                if (int1 < int2) {
                    returnValue = -1;
                    break;
                }
            } catch (Exception e) {
            }
        }
        return returnValue;
    }

    public ArrayList<File> getFileListByV(File file) {
        File dir = new File(file.getParent());
        String filename = file.getName();
        int index = filename.indexOf("_v");
        String stringStart = filename.substring(0, index);
        ArrayList<File> files = new ArrayList<File>();
        for (File f : dir.listFiles()) {
            if (f.getName().contains(stringStart)) {
                System.out.println(f.getName());
                files.add(f);
            }
        }
        return files;
    }

    public ArrayList<File> getFileSpammerListByV(File file) {
        File dir = new File(file.getParent());
        String filename = file.getName();
        String stringStart = filename.substring(filename.indexOf("_"), filename.indexOf("_v"));
        ArrayList<File> files = new ArrayList<File>();
        System.out.println(stringStart);
        for (File f : dir.listFiles()) {
            if (f.getName().contains(stringStart)) {
                System.out.println(f.getName());
                files.add(f);
            }
        }
        return files;
    }

    public void accumulateSpammerFiles() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            try {
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileSpammerListByV(file);
                    System.out.println(file.getName());
                    double accumulateMessageAmount = 0;
                    double accumulateSpamDetected = 0;
                    String[] tmpArray;
                    for (File theFile : files) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new FileReader(theFile));
                            String line = reader.readLine();
                            tmpArray = line.split(":");
                            accumulateMessageAmount += Double.parseDouble(tmpArray[1]);
                            accumulateSpamDetected += Double.parseDouble(tmpArray[3]);
                            System.out.println("fake messages:" + tmpArray[1] + "spam detected:" + tmpArray[3]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(theFile.getAbsolutePath());
                            System.err.println("FileNotFoundException: " + e.getMessage());
                        }
                    }
                    System.out.print("*************");
                    System.out.println("fake messages:" + (accumulateMessageAmount / files.size()) + "spam detected:" + (accumulateSpamDetected / files.size()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void accumulateKnownVehiclesTimeFiles() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            try {
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileSpammerListByV(file);
                    System.out.println(file.getName());
                    double averageTime = 0;
                    for (File theFile : files) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new FileReader(theFile));
                            String line = reader.readLine();
                            averageTime += Double.parseDouble(line);
                            System.out.println("added time:" + line);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(theFile.getAbsolutePath());
                            System.err.println("FileNotFoundException: " + e.getMessage());
                        }
                    }
                    System.out.print("*************");
                    System.out.println("average known vehicle time:" + (averageTime / files.size()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void accumulateVehicleFluctuation() {
        String firstLineFilter = "**********************************";
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            try {
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileListByV(file);
                    System.out.println(file.getName());
                    double[] tempArray = new double[1000];
                    for (int j = 0; j < tempArray.length; j++) tempArray[j] = -1;
                    for (File theFile : files) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new FileReader(theFile));
                            String line = reader.readLine();
                            boolean started = false;
                            int counter = 0;
                            while (line != null) {
                                if (started) {
                                    if (line.equals(""))
                                        break;
                                    else {
                                        System.out.print(Double.parseDouble(line) + " ");
                                        if (tempArray[counter] == -1)
                                            tempArray[counter] = Double.parseDouble(line);
                                        else
                                            tempArray[counter] += Double.parseDouble(line);
                                        counter++;
                                    }
                                } else {
                                    if (line.equals(firstLineFilter))
                                        started = true;
                                }
                                line = reader.readLine();
                            }
                            System.out.print("\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(theFile.getAbsolutePath());
                            System.err.println("FileNotFoundException: " + e.getMessage());
                        }
                    }
                    System.out.print("*************");
                    for (int j = 0; j < tempArray.length; j++) {
                        if (tempArray[j] == -1)
                            break;
                        System.out.println(tempArray[j] / files.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void accumulateVehicleIDSResults() {
        String firstLineFilter = ":TP:TN:FP:FN:";
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] tmpFiles = fc.getSelectedFiles();
            ArrayList<File> sortedFiles = new ArrayList<File>();
            for (int i = 0; i < tmpFiles.length; i++) {
                if (sortedFiles.size() == 0)
                    sortedFiles.add(tmpFiles[i]);
                else {
                    boolean sorted = false;
                    for (File f : sortedFiles) {
                        if (compare(tmpFiles[i].getName().split("_"), f.getName().split("_")) < 0) {
                            sorted = true;
                            sortedFiles.add(sortedFiles.indexOf(f), tmpFiles[i]);
                            break;
                        }
                    }
                    if (!sorted)
                        sortedFiles.add(tmpFiles[i]);
                }
            }
            try {
                for (File file : sortedFiles) {
                    ArrayList<File> files = getFileListByV(file);
                    System.out.println(file.getName());
                    double[] tempArray = new double[1000];
                    int[] pcn_array = new int[4];
                    int[] eebl_array = new int[4];
                    int[] rhcn_array = new int[4];
                    int[] eva_array = new int[4];
                    int[] evaforward_array = new int[4];
                    String[] strArray;
                    for (int j = 0; j < tempArray.length; j++) tempArray[j] = -1;
                    for (File theFile : files) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new FileReader(theFile));
                            String line = reader.readLine();
                            boolean started = false;
                            while (line != null) {
                                if (started) {
                                    strArray = line.split(":");
                                    if (line.contains("PCN:")) {
                                        for (int i = 0; i < pcn_array.length; i++) pcn_array[i] += Integer.parseInt(strArray[i + 2].replace(" ", "").replace("\t", ""));
                                    } else if (line.contains("RHCN:")) {
                                        for (int i = 0; i < rhcn_array.length; i++) rhcn_array[i] += Integer.parseInt(strArray[i + 1].replace(" ", "").replace("\t", ""));
                                    } else if (line.contains("EEBL")) {
                                        for (int i = 0; i < eebl_array.length; i++) {
                                            eebl_array[i] += Integer.parseInt(strArray[i + 1].replace(" ", "").replace("\t", ""));
                                        }
                                    } else if (line.contains("EVA\t:")) {
                                        for (int i = 0; i < eva_array.length; i++) {
                                            eva_array[i] += Integer.parseInt(strArray[i + 1].replace(" ", "").replace("\t", ""));
                                        }
                                    } else if (line.contains("EVA FORWARD")) {
                                        for (int i = 0; i < evaforward_array.length; i++) evaforward_array[i] += Integer.parseInt(strArray[i + 1].replace(" ", "").replace("\t", ""));
                                    }
                                }
                                if (line.equals(firstLineFilter))
                                    started = true;
                                line = reader.readLine();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(theFile.getAbsolutePath());
                            System.err.println("FileNotFoundException: " + e.getMessage());
                        }
                    }
                    System.out.println("Correct Normal:Correct Fake");
                    if (pcn_array[1] != 0 && pcn_array[0] != 0)
                        System.out.println("pcn:" + ((double) pcn_array[1] / (pcn_array[1] + pcn_array[2])) + ":" + ((double) pcn_array[0] / (pcn_array[0] + pcn_array[3])) + ":" + ((double) pcn_array[0] / (pcn_array[0] + pcn_array[2])));
                    if (rhcn_array[1] != 0 && rhcn_array[0] != 0)
                        System.out.println("rhcn:" + ((double) rhcn_array[1] / (rhcn_array[1] + rhcn_array[2])) + ":" + ((double) rhcn_array[0] / (rhcn_array[0] + rhcn_array[3])) + ":" + ((double) rhcn_array[0] / (rhcn_array[0] + rhcn_array[2])));
                    if (eebl_array[1] != 0 && eebl_array[0] != 0)
                        System.out.println("eebl:" + ((double) eebl_array[1] / (eebl_array[1] + eebl_array[2])) + ":" + ((double) eebl_array[0] / (eebl_array[0] + eebl_array[3])) + ":" + ((double) eebl_array[0] / (eebl_array[0] + eebl_array[2])));
                    if (eva_array[1] != 0 && eva_array[0] != 0)
                        System.out.println("eva:" + ((double) eva_array[1] / (eva_array[1] + eva_array[2])) + ":" + ((double) eva_array[0] / (eva_array[0] + eva_array[3])) + ":" + ((double) eva_array[0] / (eva_array[0] + eva_array[2])));
                    if (evaforward_array[1] != 0 && evaforward_array[0] != 0)
                        System.out.println("evaforward:" + ((double) evaforward_array[1] / (evaforward_array[1] + evaforward_array[2])) + ":" + ((double) evaforward_array[0] / (evaforward_array[0] + evaforward_array[3])) + ":" + ((double) evaforward_array[0] / (evaforward_array[0] + evaforward_array[2])));
                    System.out.print("*************");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void perturbationLog() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        int lineCounter = 0;
        int amountOfFiles = 10;
        int counter = 0;
        int counterTotal = 0;
        float x = 0;
        float y = 0;
        float[][] resultsX = new float[5][5];
        float[][] resultsY = new float[5][5];
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                String data[];
                while (line != null) {
                    if (counterTotal % (amountOfFiles) == 0 && counterTotal > 0) {
                        System.out.println(counter + ":" + lineCounter);
                        resultsX[counter][lineCounter] = x / amountOfFiles;
                        resultsY[counter][lineCounter] = y / amountOfFiles;
                        System.out.println(resultsX[counter][lineCounter] + ":" + resultsY[counter][lineCounter]);
                        x = 0;
                        y = 0;
                        lineCounter++;
                        if (lineCounter == 5) {
                            lineCounter = 0;
                            counter++;
                        }
                    }
                    data = line.split(":");
                    System.out.println(Float.parseFloat(data[0]) + ":" + Float.parseFloat(data[1]) + ":" + data[4] + ":" + data[6]);
                    x += Float.parseFloat(data[0]);
                    y += Float.parseFloat(data[1]);
                    counterTotal++;
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
            resultsX[counter][lineCounter] = x / amountOfFiles;
            resultsY[counter][lineCounter] = y / amountOfFiles;
            for (int i = 0; i < resultsX.length; i++) {
                for (int j = 0; j < resultsX[i].length; j++) {
                    System.out.print(resultsX[j][i] + " " + resultsY[j][i] + " ");
                }
                System.out.println();
            }
        }
    }

    public void logPercentage() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                line = reader.readLine();
                String data[];
                while (line != null) {
                    data = line.split(" ");
                    System.out.println("0.00," + Math.round(Double.parseDouble(data[1]) * 100 / (Double.parseDouble(data[1]) + Double.parseDouble(data[2])) * 100) / 100.0 + "," + Math.round(Double.parseDouble(data[3]) * 100 / (Double.parseDouble(data[3]) + Double.parseDouble(data[4])) * 100) / 100.0 + "," + Math.round(Double.parseDouble(data[5]) * 100 / (Double.parseDouble(data[5]) + Double.parseDouble(data[6])) * 100) / 100.0 + "," + Math.round(Double.parseDouble(data[7]) * 100 / (Double.parseDouble(data[7]) + Double.parseDouble(data[8])) * 100) / 100.0 + "," + Math.round(Double.parseDouble(data[9]) * 100 / (Double.parseDouble(data[9]) + Double.parseDouble(data[10])) * 100) / 100.0);
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
    }

    public void reorderData() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            FileWriter fstream;
            try {
                fstream = new FileWriter("Cluster_sorted.log", true);
                BufferedWriter out = new BufferedWriter(fstream);
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                String data1[];
                String data2[];
                String data3[];
                data1 = line.split(" ");
                line = reader.readLine();
                data2 = line.split(" ");
                line = reader.readLine();
                data3 = line.split(" ");
                for (int i = 0; i < data1.length; i++) {
                    out.write(data1[i] + " " + data2[i] + " " + data3[i] + "\n");
                }
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
    }

    public void getMax() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        int max = 0;
        int nullCounter = 0;
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                String data[];
                while (line != null) {
                    data = line.split(",");
                    for (int i = 0; i < data.length; i++) if (Integer.parseInt(data[i]) > max)
                        max = Integer.parseInt(data[i]);
                    for (int i = 0; i < data.length; i++) if (Integer.parseInt(data[i]) == 0)
                        nullCounter++;
                    line = reader.readLine();
                }
                System.out.println("max:" + max + " null zaehler: " + nullCounter);
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (availableScripts_.getSelectedIndex() == 0)
            accumulateSpammerFiles();
        else if (availableScripts_.getSelectedIndex() == 0)
            accumulateSpammerFiles();
        else if (availableScripts_.getSelectedIndex() == 1)
            accumulateVehicleIDSResults();
        else if (availableScripts_.getSelectedIndex() == 2)
            perturbationLog();
        else if (availableScripts_.getSelectedIndex() == 3)
            makejobs();
        else if (availableScripts_.getSelectedIndex() == 4)
            accumulateKnownVehiclesTimeFiles();
        else if (availableScripts_.getSelectedIndex() == 5)
            calculateAngles();
    }
}
