package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import vanetsim.gui.Renderer;
import vanetsim.localization.Messages;
 
import vanetsim.scenario.KnownEventSource;
 
import vanetsim.scenario.KnownVehicle;
import vanetsim.scenario.Vehicle;

public class EditIDSControlPanel extends JPanel implements ListSelectionListener, ActionListener, ChangeListener, FocusListener {

    private static final long serialVersionUID = -7820554929998157630L;

    private JList<String> availableIDSRules_;

    private JList<String> selectedIDSRules_;

    private DefaultListModel<String> availableRulesModel = new DefaultListModel<String>();

    private final JSpinner beaconsLogged_;

    private DefaultListModel<String> selectedRulesModel = new DefaultListModel<String>();

    private final JCheckBox activateIDSCheckBox_;

    private final JCheckBox activateAdvancedIDSCheckBox_;

    private final JFormattedTextField fakeMessageInterval_;

    private final JFormattedTextField PCNThreshold_;

    private final JFormattedTextField PCNFORWARDThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField EVAFORWARDThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField RHCNThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField EEBLThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField EVABeaconTimeThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField EVABeaconThreshold_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField EVAMessageDelay_ = new JFormattedTextField();

    private static ArrayList<JButton> buttonList_ = new ArrayList<JButton>();

    private final JCheckBox spamDetectionCheckBox_;

    private final JFormattedTextField spamMessageAmountThreshold_;

    private final JFormattedTextField spamTimeThreshold_;

    JButton locationInformationMode_;

    FileFilter logFileFilter_;

    public EditIDSControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        add(new JLabel(Messages.getString("EditIDSControlPanel.availableIDSRules")), c);
        ++c.gridy;
        availableIDSRules_ = new JList<String>(availableRulesModel);
 
