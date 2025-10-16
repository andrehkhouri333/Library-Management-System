package com.library.repository;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BookRepository
 * @author Library Team
 * @version 1.0
 */
class BookRepositoryTest {
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
    }

    @Test
    void testAddBook() {
        Book newBook = new Book("New Book", "New Author", "9999999999");
        bookRepository.addBook(newBook);

        List<Book> results = bookRepository.searchBooks("New Book");
        assertFalse(results.isEmpty());
        assertEquals("New Book", results.get(0).getTitle());
    }

    @Test
    void testSearchBooksCaseInsensitive() {
        List<Book> results = bookRepository.searchBooks("gReAt gAtSbY");
        assertFalse(results.isEmpty());
        assertEquals("The Great Gatsby", results.get(0).getTitle());
    }

    @Test
    void testGetAllBooks() {
        List<Book> allBooks = bookRepository.getAllBooks();
        assertFalse(allBooks.isEmpty());
    }
}