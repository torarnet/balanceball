/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

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
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Tor Arne
 */
public class Test extends SimpleApplication {
    
        // For moving the geometry
    float x=0;
    float y=0;
    float z=0;
    
    // Make it global to be reached by all methods
    Geometry keyGeom;
    
    public static void main(String[] args) {
        Test app = new Test();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        // Do the basic initialization of mesh, geometry, material and color.
        Box b = new Box(1, 1, 1);
        
        Mesh test = oneHouse(false);
        
        keyGeom = new Geometry("Box",test);
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
        
              
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
                         // Updates the rotation
        keyGeom.setLocalRotation(new Quaternion().fromAngleNormalAxis
              (0.1f, new Vector3f(x,y,z)));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public Mesh oneHouse(boolean indexed) {
    
        Vector3f[] triangleMesh;
        
        // Not indexed
        if (!indexed) {
            // A House with Three Triangles
            /*triangleMesh = new Vector3f[] {
                // Lower left part body of house
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0),
                // Upper right part of house
                new Vector3f(1,0,0),
                new Vector3f(1,1,0),
                new Vector3f(0,1,0),
                // Roof of house
                new Vector3f(0,1,0),
                new Vector3f(0.5f,1.5f,0),
                new Vector3f(1,1,0)
            };*/
            triangleMesh = new Vector3f[] {
                // Lower left part body of house
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0),
                
                // Roof of house
                new Vector3f(1,1,0),
                new Vector3f(0.5f,1.5f,0),
                new Vector3f(0,1,0),
                
                // Upper right part of house
                new Vector3f(1,0,0),
                new Vector3f(1,1,0),
                new Vector3f(0,1,0)
            };

        // Empty mesh object.
        Mesh houseMesh = new Mesh();

        // Assign buffers.
        houseMesh.setBuffer(VertexBuffer.Type.Position, 3,
                BufferUtils.createFloatBuffer(triangleMesh));
        
        /*
        houseMesh.setBuffer(VertexBuffer.Type.Index, 3,
                BufferUtils.createIntBuffer(indices));
        houseMesh.setBuffer(VertexBuffer.Type.Color, 4,
                BufferUtils.createFloatBuffer(colors));
                * */

        // Update the bounds of the mesh, so that bounding box is correctly
        // recalcualted internally by jME.
        houseMesh.updateBound();
            
        return houseMesh;
        }
        return null;
    }
    
}
