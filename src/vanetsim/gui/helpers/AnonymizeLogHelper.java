package vanetsim.gui.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AnonymizeLogHelper {

    public static void main(String args[]) {
        AnonymizeLogHelper ano = new AnonymizeLogHelper();
        if (args.length > 0 && args[0].equals("0")) {
            if (args.length == 8) {
                ano.perturbation(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[6], Integer.parseInt(args[7]));
            } else
                System.out.println("wrong parameters");
        }
        if (args.length > 0 && args[0].equals("1")) {
            if (args.length == 10) {
                ano.attackLog(args[1], Integer.parseInt(args[2]), Integer.parseInt((args[3])), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), args[7], Integer.parseInt(args[8]), Integer.parseInt(args[9]));
            } else
                System.out.println("wrong parameters");
        }
    }

    public void perturbation(String inputFile, String outputFile, int minPerturbation, int maxPerturbation, int index, String seperator, int skipLine) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            String newLine = "";
            String data[];
            FileWriter fstream;
            fstream = new FileWriter(outputFile, false);
            BufferedWriter out = new BufferedWriter(fstream);
            while (line != null) {
                if (skipLine > 0) {
                    out.write(line + "\n");
                    skipLine--;
                } else {
                    data = line.split(seperator);
                    newLine = "";
                    data[index] = String.valueOf(Integer.parseInt(data[index]) + (int) (Math.random() * ((maxPerturbation + 1) - minPerturbation) + minPerturbation));
                    for (int i = 0; i < data.length; i++) {
                        if (i == data.length - 1)
                            newLine += data[i];
                        else
                            newLine += data[i] + seperator;
                    }
                    out.write(newLine + "\n");
                }
                line = reader.readLine();
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public void attackLog(String inputFile, int idIndex, int timestampId, int xId, int yId, int speedId, String seperator, int skipLine, int beaconIntervalTime) {
        BufferedReader reader;
        ArrayList<String> vehicleAttacked = new ArrayList<String>();
        ArrayList<String> vehicleIds = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            String data[];
            String data2[];
            String data3[];
            double v1 = 0;
            double v2 = 0;
            double normalize;
            int x1;
            int x2;
            int y1;
            int y2;
            double dx;
            double dy;
            double tmpFactor;
            int timeBetweenBeacons = 0;
            double expectedWay = 0;
            double savedFactor;
            int savedSkipLine = skipLine;
            String savedID = "";
            int firstTimestamp = -1;
            int successCount = 0;
            int failCount = 0;
            double calibration = 1.1;
            @SuppressWarnings("unused") double factorToRealVehicle = 0;
            while (line != null) {
                if (skipLine > 0) {
                    skipLine--;
                } else {
                    data = line.split(seperator);
                    if (firstTimestamp == -1)
                        firstTimestamp = Integer.parseInt(data[timestampId]);
                    if (vehicleIds.contains(data[idIndex]))
                        break;
                    else {
                        vehicleIds.add(data[idIndex]);
                        vehicleAttacked.add(line);
                    }
                }
                line = reader.readLine();
            }
            skipLine = savedSkipLine;
            for (String vehicleLine : vehicleAttacked) {
                reader = new BufferedReader(new FileReader(inputFile));
                line = reader.readLine();
                data = vehicleLine.split(seperator);
                while (line != null) {
                    if (skipLine > 0)
                        skipLine--;
                    else {
                        data2 = line.split(seperator);
                        if (data[idIndex].equals(data2[idIndex]) && Integer.parseInt(data2[timestampId]) > Integer.parseInt(data[timestampId])) {
                            timeBetweenBeacons = Integer.parseInt(data2[timestampId]) - Integer.parseInt(data[timestampId]);
                            expectedWay = (double) (timeBetweenBeacons / 1000 * Double.parseDouble(data[speedId])) * calibration;
                            x1 = Integer.parseInt(data[xId]);
                            y1 = Integer.parseInt(data[yId]);
                            x2 = Integer.parseInt(data2[xId]);
                            y2 = Integer.parseInt(data2[yId]);
                            v1 = x2 - x1;
                            v2 = y2 - y1;
                            if (v1 == 0 && v2 == 0)
                                normalize = 1;
                            else
                                normalize = (double) (1 / Math.sqrt((double) ((v1 * v1) + (v2 * v2))));
                            v1 *= normalize * expectedWay;
                            v2 *= normalize * expectedWay;
                            v1 += x2;
                            v2 += y2;
                            break;
                        }
                    }
                    line = reader.readLine();
                }
                skipLine = savedSkipLine;
                savedFactor = 999999999;
                reader = new BufferedReader(new FileReader(inputFile));
                line = reader.readLine();
                while (line != null) {
                    data3 = line.split(seperator);
                    if (skipLine > 0)
                        skipLine--;
                    else {
                        if (Integer.parseInt(data3[timestampId]) > (beaconIntervalTime + firstTimestamp)) {
                            dx = v1 - Integer.parseInt(data3[xId]);
                            dy = v2 - Integer.parseInt(data3[yId]);
                            tmpFactor = (dx * dx + dy * dy);
                            if (data[idIndex].equals(data3[idIndex]))
                                factorToRealVehicle = tmpFactor;
                            if (savedFactor > tmpFactor) {
                                savedFactor = tmpFactor;
                                savedID = data3[idIndex];
                            }
                        }
                    }
                    line = reader.readLine();
                }
                if (data[idIndex].equals(savedID)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            System.out.println(successCount + ":" + failCount + ":x:BTI:" + beaconIntervalTime + " Name:" + inputFile);
            FileWriter fstream;
            fstream = new FileWriter("attackResult.log", true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(successCount + ":" + failCount + " :BTI:" + beaconIntervalTime + " Name:" + inputFile + "\n");
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
}
