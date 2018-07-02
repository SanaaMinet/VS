/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adhoc;

/**
 *
 * @author saliha-benkerdagh
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import static vanetsim.simulation.SimulationMaster.TIME_PER_STEP;

/**
 *
 * @author saliha-benkerdagh
 */
public class Adhoc {
//==============================================================================
    public static double vw1,vw2,vw3,vw4;

public static double NbAvg, Ravg, Nv, sig, Phi, e1,e2, rtMax, Dl, Lav,Nt,density;
public static int Nl;
//==============================================================================
    public static Thread Executer;
    public int ActualTime = 999999999;
    public static int round = 0;

//==============================================================================
    public static int IdSrc, IdCbl, idxlastTask;
    public static Groupes GRS[];
    public static Algorithms.Mozo MOZ[];
    public static Algorithms.Mozo.ConstructCLVTree CLV[];
    static Path path = Paths.get("Directory_" + date_courante());
    static boolean savefc;
    public static boolean running_ADHOC = false;
    public static boolean AdhocFirstLaunch = false;
    
    public static boolean SRemplie = false; // CreateRandom n'est pas appelé
    static Random rnd = new Random();
    static String t[] = new String[4];
    //static String Directory;
    static OutputStream fst;//sauvegarde vwca
    static PrintWriter outt;//sauvegarde vwca

    static OutputStream fst1;//sauvegarde FC
    static PrintWriter outt1;//sauvegarde FC

    static OutputStream fst2;//sauvegarde APV
    static PrintWriter outt2;//sauvegarde APV

    static OutputStream fst5;//sauvegarde APV
    static PrintWriter outt5;//sauvegarde APV
    
    static DefaultTableModel PN = new DefaultTableModel();

    static DefaultTableModel trp = new DefaultTableModel();
    static DefaultTableModel vr = new DefaultTableModel();
    static DefaultTableModel lv = new DefaultTableModel();
    static DefaultTableModel TableFit = new DefaultTableModel();
    static DefaultTableModel DIS = new DefaultTableModel();

    static DefaultTableModel MB = new DefaultTableModel();
    static DefaultTableModel DIST = new DefaultTableModel();
    static DefaultTableModel GROUPE = new DefaultTableModel();
    static DefaultTableModel GROUPE1 = new DefaultTableModel();

    static DefaultTableModel ST = new DefaultTableModel();
    static DefaultTableModel RT = new DefaultTableModel();
    static DefaultTableModel PR2 = new DefaultTableModel();
    static DefaultTableModel PR3 = new DefaultTableModel();
    public static DefaultListModel bestPathFC = new DefaultListModel();
    public static int srcCH, cblCH, posSrcCH, posCblCH;
    static int cas = 0;
//-------------------------APROVE-----------------------------------------------------
    static DefaultTableModel ss = new DefaultTableModel();
    static DefaultTableModel rr = new DefaultTableModel();
    static DefaultTableModel aa = new DefaultTableModel();
    static DefaultTableModel pp = new DefaultTableModel();
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
    static int Dt1, Dt2, Dt3, Dt4, Dt5;

    static DefaultListModel TEX1 = new DefaultListModel();
    static DefaultListModel TEX1M = new DefaultListModel();

    static DefaultListModel TEX2 = new DefaultListModel();
    static DefaultListModel TEX3 = new DefaultListModel();
    static DefaultListModel TEX4 = new DefaultListModel();

//------------------------------------------------------------------------------    
    //static DefaultTableModel NodeLifetimeStates = new DefaultTableModel();// save all states duration for FC ou pour tous ??
//    static DefaultTableModel APVLifetimeStates = new DefaultTableModel();// save all states duration for APROVE
//    static DefaultTableModel VWCALifetimeStates = new DefaultTableModel();// save all states duration for VWCA
//    static DefaultTableModel MZLifetimeStates = new DefaultTableModel();// save all states duration for MoZo
//----------------------------------------------------
    static DefaultTableModel s1 = new DefaultTableModel();//Instant + Num of Ch for FClustering
    static DefaultTableModel s2 = new DefaultTableModel();//Instant + Num of Ch for APROVE
    static DefaultTableModel s3 = new DefaultTableModel();//Instant + Num of Ch for WCA
    static DefaultTableModel s4 = new DefaultTableModel();//Instant + Num of Ch for VWCA
    static DefaultTableModel s5 = new DefaultTableModel();//Instant + Num of Ch for MOZO

    static DefaultTableModel TSC = new DefaultTableModel();//Instant + Num of Ch for MOZO
//------------------------------------------------------------------------------
    public static boolean TBFM[][];//table pour vérifier le lancement de broadcast, formation  maintenance
//------------------------------------------------------------------------------
    static double alpha =0.25, beta=0.25, gamma=0.25, sigma=0.25, lambda=0.25;
    public static int avi, portee, porteeMax, porteeRSU, porteeCA, nbv, tsim;

    //--------------------------------------------------------------------------
    static int LV[];

    static boolean VB[];
    static int neighbour[][];
    static boolean visit[][];
    static int NBS[];
    static int FAIT[];
    static String PATH[];
    static int periode, periode1, periode2, periode3, periode4;
    //static int ComptTache, round;
    public static int Lvactif1[], Lvactif3[], Lvactif3Old[];
    //static int Lvactif2[],Lvactif4[],;
    static int LvactifOld[];
    static int posBFM;
    //static int NumExec;

    

    //public static Simulation.RSU_ TRS[];
    //public static Simulation.CertificateAuthentification TCA[];
    public static Algorithms.WCA.Cluster TC[];

   // public static vanetsim.scenario.Vehicle  veh = new vanetsim.scenario.Vehicle();
    public static Algorithms.FitnessClustering AFC = new Algorithms.FitnessClustering();
    public static Algorithms.APROVE AAPV = new Algorithms.APROVE();
    public static Algorithms.WCA AWCA = new Algorithms.WCA();
    public static Algorithms.VWCA AVWCA = new Algorithms.VWCA();

    public static String FilenameGroupVWCA;
    public static String FilenameGroupAPV;
    public static String FilenameGroupMOZO;
    public static String FilenameGroupFC;
    public static String LTNdState;
    public static String PourcentNdState;

    public static Algorithms.WCA.Cluster CLS = new Algorithms.WCA.Cluster();

    public static int compteur = -1, periodeMozo = -1;
    static double TMozoDeb, TMzoFin;
    static Thread thd, ThdActif, ThdGlobal, thMetrique, ThdAprove, ThVwca, ThMozo;

    static boolean depart = false;
    static boolean departFC = false;
    static boolean departWCA = false;
    static boolean departVWCA = false;
    static boolean departAPROV = false;
    static boolean departMOZO = false;
//------------------------------------------------------------------------------
    public static int NbvActif, NbvInactif, NbvHors;
    public static double w1, w2, w3;
    public static int K;
    public static long Tdep;

//------------------------------------------------------------------------------
    public static double Tcoefficient[][] = new double[31][4];
    public static double Tlambda[] = new double[3];
    public static int TSim[] = new int[3];

    public static String S[][];

    public static int NDLIFETIMESTATE[][]; // temps des états pour chaque véhicule et sa durée de vie
    public static double PourcentLTSTATE[][];// pourcentage des états pour chaque véhicule
    //-------------------------------------------------------
    public static int NumTotalStateCh; // num de noeuds qui ont eu l'état CH
    public static double PourcentTotalStateCh;// pourcentage de noeuds qui ont eu l'état CH par rapport à leurs durées de vie
    //-------------------------------------------------------
    public static int NumTotalStateSch; // num de noeuds qui ont eu l'état SCH
    public static double PourcentTotalStateSch;
    //-------------------------------------------------------
    public static int NumTotalStateMN; // num de noeuds qui ont eu l'état MN
    public static double PourcentTotalStateMN;
    //-------------------------------------------------------
    public static int NumTotalStateSM; // num de noeuds qui ont eu l'état SM
    public static double PourcentTotalStateSM;
    //-------------------------------------------------------

    /*public static int TEVENT[][];//temps event
     public static int pevent = 30;//priod event*/
    public static int pcolect = 5;//priod event
    //--------------------------------------------------------------------------
    public static int TChoixDestCBL;// 
    public static boolean DestChoisi, sauvegarde;
    //static Mutex mutex = new Mutex();
    //---------------------------------------------------------------
    static JFileChooser DureeVieState = new JFileChooser();

    static public void SaveLTState(String nomFichier, int[][] L) {
        try {
            DureeVieState.setAcceptAllFileFilterUsed(true);
            DureeVieState.getAcceptAllFileFilter();
            //========================================
            OutputStream fs = new FileOutputStream(nomFichier);
            PrintWriter out = new PrintWriter(fs);

            for (int i = 0; i < L.length; i++) {
                for (int j = 0; j < L[i].length; j++) {
                    //  ligne = ligne + L.getElementAt(i);
                    out.print(L[i][j] + " ");

                }
                out.println();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void SaveLTState(String nomFichier, double[][] L) {
        try {
            DureeVieState.setAcceptAllFileFilterUsed(true);
            DureeVieState.getAcceptAllFileFilter();
            //========================================
            OutputStream fs = new FileOutputStream(nomFichier);
            PrintWriter out = new PrintWriter(fs);

            for (int i = 0; i < L.length; i++) {
                for (int j = 0; j < L[i].length; j++) {
                    //  ligne = ligne + L.getElementAt(i);
                    out.print(L[i][j] + " ");
                }
                out.println();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//------------------------------------------------------------

    public static void CalculNDLifeTimeStat() {
        NDLIFETIMESTATE = new int[S.length][6];
        PourcentLTSTATE = new double[S.length][6];

        NumTotalStateCh = 0; // num de noeuds qui ont eu l'état CH
        PourcentTotalStateCh = 0;// pourcentage de noeuds qui ont eu l'état CH
        //-------------------------------------------------------
        NumTotalStateSch = 0; // num de noeuds qui ont eu l'état SCH
        PourcentTotalStateSch = 0;
        //-------------------------------------------------------
        NumTotalStateMN = 0; // num de noeuds qui ont eu l'état MN
        PourcentTotalStateMN = 0;
        //-------------------------------------------------------
        NumTotalStateSM = 0; // num de noeuds qui ont eu l'état SM
        PourcentTotalStateSM = 0;
        //-------------------------------------------------------

        int DtCh, DtSch, DtMn, DtSm;

        for (int i = 0; i < S.length; i++) {
            DtCh = 0;
            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount(); j++) {
                if (!vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).equals("-1")) {
                    DtCh = DtCh + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).toString()));
                }
            }

            DtSch = 0;
            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount(); j++) {
                if (!vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).equals("-1")) {
                    DtSch = DtSch + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).toString()));
                }
            }

            DtMn = 0;
            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount(); j++) {
                if (!vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).equals("-1")) {
                    DtMn = DtMn + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).toString()));
                }
            }

            DtSm = 0;
            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount(); j++) {
                if (!vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).equals("-1")) {
                    DtSm = DtSm + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).toString()));
                }
            }

            NDLIFETIMESTATE[i][0] = vanetsim.scenario.Vehicle.TV[i].idVeh;
            NDLIFETIMESTATE[i][1] = vanetsim.scenario.Vehicle.TV[i].lifeTime;

            NDLIFETIMESTATE[i][2] = DtCh;
            NDLIFETIMESTATE[i][3] = DtSch;
            NDLIFETIMESTATE[i][4] = DtMn;
            NDLIFETIMESTATE[i][5] = DtSm;

            //-----------------------------------
            PourcentLTSTATE[i][0] = vanetsim.scenario.Vehicle.TV[i].idVeh;
            PourcentLTSTATE[i][1] = vanetsim.scenario.Vehicle.TV[i].lifeTime;

            PourcentLTSTATE[i][2] = (double) (NDLIFETIMESTATE[i][2] * 100) / (double) NDLIFETIMESTATE[i][1];// poucentage CH
            PourcentLTSTATE[i][3] = (double) (NDLIFETIMESTATE[i][3] * 100) / (double) NDLIFETIMESTATE[i][1];// poucentage SCH
            PourcentLTSTATE[i][4] = (double) (NDLIFETIMESTATE[i][4] * 100) / (double) NDLIFETIMESTATE[i][1];// poucentage MN
            PourcentLTSTATE[i][5] = (double) (NDLIFETIMESTATE[i][5] * 100) / (double) NDLIFETIMESTATE[i][1];// poucentage SM

            //-----------------------------------------------------------------
            if (PourcentLTSTATE[i][2] > 0) {
                NumTotalStateCh++;
                PourcentTotalStateCh = PourcentTotalStateCh + PourcentLTSTATE[i][2];
            }
            //-----------------------------------------------------------------
            if (PourcentLTSTATE[i][3] > 0) {
                NumTotalStateSch++;
                PourcentTotalStateSch = PourcentTotalStateSch + PourcentLTSTATE[i][3];
            }
             //-----------------------------------------------------------------

            if (PourcentLTSTATE[i][4] > 0) {
                NumTotalStateMN++;
                PourcentTotalStateMN = PourcentTotalStateMN + PourcentLTSTATE[i][4];
            }
            //-----------------------------------------------------------------
            if (PourcentLTSTATE[i][5] > 0) {
                NumTotalStateSM++;
                PourcentTotalStateSM = PourcentTotalStateSM + PourcentLTSTATE[i][5];
            }
            //-----------------------------------------------------------------

            System.out.println(" Noeud : " + NDLIFETIMESTATE[i][0] + " Lifetime : " + NDLIFETIMESTATE[i][1] + " Temps CH : " + NDLIFETIMESTATE[i][2] + " Temps SCH : " + NDLIFETIMESTATE[i][3] + " Temps MN : " + NDLIFETIMESTATE[i][4] + " Temps SM " + NDLIFETIMESTATE[i][5]);
            System.out.println(" Noeud : " + PourcentLTSTATE[i][0] + " Lifetime : " + PourcentLTSTATE[i][1] + " Temps CH : " + PourcentLTSTATE[i][2] + " Temps SCH : " + PourcentLTSTATE[i][3] + " Temps MN : " + PourcentLTSTATE[i][4] + " Temps SM " + PourcentLTSTATE[i][5]);
        }

        //----------------------------------------------------------------------
        if (NumTotalStateCh > 0) {
            System.out.println(" Moyenne de temps de vie CH : " + (PourcentTotalStateCh / NumTotalStateCh));
        } else {
            System.out.println(" Moyenne de temps de vie CH : " + 0);
        }
        if (NumTotalStateSch > 0) {
            System.out.println(" Moyenne de temps de vie SCH : " + (PourcentTotalStateSch / NumTotalStateSch));
        } else {
            System.out.println(" Moyenne de temps de vie SCH : " + 0);
        }
        if (NumTotalStateMN > 0) {
            System.out.println(" Moyenne de temps de vie MN : " + (PourcentTotalStateMN / NumTotalStateMN));
        } else {
            System.out.println(" Moyenne de temps de vie MN : " + 0);
        }
        if (NumTotalStateSM > 0) {
            System.out.println(" Moyenne de temps de vie SM : " + (PourcentTotalStateSM / NumTotalStateSM));
        } else {
            System.out.println(" Moyenne de temps de vie SM : " + 0);
        }
        //----------------------------------------------------------------------

    }

//-----------------------------Tri à bulle--------------------------------------
    public static void Tribull(double V[][], int h) {

        int t, j, i;
        double tmp, indice;
        boolean permut;

        permut = true;
        t = h;

        while (t > 0 && permut == true) {

            permut = false;

            for (i = 0; i < h; i++) {

                if (V[i][0] < V[i + 1][0]) {

                    tmp = V[i][0];
                    indice = V[i][1];

                    V[i][0] = V[i + 1][0];
                    V[i][1] = V[i + 1][1];

                    V[i + 1][0] = tmp;
                    V[i + 1][1] = indice;

                    permut = true;

                }

            }

            t = t - 1;

        }
//-------------------------------------------
//  for(int k=0;k<V.length;k++)
        // System.out.println(V[k][0]+"     ------   "+V[k][1]);
        //-------------------------------------------
    }
//------------------------------------------------------------------------------  

    public static void Tribull(int V[][], int h) {

        int t, j, i;
        int tmp, indice;
        boolean permut;

        permut = true;
        t = h;

        while (t > 0 && permut == true) {

            permut = false;

            for (i = 0; i < h; i++) {

                if (V[i][0] < V[i + 1][0]) {

                    tmp = V[i][0];
                    indice = V[i][1];

                    V[i][0] = V[i + 1][0];
                    V[i][1] = V[i + 1][1];

                    V[i + 1][0] = tmp;
                    V[i + 1][1] = indice;

                    permut = true;

                }

            }

            t = t - 1;

        }
//-------------------------------------------
//  for(int k=0;k<V.length;k++)
        // System.out.println(V[k][0]+"     ------   "+V[k][1]);
        //-------------------------------------------
    }

//------------------------------------------------------------------------------
    public static String date_courante() {
        /*  DATE*/
        Calendar cal = new GregorianCalendar();
        int moi = cal.get(Calendar.MONTH) + 1;
        int anné = cal.get(Calendar.YEAR);
        int jour = cal.get(Calendar.DAY_OF_MONTH);

        /*  heure*/
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int heure = cal.get(Calendar.HOUR_OF_DAY);

        String ms, an, jr, se, mi, he;
        if (moi < 10) {
            ms = "0" + moi;
        } else {
            ms = "" + moi;
        }
        if (anné < 10) {
            an = "0" + anné;
        } else {
            an = "" + anné;
        }
        if (jour < 10) {
            jr = "0" + jour;
        } else {
            jr = "" + jour;
        }

        if (second < 10) {
            se = "0" + second;
        } else {
            se = "" + second;
        }
        if (minute < 10) {
            mi = "0" + minute;
        } else {
            mi = "" + minute;
        }
        if (heure < 10) {
            he = "0" + heure;
        } else {
            he = "" + heure;
        }

        String times = jr + "-" + ms + "-" + an + "_" + he + "-" + mi + "-" + se;

        return times;
    }

    //------------------------------------------------------------------------
    public static void Initialisation() {
        /*
         lambda    = 5;//7,9
         tsim      = 300;//500,1000
         */
        Tlambda[0] = 3;
        Tlambda[1] = 7;
        Tlambda[2] = 10;
        TSim[0] = 40;
        TSim[1] = 500;
        TSim[2] = 1000;

        avi = 3;

        portee = 300 * 100; //projection sur pixel
        porteeMax = 1000 * 10;

        porteeRSU = 1000 * 10;
        porteeCA = 1500 * 10;

        Algorithms.APROVE.lambdaAPROV = 0.90;
        Algorithms.APROVE.Iter = 10;
        Algorithms.APROVE.Th = 8;

        K = 5;

        w1 = 0.25;
        w2 = 0.10;
        w3 = 0.65;

        vw1 = 0.25;
        vw2 = 0.25;
        vw3 = 0.25;
        vw4 = 0.25;

        NbAvg = 300;
        Ravg = 100;
        rtMax = 1000;

        Nv = NbAvg / Ravg;
        Phi = rnd.nextDouble() * (Nv - 1);
        sig = Math.exp(Phi);

        e1 = 0.60;
        e2 = 0.40;

        Dl = 35;
        Lav = 4;
        Nl = 3;
        Nt = Nl * (200.0 / (Dl + Lav));
        density = 0.80;
    }
