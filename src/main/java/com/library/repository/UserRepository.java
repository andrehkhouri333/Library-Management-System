package com.library.repository;

import com.library.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing user data
 * @author Library Team
 * @version 1.0
 */
public class UserRepository {
    private List<User> users;

    /**
     * Constructor that initializes with sample users
     */
    public UserRepository() {
        this.users = new ArrayList<>();
        initializeSampleUsers();
    }

    /**
     * Adds sample users to the repository
     */
    private void initializeSampleUsers() {
        users.add(new User("U001", "John Smith", "john.smith@email.com"));
        users.add(new User("U002", "Emma Johnson", "emma.johnson@email.com"));
        users.add(new User("U003", "Michael Brown", "michael.brown@email.com"));
        users.add(new User("U004", "Sarah Davis", "sarah.davis@email.com"));
        users.add(new User("U005", "David Wilson", "david.wilson@email.com"));
    }

    /**
     * Gets all users in the repository
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}