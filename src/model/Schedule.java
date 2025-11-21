package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

// A class to represent a section's weekly schedule.
// For simplicity, it only stores the day and time, but would be more complex in a real system.
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private DayOfWeek day;
    private LocalTime expectedStartTime;
    private LocalTime expectedEndTime;

    public Schedule(DayOfWeek day, LocalTime expectedStartTime, LocalTime expectedEndTime) {
        this.day = day;
        this.expectedStartTime = expectedStartTime;
        this.expectedEndTime = expectedEndTime;
    }

    // Getters
    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getExpectedStartTime() {
        return expectedStartTime;
    }

    public LocalTime getExpectedEndTime() {
        return expectedEndTime;
    }

    @Override
    public String toString() {
        return String.format("%s from %s to %s", day, expectedStartTime, expectedEndTime);
    }
}