/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Tor Arne
 */
public class PhysicsSkel extends SimpleApplication {
    
    public static void main(String[] args) {
        PhysicsSkel app = new PhysicsSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        getFlyByCamera().setMoveSpeed(30);
        
        Node boardNode = new Node();
        Node ballNode = new Node();
        
        ballNode.setLocalTranslation(new Vector3f(0,0.4f,0));
        
        boardNode.attachChild(ballNode);
        
        Box b = new Box(3,0.1f,3);
        Sphere s = new Sphere(10,10,0.2f);
        
        Geometry geom = new Geometry("Box",b);
        Geometry geom2 = new Geometry("Sphere",s);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        mat.setColor("Color", ColorRGBA.Blue);
        mat2.setColor("Color", ColorRGBA.Red);
        
        geom.setMaterial(mat);
        geom2.setMaterial(mat2);
        
        //geom.setLocalRotation(new Quaternion().fromAngleNormalAxis((float)Math.PI/4,Vector3f.UNIT_X));
        //geom2.setLocalTranslation(geom.getLocalTranslation());
        
        boardNode.attachChild(geom);
        ballNode.attachChild(geom2);
        
        rootNode.attachChild(boardNode);
        
        //rootNode.attachChild(geom);
        //rootNode.attachChild(geom2);
        
        //geom2.setLocalTranslation(new Vector3f(0,0,0));
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
