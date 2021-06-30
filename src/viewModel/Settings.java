package viewModel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Settings implements Serializable {

    //hashmap string to string
    //hashmap string to list of int- min and max
    private final HashMap<String,String> settings;//flight gear to CSV file names- all 42 variables
    private HashMap<String,String> minMaxSettings;



    public HashMap<String, String> getMinMaxSettings() {
        return minMaxSettings;
    }

    public Settings(String txtFileName) {
        Scanner s=null;
        try {
            s=new Scanner(new FileReader(txtFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("error reading");
        }

        String line=s.nextLine();
        settings=new HashMap<>();

        while (!(line.equals("stop"))) {//reading the names of the variables and ip, port, rate for second

            String []split=line.split(",");
            settings.put(split[0],split[1]);
            line=s.nextLine();
        }

        line=s.nextLine();
        String[] minMax=null;
        minMaxSettings=new HashMap<>();

        while (!(line.equals("end"))) {//reading the min and max values
            minMax=line.split(",");
            minMaxSettings.put(minMax[0],minMax[1]);
            line=s.nextLine();
        }
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

}
