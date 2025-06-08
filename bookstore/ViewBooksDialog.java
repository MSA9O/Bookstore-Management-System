package bookstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ViewBooksDialog extends JDialog {
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public ViewBooksDialog(JFrame parent) {
        super(parent, "View Books", true);
        setLayout(new BorderLayout());
        setSize(600, 400);

        // Column names based on the BOOKS table schema
        String[] columnNames = {"Book ID", "Title", "Author", "Genre", "Publisher", "Year Published", "Total Copies", "Available Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        booksTable = new JTable(tableModel);

        // Fetch and populate books data from the database
        fetchBooks();

        // Adding the table to a scroll pane and setting the layout
        JScrollPane scrollPane = new JScrollPane(booksTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void fetchBooks() {
        String query = "SELECT book_id, title, author, genre, publisher, year_published, copies_total, copies_available FROM BOOKS";

        // Attempt to connect and retrieve data
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through the result set and add rows to the table model
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                int yearPublished = rs.getInt("year_published");
                int copiesTotal = rs.getInt("copies_total");
                int copiesAvailable = rs.getInt("copies_available");

                // Add a new row to the table model
                tableModel.addRow(new Object[]{bookId, title, author, genre, publisher, yearPublished, copiesTotal, copiesAvailable});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
    }
}