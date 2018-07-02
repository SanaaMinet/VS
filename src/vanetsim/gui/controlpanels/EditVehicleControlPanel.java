package vanetsim.gui.controlpanels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.gui.helpers.TextAreaLabel;
import vanetsim.gui.helpers.VehicleType;
import vanetsim.gui.helpers.VehicleTypeXML;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Region;
import vanetsim.routing.WayPoint;
import vanetsim.scenario.LaneObject;
 
import vanetsim.scenario.Vehicle;
import static vanetsim.simulation.SimulationMaster.TIME_PER_STEP;

public class EditVehicleControlPanel extends JPanel implements ActionListener, MouseListener {
 
    private static final long serialVersionUID = 1347869556374738481L;

    private JLabel chooseVehicleTypeLabel_;

    private JComboBox<VehicleType> chooseVehicleType_;

    private final JFormattedTextField vehicleLength_;

    private final JFormattedTextField minSpeed_;

    private final JFormattedTextField maxSpeed_;

    private final JFormattedTextField minCommDist_;

    private final JFormattedTextField maxCommDist_;

    private final JFormattedTextField minWait_;

    private final JFormattedTextField maxWait_;

    private final JFormattedTextField minBraking_;

    private final JFormattedTextField maxBraking_;

    private final JFormattedTextField minAcceleration_;

    private final JFormattedTextField maxAcceleration_;

    private final JFormattedTextField minTimeDistance_;

    private final JFormattedTextField maxTimeDistance_;

    private final JFormattedTextField minPoliteness_;

    private final JFormattedTextField maxPoliteness_;

    private final JFormattedTextField wiFi_;

    private final JFormattedTextField emergencyVehicle_;

    private final JFormattedTextField fakingVehicle_;

    private JComboBox<String> fakeMessagesTypes_;

    private final JFormattedTextField amount_;

    private final JFormattedTextField speedStreetRestriction_;

    private final JFormattedTextField vehiclesDeviatingMaxSpeed_;

    private final JFormattedTextField deviationFromSpeedLimit_;

    private final JPanel colorPreview_;

    private static JButton createButton_;

    private static JButton deleteButton_;

    private static JButton scenarioApplyButton_;

    private static ArrayList<JButton> buttonList_ = new ArrayList<JButton>();

