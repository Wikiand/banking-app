package model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class InvestmentAccount extends Account {

    private final Map<Fund, BigDecimal> investments;

    private static final BigDecimal LOW_RISK_GROWTH =
            new BigDecimal("0.02");

    private static final BigDecimal MEDIUM_RISK_GROWTH =
            new BigDecimal("0.05");

    private static final BigDecimal HIGH_RISK_GROWTH =
            new BigDecimal("0.10");

    public InvestmentAccount() {
        super();
        investments = new LinkedHashMap<>();
    }

    public boolean deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        balance = balance.add(amount);
        return true;
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

    public boolean invest(Fund fund, BigDecimal amount) {

        if (fund == null ||
                amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (amount.compareTo(balance) > 0) {
            return false;
        }

        balance = balance.subtract(amount);

        investments.merge(
                fund,
                amount,
                BigDecimal::add
        );

        return true;
    }

    public Map<Fund, BigDecimal> getInvestments() {
        return investments;
    }

    public void addInvestmentGrowth() {

        for (Map.Entry<Fund, BigDecimal> entry :
                investments.entrySet()) {

            BigDecimal growthRate;

            switch (entry.getKey()) {
                case LOW_RISK:
                    growthRate = LOW_RISK_GROWTH;
                    break;

                case MEDIUM_RISK:
                    growthRate = MEDIUM_RISK_GROWTH;
                    break;

                case HIGH_RISK:
                    growthRate = HIGH_RISK_GROWTH;
                    break;

                default:
                    growthRate = BigDecimal.ZERO;
            }

            BigDecimal growth =
                    entry.getValue().multiply(growthRate);

            entry.setValue(
                    entry.getValue().add(growth)
            );
        }
    }

    public BigDecimal withdrawAllInvestments() {

        BigDecimal total = balance;

        for (BigDecimal amount : investments.values()) {
            total = total.add(amount);
        }

        balance = total;
        investments.clear();

        return total;
    }
}
