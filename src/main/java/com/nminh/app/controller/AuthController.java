package com.nminh.app.controller;

import com.nminh.app.common.constant.AppConstant;
import com.nminh.app.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Data
public class AuthController implements Initializable {
      private WebDriver webDriver;
      private WebDriverWait webDriverWait;
      private User user;
      private final By byUsername = By.xpath("//input[@name='email']");
      private final By byPassword = By.xpath("//input[@name='pass']");
      private final By byLogin    = By.xpath("//button[@name='login']");

      MainController mainController;

      public AuthController(){
            System.setProperty(AppConstant.CHROME_PROPERTY, AppConstant.CHORME_LOCATION);
            mainController=new MainController();
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--window-position=-32000,-32000");
            this.webDriver=new ChromeDriver(options);
            webDriverWait=new WebDriverWait(webDriver,15);
            this.webDriver.navigate().to("https://facebook.com");
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

      public void goLogin(){
            String name=txtName.getText().trim();
            String password=txtPassword.getText().trim();

            if (name.isEmpty() || password.isEmpty()) {
                  System.out.println("Loi text null");
            }else {
                  webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(this.byUsername));
                  webDriver.findElement(byUsername).clear();
                  webDriver.findElement(byUsername).sendKeys(name);

                  webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(this.byPassword));
                  webDriver.findElement(byPassword).clear();
                  webDriver.findElement(byPassword).sendKeys(password);
                  webDriver.findElement(byLogin).click();
            }

      }

      @FXML
      public void onSubmit(){
            try {
                  goLogin();
                  mainController.newWindowWithParentController(AppConstant.HOME_NAME,webDriver,AuthController.this);
                  btnSubmit.getScene().getWindow().hide();
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }
}
