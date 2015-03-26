/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * This class demonstrates a way to move geometry with the use of keyboard keys
 * It shows the use of listeners which trasnlates the geometry in the node
 * KeyMap: X=F and H , Y=T and G , Z=R and V 
 * 
 * This class also demonstrates the translations and rotations used in JME
 * 
 * @author Tor Arne
 */

public class KeyListenerSkel extends SimpleApplication {
    
    // For moving the geometry
    float x=0;
    float y=0;
    float z=0;
    
    // Make it global to be reached by all methods
    Geometry keyGeom;

    public static void main(String[] args) {
        KeyListenerSkel app = new KeyListenerSkel();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        // Do the basic initialization of mesh, geometry, material and color.
        Box b = new Box(1, 1, 1);
        
        keyGeom = new Geometry("Box",b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        keyGeom.setMaterial(mat);
        
        // Attach to Scene Graph
        rootNode.attachChild(keyGeom);
        
        // Add mapping of different keys to the inputManager
        // Recognize these by names to be used in listeners
        inputManager.addMapping("MoveX",new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("MoveX2",new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("MoveY",new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("MoveY2",new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("MoveZ",new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("MoveZ2",new KeyTrigger(KeyInput.KEY_V));
        
        // For on/off actions
        ActionListener actionListener = new ActionListener() {
            public void onAction(String name, boolean keyPressed, float tpf) {
                  /** TODO: test for mapping names and implement actions */
            }
        };
        
        // For continuus actions
        AnalogListener analogListener = new AnalogListener() {
            public void onAnalog(String name, float keyPressed, float tpf) {
                /** TODO: test for mapping names and implement actions */
                
                // Does commands based on keymapped names
                // Increases and decreases x,y,z values if keys are pressed
                if (name.equals("MoveX")) {         // test?
                    x+=0.01f;
                } 
                if (name.equals("MoveX2")) {         // test?
                    x-=0.01f;
                } 
                if (name.equals("MoveY")) {         // test?
                    y+=0.01f;
                } 
                if (name.equals("MoveY2")) {         // test?
                    y-=0.01f;
                } 
                if (name.equals("MoveZ")) {         // test?
                    z+=0.01f;
                } 
                if (name.equals("MoveZ2")) {         // test?
                    z-=0.01f;
                } 
            }
         };
        
        // Adds listeners to different names for the keymaps
        inputManager.addListener(analogListener, new String[]{"MoveX","MoveX2",
            "MoveY","MoveY2","MoveZ","MoveZ2"});
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
        
        // Use these to test rotation and translation. 
        // Only one can be used at a time. Comment the other
        
        // Updates the geometry with translation
        keyGeom.setLocalTranslation(new Vector3f(x,y,z));
        
        // Updates the rotation
        //keyGeom.setLocalRotation(new Quaternion().fromAngleNormalAxis
        //      (0.1f, new Vector3f(x,y,z)));
    }
    
}
