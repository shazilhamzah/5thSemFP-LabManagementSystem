package service;

public interface IAdmin {
    void createLabSection(String sectionID, String courseID, String roomID);
    void assignInstructor(String sectionID, String instructorID);
    void addTAtoSection(String sectionID, String taID);
    // Using simple String for date/time for console simplicity, should be LocalDate/LocalTime
    void scheduleMakeUp(String sectionID, String dateStr, String startTime, String endTime);
}