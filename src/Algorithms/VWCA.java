/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithms;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author saliha-benkerdagh
 */
public class VWCA {
 public static DefaultListModel BL = new DefaultListModel();
 public static DefaultListModel WL = new DefaultListModel();
 
 //-----------------------------pour les stats----------------------------
 public static DefaultTableModel ST4 = new DefaultTableModel();
 public static DefaultTableModel STa4 = new DefaultTableModel();
//---------------------------------------------------------
 
 
 
 
 static double Nt,Beta;
 static int dist=0;
 
//----------------------------------------------------------
public static void NewVoisin(int idv,int vact[] )/// c bon
{
            vanetsim.scenario.Vehicle.TV[idv].nbvoisin =0; 
            vanetsim.scenario.Vehicle.TV[idv].VS.removeAllElements();
            
            
            for(int i=0;i<vact.length;i++)
            {
                if(vact[i]!=idv)
                {
                  if(vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(idv, vact[i])<=vanetsim.scenario.Vehicle.TV[idv].Rnew)
                  {
                    vanetsim.scenario.Vehicle.TV[idv].nbvoisin++;
                    vanetsim.scenario.Vehicle.TV[idv].VS.addElement(vact[i]);
                  }
                }
            }
}
//--------------------ExecutingAatrAlgorithm----------------
public static void ExecutingAATRalgorithm(int vact[])/// c bon
{
            for(int i=0;i<vact.length;i++)
           {
               
               
                if(vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin >= Nt)vanetsim.scenario.Vehicle.TV[vact[i]].Ta =1;
                else vanetsim.scenario.Vehicle.TV[vact[i]].Ta = 0;
               
                if(vanetsim.scenario.Vehicle.TV[vact[i]].Ta == 1) vanetsim.scenario.Vehicle.TV[vact[i]].Rmin =100;
                else vanetsim.scenario.Vehicle.TV[vact[i]].Rmin =300; 
 
                     
                //--Calcul nb Voisin pour Rmin + Liste de voisin---------
                NewVoisin(vact[i],vact );
                
                //vanetsim.scenario.Vehicle.TV[vact[i]].TpsPro = vanetsim.scenario.Vehicle.TV[vact[i]].Rmin/adhoc.Adhoc.sigpropag;
                
                
                  boolean arret =false;
                 if(vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin==0)
                 {
                     
                do{ 
                    if(vanetsim.scenario.Vehicle.TV[vact[i]].Rnew < vanetsim.scenario.Vehicle.TV[vact[i]].Rmax)
                    {
                    double B = Math.abs(vanetsim.scenario.Vehicle.TV[vact[i]].Rmin - vanetsim.scenario.Vehicle.TV[vact[i]].Rnew);
                    double supp = (double)B * (double)adhoc.Adhoc.density;
                    //System.out.println("Supp : "+supp); 
                    vanetsim.scenario.Vehicle.TV[vact[i]].Rnew = vanetsim.scenario.Vehicle.TV[vact[i]].Rnew + supp; 
                    //System.out.println("Nd : "+vact[i]+"  B  == "+B+"    Rnew : "+vanetsim.scenario.Vehicle.TV[vact[i]].Rnew+"/"+vanetsim.scenario.Vehicle.TV[vact[i]].Rmax);
                    NewVoisin(vact[i],vact );//Compute number of neigbour
                    
                    if(vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin==0) {arret=true; break;}
                    
                    }
                    else
                    {
                    vanetsim.scenario.Vehicle.TV[vact[i]].Rnew = vanetsim.scenario.Vehicle.TV[vact[i]].Rmax;//Finding no neighbour
                    NewVoisin(vact[i],vact );
                        if(vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin==0)
                        {
                            vanetsim.scenario.Vehicle.TV[vact[i]].est_ch_VWCA   = true;
                            vanetsim.scenario.Vehicle.TV[vact[i]].est_mn_VWCA   = false;
                            vanetsim.scenario.Vehicle.TV[vact[i]].est_init_VWCA = false;
                            
                            vanetsim.scenario.Vehicle.TV[vact[i]].myCH_VWCA     = vact[i];
                            
                        }
                    
                        
                        arret= true;break;
                    }
                   }while((vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin==0) || (arret == false));
                 }
                /******************************Fin AATR******************************/
                /************************ Fin added by Saliha************************/
                //------------------------------------------------------              
           }
}
//--------------------CalculatingEntropy----------------
public static double CalculatingEntropy(int idv)/// c bon
{
 double entropy=0.0;
 double TE[][] = new double[vanetsim.scenario.Vehicle.TV[idv].VS.getSize()][2];
 int elet;
            for(int i=0;i<vanetsim.scenario.Vehicle.TV[idv].VS.getSize()/*TE.length*/;i++)
            {
                if(i<TE.length)
                {
               elet     = Integer.parseInt(vanetsim.scenario.Vehicle.TV[idv].VS.getElementAt(i).toString().trim());
               //System.out.println("idv : "+idv+"     elet : "+elet+"    ");
               TE[i][0] = vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(idv, elet);
               
               TE[i][1] = TE[i][0]*(Math.log(TE[i][0])/Math.log(2));
               entropy  = entropy + TE[i][1];
                }
            }
entropy  = (double)entropy/(double)(Math.log(TE.length+1)/Math.log(2));
 //System.out.println("Entropy du noeud : "+idv+"  est : "+entropy);
 return entropy;
}
//--------------------CalculatingDistMoyenne----------------
public static double CalculatingDistMoyenne(int idv)/// c bon
{
 double distMoy=0.0;
 
 int elet;
            for(int i=0;i<vanetsim.scenario.Vehicle.TV[idv].VS.getSize();i++)
            {
               elet     = Integer.parseInt(vanetsim.scenario.Vehicle.TV[idv].VS.getElementAt(i).toString());
               distMoy += vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(idv, elet);
            }
distMoy  = distMoy/vanetsim.scenario.Vehicle.TV[idv].VS.getSize();
 
 
 return distMoy;
}
//--------------------CalculatingWeightClusteringValue----------------
public static double CalculatingWeightClusteringValue(int idv)/// c bon
{
                double poids=0.0;
                poids = adhoc.Adhoc.vw1*vanetsim.scenario.Vehicle.TV[idv].DistrustValue - 
                        adhoc.Adhoc.vw2*vanetsim.scenario.Vehicle.TV[idv].Entropy       -
                        adhoc.Adhoc.vw3*vanetsim.scenario.Vehicle.TV[idv].nbvoisin      -
                        adhoc.Adhoc.vw4*vanetsim.scenario.Vehicle.TV[idv].Dmoy ; 
   return poids;
}
//--------------------MmvAlgorithm----------------
public static void MmvAlgorithm(int vact[])/// c bon
{
                 ThresholdSigma(vact); 
                 DetermineVerifier(vact);
                 DetermineAbnormality(vact);
                 ModifyingDistrustValue(vact);
                 UpdateWhiteBlackList(vact);
                //-------------------------------------------      
}
//-------------------Determine the Threshold--------------
public static void ThresholdSigma(int vact[])/// c bon
{
       BL.removeAllElements();
       WL.removeAllElements();
           for(int i=0;i<vact.length;i++)
           {
                   if(vanetsim.scenario.Vehicle.TV[vact[i]].DistrustValue < adhoc.Adhoc.sig)
                   {
                     WL.addElement(""+vact[i]);
                   }else BL.addElement(""+vact[i]);
           }
}
//--------------------Determine Verifier----------------------------
public static void DetermineVerifier(int vact[])/// c bon
{
           int voisin=-1;
           for(int i=0;i<vact.length;i++)
           {
                    vanetsim.scenario.Vehicle.TV[vact[i]].VerifL.removeAllElements();
                    
                    //int ListV[] = new int[vanetsim.scenario.Vehicle.TV[vact[i]].VS.getSize()];
	            //ListV = vanetsim.scenario.Vehicle.TV[vact[i]].ListVoisin(i,vact,vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin);
                   for(int v=0;v<vanetsim.scenario.Vehicle.TV[vact[i]].VS.getSize();v++)
                   {
                      voisin = Integer.parseInt(vanetsim.scenario.Vehicle.TV[vact[i]].VS.getElementAt(v).toString());// ListV[v];
                      if(vanetsim.scenario.Vehicle.TV[voisin].DistrustValue <= vanetsim.scenario.Vehicle.TV[vact[i]].DistrustValue)
                      vanetsim.scenario.Vehicle.TV[vact[i]].VerifL.addElement(""+voisin);
                   }
           }   
}
//------------------------Determine Abnormality---------------------------------
public static void DetermineAbnormality(int vact[])/// c bon
{
           int nd;
           double Ds;
           for(int i=0;i<vact.length;i++)
           {
                    vanetsim.scenario.Vehicle.TV[vact[i]].Abnormality = 0.0;
                    for(int v=0;v<vanetsim.scenario.Vehicle.TV[vact[i]].VerifL.getSize();v++)
                   {
                      nd = Integer.parseInt(vanetsim.scenario.Vehicle.TV[vact[i]].VerifL.getElementAt(v).toString());
                      Ds = Math.abs(vanetsim.scenario.Vehicle.TV[vact[i]].vitesse - vanetsim.scenario.Vehicle.TV[nd].vitesse);
                      
                      vanetsim.scenario.Vehicle.TV[vact[i]].Abnormality += adhoc.Adhoc.e1*(1.0/vanetsim.scenario.Vehicle.TV[nd].DistrustValue) + adhoc.Adhoc.e2*(1.0/(Ds+1));
                   }
           }   
}
//-------------------------Modifying Distrust Value-----------------------------
public static void ModifyingDistrustValue(int vact[])/// c bon
{
            for(int i=0;i<vact.length;i++)
           {
              vanetsim.scenario.Vehicle.TV[vact[i]].DistrustValue += Math.exp(vanetsim.scenario.Vehicle.TV[vact[i]].Abnormality);
           }    
}
//------------------------------------------------------------------------------
public static int GetPositionInBlackList(int idv)/// c bon
{
  int pos = -1;
  for(int i=0;i<BL.getSize();i++)
  {
       if(BL.getElementAt(i).equals(""+idv)) pos=i;
  }
  return pos;
}
//------------------------------------------------------------------------------
public static int GetPositionInWhiteList(int idv)/// c bon
{
  int pos = -1;
  for(int i=0;i<WL.getSize();i++)
  {
       if(WL.getElementAt(i).equals(""+idv)) pos=i;
  }
  return pos;
}
//--------------------------UpdateWhite and Black List--------------------------
public static void UpdateWhiteBlackList(int vact[])/// c bon
{
           int position;
           for(int i=0;i<vact.length;i++)
           {
                  position = GetPositionInBlackList(vact[i]);
                  if(position>-1)//je suis dans la black liste
                  {
                       if(vanetsim.scenario.Vehicle.TV[vact[i]].DistrustValue <= adhoc.Adhoc.sig)
                       {
                           WL.addElement(BL.getElementAt(position));
                           BL.removeElementAt(position);
                       }
                  
                  }else //je suis dans la white liste
                  {
                       position = GetPositionInWhiteList(vact[i]);
                       if(vanetsim.scenario.Vehicle.TV[vact[i]].DistrustValue <= adhoc.Adhoc.sig)
                       {
                           BL.addElement(WL.getElementAt(position));
                           WL.removeElementAt(position);
                       }
                  }
           }       
}
//------------------------------------------------------------------------------
 	public static void  propriete(int vact[],DefaultTableModel PR3)
	{
            if(PR3.getColumnCount()==0)
            {
            PR3.setColumnCount(0);
            PR3.addColumn("N° Véhicule");//0
            PR3.addColumn("Type");//1
            PR3.addColumn("Nbr. Voisin");//2
            PR3.addColumn("DisMoy");//3
            PR3.addColumn("Entropy (Hv)");//4
            PR3.addColumn("Weight(Wi)");//5
            PR3.addColumn("My CH");//6
            }
	   //----------------------------------
            PR3.setRowCount(vact.length);
             
	   //----------------------------------

		 for(int i=0;i<vact.length;i++)
		 {
                   
		    //PR2.setValueAt("V"+vact[i], i, 0); 
                    
                    PR3.setValueAt("V"+(vanetsim.scenario.Vehicle.TV[vact[i]].idVeh), i, 0);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].type_noeud_VWCA, i, 1);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].nbvoisin, i, 2);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].Dmoy, i, 3);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].Entropy, i, 4);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].Weight, i, 5);
                    PR3.setValueAt(vanetsim.scenario.Vehicle.TV[vact[i]].myCH_VWCA, i, 6);
	   //------------------------
	         }
        }
 //-----------------------------------------------------------------------------
   public static void Clustering(int vact[],DefaultTableModel GR)
	{
            if(GR.getColumnCount()==0)
            {
            GR.setColumnCount(0);
            GR.addColumn("N° Groupe");//0
            GR.addColumn("Leader");//1
            GR.addColumn("Degré de cluster");//2
            GR.addColumn("Eléments");//3
            }
	   //----------------------------------
           
           DefaultListModel tmp = new DefaultListModel();
           for(int i=0;i<vact.length;i++)
           { 
                 if(vanetsim.scenario.Vehicle.TV[vact[i]].est_ch_VWCA==true)  
                 {
                     tmp.addElement(vact[i]);
                     //System.out.println("New ch est : "+vact[i]);
                 }
           }
           GR.setRowCount(tmp.getSize());
           int nb;
           String elet;
           for(int i=0;i<tmp.getSize();i++)
           {
                 GR.setValueAt(""+(i+1), i, 0);
                 GR.setValueAt("V"+tmp.getElementAt(i), i, 1);
                    //-------------------
                    nb=0;
                    elet="";
                    for(int j=0;j<vact.length;j++)
                    {
                         if(vanetsim.scenario.Vehicle.TV[vact[j]].myCH_VWCA == Integer.parseInt(tmp.getElementAt(i).toString()))
                         {
                            nb++;
                            elet=elet+"V"+vact[j]+",";
                         }
                    }
                 GR.setValueAt(""+nb, i, 2);
                 GR.setValueAt(elet, i, 3);
                    //-------------------
           }
            
 
        }
