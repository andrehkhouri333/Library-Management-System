package com.library;

import com.library.service.LibraryService;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main class for the Library Management System with Design Patterns
 * @author Library Team
 * @version 2.0
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static LibraryService libraryService = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("=== Library Management System with Design Patterns ===");
        logger.info("✅ Strategy Pattern: Fine calculation for different media types");
        logger.info("✅ Observer Pattern: Notification system for events");
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            logger.info("\n=== MAIN MENU ===");
            logger.info("1. View All Media");
            logger.info("2. View Books Only");
            logger.info("3. View CDs Only");
            logger.info("4. Search Media");
            logger.info("5. Borrow Media");
            logger.info("6. Return Media");
            logger.info("7. Pay Fine");
            logger.info("8. View My Loans");
            logger.info("9. View Mixed Media Overdue Report");
            logger.info("10. Demo Design Patterns");
            logger.info("11. Admin Login");
            logger.info("12. Exit");
            logger.info("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    libraryService.displayAllMedia();
                    break;
                case 2:
                    libraryService.displayAllBooks();
                    break;
                case 3:
                    libraryService.displayAllCDs();
                    break;
                case 4:
                    libraryService.searchMedia();
                    break;
                case 5:
                    libraryService.borrowMedia();
                    break;
                case 6:
                    libraryService.returnBook();
                    break;
                case 7:
                    libraryService.payFine();
                    break;
                case 8:
                    libraryService.displayUserLoans();
                    break;
                case 9:
                    libraryService.displayMixedMediaOverdueReport();
                    break;
                case 10:
                    demoDesignPatterns();
                    break;
                case 11:
                    adminLogin();
                    break;
                case 12:
                    logger.info("Thank you for using Library Management System. Goodbye!");
                    return;
                default:
                    logger.warning("Invalid option. Please try again.");
            }
        }
    }

    private static void demoDesignPatterns() {
        logger.info("\n=== DESIGN PATTERNS DEMO ===");
        logger.info("1. Strategy Pattern Demo - Fine Calculation");
        logger.info("2. Observer Pattern Demo - Notifications");
        logger.info("3. Back to Main Menu");
        logger.info("Choose demo: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                demoStrategyPattern();
                break;
            case 2:
                demoObserverPattern();
                break;
            case 3:
                return;
            default:
                logger.warning("Invalid choice.");
        }
    }

    private static void demoStrategyPattern() {
        logger.info("\n🎯 STRATEGY PATTERN DEMO - Fine Calculation");
        logger.info("Different media types have different fine calculation strategies:");

        var fineContext = libraryService.getFineService().getFineContext();
        String[] mediaTypes = fineContext.getRegisteredMediaTypes();

        for (String mediaType : mediaTypes) {
            double fine = fineContext.calculateFine(mediaType, 5);
            logger.info(String.format("  • %s: $%.2f flat fine", mediaType, fine));
        }

        logger.info("\n✨ Benefits of Strategy Pattern:");
        logger.info("  • Easy to add new media types (e.g., DVDs, e-books)");
        logger.info("  • Fine calculation logic is encapsulated in strategies");
        logger.info("  • Can switch strategies at runtime");
        logger.info("  • Follows Open/Closed Principle");
    }

    private static void demoObserverPattern() {
        logger.info("\n👁 OBSERVER PATTERN DEMO - Notifications");
        logger.info("Multiple notification channels can be attached:");
        logger.info("  ✓ Email Notifications");
        logger.info("  ✓ Console Logging");
        logger.info("  ✓ File Logging");
        logger.info("  ✓ (Future: SMS, Push Notifications)");

        logger.info("\n✨ Benefits of Observer Pattern:");
        logger.info("  • Loose coupling between subject and observers");
        logger.info("  • Easy to add new notification channels");
        logger.info("  • Observers can be attached/detached at runtime");
        logger.info("  • Follows Single Responsibility Principle");
    }

    private static void showAdminMenu() {
        while (libraryService.getAuthService().isLoggedIn()) {
            logger.info("\n=== ADMIN MENU ===");
            logger.info("1. View All Media");
            logger.info("2. View All Users");
            logger.info("3. Add New Media");
            logger.info("4. Search Media");
            logger.info("5. View Overdue Items");
            logger.info("6. Send Overdue Reminders");
            logger.info("7. User Management");
            logger.info("8. Logout");
            logger.info("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    libraryService.displayAllMedia();
                    break;
                case 2:
                    libraryService.displayAllUsers();
                    break;
                case 3:
                    libraryService.addNewMedia();
                    break;
                case 4:
                    libraryService.searchMedia();
                    break;
                case 5:
                    libraryService.displayOverdueBooks();
                    break;
                case 6:
                    libraryService.sendOverdueReminders();
                    break;
                case 7:
                    libraryService.manageUsers();
                    break;
                case 8:
                    libraryService.getAuthService().logout();
                    logger.info("Logged out successfully.");
                    break;
                default:
                    logger.warning("Invalid option. Please try again.");
            }
        }
    }

    private static void adminLogin() {
        logger.info("\n=== ADMIN LOGIN ===");
        logger.info("Username: ");
        String username = scanner.nextLine();
        logger.info("Password: ");
        String password = scanner.nextLine();

        if (libraryService.getAuthService().login(username, password)) {
            logger.info("Login successful! Welcome, Admin.");
            showAdminMenu();
        } else {
            logger.warning("Login failed! Invalid credentials.");
        }
    }

    public static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
