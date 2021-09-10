package Backend.Databases;

import java.util.ArrayList;
import java.util.Scanner;

import Backend.Resources.Book;
import Backend.Resources.Copy;
import Backend.Resources.Resource;
import Backend.Users.Librarian;
import Backend.Users.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * File Name: UserDatabase.java
 * Creation Date: 9/12/2018
 * Copyright: No Copyright
 *
 * @version 1.0
 * @author William Lovett
 */
public class UserDatabase {

    private static final String DELIMITER = "\t";
    private static final String USER_META_FLAG = "U";
    private static final String LIBRARIAN_META_FLAG = "L";

    /**
     * The next unassigned librarian ID
     */
    private static Integer nextStaffID = null;

    /**
     * A static File of Users
     */
    private static File file = new File("src/data/Users.txt");
    /**
     * A static ArrayList of Users
     */
    private static File config_file = new File("src/data/UserDatabaseMeta.txt");
    /**
     *
     */
    private static ArrayList<User> users;


    /**  Searches through the ArrayList of Users for a specific user by userName
     * @param query
     * @return userTrans
     */
    public static User queryUserByUsername(String query) {
        for (User userTrans : users) {
            if (userTrans.getUsername().equals(query)) {
                return userTrans;
            }

        }
        return null;
    }

    /** Takes in the selected query and passes it on to queryUser (String, ArrayList<User>)
     * @param query
     * @return queryUser(query, users)
     */
    public static ArrayList<User> queryUser(String query) {
        return queryUser(query, users);
    }

    /**Searches through the ArrayList of Users for a specific user
     * @param query
     * @param users
     * @return queryResultU
     */
    public static ArrayList<User> queryUser(String query, ArrayList<User> users) {

        ArrayList<User> queryResultU = new ArrayList<User>();
        for (User userTrans : users) {
            if (partialMatch(query, false, true, userTrans.getUsername(), userTrans.getFirstName(),
                    userTrans.getLastName(), userTrans.getMobileNumber(), userTrans.getAddressLine1(),
                    userTrans.getAddressLine2(), userTrans.getPostTown(), userTrans.getPostcode(),
                    userTrans.getUSER_CREATION_DATE())) {

                queryResultU.add(userTrans);

            }

        }
        return queryResultU;

    }

    /** Checks to see if the query matches with any users even if the query isn't in the right case
     * @param matchWith
     * @param caseSensitive
     * @param ignoreSpaces
     * @param candidates
     * @return returns either true or false
     */
    private static boolean partialMatch(String matchWith, boolean caseSensitive, boolean ignoreSpaces, String... candidates) {
        if (!caseSensitive) {
            matchWith = matchWith.toLowerCase();
        }
        if (ignoreSpaces) {
            matchWith = matchWith.replaceAll(" ", "");
        }

        for (String candidate : candidates) {
            if (!caseSensitive) {
                candidate = candidate.toLowerCase();
            }
            if (ignoreSpaces) {
                candidate = candidate.replaceAll(" ", "");
            }
            if (candidate.contains(matchWith)) {
                return true;
            }
        }
        return false;
    }

