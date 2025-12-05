package com.library;

import com.library.service.LibraryService;
import java.util.Scanner;

/**
 * Main class for the Library Management System
 * @author Library Team
 * @version 1.0
 */
public class Main {
    private static LibraryService libraryService = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Library Management System ===");
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. View All Media");
            System.out.println("2. View Books Only");
            System.out.println("3. View CDs Only");
            System.out.println("4. Search Media");
            System.out.println("5. Borrow Media");
            System.out.println("6. Return Media");
            System.out.println("7. Pay Fine");
            System.out.println("8. View My Loans");
            System.out.println("9. View Mixed Media Overdue Report"); // NEW OPTION
            System.out.println("10. Admin Login");
            System.out.println("11. Exit"); // Changed from 10 to 11
            System.out.print("Choose an option: ");

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
                case 9: // NEW: Mixed media overdue report
                    libraryService.displayMixedMediaOverdueReport();
                    break;
                case 10:
                    adminLogin();
                    break;
                case 11:
                    System.out.println("Thank you for using Library Management System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showAdminMenu() {
        while (libraryService.getAuthService().isLoggedIn()) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Media");
            System.out.println("2. View All Users");
            System.out.println("3. Add New Media");
            System.out.println("4. Search Media");
            System.out.println("5. View Overdue Items");
            System.out.println("6. Send Overdue Reminders");
            System.out.println("7. User Management");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

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
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void adminLogin() {
        System.out.println("\n=== ADMIN LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (libraryService.getAuthService().login(username, password)) {
            System.out.println("Login successful! Welcome, Admin.");
            showAdminMenu();
        } else {
            System.out.println("Login failed! Invalid credentials.");
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