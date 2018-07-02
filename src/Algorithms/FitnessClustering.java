/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author saliha-benkerdagh-
 */
public class FitnessClustering {

    static int negatif = 0;

    //-----------------------------pour les stats----------------------------------
    public static DefaultTableModel ST1 = new DefaultTableModel();
    public static DefaultTableModel STa1 = new DefaultTableModel();
//------------------------------------------------------------------------------
    static Random rnd = new Random();
    static long TC = System.currentTimeMillis();
    public static int nbrClFc;

//==============================================================================
    public static boolean MembreCouverture(int nd, int ndC) {
        boolean b = false;

        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[ndC].Lcouv.length; i++) {
            if ((int) (vanetsim.scenario.Vehicle.TV[ndC].Lcouv[i][0]) == nd) {
                b = true;
            }
        }
        return b;
    }
//==============================================================================

    public static int GetEtat(int id, int tst[][]) {
        int eta = 0;
        for (int i = 0; i < tst.length; i++) {
            if (tst[i][0] == id) {
                eta = tst[i][1];
            }
        }
        return eta;
    }

    public static void AffectLastStat(DefaultTableModel tich, int nbact) {
        int tab[][] = new int[tich.getRowCount() - 1][2];
        //System.out.println("Debut AffectLastStat");
        for (int i = 0; i < tich.getRowCount() - 1; i++) {
            tab[i][0] = Integer.parseInt(tich.getValueAt(i, 0).toString());
            tab[i][1] = Integer.parseInt(tich.getValueAt(i, 1).toString());
        }
        //adhoc.GlobalParameters.ListConfirmed 
        DefaultListModel tmp = new DefaultListModel();
        boolean b;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                b = false;
                for (int k = 0; k < tmp.getSize(); k++) {
                    if (tmp.getElementAt(k).toString().trim().equals("" + tab[i][j])) {
                        b = true;
                    }
                }
                if (b == false) {
                    tmp.addElement("" + tab[i][j]);
                }
            }
        }
        //System.out.println("-------------------- AffectLastStat -------------------- 1");
        int tst[][] = new int[tmp.getSize()][2];
        for (int i = 0; i < tst.length; i++) {
            tst[i][0] = Integer.parseInt(tmp.getElementAt(i).toString());
            tst[i][1] = 0;
        }
        //System.out.println("-------------------- AffectLastStat -------------------- 2");
        //----------------------------------------------------------------------------       
        for (int i = 0; i < tst.length; i++) {
            b = false;
            for (int j = 0; j < tab.length; j++) {
                if (tab[j][1] == tst[i][0]) {
                    for (int k = 0; k < tab.length; k++) {
                        if (tab[k][0] == tst[i][0]) {
                            b = true;
                        }
                    }
                }
            }
            if (b == true) {
                tst[i][1] = 1;
            }
        }
        //System.out.println("-------------------- AffectLastStat -------------------- 3");
        //------------------------------------------------------------------------------ 
        boolean arret = false, ddetrouve = false;
        int ddeur, dde;
        int pos1 = 0/*demandeur*/, pos2 = 0/*demandé*/, pos3 = 0/*élément courant*/;
        while (arret == false) {
            //System.out.println("-------------------- AffectLastStat -------------------- 4");
            for (int d = 0; d < tab.length; d++) {
                ddetrouve = false;

                ddeur = tab[d][0];
                dde = tab[d][1];

                if (((GetEtat(ddeur, tst) == 0) && (GetEtat(dde, tst) == 0)) || ((GetEtat(ddeur, tst) == 0) && (GetEtat(dde, tst) == 1))) {

                    for (int e = 0; e < tab.length; e++) {

                        if ((tab[e][0] == dde) && (tab[e][1] == ddeur))///
                        {
                            //-------------------------
                            if (dde < ddeur) {
                                for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == ddeur) {
                                        pos1 = p;
                                    }
                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == dde) {
                                        pos2 = p;
                                    }
                                }
                                adhoc.GlobalParameters.ListConfirmed[pos2][2] = 0; //demandé devient CH
                                adhoc.GlobalParameters.ListConfirmed[pos2][1] = adhoc.GlobalParameters.ListConfirmed[pos2][0];
                                adhoc.GlobalParameters.ListConfirmed[pos1][2] = 2; //demandé devient CH
                                adhoc.GlobalParameters.ListConfirmed[pos1][1] = adhoc.GlobalParameters.ListConfirmed[pos2][0];
                            } else {
                                adhoc.GlobalParameters.ListConfirmed[pos2][2] = 2; //demandé devient CH
                                adhoc.GlobalParameters.ListConfirmed[pos2][1] = adhoc.GlobalParameters.ListConfirmed[pos1][0];
                                adhoc.GlobalParameters.ListConfirmed[pos1][2] = 0; //demandé devient CH
                                adhoc.GlobalParameters.ListConfirmed[pos1][1] = adhoc.GlobalParameters.ListConfirmed[pos1][0];
                            }
                            //---------màj 0--> 1------------
                            for (int i = 0; i < tst.length; i++) {
                                if (tst[i][0] == dde) {
                                    tst[i][1] = 1;
                                }
                                if (tst[i][0] == ddeur) {
                                    tst[i][1] = 1;
                                }
                            }
                            //---------------------
                            //--------------------------

                        } else if ((tab[e][0] == dde) && (tab[e][1] != ddeur)) {
                            ddetrouve = true;
                            for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                                if (adhoc.GlobalParameters.ListConfirmed[p][0] == ddeur) {
                                    pos1 = p;
                                }
                                if (adhoc.GlobalParameters.ListConfirmed[p][0] == dde) {
                                    pos2 = p;
                                }
                            }
                            adhoc.GlobalParameters.ListConfirmed[pos2][2] = 0; //demandé devient CH
                            adhoc.GlobalParameters.ListConfirmed[pos2][1] = adhoc.GlobalParameters.ListConfirmed[pos2][0];

                            adhoc.GlobalParameters.ListConfirmed[pos1][2] = 2; //demandeur devient MN modifier Type_noeud est_ch_FC, est_sch_FC = false, est_mn_FC = false, est_sm_FC = false, est_init_FC = false;
                            adhoc.GlobalParameters.ListConfirmed[pos1][1] = adhoc.GlobalParameters.ListConfirmed[pos2][0];

                            //---------màj 0--> 1------------
                            for (int i = 0; i < tst.length; i++) {
                                if (tst[i][0] == dde) {
                                    tst[i][1] = 1;
                                }
                                if (tst[i][0] == ddeur) {
                                    tst[i][1] = 1;
                                }
                            }
                            //---------------------

                        }
                    }
                    if (ddetrouve == false)// faux dde n'appartient pas à toute la table tab  //reformuler test +} 2ème for***
                    {

                        //--------------Groupement---------------
                        tmp.removeAllElements();
                        for (int g = 0; g < tab.length; g++) {
                            if (tab[g][1] == dde) {
                                tmp.addElement("" + tab[g][0]);
                            }
                        }

                        if (tmp.getSize() > 0) {
                            if (tmp.getSize() == 1) {
                                //------------------------------
                                for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == ddeur) {
                                        pos1 = p;
                                    }
                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == dde) {
                                        pos2 = p;
                                    }
                                }
                                // Un seul noeud devient Sm, modifier Type_noeud est_ch_FC, est_sch_FC = false, est_mn_FC = false, est_sm_FC = false, est_init_FC = false;
                                adhoc.GlobalParameters.ListConfirmed[pos1][2] = 3;
                                adhoc.GlobalParameters.ListConfirmed[pos1][1] = adhoc.GlobalParameters.ListConfirmed[pos2][1]; // prend le CH du noeud demandé
                                //Sauvegarder le noeud intermédiaire
                                //------------------------------
                            } else {
                                // **** traitement délicat
                                int MI[][] = new int[tmp.getSize()][tmp.getSize()]; // matrice d'incidence
                                int maxMI[] = new int[tmp.getSize()]; //max matrice d'incidence

                                for (int u = 0; u < tmp.getSize(); u++) {
                                    maxMI[u] = 0;
                                    for (int v = 0; v < tmp.getSize(); v++) {

                                        if (MembreCouverture(Integer.parseInt(tmp.getElementAt(u).toString()), Integer.parseInt(tmp.getElementAt(v).toString())) == true) {
                                            MI[u][v] = 1;
                                            maxMI[u]++;
                                        } else {
                                            MI[u][v] = 0;
                                        }

                                    }

                                }
                                //System.out.println("-------------------- AffectLastStat -------------------- 5");
                                //------------------------
                                int id = Integer.parseInt(tmp.elementAt(0).toString()), max = 0;
                                for (int u = 0; u < tmp.getSize(); u++) { //TO Fix
                                    if (maxMI[u] > max) {
                                        id = Integer.parseInt(tmp.elementAt(u).toString());
                                        max = maxMI[u];
                                    } else if (maxMI[u] == max) {
                                        if (vanetsim.scenario.Vehicle.TV[Integer.parseInt(tmp.elementAt(u).toString())].Fitness < vanetsim.scenario.Vehicle.TV[id].Fitness) {
                                            id = Integer.parseInt(tmp.elementAt(u).toString());
                                            max = maxMI[u];
                                        }
                                    }
                                }

                                //------------------------
                                DefaultListModel LM = new DefaultListModel(); // Liste des noeuds ayant un max de voisins partagés
                                for (int u = 0; u < tmp.getSize(); u++) {
                                    if (maxMI[u] == max) {
                                        LM.addElement(tmp.getElementAt(u));
                                    }
                                }
                                //------------------------
                                double fit = Math.pow(10, 10);
                                id = 0;
                                for (int u = 0; u < LM.getSize(); u++) {
                                    if (vanetsim.scenario.Vehicle.TV[Integer.parseInt(LM.getElementAt(u).toString())].Fitness < fit) {
                                        fit = vanetsim.scenario.Vehicle.TV[Integer.parseInt(LM.getElementAt(u).toString())].Fitness;
                                        id = u;
                                    } else if (vanetsim.scenario.Vehicle.TV[Integer.parseInt(LM.getElementAt(u).toString())].Fitness == fit) {
                                        if (vanetsim.scenario.Vehicle.TV[Integer.parseInt(LM.getElementAt(u).toString())].nbvoisin > vanetsim.scenario.Vehicle.TV[id].nbvoisin) {
                                            fit = vanetsim.scenario.Vehicle.TV[Integer.parseInt(LM.getElementAt(u).toString())].Fitness;
                                            id = u;
                                        }

                                    }
                                    //System.out.println("-------------------- AffectLastStat -------------------- 6");
                                    //-----------------------------------------------------------
                                    //id devient CH ceux connectés avec MN, les autres SM 
                                    //System.out.println("avant id ====================== "+id+"/"+LM.getSize());
                                    //-----------------------------------------------------------
                                    int ident = Integer.parseInt(LM.getElementAt(id).toString().trim());
                                    //System.out.println("aprés id ====================== "+ident);
                                    //----------------------------------------------
                                    for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                                        if (adhoc.GlobalParameters.ListConfirmed[p][0] == ident) {
                                            pos1 = p;
                                        }
                                    }
                                    //----------------------------------------------
                                    adhoc.GlobalParameters.ListConfirmed[pos1][2] = 0; //Le noeud Id devient CH
                                    adhoc.GlobalParameters.ListConfirmed[pos1][1] = ident;//adhoc.GlobalParameters.ListConfirmed[id][0];

                                    for (int k = 0; k < tmp.size(); k++) {
                                        if (ident == Integer.parseInt(tmp.elementAt(k).toString())) {
                                            for (int l = 0; l < MI.length; l++) {
                                                //------------------------------
                                                for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == Integer.parseInt(tmp.elementAt(l).toString())) {
                                                        pos1 = p;
                                                    }
                                                    if (adhoc.GlobalParameters.ListConfirmed[p][0] == dde) {
                                                        pos2 = p;
                                                    }
                                                }
                                                //------------------------------
                                                if (MI[k][l] == 1) {
                                                    adhoc.GlobalParameters.ListConfirmed[pos1][2] = 2; //Le noeud l devient MN
                                                    adhoc.GlobalParameters.ListConfirmed[pos1][1] = ident;
                                                } else {
                                                    adhoc.GlobalParameters.ListConfirmed[pos1][2] = 3; //Le noeud l devient SM
                                                    adhoc.GlobalParameters.ListConfirmed[pos1][1] = adhoc.GlobalParameters.ListConfirmed[pos2][1];
                                                }
                                                //---------màj 0--> 1------------
                                                for (int i = 0; i < tst.length; i++) {

                                                    if (tst[i][0] == ident) { //Attribution d'état faite pour id
                                                        tst[i][1] = 1;
                                                    }

                                                    if (tst[i][0] == Integer.parseInt(tmp.elementAt(l).toString())) {
                                                        tst[i][1] = 1;
                                                    }
                                                }
                                            }
                                            //System.out.println("-------------------- AffectLastStat -------------------- 7");
                                            //---------------------
                                        }
                                    }

                                }
                                //----------------------------------------------

                            }
                        }
                        //----------------------------------------

                    }

                }
            }
            //----------------------pour le test d'arrêt------------------------------
            arret = true;
            //System.out.println("-------------------- FIN AffectLastStat -------------------- ");
            //-----------------------------------------------------   
        }

        //-----------------------------------------------------------------------------
    }

    //==============================================================================
    public static boolean EstVoisin(int nd, int Lvi[]) {
        boolean b = false;
        for (int i = 0; i < Lvi.length; i++) {
            if (Lvi[i] == nd) {
                b = true;
            }
        }
        return b;
    }
