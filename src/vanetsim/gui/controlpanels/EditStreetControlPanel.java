package vanetsim.gui.controlpanels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.gui.helpers.StreetsJColorChooserPanel;
import vanetsim.gui.helpers.TextAreaLabel;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.MapHelper;
import vanetsim.map.Node;
import vanetsim.map.Street;

public final class EditStreetControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -258179274886213461L;

    private final JPanel cardPanel_;

    private static final String[] PRESET_STRINGS = { Messages.getString("EditStreetControlPanel.motorway"), Messages.getString("EditStreetControlPanel.trunk"), Messages.getString("EditStreetControlPanel.primary"), Messages.getString("EditStreetControlPanel.secondary"), Messages.getString("EditStreetControlPanel.tertiary"), Messages.getString("EditStreetControlPanel.residential") };

    private static final int[] PRESET_SPEEDS = { 130, 110, 100, 100, 90, 30 };

    private static final Color[] PRESET_COLORS = { new Color(117, 146, 185), new Color(116, 194, 116), new Color(225, 98, 102), new Color(253, 184, 100), new Color(252, 249, 105), Color.WHITE };

    private static final int[] PRESET_LANES = { 2, 2, 1, 1, 1, 1 };

    private static final boolean[] PRESET_ONEWAY = { true, true, false, false, false, false };

    private static final String[] PRESET_TYPES = { "unkown", "motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link", "secondary", "secondary_link", "tertiary", "unclassified", "road", "residential", "living_street", "service", "track", "pedestrian", "raceway", "services", "bus_guideway" };

    private JTextField newName_;

    private JTextField editName_;

    private JButton newApplyPreset_;

    private JButton editApplyPreset_;

    private JComboBox<String> newPresetChoice_;

    private JComboBox<String> newStreetTypeChoice_;

    private JComboBox<String> editPresetChoice_;

    private JCheckBox newSnap1Checkbox_;

    private JCheckBox newSnap2Checkbox_;

    private JComboBox<String> newOnewayChoice_;

    private JComboBox<String> editOnewayChoice_;

    private JComboBox<String> editStreetTypeChoice_;

    private Color newColor_ = Color.white;

    private Color editColor_ = Color.white;

    private JButton editColorButton_;

    private JButton newColorButton_;

    private JFormattedTextField newLanes_;

    private JFormattedTextField editLanes_;

    private JFormattedTextField newSpeed_;

    private JFormattedTextField editSpeed_;

    private int lastPressedX_ = -1;

    private int lastPressedY_ = -1;

    private int currentMode_ = 0;

    private Street editStreet_ = null;

    private int minX = -1;

    private int minY = -1;

    private int maxX = -1;

    private int maxY = -1;

    private boolean selectArea = false;

    public EditStreetControlPanel() {
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
        JRadioButton addItem = new JRadioButton(Messages.getString("EditStreetControlPanel.add"));
        addItem.setActionCommand("add");
        addItem.addActionListener(this);
        addItem.setSelected(true);
        group.add(addItem);
        ++c.gridy;
        add(addItem, c);
        JRadioButton editItem = new JRadioButton(Messages.getString("EditStreetControlPanel.edit"));
        editItem.setActionCommand("edit");
        editItem.addActionListener(this);
        group.add(editItem);
        ++c.gridy;
        add(editItem, c);
        JRadioButton deleteItem = new JRadioButton(Messages.getString("EditStreetControlPanel.delete"));
        deleteItem.setActionCommand("delete");
        deleteItem.setSelected(true);
        deleteItem.addActionListener(this);
        group.add(deleteItem);
        ++c.gridy;
        add(deleteItem, c);
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        JPanel newPanel = createNewPanel();
        JPanel editPanel = createEditPanel();
        JPanel deletePanel = createDeletePanel();
        cardPanel_ = new JPanel(new CardLayout());
        cardPanel_.add(newPanel, "add");
        cardPanel_.add(editPanel, "edit");
        cardPanel_.add(deletePanel, "delete");
        ++c.gridy;
        add(cardPanel_, c);
        TextAreaLabel jlabel1 = new TextAreaLabel(Messages.getString("EditStreetControlPanel.note"));
        ++c.gridy;
        add(jlabel1, c);
        c.gridwidth = 2;
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
    }

    private final JPanel createNewPanel() {
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        JPanel tmpPanel = new JPanel();
        newPresetChoice_ = new JComboBox<String>(PRESET_STRINGS);
        newPresetChoice_.setSelectedIndex(0);
        tmpPanel.add(newPresetChoice_);
        newApplyPreset_ = ButtonCreator.getJButton("ok_small.png", "newApplyPreset", Messages.getString("EditStreetControlPanel.applyPreset"), this);
        tmpPanel.add(newApplyPreset_);
        newPanel.add(tmpPanel, c);
        c.gridwidth = 1;
        JLabel jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.streetname"));
        ++c.gridy;
        newPanel.add(jLabel1, c);
        c.gridx = 1;
        newName_ = new JTextField(0);
        newName_.setPreferredSize(new Dimension(60, 20));
        newPanel.add(newName_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.streettype"));
        ++c.gridy;
        newPanel.add(jLabel1, c);
        c.gridx = 1;
        newStreetTypeChoice_ = new JComboBox<String>(PRESET_TYPES);
        newStreetTypeChoice_.setSelectedIndex(0);
        newPanel.add(newStreetTypeChoice_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.color"));
        ++c.gridy;
        newPanel.add(jLabel1, c);
        newColorButton_ = new JButton(Messages.getString("EditStreetControlPanel.changeColor"));
        newColorButton_.setForeground(newColor_);
        newColorButton_.setActionCommand("newColor");
        newColorButton_.addActionListener(this);
        c.gridx = 1;
        newPanel.add(newColorButton_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.speed"));
        ++c.gridy;
        newPanel.add(jLabel1, c);
        newSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        newSpeed_.setPreferredSize(new Dimension(60, 20));
        newSpeed_.setValue(100);
        c.gridx = 1;
        newPanel.add(newSpeed_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.lanesPerDirection"));
        ++c.gridy;
        newPanel.add(jLabel1, c);
        newLanes_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        newLanes_.setPreferredSize(new Dimension(60, 20));
        newLanes_.setValue(1);
        c.gridx = 1;
        newPanel.add(newLanes_, c);
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.directions"));
        c.gridx = 0;
        ++c.gridy;
        newPanel.add(jLabel1, c);
        c.gridx = 1;
        String[] choices = { Messages.getString("EditStreetControlPanel.twoWay"), Messages.getString("EditStreetControlPanel.oneWay"), Messages.getString("EditStreetControlPanel.reverse") };
        newOnewayChoice_ = new JComboBox<String>(choices);
        newOnewayChoice_.setSelectedIndex(0);
        newPanel.add(newOnewayChoice_, c);
        c.gridx = 0;
        newSnap1Checkbox_ = new JCheckBox(Messages.getString("EditStreetControlPanel.snapFirst"));
        ++c.gridy;
        c.gridwidth = 2;
        newPanel.add(newSnap1Checkbox_, c);
        newSnap2Checkbox_ = new JCheckBox(Messages.getString("EditStreetControlPanel.snapSecond"));
        ++c.gridy;
        newPanel.add(newSnap2Checkbox_, c);
        c.gridwidth = 2;
        c.weighty = 1.0;
        ++c.gridy;
        newPanel.add(new JPanel(), c);
        return newPanel;
    }

    private final JPanel createEditPanel() {
        JPanel editPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        JPanel tmpPanel = new JPanel();
        editPresetChoice_ = new JComboBox<String>(PRESET_STRINGS);
        editPresetChoice_.setSelectedIndex(0);
        tmpPanel.add(editPresetChoice_);
        editApplyPreset_ = ButtonCreator.getJButton("ok_small.png", "editApplyPreset", Messages.getString("EditStreetControlPanel.applyPreset"), this);
        tmpPanel.add(editApplyPreset_);
        editPanel.add(tmpPanel, c);
        c.gridwidth = 1;
        JLabel jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.streetname"));
        ++c.gridy;
        editPanel.add(jLabel1, c);
        c.gridx = 1;
        editName_ = new JTextField(0);
        editName_.setPreferredSize(new Dimension(60, 20));
        editPanel.add(editName_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.streettype"));
        ++c.gridy;
        editPanel.add(jLabel1, c);
        c.gridx = 1;
        editStreetTypeChoice_ = new JComboBox<String>(PRESET_TYPES);
        editStreetTypeChoice_.setSelectedIndex(0);
        editPanel.add(editStreetTypeChoice_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.color"));
        ++c.gridy;
        editPanel.add(jLabel1, c);
        editColorButton_ = new JButton(Messages.getString("EditStreetControlPanel.changeColor"));
        editColorButton_.setForeground(editColor_);
        editColorButton_.setActionCommand("editColor");
        editColorButton_.addActionListener(this);
        c.gridx = 1;
        editPanel.add(editColorButton_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.speed"));
        ++c.gridy;
        editPanel.add(jLabel1, c);
        editSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        editSpeed_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        editPanel.add(editSpeed_, c);
        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.lanesPerDirection"));
        ++c.gridy;
        editPanel.add(jLabel1, c);
        editLanes_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        editLanes_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        editPanel.add(editLanes_, c);
        jLabel1 = new JLabel(Messages.getString("EditStreetControlPanel.directions"));
        c.gridx = 0;
        ++c.gridy;
        editPanel.add(jLabel1, c);
        c.gridx = 1;
        String[] choices = { Messages.getString("EditStreetControlPanel.twoWay"), Messages.getString("EditStreetControlPanel.oneWay"), Messages.getString("EditStreetControlPanel.reverse") };
        editOnewayChoice_ = new JComboBox<String>(choices);
        editOnewayChoice_.setSelectedIndex(0);
        editPanel.add(editOnewayChoice_, c);
        c.gridx = 0;
        ++c.gridy;
        c.gridwidth = 2;
        editPanel.add(ButtonCreator.getJButton("savestreet.png", "save", Messages.getString("EditStreetControlPanel.save"), this), c);
        c.gridx = 0;
        ++c.gridy;
        c.gridwidth = 2;
        editPanel.add(ButtonCreator.getJButton("autoTrim.png", "autoTrimMap", Messages.getString("EditStreetControlPanel.autoTrimMap"), this), c);
        c.gridx = 0;
        ++c.gridy;
        c.gridwidth = 2;
        editPanel.add(ButtonCreator.getJButton("trim.png", "trimMap", Messages.getString("EditStreetControlPanel.trimMap"), this), c);
        c.gridwidth = 2;
        c.weighty = 1.0;
        ++c.gridy;
        editPanel.add(new JPanel(), c);
        return editPanel;
    }

    private final JPanel createDeletePanel() {
        JPanel deletePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridwidth = 1;
        c.weighty = 1.0;
        ++c.gridy;
        deletePanel.add(new JPanel(), c);
        return deletePanel;
    }

    public void receiveMouseEvent(int x, int y) {
        if (currentMode_ == 0) {
            if (lastPressedX_ == -1 && lastPressedY_ == -1) {
                lastPressedX_ = x;
                lastPressedY_ = y;
            } else {
                Node StartNode = null, EndNode = null, tmpnode;
                if (newName_.getText().equals("") || newSpeed_.getText().equals("") || newLanes_.getText().equals("")) {
                    ErrorLog.log(Messages.getString("EditStreetControlPanel.allFieldsForAdding"), 7, getClass().getName(), "addStreet", null);
                } else {
                    if (newSnap1Checkbox_.getSelectedObjects() != null) {
                        tmpnode = MapHelper.findNearestNode(lastPressedX_, lastPressedY_, 2000, new long[1]);
                        StartNode = tmpnode;
                        if (StartNode == null) {
                            int answer = JOptionPane.showConfirmDialog(VanetSimStart.getMainFrame(), Messages.getString("EditStreetControlPanel.1stPointSnappingFailedMessage"), Messages.getString("EditStreetControlPanel.snappingFailed"), JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION)
                                StartNode = new Node(lastPressedX_, lastPressedY_);
                        }
                    } else
                        StartNode = new Node(lastPressedX_, lastPressedY_);
                    if (newSnap2Checkbox_.getSelectedObjects() != null) {
                        tmpnode = MapHelper.findNearestNode(x, y, 2000, new long[1]);
                        EndNode = tmpnode;
                        if (StartNode != null && EndNode == null) {
                            int answer = JOptionPane.showConfirmDialog(VanetSimStart.getMainFrame(), Messages.getString("EditStreetControlPanel.2ndPointSnappingFailedMessage"), Messages.getString("EditStreetControlPanel.snappingFailed"), JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION)
                                EndNode = new Node(x, y);
                        }
                    } else
                        EndNode = new Node(x, y);
                    if (StartNode != null && EndNode != null) {
                        StartNode = Map.getInstance().addNode(StartNode);
                        EndNode = Map.getInstance().addNode(EndNode);
                        Map.getInstance().addStreet(new Street(newName_.getText(), StartNode, EndNode, newStreetTypeChoice_.getSelectedItem().toString(), newOnewayChoice_.getSelectedIndex(), ((Number) newLanes_.getValue()).intValue(), newColor_, Map.getInstance().getRegionOfPoint(StartNode.getX(), StartNode.getY()), ((Number) newSpeed_.getValue()).intValue() * 100000 / 3600));
                        Renderer.getInstance().ReRender(true, false);
                    }
                    lastPressedX_ = -1;
                    lastPressedY_ = -1;
                }
            }
        } else if (currentMode_ == 1) {
            if (selectArea) {
                if (minX == -1 && minY == -1) {
                    minX = x;
                    minY = y;
                } else {
                    maxX = x;
                    maxY = y;
                    selectArea = false;
                    Map.getInstance().autoTrimMap(minX, minY, maxX, maxY);
                }
            } else {
                editStreet_ = MapHelper.findNearestStreet(x, y, 20000, new double[2], new int[2]);
                if (editStreet_ == null) {
                    editName_.setText("");
                    editLanes_.setText("");
                    editSpeed_.setText("");
                    editStreetTypeChoice_.setSelectedIndex(0);
                    editColor_ = null;
                    editColorButton_.setForeground(Color.black);
                    editOnewayChoice_.setSelectedIndex(0);
                } else {
                    editName_.setText(editStreet_.getName());
                    editLanes_.setValue(editStreet_.getLanesCount());
                    editSpeed_.setValue(Math.round(editStreet_.getSpeed() * 3600.0 / 100000));
                    int tmpIndex = 0;
                    for (int i = 0; i < PRESET_TYPES.length; i++) if (PRESET_TYPES[i].equals(editStreet_.getStreetType_()))
                        tmpIndex = i;
                    editStreetTypeChoice_.setSelectedIndex(tmpIndex);
                    editColor_ = editStreet_.getDisplayColor();
                    editColorButton_.setForeground(editColor_);
                    if (editStreet_.isOneway())
                        editOnewayChoice_.setSelectedIndex(1);
                    else
                        editOnewayChoice_.setSelectedIndex(0);
                }
                Renderer.getInstance().setMarkedStreet(editStreet_);
                Renderer.getInstance().ReRender(false, false);
            }
        } else if (currentMode_ == 2) {
            Street tmpstreet = MapHelper.findNearestStreet(x, y, 20000, new double[2], new int[2]);
            if (tmpstreet != null) {
                tmpstreet.getStartNode().delOutgoingStreet(tmpstreet);
                tmpstreet.getStartNode().delCrossingStreet(tmpstreet);
                if (tmpstreet.getStartNode().getCrossingStreetsCount() == 0)
                    Map.getInstance().delNode(tmpstreet.getStartNode());
                tmpstreet.getEndNode().delOutgoingStreet(tmpstreet);
                tmpstreet.getEndNode().delCrossingStreet(tmpstreet);
                if (tmpstreet.getEndNode().getCrossingStreetsCount() == 0)
                    Map.getInstance().delNode(tmpstreet.getEndNode());
                Map.getInstance().delStreet(tmpstreet);
                Renderer.getInstance().ReRender(true, false);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("add".equals(command)) {
            currentMode_ = 0;
            CardLayout cl = (CardLayout) (cardPanel_.getLayout());
            cl.show(cardPanel_, "add");
        } else if ("edit".equals(command)) {
            currentMode_ = 1;
            CardLayout cl = (CardLayout) (cardPanel_.getLayout());
            cl.show(cardPanel_, "edit");
        } else if ("delete".equals(command)) {
            currentMode_ = 2;
            CardLayout cl = (CardLayout) (cardPanel_.getLayout());
            cl.show(cardPanel_, "delete");
        } else if ("save".equals(command)) {
            if (editStreet_ != null && !editName_.getText().equals("") && !editSpeed_.getText().equals("") && !editLanes_.getText().equals("")) {
                editStreet_.setName(editName_.getText());
                try {
                    editSpeed_.commitEdit();
                    editLanes_.commitEdit();
                    editStreet_.setSpeed((int) Math.round(((Number) editSpeed_.getValue()).intValue() * 100000.0 / 3600));
                    editStreet_.setStreetType_(editStreetTypeChoice_.getSelectedItem().toString());
                    editStreet_.setLanesCount(((Number) editLanes_.getValue()).intValue());
                    editStreet_.setStreetType_(editStreetTypeChoice_.getSelectedItem().toString());
                    editStreet_.changeOneWay(editOnewayChoice_.getSelectedIndex());
                    if (editOnewayChoice_.getSelectedIndex() == 2)
                        editOnewayChoice_.setSelectedIndex(1);
                } catch (Exception e2) {
                }
                if (editColor_ != null)
                    editStreet_.setDisplayColor(editColor_);
                Renderer.getInstance().ReRender(true, false);
            }
        } else if ("autoTrimMap".equals(command)) {
            int respons = JOptionPane.showOptionDialog(null, Messages.getString("EditStreetControlPanel.WarningMsgBoxAuto"), "Information", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, "");
            if (respons == 0) {
                Map.getInstance().autoTrimMap(-1, -1, -1, -1);
            }
        } else if ("trimMap".equals(command)) {
            int respons = JOptionPane.showOptionDialog(null, Messages.getString("EditStreetControlPanel.WarningMsgBox"), "Information", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, "");
            if (respons == 0) {
                minX = -1;
                minY = -1;
                maxX = -1;
                maxY = -1;
                selectArea = true;
            }
        } else if ("newColor".equals(command)) {
            final JColorChooser chooser;
            if (editColor_ != null)
                chooser = new JColorChooser(newColor_);
            else
                chooser = new JColorChooser();
            chooser.addChooserPanel(new StreetsJColorChooserPanel());
            ActionListener okActionListener = new ActionListener() {

                public void actionPerformed(ActionEvent actionEvent) {
                    newColor_ = chooser.getColor();
                    newColorButton_.setForeground(newColor_);
                }
            };
            JDialog dialog = JColorChooser.createDialog(VanetSimStart.getMainFrame(), Messages.getString("EditStreetControlPanel.changeStreetColorTitle"), true, chooser, okActionListener, null);
            dialog.setVisible(true);
        } else if ("editColor".equals(command)) {
            final JColorChooser chooser;
            if (editColor_ != null)
                chooser = new JColorChooser(editColor_);
            else
                chooser = new JColorChooser();
            chooser.addChooserPanel(new StreetsJColorChooserPanel());
            ActionListener okActionListener = new ActionListener() {

                public void actionPerformed(ActionEvent actionEvent) {
                    editColor_ = chooser.getColor();
                    editColorButton_.setForeground(editColor_);
                }
            };
            JDialog dialog = JColorChooser.createDialog(VanetSimStart.getMainFrame(), Messages.getString("EditStreetControlPanel.changeStreetColorTitle"), true, chooser, okActionListener, null);
            dialog.setVisible(true);
        } else if ("newApplyPreset".equals(command)) {
            int i = newPresetChoice_.getSelectedIndex();
            newSpeed_.setValue(PRESET_SPEEDS[i]);
            newColor_ = PRESET_COLORS[i];
            newColorButton_.setForeground(newColor_);
            newLanes_.setValue(PRESET_LANES[i]);
            if (newPresetChoice_.getSelectedIndex() == 0)
                newStreetTypeChoice_.setSelectedItem("motorway");
            else if (newPresetChoice_.getSelectedIndex() == 1)
                newStreetTypeChoice_.setSelectedItem("trunk");
            else if (newPresetChoice_.getSelectedIndex() == 2)
                newStreetTypeChoice_.setSelectedItem("primary");
            else if (newPresetChoice_.getSelectedIndex() == 3)
                newStreetTypeChoice_.setSelectedItem("secondary");
            else if (newPresetChoice_.getSelectedIndex() == 4)
                newStreetTypeChoice_.setSelectedItem("tertiary");
            else if (newPresetChoice_.getSelectedIndex() == 5)
                newStreetTypeChoice_.setSelectedItem("unkown");
            boolean oneway = PRESET_ONEWAY[i];
            if (oneway) {
                if (newOnewayChoice_.getSelectedIndex() == 0)
                    newOnewayChoice_.setSelectedIndex(1);
            } else
                newOnewayChoice_.setSelectedIndex(0);
        } else if ("editApplyPreset".equals(command)) {
            int i = editPresetChoice_.getSelectedIndex();
            editSpeed_.setValue(PRESET_SPEEDS[i]);
            editColor_ = PRESET_COLORS[i];
            editColorButton_.setForeground(editColor_);
            editLanes_.setValue(PRESET_LANES[i]);
            if (editPresetChoice_.getSelectedIndex() == 0)
                editStreetTypeChoice_.setSelectedItem("motorway");
            else if (editPresetChoice_.getSelectedIndex() == 1)
                editStreetTypeChoice_.setSelectedItem("trunk");
            else if (editPresetChoice_.getSelectedIndex() == 2)
                editStreetTypeChoice_.setSelectedItem("primary");
            else if (editPresetChoice_.getSelectedIndex() == 3)
                editStreetTypeChoice_.setSelectedItem("secondary");
            else if (editPresetChoice_.getSelectedIndex() == 4)
                editStreetTypeChoice_.setSelectedItem("tertiary");
            else if (editPresetChoice_.getSelectedIndex() == 5)
                editStreetTypeChoice_.setSelectedItem("unkown");
            boolean oneway = PRESET_ONEWAY[i];
            if (oneway) {
                if (editOnewayChoice_.getSelectedIndex() == 0)
                    editOnewayChoice_.setSelectedIndex(1);
            } else
                editOnewayChoice_.setSelectedIndex(0);
        }
    }
}