//------------------------------------------------------------------------------

    public static int[][] Normalisation(int V[][], int prdNor, int Ts) {
        int nb = 0, v = 0;
        while (v <= Ts) {
            nb++;
            v = v + prdNor;
        }
        int TN[][] = new int[nb][2];
        for (int i = 0; i < nb; i++) {
            TN[i][0] = i * prdNor;
            TN[i][1] = 0;
        }
System.out.println("TN length = "+TN.length);
if (V.length != 0){
        for (int i = 1; i < TN.length; i++) {
            for (int j = 0; j < V.length - 1; j++) {
                if ((TN[i][0] >= V[j][0]) && (TN[i][0] <= V[j + 1][0])) {
                    //------------------------------
                    if (TN[i][0] == V[j][0]) {
                        TN[i][1] = V[j][1];
                    } else if (TN[i][0] == V[j + 1][0]) {
                        TN[i][1] = V[j + 1][1];
                    } else {
                        TN[i][1] = (int) (0.5 + (((V[j + 1][1] - V[j][1]) / prdNor) * (TN[i][0] - V[j][0]) + V[j][1]));
                    }
                    //------------------------------
                }
            }
            
        }
//-------------------------
        System.out.println("TN length = "+TN.length+" V length = "+V.length);
        TN[TN.length - 1][1] = V[V.length - 1][1];
}
//------------------------
        return TN;

    }
//------------------------------------------------------------------------------

    static void NdActifs() {
        ThdActif = new Thread() {

            @Override
            public void run() {
                while (depart == true) {
                    //--------------------------------------------------
                    periode++;

                    if (periode == 13) {
                        periode = 0;
                    }
                    //--------------------------------------------------
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //--------------------------------------------------
                }
            }

        };
        ThdActif.start();

    }
//------------------------------------------------------------------------------

    static void Lancement(int algo) {
        //thd = new Thread() {

        //public void run() {
        switch (algo) {
            case 1:

                TSC.setColumnCount(0);
                TSC.addColumn("Instant");
                TSC.addColumn("Sources");
                TSC.addColumn("Cible");
                TSC.setRowCount(1);

                FitnessClusteringManager();
                departFC = true;
                break;
            case 2:
                APROVEManager();
                departAPROV = true;
                break;
            case 3:
                VWCAManager();
                departVWCA = true;
                break;
            case 4:
                TChoixDestCBL = 0;
                DestChoisi = false;
                Dt5 = 0;
                MOZOManager();
                departMOZO = true;
                periodeMozo = -1;
                break;
        }

        //System.out.println("Injection des véhicules");
        while (depart == true) {
            compteur++;
            /* if ((compteur / 100) >= tsim) 
             {
             for (int i = 0; i < TV.length; i++) {
             if (adhoc.Adhoc.TV[i].etat.equals("Actif")) {
             adhoc.Adhoc.TV[i].lifeTime = (tsim*100) - Integer.parseInt(adhoc.Adhoc.S[i][2]);
             }
             }

             }*/

            NbvActif = 0;
            NbvInactif = 0;
            NbvHors = 0;

            for (int i = 0; i < S.length - 1; i++) {
                //System.out.println("Avant compteur  : "+compteur);

                if ((Integer.parseInt(S[i][2]) * TIME_PER_STEP == compteur) && (vanetsim.scenario.Vehicle.TV[i].etat.equals("Inactif"))) {
                    //System.out.println("Val1 : "+S[i][1]+"  compteur = "+compteur);

                    vanetsim.scenario.Vehicle.TV[i].etat ="Actif";

                    S[i][9] = "" + vanetsim.scenario.Vehicle.TV[i].posx;
                    S[i][10] = "" + vanetsim.scenario.Vehicle.TV[i].posy;
                    S[i][7] = "" + vanetsim.scenario.Vehicle.TV[i].etat;

                    vanetsim.scenario.Vehicle.TV[i].demarrer = true;
                    //System.out.println("i="+i+"Val1 : "+S[i][1]+"  compteur = "+compteur+" TV="+TV[i].etat);
                    vanetsim.scenario.Vehicle.Vehicule.Simulation(i, S);

                }
                // System.out.println("Etat : "+TV[i].etat+"  i = "+i+"/"+S.length+"/"+TV.length+"     Actif : "+NbvActif+"     Inactif : "+NbvInactif+"   HorsSystem : "+NbvHors);

                if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                    if (((compteur / 100) >= tsim) && vanetsim.scenario.Vehicle.TV[i].demarrer == true) {

                        vanetsim.scenario.Vehicle.TV[i].lifeTime = (tsim * 100) - Integer.parseInt(adhoc.Adhoc.S[i][2]);
                        System.out.println(" Compteur === " + adhoc.Adhoc.compteur + " Temps darrivée === " + Integer.parseInt(adhoc.Adhoc.S[i][1]) + " Lifetime du vehicule " + S[i][0] + " est " + vanetsim.scenario.Vehicle.TV[i].lifeTime);
//                            //------------------------
                        CalculNDLifeTimeStat();
//                            //------------------------
                        vanetsim.scenario.Vehicle.TV[i].demarrer = false;

                    }

                    NbvActif++;
                }

                if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Inactif")) {
                    NbvInactif++;
                }
                if (vanetsim.scenario.Vehicle.TV[i].etat.equals("HorsSystem")) {
                    NbvHors++;
                }
                //----------------------

                //----------------------
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
            }
            //---------------------------
            //System.out.println("compteur:"+compteur+" S.lenght: "+S[S.length - 1][1]);
            if (compteur >= Integer.parseInt(S[S.length - 1][1])) {
                depart = false;
                //------------------------
                CalculNDLifeTimeStat();

    //public static String PourcentNdState;
                //----------------Sauvegardes des groupes-------------------
//                            SimpleDateFormat fp = new SimpleDateFormat("ddMMyyyy_HHmm");mmm
//                            LTNdState = "LTNdState" + fp.format(new Date()) + ".txt";
//                            File ff = new File(LTNdState);
//                            SaveLTState(LTNdState, NDLIFETIMESTATE) ;
                if (algo == 1) {
                    SaveLTState(path + "//FC_LifeTimeStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", NDLIFETIMESTATE);
                    ///here modif 06-09-2017
                    SaveLTState(path + "//FC_PourcentageLTStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", PourcentLTSTATE);
                }
                //==================================================
                if (algo == 2) {
                    SaveLTState(path + "//APROVE_LifeTimeStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", NDLIFETIMESTATE);
                    ///here modif 06-09-2017
                    SaveLTState(path + "//APROVE_PourcentageLTStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", PourcentLTSTATE);
                }
                //==================================================
                if (algo == 3) {
                    SaveLTState(path + "//VWCA_LifeTimeStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", NDLIFETIMESTATE);
                    ///here modif 06-09-2017
                    SaveLTState(path + "//VWCA_PourcentageLTStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", PourcentLTSTATE);
                }
                //==================================================
                if (algo == 4) {
                    SaveLTState(path + "//MoZo_LifeTimeStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", NDLIFETIMESTATE);
                    ///here modif 06-09-2017
                    SaveLTState(path + "//MoZo_PourcentageLTStates_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", PourcentLTSTATE);
                }

                //------------------------
                departFC = false;
                departWCA = false;
                departVWCA = false;
                departAPROV = false;
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                    vanetsim.scenario.Vehicle.TV[i].demarrer = false;
                }
            }
            //---------------------------
        }

        //}
        //};
        //thd.start();
    }
//------------------------------------------------------------------------------
    //----------------------------------------------------------------------------
    static JFileChooser fichier = new JFileChooser();

    static public void SAVE(String nomFichier, DefaultListModel L) {
        try {
            fichier.setAcceptAllFileFilterUsed(true);
            fichier.getAcceptAllFileFilter();
            //========================================
            OutputStream fs = new FileOutputStream(nomFichier);
            try (PrintWriter out = new PrintWriter(fs)) {
                String ligne = "";
                for (int i = 0; i < L.getSize(); i++) {
                    //  ligne = ligne + L.getElementAt(i);
                    out.println(L.getElementAt(i).toString());
                    //System.out.println(L.getElementAt(i).toString());
                }
            }
        } catch (Exception e) {
        }
    }
//------------------------------------------------------------------------------

    static public void SAVEGROUPE(String nomFichier, DefaultTableModel T) {//Groupe VWCA
        try {
            String ligne = "";
            for (int i = 0; i < T.getColumnCount(); i++) {
                ligne = ligne + T.getColumnName(i) + "    ";
            }

            outt.println(ligne);

            System.out.println("Nombre de groupes formés pas l'algo VWCA -> " + T.getRowCount());
            for (int i = 0; i < T.getRowCount(); i++) {
                ligne = "";
                for (int j = 0; j < T.getColumnCount(); j++) {
                    ligne = ligne + T.getValueAt(i, j) + "    ";

                }
                outt.println(ligne);
                //System.out.println("ligne -> " + ligne);
                //System.out.println("V" +T.getValueAt(i, 0)+"    "+T.getValueAt(i, 1)+"    CH-> V"+T.getValueAt(i, 4));
            }
            //outt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//------------------------------------------------------------------------------
    static public void SAVEGROUPE1(String nomFichier, DefaultTableModel T) {//Groupe FC
        try {
            String ligne = "";
            for (int i = 0; i < T.getColumnCount(); i++) {
                if ((i != 2) && (i != 3)) {
                    ligne = ligne + T.getColumnName(i) + "    ";
                }
            }
            System.out.println(" ligne == " + ligne);
            outt1.println(ligne);

            System.out.println("Nombre de noeuds groupés par l'algo FC : " + T.getRowCount() + " / " + NbvActif);
            for (int i = 0; i < T.getRowCount(); i++) {
                ligne = "";
                for (int j = 0; j < T.getColumnCount(); j++) {
                    if ((j != 2) && (j != 3)) {
                        ligne = ligne + T.getValueAt(i, j) + "    ";
                    }
                }
                outt1.println(ligne);
                //System.out.println("ligne -> " + ligne);
            }
            //outt1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//------------------------------------------------------------------------------

    static public void SAVEGROUPE2(String nomFichier, DefaultTableModel T) {//Groupe APV
        try {
            String ligne = "";
            for (int i = 0; i < T.getColumnCount(); i++) {
                if ((i != 2) && (i != 3)) {
                    ligne = ligne + T.getColumnName(i) + "    ";
                }
            }

            outt2.println(ligne);

            System.out.println("Nombre de noeuds groupés par l'algo APV : " + T.getRowCount() + " / " + NbvActif);
            for (int i = 0; i < T.getRowCount(); i++) {
                ligne = "";
                for (int j = 0; j < T.getColumnCount(); j++) {

                    if ((j != 2) && (j != 3)) {
                        ligne = ligne + T.getValueAt(i, j) + "    ";
                    }
                }
                outt2.println(ligne);
                System.out.println("ligne -> " + ligne);
            }
            //outt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//------------------------------------------------------------------------------

    static public void SAVE1(String nomFichier, int L[][]) {
        try {
            // fichier.setAcceptAllFileFilterUsed(true);
            // fichier.getAcceptAllFileFilter();
            //========================================
            OutputStream fs = new FileOutputStream(nomFichier);
            PrintWriter out = new PrintWriter(fs);
            String ligne = "";
            for (int i = 0; i < L.length; i++) {
                //  ligne = ligne + L.getElementAt(i);
                out.println(L[i][0] + " " + L[i][1]);
                //System.out.println(L.getElementAt(i).toString());
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//------------------------------------------------------------------------------
    public static void BroadCast() {
        //------------------------------------

        LvactifOld = new int[Lvactif1.length];

        for (int i = 0; i < Lvactif1.length; i++) {
            LvactifOld[i] = Lvactif1[i];
        }

        //  System.out.println("Debut Broadcast");
        GlobalParameters.DISTold = new double[GlobalParameters.DIST.length][GlobalParameters.DIST.length];

        for (int i = 0; i < GlobalParameters.DIST.length; i++) {
            for (int j = 0; j < GlobalParameters.DIST.length; j++) {
                if (i < GlobalParameters.DISTold.length) {
                    if (j < GlobalParameters.DISTold[i].length) {
                        GlobalParameters.DISTold[i][j] = GlobalParameters.DIST[i][j];
                    }
                }
            }
        }

        // System.out.println("Calcul Distance");
        for (int i = 0; i < LvactifOld.length; i++) {
            vanetsim.scenario.Vehicle.TV[LvactifOld[i]].TS = System.currentTimeMillis();

            for (int j = 0; j < LvactifOld.length; j++) {
                if (i != j) {
                    vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin = vanetsim.scenario.Vehicle.Vehicule.NbrVoisinFormation(i, LvactifOld.length);
                    String veh_ = AFC.BroadCastHello(LvactifOld[j], avi);

                    if (vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin > 0) {
                        AFC.ReceivePacket(veh_, LvactifOld[i], portee, LvactifOld);
                        int ListV[];

                        ListV = vanetsim.scenario.Vehicle.Vehicule.ListVoisinFormation(i, LvactifOld, vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin);

                        vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit = new double[vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin][2];

                        // System.out.println("Longueur de RecFit =="+TV[LvactifOld[i]].RecFit.length);
                        if (ListV != null) {

                            for (int z = 0; z </*TV[LvactifOld[i]].RecFit.length*/ vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin; z++) {
                                if (z < vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit.length) {
                                    if (z < ListV.length) {
                                        if (z < vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit.length) {
                                            vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[z][0] = (int) ListV[z];//here pb
                                        
                                        }
                                    }
                                }

                            }
                        }
                    }

                }//System.out.println("Calcul Distance et RecFit");
            }
        }
        if (LvactifOld != null) {
            //veh.affichage_param_fitness(LvactifOld.length,trp,vr,lv);

            for (int i = 0; i < LvactifOld.length; i++) {
                vanetsim.scenario.Vehicle.TV[LvactifOld[i]].Fitness = AFC.Fct_Fitness(i, LvactifOld, alpha, beta, gamma, sigma, trp, vr, lv);
                //System.out.println(" Fitness de "+LvactifOld[i]+ " === "+vanetsim.scenario.Vehicle.TV[LvactifOld[i]].Fitness);
                if (vanetsim.scenario.Vehicle.TV[LvactifOld[i]].nbvoisin > 1) {

                    double Fit = vanetsim.scenario.Vehicle.TV[LvactifOld[i]].Fitness;
                    int IdCH = LvactifOld[i];

                    for (int k = 0; k < vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit.length/*TV[LvactifOld[i]].nbvoisin*/; k++) {

                        //   System.out.println("apres Taille de RecFit : "+k+"/"+TV[LvactifOld[i]].RecFit.length);
                        //   System.out.println("Contenu de RecFit : "+(int)TV[LvactifOld[i]].RecFit[k][0]+"/"+TV.length);
                        vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[k][1] = vanetsim.scenario.Vehicle.TV[(int) vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[k][0]].Fitness;
                        if (vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[k][1] < Fit) {
                            Fit = vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[k][1];
                            IdCH = (int) vanetsim.scenario.Vehicle.TV[LvactifOld[i]].RecFit[k][0];
                        }
                    }

                    //-------------------
                    vanetsim.scenario.Vehicle.TV[LvactifOld[i]].FitnessMyCH = Fit;
                    vanetsim.scenario.Vehicle.TV[LvactifOld[i]].myCH_FC = IdCH;
                    //-------------------
                }
            }

        }

        //------------------------------------
    }

    //-----------------------------------------------------------------------------
    public static void Formation(int vact[]) {
        AFC.ChElectionFClustering(PN, vact);

    }

    //-----------------------------------------------------------------------------
    public static void Maintenance(int vact[]) {
        AFC.ChMaintenanceFClustering(PN, vact);//FitnessClustering
    }
//------------------------------------------------------------------------------

    public static int[] nb_voisin(int nd, int vact[]) {
        nbv = 0;
        LV = null;
//  System.out.println("avant FAIT nd================== : "+nd);
        if (nd < FAIT.length) {
            nbv = FAIT[nd];
// System.out.println("apres FAIT nd================== : "+nd+"            nbv ========== "+nbv);

            if (nbv > 0) {
                LV = new int[nbv];
                nbv = 0;

                for (int i = 0; i < vact.length; i++) {
                    if (visit[nd][i] == false) {
                        if (nbv < LV.length) {
                            if (neighbour[nd][i] < vact.length) {
                                LV[nbv] = vact[neighbour[nd][i]];
                                nbv++;
                            }
                        }
                    }
                }
            }
        }
        return LV;

    }
//------------------------------------------------------------------------------

    public static int nb_fils(int ndf, int nbn) {
        int f = 0;
        //f = FAIT[ndf];

        for (int i = 0; i < nbn; i++) {
            if (ndf < GlobalParameters.LIEN.length) {
                if (i < GlobalParameters.LIEN[ndf].length) {
                    if (ndf < VB.length) {
                        if ((GlobalParameters.LIEN[ndf][i] == 1) && (VB[ndf] == false)) {
                            f++;
                        }
                    }
                }
            }
        }

        //  System.out.println("f ======= "+f);
        return f;
    }
//------------------------------------------------------------------------------

    public static void initialiser(int vact[]) {

        VB = new boolean[vact.length];
        NBS = new int[vact.length];
        PATH = new String[vact.length];
        FAIT = new int[vact.length];

        neighbour = new int[vact.length][vact.length];
        visit = new boolean[vact.length][vact.length];

        for (int i = 0; i < vact.length; i++) {
            for (int j = 0; j < vact.length; j++) {
                neighbour[i][j] = -1;
                visit[i][j] = true;
            }
        }

        //-------------------------
        int n;
        for (int i = 0; i < vact.length; i++) {
            n = 0;
            for (int j = 0; j < vact.length; j++) {
                if (n < GlobalParameters.LIEN.length) {
                    if (i < GlobalParameters.LIEN.length) {
                        if (j < GlobalParameters.LIEN[i].length) {
                            if (GlobalParameters.LIEN[i][j] == 1) {
                                neighbour[i][n] = j;
                                visit[i][n] = false;
                                n++;
                            }
                        }
                    }
                }
            }
            FAIT[i] = n;
        }
        //-------------------------

        for (int i = 0; i < vact.length; i++) {

            VB[i] = false;
            NBS[i] = 0;
            PATH[i] = "V" + vact[i] + ",";

        }

    }
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
    public static void affichage(int nd, int vact[]) {
        int c = 1;
        for (int i = 0; i < vact.length; i++) {
            if (nd < GlobalParameters.SAUT.length) {
                if (c - 1 < GlobalParameters.SAUT[nd].length) {
                    GlobalParameters.SAUT[nd][c - 1] = NBS[i];
                    vanetsim.scenario.Vehicle.TV[vact[nd]].NBS[i] = NBS[i];
                    vanetsim.scenario.Vehicle.TV[vact[nd]].CHM[i] = PATH[i];
                    c++;
                }
            }
        }
    }
//------------------------------------------------------------------------------

    public static void NBsaut(int nd, int vact[]) {
        //int indice;
        if (nd < vact.length) {
            //System.out.println("Calcule nbr de saut du noeud "+nd+" dans la liste vact");
            nbv = 0;
            LV = nb_voisin(nd, vact);
            //System.out.println("Nbre de voisin trouvé du noeud "+nd+" est "+nbv);

            if (nbv > 0)//*/LV != null)
            {
                //	ShowMessage("OK") ;
                for (int i = 0; i < LV.length; i++) {

                    int indice = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LV[i], vact);

                    if (indice < VB.length) {
                        if (VB[indice] == false) {
                            PATH[indice] = PATH[nd] + "V" + LV[i] + ",";
                            NBS[indice] = NBS[nd] + 1;
                            for (int j = 0; j < LV.length; j++) {
                                if (neighbour[nd][j] != -1) {
                                    if (vact[neighbour[nd][j]] == LV[i]) {
                                        neighbour[nd][j] = -1;
                                        visit[nd][j] = true;
                                        FAIT[nd]--;
                                    }
                                }
                            }
                        }
                    }
                }
                //----début elimination liens fils-pére
                for (int i = 0; i < LV.length; i++) {
                    for (int j = 0; j < vact.length; j++) {
                        int indice = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LV[i], vact);

                        if (neighbour[indice][j] == nd) {
                            neighbour[indice][j] = -1;
                            visit[indice][j] = true;
                            FAIT[indice]--;
                            VB[indice] = true;
                        }

                    }
                }

                for (int u = 0; u < LV.length; u++) //  int u=0;
                //while(u<LV.length)
                {
                    int indice = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LV[u], vact);
                    if (indice > -1) {
                        if (nb_fils(indice, vact.length) > 0) {
                            //------------------------------------
                            //System.out.println("Je vais calculer le Nbre de saut du noeud : "+indice+"/"+vact.length);
                            if (indice < vact.length) {
                                NBsaut(indice, vact);
                            }
                            //System.out.println("Calcul Nbre de saut du noeud : "+indice+"/"+vact.length+" est términé");
                            //------------------------------------
                        } else {
                            VB[indice] = true;
                        }
                    }
                    //u++;
                }

                // }
            }/*else
             {

             VB[Simulation.Vehicule.GetPositionElement(nd,vact)]   = true;
             }*/

        }

    }
//------------------------------------------------------------------------------

    public static void InitialisationLoiDeDistribution() {
        for (int k = 0; k < S.length; k++) {
            //System.out.println("pass");
            Adhoc.S[k][2] = "" + 50000;
            //Adhoc.S[k][3] = "";
            Adhoc.S[k][4] = "Inactif";
        }
        // compteur = 0;
        periode = 0;

        // depart=true;
        //  for(int s=0;s<TV.length;s++) TV[s].demarrer=true;
    }
//------------------------------------------------------------------------------

    public static void ParamInit() {

        compteur = 0;
        NbvActif = 0;
        NbvInactif = S.length;
        NbvHors = 0;
        posBFM = 0;
        sauvegarde = false;

        savefc = true;
        for (int m = 0; m < S.length; m++) {
            S[m][2] = "" + 50000;
            S[m][4] = "inactif";
        }

        for (int v = 0; v < TBFM.length; v++) {
            for (int u = 0; u < TBFM[v].length; u++) {
                TBFM[v][u] = false;
            }
        }

        //----------------------------------
        depart = true;
//        departFC = true;
//        departWCA = true;
//        departVWCA = true;
//        departAPROV = true;

        periode = 0;

        //periode1 = 0;
        //periode2 = 0;
        periode3 = 0;
        periode4 = 0;

        Tdep = System.currentTimeMillis();
        //NumExec = 0;
        //compteur = 0;

        //----------------------------------
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
            vanetsim.scenario.Vehicle.TV[i].posx = 50000;
            vanetsim.scenario.Vehicle.TV[i].TempsCH = 0;
            vanetsim.scenario.Vehicle.TV[i].CHDuration.setColumnCount(0);
            vanetsim.scenario.Vehicle.TV[i].CHDuration.addColumn("TdebutCH");
            vanetsim.scenario.Vehicle.TV[i].CHDuration.addColumn("TfinCH");
            vanetsim.scenario.Vehicle.TV[i].CHDuration.setRowCount(1);
            vanetsim.scenario.Vehicle.TV[i].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount() - 1, 0);
            vanetsim.scenario.Vehicle.TV[i].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount() - 1, 1);
            //------------------------------------------------------
            vanetsim.scenario.Vehicle.TV[i].TempsSCH = 0;
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.setColumnCount(0);
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.addColumn("TdebutSCH");
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.addColumn("TfinSCH");
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.setRowCount(1);
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount() - 1, 0);
            vanetsim.scenario.Vehicle.TV[i].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount() - 1, 1);
            //------------------------------------------------------
            vanetsim.scenario.Vehicle.TV[i].TempsMN = 0;
            vanetsim.scenario.Vehicle.TV[i].MNDuration.setColumnCount(0);
            vanetsim.scenario.Vehicle.TV[i].MNDuration.addColumn("TdebutMN");
            vanetsim.scenario.Vehicle.TV[i].MNDuration.addColumn("TfinMN");
            vanetsim.scenario.Vehicle.TV[i].MNDuration.setRowCount(1);
            vanetsim.scenario.Vehicle.TV[i].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount() - 1, 0);
            vanetsim.scenario.Vehicle.TV[i].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount() - 1, 1);
            //------------------------------------------------------
            vanetsim.scenario.Vehicle.TV[i].TempsSM = 0;
            vanetsim.scenario.Vehicle.TV[i].SMDuration.setColumnCount(0);
            vanetsim.scenario.Vehicle.TV[i].SMDuration.addColumn("TdebutSM");
            vanetsim.scenario.Vehicle.TV[i].SMDuration.addColumn("TfinSM");
            vanetsim.scenario.Vehicle.TV[i].SMDuration.setRowCount(1);
            vanetsim.scenario.Vehicle.TV[i].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount() - 1, 0);
            vanetsim.scenario.Vehicle.TV[i].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount() - 1, 1);
            //------------------------------------------------------
