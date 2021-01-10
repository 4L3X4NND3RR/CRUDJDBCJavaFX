package com.alexander.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {
    private final ConnectionPreferences preferences;
    private Connection connection;
    private final String url;

    public ConnectionManager() {
        preferences = new ConnectionPreferences();
        url = "jdbc:postgresql://"+preferences.getServer()+":"+preferences.getPort()+"/"+ preferences.getDatabase()+"?user="+preferences.getUsername()+"&password="+preferences.getPassword();
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            MessageAlert.showAlertError(null,"Algo salio mal", "Error ", e);
        }
    }

    public boolean testConnection() {
        try {
            connection = DriverManager.getConnection(url, preferences.getUsername(), preferences.getPassword());
            return true;
        } catch (SQLException e) {
            MessageAlert.showAlertError(null, "Error", "Algo salio mal con la base de datos", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, preferences.getUsername(), preferences.getPassword());
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException ignored){
            }
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (resultSet !=  null) {
            try {
                resultSet.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignored){
            }
        }
    }

    public void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException ignored){
            }
        }
    }
}
