package vanetsim;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vanetsim.localization.Messages;

public final class ErrorLog {

    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void setParameters(int level, String dir, String format) {
        switch(level) {
            case 2:
                logger.setLevel(Level.FINER);
                break;
            case 3:
                logger.setLevel(Level.FINE);
                break;
            case 4:
                logger.setLevel(Level.CONFIG);
                break;
            case 5:
                logger.setLevel(Level.INFO);
                break;
            case 6:
                logger.setLevel(Level.WARNING);
                break;
            case 7:
                logger.setLevel(Level.SEVERE);
                break;
            default:
                logger.setLevel(Level.FINEST);
        }
        java.util.Date dt = new java.util.Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
        try {
            FileHandler handler = new FileHandler(dir + "log_" + df.format(dt) + "." + format, true);
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            if (format.equals("txt"))
                handler.setFormatter(new SimpleFormatter());
            else
                handler.setFormatter(new XMLFormatter());
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("ErrorLog.whileSetting"), 7, ErrorLog.class.getName(), "setParameters", e);
            System.exit(1);
        }
    }

    public static synchronized void log(String message, int severity, String errClass, String errMethod, Exception e) {
        try {
            if (e != null) {
                message = message + "\n" + e.getLocalizedMessage();
                switch(severity) {
                    case 1:
                        logger.logp(Level.FINEST, errClass, errMethod, message, e);
                        break;
                    case 2:
                        logger.logp(Level.FINER, errClass, errMethod, message, e);
                        break;
                    case 3:
                        logger.logp(Level.FINE, errClass, errMethod, message, e);
                        break;
                    case 4:
                        logger.logp(Level.CONFIG, errClass, errMethod, message, e);
                        break;
                    case 5:
                        logger.logp(Level.INFO, errClass, errMethod, message, e);
                        break;
                    case 6:
                        logger.logp(Level.WARNING, errClass, errMethod, message, e);
                        break;
                    default:
                        logger.logp(Level.SEVERE, errClass, errMethod, message, e);
                }
            } else {
                switch(severity) {
                    case 1:
                        logger.logp(Level.FINEST, errClass, errMethod, message);
                        break;
                    case 2:
                        logger.logp(Level.FINER, errClass, errMethod, message);
                        break;
                    case 3:
                        logger.logp(Level.FINE, errClass, errMethod, message);
                        break;
                    case 4:
                        logger.logp(Level.CONFIG, errClass, errMethod, message);
                        break;
                    case 5:
                        logger.logp(Level.INFO, errClass, errMethod, message);
                        break;
                    case 6:
                        logger.logp(Level.WARNING, errClass, errMethod, message);
                        break;
                    default:
                        logger.logp(Level.SEVERE, errClass, errMethod, message);
                }
            }
            if (severity == 7)
                JOptionPane.showMessageDialog(VanetSimStart.getMainFrame(), Messages.getString("ErrorLog.error") + message + (e != null ? "\n" + Messages.getString("ErrorLog.seeErrorlog") : ""), Messages.getString("ErrorLog.errorWindowTitle"), JOptionPane.ERROR_MESSAGE);
            else if (severity == 6)
                JOptionPane.showMessageDialog(VanetSimStart.getMainFrame(), Messages.getString("ErrorLog.warning") + message + (e != null ? "\n" + Messages.getString("ErrorLog.seeErrorlog") : ""), Messages.getString("ErrorLog.warningWindowTitle"), JOptionPane.WARNING_MESSAGE);
        } catch (Exception new_e) {
            System.out.println(Messages.getString("ErrorLog.whileLogging") + message + ":" + e.getLocalizedMessage() + ")! " + new_e.getLocalizedMessage());
            new_e.printStackTrace();
        }
    }

    public static void deleteOld(long loggerTrashtime, String loggerDir) {
        try {
            File path = new File(loggerDir);
            File files[] = path.listFiles();
            long deletedate = System.currentTimeMillis() - 60000 - (loggerTrashtime * 86400000);
            for (int i = 0, n = files.length; i < n; i++) {
                if (files[i].lastModified() < deletedate) {
                    String filename = files[i].toString();
                    if (files[i].delete())
                        log(Messages.getString("ErrorLog.oldLogfile") + filename + Messages.getString("ErrorLog.deletedSuccess"), 2, ErrorLog.class.getName(), "deleteold", null);
                    else
                        log(Messages.getString("ErrorLog.deleteFailed") + filename + "!", 5, ErrorLog.class.getName(), "deleteold", null);
                }
            }
        } catch (Exception e) {
            log(Messages.getString("ErrorLog.deleteFailedGlobal"), 6, ErrorLog.class.getName(), "deleteold", e);
        }
    }
}
