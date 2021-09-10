package Frontend.Tools;

import Backend.Transactions.Transaction;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

/**
 * A data model for storing resource transactions.
 * in a JavaFX tableview.
 * @author matt
 */
public class TransactionTableDataModel {

    private final SimpleStringProperty username;
    private final SimpleStringProperty transactionType;
    private final SimpleStringProperty date;

    /**
     * Create a table row initialized with data.
     * @param username the username on the transaction
     * @param transactionType the transaction type
     * @param date the date of the transaction
     */
    private TransactionTableDataModel(String username, String transactionType, String date){
        this.username = new SimpleStringProperty(username);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.date = new SimpleStringProperty(date);
    }

    /**
     * Get the transaction type: Reserved, Reservation Cancelled, Borrowed or Returned.
     * @return the transaction type.
     */
    public String getTransactionType() {
        return transactionType.get();
    }

    /**
     * Get the date of the transaction.
     * @return the transaction date
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Get the username associated with the transaction.
     * @return the username
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Get all rows from a transaction. As a transaction details both the taking and
     * receiving of a resource one transaction may make more than one row of data.
     * @param transaction the transaction to convert into table rows
     * @return a list of table rows.
     */
    public static ArrayList<TransactionTableDataModel> getData(Transaction transaction) {
        ArrayList<TransactionTableDataModel> data = new ArrayList<>();
        if (transaction.getReserved()) {
            data.add(new TransactionTableDataModel(transaction.getUSERNAME(), "Reserved", transaction.getTRANSACTION_DATE()));
            if (transaction.getReturnDate() != null) {
                data.add(new TransactionTableDataModel(transaction.getUSERNAME(), "Reservation Cancelled", transaction.getReturnDate()));
            }
        } else {
            data.add(new TransactionTableDataModel(transaction.getUSERNAME(), "Borrowed", transaction.getTRANSACTION_DATE()));
            if (transaction.getReturnDate() != null) {
                data.add(new TransactionTableDataModel(transaction.getUSERNAME(), "Returned", transaction.getReturnDate()));
            }
        }
        return data;
    }


}
