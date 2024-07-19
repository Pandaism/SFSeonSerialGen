package com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;

import atlantafx.base.theme.NordDark;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GeneratedSerialScene {
    public GeneratedSerialScene(String workOrder, List<String> serialNumbers) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFSeonSerialGenApplication.class.getResource("/com/pandaism/sfseonserialgen/sfseonserialgen/application/ui/generated-serial-number-view.fxml"));
        Parent root = fxmlLoader.load();
        GeneratedSerialController controller = fxmlLoader.getController();
        controller.setSerialNumbers(serialNumbers);

        Scene scene = new Scene(root, 400, 300);
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        Stage stage = new Stage();
        stage.setTitle("Generated Serial for " + workOrder);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
