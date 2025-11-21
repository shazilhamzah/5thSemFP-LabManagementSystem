package menu;

import service.ITimeSheet;
import java.util.Scanner;

public class AttendantMenu implements IMenu {
    private final ITimeSheet timeSheet;
    private final String attendantID;
    private final Scanner scanner = new Scanner(System.in);

    public AttendantMenu(ITimeSheet timeSheet, String attendantID) {
        this.timeSheet = timeSheet;
        this.attendantID = attendantID;
    }

    @Override
    public void display() {
        int choice;
        do {
            System.out.println("\n--- Attendant Menu (ID: " + attendantID + ") ---");
            System.out.println("1. Fill Time Sheet for a Lab Session");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice == 1) {
                    System.out.print("Enter Section ID, Date (YYYY-MM-DD), Start Time, End Time: ");
                    String[] data = scanner.nextLine().split(" ");
                    if (data.length == 4) {
                        timeSheet.fillTimeSheet(attendantID, data[0], data[1], data[2], data[3]);
                    } else {
                        System.out.println("Invalid input format.");
                    }
                } else if (choice == 0) {
                    return; // Exit the loop and return to LoginMenu
                } else {
                    System.out.println("Invalid choice. Try again.");
                }

                if (choice != 0) {
                    System.out.print("\nPress Enter to return to the Attendant menu...");
                    scanner.nextLine();
                }
            } else {
                scanner.nextLine();
                choice = -1;
            }

        } while (choice != 0);
    }
}