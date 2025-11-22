package service;

import model.LabSection;
import model.Schedule;
import model.Session;
import model.User; // NEW IMPORT
import model.Role; // NEW IMPORT
import repository.IRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Pattern; // NEW IMPORT

public class AdminService implements IAdmin {
    private final IRepository repository;

    // Pattern requires 'S' followed by one or more digits only
    private static final Pattern SECTION_ID_PATTERN = Pattern.compile("^S\\d+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Use constant
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); // Use constant

    public AdminService(IRepository repository) {
        this.repository = repository;
    }

    // --- 1. Create Lab Section (Section ID Validation) ---
    @Override
    public void createLabSection(String sectionID, String courseID, String roomID) {

        // 1. Validation Check: Section ID format
        if (!SECTION_ID_PATTERN.matcher(sectionID).matches()) {
            System.out.println("Error: Invalid Section ID format. Must be 'S' followed by only numerals (e.g., S101).");
            return;
        }

        // 2. Validation Check: Section ID uniqueness
        if (repository.getSectionByID(sectionID) != null) {
            System.out.println("Error: Section " + sectionID + " already exists.");
            return;
        }

        // 3. NEW Validation Check: Course existence
        if (repository.getCourseByID(courseID) == null) {
            System.out.println("Error: Course ID " + courseID + " does not exist in the system. Cannot create section.");
            return;
        }

        // 4. Existing Validation Check: Room existence
        if (repository.getRoomByID(roomID) == null) {
            System.out.println("Error: Room ID " + roomID + " does not exist in the system. Cannot create section.");
            return;
        }

        // If all checks pass, create the section
        LabSection newSection = new LabSection(sectionID, courseID, roomID);
        repository.addSection(newSection);
        System.out.println("SUCCESS: Lab section " + sectionID + " created for Course " + courseID + " in Room " + roomID + ".");
    }

    // --- 2. Assign Instructor (Existing User and Role Check) ---
    @Override
    public void assignInstructor(String sectionID, String instructorID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        // Check 1: Does the User exist?
        User instructor = repository.getUserByID(instructorID);
        if (instructor == null) {
            System.out.println("Error: Instructor ID " + instructorID + " does not exist in the system.");
            return;
        }

        // Check 2: Does the User have the correct Role?
        if (instructor.getRole() != Role.Instructor) {
            System.out.println("Error: User " + instructorID + " is not an Instructor. Role is " + instructor.getRole() + ".");
            return;
        }

        section.setInstructor(instructorID);
        repository.updateSection(section);
        System.out.println("SUCCESS: Instructor " + instructorID + " assigned to " + sectionID + ".");
    }

    // --- 3. Add TA to Section (Existing User and Role Check) ---
    @Override
    public void addTAtoSection(String sectionID, String taID) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        // Check 1: Does the User exist?
        User ta = repository.getUserByID(taID);
        if (ta == null) {
            System.out.println("Error: TA ID " + taID + " does not exist in the system.");
            return;
        }

        // Check 2: Does the User have the correct Role?
        if (ta.getRole() != Role.TA) {
            System.out.println("Error: User " + taID + " is not a TA. Role is " + ta.getRole() + ".");
            return;
        }

        section.addTA(taID);
        repository.updateSection(section);
        System.out.println("SUCCESS: TA " + taID + " added to " + sectionID + ".");
    }

    // --- 4. Schedule Makeup (Fixing Section ID Lookup) ---
    @Override
    public void scheduleMakeUp(String sectionID, String dateStr, String startTimeStr, String endTimeStr) {
        // FIX: The original method failed if getSectionByID returned null,
        // leading to a NullPointerException later or simply not finding the section.
        LabSection section = repository.getSectionByID(sectionID);

        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);

            // Optional: Validate time formats, though we use them as strings for now
            // LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
            // LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);

            if (section.getSessionbyDate(date) != null) {
                System.out.println("Error: Session already scheduled on " + dateStr);
                return;
            }

            Session makeupSession = new Session(date);
            section.addSession(makeupSession);
            repository.updateSection(section);
            System.out.println("SUCCESS: Makeup session scheduled for " + sectionID +
                    " on " + dateStr + " at " + startTimeStr + ".");
        } catch (Exception e) {
            System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
            // Optional: Print stack trace for debugging if necessary
        }
    }

    // --- Existing setWeeklySchedule method (ensure this remains in place) ---
    @Override
    public void setWeeklySchedule(String sectionID, String dayStr, String startTimeStr, String endTimeStr) {
        LabSection section = repository.getSectionByID(sectionID);
        if (section == null) {
            System.out.println("Error: Section " + sectionID + " not found.");
            return;
        }

        try {
            DayOfWeek day = null;
            String upperDayStr = dayStr.toUpperCase(Locale.ROOT);

            if (upperDayStr.length() == 3) {
                for (DayOfWeek d : DayOfWeek.values()) {
                    if (d.getDisplayName(TextStyle.SHORT, Locale.ROOT).toUpperCase(Locale.ROOT).equals(upperDayStr)) {
                        day = d;
                        break;
                    }
                }
            } else {
                day = DayOfWeek.valueOf(upperDayStr);
            }

            if (day == null) {
                throw new IllegalArgumentException("Invalid day abbreviation: " + dayStr);
            }

            // Time parsing logic (ensuring HH:MM format)
            if (startTimeStr.length() < 5) startTimeStr = "0" + startTimeStr;
            if (endTimeStr.length() < 5) endTimeStr = "0" + endTimeStr;

            LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
            LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);

            Schedule schedule = new Schedule(day, startTime, endTime);
            section.setSchedule(schedule);
            repository.updateSection(section);
            System.out.println("SUCCESS: Weekly schedule set for " + sectionID + ": " + schedule.toString());

        } catch (Exception e) {
            System.out.println("Error: Invalid Day format or Time format. Please use a valid Day (e.g., MON, TUESDAY) and 24-hour Time (e.g., 17:30, 08:30).");
        }
    }
}