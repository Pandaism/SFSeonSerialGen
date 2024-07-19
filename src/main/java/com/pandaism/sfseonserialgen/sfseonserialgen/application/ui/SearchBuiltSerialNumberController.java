package com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;

import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenApplication;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.SFSeonSerialGenController;
import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.popup.Messages;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchBuiltSerialNumberController {
    public TextArea searchField;
    private SFSeonSerialGenController SFSeonSerialGenController;

    public void onSearch() {
        String[] serialNumbers = this.searchField.getText().split("\n");
        Task<HashMap<String, String>> getIdentifierPairingTask = new Task<>() {
            @Override
            protected HashMap<String, String> call() {
                return SFSeonSerialGenApplication.getAccessDatabase().getPartIDList();
            }
        };

        getIdentifierPairingTask.setOnSucceeded((workerStateEvent1) -> {
            HashMap<String, String> identifierPairing = getIdentifierPairingTask.getValue();

            ExecutorService cachedExecutor = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(serialNumbers.length);
            StringBuilder investigationMessage = new StringBuilder();

            for(String serialNumber : serialNumbers) {
                String partID = identifierPairing.get(serialNumber.substring(1, 3));
                Task<SerialNumberHelper> getSerialNumberDetailTask = new Task<>() {
                    @Override
                    protected SerialNumberHelper call() {
                        return SFSeonSerialGenApplication.getAccessDatabase().getSerialNumberDetail(new SerialNumberHelper(serialNumber, partID));
                    }
                };

                getSerialNumberDetailTask.setOnSucceeded(workerStateEvent2 -> {
                    SerialNumberHelper serialNumberHelper = getSerialNumberDetailTask.getValue();

                    investigationMessage
                            .append("=================================\n")
                            .append("Unit Serial Number Investigating: ").append(serialNumberHelper.getSerialNumber()).append("\n")
                            .append("\tUnit is classified as a ").append(serialNumberHelper.getPartID()).append("\n")
                            .append("\tUnit created under work order: ").append(serialNumberHelper.getWorkNumber()).append("\n")
                            .append("\tUnit was built by: ").append(serialNumberHelper.getAssignee()).append("\n")
                            .append("\tUnit was created on: ").append(serialNumberHelper.getDateCreated()).append("\n")
                            .append("=================================\n");

                    latch.countDown();

                    if(latch.getCount() == 0) {
                        cachedExecutor.shutdown();
                        Messages.displayLongInfoMessage("Completed Search Dialog", investigationMessage.toString());
                    }
                });
                cachedExecutor.submit(getSerialNumberDetailTask);
            }
        });
        this.SFSeonSerialGenController.getExecutorService().submit(getIdentifierPairingTask);

    }

    public void setSFSeonSerialGenController(SFSeonSerialGenController SFSeonSerialGenController) {
        this.SFSeonSerialGenController = SFSeonSerialGenController;
    }

    public static class SerialNumberHelper {
        //H162406310004
        private final String serialNumber;
        private final String partID;
        private String workNumber;
        private String assignee;
        private String dateCreated;

        public SerialNumberHelper(String serialNumber, String partID) {
            this.serialNumber = serialNumber;
            this.partID = partID;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getPartID() {
            return partID;
        }

        public String getWorkNumber() {
            return workNumber;
        }

        public void setWorkNumber(String workNumber) {
            this.workNumber = workNumber;
        }

        public String getAssignee() {
            return assignee;
        }

        public void setAssignee(String assignee) {
            this.assignee = assignee;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }
    }
}
