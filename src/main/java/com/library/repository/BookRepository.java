package com.library.repository;

import com.library.model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing book data
 * @author Library Team
 * @version 1.0
 */
public class BookRepository {
    private List<Book> books;

    /**
     * Constructor that initializes with sample books
     */
    public BookRepository() {
        this.books = new ArrayList<>();
        initializeSampleBooks();
    }

    /**
     * Adds sample books to the repository
     */
    private void initializeSampleBooks() {
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565"));
        books.add(new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084"));
        books.add(new Book("1984", "George Orwell", "978-0451524935"));
        books.add(new Book("Pride and Prejudice", "Jane Austen", "978-0141439518"));
        books.add(new Book("The Catcher in the Rye", "J.D. Salinger", "978-0316769174"));
        books.add(new Book("The Hobbit", "J.R.R. Tolkien", "978-0547928227"));
        books.add(new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "978-0590353427"));
    }

    /**
     * Adds a new book to the repository
     * @param book the book to add
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Searches books by title, author, or ISBN
     * @param query the search query
     * @return list of matching books
     */
    public List<Book> searchBooks(String query) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                        book.getIsbn().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all books in the repository
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
}