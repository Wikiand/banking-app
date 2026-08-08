package service;

import exception.InvalidAmountException;
import model.User;
import java.math.BigDecimal;
import java.util.Scanner;

public class BankingService {
    private final Scanner scanner;
    private final User user = new User();

    public BankingService(Scanner scanner) {
        this.scanner = scanner;
    }

    // ---- entry point for the whole app, called once from BankingApp.main ----
// Outer loop = login sessions. Inner loop (runMenu) = one logged-in session.
// Returning from start() ends the program naturally (no System.exit).
    public void start() {
        user.createUser(); // create the CSV once if it doesn't exist

        boolean running = true;
        while (running) {
            String currentUser = login();
            if (currentUser == null) {
                // login() returns null on EOF (Ctrl+D) — end the program.
                running = false;
            } else {
                // runMenu returns false if the user chose Exit, true if they only logged out.
                boolean keepRunning = runMenu(currentUser);
                if (!keepRunning) {
                    running = false;
                }
            }
        }
        System.out.println("Goodbye.");
    }

    private String login() {
        while (true) {
            System.out.println("Enter your name to log in:");
            if (!scanner.hasNextLine()) {
                return null; // EOF
            }
            String name = scanner.nextLine().trim();

            // Accept only a name that actually exists in the file.
            for (String existing : user.getAllUsernames()) {
                if (existing.equalsIgnoreCase(name)) {
                    System.out.println("Welcome, " + existing + "!");
                    return existing; // return the stored spelling
                }
            }
            System.out.println("No such user. Try again.");
        }
    }

