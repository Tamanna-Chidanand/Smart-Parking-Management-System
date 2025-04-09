package model;

public class Invoice {
    private double originalAmount;
    private double finalAmount;
    private boolean success;

    public Invoice(double originalAmount, double finalAmount, boolean success) {
        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
        this.success = success;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public boolean isSuccess() {
        return success;
    }
}
