package edu.touro.cs.mcon364;

import java.sql.*;
import java.util.*;

public class SQLDatabaseConnection {
    static int primaryKey = 2;
    private Set<Map<String, String>> emailList;

    SQLDatabaseConnection(Set<Map<String, String>> email){
        this.emailList = email;
    }
    public void execute() throws ClassNotFoundException {
        Map<String, String> env = System.getenv();
        String endpoint = env.get("dbendpoint");
        System.out.println(endpoint);
        String connectionUrl = // specifies how to connect to the database
                "jdbc:sqlserver://" + endpoint + ";"
                        + "database=InsertDataBase;"
                        + "user=username;"
                        + "password=InsertPassword;"
                        + "encrypt=false;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        String insertSql2 = "INSERT INTO Emails (emailId, emailAddress, timeStamp, source) VALUES (?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql2, Statement.RETURN_GENERATED_KEYS)) {
            for (Map<String, String> stringStringMap : emailList) {
                try {
                    preparedStatement.setInt(1, primaryKey++);
                    preparedStatement.setString(2, stringStringMap.get("email"));
                    preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    preparedStatement.setString(4, stringStringMap.get("linkFound"));
                    preparedStatement.executeUpdate();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    while (generatedKeys.next()) {
                        System.out.println("Generated primary key: " + generatedKeys.getInt(1));
                    }
                } catch (com.microsoft.sqlserver.jdbc.SQLServerException ex) {
                    System.out.println("Data truncated, skipping record.");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }




    private static List<Map<String, String>> generateFakeEmailList() {
        List<Map<String, String>> emailList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String email = "email" + (i + 1) + "@example.com";
            String linkFound = "http://example.com/link" + (i + 1);

            Map<String, String> emailData = new HashMap<>();
            emailData.put("email", email);
            emailData.put("linkFound", linkFound);

            emailList.add(emailData);
        }

        return emailList;
    }
}


