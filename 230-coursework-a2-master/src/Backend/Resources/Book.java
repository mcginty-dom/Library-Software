package Backend.Resources;

public class Book extends Resource {

	private static final float OVERDUE_DAY_RATE = 2.f;
	private static final float MAX_OVERDUE_RATE = 25.f;

	private String author;
	private String publisher;
	private String genre;
	private String ISBN;
	private String language;

	/**
	 * @param author that the book is written by.
	 * @param publisher tat the book is pubished by.
	 * @param genre of the book.
	 * @param ISBN of the book.
	 * @param language the book is written in.
	 */

	public Book(String thumbnail, int ID, String title, int year, String minLoanDuration, String author, String publisher, String genre, String iSBN,
			String language) {
		super(thumbnail, ID, title, year, minLoanDuration);
		this.author = author;
		this.publisher = publisher;
		this.genre = genre;
		this.ISBN = iSBN;
		this.language = language;
	}

	/**
	 * Constructor for loading from file. This should not be used to create a resource not being loaded from a file
	 */

	public Book(String thumbnail, int ID, String title, int year, String minLoanDuration, String author,
				String publisher, String genre, String iSBN, String language, int maxCopyID) {
		super(thumbnail, ID, title, year, minLoanDuration, maxCopyID);
		this.author = author;
		this.publisher = publisher;
		this.genre = genre;
		this.ISBN = iSBN;
		this.language = language;
	}
    /**
     * Gets the author of the book.
     * @return author that the book is written by.
     */
	public String getAuthor() {
		return author;
	}
    /**
     * Sets the author of the book.
     * @param author that the book is written by.
     */
	public void setAuthor(String author) {
		this.author = author;
	}
    /**
     * Gets the publisher of the book.
     * @return author that the book is written by.
     */
	public String getPublisher() {
		return publisher;
	}
    /**
     * Sets the publisher of the book.
     * @param author that the book is written by.
     */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
    /**
     * Gets the genre of the book.
     * @return genre of the book.
     */
	public String getGenre() {
		return genre;
	}
    /**
     * Sets the genre of the book.
     * @param genre of the book.
     */
	public void setGenre(String genre) {
		this.genre = genre;
	}
    /**
     * Gets the ISBN of the book.
     * @return ISBN of the book.
     */
	public String getISBN() {
		return ISBN;
	}
    /**
     * Sets the genre of the book.
     * @param ISBN of the book.
     */
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
    /**
     * Sets the language of the book.
     * @return language that the book is written in.
     */
	public String getLanguage() {
		return language;
	}
    /**
     * Sets the language of the book.
     * @param language that the book is written in.
     */
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return getID() + "," + getTitle() + "," + getYear() + "," + getAuthor() + "," + getPublisher() + ","
				+ getGenre() + "," + getISBN() + "," + getLanguage();
	}

	@Override
	public float getOverdueDayRate() {
		return OVERDUE_DAY_RATE;
	}

	@Override
	public float getMaxCharge() {
		return MAX_OVERDUE_RATE;
	}
}
