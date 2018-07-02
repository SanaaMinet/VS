/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import java.util.Random;
import javax.swing.DefaultListModel;

/**
 *
 * @author pia
 */
public class Mozo {

    public static Random rnd = new Random();
    public static boolean arret = false;
    public static int Tpos[];

    public int ident;
    public int idMoz;
    public int nbV[];
    public int EletMaxDist;
    public int RelayNode;

    public double posXmoz;
    public double posYmoz;

    static double w1 = 0.5, w2 = 0.3, w3 = 0.2;

    public DefaultListModel LV1;
    public DefaultListModel LV2;
    public DefaultListModel LV3;
    public DefaultListModel Tintersec;

    public int idCapt;
    public double simCapt;

    double D1[];
    double D2[];
    double D3[];

    public double sim;

    public static void CreationMozo(int nbvActif, int Lactif[]) {
        adhoc.Adhoc.MOZ = new Mozo[nbvActif];
        Tpos = new int[nbvActif];

        for (int m = 0; m < Lactif.length; m++) {
            Mozo mz = new Mozo();
            mz.ident = m;
            mz.idMoz = Lactif[m];

            mz.posXmoz = vanetsim.scenario.Vehicle.TV[Lactif[m]].posx;
            mz.posYmoz = vanetsim.scenario.Vehicle.TV[Lactif[m]].posy;

            mz.LV1 = new DefaultListModel();
            mz.LV2 = new DefaultListModel();
            mz.LV3 = new DefaultListModel();
            mz.Tintersec = new DefaultListModel();
            mz.idCapt = 0;
            mz.simCapt = 0.0;

            mz.EletMaxDist = Lactif[m];
            mz.RelayNode = Lactif[m];

            mz.D1 = null;
            mz.D2 = null;
            mz.D3 = null;

            mz.nbV = new int[3];

            mz.sim = 0.0;

            adhoc.Adhoc.MOZ[m] = mz;
           
        }

    }
    //----------------------------------------------------------------------------
    public static void MajTpos(int nbvActif, int Lactif[])
    {
       Tpos = new int[nbvActif];
       for (int m = 0; m < Lactif.length; m++)  
       Tpos[m] = Lactif[m];
    }
    //----------------------------------------------------------------------------

