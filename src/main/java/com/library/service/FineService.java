package com.library.service;

import com.library.model.Fine;
import com.library.model.User;
import com.library.repository.FineRepository;
import com.library.repository.UserRepository;
import java.util.List;

/**
 * Service for handling fine-related operations
 * @author Library Team
 * @version 1.0
 */
public class FineService {
    private FineRepository fineRepository;
    private UserRepository userRepository;

    public FineService(UserRepository userRepository) {
        this.fineRepository = new FineRepository();
        this.userRepository = userRepository; // Use the shared UserRepository instance
    }

    public FineService() {
        this(new UserRepository());
    }

    public Fine applyFine(String userId, double amount, String reason) {
        if (amount <= 0) {
            System.out.println("Error: Fine amount must be positive.");
            return null;
        }

        Fine fine = fineRepository.createFine(userId, amount);
        if (fine != null) {
            // Update user's borrowing ability
            User user = userRepository.findUserById(userId);
            if (user != null) {
                user.setCanBorrow(false);
                userRepository.updateUser(user);
            }
            System.out.println("Fine applied: $" + amount + " for " + reason);
        }
        return fine;
    }

    public boolean payFine(String fineId, double paymentAmount) {
        if (paymentAmount <= 0) {
            System.out.println("Error: Payment amount must be positive.");
            return false;
        }

        Fine.PaymentResult paymentResult = fineRepository.makePayment(fineId, paymentAmount);
        if (paymentResult.isSuccess()) {
            Fine fine = fineRepository.findFineById(fineId);

            // Show payment result message
            System.out.println("Payment of $" + paymentAmount + " applied to fine " + fineId);
            System.out.println(paymentResult.getMessage());

            // If there was a refund, show it clearly
            if (paymentResult.getRefundAmount() > 0) {
                System.out.println("💰 Refund issued: $" + String.format("%.2f", paymentResult.getRefundAmount()));
            }

            // Update user's borrowing ability
            updateUserBorrowingAbility(fine.getUserId());

            if (fine.isPaid()) {
                System.out.println("✅ Fine " + fineId + " has been fully paid.");
            } else {
                System.out.println("Remaining balance: $" + fine.getRemainingBalance());
            }

            return true;
        } else {
            System.out.println("❌ Error: " + paymentResult.getMessage());
            return false;
        }
    }

    /**
     * Updates user's borrowing ability based on unpaid fines
     * @param userId the user ID
     */
    private void updateUserBorrowingAbility(String userId) {
        double unpaidAmount = getTotalUnpaidAmount(userId);
        User user = userRepository.findUserById(userId);
        if (user != null) {
            boolean canBorrowNow = (unpaidAmount == 0);

            // Only update if there's a change
            if (user.canBorrow() != canBorrowNow) {
                user.setCanBorrow(canBorrowNow);
                boolean updated = userRepository.updateUser(user);

                if (updated) {
                    if (canBorrowNow) {
                        System.out.println("🎉 All fines paid! User " + userId + " can now borrow books.");
                    } else {
                        System.out.println("⚠️ User " + userId + " cannot borrow books due to unpaid fines.");
                    }
                }
            }
        }
    }

    public List<Fine> getUserFines(String userId) {
        return fineRepository.findFinesByUser(userId);
    }

    public List<Fine> getUserUnpaidFines(String userId) {
        return fineRepository.getUnpaidFinesByUser(userId);
    }

    public double getTotalUnpaidAmount(String userId) {
        return fineRepository.getTotalUnpaidAmount(userId);
    }

    public void displayUserFines(String userId) {
        List<Fine> fines = getUserFines(userId);
        User user = userRepository.findUserById(userId);

        if (user == null) {
            System.out.println("Error: User not found.");
            return;
        }

        System.out.println("\n" + "=".repeat(120));
        System.out.println("FINES FOR USER: " + userId + " - " + user.getName());
        System.out.println("Can borrow books: " + (user.canBorrow() ? "YES" : "NO"));
        System.out.println("=".repeat(120));

        if (fines.isEmpty()) {
            System.out.println("No fines found for this user.");
        } else {
            for (Fine fine : fines) {
                System.out.println(fine);
            }
            double totalUnpaid = getTotalUnpaidAmount(userId);
            System.out.println("\nTOTAL UNPAID: $" + totalUnpaid);
            if (totalUnpaid > 0) {
                System.out.println("User cannot borrow books until all fines are paid.");
            } else {
                System.out.println("All fines are paid. User can borrow books.");
            }
        }
        System.out.println("=".repeat(120));
    }

    public FineRepository getFineRepository() { return fineRepository; }
    public UserRepository getUserRepository() { return userRepository; }
}