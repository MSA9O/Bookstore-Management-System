package bookstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageMembersDialog extends JPanel {
    private JTextField nameField, memberIdField, emailField, phoneField, searchField;
    private JButton addButton, backButton, removeButton, searchButton, editButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ManageMembersDialog(JFrame parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expand components horizontally
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Search Label and Field
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Search:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Make the search field expandable
        searchField = new JTextField(20);
        add(searchField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0; // Reset weight for button
        searchButton = new JButton("Search");
        add(searchButton, gbc);

        // Add key listener to searchField
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMembers(searchField.getText()); // Call the search method on Enter key press
                }
            }
        });

        // Row 2: Name Label and Field
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        add(nameField, gbc);

        // Row 3: Member ID Label and Field
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Member ID:"), gbc);

        gbc.gridx = 1;
        memberIdField = new JTextField(20);
        memberIdField.setEditable(false); // Non-editable
        add(memberIdField, gbc);

        // Row 4: Email Label and Field
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Row 5: Phone Label and Field
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        // Row 6: Buttons (Add, Edit, Remove)
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Member");
        editButton = new JButton("Edit Member");
        removeButton = new JButton("Remove Member");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, gbc);

        // Row 7: Results Table
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        String[] columnNames = {"Member ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, gbc);

        // Back Button
        gbc.weighty = 0; // Reset vertical weight
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3; // Span across all columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        backButton = new JButton("Back");
        add(backButton, gbc);

        // Action Listeners and Database Code remains the same
        addActionListeners(parent);
    }

    private void addActionListeners(JFrame parent) {
        searchButton.addActionListener(e -> searchMembers(searchField.getText()));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String memberId = memberIdField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();

                if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                    addMemberToDatabase(name, memberId, email, phone);
                    JOptionPane.showMessageDialog(parent, "Member added successfully!");
                    clearFields(); // Clear the fields after adding
                    setNextMemberId(); // Set the next auto-generated Member ID
                    searchMembers("");  // Refresh the search results
                } else {
                    JOptionPane.showMessageDialog(parent, "Please fill in all fields.");
                }
            }
        });

        removeButton.addActionListener(e -> removeSelectedMember(parent));

        backButton.addActionListener(e -> {
            Container parentContainer = ManageMembersDialog.this.getParent();
            if (parentContainer != null && parentContainer.getLayout() instanceof CardLayout) {
                CardLayout layout = (CardLayout) parentContainer.getLayout();
                layout.show(parentContainer, "Dashboard");  // Show the main dashboard
            } else {
                JOptionPane.showMessageDialog(ManageMembersDialog.this, "Cannot go back. Layout or parent not set correctly.");
            }
        });
    }

    private void addMemberToDatabase(String name, String memberId, String email, String phone) {
        String query = "INSERT INTO MEMBERS (name, member_id, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, memberId);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding member: " + e.getMessage());
        }
    }

    private void removeSelectedMember(JFrame parent) {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow != -1) {
            String memberId = (String) tableModel.getValueAt(selectedRow, 0);
            removeMemberFromDatabase(memberId);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(parent, "Member removed successfully!");
        } else {
            JOptionPane.showMessageDialog(parent, "Please select a member to remove.");
        }
    }

    private void removeMemberFromDatabase(String memberId) {
        String query = "DELETE FROM MEMBERS WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing member: " + e.getMessage());
        }
    }

    private void searchMembers(String searchText) {
        String query = "SELECT member_id, name, email, phone FROM MEMBERS WHERE member_id LIKE ? OR name LIKE ? OR email LIKE ? OR phone LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Use the search text with wildcards to match partial text
            String searchPattern = searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0); // Clear previous results
    
            while (rs.next()) {
                String memberId = rs.getString("member_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                tableModel.addRow(new Object[]{memberId, name, email, phone});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching members: " + e.getMessage());
        }
    }    

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        memberIdField.setText("");
    }

    private void setNextMemberId() {
        String query = "SELECT member_id FROM MEMBERS ORDER BY member_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("member_id").replace("BK", "");
                int nextId = Integer.parseInt(lastId) + 1;
                memberIdField.setText("BK" + nextId);
            } else {
                memberIdField.setText("BK100");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating member ID: " + e.getMessage());
        }
    }
}