//             NodeLifetimeStates.setColumnCount(0);
//             NodeLifetimeStates.addColumn("Noeud");
//             NodeLifetimeStates.addColumn("LifeTime");
//             NodeLifetimeStates.addColumn("TimeCH");
//             NodeLifetimeStates.addColumn("TimeSCH");
//             NodeLifetimeStates.addColumn("TimeMN");
//             NodeLifetimeStates.addColumn("TimeMN");
//             NodeLifetimeStates.setRowCount(1);
//             //NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 0);
//             NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 1);
//             NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 2);
//             NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 3);
//             NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 4);
//             NodeLifetimeStates.setValueAt("-1", NodeLifetimeStates.getRowCount() - 1, 5);
            //------------------------------------------------------
            // APVLifetimeStates.setColumnCount(0);
            //------------------------------------------------------
            //VWCALifetimeStates.setColumnCount(0);
            //------------------------------------------------------
            //MZLifetimeStates.setColumnCount(0);
            //------------------------------------------------------

        }
        //----------------------------------
        NdActifs();

    }

//------------------------------------------------------------------------------
    public static void FitnessClusteringManager() {
        ThdGlobal = new Thread() {
            public void run() {
                System.out.println("Lancement de Fitness Clustering Manager");
                while (departFC == true) {
                    //--------------------------------------------------
                    if (NbvActif > 0) {

                        Lvactif1 = new int[NbvActif];

                        int nbn = 0;
                        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                                if (nbn < Lvactif1.length) {
                                    Lvactif1[nbn] = i;
                                    nbn++;
                                }
                            }
                        }

                        long t1 = System.currentTimeMillis();
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);//adhoc.GlobalParameters.DIST
                        long diff = (long) (System.currentTimeMillis() - t1);
                        // System.out.println("============== DUREE D'AFFICHAGE DISTANCE ==== : "+((double)diff/1000.0)+" sec  NbVeh : "+NbvActif);

                        
                       /* 
                        
                        for (int i = 0; i < Lvactif1.length; i++) {
                            vanetsim.scenario.Vehicle.TR = new long[Lvactif1.length][Lvactif1.length];

                            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].TR.length; j++) {
                                for (int k = 0; k < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].TR[j].length; k++) {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].TR[j][k] = -1;
                                }
                            }

                        }*/
                        //-----------------------------------------------------
                        System.out.println("-----------------------------------------------------");
                        System.out.println("Compteur FC ==================================> " + (compteur / 100) + "  / " + tsim);//+ "         posBFM /TBFM.length ===> " + posBFM + "/" + (TBFM.length - 1));
                        //==========================================================================
                        if (((compteur / 100) >= tsim - 2) || (posBFM == TBFM.length - 1)) {

                            if (savefc == true) {
                                savefc = false;
                                int EntFC[][] = new int[s1.getRowCount() - 1][2], SrtFC[][];
                                for (int u = 0; u < s1.getRowCount() - 1; u++) {
                                    EntFC[u][0] = Integer.parseInt(s1.getValueAt(u, 0).toString());
                                    EntFC[u][1] = Integer.parseInt(s1.getValueAt(u, 1).toString());
                                }
                                System.out.println("Sauvegarde des résultats pour FitnessClustering");
                                SAVE1(path + "//NumCH-Time_FC"/* + date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", EntFC);
                                SrtFC = Normalisation(EntFC, pcolect, tsim);
                                SAVE1(path + "//NumCH-Time_Normalise_FC_" /*+ date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", SrtFC);

                                SAVE(path + "//TimeExecution_FC_" /*+ date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1);
                                SAVE(path + "//TimeMaintenance_FC_"/* + date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1M);
                                // JOptionPane.showConfirmDialog(null, "pause");

                            }
                        }
                        //==========================================================================

                        System.out.println("V. Actifs : " + NbvActif);
                        System.out.println("V. Inactifs : " + NbvInactif);
                        System.out.println("V. Hors système : " + NbvHors);
                        //System.out.println("-----------------------------------------------------");
                        //-----------------------------------------------------
                        initialiser(Lvactif1);
                        for (int i = 0; i < Lvactif1.length; i++) {

                            //	NBsaut(i,Lvactif);
                            affichage(i, Lvactif1); // a ajouter dans le routage

                        }
                        periode1++;

                        if ((((compteur / 100) % 12) == 0) && (compteur > 100)) {
                            posBFM = (int) (((compteur / 100) / 12)) - 1;
                        } else {
                            posBFM = (int) (((compteur / 100) / 12));
                        }

                        //-------------------------------------------------------
                        //------------------------------------------------------
                        // if (periode1 < 13) {
                        // if (periode1 == 6) {
                        //System.out.println("Il y aura au maximum" + TBFM.length+" cycles");
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][0] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == 6)) {
                            TBFM[posBFM][0] = true;
                            AFC.propriete(PN, Lvactif1);
                            //System.out.println("posBFM === " + posBFM);
                            System.out.println("Début de Broadcast n°: " + (posBFM + 1));
                            System.out.println("Compteur === " + compteur);
                            t1 = System.currentTimeMillis();
                            BroadCast();
                            diff = (long) (System.currentTimeMillis() - t1);
                            System.out.println("-------------------------------------------------------");
                            System.out.println("Temps de broadcast (FC) n°:" + (posBFM + 1) + " est " + diff + " ms.");
                            //System.out.println("-------------------------------------------------------");
                            System.out.println("Fin de Broadcast (FC)");
                        }

                        // }
                        // if (periode1 == 9) {
                        //System.out.println("Debut Formation");
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][1] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == 7)) {
                            //NumExec++;
                            AFC.propriete(PN, LvactifOld);
                            TBFM[posBFM][1] = true;
                            bestPathFC.removeAllElements();
                            System.out.println("Début de formation (FC) n°:" + (posBFM + 1) + " pour FClustering");
                            t1 = System.currentTimeMillis();
                            Formation(LvactifOld);
                            diff = (long) (System.currentTimeMillis() - t1);
                            System.out.println("---------------------------------------------------------------------");
                            System.out.println("Fin de formation (FC). \nTemps de formation n°:" + (posBFM + 1) + " est " + diff + " ms.");
                            System.out.println("---------------------------------------------------------------------");

                            //--------------------------------------------------
                            long t2 = System.currentTimeMillis();
                            adhoc.Groupes.ConstructGroup();
                            long diff2 = (long) (System.currentTimeMillis() - t2);
                            System.out.println("---------------------------------------------------------------------");
                            System.out.println("Fin de construction de groupes (FC). \nTemps consommé pour la phase n°:" + (posBFM + 1) + " est " + diff2 + " ms.");
                            System.out.println("---------------------------------------------------------------------");

                            //--------------------------------------------------
                            //System.out.println("Fin de formation n° : " + (posBFM + 1));
                            SimpleDateFormat filePattern0 = new SimpleDateFormat("ddMMyyyy_HHmm");
                            FilenameGroupFC = "GroupesFC" + filePattern0.format(new Date()) + ".txt";
                            File GroupFC = new File(FilenameGroupFC);
                            try {
                                fst1 = new FileOutputStream(FilenameGroupFC);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            outt1 = new PrintWriter(fst1);
                            System.out.println("Sauvegarde des groupes formés.");
                            SAVEGROUPE1(FilenameGroupFC, PN);
                            System.out.println("Fin de sauvegarde.");
                            //---------------------------------------------
                            System.out.println("choix de source et cible : ");

                            TSC.setValueAt("" + compteur, TSC.getRowCount() - 1, 0);

                            long t3 = System.currentTimeMillis();
                            Algorithms.Mozo.ChoixSourceCible(10000, 40000, Lvactif1);
                            long diff3 = (long) (System.currentTimeMillis() - t3);
                            System.out.println("Source et cible déterminées dans " + diff3 + " ms.");

                            TSC.setValueAt("" + IdSrc, TSC.getRowCount() - 1, 1);
                            TSC.setValueAt("" + IdCbl, TSC.getRowCount() - 1, 2);
                            TSC.setRowCount(TSC.getRowCount() + 1);

                            DefaultListModel pathFC = new DefaultListModel();

//                            if (GRS.length > 1) {
//                                Algorithms.FitnessClustering.RoutingFc(pathFC);
//                            }
                            //---------------------------Construction de path de src vers cbl---------------------------------
                            long t4 = System.currentTimeMillis();
                            DefaultListModel prov = new DefaultListModel();
                            for (int i = 0; i < bestPathFC.getSize(); i++) {
                                if (adhoc.Adhoc.VerifExiste(Integer.parseInt(bestPathFC.getElementAt(i).toString()), prov) == false) {
                                    prov.addElement(bestPathFC.getElementAt(i).toString());
                                }
                            }
                            bestPathFC.removeAllElements();
                            for (int i = 0; i < prov.getSize(); i++) {
                                bestPathFC.addElement(prov.getElementAt(i).toString());
                            }

                            System.out.print(" bestPathFC : ");

                            for (int i = 0; i < bestPathFC.getSize(); i++) {
                                //if (adhoc.Adhoc.VerifExiste(Integer.parseInt(bestPathFC.getElementAt(i).toString()), adhoc.Adhoc.bestPathFC) == false) {
                                System.out.print(bestPathFC.getElementAt(i).toString() + " --> ");
                                //}
                            }
                            System.out.println();
                            //System.out.println();

                            if (GRS.length > 1) {

                                //--------------------Path from Source to Cible -------------------------
                                //long t4 = System.currentTimeMillis();
                                Groupes.PathSrcCbl(bestPathFC, IdSrc, IdCbl, prov);
                                long diff4 = (long) (System.currentTimeMillis() - t4);
                                System.out.println(" route construite de source vers cible en " + diff4 + " ms.");
                                //-----------------------------------------------------------------------
                            }

                        }

                        //diff = (long) (System.currentTimeMillis() - t1);
                        //TEX1.addElement("" + (diff));
                        TEX1.addElement("" + (NbvActif) + "		" + (double) (diff));

                        //}
                        //  if ((periode1 == 12)) {
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][2] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == 0) && (compteur > 100)) {
                            System.out.println("Début de maintenance (FC) n°:" + (posBFM + 1));
                            AFC.propriete(PN, LvactifOld);
                            TBFM[posBFM][2] = true;
                            t1 = System.currentTimeMillis();
                            Maintenance(LvactifOld);
                            diff = System.currentTimeMillis() - t1;
                            System.out.println("Fin de maintenance (FC). \nTemps de maintenance n°:" + (posBFM + 1) + " est " + diff + " ms.");
                            System.out.println("Compteur === " + compteur);
                            TEX1M.addElement("" + (NbvActif) + "		" + (double) (diff));
                        }

                        //----------------------------------
                        Dt1++;
                        if (Dt1 == 10) {

                            s1.setValueAt("" + (compteur / 100), s1.getRowCount() - 1, 0);
                            s1.setValueAt("" + AFC.nbrClFc, s1.getRowCount() - 1, 1);
                            s1.setRowCount(s1.getRowCount() + 1);

                            AFC.ST1.setValueAt("" + AFC.ST1.getRowCount(), AFC.ST1.getRowCount() - 1, 0);
                            AFC.ST1.setValueAt("" + (compteur / 100), AFC.ST1.getRowCount() - 1, 1);
                            AFC.ST1.setValueAt("" + AFC.nbrClFc, AFC.ST1.getRowCount() - 1, 2);
                            AFC.ST1.setRowCount(AFC.ST1.getRowCount() + 1);
                            //------------------------------
                            AFC.STa1.setValueAt("" + AFC.nbrClFc, AFC.STa1.getRowCount() - 1, 0);
                            AFC.STa1.setValueAt("" + (compteur / 100), AFC.STa1.getRowCount() - 1, 1);
                            AFC.STa1.setValueAt("" + Lvactif1.length, AFC.STa1.getRowCount() - 1, 2);
                            AFC.STa1.setRowCount(AFC.STa1.getRowCount() + 1);
                            //------------------------------
                            Dt1 = 0;
                        }
                        //---------------------------------
                        //System.out.println("Compteur FC ==================================> " + (compteur/100)+"  / "+tsim+"         posBFM /TBFM.length ===> "+posBFM+"/"+(TBFM.length-1));
                        System.out.println("idxLastTask = " + TBFM[TBFM.length - 1][idxlastTask]);
                        //
                        /*if ((compteur / 100) >= tsim)
                         {
                         for (int i = 0; i < TV.length; i++)
                         {
                         if (adhoc.Adhoc.TV[i].etat.equals("Actif")) 
                         {
                         adhoc.Adhoc.TV[i].lifeTime = (tsim*100) - Integer.parseInt(adhoc.Adhoc.S[i][2]);
                         }
                         }

                         }*/

                        if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {

                            sauvegarde = true;
                            int EntFC[][] = new int[s1.getRowCount() - 1][2], SrtFC[][];
                            for (int u = 0; u < s1.getRowCount() - 1; u++) {
                                EntFC[u][0] = Integer.parseInt(s1.getValueAt(u, 0).toString());
                                EntFC[u][1] = Integer.parseInt(s1.getValueAt(u, 1).toString());
                            }
                            System.out.println("Sauvegarde des résultats pour FitnessClustering");
                            SAVE1(path + "//NumCH-Time_FC_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", EntFC);
                            SrtFC = Normalisation(EntFC, pcolect, tsim);
                            SAVE1(path + "//NumCH-Time_Normalise_FC_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", SrtFC);

                            SAVE(path + "//TimeExecution_FC_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1);
                            SAVE(path + "//TimeMaintenance_FC_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1M);
                            //JOptionPane.showConfirmDialog(null, "pause");

                            for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
                                if (vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].etat.equals("Actif")) {
                                    Algorithms.FitnessClustering.CloseStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], tsim);
                                }
                            }

                            //----------------
                            for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                                System.out.println("============= Temps total CH ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille CHDuration = " + vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount());//+" temps de vie de ce noeud = "+TV[i].lifeTime);
                                        vanetsim.scenario.Vehicle.TV[i].TempsCH = vanetsim.scenario.Vehicle.TV[i].TempsCH + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total SCH ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille SCHDuration = " + vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsSCH = vanetsim.scenario.Vehicle.TV[i].TempsSCH + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total MN ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille MNDuration = " + vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsMN = vanetsim.scenario.Vehicle.TV[i].TempsMN + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total SM ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille SMDuration = " + vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsSM = vanetsim.scenario.Vehicle.TV[i].TempsSM + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).toString())));
                                    }
                                }

                            }
                            //---------------

                        }

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //--------------------------------------------------
                }
                ThdGlobal.interrupt();
            }

        };
        ThdGlobal.start();

    }
