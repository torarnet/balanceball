/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * Demonstrates the use of textures, in two ways.
 * One way is to have the texture fill the entire geometry,
 * another is to divide the geometry in pieces with each having one 
 * instance of the texture. So, either make it with one large texture,
 * or several small textures
 * 
 * @author Tor Arne
 */
public class TextureSkel extends SimpleApplication {
    
    public static void main(String[] args) {
        TextureSkel app = new TextureSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); 
        
        // To try textures, use one of these and comment the other
        
        // Make a big texture
        makeItBig();
        
        // Make several small
        //makeSeveralSmall();
        
    }
    
    public void makeItBig() {
        // Make a flat surface
        Quad q = new Quad(7,7);
        
        Geometry geom = new Geometry("Quad",q);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Sets the texture to the geometry from a file in asset folder
        // Has one big texture
        mat.setTexture("ColorMap",assetManager.loadTexture("Textures/ocean1.png"));
        geom.setMaterial(mat);
        
        // Adjust the position
        geom.setLocalTranslation(new Vector3f(-5f,-4f,0f));
        
        rootNode.attachChild(geom);
    }
    
    public void makeSeveralSmall() {

        // Make a flat surface
        Quad q = new Quad(7,7);
        
        Geometry geom = new Geometry("Quad",q);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Makes a new texture object
        Texture text = assetManager.loadTexture("Textures/ocean1.png");
        // .setWrap makes it possible to use the texture several times
        // in the same geometry
        text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap",text);

        //Says to use the texture 20 * 20 times to fill the surface
        q.scaleTextureCoordinates(new Vector2f(20,20));
        geom.setMaterial(mat);
        
        // Adjust the position
        geom.setLocalTranslation(new Vector3f(-5f,-4f,0f));
        
        rootNode.attachChild(geom);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
