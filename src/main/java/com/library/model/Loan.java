//package com.library.model;
//
//import java.time.LocalDate;
//
///**
// * Represents a book loan in the library system
// * @author Library Team
// * @version 1.0
// */
//public class Loan {
//    private String loanId;
//    private String userId;
//    private String bookIsbn;
//    private LocalDate borrowDate;
//    private LocalDate dueDate;
//    private LocalDate returnDate;
//    private boolean isOverdue;
//
//    /**
//     * Constructor for creating a new loan
//     * @param loanId the unique loan ID
//     * @param userId the user ID
//     * @param bookIsbn the book ISBN
//     * @param borrowDate the date the book was borrowed
//     * @param dueDate the due date for return
//     */
//    public Loan(String loanId, String userId, String bookIsbn, LocalDate borrowDate, LocalDate dueDate) {
//        this.loanId = loanId;
//        this.userId = userId;
//        this.bookIsbn = bookIsbn;
//        this.borrowDate = borrowDate;
//        this.dueDate = dueDate;
//        this.returnDate = null;
//        this.isOverdue = false;
//    }
//
//    // Getters and setters
//    public String getLoanId() { return loanId; }
//    public void setLoanId(String loanId) { this.loanId = loanId; }
//
//    public String getUserId() { return userId; }
//    public void setUserId(String userId) { this.userId = userId; }
//
//    public String getBookIsbn() { return bookIsbn; }
//    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
//
//    public LocalDate getBorrowDate() { return borrowDate; }
//    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
//
//    public LocalDate getDueDate() { return dueDate; }
//    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
//
//    public LocalDate getReturnDate() { return returnDate; }
//    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
//
//    public boolean isOverdue() { return isOverdue; }
//    public void setOverdue(boolean overdue) { isOverdue = overdue; }
//
//    /**
//     * Checks if the loan is overdue based on current date
//     * @param currentDate the current date to check against
//     * @return true if overdue, false otherwise
//     */
//    public boolean checkOverdue(LocalDate currentDate) {
//        this.isOverdue = currentDate.isAfter(dueDate) && returnDate == null;
//        return this.isOverdue;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("Loan ID: %-8s | User: %-6s | Book: %-15s | Due: %s | Overdue: %s",
//                loanId, userId, bookIsbn, dueDate, isOverdue ? "Yes" : "No");
//    }
//}