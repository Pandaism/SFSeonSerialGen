package com.pandaism.sfseonserialgen.sfseonserialgen.util;

import java.time.LocalDateTime;

public class SerialNumberBuilder {
    private final String partCode;
    private final String dateCode;
    private final String sequenceCode;

    public SerialNumberBuilder(String partCode, String sequenceCode) {
        this.partCode = partCode;
        this.dateCode = calculateDateCode();
        this.sequenceCode = sequenceCode;
    }

    private String calculateDateCode() {
        // Get last 2 digit in the year (2024 = 24 | 2025 = 25 | etc)
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear() % 100);

        // Calculate month using alpha-numerical notation ([Jan-Sep] 01-09 | [Oct-Dec] 10-12)
        int monthValue = now.getMonthValue();
        String month;
        if(monthValue <= 9) {
            month = "0" + monthValue;
        } else {
            month = String.valueOf(monthValue);
        }

        // Calculate day of the month using alpha-numerical notation [1-31]
        int dayValue = now.getDayOfMonth();
        String day;
        if(dayValue <= 9) {
            day = "0" + dayValue;
        } else {
            day = String.valueOf(dayValue);
        }

        return year + month + day;
    }

    public String buildSerialNumber() {
        char locationCode = 'H';
        return locationCode + this.partCode + this.dateCode + this.sequenceCode;
    }
}
