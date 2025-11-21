package service;

import model.LabSection;
import model.Session;
import model.Schedule;
import model.ClassStatus;
import repository.IRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService implements IReport {
    private final IRepository repository;
    private static final String REPORT_DIRECTORY = "reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ReportService(IRepository repository) {
        this.repository = repository;
    }

    // --- Helper for Directory Creation ---
    private String ensureDirectory() {
        File directory = new File(REPORT_DIRECTORY);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return "ERROR: Failed to create report directory: " + REPORT_DIRECTORY;
            }
        }
        return null; // Null indicates success
    }

    // --- 1. Weekly Schedule Report (Already updated, included for completeness) ---

    @Override
    public String generateWeeklyScheduleReport() {
        List<LabSection> sections = repository.getAllSections();
        String directoryError = ensureDirectory();
        if (directoryError != null) return directoryError;

        String fileName = REPORT_DIRECTORY + "/WeeklyScheduleReport_" + System.currentTimeMillis() + ".csv";

        if (sections.isEmpty()) return "No sections found to generate schedule report.";

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {

            pw.println("SectionID,CourseID,InstructorID,Day,StartTime,EndTime");

            for (LabSection s : sections) {
                Schedule weeklySchedule = s.getSchedule();

                String day = "N/A";
                String startTime = "N/A";
                String endTime = "N/A";

                if (weeklySchedule != null) {
                    day = weeklySchedule.getDay().toString();
                    startTime = weeklySchedule.getExpectedStartTime().format(TIME_FORMATTER);
                    endTime = weeklySchedule.getExpectedEndTime().format(TIME_FORMATTER);
                }

                String line = String.format("%s,%s,%s,%s,%s,%s",
                        s.getSectionID(),
                        s.getCourseID(),
                        s.getInstructorID(),
                        day,
                        startTime,
                        endTime
                );
                pw.println(line);
            }

            return "SUCCESS: Weekly Schedule Report generated successfully at " + fileName;

        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            return "ERROR: Failed to generate report due to file writing issue.";
        }
    }

    // --- 2. Weekly TimeSheet Report (Updated for CSV) ---

    @Override
    public String generateWeeklyTimeSheetReport(String startDateStr) {
        String directoryError = ensureDirectory();
        if (directoryError != null) return directoryError;

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return "Error: Invalid start date format. Use YYYY-MM-DD.";
        }

        LocalDate endDate = startDate.plusDays(7);
        List<LabSection> allSections = repository.getAllSections();

        String fileName = REPORT_DIRECTORY + "/WeeklyTimeSheetReport_" + startDateStr + "_" + System.currentTimeMillis() + ".csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {

            // Write Header Row
            pw.println("SectionID,CourseID,InstructorID,SessionDate,Status,AttendedWeekStart");

            // Process and write data
            for (LabSection section : allSections) {
                for (Session session : section.getSessions()) {
                    LocalDate sessionDate = session.getDate();

                    // Filter by date range and status (Attendant sets status to Completed)
                    boolean isInWeek = sessionDate.isEqual(startDate) ||
                            sessionDate.isAfter(startDate) &&
                                    sessionDate.isBefore(endDate);

                    if (isInWeek && session.getStatus() == ClassStatus.Completed) {

                        String line = String.format("%s,%s,%s,%s,%s,%s",
                                section.getSectionID(),
                                section.getCourseID(),
                                section.getInstructorID(),
                                sessionDate.format(DATE_FORMATTER),
                                session.getStatus().name(),
                                startDate.format(DATE_FORMATTER)
                        );
                        pw.println(line);
                    }
                }
            }

            return "SUCCESS: Weekly TimeSheet Report generated successfully at " + fileName;

        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            return "ERROR: Failed to generate report due to file writing issue.";
        }
    }

    // --- 3. Semester Report (Updated for CSV) ---

    @Override
    public String generateSemesterReport(String sectionID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) return "Error: Section " + sectionID + " not found for semester report.";

        String directoryError = ensureDirectory();
        if (directoryError != null) return directoryError;

        String fileName = REPORT_DIRECTORY + "/SemesterReport_" + sectionID + "_" + System.currentTimeMillis() + ".csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {

            // Write metadata header (using a comment line start with #)
            pw.println("#Semester Report for Section," + sectionID);
            pw.println("#Course ID," + section.getCourseID());
            pw.println("#Instructor ID," + section.getInstructorID());
            pw.println("#Total Sessions," + section.getSessions().size());
            pw.println(); // Blank line for separation

            // Write session details header
            pw.println("SessionDate,Status,Instructor");

            // Write session details data
            for (Session session : section.getSessions()) {
                String line = String.format("%s,%s,%s",
                        session.getDate().format(DATE_FORMATTER),
                        session.getStatus().name(),
                        section.getInstructorID() // Instructor is repeated for every session line
                );
                pw.println(line);
            }

            return "SUCCESS: Semester Report generated successfully at " + fileName;

        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            return "ERROR: Failed to generate report due to file writing issue.";
        }
    }
}