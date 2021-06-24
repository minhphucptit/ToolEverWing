package com.nminh.app.controller.router;

import com.nminh.app.Main;
import com.nminh.app.controller.HomeController;
import com.nminh.app.controller.SpendingCreateUpdateController;
import com.nminh.app.controller.SpendingListController;
import com.nminh.app.model.Spending;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SpendingScreenRouter implements ScreenRouter{
    private HashMap<String, Tab> openTabs;
    private TabPane tabPanelMain;
    private Integer userId;
    private HomeController controller;

    public SpendingScreenRouter(HomeController homeController){
        this.openTabs = homeController.getOpenTabs();
        this.tabPanelMain = homeController.getTabPanelMain();
        this.userId = homeController.getUser().getId();
        this.controller = homeController;
    }

    @Override
    public void onLeftSideBarItemClick(String screenName) {
        if (null != screenName) {
            switch (screenName) {
                case "SpendingList":{
                    SpendingListController spendingListController = new SpendingListController(controller);
                    controller.openTab("SpendingListScreen.fxml", spendingListController, "Danh sách chi tiêu");
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
            case "SpendingCreateUpdate":{
                SpendingCreateUpdateController spendingCreateUpdateController = new SpendingCreateUpdateController((SpendingListController)controller,(Spending)data);
                newScene = Main.loadFXML("SpendingCreateUpdateScreen.fxml", spendingCreateUpdateController);
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
