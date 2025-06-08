package bookstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionsDialog extends JPanel {
    private JTextField memberIdField, bookIdField;
    private JButton borrowButton, returnButton, searchButton, backButton;
    private JComboBox<String> statusComboBox; // For filtering by status
    private JTable transactionsTable;
    private DefaultTableModel tableModel;

    public TransactionsDialog(JFrame parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Span across three columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the title
        add(new JLabel("Manage Transactions"), gbc);

        // Member ID Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Member ID:"), gbc);

        gbc.gridx = 1;
        memberIdField = new JTextField(10);
        add(memberIdField, gbc);

        // Book ID Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1;
        bookIdField = new JTextField(10);
        add(bookIdField, gbc);

        // Borrow Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        borrowButton = new JButton("Borrow Book");
        add(borrowButton, gbc);

        // Return Button
        gbc.gridx = 1;
        returnButton = new JButton("Return Book");
        add(returnButton, gbc);

        // Status Filter
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Filter by Status:"), gbc);

        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"All", "Borrowed", "Returned"});
        add(statusComboBox, gbc);

        // Search Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        searchButton = new JButton("Search Transactions");
        add(searchButton, gbc);

     // Back Button
        gbc.gridx = 0; 
        gbc.gridy = 8; // Adjust this number based on your layout
        gbc.gridwidth = 5; // Make it span both columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        backButton = new JButton("Back");
        add(backButton, gbc);


        // Results Table
        String[] columnNames = {"Transaction ID", "Member ID", "Book ID", "Borrow Date", "Return Date", "Status", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        
        // Set preferred size for the table
        transactionsTable.setPreferredScrollableViewportSize(new Dimension(500, 150)); // Adjust height as needed
        scrollPane.setPreferredSize(new Dimension(600, 200)); // Adjust width and height as needed
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3; // Span across three columns
        gbc.fill = GridBagConstraints.BOTH; // Allow the table to grow
        gbc.weighty = 1; // Allow vertical growth
        add(scrollPane, gbc);

        // Action listeners
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowBook(memberIdField.getText(), bookIdField.getText());
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook(memberIdField.getText(), bookIdField.getText());
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTransactions(memberIdField.getText(), bookIdField.getText(), (String) statusComboBox.getSelectedItem());
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate back to the main dashboard
                Container parentContainer = TransactionsDialog.this.getParent();
                if (parentContainer != null && parentContainer.getLayout() instanceof CardLayout) {
                    CardLayout layout = (CardLayout) parentContainer.getLayout();
                    layout.show(parentContainer, "Dashboard"); // Show the main dashboard
                }
            }
        });
    }

    // Borrow book method
    private void borrowBook(String memberId, String bookId) {
        // Check if the book is already borrowed by this member
        if (isBookAlreadyBorrowed(memberId, bookId)) {
            JOptionPane.showMessageDialog(this, "This book is already borrowed by this member.");
            return;
        }

        String query = "INSERT INTO TRANSACTIONS (member_id, book_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberId);
            stmt.setString(2, bookId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusMonths(1))); // Set due date to one month from now
            stmt.setString(5, "Borrowed");
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error borrowing book: " + e.getMessage());
        }
    }

    // Method to check if the book is already borrowed by the member
    private boolean isBookAlreadyBorrowed(String memberId, String bookId) {
        String query = "SELECT COUNT(*) FROM TRANSACTIONS WHERE member_id = ? AND book_id = ? AND status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberId);
            stmt.setString(2, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is greater than 0, it means the book is borrowed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if any error occurs
    }

    // Return book method
    private void returnBook(String memberId, String bookId) {
        String query = "UPDATE TRANSACTIONS SET return_date = ?, status = ? WHERE member_id = ? AND book_id = ? AND status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(2, "Returned");
            stmt.setString(3, memberId);
            stmt.setString(4, bookId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No borrowed record found for this member and book.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage());
        }
    }

    // Filter transactions method
    private void filterTransactions(String memberId, String bookId, String status) {
        String query = "SELECT * FROM TRANSACTIONS WHERE 1=1"; // Start with a basic query
        if (!memberId.trim().isEmpty()) {
            query += " AND member_id = ?";
        }
        if (!bookId.trim().isEmpty()) {
            query += " AND book_id = ?";
        }
        if (!status.equals("All")) {
            query += " AND status = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            if (!memberId.trim().isEmpty()) {
                stmt.setString(index++, memberId);
            }
            if (!bookId.trim().isEmpty()) {
                stmt.setString(index++, bookId);
            }
            if (!status.equals("All")) {
                stmt.setString(index++, status);
            }

            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0); // Clear previous results

            while (rs.next()) {
                // Populate table with transaction data
                tableModel.addRow(new Object[]{
                        rs.getInt("transaction_id"),
                        rs.getString("member_id"),
                        rs.getString("book_id"),
                        rs.getDate("borrow_date"),
                        rs.getDate("return_date"),
                        rs.getString("status"),
                        rs.getDate("due_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering transactions: " + e.getMessage());
        }
    }
}