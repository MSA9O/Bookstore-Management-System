
package bookstore;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("Bookstore Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 30, 80, 25);
        add(userLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 150, 25);
        add(usernameField);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 70, 80, 25);
        add(passLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 150, 25);
        add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 110, 80, 25);
        add(loginButton);

        // Action Listener for Login Button
        loginButton.addActionListener(e -> performLogin());

        // Key Listener for Enter Key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        // Add the key listener to both text fields
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Center the JFrame
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
    
        // Debugging output
        System.out.println("Attempting login with Username: " + username);
        System.out.println("Password entered: " + password);
    
        String userRole = authenticateUser(username, password);
    
        if (userRole != null) {
            JOptionPane.showMessageDialog(LoginScreen.this, "Login successful! Welcome, " + userRole + ".");
            dispose();
            new MainDashboard(username, userRole).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(LoginScreen.this, "Invalid username or password.");
        }
    }
    
    private String authenticateUser(String username, String password) {
        String query = "SELECT password_hash, role FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            if (conn == null) {
                System.out.println("Database connection failed.");
                return null;
            }
    
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");
                String role = rs.getString("role");
    
                // Debugging output
                System.out.println("Stored Password Hash: " + storedPasswordHash);
                System.out.println("User Role: " + role);
    
                // Assuming plain text comparison for testing purposes
                if (storedPasswordHash.equals(password)) {
                    return role; // Return the user role if authenticated
                } else {
                    System.out.println("Password does not match.");
                }
            } else {
                System.out.println("No user found with the given username.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
        return null; // Return null if authentication fails
    }    
    private String getMemberName(String memberId) {
        String query = "SELECT name FROM MEMBERS WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name"); // Return the member's name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Member"; // Default name if not found
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}