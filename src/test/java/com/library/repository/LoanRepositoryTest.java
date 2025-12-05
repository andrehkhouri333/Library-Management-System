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
    private MediaRepository mediaRepository;  // CHANGED: Use MediaRepository

    @BeforeEach
    void setUp() {
        mediaRepository = new MediaRepository();  // CHANGED: Use MediaRepository
        loanRepository = new LoanRepository(mediaRepository);  // CHANGED: Use MediaRepository
    }

    @Test
    void testCreateBookLoan() {
        LocalDate borrowDate = LocalDate.now();

        // Use the new createBookLoan method
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", borrowDate);

        assertNotNull(loan);
        assertEquals("U001", loan.getUserId());
        assertEquals("978-0451524935", loan.getMediaId());  // CHANGED: Use getMediaId()
        assertEquals("BOOK", loan.getMediaType());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(borrowDate.plusDays(28), loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertFalse(loan.isOverdue());
        assertTrue(loan.getLoanId().startsWith("L"));
        assertEquals(0.25, loan.getDailyFineRate(), 0.001);  // Book fine rate
    }

    @Test
    void testCreateCDLoan() {
        LocalDate borrowDate = LocalDate.now();

        // Test creating a CD loan
        Loan loan = loanRepository.createCDLoan("U001", "CD-001", borrowDate);

        assertNotNull(loan);
        assertEquals("U001", loan.getUserId());
        assertEquals("CD-001", loan.getMediaId());
        assertEquals("CD", loan.getMediaType());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(borrowDate.plusDays(7), loan.getDueDate());  // CD loan period is 7 days
        assertNull(loan.getReturnDate());
        assertFalse(loan.isOverdue());
        assertTrue(loan.getLoanId().startsWith("L"));
        assertEquals(0.50, loan.getDailyFineRate(), 0.001);  // CD fine rate
    }

    @Test
    void testCreateLoanMarksMediaUnavailable() {
        // Verify book is initially available
        assertTrue(mediaRepository.findMediaById("978-0451524935").isAvailable());

        // Create book loan
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        assertNotNull(loan);

        // Book should now be unavailable
        assertFalse(mediaRepository.findMediaById("978-0451524935").isAvailable());
    }

    @Test
    void testFindLoansByUser() {
        // Create a loan for a user
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());

        List<Loan> userLoans = loanRepository.findLoansByUser("U001");
        assertFalse(userLoans.isEmpty());
        assertTrue(userLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
    }

    @Test
    void testFindLoansByMedia() {
        // Create a loan for a book
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());

        // Use the correct method name
        List<Loan> mediaLoans = loanRepository.findLoansByMedia("978-0451524935");
        assertFalse(mediaLoans.isEmpty());
        assertTrue(mediaLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
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
    void testReturnMediaSuccess() {
        // Create a loan first
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        String loanId = loan.getLoanId();

        // Verify media is unavailable
        assertFalse(mediaRepository.findMediaById("978-0451524935").isAvailable());

        // Return the media - use returnMedia method
        boolean returnSuccess = loanRepository.returnMedia(loanId, LocalDate.now());
        assertTrue(returnSuccess);

        // Verify media is available again
        assertTrue(mediaRepository.findMediaById("978-0451524935").isAvailable());

        // Verify loan has return date
        Loan returnedLoan = loanRepository.findLoanById(loanId);
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(LocalDate.now(), returnedLoan.getReturnDate());
    }

    @Test
    void testReturnMediaNotFound() {
        boolean returnSuccess = loanRepository.returnMedia("INVALID_LOAN_ID", LocalDate.now());
        assertFalse(returnSuccess);
    }

    @Test
    void testReturnMediaAlreadyReturned() {
        // Create and return a loan
        Loan loan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        loanRepository.returnMedia(loan.getLoanId(), LocalDate.now());

        // Try to return again
        boolean secondReturn = loanRepository.returnMedia(loan.getLoanId(), LocalDate.now());
        assertFalse(secondReturn);
    }

    @Test
    void testFindLoanById() {
        Loan newLoan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        Loan foundLoan = loanRepository.findLoanById(newLoan.getLoanId());

        assertNotNull(foundLoan);
        assertEquals(newLoan.getLoanId(), foundLoan.getLoanId());
        assertEquals("U001", foundLoan.getUserId());
        assertEquals("978-0451524935", foundLoan.getMediaId());  // CHANGED: Use getMediaId()
        assertEquals("BOOK", foundLoan.getMediaType());
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

    @Test
    void testLoanIdIncrement() {
        Loan loan1 = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        Loan loan2 = loanRepository.createBookLoan("U001", "978-0141439518", LocalDate.now());
        Loan loan3 = loanRepository.createCDLoan("U001", "CD-001", LocalDate.now());

        // All loans should have unique IDs
        assertNotEquals(loan1.getLoanId(), loan2.getLoanId());
        assertNotEquals(loan2.getLoanId(), loan3.getLoanId());
        assertNotEquals(loan1.getLoanId(), loan3.getLoanId());
    }

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


    @Test
    void testMixedMediaLoans() {
        // Create both book and CD loans
        Loan bookLoan = loanRepository.createBookLoan("U001", "978-0451524935", LocalDate.now());
        Loan cdLoan = loanRepository.createCDLoan("U001", "CD-001", LocalDate.now());

        assertNotNull(bookLoan);
        assertNotNull(cdLoan);

        assertEquals("BOOK", bookLoan.getMediaType());
        assertEquals("CD", cdLoan.getMediaType());

        // Different due dates (book: 28 days, CD: 7 days)
        assertEquals(LocalDate.now().plusDays(28), bookLoan.getDueDate());
        assertEquals(LocalDate.now().plusDays(7), cdLoan.getDueDate());

        // Different fine rates
        assertEquals(0.25, bookLoan.getDailyFineRate(), 0.001);
        assertEquals(0.50, cdLoan.getDailyFineRate(), 0.001);
    }
}