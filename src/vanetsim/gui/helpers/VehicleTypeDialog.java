package vanetsim.gui.helpers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import vanetsim.VanetSimStart;
import vanetsim.localization.Messages;

public final class VehicleTypeDialog extends JDialog implements ActionListener, MouseListener {

    private static final long serialVersionUID = -2918735209479587896L;

    private JComboBox<VehicleType> chooseVehicleType_ = new JComboBox<VehicleType>();

    private final JFormattedTextField filePath_ = new JFormattedTextField();

    private final JFormattedTextField vehicleLength_ = new JFormattedTextField();

    private final JFormattedTextField minSpeed_ = new JFormattedTextField();

    private final JFormattedTextField maxSpeed_ = new JFormattedTextField();

    private final JFormattedTextField minCommDist_ = new JFormattedTextField();

    private final JFormattedTextField maxCommDist_ = new JFormattedTextField();

    private final JFormattedTextField minWait_ = new JFormattedTextField();

    private final JFormattedTextField maxWait_ = new JFormattedTextField();

    private final JFormattedTextField minBraking_ = new JFormattedTextField();

    private final JFormattedTextField maxBraking_ = new JFormattedTextField();

    private final JFormattedTextField minAcceleration_ = new JFormattedTextField();

    private final JFormattedTextField maxAcceleration_ = new JFormattedTextField();

    private final JFormattedTextField minTimeDistance_ = new JFormattedTextField();

    private final JFormattedTextField maxTimeDistance_ = new JFormattedTextField();

    private final JFormattedTextField minPoliteness_ = new JFormattedTextField();

    private final JFormattedTextField maxPoliteness_ = new JFormattedTextField();

    private final JFormattedTextField vehiclesDeviatingMaxSpeed_ = new JFormattedTextField();

    private final JFormattedTextField deviationFromSpeedLimit_ = new JFormattedTextField();

    private final JCheckBox wifi_;

    private final JCheckBox emergencyVehicle_;

    private final JPanel colorPreview_;

    private VehicleType prevItem_ = null;

    private FileFilter xmlFileFilter_;

