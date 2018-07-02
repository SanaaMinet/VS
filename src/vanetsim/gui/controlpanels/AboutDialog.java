package vanetsim.gui.controlpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vanetsim.VanetSimStart;
import vanetsim.localization.Messages;

public final class AboutDialog extends JDialog {

    private static final long serialVersionUID = -2918735209479587896L;

    public AboutDialog() {
        setUndecorated(true);
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
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        ++c.gridy;
        add(new JLabel(Messages.getString("AboutDialog.credits")), c);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
        pack();
        setLocationRelativeTo(VanetSimStart.getMainFrame());
        setVisible(true);
    }

    public void closeDialog() {
        this.dispose();
    }
}
