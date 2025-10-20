package com.library.service;

import com.library.model.Fine;
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
 * Test class for FineService
 * @author Library Team
 * @version 1.0
 */
class FineServiceTest {
    private FineService fineService;
    private UserRepository userRepository;
    private BookRepository bookRepository; // Add this field

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        bookRepository = new BookRepository(); // Initialize it
        fineService = new FineService(userRepository);
    }

    @Test
    void testApplyFineSuccess() {
        Fine fine = fineService.applyFine("U001", 15.0, "Test fine");

        assertNotNull(fine);
        assertEquals("U001", fine.getUserId());
        assertEquals(15.0, fine.getAmount(), 0.001);
        assertFalse(fine.isPaid());

        // Check that user's borrowing ability is updated
        User user = userRepository.findUserById("U001");
        assertFalse(user.canBorrow());
    }

    @Test
    void testApplyFineInvalidAmount() {
        Fine fine = fineService.applyFine("U001", -10.0, "Invalid fine");
        assertNull(fine);

        Fine zeroFine = fineService.applyFine("U001", 0.0, "Zero fine");
        assertNull(zeroFine);
    }

    @Test
    void testPayFineFullPayment() {
        // Apply a fine first
        Fine fine = fineService.applyFine("U001", 25.0, "Test fine");

        boolean paymentSuccess = fineService.payFine(fine.getFineId(), 25.0);
        assertTrue(paymentSuccess);

        // Check that fine is marked as paid
        Fine paidFine = fineService.getFineRepository().findFineById(fine.getFineId());
        assertTrue(paidFine.isPaid());
        assertEquals(0.0, paidFine.getRemainingBalance(), 0.001);

        // Check that user can borrow again
        User user = userRepository.findUserById("U001");
        assertTrue(user.canBorrow());
    }

    @Test
    void testPayFinePartialPayment() {
        // Apply a fine first
        Fine fine = fineService.applyFine("U001", 30.0, "Test fine");

        boolean paymentSuccess = fineService.payFine(fine.getFineId(), 15.0);
        assertTrue(paymentSuccess);

        // Check that fine is not fully paid
        Fine partialPaidFine = fineService.getFineRepository().findFineById(fine.getFineId());
        assertFalse(partialPaidFine.isPaid());
        assertEquals(15.0, partialPaidFine.getRemainingBalance(), 0.001);

        // User should still not be able to borrow
        User user = userRepository.findUserById("U001");
        assertFalse(user.canBorrow());
    }

    @Test
    void testPayFineOverPayment() {
        // Apply a fine first
        Fine fine = fineService.applyFine("U001", 20.0, "Test fine");

        boolean paymentSuccess = fineService.payFine(fine.getFineId(), 25.0);
        assertTrue(paymentSuccess);

        // Check that fine is fully paid with refund
        Fine paidFine = fineService.getFineRepository().findFineById(fine.getFineId());
        assertTrue(paidFine.isPaid());
        assertEquals(0.0, paidFine.getRemainingBalance(), 0.001);
    }

    @Test
    void testPayFineInvalidPayment() {
        boolean paymentSuccess = fineService.payFine("invalid-fine-id", 10.0);
        assertFalse(paymentSuccess);

        boolean zeroPayment = fineService.payFine("F0001", 0.0);
        assertFalse(zeroPayment);

        boolean negativePayment = fineService.payFine("F0001", -5.0);
        assertFalse(negativePayment);
    }

    @Test
    void testGetUserFines() {
        // U002 has sample fines
        List<Fine> fines = fineService.getUserFines("U002");
        assertFalse(fines.isEmpty());
    }

    @Test
    void testGetUserUnpaidFines() {
        List<Fine> unpaidFines = fineService.getUserUnpaidFines("U002");
        assertFalse(unpaidFines.isEmpty());

        // All unpaid fines should have isPaid = false
        for (Fine fine : unpaidFines) {
            assertFalse(fine.isPaid());
        }
    }

    @Test
    void testGetTotalUnpaidAmount() {
        double totalUnpaid = fineService.getTotalUnpaidAmount("U002");
        assertTrue(totalUnpaid > 0);
    }

    @Test
    void testCompleteFineAndReturnFlow() {
        // Step 0: Verify initial state - U002 has an overdue book and unpaid fine
        LoanService loanService = new LoanService(fineService, userRepository, bookRepository);

        // Verify U002 has unpaid fines
        List<Fine> unpaidFines = fineService.getUserUnpaidFines("U002");
        assertFalse(unpaidFines.isEmpty());
        assertEquals("F0001", unpaidFines.get(0).getFineId());
        assertEquals(25.0, unpaidFines.get(0).getAmount(), 0.001);

        // Verify U002 has overdue loans
        List<Loan> userLoans = loanService.getUserActiveLoans("U002");
        assertFalse(userLoans.isEmpty());
        boolean hasOverdue = userLoans.stream().anyMatch(Loan::isOverdue);
        assertTrue(hasOverdue);

        // Verify U002 cannot borrow initially
        User userInitial = userRepository.findUserById("U002");
        assertFalse(userInitial.canBorrow());

        // Step 1: Try to return the overdue book - should FAIL because of unpaid fine
        boolean returnAttempt1 = loanService.returnBook("L0001", LocalDate.now());
        assertFalse(returnAttempt1);

        // Step 2: Try to borrow a new book - should FAIL because of unpaid fine AND overdue book
        Loan borrowAttempt1 = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNull(borrowAttempt1);

        // Step 3: Pay the fine
        boolean paymentSuccess = fineService.payFine("F0001", 25.0);
        assertTrue(paymentSuccess);

        // Verify fine is paid
        Fine paidFine = fineService.getFineRepository().findFineById("F0001");
        assertTrue(paidFine.isPaid());
        assertEquals(0.0, paidFine.getRemainingBalance(), 0.001);

        // Step 4: Try to borrow a new book again - should STILL FAIL because overdue book not returned
        Loan borrowAttempt2 = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNull(borrowAttempt2);

        // Step 5: Now return the overdue book - should SUCCESS because fine is paid
        boolean returnAttempt2 = loanService.returnBook("L0001", LocalDate.now());
        assertTrue(returnAttempt2);

        // Verify loan is returned
        Loan returnedLoan = loanService.getLoanRepository().findLoanById("L0001");
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(LocalDate.now(), returnedLoan.getReturnDate());

        // Step 6: Now try to borrow a new book - should SUCCESS because fine is paid and overdue book is returned
        Loan borrowAttempt3 = loanService.borrowBook("U002", "978-0451524935", LocalDate.now());
        assertNotNull(borrowAttempt3);

        // Verify the new loan details
        assertEquals("U002", borrowAttempt3.getUserId());
        assertEquals("978-0451524935", borrowAttempt3.getBookIsbn());
        assertEquals(LocalDate.now().plusDays(28), borrowAttempt3.getDueDate());

        // Final verification: User should now be able to borrow
        User userFinal = userRepository.findUserById("U002");
        assertTrue(userFinal.canBorrow());
    }
}