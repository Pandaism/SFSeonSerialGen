package com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;

import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenController;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenApplication;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.popup.Messages;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNewPartIDController {
    public Label identifierLabel;
    public TextField partIDField;

    private SFSeonSerialGenController SFSeonSerialGenController;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void initialize() {
        Task<String> getIdentifierTask = new Task<>() {
            @Override
            protected String call() {
                return SFSeonSerialGenApplication.getAccessDatabase().getAvailableIdentifier();
            }
        };
        getIdentifierTask.setOnSucceeded(workerStateEvent -> Platform.runLater(() -> this.identifierLabel.setText(getIdentifierTask.getValue())));
        this.executorService.submit(getIdentifierTask);
    }

    public void createNewIdentifier() {
        if(!this.partIDField.getText().isEmpty()) {
            Task<Void> appendIdentifierTask = new Task<>() {
                @Override
                protected Void call() {
                    SFSeonSerialGenApplication.getAccessDatabase().appendPartIDIdentifier(identifierLabel.getText(), partIDField.getText());
                    return null;
                }
            };
            appendIdentifierTask.setOnSucceeded(workerStateEvent -> {
                Task<Void> createPartTable = new Task<>() {
                    @Override
                    protected Void call() {
                        SFSeonSerialGenApplication.getAccessDatabase().buildTable(partIDField.getText());
                        return null;
                    }
                };
                createPartTable.setOnSucceeded(workerStateEvent1 -> {
                    Messages.displayInfoMessage("Successful Part ID Identifier Creation",this.partIDField.getText() + " has been appended to database with identifier code " + identifierLabel.getText());

                    Task<String> getIdentifierTask = new Task<>() {
                        @Override
                        protected String call() {
                            return SFSeonSerialGenApplication.getAccessDatabase().getAvailableIdentifier();
                        }
                    };
                    getIdentifierTask.setOnSucceeded(workerStateEvent2 -> Platform.runLater(() -> {
                        this.identifierLabel.setText(getIdentifierTask.getValue());
                        this.SFSeonSerialGenController.addPartIDToComboBox(this.partIDField.getText());
                        this.partIDField.setText("");
                    }));
                    this.SFSeonSerialGenController.getExecutorService().submit(getIdentifierTask);
                });
                this.SFSeonSerialGenController.getExecutorService().submit(createPartTable);
            });
            this.SFSeonSerialGenController.getExecutorService().submit(appendIdentifierTask);
        } else {
            Messages.displayErrorMessage("Empty Part ID Field", "Part ID field is empty.");
        }
    }

    public void setSFSeonSerialGenController(SFSeonSerialGenController SFSeonSerialGenController) {
        this.SFSeonSerialGenController = SFSeonSerialGenController;
    }
}