        availableIDSRules_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableIDSRules_.removeListSelectionListener(this);
        availableIDSRules_.addListSelectionListener(this);
        add(availableIDSRules_, c);
        ++c.gridy;
        ++c.gridy;
        add(new JLabel(Messages.getString("EditIDSControlPanel.selectedIDSRules")), c);
        ++c.gridy;
        selectedIDSRules_ = new JList<String>(selectedRulesModel);
        selectedRulesModel.add(0, "-");
        selectedIDSRules_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedIDSRules_.removeListSelectionListener(this);
        selectedIDSRules_.addListSelectionListener(this);
        add(selectedIDSRules_, c);
        c.gridwidth = 1;
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("EditIDSControlPanel.activateIDS")), c);
        activateIDSCheckBox_ = new JCheckBox();
        activateIDSCheckBox_.setSelected(false);
        activateIDSCheckBox_.setActionCommand("activateIDS");
        c.gridx = 1;
        add(activateIDSCheckBox_, c);
        activateIDSCheckBox_.addActionListener(this);
        c.gridwidth = 1;
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("EditIDSControlPanel.activateAdvancedIDS")), c);
        activateAdvancedIDSCheckBox_ = new JCheckBox();
        activateAdvancedIDSCheckBox_.setSelected(false);
        activateAdvancedIDSCheckBox_.setActionCommand("activateAdvancedIDS");
        c.gridx = 1;
        add(activateAdvancedIDSCheckBox_, c);
        activateAdvancedIDSCheckBox_.addActionListener(this);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        ++c.gridy;
        c.gridwidth = 1;
        c.gridx = 0;
        JLabel label = new JLabel(Messages.getString("EditIDSControlPanel.fakeMessagesInterval"));
        ++c.gridy;
        add(label, c);
        fakeMessageInterval_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        fakeMessageInterval_.setPreferredSize(new Dimension(60, 20));
        fakeMessageInterval_.setValue(10000);
        fakeMessageInterval_.addFocusListener(this);
        c.gridx = 1;
        add(fakeMessageInterval_, c);
        c.gridx = 2;
        JButton button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("fake message interval");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        ++c.gridy;
        c.gridwidth = 1;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.PCNThreshold"));
        ++c.gridy;
        add(label, c);
        PCNThreshold_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        PCNThreshold_.setPreferredSize(new Dimension(60, 20));
        PCNThreshold_.setValue(625);
        PCNThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(PCNThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("PCN threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.PCNFORWARDThreshold"));
        ++c.gridy;
        add(label, c);
        PCNFORWARDThreshold_.setPreferredSize(new Dimension(60, 20));
        PCNFORWARDThreshold_.setValue(0.5);
        PCNFORWARDThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(PCNFORWARDThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("PCN forward threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.EVAFORWARDThreshold"));
        ++c.gridy;
        add(label, c);
        EVAFORWARDThreshold_.setPreferredSize(new Dimension(60, 20));
        EVAFORWARDThreshold_.setValue(500);
        EVAFORWARDThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(EVAFORWARDThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("EVA forward threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.RHCNThreshold"));
        ++c.gridy;
        add(label, c);
        RHCNThreshold_.setPreferredSize(new Dimension(60, 20));
        RHCNThreshold_.setValue(0.5);
        RHCNThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(RHCNThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("RHCN threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.EEBLThreshold"));
        ++c.gridy;
        add(label, c);
        EEBLThreshold_.setPreferredSize(new Dimension(60, 20));
        EEBLThreshold_.setValue(0.5);
        EEBLThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(EEBLThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("EEBL threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.EVABeaconTimeThreshold"));
        ++c.gridy;
        add(label, c);
        EVABeaconTimeThreshold_.setPreferredSize(new Dimension(60, 20));
        EVABeaconTimeThreshold_.setValue(3);
        EVABeaconTimeThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(EVABeaconTimeThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("EVA Beacon time");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.EVABeaconThreshold"));
        ++c.gridy;
        add(label, c);
        EVABeaconThreshold_.setPreferredSize(new Dimension(60, 20));
        EVABeaconThreshold_.setValue(0.5);
        EVABeaconThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(EVABeaconThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("EVA Beacon threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.EVAMessageDelay"));
        ++c.gridy;
        add(label, c);
        EVAMessageDelay_.setPreferredSize(new Dimension(60, 20));
        EVAMessageDelay_.setValue(3);
        EVAMessageDelay_.addFocusListener(this);
        c.gridx = 1;
        add(EVAMessageDelay_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("EVA Message Delay");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 1;
        label = new JLabel(Messages.getString("EditIDSControlPanel.beaconsLogged"));
        ++c.gridy;
        add(label, c);
        beaconsLogged_ = new JSpinner();
        beaconsLogged_.setValue(10);
        beaconsLogged_.addChangeListener(this);
        c.gridx = 1;
        add(beaconsLogged_, c);
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("Beacon amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("EditIDSControlPanel.activateSpamDetection")), c);
        spamDetectionCheckBox_ = new JCheckBox();
        spamDetectionCheckBox_.setSelected(false);
        spamDetectionCheckBox_.setActionCommand("activateSpamDetection");
        c.gridx = 1;
        add(spamDetectionCheckBox_, c);
        spamDetectionCheckBox_.addActionListener(this);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.spamTimeThreshold"));
        add(label, c);
        spamTimeThreshold_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        spamTimeThreshold_.setPreferredSize(new Dimension(60, 20));
        spamTimeThreshold_.setValue(12000);
        spamTimeThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(spamTimeThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("spam time threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridx = 0;
        label = new JLabel(Messages.getString("EditIDSControlPanel.spamMessageAmountThreshold"));
        add(label, c);
        spamMessageAmountThreshold_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        spamMessageAmountThreshold_.setPreferredSize(new Dimension(60, 20));
        spamMessageAmountThreshold_.setValue(3);
        spamMessageAmountThreshold_.addFocusListener(this);
        c.gridx = 1;
        add(spamMessageAmountThreshold_, c);
        c.gridx = 2;
        button = new JButton(Messages.getString("EditVehicleControlPanel.selectPropertyButton"));
        button.setActionCommand("spam message threshold");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(new JSeparator(SwingConstants.HORIZONTAL), c);
        ++c.gridy;
        c.gridx = 0;
        JButton locationInformation = new JButton("Load location data");
        locationInformation.setActionCommand("locationdata");
        locationInformation.setPreferredSize(new Dimension(200, 20));
        locationInformation.addActionListener(this);
        add(locationInformation, c);
        ++c.gridy;
        c.gridx = 0;
        locationInformationMode_ = new JButton("Switch to TN/FP");
        locationInformationMode_.setActionCommand("locationdatamode");
        locationInformationMode_.setPreferredSize(new Dimension(200, 20));
        locationInformationMode_.addActionListener(this);
        add(locationInformationMode_, c);
        ++c.gridy;
        c.gridx = 0;
        JButton reset = new JButton("Reset");
        reset.setActionCommand("reset");
        reset.setPreferredSize(new Dimension(200, 20));
        reset.addActionListener(this);
        add(reset, c);
        ++c.gridy;
        c.gridx = 0;
        JButton save = new JButton("Save to file");
        save.setActionCommand("save");
        save.setPreferredSize(new Dimension(200, 20));
        save.addActionListener(this);
        add(save, c);
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
        c.gridy = c.gridy + 100;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        @SuppressWarnings("unchecked") JList<Object> list = (JList<Object>) arg0.getSource();
        if (!list.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
            if (list.getModel().equals(availableRulesModel)) {
                updateList("available", availableRulesModel.get(list.getSelectedIndex()).toString());
            } else if (list.getModel().equals(selectedRulesModel)) {
                updateList("selected", selectedRulesModel.get(list.getSelectedIndex()).toString());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String command = arg0.getActionCommand();
        if ("activateIDS".equals(command)) {
            Vehicle.setIdsActivated(activateIDSCheckBox_.isSelected());
        } else if ("logIDS".equals(command)) {
        }   if ("fake message interval".equals(command) || "PCN threshold".equals(command) || "PCN forward threshold".equals(command) || "EVA forward threshold".equals(command) || "RHCN threshold".equals(command) || "EEBL threshold".equals(command) || "EVA Beacon time".equals(command) || "EVA Beacon threshold".equals(command) || "Beacon amount".equals(command) || "spam time threshold".equals(command) || "spam message threshold".equals(command) || "EVA Message Delay".equals(command)) {
            ResearchSeriesDialog.getInstance().hideResearchWindow(false, "generalSettings", command);
            ResearchSeriesDialog.getInstance().setVisible(true);
        } else if ("activateSpamDetection".equals(command)) {
            if (spamDetectionCheckBox_.isSelected()) {
                 
                KnownEventSource.setSpamCheck_(true);
            } else {
                
                KnownEventSource.setSpamCheck_(false);
            }
        } else if ("locationdata".equals(command)) {
            showAdvancedLocationInformation();
        } else if ("locationdatamode".equals(command)) {
            if (Renderer.getInstance().getMDSMode_())
                locationInformationMode_.setText("Switch to TP/FN");
            else
                locationInformationMode_.setText("Switch to TN/FP");
            Renderer.getInstance().setMDSMode_(!Renderer.getInstance().getMDSMode_());
            Renderer.getInstance().ReRender(true, true);
        } else if ("reset".equals(command)) {
            Renderer.getInstance().setLocationInformationMDS_(null);
            Renderer.getInstance().ReRender(true, true);
        }
    }

    public void updateList(String list, String value) {
        if (list.equals("available")) {
            selectedRulesModel.addElement(value);
            availableRulesModel.removeElement(value);
        } else if (list.equals("selected")) {
            availableRulesModel.addElement(value);
            selectedRulesModel.removeElement(value);
        }
        if (selectedRulesModel.getSize() == 0)
            selectedRulesModel.addElement("-");
        else
            selectedRulesModel.removeElement("-");
        if (availableRulesModel.getSize() == 0)
            availableRulesModel.addElement("-");
        else
            availableRulesModel.removeElement("-");
        Object[] tmpArray2 = selectedRulesModel.toArray();
        String[] tmpArray = new String[tmpArray2.length];
        for (int i = 0; i < tmpArray2.length; i++) {
            tmpArray[i] = tmpArray2[i].toString();
        }
        
    }

 

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().getClass().equals(beaconsLogged_.getClass())) {
            if (((Number) beaconsLogged_.getValue()).intValue() < 1)
                KnownVehicle.setAmountOfSavedBeacons(-1);
            else
                KnownVehicle.setAmountOfSavedBeacons(((Number) beaconsLogged_.getValue()).intValue());
        }
    }

    public void saveAttributes() {
        if (fakeMessageInterval_.getValue() != null)
            Vehicle.setFakeMessagesInterval_(((Number) fakeMessageInterval_.getValue()).intValue());
 
  
        if (spamMessageAmountThreshold_.getValue() != null)
            KnownEventSource.setSpammingThreshold_(((Number) spamMessageAmountThreshold_.getValue()).intValue());
        if (spamTimeThreshold_.getValue() != null)
            KnownEventSource.setSpammingTimeThreshold_(((Number) spamTimeThreshold_.getValue()).intValue());
    }

    public void showAdvancedLocationInformation() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(logFileFilter_);
        int status = fc.showDialog(this, Messages.getString("EditLogControlPanel.approveButton"));
        ArrayList<String> locationInformation = new ArrayList<String>();
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile().getAbsoluteFile();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    locationInformation.add(line);
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
            Renderer.getInstance().setLocationInformationMDS_(locationInformation);
            Renderer.getInstance().ReRender(true, true);
        }
    }

    public JSpinner getBeaconsLogged_() {
        return beaconsLogged_;
    }

    @Override
    public void focusGained(FocusEvent arg0) {
        saveAttributes();
    }

    @Override
    public void focusLost(FocusEvent arg0) {
        saveAttributes();
    }

    public JFormattedTextField getFakeMessageInterval_() {
        return fakeMessageInterval_;
    }

    public JCheckBox getActivateIDSCheckBox_() {
        return activateIDSCheckBox_;
    }

    public JFormattedTextField getPCNThreshold_() {
        return PCNThreshold_;
    }

    public JFormattedTextField getRHCNThreshold_() {
        return RHCNThreshold_;
    }

    public JFormattedTextField getEEBLThreshold_() {
        return EEBLThreshold_;
    }

    public JFormattedTextField getPCNFORWARDThreshold_() {
        return PCNFORWARDThreshold_;
    }

    public JFormattedTextField getEVAFORWARDThreshold_() {
        return EVAFORWARDThreshold_;
    }

    public JFormattedTextField getEVABeaconTimeThreshold_() {
        return EVABeaconTimeThreshold_;
    }

    public JFormattedTextField getEVABeaconThreshold_() {
        return EVABeaconThreshold_;
    }

    public JCheckBox getActivateAdvancedIDSCheckBox_() {
        return activateAdvancedIDSCheckBox_;
    }

    public static void activateSelectPropertiesMode(boolean mode) {
        for (JButton b : buttonList_) b.setVisible(mode);
    }

    public JCheckBox getSpamDetectionCheckBox_() {
        return spamDetectionCheckBox_;
    }

    public JFormattedTextField getSpamMessageAmountThreshold_() {
        return spamMessageAmountThreshold_;
    }

    public JFormattedTextField getSpamTimeThreshold_() {
        return spamTimeThreshold_;
    }

    public JFormattedTextField getEVAMessageDelay_() {
        return EVAMessageDelay_;
    }
}
