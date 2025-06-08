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

public class ManageBooksDialog extends JPanel {
    private JTextField searchField, titleField, authorField, genreField, copiesField;
    private JButton searchButton, addButton, removeButton, editButton, backButton,saveButton;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private int selectedRow = -1; // Track the selected row for editing
    private boolean isEditMode = false; // Flag to indicate edit mode
    private int selectedBookId; // Store selected book ID for editing

    public ManageBooksDialog(JFrame parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the title
        add(new JLabel("Manage Books"), gbc);

        // Search Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Search:"), gbc);

        gbc.gridx = 1;
        searchField = new JTextField(10);
        add(searchField, gbc);

        gbc.gridx = 2;
        searchButton = new JButton("Search");
        add(searchButton, gbc);
        
        // Add key listener to searchField
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBooks(searchField.getText().trim()); // Call the search method on Enter key press
                }
            }
        });

        // Title Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(10);
        add(titleField, gbc);

        // Author Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        authorField = new JTextField(10);
        add(authorField, gbc);

        // Genre Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Genre:"), gbc);

        gbc.gridx = 1;
        genreField = new JTextField(10);
        add(genreField, gbc);

        // Copies Field (Total Copies & Available Copies are the same)
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Copies:"), gbc); // Label for copies

        gbc.gridx = 1;
        copiesField = new JTextField(10);
        add(copiesField, gbc); // Input for copies

        // Add, Remove, and Edit Buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        addButton = new JButton("Add Book");
        add(addButton, gbc);

        gbc.gridx = 1;
        removeButton = new JButton("Remove Book");
        add(removeButton, gbc);
        
        gbc.gridx = 2;
        editButton = new JButton("Edit Book");
        add(editButton, gbc); // Edit button

        gbc.gridx = 2;
        saveButton = new JButton("Save Book");
        saveButton.setVisible(false); // Initially hidden
        add(saveButton, gbc); // Add save button

        // Table for displaying books
        String[] columnNames = {"Book ID", "Title", "Author", "Genre", "Total Copies", "Available Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(500, 200)); // Set table size

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3; // Span across all columns
        gbc.fill = GridBagConstraints.BOTH; // Allow the table to grow
        gbc.weighty = 1; // Allow vertical growth
        add(scrollPane, gbc);

        // Back Button
        gbc.weighty = 0; // Reset vertical weight
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3; // Span across all columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        backButton = new JButton("Back");
        add(backButton, gbc);

        // Action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks(searchField.getText().trim());
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Retrieve selected book details
                    selectedBookId = (int) tableModel.getValueAt(selectedRow, 0);
                    String title = (String) tableModel.getValueAt(selectedRow, 1);
                    String author = (String) tableModel.getValueAt(selectedRow, 2);
                    String genre = (String) tableModel.getValueAt(selectedRow, 3);
                    int totalCopies = (int) tableModel.getValueAt(selectedRow, 4);
                    
                    // Populate fields with selected book details
                    titleField.setText(title);
                    authorField.setText(author);
                    genreField.setText(genre);
                    copiesField.setText(String.valueOf(totalCopies));
                    
                    // Switch to edit mode
                    isEditMode = true;
                    editButton.setVisible(false); // Hide Edit button
                    saveButton.setVisible(true); // Show Save button
                } else {
                    JOptionPane.showMessageDialog(parent, "Please select a book to edit.");
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get updated values from fields
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = genreField.getText().trim();
                String copiesText = copiesField.getText().trim();
    
                if (!title.isEmpty() && !author.isEmpty() && !genre.isEmpty() && !copiesText.isEmpty()) {
                    int copies = Integer.parseInt(copiesText);
                    updateBookInDatabase(selectedBookId, title, author, genre, copies); // Update existing book
                    JOptionPane.showMessageDialog(parent, "Book updated successfully!");
                    clearFields(); // Clear the fields after updating
                    searchBooks(""); // Refresh the search results
                    saveButton.setVisible(false); // Hide Save button
                    editButton.setVisible(true); // Show Edit button again
                    isEditMode = false; // Reset edit mode
                } else {
                    JOptionPane.showMessageDialog(parent, "Please fill in all fields.");
                }
            }
        });
    
        // Existing code for addButton, removeButton, backButton...

        // Edit button action
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRow = booksTable.getSelectedRow(); // Get selected row
                if (selectedRow != -1) {
                    int bookId = (int) tableModel.getValueAt(selectedRow, 0);
                    String title = (String) tableModel.getValueAt(selectedRow, 1);
                    String author = (String) tableModel.getValueAt(selectedRow, 2);
                    String genre = (String) tableModel.getValueAt(selectedRow, 3);
                    int totalCopies = (int) tableModel.getValueAt(selectedRow, 4);

                    titleField.setText(title);
                    authorField.setText(author);
                    genreField.setText(genre);
                    copiesField.setText(String.valueOf(totalCopies));
                    
                    // Optionally remove the book here if desired
                    // removeBookFromDatabase(bookId);
                } else {
                    JOptionPane.showMessageDialog(parent, "Please select a book to edit.");
                }
            }
        });

        // Add book action
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = genreField.getText().trim();
                String copiesText = copiesField.getText().trim();

                if (!title.isEmpty() && !author.isEmpty() && !genre.isEmpty() && !copiesText.isEmpty()) {
                    int copies = Integer.parseInt(copiesText); // Convert text to integer for copies
                    if (selectedRow != -1) {
                        // Update existing book
                        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
                        updateBookInDatabase(bookId, title, author, genre, copies);
                        JOptionPane.showMessageDialog(parent, "Book updated successfully!");
                    } else {
                        // Add new book
                        addBookToDatabase(title, author, genre, copies, copies); // Set both total and available copies the same
                        JOptionPane.showMessageDialog(parent, "Book added successfully!");
                    }
                    clearFields(); // Clear the fields after adding/updating
                    searchBooks("");  // Refresh the search results
                } else {
                    JOptionPane.showMessageDialog(parent, "Please fill in all fields.");
                }
            }
        });
        
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow != -1) {
                    int bookId = (int) tableModel.getValueAt(selectedRow, 0);
                    removeBookFromDatabase(bookId);
                    tableModel.removeRow(selectedRow);  // Remove row from table view
                    JOptionPane.showMessageDialog(parent, "Book removed successfully!");
                } else {
                    JOptionPane.showMessageDialog(parent, "Please select a book to remove.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parentContainer = ManageBooksDialog.this.getParent();
                if (parentContainer != null && parentContainer.getLayout() instanceof CardLayout) {
                    CardLayout layout = (CardLayout) parentContainer.getLayout();
                    layout.show(parentContainer, "Dashboard");  // Show the main dashboard
                } else {
                    System.err.println("Back navigation failed: Parent container or layout is incorrect.");
                    JOptionPane.showMessageDialog(ManageBooksDialog.this, "Cannot go back. Layout or parent not set correctly.");
                }
            }
        });
    }

    private void addBookToDatabase(String title, String author, String genre, int totalCopies, int availableCopies) {
        String query = "INSERT INTO BOOKS (title, author, genre, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.setInt(4, totalCopies);
            stmt.setInt(5, availableCopies);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeBookFromDatabase(int bookId) {
        String query = "DELETE FROM BOOKS WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBooks(String searchText) {
        if (searchText.trim().isEmpty()) {
            tableModel.setRowCount(0);
            return;
        }

        String query = "SELECT book_id, title, author, genre, total_copies, available_copies FROM BOOKS WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, searchText + "%");
            stmt.setString(2, searchText + "%");
            stmt.setString(3, searchText + "%");
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Clear previous results

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                int totalCopies = rs.getInt("total_copies");
                int availableCopies = rs.getInt("available_copies");
                tableModel.addRow(new Object[]{bookId, title, author, genre, totalCopies, availableCopies});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage());
        }
    }

    private void updateBookInDatabase(int bookId, String title, String author, String genre, int totalCopies) {
    String query = "UPDATE BOOKS SET title = ?, author = ?, genre = ?, total_copies = ?, available_copies = ? WHERE book_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, title);
        stmt.setString(2, author);
        stmt.setString(3, genre);
        stmt.setInt(4, totalCopies);
        stmt.setInt(5, totalCopies); // Set available copies to total initially
        stmt.setInt(6, bookId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        genreField.setText("");
        copiesField.setText(""); // Clear copies field
        selectedRow = -1; // Reset selected row
    }
}