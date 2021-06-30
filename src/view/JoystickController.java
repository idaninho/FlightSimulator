package view;

import eu.hansolo.medusa.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class JoystickController {

    private static double BORDER_RADIUS;
    private static double THUMBSTICK_RADIUS;

    @FXML
    public Circle border;
    @FXML public Circle thumbStick;
    @FXML public Slider rudder;
    @FXML public Slider throttle;
    @FXML public Gauge height;
    @FXML public Gauge degrees;
    @FXML public Gauge roll;
    @FXML public Gauge pitch;
    @FXML public Gauge yaw;
    @FXML public Gauge airspeed;




    public DoubleProperty aileron = new SimpleDoubleProperty();
    public DoubleProperty elevator = new SimpleDoubleProperty();

    public void setThumbStick() {

        BORDER_RADIUS = border.radiusProperty().doubleValue();
        THUMBSTICK_RADIUS = thumbStick.radiusProperty().doubleValue();

        aileron.addListener(((observable, oldValue, newValue) -> thumbStick.setCenterX(aileron.get() * THUMBSTICK_RADIUS)));
        elevator.addListener((observable -> thumbStick.setCenterY(elevator.get() * THUMBSTICK_RADIUS)));

    }
    public void setClocks(HashMap<String, String> settings) {
        RadialGradient gradient1 = new RadialGradient(0,
                .1,
                10,
                10,
                7,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.BLUE),
                new Stop(1, Color.BLACK));
        airspeed.setBackgroundPaint(gradient1);
        height.setBackgroundPaint(gradient1);
        degrees.setBackgroundPaint(gradient1);
        pitch.setBackgroundPaint(gradient1);
        yaw.setBackgroundPaint(gradient1);
        roll.setBackgroundPaint(gradient1);

        String[] feature= settings.keySet().toArray(new String[0]);
        String featureName;
        String line;
        float temp;
        int min,max;


        for (int i=0;i<settings.size();i++) {
            featureName=feature[i];
            line=settings.get(featureName);
            String []split=line.split(":");
            temp= Float.parseFloat(split[0]);
            min= (int) (temp);
            if(temp<0)
                min--;
            temp= Float.parseFloat(split[1]);
            max=(int) (temp);
            setClock(featureName,min,max);
        }
    }

    private void setClock(String featureName, float min, float max) {
       /* Height,-13.62:699.26
Speed,0:93.41
Degrees,0:357.95
Yaw,-28.06:89.87
Pitch,-9.503:16.65
Roll,-37.44:40

        */
        switch (featureName) {
            case "Height": {
                height.setTitle(featureName);
                height.setMinValue(min);
                height.setMaxValue(max);
                height.setTitleColor(Color.WHITE);
                height.setBorderPaint(Color.HONEYDEW);
                height.setForegroundBaseColor(Color.WHITE);
                break;
            }
            case "Speed": {
                airspeed.setTitle(featureName);
                airspeed.setCustomTickLabels("0","","20","","40","","60","","80","100");
                airspeed.setCustomTickLabelFontSize(70);
                airspeed.setCustomTickLabelsEnabled(true);
                airspeed.setMinValue(min);
                airspeed.setMaxValue(max);
                airspeed.setTitleColor(Color.WHITE);
                airspeed.setBorderPaint(Color.HONEYDEW);
                airspeed.setForegroundBaseColor(Color.WHITE);
                break;
            }
            case "Degrees": {
                degrees.setStartAngle(180);
                degrees.setAngleRange(350);
                degrees.setTitle(featureName);
                degrees.setCustomTickLabels("N","","","","","","","","","E","","","","","","","","","S","","","","","","","","","W","","","","","","","","");
                degrees.setCustomTickLabelFontSize(70);
                degrees.setCustomTickLabelsEnabled(true);
                degrees.setMinValue(min);
                degrees.setMaxValue(max);
                degrees.setTitleColor(Color.WHITE);
                degrees.setBorderPaint(Color.HONEYDEW);
                degrees.setForegroundBaseColor(Color.WHITE);
                break;
            }
            case "Yaw": {
                yaw.setTitle(featureName);
                yaw.setCustomTickLabels("-30","","0","","","30","","60","","90","");
                yaw.setCustomTickLabelFontSize(70);
                yaw.setCustomTickLabelsEnabled(true);
                yaw.setMinValue(min);
                yaw.setMaxValue(max);
                yaw.setTitleColor(Color.WHITE);
                yaw.setBorderPaint(Color.HONEYDEW);
                yaw.setForegroundBaseColor(Color.WHITE);
                break;
            }
            case "Pitch": {
                pitch.setTitle(featureName);
                pitch.setCustomTickLabels("-10","0","10","20");
                pitch.setCustomTickLabelFontSize(70);
                pitch.setCustomTickLabelsEnabled(true);
                pitch.setMinValue(min);
                pitch.setMaxValue(max);
                pitch.setTitleColor(Color.WHITE);
                pitch.setBorderPaint(Color.HONEYDEW);
                pitch.setForegroundBaseColor(Color.WHITE);
                break;
            }
            case "Roll": {
                roll.setTitle(featureName);
                roll.setMinValue(min);
                roll.setMaxValue(max);
                roll.setTitleColor(Color.WHITE);
                roll.setBorderPaint(Color.HONEYDEW);
                roll.setForegroundBaseColor(Color.WHITE);
                break;
            }


        }
    }
}
