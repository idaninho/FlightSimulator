package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import test.Line;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class GraphController implements Initializable {

    @FXML
    private LineChart<?, ?> chartZscore;

    @FXML
    private LineChart<?, ?> chartCorrelated;

    @FXML
    private ScatterChart <?, ?>chartCombined;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void clear() {
        chartCombined.getData().clear();
    }
    public void leftChart(float[] arr, int time) {
        XYChart.Series series = new XYChart.Series();
        chartZscore.getData().clear();
        String temp;
        for (int i = 0; i < time; i++) {
            temp = Integer.toString(i);
            series.getData().add(new XYChart.Data(temp, arr[i]));
        }
        chartZscore.getData().add(series);
    }

    public void rightChart(float[] arr, int time) {
        XYChart.Series series = new XYChart.Series();
        chartCorrelated.getData().clear();
        String temp;
        for (int i = 0; i < time; i++) {
            temp = Integer.toString(i);
            series.getData().add(new XYChart.Data(temp, arr[i]));
        }
        chartCorrelated.getData().add(series);
    }

    public void middleChartZScore(float[] arr, float max, int time) {
        chartCombined.getData().clear();
        XYChart.Series series = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        chartCombined.getData().clear();
        for (int i = 0; i < time; i++) {
            if(arr[i]>max)
                series2.getData().add(new XYChart.Data(i, arr[i]));
            else
                series.getData().add(new XYChart.Data(i, arr[i]));
        }
        chartCombined.getData().add(series);
        chartCombined.getData().add(series2);
    }

    public void mChartHybrid(Circle c, float[] feature1, float[] feature2, int time) {
        chartCombined.getData().clear();
        XYChart.Series series = new XYChart.Series();
        double newX;
        double newY;
        double iToRad;
        for (int i = 0; i <= 360;) {
            iToRad = Math.toRadians(i);
            newX = c.getCenterX() + (c.getRadius() * Math.cos(iToRad));
            newY = c.getCenterY() + (c.getRadius() * Math.sin(iToRad));
            series.getData().add(new XYChart.Data((newX), newY));
            i += 3;
        }
        chartCombined.getData().add(series);
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        for (int i = 0; i < time; i++) {
            double distX,distY,dist;
            distX=Math.pow(c.getCenterX()-feature1[i],2);
            distY=Math.pow(c.getCenterY()-feature2[i],2);
            dist=Math.sqrt(distX+distY);
            if (dist>c.getRadius()) {
                series2.getData().add(new XYChart.Data(feature1[i], feature2[i]));
            }
            else
                series3.getData().add(new XYChart.Data(feature1[i], feature2[i]));
        }
        chartCombined.getData().add(series3);
        chartCombined.getData().add(series2);
    }


    public void midChartLinReg(Line l, double[] arr, float[] feature1, float[] feature2, int time) {
        XYChart.Series series = new XYChart.Series();
        chartCombined.getData().clear();
        for (int i = 0; i <= 100;) {
            double y=l.f(i);
            double x=i;
            series.getData().add(new XYChart.Data(x, y));
            i+=3;
        }
        chartCombined.getData().add(series);


        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        int j=0;
        for (int i = 0; i < time; i++) {
            if (arr[j]==i&&arr[j]!=0) {
                series3.getData().add(new XYChart.Data(feature1[i], feature2[i]));
                j++;
            }
            else
                series2.getData().add(new XYChart.Data(feature1[i], feature2[i]));
        }
        chartCombined.getData().add(series3);
        chartCombined.getData().add(series2);
    }
}

