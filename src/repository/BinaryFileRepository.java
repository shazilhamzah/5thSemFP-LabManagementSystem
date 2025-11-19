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
        // Initialize with dummy data for testing if files don't exist
        initializeDummyData();
        loadData();
    }

    private void initializeDummyData() {
        // Only initialize if caches are empty (assuming file load failed or is first run)
        if (userCache.isEmpty()) {
            userCache.put("I101", new User("I101", "Dr. Smith", "pass123", Role.Instructor));
            buildingCache.put("B01", new Building("B01", "Main Lab Block", "A202"));
            roomCache.put("R001", new Room("R001", "G-01", "B01"));
            courseCache.put("CS101", new LabCourse("CS101", "Intro to Programming Lab"));
            LabSection section = new LabSection("S001", "CS101", "R001");
            section.setInstructor("I101");
            sectionCache.put("S001", section);
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