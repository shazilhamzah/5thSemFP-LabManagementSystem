package model;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomID;
    private String roomNumber;
    private String buildingID;

    public Room(String roomID, String roomNumber, String buildingID) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.buildingID = buildingID;
    }

    // Getters
    public String getRoomID() {
        return roomID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBuildingID() {
        return buildingID;
    }
}