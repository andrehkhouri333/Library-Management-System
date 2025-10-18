package com.library.service;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling loan-related operations
 * @author Library Team
 * @version 1.0
 */
public class LoanService {
    private LoanRepository loanRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private FineService fineService;

    // FIX: Correct constructor order and parameters
    public LoanService(FineService fineService, UserRepository userRepository, BookRepository bookRepository) {
        this.bookRepository = bookRepository; // Use the shared BookRepository instance
        this.userRepository = userRepository; // Use the shared UserRepository instance
        this.fineService = fineService; // Use the shared FineService instance
        this.loanRepository = new LoanRepository(bookRepository); // Pass shared BookRepository to LoanRepository
    }

    // Keep other constructors for backward compatibility
    public LoanService(FineService fineService, UserRepository userRepository) {
        this(fineService, userRepository, new BookRepository());
    }

    public LoanService(FineService fineService) {
        this(fineService, new UserRepository());
    }

    public LoanService() {
        this(new FineService(), new UserRepository());
    }

    public Loan borrowBook(String userId, String bookIsbn, LocalDate borrowDate) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            System.out.println("Error: User not found.");
            return null;
        }

        // Check if user can borrow (no unpaid fines) - FIXED: Use the shared FineService
        double unpaidFines = fineService.getTotalUnpaidAmount(userId);
        if (unpaidFines > 0) {
            System.out.println("Error: User cannot borrow books. Unpaid fines: $" + unpaidFines);
            return null;
        }

        // FIX: Double-check the user's canBorrow flag and update if needed
        if (!user.canBorrow() && unpaidFines == 0) {
            // If fines are paid but user flag is still false, update it
            user.setCanBorrow(true);
            userRepository.updateUser(user);
            System.out.println("User borrowing status updated. User can now borrow books.");
        }

        // Final check - if user still cannot borrow, show error
        if (!user.canBorrow()) {
            System.out.println("Error: User cannot borrow books due to account restrictions.");
            return null;
        }

        Book book = bookRepository.searchBooks(bookIsbn).stream()
                .findFirst()
                .orElse(null);

        if (book == null) {
            System.out.println("Error: Book not found.");
            return null;
        }

        if (!book.isAvailable()) {
            System.out.println("Error: Book is already borrowed.");
            return null;
        }

        Loan loan = loanRepository.createLoan(userId, bookIsbn, borrowDate);
        if (loan != null) {
            // Book availability is already updated in LoanRepository.createLoan()
            user.addLoan(loan.getLoanId());
            userRepository.updateUser(user);
            System.out.println("Book borrowed successfully. Due date: " + loan.getDueDate());
        }

        return loan;
    }

    public boolean returnBook(String loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findLoanById(loanId);
        if (loan == null) {
            System.out.println("Error: Loan not found.");
            return false;
        }

        if (loan.getReturnDate() != null) {
            System.out.println("Error: Book already returned.");
            return false;
        }

        // FIX: Check if user has unpaid fines before allowing return
        String userId = loan.getUserId();
        double unpaidFines = fineService.getTotalUnpaidAmount(userId);
        if (unpaidFines > 0) {
            System.out.println("❌ Error: User has unpaid fines of $" + unpaidFines);
            System.out.println("Please pay all fines before returning books.");
            // FIX: Remove the confusing "Check Loan ID" message
            return false;
        }

        boolean returnSuccess = loanRepository.returnBook(loanId, returnDate);
        if (returnSuccess) {
            // Book availability is already updated in LoanRepository.returnBook()

            // Remove loan from user
            User user = userRepository.findUserById(loan.getUserId());
            if (user != null) {
                user.removeLoan(loanId);
                userRepository.updateUser(user);
            }

            // FIX: REMOVED overdue fine application
            // Users only get fines for overdue books if they try to return without paying existing fines
            // Once they pay existing fines, they can return overdue books without additional fines

            System.out.println("✅ Book returned successfully!");
        }

        return returnSuccess;
    }

    /**
     * Gets all active loans for a user (including overdue ones)
     * @param userId the user ID
     * @return list of active loans with updated overdue status
     */
    public List<Loan> getUserActiveLoans(String userId) {
        List<Loan> userLoans = loanRepository.findLoansByUser(userId).stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(java.util.stream.Collectors.toList());

        // Update overdue status for all active loans
        LocalDate currentDate = LocalDate.now();
        for (Loan loan : userLoans) {
            loan.checkOverdue(currentDate);
        }

        return userLoans;
    }

    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loanRepository.getOverdueLoans(currentDate);
    }

    public void checkAllOverdueLoans(LocalDate currentDate) {
        List<Loan> overdueLoans = loanRepository.getOverdueLoans(currentDate);
        for (Loan loan : overdueLoans) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), currentDate);
            System.out.println("Overdue loan detected: " + loan.getLoanId() + " - " + overdueDays + " days overdue");
        }
    }

    public LoanRepository getLoanRepository() { return loanRepository; }
    public UserRepository getUserRepository() { return userRepository; }
    public FineService getFineService() { return fineService; }
    public BookRepository getBookRepository() { return bookRepository; }
}