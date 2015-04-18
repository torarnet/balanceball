/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * Skeleton for different types of animation. Some using custom Shaders. Used 
 * IFE Examples as a starter point
 * 
 * @author Tor Arne
 */
public class AnimationSkel extends SimpleApplication {
    
    public static void main(String[] args) {
        AnimationSkel app = new AnimationSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        growBox();
    
    }
    
    // Makes a box and expands it using custom shader for animation.
    public void growBox() {
        Sphere s = new Sphere(10,10,0.5f);
        Geometry geom = new Geometry("Sphere", s);
        // Uses the custom shader called Grow. This will scale the geometry
        Material mat = new Material(assetManager,
                "MatDefs/Grow.j3md");
        
        Texture texture = assetManager.loadTexture("Textures/fire1.jpg");
        // Set mode to repeat, so only the fractional part is considered when we move the texture
        // coordinates.
        texture.setWrap(Texture.WrapMode.Repeat);
        // Using shader variables for setting texture and scale factor
        mat.setTexture("Image", texture);
        mat.setFloat("Size", 10.0f);
        mat.setFloat("Speed", 20.0f);
        
        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
    }
    
}
