package com.nminh.app.common;

import com.nminh.app.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class Util {
    public static String formatSecondsToOnlyDate(Long time) {
        if (time != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date myDate = Date.from(Instant.ofEpochSecond(time));
            return sdf.format(myDate);
        }
        return null;
    }
    public static Long getEpochSecondAtStartOfDayFromDatePicker(DatePicker datePicker) {
            /*
            lấy thời điểm 0h00 của 1 ngày và quy đổi ra timestamp
            */
        LocalDate dateFromLocalDate = datePicker.getConverter().fromString(datePicker.getEditor().getText());
        if (dateFromLocalDate != null) {
            return dateFromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        } else {
            // Điền linh tinh gây ra lỗi thì set datepicker rỗng
            datePicker.getEditor().setText("");
            return null;
        }
    }

    public static Optional<ButtonType> alertInformation(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        Text text = new Text(contentText);
        text.setWrappingWidth(400);
        alert.getDialogPane().setContent(text);
//        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Main.class.getResource("logog.png").toString()));
        return alert.showAndWait();
    }
}
