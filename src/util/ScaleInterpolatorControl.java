package util;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Scale interpolator, that interpolates the scale of spatial between the start
 * and target scale.
 *
 * @author thomasw
 */
public class ScaleInterpolatorControl extends AbstractControl {

    private Vector3f start;
    private Vector3f target;
    private Alpha alpha;
    private float xDiff, yDiff, zDiff;
    private boolean finished = false;
    private boolean loop = false;
    private boolean swap = false;

    /**
     * Constructs ScaleInterpolatorControl.
     *
     * @param totalTime        the total time duration for the interpolation
     * @param start            the start scale
     * @param target           the target scale
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public ScaleInterpolatorControl(float totalTime, Vector3f start, Vector3f target,
                                    boolean startImmediately, boolean loop) {
        this(new Alpha(0, totalTime, 0, 0, 0, 0, 0, 0, 0), start, target,
                startImmediately, loop);
    }

    /**
     * Constructs ScaleInterpolatorControl.
     *
     * @param alpha            the alpha object used to control the interpolation
     * @param start            the start scale
     * @param target           the target scale
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public ScaleInterpolatorControl(Alpha alpha, Vector3f start, Vector3f target,
                                    boolean startImmediately, boolean loop) {
        this.start = start;
        this.target = target;
        this.loop = loop;
        this.alpha = alpha;

        // Difference between start and target.
        xDiff = target.x - start.x;
        yDiff = target.y - start.y;
        zDiff = target.z - start.z;

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
        float x = start.x + delta * xDiff;
        float y = start.y + delta * yDiff;
        float z = start.z + delta * zDiff;
        Vector3f v = new Vector3f(x, y, z);

        // Apply scale to spatial.
        spatial.setLocalScale(v);

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
        Vector3f tmp;
        tmp = start;
        start = target;
        target = tmp;

        xDiff = target.x - start.x;
        yDiff = target.y - start.y;
        zDiff = target.z - start.z;
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
