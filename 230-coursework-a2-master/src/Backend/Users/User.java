package Backend.Users;

import Backend.Databases.ResourceDatabase;
import Backend.Databases.UserDatabase;
import Backend.Resources.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
/**
 * The user class.
 * @author Dominic McGinty
 */
public class User {

    private static final DateTimeFormatter CREATION_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String username;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String emailAddress;
    private String addressLine1;
    private String addressLine2;
    private String postTown;
    private String postcode;
    private String profileImageLocation;
    private final String USER_CREATION_DATE;
    private float accountBalance;
    private ArrayList<Copy> borrowedItems = new ArrayList<Copy>();
    private ArrayList<Copy> reservedItems = new ArrayList<Copy>();
    private ArrayList<Resource> requestedItems = new ArrayList<Resource>();


    /**
     * Constructor for the user when created for the first time.
     * @param username Username of the account.
     * @param firstName First name of the user.
     * @param lastName Last name of the user.
     * @param mobileNumber the mobile number of the user
     * @param emailAddress Email address of the user.
     * @param addressLine1 House number and street name of the librarian's residence.
     * @param addressLine2 District of the librarian's residence.
     * @param postTown Town of residence for the user.
     * @param postcode Postcode of the user.
     * @param profileImageLocation Location of the user's account image.
     */
    public User(String username, String firstName, String lastName, String mobileNumber, String emailAddress,
                String addressLine1, String addressLine2, String postTown, String postcode,
                String profileImageLocation) {

        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postTown = postTown;
        this.postcode = postcode;
        this.accountBalance = 0;
        this.profileImageLocation = profileImageLocation;
        // Used to format the date
        // Returns the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Saves the userCreationDate in the above specified format
        this.USER_CREATION_DATE = CREATION_DATE_FORMAT.format(now);
    }

