package util;

/**
 * Alpha object is used for interpolation. It interpolates in the range [0, 1].
 * Several parameters can be set, including the most basic duration of the
 * increase from 0 to 1, as well as an optional decrease from 1 to 0. In
 * addition ramp values can be set, which enables a duration of the increase
 * time that will increase at a faster pace than the remainder of the duration.
 * This also applies to the decrease. The time for the interpolation to stay
 * initially at 0 before increasing, and similarily at 1 before starting
 * decrease, or at 0 after decrease.
 * 
 * Inspired by the Alpha class in Java 3d (javax.media.j3d.Alpha).
 * http://download.java.net/media/java3d/javadoc/1.5.2/index.html
 *
 * @author thomasw
 */
public class Alpha implements Cloneable {

    float timeSinceStart = 0f;
    float startDelay;
    float increasingAlphaDuration;
    float increasingAlphaRampDuration;
    float alphaAtOneDuration;
    float decreasingAlphaDuration;
    float decreasingAlphaRampDuration;
    float alphaAtZeroDuration;
    float increaseRampValue;
    float decreaseRampValue;
    int fps;
    float totTime;
    float startIncreaseRamp;
    float startIncreaseNorm;
    float startAtOne;
    float startDecreaseRamp;
    float startDecreaseNorm;
    float startAtZero;
    float endValue = 1.0f;

    /**
     * This constructor takes all of the Alpha user-definable parameters.
     * @param startDelay Delay before starting the alpha
     * @param increasingAlphaDuration period of time during which alpha goes from zero to one
     * @param increasingAlphaRampDuration period of time during which the alpha step size increases at the beginning of the increasingAlphaDuration
     * @param alphaAtOneDuration period of time that alpha stays at one
     * @param decreasingAlphaDuration period of time during which alpha goes from one to zero
     * @param decreasingAlphaRampDuration period of time during which the alpha step size increases at the beginning of the decreasingAlphaDuration
     * @param alphaAtZeroDuration period of time that alpha stays at zero
     * @param increaseRampValue the value the increasing ramp duration will go to [0, 1]
     * @param decreaseRampValue the value the decreasing ramp duration will go to [0, 1]
     */
    public Alpha(float startDelay, float increasingAlphaDuration,
            float increasingAlphaRampDuration, float alphaAtOneDuration,
            float decreasingAlphaDuration, float decreasingAlphaRampDuration,
            float alphaAtZeroDuration, float increaseRampValue,
            float decreaseRampValue) {
        this.startDelay = startDelay;
        this.increasingAlphaDuration = increasingAlphaDuration;
        this.increasingAlphaRampDuration = increasingAlphaRampDuration;
        this.alphaAtOneDuration = alphaAtOneDuration;
        this.decreasingAlphaDuration = decreasingAlphaDuration;
        this.decreasingAlphaRampDuration = decreasingAlphaRampDuration;
        this.alphaAtZeroDuration = alphaAtZeroDuration;
        // clamp values [0, 1].
        this.increaseRampValue = clamp(increaseRampValue);
        this.decreaseRampValue = clamp(decreaseRampValue);

        setup();
    }

    private float clamp(float value) {
        if (value > 1.0f) {
            value = 1.0f;
        } else if (value < 0.0f) {
            value = 0.0f;
        }

        return value;
    }

    private void setup() {
        // The total time for this alpha (sum of all the time periods).
        totTime = startDelay + increasingAlphaDuration + increasingAlphaRampDuration
                + alphaAtOneDuration + decreasingAlphaDuration + decreasingAlphaRampDuration
                + alphaAtZeroDuration;

        // Start increase ramp after the start delay duration have finished.
        startIncreaseRamp = startDelay;
        // Start normal increase after increase ramp and previous have finished.
        startIncreaseNorm = startIncreaseRamp + increasingAlphaRampDuration;
        // Start at one after increase normal and previous have finished.
        startAtOne = startIncreaseNorm + increasingAlphaDuration;
        // Start decrease ramp after the at one and previous have finished.
        startDecreaseRamp = startAtOne + alphaAtOneDuration;
        // Start decrease normal after the decrease ramp and previous have finished.
        startDecreaseNorm = startDecreaseRamp + decreasingAlphaRampDuration;
        // Start at zero after the decrease normal and previous have finished.
        startAtZero = startDecreaseNorm + decreasingAlphaDuration;

        // Regular end value is 1.0f, but if there is decreasing values set, the
        // end value will be 0.0f.
        if (decreasingAlphaRampDuration > 0 || decreasingAlphaDuration > 0) {
            endValue = 0.0f;
        }
    }

