package ru.volunteerhelp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.volunteerhelp.service.AppService;
import ru.volunteerhelp.service.NotificationService;
import ru.volunteerhelp.service.WeatherService;
import ru.volunteerhelp.storage.DataStore;
import ru.volunteerhelp.ui.MainView;

import java.nio.file.Path;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        DataStore dataStore = new DataStore(Path.of("data", "app-data.json"));
        AppService appService = new AppService(dataStore, new WeatherService(), new NotificationService());
        appService.load();

        MainView mainView = new MainView(appService);
        Scene scene = new Scene(mainView, 1280, 760);
        var css = getClass().getResource("/application.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("ПС «Организация волонтерской помощи»");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> appService.save());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
