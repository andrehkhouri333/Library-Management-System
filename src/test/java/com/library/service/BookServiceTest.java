package com.library.service;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BookService
 * @author Library Team
 * @version 1.0
 */
class BookServiceTest {
    private BookService bookService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        authService = new AuthService();
    }

    @Test
    void testAddBookWithoutAdminLogin() {
        boolean result = bookService.addBook("Test Book", "Test Author", "1234567890", authService);
        assertFalse(result);
    }

    @Test
    void testAddBookWithAdminLogin() {
        authService.login("admin", "admin123");
        boolean result = bookService.addBook("Test Book", "Test Author", "1234567890", authService);
        assertTrue(result);

        // Verify book was added by searching for it
        List<Book> results = bookService.searchBooks("Test Book");
        assertFalse(results.isEmpty());
        assertEquals("Test Book", results.get(0).getTitle());
    }

    @Test
    void testSearchBooksByTitle() {
        List<Book> results = bookService.searchBooks("Great Gatsby");
        assertFalse(results.isEmpty());
        assertEquals("The Great Gatsby", results.get(0).getTitle());
    }

    @Test
    void testSearchBooksByAuthor() {
        List<Book> results = bookService.searchBooks("Fitzgerald");
        assertFalse(results.isEmpty());
        assertEquals("F. Scott Fitzgerald", results.get(0).getAuthor());
    }

    @Test
    void testSearchBooksByISBN() {
        List<Book> results = bookService.searchBooks("978-0743273565");
        assertFalse(results.isEmpty());
        assertEquals("978-0743273565", results.get(0).getIsbn());
    }

    @Test
    void testSearchBooksNoResults() {
        List<Book> results = bookService.searchBooks("Nonexistent Book");
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllBooks() {
        List<Book> allBooks = bookService.getAllBooks();
        assertFalse(allBooks.isEmpty());
        assertTrue(allBooks.size() >= 7); // We have 7 sample books
    }
}