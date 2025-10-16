package com.library.model;

/**
 * Represents a book in the library system
 * @author Library Team
 * @version 1.0
 */
public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    /**
     * Constructor for creating a new book
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the ISBN of the book
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Title: %-30s | Author: %-20s | ISBN: %-15s | Available: %s",
                title, author, isbn, available ? "Yes" : "No");
    }
}