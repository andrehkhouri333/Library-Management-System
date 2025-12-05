package com.library.model;

/**
 * Represents a book in the library system
 * @author Library Team
 * @version 1.0
 */
public class Book extends BaseMedia {
    private static final String MEDIA_TYPE = "BOOK";
    private static final int LOAN_PERIOD_DAYS = 28;
    private static final double DAILY_FINE_RATE = 0.25; // $0.25 per day (10 NIS ≈ $2.50 for 10 days)
    private static final double OVERDUE_FINE = 10.00; // $10 flat fine for overdue book

    /**
     * Constructor for creating a new book
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the ISBN of the book
     */
    public Book(String title, String author, String isbn) {
        super(title, author, isbn, MEDIA_TYPE, LOAN_PERIOD_DAYS, DAILY_FINE_RATE);
    }

    // Convenience getter for ISBN (alias for getIdentifier())
    public String getIsbn() {
        return getIdentifier();
    }

    // Convenience setter for ISBN (alias for setIdentifier())
    public void setIsbn(String isbn) {
        setIdentifier(isbn);
    }

    /**
     * Gets the flat overdue fine for books
     * @return $10.00 fine
     */
    public double getOverdueFine() {
        return OVERDUE_FINE;
    }
}