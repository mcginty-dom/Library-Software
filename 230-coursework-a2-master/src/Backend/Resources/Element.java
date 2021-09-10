package Backend.Resources;

import Backend.Databases.ResourceDatabase;
import Backend.Users.User;

public class Element {
	private int rating;
	private String reviewText = "";
	private String postedBy;

	/**
	 * constructor for the element that goes into the review list
	 * @param rating
	 * @param reviewText
	 * @param postedBy
	 */
	public Element (int rating, String reviewText, String postedBy) {
		this.rating = rating;
		this.reviewText = reviewText;
		this.postedBy = postedBy;
	}
	
	/**
	 * gets the rating of the element
	 * @return rating of the element
	 */
	public int getRating() {
		return rating;
	}
	
	/**
	 * sets the rating of the element
	 * @param rating of the element
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	/**
	 * gets the review string of the element
	 * @return review string of the element
	 */
	public String getReviewText() {
		return reviewText;
	}
	
	/**
	 * sets the review string of the element
	 * @param review string of the element
	 */
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	
	/**
	 * gets the the user that the element is posted by
	 * @return review string of the element
	 */
	public String getPostedBy() {
		return postedBy;
	}
	/**
	 * sets the user that posted of the element
	 * @param user that posted the element
	 */
	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	@Override
	public String toString(){
		return postedBy + ":\n\t" + reviewText + "\nRating: " + rating;
	}

    public String toLine() {
    	return Integer.toString(rating) + ResourceDatabase.DELIMITER + reviewText + ResourceDatabase.DELIMITER + postedBy;
	}
}