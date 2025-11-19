import auth.AuthService;
import auth.IAuth;
import menu.LoginMenu;
import repository.BinaryFileRepository;
import repository.IRepository;
import service.*; // Import all service interfaces/classes

public class Main {

    public static void main(String[] args) {
        System.out.println("--- System Initialization Started ---");

        // 1. Initialize Repository (The database/persistence layer)
        IRepository repository = new BinaryFileRepository();

        // The repository loads data upon creation/initialization

        // 2. Initialize Services (The business logic layer)
        IAuth authService = new AuthService(repository);
        IAdmin adminService = new AdminService(repository);
        IReport reportService = new ReportService(repository);
        ITimeSheet timeSheet = new TimeSheet(repository);

        System.out.println("--- Services Initialized ---");

        // 3. Start the main application menu (The presentation layer)
        LoginMenu loginMenu = new LoginMenu(authService, adminService, reportService, timeSheet);

        loginMenu.display();

        // 4. Save data upon application exit
        repository.saveData();
        System.out.println("--- Application Closed. Data Saved. ---");
    }
}