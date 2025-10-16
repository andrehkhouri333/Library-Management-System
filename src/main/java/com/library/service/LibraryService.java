package com.library.service;

import com.library.model.Book;
import com.library.model.User;
import com.library.repository.UserRepository;
import java.util.List;

/**
 * Main service for library operations
 * @author Library Team
 * @version 1.0
 */
public class LibraryService {
    private AuthService authService;
    private BookService bookService;
    private UserRepository userRepository;

    /**
     * Constructor that initializes all services
     */
    public LibraryService() {
        this.authService = new AuthService();
        this.bookService = new BookService();
        this.userRepository = new UserRepository();
    }

    /**
     * Displays all books in an organized format
     */
    public void displayAllBooks() {
        List<Book> books = bookService.getAllBooks();
        System.out.println("\n" + "=".repeat(100));
        System.out.println("LIBRARY BOOK COLLECTION");
        System.out.println("=".repeat(100));

        if (books.isEmpty()) {
            System.out.println("No books available in the library.");
        } else {
            for (int i = 0; i < books.size(); i++) {
                System.out.println((i + 1) + ". " + books.get(i));
            }
        }
        System.out.println("=".repeat(100));
    }

    /**
     * Displays all users in an organized format (admin only)
     */
    public void displayAllUsers() {
        if (!authService.isLoggedIn()) {
            System.out.println("Error: Admin login required to view users.");
            return;
        }

        List<User> users = userRepository.getAllUsers();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REGISTERED USERS");
        System.out.println("=".repeat(80));

        if (users.isEmpty()) {
            System.out.println("No users registered in the system.");
        } else {
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i));
            }
        }
        System.out.println("=".repeat(80));
    }

    // Getters for services
    public AuthService getAuthService() { return authService; }
    public BookService getBookService() { return bookService; }
}