    public VehicleTypeDialog() {
        setDefaultLookAndFeelDecorated(true);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                closeDialog();
            }
        });
        setModal(true);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        ++c.gridy;
        filePath_.setEditable(false);
        filePath_.setPreferredSize(new Dimension(100, 20));
        add(filePath_, c);
        c.gridx = 1;
        JPanel OpenFilePanel = new JPanel();
        add(OpenFilePanel, c);
        OpenFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton btnOpen_ = new JButton(Messages.getString("VehicleTypeDialog.btnOpen"));
        btnOpen_.setActionCommand("openFile");
        btnOpen_.setPreferredSize(new Dimension(30, 20));
        btnOpen_.addActionListener(this);
        OpenFilePanel.add(btnOpen_);
        JButton btnStandard_ = new JButton(Messages.getString("VehicleTypeDialog.btnMakeStandard"));
        btnStandard_.setActionCommand("makeStandard");
        btnStandard_.setPreferredSize(new Dimension(200, 20));
        btnStandard_.addActionListener(this);
        OpenFilePanel.add(btnStandard_);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.selectType")), c);
        c.gridx = 1;
        add(chooseVehicleType_, c);
        c.gridx = 0;
        VehicleTypeXML xml = new VehicleTypeXML(null);
        for (VehicleType type : xml.getVehicleTypes()) {
            chooseVehicleType_.addItem(type);
        }
        chooseVehicleType_.addActionListener(this);
        filePath_.setValue(xml.getDefaultPath());
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minSpeed")), c);
        c.gridx = 1;
        add(minSpeed_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxSpeed")), c);
        c.gridx = 1;
        add(maxSpeed_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minCommDist")), c);
        c.gridx = 1;
        add(minCommDist_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxCommDist")), c);
        c.gridx = 1;
        add(maxCommDist_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minWait")), c);
        c.gridx = 1;
        add(minWait_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxWait")), c);
        c.gridx = 1;
        add(maxWait_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minBraking")), c);
        c.gridx = 1;
        add(minBraking_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxBraking")), c);
        c.gridx = 1;
        add(maxBraking_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minAcceleration")), c);
        c.gridx = 1;
        add(minAcceleration_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxAcceleration")), c);
        c.gridx = 1;
        add(maxAcceleration_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minTimeDistance")), c);
        c.gridx = 1;
        add(minTimeDistance_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxTimeDistance")), c);
        c.gridx = 1;
        add(maxTimeDistance_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.minPoliteness")), c);
        c.gridx = 1;
        add(minPoliteness_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.maxPoliteness")), c);
        c.gridx = 1;
        add(maxPoliteness_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("EditVehicleControlPanel.vehiclesDeviatingMaxSpeed")), c);
        c.gridx = 1;
        add(vehiclesDeviatingMaxSpeed_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("EditVehicleControlPanel.deviationFromSpeedLimit")), c);
        c.gridx = 1;
        add(deviationFromSpeedLimit_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.vehicleLength")), c);
        c.gridx = 1;
        add(vehicleLength_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.wifi")), c);
        wifi_ = new JCheckBox();
        c.gridx = 1;
        add(wifi_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.emergencyVehicle")), c);
        emergencyVehicle_ = new JCheckBox();
        c.gridx = 1;
        add(emergencyVehicle_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("VehicleTypeDialog.color")), c);
        colorPreview_ = new JPanel();
        colorPreview_.setBackground(Color.black);
        colorPreview_.setSize(10, 10);
        colorPreview_.addMouseListener(this);
        c.gridx = 1;
        add(colorPreview_, c);
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("VehicleTypeDialog.TypeOptions")), c);
        c.gridx = 1;
        JPanel TypePanel = new JPanel();
        add(TypePanel, c);
        TypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton btnNewType_ = new JButton(Messages.getString("VehicleTypeDialog.btnNewType"));
        btnNewType_.setActionCommand("newType");
        btnNewType_.setPreferredSize(new Dimension(80, 20));
        btnNewType_.addActionListener(this);
        TypePanel.add(btnNewType_);
        JButton btnDeleteType_ = new JButton(Messages.getString("VehicleTypeDialog.btnDeleteType"));
        btnDeleteType_.setActionCommand("deleteType");
        btnDeleteType_.setPreferredSize(new Dimension(80, 20));
        btnDeleteType_.addActionListener(this);
        TypePanel.add(btnDeleteType_);
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("VehicleTypeDialog.FileOptions")), c);
        c.gridx = 1;
        JPanel FilePanel = new JPanel();
        add(FilePanel, c);
        FilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton btnNewFile_ = new JButton(Messages.getString("VehicleTypeDialog.btnNewFile"));
        btnNewFile_.setActionCommand("newFile");
        btnNewFile_.setPreferredSize(new Dimension(80, 20));
        btnNewFile_.addActionListener(this);
        FilePanel.add(btnNewFile_);
        JButton btnSaveFile_ = new JButton(Messages.getString("VehicleTypeDialog.btnSaveFile"));
        btnSaveFile_.setActionCommand("saveFile");
        btnSaveFile_.setPreferredSize(new Dimension(80, 20));
        btnSaveFile_.addActionListener(this);
        FilePanel.add(btnSaveFile_);
        JButton btnDeleteFile_ = new JButton(Messages.getString("VehicleTypeDialog.btnDeleteFile"));
        btnDeleteFile_.setActionCommand("deleteFile");
        btnDeleteFile_.setPreferredSize(new Dimension(80, 20));
        btnDeleteFile_.addActionListener(this);
        FilePanel.add(btnDeleteFile_);
        actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
        xmlFileFilter_ = new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".xml");
            }

            public String getDescription() {
                return Messages.getString("MainControlPanel.xmlFiles") + " (*.xml)";
            }
        };
        c.weighty = 1.0;
        ++c.gridy;
        add(new JPanel(), c);
        pack();
        setLocationRelativeTo(VanetSimStart.getMainFrame());
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("comboBoxChanged".equals(command)) {
            if (prevItem_ != null)
                saveType(prevItem_);
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
                wifi_.setSelected(tmpVehicleType.isWifi());
                emergencyVehicle_.setSelected(tmpVehicleType.isEmergencyVehicle());
                colorPreview_.setBackground(new Color(tmpVehicleType.getColor()));
                deviationFromSpeedLimit_.setValue((int) Math.round(tmpVehicleType.getDeviationFromSpeedLimit_() / (100000.0 / 3600)));
                vehiclesDeviatingMaxSpeed_.setValue((int) tmpVehicleType.getVehiclesDeviatingMaxSpeed_());
                pack();
                prevItem_ = tmpVehicleType;
            }
        } else if (("openFile").equals(command)) {
            saveQuestions();
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.addChoosableFileFilter(xmlFileFilter_);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(xmlFileFilter_);
            int status = fc.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                String tmpPath = filePath_.getValue().toString();
                filePath_.setValue(fc.getSelectedFile().getAbsoluteFile());
                VehicleTypeXML xml = new VehicleTypeXML(filePath_.getValue().toString());
                if (xml.getVehicleTypes().size() == 0) {
                    filePath_.setValue(tmpPath);
                    JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.loadFileError"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    openFile(filePath_.getValue().toString());
                }
            }
        } else if ("newFile".equals(command)) {
            saveQuestions();
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.addChoosableFileFilter(xmlFileFilter_);
            fc.setFileFilter(xmlFileFilter_);
            int status = fc.showDialog(this, "Create");
            if (status == JFileChooser.APPROVE_OPTION) {
                if (!fc.getSelectedFile().getAbsoluteFile().toString().equals("vehicleTypes.xml") && !fc.getSelectedFile().getAbsoluteFile().toString().equals(System.getProperty("user.dir") + "/vehicleTypes.xml")) {
                    if (fc.getSelectedFile().getAbsoluteFile().toString().toLowerCase().endsWith(".xml"))
                        filePath_.setValue(fc.getSelectedFile().getAbsoluteFile());
                    else
                        filePath_.setValue(fc.getSelectedFile().getAbsoluteFile() + ".xml");
                    VehicleTypeXML xml = new VehicleTypeXML(filePath_.getValue().toString());
                    ArrayList<VehicleType> tmpList = new ArrayList<VehicleType>();
                    String typeName = "";
                    while (typeName.equals("")) {
                        typeName = JOptionPane.showInputDialog(Messages.getString("VehicleTypeDialog.inputTypeName"));
                    }
                    tmpList.add(new VehicleType(typeName, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, -16777216));
                    xml.saveVehicleTypes(tmpList);
                    chooseVehicleType_.removeActionListener(this);
                    chooseVehicleType_.removeAllItems();
                    chooseVehicleType_.addActionListener(this);
                    for (VehicleType type : xml.getVehicleTypes()) {
                        chooseVehicleType_.addItem(type);
                    }
                    actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
                } else
                    JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.deleteVehicleTypesFile"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("saveFile".equals(command)) {
            if (chooseVehicleType_.getSelectedItem() != null)
                saveType((VehicleType) chooseVehicleType_.getSelectedItem());
            if (chooseVehicleType_.getItemCount() > 0) {
                VehicleTypeXML xml = new VehicleTypeXML(filePath_.getValue().toString());
                ArrayList<VehicleType> tmpList = new ArrayList<VehicleType>();
                for (int i = 0; i < chooseVehicleType_.getItemCount(); i++) tmpList.add((VehicleType) chooseVehicleType_.getItemAt(i));
                xml.saveVehicleTypes(tmpList);
                JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.saveFileSuccess"), "Information", JOptionPane.INFORMATION_MESSAGE);
                if ((filePath_.getValue().toString().equals("vehicleTypes.xml") || filePath_.getValue().toString().equals(System.getProperty("user.dir") + "/vehicleTypes.xml"))) {
                    VanetSimStart.getMainControlPanel().getEditPanel().getEditOneVehiclePanel().refreshVehicleTypes();
                    VanetSimStart.getMainControlPanel().getEditPanel().getEditVehiclePanel().refreshVehicleTypes();
                }
            }
        } else if ("deleteFile".equals(command)) {
            if (!filePath_.getValue().toString().equals("vehicleTypes.xml") && !filePath_.getValue().toString().equals(System.getProperty("user.dir") + "/vehicleTypes.xml")) {
                if (JOptionPane.showConfirmDialog(null, Messages.getString("VehicleTypeDialog.deleteFile"), "", JOptionPane.YES_NO_OPTION) == 0) {
                    boolean success = (new File(filePath_.getValue().toString())).delete();
                    if (success) {
                        JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.deleteSuccess"), "Information", JOptionPane.INFORMATION_MESSAGE);
                        filePath_.setValue("");
                        clearGui();
                    } else
                        JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.deleteError"), "Error", JOptionPane.ERROR_MESSAGE);
                    prevItem_ = null;
                    openFile("vehicleTypes.xml");
                }
            } else
                JOptionPane.showMessageDialog(null, Messages.getString("VehicleTypeDialog.deleteVehicleTypesFile"), "Error", JOptionPane.ERROR_MESSAGE);
        } else if ("newType".equals(command)) {
            removeEmptyTypes();
            String typeName = "";
            while (typeName != null && typeName.equals("")) {
                typeName = JOptionPane.showInputDialog(Messages.getString("VehicleTypeDialog.inputTypeName"));
            }
            chooseVehicleType_.addItem(new VehicleType(typeName, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, -16777216));
            chooseVehicleType_.setSelectedIndex(chooseVehicleType_.getItemCount() - 1);
        } else if ("deleteType".equals(command)) {
            if (JOptionPane.showConfirmDialog(null, Messages.getString("VehicleTypeDialog.deleteType"), "", JOptionPane.YES_NO_OPTION) == 0) {
                chooseVehicleType_.removeActionListener(this);
                chooseVehicleType_.removeItemAt(chooseVehicleType_.getSelectedIndex());
                chooseVehicleType_.addActionListener(this);
                if (chooseVehicleType_.getItemCount() > 0) {
                    chooseVehicleType_.setSelectedIndex(0);
                    actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
                } else
                    clearGui();
            }
        } else if ("makeStandard".equals(command)) {
            if (JOptionPane.showConfirmDialog(null, Messages.getString("VehicleTypeDialog.makeStandardBox"), "", JOptionPane.YES_NO_OPTION) == 0) {
                VehicleTypeXML xml = new VehicleTypeXML("vehicleTypes.xml");
                ArrayList<VehicleType> tmpList = new ArrayList<VehicleType>();
                for (int i = 0; i < chooseVehicleType_.getItemCount(); i++) tmpList.add((VehicleType) chooseVehicleType_.getItemAt(i));
                xml.saveVehicleTypes(tmpList);
                VanetSimStart.getMainControlPanel().getEditPanel().getEditOneVehiclePanel().refreshVehicleTypes();
                VanetSimStart.getMainControlPanel().getEditPanel().getEditVehiclePanel().refreshVehicleTypes();
            }
        }
    }

    public void clearGui() {
        chooseVehicleType_.removeActionListener(this);
        chooseVehicleType_.removeAllItems();
        chooseVehicleType_.addActionListener(this);
        maxSpeed_.setValue(0);
        vehicleLength_.setValue(0);
        maxCommDist_.setValue(0);
        maxWait_.setValue(0);
        maxBraking_.setValue(0);
        maxAcceleration_.setValue(0);
        maxTimeDistance_.setValue(0);
        maxPoliteness_.setValue(0);
        minSpeed_.setValue(0);
        minCommDist_.setValue(0);
        minWait_.setValue(0);
        minBraking_.setValue(0);
        minAcceleration_.setValue(0);
        minTimeDistance_.setValue(0);
        minPoliteness_.setValue(0);
        vehiclesDeviatingMaxSpeed_.setValue(0);
        deviationFromSpeedLimit_.setValue(0);
        wifi_.setSelected(false);
        emergencyVehicle_.setSelected(false);
        colorPreview_.setBackground(new Color(0));
    }

    public void openFile(String file) {
        filePath_.setValue(file);
        chooseVehicleType_.removeActionListener(this);
        chooseVehicleType_.removeAllItems();
        chooseVehicleType_.addActionListener(this);
        VehicleTypeXML xml = new VehicleTypeXML(filePath_.getValue().toString());
        for (VehicleType type : xml.getVehicleTypes()) {
            chooseVehicleType_.addItem(type);
        }
        actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
    }

    public boolean compareTypeList(ArrayList<VehicleType> list1, ArrayList<VehicleType> list2) {
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size() - 1; i++) {
            VehicleType type1 = list1.get(i);
            VehicleType type2 = list2.get(i);
            if (type1.getVehicleLength() != type2.getVehicleLength())
                return false;
            if (type1.getMinSpeed() != type2.getMinSpeed())
                return false;
            if (type1.getMaxSpeed() != type2.getMaxSpeed())
                return false;
            if (type1.getMinCommDist() != type2.getMinCommDist())
                return false;
            if (type1.getMaxCommDist() != type2.getMaxCommDist())
                return false;
            if (type1.getMinWaittime() != type2.getMinWaittime())
                return false;
            if (type1.getMaxWaittime() != type2.getMaxWaittime())
                return false;
            if (type1.getMinBrakingRate() != type2.getMinBrakingRate())
                return false;
            if (type1.getMaxBrakingRate() != type2.getMaxBrakingRate())
                return false;
            if (type1.getMinAccelerationRate() != type2.getMinAccelerationRate())
                return false;
            if (type1.getMaxAccelerationRate() != type2.getMaxAccelerationRate())
                return false;
            if (type1.getMinTimeDistance() != type2.getMinTimeDistance())
                return false;
            if (type1.getMaxTimeDistance() != type2.getMaxTimeDistance())
                return false;
            if (type1.getMinPoliteness() != type2.getMinPoliteness())
                return false;
            if (type1.getMaxPoliteness() != type2.getMaxPoliteness())
                return false;
            if (type1.getDeviationFromSpeedLimit_() != type2.getDeviationFromSpeedLimit_())
                return false;
            if (type1.getVehiclesDeviatingMaxSpeed_() != type2.getVehiclesDeviatingMaxSpeed_())
                return false;
            if (type1.isWifi() != type2.isWifi())
                return false;
            if (type1.isEmergencyVehicle() != type2.isEmergencyVehicle())
                return false;
            if (type1.getColor() != type2.getColor())
                return false;
        }
        return true;
    }

    public void saveType(VehicleType tmpType) {
        try {
            vehicleLength_.commitEdit();
            minSpeed_.commitEdit();
            maxSpeed_.commitEdit();
            minCommDist_.commitEdit();
            maxCommDist_.commitEdit();
            minWait_.commitEdit();
            maxWait_.commitEdit();
            minBraking_.commitEdit();
            maxBraking_.commitEdit();
            minAcceleration_.commitEdit();
            maxAcceleration_.commitEdit();
            minTimeDistance_.commitEdit();
            maxTimeDistance_.commitEdit();
            minPoliteness_.commitEdit();
            maxPoliteness_.commitEdit();
            deviationFromSpeedLimit_.commitEdit();
            vehiclesDeviatingMaxSpeed_.commitEdit();
        } catch (ParseException e) {
        }
        tmpType.setVehicleLength(((Number) vehicleLength_.getValue()).intValue());
        tmpType.setMinSpeed((int) Math.round(((Number) minSpeed_.getValue()).intValue() * (100000.0 / 3600)));
        tmpType.setMaxSpeed((int) Math.round(((Number) maxSpeed_.getValue()).intValue() * (100000.0 / 3600)));
        tmpType.setMinCommDist((int) Math.round(((Number) minCommDist_.getValue()).intValue() * 100));
        tmpType.setMaxCommDist((int) Math.round(((Number) maxCommDist_.getValue()).intValue() * 100));
        tmpType.setMinWaittime((int) Math.round(((Number) minWait_.getValue()).intValue()));
        tmpType.setMaxWaittime((int) Math.round(((Number) maxWait_.getValue()).intValue()));
        tmpType.setMinBrakingRate((int) Math.round(((Number) minBraking_.getValue()).intValue()));
        tmpType.setMaxBrakingRate((int) Math.round(((Number) maxBraking_.getValue()).intValue()));
        tmpType.setMinAccelerationRate((int) Math.round(((Number) minAcceleration_.getValue()).intValue()));
        tmpType.setMaxAccelerationRate((int) Math.round(((Number) maxAcceleration_.getValue()).intValue()));
        tmpType.setMinTimeDistance((int) Math.round(((Number) minTimeDistance_.getValue()).intValue()));
        tmpType.setMaxTimeDistance((int) Math.round(((Number) maxTimeDistance_.getValue()).intValue()));
        tmpType.setMinPoliteness((int) Math.round(((Number) minPoliteness_.getValue()).intValue()));
        tmpType.setMaxPoliteness((int) Math.round(((Number) maxPoliteness_.getValue()).intValue()));
        tmpType.setWifi(wifi_.isSelected());
        tmpType.setEmergencyVehicle(emergencyVehicle_.isSelected());
        tmpType.setColor(colorPreview_.getBackground().getRGB());
        tmpType.setDeviationFromSpeedLimit_(((int) Math.round(((Number) deviationFromSpeedLimit_.getValue()).intValue() * (100000.0 / 3600))));
        tmpType.setVehiclesDeviatingMaxSpeed_(((int) Math.round(((Number) vehiclesDeviatingMaxSpeed_.getValue()).intValue())));
    }

    public void saveQuestions() {
        if (chooseVehicleType_.getSelectedItem() != null)
            saveType((VehicleType) chooseVehicleType_.getSelectedItem());
        VehicleTypeXML xml = new VehicleTypeXML(filePath_.getValue().toString());
        ArrayList<VehicleType> tmpList = new ArrayList<VehicleType>();
        for (int i = 0; i < chooseVehicleType_.getItemCount(); i++) tmpList.add((VehicleType) chooseVehicleType_.getItemAt(i));
        if (!compareTypeList(xml.getVehicleTypes(), tmpList))
            if (JOptionPane.showConfirmDialog(null, Messages.getString("VehicleTypeDialog.saveFile"), "", JOptionPane.YES_NO_OPTION) == 0) {
                actionPerformed(new ActionEvent(chooseVehicleType_, 0, "saveFile"));
                if ((filePath_.getValue().toString().equals("vehicleTypes.xml") || filePath_.getValue().toString().equals(System.getProperty("user.dir") + "/vehicleTypes.xml"))) {
                    VanetSimStart.getMainControlPanel().getEditPanel().getEditOneVehiclePanel().refreshVehicleTypes();
                    VanetSimStart.getMainControlPanel().getEditPanel().getEditVehiclePanel().refreshVehicleTypes();
                }
            }
    }

    public void removeEmptyTypes() {
        for (int i = 0; i < chooseVehicleType_.getItemCount(); i++) {
            if (((VehicleType) chooseVehicleType_.getItemAt(i)).getName().equals(""))
                chooseVehicleType_.removeItemAt(i);
        }
    }

    public void closeDialog() {
        saveQuestions();
        this.dispose();
    }

    public void mouseClicked(MouseEvent e) {
        colorPreview_.setBackground(JColorChooser.showDialog(null, Messages.getString("VehicleTypeDialog.color"), colorPreview_.getBackground()));
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }
}
