package Backend.Databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import Backend.Resources.*;
import Backend.Transactions.Transaction;

/**
 * File Name: ResourceDatabase.java
 * Creation Date: 9/12/2018
 * Copyright: No Copyright
 *
 * @version 1.0
 * @author William Lovett
 */
public class ResourceDatabase {
    // TODO Verification of not null to allow resource removal
    public static final String DELIMITER = "\t";

    /**
     * The next unassigned resource ID
     */
    private static Integer nextResourceID = null;
    /**
     * A static arraylist of books
     */
    private static ArrayList<Book> book;
    /**
     * A static arraylist of dvds
     */
    private static ArrayList<DVD> dvd;
    /**
     * A static arraylist of laptops
     */
    private static ArrayList<Laptop> laptop;
    /**
     * The static File name for books
     */
    private static File bookFile = new File("src/data/Books.txt");
    /**
     * The static File name for dvds
     */
    private static File dvdFile = new File("src/data/DVDs.txt");
    /**
     * The static File name for laptops
     */
    private static File laptopFile = new File("src/data/Laptops.txt");


    /**
     * @param resourceId
     * @return r
     */
    public static Resource getResourceByID(int resourceId) {
        for (Resource r : getResources()) {
            if (r.getID() == resourceId) {
                return r;
            }
        }
        return null;
    }

