package Backend;

import Backend.Databases.TransactionDatabase;
import Backend.Databases.UserDatabase;
import Backend.Resources.Copy;
import Backend.Resources.Resource;
import Backend.Users.Librarian;
import Backend.Users.User;

/**
 * The library is a static controller of the essential functions of a library and allows a high level
 * interaction with the resources and users that the library is composed of.
 *
 * @author Matt
 */
public class Library {

    /**
     * Issues the given copy of a resource to a user.
     *
     * @param user the user wishing to borrow the copy.
     * @param copy the copy of the resource to borrow.
     */
    public static void issueResource(User user, Copy copy){

        if (user.getAccountBalance() < 0) {
            throw new IllegalStateException(String.format("User %s has insufficient funds!", user.getUsername()));
        } else {
            System.out.println("Issuing");
            copy.pushCurrentTransactionToHistory();
            user.addBorrowedItem(copy);
            copy.setCurrentTransaction(TransactionDatabase.addNewTransaction(user.getUsername(), copy.getResource().getID(), copy.getID(), false));
        }

    }



    /**
     * Requests the next available copy of a given resource for a user, A user may only request a resource that
     * they have not already requested (although may request a resource if they have a reserved copy)
     *
     * @param user the user wishing to borrow the copy.
     * @param resource the resource being requested.
     */
    public static void requestResource(User user, Resource resource){
        if(!resource.isRequestedBy(user)){
            resource.request(user);
            user.addRequest(resource);
        }
    }

    /**
     * Reserves a copy for a user. This performs no checks to see if a resource can be reserved.
     * @param c the copy of a resource to be reserved
     * @param user the user to reserve a copy for
     */
    public static void reserveCopy(Copy c, User user) {
        c.setCurrentTransaction(TransactionDatabase.addNewTransaction(user.getUsername(), c.getResource().getID(), c.getID(), true));
        user.addReserved(c);
    }

    /**
     * Return a copy of a resource to the library
     *
     * @param copy the copy to return.
     */
    public static void returnCopy(Copy copy){
        System.out.println("Returning copy " + copy);
        float overdueCharge = copy.getOverdueCharge();
        User borrowingUser = UserDatabase.queryUserByUsername(copy.getCurrentTransaction().getUSERNAME());
        if(overdueCharge > 0) {
            addFine(borrowingUser, overdueCharge, copy, (int) -copy.getDaysUntilDue());
        }

        copy.clearDueDate();
        copy.getCurrentTransaction().makeReturned();
        copy.pushCurrentTransactionToHistory();

        copy.getResource().updateRequests();
    }

    /**
     * Removes a reservation made by a user without issuing it to the user. This may incur a fee on the user's account if the reservation was overdue
     * @param copy
     */
    public static void cancelReservation(Copy copy) {
        /*
        This code is practically identical to return copy (at time of writing). This is so changes can be made to either
        but are referenced accordingly
         */
        float overdueCharge = copy.getOverdueCharge();
        User borrowingUser = UserDatabase.queryUserByUsername(copy.getCurrentTransaction().getUSERNAME());
        if(overdueCharge > 0) {
            addFine(borrowingUser, overdueCharge, copy, (int) -copy.getDaysUntilDue());
        }

        copy.clearDueDate();
        copy.getCurrentTransaction().makeReturned();
        copy.pushCurrentTransactionToHistory();

        copy.getResource().updateRequests();
    }

    /**
     * Removes a user's request to borrow a resource from the queue, and updates the queue accordingly
     * @param resource the resource to stop requesting
     * @param user the user terminating the request
     */
    public static void cancelRequest(Resource resource, User user) {
        resource.getRequestQueue().remove(user.getUsername());
        resource.updateRequests();

    }

    /**
     * Add a user to the system.
     * @param user the user to add.
     */
    public static void addUser(User user){
        System.out.println("Not implemented");
    }

    /**
     * Remove a user from the system.
     * @param user the user to remove from the system.
     */
    public static void removeUser(User user){
        System.out.println("Not implemented");
    }


    /**
     * Turn a user into a librarian in the system. This assigns them the next available staff number and
     * sets their employment date to now. If the user is already a Librarian this will do nothing
     * @param user the user to promote.
     */
    public static Librarian promoteToLibrarian(User user) {
        if(user instanceof Librarian){
            return (Librarian) user;
        }
        Librarian promoted = new Librarian(user, UserDatabase.nextStaffID());
        UserDatabase.updateUser(user, promoted);
        return promoted;
    }

    /**
     * Remove a users's librarian status if they are a librarian. If not this does nothing.
     * @param user the user to remove status from.
     */
    public static User revokeLibrarian(User user) {
        if(user instanceof Librarian){
            return revokeLibrarian((Librarian) user);
        }
        return user;
    }

    /**
     * Remove a librarian's status.
     * @param librarian the librarian to remove status from.
     */
    public static User revokeLibrarian(Librarian librarian){
        User revoked = new User(librarian);
        UserDatabase.updateUser(librarian, revoked);
        return revoked;
    }


    public static void makePayment(User user, float payment){
        assert (payment >= 0);
        TransactionDatabase.addNewPayment(user.getUsername(), payment);
        user.addToBalance(payment);
    }

    public static void addFine(User user, float fine, Copy copy, int daysOverdue){
        assert (fine >= 0);
        TransactionDatabase.addNewFine(user.getUsername(), fine, copy, daysOverdue);
        user.removeFromBalance(fine);
    }

}
