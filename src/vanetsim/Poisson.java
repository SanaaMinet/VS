/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vanetsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
 
 
 
 
public class Poisson {
         public static double listofdatas[][];
         public int listofdata[][];
         private int listofdatamoyenne[][];
         private int fiabilite = 1;
         private Random random;
         private int duree;
         public static int nbObj;
         public Poisson(int duree) {
            this.duree = duree;
            random = new Random();
            listofdatamoyenne = new int[duree][2];
            for (int j=0;j<duree;j++){
                       listofdatamoyenne[j][0]=j;
                       listofdatamoyenne[j][1]=0;
            }
         }
 
         public void setFiabilite(int fiabilite){
            this.fiabilite = fiabilite;
         }
        
         public static int factorielle(int n){
            if(n>1)
                       return n*factorielle(n-1);
            else
                       return 1;
         }
 
         public double getExponentielle(double lambda, double y){
            return -Math.log (1-y)/lambda;
         }
        
         public double getPoisson (double lambda, int k){
            return Math.exp(0-lambda)*Math.pow(lambda, k)/factorielle (k);       
         }
 
         public double nextPoisson(double lambda){
            return -Math.log(random.nextDouble())/lambda;
         }
        
         public void generatePoissonProcess (double lambda){
         Random random = new Random();
         //double listofdatas[][];
            listofdatas = new double[duree*100][2];
            double timeG=0; // Le temps genere
            int i = 0;
            nbObj=0;
            do {
                       timeG += getExponentielle(lambda, random.nextDouble());
                       listofdatas[i][0]=i;
                       listofdatas[i][1]=(timeG*duree*10/duree);
                       i++;
                       nbObj++;
                       //System.out.println ("Generated value: "+timeG);
            } while(timeG < (double)duree);
            i--;
            listofdata = new int[duree][2];
            for (int j=0;j<duree;j++){
                       listofdata[j][0]=j;
                       listofdata[j][1]=0;
            }
            //-----------------------------------------------
            vanetsim.gui.controlpanels.ClusteringControlPanel.S.setRowCount(nbObj);
            adhoc.Adhoc.S = new String[nbObj][12];
            for(int k=0;k<nbObj;k++)
            {
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+(k+1), k, 0);
                adhoc.Adhoc.S[k][0] = ""+(k+1);
                //System.out.println(" TGénéré === "+listofdatas[k][1]*100);
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt(""+((int)(listofdatas[k][1]*100)), k, 2);
                
                adhoc.Adhoc.S[k][2] = ""+((int)(listofdatas[k][1]*100));//nano
                vanetsim.gui.controlpanels.ClusteringControlPanel.S.setValueAt("Inactif", k, 7);
                //vanetsim.scenario.Vehicle.TV[k].etat ="Inactif"; TV non crée encore
                adhoc.Adhoc.S[k][7] = "Inactif";
            }
            /*
                S.addColumn("N°");
    S.addColumn("Tps. arr.");
    S.addColumn("Latitude");
    S.addColumn("Longitude");
    S.addColumn("Etat");
            */
            //-----------------------------------------------
            /*
            for (int j=0;j<i;j++){
                       listofdata[(int)listofdatas[j][1]][1]++;
            }
 
            for (int j=1;j<duree;j++){
                       listofdata[j][1]+=listofdata[j-1][1];
            }
            */
          //  JOptionPane.showConfirmDialog(null, "Nbre : "+nbObj);
    }
 
         public void generateAveragePoissonProcess (double lambda){
         for (int f=0; f<fiabilite; f++){
            generatePoissonProcess(lambda);
            for (int j=1;j<duree;j++){
                       listofdatamoyenne[j][1]=(listofdatamoyenne[j][1]*f+listofdata[j][1])/(f+1);
            }
         }
         listofdata = listofdatamoyenne;
    }
 
         public void writeDataFile (String filename){
            try {
                       String filepath = System.getProperty("user.dir") + File.separator + filename;
                       System.out.println("Ecriture du fichier : "+filepath);
                       FileWriter fw = new FileWriter (filepath);
                       BufferedWriter bw = new BufferedWriter (fw);
 
                       if (listofdata.length>0)
                                   for (int i=0; i<listofdata.length; i++){
                                               String line="";
                                               for (int j=0; j<listofdata[i].length; j++){
                                                          line += new Integer(listofdata[i][j]).toString()+" ";
                                               }
                                               bw.write(line+"\n");
                                   }
                       bw.close();
            } catch (IOException e) {
                       e.printStackTrace();
            }
         }
 }
