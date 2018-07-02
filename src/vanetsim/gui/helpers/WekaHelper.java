package vanetsim.gui.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WekaHelper {

    public static ArrayList<Double> readWekaCentroids(String filePath, String axis) 
    {
        System.out.println("filePath : ========= "+filePath+"    axis : "+axis);
        
        ArrayList<Double> returnArray = new ArrayList<Double>();
        String[] tmpArray = null;
        try {
            FileInputStream fstream = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String strLine;
            while ((strLine = br.readLine()) != null) 
            {
                if (strLine.length() > 0) 
                {
                    System.out.println(strLine.substring(0, 1) + ":::" + axis);
                    if (strLine.substring(0, 1).equals(axis)) 
                    {
                        tmpArray = strLine.split(" ");
                        for (int i = 0; i < tmpArray.length; i++)
                            if (!tmpArray[i].equals("") && !tmpArray[i].equals(" ") && !tmpArray[i].equals(axis))
                            returnArray.add(Double.parseDouble(tmpArray[i]));
                        break;
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }//---------------------------------------------------------------------
        for(int i=0;i<returnArray.size();i++)
        System.out.println(i+" ========= "+returnArray.get(i).toString());
        
        //----------------------------------------------------------------------
        return returnArray;
    }
}
