package Backend.Resources;

import Backend.Databases.UserDatabase;
import Backend.Library;
import Backend.Users.User;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Krystian
 *
 */
public abstract class Resource {

	public static final String ONE_DAY = "1 Day";
	public static final String ONE_WEEK = "1 Week";
	public static final String TWO_WEEKS = "2 Weeks";
	public static final String FOUR_WEEKS = "4 Weeks";


	private static final DateTimeFormatter EXPECTED_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");


	private int ID;
	private int nextCopyID;

	private String title;
	private String thumbnail;
	private int year;
	private Review review = new Review(null);
	private String minLoanDuration;

	private ArrayList<Copy> copyList = new ArrayList<>();
	private ArrayList<String> requestQueue = new ArrayList<String>();

	public Resource (String thumbnail, int ID, String title, int year, String minLoanDuration) {
		this(thumbnail, ID, title, year, minLoanDuration,0);
	}

	/**
	 * Constructor for loading from file. Should not be used to initialize a resource that is not being loaded from
	 * a file
	 * @param thumbnail String leading to the location of the thumbnail
	 * @param ID is the unique identifier for the resource
	 * @param title the name of the resource
	 * @param year the year the resource is from
	 * @param minLoanDuration the minimum time the resource is allowed to be taken out for
	 * @param nextCopyID the ID for the next copy of the resource if one is added
	 */
	public Resource (String thumbnail, int ID, String title, int year, String minLoanDuration, int nextCopyID) {
		this.thumbnail = thumbnail;
		this.ID = ID;
		this.title = title;
		this.year = year;
		this.minLoanDuration = minLoanDuration;
		this.nextCopyID = nextCopyID;
	}

    /**
     * Gets the ID of the Resource.
     * @return ID of the Resource.
     */
	public int getID() {
		return ID;
	}

    /**
     * Sets the ID of the Resource.
     * @param ID of the Resource.
     */
	public void setID(int iD) {
		ID = iD;
	}
    /**
     * Gets the Title of the Resource.
     * @return Title of the Resource.
     */
	public String getTitle() {
		return title;
	}
    /**
     * Sets the Title of the Resource.
     * @param Title of the Resource.
     */
	public void setTitle(String title) {
		this.title = title;
	}
    /**
     * Gets the Thumbnail String of the Resource.
     * @return Thumbnail of the Resource.
     */
	public String getThumbnail() {
		return thumbnail;
	}
    /**
     * Sets the Thumbnail String of the Resource.
     * @param Thumbnail of the Resource.
     */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
    /**
     * Gets the Year of the Resource.
     * @return Year of the Resource.
     */
	public int getYear() {
		return year;
	}
    /**
     * Sets the Year of the Resource.
     * @param Year of the Resource.
     */
	public void setYear(int year) {
		this.year = year;
	}
    /**
     * Gets the Array list of Copies of the Resource.
     * @return Array list of Copies of the Resource.
     */
	public ArrayList<Copy> getCopyList() {
		return copyList;
	}
    /**
     * Sets the Array list of Copies of the Resource.
     * @param Array list of Copies of the Resource.
     */
	public void setCopyList(ArrayList<Copy> copyList) {
		this.copyList = copyList;
	}

    /**
     * Gets the CopyID of the Copy of the Resource.
     * @return CopyID of the Copy of the Resource or null.
     */
	public Copy getCopy(int copyID){
		for(Copy c: copyList){
			if(c.getID() == copyID){
				return c;
			}
		}
		return null;
	}

    /**
     * Creates a new copy of the Resource and adds it to the copyList.
     * @return the copy of the resource.
     */
	public Copy createAndAddCopy(){
		Copy copy = new Copy(this, nextCopyID++);
		copyList.add(copy);
		return copy;
	}
    /**
     * Gets the Request queue of the Resource.
     * @return requestQueue of the Resource.
     */
	public ArrayList<String> getRequestQueue() {
		return requestQueue;
	}
    /**
     * Sets the Request queue of the Resource.
     * @param requestQueue of the Resource.
     */
	public void setRequestQueue(ArrayList<String> requestQueue) {
		this.requestQueue = requestQueue;
	}
    /**
     * Gets the Review class linked with of the Resource.
     * @return review of the Resource.
     */
	public Review getReview() {
		return review;
	}
    /**
     * sets the Review class linked with of the Resource.
     * @param review of the Resource.
     */
	public void setReview(Review review) {
		this.review = review;
	}
    /**
     * Gets the Minimum loan duration of the Resource.
     * @return minLoanDuration of the Resource.
     */
	public String getMinLoanDuration() {
		return minLoanDuration;
	}

	public void setMinLoanDuration(String minLoanDuration) {
		this.minLoanDuration = minLoanDuration;
	}

    /**
     * Gets the Next Copy ID of the Resource.
     * @return nextCopyID of the Resource.
     */
	public int getNextCopyID() {
		return nextCopyID;
	}

	/**
	 * Get the number of copies in the library that are currently available to be borrowed. That is,
	 * the number of copies which are not borrowed or reserved
	 * @return
	 */
    public int getNumAvailableCopies() {
    	int count = 0;
    	for(Copy c: copyList){
    		if(c.isAvailable()){
    			count++;
			}
		}
        return count;
    }

