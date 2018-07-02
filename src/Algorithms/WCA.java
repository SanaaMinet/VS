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
public class WCA {
 int idCluster;
DefaultListModel LD;
static Cluster TCluster[];
static long Tdep;
static int Nbcluster;

 //-----------------------------pour les stats----------------------------
 public static DefaultTableModel ST3 = new DefaultTableModel();
 public static DefaultTableModel STa3 = new DefaultTableModel();
//---------------------------------------------------------

static double precision(double val,int prc)
	{
		 double vl = val * Math.pow(10,prc);
		 vl = Math.ceil(vl+0.5);
		 vl = vl*Math.pow(10,-prc);
		 return vl;
	}
 
//----------------------------Difference Degree---------------------------------
  public static int DifferenceDegree(int idn, int K)   
 {
      return Math.abs(K - vanetsim.scenario.Vehicle.TV[idn].nbvoisin);
 }
//------------------------------------------------------------------------------
 //----------------------------Somme de Dv ---------------------------------
  public static double SommeDistance(int idn, int K)   
 {
      double dv =0.0;
      int indice;
      for(int i=0;i<vanetsim.scenario.Vehicle.TV2[idn].nbvoisin;i++)
      {
           if(i<vanetsim.scenario.Vehicle.TV2[idn].VS.getSize()) 
           {
           indice = Integer.parseInt(vanetsim.scenario.Vehicle.TV2[idn].VS.getElementAt(i).toString());
           dv = dv + vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(idn,i);
           }
      }
     
      return dv;
 }
//------------------------------------------------------------------------------ 
  public static void VitesseRelative(long Tcur, long Tdep, int vact[])
  {
           long T = (Tcur - Tdep);
           for(int i=0;i<vact.length;i++)
           {
              vanetsim.scenario.Vehicle.TV2[vact[i]].Mv = Math.sqrt(Math.pow(vanetsim.scenario.Vehicle.TV[vact[i]].posx - vanetsim.scenario.Vehicle.TV2[vact[i]].Xdt,2) + 
                                    Math.pow(vanetsim.scenario.Vehicle.TV[vact[i]].posy - vanetsim.scenario.Vehicle.TV2[vact[i]].Ydt,2) )/(T);
              
              vanetsim.scenario.Vehicle.TV2[vact[i]].Xdt =  vanetsim.scenario.Vehicle.TV[vact[i]].posx;
              vanetsim.scenario.Vehicle.TV2[vact[i]].Ydt =  vanetsim.scenario.Vehicle.TV[vact[i]].posy;
           }
  }
 //-----------------------------------------------------------------------------
public static void  metrique_mobilite(DefaultTableModel MB,int vact[])
{
    if(MB.getColumnCount()==0)
    {
           MB.setColumnCount(0);
           MB.addColumn("Véhicules");
           MB.addColumn("Liens connectés");
           MB.addColumn("Liens en rupture");          
    }  
            MB.setRowCount(vact.length);
    
 //System.out.println(" ---- aaa --- ");
		 for(int i=0;i<vact.length;i++)
		 {
                  MB.setValueAt("V"+(vanetsim.scenario.Vehicle.TV2[vact[i]].idVeh), i, 0);
                 if(vanetsim.scenario.Vehicle.TV2[vact[i]].lcav==null) MB.setValueAt("0", i, 1);
                 else MB.setValueAt(""+vanetsim.scenario.Vehicle.TV2[vact[i]].lcav.length, i, 1);
                  MB.setValueAt(""+vanetsim.scenario.Vehicle.TV2[vact[i]].Lrep, i, 2);

	         }
 //System.out.println(" ---- bbb --- "); 
	   //------------------------
}

