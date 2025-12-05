package com.library.service;

import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

/**
 * Test class for library reminder processor
 * @author Library Team
 * @version 1.0
 */
class LibraryReminderProcessorTest {

    @Test
    void testUserOverdueReminderIsSent() {
        // Arrange
        EmailService mockEmailService = mock(EmailService.class);
        LoanRepository mockLoanRepository = mock(LoanRepository.class);
        UserRepository mockUserRepository = mock(UserRepository.class);

        ReminderService reminderService = new ReminderService(mockEmailService, mockLoanRepository, mockUserRepository);

        // Act
        reminderService.sendOverdueReminder("andrehkhouri333@gmail.com", "John Doe", 3);

        // Assert
        verify(mockEmailService, times(1))
                .sendEmail(eq("andrehkhouri333@gmail.com"),
                        eq("Overdue Book Reminder"),
                        contains("You have 3 overdue item(s)"));  // Changed from "book(s)" to "item(s)"
    }
}
