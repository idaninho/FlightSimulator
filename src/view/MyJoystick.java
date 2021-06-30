package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MyJoystick extends AnchorPane {

    public final JoystickController controller;
    public MyJoystick() {
        FXMLLoader fxl=new FXMLLoader();
        AnchorPane hb=null;
        try {
            hb = fxl.load(getClass().getResource("MyJoystick.fxml").openStream());
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
