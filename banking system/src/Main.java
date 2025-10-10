package src;

public class Main {
    public static void main(String[] args) {
        Customer customer1 = new Customer("Gugu", "Dollo", "Gaborone");

        Account savings = new SavingsAccount("SA1001", 2000, "Main Branch", customer1);
        Account investment = new InvestmentAccount("IA2001", 1000, "Main Branch", customer1);
        Account cheque = new ChequeAccount("CA3001", 3000, "Main Branch", customer1, "TechCorp", "Gaborone CBD");

        customer1.addAccount(savings);
        customer1.addAccount(investment);
        customer1.addAccount(cheque);

        System.out.println(customer1);

        savings.deposit(500);
        savings.withdraw(200); // should not work
        investment.withdraw(300);
        cheque.withdraw(1000);

        ((InterestBearing) savings).payInterest();
        ((InterestBearing) investment).payInterest();

        System.out.println("Final Balances:");
        for (Account acc : customer1.getAccounts()) {
            System.out.println(acc.getAccountNumber() + " → BWP " + acc.getBalance());
        }
    }
}
