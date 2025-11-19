package service;

public interface ITimeSheet {
    // Using simple String for date/time for console simplicity, should be LocalDate/LocalTime
    void fillTimeSheet(String attendantID, String sectionID, String dateStr, String start, String end);
}