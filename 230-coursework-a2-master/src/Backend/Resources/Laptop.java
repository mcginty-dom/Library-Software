package Backend.Resources;

public class Laptop extends Resource{

	private static final float OVERDUE_DAY_RATE = 10.f;
	private static final float MAX_OVERDUE_RATE = 100.f;


	private String manufacturer, model, OS;
	
	/**
	 * Constructor used to create a laptop.
	 * @param thumbnail
	 * @param ID
	 * @param title
	 * @param year
	 * @param minLoanDuration
	 * @param manufacturer
	 * @param model
	 * @param oS
	 * @param maxCopyID
	 */
	public Laptop(String thumbnail, int ID, String title, int year, String minLoanDuration, String manufacturer, String model, String oS) {
		super(thumbnail, ID, title, year, minLoanDuration);
		this.manufacturer = manufacturer;
		this.model = model;
		this.OS = oS;
	}

	/**
	 * Constructor for loading from file. This should not be used to create a resource not being loaded from a file
	 * @param thumbnail
	 * @param ID
	 * @param title
	 * @param year
	 * @param minLoanDuration
	 * @param manufacturer
	 * @param model
	 * @param oS
	 * @param maxCopyID
	 */
	public Laptop(String thumbnail, int ID, String title, int year, String minLoanDuration, String manufacturer, String model, String oS, int maxCopyID) {
		super(thumbnail, ID, title, year, minLoanDuration, maxCopyID);
		this.manufacturer = manufacturer;
		this.model = model;
		this.OS = oS;
	}

	/** gets the manufacturer of the laptop.
	 * @return manufacturer of the laptop.
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	/** sets the manufacturer of the laptop.
	 * @param manufacturer of the laptop.
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	/** gets the model of the laptop.
	 * @return model of the laptop.
	 */
	public String getModel() {
		return model;
	}
	/** sets the model of the laptop.
	 * @param model of the laptop.
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/** gets the OS of the laptop.
	 * @return OS of the laptop.
	 */
	public String getOS() {
		return OS;
	}
	
	/** sets the OS of the laptop.
	 * @param OS of the laptop.
	 */
	public void setOS(String oS) {
		OS = oS;
	}


	@Override
	public String toString() {
		return getID() + ", " + getTitle() + ", " + getYear()+ ", " + getManufacturer() + ", " + getModel() + ", " + getOS();
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