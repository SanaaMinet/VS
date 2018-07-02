package vanetsim;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import vanetsim.localization.Messages;

public class VanetSimStarter {

    public static void main(String[] args) {
        if (args.length < 3)
        {
            SwingUtilities.invokeLater(new VanetSimStart());
            adhoc.Adhoc.AdhocFirstLaunch = true;
        }
        else
            SwingUtilities.invokeLater(new ConsoleStart(args[0], args[1], args[2]));
    }

    public static void restartWithLanguage(String language) {
        String[] buttons = { Messages.getString("VanetSimStarter.Yes", language), Messages.getString("VanetSimStarter.No", language) };
        if (JOptionPane.showOptionDialog(null, Messages.getString("VanetSimStarter.WarningMessage", language), "", JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]) == 0) {
            Messages.setLanguage(language);
            VanetSimStart.getMainFrame().dispose();
            SwingUtilities.invokeLater(new VanetSimStart());
        }
    }
}
