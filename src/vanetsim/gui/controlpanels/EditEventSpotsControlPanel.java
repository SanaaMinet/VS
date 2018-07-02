package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.gui.helpers.WekaHelper;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
 
import vanetsim.scenario.Vehicle;
 
import vanetsim.scenario.events.EventSpot;
import vanetsim.scenario.events.EventSpotList;

public final class EditEventSpotsControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -8161612114065521616L;

    JRadioButton addItem_;

    JRadioButton deleteItem_;

    private JLabel eventSpotTypeLabel_;

    private JComboBox<String> eventSpotType_;

    private final JFormattedTextField eventSpotRadius_;

    private final JFormattedTextField eventSpotFrequency_;

    private JButton deleteAllSpots_;

    private JCheckBox schoolBox_;

    private JCheckBox kindergartenBox_;

    private JCheckBox policeBox_;

    private JCheckBox fireBox_;

    private JCheckBox hospitalBox_;

    private final JFormattedTextField gridSize_;

    private FileFilter logFileFilter_;

    private JComboBox<String> eventType_;

 
    JPanel space_;

    public EditEventSpotsControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        ButtonGroup group = new ButtonGroup();
        addItem_ = new JRadioButton(Messages.getString("EditEventSpotsControlPanel.add"));
        addItem_.setActionCommand("add");
        addItem_.addActionListener(this);
        addItem_.setSelected(true);
        group.add(addItem_);
        ++c.gridy;
        add(addItem_, c);
        deleteItem_ = new JRadioButton(Messages.getString("EditEventSpotsControlPanel.delete"));
        deleteItem_.setActionCommand("delete");
        deleteItem_.setSelected(true);
        deleteItem_.addActionListener(this);
        group.add(deleteItem_);
        ++c.gridy;
        add(deleteItem_, c);
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        eventSpotTypeLabel_ = new JLabel(Messages.getString("EditEventSpotsControlPanel.eventSpotType"));
        ++c.gridy;
        add(eventSpotTypeLabel_, c);
        eventSpotType_ = new JComboBox<String>();
        eventSpotType_.addItem(Messages.getString("EditEventSpotsControlPanel.Hospital"));
        eventSpotType_.addItem(Messages.getString("EditEventSpotsControlPanel.Ice"));
        eventSpotType_.addItem(Messages.getString("EditEventSpotsControlPanel.StreetDamage"));
        eventSpotType_.addItem(Messages.getString("EditEventSpotsControlPanel.School"));
        eventSpotType_.setName("eventSpotType");
        eventSpotType_.addActionListener(this);
        c.gridx = 1;
        add(eventSpotType_, c);
        c.gridx = 0;
        JLabel label = new JLabel(Messages.getString("EditEventSpotsControlPanel.eventSpotRadius"));
        ++c.gridy;
        add(label, c);
        eventSpotRadius_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        eventSpotRadius_.setValue(100);
        eventSpotRadius_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(eventSpotRadius_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.eventSportFrequency"));
        ++c.gridy;
        add(label, c);
        eventSpotFrequency_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        eventSpotFrequency_.setValue(10);
        eventSpotFrequency_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(eventSpotFrequency_, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.schoolBox"));
        ++c.gridy;
        add(label, c);
        schoolBox_ = new JCheckBox();
        c.gridx = 1;
        add(schoolBox_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.kindergartenBox"));
        ++c.gridy;
        add(label, c);
        kindergartenBox_ = new JCheckBox();
        c.gridx = 1;
        add(kindergartenBox_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.policeBox"));
        ++c.gridy;
        add(label, c);
        policeBox_ = new JCheckBox();
        c.gridx = 1;
        add(policeBox_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.fireBox"));
        ++c.gridy;
        add(label, c);
        fireBox_ = new JCheckBox();
        c.gridx = 1;
        add(fireBox_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.hospitalBox"));
        ++c.gridy;
        add(label, c);
        hospitalBox_ = new JCheckBox();
        c.gridx = 1;
        add(hospitalBox_, c);
        ++c.gridy;
        c.gridx = 0;
        JButton makejobs = new JButton(Messages.getString("EditEventSpotsControlPanel.importAmenities"));
        makejobs.setActionCommand("importAmenities");
        makejobs.setPreferredSize(new Dimension(200, 20));
        makejobs.addActionListener(this);
        add(makejobs, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        ++c.gridy;
        c.gridx = 0;
        JButton createEventProbabilities = new JButton(Messages.getString("EditEventSpotsControlPanel.createProbabilities"));
        createEventProbabilities.setActionCommand("createProbabilities");
        createEventProbabilities.setPreferredSize(new Dimension(200, 20));
        createEventProbabilities.addActionListener(this);
        add(createEventProbabilities, c);
        ++c.gridy;
        c.gridx = 0;
        JButton openEventProbabilities = new JButton(Messages.getString("EditEventSpotsControlPanel.openProbabilities"));
        openEventProbabilities.setActionCommand("openProbabilities");
        openEventProbabilities.setPreferredSize(new Dimension(200, 20));
        openEventProbabilities.addActionListener(this);
        add(openEventProbabilities, c);
        ++c.gridy;
        c.gridx = 0;
        JButton importCentroids = new JButton(Messages.getString("EditEventSpotsControlPanel.importCentroids"));
        importCentroids.setActionCommand("importCentroids");
        importCentroids.setPreferredSize(new Dimension(200, 20));
        importCentroids.addActionListener(this);
        add(importCentroids, c);
        ++c.gridy;
        c.gridx = 0;
        JButton importClusters = new JButton(Messages.getString("EditEventSpotsControlPanel.importClusters"));
        importClusters.setActionCommand("importClusters");
        importClusters.setPreferredSize(new Dimension(200, 20));
        importClusters.addActionListener(this);
        add(importClusters, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.selectCluster"));
        ++c.gridy;
        add(label, c);
 
        c.gridx = 1;
       
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.eventSpotGrid"));
        ++c.gridy;
        add(label, c);
        gridSize_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        gridSize_.setValue(10000);
        gridSize_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(gridSize_, c);
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditEventSpotsControlPanel.eventTypes"));
        ++c.gridy;
        add(label, c);
        eventType_ = new JComboBox<String>();
        c.gridx = 1;
        add(eventType_, c);
        ++c.gridy;
        c.gridx = 0;
        JButton loadEventProbabilities = new JButton(Messages.getString("EditEventSpotsControlPanel.refreshProbabilities"));
        loadEventProbabilities.setActionCommand("refreshProbabilities");
        loadEventProbabilities.setPreferredSize(new Dimension(200, 20));
        loadEventProbabilities.addActionListener(this);
        add(loadEventProbabilities, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        deleteAllSpots_ = ButtonCreator.getJButton("deleteAll.png", "clearSpots", Messages.getString("EditEventSpotsControlPanel.deleteAllSpots"), this);
        add(deleteAllSpots_, c);
        logFileFilter_ = new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".log");
            }

            public String getDescription() {
                return Messages.getString("LogAnalyserDialog.logFiles") + " (*.log)";
            }
        };
        c.weighty = 1.0;
        ++c.gridy;
        space_ = new JPanel();
        space_.setOpaque(false);
        add(space_, c);
    }
/*
    public void receiveMouseEvent(int x, int y) {
        if (addItem_.isSelected()) {
            EventSpotList.getInstance().addEventSpot(new EventSpot(x, y, ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, returnAmenityCode(eventSpotType_.getSelectedItem().toString()), Vehicle.getRandom().nextLong()));
        } else if (deleteItem_.isSelected()) {
            EventSpotList.getInstance().delEventSpot(EventSpotList.getInstance().findEventSpot(x, y));
        }
        Renderer.getInstance().ReRender(true, true);
    }
*/
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("clearSpots".equals(command))
            EventSpotList.getInstance().clearEvents();
        else if ("importAmenities".equals(command)) {
            for (Node n : Map.getInstance().getAmenityList()) {
                if (schoolBox_.isSelected() && n.getAmenity_().equals("school"))
                    EventSpotList.getInstance().addEventSpot(new EventSpot(n.getX(), n.getY(), ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, n.getAmenity_(), Vehicle.getRandom().nextLong()));
                else if (kindergartenBox_.isSelected() && n.getAmenity_().equals("kindergarten"))
                    EventSpotList.getInstance().addEventSpot(new EventSpot(n.getX(), n.getY(), ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, n.getAmenity_(), Vehicle.getRandom().nextLong()));
                else if (policeBox_.isSelected() && n.getAmenity_().equals("police"))
                    EventSpotList.getInstance().addEventSpot(new EventSpot(n.getX(), n.getY(), ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, n.getAmenity_(), Vehicle.getRandom().nextLong()));
                else if (hospitalBox_.isSelected() && n.getAmenity_().equals("hospital"))
                    EventSpotList.getInstance().addEventSpot(new EventSpot(n.getX(), n.getY(), ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, n.getAmenity_(), Vehicle.getRandom().nextLong()));
                else if (fireBox_.isSelected() && n.getAmenity_().equals("fire_station"))
                    EventSpotList.getInstance().addEventSpot(new EventSpot(n.getX(), n.getY(), ((Number) eventSpotFrequency_.getValue()).intValue() * 1000, ((Number) eventSpotRadius_.getValue()).intValue() * 100, n.getAmenity_(), Vehicle.getRandom().nextLong()));
            }
        } else if ("createProbabilities".equals(command)) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.addChoosableFileFilter(logFileFilter_);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(logFileFilter_);
            int status = fc.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                EventSpotList.getInstance().calculateGrid(((Number) gridSize_.getValue()).doubleValue(), fc.getSelectedFile().getAbsoluteFile().getAbsolutePath(), eventType_.getSelectedItem().toString());
            }
        } else if ("refreshProbabilities".equals(command)) {
            EventSpotList.getInstance().showGrid(eventType_.getSelectedItem().toString(), ((Number) gridSize_.getValue()).intValue());
        } else if ("openProbabilities".equals(command)) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.addChoosableFileFilter(logFileFilter_);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(logFileFilter_);
            int status = fc.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                EventSpotList.getInstance().loadGrid(fc.getSelectedFile().getAbsoluteFile().getAbsolutePath(), eventType_.getSelectedItem().toString());
            }
        } else if ("importCentroids".equals(command)) 
        {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.addChoosableFileFilter(logFileFilter_);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(logFileFilter_);
            int status = fc.showOpenDialog(this);
            //----------------
            if (status == JFileChooser.APPROVE_OPTION) 
            {
                Renderer.getInstance().setCentroidsX(WekaHelper.readWekaCentroids(fc.getSelectedFile().getAbsoluteFile().getAbsolutePath(), "X"));
                Renderer.getInstance().setCentroidsY(WekaHelper.readWekaCentroids(fc.getSelectedFile().getAbsoluteFile().getAbsolutePath(), "Y"));
            }
            
            
        } else if ("importClusters".equals(command)) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.setAcceptAllFileFilterUsed(true);
            fc.setFileFilter(logFileFilter_);
            int status = fc.showOpenDialog(this);
 
          
 
 
            }
 
        }  
        
    }

 
 
 
