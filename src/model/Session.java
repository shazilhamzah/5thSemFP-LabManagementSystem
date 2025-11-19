package model;

import java.io.Serializable;
import java.time.LocalDate;

// Represents a single class session for a lab section.
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private ClassStatus status;

    public Session(LocalDate date) {
        this.date = date;
        this.status = ClassStatus.Scheduled; // Default status
    }

    // Setters/Mutators
    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public ClassStatus getStatus() {
        return status;
    }
}