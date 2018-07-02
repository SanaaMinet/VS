package vanetsim.simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.ReportingControlPanel;
import vanetsim.gui.controlpanels.SlowPanel;
import vanetsim.gui.helpers.GeneralLogWriter;
import vanetsim.gui.helpers.IDSLogWriter;
import vanetsim.gui.helpers.PrivacyLogWriter;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Region;
 
import vanetsim.scenario.KnownEventSourcesList;
import vanetsim.scenario.KnownVehicle;
import vanetsim.scenario.KnownVehiclesList;
 
import vanetsim.scenario.Scenario;
import vanetsim.scenario.Vehicle;
import vanetsim.scenario.events.EventList;
import vanetsim.scenario.events.EventSpotList;

public final class SimulationMaster extends Thread {

    public static final int TIME_PER_STEP = 40;
    
    public static boolean EndSimulation = false;

    private static final EventList eventList_ = EventList.getInstance();

    private volatile boolean running_ = false;

    private volatile boolean doOneStep_ = false;

    private volatile int targetStepTime_ = TIME_PER_STEP;

    private volatile boolean jumpTimeMode_ = false;

    private volatile int jumpTimeTarget_ = -1;

    private WorkerThread[] workers_ = null;

    private CyclicBarrier barrierStart_ = null;

    private CyclicBarrier barrierDuringWork_ = null;

    private CyclicBarrier barrierFinish_ = null;

    private boolean guiEnabled = true;

    private boolean logSilentPeriodHeader_ = true;

    private static int eventSpotCountdown_ = -1;

    private boolean generalLogWriterActivated_ = true;

    private String generalLogWriterHeader_ = "timestamp:x:y:v:id";

    public SimulationMaster() {
    }

    public synchronized void startThread() {
    //  JOptionPane.showConfirmDialog(null, "START THREAD");
        
int numPass = 1;
        if (Vehicle.isSlowOn())
            SlowPanel.writeSlowHeader();
           // JOptionPane.showConfirmDialog(null, "START 1");  
        
        if (Vehicle.isSilentPeriodsOn() && logSilentPeriodHeader_) {
            logSilentPeriodHeader_ = false;
            PrivacyLogWriter.log("Silent Period:Duration:" + Vehicle.getTIME_OF_SILENT_PERIODS() + ":Frequency:" + Vehicle.getTIME_BETWEEN_SILENT_PERIODS());
        }
         //     JOptionPane.showConfirmDialog(null, "START 2");
        Renderer.getInstance().notifySimulationRunning(true);
        //      JOptionPane.showConfirmDialog(null, "START 3");
        ErrorLog.log(Messages.getString("SimulationMaster.simulationStarted"), 2, SimulationMaster.class.getName(), "startThread", null);
          //    JOptionPane.showConfirmDialog(null, "START 4");
        Renderer.getInstance().ReRender(true, false);
        running_ = true;
        adhoc.Adhoc.running_ADHOC = true;
        adhoc.Adhoc.AdhocFirstLaunch = true;
        //-------------------------- Appel ADHOC -------------------------------
//JOptionPane.showConfirmDialog(null, "Appel ADHOC");
        if (adhoc.Adhoc.AdhocFirstLaunch == true) 
       {
        System.out.println("\n            --------------------------------");
        System.out.println("            Lancement de l'application Adhoc");
        System.out.println("            --------------------------------\n");
        
        //System.out.println("Début Initialisation des paramètres ADHOC");
     //   JOptionPane.showConfirmDialog(null, "Appel INIT");
        adhoc.Adhoc.Init();
        //System.out.println("Fin Initialisation des paramètres ADHOC");
            
       }
      adhoc.Adhoc.AdhocFirstLaunch = false;
        //int ActualTime = vanetsim.gui.Renderer.getInstance().getTimePassed();
        //System.out.println("pass SimulationMaster StartThread n°:"+numPass+" time == "+ActualTime);
        //JOptionPane.showConfirmDialog(null, "LANCEMENT  ADHOC");
        adhoc.Adhoc.RUNNING();
        numPass++;
        //----------------------------------------------------------------------

    
    }

