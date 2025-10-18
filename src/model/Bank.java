
package model;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String bankName;
    private List<Customer> customers;
    private List<Account> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    // Add new customer
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    // Add new account and link it to a customer
    public void addAccount(Account account, Customer customer) {
        accounts.add(account);
        customer.addAccount(account);
    }

    // Find customer by name
    public Customer findCustomer(String name) {
        for (Customer c : customers) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    // List all customers
    public List<Customer> getAllCustomers() {
        return customers;
    }

    // List all accounts
    public List<Account> getAllAccounts() {
        return accounts;
    }

    public String getBankName() {
        return bankName;
    }

    // Display summary
    public void printSummary() {
        System.out.println("=== " + bankName + " Summary ===");
        for (Customer customer : customers) {
            System.out.println(customer);
            for (Account account : customer.getAccounts()) {
                System.out.println("   -> " + account);
            }
        }
    }
}
