package service;

public interface IAdmin {
    void createLabSection(String sectionID, String courseID, String roomID);
    void assignInstructor(String sectionID, String instructorID);
    void addTAtoSection(String sectionID, String taID);

    // NEW: Method to set the standard weekly schedule
    void setWeeklySchedule(String sectionID, String dayStr, String startTimeStr, String endTimeStr);

    // Existing: Schedule makeup is already added
    void scheduleMakeUp(String sectionID, String dateStr, String startTime, String endTime);
}