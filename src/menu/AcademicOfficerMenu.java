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
            System.out.println("4. Set Weekly Schedule (Standard Lab Time)"); // NEW MENU ITEM
            System.out.println("5. Schedule Makeup Session");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Section ID, Course ID, Room ID (e.g., S003 CS103 R003): ");
                        String[] data1 = scanner.nextLine().split(" ");
                        if (data1.length == 3) adminService.createLabSection(data1[0], data1[1], data1[2]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 2:
                        System.out.print("Enter Section ID and Instructor ID (e.g., S001 I101): ");
                        String[] data2 = scanner.nextLine().split(" ");
                        if (data2.length == 2) adminService.assignInstructor(data2[0], data2[1]);
                        else System.out.println("Invalid input format.");
                        break;
                    case 3:
                        System.out.print("Enter Section ID and TA ID (e.g., S001 T202): ");
                        String[] data3 = scanner.nextLine().split(" ");
                        if (data3.length == 2) adminService.addTAtoSection(data3[0], data3[1]);
                        else System.out.println("Invalid input format.");
                        break;

                    case 4: // NEW IMPLEMENTATION
                        System.out.print("Enter Section ID, Day (MON, TUE...), Start Time (HH:MM), End Time (HH:MM): ");
                        String[] data4 = scanner.nextLine().split(" ");
                        if (data4.length == 4) adminService.setWeeklySchedule(data4[0], data4[1], data4[2], data4[3]);
                        else System.out.println("Invalid input format. Expected: SectionID Day StartTime EndTime");
                        break;

                    case 5: // EXISTING MAKEUP SESSION (now option 5)
                        System.out.print("Enter Section ID, Date (YYYY-MM-DD), Start Time, End Time: ");
                        String[] data5 = scanner.nextLine().split(" ");
                        if (data5.length == 4) adminService.scheduleMakeUp(data5[0], data5[1], data5[2], data5[3]);
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