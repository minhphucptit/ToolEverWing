package com.nminh.app.controller;

import com.nminh.app.Main;
import com.nminh.app.common.Util;
import com.nminh.app.common.constant.AppConstant;
import com.nminh.app.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.nminh.app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kong.unirest.*;
import kong.unirest.json.JSONObject;
import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Data
public class AuthController implements Initializable {

      private User user;
      private Stage stage;
      private ObjectMapper objectMapper;

      HomeController mainController;

      public AuthController(Stage stage){
            System.setProperty(AppConstant.CHROME_PROPERTY, AppConstant.CHORME_LOCATION);
            this.stage = stage;
            objectMapper = new JsonObjectMapper();
      };

      @FXML
      public TextField txtName;

      @FXML
      public PasswordField txtPassword;

      @FXML
      Button btnSubmit;

      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {

      }

      public boolean check(){
            String name=txtName.getText().trim();
            String password=txtPassword.getText().trim();

            try {
                  HttpResponse<JsonNode> response = UserService.check(name, password);
                  if (response.getStatus() == HttpStatus.NOT_FOUND) {
                        Util.alertInformation("Thông báo","Lỗi","Email hoặc password không tồn tại");
                        return false;
                  } else {
                        JSONObject jsonObject = response.getBody().getObject();
                        this.user = objectMapper.readValue(jsonObject.toString(), User.class);
                  }
            }catch (Exception e){
                  System.out.println("Server Err!!!");
            }

            return true;
      }

      @FXML
      public void onSubmit(){
            try {
                 if(check()){
                       HomeController homeController = new HomeController(this.user);
                       Parent mainScene = Main.loadFXML("Home.fxml",homeController);
                       Main.scene.setRoot(mainScene);
                       Thread.sleep(200);
                       Main.stage.setMaximized(true);
                 }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
}
