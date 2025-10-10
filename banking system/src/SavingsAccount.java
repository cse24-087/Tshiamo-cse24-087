package src;

public class SavingsAccount extends Account implements InterestBearing {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Withdrawals are not allowed from a Savings Account.");
    }

    @Override
    public void payInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Savings interest of BWP " + interest + " added to account " + accountNumber);
    }
}
