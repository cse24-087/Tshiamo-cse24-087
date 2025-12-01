package banking.controller;

import banking.model.Account;
import banking.service.AccountService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the account operation dialog.
 * Handles deposit and withdrawal operations.
 * 
 * @author Banking System
 */
public class AccountOperationController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label accountInfoLabel;
    @FXML
    private TextField amountField;
    @FXML
    private javafx.scene.control.Button submitButton;
    @FXML
    private javafx.scene.control.Button cancelButton;
    
    private Account account;
    private String operation;
    private AccountService accountService;
    private CustomerDashboardController parentController;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Initialization will be done in setAccount
    }

    /**
     * Sets the account and operation details.
     * 
     * @param account The account to operate on
     * @param operation The operation type ("DEPOSIT" or "WITHDRAW")
     * @param accountService The account service
     * @param parentController The parent dashboard controller
     */
    public void setAccount(Account account, String operation, AccountService accountService, 
                          CustomerDashboardController parentController) {
        this.account = account;
        this.operation = operation;
        this.accountService = accountService;
        this.parentController = parentController;
        
        titleLabel.setText(operation);
        accountInfoLabel.setText(String.format("Account: %s (%s) - Balance: BWP %.2f", 
                account.getAccountNumber(), account.getAccountType(), account.getBalance()));
        submitButton.setText(operation);
    }

    /**
     * Handles the submit button action.
     */
    @FXML
    private void handleSubmit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showAlert("Amount must be positive.", Alert.AlertType.ERROR);
                return;
            }
            
            if (operation.equals("DEPOSIT")) {
                accountService.deposit(account, amount);
                showAlert("Deposit successful!", Alert.AlertType.INFORMATION);
            } else if (operation.equals("WITHDRAW")) {
                accountService.withdraw(account, amount);
                showAlert("Withdrawal successful!", Alert.AlertType.INFORMATION);
            }
            
            parentController.refreshAccountList();
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid numeric amount.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the cancel button action.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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