//==============================================================================

    public static void TriCouverture(double T[][], int col) {
        
        //TriCouverture(vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv, 1); param d'appel
        double tmp[] = new double[T[0].length];
        double vi, vj;
        for (int m = 0; m < T.length; m++) {
            for (int i = 0; i < T.length - 1; i++) {
                vi = T[i][col];
                for (int j = i + 1; j < T.length; j++) {
                    vj = T[j][col];
                    if (vj < vi) {
                        for (int k = 0; k < tmp.length; k++) {
                            tmp[k] = T[i][k];
                        }
                        for (int k = 0; k < tmp.length; k++) {
                            T[i][k] = T[j][k];
                        }
                        for (int k = 0; k < tmp.length; k++) {
                            T[j][k] = tmp[k];
                        }
                    }
                }
            }
        }
    }
//==============================================================================

    public static void ChElectionFClustering(DefaultTableModel Tnd, int vact[]) {

        System.out.println(" ChElectionFClustering pour "+vact.length+" véhicules");
        adhoc.GlobalParameters.tailleListConfirmed = vact.length;
        int nbVi; //Nbr de voisin du nd i
        int Lvi[]; //liste des voisins pour le Nd i
        int nbSame = 0, nbDiff = 0;
        int ListSame[] = null, ListDiff[][] = null;
        int ListRefus[][] = new int[vact.length][3], nbrefus = 0;
        String ch_;
        nbrClFc = 0;
        adhoc.GlobalParameters.ListConfirmed = new int[vact.length][3];

//--------------------------------Initialisation des tableaux-------------------
        for (int i = 0; i < vact.length; i++) {

            adhoc.GlobalParameters.ListConfirmed[i][0] = vact[i];
            adhoc.GlobalParameters.ListConfirmed[i][2] = -1; //initialisation

            ListRefus[i][0] = vact[i];
            ListRefus[i][1] = 0;//0:orphelin, 1:rôle décidé
            ListRefus[i][2] = 0;//compteur Lcouv

//--------------- Découverte de voisins et remplissage de Lcouv-----------------		   
            nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisinFormation(i, vact.length);
            vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin = nbVi; //

            if (nbVi > 0) {

                Tnd.setValueAt("" + nbVi, i, 2);
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisinFormation(i, vact, nbVi);
                ch_ = "";
                for (int v = 0; v < nbVi; v++) {
                    ch_ = ch_ + Lvi[v] + ",";
                }
                //System.out.println("Les voisin du noeud : "+vact[i]+" sont : "+ch_);
                //vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin = nbVi+1;

                vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv = new double[nbVi + 1][2];
                vanetsim.scenario.Vehicle.TV[vact[i]].IndCouv = 0;
                for (int k = 0; k < nbVi; k++) {
                    vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[k][0] = Lvi[k];
                    vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[k][1] = vanetsim.scenario.Vehicle.TV[Lvi[k]].Fitness;
                }
                vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[nbVi][0] = vact[i];
                vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[nbVi][1] = vanetsim.scenario.Vehicle.TV[vact[i]].Fitness;

                TriCouverture(vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv, 1);

                //----------Affectaion des rôles pour les Self-CH en fct de Fit-----------------
                if ((int) vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[vanetsim.scenario.Vehicle.TV[vact[i]].IndCouv][0] == vact[i]) {
                    adhoc.GlobalParameters.ListConfirmed[i][2] = 0;//c'est un Ch
                    adhoc.GlobalParameters.ListConfirmed[i][1] = vact[i];//c'est un Ch
                    ListRefus[i][1] = 1;  // i a son rôle
                    nbSame++;
                } else {
                    nbDiff++;
                }
            } else // NbVi= 0
            {
                adhoc.GlobalParameters.ListConfirmed[i][2] = 0;//c'est un Ch
                adhoc.GlobalParameters.ListConfirmed[i][1] = vact[i];//c'est un Ch
                ListRefus[i][1] = 1;  // i a son rôle
                nbSame++;
            }
        }

//---------------------Remplissage de ListSame et ListDiff---------------------- 
//System.out.println("BBBB");
        if (nbSame > 0) {
            ListSame = new int[nbSame];
        }

        if (nbDiff > 0) {
            ListDiff = new int[nbDiff][3];
            //---------------------------------------------------
            nbSame = 0;
            nbDiff = 0;
            //----------------------------------------------------
            for (int i = 0; i < vact.length; i++) {
                if (vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin > 0) {
                    // System.out.println("i : "+i+"    Taille de vact : "+vact.length);
                    // System.out.println("contenu : "+vact[i]);
                    int w = 0;

                    if ((int) vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[vanetsim.scenario.Vehicle.TV[vact[i]].IndCouv][0] == vact[i] || ((int) vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin == 0)) {
                        ListSame[nbSame] = vact[i];
                        ListRefus[i][1] = 1; // i a son rôle
                        //System.out.println("Le noeud : "+(i+1)+" demande "+ListSame[nbSame]);
                        nbSame++;
                    } else {
                        ListDiff[nbDiff][0] = vact[i];
                        ListDiff[nbDiff][1] = (int) vanetsim.scenario.Vehicle.TV[vact[i]].Lcouv[vanetsim.scenario.Vehicle.TV[vact[i]].IndCouv][0];
                        //System.out.println("Le noeud : "+(i+1)+" demande "+(ListDiff[nbDiff][1]+1));
                        ListDiff[nbDiff][2] = 0;//i n'a pas encore son role
                        nbDiff++;
                    }

                }
            }

        }
//----------------------Début de traitement de ListDiff------------------------- 

        boolean b;
        int ListTete[][] = new int[vact.length][2], nbt = 0;
        nbrefus = 0;

        if (ListDiff != null) {
            for (int i = 0; i < ListDiff.length; i++) {
                if (ListDiff[i][2] == 0) {
                    int nd = ListDiff[i][0], ChVal = ListDiff[i][1], pos = 0, pos1 = 0;

                    //------------Localiser la position du nd dans  ListConfirmed--------------
                    for (int p = 0; p < adhoc.GlobalParameters.ListConfirmed.length; p++) {
                        if (adhoc.GlobalParameters.ListConfirmed[p][0] == nd) {
                            pos = p;
                        }
                    }

                    //------------Localiser la position du nd dans  ListRefus------------------
                    for (int p = 0; p < ListRefus.length; p++) {
                        if (ListRefus[p][0] == nd) {
                            pos1 = p;
                        }
                    }

                    //------------ 1er cas le noeud demandé existe dans ListSame-------------------
                    b = false;
                    if (ListSame != null) {
                        for (int j = 0; j < ListSame.length; j++) {
                            if (ListSame[j] == ChVal)//Le Ch demandé est dans ListSame
                            {
                                adhoc.GlobalParameters.ListConfirmed[pos][1] = ChVal;
                                adhoc.GlobalParameters.ListConfirmed[pos][2] = 2;//c'est un noeud fils
                                ListRefus[pos1][1] = 1;
                                b = true; //son CH existe dans ListeSame
                                ListDiff[i][2] = 1; //màj etat de nd i pour signaler que i est déjà traité
                                System.out.println("Le noeud  "+adhoc.GlobalParameters.ListConfirmed[pos][0]+
                                        " demande "+adhoc.GlobalParameters.ListConfirmed[pos][1]);
                            }
                        }
                    }
                    //------------ 2ème cas le noeud demandé n'existe pas dans ListSame-------------
                    if (b == false) {
                        //System.out.println("le noeud demandé n'existe pas dans ListSame");
                        //------- Remplissage de ListTete-------
                        for (int k = 0; k < nbt; k++) {
                            if (ListTete[k][0] == ChVal) {
                                b = true;
                                ListTete[k][1]++;
                            }
                        }

                        if (b == false) {
                            ListTete[nbt][0] = ChVal;
                            ListTete[nbt][1] = 1;
                            nbt++;
                        }
                    }

                }
            }
        }
// }
//System.out.println("DDDD");
        //-----------------Confirmation 2-------------------------------

        double Element[][];  //liste des elements de ListTete, les éléments qui ont demandé le même élément pour être leurs CH 
        double ElementC[][]; //liste des elements qui sont en connexion avec l'élément demandé et son CH (Éléments Communs)

        int elt, idDemande, ChDemande = 0, nbC, LcouvCmpt = 0;
        double Dfit, DfitC;
//System.out.println("-----------------------debut de confirmation 2 -------------------------------");

        DefaultTableModel TICH = new DefaultTableModel(); //TO HANDLE
        TICH.addColumn("Demandeur");
        TICH.addColumn("Demandé");
        TICH.setRowCount(1);

        for (int i = 0; i < nbt; i++) {

            Element = new double[ListTete[i][1]][3];
            elt = 0;
            Dfit = 0.0;
            System.out.println("Calcul de DFit pour les demandeurs: taille lisListTete[i][1] "+ListTete[i][1]);
            for (int j = 0; j < ListDiff.length; j++) //Remplissage de Element 
            {
                if ((ListDiff[j][1] == ListTete[i][0]) && (ListDiff[j][2] == 0)&&(elt<ListTete[i][1])) {
                    System.out.println("elt = "+elt);
                    Element[elt][0] = ListDiff[j][0];
                    Element[elt][1] = vanetsim.scenario.Vehicle.TV[ListDiff[j][0]].Fitness;
                    Element[elt][2] = Math.abs(vanetsim.scenario.Vehicle.TV[ListTete[i][0]].Fitness - vanetsim.scenario.Vehicle.TV[ListDiff[j][0]].Fitness);
                    Dfit = (double) Dfit + (double) Element[elt][2];
                    elt++;
                }

            }

            Dfit = (double) Dfit / (double) elt; // Moyenne de différence des fitness avec les éléments demandeurs

            //------------------Recherche du Ch du nd demandé---------------------------------
            idDemande = ListTete[i][0]; //sauvegarde du noeud demandé
            //---------------------------------------------

            //---------------------------------------------
            for (int j = 0; j < ListDiff.length; j++) {
                if (ListDiff[j][0] == idDemande) {
                    ChDemande = ListDiff[j][1];//sauvegarde de CH du noeud demandé
                }
            }

            b = false;
            //------------------Recherche du ChDemande dans ListSame---------------
            nbC = 0;
            if (ListSame != null) {
                for (int j = 0; j < ListSame.length; j++) {
                    if (ListSame[j] == ChDemande) {
                        b = true; //ChDemande  appartient à ListSame---------------
                    }
                }
            }

//-----------------------------------traitement du cas où le CH est trouvé dans ListeSame---------------------------
            if (b == true) {
                //System.out.println("CH demandé trouvé dans ListeSame");
                //    System.out.println("Taille ListTete : "+ListTete.length+"  taille vact : "+vact.length);
                //    System.out.println("Position l'element : "+ListTete[i][0]+" dans vact est : " + Simulation.Vehicule.GetPositionElement(ListTete[i][0], vact));
                int nbv = vanetsim.scenario.Vehicle.Vehicule.NbrVoisinFormation(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListTete[i][0], vact), vact.length);
                vanetsim.scenario.Vehicle.TV[idDemande].nbvoisin = nbv; //

                //----------------------Remplissage de la liste ElementC--------------------
                for (int j = 0; j < ListDiff.length; j++) {
                    if ((ListDiff[j][1] == ChDemande) && (MembreCouverture(ListDiff[j][0], idDemande) == true)) {
                        nbC++;
                    }
                }

                ElementC = new double[nbC][3];
                nbC = 0;
                DfitC = Math.abs(vanetsim.scenario.Vehicle.TV[ChDemande].Fitness - vanetsim.scenario.Vehicle.TV[idDemande].Fitness);
                nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisinFormation(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListTete[i][0], vact), vact.length);
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisinFormation(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListTete[i][0], vact), vact, nbVi);
                //------------fin du problème--------------
                //System.out.println("Calcul de DFit pour les éléments en commun");
                for (int j = 0; j < ListDiff.length; j++) {

                    if ((ListDiff[j][1] == ChDemande) && (EstVoisin(ListDiff[j][0], Lvi) == true)) {
                        ElementC[nbC][0] = ListDiff[j][0];
                        ElementC[nbC][1] = vanetsim.scenario.Vehicle.TV[ListDiff[j][0]].Fitness;
                        ElementC[nbC][2] = Math.abs(vanetsim.scenario.Vehicle.TV[idDemande].Fitness - vanetsim.scenario.Vehicle.TV[ListDiff[j][0]].Fitness);
                        DfitC = (double) DfitC + (double) ElementC[nbC][2];
                        nbC++;
                    }
                }

                if (nbC != 0) {
                    DfitC = (double) DfitC / (double) nbC;
                }

                int indice_;
                // ------------1er cas: DfitC >= Dfit, affectations des rôles aux éléments de la liste Element------------------------------
                if (DfitC >= Dfit) // IdDemande accepte d'être le CH des noeuds d'Element
                {
                    //System.out.println("Le noeud demandé accepte d'être le CH des noeuds demandeurs"); 
                    //debut mise à jour
                    for (int j = 0; j < elt; j++) {
                        indice_ = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement((int) Element[j][0], vact);
                        adhoc.GlobalParameters.ListConfirmed[indice_][1] = idDemande;
                        adhoc.GlobalParameters.ListConfirmed[indice_][2] = 2;//c'est un noeud fils de IdDemande
                        ListRefus[indice_][1] = 1; //
                    }
                    indice_ = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(idDemande, vact);
                    System.out.println("indice_ == "+indice_+" / idemande"+idDemande);
                    adhoc.GlobalParameters.ListConfirmed[indice_][1] = idDemande;
                    adhoc.GlobalParameters.ListConfirmed[indice_][2] = 0;//c'est un Ch
                    ListRefus[indice_][1] = 1;

                    for (int j = 0; j < elt; j++) {
                        for (int k = 0; k < ListDiff.length; k++) {
                            if (ListDiff[k][0] == (int) Element[j][0]) {
                                ListDiff[k][1] = idDemande;
                                ListDiff[k][2] = 1;
                                ListRefus[k][1] = 1;
                            }
                        }
                    }

                } // ------------2ème cas: DfitC < Dfit, les éléments de la liste Element doivent se regrouper------------------------------
                else {
                    int indelt = -1;
                    double minFit = Math.pow(10, 5);

                    //-----------------------------------//
                    for (int e = 0; e < Element.length; e++) {
                        indice_ = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement((int) Element[e][0], vact);
                        ListRefus[indice_][2]++;

                        if (Element[e][1] < minFit) {
                            minFit = Element[e][1];
                            indelt = (int) Element[e][0];
                        }
                    }
                    //-----------------------------------//
                    int nbCouv = 0;
                    for (int e = 0; e < Element.length; e++) {
                        if ((int) Element[e][0] != indelt) {
                            if (MembreCouverture((int) Element[e][0], indelt) == true) {
                                nbCouv++;
                            }
                        }
                    }
                    //----------------------------------//
                    if (nbCouv > 0) {
                        for (int e = 0; e < Element.length; e++) {
                            if ((int) Element[e][0] != indelt) {
                                if (MembreCouverture((int) Element[e][0], indelt) == true) {
                                    for (int k = 0; k < adhoc.GlobalParameters.ListConfirmed.length; k++) {
                                        if (adhoc.GlobalParameters.ListConfirmed[k][0] == (int) Element[e][0]) {
                                            adhoc.GlobalParameters.ListConfirmed[k][1] = indelt;
                                            adhoc.GlobalParameters.ListConfirmed[k][2] = 2; //noeud fils
                                           
                                            for (int m = 0; m < ListDiff.length; m++) {
                                        if (ListDiff[m][0] == indelt) {
                                            ListDiff[m][1] = indelt;
                                            ListDiff[m][2] = 1;
                                            ListRefus[m][1] = 2;
                                        }
                                            }
                                        }
                                    }
                                } else {
                                    for (int k = 0; k < adhoc.GlobalParameters.ListConfirmed.length; k++) {
                                        if (adhoc.GlobalParameters.ListConfirmed[k][0] == (int) Element[e][0]) {
                                            adhoc.GlobalParameters.ListConfirmed[k][1] = ChDemande;
                                            adhoc.GlobalParameters.ListConfirmed[k][2] = 3;
                                            ListRefus[k][1] = 2;
                                            
                                       for (int m = 0; m < ListDiff.length; m++) {
                                        if (ListDiff[m][0] == ChDemande) {
                                            ListDiff[m][1] = ChDemande;
                                            ListDiff[m][2] = 3;
                                            
                                        }
                                       }
                                        }
                                    }
                                }
                            } else {
                                for (int k = 0; k < adhoc.GlobalParameters.ListConfirmed.length; k++) {
                                    if (adhoc.GlobalParameters.ListConfirmed[k][0] == (int) Element[e][0]) {
                                        adhoc.GlobalParameters.ListConfirmed[k][1] = indelt;
                                        adhoc.GlobalParameters.ListConfirmed[k][2] = 0; //indelt est un CH
                                        ListRefus[k][1] = 2;
                                        
                                        for (int m = 0; m < ListDiff.length; m++) {
                                        if (ListDiff[m][0] == indelt) {
                                        ListDiff[m][1] = indelt;
                                        ListDiff[m][2] = 0;
                                        
                                        }
                                        }
                                    }
                                }
                            }
                        }
                    } else//==0
                    {
                        for (int e = 0; e < Element.length; e++) {
                            for (int k = 0; k < adhoc.GlobalParameters.ListConfirmed.length; k++) {
                                if (adhoc.GlobalParameters.ListConfirmed[k][0] == (int) Element[e][0]) {
                                    adhoc.GlobalParameters.ListConfirmed[k][1] = ChDemande;
                                    adhoc.GlobalParameters.ListConfirmed[k][2] = 3;
                                    ListRefus[k][1] = 2;
                                    
                                    for (int m = 0; m < ListDiff.length; m++) {
                                        if (ListDiff[m][0] == ChDemande) {
                                    ListDiff[m][1] = ChDemande;
                                    ListDiff[m][2] = 3;
                                    
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //----------------------------------//
                    //------------------------
                }
                //-----------------
            } //-----------------------------------Traitement du cas où le CH demande n'appartient pas à ListeSame---------------------------
            
            else //b == false
            {
                
                //======================================================
                for (int u = 0; u < Element.length; u++) {
                    TICH.setValueAt("" + (int) Element[u][0], TICH.getRowCount() - 1, 0);
                    TICH.setValueAt("" + idDemande, TICH.getRowCount() - 1, 1);

                    TICH.setRowCount(TICH.getRowCount() + 1);
                    
                    //System.out.println(" Element "+(int) Element[u][0]+" n'a pas de CH");
                    
                    for (int k = 0; k < adhoc.GlobalParameters.ListConfirmed.length; k++) {
                                if (adhoc.GlobalParameters.ListConfirmed[k][0] == (int) Element[u][0]) {
                                    adhoc.GlobalParameters.ListConfirmed[k][2] = 1;//MN
                                    adhoc.GlobalParameters.ListConfirmed[k][1] = idDemande;//MN
                                    //ListRefus[(int) Element[u][0]][1] = 2;
                                    ListRefus[k][1] = 2;

                                    for (int m = 0; m < ListDiff.length; m++) {
                                        if (ListDiff[m][0] == idDemande) {
                                            ListDiff[m][1] = idDemande;
                                            ListDiff[m][2] = 1;
                                            
                                        }
                                    }
                                    
                                    
                                    
                                }
                                adhoc.GlobalParameters.ListConfirmed[idDemande][1] = idDemande;
                                adhoc.GlobalParameters.ListConfirmed[idDemande][2] = 0;//CH
                                ListRefus[idDemande][1] = 2;
                            for (int m = 0; m < ListDiff.length; m++) {
                            if (ListDiff[m][0] == idDemande) {
                                ListDiff[m][1] = idDemande;
                                ListDiff[m][2] = 1;
                                
                            }
                            }
                                
                                
                }
                }
                
                                          
//======================================================

                /*

                 int compt = 0;
                 int *ListAttConf = new int[nbn];
                 ListAttConf[0]   = idDemande;
                 compt++;


                 //	ShowMessage("Debut recusivite pour le noeud : "+IntToStr(idDemande));
                 PccCH(idDemande,ListConfirmed,nbn, ListAttConf,compt);
                 //	ShowMessage("Fin recursivite : compt = "+IntToStr(compt));
                 adhoc.GlobalParameters.ListConfirmed[ListAttConf[compt-2]][1] = adhoc.GlobalParameters.ListConfirmed[ListAttConf[compt-1]][1] ;
                 adhoc.GlobalParameters.ListConfirmed[ListAttConf[compt-2]][1] = 2;
                 */
                //================================================
            }

            //---------------------------------------------------
        }//Fin for pour les i

//        for (int t = 0; t < TICH.getRowCount() - 1; t++) {
//           // System.out.println((t + 1) + " Demandeur : " + TICH.getValueAt(t, 0) + " (fitness demandeur) ===" + vanetsim.scenario.Vehicle.TV[Integer.parseInt((String) TICH.getValueAt(t, 0))].Fitness + " ==> Demandé : " + TICH.getValueAt(t, 1) + " (fitness demandé) ===" + vanetsim.scenario.Vehicle.TV[Integer.parseInt((String) TICH.getValueAt(t, 1))].Fitness);
//        }
        //System.out.println("*******------- Noeud non traités : " + (TICH.getRowCount() - 1));
        //-------------------------------------------------
        AffectLastStat(TICH, vact.length); //TO FIX AffectLastStat
        //-------------------------------------------------
        nbrefus = 0;
        for (int u = 0; u < ListRefus.length; u++) {
            if (ListRefus[u][1] == 0) {
                nbrefus++;
            }
        }

        //-------------------------------------------------
        if (nbrefus > 0) {

            int pos, nlc;

            for (int n = 0; n < ListRefus.length; n++) {
                if (ListRefus[n][1] == 0) {

                    //---------------------------------------------------
                    boolean fin = false;
                    pos = 0;
                    while ((pos < vanetsim.scenario.Vehicle.TV[ListRefus[n][0]].nbvoisin) && (fin == false)) {
                        //System.out.println("n : "+n+"/"+ListRefus.length+" contenu : "+ListRefus[n][0]);
                        if (vanetsim.scenario.Vehicle.TV[ListRefus[n][0]].Lcouv != null) {
                            nlc = (int) vanetsim.scenario.Vehicle.TV[ListRefus[n][0]].Lcouv[pos][0];
                            if (nlc == ListRefus[n][0]) //nier son orphelinat
                            {
                                //-------------------
                                int ind_ = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact);
                                adhoc.GlobalParameters.ListConfirmed[ind_][1] = ListRefus[n][0];
                                adhoc.GlobalParameters.ListConfirmed[ind_][2] = 0;//c'est un Ch
                                ListRefus[ind_][1] = 1;
                                //debut mise à jour
                                for (int k = 0; k < nbDiff; k++) {
                                    if (ListDiff[k][0] == ListRefus[n][0]) {
                                        ListDiff[k][1] = ListRefus[n][0];
                                        ListDiff[k][2] = 1;
                                    }
                                }
                                //-------------------
                                fin = true;
                            } else {
                                // System.out.println("nlc = "+nlc+"   Taille ListRefus : "+ListRefus.length+"   taille vact : "+vact.length);
                                int ind_ = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(nlc/*ListRefus[nlc][0]*/, vact);

                                if (ind_ > -1) {
                                    if (ListRefus[ind_][1] == 0)//suivant c'est un orphelin
                                    {
                                        pos++;
                                    } else {
                                        adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact)][1] = adhoc.GlobalParameters.ListConfirmed[ind_][1];
                                        adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact)][2] = 3;//membre inderect
                                        ListRefus[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact)][1] = 1;
                                        //debut mise à jour
                                        for (int k = 0; k < nbDiff; k++) {
                                            if (ListDiff[k][0] == vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact)) {
                                                ListDiff[k][1] = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(ListRefus[n][0], vact);
                                                ListDiff[k][2] = 1;
                                            }
                                        }
                                    }
                                }

                                fin = true;
                            }
                        } else {
                            pos++;
                        }
                    }
                    //---------------------------------------------------
						   /*


                     pos = ListRefus[n][2];

                     Rap->Lines->Add("Noeud"+IntToStr(ListRefus[n][0]+1)+"   ma pos : "+IntToStr(pos));
                     if((int)vanetsim.scenario.Vehicle.TV[n].Lcouv[pos][0]==n)
                     {
                     //------------------------
                     adhoc.GlobalParameters.ListConfirmed[n][1] =  n;
                     adhoc.GlobalParameters.ListConfirmed[n][2] =  0;//c'est un Ch
                     ListRefus[n][1]     =   1;
                     //debut mise à jour
                     for(int k=0;k<nbDiff;k++)
                     {
                     if(ListDiff[k][0]==n)
                     {
                     ListDiff[k][1] = n;
                     ListDiff[k][2] = 1;
                     }
                     }
                     //------------------------
                     nbrefus--;
                     } else
                     {
                     ListRefus[n][2]++;
                     }

                     */

                }
            }

        }