    public static void CalculVoisinage(int idm, int periode, double DifT) {
        int id;
        double sim1 = 0.0, sim2 = 0.0, sim3 = 0.0;
        // DefaultListModel tmp = new DefaultListModel();
        //System.out.println(" periode == " + periode + " --- taille LV1 = " + adhoc.Adhoc.MOZ[idm].LV1.getSize() + "--- nbV " + adhoc.Adhoc.MOZ[idm].nbV[0]);

        if (periode == 0) 
        {
            if(!(adhoc.Adhoc.MOZ[idm].LV1.isEmpty()))
            {
                adhoc.Adhoc.MOZ[idm].LV1.removeAllElements();
                adhoc.Adhoc.MOZ[idm].nbV[0] = 0;
            }
        }
        if (periode == 1) {
            adhoc.Adhoc.MOZ[idm].LV2.removeAllElements();
            adhoc.Adhoc.MOZ[idm].nbV[1] = 0;
        }
        if (periode == 2) {
            adhoc.Adhoc.MOZ[idm].LV3.removeAllElements();
            adhoc.Adhoc.MOZ[idm].nbV[2] = 0;
        }

            //*affichagedistance // appel avant 
        //-----------------------------------------
        vanetsim.scenario.Vehicle.Vehicule.affichage_distance(adhoc.Adhoc.Lvactif1, adhoc.Adhoc.portee);
        //-----------------------------------------
        MajTpos(adhoc.Adhoc.Lvactif1.length, adhoc.Adhoc.Lvactif1);
        for (int j = 0; j < adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident].length; j++) 
        {
           // System.out.println("Distance entre : "+adhoc.Adhoc.MOZ[idm].ident+" et "+j+" === "+adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident][j]);
            
          //  if(j<Tpos.length)
            if (adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident][j] != -1) 
            {
                
                //--------------------
                if (periode == 0) {
                    //System.out.println("prd = 0 : idm : "+idm+" / taille : "+adhoc.Adhoc.MOZ.length+"  j = "+j+" / "+Tpos.length);
                    adhoc.Adhoc.MOZ[idm].LV1.addElement("" + Tpos[j]/*vanetsim.scenario.Vehicle.GetPositionElement(j, Tpos)*/);
                    adhoc.Adhoc.MOZ[idm].nbV[0]++;
                }
                if (periode == 1) {
                    //System.out.println("prd = 1 : idm : "+idm+" / taille : "+adhoc.Adhoc.MOZ.length+"  j = "+j+" / "+Tpos.length);
                    adhoc.Adhoc.MOZ[idm].LV2.addElement("" + Tpos[j]/*vanetsim.scenario.Vehicle.GetPositionElement(j, Tpos)*/);
                    adhoc.Adhoc.MOZ[idm].nbV[1]++;
                }
                if (periode == 2) 
                {
                    //System.out.println("prd = 2 : idm : "+idm+" / taille : "+adhoc.Adhoc.MOZ.length+"  j = "+j+" / "+Tpos.length);
                    adhoc.Adhoc.MOZ[idm].LV3.addElement("" + Tpos[j]/*vanetsim.scenario.Vehicle.GetPositionElement(j, Tpos)*/);
                    adhoc.Adhoc.MOZ[idm].nbV[2]++;
                }
                //---------------------
            }

        }
        //-------------------
        if (periode == 0) {
            adhoc.Adhoc.MOZ[idm].D1 = new double[adhoc.Adhoc.MOZ[idm].LV1.getSize()];
            for (int i = 0; i < adhoc.Adhoc.MOZ[idm].LV1.getSize(); i++) {
                id = Integer.parseInt(adhoc.Adhoc.MOZ[idm].LV1.getElementAt(i).toString());

                id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Tpos);
                //System.out.println("idm : "+idm+"     id = "+id+"/"+adhoc.GlobalParameters.DIST.length+"         ident : "+adhoc.Adhoc.MOZ[idm].ident+"/"+adhoc.GlobalParameters.DIST.length);
                adhoc.Adhoc.MOZ[idm].D1[i] = adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident][id];
            }
        }
        if (periode == 1) {
            adhoc.Adhoc.MOZ[idm].D2 = new double[adhoc.Adhoc.MOZ[idm].LV2.getSize()];
            for (int i = 0; i < adhoc.Adhoc.MOZ[idm].LV2.getSize(); i++) {
                id = Integer.parseInt(adhoc.Adhoc.MOZ[idm].LV2.getElementAt(i).toString());
                //System.out.println("Avant id : "+     id);
                id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Tpos);
                //System.out.println("Aprés id : "+     id);
                adhoc.Adhoc.MOZ[idm].D2[i] = adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident][id];
            }
        }
        if (periode == 2) {
            adhoc.Adhoc.MOZ[idm].D3 = new double[adhoc.Adhoc.MOZ[idm].LV3.getSize()];
            for (int i = 0; i < adhoc.Adhoc.MOZ[idm].LV3.getSize(); i++) {
                id = Integer.parseInt(adhoc.Adhoc.MOZ[idm].LV3.getElementAt(i).toString());
                id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Tpos);
                adhoc.Adhoc.MOZ[idm].D3[i] = adhoc.GlobalParameters.DIST[adhoc.Adhoc.MOZ[idm].ident][id];
            }

            //-------------------Intersection--------------------------
            adhoc.Adhoc.MOZ[idm].Tintersec = Intersection(idm);
            //---------------------------------------------------------
            adhoc.Adhoc.MOZ[idm].sim = 0.0;

            for (int i = 0; i < adhoc.Adhoc.MOZ[idm].Tintersec.getSize(); i++) {

                // id   = Integer.parseInt(tmp.getElementAt(i).toString());
                for (int k = 0; k < adhoc.Adhoc.MOZ[idm].LV1.getSize(); k++) {
                    if (adhoc.Adhoc.MOZ[idm].LV1.getElementAt(k).equals(adhoc.Adhoc.MOZ[idm].Tintersec.getElementAt(i))) {
                        sim1 = (double) w1 * adhoc.Adhoc.MOZ[idm].D1[k];
                        //System.out.println("sim1 de véhicule : "+adhoc.Adhoc.MOZ[idm].ident+"  est "+sim1);
                    }
                }

                for (int k = 0; k < adhoc.Adhoc.MOZ[idm].LV2.getSize(); k++) {
                    if (adhoc.Adhoc.MOZ[idm].LV2.getElementAt(k).equals(adhoc.Adhoc.MOZ[idm].Tintersec.getElementAt(i))) {
                        sim2 = (double) w2 * adhoc.Adhoc.MOZ[idm].D2[k];
                        //System.out.println("sim2 de véhicule : "+adhoc.Adhoc.MOZ[idm].ident+"  est "+sim2);
                    }
                }

                for (int k = 0; k < adhoc.Adhoc.MOZ[idm].LV3.getSize(); k++) {
                    if (adhoc.Adhoc.MOZ[idm].LV3.getElementAt(k).equals(adhoc.Adhoc.MOZ[idm].Tintersec.getElementAt(i))) {
                        sim3 = (double) w3 * adhoc.Adhoc.MOZ[idm].D3[k];
                        //System.out.println("sim3 de véhicule : "+adhoc.Adhoc.MOZ[idm].ident+"  est "+sim3);
                    }
                }
                //System.out.println("-------------------------------------------------------------------------------");
                adhoc.Adhoc.MOZ[idm].sim = (double) ((double) adhoc.Adhoc.MOZ[idm].sim + (double) (Math.ceil((DifT + 0.5) * 1000) / 1000) / (double) ((Math.ceil(sim1 + sim2 + sim3 + 0.5) * 1000) / 1000));

                   // System.err.println("V"+adhoc.Adhoc.MOZ[idm].ident+/*" DifT : "+DifT+"   ===========================>  */" Sim : "+adhoc.Adhoc.MOZ[idm].sim);
            }
                //---------------------------------------------------------

            //---------------------------------------------------------
        }
    }

    //----------------------------------------------------------------------------
    public static DefaultListModel Intersection(int idm) 
    {
        DefaultListModel tmp = new DefaultListModel();
        DefaultListModel tmp1 = new DefaultListModel();
        boolean trv = false;

        for (int i = 0; i < adhoc.Adhoc.MOZ[idm].LV1.getSize(); i++) 
        {
            trv = false;
            for (int j = 0; j < adhoc.Adhoc.MOZ[idm].LV2.getSize(); j++) {
                if (adhoc.Adhoc.MOZ[idm].LV1.getElementAt(i).equals(adhoc.Adhoc.MOZ[idm].LV2.getElementAt(j))) {
                    trv = true;
                }
            }
            if (trv == true) {
                tmp.addElement(adhoc.Adhoc.MOZ[idm].LV1.getElementAt(i));
            }
        }
        //-------------------------------
        for (int i = 0; i < tmp.getSize(); i++) 
        {
            trv = false;
            for (int j = 0; j < adhoc.Adhoc.MOZ[idm].LV3.getSize(); j++) {
                if (tmp.getElementAt(i).equals(adhoc.Adhoc.MOZ[idm].LV3.getElementAt(j))) {
                    trv = true;
                }
            }
            if (trv == true) {
                tmp1.addElement(tmp.getElementAt(i));
            }
        }
                        //------------------------------

        return tmp1;
    }

    //----------------------------------------------------------------------------
    //----------------------------------------------------------------------------

    public static DefaultListModel CommunElement(DefaultListModel L1, DefaultListModel L2) {
        DefaultListModel tmp = new DefaultListModel();
        boolean trv = false;

        for (int i = 0; i < L1.getSize(); i++) {
            trv = false;
            for (int j = 0; j < L2.getSize(); j++) {
                if (L1.getElementAt(i).equals(L2.getElementAt(j))) {
                    trv = true;
                }
            }
            if (trv == true) {
                tmp.addElement(L1.getElementAt(i));
            }
        }

        return tmp;
    }

    //----------------------------------------------------------------------------

    public static void CollectEletMax(double vect[][]) {
        double xmax, dist;
        int idmoz, idmoz1, idmoz2, id, idmax;

        for (int i = 0; i < vect.length; i++) {
            xmax = vect[i][0];
            idmoz = (int) vect[i][1];

            idmoz = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(idmoz, Algorithms.Mozo.Tpos);
            dist = 0;
            idmax = idmoz;
            if (idmoz != -1) {
                System.out.println("Collecte des élément max Dist : idmoz : " + idmoz + "/" + adhoc.Adhoc.MOZ.length);
                //----------------------------------------------------
                for (int j = 0; j < adhoc.Adhoc.MOZ[idmoz].LV3.getSize(); j++) {
                    id = Integer.parseInt(adhoc.Adhoc.MOZ[idmoz].LV3.getElementAt(j).toString());
                    id = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(id, Algorithms.Mozo.Tpos);

                    if (xmax - adhoc.Adhoc.MOZ[id].posXmoz > dist) {
                        dist = xmax - adhoc.Adhoc.MOZ[id].posXmoz;
                        idmax = adhoc.Adhoc.MOZ[id].idMoz;
                    }
                }
                //----------------------------------------------------
                adhoc.Adhoc.MOZ[idmoz].EletMaxDist = idmax;
                System.out.println("L'élément qui a le max Dist pour " + adhoc.Adhoc.MOZ[idmoz].ident + " est " + adhoc.Adhoc.MOZ[idmoz].EletMaxDist);
            }
            //----------------------------------------------------
        }
        System.out.println("Fin de collecte des élément max Dist");

        DefaultListModel tmp = new DefaultListModel();
        double simmax;
        int idSim = -1, idelet;

        for (int i = 0; i < vect.length - 1; i++) {
            idmoz1 = (int) vect[i][1];
            idmoz1 = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(idmoz1, Algorithms.Mozo.Tpos);
            idmoz2 = (int) vect[i + 1][1];
            idmoz2 = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(idmoz2, Algorithms.Mozo.Tpos);

            simmax = 0;
            if ((idmoz1 != -1) && (idmoz2 != -1)) {
                System.out.println("Calcul d'intersection pour " + adhoc.Adhoc.MOZ[idmoz1].ident + " et " + adhoc.Adhoc.MOZ[idmoz2].ident);
                tmp = CommunElement(adhoc.Adhoc.MOZ[idmoz1].LV3, adhoc.Adhoc.MOZ[idmoz2].LV3);
                System.out.println("Fin de Calcul d'intersection");
                //-------------------------------
                for (int j = 0; j < tmp.getSize(); j++) {

                    idelet = Integer.parseInt(tmp.getElementAt(j).toString());
                    idelet = vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(idelet, Tpos);

                    if (adhoc.Adhoc.MOZ[idelet].sim > simmax) {
                        simmax = adhoc.Adhoc.MOZ[idelet].sim;
                        idSim = adhoc.Adhoc.MOZ[idelet].idMoz;
                    }
                }
                adhoc.Adhoc.MOZ[idmoz1].RelayNode = idSim;
                //------------------------------
                System.out.println("Noeud relais pour " + adhoc.Adhoc.MOZ[idmoz1].ident + " et le suivant est " + adhoc.Adhoc.MOZ[idmoz1].RelayNode);
            }

        }

                    //----------------------------------------------------------
    }

    //----------------------------------------------------------------------------

    public static class ConstructCLVTree {

        public int IdTeteCLV;
        public int IdXTeteCLV;
        public int nbFils;
        public DefaultListModel Fils;

        public static void ConstructCLVTree(double vect[][]) {
            for (int i = 0; i < vect.length; i++) {
                ConstructCLVTree clv = new ConstructCLVTree();
                clv.IdTeteCLV = (int) vect[i][1];
                clv.IdXTeteCLV = i + 1; //index debute à 1
                clv.nbFils = 0;
                clv.Fils = new DefaultListModel();
                adhoc.Adhoc.CLV[i] = clv;

            }
        }

    }
    
   

    //--------------------------------------------------------

    public static int EstMembre(int idNode, int idHead) {
        int b = -1;
        if (adhoc.Adhoc.CLV[idHead].IdTeteCLV == idNode) {
            b = 0;
        } else {
            for (int i = 0; i < adhoc.Adhoc.CLV[idHead].nbFils; i++) {
                if (Integer.parseInt(adhoc.Adhoc.CLV[idHead].Fils.getElementAt(i).toString()) == idNode) {
                    b = 1;
                }
            }
        }
        System.out.println("idNode : " + idNode + "     idHead : " + idHead + "    ====>  b : " + b);
        return b;
    }

    //--------------------------------------------------------

    public static int PositionTete(int tete) {
        int position = -1;
        for (int i = 0; i < adhoc.Adhoc.CLV.length; i++) {
            if (adhoc.Adhoc.CLV[i].IdTeteCLV == tete) {
                position = i;
            }
        }
        return position;

    }

    //--------------------------------------------------------

    public static int GetIndiceTete(int tete) {
        int indice = 0;
        for (int i = 0; i < adhoc.Adhoc.CLV.length; i++) {
            if (adhoc.Adhoc.CLV[i].IdTeteCLV == tete) {
                indice = i;
            }
        }
        return indice;

    }

    //--------------------------------------------------------

    public static void RoutingMozo(DefaultListModel path) {

        if (path.getSize() == 0) {
            System.out.println("Premiére entrée Taille de path : " + path.getSize());
            //--------------------------------
            for (int i = 0; i < adhoc.Adhoc.CLV.length; i++) {
                if (EstMembre(adhoc.Adhoc.IdSrc, i) == 0) {
                    path.addElement("" + adhoc.Adhoc.IdSrc);

                } else if (EstMembre(adhoc.Adhoc.IdSrc, i) == 1) {
                    path.addElement("" + adhoc.Adhoc.IdSrc);
                    path.addElement("" + adhoc.Adhoc.CLV[i].IdTeteCLV);
                }
            }

            System.out.println("Premiére sortie Taille de path : " + path.getSize());
            //--------------------------------
        } else {
            System.out.println("Deuxiéme entrée Taille de path : " + path.getSize());
            //--------------------------------------
            int tete = Integer.parseInt(path.getElementAt(path.getSize() - 1).toString());

            if (EstMembre(adhoc.Adhoc.IdCbl, GetIndiceTete(tete)) == -1) {
                //--------------------------------------------
                int position = PositionTete(tete);
                double MinDist;
                int indice;

                if (position == adhoc.Adhoc.CLV.length - 1) {
                  //-----------------------------------------------------

                    if (EstMembre(adhoc.Adhoc.IdCbl, position) == 0) {
                        path.addElement("" + adhoc.Adhoc.IdCbl);

                    } else if (EstMembre(adhoc.Adhoc.IdCbl, position) == 1) {
                        path.addElement("" + adhoc.Adhoc.IdCbl);
                        //path.addElement(""+adhoc.Adhoc.CLV[position].IdTeteCLV);
                    }
                    arret = true;
                    //-----------------------------------------------------
                } else {
                    MinDist = vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(GetIndiceTete(tete), adhoc.Adhoc.CLV[position + 1].IdTeteCLV);
                    indice = adhoc.Adhoc.CLV[position + 1].IdTeteCLV;
                    //---------------------------------------------
                    for (int f = 0; f < adhoc.Adhoc.CLV[position + 1].nbFils; f++) {
                        if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(GetIndiceTete(tete), Integer.parseInt(adhoc.Adhoc.CLV[position + 1].Fils.getElementAt(f).toString())) < MinDist) {
                            MinDist = vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(GetIndiceTete(tete), Integer.parseInt(adhoc.Adhoc.CLV[position + 1].Fils.getElementAt(f).toString()));
                            indice = Integer.parseInt(adhoc.Adhoc.CLV[position + 1].Fils.getElementAt(f).toString());
                        }
                    }
                    //--------------------------------------------
                    if (EstMembre(indice, position + 1) == 0) {
                        path.addElement("" + indice);

                    } else if (EstMembre(indice, position + 1) == 1) {
                        path.addElement("" + indice);
                        path.addElement("" + adhoc.Adhoc.CLV[position + 1].IdTeteCLV);
                    }
                    //--------------------------------------------
                }

            } else {
                if (EstMembre(adhoc.Adhoc.IdCbl, GetIndiceTete(tete)) == 0) {
                    //path.addElement(""+adhoc.Adhoc.IdCbl);
                    arret = true;
                } else {
                    // if(path.getSize()>2) path.addElement(""+tete);
                    path.addElement("" + adhoc.Adhoc.IdCbl);
                    arret = true;
                }

                //---------------------------------------------------------------
                System.out.println("Le chemin entre : " + adhoc.Adhoc.IdSrc + "  et " + adhoc.Adhoc.IdCbl + " est le suivant : ");
                for (int l = 0; l < path.getSize(); l++) {
                    System.out.print(path.getElementAt(l) + " ---> ");
                }
                System.out.println();
                //---------------------------------------------------------------
                for (int l = 0; l < path.getSize(); l++) {
                    if (Integer.parseInt(path.getElementAt(l).toString()) == adhoc.Adhoc.IdCbl) {
                        arret = true;
                    }
                }
            }

           //----------------------------------------
            //----------------------------------------
        }
       //-----------------------------------

        //-----------------------------------
        while (arret == false) {
            RoutingMozo(path);
        }

    }

    //----------------------------------------------------------------------------

    public static void ChoixSourceCible(int distlimite) {
        boolean b = false;
        if (adhoc.Adhoc.MOZ.length > 10)//
        {
            while (b == false) {
                do {
                    adhoc.Adhoc.IdSrc = rnd.nextInt(adhoc.Adhoc.MOZ.length);
                    adhoc.Adhoc.IdCbl = rnd.nextInt(adhoc.Adhoc.MOZ.length);

                } while (adhoc.Adhoc.IdSrc == adhoc.Adhoc.IdCbl);

                if (adhoc.Adhoc.MOZ[adhoc.Adhoc.IdCbl].posXmoz > adhoc.Adhoc.MOZ[adhoc.Adhoc.IdSrc].posXmoz) {
                    if (vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(adhoc.Adhoc.MOZ[adhoc.Adhoc.IdCbl].ident, adhoc.Adhoc.MOZ[adhoc.Adhoc.IdSrc].ident) > distlimite) {
                        b = true;
                    }
                }

            }

            System.out.println(" Source ===> " + adhoc.Adhoc.MOZ[adhoc.Adhoc.IdSrc].ident + "   Cible ===>" + adhoc.Adhoc.MOZ[adhoc.Adhoc.IdCbl].ident + "  Taille de MOZ : " + adhoc.Adhoc.MOZ.length);
        }

    }

    //----------------------------------------------------------------------------
    //----------------------------------------------------------------------------

    public static void ChoixSourceCible(int Xmin, int Xmax, int vact[]) {

        DefaultListModel tmp = new DefaultListModel();
        int elt;
        for (int i = 0; i < vact.length; i++) {
            elt = vact[i];
            if ((vanetsim.scenario.Vehicle.TV[elt].posx >= Xmin) && (vanetsim.scenario.Vehicle.TV[elt].posx <= Xmax)) {
                tmp.addElement("" + elt);

            }
        }
        if (!tmp.isEmpty()) {
            int tab[][] = new int[tmp.getSize()][2];
            for (int j = 0; j < tmp.getSize(); j++) {
                tab[j][1] = Integer.parseInt(tmp.getElementAt(j).toString());
                tab[j][0] = vanetsim.scenario.Vehicle.TV[tab[j][1]].posx;
            }
            adhoc.Adhoc.Tribull(tab, tab.length - 1);
            //------------------------
            adhoc.Adhoc.IdCbl = tab[tab.length - 1][1];
            adhoc.Adhoc.IdSrc = tab[0][1];
            System.out.println(" Source ===> " + adhoc.Adhoc.IdSrc + "   Cible ===> " + adhoc.Adhoc.IdCbl + "  Nos postions : " + tab[0][0] + "  et " + tab[tab.length - 1][0]);
        }
        else
        {
        System.out.println(" il n y a aucun aucun noeud dans la zone choisie");
        }  

//                adhoc.Adhoc.IdSrc = tab[0][1];
//                adhoc.Adhoc.IdCbl = tab[tab.length-1][1];
        

    }
    
