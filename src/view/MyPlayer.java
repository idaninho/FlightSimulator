package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import view.MyPlayerController;

import java.io.IOException;

public class MyPlayer extends HBox {

    public final MyPlayerController controller;
    public MyPlayer() {
        FXMLLoader fxl=new FXMLLoader();
        HBox hb=null;
        try {
            hb = fxl.load(getClass().getResource("MyPlayer.fxml").openStream());
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