//------------------------------------------------------------------------------

    public static void WCAManager() {
        thMetrique = new Thread() {
            public void run() {
                while (departWCA == true) {

                    if (NbvActif > 0) {
                        Lvactif3 = new int[NbvActif];

                        int nbn = 0;
                        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                                if (nbn < Lvactif3.length) {
                                    Lvactif3[nbn] = i;
                                    nbn++;
                                }
                            }
                        }

                        //long t1  = System.currentTimeMillis();
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif3, portee);
                        //long diff = (long)(System.currentTimeMillis() - t1) ;
                        // System.out.println("============== DUREE D'AFFICHAGE DISTANCE ==== : "+((double)diff/1000.0)+" sec  NbVeh : "+NbvActif);

                        //------------------------------------------------------------------------
                        DIST.setColumnCount(0);
                        DIST.addColumn(" ");
                        for (int d = 0; d < GlobalParameters.DIST.length; d++) {
                            if (d < Lvactif3.length) {
                                DIST.addColumn("V" + (Lvactif3[d]));
                            }
                        }

                        DIST.setRowCount(GlobalParameters.DIST.length);
                        for (int d = 0; d < GlobalParameters.DIST.length; d++) {
                            if (d < Lvactif3.length) {
                                DIST.setValueAt("V" + (Lvactif3[d]), d, 0);
                            }
                        }
                        //----------------------------------------------------------------------------

                        //----------------------------------------------------------------------------
                        for (int i = 0; i < DIST.getRowCount(); i++) {
                            for (int j = 1; j < DIST.getColumnCount(); j++) {
                                DIST.setValueAt(" ", i, j);
                                if (i + 1 == j) {
                                    DIST.setValueAt("-", i, j);
                                } else {
                                    if (i < GlobalParameters.DIST.length) {
                                        if (j - 1 < GlobalParameters.DIST[i].length) {
                                            if (GlobalParameters.DIST[i][j - 1] > portee || GlobalParameters.DIST[i][j - 1] == -1) {
                                                DIST.setValueAt("-", i, j);
                                            } else {
                                                DIST.setValueAt("" + GlobalParameters.DIST[i][j - 1], i, j);
                                            }
                                        }
                                    }

                                }

                            }
                        }

                        //------------------------------------------------------------------------
                        //--------------------------------------
                        /*
                         ST.setColumnCount(0);RT.setColumnCount(0);
                         ST.addColumn(" ");   RT.addColumn(" ");
                         for(int i=0;i<Lvactif3.length;i++)
                         {
                         ST.addColumn("V"+(Lvactif3[i]));
                         RT.addColumn("V"+(Lvactif3[i]));
                         }
                         ST.setRowCount(Lvactif3.length);
                         RT.setRowCount(Lvactif3.length);

                         for(int i=0;i<ST.getRowCount();i++)
                         for(int j=1;j<ST.getColumnCount();j++)
                         {
                         ST.setValueAt(" ", i, j);
                         RT.setValueAt(" ", i, j);
                         }

                         for(int i=0;i<Lvactif3.length;i++)
                         {
                         ST.setValueAt("V"+(Lvactif3[i]), i, 0);
                         RT.setValueAt("V"+(Lvactif3[i]), i, 0);
                         }
                         */
                        //----------------------------
                        Lvactif3Old = new int[Lvactif3.length];
                        for (int i = 0; i < Lvactif3.length; i++) {
                            Lvactif3Old[i] = Lvactif3[i];
                        }

                        periode3++;
                        if (periode3 < 13) {

                            if (periode3 == 6) {
//-----------------------------------------
                                long t3 = System.currentTimeMillis();

                                Algorithms.WCA.Cluster.Initialisation(Lvactif3Old);
                                initialiser(Lvactif3Old);

                                for (int i = 0; i < Lvactif3Old.length; i++) {

                                    NBsaut(i, Lvactif3Old);

                                    vanetsim.scenario.Vehicle.Vehicule.DecouvertVoisinage(Lvactif3Old[i], portee, Lvactif3Old);
                                    vanetsim.scenario.Vehicle.TV2[i].Wi = AWCA.Fct_Wi(K, Lvactif3Old[i], w1, w2, w3);
                                }

                                Algorithms.WCA.Cluster.FormationClusters(GROUPE, Lvactif3Old, K);

                                double diff3 = (long) (System.currentTimeMillis() - t3);
                                TEX3.addElement("" + (diff3));

                                AWCA.propriete(compteur, Lvactif3Old, PR2);

                            }

                        } else {
                            periode3 = 0;
                        }

                        //------------------------------------
                        if (cas == 0) {
                            boolean active = false;
                            for (int i = 0; i < Lvactif3.length; i++) {
//System.out.println("------------------: "+i);
                                vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].instant = compteur;
                                int nb = 0;

                                for (int j = 1; j < DIST.getColumnCount(); j++) {
                                    if (!DIST.getValueAt(i, j).toString().trim().equals("-")) {
                                        nb++;
                                    }
                                }

                                //System.out.println("nb ---------------------------: "+nb);
                                if (nb > 0) {
                                    active = true;
                                    //System.out.println("Véhicule : "+i+"   à "+(nb));
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].Lcon = nb;
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].lcav = new int[nb];
                                    nb = 0;

                                    for (int j = 1; j < DIST.getColumnCount(); j++) {
                                        if (!DIST.getValueAt(i, j).toString().trim().equals("-")) {
                                            vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].lcav[nb] = Lvactif3[j - 1];
                                            vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].INST[0][j - 1] = compteur;
                                            vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].INST[1][j - 1] = 1;
                                            //System.out.println("              Véhicule : "+(j));
                                            nb++;
                                        }
                                    }
                                } else {
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3[i]].Lcon = 0;
                                }

                            }
                            if (active == true) {
                                cas = 1;
                            }
                        } else {
//Memo1->Lines->Add("Seconde N° : "+IntToStr(sec));
                            //System.out.println(" Debut affichage metrique ");
                            for (int i = 0; i < Lvactif3Old.length; i++) {
                                vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].instant = compteur;
                                int nb = 0;

                                for (int j = 1; j < DIST.getColumnCount(); j++) {
                                    if (i < DIST.getRowCount()) {
                                        if (j < DIST.getColumnCount()) //if(DIST.getValueAt(i, j).toString() != null)
                                        {
                                            if (!DIST.getValueAt(i, j).toString().equals("-") && !DIST.getValueAt(i, j).toString().equals(" ")) {
                                                nb++;
                                            }
                                        }
                                    }
                                }

                                if (nb > 0) {
                                    // Memo1->Lines->Add("Voisin : "+IntToStr(i+1)+"   à "+IntToStr(nb));
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap = new int[nb];
                                    nb = 0;
                                    for (int j = 1; j < DIST.getColumnCount(); j++) {
                                        if (DIST.getValueAt(i, j).toString() != null) {
                                            if (!DIST.getValueAt(i, j).toString().equals("-") && !DIST.getValueAt(i, j).toString().equals(" ")) {
                                                vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap[nb] = Lvactif3Old[j - 1];
                                                nb++;
                                            }
                                        }
                                    }
                                    //------
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].Lrep = 0;

                                    if (vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav != null) {
                                        for (int j = 0; j < vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav.length /*TV[Lvactif[i]].Lcon*/; j++) {
                                            boolean t = false;
                                            for (int k = 0; k < vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap.length /*nb*/; k++) {
                                                if (vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav[j] == vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap[k]) {
                                                    t = true;
                                                    vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].INST[0][vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav[j], Lvactif3Old)]++;
                                                    vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].INST[1][vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav[j], Lvactif3Old)] = 1;
                                                }
                                            }
                                            if (t == false) {
                                                vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].Lrep++;
                                            }
                                        }
                                    }
                                    //------
                                    //TV[Lvactif[i]].LC = TV[Lvactif[i]].Lcon + TV[Lvactif[i]].Lrep;
                                    vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav = new int[vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap.length];

                                    for (int j = 0; j < vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap.length; j++) {
                                        vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcav[j] = vanetsim.scenario.Vehicle.TV2[Lvactif3Old[i]].lcap[j];
                                    }

                                    AWCA.metrique_mobilite(MB, Lvactif3Old);
                                }
                                //================================

                                //================================
                            }
                            //System.out.println(" Fin affichage metrique ");

                            //--------------------------------------------
                        }

                    }
                    //----------------------------------
                    Dt3++;
                    if (Dt3 == 10) {
                        System.out.println("Sauvegarde des résultats pour WCA");
                        s3.setValueAt("" + (compteur / 100), s3.getRowCount() - 1, 0);
                        s3.setValueAt("" + GROUPE.getRowCount(), AWCA.ST3.getRowCount() - 1, 1);
                        s3.setRowCount(s3.getRowCount() + 1);

                        /*s3.addElement("" + (compteur / 100) + "		" + GROUPE.getRowCount());
                         AWCA.ST3.setValueAt("" + AWCA.ST3.getRowCount(), AWCA.ST3.getRowCount() - 1, 0);
                         AWCA.ST3.setValueAt("" + (compteur / 100), AWCA.ST3.getRowCount() - 1, 1);
                         AWCA.ST3.setValueAt("" + GROUPE.getRowCount(), AWCA.ST3.getRowCount() - 1, 2);
                         AWCA.ST3.setRowCount(AWCA.ST3.getRowCount() + 1);*/
                        //------------------------------
                        AWCA.STa3.setValueAt("" + GROUPE.getRowCount(), AWCA.STa3.getRowCount() - 1, 0);
                        AWCA.STa3.setValueAt("" + (compteur / 100), AWCA.STa3.getRowCount() - 1, 1);
                        AWCA.STa3.setValueAt("" + Lvactif3.length, AWCA.STa3.getRowCount() - 1, 2);
                        AWCA.STa3.setRowCount(AWCA.STa3.getRowCount() + 1);
                        //------------------------------
                        Dt3 = 0;
                    }

                    //----------------------------------
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                thMetrique.interrupt();
            }
        };
        thMetrique.start();
    }
//------------------------------------------------------------------------------

    public static void VWCAManager() {
        ThVwca = new Thread() {
            public void run() {
                System.out.println("Lancement de VWCAManager");
                while (departVWCA == true) {

                    if (NbvActif > 0) {
                        Lvactif1 = new int[NbvActif];

                        int nbn = 0;
                        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                                if (nbn < Lvactif1.length) {
                                    Lvactif1[nbn] = i;
                                    nbn++;
                                }
                            }
                        }
                        System.out.println("Compteur === " + compteur);
                        System.out.println("V. Actifs : " + NbvActif);
                        System.out.println("V. Inactifs : " + NbvInactif);
                        System.out.println("V. Hors système : " + NbvHors);
                        System.out.println("-----------------------------------------------------");
                        // AVWCA.propriete(Lvactif1,PR3);
                        periode4++;
                        if ((((compteur / 100) % 12) == 0) && (compteur > 100)) {
                            posBFM = (int) (((compteur / 100) / 12)) - 1;
                        } else {
                            posBFM = (int) (((compteur / 100) / 12));
                        }
                        //if (periode4 < 13) {
                        //if (periode4 == 6) {
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][1] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == 7)) 
                        {

                            TBFM[posBFM][1] = true;
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            System.out.println("Formation n°: " + (posBFM + 1) + " pour l'algorithme VWCA");
                            System.out.println("Execution de l'algorithme MMV");
                            System.out.println("V. Actifs : " + NbvActif);
                            System.out.println("V. Inactifs : " + NbvInactif);
                            System.out.println("V. Hors système : " + NbvHors);
                            System.out.println("Nombre de véhicules actifs est " + Lvactif1.length);
                            long t4 = System.currentTimeMillis();
                            AVWCA.MmvAlgorithm(Lvactif1);
                            long diff = System.currentTimeMillis() - t4;
                            System.out.println("temps MMV ==" + diff);

                            for (int i = 0; i < Lvactif1.length; i++) {
                                AVWCA.NewVoisin(Lvactif1[i], Lvactif1);
                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].nbvoisin = vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize();

                            }
                            System.out.println("Execution de l'algorithme AATR");

                            t4 = System.currentTimeMillis();
                            AVWCA.ExecutingAATRalgorithm(Lvactif1);
                            diff = System.currentTimeMillis() - t4;
                            System.out.println("temps AATR ==" + diff);

                            System.out.println("Calcul des paramètres : Entropie, Distance moyenne et poids W des noeuds");
                            t4 = System.currentTimeMillis();
                            for (int i = 0; i < Lvactif1.length; i++) {

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Entropy = AVWCA.CalculatingEntropy(Lvactif1[i]);

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Dmoy = AVWCA.CalculatingDistMoyenne(Lvactif1[i]);

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Weight = AVWCA.CalculatingWeightClusteringValue(Lvactif1[i]);

                            }
                            diff = System.currentTimeMillis() - t4;
                            System.out.println("temps entropie + Distmoyenne + Weights ==" + diff);

                            //-------------------------------
                            for (int i = 0; i < Lvactif1.length; i++) {
                                double pmin = 1e10; //pour stocker le poids min
                                int idx = -1;//pour stocker elt ayant le poids min
                                int elet;
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize(); j++) {
                                    elet = Integer.parseInt(vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getElementAt(j).toString());
                                    if (vanetsim.scenario.Vehicle.TV[elet].Weight < pmin) {
                                        pmin = vanetsim.scenario.Vehicle.TV[elet].Weight;
                                        idx = elet;
                                    }

                                }
                                //----------------
                                if (pmin > vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Weight) {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_ch_VWCA = true;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_mn_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_init_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].type_noeud_VWCA = "Ch";

                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].myCH_VWCA = Lvactif1[i];
                                } else {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_ch_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_mn_VWCA = true;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_init_VWCA = false;

                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].myCH_VWCA = idx;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].type_noeud_VWCA = "Mn";
                                }
                                //----------------

                            }
                            //-------------------------------
                            for (int n = 0; n < Lvactif1.length; n++) {
                                System.out.println("Le noeud " + Lvactif1[n] + " est   " + vanetsim.scenario.Vehicle.TV[Lvactif1[n]].type_noeud_VWCA);
                            }
                            System.out.println("Sauvegarde des groupes");
                            AVWCA.Clustering(Lvactif1, GROUPE1); // regrouper les noeuds
                            SAVEGROUPE(FilenameGroupVWCA, GROUPE1);

                            long diff4 = (long) (System.currentTimeMillis() - t4);
                            TEX4.addElement("" + (diff4));
                            //   System.out.println("====================FIN===============");
                            System.out.println("TEMPS D'EXECUTION EST : " + diff4 + " ms");
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                        }

                        /* } else {
                         periode4 = 0;
                         }*/
                        //------------------------------------
                    }
                    //----------------------------------
                    Dt4++;
                    if (Dt4 == 10) {
                        /*
                         int EntVWCA[][] = new int[s4.getRowCount() - 1][2], SrtVWCA[][];
                         for (int u = 0; u < s4.getRowCount() - 1; u++) {
                         EntVWCA[u][0] = Integer.parseInt(s4.getValueAt(u, 0).toString());
                         EntVWCA[u][1] = Integer.parseInt(s4.getValueAt(u, 1).toString());
                         }
                         System.out.println("Sauvegarde des résultats pour FitnessClustering");
                         SAVE1("NumCH-Time_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", EntFC);
                         SrtFC = Normalisation(EntFC, pcolect, tsim);
                         SAVE1("NumCH-Time_Normalise_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", SrtFC);
                         SAVE("TimeExecution_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1);
                         }
                         */

                        s4.setValueAt("" + (compteur / 100), s4.getRowCount() - 1, 0);
                        s4.setValueAt("" + GROUPE1.getRowCount(), s4.getRowCount() - 1, 1);
                        s4.setRowCount(s4.getRowCount() + 1);

                        //s4.setValueAt("" + (compteur / 100) + "		" + GROUPE1.getRowCount());
                        AVWCA.ST4.setValueAt("" + AVWCA.ST4.getRowCount(), AVWCA.ST4.getRowCount() - 1, 0);
                        AVWCA.ST4.setValueAt("" + (compteur / 100), AVWCA.ST4.getRowCount() - 1, 1);
                        AVWCA.ST4.setValueAt("" + GROUPE1.getRowCount(), AVWCA.ST4.getRowCount() - 1, 2);
                        AVWCA.ST4.setRowCount(AVWCA.ST4.getRowCount() + 1);
                        //------------------------------

                        AVWCA.STa4.setValueAt("" + GROUPE1.getRowCount(), AVWCA.STa4.getRowCount() - 1, 0);
                        AVWCA.STa4.setValueAt("" + (compteur / 100), AVWCA.STa4.getRowCount() - 1, 1);
                        AVWCA.STa4.setValueAt("" + Lvactif1.length, AVWCA.STa4.getRowCount() - 1, 2);
                        AVWCA.STa4.setRowCount(AVWCA.STa4.getRowCount() + 1);
                        //------------------------------

                        Dt4 = 0;
                    }
                    System.out.println("Compteur VWCA ==================================> " + (compteur / 100) + "  / " + tsim);
                    if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
                        sauvegarde = true;
                        int EntVWCA[][] = new int[s4.getRowCount() - 1][2], SrtVWCA[][];
                        for (int u = 0; u < s4.getRowCount() - 1; u++) {
                            EntVWCA[u][0] = Integer.parseInt(s4.getValueAt(u, 0).toString());
                            EntVWCA[u][1] = Integer.parseInt(s4.getValueAt(u, 1).toString());
                        }
                        System.out.println("Sauvegarde des résultats pour VWCA");
                        //SAVE("NumCH-Time_VWCA_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v .txt", s4);

                        SAVE1(path + "//NumCH-Time_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", EntVWCA);
                        SrtVWCA = Normalisation(EntVWCA, pcolect, tsim);
                        SAVE1(path + "//NumCH-Time_Normalise_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", SrtVWCA);
                        SAVE(path + "//TimeExecution_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", TEX4);

                    }

                    //----------------------------------
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                ThVwca.interrupt();
            }
        };
        ThVwca.start();
    }

    /*
     periodeMozo=-1;
     static double TMozoDeb,TMzoFin;
     */
