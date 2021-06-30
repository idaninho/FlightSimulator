package view;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


public class MyPlayerController {
    public Runnable onPlay,onPause,onStop;
    @FXML
    public Label TimeStep;
    @FXML
    public Slider timeSlider;
    @FXML
    public TextField speedControl=new TextField();
    public DoubleProperty speed=new SimpleDoubleProperty();

    public void play() {
        if (onPlay!=null) {
            onPlay.run();
        }
    }

    public void pause() {
        if (onPause!=null)
            onPause.run();
    }
    public void stop() {
        if (onStop!=null)
            onStop.run();
    }

    public void forward() {
        this.timeSlider.valueProperty().set(timeSlider.valueProperty().doubleValue()+speed.doubleValue()*5);
    }

    public void rewind() {
        this.timeSlider.valueProperty().set(timeSlider.valueProperty().doubleValue()-speed.doubleValue()*5);
    }


    public void setTimeSlider(MouseEvent evt) {
        this.timeSlider.valueProperty().set(timeSlider.valueProperty().doubleValue());
    }

    public void setSpeedControl (ActionEvent event) {
        speedControl.getText();
        if (Double.parseDouble(speedControl.getText())<0)
        {
            speedControl.clear();
            return;
        }
        this.speed.set(Double.parseDouble(speedControl.getText())*10);// times 10
        speedControl.clear();
    }
}
