package com.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Loan model
 * @author Library Team
 * @version 1.0
 */
class LoanTest {

    @Test
    void testLoanCreation() {
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(28);
        Loan loan = new Loan("L0001", "U001", "978-0743273565", borrowDate, dueDate);

        assertEquals("L0001", loan.getLoanId());
        assertEquals("U001", loan.getUserId());
        assertEquals("978-0743273565", loan.getBookIsbn());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(dueDate, loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertFalse(loan.isOverdue());
    }

    @Test
    void testLoanNotOverdueWhenDueToday() {
        LocalDate borrowDate = LocalDate.now().minusDays(28);
        LocalDate dueDate = LocalDate.now();
        Loan loan = new Loan("L0001", "U001", "978-0743273565", borrowDate, dueDate);

        assertFalse(loan.checkOverdue(LocalDate.now()));
        assertFalse(loan.isOverdue());
    }

    @Test
    void testLoanOverdueAfterDueDate() {
        LocalDate borrowDate = LocalDate.now().minusDays(35);
        LocalDate dueDate = borrowDate.plusDays(28);
        Loan loan = new Loan("L0001", "U001", "978-0743273565", borrowDate, dueDate);

        assertTrue(loan.checkOverdue(LocalDate.now()));
        assertTrue(loan.isOverdue());
    }

    @Test
    void testLoanNotOverdueWhenReturned() {
        LocalDate borrowDate = LocalDate.now().minusDays(35);
        LocalDate dueDate = borrowDate.plusDays(28);
        LocalDate returnDate = LocalDate.now().minusDays(30);
        Loan loan = new Loan("L0001", "U001", "978-0743273565", borrowDate, dueDate);
        loan.setReturnDate(returnDate);

        assertFalse(loan.checkOverdue(LocalDate.now()));
        assertFalse(loan.isOverdue());
    }

    @Test
    void testLoanToString() {
        LocalDate borrowDate = LocalDate.of(2025, 10, 1);
        LocalDate dueDate = borrowDate.plusDays(28);
        Loan loan = new Loan("L0001", "U001", "978-0743273565", borrowDate, dueDate);

        String toStringResult = loan.toString();
        assertTrue(toStringResult.contains("L0001"));
        assertTrue(toStringResult.contains("U001"));
        assertTrue(toStringResult.contains("978-0743273565"));
        assertTrue(toStringResult.contains("2025-10-29")); // due date
    }
}