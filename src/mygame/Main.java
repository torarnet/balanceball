package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.List;
import util.RawInputAdapter;

/**
 * Balance Ball Main Application Class
 *
 * @author Tor Arne
 */
public class Main extends SimpleApplication {

    Geometry[] boxes;
    Geometry ball;
    Geometry board;
    Geometry sky;
    Geometry goal;
    Node boxNode;
    Node boardNode;
    Texture boardText;
    Texture sphereText;
    Texture boxText;
    MakeGeom makeGeom;
    Lights lights;
    CustomMesh mesh;
    KeyInput keyInputs;
    CustomMath custMath;
    AmbientLight ambient;
    DirectionalLight directional;
    DirectionalLight directional2;
    AnalogListener analogListener;
    ActionListener actionListener;
    
    // Physics
    private BulletAppState bulletAppState;
    private RigidBodyControl[] boxControl;
    private RigidBodyControl sphereControl;
    private RigidBodyControl goalControl;
    private final Transform sphereStartTransform = new Transform(new Vector3f(0f, 3f, 0.5f));
    private float mouseY = 0f;
    private float mouseX = 0f;
    
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;
    float cameraMove = 0;
    private boolean[] keysPressed = new boolean[0xff];
    private int amount;
    private int maxSize;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings setting = new AppSettings(true);
        setting.setTitle("BalanceBall");
        setting.setSettingsDialogImage("Interface/logo1.png");
        //setting.setFrameRate(60);
        app.setSettings(setting);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        initVars();
        initClasses();
        initCamera();
        initTextures();
        initGeom();
        addLights();
        doTranslations();
        initKeys();
        initGrid();
        makeScene();
        initPhysics();
        initMouse();
        
        rootNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);
        //rootNode.attachChild(boxes[0]);
        //rootNode.attachChild(boxes[1]);
        //rootNode.attachChild(boxes[2]);
        rootNode.attachChild(goal);

        test();

    }

    public void initVars() {
        amount = 8;
        maxSize = 4;
    }

    public void initClasses() {
        makeGeom = new MakeGeom(assetManager);
        lights = new Lights();
        mesh = new CustomMesh();
        keyInputs = new KeyInput(inputManager, keysPressed);
    }

    public void initCamera() {
        //cam.setLocation(new Vector3f(0, 7, -8));
        //cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
        moveCamera();
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
    }

    public void moveCamera() {
        //cam.setRotation(new Quaternion().fromAngleNormalAxis(cameraMove, Vector3f.UNIT_Y));
        //cam.setLocation(new Vector3f((float)Math.sin(cameraMove),7,-8));
        //cam.setLocation(new Vector3f((float)Math.cos(cameraMove),7,-8));

        float deltaX = (float) Math.cos(cameraMove);
        float deltaZ = (float) Math.sin(cameraMove);

        cam.setLocation(new Vector3f(deltaX * 8, 7, -8 * deltaZ));

        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);

    }

    public void initTextures() {
        boardText = assetManager.loadTexture("Textures/board1.jpg");
        //sphereText = assetManager.loadTexture("Textures/abs1.png");
        sphereText = assetManager.loadTexture("Textures/wall3.png");
        boxText = assetManager.loadTexture("Textures/box2.png");
    }

    public void initGeom() {
        //boxes = makeBoxes();
        //ball = makeBall();
        //board = makeBoard();
        //sky = makeSky();

        sky = makeGeom.makeSky(sphereText);
        board = makeGeom.makeBoard(boardText);
        boxes = makeGeom.makeBoxes(5, 0.2f, ColorRGBA.Blue, null);
        ball = makeGeom.makeBall(RADIUS, ColorRGBA.Red);
        goal = makeGeom.makeGoal(ColorRGBA.Yellow);

    }

    public void addLights() {
        ambient = lights.getAmbientLight(ColorRGBA.White);
        directional = lights.getDirectionalLight(ColorRGBA.White,
                new Vector3f(1, 1, 0));
        directional2 = lights.getDirectionalLight(ColorRGBA.White,
                new Vector3f(-1, -1, 0));
        rootNode.addLight(ambient);
        rootNode.addLight(directional);
        rootNode.addLight(directional2);
    }

    public void doTranslations() {
        ball.setLocalTranslation(0, 2, 0);
        goal.setLocalTranslation(0, 1, 0);
        goal.setLocalScale(0.3f);
    }

    public void doScaling() {
        goal.setLocalScale(0.3f);
    }

    public void initKeys() {
        keyInputs.mapKeys();
        List<String> keyMappings;
        keyMappings = keyInputs.getMappings();

        // For continuus actions
        analogListener = new AnalogListener() {
            public void onAnalog(String name, float keyPressed, float tpf) {
                /**
                 * TODO: test for mapping names and implement actions
                 */
                // Does commands based on keymapped names
                // Increases and decreases x,y,z values if keys are pressed
                if (name.equals("MoveLeft")) {         // test?
                    cameraMove -= 3.0f * tpf;
                    moveCamera();
                }
                if (name.equals("MoveRight")) {         // test?
                    cameraMove += 3.0f * tpf;
                    moveCamera();
                }

            }
        };

        actionListener = new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Reset") && isPressed) {
                    resetScene();
                    test();
                    //resetScene();
                    resetBall();
                    
                    //sphereControl.setPhysicsLocation(new Vector3f(0,3,0.5f));
                }
            }
        };

        addListener(keyMappings);

    }

    public void addListener(List<String> keyMappings) {
        String[] stringArray = new String[keyMappings.size()];
        stringArray = keyMappings.toArray(stringArray);

        //inputManager.addListener(analogListener, new String[]{"MoveLeft","MoveRight",
        //  "MoveY","MoveY2","MoveZ","MoveZ2"});

        inputManager.addListener(actionListener, stringArray);
        inputManager.addListener(analogListener, stringArray);
    }

    public void initGrid() {
        custMath = new CustomMath(maxSize, board, amount);
    }

    public void makeScene() {
        boardNode = new Node();
        boxNode = new Node();

        boardNode.attachChild(boxNode);
        rootNode.attachChild(boardNode);
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
                /*
                if (keysPressed[com.jme3.input.KeyInput.KEY_LEFT]) {
                    forceMove.addLocal(camLeft);
                }
                if (keysPressed[com.jme3.input.KeyInput.KEY_RIGHT]) {
                    forceMove.subtractLocal(camLeft);
                }
                if (keysPressed[com.jme3.input.KeyInput.KEY_UP]) {
                    forceMove.addLocal(camDir);
                }
                if (keysPressed[com.jme3.input.KeyInput.KEY_DOWN]) {
                    forceMove.subtractLocal(camDir);
                }
                * */
                //forceMove.addLocal(mouseX,0,0);
                forceMove.subtractLocal(camLeft.mult(mouseX));
                forceMove.addLocal(camDir.mult(mouseY));
                
                //forceMove.addLocal(mouseX,0,0);
                //forceMove.subtractLocal(0,0,mouseY);
                
                    forceMove.y = 0;
                
                if (forceMove.length() > 0) {
                    forceMove.normalize();
                }
                forceMove.multLocal(7f);
                // apply the force to the center of the sphere.
                sphereControl.applyCentralForce(forceMove);
            }

            @Override
            public void physicsTick(PhysicsSpace ps, float f) {
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
    
    public void setGeomToPhysics() {
        CollisionShape groundCollisionShape = CollisionShapeFactory.createMeshShape(board);
        RigidBodyControl groundControl = new RigidBodyControl(groundCollisionShape, 0);
        board.addControl(groundControl);
        bulletAppState.getPhysicsSpace().add(groundControl);

        
        CollisionShape sphereCollisionShape = new SphereCollisionShape(RADIUS);
        sphereControl = new RigidBodyControl(sphereCollisionShape, 3);
        ball.addControl(sphereControl);
        bulletAppState.getPhysicsSpace().add(sphereControl);
        
        
        CollisionShape goalCollisionShape = new SphereCollisionShape(RADIUS/2);
        goalControl = new RigidBodyControl(goalCollisionShape, 0);
        goal.addControl(goalControl);
        bulletAppState.getPhysicsSpace().add(goalControl);
        
        boxControl = new RigidBodyControl[amount];
        CollisionShape boxCollisionShape = new BoxCollisionShape(new Vector3f(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION));
        for (int i=0;i<boxControl.length;i++) {
            boxControl[i] = new RigidBodyControl(boxCollisionShape, 0);
            boxes[i].addControl(boxControl[i]);
            bulletAppState.getPhysicsSpace().add(boxControl[i]);
        }
        //bulletAppState.getPhysicsSpace().add(boxControl);
        
    }
    
    private void resetScene() {
        for (RigidBodyControl oneBoxCtrl : boxControl) {
            oneBoxCtrl.setEnabled(false);
            //resetRigidBodyControl(oneBoxCtrl,new Transform(oneBoxCtrl.getPhysicsLocation(),oneBoxCtrl.getPhysicsRotation(Quaternion.IDENTITY)));
        }
        //resetRigidBodyControl(boxControl, boxStartTransform);
        resetRigidBodyControl(sphereControl, sphereStartTransform);
        goalControl.setEnabled(false);
        resetRigidBodyControl(goalControl,
                new Transform(goalControl.getPhysicsLocation(),goalControl.getPhysicsRotation(Quaternion.IDENTITY)));
        mouseY=0;
        mouseX=0;
    }
    
    private void resetBall() {
        sphereControl.setPhysicsLocation(Vector3f.UNIT_Y);
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

    public void test() {

        //float[] arr = custMath.getBoardDimension(board);

        boxes=null;
        boxes = new Geometry[maxSize*maxSize];
        custMath.clearBoxVector();
        custMath.clearBoxVector2();
        custMath.removeAllTargets();
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
        Vector3f[] boxLocations = custMath.getBoxVector();
        Vector3f[][] boxLocations2 = custMath.getBoxVector2();

        //Node boxNode = new Node();

        if (boardNode.hasChild(boxNode)) {
            boardNode.detachChild(boxNode);
            boxNode.detachAllChildren();
            //boxNode.removeFromParent();
        }

        /* 
         for (Vector3f oneBox : boxLocations) {
         if (oneBox != null) {
         Geometry oneGeom = makeGeom.makeBox(0.2f, ColorRGBA.Blue, boxText);
         oneGeom.setLocalTranslation(oneBox);
         boxNode.attachChild(oneGeom);
         }
         }
         */

        //System.out.println(boxLocations2[0][0].getX());
        int boxCounter=0;
        for (int i = 0; i < custMath.getMaxSize(); i++) {
            for (int j = 0; j < custMath.getMaxSize(); j++) {
                if (boxLocations2[i][j] != null) {
                    //boxes = makeGeom.makeBoxes(16, 0.2f, ColorRGBA.Blue, boxText);
                    
                    Geometry oneGeom = makeGeom.makeBox(0.2f, ColorRGBA.Blue, boxText);
                    oneGeom.setLocalTranslation(boxLocations2[i][j]);
                    oneGeom.setUserData("PositionX", i);
                    oneGeom.setUserData("PositionZ", j);
                    boxes[boxCounter]=oneGeom;
                    
                    System.out.println(boxCounter);
                    if (boxes[boxCounter]!=null)boxNode.attachChild(boxes[boxCounter]);
                    boxCounter++;
                }
            }
        }
        goal.setLocalTranslation(custMath.getTargetCoords());


        setGeomToPhysics();
        
        boardNode.attachChild(boxNode);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        //cameraMove+=cameraMove*tpf;
        //moveCamera();
        inputManager.setCursorVisible(false);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
