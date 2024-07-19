package com.pandaism.sfseonserialgen.sfseonserialgen.application;

import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.CreateNewPartIDScene;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.GeneratedSerialScene;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.SearchBuiltSerialNumberScene;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.popup.Messages;
import com.pandaism.sfseonserialgen.sfseonserialgen.util.BackupFile;
import com.pandaism.sfseonserialgen.sfseonserialgen.util.SerialNumberBuilder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SFSeonSerialGenController {
    public ComboBox<String> partIDComboBox;
    public TextField assigneeTextField;
    public TextField quantityTextField;
    public Label statusLabel;
    public TextField workOrderTextField;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void initialize() {
        Task<Collection<String>> appendPartsToComboBoxTask = new Task<>() {
            @Override
            protected Collection<String> call() {
                return SFSeonSerialGenApplication.getAccessDatabase().getPartIDList().values();
            }
        };
        appendPartsToComboBoxTask.setOnSucceeded(workerStateEvent -> Platform.runLater(() -> partIDComboBox.getItems().addAll(appendPartsToComboBoxTask.getValue())));
        this.executorService.submit(appendPartsToComboBoxTask);
    }

    public void onSerialGeneration() {
        if(this.partIDComboBox.getSelectionModel().getSelectedItem() != null
                && !assigneeTextField.getText().isEmpty()
                && !quantityTextField.getText().isEmpty()
                && !workOrderTextField.getText().isEmpty()) {
            Task<Integer> getNextAvailableSerialNumberTask = new Task<>() {
                @Override
                protected Integer call() {
                    return SFSeonSerialGenApplication.getAccessDatabase().getNextAvailableSerialNumber(partIDComboBox.getSelectionModel().getSelectedItem());
                }
            };
            getNextAvailableSerialNumberTask.setOnSucceeded(workerStateEvent -> {
                Task<String> getIdentifierFromPartIDTask = new Task<>() {
                    @Override
                    protected String call() {
                        String identifier = SFSeonSerialGenApplication.getAccessDatabase().getIdentifierFromPartID(partIDComboBox.getSelectionModel().getSelectedItem());
                        return identifier;
                    }
                };
                getIdentifierFromPartIDTask.setOnSucceeded(workerStateEvent1 -> {
                    String identifier = getIdentifierFromPartIDTask.getValue();
                    int startSerialNumber = getNextAvailableSerialNumberTask.getValue();
                    List<String> serialNumbers = new ArrayList<>();

                    for(int i = 0; i < Integer.parseInt(quantityTextField.getText()); i++) {
                        String formatted = String.format("%04d", startSerialNumber + i);

                        String serialNumber = new SerialNumberBuilder(identifier, formatted).buildSerialNumber();
                        serialNumbers.add(serialNumber);
                    }

                    Task<Void> appendSerialNumbersToDatabaseTask = getAppendSerialNumbersToDatabaseTask(serialNumbers);
                    this.executorService.submit(appendSerialNumbersToDatabaseTask);
                });
                this.executorService.submit(getIdentifierFromPartIDTask);
            });
            this.executorService.submit(getNextAvailableSerialNumberTask);
        } else {
            Messages.displayErrorMessage("Empty Field Detected", "Please fill out all the field.");
        }
    }

    private Task<Void> getAppendSerialNumbersToDatabaseTask(List<String> serialNumbers) {
        Task<Void> appendSerialNumbersToDatabaseTask = new Task<>() {
            @Override
            protected Void call() {
                SFSeonSerialGenApplication.getAccessDatabase().appendSerialNumbersToDatabase(partIDComboBox.getSelectionModel().getSelectedItem(), workOrderTextField.getText(), assigneeTextField.getText(), serialNumbers);
                return null;
            }
        };

        appendSerialNumbersToDatabaseTask.setOnRunning(workerStateEvent -> Platform.runLater(() -> this.statusLabel.setText("Generating Serials...")));

        appendSerialNumbersToDatabaseTask.setOnSucceeded(workerStateEvent2 -> {
            Platform.runLater(() -> this.statusLabel.setText("Generation Completed..."));
            try {
                new GeneratedSerialScene(this.workOrderTextField.getText(), serialNumbers);
                new BackupFile(this.workOrderTextField.getText(), this.partIDComboBox.getSelectionModel().getSelectedItem(), serialNumbers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return appendSerialNumbersToDatabaseTask;
    }

    public void onCreatePartIDClick() {
        try {
            new CreateNewPartIDScene(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPartIDToComboBox(String partID) {
        this.partIDComboBox.getItems().add(partID);
    }

    public void onAbout() {
        Messages.displayInfoMessage("About SF Seon Serial Generator",
                "SF Seon Serial Generator\n" +
                        "Build #1.0.5 SNAPSHOT, built on June 26, 2024\n" +
                        "VM: JDK 17.0.11\" 2024-04-16 LTS\n" +
                        "Windows 10.0\n" +
                        "Created By: Mike Nguyen | github.com/Pandaism\n");
    }

    public void onSearchBuiltSerialNumbers() {
        try {
            new SearchBuiltSerialNumberScene(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}