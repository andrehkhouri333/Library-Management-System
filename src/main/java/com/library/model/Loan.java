package com.library.model;

import java.time.LocalDate;

/**
 * Represents a media loan in the library system
 * @author Library Team
 * @version 1.0
 */
public class Loan {
    private String loanId;
    private String userId;
    private String mediaId;
    private String mediaType; // "BOOK" or "CD"
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isOverdue;
    private double dailyFineRate; // Store fine rate for this specific loan

    public Loan(String loanId, String userId, String mediaId, String mediaType,
                LocalDate borrowDate, LocalDate dueDate, double dailyFineRate) {
        this.loanId = loanId;
        this.userId = userId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.isOverdue = false;
        this.dailyFineRate = dailyFineRate;
    }

    // Getters and setters
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }

    public double getDailyFineRate() { return dailyFineRate; }
    public void setDailyFineRate(double dailyFineRate) { this.dailyFineRate = dailyFineRate; }

    /**
     * Calculates fine for this loan based on overdue days
     * @param currentDate the current date
     * @return calculated fine amount
     */
    public double calculateFine(LocalDate currentDate) {
        if (returnDate != null || !isOverdue) {
            return 0.0;
        }

        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);
        if (overdueDays > 0) {
            return overdueDays * dailyFineRate;
        }
        return 0.0;
    }

    public boolean checkOverdue(LocalDate currentDate) {
        this.isOverdue = currentDate.isAfter(dueDate) && returnDate == null;
        return this.isOverdue;
    }

    @Override
    public String toString() {
        return String.format("Loan ID: %-8s | User: %-6s | Media: %-15s | Type: %-4s | Due: %s | Overdue: %s | Fine Rate: $%.2f/day",
                loanId, userId, mediaId, mediaType, dueDate, isOverdue ? "Yes" : "No", dailyFineRate);
    }
}