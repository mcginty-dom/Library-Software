package Backend.Databases;

/**
 * File Name: TransactionDatabase.java
 * Creation Date: 17/11/2018
 * Copyright: No Copyright
 *
 * @version 1.0
 * @author Ryan Lucas
 */

import Backend.Resources.Copy;
import Backend.Transactions.FinancialTransaction;
import Backend.Transactions.FineTransaction;
import Backend.Transactions.PaymentTransaction;
import Backend.Transactions.Transaction;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


/**
 * Purpose:
 * The transaction database is used to save transactions to a file. When the system is started, previous transactions
 * will be imported, manipulated if necessary in the execution of the library system, then saved back to file, once
 * the system is closed.
 */
public class TransactionDatabase {

    /**
     * Delimiter used in the file
     */
    public static final String DELIMITER = "\t";

    private static final String RESOURCE_META_FLAG = "R";
    private static final String FINANCIAL_META_FLAG = "F";

    private static final String FINE_META_FLAG = "F";
    private static final String PAYMENT_META_FLAG = "P";

    /**
     * File path of the text file
     */
    private static String filePath = "src/data/transactions.txt";

    /**
     * Array which is used to store the file information on transactions once imported
     */
    private static ArrayList<Transaction> transactionArrayList;

    /**
     * Array which is used to store the file information on financial transactions once imported
     */
    private static ArrayList<FinancialTransaction> financialTransactionsArrayList;

    /**
     * Last Transaction ID currently in the system
     */
    private static int lastTransactionID;

    /**
     * Constructs the database from the file
     */
    public static void init() {

        double ms = System.currentTimeMillis();
        TransactionDatabase.readFile();
        lastTransactionID = getLastTransactionID();
        System.out.println(
                String.format("Transaction Database Loaded. %d Transactions on file. In %.2f ms",
                        transactionArrayList.size(), System.currentTimeMillis()-ms));
    }

    /**
     * Reads the data file into the system
     * @param in the scanner used to parse the file
     * @return An arraylist of transactions produced from the file
     */
    private static void readDataFile (Scanner in) {

        transactionArrayList = new ArrayList<>();
        financialTransactionsArrayList = new ArrayList<>();

        while (in.hasNext()) {

            String[] transactionLine = in.nextLine().split(DELIMITER);
            int index = 0;
            if(transactionLine[index++].equals(RESOURCE_META_FLAG)) {
                transactionArrayList.add(readResourceTransaction(transactionLine, index));
            } else {
                financialTransactionsArrayList.add(readFinancialTransaction(transactionLine, index));
            }
        }
    }

    private static Transaction readResourceTransaction(String[] transactionLine, int index) {
        int transactionID = Integer.valueOf(transactionLine[index++]);
        String username = transactionLine[index++];
        int copyID = Integer.valueOf(transactionLine[index++]);
        int resourceID = Integer.valueOf(transactionLine[index++]);
        Boolean isReserved = Boolean.valueOf(transactionLine[index++]);

        String date = transactionLine[index++];
        String time = transactionLine[index++];
        String returnDate = transactionLine[index++];
        if (returnDate.equals("null")) {
            returnDate = null;
        }
        String transactionDate = date + " " + time;

        Transaction newTransaction = new Transaction(transactionID, username, resourceID, copyID,
                isReserved, transactionDate, returnDate);
        return newTransaction;
    }

    private static FinancialTransaction readFinancialTransaction(String[] transactionLine, int index) {

        String metaFlag = transactionLine[index++];
        String username = transactionLine[index++];
        float value = Float.valueOf(transactionLine[index++]);
        String transactionDate = transactionLine[index++];
        if(metaFlag.equals(FINE_META_FLAG)){
            int copyID = Integer.valueOf(transactionLine[index++]);
            int resourceID = Integer.valueOf(transactionLine[index++]);
            int daysOverdue = Integer.valueOf(transactionLine[index++]);
            return new FineTransaction(username, value, copyID, resourceID, daysOverdue, transactionDate);
        } else {
            return new PaymentTransaction(username, value, transactionDate);
        }
    }

    /**
     * Loads the file into the system and produces the arraylist of transactions
     * @return An arraylist of transactions produced from the file
     */
    public static void readFile () {

        File inputFile = new File (filePath);

        Scanner in = null;

        try {

            in = new Scanner(inputFile);

        } catch (FileNotFoundException e) {

            System.out.println ("Cannot open: " + filePath);

            System.exit(0);
        }

        TransactionDatabase.readDataFile(in);
    }

    /**
     * Writes a list of transaction instances to a file
     */
    public static void writeFile () {

        FileWriter transactionFile;

        try {

            transactionFile = new FileWriter(new File (filePath), false);

            for (FinancialTransaction financialTransaction : financialTransactionsArrayList) {

                String tempTransactionLine = financialTransaction.toLine();
                if(financialTransaction instanceof FineTransaction){
                    tempTransactionLine = FINE_META_FLAG + DELIMITER + tempTransactionLine;
                } else if(financialTransaction instanceof PaymentTransaction){
                    tempTransactionLine = PAYMENT_META_FLAG + DELIMITER + tempTransactionLine;
                }
                tempTransactionLine = FINANCIAL_META_FLAG + DELIMITER + tempTransactionLine;

                transactionFile.write(tempTransactionLine);
                transactionFile.write(System.lineSeparator());
            }

            for (Transaction tempTransaction : transactionArrayList) {

                String tempTransactionLine = RESOURCE_META_FLAG + DELIMITER + tempTransaction.transactionToLine();

                transactionFile.write(tempTransactionLine);
                transactionFile.write(System.lineSeparator());
            }

            transactionFile.close();

        } catch (IOException ex) {

                System.out.println("File Write Error");
        }
    }


