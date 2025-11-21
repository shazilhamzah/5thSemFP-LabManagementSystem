package service;

import model.LabSection;
import repository.IRepository;
import java.util.List;
import java.util.stream.Collectors;
import model.Schedule;
import model.LabSection;
import model.Session;
import model.ClassStatus; // Needed to filter for completed sessions
import repository.IRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService implements IReport {
    private final IRepository repository;

    public ReportService(IRepository repository) {
        this.repository = repository;
    }
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Reports are simplified to basic string generation for the console application.
    @Override
    public String generateWeeklyScheduleReport() {
        List<LabSection> sections = repository.getAllSections();
        if (sections.isEmpty()) return "No sections found to generate schedule report.";

        String schedule = "--- Weekly Lab Schedule Report ---\n";

        schedule += sections.stream()
                .map(s -> {
                    // Determine the schedule detail
                    Schedule weeklySchedule = s.getSchedule(); // Assuming getSchedule() exists in LabSection
                    String scheduleDetail;

                    if (weeklySchedule != null) {
                        scheduleDetail = String.format(
                                "%s, %s - %s",
                                weeklySchedule.getDay(),
                                weeklySchedule.getExpectedStartTime(),
                                weeklySchedule.getExpectedEndTime()
                        );
                    } else {
                        scheduleDetail = "Not Scheduled";
                    }

                    // Format the final report line
                    return String.format(
                            "Section: %s | Course: %s | Instructor: %s | Schedule: %s",
                            s.getSectionID(),
                            s.getCourseID(),
                            s.getInstructorID(),
                            scheduleDetail
                    );
                })
                .collect(Collectors.joining("\n"));

        return schedule;
    }

    @Override
    public String generateWeeklyTimeSheetReport(String startDateStr) {

        // 1. Define Reporting Period
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            // Ensure the report week starts on a Monday (or use the provided start date)
            // For simplicity, we'll assume startDate is the Monday of the week.
        } catch (Exception e) {
            return "Error: Invalid start date format. Use YYYY-MM-DD.";
        }

        // Define the week boundaries (start date inclusive, end date exclusive)
        LocalDate endDate = startDate.plusDays(7);

        StringBuilder report = new StringBuilder();
        report.append(String.format("--- Weekly TimeSheet Report (%s to %s) ---\n", startDate, endDate.minusDays(1)));

        List<LabSection> allSections = repository.getAllSections();

        if (allSections.isEmpty()) {
            report.append("No lab sections found in the system.\n");
            return report.toString();
        }

        // Map to group completed sessions by the section they belong to
        Map<LabSection, List<Session>> attendedSessions = allSections.stream()
                .collect(Collectors.toMap(
                        section -> section,
                        section -> section.getSessions().stream()
                                .filter(session -> {
                                    LocalDate sessionDate = session.getDate();
                                    // Filter by date range and status (Attendant sets status to Completed)
                                    return sessionDate.isEqual(startDate) ||
                                            sessionDate.isAfter(startDate) &&
                                                    sessionDate.isBefore(endDate) &&
                                                    session.getStatus() == ClassStatus.Completed;
                                })
                                .collect(Collectors.toList())
                ))
                // Remove sections that had no attended sessions this week
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        // 2. Format the Report Output
        if (attendedSessions.isEmpty()) {
            report.append("No attended sessions recorded for this week.\n");
            return report.toString();
        }

        report.append("Attended Labs (Recorded by Attendants):\n");

        for (Map.Entry<LabSection, List<Session>> entry : attendedSessions.entrySet()) {
            LabSection section = entry.getKey();
            List<Session> sessions = entry.getValue();

            report.append(String.format("\nSection: %s (Course: %s, Instructor: %s)\n",
                    section.getSectionID(),
                    section.getCourseID(),
                    section.getInstructorID()));

            sessions.forEach(session -> {
                report.append(String.format("  -> Date: %s | Status: %s\n",
                        session.getDate().format(DATE_FORMATTER),
                        session.getStatus()));
            });
        }

        return report.toString();
    }

    // Inside src/service/ReportService.java

// ... existing code ...

    @Override
    public String generateSemesterReport(String sectionID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) return "Error: Section " + sectionID + " not found for semester report.";

        // 1. Build the detailed session list string
        StringBuilder sessionDetails = new StringBuilder("\nSession Details:\n");

        if (section.getSessions().isEmpty()) {
            sessionDetails.append("  No sessions recorded yet.\n");
        } else {
            // Iterate through all sessions and append their date and status
            section.getSessions().stream()
                    .forEach(session -> {
                        sessionDetails.append(String.format("  Date: %s | Status: %s\n",
                                session.getDate(), session.getStatus()));
                    });
        }

        // 2. Combine all report parts
        return String.format(
                "--- Semester Report for %s ---\n" +
                        "Course ID: %s\n" +
                        "Total Sessions: %d\n" +
                        "Instructor ID: %s\n" +
                        "%s" ,
                sectionID,
                section.getCourseID(), // Added Course ID for context
                section.getSessions().size(),
                section.getInstructorID(),
                sessionDetails.toString()
        );
    }
}