 	public static void  propriete(double inst,int vact[],DefaultTableModel PR2)
	{
           // System.out.println("================= 1111 ====================");
            if(PR2.getColumnCount()==0)
            {
            PR2.setColumnCount(0);
            PR2.addColumn("N° Véhicule");//0
            PR2.addColumn("Type");//1
            PR2.addColumn("Nbr. Voisin");//2
            PR2.addColumn("Delta");//3
            PR2.addColumn("Dv");//4
            PR2.addColumn("Mv(Mob. relative)");//5
            PR2.addColumn("Wi");//6
            }
	   //----------------------------------
            PR2.setRowCount(vact.length);
          // System.out.println("================= 2222 ===================="); 
	   //----------------------------------
		 int C=0;
		// int cg=-1;
		 for(int i=0;i<vact.length;i++)
		 {
                    PR2.setValueAt("V"+(vanetsim.scenario.Vehicle.TV2[vact[i]].idVeh), i, 0);
                    PR2.setValueAt(vanetsim.scenario.Vehicle.TV2[vact[i]].type_WCA_noeud, i, 1);
                    PR2.setValueAt(vanetsim.scenario.Vehicle.TV2[vact[i]].nbvoisin, i, 2);//verif
                    PR2.setValueAt(DifferenceDegree(vact[i], adhoc.Adhoc.K)   , i, 3);
                    PR2.setValueAt(precision(SommeDistance(vact[i], adhoc.Adhoc.K),4) , i, 4);
                   
                   VitesseRelative(System.currentTimeMillis(),adhoc.Adhoc.Tdep,vact);
                   
                   PR2.setValueAt(precision(vanetsim.scenario.Vehicle.TV2[vact[i]].Mv,8), i, 5);
                   PR2.setValueAt( precision(vanetsim.scenario.Vehicle.TV2[vact[i]].Wi,5), i, 6);
 
                   
	   //------------------------
	         }
        //System.out.println("================= 3333 ====================");
        }
 //-----------------------------------------------------------------------------
  public static double Fct_Wi(int K, int nd,double w1, double w2, double w3 )
   {
      return  w1*DifferenceDegree(nd,K) + w2*vanetsim.scenario.Vehicle.TV2[nd].Dv + w3*vanetsim.scenario.Vehicle.TV2[nd].Mv;
   }      
//------------------------------------------------------------------------------   
  public static class Cluster {

    
int idCluster;
DefaultListModel LD;
public  static void CreationMaxCluster(int NbCluster)
{
     TCluster = new Cluster[NbCluster];
     for(int i=0;i<NbCluster;i++)
     {
             Cluster cl = new Cluster();
             cl.idCluster = i;
             cl.LD     = new DefaultListModel();
             TCluster[i] = cl;
     }
     Nbcluster = 0;
}
public static void Initialisation(int vact[])
{
   for(int i=0;i<vact.length;i++) 
   {
       vanetsim.scenario.Vehicle.TV2[i].situation = false;
       
       vanetsim.scenario.Vehicle.TV2[i].est_WCA_mn = false;
       vanetsim.scenario.Vehicle.TV2[i].est_WCA_ch = false;
       vanetsim.scenario.Vehicle.TV2[i].est_WCA_init = true;
   }
   for(int i=0;i< Nbcluster ;i++) {TCluster[i].LD.removeAllElements();}
   Nbcluster = 0;
 
   
   
}
public static  void FormationClusters(DefaultTableModel cl, int vact[], int k)
{
 double minWi = Math.pow(10,10);
 int leader   = -1;
 
 if(vact.length>k)
 { 
   for(int i=0;i<vact.length;i++) 
   if(vanetsim.scenario.Vehicle.TV2[vact[i]].situation==false)// etat.equals("Actif"))
          {
               if(vanetsim.scenario.Vehicle.TV2[vact[i]].Wi < minWi)
               {
                  leader = vact[i];
                  minWi = vanetsim.scenario.Vehicle.TV2[vact[i]].Wi;
               }
          }
      System.out.println("===================  LEADER  : =============== "+leader);
      if(leader!=-1)
      {
         
          vanetsim.scenario.Vehicle.TV2[leader].situation = true;
          
          vanetsim.scenario.Vehicle.TV2[leader].est_WCA_ch = true;
          vanetsim.scenario.Vehicle.TV2[leader].est_WCA_mn = false;
          vanetsim.scenario.Vehicle.TV2[leader].est_WCA_init = false;
          
          TCluster[Nbcluster].LD.addElement(""+leader);
          //----------------------------------------------------------
//          System.out.println("Je suis le leader : "+leader+"/"+vact.length+" ma positien est : "+vanetsim.scenario.Vehicle.GetPositionElement(leader, vact) );
                  
        int nbVi = vanetsim.scenario.Vehicle.Vehicule.NbrVoisin(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(leader, vact),vact.length);
							 
        int Lvi[]  = vanetsim.scenario.Vehicle.Vehicule.ListVoisin(vanetsim.scenario.Vehicle.Vehicule.GetPositionElement(leader,vact) , vact,nbVi );
							//------------fin du probléme--------------
          //----------------------------------------------------------
          if(nbVi>0)        
          for(int j=0;j<Lvi.length;j++)
          {
              int idVoisin = Lvi[j];// Integer.parseInt(vanetsim.scenario.Vehicle.TV2[leader].VS.getElementAt(j).toString());
              if(vanetsim.scenario.Vehicle.TV2[idVoisin].situation==false)// etat.equals("Actif"))
              {
                   TCluster[Nbcluster].LD.addElement(""+idVoisin);  
                   vanetsim.scenario.Vehicle.TV2[idVoisin].situation=true;
              }
          }
      
      }
       int nblibre=0;
       for(int i=0;i<vact.length;i++)
       {
          if(vanetsim.scenario.Vehicle.TV2[vact[i]].situation==false)nblibre++;
       }
       if(nblibre>0)
       {
            Nbcluster++;
            FormationClusters(cl,vact,k);
       }else
       {
            int tete,reste;
            for(int i=0;i< Nbcluster;i++)
            {
                for(int j=0;j< TCluster[i].LD.getSize();j++)
                {
                    if(j==0)
                           {
                             tete = Integer.parseInt(TCluster[i].LD.getElementAt(j).toString());
                             vanetsim.scenario.Vehicle.TV2[tete].est_WCA_ch = true;
                             vanetsim.scenario.Vehicle.TV2[tete].est_WCA_mn = false;
                             vanetsim.scenario.Vehicle.TV2[tete].est_WCA_init =false;
                           }else
                           {
                              reste = Integer.parseInt(TCluster[i].LD.getElementAt(j).toString());
                              vanetsim.scenario.Vehicle.TV2[reste].est_WCA_mn = true;
                              vanetsim.scenario.Vehicle.TV2[reste].est_WCA_ch = false;
                              vanetsim.scenario.Vehicle.TV2[reste].est_WCA_init = false;
                           }
                }
            }
       //----------------------------------
            System.out.println("===================  NBCLUSTER  : =============== "+Nbcluster);
            AffichageClustering(Nbcluster, cl);
            /*
           for(int i=0;i<F1.Nbcluster;i++)
           {
              System.out.println("Leader N°"+F1.TCluster[i].LD.getElementAt(0).toString()+" à "+F1.TCluster[i].LD.getSize()+" Elements");
              for(int j=0;j<F1.TCluster[i].LD.getSize();j++)
              {
                            System.out.println("              Drone N°"+F1.TCluster[i].LD.getElementAt(j));
              }
           }
            */
        //---------------------------------
       }
}

}
public  static void AffichageClustering(int nbclust, DefaultTableModel cl)
{
    if(cl.getColumnCount()==0)
    {
        //cl.setColumnCount(0);
        cl.addColumn("IdCluster");
        cl.addColumn("Ch");
        cl.addColumn("Nbre");
        cl.addColumn("Liste des véhicules");
    }
    cl.setRowCount(nbclust);
    int maxcol=0;
    for(int i=0;i<nbclust;i++)
    {
       if(TCluster[i].LD.getSize()>maxcol) maxcol = TCluster[i].LD.getSize();
    }
    
    for(int i=0;i<maxcol;i++) cl.setRowCount(nbclust);
    
    for(int i=0;i<nbclust;i++)
    {
        cl.setValueAt("Cl"+(i+1), i, 0); 
        cl.setValueAt("V"+(Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())), i, 1); 
        
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].est_WCA_ch = true;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].est_WCA_mn = false;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].est_WCA_init = false;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].type_WCA_noeud ="Ch";
        //vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].Dr.setBackground(new java.awt.Color(204, 204, 204));//gris
        //vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())].Dr.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        
          
        cl.setValueAt(""+(TCluster[i].LD.getSize()-1), i, 2); 
        
        String ch="";
        for(int j=1;j<TCluster[i].LD.getSize();j++)
        {
        ch = ch +("V"+Integer.parseInt(TCluster[i].LD.getElementAt(j).toString()))+",";
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].est_WCA_ch = false;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].est_WCA_mn = true;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].est_WCA_init = false;
        vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].type_WCA_noeud ="Mn";
        //vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].Dr.setBackground(new java.awt.Color(255, 0, 0));//rouge
        //vanetsim.scenario.Vehicle.TV2[Integer.parseInt(TCluster[i].LD.getElementAt(j).toString())].Dr.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        
        }
        if((TCluster[i].LD.getSize()-1)>0)  cl.setValueAt(""+ch, i, 3);
        else cl.setValueAt("-", i, 3);
    }
}
public static  void Paths(DefaultTableModel P, int portee) 
{
       P.setColumnCount(0);
       P.addColumn(" ");
       for(int i=0;i<Nbcluster;i++)
       P.addColumn("Cl"+(Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())+1));
       
       P.setRowCount(Nbcluster);
       for(int i=0;i<Nbcluster;i++)
       P.setValueAt("Cl"+(Integer.parseInt(TCluster[i].LD.getElementAt(0).toString())+1), i, 0);
       
       int s,c;
       for(int i=0;i<Nbcluster;i++)
       {
           s = Integer.parseInt(TCluster[i].LD.getElementAt(0).toString());
           for(int j=0;j<Nbcluster;j++)
           {
                c = Integer.parseInt(TCluster[j].LD.getElementAt(0).toString());
                if(s!=c)
                {
                     if(vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(s, c)<=portee)
                    {
                       P.setValueAt(""+vanetsim.scenario.Vehicle.Vehicule.distance_euclidienne(s, c), i, j+1);
                     }
                }
           }
       }
}

} 

//------------------------------------------------------------------------------
}
