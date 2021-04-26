package com.nminh.app;

import com.nminh.app.common.constant.AppConstant;
import com.nminh.app.controller.AuthController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        AuthController controller=new AuthController();
        Parent root = loadFXML(AppConstant.AUTHEN_SCREEN,controller);
        primaryStage.setTitle("EverWing");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Parent loadFXML(String location,Object controller)  throws IOException {
        FXMLLoader fxmlLoader =new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("view/"+location));
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

}
