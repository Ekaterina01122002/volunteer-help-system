package ru.volunteerhelp.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.LocationType;
import ru.volunteerhelp.model.WeatherWarning;
import ru.volunteerhelp.util.IdGenerator;
import ru.volunteerhelp.util.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

public class WeatherService {
    private final boolean apiEnabled;
    private final String apiKey;
    private final HttpClient httpClient;

    public WeatherService() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
            // Для учебного проекта ошибка чтения настроек не блокирует работу приложения.
        }
        this.apiEnabled = Boolean.parseBoolean(properties.getProperty("weather.api.enabled", "false"));
        this.apiKey = properties.getProperty("weather.api.key", "").trim();
        this.httpClient = HttpClient.newHttpClient();
    }

    public WeatherWarning checkWeather(HelpRequest request) {
        if (request.getLocationType() != LocationType.OUTDOOR) {
            return null;
        }

        if (apiEnabled && !apiKey.isBlank()) {
            WeatherWarning warning = checkByOpenWeatherMap(request);
            if (warning != null) {
                return warning;
            }
        }

        return createStubWarning(request);
    }

    private WeatherWarning checkByOpenWeatherMap(HelpRequest request) {
        try {
            String query = URLEncoder.encode(request.getAddress(), StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + query
                    + "&appid=" + apiKey + "&units=metric&lang=ru";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            double temp = root.getAsJsonObject("main").get("temp").getAsDouble();
            JsonArray weather = root.getAsJsonArray("weather");
            String condition = weather.size() > 0
                    ? weather.get(0).getAsJsonObject().get("description").getAsString()
                    : "нет данных";

            String normalized = condition.toLowerCase(Locale.ROOT);
            boolean badWeather = temp <= -5
                    || normalized.contains("дожд")
                    || normalized.contains("снег")
                    || normalized.contains("гроза")
                    || normalized.contains("storm")
                    || normalized.contains("rain")
                    || normalized.contains("snow");

            if (!badWeather) {
                return null;
            }

            String text = "Погодное предупреждение: " + condition + ", температура " + temp
                    + "°C. Нужно предупредить волонтера перед выполнением уличной заявки.";
            return new WeatherWarning(IdGenerator.nextId(), request.getId(), temp, condition, text, TimeUtil.now());
        } catch (Exception ex) {
            return null;
        }
    }

    private WeatherWarning createStubWarning(HelpRequest request) {
        int variant = Math.abs((request.getAddress() + request.getId()).hashCode()) % 3;
        if (variant == 0) {
            return null;
        }
        if (variant == 1) {
            return new WeatherWarning(
                    IdGenerator.nextId(),
                    request.getId(),
                    2.0,
                    "дождь",
                    "Погодное предупреждение: возможен дождь. Волонтера нужно предупредить о погодных условиях.",
                    TimeUtil.now()
            );
        }
        return new WeatherWarning(
                IdGenerator.nextId(),
                request.getId(),
                -7.0,
                "мороз",
                "Погодное предупреждение: ожидается мороз. При выполнении заявки требуется учитывать низкую температуру.",
                TimeUtil.now()
        );
    }
}
