package com.library.repository;

import com.library.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LoanRepository
 * @author Library Team
 * @version 1.0
 */
class LoanRepositoryTest {
    private LoanRepository loanRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        loanRepository = new LoanRepository(bookRepository);
    }

    @Test
    void testCreateLoan() {
        LocalDate borrowDate = LocalDate.now();
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", borrowDate);

        assertNotNull(loan);
        assertEquals("U001", loan.getUserId());
        assertEquals("978-0451524935", loan.getBookIsbn());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(borrowDate.plusDays(28), loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertFalse(loan.isOverdue());
        assertTrue(loan.getLoanId().startsWith("L"));
    }

    @Test
    void testCreateLoanMarksBookUnavailable() {
        // Verify book is initially available
        assertTrue(bookRepository.findBookByIsbn("978-0451524935").isAvailable());

        // Create loan
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());
        assertNotNull(loan);

        // Book should now be unavailable
        assertFalse(bookRepository.findBookByIsbn("978-0451524935").isAvailable());
    }

    @Test
    void testFindLoansByUser() {
        // Create a loan for a user
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());

        List<Loan> userLoans = loanRepository.findLoansByUser("U001");
        assertFalse(userLoans.isEmpty());
        assertTrue(userLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
    }

    @Test
    void testFindLoansByBook() {
        // Create a loan for a book
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());

        List<Loan> bookLoans = loanRepository.findLoansByBook("978-0451524935");
        assertFalse(bookLoans.isEmpty());
        assertTrue(bookLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
    }

    @Test
    void testGetActiveLoans() {
        List<Loan> activeLoans = loanRepository.getActiveLoans();

        // Should include both sample overdue loans and any new active loans
        assertFalse(activeLoans.isEmpty());
        for (Loan loan : activeLoans) {
            assertNull(loan.getReturnDate());
        }
    }

    @Test
    void testGetOverdueLoans() {
        // Test with current date (sample data has overdue loans)
        List<Loan> overdueLoans = loanRepository.getOverdueLoans(LocalDate.now());

        // Sample data should have overdue loans
        assertFalse(overdueLoans.isEmpty());
        for (Loan loan : overdueLoans) {
            assertTrue(loan.isOverdue());
            assertNull(loan.getReturnDate());
        }
    }

    @Test
    void testReturnBookSuccess() {
        // Create a loan first
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());
        String loanId = loan.getLoanId();

        // Verify book is unavailable
        assertFalse(bookRepository.findBookByIsbn("978-0451524935").isAvailable());

        // Return the book
        boolean returnSuccess = loanRepository.returnBook(loanId, LocalDate.now());
        assertTrue(returnSuccess);

        // Verify book is available again
        assertTrue(bookRepository.findBookByIsbn("978-0451524935").isAvailable());

        // Verify loan has return date
        Loan returnedLoan = loanRepository.findLoanById(loanId);
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(LocalDate.now(), returnedLoan.getReturnDate());
    }

    @Test
    void testReturnBookNotFound() {
        boolean returnSuccess = loanRepository.returnBook("INVALID_LOAN_ID", LocalDate.now());
        assertFalse(returnSuccess);
    }

    @Test
    void testReturnBookAlreadyReturned() {
        // Create and return a loan
        Loan loan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());
        loanRepository.returnBook(loan.getLoanId(), LocalDate.now());

        // Try to return again
        boolean secondReturn = loanRepository.returnBook(loan.getLoanId(), LocalDate.now());
        assertFalse(secondReturn);
    }

    @Test
    void testFindLoanById() {
        Loan newLoan = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());
        Loan foundLoan = loanRepository.findLoanById(newLoan.getLoanId());

        assertNotNull(foundLoan);
        assertEquals(newLoan.getLoanId(), foundLoan.getLoanId());
        assertEquals("U001", foundLoan.getUserId());
        assertEquals("978-0451524935", foundLoan.getBookIsbn());
    }

    @Test
    void testFindLoanByIdNotFound() {
        Loan foundLoan = loanRepository.findLoanById("NONEXISTENT_LOAN");
        assertNull(foundLoan);
    }

    @Test
    void testGetAllLoans() {
        List<Loan> allLoans = loanRepository.getAllLoans();
        assertFalse(allLoans.isEmpty());

        // Should contain at least the sample loans
        boolean hasSampleLoans = allLoans.stream()
                .anyMatch(loan -> loan.getLoanId().equals("L0001") || loan.getLoanId().equals("L0002"));
        assertTrue(hasSampleLoans);
    }

//    @Test
//    void testLoanIdIncrement() {
//        Loan loan1 = loanRepository.createLoan("U001", "978-0451524935", LocalDate.now());
//        Loan loan2 = loanRepository.createLoan("U001", "978-0141439518", LocalDate.now());
//        Loan loan3 = loanRepository.createLoan("U001", "978-0316769174", LocalDate.now());
//
//        // All loans should have unique IDs
//        assertNotEquals(loan1.getLoanId(), loan2.getLoanId());
//        assertNotEquals(loan2.getLoanId(), loan3.getLoanId());
//        assertNotEquals(loan1.getLoanId(), loan3.getLoanId());
//    }

    @Test
    void testSampleDataInitialization() {
        // Verify that sample data is properly initialized
        List<Loan> allLoans = loanRepository.getAllLoans();
        assertTrue(allLoans.size() >= 2); // At least 2 sample loans

        // Verify sample overdue loans exist
        boolean hasOverdueSample = allLoans.stream()
                .anyMatch(loan -> loan.getLoanId().equals("L0001") && loan.isOverdue());
        assertTrue(hasOverdueSample);
    }
}