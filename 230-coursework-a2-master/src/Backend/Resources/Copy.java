package Backend.Resources;

//import Backend.Transactions.Transaction;

import Backend.Databases.ResourceDatabase;
import Backend.Databases.UserDatabase;
import Backend.Transactions.Transaction;
import Backend.Users.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Copy {

    private final int ID;
    private final Resource resource;

    private boolean isAvailable = true;

    private Transaction currentTransaction = null;
    // A chronological history of all transactions associated with this copy. It is assumed that this will have earlier transactions at lower indices
    private List<Transaction> history = new ArrayList<>();

    private static final DateTimeFormatter DUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String dueDate = null;


    public Copy(Resource resource, int ID) {
        this.resource = resource;
        this.ID = ID;
    }

    public Copy(Resource resource, boolean isAvailable, Transaction currentTransaction, List<Transaction> history,
                String dueDate, int ID) {
        this.resource = resource;
        this.isAvailable = isAvailable;
        this.currentTransaction = currentTransaction;
        this.history.addAll(history);
        this.dueDate = dueDate; // Format dd/MM/yyyy
        this.ID = ID;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isReserved() {
        if(isAvailable || currentTransaction == null){
            return false;
        }
        return currentTransaction.getReserved();
    }

    public User getReservedFor() {
        if(currentTransaction == null || !currentTransaction.getReserved()){
            return null;
        }
        return UserDatabase.queryUserByUsername(currentTransaction.getUSERNAME());
    }


    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        if(currentTransaction == null){
            throw new IllegalArgumentException("Transactions can not be set to null, to clear the transaction use pushCurrentTransactionToHistory!");
        }
        isAvailable = false;
        this.currentTransaction = currentTransaction;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate() {
        LocalDate startDate = currentTransaction.getTransactionDateAsDate();
        long borrowedFor = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        int plusDays;
        if (borrowedFor >= Resource.daysInMinimumDuration(resource.getMinLoanDuration())) {
            // if it's been borrowed for longer than its allowed
            plusDays = 1;
        } else {
            plusDays = (int) (Resource.daysInMinimumDuration(resource.getMinLoanDuration()) - borrowedFor);
        }

        this.dueDate = DUE_DATE_FORMATTER.format(LocalDate.now().plusDays(plusDays));
    }

    public void clearDueDate() {
        this.dueDate = null;
    }


    public Resource getResource() {
        return resource;
    }

    public int getID() {
        return ID;
    }

    /**
     * Return a READ-ONLY version of this copy's history
     * @return a collection of transactions representing the chronological history of this copy
     * (where smaller indices are older transactions)
     */
    public List<Transaction> getHistory(){
        return history;
    }

    /**
     * Serialize copy to be saved in a file //todo improve
     *
     * ID available duedate currentTransactionId LenHistory History...
     *
     * @return
     */
    public String toLine(){
        String delim = ResourceDatabase.DELIMITER;
        String historyString = "";
        for (int i = 0; i < history.size(); i++) {
            historyString += history.get(i).getTRANSACTION_ID();
            if(i != history.size() - 1){
                historyString += delim;
            }
        }
        String currentTransactionID = "null";
        if(currentTransaction != null){
            currentTransactionID = Integer.toString(currentTransaction.getTRANSACTION_ID());
        }
        String dueDate = "null";
        if(this.dueDate != null){
            dueDate = this.dueDate;
        }

        return ID + delim + isAvailable + delim + dueDate + delim + currentTransactionID + delim +
                history.size() + delim + historyString;
    }


    /**
     * Saves the current transaction to the copy's history.
     */
    public void pushCurrentTransactionToHistory() {
        if(currentTransaction != null) {
            history.add(currentTransaction);
            currentTransaction = null;
        }
        isAvailable = true;
    }

    @Override
    public String toString(){
        return "COPY OF: " + resource.toString() + "\nINDEX: " + getID() + "\nAvailable: " + isAvailable + "\t" + "Current transaction: " + currentTransaction + "\t" + "History Length: " + history.size();
    }

    /**
     * The overdue status of the copy. If the due date is in the past this will be true.
     * If the copy is not on loan this will always be false
     * @return
     */
    public boolean isOverdue() {
        return getDaysUntilDue() < 0;
    }

    /**
     * Query how many days until a copy will be available if it is returned exactly on its due date.
     * If the copy is already available this method will return 0
     * If the copy is overdue this value will be negative.
     * @return a Long value representing the days from the time of calling to the due date of the copy
     */
    public Long getDaysUntilDue() {
        if(dueDate == null){
            return 0L;
        }
        // Returns the current date and time
        LocalDate now = LocalDate.now();
        LocalDate timeDue = LocalDate.parse(dueDate, DUE_DATE_FORMATTER);
        // Calculate how many days between now and the dueDate
        return ChronoUnit.DAYS.between(now, timeDue);
    }


    public float getOverdueCharge() {
        return Math.min(getResource().getOverdueDayRate() * (-getDaysUntilDue()), getResource().getMaxCharge());
    }

    public long getDueDateEpoch(){
        if(dueDate == null){
            return Long.MAX_VALUE;
        }

        LocalDate date = LocalDate.parse(dueDate, DUE_DATE_FORMATTER);
        ZonedDateTime zonedDateTime = date.atStartOfDay().atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli()/1000;
    }

}