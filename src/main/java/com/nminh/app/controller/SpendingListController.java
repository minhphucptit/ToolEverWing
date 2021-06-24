package com.nminh.app.controller;

import com.nminh.app.common.Util;
import com.nminh.app.controller.router.SpendingScreenRouter;
import com.nminh.app.model.Spending;
import com.nminh.app.service.SpendingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

@Data
public class SpendingListController implements Initializable {
    private Integer userId;
    private ObjectMapper objectMapper;
    private HomeController homeController;

    public SpendingListController(HomeController controller){
        this.userId = controller.getUser().getId();
        this.objectMapper = new JsonObjectMapper();
        this.homeController=controller;
    }

    @FXML
    public TableView<Spending> tbSpending;

    @FXML
    public TableColumn<Spending,String> clStt;

    @FXML
    public TableColumn<Spending,String> clName;

    @FXML
    public TableColumn<Spending,String> clType;

    @FXML
    public TableColumn<Spending,String> clCategory;

    @FXML
    public TableColumn<Spending,String> clQuantity;

    @FXML
    public TableColumn<Spending,String> clCost;

    @FXML
    public TableColumn<Spending,String> clDate;

    @FXML
    public TableColumn<Spending,Void> clAction;

    @FXML
    public Button btnAdd;

    @FXML
    public Button btnSearch;

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<String> cbCategory;

    @FXML
    public ComboBox<String> cbType;

    @FXML
    public DatePicker dpkStart;

    @FXML
    public DatePicker dpkEnd;

    @FXML
    public Label lbTotalItems;

    @FXML
    public Pagination pnPagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpComboboxCategoryAndComboboxType();
        setUpTable();
        getSpendingListToTable();
    }

    public void setUpComboboxCategoryAndComboboxType(){
        cbCategory.getItems().addAll("Tất cả","Định kỳ","Không định kỳ");
        cbCategory.getSelectionModel().select(0);
        cbType.getItems().addAll("Tất cả","Vào","Ra");
        cbType.getSelectionModel().select(0);
    }

    public void setUpTable(){
        clStt.setCellFactory(cellData -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(Integer.toString(getIndex() + 1));
                }
            }
        });
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        clType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().equals("IN")?"Vào":"Ra"));
        clCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().equals("FREQUENCY")?"Định kỳ":"Không định kỳ"));
        clCost.setCellValueFactory(cellData -> new SimpleStringProperty(new BigDecimal(cellData.getValue().getCost()).toString()));
        clDate.setCellValueFactory(cellData -> {
            Long date = cellData.getValue().getDate();
            if (date instanceof Long) {
                return new SimpleStringProperty(Util.formatSecondsToOnlyDate(date));
            }
            return null;
        });
        clAction.setCellFactory(param -> new TableCell<Spending, Void>() {
            private final Button btnEdit = new Button("Xem/Sửa");
            private final Button btnDelete = new Button("Xoá");

            {
                btnEdit.setOnAction((ActionEvent event) -> {
                    Spending data = getTableView().getItems().get(getIndex());
                    try {
                        homeController.newWindowWithParentController("SpendingCreateUpdate",data,SpendingListController.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                btnDelete.setOnAction((ActionEvent event) -> {
                    Spending data = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> optional = Util.alertInformation("Thông báo","Xác nhận","Xác nhận xoá chi tiêu");
                    if(!optional.isEmpty()){
                        if(optional.get() == ButtonType.OK){
                            SpendingService.deleteSpending(data.getId());
                            refresh();
                        }
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                Spending data = getTableRow().getItem();
                if (empty || data == null) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox( btnEdit, btnDelete);
                    hBox.setSpacing(5);
                    setGraphic(hBox);
                }
            }
        });

    }

    public void getSpendingListToTable() {
       HttpResponse<JsonNode> response;
        Map<String, Object> params = new HashMap<>();

        params.put("user_id",this.userId);

        if (!txtName.getText().isEmpty()) {
            params.put("name", txtName.getText());
        }

        String category = cbCategory.getValue();
        if (!category.equals("Tất cả")) {
            params.put("category", category.equals("Định kỳ")?"FREQUENCY":"FORTUITY");
        }

        String type = cbType.getValue();
        if (!type.equals("Tất cả")) {
            params.put("type", type.equals("Vào")?"IN":"OUT");
        }

        Long startAtFrom = Util.getEpochSecondAtStartOfDayFromDatePicker(dpkStart);
        Long finishAtTo = Util.getEpochSecondAtStartOfDayFromDatePicker(dpkEnd);
        if (startAtFrom != null && startAtFrom != 0L) {
            params.put("start", startAtFrom);
        }
        if (finishAtTo != null && finishAtTo != 0L) {
            params.put("end", finishAtTo);
        }

        System.out.println(params);
        response = SpendingService.getListSpending(params);
        switch (response.getStatus()) {
            case HttpStatus.OK:
                JSONObject json = response.getBody().getObject();
                JSONArray array = json.getJSONArray("content");
                List<Spending> list = new ArrayList<>();
                if(array!=null){
                    for(int i=0;i<array.length();i++){
                        Spending spending = objectMapper.readValue(array.get(i).toString(),Spending.class);
                        list.add(spending);
                    }
                }
                this.tbSpending.getItems().clear();
                this.tbSpending.getItems().setAll(list);
                // total item and paging
                int totalItems = list.size();
                this.lbTotalItems.setText("Tổng số chi tiêu: " + totalItems);
                if (totalItems > 0) {
                    this.pnPagination
                            .setPageCount((int) Math.ceil(totalItems / (float) 20));
                    this.pnPagination.setVisible(true);
                } else {
                    this.pnPagination.setVisible(false);
                }
                break;
            case HttpStatus.INTERNAL_SERVER_ERROR:
//                CustomWarningAlert.showAlertWithoutHeaderText("Cảnh báo", "Có lỗi xảy ra", false);
                break;
            default:
//                CustomWarningAlert.showAlertWithoutHeaderText("Cảnh báo", "Có lỗi xảy ra", false);
                break;
        }


    }

    public void refresh() {
        getSpendingListToTable();
    }

    @FXML
    public void onSearch(){
        refresh();
    }

    @FXML
    public void onCreateSpending(){
        try {
            homeController.newWindowWithParentController("SpendingCreateUpdate",null,SpendingListController.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
