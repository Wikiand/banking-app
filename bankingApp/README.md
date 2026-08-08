# Banking App

A console-based Java banking application that allows users to manage their cash, savings account, investment account, and transactions.

## Features

* User login
* View savings and investment balances
* Deposit cash into a savings account
* Withdraw money from a savings account
* Send money to another user
* Transfer money between savings and investment accounts
* Invest money in different funds
* Withdraw all investments
* Logout
* Exit the application

## Project Structure

```text
bankingApp/
├── BankingApp.java
├── exception/
│   └── InvalidAmountException.java
├── model/
│   ├── Account.java
│   ├── Fund.java
│   ├── InvestmentAccount.java
│   ├── SavingsAccount.java
│   └── User.java
├── service/
│   └── BankingService.java
└── .gitignore
```

## Requirements

* Java Development Kit (JDK)
* Java compiler (`javac`)
* Java runtime (`java`)

You can verify your Java installation with:

```powershell
java -version
javac -version
```

## Running the Application

From the project root directory, compile the application with:

```powershell
javac BankingApp.java service\BankingService.java model\*.java exception\InvalidAmountException.java
```

Then run:

```powershell
java BankingApp
```

## Default Users

The application contains four predefined users:

* Alice
* Bob
* Charlie
* Diana

Each user starts with initial cash that can be deposited into their savings account.

## Account Flow

The application separates cash from the savings and investment accounts.

For example:

```text
Initial state

Cash:              $1000
Savings:              $0
Investment:           $0
```

After depositing `$500` into savings:

```text
Cash:               $500
Savings:            $500
Investment:           $0
```

After transferring `$100` from savings to investment:

```text
Cash:               $500
Savings:            $400
Investment:         $100
```

## Main Menu

After logging in, the application provides the following options:

```text
1. Show balance
2. Deposit money
3. Withdraw money
4. Send money to a person
5. Invest in funds
6. Transfer between accounts
7. Withdraw all investments
8. Logout
9. Exit
```

### Transfer Between Accounts

Users can transfer money in either direction:

```text
1. Transfer from savings to investment
2. Transfer from investment to savings
```

Transfers are validated to ensure that the user has sufficient funds.

## Investment Funds

The application supports three fund types:

```text
LOW_RISK
MEDIUM_RISK
HIGH_RISK
```

Users can invest available money from their investment account and later withdraw their investments.

## Data and Money Handling

The application uses `BigDecimal` internally for monetary values to provide more reliable handling of financial amounts.

## Git

The project is managed using Git and hosted on Gitea.

Compiled Java `.class` files are excluded from version control through `.gitignore`.

```gitignore
*.class
```

## Author

Christine Nyambura
