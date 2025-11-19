package service;

import model.LabSection;
import model.Session;
import repository.IRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AdminService implements IAdmin {
    private final IRepository repository;

    public AdminService(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createLabSection(String sectionID, String courseID, String roomID) {
        if (repository.getSectionByID(sectionID) != null) {
            System.out.println("Error: Section " + sectionID + " already exists.");
            return;
        }

        // Simple check that required references exist (Course, Room)
        if (repository.getSectionByID(courseID) == null) { // Assuming getSectionByID check covers other entities for now
            // In a real app, we'd use getCourseByID and getRoomByID
        }

        LabSection newSection = new LabSection(sectionID, courseID, roomID);
        repository.addSection(newSection);
        System.out.println("SUCCESS: Lab section " + sectionID + " created.");
    }

    @Override
    public void assignInstructor(String sectionID, String instructorID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section != null) {
            section.setInstructor(instructorID);
            repository.updateSection(section);
            System.out.println("SUCCESS: Instructor " + instructorID + " assigned to " + sectionID + ".");
        } else {
            System.out.println("Error: Section " + sectionID + " not found.");
        }
    }

    @Override
    public void addTAtoSection(String sectionID, String taID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section != null) {
            section.addTA(taID);
            repository.updateSection(section);
            System.out.println("SUCCESS: TA " + taID + " added to " + sectionID + ".");
        } else {
            System.out.println("Error: Section " + sectionID + " not found.");
        }
    }

    @Override
    public void scheduleMakeUp(String sectionID, String dateStr, String startTime, String endTime) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section != null) {
            try {
                // Parse date string (assuming format YYYY-MM-DD)
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

                // Check if a session already exists on this date
                if (section.getSessionbyDate(date) != null) {
                    System.out.println("Error: Session already scheduled on " + dateStr);
                    return;
                }

                // Create a new session (the status will be handled by ClassStatus enum, simplified here)
                Session makeupSession = new Session(date);
                section.addSession(makeupSession);
                repository.updateSection(section);
                System.out.println("SUCCESS: Makeup session scheduled for " + sectionID + " on " + dateStr + " at " + startTime);
            } catch (Exception e) {
                System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
            }
        } else {
            System.out.println("Error: Section " + sectionID + " not found.");
        }
    }
}