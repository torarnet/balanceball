package util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Rotation interpolator, that interpolates the rotation of spatial between the
 * start and target angle.
 *
 * @author thomasw
 */
public class RotationInterpolatorControl extends AbstractControl {

    private float start;
    private float target;
    private Vector3f axis;
    private Alpha alpha;
    private Quaternion rot;
    private float rotDiff;
    private boolean finished = false;
    private boolean loop = false;
    private boolean swap = false;

    /**
     * Constructs RotationInterpolatorControl.
     *
     * @param totalTime        the total time duration for the interpolation
     * @param start            the start rotation in degrees
     * @param target           the target rotation in degrees
     * @param axis             the axis to rotate around
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public RotationInterpolatorControl(float totalTime, float start, float target,
                                       Vector3f axis, boolean startImmediately, boolean loop) {
        this(new Alpha(0, totalTime, 0, 0, 0, 0, 0, 0, 0), start, target,
                axis, startImmediately, loop);
    }

    /**
     * Constructs RotationInterpolatorControl.
     *
     * @param alpha            the alpha object used to control the interpolation
     * @param start            the start rotation in degrees
     * @param target           the target rotation in degrees
     * @param axis             the axis to rotate around
     * @param startImmediately should start immediately
     * @param loop             should loop
     */
    public RotationInterpolatorControl(Alpha alpha, float start, float target, Vector3f axis,
                                       boolean startImmediately, boolean loop) {
        this.start = start;
        this.target = target;
        this.axis = axis;
        this.loop = loop;
        this.alpha = alpha;

        // Difference between start and target.
        rotDiff = target - start;
        rot = new Quaternion();

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
        float r = start + delta * rotDiff;
        rot.fromAngleNormalAxis(r, axis);

        // Apply rotation to spatial.
        spatial.setLocalRotation(rot);

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
        float tmp;
        tmp = start;
        start = target;
        target = tmp;

        rotDiff = target - start;
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
