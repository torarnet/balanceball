/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Tor Arne
 */
public class ShaderSkel extends SimpleApplication {

    public static void main(String[] args) {
        ShaderSkel app = new ShaderSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        flyCam.setMoveSpeed(10);
        viewPort.setBackgroundColor(ColorRGBA.Blue.mult(0.3f));
        
        addLights();
        
        makeRedBall();
        
        makeGreenBall();

        makeTexturedBall();
        
    }

    public void addLights() {
        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 0.2f));
        rootNode.addLight(ambLight);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(1f, -1f, -2f).normalizeLocal());
        rootNode.addLight(sun);
    }
    
    public void makeRedBall() {
        Material material = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", ColorRGBA.Red);  // minimum material color
        material.setColor("Specular", ColorRGBA.Red); // for shininess
        material.setFloat("Shininess", 64f); // [1,128] for shininess
        
        Sphere s = new Sphere(50,50,1);
        Geometry sphere = new Geometry("sphere", s);
        //box.setLocalTranslation(0, 0, 0);
        sphere.setMaterial(material);

        rootNode.attachChild(sphere);
    }
    
    public void makeGreenBall() {
        Material material = new Material(assetManager, "MatDefs/Diffuse.j3md");
        //material.setBoolean("UseMaterialColors", true);
        material.setColor("AmbientColor", ColorRGBA.Green.mult(0.1f));
        material.setColor("DiffuseColor", ColorRGBA.Green);
        
        Sphere s = new Sphere(50,50,1);
        Geometry sphere = new Geometry("sphere", s);
        sphere.setLocalTranslation(2, 0, 0);
        sphere.setMaterial(material);

        rootNode.attachChild(sphere);
    }
    
    public void makeTexturedBall() {
        // Box rendered with a texture.
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Ambient", ColorRGBA.White);
        mat.setColor("Diffuse", ColorRGBA.Black);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1.0f);
        // Load and set texture.
        Texture tex = assetManager.loadTexture("Textures/fire5.png");
        mat.setTexture("DiffuseMap", tex);
        
        Sphere s = new Sphere(50,50,1);
        Geometry sphere = new Geometry("sphere", s);
        sphere.setLocalTranslation(4, 0, 0);
        sphere.setMaterial(mat);
        
        rootNode.attachChild(sphere);
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
