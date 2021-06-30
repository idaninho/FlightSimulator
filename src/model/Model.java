package model;


import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.shape.Circle;
import test.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;

import static test.StatLib.*;


public class Model extends Observable{

    private float[] maxZ;//zScore learning algorithm
    private float[] zScoreDetect;
    private float[] featureVal,corFeatureVal;
    private Timer t=null;
    public IntegerProperty timeStep;
    private Socket fg=new Socket();
    private int index;
    private List<CorrelatedFeaturesHybrid> hybridList=new ArrayList<CorrelatedFeaturesHybrid>();
    private Circle circle=new Circle();

    public List<CorrelatedFeaturesHybrid> getHybridList() {
        return hybridList;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(model.Circle c) {
        this.circle.setCenterX(c.c.x);
        this.circle.setCenterY(c.c.y);
        this.circle.setRadius(c.r);
    }

    public float[] getFeatureVal() {
        return featureVal;
    }

    public float[] getCorFeatureVal() {
        return corFeatureVal;
    }


    public int getIndex() {
        return index;
    }

    public float getMaxZ(int index) {
        return maxZ[index];
    }

    public float[] getzScoreDetect() {
        return zScoreDetect;
    }

    public float[] getFeatureValue(TimeSeries data, String f1) {
        float val;
        float[] fArr = new float[data.getLines_num() - 1];
        int i = 1, j;
        for (j = 0; j < data.getFeatures_num(); j++) {
            if (data.name[j].equals(f1)) {
                index = j;
                break;
            }
        }

        int a=0;

        for (i=1;i<data.getLines_num()-1;i++)
        {
            val=data.getData()[i][index];

            java.lang.reflect.Array.setFloat(fArr, a, val);
            a++;
        }
        return fArr;
    }

    public void zScore(TimeSeries data) {

        // i want to call him one time only, he will do the calc and save the data
        this.maxZ=new float[data.getFeatures_num()];

        for (int i = 0; i < data.getFeatures_num(); i++)//for every feature in the data
        {
            zScoreDetector(data, data.getName()[i], "L");//Learning
        }
    }

    public void zScoreDetector (TimeSeries data, String featureName, String status) {
        float abs = 0, sqv = 0, temp = 0;
        float[] arrX = new float[data.getLines_num() - 1];
        Arrays.fill(arrX, 0);
        float sqVar, avg;
        this.zScoreDetect = new float[data.getLines_num() - 1];
        arrX = getFeatureValue(data, featureName);
        float[] arrOne = new float[1];
        arrOne[0] = arrX[0];//first value always equals to himself dividing SQR of variance
        sqVar = var(arrOne);
        maxZ[index] = 0;
        if (status.equals("D"))
            zScoreDetect[0] = maxZ[index];//first value always equals to himself dividing SQR of variance
        for (int i = 2; i < arrX.length; i++) {
            float[] arrTemp = Arrays.copyOfRange(arrX, 0, i);
            sqVar = var(arrTemp);//until the current I
            avg = avg(arrTemp);//until the current I
            sqv = (float) Math.sqrt(sqVar);
            if ((avg == 0 && arrTemp[i - 1] == 0) || (arrTemp[i - 1] - avg) == 0 || sqv <= 0)
                temp = 0;
            else {
                abs = (Math.abs(arrTemp[i - 1] - avg));
                temp = (abs / sqv);
                temp =Float.parseFloat(new DecimalFormat("##.####").format(temp));
            }
            if (temp > maxZ[index] && status.equals("L"))//temp>max is always true//System.out.println("inside if!@#!ASD");
            {
                maxZ[index] = temp;
            } else
                if (status.equals("D")) {
                    zScoreDetect[i - 1] = temp;//we save every value of temp in the Detection mode
                }
        }
    }

    public double[] linReg(TimeSeries ts,List<AnomalyReport> detectList,String name,String corName) {
        String tempX = null, tempY = null, desc;
        double[] timestepError = null;
        timestepError = new double[detectList.size()];
        int i = 0;
        for (AnomalyReport error : detectList) {//finding the correlated feature and the max
            desc = error.description;
            String[] line = desc.split("-");
            if (line[0].equals(name)) {
                tempX = line[0];
                tempY = line[1];
                timestepError[i] = error.timeStep;
                i++;
            }
            if (line[1].equals(name)) {
                tempX = line[0];
                tempY = line[1];
                timestepError[i] = error.timeStep;
                i++;
            }
        }
        if (tempX == null) {// so we did not find Anomalies
            corFeatureVal = getFeatureValue(ts, corName);//float arr for the corFeature-for DETECTION
            featureVal = getFeatureValue(ts, name);//float arr for the Feature-for DETECTION
        }
        else {
            corFeatureVal = getFeatureValue(ts, tempY);
            featureVal = getFeatureValue(ts, tempX);
        }
        return timestepError;
    }

    public void hybrid(TimeSeries data) {//checking correlation for all features

        float max=0;
        float maxTemp = 0;
        int index=0,length=data.getFeatures_num();
        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++) {
                if (j != i) {
                    max = Math.abs(pearson(data.Get(data.data, i), data.Get(data.data, j)));
                    if (max > maxTemp) {
                        maxTemp = max;
                        index= j;
                    }
                }
            }
            hybridList.add(new CorrelatedFeaturesHybrid(data.name[i], data.name[index],maxTemp));
        }
    }


    public void hybridAlgorithm(TimeSeries data, String feature1, String feature2) {

        List<Point> points = new ArrayList<Point>();

        float[] x = new float[data.getLines_num() - 1];
        float[] y = new float[data.getLines_num() - 1];
        if (feature1 != null && feature2 != null) {
            x = getFeatureValue(data, feature1);
            y = getFeatureValue(data, feature2);
        }
        for (int i = 0; i < data.getLines_num() - 1; i++) {
            points.add(new Point(x[i], y[i]));
        }
        model.Circle c = model.Hybrid.makeCircle(points);
        setCircle(c);
    }


    public Model(IntegerProperty timeStep) {
        this.timeStep=timeStep;
    }

    public void fgConnect(String[]name, HashMap<String,FloatProperty>tsVar) {
        String line = "";
        try {
            PrintWriter out2fg = new PrintWriter(fg.getOutputStream());
            for (int i = 0; i < tsVar.size() - 2; i++) {//WORKING
                line+=(tsVar.get(name[i]).floatValue() + ",");
            }
            line+=(tsVar.get(name[tsVar.size() - 2]).floatValue());
            out2fg.println(line);
            out2fg.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connect(String ip,int port) {
        try {
            fg = new Socket(ip, port);
            System.out.println("connect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(int length, int rate) {
        int speed=1000/rate;
        if (t==null){
            t=new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (timeStep.intValue()<length) {
                        timeStep.set(timeStep.get() + 1);
                    }
                    else
                        stop();
                }
            },0,speed);
        }
    }

    public void pause() {
        if (t!=null)
            t.cancel();
        t=null;

    }

    public void stop() {
        if (t!=null)
            t.cancel();
        t=null;
        timeStep.set(0);
    }






}
