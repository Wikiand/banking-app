package model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class User {

    private final String name;
    private BigDecimal cash;

    private final SavingsAccount savingsAccount;
    private final InvestmentAccount investmentAccount;

    private static final Map<String, User> users =
            new LinkedHashMap<>();

    static {
        users.put(
                "Alice",
                new User("Alice", 1000.00)
        );

        users.put(
                "Bob",
                new User("Bob", 1000.00)
        );

        users.put(
                "Charlie",
                new User("Charlie", 1000.00)
        );

        users.put(
                "Diana",
                new User("Diana", 1000.00)
        );
    }

    public User(String name, double cash) {

        this.name = name;

        this.cash = BigDecimal.valueOf(cash);

        this.savingsAccount = new SavingsAccount();

        this.investmentAccount = new InvestmentAccount();
    }

    public static User findByName(String name) {

        if (name == null) {
            return null;
        }

        return users.get(name);
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCashAmount() {
        return cash;
    }

    public double getCash() {
        return cash.doubleValue();
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }

    public double getSavingsBalance() {
        return savingsAccount
                .getBalance()
                .doubleValue();
    }

    public double getInvestmentBalance() {
        return investmentAccount
                .getBalance()
                .doubleValue();
    }

    public void deposit(double amount) {

        BigDecimal value =
                BigDecimal.valueOf(amount);

        cash = cash.subtract(value);

        savingsAccount.deposit(value);
    }

    public void withdraw(double amount) {

        BigDecimal value =
                BigDecimal.valueOf(amount);

        if (savingsAccount.withdraw(value)) {
            cash = cash.add(value);
        }
    }

    public boolean sendMoney(
            User recipient,
            double amount) {

        if (recipient == null) {
            return false;
        }

        BigDecimal value =
                BigDecimal.valueOf(amount);

        if (!savingsAccount.withdraw(value)) {
            return false;
        }

        recipient.savingsAccount.deposit(value);

        return true;
    }

    public boolean transferSavingsToInvestment(
            double amount) {

        BigDecimal value =
                BigDecimal.valueOf(amount);

        if (!savingsAccount.withdraw(value)) {
            return false;
        }

        investmentAccount.deposit(value);

        return true;
    }

    public boolean transferInvestmentToSavings(
            double amount) {

        BigDecimal value =
                BigDecimal.valueOf(amount);

        if (!investmentAccount.withdraw(value)) {
            return false;
        }

        savingsAccount.deposit(value);

        return true;
    }

    public boolean invest(
            Fund fund,
            double amount) {

        return investmentAccount.invest(
                fund,
                BigDecimal.valueOf(amount)
        );
    }

    public void withdrawInvestments() {

        BigDecimal amount =
                investmentAccount.withdrawAllInvestments();

        savingsAccount.deposit(amount);
    }
}
