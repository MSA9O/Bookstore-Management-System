package bookstore;

import javax.swing.*;
import java.awt.*;

// ManageBooksDialog class
class ManageBooksDialog extends JPanel {
    public ManageBooksDialog() {
        setBackground(Color.LIGHT_GRAY); // Example background
        setLayout(new BorderLayout());
        add(new JLabel("Manage Books Panel", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

// ManageMembersDialog class
class ManageMembersDialog extends JPanel {
    public ManageMembersDialog() {
        setBackground(Color.GRAY); // Example background
        setLayout(new BorderLayout());
        add(new JLabel("Manage Members Panel", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

// TransactionsDialog class
class TransactionsDialog extends JPanel {
    public TransactionsDialog() {
        setBackground(Color.DARK_GRAY); // Example background
        setLayout(new BorderLayout());
        add(new JLabel("Manage Transactions Panel", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}

// MainDashboard class
public class MainDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainDashboard(String username, String role) {
        setTitle("Bookstore Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // CardLayout to manage different screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        // Dashboard panel with welcome message and navigation buttons
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome To The BookStore", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        dashboardPanel.add(welcomeLabel);

        // Username Label
        JLabel usernameLabel = new JLabel("Logged in as: " + username + " (Role: " + role + ")", SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        dashboardPanel.add(usernameLabel);

        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between labels and buttons

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Create buttons
        JButton manageBooksButton = createStyledButton("Manage Books");
        JButton manageMembersButton = createStyledButton("Manage Members");
        JButton manageTransactionsButton = createStyledButton("Manage Transactions");
        JButton logoutButton = createStyledButton("Logout");

        // Add action listeners to buttons
        manageBooksButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "ManageBooks");
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        
        manageMembersButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageMembers"));
        manageTransactionsButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageTransactions"));
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose(); // Close the application
        });

        // Add buttons to button panel
        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(manageBooksButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(manageMembersButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(manageTransactionsButton, gbc);
        gbc.gridy = 3;
        buttonPanel.add(logoutButton, gbc);

        // Add button panel to the dashboard panel
        dashboardPanel.add(buttonPanel);

        // Add the dashboard panel to the main panel with CardLayout
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(new ManageBooksDialog(), "ManageBooks"); // Display simple ManageBooksDialog
        mainPanel.add(new ManageMembersDialog(), "ManageMembers");
        mainPanel.add(new TransactionsDialog(), "ManageTransactions");

        // Add mainPanel with CardLayout to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Set a preferred size for the JFrame
        setPreferredSize(new Dimension(1450, 700));
        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    // Method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(76, 161, 217));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard("Admin", "Admin")); // Replace "Admin" with the username and role as needed
    }
}