package com.tarek.filesignerapp.model;

public class KeyGenerationRequest {
    private String alias;
    private String password;
    private String dname;
    private int keySize = 2048;
    private int validityDays = 3650;

    // Getters and setters
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDname() { return dname; }
    public void setDname(String dname) { this.dname = dname; }

    public int getKeySize() { return keySize; }
    public void setKeySize(int keySize) { this.keySize = keySize; }

    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }
}
