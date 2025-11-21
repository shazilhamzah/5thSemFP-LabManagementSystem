package service;

import model.*; // Import LabSection, Session, ClassStatus
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

        LabSection section = repository.getSectionByID(sectionID);

        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // 1. CHECK IF SESSION EXISTS OR CREATE NEW ONE
            Session session = section.getSessionbyDate(date);

            if (session == null) {
                // If this is the first time logging attendance for this date, create a new session.
                session = new Session(date);
                section.addSession(session);
            }

            // 2. UPDATE THE SESSION STATUS (assuming filling timesheet means class was completed)
            session.setStatus(ClassStatus.Completed);

            // 3. UPDATE THE REPOSITORY
            repository.updateSection(section);

            System.out.println("SUCCESS: TimeSheet filled by " + attendantID + " for " + sectionID +
                    " on " + dateStr + " (" + start + " to " + end + ").");
            System.out.println("(Session count updated in report.)");

        } catch (Exception e) {
            System.out.println("Error: Invalid date format. Use YYYY-MM-DD. " + e.getMessage());
        }
    }
}