//-------------------------------------------------------------------------------
    public static boolean VerifExiste(int nd, DefaultListModel L) {
        boolean b = false;
        for (int i = 0; i < L.getSize(); i++) {
            if (L.getElementAt(i).equals("" + nd)) {
                b = true;
            }
        }

        return b;
    }
//------------------------------------------------------------------------------

    public static void MOZOManager() {
        ThMozo = new Thread() {
            public void run() {
                System.out.println("Lancement de mozo");

                //----------------------------------------------------------
                //System.out.println("compteur ==================== " + compteur + "  NbvActif ==================== +" + NbvActif);
                //------------------------------------
                while (departMOZO == true) {

                    System.out.println("Compteur === " + compteur);
                    System.out.println("V. Actifs : " + NbvActif);
                    System.out.println("V. Inactifs : " + NbvInactif);
                    System.out.println("V. Hors système : " + NbvHors);
                    System.out.println("-----------------------------------------------------");

                    if (periodeMozo == -1) {
                        if (NbvActif > 0) {
                            Lvactif1 = new int[NbvActif];
                            int nbn = 0;

                            for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                                if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                                    if (nbn < Lvactif1.length) {
                                        Lvactif1[nbn] = i;
                                        nbn++;
                                    }
                                }
                            }

                            vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                            //System.err.println("================  CREATION MOZO ==============================");
                            Algorithms.Mozo.CreationMozo(Lvactif1.length, Lvactif1);

                            periodeMozo++;
                        }
                    } else if (periodeMozo == 0) {
                        TMozoDeb = (double) compteur / 100.0;
                        System.out.println("Temps début :======================================= " + TMozoDeb);
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        System.out.println("Taille Lvactif : " + Lvactif1.length + " / taille Moz" + adhoc.Adhoc.MOZ.length);
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            Algorithms.Mozo.CalculVoisinage(i, periodeMozo, -1);
                        }
                        periodeMozo++;
                    } else if (periodeMozo == 1) {
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            Algorithms.Mozo.CalculVoisinage(i, periodeMozo, -1);
                        }
                        periodeMozo++;
                    } else if (periodeMozo == 2) {
                        TChoixDestCBL++;
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        TMzoFin = (double) compteur / 100.0;
                        System.out.println("Temps fin :======================================= " + TMzoFin);
                        int idCapt = 0;
                        double simCapt = 0.0;

                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            if (TMozoDeb != TMzoFin) {
                                Algorithms.Mozo.CalculVoisinage(i, periodeMozo, (TMzoFin - TMozoDeb));

                            }
                        }
                        //------------------------------
                        int id = 0;
                        DefaultListModel Tcapt = new DefaultListModel();
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            idCapt = 0;
                            simCapt = 0.0;

                            if (adhoc.Adhoc.MOZ[i].Tintersec.size() == 0) {
                                adhoc.Adhoc.MOZ[i].idCapt = adhoc.Adhoc.MOZ[i].idMoz;
                                adhoc.Adhoc.MOZ[i].simCapt = adhoc.Adhoc.MOZ[i].sim;
                            } else {

                                for (int j = 0; j < adhoc.Adhoc.MOZ[i].Tintersec.size(); j++) {
                                    id = Integer.parseInt(adhoc.Adhoc.MOZ[i].Tintersec.getElementAt(j).toString());
                                    id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Algorithms.Mozo.Tpos);

                                    if (adhoc.Adhoc.MOZ[id].sim > adhoc.Adhoc.MOZ[i].sim) {
                                        idCapt = id;
                                        simCapt = adhoc.Adhoc.MOZ[id].sim;
                                    }
                                }
                                adhoc.Adhoc.MOZ[i].idCapt = idCapt;
                                adhoc.Adhoc.MOZ[i].simCapt = simCapt;
                            }
                            //--------------------------
                            if (VerifExiste(adhoc.Adhoc.MOZ[i].idCapt, Tcapt) == false) {
                                Tcapt.addElement("" + adhoc.Adhoc.MOZ[i].idCapt);
                            }
                            //--------------------------
                        }
                        //-----------------------------
                        Dt5++;
                        if (Dt5 == 5) {

                            s5.setValueAt("" + (compteur / 100), s5.getRowCount() - 1, 0);
                            s5.setValueAt("" + Tcapt.getSize(), s5.getRowCount() - 1, 1);
                            s5.setRowCount(s5.getRowCount() + 1);

                            Dt5 = 0;
                        }
                        //-----------------------------
                        //-------------------Tri à bulle---------------------
                        double vect[][] = new double[Tcapt.getSize()][2];

                        for (int i = 0; i < Tcapt.getSize(); i++) {
                            id = Integer.parseInt(Tcapt.getElementAt(i).toString());
                            //System.err.println(" Id ==============================================> "+id);
                            // id = Simulation.Vehicule.GetPositionElement(id+1, Lvactif1);
                            for (int k = 0; k < adhoc.Adhoc.MOZ.length; k++) {
                                if (adhoc.Adhoc.MOZ[k].ident == id) {
                                    //System.err.println(" Id ==> "+id+" / taille vect ==>"+vect.length+"   / Taille de MOZ : "+adhoc.Adhoc.MOZ.length  );
                                    vect[i][0] = adhoc.Adhoc.MOZ[k].posXmoz;
                                    vect[i][1] = adhoc.Adhoc.MOZ[k].idMoz;
                                }

                            }

                        }
                        adhoc.Adhoc.Tribull(vect, vect.length - 1);

                        /* for (int i = 0; i < vect.length; i++) 
                         {
                         System.err.println("vect["+i+"][0] == "+ (int)vect[i][0]+" ======>  "+"vect["+i+"][1] == "+ (int)vect[i][1]);
                         }*/
                        System.out.println("val1 :    " + (compteur / 100) + "           val2 :     " + tsim);
//                        if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
//                        {
//                        sauvegarde =true;
//                        //============Sauvegarde des résultats pour MOZO===============
//                        int EntMZ[][] = new int[s5.getRowCount() - 1][2], SrtMZ[][];
//                        for (int u = 0; u < s5.getRowCount() - 1; u++) 
//                        {
//                            EntMZ[u][0] = Integer.parseInt(s5.getValueAt(u, 0).toString());
//                            EntMZ[u][1] = Integer.parseInt(s5.getValueAt(u, 1).toString());
//                        }
//                        System.out.println("Sauvegarde des résultats pour MOZO");
//                        SAVE1(path + "//NumCH-Time_MZ"/* + date_courante()*/ + ").txt", EntMZ);
//                        SrtMZ = Normalisation(EntMZ, pcolect, tsim);
//                        SAVE1(path + "//NumCH-Time_Normalise_MZ_" /*+ date_courante()*/ + ").txt", SrtMZ);
//                         }
                        //============ Fin de sauvegarde des résultats pour MOZO===============

                        //------------------------------
                        Algorithms.Mozo.CollectEletMax(vect);
                        //------------------------------
                        /*for(int i=0;i<Tcapt.getSize();i++)
                         {
                         System.err.println("Tcapt["+i+"] ======  "+Tcapt.getElementAt(i));
                         }*/
                        CLV = new Algorithms.Mozo.ConstructCLVTree[vect.length];
                        Algorithms.Mozo.ConstructCLVTree.ConstructCLVTree(vect);

                        for (int k = 0; k < Tcapt.getSize(); k++) {
                            for (int l = 0; l < adhoc.Adhoc.MOZ.length; l++) {
                                //System.err.println("idCapt :  "+adhoc.Adhoc.MOZ[l].idCapt+" <====> Vect : "+(int)vect[k][1]+"  <====> ident : "+adhoc.Adhoc.MOZ[l].ident +"   <====>  idMoz : "+adhoc.Adhoc.MOZ[l].idMoz);

                                if ((adhoc.Adhoc.MOZ[l].idCapt == Integer.parseInt(Tcapt.getElementAt(k).toString())))// && (adhoc.Adhoc.MOZ[l].ident!= vect[k][1] ))
                                {
                                    CLV[k].nbFils++;
                                    CLV[k].Fils.addElement(adhoc.Adhoc.MOZ[l].ident);

                                }
                            }
                            System.out.println("Les fils de " + Tcapt.getElementAt(k) + " sont : ");
                            //--------------------------
                            for (int l = 0; l < CLV[k].Fils.getSize(); l++) {
                                System.out.println("     -> " + CLV[k].Fils.getElementAt(l));
                            }
                            System.out.println("===========================================================");
                            //--------------------------
                        }
                        //------------------------------
//                              if((TChoixDestCBL >= (int)(0.2*tsim))&&(DestChoisi ==false))// to fix here
//                              {
//                                 Algorithms.Mozo.ChoixSourceCible(2000); 
//                                 DestChoisi = true;
//                                 DefaultListModel path = new DefaultListModel();
//                                 //------------------------------
//                                 Algorithms.Mozo.RoutingMozo(path);
//                                 //------------------------------
//                              }
                        //------------------------------
                        //------------------------------
                        periodeMozo = -1;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //-------------------------------------
                ThMozo.interrupt();
            }
        };
        ThMozo.start();
    }
//------------------------------------------------------------------------------

    public static void APROVEManager() {
        ThdAprove = new Thread() {
            public void run() {
                //ParamInit();
                System.out.println("Lancement de APROVEManager");
                while (departAPROV == true) {

                    //----------------------------------------------------------
                    //System.out.println("compteur ==================== " + compteur + "  NbvActif ==================== +" + NbvActif);
                    System.out.println("Compteur === " + compteur);
                    System.out.println("V. Actifs : " + NbvActif);
                    System.out.println("V. Inactifs : " + NbvInactif);
                    System.out.println("V. Hors système : " + NbvHors);
                    System.out.println("-----------------------------------------------------");
                    //----------------------------------------------------------
                    if (NbvActif > 0) {

                        Lvactif1 = new int[NbvActif];
                        int nbn = 0;

                        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                                if (nbn < Lvactif1.length) {
                                    Lvactif1[nbn] = i;
                                    nbn++;
                                }
                            }
                        }
                        //------------------------------------------------------
                        if ((((compteur / 100) % 12) == 0) && (compteur > 100)) {
                            posBFM = (int) (((compteur / 100) / 12)) - 1;
                        } else {
                            posBFM = (int) (((compteur / 100) / 12));
                        }
                        //------------------------------------------------------

                        //System.out.println("periode === " + periode + "/" + AAPV.Th);
                        //periode2++;
                        //if (periode2 < 13) {
                        // System.out.println("===================A3================ ");
                        //System.out.println("Taille : " + posBFM + " / " + TBFM.length);
                        //if (periode2 == AAPV.Th) {
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][1] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == AAPV.Th - 1)) {

                            TBFM[posBFM][1] = true;
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            System.out.println("Découverte de voisinage APROVE");
                            for (int i = 0; i < Lvactif1.length; i++) {
                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].OVS.removeAllElements();
                                vanetsim.scenario.Vehicle.Vehicule.DecouvertVoisinage(Lvactif1[i], portee, Lvactif1);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize(); j++) {
                                    if (i < Lvactif1.length) {
                                        vanetsim.scenario.Vehicle.TV[Lvactif1[i]].OVS.addElement(vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getElementAt(j));
                                    }
                                }

                            }

                            System.out.println("Début de formation n:° " + (posBFM + 1) + " pour APROVE");
                            long t2 = System.currentTimeMillis();
                            AAPV.AlgorithmAPROVE(ss, rr, aa, Lvactif1);

                            long diff2 = (long) (System.currentTimeMillis() - t2);

                            System.out.println("---------------------------------------------------------------------");
                            System.out.println("Fin de formation (APV). \nTemps de formation (APV) n°:" + (posBFM + 1) + " : " + diff2 + " ms.");
                            System.out.println("---------------------------------------------------------------------");

                            TEX2.addElement("" + (diff2));

                            System.out.println("Sauvegarde des groupes (APV)");
                            AAPV.propriete(pp, Lvactif1);//affichage Aprove Formation
                            SAVEGROUPE2(FilenameGroupAPV, pp);
                            System.out.println("Fin de sauvegarde des groupes (APV)");
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                        }

                        //------------------------------------------------------
                        //if (periode2 == AAPV.Th + 3) {
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][2] == false) && (posBFM <= (int) ((compteur / 100) / 12)) && (((((int) (compteur / 100) % 12))) == AAPV.Th + 3) && (compteur > 100)) {
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            System.out.println("Début de maintenance n:° " + (posBFM + 1) + " pour APROVE");
                            TBFM[posBFM][2] = true;
                            long t1 = System.currentTimeMillis();
                            AAPV.MaintenanceAPROVE(Lvactif1);
                            AAPV.propriete(pp, Lvactif1); //affichage Aprove Maintenance
                            System.out.println("Fin de maintenance");
                            System.out.println("Durée de la maintenance : " + ((double) (System.currentTimeMillis() - t1)) + " ms");
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            //        LvactifOld =   new int[NbvActif];
                            //         for(int i=0;i<LvactifOld.length;i++) LvactifOld[i] = Lvactif[i];
                        }

                        /* } else {
                         periode2 = 0;
                         }*/
                        //----------------------------------
                        //Return APROVE
                        Dt2++;
                        if (Dt2 == 10) {
                            s2.setValueAt("" + (compteur / 100), s2.getRowCount() - 1, 0);
                            s2.setValueAt("" + AAPV.nbrchapv, s2.getRowCount() - 1, 1);
                            s2.setRowCount(s2.getRowCount() + 1);

                            //s2.addElement("" + (compteur / 100) + "		" + AAPV.nbrchapv);
                            //   System.out.println("===================A9================ ");
                            AAPV.ST2.setValueAt("" + AAPV.ST2.getRowCount(), AAPV.ST2.getRowCount() - 1, 0);
                            AAPV.ST2.setValueAt("" + (compteur / 100), AAPV.ST2.getRowCount() - 1, 1);
                            AAPV.ST2.setValueAt("" + AAPV.nbrchapv, AAPV.ST2.getRowCount() - 1, 2);
                            AAPV.ST2.setRowCount(AAPV.ST2.getRowCount() + 1);
                            //  System.out.println("===================A10================ ");
                            //------------------------------
                            AAPV.STa2.setValueAt("" + AAPV.nbrchapv, AAPV.STa2.getRowCount() - 1, 0);
                            AAPV.STa2.setValueAt("" + (compteur / 100), AAPV.STa2.getRowCount() - 1, 1);
                            AAPV.STa2.setValueAt("" + Lvactif1.length, AAPV.STa2.getRowCount() - 1, 2);
                            AAPV.STa2.setRowCount(AAPV.STa2.getRowCount() + 1);
                            //------------------------------
                            //------------------------------
                            Dt2 = 0;
                        }
                        System.out.println("Compteur APROVE ==================================> " + (compteur / 100) + "  / " + tsim);
                        if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
                            sauvegarde = true;
                            int EntAPV[][] = new int[s2.getRowCount()][2], SrtAPV[][];
                            for (int u = 0; u < s2.getRowCount(); u++) {
                                EntAPV[u][0] = Integer.parseInt(s2.getValueAt(u, 0).toString());
                                System.out.println("valeur s2 "+Integer.parseInt(s2.getValueAt(u, 0).toString()));
                                EntAPV[u][1] = Integer.parseInt(s2.getValueAt(u, 1).toString());
                            }
                            System.out.println("Sauvegarde des résultats pour APROVE");
                            //SAVE("NumCH-Time_APROVE_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v .txt", s2);

                            SAVE1(path + "//NumCH-Time_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", EntAPV);
                            SrtAPV = Normalisation(EntAPV, pcolect, tsim);
                            SAVE1(path + "//NumCH-Time_Normalise_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", SrtAPV);
                            SAVE(path + "//TimeExecution_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", TEX2);
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            //   JOptionPane.showConfirmDialog(null, "pause1");

                            // Modif 06-09-2017
                            for (int i = 0; i < Lvactif1.length; i++) {
                                if (vanetsim.scenario.Vehicle.TV[Lvactif1[i]].etat.equals("Actif")) {
                                    Algorithms.FitnessClustering.CloseStateDuration(Lvactif1[i], tsim);
                                }
                            }

                            //----------------
                            for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                                System.out.println("============= Temps total CH APROVE ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille CHDuration = " + vanetsim.scenario.Vehicle.TV[i].CHDuration.getRowCount());//+" temps de vie de ce noeud = "+TV[i].lifeTime);
                                        vanetsim.scenario.Vehicle.TV[i].TempsCH = vanetsim.scenario.Vehicle.TV[i].TempsCH + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].CHDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total SCH APROVE ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille SCHDuration = " + vanetsim.scenario.Vehicle.TV[i].SCHDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsSCH = vanetsim.scenario.Vehicle.TV[i].TempsSCH + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SCHDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total MN APROVE ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille MNDuration = " + vanetsim.scenario.Vehicle.TV[i].MNDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsMN = vanetsim.scenario.Vehicle.TV[i].TempsMN + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].MNDuration.getValueAt(j, 0).toString())));
                                    }
                                }
                                System.out.println("============= Temps total SM APROVE ================== Veh : " + vanetsim.scenario.Vehicle.TV[i].idVeh);
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount(); j++) {
                                    if (!vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).equals("-1")) {
                                        System.out.println("Taille SMDuration = " + vanetsim.scenario.Vehicle.TV[i].SMDuration.getRowCount());
                                        vanetsim.scenario.Vehicle.TV[i].TempsSM = vanetsim.scenario.Vehicle.TV[i].TempsSM + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).toString()));
                                        System.out.println(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0) + "  " + vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1) + "  diff : " + (Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 1).toString()) - Integer.parseInt(vanetsim.scenario.Vehicle.TV[i].SMDuration.getValueAt(j, 0).toString())));
                                    }
                                }

                            }
                            //Fin modif 06-09-2017

                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //--------------------------------------------------
                    }

                }
                ThdAprove.interrupt();
            }

        };
        ThdAprove.start();

    } //------------------------------------------------------------------------------

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        savefc = true;

        //Créer un nom de fichier basé sur la date et l'heure
