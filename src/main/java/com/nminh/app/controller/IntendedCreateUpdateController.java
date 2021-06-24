package com.nminh.app.controller;

import com.nminh.app.common.IntendedStatusEnum;
import com.nminh.app.common.Util;
import com.nminh.app.model.Intended;
import com.nminh.app.model.Spending;
import com.nminh.app.service.IntendedService;
import com.nminh.app.service.SpendingService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class IntendedCreateUpdateController implements Initializable {
    private Integer userId;
    private IntendedListController intendedListController;
    private String title;
    private HomeController homeController;
    private Intended intended;
    private boolean isEdit;

    public IntendedCreateUpdateController(IntendedListController intendedListController, Intended data) {
        this.intendedListController = intendedListController;
        this.homeController = intendedListController.getHomeController();
        this.userId = intendedListController.getUserId();
        title = "Tạo dự định";
        if(data!=null){
            isEdit=true;
            this.intended=data;
            title = "Chi tiết dự định";
        }
    }

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<IntendedStatusEnum> cbStatus;

    @FXML
    public DatePicker dpkCreateAt;

    @FXML
    public DatePicker dpkFinishAt;

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

    @FXML
    public Label lbStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cbStatus.getItems().addAll(IntendedStatusEnum.values());
       cbStatus.getItems().remove(IntendedStatusEnum.ALL);
       cbStatus.getSelectionModel().select(0);
       cbStatus.setVisible(false);
       lbStatus.setVisible(false);
//        validateForm();
        btnUpdate.setVisible(false);
        lbTitle.setText(title);
        if (this.isEdit) {
            // Nếu là TH nhân bản thì setup các trường dựa theo originContract
            setupClone(this.intended);
            btnUpdate.setVisible(true);
            btnCreate.setVisible(false);
            cbStatus.setVisible(true);
            lbStatus.setVisible(true);
        }
        cbStatus.setConverter(new StringConverter<IntendedStatusEnum>() {
            @Override
            public String toString(IntendedStatusEnum object) {
                if(object==null) return "";
                return object.getName();
            }

            @Override
            public IntendedStatusEnum fromString(String string) {
                return null;
            }
        });
    }

    public void setupClone(Intended intended){
        String name = intended.getName();
        String status = intended.getStatus();
        Integer quantity = intended.getQuantity();
        Float cost = intended.getCost();
        Long createAt = intended.getCreateAt();
        Long finishAt = intended.getFinishAt();
        String content = intended.getContent();

        if(name!=null) txtName.setText(name);
        if(status!=null){
            cbStatus.getSelectionModel().select(IntendedStatusEnum.valueOf(status));
        }
        if(quantity!=null) txtQuantity.setText(Integer.toString(quantity));
        if(cost != null) {
            BigDecimal bigNumber = new BigDecimal(cost);
            txtCost.setText(bigNumber.toString());
        }
        if(content!=null) txtContent.setText(content);
        if(createAt!=null) {
            LocalDate localDate = Instant.ofEpochSecond(createAt).atZone(ZoneId.systemDefault()).toLocalDate();
            dpkCreateAt.setValue(localDate);
        }
        if(finishAt!=null) {
            LocalDate localDate = Instant.ofEpochSecond(finishAt).atZone(ZoneId.systemDefault()).toLocalDate();
            dpkFinishAt.setValue(localDate);
        }
    }

    public JSONObject getFormData(){
        JSONObject data = new JSONObject();

        try {

            String name = txtName.getText();
            String status = cbStatus.getValue().name();
            Integer quantity = Integer.valueOf(txtQuantity.getText().trim());
            LocalDate localDate = this.dpkCreateAt.getConverter().fromString(dpkCreateAt.getEditor().getText());
            Long createAt = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
            LocalDate localDate1 = this.dpkFinishAt.getConverter().fromString(dpkCreateAt.getEditor().getText());
            Long finishAt = localDate1.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
            Float cost = Float.valueOf(txtCost.getText().trim());
            String content = txtContent.getText();

            data.put("user_id", userId);
            data.put("name", name);
            data.put("status", status);
            data.put("cost", cost);
            data.put("quantity", quantity);
            data.put("create_at", createAt);
            data.put("finish_at", finishAt);
            if (!content.isEmpty())
                data.put("content", content);
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

            HttpResponse<JsonNode> response = IntendedService.createIntended(data);

            if (response.getStatus() == HttpStatus.CREATED) {
                Util.alertInformation("Thông báo", "Thành công", "Thêm dự định thành công");
                btnCreate.getScene().getWindow().hide();
                intendedListController.refresh();
            } else {
                Util.alertInformation("Thông báo", "Lỗi", "Có lỗi xảy ra");
            }
        }
    }

    @FXML
    public void onUpdateSpending(){
        JSONObject data = getFormData();

        HttpResponse<JsonNode> response = IntendedService.updateIntended(this.intended.getId(),data);

        if(response.getStatus()== HttpStatus.OK){
            Util.alertInformation("Thông báo","Thành công","chỉnh sửa dự định thành công");
            btnCreate.getScene().getWindow().hide();
            intendedListController.refresh();
        }else {
            Util.alertInformation("Thông báo","Lỗi","Có lỗi xảy ra");
        }
    }
}
