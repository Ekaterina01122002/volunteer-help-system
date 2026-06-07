package ru.volunteerhelp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.HelpType;
import ru.volunteerhelp.model.LocationType;
import ru.volunteerhelp.model.RequestStatus;
import ru.volunteerhelp.model.Volunteer;
import ru.volunteerhelp.service.AppService;
import ru.volunteerhelp.service.NotificationService;
import ru.volunteerhelp.service.WeatherService;
import ru.volunteerhelp.storage.DataStore;
import ru.volunteerhelp.util.TimeUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void createRequestShouldSetNewStatus() {
        AppService service = createService();
        HelpRequest request = service.createRequest(
                "Иванова Анна",
                "+79000000000",
                HelpType.FOOD_DELIVERY,
                LocationType.INDOOR,
                "Москва, ул. Тестовая, 1",
                TimeUtil.format(LocalDateTime.now().plusDays(1)),
                "Нужна доставка продуктов"
        );

        assertNotNull(request.getId());
        assertEquals(RequestStatus.NEW, request.getStatus());
        assertEquals(1, service.getAllRequests().size());
    }

    @Test
    void assignVolunteerShouldMoveRequestToInProgress() {
        AppService service = createService();
        HelpRequest request = service.createRequest(
                "Иванова Анна",
                "+79000000000",
                HelpType.FOOD_DELIVERY,
                LocationType.INDOOR,
                "Москва, ул. Тестовая, 1",
                TimeUtil.format(LocalDateTime.now().plusDays(1)),
                "Нужна доставка продуктов"
        );
        Volunteer volunteer = service.addVolunteer("Петров Петр", "+79000000001", "Доставка", true);

        service.assignVolunteer(request, volunteer);

        assertEquals(RequestStatus.IN_PROGRESS, request.getStatus());
        assertEquals(volunteer.getId(), request.getAssignedVolunteerId());
    }

    @Test
    void overdueCheckShouldCreateNotification() {
        AppService service = createService();
        HelpRequest request = service.createRequest(
                "Иванова Анна",
                "+79000000000",
                HelpType.FOOD_DELIVERY,
                LocationType.INDOOR,
                "Москва, ул. Тестовая, 1",
                TimeUtil.format(LocalDateTime.now().plusDays(1)),
                "Нужна доставка продуктов"
        );
        request.setCreatedAt(TimeUtil.format(LocalDateTime.now().minusDays(4)));

        int changed = service.checkOverdueRequests();

        assertEquals(1, changed);
        assertEquals(RequestStatus.OVERDUE, request.getStatus());
        assertEquals(1, service.getAllNotifications().size());
        assertNotNull(request.getNotificationText());
    }

    @Test
    void volunteerAssignmentCounterShouldBeUpdated() {
        AppService service = createService();
        HelpRequest request = service.createRequest(
                "Иванова Анна",
                "+79000000000",
                HelpType.FOOD_DELIVERY,
                LocationType.INDOOR,
                "Москва, ул. Тестовая, 1",
                TimeUtil.format(LocalDateTime.now().plusDays(1)),
                "Нужна доставка продуктов"
        );
        Volunteer volunteer = service.addVolunteer("Петров Петр", "+79000000001", "Доставка", true);

        service.assignVolunteer(request, volunteer);

        assertEquals(1, service.getAllVolunteers().get(0).getAssignedCount());
    }

    private AppService createService() {
        DataStore dataStore = new DataStore(tempDir.resolve("app-data.json"));
        return new AppService(dataStore, new WeatherService(), new NotificationService());
    }
}
