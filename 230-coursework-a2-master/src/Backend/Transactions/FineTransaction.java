package Backend.Transactions;

import Backend.Databases.TransactionDatabase;
import Backend.Resources.Copy;
import Backend.Resources.Resource;

public class FineTransaction extends FinancialTransaction {


    private final int RESOURCE_ID;
    private final int COPY_ID;
    private final int DAYS_OVERDUE;

    /**
     * Instantiates a Transaction for the first time
     *
     *
     * @param username      The username associated with the transaction
     * @param value
     */
    public FineTransaction(String username, float value, Copy copy, int daysOverdue) {
        super(username, value);
        this.RESOURCE_ID = copy.getResource().getID();
        this.COPY_ID = copy.getID();
        this.DAYS_OVERDUE = daysOverdue;
    }

    /**
     * Instantiates a Transaction. Used when importing from file when date and time has already been set.
     *
     * @param username      The username associated with the transaction
     * @param value         The copy value associated with the transaction
     * @param date          Date of the transaction, if it has already been set
     */
    public FineTransaction(String username, float value, int copyID, int resourceID, int daysOverdue, String date) {
        super(username, value, date);
        this.RESOURCE_ID = resourceID;
        this.COPY_ID = copyID;
        this.DAYS_OVERDUE = daysOverdue;
    }


    /**
     * Converts a transaction object into a string line to be placed in the file
     * @return a transaction to a string
     */
    @Override
    public String toLine() {
        String delim = TransactionDatabase.DELIMITER;
        String transactionLine = super.toLine();
        transactionLine +=
                        getCOPY_ID() + delim +
                        getRESOURCE_ID() + delim +
                        getDAYS_OVERDUE();
        return transactionLine;

    }

    /**
     * Gets RESOURCE_ID
     *
     * @return value of RESOURCE_ID
     */
    public int getRESOURCE_ID() {
        return RESOURCE_ID;
    }

    /**
     * Gets COPY_ID
     *
     * @return value of COPY_ID
     */
    public int getCOPY_ID() {
        return COPY_ID;
    }

    /**
     * Gets DAYS_OVERDUE
     *
     * @return value of DAYS_OVERDUE
     */
    public int getDAYS_OVERDUE() {
        return DAYS_OVERDUE;
    }
}
