package com.library.service;

import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling reminder operations
 * @author Library Team
 * @version 1.0
 */
public class ReminderService {
    private final EmailService emailService;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public ReminderService(EmailService emailService, LoanRepository loanRepository, UserRepository userRepository) {
        this.emailService = emailService;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    /**
     * Sends overdue reminders to all users with overdue books
     */
    public void sendOverdueRemindersToAllUsers() {
        LocalDate currentDate = LocalDate.now();
        List<Loan> overdueLoans = loanRepository.getOverdueLoans(currentDate);

        // Group overdue loans by user
        var loansByUser = overdueLoans.stream()
                .collect(Collectors.groupingBy(Loan::getUserId));

        for (var entry : loansByUser.entrySet()) {
            String userId = entry.getKey();
            List<Loan> userOverdueLoans = entry.getValue();

            sendOverdueReminderToUser(userId, userOverdueLoans.size());
        }
    }

    /**
     * Sends an overdue reminder to a specific user
     * @param userId the user ID
     * @param overdueCount the number of overdue books
     */
    public void sendOverdueReminderToUser(String userId, int overdueCount) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            System.out.println("Error: User not found with ID: " + userId);
            return;
        }

        String userEmail = "andrehkhouri333@gmail.com"; // Fixed email as requested
        String subject = "Overdue Book Reminder";
        String body = String.format(
                "Dear %s,\n\nYou have %d overdue book(s). Please return them as soon as possible to avoid additional fines.\n\nBest regards,\nAn Najah Library System",
                user.getName(), overdueCount
        );

        try {
            emailService.sendEmail(userEmail, subject, body);
            System.out.println("Overdue reminder sent to " + user.getName() + " (" + userId + ")");
        } catch (Exception e) {
            System.out.println("Failed to send reminder to " + user.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Sends overdue reminder to a user by email (alternative method)
     * @param email the user's email
     * @param userName the user's name
     * @param overdueCount the number of overdue books
     */
    public void sendOverdueReminder(String email, String userName, int overdueCount) {
        String subject = "Overdue Book Reminder";
        String body = String.format(
                "Dear %s,\n\nYou have %d overdue book(s). Please return them as soon as possible to avoid additional fines.\n\nBest regards,\nAn Najah Library System",
                userName, overdueCount
        );

        try {
            emailService.sendEmail(email, subject, body);
            System.out.println("Overdue reminder sent to " + userName + " at " + email);
        } catch (Exception e) {
            System.out.println("Failed to send reminder to " + email + ": " + e.getMessage());
        }
    }
}