    /**
     * Constructor for the user when imported in from a file (as the final variable has already
     * been set).
     * @param username Username of the account.
     * @param firstName First name of the user.
     * @param lastName Last name of the user.
     * @param mobileNumber Phone number of the user.
     * @param emailAddress Email address of the user.
     * @param addressLine1 House number and street name of the user's residence.
     * @param addressLine2 District of the user's residence.
     * @param postTown Town of residence for the user.
     * @param postcode Postcode of the user.
     * @param profileImageLocation Location of the user's account image.
     * @param userCreationDate Creation date of the user's account.
     * @param accountBalance Account balance of the user's account.
     * @param borrowedItems List of borrowed item's of the user's account.
     * @param reservedItems List of reserved item's of the user's account.
     * @param requestedItems List of requested item's of the user's account.
     */
    public User(String username, String firstName, String lastName, String mobileNumber, String emailAddress,
                String addressLine1, String addressLine2, String postTown, String postcode,
                String profileImageLocation, String USER_CREATION_DATE, float accountBalance,
                ArrayList<Copy> borrowedItems, ArrayList<Copy> reservedItems, ArrayList<Resource> requestedItems ) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postTown = postTown;
        this.postcode = postcode;
        this.profileImageLocation = profileImageLocation;
        this.USER_CREATION_DATE = USER_CREATION_DATE;
        this.accountBalance = accountBalance;
        this.borrowedItems = borrowedItems;
        this.reservedItems = reservedItems;
        this.requestedItems = requestedItems;
    }

    /**
     * Create a user from a librarian mirroring all shared fields. In effect removing a librarian's status
     * @param librarian the librarian to copy
     */
    public User(Librarian librarian) {
        this.username =             librarian.getUsername();
        this.firstName =            librarian.getFirstName();
        this.lastName =             librarian.getLastName();
        this.mobileNumber =         librarian.getMobileNumber();
        this.emailAddress =         librarian.getEmailAddress();
        this.addressLine1 =         librarian.getAddressLine1();
        this.addressLine2 =         librarian.getAddressLine2();
        this.postTown =             librarian.getPostTown();
        this.postcode =             librarian.getPostcode();
        this.profileImageLocation = librarian.getProfileImageLocation();
        this.USER_CREATION_DATE =   librarian.getUSER_CREATION_DATE();
        this.accountBalance =       librarian.getAccountBalance();
        this.borrowedItems =        librarian.getBorrowedItems();
        this.reservedItems =        librarian.getReservedItems();
        this.requestedItems =       librarian.getRequestedItems();
    }


    /**
     * Sets the username of the account.
     * @param username Username of the account.
     */
    public void setUsername(String username) {
        User check = UserDatabase.queryUserByUsername(username);
        if(check != null && ! this.username.equals(username)){
            throw new IllegalArgumentException("Username: " + username + " is already taken. You cannot set two users to the same name!");
        }
        this.username = username;
    }

    /**
     * Gets the username of the account.
     * @return Username of the account.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the first name of the user.
     * @param firstName First name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the first name of the user.
     * @return First name of the user.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the last name of the user.
     * @param lastName Last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the last name of the user.
     * @return Last name of the user.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the phone number of the user.
     * @param mobileNumber Phone number of the user.
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets the phone number of the user.
     * @return Phone number of the user.
     */
    public String getMobileNumber() {
        return this.mobileNumber;
    }

    /**
     * Sets the email address of the user.
     * @param emailAddress email address of the user.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the email address of the user.
     * @return email address of the user.
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /**
     * Sets the house number and street name of the user's residence.
     * @param addressLine1 House number and street name of the user's residence.
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the house number and street name of the user's residence.
     * @return House number and Street name of the user's residence.
     */
    public String getAddressLine1() {
        return this.addressLine1;
    }

    /**
     * Sets the district of the user's residence.
     * @param @param addressLine2 District of the user's residence.
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the district of the user's residence.
     * @return District of the user's residence.
     */
    public String getAddressLine2() {
        return this.addressLine2;
    }

    /**
     * Sets the town of residence for the user.
     * @param postTown Town of residence for the user.
     */
    public void setPostTown(String postTown) {
        this.postTown = postTown;
    }

    /**
     * Gets the town of residence for the user.
     * @return Town of residence for the user.
     */
    public String getPostTown() {
        return this.postTown;
    }

    /**
     * Sets the postcode of the user.
     * @param postcode Postcode of the user.
     */
    public void setPostcode(String postcode) {
        this.postTown = postTown;
    }

    /**
     * Gets the postcode of the user.
     * @return Postcode of the user.
     */
    public String getPostcode() {
        return this.postcode;
    }

    /**
     * Sets the location of the user's account image.
     * @param profileImageLocation Location of the user's account image.
     */
    public void setProfileImageLocation(String profileImageLocation) {
        this.profileImageLocation = profileImageLocation;
    }

    /**
     * Gets the location of the user's account image.
     * @return Location of the user's account image.
     */
    public String getProfileImageLocation() {
        return this.profileImageLocation;
    }

    /**
     * Gets the creation date of the user's account.
     * @return Creation date of the user's account.
     */
    public String getUSER_CREATION_DATE() {
        return this.USER_CREATION_DATE;
    }

    /**
     * Gets account balance of the user's account.
     * @return Value of accountBalance
     */
    public float getAccountBalance() {
        return accountBalance;
    }

    /**
     * Gets account balance of the user's account.
     * @return Value of accountBalance
     */
    public String getPrintableAccountBalance() {
        String printableBalance = String.format("£%.2f", Math.abs(accountBalance));
        if(accountBalance < 0){
            printableBalance = "-"+printableBalance;
        }
        return printableBalance;
    }

    /**
     * Gets the list of user's borrowed items.
     * @return List of user's borrowed items.
     */
    public ArrayList<Copy> getBorrowedItems() {
        return this.borrowedItems;
    }

    /**
     * Adds a copy of a resource into the user's borrowed items.
     * @param borrowedResource A copy of a resource.
     */
    public void addBorrowedItem(Copy borrowedResource) {
        borrowedItems.add(borrowedResource);
    }

    /**
     * Removes a copy of a resource from the user's borrowed items.
     * @param returnedResource A copy of a resource.
     */
    public void removeBorrowedItem(Copy returnedResource) {
        borrowedItems.remove(returnedResource);
    }

    /**
     * Sets the list of a user's borrowed items.
     * @param borrowedItems List of a user's borrowed items.
     */
    public void setBorrowedItems(ArrayList<Copy> borrowedItems) {
        this.borrowedItems = borrowedItems;
    }

    /**
     * Sets the list of a user's reserved items.
     * @param reservedItems List of a user's reserved items.
     */
    public void setReservedItems(ArrayList<Copy> reservedItems) {
        this.reservedItems = reservedItems;
    }

    /**
     * Sets the list of a user's requested items.
     * @param requestedItems List of a user's requested items.
     */
    public void setRequestedItems(ArrayList<Resource> requestedItems) {
        this.requestedItems = requestedItems;
    }

    /**
     * Gets the list of a user's requested items.
     * @return List of user's requested items.
     */
    public ArrayList<Resource> getRequestedItems() {
        return this.requestedItems;
    }

    /**
     * Adds a resource to into the user's requested items.
     * @param newResource A resource.
     */
    public void addRequest(Resource newResource) {
        requestedItems.add(newResource);
    }

    /**
     * Gets the list of a user's reserved items.
     * @return List of user's reserved items.
     */
    public ArrayList<Copy> getReservedItems() {
        return this.reservedItems;
    }

    /**
     * Adds a copy to into the user's reserved items.
     * @param c A Copy of a resource.
     */
    public void addReserved(Copy c) {
        reservedItems.add(c);
    }

    /**
     * Adds an amount to the user's balance to pay off their debt.
     * @param amount Money the user has inputted into the system.
     */
    public void addToBalance(float amount) {
        this.accountBalance += amount;
    }

    /**
     * Adds a debt to the user's balance.
     * @param amount Calculation of debt added the user's balance.
     */
    public void removeFromBalance(float amount) {
        this.accountBalance -= amount;
    }

    /**
     * Converts the user class into a string output.
     * @return Information about the user.
     */
    public String toString() {
        return "User {" +
                "Username: " + username + '\n' +
                "Name: " + firstName + " " + lastName + '\n' +
                "Mobile Number: '" + mobileNumber + '\n' +
                "Address: " + addressLine1 + " " + addressLine2 + '\n' + postTown + '\n' + postcode + '\n' +
                "Debt Accrued: " + (-accountBalance) + '\n' +
                "Image Location: " + profileImageLocation + '\n' +
                "Creation Date: " + USER_CREATION_DATE + '\n' +
                '}';
    }


    public long getUSER_CREATION_DATE_epoch() {
        LocalDate date = LocalDate.parse(USER_CREATION_DATE, CREATION_DATE_FORMAT);
        ZonedDateTime zonedDateTime = date.atStartOfDay().atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli()/1000;
    }

    /**
     * A check to see if a user has any overdue copies on their account
     * @return true if the user has one or more overdue copies on their account,
     * false otherwise.
     */
    public boolean hasOverdueItems() {
        for (Copy testCopy: borrowedItems) {
            if (testCopy.isOverdue()) {
                return true;
            }
        }
        for (Copy testCopy: reservedItems) {
            if (testCopy.isOverdue()) {
                return true;
            }
        }
        return false;
    }
}