    /** Takes in a Scanner of user entries and creates an ArrayList of Users
     * @param in
     * @return users
     * @throws ParseException
     */
    private static ArrayList<User> loadUsers(Scanner in) throws ParseException {
        users = new ArrayList<User>();
        // username firstName lastName 12 1 streetName postTown postcode
        // profileImageLocation 1999-10-10
        String str;
        String[] userList;

        while (in.hasNext()) {
            str = in.nextLine();
            System.out.println(str);
            userList = str.split(DELIMITER);
            int index = 0;

            String userType = userList[index++];
            String username = userList[index++];
            String firstName = userList[index++];
            String lastName = userList[index++];
            String mobileNumber = userList[index++];
            String emailAddress = userList[index++];
            String addressLine1 = userList[index++];
            String addressLine2 = userList[index++];
            String postTown = userList[index++];
            String postcode = userList[index++];
            String profileImageLocation = userList[index++];
            String userCreationDate = userList[index++];
            float accountBalance = Float.parseFloat(userList[index++]);

            ArrayList<Copy> borrowedItems = readCopies(in.nextLine().split(DELIMITER));
            ArrayList<Copy> reservedItems = readCopies(in.nextLine().split(DELIMITER));
            ArrayList<Resource> requestedItems = readResources(in.nextLine().split(DELIMITER));

            User newUser;
            if (userType.equals(LIBRARIAN_META_FLAG)) {
                String employmentDate = userList[index++];
                int staffNumber = Integer.parseInt(userList[index++]);
                newUser = new Librarian(username, firstName, lastName, mobileNumber, emailAddress, addressLine1, addressLine2, postTown,
                        postcode, profileImageLocation, userCreationDate, accountBalance, borrowedItems, reservedItems,
                        requestedItems, employmentDate, staffNumber);
            } else {
                newUser = new User(username, firstName, lastName, mobileNumber, emailAddress, addressLine1, addressLine2, postTown,
                        postcode, profileImageLocation, userCreationDate, accountBalance, borrowedItems, reservedItems, requestedItems);

            }

            users.add(newUser);
        }

        return users;

    }

