package com.library.service;

import com.library.model.*;
import com.library.repository.LoanRepository;
import com.library.repository.MediaRepository;
import com.library.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import static com.library.Main.getIntInput;

/**
 * Main service for library operations
 * @author Library Team
 * @version 1.0
 */
public class LibraryService {
    private AuthService authService;
    private MediaService mediaService;
    private UserRepository userRepository;
    private LoanService loanService;
    private FineService fineService;
    private ReminderService reminderService;
    private UserManagementService userManagementService;
    private Scanner scanner;

    /**
     * Display simple mixed media overdue report (US5.3)
     */
    public void displayMixedMediaOverdueReport() {
        System.out.println("\n=== MIXED MEDIA OVERDUE REPORT ===");

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        // Generate and display the simple report
        String report = loanService.getSimpleMixedMediaReport(userId, LocalDate.now());
        System.out.println(report);
    }

    public LibraryService() {
        this.authService = new AuthService();
        this.userRepository = new UserRepository();

        // Create shared MediaRepository first
        MediaRepository sharedMediaRepository = new MediaRepository();
        this.mediaService = new MediaService(sharedMediaRepository);

        // Create FineService first (without LoanService dependency)
        this.fineService = new FineService(userRepository);

        // Create LoanService with the FineService
        this.loanService = new LoanService(fineService, userRepository, sharedMediaRepository);

        // Set the LoanService dependency in FineService
        this.fineService.setLoanService(loanService);

        // Create UserManagementService
        LoanRepository loanRepository = new LoanRepository(sharedMediaRepository);
        this.userManagementService = new UserManagementService(userRepository, loanRepository,
                fineService.getFineRepository());

        EmailService emailService = new EmailService();
        this.reminderService = new ReminderService(emailService, loanRepository, userRepository, true);

        this.scanner = new Scanner(System.in);
    }

    // Add the missing payFine() method:
    public void payFine() {
        System.out.println("\n=== PAY FINE ===");

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        // First, check and apply any overdue fines
        loanService.checkAndApplyOverdueFines(userId, LocalDate.now());

        fineService.displayUserFines(userId);

        List<Fine> unpaidFines = fineService.getUserUnpaidFines(userId);
        if (unpaidFines.isEmpty()) {
            System.out.println("✅ No unpaid fines found.");
            return;
        }

        System.out.print("Enter Fine ID to pay: ");
        String fineId = scanner.nextLine().trim();

        System.out.print("Enter payment amount: ");
        try {
            double paymentAmount = Double.parseDouble(scanner.nextLine().trim());
            fineService.payFine(fineId, paymentAmount);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Invalid payment amount.");
        }
    }

    // Also add the missing displayUserLoans() method:
    public void displayUserLoans() {
        System.out.println("\n=== USER LOANS ===");

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        List<Loan> userLoans = loanService.getUserActiveLoans(userId);
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ACTIVE LOANS FOR USER: " + userId);
        System.out.println("=".repeat(100));

        if (userLoans.isEmpty()) {
            System.out.println("No active loans found.");
        } else {
            for (Loan loan : userLoans) {
                System.out.println(loan);
            }

            // Show summary
            long overdueCount = userLoans.stream().filter(Loan::isOverdue).count();
            if (overdueCount > 0) {
                System.out.println("\n⚠️ User has " + overdueCount + " overdue item(s) that must be returned:");
                System.out.println("1. Return all overdue items first");
                System.out.println("2. Then you can pay fines for returned items");
                System.out.println("3. Only after all fines are paid can you borrow new items");
            }
        }
        System.out.println("=".repeat(100));
    }

    // Add other missing methods that might be needed:

