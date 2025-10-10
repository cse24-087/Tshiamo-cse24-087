package src;

public class Main {
    public static void main(String[] args) {
        // Use a concrete subclass of Customer
        Customer customer1 = new IndividualCustomer("Gugu", "Gaborone");

        // Create accounts linked to this customer
        Account savings = new SavingsAccount("SA1001", 2000, "Main Branch", customer1);
        Account investment = new InvestmentAccount("IA2001", 1000, "Main Branch", customer1);
        Account cheque = new ChequeAccount("CA3001", 3000, "Main Branch", customer1, "TechCorp", "Gaborone CBD");

        // Link accounts to customer
        customer1.addAccount(savings);
        customer1.addAccount(investment);
        customer1.addAccount(cheque);

        // Display customer info
        System.out.println(customer1);

        // Perform transactions
        savings.deposit(500);
        savings.withdraw(200); // example withdrawal
        investment.withdraw(300);
        cheque.withdraw(1000);

        // Apply interest to interest-bearing accounts
        ((InterestBearing) savings).payInterest();
        ((InterestBearing) investment).payInterest();

        // Display final balances
        System.out.println("\nFinal Balances:");
        for (Account acc : customer1.getAccounts()) {
            System.out.println(acc.getAccountNumber() + " → BWP " + acc.getBalance());
        }
    }
}
