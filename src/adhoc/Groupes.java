/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adhoc;

import javax.swing.DefaultListModel;

/**
 *
 * @author hp probook
 */
public class Groupes {
//------------------------------------------------------------------------------

    public static boolean est_membre;
    public static boolean est_leader;
    public static int IdGrp;

//------------------------------------------------------------------------------    
    static int tab[][];

    int idGroupe;
    int Leader;
    int nbMembre;
    int ListeMembre[][];

    int PosGroupe;
    int xGrp;

    public static void CreationGroupes(int nbGrp) {
        Adhoc.GRS = new Groupes[nbGrp];

        for (int g = 0; g < nbGrp; g++) {
            Groupes grp = new Groupes();
            grp.idGroupe = g;
            grp.Leader = -1;
            grp.nbMembre = 0;
            grp.ListeMembre = null;
            grp.PosGroupe = -1;
            grp.xGrp = -1;
            Adhoc.GRS[g] = grp;
        }
    }

    //---------------------------------------------------------------
    public static void Recherchemembre(int idVeh)// Inutile
    {
        est_membre = false;
        est_leader = false;
        IdGrp = -1;

        for (int i = 0; i < Adhoc.GRS.length; i++) {
            if (Adhoc.GRS[i].Leader == idVeh) {
                est_membre = false;
                est_leader = true;
                IdGrp = i;
            } else {
                for (int j = 0; j < Adhoc.GRS[i].ListeMembre.length; j++) {
                    if (Adhoc.GRS[i].ListeMembre[j][0] == idVeh) {
                        est_membre = true;
                        est_leader = false;
                        IdGrp = i;
                    }
                }
            }
        }

    }

    //---------------------------------------------------------------

