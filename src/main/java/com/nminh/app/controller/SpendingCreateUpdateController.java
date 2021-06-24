package com.nminh.app.controller;

import com.nminh.app.common.Util;
import com.nminh.app.controller.router.ScreenRouter;
import com.nminh.app.model.Spending;
import com.nminh.app.service.SpendingService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SpendingCreateUpdateController implements Initializable {

    private Integer userId;
    private SpendingListController spendingListController;
    private String title;
    private HomeController homeController;
    private Spending spending;
    private boolean isEdit;

    public SpendingCreateUpdateController(SpendingListController spendingListController, Spending data) {
        this.spendingListController = spendingListController;
        this.homeController = spendingListController.getHomeController();
        this.userId = spendingListController.getUserId();
        title = "Tạo chi tiêu";
        if(data!=null){
            isEdit=true;
            this.spending=data;
            title = "Chi tiết chi tiêu";
        }
    }

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<String> cbCategory;

    @FXML
    public ComboBox<String> cbType;

    @FXML
    public DatePicker dpkDate;

    @FXML
    public TextField txtQuantity;

    @FXML
    public TextField txtCost;

    @FXML
    public TextArea txtContent;

    @FXML
    public Button btnCreate;

    @FXML
    public Button btnUpdate;

    @FXML
    public Label lbNameErr;

    @FXML
    public Label lbDateErr;

    @FXML
    public Label lbQuantityErr;

    @FXML
    public Label lbCostErr;

    @FXML
    public Label lbTitle;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCategory.getItems().addAll("Định kỳ","Không định kỳ");
        cbCategory.getSelectionModel().select(0);
        cbType.getItems().addAll("Vào","Ra");
        cbType.getSelectionModel().select(0);
//        validateForm();
        btnUpdate.setVisible(false);
        lbTitle.setText(title);
        if (this.isEdit) {
            // Nếu là TH nhân bản thì setup các trường dựa theo originContract
            setupClone(this.spending);
            btnUpdate.setVisible(true);
            btnCreate.setVisible(false);
        }
    }

    public void setupClone(Spending spending){
        String name = spending.getName();
        String category = spending.getCategory();
        String type = spending.getType();
        Integer quantity = spending.getQuantity();
        Float cost = spending.getCost();
        Long date = spending.getDate();
        String content = spending.getContent();

        if(name!=null) txtName.setText(name);
        if(category!=null){
            cbCategory.getSelectionModel().select(category.equals("FREQUENCY")?"Định kỳ":"Không định kỳ");
        }
        if(type!=null){
            cbType.getSelectionModel().select(type.equals("IN")?"Vào":"Ra");
        }
        if(quantity!=null) txtQuantity.setText(Integer.toString(quantity));
        if(cost != null) {
            BigDecimal bigNumber = new BigDecimal(cost);
            txtCost.setText(bigNumber.toString());
        }
        if(content!=null) txtContent.setText(content);
        if(date!=null) {
            LocalDate localDate = Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault()).toLocalDate();
            dpkDate.setValue(localDate);
        }
    }

    public JSONObject getFormData(){
        JSONObject data = new JSONObject();

        try {
            String name = txtName.getText();
            String category = cbCategory.getValue().equals("Định kỳ") ? "FREQUENCY" : "FORTUITY";
            String type = cbType.getValue().equals("Vào") ? "IN" : "OUT";
            Integer quantity = Integer.valueOf(txtQuantity.getText().trim());
            LocalDate localDate = this.dpkDate.getConverter().fromString(dpkDate.getEditor().getText());
            Long date = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
            Float cost = Float.valueOf(txtCost.getText().trim());
            String content = txtContent.getText();

        data.put("user_id",userId);
        data.put("name",name);
        data.put("type",type);
        data.put("category",category);
        data.put("cost",cost);
        data.put("quantity",quantity);
        data.put("date",date);
        if(!content.isEmpty())
            data.put("content",content);
        }catch (Exception e){
            Util.alertInformation("Thông báo","Lỗi","Phải nhập đủ thông tin");
            return null;
        }

        return data;
    }

    @FXML
    public void onCreateSpending(){
        JSONObject data = getFormData();

        if(data!=null) {

            HttpResponse<JsonNode> response = SpendingService.createSpending(data);

            if (response.getStatus() == HttpStatus.CREATED) {
                Util.alertInformation("Thông báo", "Thành công", "Thêm chi tiêu thành công");
                btnCreate.getScene().getWindow().hide();
                spendingListController.refresh();
            } else {
                Util.alertInformation("Thông báo", "Lỗi", "Có lỗi xảy ra");
            }
        }
    }

    @FXML
    public void onUpdateSpending(){
        JSONObject data = getFormData();

        HttpResponse<JsonNode> response = SpendingService.updateSpending(this.spending.getId(),data);

        if(response.getStatus()== HttpStatus.OK){
            Util.alertInformation("Thông báo","Thành công","chỉnh sửa chi tiêu thành công");
            btnCreate.getScene().getWindow().hide();
            spendingListController.refresh();
        }else {
            Util.alertInformation("Thông báo","Lỗi","Có lỗi xảy ra");
        }
    }
}
