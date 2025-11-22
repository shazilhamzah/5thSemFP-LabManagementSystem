package repository;

import model.*;
import java.util.List;

// The interface for data persistence operations.
public interface IRepository {
    void loadData();
    void saveData();

    // User & Building lookup
    User getUserByID(String userID);
    Building getBuildingByAttendant(String attendantID);

    // Section operations
    LabSection getSectionByID(String sectionID);
    List<LabSection> getAllSections();
    void updateSection(LabSection section);
    void addSection(LabSection section);

    Room getRoomByID(String roomID);
    LabCourse getCourseByID(String courseID);
}