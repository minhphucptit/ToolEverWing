package com.nminh.app.controller;

import com.nminh.app.common.IntendedStatusEnum;
import com.nminh.app.common.Util;
import com.nminh.app.controller.router.SpendingScreenRouter;
import com.nminh.app.model.Intended;
import com.nminh.app.model.Spending;
import com.nminh.app.service.IntendedService;
import com.nminh.app.service.SpendingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

@Data
public class IntendedListController implements Initializable {
    private Integer userId;
    private ObjectMapper objectMapper;
    private HomeController homeController;

    public IntendedListController(HomeController controller){
        this.userId = controller.getUser().getId();
        this.objectMapper = new JsonObjectMapper();
        this.homeController=controller;
    }

    @FXML
    public TableView<Intended> tbSpending;

    @FXML
    public TableColumn<Intended,String> clStt;

    @FXML
    public TableColumn<Intended,String> clName;

    @FXML
    public TableColumn<Intended,String> clStatus;

    @FXML
    public TableColumn<Intended,String> clQuantity;

    @FXML
    public TableColumn<Intended,String> clCost;

    @FXML
    public TableColumn<Intended,String> clCreateAt;

    @FXML
    public TableColumn<Intended,String> clFinishAt;

    @FXML
    public TableColumn<Intended,Void> clAction;

    @FXML
    public Button btnAdd;

    @FXML
    public Button btnSearch;

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<IntendedStatusEnum> cbStatus;

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
        setUpComboboxStatus();
        setUpTable();
        getSpendingListToTable();
    }

    public void setUpComboboxStatus(){
        cbStatus.setConverter(new StringConverter<IntendedStatusEnum>() {
            @Override
            public String toString(IntendedStatusEnum object) {
                if(object == null) return "Tất cả";
                return object.getName();
            }

            @Override
            public IntendedStatusEnum fromString(String string) {
                try {
                    return IntendedStatusEnum.valueOf(string);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        cbStatus.getItems().addAll(IntendedStatusEnum.values());
        cbStatus.getSelectionModel().select(0);
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
        clStatus.setCellValueFactory(cellData -> new SimpleStringProperty(IntendedStatusEnum.valueOf(cellData.getValue().getStatus()).getName()));
        clCost.setCellValueFactory(cellData -> new SimpleStringProperty(new BigDecimal(cellData.getValue().getCost()).toString()));
        clCreateAt.setCellValueFactory(cellData -> {
            Long date = cellData.getValue().getCreateAt();
            if (date instanceof Long) {
                return new SimpleStringProperty(Util.formatSecondsToOnlyDate(date));
            }
            return null;
        });
        clFinishAt.setCellValueFactory(cellData -> {
            Long date = cellData.getValue().getFinishAt();
            if (date instanceof Long) {
                return new SimpleStringProperty(Util.formatSecondsToOnlyDate(date));
            }
            return null;
        });
        clAction.setCellFactory(param -> new TableCell<Intended, Void>() {
            private final Button btnEdit = new Button("Xem/Sửa");
            private final Button btnDelete = new Button("Xoá");

            {
                btnEdit.setOnAction((ActionEvent event) -> {
                    Intended data = getTableView().getItems().get(getIndex());
                    try {
                        homeController.newWindowWithParentController("IntendedCreateUpdate",data,IntendedListController.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                btnDelete.setOnAction((ActionEvent event) -> {
                    Intended data = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> optional = Util.alertInformation("Thông báo","Xác nhận","Xác nhận xoá dự định");
                    if(!optional.isEmpty()){
                        if(optional.get() == ButtonType.OK){
                            IntendedService.deleteIntended(data.getId());
                            refresh();
                        }
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                Intended data = getTableRow().getItem();
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

        String status = cbStatus.getValue().name();
        if (!status.equals(IntendedStatusEnum.ALL.name())) {
            params.put("status", status);
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
        response = IntendedService.getListIntended(params);
        switch (response.getStatus()) {
            case HttpStatus.OK:
                JSONObject json = response.getBody().getObject();
                JSONArray array = json.getJSONArray("content");
                List<Intended> list = new ArrayList<>();
                if(array!=null){
                    for(int i=0;i<array.length();i++){
                        Intended intended = objectMapper.readValue(array.get(i).toString(),Intended.class);
                        list.add(intended);
                    }
                }
                this.tbSpending.getItems().clear();
                this.tbSpending.getItems().setAll(list);
                // total item and paging
                int totalItems = list.size();
                this.lbTotalItems.setText("Tổng số dự định: " + totalItems);
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
            homeController.newWindowWithParentController("IntendedCreateUpdate",null,IntendedListController.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
