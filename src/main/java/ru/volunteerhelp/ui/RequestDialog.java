package ru.volunteerhelp.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.HelpType;
import ru.volunteerhelp.model.LocationType;
import ru.volunteerhelp.util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class RequestDialog {
    public record RequestFormData(String applicantName, String applicantPhone, HelpType helpType,
                                  LocationType locationType, String address, String plannedDateTime,
                                  String description) {
    }

    private RequestDialog() {
    }

    public static Optional<RequestFormData> showCreate() {
        return show(null);
    }

    public static Optional<RequestFormData> showEdit(HelpRequest request) {
        return show(request);
    }

    private static Optional<RequestFormData> show(HelpRequest request) {
        Dialog<RequestFormData> dialog = new Dialog<>();
        dialog.setTitle(request == null ? "Создание заявки" : "Редактирование заявки");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField applicantNameField = new TextField();
        TextField applicantPhoneField = new TextField();
        ComboBox<HelpType> helpTypeBox = new ComboBox<>(FXCollections.observableArrayList(HelpType.values()));
        ComboBox<LocationType> locationTypeBox = new ComboBox<>(FXCollections.observableArrayList(LocationType.values()));
        TextField addressField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 10);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);

        if (request != null) {
            applicantNameField.setText(request.getApplicantName());
            applicantPhoneField.setText(request.getApplicantPhone());
            helpTypeBox.setValue(request.getHelpType());
            locationTypeBox.setValue(request.getLocationType());
            addressField.setText(request.getAddress());
            LocalDateTime dateTime = TimeUtil.parse(request.getPlannedDateTime());
            datePicker.setValue(dateTime.toLocalDate());
            hourSpinner.getValueFactory().setValue(dateTime.getHour());
            minuteSpinner.getValueFactory().setValue(dateTime.getMinute());
            descriptionArea.setText(request.getDescription());
        } else {
            helpTypeBox.setValue(HelpType.ACCOMPANIMENT);
            locationTypeBox.setValue(LocationType.OUTDOOR);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(new Label("ФИО заявителя:"), 0, 0);
        grid.add(applicantNameField, 1, 0);
        grid.add(new Label("Телефон:"), 0, 1);
        grid.add(applicantPhoneField, 1, 1);
        grid.add(new Label("Тип помощи:"), 0, 2);
        grid.add(helpTypeBox, 1, 2);
        grid.add(new Label("Место:"), 0, 3);
        grid.add(locationTypeBox, 1, 3);
        grid.add(new Label("Адрес:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Дата:"), 0, 5);
        grid.add(datePicker, 1, 5);
        grid.add(new Label("Час:"), 0, 6);
        grid.add(hourSpinner, 1, 6);
        grid.add(new Label("Минута:"), 0, 7);
        grid.add(minuteSpinner, 1, 7);
        grid.add(new Label("Описание:"), 0, 8);
        grid.add(descriptionArea, 1, 8);

        dialog.getDialogPane().setContent(grid);
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(applicantNameField.textProperty().isEmpty()
                .or(applicantPhoneField.textProperty().isEmpty())
                .or(addressField.textProperty().isEmpty()));

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String plannedDateTime = TimeUtil.format(datePicker.getValue(), hourSpinner.getValue(), minuteSpinner.getValue());
                return new RequestFormData(
                        applicantNameField.getText(),
                        applicantPhoneField.getText(),
                        helpTypeBox.getValue(),
                        locationTypeBox.getValue(),
                        addressField.getText(),
                        plannedDateTime,
                        descriptionArea.getText()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