//System.out.println("EEEE"); 

        //-------------------------Debut traitement de la liste des refus-----
        //------------------------------------------------------------------
        for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
            //---------------------
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {

                //-----------------------------------------------------------
                // Rap->Lines->Add("1111");
                int nbv = vanetsim.scenario.Vehicle.Vehicule.NbrVoisinFormation(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(adhoc.GlobalParameters.ListConfirmed[i][0], vact), vact.length), l = 0;// nbVi = ;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].nbvoisin = nbv;
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisinFormation(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(adhoc.GlobalParameters.ListConfirmed[i][0], vact), vact, nbv);

                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv = new double[nbv + 1][2];

                for (int k = 0; k < vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv.length - 1; k++) {
                    vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[k][0] = Lvi[k];
                    vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[k][1] = vanetsim.scenario.Vehicle.TV[Lvi[k]].Fitness;
                }
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[nbv][0] = i;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[nbv][1] = vanetsim.scenario.Vehicle.TV[i].Fitness;
                TriCouverture(vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv, 1);
                System.out.println(" LCouv trié de "+i+" : ");
                for (int s = 0; s <vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv.length;s++)
                {
                    System.out.println("Pos "+s+" "+(int)vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[s][0]
                            +" Fitness "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[s][1]);
                }

//	 Rap->Lines->Add("3333");
                if (nbv > 1) {
                    while (l < nbv)//********* une fois
                    {
                        if ((int) vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l][0] == adhoc.GlobalParameters.ListConfirmed[i][0]) {
                            //-------------------

                            if (l < nbv) {
                                //

                                if (adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElementTableau((int) vanetsim.scenario.Vehicle.TV[i].Lcouv[l + 1][0], adhoc.GlobalParameters.ListConfirmed, 0)][2] == 2) {
                                    adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElementTableau((int) vanetsim.scenario.Vehicle.TV[i].Lcouv[l + 1][0], adhoc.GlobalParameters.ListConfirmed, 0)][2] = 1;
                                }

                            }

                            //-------------------
                        }

                        l++;
                    }
                } else {
                    adhoc.GlobalParameters.ListConfirmed[i][2] = 0;
                }
                
            }
            //---------------------
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                nbrClFc++;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Ch";
                Tnd.setValueAt("Ch", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
                CalculStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], adhoc.Adhoc.compteur/100);
                        System.out.println("Le noeud n°: "+(i+1)+" est "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC
                    +" MyCh est "+adhoc.GlobalParameters.ListConfirmed[i][1] );
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 1) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Sch";
                Tnd.setValueAt("Sch", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
                //CalculStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], vanetsim.scenario.Vehicle.compteur/100);
                        System.out.println("Le noeud n°: "+(i+1)+" est "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC
                    +" MyCh est "+adhoc.GlobalParameters.ListConfirmed[i][1] );
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 2) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Mn";
                Tnd.setValueAt("Mn", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
                //CalculStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], vanetsim.scenario.Vehicle.compteur/100);
                        System.out.println("Le noeud n°: "+(i+1)+" est "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC
                    +" MyCh est "+adhoc.GlobalParameters.ListConfirmed[i][1] );
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 3) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Sm";
                Tnd.setValueAt("Sm", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
                //CalculStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], vanetsim.scenario.Vehicle.compteur/100);
                        System.out.println("Le noeud n°: "+(i+1)+" est "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC
                    +" MyCh est "+adhoc.GlobalParameters.ListConfirmed[i][1] );
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == -1) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Init";
                Tnd.setValueAt("Init", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = true;
            System.out.println("Le noeud n°: "+(i+1)+" est "+vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC
                    +" MyCh est "+adhoc.GlobalParameters.ListConfirmed[i][1] );
            }

            Tnd.setValueAt("" + (adhoc.GlobalParameters.ListConfirmed[i][1] + 1), i, 4);
            //System.out.println("Instant : ===================               " + (vanetsim.scenario.Vehicle.compteur / 100));
            CalculStateDuration(adhoc.GlobalParameters.ListConfirmed[i][0], adhoc.Adhoc.compteur / 100);
        }
        //System.out.println("FFFF");
        CalculateStateDuration_FC();
      //--------------------------

       //-------------------------
        
        
    }

    //=============================================================================
    public static void CloseStateDuration(int idveh, int compt) 
    {
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount(); i++) 
	{
                if ((!vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(i, 0).equals("-1")) && 
                    (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(i, 1).equals("-1"))) 
                     vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, i, 1);
											 
	} 
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount(); i++) 
	{
                if ((!vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(i, 0).equals("-1")) && 
                    (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(i, 1).equals("-1"))) 
                     vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, i, 1);
											 
	}        
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount(); i++) 
	{
                if ((!vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(i, 0).equals("-1")) && 
                    (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(i, 1).equals("-1"))) 
                     vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, i, 1);
											 
	}        
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount(); i++) 
	{
                if ((!vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(i, 0).equals("-1")) && 
                    (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(i, 1).equals("-1"))) 
                     vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, i, 1);
											 
	}        
        
    }
    //==============================================================================
    public static void CalculStateDuration(int idveh, int compt) {
        boolean trv = false;
        int flag;
        //==========================CH========================================
        if (vanetsim.scenario.Vehicle.TV[idveh].est_ch_FC == true) 
		{

            if (adhoc.Adhoc.TBFM[0][2] == false)//premiére formation
            {
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(i, 0).equals("-1")) 
					{
                    //System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
                    vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, i, 0);
					}
				}
            }
            else //plusieur formation
            {
			     trv =false;
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(i, 0).equals("-1")) 
					{
						//System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
						vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, i, 0);
						trv=true;
						flag = 0;
					 //---------------------------------------------------------------------------
					                     int j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 1);
										}
					 if(flag==0)
					 {
					                    j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 1);
										}
										if(flag==0)
										{
										 j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 1);
										}
										}
					 }
					 //---------------------------------------------------------------------------
					
					}
				}

				
				
				
				 
                    //----------------------------------------------------
				
            }
            
        }    
