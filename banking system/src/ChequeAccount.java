package src;

public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer customer, String employerName, String employerAddress) {
        super(accountNumber, balance, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
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
