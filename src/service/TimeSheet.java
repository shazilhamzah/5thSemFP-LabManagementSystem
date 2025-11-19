package service;

import repository.IRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeSheet implements ITimeSheet {
    private final IRepository repository;

    public TimeSheet(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public void fillTimeSheet(String attendantID, String sectionID, String dateStr, String start, String end) {
        // 1. Basic Validation: Is the attendant responsible for this section's building?
        // This requires getting the section's room, the room's building, and checking the building's attendant ID.

        // Simplified validation for demonstration:
        if (repository.getSectionByID(sectionID) == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // In a real system, this would write a record to a separate TimeSheet data structure.
            // For now, we simulate the action.
            System.out.println("SUCCESS: TimeSheet filled by " + attendantID + " for " + sectionID +
                    " on " + dateStr + " (" + start + " to " + end + ").");

            // OPTIONAL: Update the section's session status to 'Completed' here if logging attendance.
            // LabSection section = repository.getSectionByID(sectionID);
            // Session session = section.getSessionbyDate(date);
            // if (session != null) {
            //     session.setStatus(ClassStatus.Completed);
            //     repository.updateSection(section);
            // }

        } catch (Exception e) {
            System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
        }
    }
}