package model;

public class ChequeAccount extends Account {
    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer customer, String employerName, String employerAddress) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew BWP " + amount + " from cheque account " + accountNumber);
        } else {
            System.out.println("Invalid withdrawal amount.");
        }
    }
}
