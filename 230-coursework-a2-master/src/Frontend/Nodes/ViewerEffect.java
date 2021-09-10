package Frontend.Nodes;

import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

/**
 * Composite effects to apply to JavaFX nodes for more striking
 * visual appeal.
 * @author matt
 */
public abstract class ViewerEffect {

    /**
     * A Bevel effect, will make a node appear to stick out of the page.
     */
    public static final InnerShadow BEVEL;
    /**
     * A Depressed effect, will make a node appear to fall into the page.
     */
    public static final InnerShadow DEPRESSED;

    private static final int SHADOW_SIZE = 21;
    private static final int SHADOW_RADIUS = 10;
    private static final int SHADOW_OFFSET = 3;
    private static final Color BLACK_HALF_OPACITY = Color.color(0, 0, 0, .5f);
    private static final Color WHITE_HALF_OPACITY = Color.color(1, 1, 1, .5f);

    static {

        InnerShadow dark = new InnerShadow();
        dark.setChoke(0);
        dark.setWidth(SHADOW_SIZE);
        dark.setHeight(SHADOW_SIZE);
        dark.setRadius(SHADOW_RADIUS);
        dark.setOffsetX(-SHADOW_OFFSET);
        dark.setOffsetY(-SHADOW_OFFSET);
        dark.setColor(BLACK_HALF_OPACITY);

        InnerShadow light = new InnerShadow();
        light.setChoke(0);
        light.setWidth(SHADOW_SIZE);
        light.setHeight(SHADOW_SIZE);
        light.setRadius(SHADOW_RADIUS);
        light.setOffsetX(SHADOW_OFFSET);
        light.setOffsetY(SHADOW_OFFSET);
        light.setColor(WHITE_HALF_OPACITY);

        dark.setInput(light);
        BEVEL = dark;
    }

    static {
        InnerShadow dark = new InnerShadow();
        dark.setChoke(0);
        dark.setWidth(SHADOW_SIZE);
        dark.setHeight(SHADOW_SIZE);
        dark.setRadius(SHADOW_RADIUS);
        dark.setOffsetX(SHADOW_OFFSET);
        dark.setOffsetY(SHADOW_OFFSET);
        dark.setColor(BLACK_HALF_OPACITY);

        InnerShadow light = new InnerShadow();
        light.setChoke(0);
        light.setWidth(SHADOW_SIZE);
        light.setHeight(SHADOW_SIZE);
        light.setRadius(SHADOW_RADIUS);
        light.setOffsetX(-SHADOW_OFFSET);
        light.setOffsetY(-SHADOW_OFFSET);
        light.setColor(WHITE_HALF_OPACITY);

        dark.setInput(light);
        DEPRESSED = dark;
    }
}
