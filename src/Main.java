//Charlie Evans 5537302

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    static String jdbcUrl = "jdbc:postgresql://localhost:63333/evan0712";
    static Connection conn;

    public static void main(String args[]) throws Exception {

        getDBConnection(); //Connect to database

        //Loop input
        while(true) {
            System.out.println("To sell a piano to existing customer, type 1");
            System.out.println("To find a piano from make and model, type 2");
            Scanner sc = new Scanner(System.in);
            int input = sc.nextInt(); //get input from command line

            if (input == 1) {
                sellPiano(); //begin the process of selling piano
            } else if (input == 2) {
                findPiano(); //begin the process of finding piano
            } else {
                System.out.println("Not a valid input");
            }
        }
    }

    //Setup database connection
    public static Connection getDBConnection() throws SQLException {
        if (conn == null) {
            //Create GUI
            JLabel label = new JLabel("Postgres Username");
            JTextField jtf = new JTextField();
            JLabel label2 = new JLabel("Postgres Password");
            JPasswordField jpf = new JPasswordField();
            JOptionPane.showConfirmDialog(null,
                    new Object[]{label, jtf, label2, jpf},
                    "Password:", JOptionPane.OK_CANCEL_OPTION);

            String password = String.valueOf(jpf.getPassword());
            conn = DriverManager.getConnection(jdbcUrl, jtf.getText(), password);
        }
        conn.setAutoCommit(true);
        return conn;
    }

    /////////////////////////
    //QUERY 1 SELL PIANO TO CUSTOMER
    ////////////////////////
    public static void sellPiano() {
        int updatedRows = 0; //Used for outputting row count

        //Get parameter 1, serial number
        System.out.print("Type the serial number of the piano (EX: ABC-1234) : ");
        Scanner sc = new Scanner(System.in);
        String pianoSerialNum = sc.nextLine();

        //Get parameter 2, customer id
        System.out.print("Type the customer id: ");
        sc = new Scanner(System.in);
        int customerId = sc.nextInt();

        //Get parameter 3, salesperson name
        System.out.print("Type the name of the salesperson: ");
        sc = new Scanner(System.in);
        String salesperson = sc.nextLine();

        //Get current date in SQL Date obj in the format 'YYYY-MM-DD'
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Date date = Date.valueOf(dateObj.format(formatter));

        //Randomly generate receipt id since not specified
        String receiptId = String.valueOf((int) (Math.random() * (10000)));

        //Randomly generate amount paid for piano since not specified
        int amountPaid = (int) (Math.random() * (300000 - 10000)) + 10000;

        //Construct prepared statement for inserting into receipt
        String sqlInsert = "INSERT INTO receipt VALUES(?,?,?,?,?)";

        try(PreparedStatement sellPianoStmt = conn.prepareStatement(sqlInsert);) {
            //Insert data into prepared statement
            sellPianoStmt.setString(1, receiptId);
            sellPianoStmt.setDate(2, date);
            sellPianoStmt.setInt(3, amountPaid);
            sellPianoStmt.setString(4, salesperson);
            sellPianoStmt.setInt(5, customerId);

            //Execute prepared statement, returning row count
            updatedRows = sellPianoStmt.executeUpdate();
            System.out.println(updatedRows + " added to receipt table");
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        //Construct prepared statement for deleting piano
        sqlInsert = "DELETE FROM piano WHERE serial_number = ?";

        try(PreparedStatement sellPianoStmt = conn.prepareStatement(sqlInsert);) {
            //Insert serial number into prepared statement
            sellPianoStmt.setString(1, pianoSerialNum);

            //Execute the prepared statement, returning rows
            updatedRows = sellPianoStmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        System.out.println(updatedRows + " rows deleted from piano table");
    }

    /////////////////////////
    //QUERY 2 FIND PIANO MAKE AND MODEL
    ////////////////////////
    public static void findPiano() {
        String serial_number, make, model, year, msrp, tradein_value;

        //Get parameter 1, the make of the piano
        System.out.print("Type the make of the piano: ");
        Scanner sc = new Scanner(System.in);
        String inputMake = sc.nextLine();

        //Get parameter 2, the model of the piano
        System.out.print("Type the model of the piano: ");
        sc = new Scanner(System.in);
        String inputModel = sc.nextLine();

        //Create prepared statement for finding piano from make and model
        String sqlInsert = "SELECT * FROM piano WHERE make = ? AND model = ?";

        try (PreparedStatement findPianoStmt = conn.prepareStatement(sqlInsert);) {
            //Insert data into prepared statement
            findPianoStmt.setString(1, inputMake);
            findPianoStmt.setString(2, inputModel);

            //Execute the prepared statement, and store the results in rs
            ResultSet rs = findPianoStmt.executeQuery();

            int i = 0; //Used to indicate rows affected

            //Loop over results until empty
            while (rs.next()) {
                i++;
                //Get data from results
                serial_number = rs.getString(1);
                make = rs.getString(2);
                model = rs.getString(3);
                year = rs.getString(4);
                msrp = rs.getString(5);
                tradein_value = rs.getString(6);
                //Print out each row
                System.out.println("Serial number: " + serial_number + " Make: " + make + " model: "
                        + model + " Year: " + year + " MSRP: " + msrp + " Trade in value: " + tradein_value);
            }
            rs.close();
            if (i == 0) {
                System.out.println("No results found try again");
            } else {
                System.out.println(i + " results found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}