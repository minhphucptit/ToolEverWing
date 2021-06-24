package com.nminh.app.controller.router;

import com.nminh.app.Main;
import com.nminh.app.controller.*;
import com.nminh.app.model.Intended;
import com.nminh.app.model.Spending;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class IntendedScreenRouter implements ScreenRouter{
    private HashMap<String, Tab> openTabs;
    private TabPane tabPanelMain;
    private Integer userId;
    private HomeController controller;

    public IntendedScreenRouter(HomeController homeController){
        this.openTabs = homeController.getOpenTabs();
        this.tabPanelMain = homeController.getTabPanelMain();
        this.userId = homeController.getUser().getId();
        this.controller = homeController;
    }

    @Override
    public void onLeftSideBarItemClick(String screenName) {
        if (null != screenName) {
            switch (screenName) {
                case "IntendedList":{
                    IntendedListController intendedListController = new IntendedListController(controller);
                    controller.openTab("IntendedListScreen.fxml", intendedListController, "Danh sách dự định");
                    break;
                }
                default:break;
            }
        }
    }

    @Override
    public void newWindowWithParentController(String screenNameAction, Object data, Object controller) throws IOException {
        Stage newWindow = new Stage();
        Parent newScene = null;
        Modality modality = Modality.APPLICATION_MODAL;  // 1 số màn hình cần phải đặt modal này để maximize
        boolean showAndWait = false;
        switch (screenNameAction) {
            case "IntendedCreateUpdate":{
                IntendedCreateUpdateController intendedCreateUpdateController = new IntendedCreateUpdateController((IntendedListController)controller,(Intended)data);
                newScene = Main.loadFXML("IntendedCreateUpdateScreen.fxml", intendedCreateUpdateController);
                newWindow.setResizable(false);
                break;
            }
            default:break;
        }
        if (newScene != null) {
            Scene secondScene = new Scene(newScene);
//            newWindow.getIcons().add(new Image(Main.class.getResource("logog.png").toString()));
            newWindow.setScene(secondScene);
            newWindow.initModality(modality);
            if (showAndWait) {
                newWindow.showAndWait();
            } else {
                newWindow.show();
            }
        }
    }

    @Override
    public void newTabWithParent(String screenNameAction, Object data, Object controller) {

    }
}
