package menu;

import auth.IAuth;
import model.Role;
import model.User;
import service.IAdmin;
import service.IReport;
import service.ITimeSheet;
import java.util.Scanner;

public class LoginMenu implements IMenu {
    // Services are injected via the constructor (Dependency Injection)
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
        User loggedInUser = null;
        while (loggedInUser == null) {
            System.out.println("\n--- Lab Management System Login ---");
            System.out.print("Enter User ID (A001/A202/H001/I101): ");
            String userID = scanner.nextLine();
            System.out.print("Enter Password (pass123): ");
            String password = scanner.nextLine();

            loggedInUser = authService.login(userID, password);

            if (loggedInUser != null) {
                launchUserMenu(loggedInUser);
            }
        }
    }

    private void launchUserMenu(User user) {
        Role role = user.getRole();
        IMenu userMenu = null;

        switch (role) {
            case AcademicOfficer:
                userMenu = new AcademicOfficerMenu(adminService);
                break;
            case Attendant:
                // Pass attendantID for specific menu actions
                userMenu = new AttendantMenu(timeSheet, user.getUserID());
                break;
            case HOD:
                userMenu = new HeadOfDepMenu(reportService);
                break;
            case Instructor:
            case TA:
                // Simple placeholder menu for other roles
                System.out.println("Welcome, " + role + " " + user.getUserID() + ". Functionality pending.");
                return;
            default:
                System.out.println("Unknown role. Logging out.");
                return;
        }

        if (userMenu != null) {
            userMenu.display();
            System.out.println("\n--- Returning to Login Screen ---");
        }
    }
}