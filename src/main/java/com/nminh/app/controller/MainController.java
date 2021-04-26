package com.nminh.app.controller;

import com.nminh.app.Main;
import com.nminh.app.common.constant.AppConstant;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

      public void newWindowWithParentController(String screenName, Object data, Object controller) throws IOException {
            Stage newWindow = new Stage();
            Parent newScene = null;
            Modality modality=Modality.APPLICATION_MODAL;
            boolean showAndWait = false;
            switch(screenName){
                  case AppConstant.HOME_NAME:{
                        HomeController homeController = new HomeController((AuthController) controller);
                        newScene= Main.loadFXML(AppConstant.HOME_SCREEN,homeController);
                        newWindow.setResizable(false);
                        break;
                  }
                  default:
                        break;
            }
            if(newScene!=null){
                  Scene newScene1 = new Scene(newScene);
                  newWindow.setScene(newScene1);
                  newWindow.initModality(modality);
                  if(showAndWait){
                        newWindow.showAndWait();
                  }else newWindow.show();
            }
      };

}
