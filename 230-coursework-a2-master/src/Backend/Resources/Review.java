package Backend.Resources;

import Backend.Databases.ResourceDatabase;

import java.util.ArrayList;

/**
 * @author Krystian
 *
 */

public class Review {
    /**
     * @param rating average of all reviews for a resource.
     * @param reviewList the list of all the review for a resource.
     */
	private float rating = 0;
	private ArrayList<Element> reviewList = new ArrayList<Element>();
	
	/*
	 * constructor for the review class automatically called when a resource is made
	 */
	public Review(ArrayList<Element> reviewList){
		if(reviewList == null){
			reviewList = new ArrayList<>();
		}
		this.reviewList = reviewList;
		updateRating();
	}
	
    /**
     * updates the average rating buy calculating the average
     * @param rating average of all reviews for a resource.
     */
	private void updateRating() {
		float total = 0;
		for(int i = 0 ; i < reviewList.size(); i++) {
			total = total + reviewList.get(i).getRating();
		}
		rating = total/ (float) reviewList.size();
	}
	
    /**
     * adds a review to the list of reviews, doesn't allow multiple reviews by the same person.
     * @param Element an individual review made by a user.
     */
	public void addReview(Element element) {
		if (checkUser(element.getPostedBy())) {
			reviewList.add(element);
			updateRating();
		}else{
			System.out.println("Already reviewed by you");
		}
	}
	
    /**
     * adds a review to the list of reviews, doesn't allow multiple reviews by the same person.
     * @param Element an individual review made by a user.
     */
	public void addReview(String reviewText, int rating, String userName){
		addReview(new Element(rating, reviewText, userName));
	}
	
    /**
     * checks if the user trying to add a review already reviewed the item.
     * @param user that reviewed the item.
     */
	private boolean checkUser (String newUser) {
		for (int i = 0 ; i < reviewList.size(); i++) {
			if (newUser.equals(reviewList.get(i).getPostedBy())) {
				return false;
			}
		}
		return true;
		
	}
	
    /**
     * gets the average rating if there are any returns -1 if there are no reviews.
     * @return rating of the of the resource.
     */
	public float getRating() {
		if(reviewList.size() == 0){
			return -1;
		}
		return rating;
	}
	
    /**
     * sets the average rating.
     * @param rating of the of the resource.
     */
	public void setRating(float rating) {
		this.rating = rating;
	}
	
    /**
     * gets the review list of the resource.
     * @return review list.
     */
	public ArrayList<Element> getReviewList() {
		return reviewList;
	}
    /**
     * sets the review list.
     * @param review list with all the elements.
     */
	public void setReviewList(ArrayList<Element> reviewList) {
		this.reviewList = reviewList;
	}

	public String toLine(){
		String line = Integer.toString(reviewList.size());
		for (Element e : reviewList) {
			line += ResourceDatabase.DELIMITER;
			line += e.toLine();
		}
		return line;
	}


}