//        SimpleDateFormat filePattern = new SimpleDateFormat("ddMMyyyy_HHmm");
//         String filename = filePattern.format(new Date()) + ".txt";
//         //ouvrir le fichier
//         File file = new File("TraceExecution_" + filename);
//         try {
//         PrintStream printStream = new PrintStream(file);
//         System.setOut(printStream);
////         System.out.println("            Lancement de l'application adhoc");
////         System.out.println("            --------------------------------\n");
//         } catch (FileNotFoundException e) {
//         e.printStackTrace();
//         }
        //-----------------Création d'un nouveau dossier--------------------------
        Files.createDirectories(path);

        //--------------------------
        System.out.println("            Lancement de l'application Adhoc");
        System.out.println("            --------------------------------\n");
        System.out.println("Initialisation des paramètres");//NaNo
        AVWCA.ST4.setColumnCount(0);
        trp.setColumnCount(0);
        vr.setColumnCount(0);
        lv.setColumnCount(0);
        PN.setColumnCount(0);
        MB.setColumnCount(0);
        DIST.setColumnCount(0);
        ST.setColumnCount(0);
        RT.setColumnCount(0);

        PR2.setColumnCount(0);
        PR3.setColumnCount(0);

        GROUPE.setColumnCount(0);
        GROUPE1.setColumnCount(0);
        ss.setColumnCount(0);
        rr.setColumnCount(0);
        aa.setColumnCount(0);
        pp.setColumnCount(0);

        //----------------------------------------------------------
        Initialisation();
        int lig = 0;
        System.out.println("Génération de la table des coefficients de l'algorithme FClustering");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        if (i * 0.25 + j * 0.25 + k * 0.25 + l * 0.25 == 1) {
                            Tcoefficient[lig][0] = i * 0.25;
                            Tcoefficient[lig][1] = j * 0.25;
                            Tcoefficient[lig][2] = k * 0.25;
                            Tcoefficient[lig][3] = l * 0.25;
                            System.out.println("alpha=" + i * 0.25 + ";beta=" + j * 0.25 + ";gamma=" + k * 0.25 + ";theta=" + l * 0.25);
                            lig++;
                        }
                    }
                }
            }
        }

        //----------------------------------------------------------
        for (int j = 0; j < 1/*Tlambda.length*/; j++) {

            System.out.println("------------------------------------------------------------------------------------------\n");
            //System.out.println("Exécution N° : " + (j + 1));
            lambda = Tlambda[j];

            for (int k = 0; k < 1/*TSim.length*/; k++) {
                tsim = TSim[k];

                posBFM = 0;

                //----------------------------------------
                TBFM = new boolean[(int) ((tsim - 6) / 12) + 1][3];
                for (int v = 0; v < TBFM.length; v++) {
                    for (int u = 0; u < TBFM[v].length; u++) {
                        TBFM[v][u] = false;
                    }
                }
                System.out.println("Il y aura au maximum " + TBFM.length + " cycles (Broadcast, Formation, Maintenance)\n");

                if ((tsim / 100) % 12 == 6) {
                    idxlastTask = 0;//dernière tache --> Broadcast
                } else if (((tsim / 100) % 12 > 6)) {
                    idxlastTask = 1;//dernière tache --> Formation
                } else {
                    if ((tsim / 100) % 12 != 0) {
                        idxlastTask = 2;//dernière tache --> Maintenance
                    } else {
                        idxlastTask = 1;
                    }
                }
                System.out.println("idxlastTask === " + idxlastTask);
                //------------------------ générer loi de poisson K fois
                for (int p = 0; p < 1/*10*/; p++) {
                    try {
                        System.out.println("Distribution de Poisson N° : " + (p + 1) + " pour Lambda = " + lambda + " et Temps de simulation = " + tsim);
                        //----------------------------------
                        // vanetsim.Poisson ps = new vanetsim.Poisson(tsim);
                        // ps.generatePoissonProcess(lambda);

                        //Dessin.dessin.DessinRoute(50000, zone.getGraphics());
                        //  System.out.println("Création de " + S.length + " vehicules");
                        //  vanetsim.scenario.Vehicle.CreationVehicule(S.length);
                        //----------------------------------------------------------
                        /*
                         for (int m = 0; m < S.length; m++) 
                         {
                         System.out.println("Vehicule n° : " + S[m][0] + "       Temps d'arrivée : " + S[m][1]);
                         //System.out.println("N° Veh : " + S[m][0] + "   Tarr : " + S[m][1] + "   posx : " + S[m][2] + "    posy : " + S[m][3] + "    etat : " + S[m][4] + "\n");
                         }
                         */
                        ParamInit();
                        System.out.println("\n------------------------------------------------------------------------------------------------------");
                        System.out.println("                    Exécution n°: " + (p + k + j + 1) + " pour la distribution n°: " + (p + 1));
                        System.out.println("------------------------------------------------------------------------------------------------------\n");

                        for (int i = 0; i < 1/*Tcoefficient.length*/; i++) {

                            System.out.println("        Exécution de l'algorithme FitnessClustering :");

                            alpha = Tcoefficient[i][0];
                            beta = Tcoefficient[i][1];
                            gamma = Tcoefficient[i][2];
                            sigma = Tcoefficient[i][3];

                            System.out.println("Combinaison n°:" + (i + 1) + " des coefficients de l'agorithme FC: alpha = " + Tcoefficient[i][0] + "; beta=" + Tcoefficient[i][1] + "; gamma=" + Tcoefficient[i][2] + "; theta=" + Tcoefficient[i][3]);
                            System.out.println("------------------------------------------------------------------------------------------------------\n");

                            TEX1.removeAllElements();
                            TEX1M.removeAllElements();
                            s1.setColumnCount(0);
                            s1.setColumnCount(2);
                            s1.setRowCount(1);
                            //System.out.println("s1"+s1.size());
                            AFC.STa1.setColumnCount(0);
                            AFC.STa1.addColumn("Nbr CH");
                            AFC.STa1.addColumn("Time");
                            AFC.STa1.addColumn("Density");
                            AFC.STa1.setRowCount(1);

                            AFC.ST1.setColumnCount(0);
                            AFC.ST1.addColumn("N°");
                            AFC.ST1.addColumn("Times");
                            AFC.ST1.addColumn("Num. of Ch");
                            AFC.ST1.setRowCount(1);
                            Dt1 = 0;

                            //----------------Sauvegardes des groupes-------------------
                            SimpleDateFormat filePattern0 = new SimpleDateFormat("ddMMyyyy_HHmm");
                            FilenameGroupFC = "GroupesFC" + filePattern0.format(new Date()) + ".txt";
                            File GroupFC = new File(FilenameGroupFC);
                            fst1 = new FileOutputStream(FilenameGroupFC);
                            outt1 = new PrintWriter(fst1);
                            //----------------------------------------------------------

                            //System.out.println("FC Coefficient: " + (i + 1));
                            Lancement(1);
                            try {
                                ThdGlobal.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("Compteur = " + compteur);
                            InitialisationLoiDeDistribution(); // Fixé
                            // vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(S.length);
                            /*
                             for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
                             vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(int newVeh,int X, int Y, int voie, int larg, int long_,double vitesse) 
                             */

                           // ParamInit();
                            System.out.println("==========================================================================================\n");

                        }

                        System.out.println("Execution de l'algorithme : APROVE\n");
                        System.out.println("------------------------------------------------------------------------------------------");

                        //----------------------------------------------------------
                       // System.out.println("Réinitialisation des ressources partagées");
                      //  InitialisationLoiDeDistribution();
                        //  vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(S.length);
                        //   ParamInit();

                        //----------------Sauvegardes des groupes-------------------
                        SimpleDateFormat filePattern1 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupAPV = "GroupesAPROV" + filePattern1.format(new Date()) + ".txt";
                        File GroupAPV = new File(FilenameGroupAPV);
                        fst2 = new FileOutputStream(FilenameGroupAPV);
                        outt2 = new PrintWriter(fst2);
                        //----------------------------------------------------------

                        /*for (int m = 0; m < Adhoc.S.length; m++) {
                         System.out.println("N° Veh : " + S[m][0] + "   Tarr : " + S[m][1] + "   posx : " + S[m][2] + "    posy : " + S[m][3] + "    etat : " + S[m][4] + "\n");
                         }*/
                        //----------------------------------------------------------
                        /*System.out.println("Nombre de véhicule actifs === " + NbvActif);
                         System.out.println("Nombre de véhicule inactifs === " + NbvInactif);
                         System.out.println("Nombre de véhicule hors système === " + NbvHors);*/
                        //----------------------------------------------------------
                        TEX2.removeAllElements();
                        s2.setColumnCount(0);
                        s2.setColumnCount(2);
                        s2.setRowCount(1);
                        //s2.removeAllElements();
                        AAPV.STa2.setColumnCount(0);
                        AAPV.STa2.addColumn("Nbr CH");
                        AAPV.STa2.addColumn("Time");
                        AAPV.STa2.addColumn("Density");
                        AAPV.STa2.setRowCount(1);

                        AAPV.ST2.setColumnCount(0);
                        AAPV.ST2.addColumn("N°");
                        AAPV.ST2.addColumn("Times");
                        AAPV.ST2.addColumn("Num. of Ch");
                        AAPV.ST2.setRowCount(1);
                        Dt2 = 0;

                        //APROVEManager();
                        // verrou.lock();
                        Lancement(2);
                        try {
                            ThdAprove.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                         System.out.println("Réinitialisation des ressources partagées");
                         InitialisationLoiDeDistribution();
                         //Simulation.Vehicule.CreationVehicule(S.length);
                         ParamInit();
                         //verrou.unlock();

                        //----------------WCA--------------------
                        //
                        /*
                         System.out.println("Execution de l'algorithme : WCA");
                         //compteur =0;
                         TEX3.removeAllElements();
                         s3.removeAllElements();
                         AWCA.STa3.setColumnCount(0);
                         AWCA.STa3.addColumn("Nbr CH");
                         AWCA.STa3.addColumn("Time");
                         AWCA.STa3.addColumn("Density");
                         AWCA.STa3.setRowCount(1);

                         AWCA.ST3.setColumnCount(0);
                         AWCA.ST3.addColumn("N°");
                         AWCA.ST3.addColumn("Times");
                         AWCA.ST3.addColumn("Num. of Ch");
                         AWCA.ST3.setRowCount(1);
                         Dt3 = 0;
                         //  System.out.println("Debut de l'algorithme WCA");
                         Algorithms.WCA.Cluster.CreationMaxCluster(10000);
                         WCAManager();
                         */
                        System.out.println("Execution de l'algorithme : VWCA\n");
                        System.out.println("------------------------------------------------------------------------------------------");

                        System.out.println("Réinitialisation des ressources partagées");
                        InitialisationLoiDeDistribution();
                        //Simulation.Vehicule.CreationVehicule(S.length);
                        ParamInit();
                        //----------------Sauvegardes des groupes-------------------
                        SimpleDateFormat filePattern3 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupVWCA = "GroupesVWCA" + filePattern3.format(new Date()) + ".txt";
                        File GroupVWCA = new File(FilenameGroupVWCA);
                        fst = new FileOutputStream(FilenameGroupVWCA);
                        outt = new PrintWriter(fst);
                        //----------------------------------------------------------

                        TEX4.removeAllElements();
                        s4.setColumnCount(0);
                        s4.setColumnCount(2);
                        s4.setRowCount(1);

                        //s4.removeAllElements();
                        AVWCA.STa4.setColumnCount(0);
                        AVWCA.STa4.addColumn("Nbr CH");
                        AVWCA.STa4.addColumn("Time");
                        AVWCA.STa4.addColumn("Density");
                        AVWCA.STa4.setRowCount(1);

                        AVWCA.ST4.setColumnCount(0);
                        AVWCA.ST4.addColumn("N°");
                        AVWCA.ST4.addColumn("Times");
                        AVWCA.ST4.addColumn("Num. of Ch");
                        AVWCA.ST4.setRowCount(1);
                        Dt4 = 0;

                        Lancement(3);
                        System.out.println("Fin de VWCA.");
                        try {
                            ThVwca.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            System.out.println("===> Réinitialisation des paramètres pour Mozo");
                          InitialisationLoiDeDistribution(); // Fixé

                        ParamInit();
                        //   System.out.println("===> Réinitialisation des ressources partagées");
                        //   vanetsim.scenario.Vehicle.Vehicule.CreationVehicule(S.length);
//                      
                        //======================================================
                        s5.setColumnCount(0);
                        s5.setColumnCount(2);
                        s5.setRowCount(1);
                        

                        //----------------Sauvegardes des groupes-------------------
                        SimpleDateFormat filePattern4 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupMOZO = "GroupesMOZO" + filePattern4.format(new Date()) + ".txt";
                        File GroupMZ = new File(FilenameGroupMOZO);
                        
                        fst5 = new FileOutputStream(FilenameGroupMOZO);
                        
                        outt5 = new PrintWriter(fst5);
                        //----------------------------------------------------------

                        //======================================================
                        System.out.println("===================================== Lancement de l'algo MoZo ======================================\n");
                        Lancement(4);
                        System.out.println("Compteur = " + compteur);
                        try {
                            ThdGlobal.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        System.out.println("Fin de l'exécution n°: " + (p + k + j + 1));
                        System.out.println("------------------------------------------------------------------------------------------\n");

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    /*System.out.println("Réinitialisation des ressources partagées");
                     InitialisationLoiDeDistribution();
                     Simulation.Vehicule.CreationVehicule(S.length);
                     ParamInit();
                     //verrou.unlock();*/
                }
            }

        }
        if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
            //============Sauvegarde des résultats pour MOZO===============
            sauvegarde = true;
            int EntMZ[][] = new int[s5.getRowCount() - 1][2], SrtMZ[][];
            for (int u = 0; u < s5.getRowCount() - 1; u++) {
                EntMZ[u][0] = Integer.parseInt(s5.getValueAt(u, 0).toString());
                EntMZ[u][1] = Integer.parseInt(s5.getValueAt(u, 1).toString());
            }
            System.out.println("Sauvegarde des résultats pour MOZO");
            SAVE1(path + "//NumCH-Time_MZ"/* + date_courante()*/ + ").txt", EntMZ);
            SrtMZ = Normalisation(EntMZ, pcolect, tsim);
            SAVE1(path + "//NumCH-Time_Normalise_MZ_" /*+ date_courante()*/ + ").txt", SrtMZ);
        }

        System.out.println("==========================================================================================\n");

        System.out.println("Fin de simulation.");
        // outt.close();
        outt1.close();
        //  outt2.close();
        //-----------------
        //-----------------
        System.exit(0);//----------------------------------------------------------
    }

//==============================================================================
    public static void GetVehActif() {
        int nbact = 0;
        System.out.println("aaa aaa aaa");
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {

            //System.out.println("i ============== "+i+"/"+vanetsim.scenario.Vehicle.TV.length+" etat "+vanetsim.scenario.Vehicle.TV[i].etat+" position "+vanetsim.scenario.Vehicle.TV[i].posx);
           //if (vanetsim.scenario.Vehicle.TV[i].etat==-1/*.equals("Inactif")*/) System.out.println(" GetVeh Incatif");
            
            
            //System.out.println("i ============== "+i+"/"+vanetsim.scenario.Vehicle.TV.length);
            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                nbact++;
                //System.out.println("nbact first === "+nbact); 
            }

        }
        
        System.out.println("nbact ======================= "+nbact);
        if (nbact > 0) {
            int nb = nbact;
            //System.out.println("nbact second =========== "+nbact);
            Lvactif1 = new int[nb];
          //  System.out.println("nbact : " + nb);
            nb = 0;
            for (int i = 0; i < vanetsim.scenario.Vehicle.TV.length; i++) {
                if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif") && (nb < Lvactif1.length)) {
                    //System.out.println("i : "+i+"  nb ==== "+nb+"   taille : "+Lvactif1.length);
                    Lvactif1[nb] = i;
                    nb++;
                }
            }
        }
        /*
         nbact second =========== 2
         nbact : 2
         i : 0  nb ==== 0   taille : 2
         i : 1  nb ==== 1   taille : 2
         i : 2  nb ==== 2   taille : 2  
         */
    }
//==============================================================================

    public static void Init() {
        savefc = true;
        portee = 300;//Nano

        compteur = 0;
        NbvActif = 0;
        NbvInactif = S.length;
        NbvHors = 0;
        posBFM = 0;
        sauvegarde = false;
        savefc = true;

        TSC.setColumnCount(0);
        TSC.addColumn("Instant");
        TSC.addColumn("Sources");
        TSC.addColumn("Cible");
        TSC.setRowCount(1);
        //Créer un nom de fichier basé sur la date et l'heure
//        SimpleDateFormat filePattern = new SimpleDateFormat("ddMMyyyy_HHmm");
//         String filename = filePattern.format(new Date()) + ".txt";
//         //ouvrir le fichier
//         File file = new File("TraceExecution_" + filename);
//         try {
//         PrintStream printStream = new PrintStream(file);
//         System.setOut(printStream);
////         System.out.println("            Lancement de l'application adhoc");
////         System.out.println("            --------------------------------\n");
//         } catch (FileNotFoundException e) {
//         e.printStackTrace();
//         }
        //-----------------Création d'un nouveau dossier--------------------------
        //   Files.createDirectories(path);

        //--------------------------
        System.out.println("Initialisation des paramètres ADHOC");
        AVWCA.ST4.setColumnCount(0);
        trp.setColumnCount(0);
        vr.setColumnCount(0);
        lv.setColumnCount(0);
        PN.setColumnCount(0);
        MB.setColumnCount(0);
        DIST.setColumnCount(0);
        ST.setColumnCount(0);
        RT.setColumnCount(0);

        PR2.setColumnCount(0);
        PR3.setColumnCount(0);

        GROUPE.setColumnCount(0);
        GROUPE1.setColumnCount(0);
        ss.setColumnCount(0);
        rr.setColumnCount(0);
        aa.setColumnCount(0);
        pp.setColumnCount(0);

        //----------------------------------------------------------
        Initialisation();
        int lig = 0;
        System.out.println("Génération de la table des coefficients de l'algorithme FClustering");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        if (i * 0.25 + j * 0.25 + k * 0.25 + l * 0.25 == 1) {
                            Tcoefficient[lig][0] = i * 0.25;
                            Tcoefficient[lig][1] = j * 0.25;
                            Tcoefficient[lig][2] = k * 0.25;
                            Tcoefficient[lig][3] = l * 0.25;
                            System.out.println("alpha=" + i * 0.25 + ";beta=" + j * 0.25 + ";gamma=" + k * 0.25 + ";theta=" + l * 0.25);
                            lig++;
                        }
                    }
                }
            }
        }
                          /*  alpha = 0.25;//Tcoefficient[0][0];
                            beta = 0.25;//Tcoefficient[0][1];
                            gamma = 0.25;//Tcoefficient[0][2];
                            sigma = 0.25;//Tcoefficient[0][3];*/
        //----------------------------------------------------------
        System.out.println("------------------------------------------------------------------------------------------\n");
        //System.out.println("Exécution N° : " + (j + 1));
        lambda = Tlambda[0];

        tsim = vanetsim.gui.controlpanels.ClusteringControlPanel.tsim * TIME_PER_STEP;
        posBFM = 0;

        //----------------------------------------
        TBFM = new boolean[(int) ((tsim - 6) / (12 * TIME_PER_STEP)) + 1][3];
        for (int v = 0; v < TBFM.length; v++) {
            for (int u = 0; u < TBFM[v].length; u++) {
                TBFM[v][u] = false;
            }
        }
        System.out.println("Il y aura au maximum " + TBFM.length + " cycles (Broadcast, Formation, Maintenance)\n");

        if ((tsim / (100*TIME_PER_STEP)) % 12 == 6) {
            idxlastTask = 0;//dernière tache --> Broadcast
        } else if (((tsim / (100*TIME_PER_STEP)) % 12 > 6)) {
            idxlastTask = 1;//dernière tache --> Formation
        } else {
            if ((tsim / (100*TIME_PER_STEP)) % 12 != 0) {
                idxlastTask = 2;//dernière tache --> Maintenance
            } else {
                idxlastTask = 1;
            }
        }
        System.out.println("idxlastTask === " + idxlastTask);

        //==========================Param APROVE========================
        TEX2.removeAllElements();
        s2.setColumnCount(0);
        s2.setColumnCount(2);
        s2.setRowCount(1);
        //s2.removeAllElements();
        AAPV.STa2.setColumnCount(0);
        AAPV.STa2.addColumn("Nbr CH");
        AAPV.STa2.addColumn("Time");
        AAPV.STa2.addColumn("Density");
        AAPV.STa2.setRowCount(1);

        AAPV.ST2.setColumnCount(0);
        AAPV.ST2.addColumn("N°");
        AAPV.ST2.addColumn("Times");
        AAPV.ST2.addColumn("Num. of Ch");
        AAPV.ST2.setRowCount(1);
        Dt2 = 0;
        
        
        //==========================Param VWCA========================
        TEX4.removeAllElements();
        s4.setColumnCount(0);
        s4.setColumnCount(2);
        s4.setRowCount(1);

        //s4.removeAllElements();
        AVWCA.STa4.setColumnCount(0);
        AVWCA.STa4.addColumn("Nbr CH");
        AVWCA.STa4.addColumn("Time");
        AVWCA.STa4.addColumn("Density");
        AVWCA.STa4.setRowCount(1);

        AVWCA.ST4.setColumnCount(0);
        AVWCA.ST4.addColumn("N°");
        AVWCA.ST4.addColumn("Times");
        AVWCA.ST4.addColumn("Num. of Ch");
        AVWCA.ST4.setRowCount(1);
        Dt4 = 0;
        
        //==========================Param MOZO========================
        s5.setColumnCount(0);
        s5.setColumnCount(2);
        s5.setRowCount(1);
        TChoixDestCBL = 0;
        DestChoisi = false;
        Dt5 = 0;
        departMOZO = true;
        periodeMozo = -1;

    }
