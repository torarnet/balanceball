/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseMotionEvent;
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
    private RigidBodyControl boxControl;
    private final Transform boxStartTransform = new Transform(
            new Vector3f(0, 3, -0.75f), new Quaternion(new float[]{1, 0, 1}));
    private RigidBodyControl boxControl2;
    private final Transform boxStartTransform2 = new Transform(
            new Vector3f(2, 3, -0.75f), new Quaternion(new float[]{1, 0, 1}));
    private RigidBodyControl sphereControl;
    private final Transform sphereStartTransform = new Transform(new Vector3f(0f, 3, 1f));
    private boolean[] keysPressed = new boolean[0xff];
    private float mouseY = 0f;
    private float mouseX = 0f;
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;

    public static void main(String[] args) {
        PhysicsSkel app = new PhysicsSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //getFlyByCamera().setMoveSpeed(10);
        //flyCam.setEnabled(false);
        //inputManager.setCursorVisible(false);
        
        viewPort.setBackgroundColor(ColorRGBA.Magenta.mult(0.5f));

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

        addLights();
        initCamera();
        initPhysics();
        initInput();
        initMouse();
        setGeomToPhysics();

    }

    private void initCamera() {
        cam.setLocation(new Vector3f(-3, 5, 6));
        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
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
                forceMove.addLocal(mouseX,0,0);
                forceMove.subtractLocal(0,0,mouseY);
                
                    forceMove.y = 0;
                
                if (forceMove.length() > 0) {
                    forceMove.normalize();
                }
                forceMove.multLocal(10f);
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

    private void initMouse() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent mme) {
                mouseY = (float)mme.getDY();
                mouseX = (float)mme.getDX();
            }
        });

    }

    private void resetScene() {
        resetRigidBodyControl(boxControl, boxStartTransform);
        resetRigidBodyControl(boxControl2, boxStartTransform2);
        resetRigidBodyControl(sphereControl, sphereStartTransform);
        mouseY=0f;
        mouseX=0;
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

        obstacleOne.setLocalTransform(boxStartTransform);
        CollisionShape boxCollisionShape = new BoxCollisionShape(new Vector3f(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION));
        // this control has a mass of 1. Any mass different than 0 means that it
        // is dynamic, and this will make the box fall when added to the physics
        // space.
        boxControl = new RigidBodyControl(boxCollisionShape, 1);
        obstacleOne.addControl(boxControl);
        bulletAppState.getPhysicsSpace().add(boxControl);

        obstacleTwo.setLocalTransform(boxStartTransform2);
        CollisionShape boxCollisionShape2 = new BoxCollisionShape(new Vector3f(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION));

        boxControl2 = new RigidBodyControl(boxCollisionShape2, 1);
        obstacleTwo.addControl(boxControl2);
        bulletAppState.getPhysicsSpace().add(boxControl2);
    }

    public Geometry makeBoard() {
        Box b = new Box(3, 0.1f, 3);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture text = assetManager.loadTexture("Textures/board3.png");

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

        mat2.setColor("Diffuse", ColorRGBA.Gray);  // minimum material color
        mat2.setColor("Specular", ColorRGBA.Gray.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess

        geom2.setMaterial(mat2);

        //geom2.setLocalTransform(sphereStartTransform);

        return geom2;
    }

    public Geometry makeObstacle(ColorRGBA color) {
        Box b = new Box(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION);
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
        System.out.println("X: " + mouseX + "Y: "+mouseY);
        inputManager.setCursorVisible(false);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
}
