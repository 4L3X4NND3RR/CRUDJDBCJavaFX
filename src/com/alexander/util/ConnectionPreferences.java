package com.alexander.util;

import java.util.prefs.Preferences;

public class ConnectionPreferences {
    private final Preferences preferences;
    private String server;
    private String port;
    private String database;
    private String username;
    private String password;

    public ConnectionPreferences(){
        preferences = Preferences.userNodeForPackage(ConnectionPreferences.class);
    }

    public String getServer() {
        return preferences.get("server", "");
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return preferences.get("port", "");
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return preferences.get("database", "");
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return preferences.get("username", "");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return preferences.get("password", "");
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void savePreferences(){
        preferences.put("server", server);
        preferences.put("port", port);
        preferences.put("database", database);
        preferences.put("username", username);
        preferences.put("password", password);
    }
}