    /**
     * Display all users (admin only)
     */
    public void displayAllUsers() {
        if (!authService.isLoggedIn()) {
            System.out.println("Error: Admin login required to view users.");
            return;
        }

        List<User> users = userRepository.getAllUsers();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REGISTERED USERS (ALL)");
        System.out.println("=".repeat(80));

        if (users.isEmpty()) {
            System.out.println("No users registered in the system.");
        } else {
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i));
            }
        }
        System.out.println("=".repeat(80));
    }

    /**
     * Send overdue reminders
     */
    public void sendOverdueReminders() {
        if (!authService.isLoggedIn()) {
            System.out.println("Error: Admin login required to send reminders.");
            return;
        }

        System.out.println("\n=== SEND OVERDUE REMINDERS ===");
        System.out.println("1. Send reminder to specific user");
        System.out.println("2. Send reminders to all users with overdue items");
        System.out.println("3. Cancel");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                sendReminderToSpecificUser();
                break;
            case 2:
                reminderService.sendOverdueRemindersToAllUsers();
                System.out.println("Reminders sent to all users with overdue items.");
                break;
            case 3:
                System.out.println("Operation cancelled.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void sendReminderToSpecificUser() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        // Get user's overdue items count
        List<Loan> userActiveLoans = loanService.getUserActiveLoans(userId);
        long overdueCount = userActiveLoans.stream()
                .filter(Loan::isOverdue)
                .count();

        if (overdueCount == 0) {
            System.out.println("User " + userId + " has no overdue items.");
            return;
        }

        System.out.println("User " + userId + " has " + overdueCount + " overdue item(s).");
        System.out.print("Send reminder? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            reminderService.sendOverdueReminderToUser(userId, (int) overdueCount);
            System.out.println("Reminder sent successfully!");
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    /**
     * Manage users (admin only)
     */
    public void manageUsers() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Error: Admin login required to manage users.");
            return;
        }

        System.out.println("\n=== USER MANAGEMENT ===");
        System.out.println("1. Unregister user");
        System.out.println("2. View active users");
        System.out.println("3. View inactive users");
        System.out.println("4. Reactivate user");
        System.out.println("5. Back to main menu");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                unregisterUser();
                break;
            case 2:
                displayActiveUsers();
                break;
            case 3:
                displayInactiveUsers();
                break;
            case 4:
                reactivateUser();
                break;
            case 5:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void unregisterUser() {
        System.out.println("\n=== UNREGISTER USER ===");

        // Display active users first
        List<User> activeUsers = userManagementService.getActiveUsers();
        if (activeUsers.isEmpty()) {
            System.out.println("No active users found.");
            return;
        }

        System.out.println("\nActive Users:");
        System.out.println("=".repeat(80));
        for (int i = 0; i < activeUsers.size(); i++) {
            System.out.println((i + 1) + ". " + activeUsers.get(i));
        }
        System.out.println("=".repeat(80));

        System.out.print("Enter User ID to unregister: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to unregister user " + userId + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            UserManagementService.UnregistrationResult result =
                    userManagementService.unregisterUser(userId, authService);
            System.out.println(result.getMessage());
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    private void displayActiveUsers() {
        List<User> activeUsers = userManagementService.getActiveUsers();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ACTIVE USERS");
        System.out.println("=".repeat(80));

        if (activeUsers.isEmpty()) {
            System.out.println("No active users found.");
        } else {
            for (int i = 0; i < activeUsers.size(); i++) {
                System.out.println((i + 1) + ". " + activeUsers.get(i));
            }
        }
        System.out.println("=".repeat(80));
    }

    private void displayInactiveUsers() {
        List<User> inactiveUsers = userManagementService.getInactiveUsers();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("INACTIVE USERS");
        System.out.println("=".repeat(80));

        if (inactiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
        } else {
            for (int i = 0; i < inactiveUsers.size(); i++) {
                System.out.println((i + 1) + ". " + inactiveUsers.get(i));
            }
        }
        System.out.println("=".repeat(80));
    }

    private void reactivateUser() {
        System.out.println("\n=== REACTIVATE USER ===");

        List<User> inactiveUsers = userManagementService.getInactiveUsers();
        if (inactiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
            return;
        }

        System.out.println("\nInactive Users:");
        System.out.println("=".repeat(80));
        for (int i = 0; i < inactiveUsers.size(); i++) {
            System.out.println((i + 1) + ". " + inactiveUsers.get(i));
        }
        System.out.println("=".repeat(80));

        System.out.print("Enter User ID to reactivate: ");
        String userId = scanner.nextLine().trim();

        boolean success = userManagementService.reactivateUser(userId, authService);
        if (!success) {
            System.out.println("Failed to reactivate user.");
        }
    }

    /**
     * Borrow media (book or CD)
     */
    public void borrowMedia() {
        System.out.println("\n=== BORROW MEDIA ===");

        System.out.println("1. Borrow Book");
        System.out.println("2. Borrow CD");
        System.out.print("Choose media type: ");

        int mediaTypeChoice = getIntInput();
        if (mediaTypeChoice < 1 || mediaTypeChoice > 2) {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();

        User user = userRepository.findUserById(userId);
        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        // Check if user is active
        if (!user.isActive()) {
            System.out.println("❌ Error: User account is not active.");
            System.out.println("Please contact administrator to reactivate your account.");
            return;
        }

        if (mediaTypeChoice == 1) {
            // Borrow book
            System.out.print("Enter Book ISBN: ");
            String isbn = scanner.nextLine().trim();
            borrowBook(userId, isbn);
        } else {
            // Borrow CD
            System.out.print("Enter CD Catalog Number: ");
            String catalogNumber = scanner.nextLine().trim();
            borrowCD(userId, catalogNumber);
        }
    }

    /**
     * Borrow a book
     */
    private void borrowBook(String userId, String isbn) {
        Loan loan = loanService.borrowBook(userId, isbn, LocalDate.now());
        if (loan != null) {
            System.out.println("✅ Book borrowed successfully!");
            System.out.println("Due date: " + loan.getDueDate());
            System.out.println("Loan period: 28 days");
        }
    }

    /**
     * Borrow a CD
     */
    private void borrowCD(String userId, String catalogNumber) {
        Loan loan = loanService.borrowCD(userId, catalogNumber, LocalDate.now());
        if (loan != null) {
            System.out.println("✅ CD borrowed successfully!");
            System.out.println("Due date: " + loan.getDueDate());
            System.out.println("Loan period: 7 days");
        }
    }



    /**
     * Add new media (book or CD)
     */
    public void addNewMedia() {
        if (!authService.isLoggedIn()) {
            System.out.println("Error: Admin login required to add media.");
            return;
        }

        System.out.println("\n=== ADD NEW MEDIA ===");
        System.out.println("1. Add Book");
        System.out.println("2. Add CD");
        System.out.print("Choose media type: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                addNewBook();
                break;
            case 2:
                addNewCD();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Add a new book
     */
    private void addNewBook() {
        System.out.println("\n=== ADD NEW BOOK ===");
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("Error: All fields are required.");
            return;
        }

        boolean success = mediaService.addBook(title, author, isbn, authService);
        if (success) {
            System.out.println("✅ Book added successfully!");
        }
    }

    /**
     * Add a new CD
     */
    private void addNewCD() {
        System.out.println("\n=== ADD NEW CD ===");
        System.out.print("Enter CD title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter artist: ");
        String artist = scanner.nextLine().trim();

        System.out.print("Enter catalog number: ");
        String catalogNumber = scanner.nextLine().trim();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        System.out.print("Enter track count: ");
        try {
            int trackCount = Integer.parseInt(scanner.nextLine().trim());

            if (title.isEmpty() || artist.isEmpty() || catalogNumber.isEmpty() || genre.isEmpty()) {
                System.out.println("Error: All fields are required.");
                return;
            }

            boolean success = mediaService.addCD(title, artist, catalogNumber, genre, trackCount, authService);
            if (success) {
                System.out.println("✅ CD added successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Track count must be a number.");
        }
    }

    /**
     * Search media
     */
    public void searchMedia() {
        System.out.print("\nEnter search query (title, author, or ID): ");
        String query = scanner.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        List<Media> results = mediaService.searchMedia(query);

        System.out.println("\n" + "=".repeat(120));
        System.out.println("SEARCH RESULTS for: '" + query + "'");
        System.out.println("=".repeat(120));

        if (results.isEmpty()) {
            System.out.println("No media found matching your search.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
        System.out.println("=".repeat(120));
    }

    /**
     * Display all media
     */
    public void displayAllMedia() {
        mediaService.displayAllMedia();
    }

    /**
     * Display all books
     */
    public void displayAllBooks() {
        mediaService.displayAllBooks();
    }

    /**
     * Display all CDs
     */
    public void displayAllCDs() {
        mediaService.displayAllCDs();
    }

    /**
     * Return media
     */
    public void returnBook() {
        System.out.println("\n=== RETURN MEDIA ===");

        System.out.print("Enter Loan ID: ");
        String loanId = scanner.nextLine().trim();

        boolean success = loanService.returnBook(loanId, LocalDate.now());
        if (success) {
            System.out.println("✅ Media returned successfully!");
        }
    }

    /**
     * Display overdue items
     */
    public void displayOverdueBooks() {
        if (!authService.isLoggedIn()) {
            System.out.println("Error: Admin login required to view overdue items.");
            return;
        }

        List<Loan> overdueLoans = loanService.getOverdueLoans(LocalDate.now());
        System.out.println("\n" + "=".repeat(120));
        System.out.println("OVERDUE ITEMS (ALL USERS)");
        System.out.println("=".repeat(120));

        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue items found.");
        } else {
            double totalFines = 0;
            for (Loan loan : overdueLoans) {
                System.out.println(loan);
                totalFines += loan.calculateFine(LocalDate.now());
            }
            System.out.println("-".repeat(120));
            System.out.println(String.format("TOTAL OVERDUE FINES: $%.2f", totalFines));
        }
        System.out.println("=".repeat(120));
    }

    // Getters
    public AuthService getAuthService() { return authService; }
    public MediaService getMediaService() { return mediaService; }
    public UserRepository getUserRepository() { return userRepository; }
    public LoanService getLoanService() { return loanService; }
    public FineService getFineService() { return fineService; }
    public ReminderService getReminderService() { return reminderService; }
    public UserManagementService getUserManagementService() { return userManagementService; }
}