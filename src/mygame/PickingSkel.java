/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Skeleton class for demonstrating how to use picking in JME.
 * Here there are two objects on a surface which can be moved
 * independent of each other.
 * 
 * @author Tor Arne using IFE Examples
 */
public class PickingSkel extends SimpleApplication implements ActionListener, AnalogListener {

    final static String PickDown = "PickDown";
    final static String MouseMoveLeft = "MouseMoveLeft";
    final static String MouseMoveRight = "MouseMoveRight";
    final static String MouseMoveUp = "MouseMoveUp";
    final static String MouseMoveDown = "MouseMoveDown";
    boolean moveEnabled = false;
    boolean hasPickedObject = false;
    Spatial pickedObject = null;
    float objOffsetY = 0.0f;
    Node pickables;
    
    public static void main (String[] args) {
        PickingSkel app = new PickingSkel();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        // Disable FlyByCamera.
        flyCam.setEnabled(false);
        
        // Register mouse in system and add listeners to its movements
        registerInput();
        
        // All the objects that are pickable are added to this node.
        pickables = new Node("pickables");
        
        // Make a floor
        Geometry floorGeom = createFloorGeometry(new Vector3f(0,0,0),
                new Vector3f(6,0.4f,6),ColorRGBA.Gray);
        
        // Position the floor
        floorGeom.setLocalTranslation(new Vector3f(-2,1.5f,-2));
        floorGeom.setLocalRotation(new Quaternion().fromAngleNormalAxis((float)Math.PI/4, Vector3f.UNIT_X));
        
        // Make two boxes to play with
        Geometry geom = createBoxGeometry(new Vector3f(0,0,0),
                new Vector3f(1,1,1),ColorRGBA.Blue);
        
        Geometry geom2 = createBoxGeometry(new Vector3f(0,0,0),
                new Vector3f(1,1,1),ColorRGBA.Yellow);
        
       // Put them in the pickable node
        pickables.attachChild(floorGeom);
        pickables.attachChild(geom);
        pickables.attachChild(geom2);
        
        rootNode.attachChild(pickables);
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private Geometry createFloorGeometry(Vector3f min, Vector3f max, ColorRGBA color) {
        // Create a mesh for the box.
        Mesh boxMesh = new Box(min, max);

        // Create geometry based on the box mesh.
        final Geometry floor = new Geometry("floor", boxMesh);

        // Load material definition. Simple unshaded material.
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        // Set color.
        mat.setColor("Color", color);
        
        // Set material to geometry.
        floor.setMaterial(mat);
        
        return floor;
    }
    
    private Geometry createBoxGeometry(Vector3f min, Vector3f max, ColorRGBA color) {
        // Create a mesh for the box.
        Mesh boxMesh = new Box(min, max);

        // Create geometry based on the box mesh.
        final Geometry box = new Geometry("box", boxMesh);

        // Load material definition. Simple unshaded material.
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        // Set color.
        mat.setColor("Color", color);
        
        // Set material to box.
        box.setMaterial(mat);
        
        // Makes the box recognizable in the scene graph by its name 
        // "topParent". This is needed for movement of box. 
        // Name is used further down in code
        box.depthFirstTraversal(new SceneGraphVisitor() {

            @Override
            public void visit(Spatial spatial) {
                spatial.setUserData("topParent", box);
            }
        });
        
        return box;
    }
    
    private void registerInput() {
        inputManager.addMapping(PickDown, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(MouseMoveLeft, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(MouseMoveRight, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(MouseMoveUp, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(MouseMoveDown, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        //inputManager.addMapping(Rotate, new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(this, PickDown, MouseMoveLeft, MouseMoveRight, MouseMoveUp, MouseMoveDown);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        // Check if mouse key pressed
        if (name.equals(PickDown)) {
            if (isPressed) {
                pick();
                moveEnabled = true; 
            } else {
                unpick();
                moveEnabled = false;
            }
        } 
        
    }

    public void onAnalog(String name, float value, float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
        // Move object by calling placeObjAtContactPoint() if mouse moves
        if (name.equals(MouseMoveLeft) || name.equals(MouseMoveRight)
                || name.equals(MouseMoveUp) || name.equals(MouseMoveDown)) {
            if (hasPickedObject && moveEnabled) {
                placeObjAtContactPoint();
            }
        }
    }
    
    private CollisionResult pickIfAny() {
        // Collision result holds the results from a pick operation.
        CollisionResults results = new CollisionResults();

        // Mouse coords.
        Vector2f click2d = inputManager.getCursorPosition();
        // Convert mouse coords to world space.
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f).clone();
        // Calculate direction of the mouse click in world space.
        Vector3f direction = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);

        // Ray casting is projecting a beam into scene graph
        // based on where the mouse points. Makes it 3D, going into the depth
        // Ray casting to find if any object intersects.
        Ray ray = new Ray(click3d, direction);
        pickables.collideWith(ray, results);

        CollisionResult closestCR = null;

        if (results.size() > 0) {
            // Only interested in the closest collision.
            closestCR = results.getClosestCollision();
            //System.out.println(closestCR.getContactPoint());
        }
        
        // Return closest pick result.
        return closestCR;
    }
    
    private void pick() {
        // We are only interested in the closest picking result.
        CollisionResult result = pickIfAny();

        if (result != null) {
            hasPickedObject = true;
            // The actual geometry we collided with, this can be any sub mesh
            // of the model.
            pickedObject = result.getGeometry();
            // Therefore we use the dirty hack, and obtain the handle to the
            // topmost parent for the sub mesh, so that we can manipulate the
            // object as a whole.
            // Here the name given earlier to the geometry is used
            pickedObject = pickedObject.getUserData("topParent");

            if (pickedObject == null) {
                hasPickedObject = false;
                return;
            }

            // Remove the picked object from the pickable node, and add it
            // directly to the root of the scene graph.
            // This way it can be moved all around the scene graph.
            pickedObject.removeFromParent();
            rootNode.attachChild(pickedObject);
            System.out.println("picked " + pickedObject.getName());
        } else {
            System.out.println("picked nothing");
        }
    }
    
    private void unpick() {
        // This method removes the picked object from the root of the scene
        // graph, and adds it back to the pickable node.
        if (!hasPickedObject) {
            return;
        }
        // Puts the geometry back in the pickable node for further picking
        System.out.println("released " + pickedObject.getName());
        pickedObject.removeFromParent();
        pickables.attachChild(pickedObject);
        pickedObject = null;
        hasPickedObject = false;
    }
    
    private void placeObjAtContactPoint() {
        // When moving the picked object, we perform picking again, to find
        // which object we collide with, and then we place the picked object
        // on top of it. This gives a easy way to stack objects in the scene,
        // and prevents the user from moving the object into "thin-air".
        
        // This method follows the mouse using the 
        // CollisionResult.getContactPoint. Then it sets the transformation
        // using these coordinates, one little step at a time
        
        // This is called each time the mouse moves (from onAnalog), 
        // continually updating the pickedObject location using getContactPoint
        
        // As long as we have the floor in the pickables variable, 
        // it will register new mouse coordinates as long as we have
        // the mouse over the floor geometry.
        
        CollisionResult moveToPoint = pickIfAny();
        if (moveToPoint != null) {
            Vector3f loc = moveToPoint.getContactPoint();
            loc.y += objOffsetY;
            pickedObject.setLocalTranslation(loc);
        }
    }
    
}
