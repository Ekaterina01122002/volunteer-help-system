package ru.volunteerhelp.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.volunteerhelp.model.Volunteer;

import java.util.Optional;

public class VolunteerDialog {
    public record VolunteerFormData(String fullName, String phone, String skills, boolean available) {
    }

    private VolunteerDialog() {
    }

    public static Optional<VolunteerFormData> showCreate() {
        return show(null);
    }

    public static Optional<VolunteerFormData> showEdit(Volunteer volunteer) {
        return show(volunteer);
    }

    private static Optional<VolunteerFormData> show(Volunteer volunteer) {
        Dialog<VolunteerFormData> dialog = new Dialog<>();
        dialog.setTitle(volunteer == null ? "Добавление волонтера" : "Редактирование волонтера");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        TextArea skillsArea = new TextArea();
        skillsArea.setPrefRowCount(3);
        CheckBox availableBox = new CheckBox("Доступен для назначения");
        availableBox.setSelected(true);

        if (volunteer != null) {
            fullNameField.setText(volunteer.getFullName());
            phoneField.setText(volunteer.getPhone());
            skillsArea.setText(volunteer.getSkills());
            availableBox.setSelected(volunteer.isAvailable());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Телефон:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Навыки:"), 0, 2);
        grid.add(skillsArea, 1, 2);
        grid.add(new Label("Доступность:"), 0, 3);
        grid.add(availableBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(fullNameField.textProperty().isEmpty().or(phoneField.textProperty().isEmpty()));

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new VolunteerFormData(
                        fullNameField.getText(),
                        phoneField.getText(),
                        skillsArea.getText(),
                        availableBox.isSelected()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
