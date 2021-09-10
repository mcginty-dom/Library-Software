package Frontend.Tools;

/**
 * Interface representing a Sanitiser, which takes in a String and returns the String after performing (possible no)
 * changes.
 * @author matt
 */
public interface StringSanitiser {

    /**
     * Sanitise the given string
     * @param s a string to sanitise
     * @return the sanitised string
     */
    String sanitise(String s);

}
