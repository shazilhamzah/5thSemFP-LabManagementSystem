package menu;

import service.IReport;
import java.util.Scanner;

public class HeadOfDepMenu implements IMenu {
    private final IReport reportService;
    private final Scanner scanner = new Scanner(System.in);

    public HeadOfDepMenu(IReport reportService) {
        this.reportService = reportService;
    }

    @Override
    public void display() {
        int choice;
        do {
            System.out.println("\n--- Head of Department Menu ---");
            System.out.println("1. Generate Weekly Schedule Report");
            System.out.println("2. Generate Weekly Timesheet Report");
            System.out.println("3. Generate Semester Report");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println(reportService.generateWeeklyScheduleReport());
                        break;
                    case 2:
                        System.out.print("Enter Start Date (YYYY-MM-DD): ");
                        String startDate = scanner.nextLine();
                        System.out.println(reportService.generateWeeklyTimeSheetReport(startDate));
                        break;
                    case 3:
                        System.out.print("Enter Section ID for summary: ");
                        String sectionID = scanner.nextLine();
                        System.out.println(reportService.generateSemesterReport(sectionID));
                        break;
                    case 0:
                        return; // Exit the loop and return to LoginMenu
                    default:
                        System.out.println("Invalid choice. Try again.");
                }

                if (choice != 0) {
                    System.out.print("\nPress Enter to return to the HOD menu...");
                    scanner.nextLine();
                }
            } else {
                scanner.nextLine(); // Consume invalid input
                choice = -1;
            }

        } while (choice != 0);
    }
}