//------------------------------------------------------------------------------
public static void CalculateStateDuration_MOZO(){
        
    for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
    {
        if (vanetsim.scenario.Vehicle.TV[v].etat == "Actif")
        if(!vanetsim.scenario.Vehicle.TV[v].type_noeud_MOZO.equals(vanetsim.scenario.Vehicle.TV[v].ETAT_MOZO.get(vanetsim.scenario.Vehicle.TV[v].ETAT_MOZO.size()-1)))
        {   
            vanetsim.scenario.Vehicle.TV[v].transfin_MOZO = System.currentTimeMillis();
            vanetsim.scenario.Vehicle.TV[v].DUREE_MOZO.add(vanetsim.scenario.Vehicle.TV[v].transfin_MOZO - vanetsim.scenario.Vehicle.TV[v].transdeb_MOZO);
            vanetsim.scenario.Vehicle.TV[v].transdeb_MOZO = vanetsim.scenario.Vehicle.TV[v].transfin_MOZO;
            vanetsim.scenario.Vehicle.TV[v].ETAT_MOZO.add(""+vanetsim.scenario.Vehicle.TV[v].type_noeud_MOZO);   
            System.out.println("ETAT_MOZO du noeud "+vanetsim.scenario.Vehicle.TV[v].idVeh+" est "
            +vanetsim.scenario.Vehicle.TV[v].ETAT_MOZO.get(vanetsim.scenario.Vehicle.TV[v].ETAT_MOZO.size()-1)+" pendant (DUREE_MOZO)"
            +vanetsim.scenario.Vehicle.TV[v].DUREE_MOZO.get(vanetsim.scenario.Vehicle.TV[v].DUREE_MOZO.size()-1)+" ms.");
            
        }     
    }     
}
  
    //-------------------
}
