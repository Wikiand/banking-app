package model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {

    private static final BigDecimal INTEREST_RATE =
            new BigDecimal("0.01");

    public SavingsAccount() {
        super();
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        balance = balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (amount.compareTo(balance) > 0) {
            return false;
        }

        balance = balance.subtract(amount);
        return true;
    }

    public void addInterest() {
        BigDecimal interest = balance.multiply(INTEREST_RATE);
        balance = balance.add(interest);
    }
}
