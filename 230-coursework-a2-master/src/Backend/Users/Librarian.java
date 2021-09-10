package Backend.Users;

import Backend.Resources.Copy;
import Backend.Resources.Resource;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * The librarian class.
 * @author Dominic McGinty
 */
public class Librarian extends User {
    private final String EMPLOYMENT_DATE;
    private int staffNumber;

    /**
     * Constructor for the librarian when created for the first time
     * @param username Username of the account
     * @param firstName First name of the librarian.
     * @param lastName Last name of the librarian.
     * @param mobileNumber Phone number of the librarian.
     * @param addressLine1 House number and street name of the librarian's residence.
     * @param addressLine2 District of the librarian's residence.
     * @param postTown Town of residence of the librarian.
     * @param postcode Postcode of the librarian.
     * @param profileImageLocation Location of the librarian's account image.
     * @param userCreationDate Creation date of the librarian's account.
     * @param employmentDate Employment date of the librarian.
     * @param staffNumber Staff number of the librarian.
     */
    public Librarian(User user, int staffNumber) {
        super(user.getUsername(),user.getFirstName(),user.getLastName(),user.getMobileNumber(),user.getEmailAddress(),
                user.getAddressLine1(), user.getAddressLine2(),user.getPostTown(),user.getPostcode(),user.getProfileImageLocation());

        // Used to format the date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Returns the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Saves the userCreationDate in the above specified format
        this.EMPLOYMENT_DATE = dtf.format(now);
        this.staffNumber=staffNumber;
    }

    /**
     * Constructor for the librarian when created for the first time
     * @param username Username of the account
     * @param firstName First name of the librarian.
     * @param lastName Last name of the librarian.
     * @param mobileNumber Phone number of the librarian.
     * @param addressLine1 House number and street name of the librarian's residence.
     * @param addressLine2 District of the librarian's residence.
     * @param postTown Town of residence of the librarian.
     * @param postcode Postcode of the librarian.
     * @param profileImageLocation Location of the librarian's account image.
     * @param userCreationDate Creation date of the librarian's account.
     * @param employmentDate Employment date of the librarian.
     * @param staffNumber Staff number of the librarian.
     */
    public Librarian(String username, String firstName, String lastName, String mobileNumber, String emailAddress,
                     String address1, String address2, String postTown, String postcode,
                     String profileImageLocation, int staffNumber) {
        super(username,firstName,lastName,mobileNumber,emailAddress,address1,address2,postTown,postcode,
                profileImageLocation);

        // Used to format the date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Returns the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Saves the userCreationDate in the above specified format
        this.EMPLOYMENT_DATE = dtf.format(now);
        this.staffNumber=staffNumber;
    }

    /**
     * Constructor for the librarian when imported in from a file (as the final variable
     * has already been set.
     * @param username Username of the account
     * @param firstName First name of the librarian.
     * @param lastName Last name of the librarian.
     * @param mobileNumber Phone number of the librarian.
     * @param addressLine1 House number and street name of the librarian's residence.
     * @param addressLine2 District of the librarian's residence.
     * @param postTown Town of residence of the librarian.
     * @param postcode Postcode of the librarian.
     * @param profileImageLocation Location of the librarian's account image.
     * @param USER_CREATION_DATE Creation date of the librarian's account.
     * @param EMPLOYMENT_DATE Employment date of the librarian.
     * @param staffNumber Staff number of the librarian.
     */
    public Librarian(String username, String firstName, String lastName, String mobileNumber, String emailAddress,
                     String addressLine1, String addressLine2, String postTown, String postcode,
                     String profileImageLocation, String USER_CREATION_DATE, float accountBalance, ArrayList<Copy> borrowedItems,
                     ArrayList<Copy> reservedItems, ArrayList<Resource> requestedItems, String EMPLOYMENT_DATE,
                     int staffNumber ) {
        super(username,firstName,lastName,mobileNumber,emailAddress,addressLine1,addressLine2,postTown,postcode,
                profileImageLocation,USER_CREATION_DATE, accountBalance, borrowedItems, reservedItems, requestedItems);

        this.EMPLOYMENT_DATE=EMPLOYMENT_DATE;
        this.staffNumber=staffNumber;
    }

    /**
     * Gets the employment date of the librarian.
     * @return Employment date of the librarian.
     */
    public String getEMPLOYMENT_DATE() {
        return this.EMPLOYMENT_DATE;
    }

    /**
     * Sets the staff number of the librarian.
     * @param staffNumber Staff number of the librarian.
     */
    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    /**
     * Gets the staff number of the librarian.
     * @return Staff number of the librarian.
     */
    public int getStaffNumber() {
        return this.staffNumber;
    }


    /**
     * Converts the librarian class into a string output.
     * @return Information about the librarian.
     */
    public String toString() {
        return "Librarian {" +
                "Username: " + getUsername() + '\n' +
                "Name: " + getFirstName() + " " + getLastName() + '\n' +
                "Mobile Number: '" + getMobileNumber() + '\n' +
                "Address: " + getAddressLine1() + " " + getAddressLine2() + '\n' +
                getPostTown() + '\n' + getPostcode() + '\n' +
                "Image Location: " + getProfileImageLocation() + '\n' +
                "Creation Date: " + getUSER_CREATION_DATE() + '\n' +
                "Employment Date: " + getEMPLOYMENT_DATE() + '\n' +
                "Staff Number: " + getStaffNumber() + '\n' +
                '}';
    }

}