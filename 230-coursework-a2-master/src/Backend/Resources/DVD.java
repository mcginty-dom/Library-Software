package Backend.Resources;

import java.util.Arrays;

public class DVD extends Resource {

	private static final float OVERDUE_DAY_RATE = 2.f;
	private static final float MAX_OVERDUE_RATE = 25.f;


	private String director, language;
	private String[] subtitles;
	private int runtime;
	
	/**
	 * Constructor for the DVD class.
	 * @param director that directed the DVD.
	 * @param language the DVD is written in.
	 * @param subtitles that the DVD come with if any.
	 * @param runtime of the DVD in minutes.
	 * @param minLoanDuration the minimum time the resource is allowed to be taken out for.
	 * @param nextCopyID the ID for the next copy of the resource if one is added.
	 */

	public DVD(String thumbnail, int ID, String title, int year, String minLoanDuration, String director, String language, String[] subtitles, int runtime) {
		super(thumbnail, ID, title, year, minLoanDuration);
		this.director = director;
		this.language = language;
		if(subtitles == null){
			subtitles = new String[0];
		}
		this.subtitles = subtitles;
		this.runtime = runtime;
	}

	/**
	 * Constructor for loading from file. This should not be used to create a resource not being loaded from a file
	 *
	 */
	public DVD(String thumbnail, int ID, String title, int year, String minLoanDuration,  String director,
			   String language, String[] subtitles, int runtime, int maxCopyID) {
		super(thumbnail, ID, title, year, minLoanDuration, maxCopyID);
		this.director = director;
		this.language = language;
		this.subtitles = subtitles;
		this.runtime = runtime;
	}
	
    /**
     * Gets the Director of the DVD.
     * @return Director of the DVD.
     */
	public String getDirector() {
		return director;
	}
    /**
     * Sets the Director of the DVD.
     * @param Director of the DVD.
     */
	public void setDirector(String director) {
		this.director = director;
	}
    /**
     * Gets the Language of the DVD.
     * @return Language of the DVD.
     */
	public String getLanguage() {
		return language;
	}
    /**
     * Sets the Language of the DVD.
     * @param Language of the DVD.
     */
	public void setLanguage(String language) {
		this.language = language;
	}
    /**
     * Gets the Subtitle array of the DVD.
     * @return Subtitle array of the DVD.
     */
	public String[] getSubtitles() {
		return subtitles;
	}
    /**
     * Sets the Subtitle array of the DVD.
     * @param Subtitle array of the DVD.
     */
	public void setSubtitles(String[] subtitles) {
		this.subtitles = subtitles;
	}
    /**
     * Gets the Runtime of the DVD.
     * @return Runtime of the DVD.
     */
	public int getRuntime() {
		return runtime;
	}
    /**
     * Sets the Runtime of the DVD.
     * @param Runtime of the DVD.
     */
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	@Override
	public String toString() {
		return getID() + "," + getTitle() + "," + getYear() + "," + getDirector() + "," + getLanguage() + ","
				+ Arrays.toString(getSubtitles()) + "," + getRuntime();
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
