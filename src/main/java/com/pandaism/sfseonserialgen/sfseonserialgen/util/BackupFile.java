package com.pandaism.sfseonserialgen.sfseonserialgen.util;

import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.popup.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BackupFile {

    public BackupFile(String workOrder, String partID, List<String> serialNumbers) {
        File backupFile = new File("./Generated Serial Numbers/" + workOrder + "-" + partID + ".txt");

        buildBackupFile(backupFile, workOrder, serialNumbers);
    }

    private void buildBackupFile(File file, String workOrder, List<String> serialNumbers) {
        if(fileCreation(file)) {
            // Append data to the file
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for(int i = 0; i < serialNumbers.size(); i++) {
                    if(i == serialNumbers.size() - 1) {
                        writer.write(serialNumbers.get(i));
                    } else {
                        writer.write(serialNumbers.get(i) + "\n");
                    }
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Messages.displayErrorMessage("Work Order File Already Exist", "File for work order [" + workOrder + "] already exists.");
        }
    }

    private boolean fileCreation(File file) {
        //Check for file existence before creation
        if(file.exists()) {
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
