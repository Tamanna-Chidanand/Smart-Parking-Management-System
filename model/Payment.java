package model;

public class Payment {
    private double originalAmount;
    private double discountedAmount;
    private boolean success;

    public Payment(double amount) {
        this.originalAmount = amount;
        this.discountedAmount = amount;
        this.success = false;
    }

    public void applyDiscount(double percent) {
        discountedAmount = originalAmount * (1 - percent / 100);
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getDiscountedAmount() {
        return discountedAmount;
    }

    public void markSuccess() {
        success = true;
    }

    public void markFailure() {
        success = false;
    }

    public boolean isSuccess() {
        return success;
    }
}