	public String getTypeString() {
		if(this instanceof Book){
			return "Book";
		} else if(this instanceof DVD){
			return "DVD";
		} else if(this instanceof Laptop){
			return "Laptop";
		}
		return null;
	}

	/**
	 * Returns true if a user has already requested this resource and it has not yet been reserved for them
	 * @param user the user to check
	 * @return true if the user has requested this resource and not received it yet, false otherwise
	 */
	public boolean isRequestedBy(User user){
    	return requestQueue.contains(user.getUsername());
	}

	/**
	 * The latest date that a copy will be available if no more requests are made, and all resources are
	 * returned exactly on their due date. If a copy is already available the date is the current date.
	 *
	 * @return
	 */
	public String getExpectedAvailableDate() {
		if(copyList.size() == 0){
			return null;
		}

		// Iterate through each copy to find how many days until each one is available
		ArrayList<Long> timesUntilAvailable = new ArrayList<>();
		for(Copy copy: copyList){
			if(copy.isAvailable()){
				// A copy is already available, return now
				return EXPECTED_DATE_FORMAT.format(LocalDateTime.now());
			}

			if(copy.getDueDate() == null){
				// If the copy is has no due date treat it as full duration
				timesUntilAvailable.add((long) daysInMinimumDuration(copy.getResource().getMinLoanDuration()));
			} else {
				// If the copy is overdue, limit this to 0
				timesUntilAvailable.add(Math.max(0, copy.getDaysUntilDue()));
			}
		}
		// Set the list in order
		timesUntilAvailable.sort(Long::compareTo);

		// How many full return/request cycles will take place
		int cycles = Math.floorDiv(requestQueue.size(), copyList.size());
		int offset = requestQueue.size() % copyList.size();
		int partialWaitTime = cycles * daysInMinimumDuration(getMinLoanDuration());
		int fullWaitTime = (int) (partialWaitTime + timesUntilAvailable.get(offset));

		return EXPECTED_DATE_FORMAT.format(LocalDateTime.now().plusDays(fullWaitTime));
	}


	public long getExpectedAvailableDateEpoch() {
		String expectedAvailableDate = getExpectedAvailableDate();
		if(expectedAvailableDate == null){
			return Long.MAX_VALUE;
		}
		LocalDate dateTime = LocalDate.parse(expectedAvailableDate, EXPECTED_DATE_FORMAT);
		ZonedDateTime zonedDateTime = dateTime.atStartOfDay(ZoneId.systemDefault());
		return zonedDateTime.toInstant().toEpochMilli()/1000;
	}

	/**
	 * Conversion from Resource minimum duration to days, will convert any of the time constants provided
	 * by the Resource class. Any other values will return -1
	 * @param minLoanDuration a mimimum loan duration as a String
	 * @return how many days that string represents (or -1 if the String is invalid)
	 */
	public static int daysInMinimumDuration(String minLoanDuration){
		switch (minLoanDuration){
			case ONE_DAY:
				return 1;
			case ONE_WEEK:
				return 7;
			case TWO_WEEKS:
				return 14;
			case FOUR_WEEKS:
				return 28;
			default:
				return -1;
		}
	}

	/**
	 * Disconnect a copy from a resource. By extension this will remove the copy entirely from system
	 * with the exception of any history in associated transactions.
	 *
	 * Copies which are actively being borrowed or are reserved CANNOT be removed.
	 *
	 * @param copy the copy to remove from this resource
	 */
	public void removeCopy(Copy copy){
		if(copy.isAvailable() && !copy.isReserved())
		this.copyList.remove(copy);
	}

	/**
	 * Adds the user to the end of this resource's request queue, if there is an available copy this will reserve
	 * that copy for this user.
	 * @param user the user requesting the resource
	 */
	public void request(User user){
		requestQueue.add(user.getUsername());
		updateRequests();
	}

	/**
	 * Check if the request queue can be moved forwards, this should be performed any time a copy becomes available or
	 * a user requests a copy
	 */
	public void updateRequests(){
		for(Copy c: copyList){
			if(requestQueue.size() > 0) {
				String username = requestQueue.get(0);
				if (c.isAvailable()) {
					Library.reserveCopy(c, UserDatabase.queryUserByUsername(username));
					requestQueue.remove(0);
				}
			}
		}
		// If there are more requests than available copies
		if(requestQueue.size() > 0){
			List<Copy> copyListClone = new ArrayList<>(copyList);
			Collections.sort(copyListClone, (o1, o2) -> {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Used to format the date
                LocalDateTime o1Time = LocalDateTime.parse(o1.getCurrentTransaction().getTRANSACTION_DATE(), dtf);
                LocalDateTime o2Time = LocalDateTime.parse(o2.getCurrentTransaction().getTRANSACTION_DATE(), dtf);
                long o1Epoch = o1Time.atZone(ZoneId.systemDefault()).toEpochSecond();
                long o2Epoch = o2Time.atZone(ZoneId.systemDefault()).toEpochSecond();
                return Long.compare(o1Epoch, o2Epoch);
            });
			// copyListClone is now in ascending order of transaction time
			for(int i = 0; i < requestQueue.size() && i < copyListClone.size(); i++){
				// Add a due date to each item in the copy list that does not have one already
				if(copyListClone.get(i).getDueDate() == null) {
					copyListClone.get(i).setDueDate();
				}
			}
		}

	}

    public abstract float getOverdueDayRate();

	public abstract float getMaxCharge();

}