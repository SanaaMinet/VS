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
public class APROVE {
//N is the number of two-dimension data points
//S is the similarity matrix
//R is the responsibility matrix
//A is the availabiltiy matrix
//iter is the maximum number of iterations
//lambda is the damping factor
 //-----------------------------pour les stats----------------------------
 public static DefaultTableModel ST2 = new DefaultTableModel();
 public static DefaultTableModel STa2 = new DefaultTableModel();
 public static int nbrchapv;
//---------------------------------------------------------
 
public static double lambdaAPROV;
public static int Th,Iter;
public static int N;
    
//const int N = 25;
public static double S[][];//[N][N] = {0};
public static double R[][];//[N][N] = {0};
public static double A[][];//[N][N] = {0};
//int iter = 230;
//double lambda = 0.9;
    
    
static void PositionPredict(int idv)
{
  vanetsim.scenario.Vehicle.TV[idv].posPx = (int)(vanetsim.scenario.Vehicle.TV[idv].posx + (((vanetsim.scenario.Vehicle.TV[idv].vitesse*1000/3600)*(double)Th)*100*37/0.98));
  vanetsim.scenario.Vehicle.TV[idv].posPy = vanetsim.scenario.Vehicle.TV[idv].posy;
  
}
static double NormeVector(int idv)
{
   return Math.sqrt(Math.pow(vanetsim.scenario.Vehicle.TV[idv].posx, 2) + Math.pow(vanetsim.scenario.Vehicle.TV[idv].posy, 2));
}
static double NormeVector(int Vx, int Vy)
{
 return Math.sqrt(Math.pow(NormeVector(Vx), 2) + Math.pow(NormeVector(Vy), 2) - 2*(vanetsim.scenario.Vehicle.TV[Vx].posx*vanetsim.scenario.Vehicle.TV[Vy].posx + vanetsim.scenario.Vehicle.TV[Vx].posy*vanetsim.scenario.Vehicle.TV[Vy].posy) );
}
static double NormeVectorPredict(int idv)
{
   return Math.sqrt(Math.pow(vanetsim.scenario.Vehicle.TV[idv].posPx, 2) + Math.pow(vanetsim.scenario.Vehicle.TV[idv].posPy, 2));
}
static double NormeVectorPredict(int Vx, int Vy)
{
 return Math.sqrt(Math.pow(NormeVectorPredict(Vx), 2) + Math.pow(NormeVectorPredict(Vy), 2) - 2*(vanetsim.scenario.Vehicle.TV[Vx].posPx*vanetsim.scenario.Vehicle.TV[Vy].posPx + vanetsim.scenario.Vehicle.TV[Vx].posPy*vanetsim.scenario.Vehicle.TV[Vy].posPy) );
}
static double CalculateSimilarity(int Vx, int Vy)//1ère métrique
{
  PositionPredict(Vx);
  PositionPredict(Vy);
    
  return  -(NormeVector(Vx,Vy)+NormeVectorPredict(Vx,Vy));
}
//==============================================================================

//const char* dataFileName = "ToyProblemData.txt";

static void readS(int Vactif[]) {
 
	N = Vactif.length;
        
        S = new double[N][N];
        R = new double[N][N];
        A = new double[N][N];
        
        for(int i=0; i<N; i++)
        for(int j=0; j<N; j++)
        {
           S[i][j] = 0.0;
           R[i][j] = 0.0;
           A[i][j] = 0.0;
        }
           
	double dataPoint[][]=new double[N][2];
	for(int i=0; i<N; i++) 
        {
	    dataPoint[i][0] = vanetsim.scenario.Vehicle.TV[Vactif[i]].posx;
            dataPoint[i][1] = vanetsim.scenario.Vehicle.TV[Vactif[i]].posy;
	}
	 
	
    // à utiliser
	int size = N*(N-1)/2; // S: le nombre des similarités calculées
	//Vector tmpS = null;
        double som;
	//compute similarity between data point i and j (i is not equal to j)
	for(int i=0; i<N-1; i++) 
        {
        som = 0;
       
	for(int j=i+1; j<N; j++) 
        {
            if(vanetsim.scenario.Vehicle.Vehicule.VerifVoisin(Vactif[i], Vactif[j])==true)
            {
	    S[i][j] = /*CalculateSimilarity(Vactif[i], Vactif[j]); */ -((dataPoint[i][0]-dataPoint[j][0])*(dataPoint[i][0]-dataPoint[j][0])+(dataPoint[i][1]-dataPoint[j][1])*(dataPoint[i][1]-dataPoint[j][1])); // ((xi-xj)+(yi-yj))+((x'i-x'j)+(y'i-y'j)) sachant que x',y' sont les positions estimées
            S[j][i] = S[i][j];
            som += S[i][j] ; 
            }else
            {
	    S[i][j] = 0;
            S[j][i] = S[i][j];
            //som += S[i][j] ; 
            
            }
            
	   // tmpS.add(S[i][j]); 
        }
       // System.out.println("i = "+i+"     som : "+som);
        S[i][i] = som;
	}
        /*
	//compute preferences for all data points: median    // S: ce n'est pas impliqué dans APROVE
	//   Arrays.sort() (tmpS.begin(), tmpS.end());    //S: à oter
	double median = 0;     // S: à oter
	
	if(size%2==0)   //S: à oter
		median = (tmpS[size/2]+tmpS[size/2-1])/2;    //S: à oter
	else  //S: à oter
		median = tmpS[size/2];  //S: à oter
	for(int i=0; i<N; i++) S[i][i] = median; //S: à oter
        */
}

public static void AlgorithmAPROVE(DefaultTableModel Ss,DefaultTableModel Rr,DefaultTableModel Aa,int Vactif[])
{
   //System.out.println("===================Call from manager of AlgorithmAPROVE================ ");
	readS(Vactif);  //S: à oter et remplacer par le flux de véhicules
   System.out.println("Mise à jour des paramètres (responsibility & availability) ");
	for(int m=0; m<Iter; m++) {  
	//update responsibility
		for(int i=0; i<N; i++) 
                {
			for(int k=0; k<N; k++) {
				double max = -1e100;
				for(int kk=0; kk<k; kk++) 
                                {
                                        if(S[i][kk] != 0)
                                        {
                                            if(S[i][kk]+A[i][kk]>max) 
                                            max = S[i][kk]+A[i][kk];
                                        }
                                        
				}
				for(int kk=k+1; kk<N; kk++) 
                                {
                                         if(S[i][kk] != 0)
                                         {
					    if(S[i][kk]+A[i][kk]>max) 
						max = S[i][kk]+A[i][kk];
                                         }
				}
				R[i][k] = (1-lambdaAPROV)*(S[i][k] - max) + lambdaAPROV*R[i][k];
			}
		}
        //System.out.println("=================== update availability ================ ");
	//update availability
		for(int i=0; i<N; i++) {
			for(int k=0; k<N; k++) {
				if(i==k) {
					double sum = 0.0;
					for(int ii=0; ii<i; ii++) {
						sum += Math.max(0.0, R[ii][k]);
					}
					for(int ii=i+1; ii<N; ii++) {
						sum += Math.max(0.0, R[ii][k]);
					}
					A[i][k] = (1-lambdaAPROV)*sum + lambdaAPROV*A[i][k];
				} else {
					double sum = 0.0;
					int maxik = Math.max(i, k);
					int minik = Math.min(i, k);
					for(int ii=0; ii<minik; ii++) {
						sum += Math.max(0.0, R[ii][k]);
					}
					for(int ii=minik+1; ii<maxik; ii++) {
						sum += Math.max(0.0, R[ii][k]);
					}
					for(int ii=maxik+1; ii<N; ii++) {
						sum += Math.max(0.0, R[ii][k]);
					}
					A[i][k] = (1-lambdaAPROV)*Math.min(0.0, R[k][k]+sum) + lambdaAPROV*A[i][k];
				}
			}
		}
	}
       System.out.println("Recherche des noeuds exemplaires (Exemplars)");
	
	//find the exemplar
	double E[][] = new double[N][N];
        for(int i=0; i<N; i++)
        for(int j=0; j<N; j++) E[i][j]=0;
                
	//vector<int> center;
        DefaultListModel center = new DefaultListModel();
        
	for(int i=0; i<N; i++) 
        {
		E[i][i] = R[i][i] + A[i][i];//chcvg
                vanetsim.scenario.Vehicle.TV[Vactif[i]].Cvg = E[i][i];
                //vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV = ?;
                
		if(E[i][i]>0) 
                {
                    
                    
			//center.push_back(i);
                   center.addElement(""+i);
                        //------------------------------------
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = true;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = false;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Ch";
                        //------------------------------------
		}
                else
                {
                 vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = false;
                 vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = true;
                 vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                 vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Mn";
                }
	}
      System.out.println("Localisation des CH des noeuds");
        //--------localiser Ch des noeuds-----------
        int id,idx;
        double maxCvg;
        nbrchapv = 0;
        for(int i=0; i<N; i++) 
        {
               if(vanetsim.scenario.Vehicle.TV[Vactif[i]].Cvg>0)
               {
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV=Vactif[i];
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = true;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = false;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                    vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Ch";
                    nbrchapv++;
               }else
               {
                    if(vanetsim.scenario.Vehicle.TV[Vactif[i]].nbvoisin>0) //verif
                    {
                         id     = Integer.parseInt(vanetsim.scenario.Vehicle.TV[Vactif[i]].VS.getElementAt(0).toString()); //verif
                         maxCvg = vanetsim.scenario.Vehicle.TV[id].Cvg;
                         
                         for(int j=1;j<vanetsim.scenario.Vehicle.TV[Vactif[i]].VS.getSize();j++)//verif
                         {
                             idx = Integer.parseInt(vanetsim.scenario.Vehicle.TV[Vactif[i]].VS.getElementAt(j).toString());
                             
                             if(vanetsim.scenario.Vehicle.TV[idx].Cvg>maxCvg)
                             {
                                id     = idx;
                                maxCvg = vanetsim.scenario.Vehicle.TV[idx].Cvg;
                             }
                         }
                        if(maxCvg<0) 
                        {
                            vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV=Vactif[i];
                            vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = true;
                            vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = false;
                            vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                            vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Ch";
                            nbrchapv++;
                        }
                        else 
                        {
                                vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV=id;
                                vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = false;
                                vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = true;
                                vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                                vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Mn";
                        }
                         
                    }else
                    {
                     vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV=Vactif[i];
                     vanetsim.scenario.Vehicle.TV[Vactif[i]].est_ch_APV = true;
                     vanetsim.scenario.Vehicle.TV[Vactif[i]].est_mn_APV = false;
                     vanetsim.scenario.Vehicle.TV[Vactif[i]].est_init_APV = false;
                     vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV ="Ch";
                     nbrchapv++;
                    }
               
               }
        }

	//data point assignment, idx[i] is the exemplar for data point i
        //Affichage(Ss,Rr,Aa,Vactif);
 
        /*
	int idx[] =new int[N];
        for(int i=0;i<N;i++) idx[i]=0;
        
        
	for(int i=0; i<N; i++) {
		int idxForI = 0;
		double maxSim = -1e100;
		for(int j=0; j<center.getSize(); j++) {
			int c = Integer.parseInt(center.getElementAt(j).toString());
			if (S[i][c]>maxSim) {
				maxSim = S[i][c];
				idxForI = c;
			}
		}
		idx[i] = idxForI;
	}
	//output the assignment
	for(int i=0; i<N; i++) 
        {
		//since the index of data points starts from zero, I add 1 to let it start from 1
                System.out.println("Index"+i+"   === "+(idx[i]+1));
		//cout << idx[i]+1 << endl; 
	}
        */
        /*for (int i = 0; i < N; i++) {
        Algorithms.FitnessClustering.CalculStateDuration(vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh, adhoc.Adhoc.compteur / 100);// Modif 06-09-2017
	}*/
        CalculateStateDuration_APV();
}
//==============================================================================

public static void Affichage(DefaultTableModel Ss,DefaultTableModel Rr,DefaultTableModel Aa, int Vactif[])
{
  //  System.out.println("===================A5_______6________1================ ");
    Ss.setColumnCount(0); Ss.addColumn(" ");
    Rr.setColumnCount(0); Rr.addColumn(" ");
    Aa.setColumnCount(0); Aa.addColumn(" ");
//    System.out.println("===================A5_______6________2================ ");
    for(int i=0;i<Vactif.length;i++)
    {
      Ss.addColumn("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh);
      Rr.addColumn("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh);
      Aa.addColumn("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh);
      
    }
  //  System.out.println("===================A5_______6________3================ ");
    Ss.setRowCount(Vactif.length);
    Rr.setRowCount(Vactif.length);
    Aa.setRowCount(Vactif.length);
//    System.out.println("===================A5_______6________4================ ");
    for(int i=0;i<Vactif.length;i++)
    {
      Ss.setValueAt("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh, i, 0);  
      Rr.setValueAt("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh, i, 0);
      Aa.setValueAt("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh, i, 0);
      
      for(int j=0;j<Vactif.length;j++)
      {
         Ss.setValueAt(""+S[i][j], i, j+1);
         Rr.setValueAt(""+R[i][j], i, j+1);
         Aa.setValueAt(""+A[i][j], i, j+1);
      }
      
    }
  //   System.out.println("===================A5_______6________5================ ");
    
}

//==============================================================================
 public static void  propriete(DefaultTableModel PR,int Vactif[])
	{
	 
	//-------------------------------FitnessClustering----------------
         //   System.out.println("================= 4444 ====================");
            if(PR.getColumnCount()==0)
            {
        
	   PR.addColumn("Noeuds");
	   PR.addColumn("Type");
	   PR.addColumn("Nbr. Voisin");
	   PR.addColumn("Ch. Convergence");
	   PR.addColumn("CH");
           
            }
           PR.setRowCount(Vactif.length);
  
//System.out.println("================= 5555 ====================");
/*
	for(int i=0;i<PR.getColumnCount();i++)
	{
	 for(int j=0;j<PR.getRowCount();j++)
	 {
	   if(i==0)PR.setValueAt("V"+(vanetsim.scenario.Vehicle.TV[Vactif[j]].idVeh), j, 0);
	   else PR.setValueAt(" ", j, i);
	 }
	}
 */
		 for(int i=0;i<Vactif.length;i++)
		 {
                     PR.setValueAt("V"+(vanetsim.scenario.Vehicle.TV[Vactif[i]].idVeh), i, 0);
                     PR.setValueAt(vanetsim.scenario.Vehicle.TV[Vactif[i]].type_noeud_APV,i, 1);
                     PR.setValueAt(vanetsim.scenario.Vehicle.TV[Vactif[i]].nbvoisin,i, 2);
                     PR.setValueAt(vanetsim.scenario.Vehicle.TV[Vactif[i]].Cvg,i, 3);
                     PR.setValueAt("V"+vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV,i, 4);
                    // PR.setValueAt(vanetsim.scenario.Vehicle.TV[Lvact[i]].,i, 5);
		 }
//System.out.println("================= 6666 ====================");
	}
//------------------------------------------------------------------------------
 
 public static void MaintenanceAPROVE(int Vactif[])
 {
            int  vap,vlost;
            int Llost[] = null;
            boolean lostCH;
            int myCHi,myCHap;
            DefaultListModel LOST = new DefaultListModel();
            DefaultListModel FOUND = new DefaultListModel();
            DefaultListModel vs = new DefaultListModel();
            //nbrchapv = 0;
         //   System.out.println("--------------------M1 "); 
            for(int i=0;i<Vactif.length;i++)
            {
                vs.removeAllElements();
                for(int l=0;l<vanetsim.scenario.Vehicle.TV[Vactif[i]].VS.getSize();l++)
                vs.addElement(vanetsim.scenario.Vehicle.TV[Vactif[i]].VS.getElementAt(l));
                
                myCHi = vanetsim.scenario.Vehicle.TV[Vactif[i]].myCH_APV;
                vanetsim.scenario.Vehicle.Vehicule.DecouvertVoisinage(Vactif[i],adhoc.Adhoc.portee,Vactif); 
                //-------------------------
                    lostCH = true;
                //-------------------------
                    for(int ap=0;ap<vs.getSize();ap++)
                    {
                        if(i<Vactif.length)
                        if(ap<vs.getSize())
                        if(vs.getElementAt(ap).toString().trim().length()>0)
                        {
                           /* System.out.println("debut");
                            System.out.println("i :  "+i+"/"+Vactif.length+"       ap : "+ap+"/"+vs.getSize());
                            System.out.println("contenu :  "+Vactif[i]+"/"+vanetsim.scenario.Vehicle.TV.length);
                            System.out.println("valeur :  "+vs.getElementAt(ap).toString());*/
                                     vap = Integer.parseInt(vs.getElementAt(ap).toString().trim());
                            //System.out.println("valeur :  "+vs.getElementAt(ap).toString() +"     vap : "+vap );         
                           //System.out.println("fin");
                         if(vap==myCHi)lostCH =false; 
                        }
                    }
                    
 /*
i :  64/116       ap : 2/7
contenu :  362/1498
valeur :  358                  
*/
                    if(lostCH==true) LOST.addElement(""+Vactif[i]);//cdt 1
                    else 
                       {
                             if(Vactif[i]==myCHi)
                             {
                                  lostCH = false;
                                  for(int ap=0;ap<vs.getSize();ap++)
                                  {
                                        vap     = Integer.parseInt(vs.getElementAt(ap).toString());
                                        myCHap  = vanetsim.scenario.Vehicle.TV[vap].myCH_APV;
                                        if(vap==myCHap)
                                        {
                                            if(vanetsim.scenario.Vehicle.TV[Vactif[i]].Cvg<vanetsim.scenario.Vehicle.TV[vap].Cvg) lostCH =true;
                                        }
                                  }
                                   if(lostCH==true) LOST.addElement(""+Vactif[i]);//cdt 2
                                   else FOUND.addElement(Vactif[i]);
                             }
                       
                       }
                        
                    }
         //   System.out.println("--------------------M2 "); 
                //-------------------------
               // System.out.println("Liste des elements de LOST sont :");
               // for(int i=0;i<LOST.getSize();i++)
               // System.out.println("Elet ->  "+LOST.getElementAt(i));
              //  System.out.println("=================================================");
                //-------------------------
                double CVG = Math.pow(10,10);
                int element =-1;
                
                for(int f=0;f<LOST.getSize();f++)
                {
                    
                      element = -1;
                      CVG = -Math.pow(10,10);
                      
                      vlost = Integer.parseInt(LOST.getElementAt(f).toString());
                      vanetsim.scenario.Vehicle.Vehicule.DecouvertVoisinage(vlost,adhoc.Adhoc.portee,Vactif);
                      
                      if(vs.getSize()>0)
                      {
                      Llost = new int[vanetsim.scenario.Vehicle.TV[vlost].VS.getSize()];
                      //-----------------------------------------
                      
                      for(int v=0;v<Llost.length;v++)
                      if(v<vs.getSize())
                      Llost[v] = Integer.parseInt(vs.getElementAt(v).toString());
                      
                      for(int i=0;i<Llost.length;i++)
                      {
                         myCHap = vanetsim.scenario.Vehicle.TV[Llost[i]].myCH_APV;
                         
                         if(Llost[i]==myCHap)
                         {
                             if(vanetsim.scenario.Vehicle.TV[Llost[i]].Cvg > CVG)
                             {
                                 CVG      = vanetsim.scenario.Vehicle.TV[Llost[i]].Cvg;
                                 element  = Llost[i];
                             }
                         }
                         
                      }
                      
                     }
                      
                      if(element != -1)
                      {
                         // System.out.println("Vehicule : "+vlost+" est un Mn");
                          vanetsim.scenario.Vehicle.TV[vlost].myCH_APV = element;
                          vanetsim.scenario.Vehicle.TV[vlost].type_noeud_APV ="Mn";
                          vanetsim.scenario.Vehicle.TV[vlost].est_ch_APV = false;
                          vanetsim.scenario.Vehicle.TV[vlost].est_mn_APV = true;
                          vanetsim.scenario.Vehicle.TV[vlost].est_init_APV = false;
                          //nbrchapv--;
                          
                           
                      } 
                      else 
                      {
                         // System.out.println("Vehicule : "+vlost+" est un Ch");
                          vanetsim.scenario.Vehicle.TV[vlost].myCH_APV = vlost;
                          vanetsim.scenario.Vehicle.TV[vlost].type_noeud_APV ="Ch";
                          vanetsim.scenario.Vehicle.TV[vlost].est_ch_APV = true;
                          vanetsim.scenario.Vehicle.TV[vlost].est_mn_APV = false;
                          vanetsim.scenario.Vehicle.TV[vlost].est_init_APV = false;
                          //nbrchapv++;
                      } 
                        
                       
                      
                }
                
                CalculateStateDuration_APV();
               // System.out.println("--------------------M3 "); 
                //-------------------------
 }
 
//------------------------------------------------------------------------------
public static void CalculateStateDuration_APV(){
        
    for(int v=0;v<vanetsim.scenario.Vehicle.TV.length;v++)
    {
        if (vanetsim.scenario.Vehicle.TV[v].etat == "Actif")
        if(!vanetsim.scenario.Vehicle.TV[v].type_noeud_APV.equals(vanetsim.scenario.Vehicle.TV[v].ETAT_APV.get(vanetsim.scenario.Vehicle.TV[v].ETAT_APV.size()-1)))
        {   
            vanetsim.scenario.Vehicle.TV[v].transfin_APV = System.currentTimeMillis();
            vanetsim.scenario.Vehicle.TV[v].DUREE_APV.add(vanetsim.scenario.Vehicle.TV[v].transfin_APV - vanetsim.scenario.Vehicle.TV[v].transdeb_APV);
            vanetsim.scenario.Vehicle.TV[v].transdeb_APV = vanetsim.scenario.Vehicle.TV[v].transfin_APV;
            vanetsim.scenario.Vehicle.TV[v].ETAT_APV.add(""+vanetsim.scenario.Vehicle.TV[v].type_noeud_APV);   
            System.out.println("ETAT_APV du noeud "+vanetsim.scenario.Vehicle.TV[v].idVeh+" est "
            +vanetsim.scenario.Vehicle.TV[v].ETAT_APV.get(vanetsim.scenario.Vehicle.TV[v].ETAT_APV.size()-1)+" pendant (DUREE_APV)"
            +vanetsim.scenario.Vehicle.TV[v].DUREE_APV.get(vanetsim.scenario.Vehicle.TV[v].DUREE_APV.size()-1)+" ms.");
            
        }     
    }     
}
  
    //-------------------
 
}
