package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LabSection implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sectionID;
    private String courseID;
    private String instructorID;
    private String roomID;
    private List<String> taIDs;
    private Schedule schedule;
    private List<Session> sessions;

    public LabSection(String sectionID, String courseID, String roomID) {
        this.sectionID = sectionID;
        this.courseID = courseID;
        this.roomID = roomID;
        this.taIDs = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    // Mutators/Setters
    public void setInstructor(String instructorID) {
        this.instructorID = instructorID;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void addTA(String taID) {
        if (!taIDs.contains(taID)) {
            taIDs.add(taID);
        }
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    // Getters
    public String getSectionID() {
        return sectionID;
    }

    public String getCourseID() {
        return courseID;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    // Other Methods
    public Session getSessionbyDate(LocalDate date) {
        return sessions.stream()
                .filter(s -> s.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    public String getInstructorID() {
        return instructorID;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}