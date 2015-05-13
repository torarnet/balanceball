package util;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Color interpolator, that interpolates the color for geometry between the
 * start and target color.
 *
 * @author thomasw
 */
public class ColorInterpolatorControl extends AbstractControl {

    private ColorRGBA start;
    private ColorRGBA target;
    private String uniformMapping;
    private boolean finished = false;
    private boolean loop = false;
    private boolean swap = false;
    private Alpha alpha;
    private float rDiff, gDiff, bDiff, aDiff;

    /**
     * Constructs ColorInterpolatorControl.
     *
     * @param totalTime        the total time duration for the interpolation
     * @param start            the start color
     * @param target           the target color
     * @param uniformMapping   the mapping to uniform variable in shader
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public ColorInterpolatorControl(float totalTime, ColorRGBA start,
                                    ColorRGBA target, String uniformMapping,
                                    boolean startImmediately, boolean loop) {
        this(new Alpha(0, totalTime, 0, 0, 0, 0, 0, 0, 0), start, target,
                uniformMapping, startImmediately, loop);
    }

    /**
     * Constructs ColorInterpolatorControl.
     *
     * @param alpha            the alpha object used to control the interpolation
     * @param start            the start color
     * @param target           the target color
     * @param uniformMapping   the mapping to uniform variable in shader
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public ColorInterpolatorControl(Alpha alpha, ColorRGBA start, ColorRGBA target,
                                    String uniformMapping, boolean startImmediately, boolean loop) {
        this.start = start;
        this.target = target;
        this.uniformMapping = uniformMapping;
        this.loop = loop;
        this.alpha = alpha;

        // Difference between start and target.
        rDiff = target.r - start.r;
        gDiff = target.g - start.g;
        bDiff = target.b - start.b;
        aDiff = target.a - start.a;

        finished = !startImmediately;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (finished) {
            return;
        }

        // Calculate the new value by multiplying the delta (between 0 to 1 - 
        // alpha controlled), with the difference, to set correct values based 
        // on how far along we are in the animation.
        float delta = alpha.value(tpf);
        float r = start.r + delta * rDiff;
        float g = start.g + delta * gDiff;
        float b = start.b + delta * bDiff;
        float a = start.a + delta * aDiff;
        ColorRGBA c = new ColorRGBA(r, g, b, a);

        // Apply new color for geometry.
        if(spatial instanceof Geometry){
            ((Geometry) spatial).getMaterial().setColor(uniformMapping, c);
        }

        // If finished.
        if (alpha.isFinished()) {
            // Swap values if set to swap values at end.
            if (swap) {
                swapValues();
            }

            // Restart or stop based on looping or not.
            if (loop) {
                start();
            } else {
                finished = true;
            }
        }
    }

    /**
     * Set whether this control should swap values at the end.
     *
     * @param swap should swap values
     */
    public void swapValuesAtEnd(boolean swap) {
        this.swap = swap;
    }

    private void swapValues() {
        ColorRGBA tmp;
        tmp = start;
        start = target;
        target = tmp;

        rDiff = target.r - start.r;
        gDiff = target.g - start.g;
        bDiff = target.b - start.b;
        aDiff = target.a - start.a;
    }

    /**
     * Starts this control.
     */
    public void start() {
        alpha.reset();
        finished = false;
    }

    /**
     * Is control finished.
     *
     * @return is control finished
     */
    public boolean isFinished() {
        return finished;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
