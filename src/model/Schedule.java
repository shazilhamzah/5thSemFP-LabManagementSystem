package model;

import java.io.Serializable;
import java.time.DayOfWeek;

// A class to represent a section's weekly schedule.
// For simplicity, it only stores the day and time, but would be more complex in a real system.
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private DayOfWeek day;
    private String timeSlot; // e.g., "10:00 - 11:30"

    public Schedule(DayOfWeek day, String timeSlot) {
        this.day = day;
        this.timeSlot = timeSlot;
    }

    // Getters
    public DayOfWeek getDay() {
        return day;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}