    public synchronized void stopThread() {
        if (running_)
            ErrorLog.log(Messages.getString("SimulationMaster.simulationStopped"), 2, SimulationMaster.class.getName(), "stopThread", null);
        running_ = false;
        adhoc.Adhoc.running_ADHOC = false;
        if ((Map.getInstance().getReadyState() == false || Scenario.getInstance().getReadyState() == false) && workers_ != null) {
            while (barrierStart_.getParties() - barrierStart_.getNumberWaiting() != 1) {
                try {
                    sleep(1);
                } catch (Exception e) {
                }
            }
            workers_[0].interrupt();
            workers_ = null;
        }
        Renderer.getInstance().notifySimulationRunning(false);
    }

    public void jumpToTime(int time) {
        jumpTimeMode_ = true;
        jumpTimeTarget_ = time;
        if (!Renderer.getInstance().isConsoleStart())
            VanetSimStart.setProgressBar(true);
        startThread();
    }

    public void setTargetStepTime(int time) {
        if (time > 0)
            targetStepTime_ = time;
    }

    public void doOneStep() {
        if (!running_) {
            Renderer.getInstance().notifySimulationRunning(true);
            doOneStep_ = true;
        }
    }

    public WorkerThread[] createWorkers(int timePerStep, int threads) {
        ArrayList<WorkerThread> tmpWorkers = new ArrayList<WorkerThread>();
        WorkerThread tmpWorker = null;
        Region[][] regions = Map.getInstance().getRegions();
        ArrayList<Region> tmpRegions = new ArrayList<Region>();
        long regionCountX = Map.getInstance().getRegionCountX();
        long regionCountY = Map.getInstance().getRegionCountY();
        double regionsPerThread = regionCountX * regionCountY / (double) threads;
        long count = 0;
        double target = regionsPerThread;
        threads = 0;
        for (int i = 0; i < regionCountX; ++i) {
            for (int j = 0; j < regionCountY; ++j) {
                ++count;
                tmpRegions.add(regions[i][j]);
                if (count >= Math.round(target)) {
                    try {
                        tmpWorker = new WorkerThread(tmpRegions.toArray(new Region[0]), timePerStep);
                       
                        
                        ++threads;
                        tmpWorkers.add(tmpWorker);
                        tmpWorker.start();
                    } catch (Exception e) {
                        ErrorLog.log(Messages.getString("SimulationMaster.errorWorkerThread"), 7, SimulationMaster.class.getName(), "createWorkers", e);
                    }
                    tmpRegions = new ArrayList<Region>();
                    target += regionsPerThread;
                }
            }
        }
        if (tmpRegions.size() > 0) {
            ErrorLog.log(Messages.getString("SimulationMaster.regionsRemained"), 6, SimulationMaster.class.getName(), "createWorkers", null);
            try {
                tmpWorker = new WorkerThread(tmpRegions.toArray(new Region[0]), timePerStep);
                ++threads;
                tmpWorkers.add(tmpWorker);
                tmpWorker.start();
            } catch (Exception e) {
                ErrorLog.log(Messages.getString("SimulationMaster.errorAddingRemainingRegions"), 7, SimulationMaster.class.getName(), "createWorkers", e);
            }
        }
        barrierStart_ = new CyclicBarrier(threads + 1);
        barrierDuringWork_ = new CyclicBarrier(threads);
        barrierFinish_ = new CyclicBarrier(threads + 1);
        Iterator<WorkerThread> iterator = tmpWorkers.iterator();
        while (iterator.hasNext()) {
            iterator.next().setBarriers(barrierStart_, barrierDuringWork_, barrierFinish_);
        }
        return tmpWorkers.toArray(new WorkerThread[0]);
    }

