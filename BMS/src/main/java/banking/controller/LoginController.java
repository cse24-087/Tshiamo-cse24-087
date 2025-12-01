package banking.controller;

import banking.dao.CustomerDAO;
import banking.dao.EmployeeDAO;
import banking.model.Customer;
import banking.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the login view.
 * Handles user authentication and navigation to appropriate dashboard.
 * 
 * @author Banking System
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private RadioButton customerRadio;
    @FXML
    private RadioButton employeeRadio;
    private ToggleGroup userTypeGroup;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    
    private CustomerDAO customerDAO;
    private EmployeeDAO employeeDAO;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        customerDAO = new CustomerDAO();
        employeeDAO = new EmployeeDAO();
        // Create and set up ToggleGroup
        userTypeGroup = new ToggleGroup();
        customerRadio.setToggleGroup(userTypeGroup);
        employeeRadio.setToggleGroup(userTypeGroup);
        customerRadio.setSelected(true);
    }

    /**
     * Handles the login button action.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        
        try {
            if (customerRadio.isSelected()) {
                Customer customer = customerDAO.getCustomerByCredentials(username, password);
                if (customer != null) {
                    loadCustomerDashboard(customer);
                } else {
                    showError("Invalid username or password.");
                }
            } else {
                Employee employee = employeeDAO.getEmployeeByCredentials(username, password);
                if (employee != null) {
                    loadEmployeeDashboard(employee);
                } else {
                    showError("Invalid username or password.");
                }
            }
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows an error message to the user.
     * 
     * @param message The error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Loads the customer dashboard.
     * 
     * @param customer The authenticated customer
     */
    private void loadCustomerDashboard(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/banking/view/customer-dashboard.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(getClass().getResource("/banking/view/styles.css").toExternalForm());
            
            CustomerDashboardController controller = loader.getController();
            controller.setCustomer(customer);
            
            stage.setTitle("Banking System - Customer Dashboard");
            stage.setScene(scene);
            stage.setResizable(true);
        } catch (IOException e) {
            showError("Failed to load customer dashboard.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the employee dashboard.
     * 
     * @param employee The authenticated employee
     */
    private void loadEmployeeDashboard(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/banking/view/employee-dashboard.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/banking/view/styles.css").toExternalForm());
            
            EmployeeDashboardController controller = loader.getController();
            controller.setEmployee(employee);
            
            stage.setTitle("Banking System - Employee Dashboard");
            stage.setScene(scene);
            stage.setResizable(true);
        } catch (IOException e) {
            showError("Failed to load employee dashboard.");
            e.printStackTrace();
        }
    }
}
