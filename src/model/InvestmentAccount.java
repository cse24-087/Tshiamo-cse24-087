package model;

public class InvestmentAccount extends Account implements InterestBearing {
    private static final double INTEREST_RATE = 0.05; // 5% monthly

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
        if (balance < 500.0) {
            System.out.println("Minimum deposit for Investment Account is BWP 500. Setting balance to 500.");
            this.balance = 500.0;
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew BWP " + amount + " from investment account " + accountNumber);
        } else {
            System.out.println("Invalid withdrawal amount.");
        }
    }

    @Override
    public void payInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Investment interest of BWP " + interest + " added to account " + accountNumber);
    }
}
