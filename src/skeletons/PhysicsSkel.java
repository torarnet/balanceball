/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import util.RawInputAdapter;

/**
 *
 * @author Tor Arne
 */
public class PhysicsSkel extends SimpleApplication {

    Geometry board;
    Geometry ball;
    Geometry obstacleOne;
    Geometry obstacleTwo;
    
    // Bullet physics state.
    private BulletAppState bulletAppState;
    
    private RigidBodyControl sphereControl;
    private final Transform sphereStartTransform = new Transform(new Vector3f(0f, 3, 1f));
    private boolean[] keysPressed = new boolean[0xff];
    final float RADIUS = 0.2f;

    public static void main(String[] args) {
        PhysicsSkel app = new PhysicsSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //getFlyByCamera().setMoveSpeed(10);
        //flyCam.setEnabled(false);
        
        viewPort.setBackgroundColor(ColorRGBA.Magenta.mult(0.5f));

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
        
        //boardNode.rotate(new Quaternion().fromAngleNormalAxis((float)Math.PI/4, Vector3f.UNIT_X));

        rootNode.attachChild(boardNode);
        
        initCamera();
        initPhysics();
        initInput();
        setGeomToPhysics();

    }
    
    
    private void initCamera() {
        cam.setLocation(new Vector3f(-3, 5, 6));
        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
    }
    
    
    private void initPhysics() {
        bulletAppState = new BulletAppState();
        // add the bullet app state to the state manager, so that bullet is
        // udpated during each rendering.
        stateManager.attach(bulletAppState);

        bulletAppState.getPhysicsSpace().addTickListener(new PhysicsTickListener() {

            @Override
            public void prePhysicsTick(PhysicsSpace ps, float f) {
                // the force to apply to the sphere in each axis for moving it.
                Vector3f forceMove = new Vector3f();
                // movement of the sphere accounts for camera rotation.
                Vector3f camDir = cam.getDirection().clone();
                Vector3f camLeft = cam.getLeft();
                if (keysPressed[KeyInput.KEY_LEFT]) {
                    forceMove.addLocal(camLeft);
                }
                if (keysPressed[KeyInput.KEY_RIGHT]) {
                    forceMove.subtractLocal(camLeft);
                }
                if (keysPressed[KeyInput.KEY_UP]) {
                    forceMove.addLocal(camDir);
                }
                if (keysPressed[KeyInput.KEY_DOWN]) {
                    forceMove.subtractLocal(camDir);
                }
                forceMove.y = 0;
                if (forceMove.length() > 0) {
                    forceMove.normalize();
                }
                forceMove.multLocal(20f);
                // apply the force to the center of the sphere.
                sphereControl.applyCentralForce(forceMove);
            }

            @Override
            public void physicsTick(PhysicsSpace ps, float f) {
            }
        });
    }
    
    private void initInput() {
        inputManager.addRawInputListener(new RawInputAdapter() {

            @Override
            public void onKeyEvent(KeyInputEvent kie) {
                keysPressed[kie.getKeyCode() % keysPressed.length] = kie.isPressed() || kie.isRepeating();

                if (kie.isPressed() && kie.getKeyCode() == KeyInput.KEY_1) {
                    resetScene();
                } else if (kie.isPressed() && kie.getKeyCode() == KeyInput.KEY_2) {
                    toggleDebug();
                }
            }
        });
    }
    
    private void resetScene() {
        //resetRigidBodyControl(boxControl, boxStartTransform);
        resetRigidBodyControl(sphereControl, sphereStartTransform);
    }

    private void toggleDebug() {
        bulletAppState.setDebugEnabled(!bulletAppState.isDebugEnabled());
    }

    private void resetRigidBodyControl(RigidBodyControl control, Transform startTransform) {
        control.setPhysicsLocation(startTransform.getTranslation());
        control.setPhysicsRotation(startTransform.getRotation());
        // Stop it from moving and spinning
        control.setLinearVelocity(Vector3f.ZERO);
        control.setAngularVelocity(Vector3f.ZERO);
        // Physic object might have been deactivated if there have been
        // no forces applied to it for a certain time (optimization),
        // therefore it must be reactivated.
        control.activate();
    }
    
    public void addLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0.5f, -1f, -0.5f).normalizeLocal());
        rootNode.addLight(sun);
    }
    
    public void setGeomToPhysics() {
        CollisionShape groundCollisionShape = CollisionShapeFactory.createMeshShape(board);
        RigidBodyControl groundControl = new RigidBodyControl(groundCollisionShape, 0);
        board.addControl(groundControl);
        bulletAppState.getPhysicsSpace().add(groundControl);
        
        CollisionShape sphereCollisionShape = new SphereCollisionShape(RADIUS);
        sphereControl = new RigidBodyControl(sphereCollisionShape, 3);
        ball.addControl(sphereControl);
        bulletAppState.getPhysicsSpace().add(sphereControl);
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
        Sphere s = new Sphere(10, 10, RADIUS);
        Geometry geom2 = new Geometry("Sphere", s);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", ColorRGBA.Red);  // minimum material color
        mat2.setColor("Specular", ColorRGBA.Red.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess
        
        geom2.setMaterial(mat2);
        
        //geom2.setLocalTransform(sphereStartTransform);
        
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
