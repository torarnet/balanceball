/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Tor Arne
 */
public class Geometry {
    
    //private static AssetManager assetManager;
    
    public Geometry() {

    }
    
    public Geometry getBackGround(AssetManager assetsManager,Texture text) {
        Sphere sphereBackground = new Sphere(10,10,100);
        
        Geometry geometry = new Geometry("Sphere", sphereBackground);
        
        return null;
        
        
    }
    
    public Geometry getBoard(AssetManager assetManager, Texture text) {
        Box b = new Box(3, 0.1f, 3);
        Geometry geom = new Geometry("Box", b);

        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Texture text = assetManager.loadTexture("Textures/tile4.png");

        mat.setTexture("ColorMap", text);

        text.setWrap(Texture.WrapMode.Repeat);

        geom.setMaterial(mat);
    }
    
    public Geometry getBoxes(float size,ColorRGBA color,Texture text) {
         Box b = new Box(size,size,size);
        //Geometry geom3 = new Geometry("Box", b);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess

        geom3.setMaterial(mat2);
        
        return geom3;
    }
    
}
