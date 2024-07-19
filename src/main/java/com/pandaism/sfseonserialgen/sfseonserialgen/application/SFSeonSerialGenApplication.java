package com.pandaism.sfseonserialgen.sfseonserialgen.application;

import atlantafx.base.theme.NordDark;
import com.pandaism.sfseonserialgen.sfseonserialgen.util.AccessDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SFSeonSerialGenApplication extends Application {
    private static AccessDatabase accessDatabase;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFSeonSerialGenApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 280, 350);
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        stage.setTitle("SF Seon Serial Generator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void launchApplication() {
        accessDatabase = new AccessDatabase();
        accessDatabase.initializeDatabase();
        launch();
    }

    public static AccessDatabase getAccessDatabase() {
        return accessDatabase;
    }
}