//-----------------------------------------------------------------------------------------------------------

       
	   
         //==========================SCH========================================
         if(vanetsim.scenario.Vehicle.TV[idveh].est_sch_FC==true)
         {
            if (adhoc.Adhoc.TBFM[0][2] == false)//premiére formation
            {
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(i, 0).equals("-1")) 
					{
                    //System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
                    vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, i, 0);
					}
				}
            }
            else //plusieur formation
            {
			     trv =false;
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(i, 0).equals("-1")) 
					{
						//System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
						vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, i, 0);
						trv=true;
						flag = 0;
					 //---------------------------------------------------------------------------
					                     int j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 1);
										}
					 if(flag==0)
					 {
					                    j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 1);
										}
										if(flag==0)
										{
										 j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 1);
										}
										}
					 }
					 //---------------------------------------------------------------------------
					
					}
				}

				
				
				
				 
                    //----------------------------------------------------
				
            }
         }
          
         //==========================MN========================================
         if(vanetsim.scenario.Vehicle.TV[idveh].est_mn_FC==true)
         {
            if (adhoc.Adhoc.TBFM[0][2] == false)//premiére formation
            {
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(i, 0).equals("-1")) 
					{
                    //System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
                    vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, i, 0);
					}
				}
            }
            else //plusieur formation
            {
			     trv =false;
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(i, 0).equals("-1")) 
					{
						//System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
						vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, i, 0);
						trv=true;
						flag = 0;
					 //---------------------------------------------------------------------------
					                     int j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 1);
										}
					 if(flag==0)
					 {
					                    j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 1);
										}
										if(flag==0)
										{
										 j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount() - 1, 1);
										}
										}
					 }
					 //---------------------------------------------------------------------------
					
					}
				}

				
				
				
				 
                    //----------------------------------------------------
				
            }
         }
         //    //==========================SM========================================
         if(vanetsim.scenario.Vehicle.TV[idveh].est_sm_FC==true)
         {
            if (adhoc.Adhoc.TBFM[0][2] == false)//premiére formation
            {
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(i, 0).equals("-1")) 
					{
                    //System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
                    vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, i, 0);
					}
				}
            }
            else //plusieur formation
            {
			     trv =false;
                for (int i = 0; i < vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getRowCount(); i++) 
				{
                    if (vanetsim.scenario.Vehicle.TV[idveh].SMDuration.getValueAt(i, 0).equals("-1")) 
					{
						//System.err.println("Passe 1 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
						vanetsim.scenario.Vehicle.TV[idveh].SMDuration.setValueAt("" + compt, i, 0);
						trv=true;
						flag = 0;
					 //---------------------------------------------------------------------------
					                     int j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].SCHDuration.getRowCount() - 1, 1);
										}
					 if(flag==0)
					 {
					                    j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].MNDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].MNDuration.getRowCount() - 1, 1);
										}
										if(flag==0)
										{
										 j=0;
					                     while ((j < vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount()) && (flag==0)) 
										{
											if ((!vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 0).equals("-1")) && (vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getValueAt(j, 1).equals("-1"))) 
											{
												//System.err.println("Passe 2 pour le véhicule : "+vanetsim.scenario.Vehicle.TV[idveh].idVeh);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("" + compt, j, 1);
												flag = 1;
											}
											j++;
										}
										if(flag==1)
										{
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setRowCount(vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() + 1);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 0);
												vanetsim.scenario.Vehicle.TV[idveh].CHDuration.setValueAt("-1", vanetsim.scenario.Vehicle.TV[idveh].CHDuration.getRowCount() - 1, 1);
										}
										}
					 }
					 //---------------------------------------------------------------------------
					
					}
				}

				
				
				
				 
                    //----------------------------------------------------
				
            }
         }
        
		
    }

    //=============================================================================

    public static void ChMaintenanceFClustering(DefaultTableModel Tnd, int vact[]) {

        int nbVi = 0; //Nbr de voisin du nd i
        int Lvi[]; //liste des voisins pour le Nd i

        int nbCh = 0, nbSch = 0, nbMn = 0, nbSm = 0;
        int LCH[], LSCH[][], LMN[][], LSM[][];

        boolean exist;
        int nbsup = 0;
        int svact[] = new int[vact.length];

        for (int i = 0; i < vact.length; i++) //pour la maintenance
        {
            exist = false;
            for (int j = 0; j < adhoc.GlobalParameters.tailleListConfirmed; j++) //pour formation
            {
                if (adhoc.GlobalParameters.ListConfirmed[j][0] == vact[i]) {
                    if (adhoc.GlobalParameters.ListConfirmed[j][2] == 0) {
                        nbCh++;
                    }
                    if (adhoc.GlobalParameters.ListConfirmed[j][2] == 1) {
                        nbSch++;
                    }
                    if (adhoc.GlobalParameters.ListConfirmed[j][2] == 2) {
                        nbMn++;
                    }
                    if (adhoc.GlobalParameters.ListConfirmed[j][2] == 3) {
                        nbSm++;
                    }
                    exist = true;
                }
            }
            if (exist == false) {
                svact[nbsup] = vact[i];
                nbsup++;
            }
        }

        //---------------------------------------------------------------------------
        if (nbsup > 0) {

            //-------------------------------------------------
            int ListConfirmedS[][] = new int[adhoc.GlobalParameters.tailleListConfirmed][3];

            for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed; i++) {
                for (int j = 0; j < 3; j++) {
                    ListConfirmedS[i][j] = adhoc.GlobalParameters.ListConfirmed[i][j];
                }
            }

            adhoc.GlobalParameters.ListConfirmed = new int[adhoc.GlobalParameters.tailleListConfirmed + nbsup][3];

            for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed; i++) {
                for (int j = 0; j < 3; j++) {
                    adhoc.GlobalParameters.ListConfirmed[i][j] = ListConfirmedS[i][j];
                }
            }
            int l = 0;

            for (int i = adhoc.GlobalParameters.tailleListConfirmed; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {

                adhoc.GlobalParameters.ListConfirmed[i][0] = svact[l];

                //--------------------------------------
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(svact[l], vact, nbVi);//nbVi à calculer avant*******
                exist = false;

                for (int k = 0; k < nbVi; k++) {
                    if (vanetsim.scenario.Vehicle.TV[Lvi[k]].est_ch_FC == true) {
                        adhoc.GlobalParameters.ListConfirmed[i][1] = Lvi[k];
                        adhoc.GlobalParameters.ListConfirmed[i][2] = 2;

                        exist = true;
                    }
                }

                if (exist == false) {

                    for (int k = 0; k < nbVi; k++) {
                        if (vanetsim.scenario.Vehicle.TV[Lvi[k]].est_sch_FC == true) {
                            //---------------------
                            for (int u = 0; u < i; u++) {
                                if (adhoc.GlobalParameters.ListConfirmed[u][0] == Lvi[k]) {
                                    adhoc.GlobalParameters.ListConfirmed[i][1] = adhoc.GlobalParameters.ListConfirmed[u][1];
                                    adhoc.GlobalParameters.ListConfirmed[i][2] = 3;
                                }
                            }
                            //---------------------

                            exist = true;
                        }
                    }

                    if (exist == false) {

                        for (int k = 0; k < nbVi; k++) {
                            if (vanetsim.scenario.Vehicle.TV[Lvi[k]].est_mn_FC == true) {
                                //---------------------
                                for (int u = 0; u < i; u++) {
                                    if (adhoc.GlobalParameters.ListConfirmed[u][0] == Lvi[k]) {
                                        adhoc.GlobalParameters.ListConfirmed[i][1] = adhoc.GlobalParameters.ListConfirmed[u][1];
                                        adhoc.GlobalParameters.ListConfirmed[i][2] = 3;
                                    }
                                }
                                //---------------------

                                exist = true;
                            }
                        }

                    }

                    if (exist == false) {

                        adhoc.GlobalParameters.ListConfirmed[i][1] = adhoc.GlobalParameters.ListConfirmed[i][0];
                        adhoc.GlobalParameters.ListConfirmed[i][2] = 0;

                    }

                }
                //--------------------------------------
                l++;
                //-----------------------------------
                if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                    nbCh++;
                }
                if (adhoc.GlobalParameters.ListConfirmed[i][2] == 1) {
                    nbSch++;
                }
                if (adhoc.GlobalParameters.ListConfirmed[i][2] == 2) {
                    nbMn++;
                }
                if (adhoc.GlobalParameters.ListConfirmed[i][2] == 3) {
                    nbSm++;
                }
                //------------------------------------
            }
            //-------------------------------------------------

        }

        //---------------------------------------------------------------------------
        for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {
            exist = false;
            for (int k = 0; k < vact.length; k++) {
                if (vact[k] == adhoc.GlobalParameters.ListConfirmed[i][0]) {
                    exist = true;
                }
            }
            if (exist == false) {
                adhoc.GlobalParameters.ListConfirmed[i][2] = -1;
            }
        }
        //---------------------------------------------------------------------------

        LCH = new int[nbCh];
        LSCH = new int[nbSch][2];
        LMN = new int[nbMn][3];
        LSM = new int[nbSm][4];

        nbCh = 0;
        nbSch = 0;
        nbMn = 0;
        nbSm = 0;

        for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                LCH[nbCh] = vact[i];  //ch
                nbCh++;
            }

            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 1) {
                LSCH[nbSch][0] = adhoc.GlobalParameters.ListConfirmed[i][0];   //sch
                LSCH[nbSch][1] = adhoc.GlobalParameters.ListConfirmed[i][1];   //ch
                nbSch++;
            }
        }

        for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {

            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 2) {
                LMN[nbMn][0] = adhoc.GlobalParameters.ListConfirmed[i][0];    //mn
                LMN[nbMn][2] = adhoc.GlobalParameters.ListConfirmed[i][1];    //ch
                //------------------------
                for (int j = 0; j < nbSch; j++) {
                    if (LSCH[j][1] == LMN[nbMn][2]) {
                        LMN[nbMn][1] = LSCH[j][0]; //sch  **
                    }
                }
                //------------------------
                nbMn++;
            }
        }

        for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 3) {
                LSM[nbSm][0] = adhoc.GlobalParameters.ListConfirmed[i][0];   //sm
                LSM[nbSm][3] = adhoc.GlobalParameters.ListConfirmed[i][1];   //ch

                for (int j = 0; j < nbMn; j++) {
                    if (LMN[j][2] == LSM[nbSm][3]) {
                        LSM[nbSm][1] = LMN[j][0];   //mn
                        LSM[nbSm][2] = LMN[j][1];   //sch
                    }
                }
                nbSm++;
            }
        }

