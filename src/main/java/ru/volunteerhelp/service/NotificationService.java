package ru.volunteerhelp.service;

import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.NotificationMessage;
import ru.volunteerhelp.util.IdGenerator;
import ru.volunteerhelp.util.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationService {
    private final String coordinatorName;
    private final String coordinatorPhone;

    public NotificationService() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
            // Для учебного проекта ошибка чтения настроек не блокирует работу приложения.
        }
        this.coordinatorName = properties.getProperty("coordinator.name", "Координатор волонтерского центра");
        this.coordinatorPhone = properties.getProperty("coordinator.phone", "+79990000000");
    }

    public NotificationMessage createOverdueMessage(HelpRequest request) {
        String text = "Заявка №" + request.getId() + " просрочена. "
                + "Заявка находится без назначенного волонтера более 3 дней. "
                + "Тип помощи: " + request.getHelpTypeText() + ". "
                + "Адрес: " + request.getAddress() + ". "
                + "Необходимо назначить волонтера или связаться с заявителем.";

        return new NotificationMessage(
                IdGenerator.nextId(),
                request.getId(),
                coordinatorName,
                coordinatorPhone,
                text,
                TimeUtil.now(),
                "Сформировано"
        );
    }
}
