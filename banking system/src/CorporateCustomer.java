package src;

public class CorporateCustomer extends Customer {
    private String companyRegistrationNumber;

    public CorporateCustomer(String name, String address, String registrationNumber) {
        super(name, address);
        this.companyRegistrationNumber = registrationNumber;
    }

    @Override
    public String getCustomerType() {
        return "Corporate";
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }
}
