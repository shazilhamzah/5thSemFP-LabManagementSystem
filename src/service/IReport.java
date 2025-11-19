package service;

public interface IReport {
    String generateWeeklyScheduleReport();
    String generateWeeklyTimeSheetReport(String startDate);
    String generateSemesterReport(String sectionID);
}