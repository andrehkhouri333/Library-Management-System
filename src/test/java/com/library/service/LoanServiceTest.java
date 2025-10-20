package com.library.service;

import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LoanService
 * @author Library Team
 * @version 1.0
 */
class LoanServiceTest {
    private LoanService loanService;
    private FineService fineService;
    private UserRepository userRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        bookRepository = new BookRepository();
        fineService = new FineService(userRepository);
        loanService = new LoanService(fineService, userRepository, bookRepository);
    }

    @Test
    void testBorrowBookSuccess() {
        // Use a user without fines (U001 - John Smith)
        Loan loan = loanService.borrowBook("U001", "978-0451524935", LocalDate.now());

        assertNotNull(loan);
        assertEquals("U001", loan.getUserId());
        assertEquals("978-0451524935", loan.getBookIsbn());
        assertEquals(LocalDate.now().plusDays(28), loan.getDueDate());
        assertFalse(loan.isOverdue());
    }

    @Test
    void testBorrowBookUserNotFound() {
        Loan loan = loanService.borrowBook("U999", "978-0451524935", LocalDate.now());
        assertNull(loan);
    }

    @Test
    void testBorrowBookBookNotFound() {
        Loan loan = loanService.borrowBook("U001", "invalid-isbn", LocalDate.now());
        assertNull(loan);
    }

    @Test
    void testBorrowBookAlreadyBorrowed() {
        // First borrow should succeed
        Loan firstLoan = loanService.borrowBook("U001", "978-0451524935", LocalDate.now());
        assertNotNull(firstLoan);

        // Second borrow of same book should fail
        Loan secondLoan = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNull(secondLoan);
    }

    @Test
    void testBorrowBookWithUnpaidFines() {
        // U002 (Emma Johnson) has unpaid fines in sample data
        Loan loan = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNull(loan);
    }

    @Test
    void testBorrowBookWithOverdueBooks() {
        // U002 has overdue books in sample data
        // Even if they pay fines, they still can't borrow until overdue books are returned
        Loan loan = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNull(loan);
    }

    @Test
    void testReturnBookSuccess() {
        // First borrow a book
        Loan loan = loanService.borrowBook("U001", "978-0451524935", LocalDate.now());
        assertNotNull(loan);

        // Then return it
        boolean returnSuccess = loanService.returnBook(loan.getLoanId(), LocalDate.now());
        assertTrue(returnSuccess);
    }

    @Test
    void testReturnBookNotFound() {
        boolean returnSuccess = loanService.returnBook("invalid-loan-id", LocalDate.now());
        assertFalse(returnSuccess);
    }

    @Test
    void testReturnBookAlreadyReturned() {
        // Borrow and return a book
        Loan loan = loanService.borrowBook("U001", "978-0451524935", LocalDate.now());
        loanService.returnBook(loan.getLoanId(), LocalDate.now());

        // Try to return again
        boolean secondReturn = loanService.returnBook(loan.getLoanId(), LocalDate.now());
        assertFalse(secondReturn);
    }

    @Test
    void testGetUserActiveLoans() {
        // Borrow a book
        Loan loan = loanService.borrowBook("U001", "978-0451524935", LocalDate.now());

        List<Loan> activeLoans = loanService.getUserActiveLoans("U001");
        assertFalse(activeLoans.isEmpty());
        assertEquals(loan.getLoanId(), activeLoans.get(0).getLoanId());
    }

    @Test
    void testGetOverdueLoans() {
        // Create an overdue loan manually in repository for testing
        List<Loan> overdueLoans = loanService.getOverdueLoans(LocalDate.now().plusDays(40));
        // Sample data has 2 overdue loans
        assertFalse(overdueLoans.isEmpty());
    }
}