    /**
     * This method returns a value between 0.0f and 1.0f inclusive. The time per
     * frame value is added to the total time since start of this alpha, which
     * determines the result.
     * 
     * @param tpf the time per frame
     * @return alpha value based on the tpf, between 0.0f and 1.0f inclusive
     */
    public float value(float tpf) {
        // If alpha is finished, return the end value.
        if (timeSinceStart >= totTime) {
            return endValue;
        }

        float value = 0.0f;

        if (timeSinceStart >= startDelay) {
            // Inside here means that start delay have finished.
            
            // a is the value to interpolate from, b is the one to interpolate to,
            // and t is the percentage between a and b.
            float a = 0, b = 0, t = 0;

            if (timeSinceStart < startIncreaseNorm) {
                // Inside here means that we are at increase ramp (acceleration).
                a = 0;
                b = increaseRampValue;
                t = (timeSinceStart - startDelay) / increasingAlphaRampDuration;
            } else if (timeSinceStart < startAtOne) {
                // Inside here means that we are at increase normal.
                a = increaseRampValue;
                b = 1.0f;
                t = (timeSinceStart - startDelay - increasingAlphaRampDuration)
                        / increasingAlphaDuration;
            } else if (timeSinceStart < startDecreaseRamp) {
                // Inside here means that we are at 1.
                a = 0;
                b = 1;
                t = 1;
            } else if (timeSinceStart < startDecreaseNorm) {
                // Inside here means that we are at decrease ramp (acceleration).
                a = 1.0f;
                b = decreaseRampValue;
                t = (timeSinceStart - startDelay - increasingAlphaRampDuration
                        - increasingAlphaDuration - alphaAtOneDuration)
                        / decreasingAlphaRampDuration;
            } else if (timeSinceStart < startAtZero) {
                // Inside here means that we are at decrease normal.
                a = (decreaseRampValue == 0 ? 1 : decreaseRampValue);
                b = 0.0f;
                t = (timeSinceStart - startDelay - increasingAlphaRampDuration
                        - increasingAlphaDuration - alphaAtOneDuration
                        - decreasingAlphaRampDuration)
                        / decreasingAlphaDuration;
            }

            // Interpolate.
            value = Interpolation.lerp(a, b, t);

            if (timeSinceStart > startAtZero + alphaAtZeroDuration) {
                // Stay at zero (no change).
                value = endValue;
            }

        }

        // Increase the time duration by the time since last frame.
        timeSinceStart += tpf;

        return value;
    }

    /**
     * Returns the total time duration set for this Alpha object.
     * @return the total time duration
     */
    public float getTotalTimeDuration() {
        return totTime;
    }

    /**
     * Returns the time since start for this Alpha object.
     * @return the time since start
     */
    public float getTimeSinceStart() {
        return timeSinceStart;
    }

    /**
     * Resets this Alpha object, setting internal members to the state of object
     * creation.
     */
    public void reset() {
        timeSinceStart = 0;
    }

    /**
     * Returns the end value for this Alpha object. Can be 1.0f if only increasing,
     * and 0.0f if only or also decreasing.
     * @return the end value
     */
    public float getEndValue() {
        return endValue;
    }
    
    /**
     * Returns whether this Alpha object have finished or not.
     * @return if it has finished
     */
    public boolean isFinished(){
        float value = value(0);
        
        return value >= getEndValue() && 
                getTimeSinceStart() >= getTotalTimeDuration();
    }

    @Override
    public Alpha clone() {
        try {
            Alpha alpha = (Alpha) super.clone();
            return alpha;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }
}
