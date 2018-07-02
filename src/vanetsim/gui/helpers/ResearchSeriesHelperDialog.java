package vanetsim.gui.helpers;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vanetsim.gui.controlpanels.ResearchSeriesDialog;
import vanetsim.localization.Messages;

public final class ResearchSeriesHelperDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -2918735209479587896L;

    private final String mode_;

    private final String command_;

    private final JFormattedTextField property_ = new JFormattedTextField();

    private final JFormattedTextField startValue_ = new JFormattedTextField(new DecimalFormat());

    private final JFormattedTextField stepSize_ = new JFormattedTextField(new DecimalFormat());

    private JFormattedTextField stepAmount_;

    private JLabel vehicleAmountVariationLabel_;

    private final JCheckBox vehicleAmountVariation_;

    TextAreaLabel errorNote_;

    public ResearchSeriesHelperDialog(String mode, String command) {
        setUndecorated(true);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                closeDialog();
            }
        });
        setModal(true);
        mode_ = mode;
        command_ = command;
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
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("ResearchSeriesHelperDialog.inputValues")), c);
        ++c.gridy;
        add(new JLabel(Messages.getString("ResearchSeriesHelperDialog.propertyTerm")), c);
        c.gridx = 1;
        property_.setValue(command);
        property_.setEditable(false);
        property_.setPreferredSize(new Dimension(100, 20));
        add(property_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("ResearchSeriesHelperDialog.startValue")), c);
        c.gridx = 1;
        add(startValue_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("ResearchSeriesHelperDialog.stepSize")), c);
        c.gridx = 1;
        add(stepSize_, c);
        c.gridx = 0;
        ++c.gridy;
        add(new JLabel(Messages.getString("ResearchSeriesHelperDialog.stepAmount")), c);
        c.gridx = 1;
        stepAmount_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        add(stepAmount_, c);
        c.gridx = 0;
        c.gridwidth = 1;
        ++c.gridy;
        c.gridx = 0;
        vehicleAmountVariationLabel_ = new JLabel(Messages.getString("ResearchSeriesHelperDialog.vehicleAmountVariation"));
        add(vehicleAmountVariationLabel_, c);
        vehicleAmountVariation_ = new JCheckBox();
        vehicleAmountVariation_.setSelected(false);
        vehicleAmountVariation_.setActionCommand("activateAmountVariation");
        c.gridx = 1;
        add(vehicleAmountVariation_, c);
        vehicleAmountVariationLabel_.setVisible(true);
        vehicleAmountVariation_.setVisible(true);
        vehicleAmountVariationLabel_.setVisible(false);
        vehicleAmountVariation_.setVisible(false);
        vehicleAmountVariation_.addActionListener(this);
        JButton button = new JButton(Messages.getString("ResearchSeriesHelperDialog.apply"));
        ++c.gridy;
        add(button, c);
        button.setActionCommand("apply");
        button.addActionListener(this);
        ++c.gridy;
        errorNote_ = new TextAreaLabel(Messages.getString("ResearchSeriesHelperDialog.MsgBoxMissingInformation"));
        ++c.gridy;
        c.gridwidth = 2;
        c.gridx = 0;
        add(errorNote_, c);
        errorNote_.setVisible(false);
        c.weighty = 1.0;
        ++c.gridy;
        add(new JPanel(), c);
        pack();
        setLocationRelativeTo(ResearchSeriesDialog.getInstance());
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("apply".equals(command)) {
            if (startValue_.getValue() == null || startValue_.getValue().equals("") || (!vehicleAmountVariation_.isSelected() && (stepSize_.getValue() == null || stepSize_.getValue().equals("") || stepAmount_.getValue() == null || stepAmount_.getValue().equals("")))) {
                errorNote_.setVisible(true);
            } else {
                if (mode_.equals("vehicles")) {
                    if (vehicleAmountVariation_.isSelected() && command_.equals("amount"))
                        ResearchSeriesDialog.getInstance().getActiveVehicleSet_().getPropertyList_().add(new SimulationProperty("amount", Double.parseDouble(startValue_.getValue().toString()), -1, 1));
                    else
                        ResearchSeriesDialog.getInstance().getActiveVehicleSet_().getPropertyList_().add(new SimulationProperty(command_, Double.parseDouble(startValue_.getValue().toString()), Double.parseDouble(stepSize_.getValue().toString()), Integer.parseInt(stepAmount_.getValue().toString())));
                } else if (mode_.equals("generalSettings")) {
                    ResearchSeriesDialog.getInstance().getActiveSeries_().getPropertyList_().add(new SimulationProperty(command_, Double.parseDouble(startValue_.getValue().toString()), Double.parseDouble(stepSize_.getValue().toString()), Integer.parseInt(stepAmount_.getValue().toString())));
                }
                closeDialog();
            }
        } else if ("activateAmountVariation".equals(command)) {
            if (vehicleAmountVariation_.isSelected()) {
                stepSize_.setEditable(false);
                stepAmount_.setEditable(false);
            } else {
                stepSize_.setEditable(true);
                stepAmount_.setEditable(true);
            }
        }
    }

    public void closeDialog() {
        this.dispose();
    }
}
