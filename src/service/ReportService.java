package service;

import model.LabSection;
import repository.IRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService implements IReport {
    private final IRepository repository;

    public ReportService(IRepository repository) {
        this.repository = repository;
    }

    // Reports are simplified to basic string generation for the console application.
    @Override
    public String generateWeeklyScheduleReport() {
        List<LabSection> sections = repository.getAllSections();
        if (sections.isEmpty()) return "No sections found to generate schedule report.";

        String schedule = "--- Weekly Lab Schedule Report ---\n";
        schedule += sections.stream()
                .map(s -> "Section: " + s.getSectionID() + ", Course: " + s.getCourseID() + ", Instructor: " + s.getInstructorID())
                .collect(Collectors.joining("\n"));

        return schedule;
    }

    @Override
    public String generateWeeklyTimeSheetReport(String startDate) {
        // In a real application, this would query the TimeSheet data structure.
        return String.format("--- Weekly TimeSheet Report (Starting %s) ---\n" +
                "Report for attendants' recorded hours. (Implementation placeholder)", startDate);
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