    public static int CalculNbrGroupe() {
        int n = 0;
        for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                n++;
            }
        }
        return n;
    }

    //-------------------------------------------------------------------------
    public static void CollectMembreGroupe() {
        int n = 0;
        for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
            if (adhoc.GlobalParameters.ListConfirmed[i][2] == 0) {
                Adhoc.GRS[n].Leader = adhoc.GlobalParameters.ListConfirmed[i][0];
                Adhoc.GRS[n].xGrp = vanetsim.scenario.Vehicle.TV[Adhoc.GRS[n].Leader].posx;

                n++;
            }
        }
        //----------------------------------------
        n = 0;
        for (int j = 0; j < Adhoc.GRS.length; j++) {
            for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
                if (adhoc.GlobalParameters.ListConfirmed[i][1] == Adhoc.GRS[j].Leader) {
                    Adhoc.GRS[j].nbMembre++;
                }
            }
            Adhoc.GRS[j].ListeMembre = new int[Adhoc.GRS[j].nbMembre][4];
            n++;
        }
        //----------------------------------------

        n = 0;
        int ident, elet;
        for (int j = 0; j < Adhoc.GRS.length; j++) {
            n = 0;
            System.out.println("le groupe " + Adhoc.GRS[j].idGroupe + " contient " + Adhoc.GRS[j].nbMembre + " éléments, ---> Leader : " + Adhoc.GRS[j].Leader);
            for (int i = 0; i < adhoc.GlobalParameters.ListConfirmed.length; i++) {
                if (adhoc.GlobalParameters.ListConfirmed[i][1] == Adhoc.GRS[j].Leader) {

                    Adhoc.GRS[j].ListeMembre[n][0] = adhoc.GlobalParameters.ListConfirmed[i][0];
                    Adhoc.GRS[j].ListeMembre[n][1] = adhoc.GlobalParameters.ListConfirmed[i][2];//

                    elet = adhoc.GlobalParameters.ListConfirmed[i][0];
                    //ident    = Simulation.Vehicule.GetPositionElement(adhoc.GlobalParameters.ListConfirmed[i][0],Adhoc.Lvactif1);
                    ident = adhoc.GlobalParameters.ListConfirmed[i][0];
                    //System.err.println("ident=======>" + ident);

                    Adhoc.GRS[j].ListeMembre[n][2] = vanetsim.scenario.Vehicle.TV[ident].posx;
                    Adhoc.GRS[j].ListeMembre[n][3] = vanetsim.scenario.Vehicle.TV[ident].posy;

                   // System.out.println("L'élément: " + Adhoc.GRS[j].ListeMembre[n][0] + " etat: " + Adhoc.GRS[j].ListeMembre[n][1] + " posx: " + Adhoc.GRS[j].ListeMembre[n][2] + " posy: " + Adhoc.GRS[j].ListeMembre[n][3]);
                    n++;
                }

            }
             //---------------------------------

            //----------------------------------
        }

    }

    //-------------------------------------------------------------------------  
    public static void TriGroupe() {
        tab = new int[Adhoc.GRS.length][2];
        for (int i = 0; i < tab.length; i++) {
            tab[i][0] = Adhoc.GRS[i].xGrp;
            tab[i][1] = i;
        }
        int tmp[] = new int[2], vi, vj;

        for (int n = 0; n < tab.length; n++) {
            for (int i = 0; i < tab.length - 1; i++) {

                vi = tab[i][0];
                for (int j = i + 1; j < tab.length; j++) {
                    vj = tab[j][0];

                    if (vj > vi) { //here modif 21/08/2017 < / >
                        for (int k = 0; k < tmp.length; k++) {
                            tmp[k] = tab[i][k];
                        }
                        for (int k = 0; k < tmp.length; k++) {
                            tab[i][k] = tab[j][k];
                        }
                        for (int k = 0; k < tmp.length; k++) {
                            tab[j][k] = tmp[k];
                        }
                    }
                }
            }
        }
        //-----------------------------------------
        for (int i = 0; i < tab.length; i++) {
            Adhoc.GRS[tab[i][1]].PosGroupe = i;
            System.out.println("Dans la position " + (i + 1) + "  il y a le groupe " + tab[i][1] + " ---> position x : " + Adhoc.GRS[tab[i][1]].xGrp);
        }
    }

    //-------------------------------------------------------------------------
    public static int GetIdGroup(int idpos) {
        int idleader = 0;
        for (int i = 0; i < Adhoc.GRS.length; i++) {
            if (Adhoc.GRS[i].PosGroupe == idpos) {
                idleader = Adhoc.GRS[i].idGroupe;
            }
        }
        return idleader;

    }

    //--------------------------------------------------------------------------

    public static double GetRp(DefaultListModel liste, double a, double b) {
        double lv = 0.0, trp = 0.0;
        int Tnode[] = new int[liste.getSize()];
        for (int i = 0; i < liste.getSize(); i++) {
            Tnode[i] = Integer.parseInt(liste.getElementAt(i).toString());
        }
        int id, id1;

        for (int i = 0; i < Tnode.length - 1; i++) {
            id =  vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(Tnode[i], adhoc.Adhoc.LvactifOld);
            id1 = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(Tnode[i + 1], adhoc.Adhoc.LvactifOld);

            //System.err.println("id : "+id+"  Tnode[i] = "+Tnode[i]+"  id1 : "+id1+"   Tnode[i+1] = "+Tnode[i+1]+"    Taille TR : "+adhoc.Adhoc.TV[Tnode[i+1]].TR.length+" Taille LvactifOLD : "+adhoc.Adhoc.LvactifOld.length);
            lv = lv + Algorithms.FitnessClustering.Fct_LinkLifeTimeValidity(id, id1, id);

            if ((id1 < vanetsim.scenario.Vehicle.TR.length) && (id < vanetsim.scenario.Vehicle.TR.length)) {
                trp = trp + Math.abs((double) (vanetsim.scenario.Vehicle.TR[id1][id]));
            }
        }
        return a * lv + b * trp;
    }

    //--------------------------------------------------------------------------

    public static DefaultListModel LienGroupe(int idps, int idpc, int portee, double a, double b) {
        double rp = 0.0;
        int src = -1, cbl = -1;
        int Chsrc, Chcbl;
        int idG1, idG2;
        int Xg1, Yg1, Xg2, Yg2;

        idG1 = GetIdGroup(idps);
        idG2 = GetIdGroup(idpc);
        System.out.println("IdG1 ===> " + idG1 + "   IdG2===>" + idG2);
        DefaultListModel saveBestPath = new DefaultListModel();
        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//-----------------------------------------------
        if (idG1 != idG2) {
            System.out.println("idps : " + idps + " ==> idG1 " + idG1 + " ===>  " + idpc + " ==> idG2 " + idG2 + "   ====>  Nombre de groupes : " + Adhoc.GRS.length);

            Chsrc = Adhoc.GRS[idG1].Leader;
            Chcbl = Adhoc.GRS[idG2].Leader;

            double rpMin = Math.pow(10, 20);
            DefaultListModel liste = new DefaultListModel();

            System.out.println("Le nombre de fils de " + idG1 + " est " + Adhoc.GRS[idG1].nbMembre);
            System.out.println("Le nombre de fils de " + idG2 + " est " + Adhoc.GRS[idG2].nbMembre);

            if (Adhoc.GRS[idG1].nbMembre == 2) {
                for (int i = 0; i < Adhoc.GRS[idG1].nbMembre; i++) {
                   
                        System.out.println(" fils " + i + " : " + Adhoc.GRS[idG1].ListeMembre[i][0]);
                   
                }
            }
            if (Adhoc.GRS[idG2].nbMembre == 2) {
                for (int i = 0; i < Adhoc.GRS[idG2].nbMembre; i++) {
                    for (int j = 0; j < Adhoc.GRS[idG2].ListeMembre.length; j++) {
                        System.out.println(" fils " + j + " : " + Adhoc.GRS[idG2].ListeMembre[j][0]);
                    }
                }
            }

            for (int i = 0; i < Adhoc.GRS[idG1].nbMembre; i++) {
                liste.removeAllElements();
                liste.addElement(Chsrc);
                src = Adhoc.GRS[idG1].ListeMembre[i][0];
                //===========pour CH1-MN1-MN2-CH2 ===================
                for (int j = 0; j < Adhoc.GRS[idG2].nbMembre; j++) {

                    cbl = Adhoc.GRS[idG2].ListeMembre[j][0];
                    //===============pour CH1-MN2-CH2=======================
                    if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(src, cbl) <= portee) {
                        //System.err.println("============> Case 1 (CH1-MN1-MN2-CH2 )");
                        liste.addElement(src);
                        liste.addElement(cbl);
                        liste.addElement(Chcbl);

                        //--------------Verif si le meuilleur chemin -----------------
                        rp = GetRp(liste, a, b);
                        //System.out.println(" pass 1 ---> RP  =  "+rp+" / rpMin = "+rpMin);
                        if (rp < rpMin) {
                            //System.err.println("============> Save 1");
                            rpMin = rp;
                            saveBestPath.removeAllElements();
                            for (int k = 0; k < liste.getSize(); k++) {
                                saveBestPath.addElement(liste.getElementAt(k));
                            }
                        }
                        // System.out.println(" pass 1 après ---> RP  =  "+rp+" / rpMin = "+rpMin+" list tmp contient "+liste.getSize()+" chemin sauvegardé contient "+saveBestPath+" noeuds");
                    }
                    //------------------------------------------------------------

                }

            }
            //------------------pour CH1-CH2---------------
            if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(Chsrc, Chcbl) <= portee) {
                liste.removeAllElements();
                liste.addElement(Chsrc);
                liste.addElement(Chcbl);
                rp = GetRp(liste, a, b);
                //System.out.println(" pass 2 ---> RP  =  "+rp+" / rpMin = "+rpMin);
                if (rp < rpMin) {
                    //System.err.println("============> Case 2 (CH1-CH2)");
                    rpMin = rp;
                    saveBestPath.removeAllElements();
                    for (int k = 0; k < liste.getSize(); k++) {
                        saveBestPath.addElement(liste.getElementAt(k));
                        //System.err.println("============> Save 2");
                    }
                }
                //System.out.println(" pass 2 après ---> RP  =  "+rp+" / rpMin = "+rpMin+" list tmp contient "+liste.getSize()+" chemin sauvegardé contient "+saveBestPath+" noeuds");
            }
            //------------------pour CH1-MN1-CH2---------------------------------
            for (int i = 0; i < Adhoc.GRS[idG1].nbMembre; i++) {
                liste.removeAllElements();
                liste.addElement(Chsrc);
                src = Adhoc.GRS[idG1].ListeMembre[i][0];

                if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(src, Chcbl) <= portee) {
                    liste.addElement(src);
                    liste.addElement(Chcbl);

                    //--------------Verif si le meuilleur chemin -----------------
                    rp = GetRp(liste, a, b);
                    //System.out.println(" pass 3 ---> RP  =  "+rp+" / rpMin = "+rpMin);
                    if (rp < rpMin) {
                        //System.err.println("============> Case 3 (CH1-MN1-CH2)");
                        rpMin = rp;
                        saveBestPath.removeAllElements();
                        for (int k = 0; k < liste.getSize(); k++) {
                            saveBestPath.addElement(liste.getElementAt(k));
                            //System.err.println("============> Save 3");
                        }
                    }
                    //System.out.println(" pass 3 après ---> RP  =  "+rp+" / rpMin = "+rpMin+" list tmp contient "+liste.getSize()+" chemin sauvegardé contient "+saveBestPath+" noeuds");
                }
            }
            //--------------------------------
            liste.removeAllElements();
            liste.addElement(Chsrc);

            //------------------pour CH1-MN2-CH2--------------------------------- 
            for (int j = 0; j < Adhoc.GRS[idG2].nbMembre; j++) {
                cbl = Adhoc.GRS[idG2].ListeMembre[j][0];
                //===============pour CH1-MN2-CH2=======================
                if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(Chsrc, cbl) <= portee) {
                    liste.addElement(cbl);
                    liste.addElement(Chcbl);

                    //--------------Verif si le meuilleur chemin -----------------
                    rp = GetRp(liste, a, b);
                    //System.out.println(" pass 4 ---> RP  =  "+rp+" / rpMin = "+rpMin);
                    if (rp < rpMin) {
                        //System.err.println("============> Case 4 (CH1-MN2-CH2)");
                        rpMin = rp;
                        saveBestPath.removeAllElements();
                        for (int k = 0; k < liste.getSize(); k++) {
                            saveBestPath.addElement(liste.getElementAt(k));
                            //System.err.println("============> Save 4");
                        }
                    }
                    //System.out.println(" pass 4 après ---> RP  =  "+rp+" / rpMin = "+rpMin+" list tmp contient "+liste.getSize()+" chemin sauvegardé contient "+saveBestPath+" noeuds");
                }
                //------------------------------------------------------------

            }
            System.out.println("Le meilleur chemin entre le groupe " + (idG1) + " et le groupe " + (idG2) + " est : ");
            for (int i = 0; i < saveBestPath.getSize(); i++) {
                if (adhoc.Adhoc.VerifExiste(Integer.parseInt(saveBestPath.getElementAt(i).toString()), adhoc.Adhoc.bestPathFC) == false) {
                    adhoc.Adhoc.bestPathFC.addElement(saveBestPath.getElementAt(i));
                    System.out.print(saveBestPath.getElementAt(i).toString() + " --> ");
                }

            }
            System.out.println();
        }
        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        //--------------------------------

        return saveBestPath;
    }

    //-------------------------------------------------------------------------

    public static int GetPositionElementList(int elt, DefaultListModel L) {
        int pos = -1;
        for (int i = 0; i < L.getSize(); i++) {
            if (L.getElementAt(i).equals("" + elt)) {
                pos = i;
            }

        }
        return pos;

    }

    //-------------------------------------------------------------------------

    public static int GetChElement(int Id) {
        int ch = -1;
        for (int i = 0; i < adhoc.Adhoc.GRS.length; i++) {
            //----localiser CH src
            for (int j = 0; j < adhoc.Adhoc.GRS[i].ListeMembre.length; j++) {
                if ((adhoc.Adhoc.GRS[i].ListeMembre[j][0] == Id) || (adhoc.Adhoc.GRS[i].Leader == Id)) {
                    ch = adhoc.Adhoc.GRS[i].Leader;
                    
                    

                }

            }

        }
        return ch;
    }

    //-------------------------------------------------------------------------

    public static DefaultListModel PathSrcCbl(DefaultListModel path, int idSrc, int idCbl, DefaultListModel PathOpt) {
        PathOpt.removeAllElements();
        int indiceSrc = -1, indiceCbl = -1;
        int chS, chC;
        boolean s = false, c = false;// source trouvée, cible trouvée

        if (adhoc.Adhoc.VerifExiste(idSrc, path) == true) {
            s = true;
        }
        if (adhoc.Adhoc.VerifExiste(idCbl, path) == true) {
            c = true;
        }

        if (s == true && c == true) {
            //-------------------------------------
            indiceSrc = GetPositionElementList(idSrc, path);
            indiceCbl = GetPositionElementList(idCbl, path);
            System.out.println("Chemin optimal est : ");
            for (int j = indiceSrc; j <= indiceCbl; j++) {
                System.err.println(" j ===  " +j+" taille path ==="+ path.size());
                PathOpt.addElement(path.getElementAt(j).toString());
                System.out.println(" Vehicule --> : " + path.getElementAt(j).toString());
            }
            //-------------------------------------    
        }
        if (s == true && c == false) {
            //-------------------------------------
            indiceSrc = GetPositionElementList(idSrc, path);
            chC = GetChElement(idCbl);
            System.out.println("CH cible =====> " + chC);
            indiceCbl = GetPositionElementList(chC, path);
            System.out.println("Chemin optimal est : ");
            for (int j = indiceSrc; j <= indiceCbl; j++) {
                System.err.println(" j ===  " +j+" taille path ==="+ path.size());
                PathOpt.addElement(path.getElementAt(j).toString());
                System.out.println(" Vehicule --> : " + path.getElementAt(j).toString());
            }
            //------------------------------------- 
        }
        if (s == false && c == true) {
            //-------------------------------------
            indiceCbl = GetPositionElementList(idCbl, path);
            chS = GetChElement(idSrc);
            System.out.println("CH source =====> " + chS);
            indiceSrc = GetPositionElementList(chS, path);
            System.out.println("Chemin optimal est : ");
            for (int j = indiceSrc; j <= indiceCbl; j++) {
                System.err.println(" j ===  " +j+" taille path ==="+ path.size());
                PathOpt.addElement(path.getElementAt(j).toString());
                System.out.println(" Vehicule --> : " + path.getElementAt(j).toString());
            }
            //------------------------------------- 
        }
        if (s == false && c == false) {
            //-------------------------------------
            chS = GetChElement(idSrc);
            System.out.println("CH source =====> " + chS);
            indiceSrc = GetPositionElementList(chS, path);
            chC = GetChElement(idCbl);
            System.out.println("CH source =====> " + chC);
            indiceCbl = GetPositionElementList(chC, path);
            System.out.println("Chemin optimal est : ");
            for (int j = indiceSrc; j <= indiceCbl; j++) { //Nano
                System.err.println(" j ===  " +j+" taille path ==="+ path.size());
                //if (j = indiceSrc)PathOpt.addElement(indiceSrc); //to fix
                PathOpt.addElement(path.getElementAt(j).toString());
                System.out.println(" Vehicule --> : " + path.getElementAt(j).toString());
            }
            //------------------------------------- 
        }

        return PathOpt;
    }

    //-------------------------------------------------------------------------

    public static void ConstructGroup() {
        CreationGroupes(CalculNbrGroupe());

        CollectMembreGroupe();

        TriGroupe();

        for (int g = 0; g < Adhoc.GRS.length - 1; g++) {
            LienGroupe(g, g + 1, adhoc.Adhoc.portee, 0.5, 0.5);

        }

    }

}
