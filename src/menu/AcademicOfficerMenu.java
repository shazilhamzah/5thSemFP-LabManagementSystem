package menu;

import service.IAdmin;
import java.util.Scanner;

public class AcademicOfficerMenu implements IMenu {
    private final IAdmin adminService;
    private final Scanner scanner = new Scanner(System.in);

    public AcademicOfficerMenu(IAdmin adminService) {
        this.adminService = adminService;
    }

    @Override
    public void display() {
        int choice;
        do {
            System.out.println("\n--- Academic Officer Menu ---");
            System.out.println("1. Create Lab Section");
            System.out.println("2. Assign Instructor");
            System.out.println("3. Add TA to Section");
            System.out.println("4. Schedule Makeup Session");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Section ID, Course ID, Room ID (e.g., S003 CS103 R003): ");
                        String[] data = scanner.nextLine().split(" ");
                        if (data.length == 3) adminService.createLabSection(data[0], data[1], data[2]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 2:
                        System.out.print("Enter Section ID and Instructor ID (e.g., S001 I101): ");
                        data = scanner.nextLine().split(" ");
                        if (data.length == 2) adminService.assignInstructor(data[0], data[1]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 3:
                        System.out.print("Enter Section ID and TA ID (e.g., S001 T202): ");
                        data = scanner.nextLine().split(" ");
                        if (data.length == 2) adminService.addTAtoSection(data[0], data[1]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 4:
                        System.out.print("Enter Section ID, Date (YYYY-MM-DD), Start Time, End Time: ");
                        data = scanner.nextLine().split(" ");
                        if (data.length == 4) adminService.scheduleMakeUp(data[0], data[1], data[2], data[3]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 0:
                        return; // Exit the loop and return to LoginMenu
                    default:
                        System.out.println("Invalid choice. Try again.");
                }

                if (choice != 0) {
                    System.out.print("\nPress Enter to return to the AO menu...");
                    scanner.nextLine();
                }
            } else {
                scanner.nextLine(); // Consume invalid input
                choice = -1;
            }

        } while (choice != 0);
    }
}