    public void run() {
        setName("SimulationMaster");
        int time, threads;
        long renderTime;
        Renderer renderer = Renderer.getInstance();
        CyclicBarrier barrierRender = new CyclicBarrier(2);
        renderer.setBarrierForSimulationMaster(barrierRender);
        ReportingControlPanel statsPanel = null;
        if (!Renderer.getInstance().isConsoleStart())
            statsPanel = VanetSimStart.getMainControlPanel().getReportingPanel();
        long timeOld = 0;
        long timeNew = 0;
        long timeDistance = 0;
        boolean consoleStart = Renderer.getInstance().isConsoleStart();
        KnownVehiclesList.setTimePerStep_(TIME_PER_STEP);
        if (generalLogWriterActivated_) {
            GeneralLogWriter.setLogPath(System.getProperty("user.dir"));
            GeneralLogWriter.log(generalLogWriterHeader_);
        }
        while (true) {
            try {
                if (running_ || doOneStep_) {
                    renderTime = System.nanoTime();
                    barrierRender.reset();
                    while (workers_ == null) {
                        if (Map.getInstance().getReadyState() == true && Scenario.getInstance().getReadyState() == true) {
                            if (Runtime.getRuntime().availableProcessors() < 2)//nb processeur = 1
                                threads = 1;
                            else
                                threads = Runtime.getRuntime().availableProcessors() * 2;//nb processeur > 1
                            long max_heap = Runtime.getRuntime().maxMemory() / 1048576;
                            ErrorLog.log(Messages.getString("SimulationMaster.preparingSimulation") + threads + Messages.getString("SimulationMaster.threadsDetected") + max_heap + Messages.getString("SimulationMaster.heapMemory"), 3, SimulationMaster.class.getName(), "run", null);
                            workers_ = createWorkers(TIME_PER_STEP, threads);
                        } else {
                            sleep(50);
                        }
                    }
                     if (SimulationMaster.EndSimulation == true) {
                        Renderer.getInstance().notifySimulationRunning(false);
                        adhoc.Adhoc.running_ADHOC = false;
                        JOptionPane.showMessageDialog(null, "FIN DE SIMULATION");
                        System.out.println("FIN DE SIMULATION");
                        
                    }
                    
                    //timePassed = renderer.getTimePassed();
                    time = renderer.getTimePassed() + TIME_PER_STEP;
                    //System.out.println(" SimulationMaster.java : TimePassed == "+timePassed+" vs time (en fct de TIME_PER_STEP) == "+time);
                    
                    eventList_.processEvents(time);
                    barrierStart_.await();
                    barrierFinish_.await();
                    KnownVehiclesList.setTimePassed(time);
                     
                    
                    renderer.setTimePassed(time);
                    KnownEventSourcesList.setTimePassed(time);
                    if (eventSpotCountdown_ < time)
                        eventSpotCountdown_ = EventSpotList.getInstance().doStep(time);
                    if (!jumpTimeMode_) {
                        renderer.ReRender(false, true);
                        statsPanel.checkUpdates(TIME_PER_STEP);
                        Thread.yield();
                        barrierRender.await(3, TimeUnit.SECONDS);
                        renderTime = ((System.nanoTime() - renderTime) / 1000000);
                        if (renderTime > 0)
                            renderTime = targetStepTime_ - renderTime;
                        else
                            renderTime = targetStepTime_ + renderTime;
                        if (renderTime > 0 && renderTime <= targetStepTime_) {
                            sleep(renderTime);
                        }
                    } else {
                        if (consoleStart && time % 5000 == 0) {
                            timeNew = System.currentTimeMillis();
                            timeDistance = timeNew - timeOld;
                            System.out.println("Time:::" + timeDistance);
                            timeOld = timeNew;
                            System.out.println(time);
                        }
                        if (time >= jumpTimeTarget_) {
                            jumpTimeTarget_ = -1;
                            jumpTimeMode_ = false;
                            stopThread();
                            if (consoleStart) {
                                System.out.println("Time:" + new Date());
                                System.out.println(Messages.getString("ConsoleStart.SimulationEnded"));
 
                                System.exit(0);
                            }
                            if (!consoleStart) {
                                VanetSimStart.setProgressBar(false);
                                renderer.ReRender(false, true);
                                statsPanel.checkUpdates(TIME_PER_STEP);
                            }
                        }
                    }
                    if (doOneStep_) {
                        doOneStep_ = false;
                        renderer.notifySimulationRunning(false);
                    }
                } else {
                    sleep(50);
                }
            } catch (Exception e) {
            }
            ;
        }
    }

