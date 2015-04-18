/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tor Arne
 */
public class Lights {

    public AmbientLight getAmbientLight(ColorRGBA color) {
        // Set up ambient light.
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(color);
        return ambient;
    }

    public DirectionalLight getDirectionalLight(ColorRGBA color,
            Vector3f direction) {
        DirectionalLight directional = new DirectionalLight();
        directional.setColor(color);
        directional.setDirection(direction);
        return directional;
    }
    
}
