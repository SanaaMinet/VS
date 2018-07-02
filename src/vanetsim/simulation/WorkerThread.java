package vanetsim.simulation;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.swing.JOptionPane;
import vanetsim.ErrorLog;
import vanetsim.gui.Renderer;
import vanetsim.gui.controlpanels.ClusteringControlPanel;
import vanetsim.localization.Messages;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.routing.WayPoint;
import vanetsim.scenario.Vehicle;
 

public final class WorkerThread extends Thread {

    private final Region[] ourRegions_;
    
    private int numPass = 1;

    private final int timePerStep_;

    private final LinkedHashSet<Integer> changedRegions_ = new LinkedHashSet<Integer>(16);

    private CyclicBarrier barrierStart_;

    private CyclicBarrier barrierDuringWork_;

    private CyclicBarrier barrierFinish_;

    private static int simulationMode_ = 1;
    
    //public static boolean EndSimulation = false;

    public WorkerThread(Region[] ourRegions, int timePerStep) 
    {
        setName("Worker startX:" + ourRegions[0].getX() + " startY:" + +ourRegions[0].getY());
        ourRegions_ = ourRegions;
        timePerStep_ = timePerStep;
        ErrorLog.log(Messages.getString("WorkerThread.workerCreated") + ourRegions_.length + Messages.getString("WorkerThread.regions"), 1, this.getName(), "Worker constructor", null);
    }

    public void setBarriers(CyclicBarrier barrierStart, CyclicBarrier barrierDuringWork, CyclicBarrier barrierFinish) {
        barrierStart_ = barrierStart;
        barrierDuringWork_ = barrierDuringWork;
        barrierFinish_ = barrierFinish;
    }

    public void addChangedRegion(int i) {
        synchronized (changedRegions_) {
            changedRegions_.add(Integer.valueOf(i));
        }
    }

