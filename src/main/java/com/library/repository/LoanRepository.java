package com.library.repository;

import com.library.model.Loan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing loan data
 * @author Library Team
 * @version 1.0
 */
public class LoanRepository {
    private List<Loan> loans;
    private int loanCounter;
    private BookRepository bookRepository;

    // FIX: Add constructor that accepts BookRepository
    public LoanRepository(BookRepository bookRepository) {
        this.loans = new ArrayList<>();
        this.bookRepository = bookRepository;
        this.loanCounter = 1;
        initializeSampleLoans(); // Add sample loans for testing
    }

    // Default constructor for backward compatibility
    public LoanRepository() {
        this(new BookRepository());
    }

    /**
     * Adds sample loans for testing - including overdue ones
     */
    private void initializeSampleLoans() {
        // Create some sample overdue loans for our test users
        LocalDate pastDate = LocalDate.now().minusDays(35); // 7 days overdue
        LocalDate pastDate2 = LocalDate.now().minusDays(40); // 12 days overdue

        // U002 - Emma Johnson has an overdue loan for "The Great Gatsby"
        Loan overdueLoan1 = new Loan("L0001", "U002", "978-0743273565", pastDate, pastDate.plusDays(28));
        overdueLoan1.setOverdue(true); // Mark as overdue
        loans.add(overdueLoan1);

        // Mark the book as unavailable
        bookRepository.updateBookAvailability("978-0743273565", false);

        // U004 - Sarah Davis has an overdue loan for "To Kill a Mockingbird"
        Loan overdueLoan2 = new Loan("L0002", "U004", "978-0061120084", pastDate2, pastDate2.plusDays(28));
        overdueLoan2.setOverdue(true); // Mark as overdue
        loans.add(overdueLoan2);

        // Mark the book as unavailable
        bookRepository.updateBookAvailability("978-0061120084", false);

        loanCounter = 3; // Set counter to continue from L0003
    }

    public Loan createLoan(String userId, String bookIsbn, LocalDate borrowDate) {
        LocalDate dueDate = borrowDate.plusDays(28);
        String loanId = "L" + String.format("%04d", loanCounter++);

        Loan newLoan = new Loan(loanId, userId, bookIsbn, borrowDate, dueDate);
        loans.add(newLoan);

        // Mark the book as unavailable when loan is created
        bookRepository.updateBookAvailability(bookIsbn, false);

        return newLoan;
    }

    public List<Loan> findLoansByUser(String userId) {
        return loans.stream()
                .filter(loan -> loan.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Loan> findLoansByBook(String bookIsbn) {
        return loans.stream()
                .filter(loan -> loan.getBookIsbn().equals(bookIsbn))
                .collect(Collectors.toList());
    }

    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loans.stream()
                .filter(loan -> {
                    loan.checkOverdue(currentDate);
                    return loan.isOverdue() && loan.getReturnDate() == null;
                })
                .collect(Collectors.toList());
    }

    public boolean returnBook(String loanId, LocalDate returnDate) {
        Loan loan = findLoanById(loanId);
        if (loan != null && loan.getReturnDate() == null) {
            loan.setReturnDate(returnDate);
            loan.setOverdue(false);

            // Mark the book as available when returned
            bookRepository.updateBookAvailability(loan.getBookIsbn(), true);

            return true;
        }
        return false;
    }

    public Loan findLoanById(String loanId) {
        return loans.stream()
                .filter(loan -> loan.getLoanId().equals(loanId))
                .findFirst()
                .orElse(null);
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }
}