package edu.touro.cs.mcon364;

import java.sql.*;
import java.util.Map;

public class SQLDatabaseConnection {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void main(String[] args) throws ClassNotFoundException {
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
        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection(connectionUrl); // AutoCloseable
             Statement statement = connection.createStatement();)
        {
            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT * FROM Emails;"; // Guardrails
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql2 = "INSERT INTO Students (FirstName, LastName) VALUES (?, ?);";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql2, Statement.RETURN_GENERATED_KEYS);) {
            {
                prepsInsertProduct.setString(1,"Joe");
                prepsInsertProduct.setString(2,"Shmoe");
                prepsInsertProduct.execute();

                resultSet = prepsInsertProduct.getGeneratedKeys();
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
            }} catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}


