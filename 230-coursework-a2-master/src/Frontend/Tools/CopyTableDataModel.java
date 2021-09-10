package Frontend.Tools;

import Backend.Databases.ResourceDatabase;
import Backend.Resources.Copy;
import Backend.Transactions.Transaction;
import javafx.beans.property.SimpleStringProperty;

/**
 * A data model for storing active transactions
 * in a JavaFX tableview.
 * @author matt
 */
public class CopyTableDataModel {

    private final SimpleStringProperty title;
    private final SimpleStringProperty type;
    private final SimpleStringProperty startDate;
    private final SimpleStringProperty dueDate;
    private final SimpleStringProperty overdue;
    private final Copy copy;

    /**
     * Create an element of the table using a transaction, initialising all fields.
     * @param t the transaction to load.
     */
    public CopyTableDataModel(Transaction t) {
        Copy c = ResourceDatabase.getResourceByID(t.getRESOURCE_ID()).getCopy(t.getCOPY_ID());
        this.copy = c;
        this.overdue = new SimpleStringProperty(Boolean.toString(c.isOverdue()));
        String transactionType;
        if (t.getReserved()) {
            transactionType = "Reserved";
        } else {
            transactionType = "Borrowed";
        }
        this.title = new SimpleStringProperty(c.getResource().getTitle());
        this.type = new SimpleStringProperty(transactionType);
        this.startDate = new SimpleStringProperty(t.getOutputFormatTRANSACTION_DATE());

        String dueDate = c.getDueDate();
        if (dueDate == null) {
            dueDate = "-";
        }
        this.dueDate = new SimpleStringProperty(dueDate);
    }

    /**
     * Get the title of the resource.
     * @return the title of the resource
     */
    public String getTitle() {
        return title.get();
    }


    /**
     * Get the type of the resource.
     * @return the title of the resource
     */
    public String getType() {
        return type.get();
    }


    /**
     * Get the start date of the resource.
     * @return the start date of the resource
     */
    public String getStartDate() {
        return startDate.get();
    }


    /**
     * Get the due date of the resource.
     * @return the due date of the resource
     */
    public String getDueDate() {
        return dueDate.get();
    }


    /**
     * Get the copy referenced in the table.
     * @return the title of the book
     */
    public Copy getCopy() {
        return copy;
    }


    /**
     * Get the title of the book.
     * @return the title of the book
     */
    public String getOverdue() {
        return overdue.get();
    }
}