    public boolean isSimulationRunning() {
        return running_;
    }

    public boolean isGuiEnabled() {
        return guiEnabled;
    }

    public void setGuiEnabled(boolean guiEnabled) {
        this.guiEnabled = guiEnabled;
    }

    public static int getEventSpotCountdown_() {
        return eventSpotCountdown_;
    }

    public static void setEventSpotCountdown_(int eventSpotCountdown) {
        eventSpotCountdown_ = eventSpotCountdown;
    }

    public void writeToFile(String text, String filePath, String fileName) {
        System.out.println("writing file...");
        System.out.println(filePath + "/" + System.currentTimeMillis() + "_" + fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath + "/" + System.currentTimeMillis() + "_" + fileName));
            if (text != null) {
                out.write(text);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void vehicleFluctuation() throws Exception {
        Region[][] Regions = Map.getInstance().getRegions();
        int Region_max_x = Map.getInstance().getRegionCountX();
        int Region_max_y = Map.getInstance().getRegionCountY();
        int i, j;
        for (i = 0; i < Region_max_x; ++i) {
            for (j = 0; j < Region_max_y; ++j) {
                Vehicle[] vehiclesArray = Regions[i][j].getVehicleArray();
                for (int k = 0; k < vehiclesArray.length; ++k) {
                    Vehicle vehicle = vehiclesArray[k];
                    vehicle.getKnownEventSourcesList_().writeOutputFile();
                }
            }
        }
        FileInputStream fstream = new FileInputStream(GeneralLogWriter.getFile_());
        DataInputStream in = new DataInputStream(fstream);
        String line = "";
        int timestamp = 0;
        int tmpTime = 0;
        String[] lineSplit = null;
        int createCounter = 0;
        int updateCounter = 0;
        int fakeMessageInterval = Vehicle.getFakeMessagesInterval_();
        int[] amountOfSecondContactsInPercent = new int[6];
        ArrayList<String> theIDs = new ArrayList<String>();
        int maxUpdatesCounter = 0;
        String[] updateArray;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    lineSplit = line.split(":");
                    if (lineSplit != null && lineSplit.length > 1 && lineSplit[0].substring(0, 3).equals("***")) {
                        updateArray = lineSplit[1].split("#");
                        for (int o = 0; o < updateArray.length; o++) {
                            if (Integer.parseInt(updateArray[o]) > maxUpdatesCounter)
                                maxUpdatesCounter = Integer.parseInt(updateArray[o]);
                        }
                    } else if (lineSplit != null && lineSplit.length > 2) {
                        if (!theIDs.contains(lineSplit[1]))
                            theIDs.add(lineSplit[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while doing accumulating data");
            e.printStackTrace();
        }
        for (String senderID : theIDs) {
            fstream = new FileInputStream(GeneralLogWriter.getFile_());
            in = new DataInputStream(fstream);
            updateCounter = 0;
            createCounter = 0;
            line = "";
            timestamp = 0;
            tmpTime = 0;
            reader = new BufferedReader(new InputStreamReader(in));
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 0) {
                        lineSplit = line.split(":");
                        if (!lineSplit[0].substring(0, 3).equals("***") && senderID.equals(lineSplit[1])) {
                            tmpTime = Integer.parseInt(lineSplit[0]);
                            if (lineSplit != null && lineSplit.length > 2) {
                                if (tmpTime >= (timestamp + 1000)) {
                                    if (createCounter > 0 || updateCounter > 0) {
                                        double percentage = (double) updateCounter / (double) (createCounter + updateCounter);
                                        if (percentage == 0) {
                                            amountOfSecondContactsInPercent[0]++;
                                        } else if (percentage <= 0.2) {
                                            amountOfSecondContactsInPercent[1]++;
                                        } else if (percentage <= 0.4) {
                                            amountOfSecondContactsInPercent[2]++;
                                        } else if (percentage <= 0.6) {
                                            amountOfSecondContactsInPercent[3]++;
                                        } else if (percentage <= 0.8) {
                                            amountOfSecondContactsInPercent[4]++;
                                        } else if (percentage <= 1) {
                                            amountOfSecondContactsInPercent[5]++;
                                        } else
                                            System.out.println("error");
                                    }
                                    updateCounter = 0;
                                    createCounter = 0;
                                    while (tmpTime >= (timestamp + 1000 + fakeMessageInterval)) {
                                        timestamp += (fakeMessageInterval + 40);
                                    }
                                    timestamp = tmpTime;
                                }
                                if (lineSplit[2].equals("update"))
                                    updateCounter++;
                                else if (lineSplit[2].equals("create"))
                                    createCounter++;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while doing accumulating data");
                e.printStackTrace();
            }
        }
        int eventsWithoutZeroTotal = 0;
        for (int g = 0; g < amountOfSecondContactsInPercent.length; g++) eventsWithoutZeroTotal += amountOfSecondContactsInPercent[g];
        String percentageStats = "";
        String normalStats = "";
        for (int h = 0; h < amountOfSecondContactsInPercent.length; h++) {
            percentageStats += (double) amountOfSecondContactsInPercent[h] / eventsWithoutZeroTotal * 100 + "\n";
            normalStats += amountOfSecondContactsInPercent[h] + "\n";
        }
        fstream = new FileInputStream(GeneralLogWriter.getFile_());
        in = new DataInputStream(fstream);
        line = "";
        lineSplit = null;
        int creates = 0;
        int updates = 0;
        int[] updatesArray = new int[maxUpdatesCounter + 1];
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    lineSplit = line.split(":");
                    if (lineSplit != null && lineSplit.length > 1 && lineSplit[0].substring(0, 3).equals("***")) {
                        updateArray = lineSplit[1].split("#");
                        for (int o = 0; o < updateArray.length; o++) {
                            creates++;
                            updates += Integer.parseInt(updateArray[o]);
                            updatesArray[Integer.parseInt(updateArray[o])]++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while doing accumulating data");
            e.printStackTrace();
        }
        int tmpCreates = creates;
        String stats = creates + "\n";
        for (int l = 0; l < updatesArray.length; l++) {
            tmpCreates -= updatesArray[l];
            stats += tmpCreates + "\n";
        }
        String statsInPercent = "1\n";
        tmpCreates = creates;
        for (int l = 0; l < updatesArray.length; l++) {
            tmpCreates -= updatesArray[l];
            statsInPercent += ((double) tmpCreates / creates) * 100 + "\n";
        }
        double[] knownVehicleData = returnAverageKnownVehiclesAndTimes();
        if (Scenario.getInstance().getScenarioName().equals(""))
            writeToFile("\n\nAverage known vehicles:" + knownVehicleData[0] + "\nAverage known time:" + knownVehicleData[1] + "\nVehiclesWithContact:" + creates + ":EventsSeen:" + (updates + creates) + ":UpdatesTotal:" + updates + ":AverageEventsPerLoggedVehicle:" + ((double) (updates + creates) / (double) creates) + "\n\nUpdateStats:\n" + stats + "\n\nUpdateStatsInPercent:\n" + statsInPercent + "\n\nPercentageStats:\n" + percentageStats + "\n\n\nNormalStats:\n" + normalStats, System.getProperty("user.dir"), "neighbordata.txt");
        else
            writeToFile("\n\nAverage known vehicles:" + knownVehicleData[0] + "\nAverage known time:" + knownVehicleData[1] + "\nVehiclesWithContact:" + creates + ":EventsSeen:" + (updates + creates) + ":UpdatesTotal:" + updates + ":AverageEventsPerLoggedVehicle:" + ((double) (updates + creates) / (double) creates) + "\n\nUpdateStats:\n" + stats + "\n\nUpdateStatsInPercent:\n" + statsInPercent + "\n\nPercentageStats:\n" + percentageStats + "\n\n\nNormalStats:\n" + normalStats, System.getProperty("user.dir"), Scenario.getInstance().getScenarioName() + ".txt");
    }

    public void createAndSaveSpamData() {
        int spamAmount = 0;
        int fakeMessageCounter = 0;
        Region[][] Regions = Map.getInstance().getRegions();
        int Region_max_x = Map.getInstance().getRegionCountX();
        int Region_max_y = Map.getInstance().getRegionCountY();
        int i, j;
        for (i = 0; i < Region_max_x; ++i) {
            for (j = 0; j < Region_max_y; ++j) {
                Vehicle[] vehiclesArray = Regions[i][j].getVehicleArray();
                for (int k = 0; k < vehiclesArray.length; ++k) {
                    Vehicle vehicle = vehiclesArray[k];
                    vehicle.getKnownEventSourcesList_().clear();
                    spamAmount += vehicle.getKnownEventSourcesList_().getSpamCount();
                    fakeMessageCounter += vehicle.getFakeMessagesCreated_();
                }
            }
        }
        try {
            writeToFile("Fake Messages:" + fakeMessageCounter + ":SpamDectected:" + spamAmount, System.getProperty("user.dir"), "_spammerData_" + Scenario.getInstance().getScenarioName().substring(0, (Scenario.getInstance().getScenarioName().length() - 4)) + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int spam() throws Exception {
        FileInputStream fstream = new FileInputStream(GeneralLogWriter.getFile_());
        DataInputStream in = new DataInputStream(fstream);
        int updatesCounter = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while (reader.readLine() != null) {
                updatesCounter++;
            }
        } catch (IOException e) {
            System.out.println("Error while doing accumulating data");
            e.printStackTrace();
        }
        reader.close();
        return updatesCounter;
    }

    public void writeAverageKnownVehiclesTime() throws Exception {
        FileInputStream fstream = new FileInputStream(GeneralLogWriter.getFile_());
        DataInputStream in = new DataInputStream(fstream);
        String line = "";
        double accumulatedTime = 0;
        double knownVehiclesCounter = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                if (line != null && !line.equals("")) {
                    knownVehiclesCounter++;
                    accumulatedTime += Long.parseLong(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while doing accumulating data");
            e.printStackTrace();
        }
        try {
            writeToFile(String.valueOf(accumulatedTime / knownVehiclesCounter), System.getProperty("user.dir"), "_knownVehiclesTimeData_" + Scenario.getInstance().getScenarioName().substring(0, (Scenario.getInstance().getScenarioName().length() - 4)) + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] returnAverageKnownVehiclesAndTimes() {
        double[] returnArray = new double[2];
        Region[][] regions = Map.getInstance().getRegions();
        Vehicle[] vehicles;
        Vehicle vehicle;
        int i, j, k;
        double knownTimeTotal = 0;
        double counter = 0;
        double knownVehiclesCounter = 0;
        double knownVehiclesTotal = 0;
        for (i = 0; i < regions.length; ++i) {
            for (j = 0; j < regions[i].length; ++j) {
                vehicles = regions[i][j].getVehicleArray();
                for (k = 0; k < vehicles.length; ++k) {
                    vehicle = vehicles[k];
                    if (vehicle.isActive() && vehicle.isWiFiEnabled()) {
                        ++counter;
                        knownVehiclesTotal += vehicle.getKnownVehiclesList().getSize();
                        KnownVehicle next;
                        for (int l = 0; l < KnownVehiclesList.getHashSize(); ++l) {
                            next = vehicle.getKnownVehiclesList().getFirstKnownVehicle()[l];
                            while (next != null) {
                                knownVehiclesCounter++;
                                knownTimeTotal += (next.getLastUpdate() - next.getFirstContact_());
                                next = next.getNext();
                            }
                        }
                    }
                }
            }
        }
        returnArray[0] = (knownVehiclesTotal / counter);
        returnArray[1] = (knownTimeTotal / knownVehiclesCounter);
        return returnArray;
    }
}
