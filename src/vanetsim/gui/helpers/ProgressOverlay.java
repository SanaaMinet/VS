package vanetsim.gui.helpers;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import vanetsim.VanetSimStart;
import vanetsim.localization.Messages;

public final class ProgressOverlay extends JDialog implements ActionListener {

    private static final long serialVersionUID = 6272889496006127410L;

    public ProgressOverlay() {
        super(VanetSimStart.getMainFrame());
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.PAGE_START);
        add(ButtonCreator.getJButton("shutdown.png", "shutdown", Messages.getString("ProgressOverlay.quitProgram"), this), BorderLayout.PAGE_END);
        pack();
        setVisible(false);
    }

    public void setVisible(boolean state) {
        if (state == true) {
            VanetSimStart.getMainFrame().setEnabled(false);
            Point p = VanetSimStart.getMainFrame().getLocationOnScreen();
            setLocation((VanetSimStart.getMainFrame().getBounds().width - getBounds().width) / 2 + p.x, (VanetSimStart.getMainFrame().getBounds().height - getBounds().height) / 2 + p.y);
        } else
            VanetSimStart.getMainFrame().setEnabled(true);
        super.setVisible(state);
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(ABORT);
    }
}
