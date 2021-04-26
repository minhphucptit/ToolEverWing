package com.nminh.app.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomeController implements Initializable {

      private WebDriver webDriver;
      private WebDriverWait webDriverWait;
      private MainController mainController;
      private final By byTagA = By.tagName("a");
      private final By byTagIframe = By.tagName("iframe");
      private int i=0;


      public HomeController(AuthController authController){
            this.webDriver=authController.getWebDriver();
            this.webDriverWait=authController.getWebDriverWait();
            this.mainController=authController.getMainController();
      }

      @FXML
      public TextField txtUrl;

      @FXML
      public Label lblTotalWorks;

      @FXML
      public TextField txtNumber;

      @FXML
      public BorderPane parent;

      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {
      }

      public void goGame(int number,String linkPost) throws InterruptedException, RuntimeException {
            while (i<number){
                  webDriver.navigate().to(linkPost);
                  webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byTagA));
                  List<WebElement> links = webDriver.findElements(byTagA);
                  for(WebElement link:links){
                        String linkGame=link.getAttribute("href");
                        if(linkGame.contains("instantgames/play")) {
                             link.click();
                              webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byTagIframe));
                              Thread.sleep(13000);
                              break;
                        }
                  }
                  i++;
                  System.out.println(i);
                  lblTotalWorks.textProperty().bind(Bindings.convert(new SimpleIntegerProperty(i)));

            }
      }

      @FXML
      public void onGo() throws InterruptedException {
            txtNumber.setEditable(false);
            txtNumber.setDisable(true);
            String linkPost = txtUrl.getText().trim();
            int number =Integer.parseInt(txtNumber.getText().trim());
            try {
                  goGame(number,linkPost);
            }catch (InterruptedException e){
                  System.out.println("Can't wait element");
                  goGame(number,linkPost);
            }catch (RuntimeException e){
                  goGame(number,linkPost);
            }finally {
                  webDriver.quit();
            }
      }
}
