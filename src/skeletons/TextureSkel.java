/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * Demonstrates the use of textures, transparency and models
 * One way is to have the texture fill the entire geometry,
 * another is to divide the geometry in pieces with each having one 
 * instance of the texture. So, either make it with one large texture,
 * or several small textures. There also is loading of a model and
 * a transparent texture
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
        
        flyCam.setMoveSpeed(15);
        
        // To try textures, use one of these and comment the other
        
        // Make a big texture
        //makeItBig();
        
        // Make several small
        //makeSeveralSmall();
        
        // Make a transparent Texture
        //makeTransparent();
        
        // Load a coffee model
        //loadModel();
        
        // Change colors of model
        loadColoredModel();
        
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
        
        // Makes the geometry visible from both sides
        geom.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        
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
        
        // Makes the geometry visible from both sides
        geom.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        
        rootNode.attachChild(geom);
    }
    
    public void makeTransparent() {

        // Gray color is partially transparent, black is solid, white is fully
        // Transparent. Need to set alpha channel when creating the texture file
        // Or make it transparent from the paint program directly
        
        Quad quad2Mesh = new Quad(7,7);
        Geometry quad2Geo = new Geometry("window frame", quad2Mesh);
        Material quad2Mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        quad2Mat.setTexture("ColorMap",
                assetManager.loadTexture("Textures/tree2.png"));
        quad2Mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);  // !
        quad2Geo.setQueueBucket(Bucket.Transparent);                        // !
        quad2Geo.setMaterial(quad2Mat);
        rootNode.attachChild(quad2Geo);

    }

    public void loadModel() {
        Spatial model = assetManager.loadModel("Models/Coffee/"
                + "coffeeMaker.scene");
        model.scale(5,5,5);
     
        // Culling is about rendering the back side when seen from front
        // It also about showing geometry from both sides
        // Traverse scene to disable face culling for the geometry.
        model.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    // Turn off face culling, so that we can see both sides.
                    ((Geometry) spatial).getMaterial().getAdditionalRenderState().
                            setFaceCullMode(RenderState.FaceCullMode.Off);
                }
            }
        });

        rootNode.attachChild(model);
  
        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        // Light for all directions
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        rootNode.addLight(ambient);
    }
    
    public void loadColoredModel() {
        Spatial model = assetManager.loadModel("Models/Coffee/"
                + "coffeeMaker.scene");
        model.scale(5,5,5);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        
        model.setMaterial(mat);
     
        // Culling is about rendering the back side when seen from front
        // It also about showing geometry from both sides
        // Traverse scene to disable face culling for the geometry.
        model.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    // Turn off face culling, so that we can see both sides.
                    ((Geometry) spatial).getMaterial().getAdditionalRenderState().
                            setFaceCullMode(RenderState.FaceCullMode.Off);
                }
            }
        });

        rootNode.attachChild(model);
  
        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        // Light for all directions
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        rootNode.addLight(ambient);
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
