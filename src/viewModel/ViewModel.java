package viewModel;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Circle;
import model.CorrelatedFeaturesHybrid;
import model.Model;
import test.*;

import java.util.*;

public class ViewModel implements Observer {

    public IntegerProperty timeStep, rateToSecond;
    HashMap<String, FloatProperty> tsVariables;//display flight according to the values in this hash map<time series name,his value>
    public Runnable play, pause, stop;
    public TimeSeries ts, anomalyTs;
    SimpleAnomalyDetector sad = new SimpleAnomalyDetector();
    public Line l;
    List<AnomalyReport> detectListLinReg;
    public Settings setting;
    private final Model m;
    public float[] featureValue, corFeatureValue, zScoreVisual;
    public String featureName, corFeature;
    private int index = -1;//index of the feature name i calculate in getFeatureValue in model
    public float maxZScore;//the value that return from model-getmaxz value
    public Circle hybridCircle;


    public ViewModel(String csvPath, String settingsPath) {
        setTimeSeries(csvPath);
        setSettings(settingsPath);
        this.rateToSecond = new SimpleIntegerProperty(Integer.parseInt(setting.getSettings().get("rateToSecond")));
        this.corFeatureValue = new float[ts.getLines_num()];
        this.featureValue = new float[ts.getLines_num()];
        timeStep = new SimpleIntegerProperty(0);
        m = new Model(timeStep);
        tsVariables = new HashMap<>();
        //read from file
        setTsVariables(ts);
        if (this.ts != null)// cuz at first they are null
        {
            rateToSecond.addListener((o, ov, nv) -> {
                pause.run();
                play.run();
            });

            timeStep.addListener((o, ov, nv) -> {
                if (anomalyTs == null) {
                    Platform.runLater(() -> {
                        tsVariables.get("timeStep").set(nv.intValue());
                        for (int i = 0; i < ts.getFeatures_num(); i++) {
                            tsVariables.get(ts.name[i]).set(ts.getData()[nv.intValue()][i]);
                        }
                    });
                    m.fgConnect(ts.name, tsVariables);
                } else {
                    Platform.runLater(() -> {
                        tsVariables.get("timeStep").set(nv.intValue());
                        for (int i = 0; i < anomalyTs.getFeatures_num(); i++) {
                            tsVariables.get(anomalyTs.name[i]).set(anomalyTs.getData()[nv.intValue()][i]);
                        }
                    });
                    m.fgConnect(anomalyTs.name, tsVariables);
                }
            });
        }
        play = () -> m.play(ts.getLines_num() - 1, rateToSecond.intValue());
        pause = () -> m.pause();
        stop = () -> m.stop();
        if (this.ts != null)// cuz at first they are null
            m.zScore(ts);
        m.connect(setting.getSettings().get("ip"), Integer.parseInt(setting.getSettings().get("port")));
    }

    private void setTsVariables(TimeSeries ts) {
        // IT SHOULD BE THE NAMES OF THE VALUES IN THE CSV FILE OR FLIGHTGEAR?--GETKEYS FUNCTION its not correct!
        String[] name = ts.getName();
        tsVariables.put("timeStep", new SimpleFloatProperty(0));
        for (int i = 0; i < ts.getFeatures_num(); i++) {
            tsVariables.put(name[i], new SimpleFloatProperty(ts.getData()[timeStep.intValue()][i]));
        }
    }

    public FloatProperty getProperty(String name) {
        return tsVariables.get(name);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void setTimeSeries(String filePath) {
        this.ts = new TimeSeries(filePath);
    }

    public void setAnomalyTimeSeries(String filePath) {
        this.anomalyTs = new TimeSeries(filePath);
        stop.run();
        rateToSecond.set(Integer.parseInt(setting.getSettings().get("rateToSecond")));
        timeStep.set(0);
        for (int i = 0; i < anomalyTs.getFeatures_num(); i++) {
            tsVariables.get(anomalyTs.name[i]).set(anomalyTs.getData()[0][i]);
        }
        play = () -> m.play(ts.getLines_num() - 1, rateToSecond.intValue());
        pause = () -> m.pause();
        stop = () -> m.stop();
    }

    private void setSettings(String filePath) {
        this.setting = new Settings(filePath);
    }

    public void setGraphs(String newVal) {
        featureName = newVal;
        sad.learnNormal(ts);
        corFeature = sad.getCorrelatedFeature(newVal);
        if (corFeature == null)//not all features have correlated features
            Arrays.fill(corFeatureValue, 0);
        else
            corFeatureValue = m.getFeatureValue(ts, corFeature);
        featureValue = m.getFeatureValue(ts, newVal);
    }

    public void calcZScore() {
        if (index == -1) {
            m.zScore(ts);
        }
        if (this.anomalyTs != null) {//only if this is the anomaly flight CSV we check the Detection
            m.zScoreDetector(anomalyTs, featureName, "D");
            zScoreVisual = m.getzScoreDetect();//get the float arr calculated in zScoreDetector
            index = m.getIndex();//the index of the featureName in the maxZ array
            maxZScore = m.getMaxZ(index);
        }
    }

    public String calcHybrid() {
        if (m.getHybridList() == null)
            m.hybrid(ts);//only one time calculating the corFeatures and max
        String feature2 = null;
        float max = 0;
        for (CorrelatedFeaturesHybrid list : m.getHybridList()) {//finding the correlated feature and the max
            String fit1 = list.feature1;
            String fit2 = list.feature2;
            if (fit1.equals(featureName)) {
                feature2 = list.feature2;
                max = list.corrlation;
            }
            if (fit2.equals(featureName)) {
                feature2 = list.feature1;
                max = list.corrlation;
            }
        }

        if (max >= 0.95) {
            return "Lin Reg";
        } else if (max <= 0.5) {
            return "ZScore";
        } else if (this.anomalyTs != null) {
            m.hybridAlgorithm(anomalyTs, featureName, feature2);//creating the Circle
            hybridCircle = m.getCircle();//get
            corFeatureValue = m.getFeatureValue(anomalyTs, feature2);//float arr for the corFeature-for DETECTION
            featureValue = m.getFeatureValue(anomalyTs, featureName);//float arr for the Feature-for DETECTION
        }
        return "Hybrid";
    }


    public double[] calcLinReg() {
        detectListLinReg = new ArrayList<>();
        double[] timestep = null;
        if (this.anomalyTs != null) {
            detectListLinReg = sad.detect(anomalyTs);
            timestep=m.linReg(anomalyTs,detectListLinReg,featureName,corFeature);
            featureValue=m.getFeatureVal();
            corFeatureValue=m.getCorFeatureVal();

            for (CorrelatedFeatures list : sad.anoList) {
                if (list.feature1.equals(featureName))
                    l = list.lin_reg;
                if (list.feature2.equals(featureName))
                    l = list.lin_reg;
            }
        }
        return timestep;
    }
}

