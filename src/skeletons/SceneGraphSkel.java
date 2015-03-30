/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Tor Arne
 */
public class SceneGraphSkel extends SimpleApplication {

    private Geometry floor;
    private Geometry lowerCross;
    private Geometry upperCross;
    private Geometry middleCross;
    Node cross1;
    Node cross2;
    Node cross3;

    public static void main(String[] args) {
        SceneGraphSkel app = new SceneGraphSkel();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(15);
        
        Box floorMesh = new Box(5, 0.1f, -5);
        Material floorMat = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");

        floorMat.setColor("Color", ColorRGBA.Gray);
        
        floor = new Geometry("floor",floorMesh);
        floor.setMaterial(floorMat);
        
        rootNode.attachChild(floor);
        
        cross1 = new Node();
        
        

    }
    
}
