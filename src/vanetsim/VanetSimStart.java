package vanetsim;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.UIManager;
import vanetsim.gui.DrawingArea;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.MainControlPanel;
import vanetsim.gui.helpers.MouseClickManager;
import vanetsim.gui.helpers.ProgressOverlay;
import vanetsim.gui.helpers.ReRenderManager;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.simulation.SimulationMaster;

public final class VanetSimStart implements Runnable {

    private static SimulationMaster simulationMaster_;

    private static MainControlPanel controlPanel_;

    private static JFrame mainFrame_;

    private static boolean useDoubleBuffering_;

    private static boolean drawManualBuffered_;

    private static ProgressOverlay progressBar_;

    public VanetSimStart() {
        //readconfig("C:\\2015 ADHOC\\VanetSimulator\\VS\\src\\vanetsim\\config.txt");
        readconfig("//Users//saliha-benkerdagh//Desktop//VanetSimulator//VS//src//vanetsim//config.txt");
    }

    public void run() {
        mainFrame_ = new JFrame();
        mainFrame_.setTitle(Messages.getString("StartGUI.applicationtitle"));
        mainFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressBar_ = new ProgressOverlay();
        if (Runtime.getRuntime().maxMemory() < 120000000)
            ErrorLog.log(Messages.getString("StartGUI.detectedLowMemory"), 6, VanetSimStart.class.getName(), "run", null);
        URL appicon = ClassLoader.getSystemResource("vanetsim/images/logo.png");
        if (appicon != null) {
            mainFrame_.setIconImage(Toolkit.getDefaultToolkit().getImage(appicon));
        } else
            ErrorLog.log(Messages.getString("StartGUI.noAppIcon"), 6, VanetSimStart.class.getName(), "run", null);
        DrawingArea drawarea = addComponentsToPane(mainFrame_.getContentPane());
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        String osName = System.getProperty("os.name").toLowerCase();
        @SuppressWarnings("unused") boolean isMacOs = osName.startsWith("mac os x");
        mainFrame_.pack();
        mainFrame_.setSize((int) bounds.getWidth(), (int) bounds.getHeight());
        mainFrame_.setLocationRelativeTo(null);
        mainFrame_.setResizable(true);
        mainFrame_.setVisible(true);
        controlPanel_.getEditPanel().setEditMode(false);
        simulationMaster_ = new SimulationMaster();
        simulationMaster_.start();
        Map.getInstance().initNewMap(100000, 100000, 10000, 10000);
        Map.getInstance().signalMapLoaded();
        ReRenderManager.getInstance().start();
        MouseClickManager.getInstance().setDrawArea(drawarea);
        MouseClickManager.getInstance().start();
    }

    public static DrawingArea addComponentsToPane(Container container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        DrawingArea drawarea = new DrawingArea(useDoubleBuffering_, drawManualBuffered_);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        container.add(drawarea, c);
        Renderer.getInstance().setDrawArea(drawarea);
        controlPanel_ = new MainControlPanel();
        controlPanel_.setPreferredSize(new Dimension(200, 100000));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        container.add(controlPanel_, c);
        return drawarea;
    }

    public static void setProgressBar(boolean state) {
        progressBar_.setVisible(state);
    }

    public static MainControlPanel getMainControlPanel() {
        return controlPanel_;
    }

    public static JFrame getMainFrame() {
        return mainFrame_;
    }

    public static SimulationMaster getSimulationMaster() {
        return simulationMaster_;
    }

    private static void readconfig(String configFilePath) {
        String loggerFormat, loggerDir;
        Integer loggerLevel;
        Long loggerTrashtime;
        boolean loggerFormatError = false;
        Properties configFile = new Properties();
        try {
            configFile.load(new FileInputStream(configFilePath));
            String guiTheme = configFile.getProperty("gui_theme", "");
            if (!guiTheme.equals("")) {
                try {
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
                } catch (Exception e) {
                    ErrorLog.log(Messages.getString("StartGUI.substanceThemeError"), 3, VanetSimStart.class.getName(), "readconfig", e);
                }
            }
            loggerTrashtime = Long.parseLong(configFile.getProperty("logger_trashtime", "365000"));
            //loggerDir = "C:\\2015 ADHOC\\VanetSimulator\\VS\\src\\logs\\";
            loggerDir = "//Users//saliha-benkerdagh//Desktop//VanetSimulator//VS//src//logs//"; 
          
            loggerFormat = configFile.getProperty("logger_format", "txt");
            loggerLevel = Integer.parseInt(configFile.getProperty("logger_level", "1"));
            System.out.println("loggerTrashtime : " + loggerTrashtime);
            System.out.println("loggerDir : " + loggerDir);
            System.out.println("loggerFormat : " + loggerFormat);
            System.out.println("loggerLevel : " + loggerLevel);
            if (!loggerFormat.equals("txt") && !loggerFormat.equals("xml")) {
                loggerFormatError = true;
                loggerFormat = "txt";
            }
            ErrorLog.setParameters(loggerLevel, loggerDir, loggerFormat);
            if (loggerTrashtime < 0 || loggerTrashtime > 365000) {
                loggerTrashtime = (long) 365000;
                ErrorLog.log("", 4, VanetSimStart.class.getName(), "readconfig", null);
            }
            ErrorLog.deleteOld(loggerTrashtime, loggerDir);
            if (loggerFormatError)
                ErrorLog.log(Messages.getString("StartGUI.wrongLogformat"), 4, VanetSimStart.class.getName(), "readconfig", null);
            if (loggerLevel < 1 || loggerLevel > 7)
                ErrorLog.log(Messages.getString("StartGUI.wrongLoglevel"), 4, VanetSimStart.class.getName(), "readconfig", null);
            useDoubleBuffering_ = Boolean.parseBoolean(configFile.getProperty("double_buffer", "true"));
            drawManualBuffered_ = Boolean.parseBoolean(configFile.getProperty("draw_manual_buffered", "false"));
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("StartGUI.whileConfigreading"), 7, VanetSimStart.class.getName(), "readconfig", e);
            System.exit(1);
        }
    }
}
