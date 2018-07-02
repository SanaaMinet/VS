package vanetsim.gui.controlpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import vanetsim.gui.helpers.TextAreaLabel;
import vanetsim.localization.Messages;
import vanetsim.simulation.WorkerThread;

public class EditTrafficModelControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -8394785435746199543L;

    JRadioButton selectModel_;

    JRadioButton selectTraces_;

    private JComboBox<String> chooseTrafficModel_;

    private JLabel chooseTrafficModelLabel_;

    private JComboBox<String> chooseTraces_;

    private JLabel chooseTracesLabel_;

    TextAreaLabel Note;

    public EditTrafficModelControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        ButtonGroup group = new ButtonGroup();
        selectModel_ = new JRadioButton(Messages.getString("EditTrafficControlPanel.model"));
        selectModel_.setActionCommand("model");
        selectModel_.addActionListener(this);
        selectModel_.setSelected(true);
        group.add(selectModel_);
        ++c.gridy;
        add(selectModel_, c);
        selectTraces_ = new JRadioButton(Messages.getString("EditTrafficControlPanel.traces"));
        selectTraces_.setActionCommand("traces");
        selectTraces_.addActionListener(this);
        group.add(selectTraces_);
        ++c.gridy;
        add(selectTraces_, c);
        c.gridx = 0;
        chooseTrafficModelLabel_ = new JLabel(Messages.getString("EditTrafficControlPanel.comboBoxModel"));
        ++c.gridy;
        add(chooseTrafficModelLabel_, c);
        chooseTrafficModel_ = new JComboBox<String>();
        chooseTrafficModel_.setActionCommand("chooseTrafficModel");
        chooseTrafficModel_.addItem("VANETSim classic");
        chooseTrafficModel_.addItem("IDM/MOBIL");
        chooseTrafficModel_.addActionListener(this);
        c.gridx = 1;
        add(chooseTrafficModel_, c);
        c.gridx = 0;
        chooseTracesLabel_ = new JLabel(Messages.getString("EditTrafficControlPanel.comboBoxTraces"));
        ++c.gridy;
        add(chooseTracesLabel_, c);
        chooseTraces_ = new JComboBox<String>();
        chooseTraces_.setActionCommand("chooseTraces");
        chooseTraces_.addItem("sjtu taxi traces");
        chooseTraces_.addItem("San Francisco traces");
        chooseTraces_.addActionListener(this);
        c.gridx = 1;
        add(chooseTraces_, c);
        chooseTraces_.setVisible(false);
        chooseTracesLabel_.setVisible(false);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String command = arg0.getActionCommand();
        if ("model".equals(command)) {
            if (((String) chooseTrafficModel_.getSelectedItem()).equals("VANETSim classic"))
                WorkerThread.setSimulationMode_(1);
            else if (((String) chooseTrafficModel_.getSelectedItem()).equals("IDM/MOBIL"))
                WorkerThread.setSimulationMode_(2);
            chooseTrafficModelLabel_.setVisible(true);
            chooseTrafficModel_.setVisible(true);
            chooseTraces_.setVisible(false);
            chooseTracesLabel_.setVisible(false);
        } else if ("traces".equals(command)) {
            if (((String) chooseTraces_.getSelectedItem()).equals("sjtu taxi traces"))
                WorkerThread.setSimulationMode_(3);
            else if (((String) chooseTraces_.getSelectedItem()).equals("San Francisco traces"))
                WorkerThread.setSimulationMode_(4);
            chooseTraces_.setVisible(true);
            chooseTracesLabel_.setVisible(true);
            chooseTrafficModel_.setVisible(false);
            chooseTrafficModelLabel_.setVisible(false);
        } else if ("chooseTrafficModel".equals(command)) {
            if (((String) chooseTrafficModel_.getSelectedItem()).equals("VANETSim classic"))
                WorkerThread.setSimulationMode_(1);
            else if (((String) chooseTrafficModel_.getSelectedItem()).equals("IDM/MOBIL"))
                WorkerThread.setSimulationMode_(2);
        } else if ("chooseTraces".equals(command)) {
            if (((String) chooseTraces_.getSelectedItem()).equals("sjtu taxi traces"))
                WorkerThread.setSimulationMode_(3);
            else if (((String) chooseTraces_.getSelectedItem()).equals("San Francisco traces"))
                WorkerThread.setSimulationMode_(4);
        }
    }
}