//============================================================================== 

    public static void RUNNING() {
        System.out.println("Debut RUNNING ADHOC");
        
        
        Executer = new Thread() {
            public void run() {
            
                //ActualTime = vanetsim.gui.Renderer.getInstance().getTimePassed();
                                   try {
//                        int ActualTime = vanetsim.gui.Renderer.getInstance().getTimePassed();
//                        System.out.println("Actual Time === "+ActualTime);
//                        if(ActualTime >= tsim * TIME_PER_STEP)
//                        {
//                            vanetsim.gui.Renderer.getInstance().notifySimulationRunning(false);
//                            adhoc.Adhoc.running_ADHOC = false;
//                        }
                           //-----------------------------
                           //Créer un nom de fichier basé sur la date et l'heure
//        SimpleDateFormat filePattern = new SimpleDateFormat("ddMMyyyy_HHmm");
//         String filename = filePattern.format(new Date()) + ".txt";
//         //ouvrir le fichier
//         File file = new File("TraceExecution_" + filename);
//         try {
//         PrintStream printStream = new PrintStream(file);
//         System.setOut(printStream);
////         System.out.println("            Lancement de l'application adhoc");
////         System.out.println("            --------------------------------\n");
//         } catch (FileNotFoundException e) {
//         e.printStackTrace();
//         }
                        //-----------------Création d'un nouveau dossier--------------------------
                        System.out.println("path ============================== "+path);
                        Files.createDirectories(path);
                        
                        
                    } catch (IOException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                while (running_ADHOC == true) {

//int tmpTimePassed = vanetsim.gui.Renderer.getInstance().getTimePassed();

                    //-----------------------------
                    long t1, diff1, t2, diff2, t3, diff3, t4, diff4, t5, diff5, t6, diff6, t7, diff7, t8, diff8, t9, diff9, t10, diff10;

                    //-------------------- Phase BROADCAST ---------------------
                   // for(int u=0;u<vanetsim.scenario.Vehicle.TV.length;u++)
                   // for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++) vanetsim.scenario.Vehicle.TR[u][v]=-1;
                    System.out.println("-------------------------------> : taille de tv "+vanetsim.scenario.Vehicle.TV.length);
                    GetVehActif();
                    System.out.println("Liste des véhicules actifs obtenue");
                    if (Lvactif1 != null) 
                    {
                         System.out.println("Liste de véhicule actifs obtenu, taille Lvactif1 "+Lvactif1.length);
                        //System.out.println("NbvActif = "+NbvActif+" Lvactif1.length"+Lvactif1.length);
                        NbvActif = Lvactif1.length;
                        System.out.println(" Nombre de véhicule actifs === " + Lvactif1.length);

                        //--------------

                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                            //System.out.println("--------------BROADCAST------------------");
                        System.out.println("Compteur === " + compteur+"  posBFM === " + posBFM);
   
                        //System.out.println(" posBFM = " + posBFM + " TBFM lenght " + TBFM.length + " Compteur =" + compteur + " Compteur%12 = " + (int) (compteur % 12));
                        if ((posBFM < TBFM.length) && (TBFM[posBFM][0] == false) && (posBFM <= (int) ((compteur) / (12*1000))) && (((((int) (compteur) % (12*1000)))) >= 6*1000/**1000*/)&&(((((int) (compteur) % (12*1000)))) <= 7*1000/**1000*/)) 
                        {
                            System.out.println("Compteur === " + compteur+"  posBFM === " + posBFM);
                            TBFM[posBFM][0] = true;
                            AFC.propriete(PN, Lvactif1);
                            //System.out.println("posBFM === " + posBFM);
                            System.out.println("Début de Broadcast n°: " + (posBFM + 1));
                            //System.out.println("Compteur === " + compteur);
                            t1 = System.currentTimeMillis();
                            BroadCast();
                            diff1 = (long) (System.currentTimeMillis() - t1);
                            System.out.println("-------------------------------------------------------");
                            System.out.println("Temps de broadcast (FC) n°:" + (posBFM + 1) + " est " + diff1 + " ms.");
                            //System.out.println("-------------------------------------------------------");
                            System.out.println("Fin de Broadcast (FC)");

                      // }// Fin Condition Broadcast
                            
                        
                    //}// Fin Condition (Lvactif!=null)
                    //--------------- Fin de la phase BROADCAST ----------------
                    
//*********************************************    Phase FORMATION    ****************************************************************

//################################################ Formation Fitness Clustering   #####################################################################
                    //System.out.println("Formation : posBFM = " + posBFM + " TBFM lenght " + TBFM.length + " Compteur =" + compteur + " Compteur%12 = " + (int) (compteur % 12) + " TBFM[" + posBFM + "][1] = " + TBFM[posBFM][1]);                              //System.out.println("Debut Formation");
                    //if ((posBFM < TBFM.length) && (TBFM[posBFM][0] == true)&& (TBFM[posBFM][1] == false) && (posBFM <= (int) (compteur / (12*1000))) && (((((int) (compteur) % (12*1000)))) >= 7*1000/**1000*/)&&(((((int) (compteur) % (12*1000)))) <= 8*1000/**1000*/)) 
                   // {
                        //NumExec++;
                        System.out.println("  (A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ")"); 
                        System.out.println("Compteur === "+compteur);
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("FORMATION Fitness Clustering n°:" + (posBFM + 1));
                        System.out.println("---------------------------------------------------------------------");
                        AFC.propriete(PN, LvactifOld);
                        TBFM[posBFM][1] = true;
                        bestPathFC.removeAllElements();
                        t1 = System.currentTimeMillis();
                        Formation(LvactifOld);
                        diff1 = (long) (System.currentTimeMillis() - t1);
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Fin de formation (FC). \nTemps de formation n°:" + (posBFM + 1) + " est " + diff1 + " ms.");

                        //--------------------------------------------------
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Construction de chemins entre groupes (FC) pour la formation n°:" + (posBFM + 1));
                        System.out.println("---------------------------------------------------------------------");
                        t2 = System.currentTimeMillis();
                        adhoc.Groupes.ConstructGroup();
                        diff2 = (long) (System.currentTimeMillis() - t2);
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Fin de construction de groupes (FC). \nTemps consommé pour la phase n°:" + (posBFM + 1) + " est " + diff2 + " ms.");
                        System.out.println("---------------------------------------------------------------------\n");

                            //--------------------------------------------------
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Sauvegarde des groupes (FC) pour la phase n°:" + (posBFM + 1));
                        System.out.println("---------------------------------------------------------------------");

                        SimpleDateFormat filePattern0 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupFC = "GroupesFC" + filePattern0.format(new Date()) + ".txt";
                        File GroupFC = new File(FilenameGroupFC);
                        
                                                try {
                            fst1 = new FileOutputStream(FilenameGroupFC);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        outt1 = new PrintWriter(fst1);
                        System.out.println("Sauvegarde des groupes formés.");
                        SAVEGROUPE1(FilenameGroupFC, PN);
                        System.out.println("Fin de sauvegarde.");
                        
                    if (((compteur / 100) >= tsim - 2) || (posBFM == TBFM.length - 1)) {

                            if (savefc == true) {
                                savefc = false;
                                int EntFC[][] = new int[s1.getRowCount() - 1][2], SrtFC[][];
                                for (int u = 0; u < s1.getRowCount() - 1; u++) {
                                    EntFC[u][0] = Integer.parseInt(s1.getValueAt(u, 0).toString());
                                    EntFC[u][1] = Integer.parseInt(s1.getValueAt(u, 1).toString());
                                }
                                System.out.println("Sauvegarde des résultats pour FitnessClustering");
                                SAVE1(path + "//NumCH-Time_FC"/* + date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", EntFC);
                                SrtFC = Normalisation(EntFC, pcolect, tsim);
                                SAVE1(path + "//NumCH-Time_Normalise_FC_" /*+ date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", SrtFC);

                                SAVE(path + "//TimeExecution_FC_" /*+ date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1);
                                SAVE(path + "//TimeMaintenance_FC_"/* + date_courante()*/ + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1M);
                                // JOptionPane.showConfirmDialog(null, "pause");

                            }
                        }

                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Fin de Sauvegarde des groupes (FC) pour la phase n°:" + (posBFM + 1));
                        System.out.println("---------------------------------------------------------------------\n");
                        
////#############################################     Formation APROVE    ##################################################################
                        
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Execution de l'algorithme : APROVE");
                        System.out.println("---------------------------------------------------------------------");

                        //System.out.println("------------------------------------------------------------------------------------------------------\n");
                        System.out.println("Découverte de voisinage APROVE");
                        for (int i = 0; i < Lvactif1.length; i++) {
                            vanetsim.scenario.Vehicle.TV[Lvactif1[i]].OVS.removeAllElements();
                            vanetsim.scenario.Vehicle.Vehicule.DecouvertVoisinage(Lvactif1[i], portee, Lvactif1);
                            for (int j = 0; j < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize(); j++) {
                                if (i < Lvactif1.length) {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].OVS.addElement(vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getElementAt(j));
                                }
                            }

                        }

                        System.out.println("Début de formation APROVE (APV) n:° " + (posBFM + 1));
                        t3 = System.currentTimeMillis();
                        AAPV.AlgorithmAPROVE(ss, rr, aa, Lvactif1);

                        diff3 = (long) (System.currentTimeMillis() - t3);

                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Fin de formation (APV). \nTemps de formation (APV) n°:" + (posBFM + 1) + " : " + diff2 + " ms.");
                        System.out.println("---------------------------------------------------------------------\n");

                        TEX2.addElement("" + (diff2));
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Sauvegarde des groupes (APV)");
                        System.out.println("---------------------------------------------------------------------");
                        AAPV.propriete(pp, Lvactif1);//affichage Aprove Formation
                        
                        
                        SimpleDateFormat filePattern1 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupAPV = "GroupesAPROV" + filePattern1.format(new Date()) + ".txt";
                        File GroupAPV = new File(FilenameGroupAPV);
                        try {
                            fst2 = new FileOutputStream(FilenameGroupAPV);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        outt2 = new PrintWriter(fst2);
                        SAVEGROUPE2(FilenameGroupAPV, pp);
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Fin de sauvegarde des groupes (APV)");
                        System.out.println("---------------------------------------------------------------------\n");
////#############################################    Formation VWCA   ######################################################################
                        System.out.println("---------------------------------------------------------------------");
                        System.out.println("Execution de l'algorithme : VWCA\n");
                        System.out.println("---------------------------------------------------------------------\n");
                        //----------------Sauvegardes des groupes-------------------
                        SimpleDateFormat filePattern3 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupVWCA = "GroupesVWCA" + filePattern3.format(new Date()) + ".txt";
                        File GroupVWCA = new File(FilenameGroupVWCA);
                        try {
                            fst = new FileOutputStream(FilenameGroupVWCA);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        outt = new PrintWriter(fst);
                        //----------------------------------------------------------
                            TBFM[posBFM][1] = true;
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            System.out.println("Formation n°: " + (posBFM + 1) + " pour l'algorithme VWCA");
                            System.out.println("Execution de l'algorithme MMV");
                            System.out.println("V. Actifs : " + NbvActif);
                            System.out.println("V. Inactifs : " + NbvInactif);
                            System.out.println("V. Hors système : " + NbvHors);
                            System.out.println("Nombre de véhicules actifs est " + Lvactif1.length);
                            t4 = System.currentTimeMillis();
                            AVWCA.MmvAlgorithm(Lvactif1);
                            diff4 = System.currentTimeMillis() - t4;
                            System.out.println("temps MMV ==" + diff4);

                            for (int i = 0; i < Lvactif1.length; i++) {
                                AVWCA.NewVoisin(Lvactif1[i], Lvactif1);
                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].nbvoisin = vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize();

                            }
                            System.out.println("Execution de l'algorithme AATR");

                            t8 = System.currentTimeMillis();
                            AVWCA.ExecutingAATRalgorithm(Lvactif1);
                            diff8 = System.currentTimeMillis() - t8;
                            System.out.println("temps AATR ==" + diff8);

                            System.out.println("Calcul des paramètres : Entropie, Distance moyenne et poids W des noeuds");
                            t9 = System.currentTimeMillis();
                            for (int i = 0; i < Lvactif1.length; i++) {

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Entropy = AVWCA.CalculatingEntropy(Lvactif1[i]);

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Dmoy = AVWCA.CalculatingDistMoyenne(Lvactif1[i]);

                                vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Weight = AVWCA.CalculatingWeightClusteringValue(Lvactif1[i]);

                            }
                            diff9 = System.currentTimeMillis() - t9;
                            System.out.println("temps entropie + Distmoyenne + Weights ==" + diff9);

                            //-------------------------------
                            for (int i = 0; i < Lvactif1.length; i++) {
                                double pmin = 1e10; //pour stocker le poids min
                                int idx = -1;//pour stocker elt ayant le poids min
                                int elet;
                                for (int j = 0; j < vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getSize(); j++) {
                                    elet = Integer.parseInt(vanetsim.scenario.Vehicle.TV[Lvactif1[i]].VS.getElementAt(j).toString());
                                    if (vanetsim.scenario.Vehicle.TV[elet].Weight < pmin) {
                                        pmin = vanetsim.scenario.Vehicle.TV[elet].Weight;
                                        idx = elet;
                                    }

                                }
                                //----------------
                                if (pmin > vanetsim.scenario.Vehicle.TV[Lvactif1[i]].Weight) {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_ch_VWCA = true;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_mn_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_init_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].type_noeud_VWCA = "Ch";

                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].myCH_VWCA = Lvactif1[i];
                                } else {
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_ch_VWCA = false;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_mn_VWCA = true;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].est_init_VWCA = false;

                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].myCH_VWCA = idx;
                                    vanetsim.scenario.Vehicle.TV[Lvactif1[i]].type_noeud_VWCA = "Mn";
                                }
                                //----------------------------------------------
                                Algorithms.VWCA.CalculateStateDuration_VWCA();
                                //-----------------------------------------------

                            }
                            //-------------------------------
                            for (int n = 0; n < Lvactif1.length; n++) {
                                System.out.println("Le noeud " + Lvactif1[n] + " est   " + vanetsim.scenario.Vehicle.TV[Lvactif1[n]].type_noeud_VWCA);
                            }
                            System.out.println("Sauvegarde des groupes");
                            AVWCA.Clustering(Lvactif1, GROUPE1); // regrouper les noeuds
                            SAVEGROUPE(FilenameGroupVWCA, GROUPE1);

                            diff10 = (long) (System.currentTimeMillis() - t4);
                            TEX4.addElement("" + (diff4));
                            
                            System.out.println("TEMPS D'EXECUTION VWCA EST : " + diff10 + " ms");
                                                Dt4++;
                    /*if (Dt4 == 10) {
                       
                         int EntVWCA[][] = new int[s4.getRowCount() - 1][2], SrtVWCA[][];
                         for (int u = 0; u < s4.getRowCount() - 1; u++) {
                         EntVWCA[u][0] = Integer.parseInt(s4.getValueAt(u, 0).toString());
                         EntVWCA[u][1] = Integer.parseInt(s4.getValueAt(u, 1).toString());
                         }
                         System.out.println("Sauvegarde des résultats pour FitnessClustering");
                         SAVE1("NumCH-Time_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", EntVWCA);
                         SrtVWCA = Normalisation(EntVWCA, pcolect, tsim);
                         SAVE1("NumCH-Time_Normalise_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", SrtVWCA);
                         SAVE("TimeExecution_FC_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v(A=" + alpha + "-B=" + beta + "-G=" + gamma + "-S=" + sigma + ").txt", TEX1);
                         }*/
                         

                        s4.setValueAt("" + (compteur / 100), s4.getRowCount() - 1, 0);
                        s4.setValueAt("" + GROUPE1.getRowCount(), s4.getRowCount() - 1, 1);
                        s4.setRowCount(s4.getRowCount() + 1);

                        //s4.setValueAt("" + (compteur / 100) + "		" + GROUPE1.getRowCount());
                        AVWCA.ST4.setValueAt("" + AVWCA.ST4.getRowCount(), AVWCA.ST4.getRowCount() - 1, 0);
                        AVWCA.ST4.setValueAt("" + (compteur / 100), AVWCA.ST4.getRowCount() - 1, 1);
                        AVWCA.ST4.setValueAt("" + GROUPE1.getRowCount(), AVWCA.ST4.getRowCount() - 1, 2);
                        AVWCA.ST4.setRowCount(AVWCA.ST4.getRowCount() + 1);
                        //------------------------------

                        AVWCA.STa4.setValueAt("" + GROUPE1.getRowCount(), AVWCA.STa4.getRowCount() - 1, 0);
                        AVWCA.STa4.setValueAt("" + (compteur / 100), AVWCA.STa4.getRowCount() - 1, 1);
                        AVWCA.STa4.setValueAt("" + Lvactif1.length, AVWCA.STa4.getRowCount() - 1, 2);
                        AVWCA.STa4.setRowCount(AVWCA.STa4.getRowCount() + 1);
                        //------------------------------

                        Dt4 = 0;
                    }
                    
                    if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
                        sauvegarde = true;
                        int EntVWCA[][] = new int[s4.getRowCount() - 1][2], SrtVWCA[][];
                        for (int u = 0; u < s4.getRowCount() - 1; u++) {
                            EntVWCA[u][0] = Integer.parseInt(s4.getValueAt(u, 0).toString());
                            EntVWCA[u][1] = Integer.parseInt(s4.getValueAt(u, 1).toString());
                        }
                        System.out.println("Sauvegarde des résultats pour VWCA");
                        //SAVE("NumCH-Time_VWCA_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v .txt", s4);

                        SAVE1(path + "//NumCH-Time_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", EntVWCA);
                        //SrtVWCA = Normalisation(EntVWCA, pcolect, tsim);
                        //SAVE1(path + "//NumCH-Time_Normalise_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", SrtVWCA);
                        SAVE(path + "//TimeExecution_VWCA_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", TEX4);
                        System.out.println("====================FIN VWCA ===============");
                    }
                            
                   System.out.println("------------------------------------------------------------------------------------------------------\n");
                        

////#############################################    Formation MOZO   ######################################################################
                        //----------------Sauvegardes des groupes-------------------
                        SimpleDateFormat filePattern4 = new SimpleDateFormat("ddMMyyyy_HHmm");
                        FilenameGroupMOZO = "GroupesMOZO" + filePattern4.format(new Date()) + ".txt";
                        File GroupMZ = new File(FilenameGroupMOZO);
                        
                        try {
                            fst5 = new FileOutputStream(FilenameGroupMOZO);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        outt5 = new PrintWriter(fst5);
                        System.out.println("================  CREATION MOZO =============================="+periodeMozo);
                        
                        if (periodeMozo == -1) {
                            
                        Algorithms.Mozo.CreationMozo(Lvactif1.length, Lvactif1);
                        
                        periodeMozo++;
                        System.out.println("periodeMozo ==="+periodeMozo);
                        } else if (periodeMozo == 0) {
                        TMozoDeb = (double) compteur / 40.0;
                        System.out.println("Temps début :======================================= " + TMozoDeb);
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        System.out.println("Taille Lvactif : " + Lvactif1.length + " / taille Moz" + adhoc.Adhoc.MOZ.length);
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                            Algorithms.Mozo.CalculVoisinage(i, periodeMozo, -1);
                        }
                        periodeMozo++;
                    } else if (periodeMozo == 1) {
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            Algorithms.Mozo.CalculVoisinage(i, periodeMozo, -1);
                        }
                        periodeMozo++;
                    } else if (periodeMozo == 2) {
                        TChoixDestCBL++;
                        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(Lvactif1, portee);
                        TMzoFin = (double) compteur / 100.0;
                        System.out.println("Temps fin :======================================= " + TMzoFin);
                        int idCapt = 0;
                        double simCapt = 0.0;

                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) {
                            if (TMozoDeb != TMzoFin) {
                                Algorithms.Mozo.CalculVoisinage(i, periodeMozo, (TMzoFin - TMozoDeb));

                            }
                        }
                        //------------------------------
                        int id = 0;
                        DefaultListModel Tcapt = new DefaultListModel();
                        for (int i = 0; i < adhoc.Adhoc.MOZ.length; i++) 
                        {
                            idCapt = 0;
                            simCapt = 0.0;

                            if (adhoc.Adhoc.MOZ[i].Tintersec.size() == 0) 
                            {
                                adhoc.Adhoc.MOZ[i].idCapt = adhoc.Adhoc.MOZ[i].idMoz;
                                adhoc.Adhoc.MOZ[i].simCapt = adhoc.Adhoc.MOZ[i].sim;
                            } else {

                                for (int j = 0; j < adhoc.Adhoc.MOZ[i].Tintersec.size(); j++) 
                                {
                                    
                                    id = Integer.parseInt(adhoc.Adhoc.MOZ[i].Tintersec.getElementAt(j).toString());
                                    if(id<adhoc.Adhoc.MOZ.length)
                                    {
                                    //System.out.println("id avant = "+id+"/"+adhoc.Adhoc.MOZ.length+"");
                                    id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Algorithms.Mozo.Tpos);

                                    //System.out.println("id apres = "+id+"/"+adhoc.Adhoc.MOZ.length+"");
                                    if (adhoc.Adhoc.MOZ[id].sim > adhoc.Adhoc.MOZ[i].sim) 
                                    {
                                        idCapt = id;
                                        simCapt = adhoc.Adhoc.MOZ[id].sim;
                                    }
                                    }
                                }
                                adhoc.Adhoc.MOZ[i].idCapt = idCapt;
                                adhoc.Adhoc.MOZ[i].simCapt = simCapt;
                            }
                            //--------------------------
                            if (VerifExiste(adhoc.Adhoc.MOZ[i].idCapt, Tcapt) == false) {
                                Tcapt.addElement("" + adhoc.Adhoc.MOZ[i].idCapt);
                            }
                            //--------------------------
                        }
                        //-----------------------------
                        Dt5++;
                        if (Dt5 == 5) {

                            s5.setValueAt("" + (compteur / 100), s5.getRowCount() - 1, 0);
                            s5.setValueAt("" + Tcapt.getSize(), s5.getRowCount() - 1, 1);
                            s5.setRowCount(s5.getRowCount() + 1);

                            Dt5 = 0;
                        }
                        //-----------------------------
                        //-------------------Tri à bulle---------------------
                        double vect[][] = new double[Tcapt.getSize()][2];

                        for (int i = 0; i < Tcapt.getSize(); i++) {
                            id = Integer.parseInt(Tcapt.getElementAt(i).toString());
                            //System.err.println(" Id ==============================================> "+id);
                            // id = Simulation.Vehicule.GetPositionElement(id+1, Lvactif1);
                            for (int k = 0; k < adhoc.Adhoc.MOZ.length; k++) {
                                if (adhoc.Adhoc.MOZ[k].ident == id) {
                                    //System.err.println(" Id ==> "+id+" / taille vect ==>"+vect.length+"   / Taille de MOZ : "+adhoc.Adhoc.MOZ.length  );
                                    vect[i][0] = adhoc.Adhoc.MOZ[k].posXmoz;
                                    vect[i][1] = adhoc.Adhoc.MOZ[k].idMoz;
                                }

                            }

                        }
                        adhoc.Adhoc.Tribull(vect, vect.length - 1);

                        /* for (int i = 0; i < vect.length; i++) 
                         {
                         System.err.println("vect["+i+"][0] == "+ (int)vect[i][0]+" ======>  "+"vect["+i+"][1] == "+ (int)vect[i][1]);
                         }*/
                        System.out.println("val1 :    " + (compteur / 100) + "           val2 :     " + tsim);
                        if ((((compteur / 100) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
                        {
                        sauvegarde =true;
                        //============Sauvegarde des résultats pour MOZO===============
                        int EntMZ[][] = new int[s5.getRowCount() - 1][2], SrtMZ[][];
                        for (int u = 0; u < s5.getRowCount() - 1; u++) 
                        {
                            EntMZ[u][0] = Integer.parseInt(s5.getValueAt(u, 0).toString());
                            EntMZ[u][1] = Integer.parseInt(s5.getValueAt(u, 1).toString());
                        }
                        System.out.println("Sauvegarde des résultats pour MOZO");
                        SAVE1(path + "//NumCH-Time_MZ"/* + date_courante()*/ + ").txt", EntMZ);
                        SrtMZ = Normalisation(EntMZ, pcolect, tsim);
                        SAVE1(path + "//NumCH-Time_Normalise_MZ_" /*+ date_courante()*/ + ").txt", SrtMZ);
                         }
                        //============ Fin de sauvegarde des résultats pour MOZO===============

                        //------------------------------
                        Algorithms.Mozo.CollectEletMax(vect);
                        //------------------------------
                        /*for(int i=0;i<Tcapt.getSize();i++)
                         {
                         System.err.println("Tcapt["+i+"] ======  "+Tcapt.getElementAt(i));
                         }*/
                        CLV = new Algorithms.Mozo.ConstructCLVTree[vect.length];
                        Algorithms.Mozo.ConstructCLVTree.ConstructCLVTree(vect);

                        for (int k = 0; k < Tcapt.getSize(); k++) {
                            for (int l = 0; l < adhoc.Adhoc.MOZ.length; l++) {
                                //System.err.println("idCapt :  "+adhoc.Adhoc.MOZ[l].idCapt+" <====> Vect : "+(int)vect[k][1]+"  <====> ident : "+adhoc.Adhoc.MOZ[l].ident +"   <====>  idMoz : "+adhoc.Adhoc.MOZ[l].idMoz);
                    } 
                }
            }
                    periodeMozo = -1;
                    Algorithms.Mozo.CalculateStateDuration_MOZO();
                           }//à désactiver si MOZO commenté
                   
                    }//à désactiver si MOZO commenté
                        //----------------------------------------------------------