    /**
     * Gets the transaction ID of the last transaction in the system
     * @return ID of the last transaction
     */
    public static int getLastTransactionID () {
        if(transactionArrayList.size() == 0) {
            return 0;
        }
        Transaction lastTransaction = transactionArrayList.get(transactionArrayList.size()-1);

        return lastTransaction.getTRANSACTION_ID();
    }

    /**
     * Gets all transactions in the system
     * @return Arraylist of transaction instances
     */
    public static ArrayList getAllTransactions() {

        return transactionArrayList;
    }

    /**
     * Gets a specific transaction using the transaction ID
     * @return Transaction instance with the corresponding transaction ID
     */
    public static Transaction getSpecificTransaction(int transactionID) {

        Transaction transaction = null;

        for (Transaction tempTransaction : transactionArrayList) {

            if (tempTransaction.getTRANSACTION_ID() == transactionID) {

                transaction = tempTransaction;
            }
        }
        return transaction;
    }

    /**
     * Adds a new transaction to the database
     * @param username The username associated with the transaction
     * @param copyID The copy id associated with the transaction
     * @param isReserved If the item is reserved
     */
    public static Transaction addNewTransaction (String username, int resourceID, int copyID, Boolean isReserved) {

        int transactionID = getLastTransactionID()+1;
        Transaction newTransaction = new Transaction(transactionID, username, resourceID, copyID, isReserved);

        transactionArrayList.add(newTransaction);

        TransactionDatabase.writeFile();
        return newTransaction;
    }

    /**
     * Adds a new transaction to the database
     * @param username The username associated with the transaction
     */
    public static FineTransaction addNewFine(String username, float value, Copy c, int daysOverdue) {


        FineTransaction newTransaction = new FineTransaction(username, value, c, daysOverdue);
        financialTransactionsArrayList.add(newTransaction);

        TransactionDatabase.writeFile();
        return newTransaction;
    }

    /**
     * Adds a new transaction to the database
     * @param username The username associated with the transaction
     */
    public static PaymentTransaction addNewPayment(String username, float value) {

        PaymentTransaction newTransaction = new PaymentTransaction(username, value);
        financialTransactionsArrayList.add(newTransaction);

        TransactionDatabase.writeFile();
        return newTransaction;
    }


    public static ArrayList<FinancialTransaction> getUserFinancialTransactionHistory(String username) {

        ArrayList<FinancialTransaction> usersTransactions = new ArrayList<>();
        for (FinancialTransaction tempTransaction : financialTransactionsArrayList) {
            if (tempTransaction.getUSERNAME().equals(username)) {
                usersTransactions.add(tempTransaction);
            }
        }
        return usersTransactions;
    }

    /**
     * Gets all the transactions of a specific user
     * @param username username of the user
     * @return An arraylist of transactions
     */
    public static ArrayList<Transaction> getAllTransactionsUser (String username) {

        ArrayList<Transaction> usersTransactions = new ArrayList<>();

        for (Transaction tempTransaction : transactionArrayList) {

            if (tempTransaction.getUSERNAME().equals(username)) {

                usersTransactions.add(tempTransaction);
            }
        }

        return usersTransactions;
    }

    /**
     * Changes the reservation status of a transaction
     * @param username username of the user
     * @param copyID the copyID which the user has taken out
     * @param isReserved the reserve status of the transaction
     */
    public static void changeReservedStatusOfTransaction (String username, int copyID, Boolean isReserved) {

        Transaction transaction = null;

        for (Transaction tempTransaction : transactionArrayList) {

            System.out.println(tempTransaction.getUSERNAME());
            System.out.println(tempTransaction.getCOPY_ID());

            if (tempTransaction.getUSERNAME().equals(username) && tempTransaction.getCOPY_ID() == copyID) {

                transaction = tempTransaction;
            }


        }
        transaction.setReserved(isReserved);
        TransactionDatabase.writeFile();
    }

    /**
     * Converts a transaction array to a string
     * @param transactions the arraylist of transactions which you want to convert
     * @return The transaction array in string format
     */
    public static String transactionsArrayToString (ArrayList<Transaction> transactions) {

        String toString = "";

        for (Transaction tempTransaction : transactions) {

            toString = toString + "\n" + tempTransaction.toString();

        }
        return "TransactionDatabase: "
                + "\n" + toString;
    }

    /**
     * Closes the database saving all data to file.
     * MUST be called at the end of the program
     */
    public static void close(){
        TransactionDatabase.writeFile();
    }


    @Override
    public String toString() {

        String toString = "";

        for (Transaction tempTransaction : transactionArrayList) {

            toString = toString + "\n" + tempTransaction.toString();

        }
        return "TransactionDatabase: "
                + "\n" + toString;
    }

    /**
     * Tests the Transaction Database
     */
    public static void main (String[] args) {

        //Initialize database
        init();
        for(Transaction t: transactionArrayList) {
            System.out.println(t);
        }
        // Clear data for now
        transactionArrayList.clear();

        addNewTransaction("Jeff", 80, 2, false);
        addNewTransaction("Long ass name with some weird characters in it ?", Integer.MIN_VALUE,
                Integer.MAX_VALUE, true);
        Transaction t = addNewTransaction("", 0, 0, false);
        t.makeReturned();
    }
}
