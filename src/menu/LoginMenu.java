package menu;

import auth.IAuth;
import model.Role;
import model.User;
import service.IAdmin;
import service.IReport;
import service.ITimeSheet;
import java.util.Scanner;

public class LoginMenu implements IMenu {

    private final IAuth authService;
    private final IAdmin adminService;
    private final IReport reportService;
    private final ITimeSheet timeSheet;
    private final Scanner scanner = new Scanner(System.in);

    public LoginMenu(IAuth authService, IAdmin adminService, IReport reportService, ITimeSheet timeSheet) {
        this.authService = authService;
        this.adminService = adminService;
        this.reportService = reportService;
        this.timeSheet = timeSheet;
    }

    @Override
    public void display() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Login ---");
            System.out.print("Enter username: ");
            String userID = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User loggedInUser = authService.login(userID, password);

            if (loggedInUser != null) {
                System.out.println("\nWelcome to Lab Management System");
                launchUserMenu(loggedInUser);
            }
        }
    }

    private void launchUserMenu(User user) {
        Role role = user.getRole();
        IMenu userMenu = null;

        // Map user role to the specific menu
        switch (role) {
            case AcademicOfficer:
                userMenu = new AcademicOfficerMenu(adminService);
                break;
            case Attendant:
                userMenu = new AttendantMenu(timeSheet, user.getUserID());
                break;
            case HOD:
                userMenu = new HeadOfDepMenu(reportService);
                break;
            case Instructor:
            case TA:
                // Placeholder for roles without current functionality
                System.out.println("Menu for " + role + " (" + user.getUserID() + ") is pending.");
                return;
            default:
                System.out.println("Unknown role. Logging out.");
                return;
        }

        if (userMenu != null) {
            userMenu.display(); // This runs the submenu loop until logout (choice 0)
            System.out.println("\nLogging out from " + role + "...");
        }
    }
}