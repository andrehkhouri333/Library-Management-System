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
    private LoanService loanService;

    /**
     * Gets fine breakdown by media type
     * @param userId the user ID
     * @return formatted string with fine breakdown
     */
    public String getFineBreakdownByMediaType(String userId) {
        List<Fine> fines = getUserFines(userId);

        if (fines.isEmpty()) {
            return "✅ No fines found.";
        }

        StringBuilder breakdown = new StringBuilder();
        breakdown.append("\n📊 FINE BREAKDOWN BY MEDIA TYPE:");
        breakdown.append("\n").append("-".repeat(50));

        // Separate fines by media type
        double bookFines = 0;
        double cdsFines = 0;
        int bookCount = 0;
        int cdCount = 0;

        for (Fine fine : fines) {
            if (!fine.isPaid()) {
                // Determine media type from loan
                if (loanService != null && fine.getLoanId() != null) {
                    com.library.model.Loan loan = loanService.getLoanRepository().findLoanById(fine.getLoanId());
                    if (loan != null) {
                        if ("BOOK".equals(loan.getMediaType())) {
                            bookFines += fine.getRemainingBalance();
                            bookCount++;
                        } else if ("CD".equals(loan.getMediaType())) {
                            cdsFines += fine.getRemainingBalance();
                            cdCount++;
                        }
                    }
                }
            }
        }

        if (bookCount > 0) {
            breakdown.append(String.format("\n📚 BOOK Fines: %d items | Total: $%.2f", bookCount, bookFines));
        }

        if (cdCount > 0) {
            breakdown.append(String.format("\n💿 CD Fines: %d items | Total: $%.2f", cdCount, cdsFines));
        }

        double total = bookFines + cdsFines;
        breakdown.append("\n").append("-".repeat(50));
        breakdown.append(String.format("\n💰 TOTAL UNPAID FINES: $%.2f", total));

        return breakdown.toString();
    }

    /**
     * Clean up duplicate fines (one-time use)
     */
    public void cleanupDuplicateFines() {
        System.out.println("Cleaning up duplicate fines...");
        // This would remove duplicate fines for the same loan
        // For now, just a placeholder
    }

    // Primary constructor with all dependencies
    public FineService(UserRepository userRepository, LoanService loanService) {
        this.fineRepository = new FineRepository();
        this.userRepository = userRepository;
        this.loanService = loanService;
    }

    // Constructor without LoanService - for backward compatibility
    public FineService(UserRepository userRepository) {
        this.fineRepository = new FineRepository();
        this.userRepository = userRepository;
        this.loanService = null; // Will be set later via setter
    }

    // Default constructor
    public FineService() {
        this.fineRepository = new FineRepository();
        this.userRepository = new UserRepository();
        this.loanService = null; // Will be set later via setter
    }

    /**
     * Set the LoanService dependency (to break circular dependency)
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Apply flat fine based on media type
     */
    public Fine applyFine(String userId, String reason, String loanId) {
        if (loanService == null) {
            System.out.println("❌ Error: Loan service not available.");
            return null;
        }

        // Get the loan
        com.library.model.Loan loan = loanService.getLoanRepository().findLoanById(loanId);
        if (loan == null) {
            System.out.println("❌ Error: Loan not found.");
            return null;
        }

        // Calculate flat fine based on media type
        double fineAmount = 0.0;
        if ("BOOK".equals(loan.getMediaType())) {
            fineAmount = 10.00; // $10 for books
        } else if ("CD".equals(loan.getMediaType())) {
            fineAmount = 20.00; // $20 for CDs
        }

        if (fineAmount <= 0) {
            System.out.println("❌ Error: Invalid fine amount.");
            return null;
        }

        // Check if fine already exists for this loan
        Fine existingFine = fineRepository.findFineByLoanId(loanId);
        if (existingFine != null) {
            System.out.println("⚠️ Fine already exists for loan " + loanId + ": " + existingFine.getFineId());
            // Update the amount if it's different
            if (existingFine.getAmount() != fineAmount) {
                existingFine.setAmount(fineAmount);
                System.out.println("⚠️ Updated fine amount to $" + fineAmount);
            }
            return existingFine;
        }

        Fine fine = fineRepository.createFine(userId, fineAmount, loanId);
        if (fine != null) {
            // Update user's borrowing ability
            User user = userRepository.findUserById(userId);
            if (user != null) {
                user.setCanBorrow(false);
                userRepository.updateUser(user);
            }
            System.out.println("Fine applied: $" + fineAmount + " for " + reason);
        }
        return fine;
    }

    /**
     * Apply a fine with amount (for backward compatibility)
     */
    public Fine applyFine(String userId, double amount, String reason) {
        // Validate amount
        if (amount <= 0) {
            System.out.println("❌ Error: Fine amount must be positive.");
            return null;  // Return null for invalid amounts
        }

        // For backward compatibility - create fine without loan ID
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
            System.out.println("❌ Error: Payment amount must be positive.");
            return false;
        }

        // Check if loanService is available
        if (loanService == null) {
            System.out.println("❌ Error: Loan service not available.");
            return false;
        }

        Fine fine = fineRepository.findFineById(fineId);
        if (fine == null) {
            System.out.println("❌ Error: Fine not found.");
            return false;
        }

        // Check if the fine is already paid
        if (fine.isPaid()) {
            System.out.println("❌ Error: Fine " + fineId + " is already paid.");
            return false;
        }

        String userId = fine.getUserId();
        String loanId = fine.getLoanId();

        // NEW LOGIC: Only check if the specific loan associated with this fine is still active
        if (loanId != null) {
            // Get the loan associated with this fine
            com.library.model.Loan loan = loanService.getLoanRepository().findLoanById(loanId);

            // Check if the loan exists and is still active (not returned)
            if (loan != null && loan.getReturnDate() == null) {
                System.out.println("❌ Error: Cannot pay fine for loan " + loanId + " because the item is not returned yet.");
                System.out.println("Please return the item first before paying the fine.");
                return false;
            }
        }

        Fine.PaymentResult paymentResult = fineRepository.makePayment(fineId, paymentAmount);
        if (paymentResult.isSuccess()) {
            // Show payment result message
            System.out.println("✅ Payment of $" + paymentAmount + " applied to fine " + fineId);
            System.out.println(paymentResult.getMessage());

            // If there was a refund, show it clearly
            if (paymentResult.getRefundAmount() > 0) {
                System.out.println("💰 Refund issued: $" + String.format("%.2f", paymentResult.getRefundAmount()));
            }

            // Update user's borrowing ability
            updateUserBorrowingAbility(userId);

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
                // Check if the fine is for a returned item
                String statusNote = "";
                if (fine.getLoanId() != null) {
                    com.library.model.Loan loan = loanService.getLoanRepository().findLoanById(fine.getLoanId());
                    if (loan != null && loan.getReturnDate() == null) {
                        statusNote = " (Item not returned)";
                    } else {
                        statusNote = " (Item returned)";
                    }
                }
                System.out.println(fine + statusNote);
            }
            double totalUnpaid = getTotalUnpaidAmount(userId);
            System.out.println("\nTOTAL UNPAID: $" + totalUnpaid);

            if (totalUnpaid > 0) {
                System.out.println("⚠️ Note: Fines for returned items can be paid immediately.");
                System.out.println("Fines for unreturned items require returning the item first.");
            } else {
                System.out.println("✅ All fines are paid. User can borrow books.");
            }
        }
        System.out.println("=".repeat(120));
    }

    public FineRepository getFineRepository() { return fineRepository; }
    public UserRepository getUserRepository() { return userRepository; }
    public LoanService getLoanService() { return loanService; }
}