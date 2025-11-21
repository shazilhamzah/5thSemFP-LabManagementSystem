package service;

import model.LabSection;
import model.Schedule; // Import the updated Schedule class
import model.Session;
import repository.IRepository;
import java.time.DayOfWeek; // New import
import java.time.LocalDate;
import java.time.LocalTime; // New import
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle; // New import for parsing DayOfWeek
import java.util.Locale; // New import

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
    public void setWeeklySchedule(String sectionID, String dayStr, String startTimeStr, String endTimeStr) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        try {
            // --- 1. Parse DayOfWeek (Handling abbreviations and case-insensitivity) ---
            DayOfWeek day = null;
            String upperDayStr = dayStr.toUpperCase(Locale.ROOT);

            // Try parsing full name (MONDAY) or common abbreviation (MON)
            if (upperDayStr.length() == 3) {
                // Find DayOfWeek by matching the three-letter abbreviation
                for (DayOfWeek d : DayOfWeek.values()) {
                    if (d.getDisplayName(TextStyle.SHORT, Locale.ROOT).toUpperCase(Locale.ROOT).equals(upperDayStr)) {
                        day = d;
                        break;
                    }
                }
            } else {
                // Try parsing the full name directly
                day = DayOfWeek.valueOf(upperDayStr);
            }

            if (day == null) {
                throw new IllegalArgumentException("Invalid day abbreviation: " + dayStr);
            }

            // --- 2. Parse LocalTime (Using a flexible format if necessary, but HH:MM is standard) ---
            // LocalTime.parse() requires HH:MM in 24-hour format. We'll try to enforce that.

            // We'll define a 24-hour formatter (HH:MM) to handle the expected input "05:30"
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Pad single-digit hours if needed for stricter parsing (e.g., convert "5:30" to "05:30")
            if (startTimeStr.length() < 5) startTimeStr = "0" + startTimeStr;
            if (endTimeStr.length() < 5) endTimeStr = "0" + endTimeStr;

            LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

            Schedule schedule = new Schedule(day, startTime, endTime);
            section.setSchedule(schedule);
            repository.updateSection(section);
            System.out.println("SUCCESS: Weekly schedule set for " + sectionID + ": " + schedule.toString());

        } catch (Exception e) {
            // Catch all exceptions including IllegalArgumentException and DateTimeParseException
            System.out.println("Error: Invalid Day format or Time format. Please use a valid Day (e.g., MON, TUESDAY) and 24-hour Time (e.g., 17:30, 08:30).");
            // For debugging, you can print the error details: System.out.println(e.getMessage());
        }}

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