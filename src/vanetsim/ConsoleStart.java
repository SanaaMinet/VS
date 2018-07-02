package vanetsim;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import vanetsim.gui.Renderer;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.scenario.Scenario;
import vanetsim.simulation.SimulationMaster;

public final class ConsoleStart implements Runnable {

    private static SimulationMaster simulationMaster_;

    private static File mapFile_ = null;

    private static File scenarioFile_ = null;

    private static int simulationTime_ = 0;

    public ConsoleStart(String mapFile, String scenarioFile, String simulationTime) {
        //readconfig("C:\\2015 ADHOC\\VanetSimulator\\VS\\src\\vanetsim\\config.txt");
        readconfig("//Users//saliha-benkerdagh//Desktop//VanetSimulator//VS//src//vanetsim//config.txt");
        mapFile_ = new File(mapFile);
        scenarioFile_ = new File(scenarioFile);
        simulationTime_ = Integer.parseInt(simulationTime);
    }

    public void run() {
        Renderer.getInstance().setConsoleStart(true);
        System.out.println("Time:" + new Date());
        System.out.println(Messages.getString("ConsoleStart.SimMasterInit"));
        simulationMaster_ = new SimulationMaster();
        simulationMaster_.start();
        System.out.println(Messages.getString("ConsoleStart.SimMasterInited"));
        Map.getInstance().initNewMap(100000, 100000, 10000, 10000);
        Map.getInstance().signalMapLoaded();
        System.out.println(Messages.getString("ConsoleStart.MapLoad"));
        Map.getInstance().load(mapFile_, false);
        System.out.println(Messages.getString("ConsoleStart.MapLoaded"));
        System.out.println(Messages.getString("ConsoleStart.ScenarioLoad"));
        Scenario.getInstance().load(scenarioFile_, false);
        System.out.println(Messages.getString("ConsoleStart.ScenarioLoaded"));
        System.out.println(Messages.getString("ConsoleStart.SetSimTime"));
        ConsoleStart.getSimulationMaster().jumpToTime(simulationTime_);
        System.out.println(Messages.getString("ConsoleStart.SimTimeSet"));
        System.out.println(Messages.getString("ConsoleStart.SimulationStart"));
        ConsoleStart.getSimulationMaster().startThread();
        System.out.println(Messages.getString("ConsoleStart.SimulationStarted"));
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
                } catch (Exception e) {
                    ErrorLog.log(Messages.getString("StartGUI.substanceThemeError"), 3, VanetSimStart.class.getName(), "readconfig", e);
                }
            }
            loggerTrashtime = Long.parseLong(configFile.getProperty("logger_trashtime", "365000"));
            loggerDir = configFile.getProperty("logger_dir", "./");
            loggerFormat = configFile.getProperty("logger_format", "txt");
            loggerLevel = Integer.parseInt(configFile.getProperty("logger_level", "1"));
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
        } catch (Exception e) {
            ErrorLog.log(Messages.getString("StartGUI.whileConfigreading"), 7, VanetSimStart.class.getName(), "readconfig", e);
            System.exit(1);
        }
    }
}
