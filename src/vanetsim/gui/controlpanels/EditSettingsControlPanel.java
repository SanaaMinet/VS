package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vanetsim.gui.helpers.ReRenderManager;
import vanetsim.gui.helpers.TextAreaLabel;
import vanetsim.localization.Messages;
import vanetsim.scenario.Vehicle;
 
import vanetsim.simulation.SimulationMaster;

public class EditSettingsControlPanel extends JPanel implements ItemListener, PropertyChangeListener {

    private static final long serialVersionUID = -7820554929526157630L;

    private final JComboBox<String> routingModeChoice_;

    private final JCheckBox recyclingCheckBox_;

    private final JCheckBox communicationCheckBox_;

    private final JPanel communicationPanel_;

    private JFormattedTextField communicationInterval_;

    private JPanel beaconPanel_;

    private JCheckBox beaconsCheckBox_;

    private JFormattedTextField beaconInterval_;

    private JCheckBox globalInfrastructureCheckBox_;

    //private JCheckBox mixZonesCheckBox_;

    //private JPanel mixZonePanel_;

    //private JCheckBox fallbackInMixZonesCheckBox_;

    //private JPanel fallbackInMixZonesPanel_;

    //private JCheckBox fallbackInMixZonesFloodingOnlyCheckBox_;

    //private JFormattedTextField mixZoneRadius_;

    public EditSettingsControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        JLabel jLabel1 = new JLabel(Messages.getString("EditSettingsControlPanel.routingBasedOn"));
        add(jLabel1, c);
        c.gridx = 1;
        c.weightx = 0;
        String[] choices = { Messages.getString("EditSettingsControlPanel.distance"), Messages.getString("EditSettingsControlPanel.time") };
        routingModeChoice_ = new JComboBox<String>(choices);
        routingModeChoice_.setSelectedIndex(1);
        routingModeChoice_.addItemListener(this);
        add(routingModeChoice_, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        c.insets = new Insets(0, 5, 5, 5);
        jLabel1 = new JLabel(Messages.getString("EditSettingsControlPanel.routingNote"));
        add(jLabel1, c);
        c.insets = new Insets(5, 5, 5, 5);
        ++c.gridy;
        recyclingCheckBox_ = new JCheckBox(Messages.getString("EditSettingsControlPanel.enableRecycling"), true);
        recyclingCheckBox_.setSelected(true);
        recyclingCheckBox_.addItemListener(this);
        add(recyclingCheckBox_, c);
        communicationCheckBox_ = new JCheckBox(Messages.getString("EditSettingsControlPanel.enableCommunication"), true);
        communicationCheckBox_.addItemListener(this);
        ++c.gridy;
        add(communicationCheckBox_, c);
        ++c.gridy;
        c.insets = new Insets(0, 10, 0, 5);
        communicationPanel_ = createCommunicationPanel();
        add(communicationPanel_, c);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel, c);
    }

    private final JPanel createCommunicationPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 0, 5, 0);
        JLabel jLabel1 = new JLabel(Messages.getString("EditSettingsControlPanel.communicationInterval"));
        panel.add(jLabel1, c);
        communicationInterval_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        communicationInterval_.setPreferredSize(new Dimension(60, 20));
        communicationInterval_.setValue(160);
        communicationInterval_.addPropertyChangeListener("value", this);
        c.gridx = 1;
        c.weightx = 0;
        panel.add(communicationInterval_, c);
        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        beaconsCheckBox_ = new JCheckBox(Messages.getString("EditSettingsControlPanel.enableBeacons"), true);
        beaconsCheckBox_.addItemListener(this);
        panel.add(beaconsCheckBox_, c);
        ++c.gridy;
        c.insets = new Insets(0, 10, 0, 0);
        beaconPanel_ = createBeaconPanel();
        panel.add(beaconPanel_, c);
        ++c.gridy;
        c.insets = new Insets(5, 0, 5, 0);
        
        TextAreaLabel jlabel1 = new TextAreaLabel(Messages.getString("EditSettingsControlPanel.intervalNote1") + SimulationMaster.TIME_PER_STEP + Messages.getString("EditSettingsControlPanel.intervalNote2"));
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(15, 0, 5, 0);
        panel.add(jlabel1, c);
        return panel;
    }

    private final JPanel createBeaconPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 0, 5, 0);
        JLabel jLabel1 = new JLabel(Messages.getString("EditSettingsControlPanel.beaconInterval"));
        panel.add(jLabel1, c);
        beaconInterval_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        beaconInterval_.setPreferredSize(new Dimension(60, 20));
        beaconInterval_.setValue(240);
        beaconInterval_.addPropertyChangeListener("value", this);
        c.gridx = 1;
        c.weightx = 0;
        panel.add(beaconInterval_, c);
        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        globalInfrastructureCheckBox_ = new JCheckBox(Messages.getString("EditSettingsControlPanel.enableInfrastructure"), true);
        globalInfrastructureCheckBox_.addItemListener(this);
        return panel;
    }

    private final JPanel createMixPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 0, 5, 0);
        JLabel jLabel1 = new JLabel(Messages.getString("EditSettingsControlPanel.mixZoneSize"));
        panel.add(jLabel1, c);

        c.gridx = 1;
        c.weightx = 0;

        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;

        return panel;
    }

    private final JPanel createMixFallBackPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 10, 5, 0);
      
        return panel;
    }

    public void setCommunication(boolean state) {
        communicationPanel_.setVisible(state);
        communicationCheckBox_.setSelected(state);
    }

    public void setBeacons(boolean state) {
        beaconPanel_.setVisible(state);
        beaconsCheckBox_.setSelected(state);
    }






    public void setRecyclingEnabled(boolean state) {
        recyclingCheckBox_.setSelected(state);
    }

    public void setGlobalInfrastructure(boolean state) {
        globalInfrastructureCheckBox_.setSelected(state);
    }

    public void setCommunicationInterval(int communicationInterval) {
        communicationInterval_.setValue(communicationInterval);
    }

    public void setBeaconInterval(int beaconInterval) {
        beaconInterval_.setValue(beaconInterval);
    }



    public void setRoutingMode(int mode) {
        routingModeChoice_.setSelectedIndex(mode);
    }

    public void itemStateChanged(ItemEvent e) {
        boolean state;
        if (e.getStateChange() == ItemEvent.SELECTED)
            state = true;
        else
            state = false;
        Object source = e.getSource();
        if (source == communicationCheckBox_) {
            setCommunication(state);
            Vehicle.setCommunicationEnabled(state);
             
        } else if (source == beaconsCheckBox_) {
            setBeacons(state);
            Vehicle.setBeaconsEnabled(state);
            
        } else if (source == globalInfrastructureCheckBox_) {
            setGlobalInfrastructure(state);
        } else if (source == routingModeChoice_) {
            Vehicle.setRoutingMode(routingModeChoice_.getSelectedIndex());
        } else if (source == recyclingCheckBox_) {
            Vehicle.setRecyclingEnabled(state);
        } 
    }

    public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == communicationInterval_) {
            Vehicle.setCommunicationInterval(((Number) communicationInterval_.getValue()).intValue());
            
        } else if (source == beaconInterval_) {
            Vehicle.setBeaconInterval(((Number) beaconInterval_.getValue()).intValue());
         
        }
    }
}
