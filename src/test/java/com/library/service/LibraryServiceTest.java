package com.library.service;

import com.library.model.Book;
import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LibraryService
 * @author Library Team
 * @version 1.0
 */
class LibraryServiceTest {
    private LibraryService libraryService;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testDisplayAllBooks() {
        libraryService.displayAllBooks();
        String output = outputStream.toString();

        assertTrue(output.contains("LIBRARY BOOK COLLECTION"));
        assertTrue(output.contains("The Great Gatsby"));
        assertTrue(output.contains("F. Scott Fitzgerald"));
        assertTrue(output.contains("Available: Yes"));
    }

    @Test
    void testDisplayAllUsersWithoutAdminLogin() {
        libraryService.displayAllUsers();
        String output = outputStream.toString();

        assertTrue(output.contains("Error: Admin login required to view users."));
    }

    @Test
    void testDisplayAllUsersWithAdminLogin() {
        // Login as admin first
        libraryService.getAuthService().login("admin", "admin123");

        libraryService.displayAllUsers();
        String output = outputStream.toString();

        assertTrue(output.contains("REGISTERED USERS"));
        assertTrue(output.contains("John Smith"));
        assertTrue(output.contains("Emma Johnson"));
        assertTrue(output.contains("@email.com"));
    }

//    @Test
//    void testGetAuthService() {
//        AuthService authService = libraryService.getAuthService();
//        assertNotNull(authService);
//        assertFalse(authService.isLoggedIn());
//    }
//
//    @Test
//    void testGetBookService() {
//        BookService bookService = libraryService.getBookService();
//        assertNotNull(bookService);
//
//        // Test that book service can search books
//        List<Book> results = bookService.searchBooks("Gatsby");
//        assertFalse(results.isEmpty());
//        assertEquals("The Great Gatsby", results.get(0).getTitle());
//    }
//
//    @Test
//    void testLibraryServiceIntegration() {
//        // Test integration between services
//        AuthService authService = libraryService.getAuthService();
//        BookService bookService = libraryService.getBookService();
//
//        // Login and perform book operations
//        assertTrue(authService.login("admin", "admin123"));
//        assertTrue(authService.isLoggedIn());
//
//        // Add a new book
//        boolean bookAdded = bookService.addBook("Integration Test Book", "Test Author", "999888777", authService);
//        assertTrue(bookAdded);
//
//        // Search for the added book
//        List<Book> searchResults = bookService.searchBooks("Integration Test");
//        assertFalse(searchResults.isEmpty());
//        assertEquals("Integration Test Book", searchResults.get(0).getTitle());
//    }
//
//    @Test
//    void testDisplayMethodsDoNotThrowExceptions() {
//        // These should not throw any exceptions even when called multiple times
//        assertDoesNotThrow(() -> libraryService.displayAllBooks());
//        assertDoesNotThrow(() -> libraryService.displayAllUsers());
//
//        // Login and test again
//        libraryService.getAuthService().login("admin", "admin123");
//        assertDoesNotThrow(() -> libraryService.displayAllBooks());
//        assertDoesNotThrow(() -> libraryService.displayAllUsers());
//    }
//
//    @Test
//    void testServiceDependenciesAreInitialized() {
//        // Verify that all services are properly initialized and connected
//        assertNotNull(libraryService.getAuthService());
//        assertNotNull(libraryService.getBookService());
//
//        // AuthService should be functional
//        AuthService authService = libraryService.getAuthService();
//        assertFalse(authService.isLoggedIn());
//        assertTrue(authService.login("admin", "admin123"));
//        assertTrue(authService.isLoggedIn());
//
//        // BookService should be functional
//        BookService bookService = libraryService.getBookService();
//        List<Book> allBooks = bookService.getAllBooks();
//        assertFalse(allBooks.isEmpty());
//        assertTrue(allBooks.size() >= 7); // Should have at least sample books
//    }


}