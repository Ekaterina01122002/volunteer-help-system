package ru.volunteerhelp.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataStore {
    private final Path filePath;
    private final Gson gson;

    public DataStore(Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public AppData load() {
        if (!Files.exists(filePath)) {
            return new AppData();
        }

        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                return new AppData();
            }
            AppData data = gson.fromJson(json, AppData.class);
            return data == null ? new AppData() : data;
        } catch (IOException ex) {
            return new AppData();
        }
    }

    public void save(AppData data) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(filePath, gson.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Не удалось сохранить данные приложения", ex);
        }
    }
}
