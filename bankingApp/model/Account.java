package model;

import java.math.BigDecimal;

public abstract class Account {

    protected BigDecimal balance;

    public Account() {
        balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
