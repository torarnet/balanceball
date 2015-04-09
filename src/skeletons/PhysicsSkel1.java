/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Tor Arne
 */
public class PhysicsSkel1 extends SimpleApplication {

    Geometry board;
    Geometry ball;
    Geometry obstacleOne;
    Geometry obstacleTwo;

    public static void main(String[] args) {
        PhysicsSkel1 app = new PhysicsSkel1();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        getFlyByCamera().setMoveSpeed(10);

        addLights();

        board = makeBoard();
        ball = makeBall();
        obstacleOne = makeObstacle(ColorRGBA.Blue);
        obstacleTwo = makeObstacle(ColorRGBA.Green);
        
        obstacleOne.setLocalTranslation(1f, 0f, 0f);
        obstacleTwo.setLocalTranslation(-1f, 0f, 0f);

        Node boardNode = new Node();
        Node ballNode = new Node();
        Node obstacleNode = new Node();

        ballNode.setLocalTranslation(new Vector3f(0, 0.4f, 0));
        obstacleNode.setLocalTranslation(new Vector3f(0, 0.4f, 0));

        boardNode.attachChild(ballNode);
        boardNode.attachChild(obstacleNode);

        boardNode.attachChild(board);
        ballNode.attachChild(ball);
        obstacleNode.attachChild(obstacleOne);
        obstacleNode.attachChild(obstacleTwo);

        rootNode.attachChild(boardNode);

    }

    public void addLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0.5f, -1f, -0.5f).normalizeLocal());
        rootNode.addLight(sun);
    }

    public Geometry makeBoard() {
        Box b = new Box(3, 0.1f, 3);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture text = assetManager.loadTexture("Textures/tile4.png");

        mat.setTexture("ColorMap", text);

        text.setWrap(Texture.WrapMode.Repeat);

        geom.setMaterial(mat);

        return geom;
    }

    public Geometry makeBall() {
        Sphere s = new Sphere(10, 10, 0.2f);
        Geometry geom2 = new Geometry("Sphere", s);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", ColorRGBA.Red);  // minimum material color
        mat2.setColor("Specular", ColorRGBA.Red.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess
        
        geom2.setMaterial(mat2);
        
        return geom2;
    }
    
    public Geometry makeObstacle(ColorRGBA color) {
        Box b = new Box(0.2f, 0.2f, 0.2f);
        Geometry geom3 = new Geometry("Box", b);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess
        
        geom3.setMaterial(mat2);
        
        return geom3;
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
