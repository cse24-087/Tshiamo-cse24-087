
package src;

public class IndividualCustomer extends Customer {
    public IndividualCustomer(String name, String address) {
        super(name, address);
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }
}
