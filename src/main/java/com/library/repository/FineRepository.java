package com.library.repository;

import com.library.model.Fine;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing fine data
 * @author Library Team
 * @version 1.0
 */
public class FineRepository {
    private List<Fine> fines;
    private int fineCounter;

    public FineRepository() {
        this.fines = new ArrayList<>();
        this.fineCounter = 1;
        initializeSampleFines();
    }

    /**
     * Adds sample fines for testing
     */
    private void initializeSampleFines() {
        // Create fines for two users
        createFine("U002", 25.0); // Emma Johnson has $25 fine
        createFine("U004", 40.0); // Sarah Davis has $40 fine
    }

    public Fine createFine(String userId, double amount) {
        String fineId = "F" + String.format("%04d", fineCounter++);
        Fine newFine = new Fine(fineId, userId, amount);
        fines.add(newFine);
        return newFine;
    }

    public List<Fine> findFinesByUser(String userId) {
        return fines.stream()
                .filter(fine -> fine.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Fine> getUnpaidFinesByUser(String userId) {
        return fines.stream()
                .filter(fine -> fine.getUserId().equals(userId) && !fine.isPaid())
                .collect(Collectors.toList());
    }

    public double getTotalUnpaidAmount(String userId) {
        return getUnpaidFinesByUser(userId).stream()
                .mapToDouble(Fine::getRemainingBalance)
                .sum();
    }

    public Fine.PaymentResult makePayment(String fineId, double paymentAmount) {
        Fine fine = findFineById(fineId);
        if (fine != null && !fine.isPaid()) {
            return fine.makePayment(paymentAmount);
        }
        return new Fine.PaymentResult(false, 0, "Fine not found or already paid.");
    }

    public Fine findFineById(String fineId) {
        return fines.stream()
                .filter(fine -> fine.getFineId().equals(fineId))
                .findFirst()
                .orElse(null);
    }

    public List<Fine> getAllFines() {
        return new ArrayList<>(fines);
    }
}