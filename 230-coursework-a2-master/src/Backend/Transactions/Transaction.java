package Backend.Transactions;
// TODO heirarchy?

/**
 * File Name: Transaction.java
 * Creation Date: 17/11/2018
 * Copyright: No Copyright
 *
 * @version 1.0
 * @author Ryan Lucas
 */

import Backend.Databases.ResourceDatabase;
import Backend.Databases.TransactionDatabase;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Purpose:
 * A transaction instance is made when a book is borrowed. It stores the user which has taken the
 * resource out, the specific copy instance of that resource and the date and time the transaction has
 * taken place.
 * Note: Transactions.txt are immutable once creates with the exception of their reserve status
 */
public class Transaction {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Used to format the date
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // Used to format the date

    private final int TRANSACTION_ID; // The unique transaction ID for the transaction
    private final String USERNAME; // The USERNAME of the user associated with the transaction
    private final int RESOURCE_ID; // The resource ID associated with the transaction
    private final int COPY_ID; // The copy of a resource associated with the transaction
    private final String TRANSACTION_DATE; // The date of which the transaction has occurred

    private String returnDate; // The date of which the transaction was returned
    private Boolean isReserved; // If the copy has been reserved

    /**
     * Instantiates a Transaction for the first time
     * @param transactionID The transaction ID of the transaction
     * @param USERNAME The USERNAME associated with the transaction
     * @param copyID The copyID associated with the transaction
     * @param isReserved If the item is reserved
     */
    public Transaction (int transactionID, String USERNAME, int resourceID, int copyID, Boolean isReserved) {

        this.TRANSACTION_ID = transactionID;
        this.USERNAME = USERNAME;
        this.RESOURCE_ID = resourceID;
        this.COPY_ID = copyID;
        this.isReserved = isReserved;

        LocalDateTime now = LocalDateTime.now(); // Returns the current date and time
        this.TRANSACTION_DATE = DATE_FORMAT.format(now); // Saves the transaction date in the above specified format

    }

    /**
     * Instantiates a Transaction. Used when importing from file when date and time has already been set.
     * @param transactionID The transaction ID of the transaction
     * @param USERNAME The USERNAME associated with the transaction
     * @param copyID The copy id associated with the transaction
     * @param isReserved If the item is reserved
     * @param date Date of the transaction, if it has already been set
     */
    public Transaction (int transactionID, String USERNAME, int resourceID, int copyID, Boolean isReserved, String date, String returnDate) {

        this.TRANSACTION_ID = transactionID;
        this.USERNAME = USERNAME;
        this.COPY_ID = copyID;
        this.RESOURCE_ID = resourceID;
        this.isReserved = isReserved;
        this.TRANSACTION_DATE = date; // Saves the transaction date in the above specified format
        this.returnDate = returnDate;
    }

    public long startDateEpoch(){
        return dateEpoch(TRANSACTION_DATE);
    }

    public long returnDateEpoch(){
        if(this.returnDate == null){
            return Long.MAX_VALUE;
        }
        return dateEpoch(returnDate);
    }

    private long dateEpoch(String date){
        LocalDateTime dateTime = LocalDateTime.parse(date, DATE_FORMAT);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli()/1000;
    }


    public boolean isActive() {
        Transaction copyActiveTransaction = ResourceDatabase.getResourceByID(RESOURCE_ID).getCopy(COPY_ID).getCurrentTransaction();
        if(copyActiveTransaction == null){
            return false;
        }
        return copyActiveTransaction.equals(this);
    }

    /**
     * @return The transaction ID of the transaction
     */
    public int getTRANSACTION_ID() {

        return TRANSACTION_ID;
    }

    /**
     * @return Username of the user associated with the transaction
     */
    public String getUSERNAME() {
        return USERNAME;
    }

    /**
     * @return The copy of the resource associated with the transaction
     */
    public int getCOPY_ID() {

        return COPY_ID;
    }
    /**
     * @return The resource associated with the transaction
     */
    public int getRESOURCE_ID() {
        return RESOURCE_ID;
    }

    /**
     * @return The transaction date in the string format "yyyy/MM/dd HH:mm:ss"
     */
    public String getTRANSACTION_DATE() {

        return TRANSACTION_DATE;
    }

    /**
     * @return The transaction date as a LocalDate object
     */
    public LocalDate getTransactionDateAsDate() {
        return LocalDateTime.parse(TRANSACTION_DATE, DATE_FORMAT).toLocalDate();
    }


    public String getOutputFormatTRANSACTION_DATE() {
        return LocalDateTime.parse(TRANSACTION_DATE, DATE_FORMAT).format(OUTPUT_DATE_FORMAT);
    }

    /**
     * @return The reserve status of the Copy associated with the transaction
     */
    public Boolean getReserved() {

        return isReserved;
    }

    /**
     * Sets the reservation status of the Copy associated with the transaction
     * @param reserveStatus The reserve status of the Copy associated with the transaction
     */
    public void setReserved(Boolean reserveStatus) {

        isReserved = reserveStatus;
    }

    /**
     * Adds a return date to this transaction set to the current system time
     */
    public void makeReturned() {
        LocalDateTime now = LocalDateTime.now(); // Returns the current date and time
        this.returnDate = DATE_FORMAT.format(now); // Saves the return date in the above specified format
    }

    /**
     * Gets the date that this transaction was returned or null if it never was
     * @return
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Converts a transaction object into a string line to be placed in the file
     * @return a transaction to a string
     */
    public String transactionToLine() {
        String delim = TransactionDatabase.DELIMITER;
        String transactonLine = getTRANSACTION_ID() + delim
                + getUSERNAME()
                + delim + getCOPY_ID() + delim + getRESOURCE_ID() + delim
                + getReserved() + delim
                + getTRANSACTION_DATE().split(" ")[0] + delim
                + getTRANSACTION_DATE().split(" ")[1] + delim
                + returnDate;

        return transactonLine;

    }

    /**
     * Converts a Transaction into a String.
     * @return A Transaction in String format
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "TRANSACTION_ID=" + TRANSACTION_ID +
                ", USERNAME='" + USERNAME + '\'' +
                ", RESOURCE_ID='" + RESOURCE_ID + '\'' +
                ", COPY_ID='" + COPY_ID + '\'' +
                ", TRANSACTION_DATE='" + TRANSACTION_DATE + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", isReserved=" + isReserved +
                '}';
    }


}
