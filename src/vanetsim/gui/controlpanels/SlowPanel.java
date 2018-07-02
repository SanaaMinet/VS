package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vanetsim.gui.helpers.PrivacyLogWriter;
import vanetsim.localization.Messages;
import vanetsim.scenario.Vehicle;

public class SlowPanel extends JPanel implements ActionListener, FocusListener {

    private static final long serialVersionUID = -8294786435746799533L;

    private final JCheckBox enableSlow_;

    private final JLabel enableSlowLabel_;

    private final JFormattedTextField timeToPseudonymChange_;

    private final JLabel timeToPseudonymChangeLabel_;

    private final JFormattedTextField slowSpeedLimit_;

    private final JLabel slowSpeedLimitLabel_;

    public SlowPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        timeToPseudonymChangeLabel_ = new JLabel(Messages.getString("SlowPanel.timeToPseudonymChange"));
        ++c.gridy;
        add(timeToPseudonymChangeLabel_, c);
        timeToPseudonymChange_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        timeToPseudonymChange_.setValue(3000);
        timeToPseudonymChange_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        timeToPseudonymChange_.addFocusListener(this);
        add(timeToPseudonymChange_, c);
        c.gridx = 0;
        slowSpeedLimitLabel_ = new JLabel(Messages.getString("SlowPanel.speedLimit"));
        ++c.gridy;
        add(slowSpeedLimitLabel_, c);
        slowSpeedLimit_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        slowSpeedLimit_.setValue(30);
        slowSpeedLimit_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        slowSpeedLimit_.addFocusListener(this);
        add(slowSpeedLimit_, c);
        c.gridx = 0;
        enableSlowLabel_ = new JLabel(Messages.getString("SlowPanel.enable"));
        ++c.gridy;
        add(enableSlowLabel_, c);
        enableSlow_ = new JCheckBox();
        enableSlow_.setSelected(false);
        enableSlow_.setActionCommand("enableSlow");
        c.gridx = 1;
        enableSlow_.addFocusListener(this);
        add(enableSlow_, c);
        enableSlow_.addActionListener(this);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
    }

    public void saveAttributes() {
        Vehicle.setTIME_TO_PSEUDONYM_CHANGE(((Number) timeToPseudonymChange_.getValue()).intValue());
        Vehicle.setSLOW_SPEED_LIMIT((int) Math.round(((Number) slowSpeedLimit_.getValue()).intValue() * (100000.0 / 3600)));
        Vehicle.setSlowOn(enableSlow_.isSelected());
    }

    public void loadAttributes() {
        timeToPseudonymChange_.setValue(Vehicle.getTIME_TO_PSEUDONYM_CHANGE());
        slowSpeedLimit_.setValue((int) Math.round(Vehicle.getSLOW_SPEED_LIMIT() / (100000.0 / 3600)));
        enableSlow_.setSelected(Vehicle.isSlowOn());
    }

    public void actionPerformed(ActionEvent e) {
    }

    public static void writeSlowHeader() {
        PrivacyLogWriter.log("Slow speed limit:" + Vehicle.getSLOW_SPEED_LIMIT() + ":Time to pseudonym change:" + Vehicle.getTIME_TO_PSEUDONYM_CHANGE());
    }

    @Override
    public void focusGained(FocusEvent arg0) {
        saveAttributes();
    }

    @Override
    public void focusLost(FocusEvent arg0) {
        saveAttributes();
    }

    public JCheckBox getEnableSlow_() {
        return enableSlow_;
    }

    public JFormattedTextField getTimeToPseudonymChange_() {
        return timeToPseudonymChange_;
    }

    public JFormattedTextField getSlowSpeedLimit_() {
        return slowSpeedLimit_;
    }
}
