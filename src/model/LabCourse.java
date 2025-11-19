package model;

import java.io.Serializable;

public class LabCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseID;
    private String title;

    public LabCourse(String courseID, String title) {
        this.courseID = courseID;
        this.title = title;
    }

    // Getters
    public String getCourseID() {
        return courseID;
    }
}