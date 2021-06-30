package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Graph extends VBox {
    public final GraphController controller;
    public Graph() {
        FXMLLoader fxl=new FXMLLoader();
        VBox hb=null;
        try {
            hb = fxl.load(getClass().getResource("Graph.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hb!=null)
        {
            controller=fxl.getController();
            this.getChildren().add(hb);
        }
        else
            controller=null;
    }
}
