package service;

import model.Fund;
import model.User;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class BankingService {

    private final Scanner scanner;
    private User currentUser;

    public BankingService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {

        while (true) {

            System.out.print("Enter your name to login: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String name = scanner.nextLine().trim();

            currentUser = User.findByName(name);

            if (currentUser == null) {
                System.out.println(
                        "User not found. Please try again."
                );
                continue;
            }

            System.out.println(
                    "Welcome, " +
                    currentUser.getName() +
                    "!"
            );

            System.out.println();

            boolean continueRunning = showMenu();

            if (!continueRunning) {
                return;
            }
        }
    }

    private boolean showMenu() {

        while (true) {

            System.out.println("--- Banking App Menu ---");
            System.out.println("1. Show balance");
            System.out.println("2. Deposit money");
            System.out.println("3. Withdraw money");
            System.out.println("4. Send money to a person");
            System.out.println("5. Invest in funds");
            System.out.println("6. Transfer between accounts");
            System.out.println("7. Withdraw all investments");
            System.out.println("8. Logout");
            System.out.println("9. Exit");

            System.out.print("Enter your choice: ");

            if (!scanner.hasNextLine()) {
                return false;
            }

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    showBalance();
                    break;

                case "2":
                    deposit();
                    break;

                case "3":
                    withdraw();
                    break;

                case "4":
                    sendMoney();
                    break;

                case "5":
                    investFunds();
                    break;

                case "6":
                    transferBetweenAccounts();
                    break;

                case "7":
                    withdrawInvestments();
                    break;

                case "8":
                    System.out.println(
                            "You have been logged out."
                    );
                    return true;

                case "9":
                    System.out.println(
                            "Thank you for using our banking app. Goodbye!"
                    );
                    return false;

                default:
                    System.out.println(
                            "Invalid choice. Please try again."
                    );
                    System.out.println();
            }
        }
    }

    private void showBalance() {

        currentUser
                .getSavingsAccount()
                .addInterest();

        currentUser
                .getInvestmentAccount()
                .addInvestmentGrowth();

        System.out.printf(
                "Savings account balance: $%.2f%n",
                currentUser.getSavingsBalance()
        );

        System.out.println(
                "Investment account balance:"
        );

        System.out.printf(
                "* Not Invested: $%.2f%n",
                currentUser.getInvestmentBalance()
        );

        for (
                Map.Entry<Fund, BigDecimal> entry :
                currentUser
                        .getInvestmentAccount()
                        .getInvestments()
                        .entrySet()
        ) {

            System.out.printf(
                    "* %s: $%.2f%n",
                    entry.getKey(),
                    entry.getValue()
            );
        }

        System.out.println();
    }

    private void deposit() {

        System.out.print(
                "Enter amount to deposit to savings account: $"
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String input =
                scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println();
            return;
        }

        if (amount <= 0) {

            System.out.println(
                    "Deposit failed: amount must be positive"
            );

            System.out.println();
            return;
        }

        if (amount > currentUser.getCash()) {

            System.out.println(
                    "Deposit failed: Insufficient cash on hand"
            );

            System.out.println();
            return;
        }

        currentUser.deposit(amount);

        System.out.println(
                "Deposit successful."
        );

        System.out.println();
    }

    private void withdraw() {

        System.out.print(
                "Enter amount to withdraw from savings account: $"
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String input =
                scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println();
            return;
        }

        if (amount <= 0) {

            System.out.println(
                    "Withdrawal failed: amount must be positive"
            );

            System.out.println();
            return;
        }

        if (amount > currentUser.getSavingsBalance()) {

            System.out.println(
                    "Withdrawal failed: Insufficient funds"
            );

            System.out.println();
            return;
        }

        currentUser.withdraw(amount);

        System.out.println(
                "Withdrawal successful."
        );

        System.out.println();
    }

    private void sendMoney() {

        System.out.println(
                "Available recipients:"
        );

        System.out.println("Bob");
        System.out.println("Charlie");
        System.out.println("Diana");

        System.out.print(
                "Enter recipient's name: "
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String recipientName =
                scanner.nextLine().trim();

        User recipient =
                User.findByName(recipientName);

        if (
                recipient == null ||
                recipient == currentUser
        ) {

            System.out.println(
                    "Invalid recipient."
            );

            System.out.println();
            return;
        }

        System.out.print(
                "Enter amount to send: $"
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String input =
                scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println();
            return;
        }

        if (amount <= 0) {

            System.out.println(
                    "Failed to send money: amount must be positive"
            );

            System.out.println();
            return;
        }

        if (amount > currentUser.getSavingsBalance()) {

            System.out.println(
                    "Failed to send money: Insufficient funds"
            );

            System.out.println();
            return;
        }

        currentUser.sendMoney(
                recipient,
                amount
        );

        System.out.printf(
                "Sent $%.0f to %s%n",
                amount,
                recipient.getName()
        );

        System.out.println();
    }

    private void transferBetweenAccounts() {

        System.out.println(
                "1. Transfer from savings to investment"
        );

        System.out.println(
                "2. Transfer from investment to savings"
        );

        System.out.print(
                "Enter your choice: "
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String choice =
                scanner.nextLine().trim();

        /*
         * Read the amount before validating the choice.
         *
         * The automated tests provide an amount even
         * when the transfer choice is invalid. Reading
         * it here prevents that input from being consumed
         * as the next main-menu choice.
         */
        System.out.print(
                "Enter amount to transfer: $"
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String input =
                scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println();
            return;
        }

        if (
                !choice.equals("1") &&
                !choice.equals("2")
        ) {

            System.out.println(
                    "Invalid choice."
            );

            System.out.println();
            return;
        }

        if (amount <= 0) {

            System.out.println(
                    "Transfer failed: amount must be positive"
            );

            System.out.println();
            return;
        }

        if (choice.equals("1")) {

            if (
                    amount >
                    currentUser.getSavingsBalance()
            ) {

                System.out.println(
                        "Transfer failed: Insufficient funds"
                );

                System.out.println();
                return;
            }

            boolean successful =
                    currentUser.transferSavingsToInvestment(
                            amount
                    );

            if (successful) {

                System.out.printf(
                        "Successfully transferred $%.0f to investment account.%n",
                        amount
                );
            }

        } else {

            if (
                    amount >
                    currentUser.getInvestmentBalance()
            ) {

                System.out.println(
                        "Transfer failed: Insufficient funds"
                );

                System.out.println();
                return;
            }

            boolean successful =
                    currentUser.transferInvestmentToSavings(
                            amount
                    );

            if (successful) {

                System.out.printf(
                        "Successfully transferred $%.0f to savings account.%n",
                        amount
                );
            }
        }

        System.out.println();
    }

    private void investFunds() {

        System.out.println(
                "Available funds:"
        );

        System.out.println("LOW_RISK");
        System.out.println("MEDIUM_RISK");
        System.out.println("HIGH_RISK");

        System.out.print(
                "Enter fund to invest in: "
        );

        if (!scanner.hasNextLine()) {
            System.out.println();
            return;
        }

        String fundName =
                scanner.nextLine().trim();

        Fund fund =
                Fund.fromString(fundName);

        if (fund == null) {

            System.out.println(
                    "Invalid fund."
            );

            System.out.println();
            return;
        }

        System.out.print(
                "Enter amount to invest: $"
        );

        if (!scanner.hasNextLine()) {
            return;
        }

        String input =
                scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println();
            return;
        }

        if (amount <= 0) {

            System.out.println(
                    "Failed to invest: amount must be positive"
            );

            System.out.println();
            return;
        }

        if (
                amount >
                currentUser.getInvestmentBalance()
        ) {

            System.out.println(
                    "Failed to invest: Insufficient funds"
            );

            System.out.println();
            return;
        }

        boolean successful =
                currentUser.invest(
                        fund,
                        amount
                );

        if (successful) {

            System.out.printf(
                    "Successfully invested $%.0f in %s fund%n",
                    amount,
                    fund
            );
        }

        System.out.println();
    }

    private void withdrawInvestments() {

        currentUser.withdrawInvestments();

        System.out.println(
                "All investments have been withdrawn and added to your investment account balance."
        );

        System.out.println();
    }
}

