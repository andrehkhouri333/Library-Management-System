package com.library.repository;

import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UserRepository
 * @author Library Team
 * @version 1.0
 */
class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void testGetAllUsers() {
        List<User> allUsers = userRepository.getAllUsers();
        assertFalse(allUsers.isEmpty());
        assertEquals(5, allUsers.size()); // We have 5 sample users
    }

    @Test
    void testUserDataIntegrity() {
        List<User> users = userRepository.getAllUsers();
        User firstUser = users.get(0);

        assertNotNull(firstUser.getUserId());
        assertNotNull(firstUser.getName());
        assertNotNull(firstUser.getEmail());
    }
}