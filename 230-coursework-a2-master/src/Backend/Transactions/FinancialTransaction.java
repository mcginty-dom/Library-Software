package Backend.Transactions;

import Backend.Databases.ResourceDatabase;
import Backend.Databases.TransactionDatabase;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class FinancialTransaction {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Used to format the date

    private final String USERNAME; // The username of the user associated with the transaction
    private final String TRANSACTION_DATE; // The date of which the transaction has occurred
    private final float VALUE;

    /**
     * Instantiates a Transaction for the first time
     * @param username The username associated with the transaction
     */
    public FinancialTransaction (String username, float value) {
        this.USERNAME = username;
        this.VALUE = value;

        LocalDateTime now = LocalDateTime.now(); // Returns the current date and time
        this.TRANSACTION_DATE = DATE_FORMAT.format(now); // Saves the transaction date in the above specified format
    }

    /**
     * Instantiates a Transaction. Used when importing from file when date and time has already been set.
     * @param username The username associated with the transaction
     * @param value The copy value associated with the transaction
     * @param date Date of the transaction, if it has already been set
     */
    public FinancialTransaction (String username, float value, String date) {
        this.USERNAME = username;
        this.VALUE = value;
        this.TRANSACTION_DATE = date;
    }

    public long startDateEpoch(){
        return dateEpoch(TRANSACTION_DATE);
    }

    private long dateEpoch(String date){
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, DATE_FORMAT);
        return zonedDateTime.toEpochSecond();
    }


    /**
     * @return Username of the user associated with the transaction
     */
    public String getUSERNAME() {

        return USERNAME;
    }

    /**
     * @return The value associated with the transaction
     */
    public float getVALUE() {

        return VALUE;
    }

    /**
     * @return The transaction date in the string format "yyyy/MM/dd HH:mm:ss"
     */
    public String getTRANSACTION_DATE() {

        return TRANSACTION_DATE;
    }

    /**
     * Converts a transaction object into a string line to be placed in the file
     * @return a transaction to a string
     */
    public String toLine() {
        String delim = TransactionDatabase.DELIMITER;
        String transactionLine =
                getUSERNAME() + delim +
                getVALUE() + delim +
                getTRANSACTION_DATE() + delim;
        return transactionLine;

    }

    /**
     * Converts a Transaction into a String.
     * @return A Transaction in String format
     */
    @Override
    public String toString() {
        return "Transaction{" +
                ", USERNAME='" + USERNAME + '\'' +
                ", VALUE='" + VALUE + "\'" +
                ", TRANSACTION_DATE='" + TRANSACTION_DATE + '\'' +
                '}';
    }

    public long getTRANSACTION_DATE_epoch() {
        LocalDateTime date = LocalDateTime.parse(TRANSACTION_DATE, DATE_FORMAT);
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli()/1000;
    }
}
