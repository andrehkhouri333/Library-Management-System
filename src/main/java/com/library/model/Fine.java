//package com.library.model;
//
///**
// * Represents a fine in the library system
// * @author Library Team
// * @version 1.0
// */
//public class Fine {
//    private String fineId;
//    private String userId;
//    private double amount;
//    private double paidAmount;
//    private boolean isPaid;
//
//    /**
//     * Constructor for creating a new fine
//     * @param fineId the unique fine ID
//     * @param userId the user ID
//     * @param amount the fine amount
//     */
//    public Fine(String fineId, String userId, double amount) {
//        this.fineId = fineId;
//        this.userId = userId;
//        this.amount = amount;
//        this.paidAmount = 0.0;
//        this.isPaid = false;
//    }
//
//    // Getters and setters
//    public String getFineId() { return fineId; }
//    public void setFineId(String fineId) { this.fineId = fineId; }
//
//    public String getUserId() { return userId; }
//    public void setUserId(String userId) { this.userId = userId; }
//
//    public double getAmount() { return amount; }
//    public void setAmount(double amount) { this.amount = amount; }
//
//    public double getPaidAmount() { return paidAmount; }
//    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
//
//    public boolean isPaid() { return isPaid; }
//    public void setPaid(boolean paid) { isPaid = paid; }
//
//    /**
//     * Gets the remaining balance to pay
//     * @return the remaining balance
//     */
//    public double getRemainingBalance() {
//        return amount - paidAmount;
//    }
//
//    /**
//     * Makes a payment towards the fine
//     * @param paymentAmount the amount to pay
//     * @return true if payment successful, false otherwise
//     */
//    public boolean makePayment(double paymentAmount) {
//        if (paymentAmount <= 0 || paymentAmount > getRemainingBalance()) {
//            return false;
//        }
//
//        paidAmount += paymentAmount;
//        if (paidAmount >= amount) {
//            isPaid = true;
//            paidAmount = amount; // Prevent overpayment
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("Fine ID: %-8s | User: %-6s | Amount: $%-6.2f | Paid: $%-6.2f | Remaining: $%-6.2f | Status: %s",
//                fineId, userId, amount, paidAmount, getRemainingBalance(), isPaid ? "Paid" : "Unpaid");
//    }
//}