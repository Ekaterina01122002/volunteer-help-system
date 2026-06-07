package ru.volunteerhelp.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.HelpType;
import ru.volunteerhelp.model.NotificationMessage;
import ru.volunteerhelp.model.RequestStatus;
import ru.volunteerhelp.model.Volunteer;
import ru.volunteerhelp.service.AppService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class MainView extends BorderPane {
    private final AppService service;

    private final TableView<HelpRequest> requestTable = new TableView<>();
    private final TableView<Volunteer> volunteerTable = new TableView<>();
    private final TableView<NotificationMessage> notificationTable = new TableView<>();
    private final ComboBox<RequestStatus> statusFilter = new ComboBox<>();
    private final ComboBox<HelpType> helpTypeFilter = new ComboBox<>();
    private final DatePicker dateFilter = new DatePicker();
    private final ComboBox<Volunteer> volunteerFilter = new ComboBox<>();
    private final TextField searchField = new TextField();

    private final ComboBox<HelpRequest> assignmentRequestBox = new ComboBox<>();
    private final ComboBox<Volunteer> assignmentVolunteerBox = new ComboBox<>();

    private final Label statsLabel = new Label();

    public MainView(AppService service) {
        this.service = service;
        buildLayout();
        refreshAll();
    }

    private void buildLayout() {
        Label header = new Label("ПС «Организация волонтерской помощи»");
        header.getStyleClass().add("header-label");

        TabPane tabs = new TabPane();
        tabs.getTabs().add(createRequestsTab());
        tabs.getTabs().add(createVolunteersTab());
        tabs.getTabs().add(createAssignmentTab());
        tabs.getTabs().add(createNotificationsTab());
        tabs.getTabs().add(createStatsTab());

        VBox root = new VBox(header, tabs);
        root.setPadding(new Insets(10));
        VBox.setVgrow(tabs, Priority.ALWAYS);
        setCenter(root);
    }

    private Tab createRequestsTab() {
        requestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HelpRequest, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));

        TableColumn<HelpRequest, String> applicantColumn = new TableColumn<>("Заявитель");
        applicantColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantName()));

        TableColumn<HelpRequest, String> phoneColumn = new TableColumn<>("Телефон");
        phoneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApplicantPhone()));

        TableColumn<HelpRequest, String> typeColumn = new TableColumn<>("Тип помощи");
        typeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHelpTypeText()));

        TableColumn<HelpRequest, String> addressColumn = new TableColumn<>("Адрес");
        addressColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAddress()));

        TableColumn<HelpRequest, String> dateColumn = new TableColumn<>("Дата и время");
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPlannedDateTime()));

        TableColumn<HelpRequest, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatusText()));

        TableColumn<HelpRequest, String> volunteerColumn = new TableColumn<>("Волонтер");
        volunteerColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(service.getVolunteerName(data.getValue().getAssignedVolunteerId())));

        TableColumn<HelpRequest, String> weatherColumn = new TableColumn<>("Погода");
        weatherColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getWeatherWarningText()));

        requestTable.getColumns().addAll(idColumn, applicantColumn, phoneColumn, typeColumn, addressColumn,
                dateColumn, statusColumn, volunteerColumn, weatherColumn);

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> addRequest());

        Button editButton = new Button("Редактировать");
        editButton.setOnAction(event -> editSelectedRequest());

        Button cancelButton = new Button("Отменить");
        cancelButton.setOnAction(event -> cancelSelectedRequest());

        Button completeButton = new Button("Выполнить");
        completeButton.setOnAction(event -> completeSelectedRequest());

        Button detailsButton = new Button("Подробно");
        detailsButton.setOnAction(event -> showRequestDetails());

        Button overdueButton = new Button("Проверить просрочки");
        overdueButton.setOnAction(event -> checkOverdueRequests());

        statusFilter.setPromptText("Статус");
        statusFilter.getItems().add(null);
        statusFilter.getItems().addAll(RequestStatus.values());
        statusFilter.setOnAction(event -> refreshRequests());

        helpTypeFilter.setPromptText("Тип помощи");
        helpTypeFilter.getItems().add(null);
        helpTypeFilter.getItems().addAll(HelpType.values());
        helpTypeFilter.setOnAction(event -> refreshRequests());

        dateFilter.setPromptText("Дата");
        dateFilter.setOnAction(event -> refreshRequests());

        volunteerFilter.setPromptText("Волонтер");
        volunteerFilter.setOnAction(event -> refreshRequests());

        Button resetFiltersButton = new Button("Сбросить");
        resetFiltersButton.setOnAction(event -> {
            statusFilter.setValue(null);
            helpTypeFilter.setValue(null);
            dateFilter.setValue(null);
            volunteerFilter.setValue(null);
            searchField.clear();
            refreshRequests();
        });

        searchField.setPromptText("Поиск по заявителю, адресу, типу помощи");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> refreshRequests());

        HBox filters = new HBox(8, new Label("Фильтр:"), statusFilter, helpTypeFilter, dateFilter,
                volunteerFilter, searchField, resetFiltersButton);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox buttons = new HBox(8, addButton, editButton, cancelButton, completeButton, detailsButton, overdueButton);
        buttons.getStyleClass().add("toolbar");

        VBox content = new VBox(8, filters, requestTable, buttons);
        VBox.setVgrow(requestTable, Priority.ALWAYS);
        content.setPadding(new Insets(10));

        Tab tab = new Tab("Заявки", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab createVolunteersTab() {
        volunteerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Volunteer, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));

        TableColumn<Volunteer, String> nameColumn = new TableColumn<>("ФИО");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFullName()));

        TableColumn<Volunteer, String> phoneColumn = new TableColumn<>("Телефон");
        phoneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhone()));

        TableColumn<Volunteer, String> skillsColumn = new TableColumn<>("Навыки");
        skillsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSkills()));

        TableColumn<Volunteer, String> availableColumn = new TableColumn<>("Доступность");
        availableColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAvailableText()));

        TableColumn<Volunteer, String> assignedColumn = new TableColumn<>("Назначено");
        assignedColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getAssignedCount())));

        volunteerTable.getColumns().addAll(idColumn, nameColumn, phoneColumn, skillsColumn, availableColumn, assignedColumn);

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> addVolunteer());

        Button editButton = new Button("Редактировать");
        editButton.setOnAction(event -> editSelectedVolunteer());

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(event -> deleteSelectedVolunteer());

        HBox buttons = new HBox(8, addButton, editButton, deleteButton);
        buttons.getStyleClass().add("toolbar");

        VBox content = new VBox(8, volunteerTable, buttons);
        VBox.setVgrow(volunteerTable, Priority.ALWAYS);
        content.setPadding(new Insets(10));

        Tab tab = new Tab("Волонтеры", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab createAssignmentTab() {
        assignmentRequestBox.setPrefWidth(520);
        assignmentVolunteerBox.setPrefWidth(360);

        Button assignButton = new Button("Назначить волонтера");
        assignButton.setOnAction(event -> assignVolunteer());

        VBox box = new VBox(12,
                new Label("Заявка без назначенного волонтера:"),
                assignmentRequestBox,
                new Label("Доступный волонтер:"),
                assignmentVolunteerBox,
                assignButton
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_LEFT);

        Tab tab = new Tab("Назначение", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab createNotificationsTab() {
        notificationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<NotificationMessage, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));

        TableColumn<NotificationMessage, String> requestColumn = new TableColumn<>("Заявка");
        requestColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRequestId()));

        TableColumn<NotificationMessage, String> receiverColumn = new TableColumn<>("Получатель");
        receiverColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCoordinatorName()));

        TableColumn<NotificationMessage, String> textColumn = new TableColumn<>("Текст сообщения");
        textColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMessageText()));

        TableColumn<NotificationMessage, String> createdColumn = new TableColumn<>("Дата");
        createdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCreatedAt()));

        TableColumn<NotificationMessage, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDeliveryStatus()));

        notificationTable.getColumns().addAll(idColumn, requestColumn, receiverColumn, textColumn, createdColumn, statusColumn);

        VBox content = new VBox(notificationTable);
        VBox.setVgrow(notificationTable, Priority.ALWAYS);
        content.setPadding(new Insets(10));

        Tab tab = new Tab("Уведомления", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab createStatsTab() {
        Button refreshButton = new Button("Обновить статистику");
        refreshButton.setOnAction(event -> refreshStats());
        VBox content = new VBox(15, statsLabel, refreshButton);
        content.setPadding(new Insets(20));
        Tab tab = new Tab("Статистика", content);
        tab.setClosable(false);
        return tab;
    }

    private void addRequest() {
        RequestDialog.showCreate().ifPresent(form -> runSafely(() -> {
            service.createRequest(form.applicantName(), form.applicantPhone(), form.helpType(), form.locationType(),
                    form.address(), form.plannedDateTime(), form.description());
            refreshAll();
        }));
    }

    private void editSelectedRequest() {
        HelpRequest request = getSelectedRequest();
        if (request == null) {
            return;
        }
        RequestDialog.showEdit(request).ifPresent(form -> runSafely(() -> {
            service.updateRequest(request, form.applicantName(), form.applicantPhone(), form.helpType(), form.locationType(),
                    form.address(), form.plannedDateTime(), form.description());
            refreshAll();
        }));
    }

    private void cancelSelectedRequest() {
        HelpRequest request = getSelectedRequest();
        if (request == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog("Отмена по инициативе заявителя");
        dialog.setTitle("Отмена заявки");
        dialog.setHeaderText("Укажите причину отмены заявки №" + request.getId());
        dialog.showAndWait().ifPresent(reason -> runSafely(() -> {
            service.cancelRequest(request, reason);
            refreshAll();
        }));
    }

    private void completeSelectedRequest() {
        HelpRequest request = getSelectedRequest();
        if (request == null) {
            return;
        }
        runSafely(() -> {
            service.completeRequest(request);
            refreshAll();
        });
    }

    private void checkOverdueRequests() {
        runSafely(() -> {
            int count = service.checkOverdueRequests();
            refreshAll();
            showInfo("Проверка завершена", "Просроченных заявок найдено: " + count);
        });
    }

    private void showRequestDetails() {
        HelpRequest request = getSelectedRequest();
        if (request == null) {
            return;
        }
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(650);
        area.setPrefHeight(420);
        area.setText("Заявка №" + request.getId() + "\n\n"
                + "Заявитель: " + request.getApplicantName() + "\n"
                + "Телефон: " + request.getApplicantPhone() + "\n"
                + "Тип помощи: " + request.getHelpTypeText() + "\n"
                + "Место: " + request.getLocationTypeText() + "\n"
                + "Адрес: " + request.getAddress() + "\n"
                + "Дата и время: " + request.getPlannedDateTime() + "\n"
                + "Статус: " + request.getStatusText() + "\n"
                + "Волонтер: " + service.getVolunteerName(request.getAssignedVolunteerId()) + "\n"
                + "Погодное предупреждение: " + request.getWeatherWarningText() + "\n"
                + "Уведомление координатору: " + nullToEmpty(request.getNotificationText()) + "\n"
                + "Причина отмены: " + nullToEmpty(request.getCancelReason()) + "\n\n"
                + "Описание:\n" + nullToEmpty(request.getDescription()) + "\n\n"
                + "История статусов:\n" + nullToEmpty(request.getStatusHistory()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Карточка заявки");
        alert.setHeaderText("Подробная информация");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void addVolunteer() {
        VolunteerDialog.showCreate().ifPresent(form -> runSafely(() -> {
            service.addVolunteer(form.fullName(), form.phone(), form.skills(), form.available());
            refreshAll();
        }));
    }

    private void editSelectedVolunteer() {
        Volunteer volunteer = getSelectedVolunteer();
        if (volunteer == null) {
            return;
        }
        VolunteerDialog.showEdit(volunteer).ifPresent(form -> runSafely(() -> {
            service.updateVolunteer(volunteer, form.fullName(), form.phone(), form.skills(), form.available());
            refreshAll();
        }));
    }

    private void deleteSelectedVolunteer() {
        Volunteer volunteer = getSelectedVolunteer();
        if (volunteer == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Удаление волонтера");
        confirm.setHeaderText("Удалить волонтера " + volunteer.getFullName() + "?");
        confirm.showAndWait().filter(button -> button == ButtonType.OK).ifPresent(button -> runSafely(() -> {
            service.deleteVolunteer(volunteer);
            refreshAll();
        }));
    }

    private void assignVolunteer() {
        HelpRequest request = assignmentRequestBox.getValue();
        Volunteer volunteer = assignmentVolunteerBox.getValue();
        runSafely(() -> {
            service.assignVolunteer(request, volunteer);
            refreshAll();
        });
    }

    private HelpRequest getSelectedRequest() {
        HelpRequest request = requestTable.getSelectionModel().getSelectedItem();
        if (request == null) {
            showInfo("Нет выбора", "Выберите заявку в таблице");
        }
        return request;
    }

    private Volunteer getSelectedVolunteer() {
        Volunteer volunteer = volunteerTable.getSelectionModel().getSelectedItem();
        if (volunteer == null) {
            showInfo("Нет выбора", "Выберите волонтера в таблице");
        }
        return volunteer;
    }

    private void refreshAll() {
        refreshRequestFilterLists();
        refreshRequests();
        refreshVolunteers();
        refreshAssignment();
        refreshNotifications();
        refreshStats();
    }

    private void refreshRequests() {
        List<HelpRequest> list = service.getAllRequests();
        RequestStatus selectedStatus = statusFilter.getValue();
        HelpType selectedHelpType = helpTypeFilter.getValue();
        LocalDate selectedDate = dateFilter.getValue();
        Volunteer selectedVolunteer = volunteerFilter.getValue();
        String query = searchField.getText() == null ? "" : searchField.getText().toLowerCase(Locale.ROOT).trim();

        List<HelpRequest> filtered = list.stream()
                .filter(request -> selectedStatus == null || request.getStatus() == selectedStatus)
                .filter(request -> selectedHelpType == null || request.getHelpType() == selectedHelpType)
                .filter(request -> selectedDate == null || nullToEmpty(request.getPlannedDateTime()).startsWith(selectedDate.toString()))
                .filter(request -> selectedVolunteer == null
                        || selectedVolunteer.getId().equals(request.getAssignedVolunteerId()))
                .filter(request -> query.isBlank()
                        || nullToEmpty(request.getApplicantName()).toLowerCase(Locale.ROOT).contains(query)
                        || nullToEmpty(request.getAddress()).toLowerCase(Locale.ROOT).contains(query)
                        || nullToEmpty(request.getHelpTypeText()).toLowerCase(Locale.ROOT).contains(query))
                .toList();
        ObservableList<HelpRequest> items = FXCollections.observableArrayList(filtered);
        requestTable.setItems(items);
    }

    private void refreshRequestFilterLists() {
        Volunteer selectedVolunteer = volunteerFilter.getValue();
        volunteerFilter.getItems().setAll(service.getAllVolunteers());
        if (selectedVolunteer != null && service.getAllVolunteers().stream()
                .anyMatch(volunteer -> volunteer.getId().equals(selectedVolunteer.getId()))) {
            volunteerFilter.setValue(selectedVolunteer);
        } else {
            volunteerFilter.setValue(null);
        }
    }

    private void refreshVolunteers() {
        volunteerTable.setItems(FXCollections.observableArrayList(service.getAllVolunteers()));
    }

    private void refreshAssignment() {
        assignmentRequestBox.setItems(FXCollections.observableArrayList(service.getAssignableRequests()));
        assignmentVolunteerBox.setItems(FXCollections.observableArrayList(service.getAvailableVolunteers()));
    }

    private void refreshNotifications() {
        notificationTable.setItems(FXCollections.observableArrayList(service.getAllNotifications()));
    }

    private void refreshStats() {
        statsLabel.setText("Новые заявки: " + service.countByStatus(RequestStatus.NEW) + "\n"
                + "В работе: " + service.countByStatus(RequestStatus.IN_PROGRESS) + "\n"
                + "Выполнены: " + service.countByStatus(RequestStatus.COMPLETED) + "\n"
                + "Отменены: " + service.countByStatus(RequestStatus.CANCELLED) + "\n"
                + "Просрочены: " + service.countByStatus(RequestStatus.OVERDUE) + "\n"
                + "Уведомлений сформировано: " + service.getAllNotifications().size());
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            showError("Ошибка", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
