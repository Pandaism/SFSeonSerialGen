package com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;

import javafx.scene.control.TextArea;

import java.util.List;

public class GeneratedSerialController {
    public TextArea serialNumberTextArea;

    public void initialize() {
        serialNumberTextArea.setEditable(false);
    }

    public void setSerialNumbers(List<String> serialNumbers) {
        StringBuilder serialNumberString = new StringBuilder();
        for(int i = 0; i < serialNumbers.size(); i++) {
            if(i == serialNumbers.size() - 1) {
                serialNumberString.append(serialNumbers.get(i));
            } else {
                serialNumberString.append(serialNumbers.get(i)).append("\n");
            }
        }
        this.serialNumberTextArea.setText(serialNumberString.toString());
    }
}