//------------------------- Pour verifier les états des SCH---------------------
        for (int i = 0; i < nbSch; i++) {
            exist = false;
            nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(LSCH[i][0], vact.length);
            if (nbVi > 0) {
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(LSCH[i][0], vact, nbVi);
                for (int j = 0; j < nbVi; j++) {
                    if (Lvi[j] == LSCH[i][1]) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    //----------traitement maintenance
                    int nbvch = 0;
                    for (int j = 0; j < nbVi; j++) {
                        for (int k = 0; k < nbCh; k++) {
                            if (LCH[k] == Lvi[j]) {
                                nbvch++;
                            }
                        }
                    }
                    //--------------------------------
                    if (nbvch > 0) {
                        //-------------------------
                        double tmp[][] = new double[nbvch][2];
                        double FitMin = 10 * Math.E + 6;
                        int idX = 0;

                        nbvch = 0;

                        for (int j = 0; j < nbVi; j++) {
                            for (int k = 0; k < nbCh; k++) {
                                if (LCH[k] == Lvi[j]) {
                                    tmp[nbvch][0] = Lvi[j];
                                    tmp[nbvch][1] = Math.abs(vanetsim.scenario.Vehicle.TV[LSCH[i][0]].Fitness - vanetsim.scenario.Vehicle.TV[Lvi[j]].Fitness);
                                    if (tmp[nbvch][1] < FitMin) {
                                        FitMin = tmp[nbvch][1];
                                        idX = Lvi[j];
                                    }
                                    nbvch++;
                                }
                            }
                        }
                        //------------------------

                        adhoc.GlobalParameters.ListConfirmed[LSCH[i][0]][1] = idX;
                        adhoc.GlobalParameters.ListConfirmed[LSCH[i][0]][2] = 2;
                        //-------------------------
                    } else {
                        adhoc.GlobalParameters.ListConfirmed[LSCH[i][0]][1] = LSCH[i][0];
                        adhoc.GlobalParameters.ListConfirmed[LSCH[i][0]][2] = 0;
                    }
                    //--------------------------------
                }

            }
        }

