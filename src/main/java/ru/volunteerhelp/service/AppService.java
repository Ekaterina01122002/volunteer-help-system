package ru.volunteerhelp.service;

import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.HelpType;
import ru.volunteerhelp.model.LocationType;
import ru.volunteerhelp.model.NotificationMessage;
import ru.volunteerhelp.model.RequestStatus;
import ru.volunteerhelp.model.Volunteer;
import ru.volunteerhelp.model.WeatherWarning;
import ru.volunteerhelp.storage.AppData;
import ru.volunteerhelp.storage.DataStore;
import ru.volunteerhelp.util.IdGenerator;
import ru.volunteerhelp.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppService {
    private final DataStore dataStore;
    private final WeatherService weatherService;
    private final NotificationService notificationService;
    private AppData data;

    public AppService(DataStore dataStore, WeatherService weatherService, NotificationService notificationService) {
        this.dataStore = dataStore;
        this.weatherService = weatherService;
        this.notificationService = notificationService;
        this.data = new AppData();
    }

    public void load() {
        data = dataStore.load();
        if (data.getRequests().isEmpty() && data.getVolunteers().isEmpty()) {
            fillDemoData();
            save();
        }
    }

    public void save() {
        refreshVolunteerAssignmentCounts();
        dataStore.save(data);
    }

    public List<HelpRequest> getAllRequests() {
        return data.getRequests().stream()
                .sorted(Comparator.comparing(HelpRequest::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Volunteer> getAllVolunteers() {
        refreshVolunteerAssignmentCounts();
        return data.getVolunteers().stream()
                .sorted(Comparator.comparing(Volunteer::getFullName))
                .collect(Collectors.toList());
    }

    public List<Volunteer> getAvailableVolunteers() {
        refreshVolunteerAssignmentCounts();
        return data.getVolunteers().stream()
                .filter(Volunteer::isAvailable)
                .sorted(Comparator.comparing(Volunteer::getFullName))
                .collect(Collectors.toList());
    }

    public List<NotificationMessage> getAllNotifications() {
        return data.getNotifications().stream()
                .sorted(Comparator.comparing(NotificationMessage::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public HelpRequest createRequest(String applicantName, String applicantPhone, HelpType helpType,
                                     LocationType locationType, String address, String plannedDateTime,
                                     String description) {
        validateRequestFields(applicantName, applicantPhone, helpType, locationType, address, plannedDateTime);

        String now = TimeUtil.now();
        HelpRequest request = new HelpRequest(
                IdGenerator.nextId(),
                applicantName.trim(),
                applicantPhone.trim(),
                helpType,
                locationType,
                address.trim(),
                plannedDateTime,
                description == null ? "" : description.trim(),
                RequestStatus.NEW,
                now,
                now
        );

        WeatherWarning warning = weatherService.checkWeather(request);
        request.setWeatherWarning(warning);
        if (warning != null) {
            request.addHistory(TimeUtil.now() + ": добавлено погодное предупреждение");
        }

        data.getRequests().add(request);
        save();
        return request;
    }

    public void updateRequest(HelpRequest request, String applicantName, String applicantPhone, HelpType helpType,
                              LocationType locationType, String address, String plannedDateTime, String description) {
        Objects.requireNonNull(request, "Заявка не выбрана");
        if (request.isClosed()) {
            throw new IllegalStateException("Завершенную или отмененную заявку нельзя редактировать");
        }
        validateRequestFields(applicantName, applicantPhone, helpType, locationType, address, plannedDateTime);

        request.setApplicantName(applicantName.trim());
        request.setApplicantPhone(applicantPhone.trim());
        request.setHelpType(helpType);
        request.setLocationType(locationType);
        request.setAddress(address.trim());
        request.setPlannedDateTime(plannedDateTime);
        request.setDescription(description == null ? "" : description.trim());
        request.setUpdatedAt(TimeUtil.now());
        request.setWeatherWarning(weatherService.checkWeather(request));
        request.addHistory(TimeUtil.now() + ": заявка отредактирована");
        save();
    }

    public Volunteer addVolunteer(String fullName, String phone, String skills, boolean available) {
        validateVolunteerFields(fullName, phone);
        Volunteer volunteer = new Volunteer(IdGenerator.nextId(), fullName.trim(), phone.trim(),
                skills == null ? "" : skills.trim(), available);
        data.getVolunteers().add(volunteer);
        save();
        return volunteer;
    }

    public void updateVolunteer(Volunteer volunteer, String fullName, String phone, String skills, boolean available) {
        Objects.requireNonNull(volunteer, "Волонтер не выбран");
        validateVolunteerFields(fullName, phone);
        volunteer.setFullName(fullName.trim());
        volunteer.setPhone(phone.trim());
        volunteer.setSkills(skills == null ? "" : skills.trim());
        volunteer.setAvailable(available);
        save();
    }

    public void deleteVolunteer(Volunteer volunteer) {
        Objects.requireNonNull(volunteer, "Волонтер не выбран");
        boolean hasActiveRequest = data.getRequests().stream()
                .anyMatch(request -> volunteer.getId().equals(request.getAssignedVolunteerId())
                        && request.getStatus() == RequestStatus.IN_PROGRESS);
        if (hasActiveRequest) {
            throw new IllegalStateException("Нельзя удалить волонтера, назначенного на активную заявку");
        }
        data.getVolunteers().remove(volunteer);
        save();
    }

    public void assignVolunteer(HelpRequest request, Volunteer volunteer) {
        Objects.requireNonNull(request, "Заявка не выбрана");
        Objects.requireNonNull(volunteer, "Волонтер не выбран");
        if (!volunteer.isAvailable()) {
            throw new IllegalStateException("Выбранный волонтер недоступен");
        }
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя назначить волонтера на закрытую заявку");
        }

        request.setAssignedVolunteerId(volunteer.getId());
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setUpdatedAt(TimeUtil.now());
        request.addHistory(TimeUtil.now() + ": назначен волонтер " + volunteer.getFullName()
                + ", статус изменен на " + RequestStatus.IN_PROGRESS.getTitle());
        save();
    }

    public void completeRequest(HelpRequest request) {
        Objects.requireNonNull(request, "Заявка не выбрана");
        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Выполнить можно только заявку в работе");
        }
        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletedAt(TimeUtil.now());
        request.setUpdatedAt(TimeUtil.now());
        request.addHistory(TimeUtil.now() + ": заявка выполнена");
        save();
    }

    public void cancelRequest(HelpRequest request, String reason) {
        Objects.requireNonNull(request, "Заявка не выбрана");
        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new IllegalStateException("Выполненную заявку нельзя отменить");
        }
        request.setStatus(RequestStatus.CANCELLED);
        request.setCancelReason(reason == null ? "" : reason.trim());
        request.setUpdatedAt(TimeUtil.now());
        request.addHistory(TimeUtil.now() + ": заявка отменена. Причина: " + request.getCancelReason());
        save();
    }

    public int checkOverdueRequests() {
        int changed = 0;
        for (HelpRequest request : data.getRequests()) {
            boolean noVolunteer = request.getAssignedVolunteerId() == null || request.getAssignedVolunteerId().isBlank();
            boolean overdue = request.getStatus() == RequestStatus.NEW
                    && noVolunteer
                    && TimeUtil.isOlderThanDays(request.getCreatedAt(), 3);
            if (overdue) {
                request.setStatus(RequestStatus.OVERDUE);
                request.setUpdatedAt(TimeUtil.now());
                request.addHistory(TimeUtil.now() + ": заявка автоматически переведена в статус "
                        + RequestStatus.OVERDUE.getTitle());
                NotificationMessage message = notificationService.createOverdueMessage(request);
                request.setNotificationText(message.getMessageText());
                data.getNotifications().add(message);
                changed++;
            }
        }
        if (changed > 0) {
            save();
        }
        return changed;
    }

    public String getVolunteerName(String volunteerId) {
        if (volunteerId == null || volunteerId.isBlank()) {
            return "";
        }
        return data.getVolunteers().stream()
                .filter(volunteer -> volunteer.getId().equals(volunteerId))
                .map(Volunteer::getFullName)
                .findFirst()
                .orElse("Не найден");
    }

    public Optional<HelpRequest> findRequestById(String id) {
        return data.getRequests().stream()
                .filter(request -> request.getId().equals(id))
                .findFirst();
    }

    public List<HelpRequest> getAssignableRequests() {
        return data.getRequests().stream()
                .filter(request -> request.getStatus() == RequestStatus.NEW || request.getStatus() == RequestStatus.OVERDUE)
                .sorted(Comparator.comparing(HelpRequest::getCreatedAt))
                .collect(Collectors.toList());
    }

    public long countByStatus(RequestStatus status) {
        return data.getRequests().stream()
                .filter(request -> request.getStatus() == status)
                .count();
    }

    private void refreshVolunteerAssignmentCounts() {
        for (Volunteer volunteer : data.getVolunteers()) {
            int count = (int) data.getRequests().stream()
                    .filter(request -> volunteer.getId().equals(request.getAssignedVolunteerId()))
                    .filter(request -> request.getStatus() != RequestStatus.CANCELLED)
                    .count();
            volunteer.setAssignedCount(count);
        }
    }

    private void validateRequestFields(String applicantName, String applicantPhone, HelpType helpType,
                                       LocationType locationType, String address, String plannedDateTime) {
        if (isBlank(applicantName)) {
            throw new IllegalArgumentException("Укажите ФИО заявителя");
        }
        if (isBlank(applicantPhone)) {
            throw new IllegalArgumentException("Укажите телефон заявителя");
        }
        if (helpType == null) {
            throw new IllegalArgumentException("Выберите тип помощи");
        }
        if (locationType == null) {
            throw new IllegalArgumentException("Выберите тип места");
        }
        if (isBlank(address)) {
            throw new IllegalArgumentException("Укажите адрес");
        }
        if (isBlank(plannedDateTime)) {
            throw new IllegalArgumentException("Укажите дату и время");
        }
    }

    private void validateVolunteerFields(String fullName, String phone) {
        if (isBlank(fullName)) {
            throw new IllegalArgumentException("Укажите ФИО волонтера");
        }
        if (isBlank(phone)) {
            throw new IllegalArgumentException("Укажите телефон волонтера");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void fillDemoData() {
        Volunteer v1 = new Volunteer(IdGenerator.nextId(), "Иванов Артем Сергеевич", "+79000000001", "Сопровождение, доставка продуктов", true);
        Volunteer v2 = new Volunteer(IdGenerator.nextId(), "Петрова Мария Андреевна", "+79000000002", "Доставка лекарств", true);
        Volunteer v3 = new Volunteer(IdGenerator.nextId(), "Сидоров Павел Игоревич", "+79000000003", "Бытовая помощь", false);
        data.getVolunteers().add(v1);
        data.getVolunteers().add(v2);
        data.getVolunteers().add(v3);

        HelpRequest r1 = new HelpRequest(IdGenerator.nextId(), "Смирнова Анна Николаевна", "+79110000001",
                HelpType.FOOD_DELIVERY, LocationType.INDOOR, "Москва, ул. Примерная, 10",
                TimeUtil.format(LocalDateTime.now().plusDays(1)), "Нужно доставить продукты из ближайшего магазина.",
                RequestStatus.NEW, TimeUtil.now(), TimeUtil.now());

        HelpRequest r2 = new HelpRequest(IdGenerator.nextId(), "Кузнецов Олег Петрович", "+79110000002",
                HelpType.ACCOMPANIMENT, LocationType.OUTDOOR, "Москва, ул. Садовая, 15",
                TimeUtil.format(LocalDateTime.now().plusDays(2)), "Нужно сопроводить до поликлиники.",
                RequestStatus.NEW, TimeUtil.format(LocalDateTime.now().minusDays(4)), TimeUtil.now());
        r2.setWeatherWarning(weatherService.checkWeather(r2));

        data.getRequests().add(r1);
        data.getRequests().add(r2);
    }
}
