package model;

import java.io.Serializable;

public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    private String buildingID;
    private String name;
    private String attendantID;

    public Building(String buildingID, String name, String attendantID) {
        this.buildingID = buildingID;
        this.name = name;
        this.attendantID = attendantID;
    }

    // Getters
    public String getBuildingID() {
        return buildingID;
    }

    public String getAttendantID() {
        return attendantID;
    }
}