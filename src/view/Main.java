package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxl=new FXMLLoader();
        Parent root = fxl.load(getClass().getResource("Window.fxml").openStream());
        WindowController wc=fxl.getController();

        primaryStage.setTitle("PTM Project");
        primaryStage.setScene(new Scene(root, 1100, 500));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
