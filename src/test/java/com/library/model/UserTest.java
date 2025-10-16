package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User model
 * @author Library Team
 * @version 1.0
 */
class UserTest {

    @Test
    void testUserCreation() {
        User user = new User("U001", "John Doe", "john.doe@email.com");

        assertEquals("U001", user.getUserId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@email.com", user.getEmail());
    }

    @Test
    void testUserSetters() {
        User user = new User("U001", "Initial Name", "initial@email.com");

        user.setUserId("U999");
        user.setName("Updated Name");
        user.setEmail("updated@email.com");

        assertEquals("U999", user.getUserId());
        assertEquals("Updated Name", user.getName());
        assertEquals("updated@email.com", user.getEmail());
    }

    @Test
    void testUserToString() {
        User user = new User("U001", "John Smith", "john.smith@email.com");
        String toStringResult = user.toString();

        assertTrue(toStringResult.contains("U001"));
        assertTrue(toStringResult.contains("John Smith"));
        assertTrue(toStringResult.contains("john.smith@email.com"));
    }

    @Test
    void testUserWithEmptyFields() {
        User user = new User("", "", "");

        assertEquals("", user.getUserId());
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
    }
}