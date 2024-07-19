package com.pandaism.sfseonserialgen.sfseonserialgen.util;

import com.pandaism.sfseonserialgen.sfseonserialgen.application.ui.SearchBuiltSerialNumberController.SerialNumberHelper;
import net.ucanaccess.jdbc.UcanaccessSQLException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

// Part ID have to replace '-' with '_' in table name
public class AccessDatabase {
    // Access database path
    private final String databaseURL = "jdbc:ucanaccess://V://Non-Critical//Seon//- SF Seon Serial Number Generator Tool//Serial Number Database.accdb";

    // Get the next available serial number in the database based on part ID
    public int getNextAvailableSerialNumber(String partID) {
        try (Connection connection = getConnection()) {
            if(isNumberical(partID)) {
                partID = "TBB-" + partID;
            }

            String identifierNumberQuery =
                    "SELECT SERIAL_NUMBER " +
                            "FROM " + partID.replaceAll("-", "_") + " " +
                            "WHERE ID = (SELECT MAX(ID) " +
                            "FROM " + partID.replaceAll("-", "_") + ");";
            PreparedStatement preparedStatement = connection.prepareStatement(identifierNumberQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                String serialNumber = rs.getString(1);

                LocalDateTime now = LocalDateTime.now();
                int dayValue = now.getDayOfMonth();
                String day;
                if(dayValue <= 9) {
                    day = "0" + dayValue;
                } else {
                    day = String.valueOf(dayValue);
                }

                if(serialNumber.substring(7, 9).equals(day)) {
                    // Return the last 4 digit in sequence and add 1 to get next available sequence number to use
                    return Integer.parseInt(serialNumber.substring(9)) + 1;
                }
            }
            // If database table is empty start from 0
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNumberical(String partID) {
        try {
             Integer.parseInt(partID);
             return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Append data to the appropriate part ID table in database
    public void appendSerialNumbersToDatabase(String partID, String workOrder, String assignee, List<String> serialNumbers) {
       try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            String appendPartIDIdentifier = "INSERT INTO " + partID.replaceAll("-", "_") + " ([ASSIGNEE], [WORK_ORDER], [DATE_CREATED], [SERIAL_NUMBER]) VALUES (?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(appendPartIDIdentifier)) {
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
                for (String serialNumber : serialNumbers) {
                    statement.setString(1, assignee);
                    statement.setString(2, workOrder);
                    statement.setString(3, date);
                    statement.setString(4, serialNumber);
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get part ID identifier code
    public String getIdentifierFromPartID(String partID) {
        try (Connection connection = getConnection()) {
            String identifierNumberQuery = "SELECT IDENTIFIER FROM Identifiers WHERE PART_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(identifierNumberQuery);
            preparedStatement.setString(1, partID);
            ResultSet rs = preparedStatement.executeQuery();
            String result = "";
            while (rs.next()) {
                result = rs.getString(1);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get the next available Identifier code
    public String getAvailableIdentifier() {
        try (Connection connection = getConnection()) {
            String identifierNumberQuery =
                    "SELECT IDENTIFIER " +
                            "FROM Identifiers " +
                            "WHERE ID = (SELECT MAX(ID) " +
                            "FROM Identifiers);";
            PreparedStatement preparedStatement = connection.prepareStatement(identifierNumberQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                int identifier = Integer.parseInt(rs.getString(1)) + 1;
                if(identifier < 10) {
                    // If identifier is less than 10, add a leading zero
                    return "0" + identifier;
                } else {
                    return String.valueOf(identifier);
                }
            } else {
                return "00";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get all identifier and part ID pairing
    public HashMap<String, String> getPartIDList() {
        try (Connection connection = getConnection()) {
            String identifierNumberQuery = "SELECT IDENTIFIER, PART_ID FROM Identifiers;";
            PreparedStatement preparedStatement = connection.prepareStatement(identifierNumberQuery);
            ResultSet rs = preparedStatement.executeQuery();

            HashMap<String, String> partIDs = new HashMap<>();

            while (rs.next()) {
                partIDs.put(rs.getString(1), rs.getString(2));
            }

            return partIDs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Append a new part ID identifier to Identifiers table
    public void appendPartIDIdentifier(String identifier, String partID) {
        try (Connection connection = getConnection()) {
            String appendPartIDIdentifier = "INSERT INTO Identifiers ([IDENTIFIER], [PART_ID]) VALUES (?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(appendPartIDIdentifier)) {
                statement.setString(1, identifier);
                statement.setString(2, partID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get serial number details
    public SerialNumberHelper getSerialNumberDetail(SerialNumberHelper serialNumberHelper) {
        String partID = serialNumberHelper.getPartID().replaceAll("-", "_");
        try (Connection connection = getConnection()) {
            String getSerialNumberDetailQuery = "SELECT * FROM " + partID + " WHERE SERIAL_NUMBER = ?";
            PreparedStatement statement = connection.prepareStatement(getSerialNumberDetailQuery);
            statement.setString(1, serialNumberHelper.getSerialNumber());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                serialNumberHelper.setWorkNumber(rs.getString("WORK_ORDER"));
                serialNumberHelper.setAssignee(rs.getString("ASSIGNEE"));
                serialNumberHelper.setDateCreated(rs.getString("DATE_CREATED"));
            }

            return serialNumberHelper;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Build a default table to store all serial numbers created per part ID
    public void buildTable(String partID) {
        try (Connection connection = getConnection()) {
            if(isNumberical(partID)) {
                partID = "TBB-" + partID;
            }

            String createPartIDTableQuery = "CREATE TABLE " + partID.replaceAll("-", "_") + " (" +
                    "ID COUNTER PRIMARY KEY," +
                    "WORK_ORDER TEXT(100)," +
                    "ASSIGNEE TEXT(100)," +
                    "DATE_CREATED TEXT(100)," +
                    "SERIAL_NUMBER TEXT(13)," +
                    "UNIQUE (SERIAL_NUMBER)" +
                    ");";
            try(PreparedStatement createPartIDTableStatement = connection.prepareStatement(createPartIDTableQuery)) {
                createPartIDTableStatement.executeUpdate();
            } catch (UcanaccessSQLException e) {
                if(e.getMessage().indexOf("object name already exist") > 0) {
                    System.out.println("INFO: " + partID + " table already exists.");
                } else {
                    throw e;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Create the necessary Indentifiers table in Access database
    public void initializeDatabase() {
        try (Connection connection = getConnection()) {
            String createIdentifierTableQuery = "CREATE TABLE Identifiers (" +
                    "ID COUNTER PRIMARY KEY," +
                    "IDENTIFIER TEXT(2)," +
                    "PART_ID TEXT(100)," +
                    "UNIQUE (IDENTIFIER, PART_ID)" +
                    ");";
            try(PreparedStatement createIdentifierTableStatement = connection.prepareStatement(createIdentifierTableQuery)) {
                createIdentifierTableStatement.executeUpdate();
            } catch (UcanaccessSQLException e) {
                if(e.getMessage().indexOf("object name already exist") > 0) {
                    System.out.println("INFO: Identifier table already exists.");
                } else {
                    throw e;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseURL);
    }
}
