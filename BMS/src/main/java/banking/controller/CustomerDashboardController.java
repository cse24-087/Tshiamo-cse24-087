package banking.controller;

import banking.model.Account;
import banking.model.Customer;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the customer dashboard view.
 * Handles customer account operations and navigation.
 * 
 * @author Banking System
 */
public class CustomerDashboardController {
    @FXML
    private Text welcomeText;
    @FXML
    private ListView<Account> accountListView;
    @FXML
    private Text balanceText;
    @FXML
    private javafx.scene.control.Button depositButton;
    @FXML
    private javafx.scene.control.Button withdrawButton;
    @FXML
    private javafx.scene.control.Button logoutButton;
    @FXML
    private TextField newAccountNumber;
    @FXML
    private ComboBox<String> newAccountType;
    @FXML
    private TextField newAccountDeposit;
    @FXML
    private TextField newAccountBranch;
    @FXML
    private VBox employmentInfoBox;
    @FXML
    private TextField newAccountEmployerName;
    @FXML
    private TextField newAccountEmployerAddress;
    @FXML
    private javafx.scene.control.Button openAccountButton;
    
    private Customer customer;
    private AccountService accountService;
    private ObservableList<Account> accounts;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        accountService = new AccountService();
        accounts = FXCollections.observableArrayList();
        accountListView.setItems(accounts);
        
        // Set up account type combo box
        newAccountType.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        newAccountType.setValue("SAVINGS");
        
        // Show/hide employment info based on account type
        newAccountType.valueProperty().addListener((obs, oldVal, newVal) -> {
            employmentInfoBox.setVisible("CHEQUE".equals(newVal));
        });
        
        accountListView.setCellFactory(param -> new javafx.scene.control.ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " — " + account.getAccountType() + " — BWP " + 
                            String.format("%.2f", account.getBalance()));
                }
            }
        });
        
        accountListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateBalanceDisplay();
        });
    }

    /**
     * Sets the customer and loads their accounts.
     * 
     * @param customer The customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            welcomeText.setText("Welcome, " + customer.getFullName());
            accounts.clear();
            accounts.addAll(customer.getAccounts());
        }
    }

    /**
     * Updates the balance display based on the selected account.
     */
    private void updateBalanceDisplay() {
        Account selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            balanceText.setText(String.format("Balance: BWP %.2f (%s)", 
                    selected.getBalance(), selected.getAccountType()));
        } else {
            balanceText.setText("Select an account to view balance");
        }
    }

    /**
     * Handles the deposit button action.
     */
    @FXML
    private void handleDeposit() {
        Account selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account.", Alert.AlertType.WARNING);
            return;
        }
        openAccountOperation("DEPOSIT", selected);
    }

    /**
     * Handles the withdraw button action.
     */
    @FXML
    private void handleWithdraw() {
        Account selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account.", Alert.AlertType.WARNING);
            return;
        }
        openAccountOperation("WITHDRAW", selected);
    }


    /**
     * Opens the account operation dialog.
     * 
     * @param operation The operation type ("DEPOSIT" or "WITHDRAW")
     * @param account The account to operate on
     */
    private void openAccountOperation(String operation, Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/banking/view/account-operation.fxml")); // This path seems correct based on your resources
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load(), 400, 300);
            scene.getStylesheets().add(getClass().getResource("/banking/view/styles.css").toExternalForm());
            
            AccountOperationController controller = loader.getController();
            controller.setAccount(account, operation, accountService, this);
            
            stage.setTitle("Account Operation - " + operation);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert("Failed to open account operation dialog.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the account list display.
     */
    public void refreshAccountList() {
        accountListView.refresh();
        updateBalanceDisplay();
    }

    /**
     * Handles the open account button action.
     */
    @FXML
    private void handleOpenAccount() {
        try {
            String accountNumber = newAccountNumber.getText().trim();
            String accountType = newAccountType.getValue();
            double initialDeposit = Double.parseDouble(newAccountDeposit.getText().trim());
            String branch = newAccountBranch.getText().trim();
            
            if (accountNumber.isEmpty() || branch.isEmpty()) {
                showAlert("Please fill in all required fields.", Alert.AlertType.WARNING);
                return;
            }
            
            Account newAccount = null;
            
            if ("CHEQUE".equals(accountType)) {
                String employerName = newAccountEmployerName.getText().trim();
                String employerAddress = newAccountEmployerAddress.getText().trim();
                
                if (employerName.isEmpty() || employerAddress.isEmpty()) {
                    showAlert("Employment information is required for Cheque accounts.", Alert.AlertType.WARNING);
                    return;
                }
                
                newAccount = accountService.createChequeAccount(customer.getId(), accountNumber, 
                                                               initialDeposit, branch, 
                                                               employerName, employerAddress);
            } else if ("SAVINGS".equals(accountType)) {
                newAccount = accountService.createSavingsAccount(customer.getId(), accountNumber, 
                                                                 initialDeposit, branch);
            } else if ("INVESTMENT".equals(accountType)) {
                newAccount = accountService.createInvestmentAccount(customer.getId(), accountNumber, 
                                                                   initialDeposit, branch);
            }
            
            if (newAccount != null) {
                showAlert("Account opened successfully!", Alert.AlertType.INFORMATION);
                customer.addAccount(newAccount);
                accounts.add(newAccount);
                clearAccountForm();
                // Reload customer to get updated account list from database
                refreshCustomerData();
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error opening account: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Clears the account opening form.
     */
    private void clearAccountForm() {
        newAccountNumber.clear();
        newAccountDeposit.clear();
        newAccountBranch.clear();
        newAccountEmployerName.clear();
        newAccountEmployerAddress.clear();
        newAccountType.setValue("SAVINGS");
        employmentInfoBox.setVisible(false);
    }

    /**
     * Refreshes customer data from the database.
     */
    private void refreshCustomerData() {
        // Reload customer from database to get updated account list
        banking.dao.CustomerDAO customerDAO = new banking.dao.CustomerDAO();
        Customer updatedCustomer = customerDAO.getCustomerById(customer.getId());
        if (updatedCustomer != null) {
            this.customer = updatedCustomer;
            accounts.clear();
            accounts.addAll(customer.getAccounts());
        }
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