//--------------------------- Pour verifier les états des MN--------------------
        for (int i = 0; i < nbMn; i++) {
            exist = false;
            nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(LMN[i][0], vact.length);
            if (nbVi > 0) {

                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(LMN[i][0], vact, nbVi);
                for (int j = 0; j < nbVi; j++) {
                    if (Lvi[j] == LMN[i][2]) {
                        exist = true;
                    }
                }

                if (exist == false) {
                    //----------traitement maintenance

                    for (int j = 0; j < nbVi; j++) {
                        if (Lvi[j] == LMN[i][1]) {
                            exist = true;
                            //--------------------
                            for (int k = 0; k < nbSch; k++) {
                                if (LMN[i][1] == LSCH[k][0]) {
                                    adhoc.GlobalParameters.ListConfirmed[LMN[i][0]][1] = LSCH[k][1];
                                    adhoc.GlobalParameters.ListConfirmed[LMN[i][0]][2] = 3;
                                }
                            }
                            //---------------------
                        }
                    }

                    if (exist == false) {
                        //------------------

                        int nbvch = 0;
                        for (int j = 0; j < nbVi; j++) {
                            for (int k = 0; k < nbCh; k++) {
                                if (LCH[k] == Lvi[j]) {
                                    nbvch++;
                                }
                            }
                        }

                        if (nbvch > 0) {

                            double tmp[][] = new double[nbvch][2];
                            double FitMin = 10 * Math.E + 6;
                            int idX = 0;

                            nbvch = 0;

                            for (int j = 0; j < nbVi; j++) {
                                for (int k = 0; k < nbCh; k++) {
                                    if (LCH[k] == Lvi[j]) {
                                        tmp[nbvch][0] = Lvi[j];
                                        tmp[nbvch][1] = Math.abs(vanetsim.scenario.Vehicle.TV[LMN[i][0]].Fitness - vanetsim.scenario.Vehicle.TV[Lvi[j]].Fitness);
                                        if (tmp[nbvch][1] < FitMin) {
                                            FitMin = tmp[nbvch][1];
                                            idX = Lvi[j];
                                        }
                                        nbvch++;
                                    }
                                }
                            }
                            //------------------------
                            if (i < LMN.length) {
                                if (LMN[i][0] < adhoc.GlobalParameters.ListConfirmed.length) {
                                    adhoc.GlobalParameters.ListConfirmed[LMN[i][0]][1] = idX;
                                    adhoc.GlobalParameters.ListConfirmed[LMN[i][0]][2] = 2;
                                }
                            }
                            //-------------------------
                        } else //calcul de RelativeFitness
                        {
                            //-----------------------------

                            double list[][] = new double[nbVi][6];

                            for (int v = 0; v < nbVi; v++) {
                                list[v][0] = Lvi[v];
                                for (int j = 0; j < nbMn; j++) {
                                    if (LMN[j][0] == Lvi[v]) {
                                        list[v][1] = LMN[j][1];
                                        list[v][2] = LMN[j][2];
                                        list[v][3] = Math.abs(vanetsim.scenario.Vehicle.TV[(int) list[v][0]].Fitness - vanetsim.scenario.Vehicle.TV[(int) list[v][1]].Fitness);
                                        list[v][4] = Math.abs(vanetsim.scenario.Vehicle.TV[(int) list[v][0]].Fitness - vanetsim.scenario.Vehicle.TV[(int) list[v][2]].Fitness);
                                        //---------------------------------------------------------------------------
                                        if (list[v][3] < list[v][4]) {
                                            list[v][5] = Math.abs(vanetsim.scenario.Vehicle.TV[i].Fitness - vanetsim.scenario.Vehicle.TV[(int) list[v][0]].Fitness) + list[v][3];
                                        } else {
                                            list[v][5] = Math.abs(vanetsim.scenario.Vehicle.TV[i].Fitness - vanetsim.scenario.Vehicle.TV[(int) list[v][0]].Fitness) + list[v][4];
                                        }
                                        //----------------------------------------------------------------------------
                                    }

                                }
                            }

                            //-----------------------------
                            int index = 0, monch;
                            double Minfit = list[index][5];
                            for (int v = 1; v < nbVi; v++) {
                                if (list[v][5] < Minfit) {
                                    Minfit = list[v][5];
                                    index = v;
                                }
                            }
                            //-----------------------------
                            if (list[index][3] < list[index][4]) {
                                monch = (int) list[index][1];
                            } else {
                                monch = (int) list[index][2];
                            }
                            //----------------------------------
                            // System.out.println("B - ListConfirmed : "+adhoc.GlobalParameters.ListConfirmed.length+"  LMN : "+LMN.length+"  i : "+i+"  Contenu : "+LMN[i][0]);

                            adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LMN[i][0], vact)][1] = monch;   //y'a un doute
                            adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LMN[i][0], vact)][2] = 3;
                            //-----------------------------

                        }

                        //------------------
                    }

                }

            } else {
                //  System.out.println("A - ListConfirmed : "+adhoc.GlobalParameters.ListConfirmed.length+"  LMN : "+LMN.length+"  i : "+i+"  Contenu : "+LMN[i][0]);

                adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LMN[i][0], vact)][1] = LMN[i][0];
                adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(LMN[i][0], vact)][2] = 0;
            }
        }

