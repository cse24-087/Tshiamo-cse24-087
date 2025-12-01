package banking.model;

/**
 * Represents an employee in the banking system.
 * Employees can manage customer accounts and perform administrative tasks.
 * 
 * @author Banking System
 */
public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String role; // e.g., "MANAGER", "TELLER", "ADMIN"

    /**
     * Constructs an Employee with basic information.
     * 
     * @param id The unique employee ID
     * @param firstName The employee's first name
     * @param lastName The employee's last name
     * @param email The employee's email
     * @param role The employee's role
     */
    public Employee(int id, String firstName, String lastName, String email, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    /**
     * Gets the employee's unique ID.
     * 
     * @return The employee ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the employee's first name.
     * 
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the employee's last name.
     * 
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the employee's email.
     * 
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the employee's role.
     * 
     * @return The role
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the employee's full name.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