    /** manages the resources associated with a user
     * @param split
     * @return resources
     */
    private static ArrayList<Resource> readResources(String[] split) {
        int length = Integer.parseInt(split[0]);
        int index = 1;
        ArrayList<Resource> resources = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            int resourceId = Integer.parseInt(split[index++]);
            resources.add(ResourceDatabase.getResourceByID(resourceId));
        }
        return resources;
    }

    /**manages the copies associated with a user
     * @param split
     * @return copies
     */
    private static ArrayList<Copy> readCopies(String[] split) {
        int length = Integer.parseInt(split[0]);
        int index = 1;
        ArrayList<Copy> copies = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            int resourceId = Integer.parseInt(split[index++]);
            int copyId = Integer.parseInt(split[index++]);
            copies.add(ResourceDatabase.getResourceByID(resourceId).getCopy(copyId));
        }
        return copies;
    }

    /**
     * Reads the Users from the User.txt file and passes to the load User class
     * @return UserDatabase.loadUsers(in)
     * @throws ParseException
     */
    private static ArrayList<User> readUserFile() throws ParseException {
        Scanner in = null;

        try {
            in = new Scanner(file);

        } catch (FileNotFoundException e) {

            System.out.println("Cannot open");

            System.exit(0);
        }

        return UserDatabase.loadUsers(in);
    }

    /**Writes the User ArrayList to the User.txt file
     * @param user
     */
    public static void saveDatabase(ArrayList<User> user) {

        FileWriter fw;

        try {

            fw = new FileWriter(file, false);

            for (User tempUser : user) {

                String tempUserLine = tempUser.getUsername() + DELIMITER + tempUser.getFirstName() + DELIMITER
                        + tempUser.getLastName() + DELIMITER + tempUser.getMobileNumber() + DELIMITER + tempUser.getEmailAddress() + DELIMITER + tempUser.getAddressLine1()
                        + DELIMITER + tempUser.getAddressLine2() + DELIMITER + tempUser.getPostTown() + DELIMITER + tempUser.getPostcode()
                        + DELIMITER + tempUser.getProfileImageLocation() + DELIMITER
                        + tempUser.getUSER_CREATION_DATE() + DELIMITER + String.format("%.2f", tempUser.getAccountBalance());

                if (tempUser instanceof Librarian) {
                    tempUserLine = LIBRARIAN_META_FLAG + DELIMITER + tempUserLine;
                    Librarian tempLib = (Librarian) tempUser;
                    tempUserLine += DELIMITER + tempLib.getEMPLOYMENT_DATE() + DELIMITER + tempLib.getStaffNumber();
                } else {
                    tempUserLine = USER_META_FLAG + DELIMITER + tempUserLine;
                }

                tempUserLine += System.lineSeparator();

                tempUserLine += tempUser.getBorrowedItems().size();

                for (Copy c : tempUser.getBorrowedItems()) {
                    tempUserLine += DELIMITER + c.getResource().getID() + DELIMITER + c.getID();
                }

                tempUserLine += System.lineSeparator();

                tempUserLine += tempUser.getReservedItems().size();

                for (Copy c : tempUser.getReservedItems()) {
                    tempUserLine += DELIMITER + c.getResource().getID() + DELIMITER + c.getID();
                }

                tempUserLine += System.lineSeparator();

                tempUserLine += tempUser.getRequestedItems().size();

                for (Resource r : tempUser.getRequestedItems()) {
                    tempUserLine += DELIMITER + r.getID();
                }

                fw.write(tempUserLine);
                fw.write(System.lineSeparator());
            }

            fw.close();


        } catch (IOException ex) {

            System.out.println("File Write Error");
        }

    }

    /** Removes a User from the User ArrayList
     * @param ripUser
     */
    public static void removeUser(User ripUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(ripUser)) {
                users.remove(i);
            }
        }
    }

    /** Changes the data in an element of the User ArrayList
     * @param oldUser
     * @param newUser
     */
    public static void updateUser(User oldUser, User newUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(oldUser)) {
                users.set(i, newUser);
            }
        }
    }

    /** Adds a User to the ArrayList of Users
     * @param newUser
     */
    public static void addUser(User newUser) {
        users.add(newUser);
    }


    /**Gets an unassigned staff ID.
     * This method is designed to NEVER return the same value twice.
     * @return a unique integer.
     */
    public static int nextStaffID() {
        if (nextStaffID == null) {
            nextStaffID = 0;
            for (User u : users) {
                if (u instanceof Librarian) {
                    int staffNum = ((Librarian) u).getStaffNumber();
                    if (staffNum > nextStaffID)
                        nextStaffID = staffNum + 1;
                }
            }
        }
        nextStaffID++;
        return nextStaffID;
    }

    /** Loads the Datbase when the program is started
     * @throws ParseException
     */
    public static void init() throws ParseException {
        double ms = System.currentTimeMillis();
        readConfigFile();
        readUserFile();
        System.out.println(
                String.format("User Database Loaded. %d Users on file. In %.2f ms",
                        users.size(), System.currentTimeMillis() - ms));

    }

    /**
     * Loads resource ids
     */
    private static void readConfigFile() {
        nextStaffID = 0;
        if (!config_file.exists()) {
            return;
        }
        Scanner in = null;
        try {
            in = new Scanner(config_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        in.nextLine();
        nextStaffID = Integer.parseInt(in.nextLine());
    }

    /** Saves the current resource id
     *
     */
    private static void writeConfigFile() {
        FileWriter fw;
        try {
            fw = new FileWriter(config_file, false);
            fw.write("Next Staff ID:");
            fw.write(System.lineSeparator());
            fw.write(nextStaffID.toString());
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Saves the database when the program is closed
     */
    public static void close() {
        saveDatabase(users);
        writeConfigFile();
    }

    public static void main(String[] args) {

        try {
            TransactionDatabase.init();
            ResourceDatabase.init();
            init();
            System.out.println(queryUser("xxx").get(0).getBorrowedItems().get(0));
            System.out.println(queryUser("lib"));
            users.clear();
        } catch (Exception e) {
            System.out.println(e);
        }

        Book testResource = new Book("Thumbnail", -900, "Title", 1997, Resource.ONE_DAY, "Author", "Publisher", "Genre", "ISBN", "English");
        Copy copy = testResource.createAndAddCopy();
        ResourceDatabase.addBook(testResource);

        User user = new User("Username_XxX", "Tim", "Lastnameson", "+447123456789", "@nothing", "Some address like who really knows", "", "Scunthorpe", "AB1 2CD", "Path to a file");
        user.addToBalance(8.91f);
        user.addBorrowedItem(copy);

        Librarian librarian = new Librarian("Librarian", "Goeff", "Smith", "+447987654321", "gmail?", "Address mcAddressFace", "", "Swansea", "XY9 8ZW", "Location", 11);
        addUser(user);
        addUser(librarian);

        close();
        ResourceDatabase.close();

    }
}
