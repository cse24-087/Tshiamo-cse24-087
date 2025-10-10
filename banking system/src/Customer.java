package src;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected String name;
    protected String address;
    protected List<Account> accounts;

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Customer: " + name + ", Address: " + address;
    }

    public abstract String getCustomerType();
}
