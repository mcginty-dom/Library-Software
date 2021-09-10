package Frontend.Tools;

import Backend.Databases.ResourceDatabase;
import Backend.Transactions.FineTransaction;
import Backend.Transactions.PaymentTransaction;
import Backend.Transactions.Transaction;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

/**
 * A data model for storing financial transactions.
 * in a JavaFX tableview.
 * @author matt
 */
public class FinancialTransactionTableDataModel {

    private final SimpleStringProperty transactionType;
    private final SimpleStringProperty value;
    private final SimpleStringProperty date;
    private final SimpleStringProperty resource;
    private final SimpleStringProperty copyNumber;
    private final SimpleStringProperty daysOverdue;

    /**
     * Create a new row in the table initialised as a fine transaction.
     * @param fineTransaction the fine to display in the table.
     */
    public FinancialTransactionTableDataModel(FineTransaction fineTransaction){
        this.transactionType = new SimpleStringProperty("Fine");
        this.date = new SimpleStringProperty(fineTransaction.getTRANSACTION_DATE());
        this.value = new SimpleStringProperty(String.format("-£%.2f", fineTransaction.getVALUE()));
        this.resource = new SimpleStringProperty(ResourceDatabase.getResourceByID(fineTransaction.getRESOURCE_ID()).getTitle());
        this.copyNumber = new SimpleStringProperty(Integer.toString(fineTransaction.getCOPY_ID()));
        this.daysOverdue = new SimpleStringProperty(Integer.toString(fineTransaction.getDAYS_OVERDUE()));
    }

    /**
     * Create a new row in the table initialised as a payment transaction.
     * @param paymentTransaction the payment to display in the table.
     */
    public FinancialTransactionTableDataModel(PaymentTransaction paymentTransaction){
        this.transactionType = new SimpleStringProperty("Payment");
        this.date = new SimpleStringProperty(paymentTransaction.getTRANSACTION_DATE());
        this.value = new SimpleStringProperty(String.format("£%.2f", paymentTransaction.getVALUE()));
        this.resource = new SimpleStringProperty("-");
        this.copyNumber = new SimpleStringProperty("-");
        this.daysOverdue = new SimpleStringProperty("-");
    }

    /**
     * Get the transaction type: fine, or payment.
     * @return transaction type
     */
    public String getTransactionType() {
        return transactionType.get();
    }

    /**
     * Get the transaction date.
     * @return the transaction date
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Get the value of the transaction with a leading "£".
     * @return the value of the transaction
     */
    public String getValue() {
        return value.get();
    }

    /**
     * Get the resource the transaction references.
     * @return the resource
     */
    public String getResource() {
        return resource.get();
    }

    /**
     * Get the copy number the transaction references.
     * @return the copy number
     */
    public String getCopyNumber() {
        return copyNumber.get();
    }

    /**
     * Get how many days overdue an item was.
     * @return days overdue.
     */
    public String getDaysOverdue() {
        return daysOverdue.get();
    }


}
