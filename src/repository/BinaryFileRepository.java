package repository;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Concrete implementation of IRepository using binary file serialization.
public class BinaryFileRepository implements IRepository {

    // Caches as per the diagram
    private Map<String, User> userCache = new HashMap<>();
    private Map<String, LabSection> sectionCache = new HashMap<>();
    private Map<String, Building> buildingCache = new HashMap<>();
    private Map<String, Room> roomCache = new HashMap<>();
    private Map<String, LabCourse> courseCache = new HashMap<>();

    // File paths
    private final String UserFile = "users.dat";
    private final String SectionFile = "sections.dat";
    private final String BuildingFile = "buildings.dat";
    private final String RoomFile = "rooms.dat";
    private final String CourseFile = "courses.dat";

    public BinaryFileRepository() {
        loadData();
        if (userCache.isEmpty()) {
            initializeDummyData();
            saveData();
        }
        userCache.forEach((k, v) ->
                System.out.println(k + " -> " + v.getName() + " (" + v.getRole() + ")")
        );
    }


    private void initializeDummyData() {
        if (userCache.isEmpty()) {

            // --- HOD ---
            userCache.put("H1", new User("H1", "Kashif Zafar", "pass123", Role.HOD));

            // --- Academic Officer ---
            userCache.put("AO1", new User("AO1", "Mr. Ahmed", "pass123", Role.AcademicOfficer));

            // --- Instructors ---
            userCache.put("I1", new User("I1", "Dr. Smith", "pass123", Role.Instructor));
            userCache.put("I2", new User("I2", "Dr. Sarah", "pass123", Role.Instructor));
            userCache.put("I3", new User("I3", "Dr. Ali", "pass123", Role.Instructor));

            // --- Attendants ---
            userCache.put("A1", new User("A1", "Attendant Aslam", "pass123", Role.Attendant));
            userCache.put("A2", new User("A2", "Attendant Bilal", "pass123", Role.Attendant));
            userCache.put("A3", new User("A3", "Attendant Hussain", "pass123", Role.Attendant));

            // --- Teaching Assistants ---
            userCache.put("T1", new User("T1", "TA Umar", "pass123", Role.TA));
            userCache.put("T2", new User("T2", "TA Aysha", "pass123", Role.TA));
            userCache.put("T3", new User("T3", "TA Hamza", "pass123", Role.TA));


            // --- Buildings ---
            buildingCache.put("B1", new Building("B1", "Main Lab Block", "A1"));
            buildingCache.put("B2", new Building("B2", "CS Department Labs", "A2"));
            buildingCache.put("B3", new Building("B3", "Engineering Lab Wing", "A3"));

            // --- Rooms ---
            roomCache.put("R1", new Room("R1", "G-01", "B1"));
            roomCache.put("R2", new Room("R2", "G-05", "B2"));
            roomCache.put("R3", new Room("R3", "F-12", "B3"));

            // --- Lab Courses ---
            courseCache.put("CS1", new LabCourse("CS1", "Intro to Programming Lab"));
            courseCache.put("CS2", new LabCourse("CS2", "Data Structures Lab"));
            courseCache.put("CS3", new LabCourse("CS3", "Operating Systems Lab"));

            // --- Sections ---
            LabSection s1 = new LabSection("S1", "CS1", "R1");
            s1.setInstructor("I1");
            sectionCache.put("S1", s1);

            LabSection s2 = new LabSection("S2", "CS2", "R2");
            s2.setInstructor("I2");
            sectionCache.put("S2", s2);

            LabSection s3 = new LabSection("S3", "CS3", "R3");
            s3.setInstructor("I3");
            sectionCache.put("S3", s3);
        }
    }


    // Private helper method for serialization (to keep load/save methods clean)
    private <T> void serializeMap(Map<String, T> map, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(map);
        } catch (IOException e) {
            System.err.println("Error saving data to " + fileName + ": " + e.getMessage());
        }
    }

    // Private helper method for deserialization
    private <T> Map<String, T> deserializeMap(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            // Unchecked cast is safe here due to file content expectation, suppressed for clean code
            @SuppressWarnings("unchecked")
            Map<String, T> map = (Map<String, T>) ois.readObject();
            return map;
        } catch (FileNotFoundException e) {
            // File not found is normal on first run. Return empty map.
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + fileName + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public void loadData() {
        System.out.println("Loading data from binary files...");
        // Deserialize all caches
        userCache = deserializeMap(UserFile);
        sectionCache = deserializeMap(SectionFile);
        buildingCache = deserializeMap(BuildingFile);
        roomCache = deserializeMap(RoomFile);
        courseCache = deserializeMap(CourseFile);

        if (userCache.isEmpty()) {
            initializeDummyData();
        }
    }

    @Override
    public void saveData() {
        System.out.println("Saving data to binary files...");
        // Serialize all caches
        serializeMap(userCache, UserFile);
        serializeMap(sectionCache, SectionFile);
        serializeMap(buildingCache, BuildingFile);
        serializeMap(roomCache, RoomFile);
        serializeMap(courseCache, CourseFile);
    }

    // --- Data Retrieval Methods ---

    @Override
    public User getUserByID(String userID) {
        return userCache.get(userID);
    }

    @Override
    public Building getBuildingByAttendant(String attendantID) {
        // Linear search required as Building is cached by buildingID, not attendantID
        return buildingCache.values().stream()
                .filter(b -> attendantID.equals(b.getAttendantID()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public LabSection getSectionByID(String sectionID) {
        return sectionCache.get(sectionID);
    }

    @Override
    public List<LabSection> getAllSections() {
        return new ArrayList<>(sectionCache.values());
    }

    @Override
    public void updateSection(LabSection section) {
        // In a Map, put/replace works for update. Assuming sectionID is the key.
        sectionCache.put(section.getSectionID(), section);
        saveData(); // Save changes immediately (can be batched in a real app)
    }

    @Override
    public void addSection(LabSection section) {
        sectionCache.put(section.getSectionID(), section);
        saveData(); // Save changes immediately
    }
}