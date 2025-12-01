package banking.controller;

import banking.dao.CustomerDAO;
import banking.model.Account;
import banking.model.Customer;
import banking.model.Employee;
import banking.service.AccountService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the employee dashboard view.
 * Handles employee operations like viewing all customers and accounts,
 * creating new accounts, and managing customer accounts.
 * 
 * @author Banking System
 */
public class EmployeeDashboardController {
    @FXML
    private Text welcomeText;
    @FXML
    private ListView<Customer> customerListView;
    @FXML
    private ListView<Account> accountListView;
    @FXML
    private TextField customerIdField;
    @FXML
    private TextField accountNumberField;
    @FXML
    private TextField initialDepositField;
    @FXML
    private TextField branchField;
    @FXML
    private ComboBox<String> accountTypeComboBox;
    @FXML
    private javafx.scene.control.Button createAccountButton;
    @FXML
    private javafx.scene.control.Button logoutButton;
    @FXML
    private javafx.scene.control.Button refreshButton;
    @FXML
    private javafx.scene.control.Button applyInterestButton;
    @FXML
    private TextField newCustomerFirstName;
    @FXML
    private TextField newCustomerLastName;
    @FXML
    private TextField newCustomerAddress;
    @FXML
    private TextField newCustomerEmployerName;
    @FXML
    private TextField newCustomerEmployerAddress;
    @FXML
    private TextField newCustomerUsername;
    @FXML
    private PasswordField newCustomerPassword;
    @FXML
    private javafx.scene.control.Button registerCustomerButton;
    
    private Employee employee;
    private CustomerDAO customerDAO;
    private AccountService accountService;
    private ObservableList<Customer> customers;
    private ObservableList<Account> accounts;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        customerDAO = new CustomerDAO();
        accountService = new AccountService();
        customers = FXCollections.observableArrayList();
        accounts = FXCollections.observableArrayList();
        
        customerListView.setItems(customers);
        accountListView.setItems(accounts);
        
        accountTypeComboBox.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        accountTypeComboBox.setValue("SAVINGS");
        
        customerListView.setCellFactory(param -> new javafx.scene.control.ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                } else {
                    setText(customer.getId() + " - " + customer.getFullName());
                }
            }
        });
        
        accountListView.setCellFactory(param -> new javafx.scene.control.ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - " + account.getAccountType() + 
                            " - BWP " + String.format("%.2f", account.getBalance()));
                }
            }
        });
        
        customerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadCustomerAccounts(newVal);
            }
        });
    }

    /**
     * Sets the employee and loads data.
     * 
     * @param employee The employee
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            welcomeText.setText("Welcome, " + employee.getFullName() + " (" + employee.getRole() + ")");
            loadAllCustomers();
        }
    }

    /**
     * Loads all customers from the database.
     */
    private void loadAllCustomers() {
        customers.clear();
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        customers.addAll(allCustomers);
    }

    /**
     * Loads accounts for a selected customer.
     * 
     * @param customer The customer
     */
    private void loadCustomerAccounts(Customer customer) {
        accounts.clear();
        accounts.addAll(customer.getAccounts());
        customerIdField.setText(String.valueOf(customer.getId()));
    }

    /**
     * Handles the create account button action.
     */
    @FXML
    private void handleCreateAccount() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            String accountNumber = accountNumberField.getText().trim();
            double initialDeposit = Double.parseDouble(initialDepositField.getText().trim());
            String branch = branchField.getText().trim();
            String accountType = accountTypeComboBox.getValue();
            
            if (accountNumber.isEmpty() || branch.isEmpty()) {
                showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
                return;
            }
            
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                showAlert("Customer not found.", Alert.AlertType.ERROR);
                return;
            }
            
            Account account = null;
            switch (accountType) {
                case "SAVINGS":
                    account = accountService.createSavingsAccount(customerId, accountNumber, initialDeposit, branch);
                    break;
                case "INVESTMENT":
                    account = accountService.createInvestmentAccount(customerId, accountNumber, initialDeposit, branch);
                    break;
                case "CHEQUE":
                    account = accountService.createChequeAccount(customerId, accountNumber, initialDeposit, branch);
                    break;
            }
            
            if (account != null) {
                showAlert("Account created successfully!", Alert.AlertType.INFORMATION);
                customer.addAccount(account);
                loadCustomerAccounts(customer);
                clearForm();
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Clears the account creation form.
     */
    private void clearForm() {
        accountNumberField.clear();
        initialDepositField.clear();
        branchField.clear();
    }

    /**
     * Handles the register customer button action.
     */
    @FXML
    private void handleRegisterCustomer() {
        try {
            String firstName = newCustomerFirstName.getText().trim();
            String lastName = newCustomerLastName.getText().trim();
            String address = newCustomerAddress.getText().trim();
            String employerName = newCustomerEmployerName.getText().trim();
            String employerAddress = newCustomerEmployerAddress.getText().trim();
            String username = newCustomerUsername.getText().trim();
            String password = newCustomerPassword.getText().trim();
            
            if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || 
                username.isEmpty() || password.isEmpty()) {
                showAlert("Please fill in all required fields (First Name, Last Name, Address, Username, Password).", 
                         Alert.AlertType.WARNING);
                return;
            }
            
            // Convert empty strings to null for optional fields
            String empName = employerName.isEmpty() ? null : employerName;
            String empAddr = employerAddress.isEmpty() ? null : employerAddress;
            
            Customer newCustomer = customerDAO.createCustomer(firstName, lastName, address, 
                                                              empName, empAddr, username, password);
            
            if (newCustomer != null) {
                showAlert("Customer registered successfully! Customer ID: " + newCustomer.getId(), 
                         Alert.AlertType.INFORMATION);
                clearCustomerForm();
                loadAllCustomers();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint")) {
                showAlert("Username already exists. Please choose a different username.", 
                         Alert.AlertType.ERROR);
            } else {
                showAlert("Error registering customer: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error registering customer: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Clears the customer registration form.
     */
    private void clearCustomerForm() {
        newCustomerFirstName.clear();
        newCustomerLastName.clear();
        newCustomerAddress.clear();
        newCustomerEmployerName.clear();
        newCustomerEmployerAddress.clear();
        newCustomerUsername.clear();
        newCustomerPassword.clear();
    }

    /**
     * Handles the apply interest button action.
     */
    @FXML
    private void handleApplyInterest() {
        Account selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account from the list.", Alert.AlertType.WARNING);
            return;
        }
        try {
            accountService.applyMonthlyInterest(selected);
            showAlert("Interest applied successfully to account " + selected.getAccountNumber() + "!", 
                     Alert.AlertType.INFORMATION);
            // Refresh the account list to show updated balance
            Customer selectedCustomer = customerListView.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                loadCustomerAccounts(selectedCustomer);
            }
        } catch (Exception e) {
            showAlert("Error applying interest: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Handles the refresh button action.
     */
    @FXML
    private void handleRefresh() {
        loadAllCustomers();
        accounts.clear();
    }

    /**
     * Handles the logout button action.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/banking/view/login.fxml")); // This path seems correct based on your resources
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 600, 400);
            scene.getStylesheets().add(getClass().getResource("/banking/view/styles.css").toExternalForm());
            
            stage.setTitle("Banking System - Login");
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog.
     * 
     * @param message The message to display
     * @param alertType The alert type
     */
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }
}