//----------------------------- Pour verifier les états des SM------------------
        for (int i = 0; i < nbSm; i++) {
            exist = false;
            nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(LSM[i][0], vact.length);
            if (nbVi > 0) {
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(LSM[i][0], vact, nbVi);
                for (int j = 0; j < nbVi; j++) {
                    if (Lvi[j] == LSM[i][3] || Lvi[j] == LSM[i][2] || Lvi[j] == LSM[i][1]) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    //----------traitement maintenance
                }

            }
        }

        //=======================
        for (int i = 0; i < adhoc.GlobalParameters.tailleListConfirmed + nbsup; i++) {
            //---------------------
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                int nbv = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(adhoc.GlobalParameters.ListConfirmed[i][0], vact.length), l = 0;// nbVi = ;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].nbvoisin = nbv;
                Lvi = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(adhoc.GlobalParameters.ListConfirmed[i][0], vact, nbv);

                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv = new double[nbv + 1][2];

                for (int k = 0; k < nbv; k++) {
                    vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[k][0] = Lvi[k];
                    vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[k][1] = vanetsim.scenario.Vehicle.TV[Lvi[k]].Fitness;
                }
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[nbv][0] = i;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[nbv][1] = vanetsim.scenario.Vehicle.TV[i].Fitness;
                TriCouverture(vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv, 1);

                if (nbv > 1) {
                    while (l < nbv) {
                        if ((int) vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l][0] == i) {
							//-------------------
                                                     /*
                             l : 0/4
                             Taille ListConfirmed       : 55
                             Contenu ListConfirmed[3][0]: 34
                             Contenu Lcouv[1][0]: 64
                                                     
                             */
                            //----------------------
                            if (l < nbv) {
                                                          //  System.out.println("l : "+l+"/"+nbv); 
                                //   System.out.println("Taille ListConfirmed       : "+adhoc.GlobalParameters.ListConfirmed.length);
                                //  System.out.println("Contenu ListConfirmed["+i+"][0]: "+adhoc.GlobalParameters.ListConfirmed[i][0]);
                                //   System.out.println("Contenu Lcouv["+(l+1)+"][0]: "+(int)vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l+1][0]);

                                //  System.out.println("position Lcouv["+(l+1)+"][0]: "+Simulation.Vehicule.GetPositionElementTableau((int)vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l+1][0],   adhoc.GlobalParameters.ListConfirmed, 0));
                                if (adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElementTableau((int) vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l + 1][0], adhoc.GlobalParameters.ListConfirmed, 0)][2] == 2) {
                                    adhoc.GlobalParameters.ListConfirmed[vanetsim.scenario.Vehicle.Vehicule.GetPositionElementTableau((int) vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].Lcouv[l + 1][0], adhoc.GlobalParameters.ListConfirmed, 0)][2] = 1;
                                }
                            }
                            //-------------------
                        }
                        l++;
                    }
                } else {
                    adhoc.GlobalParameters.ListConfirmed[i][2] = 0;
                }
            }
            //---------------------

            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Ch";
                Tnd.setValueAt("Ch", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 1) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Sch";
                Tnd.setValueAt("Sch", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 2) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Mn";
                Tnd.setValueAt("Mn", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 3) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Sm";
                Tnd.setValueAt("Sm", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = true;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = false;
            }
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == -1) {
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].type_noeud_FC = "Init";
                Tnd.setValueAt("init", i, 1);
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_ch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sch_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_mn_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_sm_FC = false;
                vanetsim.scenario.Vehicle.TV[adhoc.GlobalParameters.ListConfirmed[i][0]].est_init_FC = true;
            }

            Tnd.setValueAt("" + (adhoc.GlobalParameters.ListConfirmed[i][1] + 1), i, 4);
        }
        CalculateStateDuration_FC();

    }

//==============================================================================
    public static void Tri(DefaultTableModel T, int col) //saliha
    {
        String tmp[] = new String[T.getColumnCount()];
        double vi, vj;
        for (int m = 1; m < T.getRowCount(); m++) {
            for (int i = 1; i < T.getRowCount() - 1; i++) {
                vi = Double.parseDouble(T.getValueAt(i, col).toString());
                for (int j = i + 1; j < T.getRowCount(); j++) {
                    vj = Double.parseDouble(T.getValueAt(j, col).toString());
                    if (vj < vi) {
                        for (int k = 0; k < T.getColumnCount(); k++) {
                            tmp[k] = T.getValueAt(i, k).toString();
                        }
                        for (int k = 0; k < T.getColumnCount(); k++) {
                            T.setValueAt(T.getValueAt(j, k), i, k);
                        }
                        for (int k = 0; k < T.getColumnCount(); k++) {
                            T.setValueAt(tmp[k], j, k);
                        }
                    }

                }

            }
        }

    }

//==============================================================================
    public static void propriete(DefaultTableModel PR, int Lvact[]) {

        //-------------------------------FitnessClustering----------------
        if (PR.getColumnCount() == 0) {

            PR.addColumn("Noeuds");
            PR.addColumn("Type");
            PR.addColumn("Nbr. Voisin");
            PR.addColumn("Fitness");
            PR.addColumn("CH");
            PR.addColumn("SCH");
            PR.setRowCount(Lvact.length);
        } else {

            PR.setRowCount(Lvact.length);
        }

        for (int i = 0; i < PR.getColumnCount(); i++) {
            for (int j = 0; j < PR.getRowCount(); j++) {
                if (i == 0) {
                    PR.setValueAt("V" + (vanetsim.scenario.Vehicle.TV[Lvact[j]].idVeh + 1), j, 0);
                } else {
                    PR.setValueAt(" ", j, i);
                }
            }
        }

        for (int i = 0; i < Lvact.length; i++) {
            PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].type_noeud_FC, i, 1);
            PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].nbvoisin, i, 2);
            PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].Fitness, i, 3);
            PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].myCH_FC, i, 4);
            // PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].,i, 5);
        }

    }
//------------------------------------------------------------------------------

//==============================================================================
    public static double Fct_Fitness(int id, int vact[], double a, double b, double c, double d, DefaultTableModel T, DefaultTableModel V, DefaultTableModel L) //saliha
    {

        int CD = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(id, vact.length), n;
        n = CD;
        double fitness = 0, TRP, Vr, Lv;

        int src = id, cbl = 0;
        //for(int j=0;j<vact.length;j++)
        //if(vact[j]==vact[id]) src = j;

        if (CD > 0) {
            int ListV[];

            ListV = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(id, vact, CD);
            
            //----------------------------

            for (int i = 0; i < ListV.length; i++) {

                for (int j = 0; j < vact.length; j++) {
                    if (vact[j] == ListV[i]) {
                        cbl = j;
                    }
                }

                TRP = Math.abs((double) (vanetsim.scenario.Vehicle.TR[i][id]));//  -   vanetsim.scenario.Vehicle.TV[vact[id]].TS));
             
               

//vanetsim.scenario.Vehicle.TR[ids][idr] =Dt;
                // T.setValueAt(""+TRP, id, i+1); //????
                Vr = Fct_RelativeVelocity(vact[id], ListV[i]);
                // V.setValueAt(""+Vr,id,i+1);
                Lv = Fct_LinkLifeTimeValidity(src, cbl, vact[id]);
                //  L.setValueAt(""+Lv,id,i+1);	   //rap->Lines->Add("FF            ggg");
                //System.out.println(" a = "+a+" b = "+b+" c = "+c+" d = "+d);
                fitness = fitness + Math.abs(a * ((double) TRP / (double) n) - b * ((double) CD / (double) n) + c * ((double) Vr / (double) n) - d * ((double) Lv / (double) n));
           System.out.println("CD = "+CD+ " Vr = "+Vr+" Lv= "+Lv+" fitness= "+fitness);
            }
            //----------------------------

        }
        return fitness;
    }
//==============================================================================

    public static double precision(double val, int prc) //saliha
    {
        double vl = val * Math.pow(10, prc);
        vl = Math.ceil(vl + 0.5);
        vl = vl * Math.pow(10, -prc);
        return vl;
    }
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
    public static double Fct_RelativeVelocity(int ids, int idr) {
        return Math.abs(vanetsim.scenario.Vehicle.TV[idr].vitesse - vanetsim.scenario.Vehicle.TV[ids].vitesse);
    }
