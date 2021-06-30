package view;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import viewModel.ViewModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;

public class WindowController {


    private ViewModel vm;

    @FXML
    public ListView AlgorithmScript;
    @FXML
    public ListView TimeSeriesScript;
    @FXML
    MyPlayer myPlayer=new MyPlayer();
    @FXML
    Graph graph=new Graph();
    @FXML
    MyJoystick joystick=new MyJoystick();
    String [] name=null;
    Path csvPath,settingsPath;

    public WindowController() {
    }

    public void init() {
        if (myPlayer.controller != null) {
            myPlayer.controller.timeSlider.setMax(vm.ts.getLines_num());//max value is the lines num of TS
            myPlayer.controller.speed = new SimpleDoubleProperty(Double.parseDouble(vm.setting.getSettings().get("rateToSecond")));//initialize
            vm.timeStep.bindBidirectional(myPlayer.controller.timeSlider.valueProperty());//timeStep connected to Slider in GUI and can changed from the model or from the user interface, for this reason is double binding
            vm.rateToSecond.bindBidirectional(myPlayer.controller.speed);//ratetosecond connected to "PlaySpeed" in GUI
        }
        vm.timeStep.addListener((o,ov,nv)-> {//listens to timeStep

            Platform.runLater(()-> myPlayer.controller.TimeStep.textProperty().bind(vm.getProperty("timeStep").asString(String.format("%02d:%02d:%02d", (vm.getProperty("timeStep").intValue()) / 3600,//thread collusion between the view and the model, the view must act before the model, make sure that the model dosent operate the view.
                    (vm.getProperty("timeStep").intValue() % 3600) / 60,vm.getProperty("timeStep").intValue() %60))));//00:00:00

        });
        if (joystick.controller != null) {
            joystick.controller.airspeed.valueProperty().bind(vm.getProperty("airspeed-indicator_indicated-speed-kt"));
            joystick.controller.rudder.valueProperty().bind(vm.getProperty("rudder"));
            joystick.controller.throttle.valueProperty().bind(vm.getProperty("throttle"));
            joystick.controller.height.valueProperty().bind(vm.getProperty("altimeter_indicated-altitude-ft"));
            joystick.controller.degrees.valueProperty().bind(vm.getProperty("indicated-heading-deg"));
            joystick.controller.pitch.valueProperty().bind(vm.getProperty("attitude-indicator_internal-pitch-deg"));
            joystick.controller.roll.valueProperty().bind(vm.getProperty("attitude-indicator_indicated-roll-deg"));
            joystick.controller.yaw.valueProperty().bind(vm.getProperty("side-slip-deg"));
            joystick.controller.setThumbStick();//initialize thumbSticks-radius and elevator and aileron
            joystick.controller.setClocks(vm.setting.getMinMaxSettings());//initialize thumbSticks-radius and elevator and aileron
            joystick.controller.aileron.bind(vm.getProperty("aileron"));
            joystick.controller.elevator.bind(vm.getProperty("elevator"));
        }
        //explanation: connecting all the features in the user interface to their values in the CSV file

        myPlayer.controller.onPlay=vm.play;
        myPlayer.controller.onPause=vm.pause;
        myPlayer.controller.onStop=vm.stop;

        TimeSeriesScript.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->{
            if (graph.controller != null) {
                try {
                    CompletableFuture.runAsync(()->vm.setGraphs(nv.toString())).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                graph.controller.leftChart(vm.featureValue,vm.timeStep.intValue());//the Feature Value
                graph.controller.rightChart(vm.corFeatureValue,vm.timeStep.intValue());//the Cor Feature Value-we dont always have correlated features
                //this operation will show in the top left graph the values of the selected feature from the view list.
                //And in the top right graph this operation will show the values of the most correlated feature to the selected one
            }
        });

        AlgorithmScript.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)-> {
            try {
                handleAlgorithms(nv.toString());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
       /*in this section we handle the chosen algorithm from the bottom view list, that will fill up only after
        choosing anomaly CSV file*/
    }

    public void handleAlgorithms(String algo) throws ExecutionException, InterruptedException {
        if (algo.equals("ZScore")) {
            CompletableFuture.runAsync(()->vm.calcZScore()).get();
            if (graph.controller!=null)
                graph.controller.middleChartZScore(vm.zScoreVisual,vm.maxZScore,vm.timeStep.intValue());
        }

        if (algo.equals("Hybrid")) {
            String temp=CompletableFuture.supplyAsync(()->vm.calcHybrid()).get();
            if (temp.equals("Hybrid")) {
                if (graph.controller != null) {
                    graph.controller.mChartHybrid(vm.hybridCircle, vm.featureValue, vm.corFeatureValue, vm.timeStep.intValue());
                }
            }
            else{
                handleAlgorithms(temp);
            }
        }

        if (algo.equals("Lin Reg")) {
            if (vm.corFeature != null && graph.controller != null) {//if there is no Correlated Feature, there is no Line, this Algo will not work
                double[] arr =CompletableFuture.supplyAsync(()->vm.calcLinReg()).get();
                graph.controller.midChartLinReg(vm.l, arr, vm.featureValue, vm.corFeatureValue, vm.timeStep.intValue());
            }
        }
    }

    public void loadDataFile(ActionEvent actionEvent) {
        csvPath = openFilePath("Select CSV File", "./resources", "CSV Files", "*.csv");
        if (csvPath == null)
        {
            Alert alert= new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("CSV File Path is wrong");
            alert.setContentText("please upload CSV file");
            alert.showAndWait();
            return;
        }
        if (settingsPath == null)
        {
            Alert alert= new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Settings File Error");
            alert.setContentText("please upload Settings file before uploading CSV file");
            alert.showAndWait();
            return;
        }
        //settings file must be declaired before uploading CSV file, if settings file wasn't uploaded make error

        if (csvPath.getFileName().toString().equals("reg_flight.csv"))
            this.vm=new ViewModel(csvPath.toString(),settingsPath.toString());
            //we want to create the viewModel only after we got the CSV "reg_flight" and the Setting file

        else
        if (vm!=null) {
            this.vm.setAnomalyTimeSeries(csvPath.toString());
            String[] temp = {"ZScore", "Hybrid", "Lin Reg"};
            for (String theTemp : temp) {
                AlgorithmScript.getItems().add(theTemp);
                //prints the algorithm names so the user will be able to choose
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Wrong CSV File");
            alert.setContentText("please upload Train CSV File before uploading Test CSV file");
            alert.showAndWait();
            return;
        }
        init();

        if (name == null)
        {
            //if you load the CSV file one more time, this doesnt add values, i clean the listview and print the new values
            name = vm.ts.getName();
            //names of all the features
            for (String theName : name) {
                TimeSeriesScript.getItems().add(theName);
                //print the Script of all the features name in the big viewList at the left side of the GUI
            }
        }
    }

    public void loadSettings(ActionEvent actionEvent) {
        settingsPath = openFilePath("Select Settings File", "./resources", "Txt files", "*.txt");
        if (settingsPath == null) return;
    }


    private Path openFilePath(String title, String pathname, String description, String extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(pathname));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extensions));
        File chosenFile = fileChooser.showOpenDialog(null);
        return chosenFile == null ? null : chosenFile.toPath();
    }
}