    public EditVehicleControlPanel() {
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        chooseVehicleTypeLabel_ = new JLabel(Messages.getString("EditOneVehicleControlPanel.selectVehicleType"));
        ++c.gridy;
        add(chooseVehicleTypeLabel_, c);
        chooseVehicleType_ = new JComboBox<VehicleType>();
        chooseVehicleType_.setName("chooseVehicleType");
        refreshVehicleTypes();
        chooseVehicleType_.addActionListener(this);
        c.gridx = 1;
        add(chooseVehicleType_, c);
        c.gridx = 0;
        JLabel jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minSpeed"));
        ++c.gridy;
        add(jLabel1, c);
        minSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minSpeed_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minSpeed_, c);
        c.gridx = 2;
        c.gridheight = 2;
        JButton button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("speed");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxSpeed"));
        ++c.gridy;
        add(jLabel1, c);
        maxSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxSpeed_.setPreferredSize(new Dimension(60, 20));
        ;
        c.gridx = 1;
        add(maxSpeed_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minCommDistance"));
        ++c.gridy;
        add(jLabel1, c);
        minCommDist_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minCommDist_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minCommDist_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("communication distance");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxCommDistance"));
        ++c.gridy;
        add(jLabel1, c);
        maxCommDist_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxCommDist_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxCommDist_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minWaittime"));
        ++c.gridy;
        add(jLabel1, c);
        minWait_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minWait_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minWait_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("wait time");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxWaittime"));
        ++c.gridy;
        add(jLabel1, c);
        maxWait_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxWait_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxWait_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minBraking_rate"));
        ++c.gridy;
        add(jLabel1, c);
        minBraking_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minBraking_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minBraking_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("braking rate");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxBraking_rate"));
        ++c.gridy;
        add(jLabel1, c);
        maxBraking_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxBraking_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxBraking_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minAcceleration_rate"));
        ++c.gridy;
        add(jLabel1, c);
        minAcceleration_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minAcceleration_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minAcceleration_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("acceleration");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxAcceleration_rate"));
        ++c.gridy;
        add(jLabel1, c);
        maxAcceleration_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxAcceleration_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxAcceleration_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minTimeDistance"));
        ++c.gridy;
        add(jLabel1, c);
        minTimeDistance_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minTimeDistance_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minTimeDistance_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("time distance");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxTimeDistance"));
        ++c.gridy;
        add(jLabel1, c);
        maxTimeDistance_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxTimeDistance_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxTimeDistance_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.minPoliteness"));
        ++c.gridy;
        add(jLabel1, c);
        minPoliteness_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minPoliteness_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(minPoliteness_, c);
        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("politeness");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.maxPoliteness"));
        ++c.gridy;
        add(jLabel1, c);
        maxPoliteness_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxPoliteness_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(maxPoliteness_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.vehiclesDeviatingMaxSpeed"));
        ++c.gridy;
        add(jLabel1, c);
        vehiclesDeviatingMaxSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        vehiclesDeviatingMaxSpeed_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(vehiclesDeviatingMaxSpeed_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("vehicles deviating speed");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.deviationFromSpeedLimit"));
        ++c.gridy;
        add(jLabel1, c);
        deviationFromSpeedLimit_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        deviationFromSpeedLimit_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(deviationFromSpeedLimit_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("speed deviation");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.vehicleLength"));
        ++c.gridy;
        add(jLabel1, c);
        vehicleLength_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        vehicleLength_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(vehicleLength_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("length");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.wiFiVehicles"));
        ++c.gridy;
        add(jLabel1, c);
        wiFi_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        wiFi_.setPreferredSize(new Dimension(60, 20));
        wiFi_.setValue(300);
        c.gridx = 1;
        add(wiFi_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("wifi amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.emergencyVehicles"));
        ++c.gridy;
        add(jLabel1, c);
        emergencyVehicle_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        emergencyVehicle_.setPreferredSize(new Dimension(60, 20));
        emergencyVehicle_.setValue(0);
        c.gridx = 1;
        add(emergencyVehicle_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("emergency amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.fakingVehicle"));
        ++c.gridy;
        add(jLabel1, c);
        fakingVehicle_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        fakingVehicle_.setPreferredSize(new Dimension(60, 20));
        fakingVehicle_.setValue(0);
        c.gridx = 1;
        add(fakingVehicle_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("faking amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.selectFakeMessageType"));
        ++c.gridy;
        add(jLabel1, c);
        fakeMessagesTypes_ = new JComboBox<String>();
        fakeMessagesTypes_.setName("fakeMessagesTypes");
        fakeMessagesTypes_.addItem(Messages.getString("EditVehicleControlPanel.all"));
      
        c.gridx = 1;
        add(fakeMessagesTypes_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.amount"));
        ++c.gridy;
        add(jLabel1, c);
        amount_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        amount_.setPreferredSize(new Dimension(60, 20));
        amount_.setValue(100);
        c.gridx = 1;
        add(amount_, c);
        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditOneVehicleControlPanel.color"));
        ++c.gridy;
        add(jLabel1, c);
        colorPreview_ = new JPanel();
        colorPreview_.setBackground(Color.black);
        colorPreview_.setSize(10, 10);
        colorPreview_.addMouseListener(this);
        c.gridx = 1;
        add(colorPreview_, c);
        c.gridx = 0;
        jLabel1 = new JLabel("<html>" + Messages.getString("EditVehicleControlPanel.onlyOnLowerSpeedStreets"));
        ++c.gridy;
        add(jLabel1, c);
        speedStreetRestriction_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        speedStreetRestriction_.setPreferredSize(new Dimension(60, 20));
        speedStreetRestriction_.setValue(80);
        c.gridx = 1;
        add(speedStreetRestriction_, c);
        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        createButton_ = ButtonCreator.getJButton("randomVehicles.png", "createRandom", Messages.getString("EditVehicleControlPanel.createRandom"), this);
        add(createButton_, c);
        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        deleteButton_ = ButtonCreator.getJButton("deleteAll.png", "clearVehicles", Messages.getString("EditVehicleControlPanel.btnClearVehicles"), this);
        add(deleteButton_, c);
        c.gridx = 0;
        scenarioApplyButton_ = new JButton(Messages.getString("EditVehicleControlPanel.apply"));
        scenarioApplyButton_.setActionCommand("applyToScenarioCreator");
        scenarioApplyButton_.addActionListener(this);
        add(scenarioApplyButton_, c);
        c.gridheight = 1;
        c.gridx = 0;
        ++c.gridy;
        add(ButtonCreator.getJButton("openTypeDialog.png", "openTypeDialog", Messages.getString("EditControlPanel.openTypeDialog"), this), c);
        c.gridx = 0;
        ++c.gridy;
        TextAreaLabel jlabel1 = new TextAreaLabel(Messages.getString("EditVehicleControlPanel.note"));
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 2;
        add(jlabel1, c);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
        actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        //JOptionPane.showConfirmDialog(null, "command : "+command);
        if ("createRandom".equals(command)) {
            //JOptionPane.showConfirmDialog(null, "CreateRandom");
            Renderer.getInstance().setShowVehicles(true);
            Runnable job = new Runnable() {

                public void run() {
                    int i, j, k, l = 0;
                    VanetSimStart.setProgressBar(true);
                    int maxX = Map.getInstance().getMapWidth();
                    int maxY = Map.getInstance().getMapHeight();
                    int minSpeedValue = (int) Math.round(((Number) minSpeed_.getValue()).intValue() * 100000.0 / 3600);
                    int maxSpeedValue = (int) Math.round(((Number) maxSpeed_.getValue()).intValue() * 100000.0 / 3600);
                    int minCommDistValue = ((Number) minCommDist_.getValue()).intValue() * 100;
                    int maxCommDistValue = ((Number) maxCommDist_.getValue()).intValue() * 100;
                    int minWaitValue = ((Number) minWait_.getValue()).intValue();
                    int maxWaitValue = ((Number) maxWait_.getValue()).intValue();
                    int minBrakingValue = ((Number) minBraking_.getValue()).intValue();
                    int maxBrakingValue = ((Number) maxBraking_.getValue()).intValue();
                    int minAccelerationValue = ((Number) minAcceleration_.getValue()).intValue();
                    int maxAccelerationValue = ((Number) maxAcceleration_.getValue()).intValue();
                    int minTimeDistance = ((Number) minTimeDistance_.getValue()).intValue();
                    int maxTimeDistance = ((Number) maxTimeDistance_.getValue()).intValue();
                    int minPoliteness = ((Number) minPoliteness_.getValue()).intValue();
                    int maxPoliteness = ((Number) maxPoliteness_.getValue()).intValue();
                    int vehiclesDeviatingMaxSpeed = ((Number) vehiclesDeviatingMaxSpeed_.getValue()).intValue();
                    int deviationFromSpeedLimit = ((Number) deviationFromSpeedLimit_.getValue()).intValue();
                    int speedDeviation = 0;
                    int wiFiValue = ((Number) wiFi_.getValue()).intValue();
                    int emergencyValue = ((Number) emergencyVehicle_.getValue()).intValue();
                    int speedRestriction = (int) Math.round(((Number) speedStreetRestriction_.getValue()).intValue() * 100000.0 / 3600);
                    int vehiclesFaking = ((Number) fakingVehicle_.getValue()).intValue();
                    if (wiFiValue < 0) {
                        wiFiValue = 0;
                        wiFi_.setValue(0);
                    } else if (wiFiValue > 100) {
                        wiFiValue = 300;
                        wiFi_.setValue(300);
                    }
                    if (emergencyValue < 0) {
                        emergencyValue = 0;
                        emergencyVehicle_.setValue(0);
                    } else if (emergencyValue > 100) {
                        emergencyValue = 100;
                        emergencyVehicle_.setValue(100);
                    }
                    if (vehiclesFaking < 0) {
                        vehiclesFaking = 0;
                        fakingVehicle_.setValue(0);
                    } else if (vehiclesFaking > 100) {
                        vehiclesFaking = 100;
                        fakingVehicle_.setValue(100);
                    }
                    int amountValue = ClusteringControlPanel.NbreDeVehicule;//  3;//((Number) amount_.getValue()).intValue();
                    
                    //-------------Creation ------------------------------------
                    vanetsim.scenario.Vehicle.TV = new vanetsim.scenario.Vehicle.Vehicule[amountValue];
                   // vanetsim.scenario.Vehicle.TV2 = new vanetsim.scenario.Vehicle.Vehicule[amountValue];
                   // vanetsim.scenario.Vehicle.TV3 = new vanetsim.scenario.Vehicle.Vehicule[amountValue];
                    
                    vanetsim.scenario.Vehicle.TR = new long[amountValue][amountValue];
                    
                    for(int u=0;u<amountValue;u++)
                    {
                      
                            for(int v=0;v<amountValue;v++) vanetsim.scenario.Vehicle.TR[u][v]=-1;
                    } 
                    
                     
                   // JOptionPane.showConfirmDialog(null, "Taille des vehicules : "+vanetsim.scenario.Vehicle.TV.length);
                    //----------------------------------------------------------
                    boolean wiFiEnabled;
                    boolean emergencyEnabled;
                    boolean fakingEnabled;
                    ArrayDeque<WayPoint> destinations = null;
                    Vehicle tmpVehicle;
                    Random random = new Random();
                    int tmpRandom = -1;
                    //JOptionPane.showConfirmDialog(null, "destinations avant X"+);// WayPoint.getX());
                    //JOptionPane.showConfirmDialog(null, "destinations avant Y"+destinations.element().toString()); // ReturnHere
                    //----------------------------------------------------------
                    for (i = 0; i < amountValue; ) 
                    {
                        j = 0;
                        k = 0;
                        ++l;
                        
                        destinations = new ArrayDeque<WayPoint>(2);
                        //JOptionPane.showConfirmDialog(null, "Taille destinations: "+destinations.size()+" / "+amountValue);
                        while (j < 2 && k < 20) {
                            try {
                                ++k;
                                WayPoint tmpWayPoint = new WayPoint(random.nextInt(maxX), random.nextInt(maxY), getRandomRange(minWaitValue, maxWaitValue, random));
                                if (tmpWayPoint.getStreet().getSpeed() <= speedRestriction) 
                                {
                                    destinations.add(tmpWayPoint);
                                    //--------------------------------------------
                                //JOptionPane.showConfirmDialog(null, "tmpWayPoint X"+tmpWayPoint.getX());
                                //JOptionPane.showConfirmDialog(null, "tmpWayPoint Y"+tmpWayPoint.getY());
                                //JOptionPane.showConfirmDialog(null, "tmpWayPoint Y"+tmpWayPo);
                                    //------------------------------------------
                                    
                                    /*ClusteringControlPanel.S.setValueAt(""+(i+1), i, 0); 
                                    ClusteringControlPanel.S.setValueAt(""+tmpWayPoint.getX(), i, 4);  
                                    ClusteringControlPanel.S.setValueAt(""+tmpWayPoint.getY(), i, 5);*/
                                    
                                    ++j;
                                }
 
                             
                            } catch (Exception e) {
                            }
                        }
                        if (k < 20) {
                            try {
                                tmpRandom = getRandomRange(300, 400, random);
                                //System.out.println("tmpRandom Range "+tmpRandom+" VS wiFiValue "+wiFiValue);
                                if (tmpRandom <= vehiclesDeviatingMaxSpeed)
                                {
                                    speedDeviation = getRandomRange(-deviationFromSpeedLimit, deviationFromSpeedLimit, random);
                                    System.out.println("----> tmpRandom Range modified "+tmpRandom);
                                }
                                else
                                    speedDeviation = 0;
                                if (getRandomRange(300, 400, random) < wiFiValue)
                                {
                                    wiFiEnabled = true;
                                    System.out.println("----> wiFiValue "+wiFiValue);
                                }
                                else
                                    wiFiEnabled = false;
                                if (getRandomRange(300, 400, random) < emergencyValue)
                                    emergencyEnabled = true;
                                else
                                    emergencyEnabled = false;
                                if (getRandomRange(300, 400, random) < vehiclesFaking)
                                    fakingEnabled = true;
                                else
                                    fakingEnabled = false;
                                tmpVehicle = new Vehicle(destinations, ((Number) vehicleLength_.getValue()).intValue(), getRandomRange(minSpeedValue, maxSpeedValue, random), getRandomRange(minCommDistValue, maxCommDistValue, random), wiFiEnabled, emergencyEnabled, getRandomRange(minBrakingValue, maxBrakingValue, random), getRandomRange(minAccelerationValue, maxAccelerationValue, random), getRandomRange(minTimeDistance, maxTimeDistance, random), getRandomRange(minPoliteness, maxPoliteness, random), (int) Math.round(speedDeviation * 100000.0 / 3600), colorPreview_.getBackground(), fakingEnabled, fakeMessagesTypes_.getSelectedItem().toString(), 0); // Bricole
                             
                                Map.getInstance().addVehicle(tmpVehicle);
                                //ClusteringControlPanel.S.setValueAt(""+tmpVehicle.getX(), i, 2);  
                                //ClusteringControlPanel.S.setValueAt(""+tmpVehicle.getY(), i, 3);
                                //ClusteringControlPanel.S.setValueAt(""+tmpVehicle.isActive(), i, 7);//isActive intialized to false
                                //ClusteringControlPanel.S.setValueAt(""+tmpVehicle.getHexID(), i, 7);
                                //System.out.println("i === "+i+"   RegionX: "+tmpVehicle.getRegionX()+"    getRegionY: "+tmpVehicle.getRegionY()+"   HexID "+tmpVehicle.getHexID()+" NumOrder ");
                                ++i;
                            } catch (Exception e) {
                            }
                        }
                        if (l > amountValue * 4) break;
                    }
                    

                    //---------------------------------------
                    
                    
                    int errorLevel = 2;
                    if (i < amountValue) errorLevel = 6;
                    ErrorLog.log(Messages.getString("EditVehicleControlPanel.createdRandomVehicles") + i + " (" + amountValue + Messages.getString("EditVehicleControlPanel.requested"), errorLevel, getClass().getName(), "actionPerformed", null);
                    VanetSimStart.setProgressBar(false);
                    Renderer.getInstance().ReRender(false, false);
                    
                    Iterator<WayPoint> wayPointIterator;//Parcours des véhicules par région EditVehicleControlPanel
                    WayPoint wayPoint;
                    Vehicle[] vehiclesArray;
                    Vehicle vehicle;
                    int Region_cnt_x = Map.getInstance().getRegionCountX();
                    int Region_cnt_y = Map.getInstance().getRegionCountY();
                    Region[][] Regions = Map.getInstance().getRegions();
                    int z=0;
                    for (i = 0; i < Region_cnt_x; ++i) {
                        for (j = 0; j < Region_cnt_y; ++j) {
                            vehiclesArray = Regions[i][j].getVehicleArray();
                            for (k = 0; k < vehiclesArray.length; ++k, z++) 
                            {
                                vehicle = vehiclesArray[k];
                                ClusteringControlPanel.S.setValueAt(""+vehicle.steadyID_, z, 1);
                                adhoc.Adhoc.S[z][1] = ""+vehicle.steadyID_;
                                adhoc.Adhoc.SRemplie = true;
                                ClusteringControlPanel.S.setValueAt(""+vehicle.getX(), z, 3);
                                adhoc.Adhoc.S[z][3] = ""+vehicle.getX();
                                ClusteringControlPanel.S.setValueAt(""+vehicle.getY(), z, 4);
                                adhoc.Adhoc.S[z][4] = ""+vehicle.getY();
                                ClusteringControlPanel.S.setValueAt(""+0, z, 8);
                                adhoc.Adhoc.S[z][8] = ""+0;
                                destinations = vehicle.getDestinations();
                                wayPointIterator = destinations.iterator();

                                while (wayPointIterator.hasNext()) {
                                    wayPoint = wayPointIterator.next();
                                    ClusteringControlPanel.S.setValueAt(""+wayPoint.getX(), z, 5);
                                    adhoc.Adhoc.S[z][5] = ""+ wayPoint.getX();
                                    ClusteringControlPanel.S.setValueAt(""+wayPoint.getY(), z, 6);
                                    adhoc.Adhoc.S[z][6] = ""+wayPoint.getY();
                                }
                                vehicle.setVehicleID(vehicle.steadyID_);
                                vehicle.setArrivalTime((int)(vanetsim.Poisson.listofdatas[z][1]*100)*TIME_PER_STEP);
                                ClusteringControlPanel.S.setValueAt(""+vehicle.getX(), z, 9);
                                adhoc.Adhoc.S[z][9] = ""+vehicle.getX();
                                ClusteringControlPanel.S.setValueAt(""+vehicle.getY(), z, 10);
                                adhoc.Adhoc.S[z][10] = ""+vehicle.getY();
                                ClusteringControlPanel.S.setValueAt(""+0, z, 11);
                                adhoc.Adhoc.S[z][11] = ""+0;
                                
                                //System.out.println("    DEBUT CREATION DES VEHICULES     ");
                                if(vanetsim.scenario.Vehicle.TV!=null) 
                                {
                                vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(z,vehicle.getX(), vehicle.getY(), vehicle.getCurLane(), vehicle.getVehicleLength(), 200,(double)vehicle.getCurSpeed()) ;
                                //System.out.println("Creation du véhicule n°: "+z+" SteadyID : "+vehicle.steadyID_+" Temps d'arrivée : "+vehicle.getArrivalTime()+" PosX : "+vehicle.getX()+" PosY : "+vehicle.getY());
                                } 
                                 //System.out.println("    FIN CREATION DES VEHICULES     "); 
                                
                            }
                            
                        }
                    } //Fin parcours du tableau de véhicules
                                
                }
            };
            new Thread(job).start();
        } else if ("comboBoxChanged".equals(command)) {
            if (((Component) e.getSource()).getName().equals("chooseVehicleType")) {
                VehicleType tmpVehicleType = (VehicleType) chooseVehicleType_.getSelectedItem();
                if (tmpVehicleType != null) {
                    maxSpeed_.setValue((int) Math.round(tmpVehicleType.getMaxSpeed() / (100000.0 / 3600)));
                    vehicleLength_.setValue(tmpVehicleType.getVehicleLength());
                    maxCommDist_.setValue((int) Math.round(tmpVehicleType.getMaxCommDist() / 100));
                    maxWait_.setValue((int) tmpVehicleType.getMaxWaittime());
                    maxBraking_.setValue((int) tmpVehicleType.getMaxBrakingRate());
                    maxAcceleration_.setValue((int) tmpVehicleType.getMaxAccelerationRate());
                    maxTimeDistance_.setValue((int) tmpVehicleType.getMaxTimeDistance());
                    maxPoliteness_.setValue((int) tmpVehicleType.getMaxPoliteness());
                    minSpeed_.setValue((int) Math.round(tmpVehicleType.getMinSpeed() / (100000.0 / 3600)));
                    minCommDist_.setValue((int) Math.round(tmpVehicleType.getMinCommDist() / 100));
                    minWait_.setValue((int) tmpVehicleType.getMinWaittime());
                    minBraking_.setValue((int) tmpVehicleType.getMinBrakingRate());
                    minAcceleration_.setValue((int) tmpVehicleType.getMinAccelerationRate());
                    minTimeDistance_.setValue((int) tmpVehicleType.getMinTimeDistance());
                    minPoliteness_.setValue((int) tmpVehicleType.getMinPoliteness());
                    vehiclesDeviatingMaxSpeed_.setValue((int) tmpVehicleType.getVehiclesDeviatingMaxSpeed_());
                    deviationFromSpeedLimit_.setValue((int) Math.round(tmpVehicleType.getDeviationFromSpeedLimit_() / (100000.0 / 3600)));
                    colorPreview_.setBackground(new Color(tmpVehicleType.getColor()));
                }
            } else if (((Component) e.getSource()).getName().equals("fakeMessagesTypes")) {
            }
        } else if ("clearVehicles".equals(command)) {
            if (JOptionPane.showConfirmDialog(null, Messages.getString("EditVehicleControlPanel.msgBoxClearAll"), "", JOptionPane.YES_NO_OPTION) == 0) {
                Map.getInstance().clearVehicles();
                Renderer.getInstance().ReRender(true, false);
            }
        } else if ("speed".equals(command) || "communication distance".equals(command) || "wait time".equals(command) || "braking rate".equals(command) || "acceleration".equals(command) || "time distance".equals(command) || "politeness".equals(command) || "vehicles deviating speed".equals(command) || "speed deviation".equals(command) || "length".equals(command) || "wifi amount".equals(command) || "emergency amount".equals(command) || "faking amount".equals(command) || "amount".equals(command)) {
            ResearchSeriesDialog.getInstance().hideResearchWindow(false, "vehicles", command);
            ResearchSeriesDialog.getInstance().setVisible(true);
        } else if ("applyToScenarioCreator".equals(command)) {
            ResearchSeriesDialog.getInstance().hideResearchWindow(false, "allVehicleProperties", "all");
            int minSpeedValue = (int) Math.round(((Number) minSpeed_.getValue()).intValue() * 100000.0 / 3600);
            int maxSpeedValue = (int) Math.round(((Number) maxSpeed_.getValue()).intValue() * 100000.0 / 3600);
            int minCommDistValue = ((Number) minCommDist_.getValue()).intValue() * 100;
            int maxCommDistValue = ((Number) maxCommDist_.getValue()).intValue() * 100;
            int minWaitValue = ((Number) minWait_.getValue()).intValue();
            int maxWaitValue = ((Number) maxWait_.getValue()).intValue();
            int minBrakingValue = ((Number) minBraking_.getValue()).intValue();
            int maxBrakingValue = ((Number) maxBraking_.getValue()).intValue();
            int minAccelerationValue = ((Number) minAcceleration_.getValue()).intValue();
            int maxAccelerationValue = ((Number) maxAcceleration_.getValue()).intValue();
            int minTimeDistance = ((Number) minTimeDistance_.getValue()).intValue();
            int maxTimeDistance = ((Number) maxTimeDistance_.getValue()).intValue();
            int minPoliteness = ((Number) minPoliteness_.getValue()).intValue();
            int maxPoliteness = ((Number) maxPoliteness_.getValue()).intValue();
            int vehiclesDeviatingMaxSpeed = ((Number) vehiclesDeviatingMaxSpeed_.getValue()).intValue();
            int deviationFromSpeedLimit = ((Number) deviationFromSpeedLimit_.getValue()).intValue();
            int wiFiValue = ((Number) wiFi_.getValue()).intValue();
            int emergencyValue = ((Number) emergencyVehicle_.getValue()).intValue();
            int speedRestriction = (int) Math.round(((Number) speedStreetRestriction_.getValue()).intValue() * 100000.0 / 3600);
            int vehiclesFaking = ((Number) fakingVehicle_.getValue()).intValue();
            if (wiFiValue < 0) {
                wiFiValue = 0;
                wiFi_.setValue(0);
            } else if (wiFiValue > 100) {
                wiFiValue = 300;
                wiFi_.setValue(300);
                System.out.println("applyToScenarioCreator : wiFiValue = "+wiFiValue);
            }
            if (emergencyValue < 0) {
                emergencyValue = 0;
                emergencyVehicle_.setValue(0);
            } else if (emergencyValue > 100) {
                emergencyValue = 100;
                emergencyVehicle_.setValue(100);
            }
            if (vehiclesFaking < 0) {
                vehiclesFaking = 0;
                fakingVehicle_.setValue(0);
            } else if (vehiclesFaking > 100) {
                vehiclesFaking = 100;
                fakingVehicle_.setValue(100);
            }
            int amountValue = ((Number) amount_.getValue()).intValue();
            ResearchSeriesDialog.getInstance().getActiveVehicleSet_().setData(((Number) vehicleLength_.getValue()).intValue(), minSpeedValue, maxSpeedValue, minCommDistValue, maxCommDistValue, minWaitValue, maxWaitValue, minBrakingValue, maxBrakingValue, minAccelerationValue, maxAccelerationValue, minTimeDistance, maxTimeDistance, minPoliteness, maxPoliteness, wiFiValue, emergencyValue, vehiclesFaking, fakeMessagesTypes_.getSelectedItem().toString(), amountValue, speedRestriction, vehiclesDeviatingMaxSpeed, deviationFromSpeedLimit, colorPreview_.getBackground());
            ResearchSeriesDialog.getInstance().setVisible(true);
            JOptionPane.showConfirmDialog(null, ""+ResearchSeriesDialog.getInstance().getActiveVehicleSet_().getAmount_());
        } else if ("openTypeDialog".equals(command)) {
            new VehicleTypeDialog();
        }
    }

    private int getRandomRange(int min, int max, Random random) {
        if (min == max)
            return min;
        else {
            if (max < min) {
                int tmp = max;
                max = min;
                min = tmp;
            }
            return (random.nextInt(max - min + 1) + min);
        }
    }

    public void refreshVehicleTypes() {
        chooseVehicleType_.removeActionListener(this);
        chooseVehicleType_.removeAllItems();
        VehicleTypeXML xml = new VehicleTypeXML(null);
        for (VehicleType type : xml.getVehicleTypes()) {
            chooseVehicleType_.addItem(type);
        }
        chooseVehicleType_.addActionListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        Color color = JColorChooser.showDialog(this, Messages.getString("EditOneVehicleControlPanel.color"), colorPreview_.getBackground());
        if (color == null)
            colorPreview_.setBackground(Color.black);
        else
            colorPreview_.setBackground(color);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public static void activateSelectPropertiesMode(boolean mode) {
        for (JButton b : buttonList_) b.setVisible(mode);
    }

    public static void activateSelectAllPropertiesMode(boolean mode) {
        createButton_.setVisible(!mode);
        deleteButton_.setVisible(!mode);
        scenarioApplyButton_.setVisible(mode);
    }
}