//------------------------------------------------------------------------------

    public static double Fct_LinkLifeTimeValidity(int ids, int idr, int pos) //saliha
    {
        if (ids < adhoc.GlobalParameters.DIST.length) {
            if (idr < adhoc.GlobalParameters.DIST[ids].length) {
                if (vanetsim.scenario.Vehicle.TV[pos].RelativeVelocity != 0) {
                    return Math.abs((double) adhoc.GlobalParameters.DIST[ids][idr] / (double) vanetsim.scenario.Vehicle.TV[pos].RelativeVelocity);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
//------------------------------------------------------------------------------

    public static String BroadCastHello(int ids, int avi) //saliha
    {
        long TS = System.currentTimeMillis();
        vanetsim.scenario.Vehicle.TV[ids].TS = TS;
        String Packet = "";
        //System.out.println("Broadcast Hello Debut");
        Packet = "" + vanetsim.scenario.Vehicle.TV[ids].idVeh + ":"
                + vanetsim.scenario.Vehicle.TV[ids].posx + ":"
                + vanetsim.scenario.Vehicle.TV[ids].posy + ":"
                + (int) vanetsim.scenario.Vehicle.TV[ids].vitesse + ":"
                + vanetsim.scenario.Vehicle.TV[ids].idVoie + ":"
                + vanetsim.scenario.Vehicle.TV[ids].direction + ":"
                + vanetsim.scenario.Vehicle.TV[ids].nbvoisin + ":"
                + TS + ":"
                + avi + ":";
        //System.out.println("Broadcast Hello Fin");
        return Packet;
    }
//------------------------------------------------------------------------------

    public static void ReceivePacket(String Pkt, int idr, int portee, int vact[])//saliha
    {

        DecompPacket(Pkt, idr);

        long tr = System.currentTimeMillis() + (long) rnd.nextDouble(), Dt;

        int ids = 0, x, y, vsAv, vrAv, vsAp = 0, vrAp = 0;

        ids = Integer.parseInt(vanetsim.scenario.Vehicle.TV[idr].Dpacket[0]);
        x = Integer.parseInt(vanetsim.scenario.Vehicle.TV[idr].Dpacket[1]);
        y = Integer.parseInt(vanetsim.scenario.Vehicle.TV[idr].Dpacket[2]);

        vsAv = ids;
        vrAv = idr;

        for (int i = 0; i < vact.length; i++) {
            if (vact[i] == ids) {
                vsAp = i;
            }
            if (vact[i] == idr) {
                vrAp = i;
            }
        }

        //----------------------------------------------
        ids = vsAp;
        idr = vrAp;

        // vanetsim.scenario.Vehicle.TV[vsAv].TR[ids][idr] = tr;    //**************
        if (ids != idr) {
            if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(vact[ids], vact[idr]) <= portee) {

                //--------------Ajout le packet à l'historique -------------------------
                if (vanetsim.scenario.Vehicle.TV[vrAv].nbPacket == 0) {

                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt = new double[1][15];
                    Dt = (tr - vanetsim.scenario.Vehicle.TV[vsAv].TS);
                   // vanetsim.scenario.Vehicle.TV[vsAv].TR[ids][idr] = Dt;
                    vanetsim.scenario.Vehicle.TR[ids][idr] =Dt;
                    //tr = tr + 1000000*((double)rand()*1E-4/RAND_MAX);

                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][0] = vanetsim.scenario.Vehicle.TV[vrAv].nbPacket + 1;

                    for (int i = 0; i < 9; i++) {

                        if (i == 0) {
                            vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][i + 1] = Float.parseFloat(vanetsim.scenario.Vehicle.TV[vrAv].Dpacket[i]) + 1;
                        } else {
                            vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][i + 1] = Float.parseFloat(vanetsim.scenario.Vehicle.TV[vrAv].Dpacket[i]);
                        }
                    }

                    //----------------------------------------------
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][10] = tr;
                    vanetsim.scenario.Vehicle.TV[vsAv].RelativeVelocity = Fct_RelativeVelocity(vsAv, vrAv);
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][11] = vanetsim.scenario.Vehicle.TV[vsAv].RelativeVelocity;
                    vanetsim.scenario.Vehicle.TV[vsAv].LinkValidity = Fct_LinkLifeTimeValidity(vsAp, vrAp, vsAv);
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][12] = vanetsim.scenario.Vehicle.TV[vsAv].LinkValidity;
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][13] = Dt;//  (double)tr - (double)vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][7];
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][14] = 1; //message tjrs valide
                    vanetsim.scenario.Vehicle.TV[vrAv].nbPacket++;

                } else {

                    Dt = Math.abs((tr - vanetsim.scenario.Vehicle.TV[vrAv].TS));
                    vanetsim.scenario.Vehicle.TR[ids][idr] = Dt;
                    double tmp[][] = new double[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket][15];

                    for (int i = 0; i < vanetsim.scenario.Vehicle.TV[vrAv].nbPacket; i++) {
                        for (int j = 0; j < 15; j++) {
                            tmp[i][j] = vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[i][j];
                        }
                    }

                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt = new double[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket + 1][15];

                    for (int i = 0; i < vanetsim.scenario.Vehicle.TV[vrAv].nbPacket; i++) {
                        for (int j = 0; j < 15; j++) {
                            vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[i][j] = tmp[i][j];
                        }
                    }

                    //----------------------------------------------------------------
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][0] = vanetsim.scenario.Vehicle.TV[vrAv].nbPacket;
                    for (int i = 0; i < 9; i++) {
                        if (i == 0) {
                            vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][i + 1] = Float.parseFloat(vanetsim.scenario.Vehicle.TV[vrAv].Dpacket[i]) + 1;
                        } else {
                            vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][i + 1] = Float.parseFloat(vanetsim.scenario.Vehicle.TV[vrAv].Dpacket[i]);
                        }
                    }
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][10] = tr; //message tjrs valide
                    vanetsim.scenario.Vehicle.TV[vsAv].RelativeVelocity = Fct_RelativeVelocity(vsAv, vrAv);
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][11] = vanetsim.scenario.Vehicle.TV[vsAv].RelativeVelocity;
                    vanetsim.scenario.Vehicle.TV[vsAv].LinkValidity = Fct_LinkLifeTimeValidity(vsAp, vrAp, vsAv);
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][12] = vanetsim.scenario.Vehicle.TV[vsAv].LinkValidity;
                    vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket - 1][13] = Dt;// (float)tr - (float)vanetsim.scenario.Vehicle.TV[vrAv].TReceivedPkt[vanetsim.scenario.Vehicle.TV[vrAv].nbPacket-1][7];
                    vanetsim.scenario.Vehicle.TV[vrAv].nbPacket++;

                }
                //---------------------------------------
            }
        }

    }
//------------------------------------------------------------------------------

    static void DecompPacket(String Pkt, int id)//saliha
    {
        String ch = Pkt, mot = "";
        int taille = ch.length(), ind = 0;

        for (int i = 0; i < taille; i++) {
            if (!ch.substring(i, i + 1).equals(":")) {
                mot = mot + ch.substring(i, i + 1);
            } else {
                if (mot.trim().length() != 0) {
                    vanetsim.scenario.Vehicle.TV[id].Dpacket[ind] = mot.trim();
                    ind++;
                }
                mot = "";
            }
        }

    }
//------------------------------------------------------------------------------

    static void affichageHistorique(int nd, DefaultTableModel A) //saliha
    {
        for (int i = 0; i < vanetsim.scenario.Vehicle.TV[nd].nbPacket; i++) {
            for (int j = 0; j < 15; j++) {
                A.setValueAt(vanetsim.scenario.Vehicle.TV[nd].TReceivedPkt[i][j], A.getRowCount() - 1, j);
            }
            A.setRowCount(A.getRowCount() + 1);

        }
        A.setRowCount(A.getRowCount() - 1);
    }
//------------------------------------------------------------------------------

    public static int nbr_saut(int nd, int cg) {
        return vanetsim.scenario.Vehicle.TV[nd].NBS[cg];
    }

    public static int nbr_voisin(DefaultTableModel V, int nd) {
        int v = 0;
        for (int i = 1; i < V.getColumnCount(); i++) {
            if (!V.getValueAt(nd, i).equals(" ")) {
                v++;
            }
        }
        return v;
    }
//------------------------------------------------------------------------------     

    public static boolean verif_impact(int id, int nbn, double x, double y) //It was used in the previous version
    {
        double dist;
        boolean trv;
        int i = 0;
        trv = false;

        while (i < nbn) {
            if (i != id) {
                if (vanetsim.scenario.Vehicle.TV[id].idVoie == vanetsim.scenario.Vehicle.TV[i].idVoie) {
                    if (vanetsim.scenario.Vehicle.TV[id].posx > vanetsim.scenario.Vehicle.TV[i].posx) {
                        dist = vanetsim.scenario.Vehicle.TV[id].posx - (vanetsim.scenario.Vehicle.TV[i].posx + vanetsim.scenario.Vehicle.TV[i].largeur);
                        if (dist < 35) {
                            trv = true;
                        }
                    }
                }
            }
            i++;
        }
        return trv;
    }
//------------------------------------------------------------------------------

    public static boolean test_arret(int nbn) {
        boolean t = false;
        for (int i = 0; i < nbn; i++) {
            if (vanetsim.scenario.Vehicle.TV[i].etat.equals("Actif")) {
                t = true;
            }
        }
        return t;
    }
//==============================================================================

    public static void RoutingFc(DefaultListModel path) {

        System.out.println("Fclustering : Premiére entrée Taille de path : " + path.getSize());
        //--------------------------------         
        adhoc.Groupes.Recherchemembre(adhoc.Adhoc.IdSrc);

        boolean est_membreSrc = adhoc.Groupes.est_membre;
        boolean est_leaderSrc = adhoc.Groupes.est_leader;
        int IdGrpSrc = adhoc.Groupes.IdGrp;

        adhoc.Groupes.Recherchemembre(adhoc.Adhoc.IdCbl);

        boolean est_membreCbl = adhoc.Groupes.est_membre;
        boolean est_leaderCbl = adhoc.Groupes.est_leader;
        int IdGrpCbl = adhoc.Groupes.IdGrp;

                  //------------------------------------------------------------
                 /*  if(est_membreSrc == true)
         {
         path.addElement(""+vanetsim.scenario.Vehicle.IdSrc);
         path.addElement(""+IdGrpSrc);
         }
         else
         {
         path.addElement(""+vanetsim.scenario.Vehicle.IdSrc);
         }*/
        path = adhoc.Groupes.LienGroupe(adhoc.Adhoc.IdSrc, adhoc.Adhoc.IdCbl, adhoc.Adhoc.portee, 0.5, 0.5);

  //----------------------------------------------------------------------------
//==============================================================================
    }
    //--------------------
    public static void CalculateStateDuration_FC(){
        
    for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
    {
        if (vanetsim.scenario.Vehicle.TV[v].etat == "Actif")
        if(!vanetsim.scenario.Vehicle.TV[v].type_noeud_FC.equals(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1)))
        {   
            vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
            vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
            vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
            vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add(""+vanetsim.scenario.Vehicle.TV[v].type_noeud_FC);   
            System.out.println("ETAT_FC du noeud "+vanetsim.scenario.Vehicle.TV[v].idVeh+" est "
            +vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1)+" pendant (DUREE_FC)"
            +vanetsim.scenario.Vehicle.TV[v].DUREE_FC.get(vanetsim.scenario.Vehicle.TV[v].DUREE_FC.size()-1)+" ms.");
            
        }     
    }
        
   /* for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
         {
             if (vanetsim.scenario.Vehicle.TV[v].etat == "Actif"){
             if (vanetsim.scenario.Vehicle.TV[v].type_noeud_FC){}
                 if(vanetsim.scenario.Vehicle.TV[v].est_init_FC == true && !vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1).equals("Init") )
                 {
                      vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
                      vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
                      vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
                      vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add("Init");
                 }else
                 if(vanetsim.scenario.Vehicle.TV[v].est_ch_FC == true && !vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1).equals("Ch") )
                 {
                      vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
                      vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
                      vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
                      vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add("Ch");
                 }else 
                 if(vanetsim.scenario.Vehicle.TV[v].est_sch_FC == true && !vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1).equals("Sch") )
                 {
                      vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
                      vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
                      vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
                      vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add("Sch");
                 }else 
                 if(vanetsim.scenario.Vehicle.TV[v].est_mn_FC == true && !vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1).equals("Mn") )
                 {
                      vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
                      vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
                      vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
                      vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add("Mn");
                 }else 
                 if(vanetsim.scenario.Vehicle.TV[v].est_sm_FC == true && !vanetsim.scenario.Vehicle.TV[v].ETAT_FC.get(vanetsim.scenario.Vehicle.TV[v].ETAT_FC.size()-1).equals("Sm") )
                 {
                      vanetsim.scenario.Vehicle.TV[v].transfin_FC = System.currentTimeMillis();
                      vanetsim.scenario.Vehicle.TV[v].DUREE_FC.add(vanetsim.scenario.Vehicle.TV[v].transfin_FC - vanetsim.scenario.Vehicle.TV[v].transdeb_FC);
                      vanetsim.scenario.Vehicle.TV[v].transdeb_FC = vanetsim.scenario.Vehicle.TV[v].transfin_FC;
                      vanetsim.scenario.Vehicle.TV[v].ETAT_FC.add("Sm");
                 }
                                  
         } 
    }*/
        
}
  
    //-------------------
}
