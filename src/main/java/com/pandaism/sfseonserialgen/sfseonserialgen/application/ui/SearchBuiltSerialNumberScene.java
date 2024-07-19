package com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;

import atlantafx.base.theme.NordDark;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenApplication;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchBuiltSerialNumberScene {
    public SearchBuiltSerialNumberScene(SFSeonSerialGenController SFSeonSerialGenController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFSeonSerialGenApplication.class.getResource("/com/pandaism/sfseonserialgen/sfseonserialgen/application/ui/search-built-serial-numbers-view.fxml"));
        Parent root = fxmlLoader.load();
        SearchBuiltSerialNumberController controller = fxmlLoader.getController();
        controller.setSFSeonSerialGenController(SFSeonSerialGenController);

        Scene scene = new Scene(root, 280, 250);
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        Stage stage = new Stage();
        stage.setTitle("Serial Number Detailed Search");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
