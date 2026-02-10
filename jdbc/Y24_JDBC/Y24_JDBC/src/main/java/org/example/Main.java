package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUrl = "jdbc:mysql://localhost:3306/myjdbc?useSSL=false&allowPublicKeyRetrieval=true";
        String uname = "root";
        String pass = "Arjun@2006";

        Scanner sc = new Scanner(System.in);

        System.out.println("Choose Operation:");
        System.out.println("1. Insert");
        System.out.println("2. Delete");
        System.out.println("3. List");
        System.out.print("Enter choice: ");
        int choice = readIntFromLine(sc);

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection serverCon = DriverManager.getConnection(serverUrl, uname, pass);
             Statement serverStmt = serverCon.createStatement()) {
            serverStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS myjdbc");
        }

        Connection con = DriverManager.getConnection(dbUrl, uname, pass);
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS faculty (" +
                "userid INT PRIMARY KEY," +
                "username VARCHAR(100) NOT NULL," +
                "empid INT NOT NULL" +
                ")"
            );
        }

        if (choice == 1) {
            // INSERT
            System.out.print("Enter User ID: ");
            int userid = readIntFromLine(sc);

            System.out.print("Enter Username: ");
            String user = sc.nextLine();

            System.out.print("Enter Employee ID: ");
            int empid = readIntFromLine(sc);

            String insertQuery =
                "INSERT INTO faculty (userid, username, empid) VALUES (?, ?, ?)";

            PreparedStatement st = con.prepareStatement(insertQuery);
            st.setInt(1, userid);
            st.setString(2, user);
            st.setInt(3, empid);

            int count = st.executeUpdate();
            System.out.println("Rows inserted: " + count);

            st.close();

        } else if (choice == 2) {
            // DELETE
            System.out.print("Enter User ID to delete: ");
            int userid = readIntFromLine(sc);

            String deleteQuery =
                "DELETE FROM faculty WHERE userid = ?";

            PreparedStatement st = con.prepareStatement(deleteQuery);
            st.setInt(1, userid);

            int count = st.executeUpdate();
            System.out.println("Rows deleted: " + count);

            st.close();

        } else if (choice == 3) {
            // LIST
            String selectQuery = "SELECT userid, username, empid FROM faculty ORDER BY userid";
            PreparedStatement st = con.prepareStatement(selectQuery);
            ResultSet rs = st.executeQuery();

            System.out.println("\nFaculty Records:");
            while (rs.next()) {
                int userid = rs.getInt("userid");
                String username = rs.getString("username");
                int empid = rs.getInt("empid");
                System.out.println(userid + " | " + username + " | " + empid);
            }

            rs.close();
            st.close();
        } else {
            System.out.println("Invalid choice");
        }

        con.close();
        sc.close();
    }

    private static int readIntFromLine(Scanner sc) {
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String first = parts[0].replaceAll("[^0-9-]", "");
            if (!first.isEmpty() && !first.equals("-")) {
                try {
                    return Integer.parseInt(first);
                } catch (NumberFormatException ignored) {
                    // fall through to prompt again
                }
            }
            System.out.print("Please enter a number: ");
        }
    }
}