    public void run() {
        
        
        int i, j, length;
        int ourRegionsLength = ourRegions_.length;
        System.out.println("ourRegions_.length"+ourRegions_.length);
        Vehicle[][] vehicles = new Vehicle[ourRegionsLength][];
        Vehicle[] vehicleSubarray;
        Vehicle vehicle;
        int tmpTimePassed = 999999999;
        int tmpTimePassedSaved = 99999999;
        int silentPeriodDuration = Vehicle.getTIME_OF_SILENT_PERIODS();
        int silentPeriodFrequency = Vehicle.getTIME_BETWEEN_SILENT_PERIODS();
 
        Iterator<Integer> changedRegionIterator;
        int tmp;//nbv=0;
        
        for (i = 0; i < ourRegionsLength; ++i) 
        {
            ourRegions_[i].createBacklink(this, i);
            ourRegions_[i].calculateJunctions();
            vehicles[i] = ourRegions_[i].getVehicleArray();
            //nbv = nbv + vehicles[i].length;
            
        }
        
        //System.out.println("NBVEHICULES =============================================== "+nbv);
       
        boolean communicationEnabled = Vehicle.getCommunicationEnabled();
        boolean beaconsEnabled = Vehicle.getBeaconsEnabled();
        boolean recyclingEnabled = Vehicle.getRecyclingEnabled();
        boolean idsEnabled = Vehicle.isIdsActivated();
        //String vehicleStat = Vehicle.getVehicleStat();
        
        while (barrierStart_ == null || barrierDuringWork_ == null || barrierFinish_ == null) {
            try {
                sleep(50);
            } catch (Exception e) {
            }
        }
        if (simulationMode_ == 1) {
            tmpTimePassed = Renderer.getInstance().getTimePassed();
          //  System.out.println("simulationMode = "+simulationMode_);
            
            while ((tmpTimePassed/timePerStep_)<(ClusteringControlPanel.tsim*1000)) { // return cdt timePassed
                  tmpTimePassed = Renderer.getInstance().getTimePassed();      //System.out.println(" NumPass WorkerThread run() == "+numPass);
                //System.out.println("tmpTimePassed == "+tmpTimePassed+"  timePerStep_ == "+  timePerStep_+"  tsim =="+ClusteringControlPanel.tsim);
                if (tmpTimePassed / timePerStep_ >= ClusteringControlPanel.tsim * 1000) {
                    SimulationMaster.EndSimulation = true;
                    System.out.println("END SIMULATION = "+ClusteringControlPanel.tsim);
                }
                adhoc.Adhoc.compteur = tmpTimePassed/timePerStep_;
                //System.out.println("Compteur en fct de timePerStep "+adhoc.Adhoc.compteur);
                  numPass++;
                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }
                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {
                }
                try {
                   
                    int myTimePassed = Renderer.getInstance().getTimePassed();
                    int pos=-1; 
                    
                    //if(myTimePassed < adhoc.Adhoc.tsim*timePerStep_){
                    for (i = 0; i < ourRegionsLength; ++i) 
                    {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) 
                        {
                            //vehicleSubarray[j].adjustSpeed(timePerStep_);
                           //System.out.println("vehicleSubarray[j].steadyID_  === "+vehicleSubarray[j].steadyID_); 
                            int arrivalTime =  vehicleSubarray[j].getArrivalTime();
                            //System.err.println(vehicleSubarray[j].steadyID_+"    : arrivalTime : "+arrivalTime+"  ====>  myTimePassed : "+myTimePassed);
                          
                            if (myTimePassed == arrivalTime) 
                            { // && vehicleSubarray[j].getVehicleStat().equals("Inactif")) {
                                //System.out.println("j = "+j+"lenght= "+length+"ArrivalTime=myTimePassed="+(arrivalTime/40)+" vehicleSubarray[j]="+vehicleSubarray[j].steadyID_);
                                vehicleSubarray[j].setVehicleStat("Actif");
                                vehicleSubarray[j].setWiFiEnabled(true);
                                //----------------------------------------------
                               
                                pos = Vehicle.GetIndice(ClusteringControlPanel.S,vehicleSubarray[j].steadyID_);
                                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt("Actif", pos, 7);
                                vanetsim.scenario.Vehicle.TV[pos].etat ="Actif";
                                //----------------------------------------------
                                vehicleSubarray[j].adjustSpeed(timePerStep_);
                            }
                        }
                    }
 
                    barrierDuringWork_.await();
                    /*}else
                    {
                    // vanetsim.simulation.SimulationMaster.running_ = false;
                    vanetsim.gui.Renderer.getInstance().notifySimulationRunning(false);
                    adhoc.Adhoc.running_ADHOC = true;
                    }*/
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }
                if (communicationEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                    
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
 
                            }
                        }
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (communicationEnabled && beaconsEnabled) {
                    try {
                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null ) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive())
                            {
                                vehicleSubarray[j].move(timePerStep_);
                               // vehicleSubarray[j].getID();
                                           // System.out.println("111111111111111111111111111111111111 : "+steadyID_);
                   if(ClusteringControlPanel.S.getColumnCount()!=0)
                   {
                      //System.out.println("j ====== : "+j+"/"+vehicleSubarray.length+"  =====> steadyID_ : "+vehicleSubarray[j].steadyID_+"    ===> "+vehicleSubarray[j].getVehicleID()+"   X : "+vehicleSubarray[j].getX()+"   Y : "+vehicleSubarray[j].getY());
                   
                    int id = Vehicle.GetIndice(ClusteringControlPanel.S,vehicleSubarray[j].steadyID_);
            /* WayPoint nextDestination = vehicleSubarray[j].getDestinations().peekFirst();
            if (nextDestination != null) 
//          System.out.println("Vehicule : "+vehicleSubarray[j].steadyID_+" Next position "+nextDestination.getX()+"    "+nextDestination.getY());
//          */
                   // System.out.println("X : "+vehicleSubarray[j].getX()+"   Y : "+vehicleSubarray[j].getY()+"   id : "+id+" : VehicleID : "+vehicleSubarray[j].getVehicleID());
                   if(id!=-1)
                   {
                       vanetsim.scenario.Vehicle.TV[id].posx    = vehicleSubarray[j].getX();
                       vanetsim.scenario.Vehicle.TV[id].posy    = vehicleSubarray[j].getY();
                       vanetsim.scenario.Vehicle.TV[id].vitesse = vehicleSubarray[j].getCurSpeed();
                       vanetsim.scenario.Vehicle.TV[id].ActuelPosition = vehicleSubarray[j].getCurPosition();
                    //---------------------------------Position prédite------------------------------------------                   
                    /*vehicleSubarray[j].PredictPosition(timePerStep_);
                       vanetsim.scenario.Vehicle.TV[id].positionPredite = vehicleSubarray[j].predictedPosition;
                    //vanetsim.scenario.Vehicle.TV[id].posPx    = vehicleSubarray[j].predictedPosition; APROVE Predicted Position
                    System.out.println("Vehicule : "+vehicleSubarray[j].steadyID_+" predicted position = "+vehicleSubarray[j].predictedPosition);*/
                    //--------------------------------------------------------------------------------------------
                    
                    ClusteringControlPanel.S.setValueAt(""+vanetsim.scenario.Vehicle.TV[id].ActuelPosition, id, 8);
                    adhoc.Adhoc.S[id][8] = ""+vanetsim.scenario.Vehicle.TV[id].ActuelPosition;          
                               
                    ClusteringControlPanel.S.setValueAt(""+vanetsim.scenario.Vehicle.TV[id].posx, id, 9);
                    adhoc.Adhoc.S[id][9] = ""+vanetsim.scenario.Vehicle.TV[id].posx;
                    
                    ClusteringControlPanel.S.setValueAt(""+vanetsim.scenario.Vehicle.TV[id].posy, id, 10);
                    adhoc.Adhoc.S[id][10] = ""+vanetsim.scenario.Vehicle.TV[id].posy;
                                      
                    ClusteringControlPanel.S.setValueAt(""+vanetsim.scenario.Vehicle.TV[id].vitesse, id, 11);
                    adhoc.Adhoc.S[id][11] = ""+vanetsim.scenario.Vehicle.TV[id].vitesse; 
                    

                    
                    
                   
                   }
                    
                   }
                    //----------------------------------------------------------
           // System.out.println("333333333333333333333333333333333333333333333");
                                
                                
                            }
                            
                            
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();
                        }
                    }
                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }
                try {
                    Node[] tmpNodes = null;
                    for (i = 0; i < ourRegions_.length; i++) {
                        tmpNodes = ourRegions_[i].getNodes();
                        for (j = 0; j < tmpNodes.length; j++) {
                            if (tmpNodes[j].isHasTrafficSignal_() && tmpNodes[j].getJunction() != null && tmpNodes[j].getJunction().getNode().getTrafficLight_() != null) {
                                tmpNodes[j].getJunction().getNode().getTrafficLight_().changePhases(timePerStep_);
                            }
                        }
                    }
                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }
            }
        } else if (simulationMode_ == 2) {
            //System.out.println("simulationMode = "+simulationMode_);
            while (true) {
                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }
                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {
                }
                for (i = 0; i < ourRegionsLength; ++i) {
                    vehicleSubarray = vehicles[i];
                    length = vehicleSubarray.length;
                    for (j = 0; j < length; ++j) {
                        vehicleSubarray[j].adjustSpeedWithIDM(timePerStep_);
                    }
                }
 
                try {
                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }
                if (communicationEnabled) {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                vehicle.sendMessages();
                            }
                        }
                    }
 
                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (idsEnabled) {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
 
                        }
                    }
                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (communicationEnabled && beaconsEnabled) {
                    if (Vehicle.isSilentPeriodsOn()) {
                        tmpTimePassed = Renderer.getInstance().getTimePassed();
                        if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                            tmpTimePassedSaved = tmpTimePassed;
                            Vehicle.setSilent_period(true);
                        } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                            Vehicle.setSilent_period(false);
                    }
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                vehicle.sendBeacons();
                            }
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null) {
                                vehicle.sendEncryptedBeacons();
                            }
                        }
                    }
 
                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                for (i = 0; i < ourRegionsLength; ++i) {
                    vehicleSubarray = vehicles[i];
                    length = vehicleSubarray.length;
                    for (j = 0; j < length; ++j) {
                        if (vehicleSubarray[j].isActive())
                            vehicleSubarray[j].move(timePerStep_);
                        else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                            vehicleSubarray[j].reset();
                    }
                }
                try {
                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }
            }
        }
        if (simulationMode_ == 3) {
            //System.out.println("simulationMode = "+simulationMode_);
            while (true) {
                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }
                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {
                }
                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicleSubarray[j].adjustSpeedWithSJTUTraceFiles(timePerStep_);
                        }
                    }
 
                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }
                if (communicationEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
 
                            }
                        }
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (communicationEnabled && beaconsEnabled) {
                    try {
                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null ) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive())
                                vehicleSubarray[j].move(timePerStep_);
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();
                        }
                    }
                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }
            }
        }
        if (simulationMode_ == 4) {
            //System.out.println("simulationMode = "+simulationMode_);
            while (true) {
                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }
                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {
                }
                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicleSubarray[j].adjustSpeedWithSanFranciscoTraceFiles(timePerStep_);
                        }
                    }
 
                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }
                if (communicationEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
 
                            }
                        }
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                if (communicationEnabled && beaconsEnabled) {
                    try {
                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null ) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }
 
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {
                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }
                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive())
                                vehicleSubarray[j].move(timePerStep_);
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();
                        }
                    }
                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {
                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }
            }
        }
        for (i = 0; i < ourRegionsLength; ++i) {
            ourRegions_[i].createBacklink(null, -1);
        }
        ErrorLog.log(Messages.getString("WorkerThread.workerExited"), 1, this.getName(), "run", null);

    }

    public static int getSimulationMode_() {
        return simulationMode_;
    }

    public static void setSimulationMode_(int simulationMode) {
        simulationMode_ = simulationMode;
    }
}
