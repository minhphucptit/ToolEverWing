package com.nminh.app.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.nminh.app.Main;
import com.nminh.app.controller.router.IntendedScreenRouter;
import com.nminh.app.controller.router.ScreenRouter;
import com.nminh.app.controller.router.SpendingScreenRouter;
import com.nminh.app.model.TreeItemInfo;
import com.nminh.app.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class HomeController implements Initializable {

      private User user;
      private HashMap<String, Tab> openTabs;
      private ScreenRouter spendingRouter;
      private ScreenRouter intendedRouter;


      public HomeController(User user){
            this.user = user;
            this.openTabs = new HashMap<>();
      }

      @FXML
      public Label lblInfo;

      @FXML
      public Label lblFullName;

      @FXML
      public TreeView<TreeItemInfo> treeMenu;

      @FXML
      public ImageView imgView;

      @FXML
      public TabPane tabPanelMain;

      @FXML
      public ProgressBar progressBar;

      @FXML
      public Label lblPercentage;

      @FXML
      public MenuItem menuLogout;

      @FXML
      public Circle avatar;

      @FXML
      public MenuItem menuAccount;

      @FXML
      public GridPane leftSideBarPane;

      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {
            this.tabPanelMain.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
            lblInfo.setText(user.getName());
            loadTreeMenu();
            System.out.println(user.toString());
            Thread loadAvatarThread = new Thread(() -> {
                  Image imageAvatar = new Image(Main.class.getResource(user.getImgLink()).toString());
                  ImagePattern imagePattern = new ImagePattern(imageAvatar);
                  avatar.setFill(imagePattern);
            });
            loadAvatarThread.setDaemon(true);
            loadAvatarThread.start();
      }

      public void loadTreeMenu() {
            treeMenu.setCellFactory((p) -> {

                  TreeCell<TreeItemInfo> cell = new TreeCell<>() {
                        @Override
                        public void updateItem(TreeItemInfo item, boolean empty) {
                              super.updateItem(item, empty);
                              setText(null);
                              setGraphic(null);
                              setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                              if (!empty) {
                                    Text t = new Text(item.getDisplayText());
                                    t.setWrappingWidth(treeMenu.getWidth() - 70);
                                    switch (item.getStyleClass()) {
                                          case "header":
                                                t.getStyleClass().add("left-sidebar-header");
                                                break;
                                          case "item":
                                                t.getStyleClass().add("left-sidebar-item");
                                                break;
                                          case "item2":
                                                t.getStyleClass().add("left-sidebar-item2");
                                                break;
                                          default:
                                                break;
                                    }
                                    setGraphic(t);
                              }
                        }
                  };

                  cell.setOnMouseClicked(event -> {
                        if (!cell.isEmpty()) {
                              TreeItem<TreeItemInfo> treeItem = cell.getTreeItem();
                              try {
                                    onLeftSideBarItemClick(treeItem.getValue().getScreenName());
                              } catch (IOException ex) {

                              }
                        }
                  });
                  return cell;
            });
            TreeItem<TreeItemInfo> root = new TreeItem<>(new TreeItemInfo("", "", ""));
            // Các tính năng cho tất cả người dùng thì ở trên đầu
            spendingRouter = new SpendingScreenRouter(this);
            intendedRouter = new IntendedScreenRouter(this);
            loadTreeItem(root);
            treeMenu.setRoot(root);
            treeMenu.setShowRoot(false);

      }


      public TreeItem<TreeItemInfo> getLeftSideBarNode(String displayText, String screenName, String styleClass) {
            TreeItem<TreeItemInfo> node = new TreeItem<>(new TreeItemInfo(displayText, screenName, styleClass));

            return node;
      }
      //should remove
      public void openTab(String fxmlFile, Object controller, String name) {
            if (openTabs.containsKey(fxmlFile)) {
                  this.tabPanelMain.getSelectionModel().select(openTabs.get(fxmlFile));
            } else {
                  try {
                        Tab myNewTab = new Tab(name);
                        Parent content = Main.loadFXML(fxmlFile, controller);
                        myNewTab.setContent(content);
                        this.tabPanelMain.getTabs().add(myNewTab);
                        openTabs.put(fxmlFile, myNewTab);
                        myNewTab.setOnClosed(e -> openTabs.remove(fxmlFile));
                        this.tabPanelMain.getSelectionModel().select(openTabs.get(fxmlFile));

                        // Create ContextMenu
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem item1 = new MenuItem("Đóng tất cả");
                        item1.setOnAction(event -> {
                              //giữ lại trang chủ
                              Tab home = tabPanelMain.getTabs().get(0);
                              tabPanelMain.getTabs().clear();
                              tabPanelMain.getTabs().add(home);
                              openTabs.clear();
                        });

                        MenuItem item2 = new MenuItem("Đóng");
                        item2.setOnAction(event -> {
                              tabPanelMain.getTabs().remove(tabPanelMain.getSelectionModel().getSelectedItem());
                              openTabs.remove(fxmlFile);
                        });
                        // Add MenuItem to ContextMenu
                        contextMenu.getItems().addAll(item2, item1);

                        myNewTab.contextMenuProperty().set(contextMenu);

                  } catch (IOException e1) {
                        e1.printStackTrace();
                  }
            }
      }

      public void openNewTab(String fxmlFile, Object controller, String name) {
            try {
                  Tab myNewTab = new Tab(name);
                  Parent content = Main.loadFXML(fxmlFile, controller);
                  myNewTab.setContent(content);
                  this.tabPanelMain.getTabs().add(myNewTab);
                  openTabs.put(fxmlFile, myNewTab);
                  myNewTab.setOnClosed(e -> openTabs.remove(fxmlFile));
                  this.tabPanelMain.getSelectionModel().select(openTabs.get(fxmlFile));
            } catch (IOException e1) {
                  e1.printStackTrace();
            }
      }

      public void loadTreeItem(TreeItem<TreeItemInfo> root){
            TreeItem<TreeItemInfo> nodeRP = getLeftSideBarNode("Quản lý chi tiêu", "SpendingList", "header");
            TreeItem<TreeItemInfo> nodeRP1 = getLeftSideBarNode("Quản lý dự định", "IntendedList", "header");
            TreeItem<TreeItemInfo> nodeRP2 = getLeftSideBarNode("Quản lý tiết kiệm", null, "header");
            TreeItem<TreeItemInfo> nodeC1 = getLeftSideBarNode("Thống kê tiết kiệm", null, "item");
            TreeItem<TreeItemInfo> nodeC2 = getLeftSideBarNode("Gợi ý tiết kiệm", null, "item");
            nodeRP2.getChildren().addAll(nodeC1,nodeC2);
            root.getChildren().addAll(nodeRP,nodeRP1,nodeRP2);
      }

      public void onLeftSideBarItemClick(String screenName) throws IOException {

            if (null != screenName) {
                  spendingRouter.onLeftSideBarItemClick(screenName);
                  intendedRouter.onLeftSideBarItemClick(screenName);
            }

      }

      public void newWindowWithParentController(String screenNameAction, Object data, Object controller) throws IOException{
            if(spendingRouter!=null)
                  spendingRouter.newWindowWithParentController(screenNameAction,data,controller);
            if(intendedRouter!=null)
                  intendedRouter.newWindowWithParentController(screenNameAction,data,controller);
      }

      public void newTabWithParent(String screenNameAction, Object data, Object controller) {

      }

      @FXML
      public void onLogoutClick() throws IOException {
            Stage stage = Main.stage;
            AuthController authController = new AuthController(stage);
            Main.scene.setRoot(Main.loadFXML("authenScreen.fxml", authController));
            stage.setMaximized(false);
      }

      @FXML
      public void onAccountClick() {

      }

      @FXML
      public void collapse() {
            leftSideBarPane.setPrefWidth(leftSideBarPane.getPrefWidth() == 0 ? -1.0 : 0.0);
      }


}
