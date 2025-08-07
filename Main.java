import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LIB";
    private static final String USER = "root";
    private static final String PASS = "root";

    private Connection connection;
    private Scanner scanner;

    public Main() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector-j-x.x.x.jar is in your classpath.");
        }
    }

    public void start() {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }

        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addBooks();
                case 2 -> viewAllBooks();
                case 3 -> updateBooks();
                case 4 -> deleteBooks();
                case 5 -> System.out.println("Exiting... Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }

    private void displayMenu() {
        System.out.println("--- Simple Library Books Details ---");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Update Book Name / Author");
        System.out.println("4. Delete Book");
        System.out.println("5. Exit");
    }

    // --- CREATE ---
    private void addBooks() {
        System.out.println("\n--- Add New Book ---");

        System.out.print("Enter Book Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Author Name: ");
        String aname = scanner.nextLine();

        String sql = "INSERT INTO Books (Name, aname) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, aname);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book '" + name + "' added successfully.");
            } else {
                System.out.println("Failed to add book.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // --- READ ---
    private void viewAllBooks() {
        System.out.println("\n--- All Books in Library ---");
        String sql = "SELECT ID, name, aname FROM Books";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("ID");
                String name = rs.getString("name");
                String aname = rs.getString("aname");
                System.out.printf("ID: %d | Name: %s | Author: %s\n", id, name, aname);
            }

            if (!found) {
                System.out.println("No books found in library.");
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // --- UPDATE ---
    private void updateBooks() {
        System.out.println("\n--- Update Book ---");

        System.out.print("Enter Book ID to update: ");
        int ID = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new Book Name: ");
        String newName = scanner.nextLine();

        System.out.print("Enter new Author Name: ");
        String newAname = scanner.nextLine();

        String sql = "UPDATE Books SET name = ?, aname = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newAname);
            pstmt.setInt(3, ID);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book with ID " + ID + " updated successfully.");
            } else {
                System.out.println("No book found with ID " + ID + " to update.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // --- DELETE ---
    private void deleteBooks() {
        System.out.println("\n--- Delete Book ---");

        System.out.print("Enter Book ID to delete: ");
        int ID = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM Books WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ID);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book with ID " + ID + " deleted successfully.");
            } else {
                System.out.println("No book found with ID " + ID + " to delete.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
}