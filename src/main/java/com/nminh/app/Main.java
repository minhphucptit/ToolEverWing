package com.nminh.app;

import com.nminh.app.common.constant.AppConstant;
import com.nminh.app.controller.AuthController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Data;

public class Main extends Application {

    public static Scene scene;
    public static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        AuthController controller=new AuthController(primaryStage);
        stage = primaryStage;
        scene = new Scene(Main.loadFXML(AppConstant.AUTHEN_SCREEN,controller));
//        Parent root = loadFXML(AppConstant.AUTHEN_SCREEN,controller);
        primaryStage.setTitle("Money Savings");
        stage.getIcons().add(new Image(Main.class.getResource("view/logog.png").toString()));
        primaryStage.setScene(scene);
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