    /**
     * @return Collections.unmodifiableCollection(resources)
     */
    private static Collection<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(book);
        resources.addAll(dvd);
        resources.addAll(laptop);
        return Collections.unmodifiableCollection(resources);
    }

    /**
     * Checks to see if a desired book is in the catalogue
     *
     * @param query String
     * @return desiredBook Book
     */
    public static ArrayList<Book> queryBook(String query) {
        return queryBook(query, book);
    }

    /**
     * Checks to see if a desired book is in the catalogue
     *
     * @param query String
     * @return desiredBook Book
     */
    public static ArrayList<Book> queryBook(String query, ArrayList<Book> books) {

        ArrayList<Book> queryResultB = new ArrayList<Book>();
        for (Book bookTrans : books) {
            if (partialMatch(query, false, true, Integer.toString(bookTrans.getID()), bookTrans.getTitle(),
                    Integer.toString(bookTrans.getYear()), bookTrans.getAuthor(), bookTrans.getPublisher(),
                    bookTrans.getGenre(), bookTrans.getISBN(), bookTrans.getLanguage())) {

                queryResultB.add(bookTrans);

            }

        }
        return queryResultB;
    }

    /** Checks to see if the query matches with any resource even if the query isn't in the right case
     * @param matchWith
     * @param caseSensitive
     * @param ignoreSpaces
     * @return Returns either true or false
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

    /**
     * Loads books into an ArrayList of Books
     *
     * @param in
     * @return book
     */
    private static ArrayList<Book> readBook(Scanner in) {
        book = new ArrayList<Book>();
        String str;
        String[] bookList;

        while (in.hasNext()) {
            str = in.nextLine();
            bookList = str.split(DELIMITER);
            int index = 0;

            String thumbnail = bookList[index++];
            int ID = Integer.parseInt(bookList[index++]);
            String title = bookList[index++];
            int year = Integer.parseInt(bookList[index++]);
            String minLoanDuration = bookList[index++];
            int maxCopyID = Integer.parseInt(bookList[index++]);
            String author = bookList[index++];
            String publisher = bookList[index++];
            String genre = bookList[index++];
            String iSBN = bookList[index++];
            String language = bookList[index++];
            int copyListSize = Integer.parseInt(bookList[index++]);
            int requestQueueSize = Integer.parseInt(bookList[index++]);

            ArrayList<String> requestQueue = readRequestQueue(requestQueueSize, index, bookList);

            Book newBook = new Book(thumbnail, ID, title, year, minLoanDuration, author, publisher, genre, iSBN, language, maxCopyID);

            ArrayList<Copy> copyList = readCopyLines(copyListSize, in, newBook);
            newBook.setCopyList(copyList);
            newBook.setRequestQueue(requestQueue);

            String[] reviewString = in.nextLine().split(DELIMITER);
            newBook.setReview(readReview(reviewString));

            book.add(newBook);
        }

        return book;

    }


    /**
     * Reads books in from the Book.txt file
     *
     * @return ResourceDatabase.readBook(in)
     */
    public static ArrayList<Book> readBookFile() {

        Scanner in = null;

        try {
            in = new Scanner(bookFile);

        } catch (FileNotFoundException e) {

            System.out.println("Cannot open: " + bookFile);

            System.exit(0);
        }

        return ResourceDatabase.readBook(in);

    }

    /**
     * Writes an ArrayList of Books to the Book.txt file
     *
     * @param newBook
     */
    public static void writeBookFile(ArrayList<Book> newBook) {

        FileWriter fw;

        try {

            fw = new FileWriter(bookFile, false);

            for (Book tempBook : newBook) {

                String tempBookLine = tempBook.getThumbnail() + DELIMITER + tempBook.getID() + DELIMITER
                        + tempBook.getTitle() + DELIMITER + tempBook.getYear()
                        + DELIMITER + tempBook.getMinLoanDuration() + DELIMITER + tempBook.getNextCopyID() + DELIMITER
                        + tempBook.getAuthor() + DELIMITER + tempBook.getPublisher() + DELIMITER + tempBook.getGenre()
                        + DELIMITER + tempBook.getISBN() + DELIMITER + tempBook.getLanguage() + DELIMITER
                        + tempBook.getCopyList().size() + DELIMITER + tempBook.getRequestQueue().size() + DELIMITER;

                for (int i = 0; i < tempBook.getRequestQueue().size(); i++) {
                    tempBookLine += tempBook.getRequestQueue().get(i);
                    if (i != tempBook.getRequestQueue().size() - 1) {
                        tempBookLine += DELIMITER;
                    }
                }

                // Copies
                for (int i = 0; i < tempBook.getCopyList().size(); i++) {
                    tempBookLine += System.lineSeparator();
                    tempBookLine += tempBook.getCopyList().get(i).toLine();
                }

                tempBookLine += System.lineSeparator();

                tempBookLine += tempBook.getReview().toLine();

                fw.write(tempBookLine);
                fw.write(System.lineSeparator());
            }

            fw.close();

        } catch (IOException ex) {

            System.out.println("File Write Error");
        }

    }

    /**
     * Removes a book from the ArrayList of books
     *
     * @param ripBook
     */
    public static void removeBook(Book ripBook) {
        for (int i = 0; i < book.size(); i++) {
            if (book.get(i).equals(ripBook)) {
                book.remove(i);
            }
        }

    }

    /**
     * Updates a book in the Book ArrayList
     *
     * @param oldBook a book to be removed from the database
     * @param newBook a book to take its place
     */
    public static void updateBook(Book oldBook, Book newBook) {
        for (int i = 0; i < book.size(); i++) {
            if (book.get(i).equals(oldBook)) {
                book.set(i, newBook);
            }
        }
    }

    /**
     * Adds a book to the Book ArrayList
     *
     * @param newBook
     */
    public static void addBook(Book newBook) {
        book.add(newBook);
    }

    ///////////////////////////////
    // DVD //
    ///////////////////////////////

    /**
     * Takes in a query for a dvd and passes it on to queryDVD(String , ArrayList<DVD> )
     *
     * @param query
     * @return queryDVD(query, dvd)
     */
    public static ArrayList<DVD> queryDVD(String query) {
        return queryDVD(query, dvd);
    }

    /**
     * Checks to see if a desired book is in the catalogue
     *
     * @param query
     * @param dvds
     * @return queryResultD
     */
    public static ArrayList<DVD> queryDVD(String query, ArrayList<DVD> dvds) {

        ArrayList<DVD> queryResultD = new ArrayList<DVD>();
        for (DVD dvdTrans : dvds) {
            if (partialMatch(query, false, true, Integer.toString(dvdTrans.getID()), dvdTrans.getTitle(),
                    Integer.toString(dvdTrans.getYear()), dvdTrans.getDirector(), dvdTrans.getLanguage(),
                    Integer.toString(dvdTrans.getRuntime()))) {
                queryResultD.add(dvdTrans);
            }

        }
        return queryResultD;
    }

    /**
     * Loads dvds into an ArrayList of DVDS
     *
     * @param in
     * @return dvd
     */
    private static ArrayList<DVD> readDVD(Scanner in) {
        dvd = new ArrayList<DVD>();

        String str;
        String[] dvdList;

        while (in.hasNext()) {
            str = in.nextLine();
            dvdList = str.split(DELIMITER);
            int index = 0;

            String thumbnailLocation = dvdList[index++];
            int ID = Integer.parseInt(dvdList[index++]);
            String title = dvdList[index++];
            int year = Integer.parseInt(dvdList[index++]);
            String minLoanDuration = dvdList[index++];
            int maxCopyID = Integer.parseInt(dvdList[index++]);
            String director = dvdList[index++];
            String language = dvdList[index++];

            int subtitlesLength = Integer.parseInt(dvdList[index++]);
            String[] subtitles = new String[subtitlesLength];
            for (int i = 0; i < subtitlesLength; i++) {
                subtitles[i] = dvdList[index++];
            }

            int runtime = Integer.parseInt(dvdList[index++]);
            int copyListSize = Integer.parseInt(dvdList[index++]);
            int requestQueueSize = Integer.parseInt(dvdList[index++]);
            ArrayList<String> requestQueue = readRequestQueue(requestQueueSize, index, dvdList);

            DVD newDVD = new DVD(thumbnailLocation, ID, title, year, minLoanDuration, director, language, subtitles, runtime, maxCopyID);

            ArrayList<Copy> copyList = readCopyLines(copyListSize, in, newDVD);
            newDVD.setCopyList(copyList);
            newDVD.setRequestQueue(requestQueue);

            String[] reviewString = in.nextLine().split(DELIMITER);
            newDVD.setReview(readReview(reviewString));


            dvd.add(newDVD);
        }
        return dvd;

    }

    /**
     * Reads in dvds from the DVD.txt file
     *
     * @return ResourceDatabase.readDVD(in)
     */
    public static ArrayList<DVD> readDVDFile() {

        Scanner in = null;

        try {
            in = new Scanner(dvdFile);

        } catch (FileNotFoundException e) {

            System.out.println("Cannot open: " + dvdFile);

            System.exit(0);
        }

        return ResourceDatabase.readDVD(in);

    }

    /**
     * Writes and ArrayList of DVD to the DVD.txt file
     *
     * @param newDVD
     */
    public static void writeDVDFile(ArrayList<DVD> newDVD) {

        FileWriter fw;

        try {

            fw = new FileWriter(dvdFile, false);

            for (DVD tempDVD : newDVD) {

                String tempDVDLine = tempDVD.getThumbnail() + DELIMITER + tempDVD.getID() + DELIMITER + tempDVD.getTitle() + DELIMITER + tempDVD.getYear() + DELIMITER
                        + tempDVD.getMinLoanDuration() + DELIMITER + tempDVD.getNextCopyID() + DELIMITER
                        + tempDVD.getDirector() + DELIMITER + tempDVD.getLanguage() + DELIMITER;

                tempDVDLine += tempDVD.getSubtitles().length;

                for (String language : tempDVD.getSubtitles()) {
                    tempDVDLine += DELIMITER + language;
                }

                tempDVDLine += DELIMITER + tempDVD.getRuntime() + DELIMITER + tempDVD.getCopyList().size() + DELIMITER + tempDVD.getRequestQueue().size() + DELIMITER;

                for (int i = 0; i < tempDVD.getRequestQueue().size(); i++) {
                    tempDVDLine += tempDVD.getRequestQueue().get(i);
                    if (i != tempDVD.getCopyList().size() - 1) {
                        tempDVDLine += DELIMITER;
                    }
                }

                for (int i = 0; i < tempDVD.getCopyList().size(); i++) {
                    tempDVDLine += System.lineSeparator();
                    tempDVDLine += tempDVD.getCopyList().get(i).toLine();
                }

                tempDVDLine += System.lineSeparator();

                tempDVDLine += tempDVD.getReview().toLine();

                fw.write(tempDVDLine);
                fw.write(System.lineSeparator());
            }

            fw.close();

        } catch (IOException ex) {

            System.out.println("File Write Error");
        }

    }

    /**
     * Removes a DVD from and ArrayList of the DVD
     *
     * @param ripDVD
     */
    public static void removeDVD(DVD ripDVD) {
        for (int i = 0; i < dvd.size(); i++) {
            if (dvd.get(i).equals(ripDVD)) {
                dvd.remove(i);
            }
        }

    }

    /**
     * Updates a DVD in the DVD ArrayList
     *
     * @param oldDVD the dvd to remove from the database
     * @param newDVD the dvd to add to the database
     */
    public static void updateDVD(DVD oldDVD, DVD newDVD) {
        for (int i = 0; i < dvd.size(); i++) {
            if (dvd.get(i).equals(oldDVD)) {
                dvd.set(i, newDVD);
            }
        }
    }

    /**
     * Adds a DVD to the ArrayList of DVDs
     *
     * @param newDvd
     */
    public static void addDvd(DVD newDvd) {
        dvd.add(newDvd);
    }