    private boolean runMenu(String currentUser) {
        while (true) {
            printMenu();

            if (!scanner.hasNextLine()) {
                return false; // EOF ends the program
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleShowBalance(currentUser);
                    break;
                case "2":
                    handleDeposit(currentUser);
                    break;
                case "3":
                    handleWithdraw(currentUser);
                    break;
                case "4":
                    handleSend(currentUser);
                    break;
                case "5":
                    handleInvest(currentUser);
                    break;
                case "6":
                    handleTransfer(currentUser);
                    break;
                case "7":
                    handleWithdrawAllInvestments(currentUser);
                    break;
                case "8":
                    System.out.println("Logged out.");
                    return true;  // back to login
                case "9":
                    return false; // exit the program
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ---- the menu text, printed before each choice ----
    private void printMenu() {
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
    }


    private void moveMoney(String senderName, String recipientName,
                           String fromAccount, String toAccount,
                           BigDecimal amount) throws InvalidAmountException {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        BigDecimal sourceBalance = user.getAccountBalance(senderName, fromAccount);
        if (sourceBalance == null) {
            throw new InvalidAmountException("Account not found: " + senderName + " / " + fromAccount);
        }

        if (sourceBalance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds.");
        }

        BigDecimal destBalance = user.getAccountBalance(recipientName, toAccount);
        if (destBalance == null) {
            throw new InvalidAmountException("Account not found: " + recipientName + " / " + toAccount);
        }

        user.setAccountBalance(senderName, fromAccount, sourceBalance.subtract(amount));
        user.setAccountBalance(recipientName, toAccount, destBalance.add(amount));
    }

    private BigDecimal askAmount(String prompt) {
        System.out.println(prompt);
        if (!scanner.hasNextLine()) {
            return null;
        }
        String line = scanner.nextLine().trim();
        try {
            return new BigDecimal(line);
        } catch (NumberFormatException e) {
            System.out.println("That's not a valid number.");
            return null;
        }
    }

    public void handleDeposit(String currentUser) {
        BigDecimal amount = askAmount("Enter amount to deposit:");
        if (amount == null) return;
        try {
            moveMoney(currentUser, currentUser, "cashBalance", "savingsBalance", amount);
            System.out.println("Deposit successful.");
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleWithdraw(String currentUser) {
        BigDecimal amount = askAmount("Enter amount to withdraw:");
        if (amount == null) return;
        try {
            moveMoney(currentUser, currentUser, "savingsBalance", "cashBalance", amount);
            System.out.println("Withdrawal successful.");
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleSend(String currentUser) {
        // Build the recipient list: everyone EXCEPT the logged-in user. Because the
        // current user is never in this list, a self-send is impossible by construction.
        java.util.List<String> recipients = new java.util.ArrayList<>();
        for (String name : user.getAllUsernames()) {
            if (!name.equalsIgnoreCase(currentUser)) {
                recipients.add(name);
            }
        }

        if (recipients.isEmpty()) {
            System.out.println("There is no one to send money to.");
            return;
        }

        // Show the choices as a numbered menu.
        System.out.println("Choose a recipient:");
        for (int i = 0; i < recipients.size(); i++) {
            System.out.println((i + 1) + ". " + recipients.get(i));
        }

        if (!scanner.hasNextLine()) return;
        String choice = scanner.nextLine().trim();

        int index;
        try {
            index = Integer.parseInt(choice) - 1; // menu is 1-based
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (index < 0 || index >= recipients.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        String recipient = recipients.get(index);

        BigDecimal amount = askAmount("Enter amount to send:");
        if (amount == null) return;
        try {
            moveMoney(currentUser, recipient, "savingsBalance", "savingsBalance", amount);
            System.out.println("Money sent to " + recipient + ".");
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    // 1. Show balance: BEFORE displaying, apply savings interest (1%) and each fund's
    // appreciation, add those gains to the stored balances, persist, then show totals
    // with interest already included.
    public void handleShowBalance(String currentUser) {
        // --- savings interest (1%) ---
        model.SavingsAccount savings = new model.SavingsAccount();
        BigDecimal savingsBal = user.getAccountBalance(currentUser, "savingsBalance");
        if (savingsBal == null) {
            System.out.println("No such user: " + currentUser);
            return;
        }
        BigDecimal newSavings = savingsBal.add(savings.calculateInterest(savingsBal));
        user.setAccountBalance(currentUser, "savingsBalance", newSavings);

        // --- fund appreciation (2% / 5% / 10%) ---
        model.InvestmentAccount investment = new model.InvestmentAccount();
        for (model.Fund fund : model.Fund.values()) {
            BigDecimal fundBal = user.getAccountBalance(currentUser, fund.getColumn());
            if (fundBal == null) continue;
            BigDecimal newFundBal = fundBal.add(investment.calculateFundGain(fundBal, fund));
            user.setAccountBalance(currentUser, fund.getColumn(), newFundBal);
        }

        // --- display everything, now that gains are applied and saved ---
        System.out.println("--- Balance for " + currentUser + " ---");
        System.out.println("Cash: " + user.getAccountBalance(currentUser, "cashBalance"));
        System.out.println("Savings: " + user.getAccountBalance(currentUser, "savingsBalance"));
        System.out.println("Investment: " + user.getAccountBalance(currentUser, "investmentBalance"));
        System.out.println("Low risk fund: " + user.getAccountBalance(currentUser, "lowRiskFundBalance"));
        System.out.println("Medium risk fund: " + user.getAccountBalance(currentUser, "mediumRiskFundBalance"));
        System.out.println("High risk fund: " + user.getAccountBalance(currentUser, "highRiskFundBalance"));
    }

    public void handleTransfer(String currentUser) {
        System.out.println("Transfer direction:");
        System.out.println("1. Savings to Investment");
        System.out.println("2. Investment to Savings");
        if (!scanner.hasNextLine()) return;
        String choice = scanner.nextLine().trim();

        String from;
        String to;
        if (choice.equals("1")) {
            from = "savingsBalance";
            to = "investmentBalance";
        } else if (choice.equals("2")) {
            from = "investmentBalance";
            to = "savingsBalance";
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        BigDecimal amount = askAmount("Enter amount to transfer:");
        if (amount == null) return;
        try {
            moveMoney(currentUser, currentUser, from, to, amount);
            System.out.println("Transfer successful.");
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleInvest(String currentUser) {
        System.out.println("Choose a fund:");
        System.out.println("1. LOW_RISK");
        System.out.println("2. MEDIUM_RISK");
        System.out.println("3. HIGH_RISK");
        if (!scanner.hasNextLine()) return;
        String choice = scanner.nextLine().trim();

        String fundColumn;
        switch (choice) {
            case "1": fundColumn = "lowRiskFundBalance"; break;
            case "2": fundColumn = "mediumRiskFundBalance"; break;
            case "3": fundColumn = "highRiskFundBalance"; break;
            default:
                System.out.println("Invalid fund choice.");
                return;
        }

        BigDecimal amount = askAmount("Enter amount to invest:");
        if (amount == null) return;
        try {
            moveMoney(currentUser, currentUser, "investmentBalance", fundColumn, amount);
            System.out.println("Investment successful.");
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleWithdrawAllInvestments(String currentUser) {
        boolean movedAnything = false;

        for (model.Fund fund : model.Fund.values()) {
            BigDecimal fundBalance = user.getAccountBalance(currentUser, fund.getColumn());
            if (fundBalance == null) {
                System.out.println("No such user: " + currentUser);
                return;
            }
            // Only move funds that actually hold money; a zero amount would be rejected.
            if (fundBalance.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    moveMoney(currentUser, currentUser, fund.getColumn(), "investmentBalance", fundBalance);
                    movedAnything = true;
                } catch (InvalidAmountException e) {
                    // Should not happen (we only move a positive balance we just read),
                    // but if it does, report it rather than crash.
                    System.out.println(e.getMessage());
                }
            }
        }

        if (movedAnything) {
            System.out.println("All investments withdrawn to your investment account.");
        } else {
            System.out.println("You have no investments to withdraw.");
        }
    }

}