//------------------------------------------------------------------------------
   //------------------------------------------------------------------------------
public static void CalculateStateDuration_VWCA(){
        
    for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
    {
        if (vanetsim.scenario.Vehicle.TV[v].etat == "Actif")
        if(!vanetsim.scenario.Vehicle.TV[v].type_noeud_VWCA.equals(vanetsim.scenario.Vehicle.TV[v].ETAT_VWCA.get(vanetsim.scenario.Vehicle.TV[v].ETAT_VWCA.size()-1)))
        {   
            vanetsim.scenario.Vehicle.TV[v].transfin_VWCA = System.currentTimeMillis();
            vanetsim.scenario.Vehicle.TV[v].DUREE_VWCA.add(vanetsim.scenario.Vehicle.TV[v].transfin_VWCA - vanetsim.scenario.Vehicle.TV[v].transdeb_VWCA);
            vanetsim.scenario.Vehicle.TV[v].transdeb_VWCA = vanetsim.scenario.Vehicle.TV[v].transfin_VWCA;
            vanetsim.scenario.Vehicle.TV[v].ETAT_VWCA.add(""+vanetsim.scenario.Vehicle.TV[v].type_noeud_VWCA);   
            System.out.println("ETAT_VWCA du noeud "+vanetsim.scenario.Vehicle.TV[v].idVeh+" est "
            +vanetsim.scenario.Vehicle.TV[v].ETAT_VWCA.get(vanetsim.scenario.Vehicle.TV[v].ETAT_VWCA.size()-1)+" pendant (DUREE_VWCA)"
            +vanetsim.scenario.Vehicle.TV[v].DUREE_VWCA.get(vanetsim.scenario.Vehicle.TV[v].DUREE_VWCA.size()-1)+" ms.");
            
        }     
    }     
}
  
    //-------------------
 
}