///////////////////////////////
//           Laptop          //
///////////////////////////////

    /**
     * Takes in a query for a laptop and passes it on to queryLaptop(String , ArrayList<Laptop> )
     *
     * @param query
     * @return queryLaptop(query, laptop)
     */
    public static ArrayList<Laptop> queryLaptop(String query) {
        return queryLaptop(query, laptop);
    }

    /**
     * Checks to see if a desired laptop is in the catalogue
     *
     * @param query
     * @return queryResult
     */
    public static ArrayList<Laptop> queryLaptop(String query, ArrayList<Laptop> laptops) {

        ArrayList<Laptop> queryResultL = new ArrayList<Laptop>();
        for (Laptop laptopTrans : laptops) {
            if (partialMatch(query, false, true, Integer.toString(laptopTrans.getID()), laptopTrans.getTitle(),
                    Integer.toString(laptopTrans.getYear()), laptopTrans.getManufacturer(), laptopTrans.getModel(),
                    laptopTrans.getOS())) {
                queryResultL.add(laptopTrans);

            }

        }
        return queryResultL;
    }

    /*
     * public Laptop queryLaptop(String key, String value) { System.out.
     * println("TODO: return a Backend.Resources.Laptop based off key and value");
     * return null; }
     */

    /**
     * Loads laptops into an ArrayList of Laptop
     *
     * @param in
     * @return Laptop
     */
    private static ArrayList<Laptop> readLaptop(Scanner in) {
        laptop = new ArrayList<Laptop>();

        String str;
        String[] laptopList;

        while (in.hasNext()) {
            str = in.nextLine();
            laptopList = str.split(DELIMITER);
            int index = 0;

            String thumbnailLocation = laptopList[index++];
            int ID = Integer.parseInt(laptopList[index++]);
            String title = laptopList[index++];
            int year = Integer.parseInt(laptopList[index++]);
            String minLoanDuration = laptopList[index++];
            int maxCopyID = Integer.parseInt(laptopList[index++]);
            String manufacturer = laptopList[index++];
            String model = laptopList[index++];
            String oS = laptopList[index++];
            int copyListSize = Integer.parseInt(laptopList[index++]);
            int requestQueueSize = Integer.parseInt(laptopList[index++]);
            ArrayList<String> requestQueue = readRequestQueue(requestQueueSize, index, laptopList);


            Laptop newLaptop = new Laptop(thumbnailLocation, ID, title, year, minLoanDuration, manufacturer, model, oS, maxCopyID);

            ArrayList<Copy> copyList = readCopyLines(copyListSize, in, newLaptop);
            newLaptop.setCopyList(copyList);
            newLaptop.setRequestQueue(requestQueue);

            String[] reviewString = in.nextLine().split(DELIMITER);
            newLaptop.setReview(readReview(reviewString));

            laptop.add(newLaptop);
        }
        return laptop;

    }

    /**
     * Reads in laptops from Laptop.txt file
     *
     * @return ResourceDatabase.readLaptop(in);
     */
    public static ArrayList<Laptop> readLaptopFile() {

        Scanner in = null;

        try {
            in = new Scanner(laptopFile);

        } catch (FileNotFoundException e) {

            System.out.println("Cannot open: " + laptopFile);

            System.exit(0);
        }

        return ResourceDatabase.readLaptop(in);

    }

    /**
     * Writes and ArrayList of laptops to the Laptop.txt file
     *
     * @param newLaptop
     */
    public static void writeLaptopFile(ArrayList<Laptop> newLaptop) {

        FileWriter fw;

        try {

            fw = new FileWriter(laptopFile, false);

            for (Laptop tempLaptop : newLaptop) {

                String tempLaptopLine = tempLaptop.getThumbnail() + DELIMITER + tempLaptop.getID() + DELIMITER + tempLaptop.getTitle() + DELIMITER + tempLaptop.getYear()
                        + DELIMITER + tempLaptop.getMinLoanDuration() + DELIMITER + tempLaptop.getNextCopyID()
                        + DELIMITER + tempLaptop.getManufacturer() + DELIMITER + tempLaptop.getModel()
                        + DELIMITER + tempLaptop.getOS() + DELIMITER + tempLaptop.getCopyList().size() + DELIMITER
                        + tempLaptop.getRequestQueue().size() + DELIMITER;

                for (int i = 0; i < tempLaptop.getRequestQueue().size(); i++) {
                    tempLaptopLine += tempLaptop.getRequestQueue().get(i);
                    if (i != tempLaptop.getCopyList().size() - 1) {
                        tempLaptopLine += DELIMITER;
                    }
                }

                for (int i = 0; i < tempLaptop.getCopyList().size(); i++) {
                    tempLaptopLine += System.lineSeparator();
                    tempLaptopLine += tempLaptop.getCopyList().get(i).toLine();
                }

                tempLaptopLine += System.lineSeparator();

                tempLaptopLine += tempLaptop.getReview().toLine();

                fw.write(tempLaptopLine);
                fw.write(System.lineSeparator());
            }

            fw.close();

        } catch (IOException ex) {

            System.out.println("File Write Error");
        }

    }

    /**
     * Removes a laptop from the arrayList of Laptops
     *
     * @param ripLaptop
     */
    public static void removeLaptop(Laptop ripLaptop) {
        for (int i = 0; i < laptop.size(); i++) {
            if (laptop.get(i).equals(ripLaptop)) {
                laptop.remove(i);
            }
        }

    }

    /**
     * Updates a laptop in the Laptop ArrayList
     *
     * @param newLaptop
     */
    public static void updateLaptop(Laptop newLaptop) {
        for (int i = 0; i < laptop.size(); i++) {
            if (laptop.get(i).equals(newLaptop)) {
                laptop.set(i, newLaptop);
            }
        }
    }

    /**
     * adds a Laptop to the ArrayList of Laptops
     *
     * @param newLaptop
     */
    public static void addLaptop(Laptop newLaptop) {
        laptop.add(newLaptop);
    }

    /**
     * Reads the Book.txt, DVD.txt, Laptop.txt file and fetches the copies
     *
     * @param copyListSize
     * @param in
     * @param resource
     * @return copyList
     */
    private static ArrayList<Copy> readCopyLines(int copyListSize, Scanner in, Resource resource) {
        ArrayList<Copy> copyList = new ArrayList<>();
        for (int i = 0; i < copyListSize; i++) {
            String str = in.nextLine();
            String[] copyStringArray = str.split(DELIMITER);
            //ID available duedate currentTransactionId LenHistory History...
            int index = 0;

            int copyID = Integer.parseInt(copyStringArray[index++]);
            boolean isAvailable = Boolean.valueOf(copyStringArray[index++]);
            String dueDate = copyStringArray[index++];
            String currentTransactionId = copyStringArray[index++];
            Transaction currentTransaction;
            if (currentTransactionId.equals("null")) {
                currentTransaction = null;
            } else {
                currentTransaction = TransactionDatabase.getSpecificTransaction(Integer.parseInt(currentTransactionId));
            }
            if (dueDate.equals("null")) {
                dueDate = null;
            }
            int historyLength = Integer.parseInt(copyStringArray[index++]);
            List<Transaction> history = new ArrayList<>();
            for (int j = index; j < index + historyLength; j++) {
                history.add(TransactionDatabase.getSpecificTransaction(Integer.parseInt(copyStringArray[j])));
            }
            copyList.add(new Copy(resource, isAvailable, currentTransaction, history, dueDate, copyID));
        }
        return copyList;
    }

    /** Sees who's next on the queue of requests for a resource
     * @param requestQueueSize
     * @param index
     * @param usernameList
     * @return requestQueue
     */
    private static ArrayList<String> readRequestQueue(int requestQueueSize, int index, String[] usernameList) {
        ArrayList<String> requestQueue = new ArrayList<>();
        System.out.println(Arrays.toString(usernameList) + "(" + index + ")" + requestQueueSize);
        for (int i = index; i < index + requestQueueSize; i++) {
            requestQueue.add(usernameList[i]);
        }
        return requestQueue;
    }

    /**
     * Reads in the user reviews from the Resource files
     *
     * @param reviewList
     * @return Review(elements)
     */
    private static Review readReview(String[] reviewList) {
        int length = Integer.parseInt(reviewList[0]);
        int index = 1;

        ArrayList<Element> elements = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int rating = Integer.parseInt(reviewList[index++]);
            String reviewText = reviewList[index++];
            String poster = reviewList[index++];
            elements.add(new Element(rating, reviewText, poster));
        }
        return new Review(elements);
    }

    /**
     * Get a new unassigned resource ID.
     * This method is designed to NEVER return the same value twice.
     *
     * @return a unique integer.
     */
    public static int getNextResourceID() {
        if (nextResourceID == null) {
            nextResourceID = 0;
            for (Resource r : getResources()) {
                if (r.getID() >= nextResourceID) {
                    nextResourceID = r.getID() + 1;
                }
            }
        }
        return nextResourceID++;
    }


    /**
     * Loads the Datbase when the program is started
     */
    public static void init() {
        double ms = System.currentTimeMillis();
        readBookFile();
        readDVDFile();
        readLaptopFile();
        System.out.println(
                String.format("Resource Database Loaded. %d Books, %d DVDs, %d Laptops on file. In %.2f ms",
                        book.size(), dvd.size(), laptop.size(), System.currentTimeMillis() - ms));
    }

    /**
     * Saves the database when the program is closed
     */
    public static void close() {
        writeBookFile(book);
        writeDVDFile(dvd);
        writeLaptopFile(laptop);
    }

    public static void main(String... args) {// Load all data from file
        TransactionDatabase.init();
        init();

        // Empty arrays for testing purposes
        try {
            System.out.println("BOOK         >" + queryBook("title").get(0));
            System.out.println("BOOK REVIEW >>" + queryBook("title").get(0).getReview().getReviewList().get(0));
            System.out.println("BOOK QUEUE >>>" + Arrays.toString(queryBook("title").get(0).getRequestQueue().toArray(new String[0])));

            System.out.println("Number of books: " + queryBook("title").size());

            System.out.println("BOOK2 History >" + getResourceByID(421).getCopy(0).getHistory());

            System.out.println("DVD         >" + queryDVD("movie").get(0));
            System.out.println("DVD REVIEW >>" + queryDVD("movie").get(0).getReview().getReviewList().get(0));
            System.out.println("DVD QUEUE >>>" + Arrays.toString(queryDVD("movie").get(0).getRequestQueue().toArray(new String[0])));

            System.out.println("LAPTOP         >" + queryLaptop("laptop").get(0));
            System.out.println("LAPTOP QUEUE >>>" + Arrays.toString(queryLaptop("laptop").get(0).getRequestQueue().toArray(new String[0])));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        book.clear();
        dvd.clear();
        laptop.clear();

        // Create a new book
        Book book = new Book("Thumbnail Location", 420, "Book title", 2018, Resource.ONE_DAY,
                "Author's name", "Publisher's name", "Genre", "ISBN", "English");

        addBook(book);
        book.createAndAddCopy();
        book.createAndAddCopy();
        book.getCopy(0).setCurrentTransaction(TransactionDatabase.addNewTransaction("Jeff",
                book.getID(), 0, false));

        book.getReview().addReview("This book makes literally no sense", 5, "Jeff");
        book.getReview().addReview("I agree with jeff", 0, "Not jeff");
        book.getRequestQueue().addAll(Arrays.asList("Alphie", "Betty", "Charlie", "Donnie", "Egbert"));

        Book book2 = new Book("Another thumbnail", 421, "Title of the book", 2017, Resource.FOUR_WEEKS,
                "J. K. Rowling", "Publisher", "Fantasy", "1111", "German");

        addBook(book2);
        book2.createAndAddCopy();
        book2.getCopy(0).setCurrentTransaction(TransactionDatabase.addNewTransaction("Jeff",
                book2.getID(), 0, false));
        book2.getCopy(0).pushCurrentTransactionToHistory();

        DVD dvd = new DVD("Thumbnail Location", 422, "Some movie", 1998, Resource.TWO_WEEKS,
                "Director", "English", new String[]{"English", "Spanish", "Klingon"}, 91);

        addDvd(dvd);
        dvd.createAndAddCopy();

        dvd.getReview().addReview("This dvd is getting reviewed", 2, "Sally");


        Laptop laptop = new Laptop("Thumbnail Location", 422, "Some laptop", -5, Resource.ONE_DAY,
                "Manufacturer", "Model", "Linux");

        addLaptop(laptop);


        close();
    }

}
