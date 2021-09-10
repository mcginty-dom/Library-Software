package Frontend.Paint.Tools;

/**
 * A transform caries the the data to compute
 * a translation and scaling in a 2D plane.
 *
 * @author matt
 */
public class Transform {

    private double xTransform = 0;
    private double yTransform = 0;
    private double scrollValue = 0;

    /**
     * Create an empty transform.
     */
    public Transform() {
        this(0, 0, 0);
    }

    /**
     * Create an initialized transform, translated xTransform in the x axis,
     * yTransform in the y and scaled according to the scroll value.
     * @param xTransform the transform in the x axis.
     * @param yTransform the transform in the y axis.
     * @param scrollValue the scale change.
     */
    public Transform(double xTransform, double yTransform, float scrollValue) {
        this.xTransform = xTransform;
        this.yTransform = yTransform;
        this.scrollValue = scrollValue;
    }

    /**
     * Gets xTransform.
     *
     * @return value of xTransform
     */
    public double getxTransform() {
        return xTransform;
    }

    /**
     * Sets the value of xTransform.
     *
     * @param xTransform the new value
     */
    public void setxTransform(double xTransform) {
        this.xTransform = xTransform;
    }

    /**
     * Gets yTransform.
     *
     * @return value of yTransform
     */
    public double getyTransform() {
        return yTransform;
    }

    /**
     * Sets the value of yTransform.
     *
     * @param yTransform the new value
     */
    public void setyTransform(double yTransform) {
        this.yTransform = yTransform;
    }

    /**
     * Gets scrollValue.
     *
     * @return value of scrollValue
     */
    public double getScrollValue() {
        return scrollValue;
    }

    /**
     * Sets the value of scrollValue.
     *
     * @param scrollValue the new value
     */
    public void setScrollValue(double scrollValue) {
        this.scrollValue = scrollValue;
    }




}
