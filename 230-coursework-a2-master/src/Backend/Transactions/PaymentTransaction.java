package Backend.Transactions;

public class PaymentTransaction extends FinancialTransaction {

    /**
     * Instantiates a Transaction for the first time
     *
     * @param username      The username associated with the transaction
     * @param value
     */
    public PaymentTransaction(String username, float value) {
        super(username, value);
    }

    /**
     * Instantiates a Transaction. Used when importing from file when date and time has already been set.
     *
     * @param username      The username associated with the transaction
     * @param value         The copy value associated with the transaction
     * @param date          Date of the transaction, if it has already been set
     */
    public PaymentTransaction(String username, float value, String date) {
        super(username, value, date);
    }
}
