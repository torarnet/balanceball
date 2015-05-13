package util;

/**
 * Math methods for interpolation.
 *
 * @author thomasw
 */
public class Interpolation {
    
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    
    public static float bezier(float a, float b, float c, float t) {
        float ab = lerp(a, b, t);
        float bc = lerp(b, c, t);
        float abbc = lerp(ab, bc, t);
        
        return abbc;
    }
    
    public static float bezier2(float a, float b, float c, float d, float t) {
        float ab = lerp(a, b, t);
        float bc = lerp(b, c, t);
        float cd = lerp(c, d, t);
        float abbc = lerp(ab, bc, t);
        float bccd = lerp(bc, cd, t);
        float dest = lerp(abbc, bccd, t);
        
        return dest;
    }
}