//#############################################    Génération d'un évènement   ######################################################################
//                        System.out.println("choix de source et cible : ");
//                        System.out.println("TSC length " + TSC.getRowCount());
//                        TSC.setValueAt("" + compteur, TSC.getRowCount() - 1, 0);
//
//                        t4 = System.currentTimeMillis();
//                        Algorithms.Mozo.ChoixSourceCible(10000, 40000, Lvactif1);
//                        diff4 = (long) (System.currentTimeMillis() - t4);
//                        System.out.println("Source et cible déterminées dans " + diff4 + " ms.");
//
//                        TSC.setValueAt("" + IdSrc, TSC.getRowCount() - 1, 1);
//                        TSC.setValueAt("" + IdCbl, TSC.getRowCount() - 1, 2);
//                        TSC.setRowCount(TSC.getRowCount() + 1);
//
//                        DefaultListModel pathFC = new DefaultListModel();
//
////                            if (GRS.length > 1) {
////                                Algorithms.FitnessClustering.RoutingFc(pathFC); À revoir
////                            }
//                        //---------------------------Construction de path de src vers cbl---------------------------------
//                        t5 = System.currentTimeMillis();
//                        DefaultListModel prov = new DefaultListModel();
//                        for (int i = 0; i < bestPathFC.getSize(); i++) {
//                            if (adhoc.Adhoc.VerifExiste(Integer.parseInt(bestPathFC.getElementAt(i).toString()), prov) == false) {
//                                prov.addElement(bestPathFC.getElementAt(i).toString());
//                            }
//                        }
//                        bestPathFC.removeAllElements();
//                        for (int i = 0; i < prov.getSize(); i++) {
//                            bestPathFC.addElement(prov.getElementAt(i).toString());
//                        }
//
//                        System.out.print(" bestPathFC : ");
//
//                        for (int i = 0; i < bestPathFC.getSize(); i++) {
//                            //if (adhoc.Adhoc.VerifExiste(Integer.parseInt(bestPathFC.getElementAt(i).toString()), adhoc.Adhoc.bestPathFC) == false) {
//                            System.out.print(bestPathFC.getElementAt(i).toString() + " --> ");
//                            //}
//                        }
//
//
//                        if (GRS.length > 1) {
//
//                                //--------------------Path from Source to Cible -------------------------
//                            //long t4 = System.currentTimeMillis();
//                            //Groupes.PathSrcCbl(bestPathFC, IdSrc, IdCbl, prov); //Nano
//                            diff5 = (long) (System.currentTimeMillis() - t5);
//                            System.out.println(" route construite de source vers cible en " + diff5 + " ms.");
//                            //-----------------------------------------------------------------------
//                        }
//
//                  
//                //}// Fin Condition Formation
//                    }
//               }
//*********************************************    Phase Maintenance    ****************************************************************

//################################################ Maintenance Fitness Clustering   #####################################################################
                    if ((posBFM < TBFM.length) &&(TBFM[posBFM][0] == true)&& (TBFM[posBFM][1] == true)&& (TBFM[posBFM][2] == false) && (posBFM <= (int) ((compteur) / (12*1000))) && (((((int) (compteur) % (12*1000/**1000*/)))) >= 0) && (((((int) (compteur) % (12*1000/**1000*/)))) <= 1*1000)&& (compteur > 1*1000)) 
                    {
                        
                        System.out.println("--------------MAINTENANCE");
                        System.out.println("Compteur === "+compteur);
                        System.out.println("Début de maintenance (FC) n°:" + (posBFM + 1));
                        AFC.propriete(PN, LvactifOld);
                        TBFM[posBFM][2] = true;
                        t6 = System.currentTimeMillis();
                        Maintenance(LvactifOld);
                        diff6 = System.currentTimeMillis() - t6;
                        System.out.println("Fin de maintenance (FC). \nTemps de maintenance n°:" + (posBFM + 1) + " est " + diff6 + " ms.");
                        System.out.println("Compteur === " + compteur);
                        TEX1M.addElement("" + (NbvActif) + "		" + (double) (diff6));
//################################################ Fin de Maintenance Fitness Clustering   #####################################################################
 
////################################################ Maintenance APROVE   #####################################################################
                        System.out.println("------------------------------------------------------------------------------------------------------\n");
                        System.out.println("Début de maintenance n:° " + (posBFM + 1) + " pour APROVE");
                        TBFM[posBFM][2] = true;
                        t7 = System.currentTimeMillis();
                        AAPV.MaintenanceAPROVE(Lvactif1);
                        AAPV.propriete(pp, Lvactif1); //affichage Aprove Maintenance
                        System.out.println("Fin de maintenance");
                        diff7 = System.currentTimeMillis() - t7;
                        System.out.println("Durée de la maintenance : " + diff7 + " ms");
                        System.out.println("------------------------------------------------------------------------------------------------------\n");
                            //        LvactifOld =   new int[NbvActif];
                        //         for(int i=0;i<LvactifOld.length;i++) LvactifOld[i] = Lvactif[i];
                        //---------------------------------------------
                        //----------------------------------
                        //Return APROVE
                        Dt2++;
                        if (Dt2 == 10) {
                            s2.setValueAt("" + (compteur), s2.getRowCount() - 1, 0);
                            s2.setValueAt("" + AAPV.nbrchapv, s2.getRowCount() - 1, 1);
                            s2.setRowCount(s2.getRowCount() + 1);

                            //s2.addElement("" + (compteur / 100) + "		" + AAPV.nbrchapv);
                            //   System.out.println("===================A9================ ");
                            AAPV.ST2.setValueAt("" + AAPV.ST2.getRowCount(), AAPV.ST2.getRowCount() - 1, 0);
                            AAPV.ST2.setValueAt("" + (compteur), AAPV.ST2.getRowCount() - 1, 1);
                            AAPV.ST2.setValueAt("" + AAPV.nbrchapv, AAPV.ST2.getRowCount() - 1, 2);
                            AAPV.ST2.setRowCount(AAPV.ST2.getRowCount() + 1);
                            //  System.out.println("===================A10================ ");
                            //------------------------------
                            AAPV.STa2.setValueAt("" + AAPV.nbrchapv, AAPV.STa2.getRowCount() - 1, 0);
                            AAPV.STa2.setValueAt("" + (compteur), AAPV.STa2.getRowCount() - 1, 1);
                            AAPV.STa2.setValueAt("" + Lvactif1.length, AAPV.STa2.getRowCount() - 1, 2);
                            AAPV.STa2.setRowCount(AAPV.STa2.getRowCount() + 1);
                            //------------------------------
                            //------------------------------
                            Dt2 = 0;
                        }
                        //System.out.println("Compteur APROVE ==================================> " + (compteur) + "  / " + tsim);
                        if ((((compteur) >= tsim) || (TBFM[TBFM.length - 1][idxlastTask] == true)) && (sauvegarde == false)) {
                            sauvegarde = true;
                            int EntAPV[][] = new int[s2.getRowCount() - 1][2], SrtAPV[][];
                            System.out.println("taille EntAPV "+EntAPV.length+ " taille s2 "+s2.getRowCount());
                            for (int u = 0; u < s2.getRowCount() - 1; u++) {
                                EntAPV[u][0] = Integer.parseInt(s2.getValueAt(u, 0).toString());
                                System.out.println("Valuer s2 - 0"+Integer.parseInt(s2.getValueAt(u, 0).toString()));
                                EntAPV[u][1] = Integer.parseInt(s2.getValueAt(u, 1).toString());
                                System.out.println("Valuer s2 - 1"+Integer.parseInt(s2.getValueAt(u, 1).toString()));
                            }
                            System.out.println("Sauvegarde des résultats pour APROVE");
                            //SAVE("NumCH-Time_APROVE_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v .txt", s2);

                            SAVE1(path + "//NumCH-Time_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", EntAPV);
                            SrtAPV = Normalisation(EntAPV, pcolect, tsim);
                            SAVE1(path + "//NumCH-Time_Normalise_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", SrtAPV);
                            SAVE(path + "//TimeExecution_APROVE_" + date_courante() + "_L=" + lambda + "_TS=" + tsim + "s_" + S.length + "v.txt", TEX2);
                            System.out.println("------------------------------------------------------------------------------------------------------\n");
                            //   JOptionPane.showConfirmDialog(null, "pause1");

                            // Modif 06-09-2017
                            for (int i = 0; i < Lvactif1.length; i++) {
                                if (vanetsim.scenario.Vehicle.TV[Lvactif1[i]].etat.equals("Actif")) {
                                    Algorithms.FitnessClustering.CloseStateDuration(Lvactif1[i], tsim);
                                }
                            }
                        }
////################################################ Fin de Maintenance APROVE   #####################################################################

                        posBFM++;        
//--------------FIN DE MAINTENANCE-------------

                    
                    }
                    //compteur=compteur+TIME_PER_STEP;
                    
                    
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Adhoc.class.getName()).log(Level.SEVERE, null, ex);
                    }  
            }
        }
     
        
//==============================================================================
};
       Executer.start();
    }
}
