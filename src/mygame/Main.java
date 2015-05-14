package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.List;
import util.*;

/**
 * Balance Ball Main Application Class
 *
 * @author Tor Arne
 */
public class Main extends SimpleApplication {

    // Geometry, Textures and Nodes
    Geometry[] boxes;
    Geometry ball;
    Geometry board;
    Geometry sky;
    Geometry goal;
    Node boxNode;
    Node boardNode;
    Node explosionNode;
    Texture boardText;
    Texture sphereText;
    Texture boxText;
    // Custom classes and other classes
    MakeGeom makeGeom;
    Lights lights;
    CustomMesh mesh;
    KeyInput keyInputs;
    CustomMath custMath;
    BitmapText hudText;
    // Lights
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
    // Picking
    boolean moveEnabled = false;
    boolean hasPickedObject = false;
    Spatial pickedObject = null;
    float objOffsetY = 0.3f;
    boolean pickMode = false;
    Node pickables;
    // Vars
    private float mouseY = 0f;
    private float mouseX = 0f;
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;
    float cameraMove = 0;
    private boolean[] keysPressed = new boolean[0xff];
    private int amount;
    private int maxSize;
    private float score = 0.0f;
    private float explosionSize = 1;
    private float explosionSpeed = 1;
    private float rotY = 0;
    private float innerTpf = 0;
    float timeCount = 0;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings setting = new AppSettings(true);
        setting.setTitle("BalanceBall");
        setting.setSettingsDialogImage("Interface/logo2.png");
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
        setInitialPhysics();
        makeBoxGrid();
        setBoxesToPhysics();
        setGoalToPhysics();
        makeHud();

        boardNode.attachChild(board);
        //rootNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);
        rootNode.attachChild(goal);

        interpolate();
        //test();

    }

    public void initVars() {
        amount = 12;
        maxSize = 6;
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

        //cam.setLocation(new Vector3f(deltaX * 6, 7, -6 * deltaZ));
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
                // Move object by calling placeObjAtContactPoint() if mouse moves
                /*if (name.equals(MouseMoveLeft) || name.equals(MouseMoveRight)
                        || name.equals(MouseMoveUp) || name.equals(MouseMoveDown)) {
                    if (hasPickedObject && moveEnabled) {
                        placeObjAtContactPoint();
                    }
                }*/
                // Move object by calling placeObjAtContactPoint() if mouse moves
                
                /*
                if (mouseX>0 || mouseY>0
                        || mouseX<0 || mouseY<0) {
                    if (hasPickedObject && moveEnabled) {
                        placeObjAtContactPoint();
                    }
                }*/

            }
        };

        actionListener = new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Reset") && isPressed) {
                    resetAllPhysics();
                    makeBoxGrid();
                    reInitiateAllPhysics();
                    explosionNode.detachAllChildren();
                    //resetScene();
                    //test();
                    //resetScene();
                    //resetBall();
                    //sphereControl.setPhysicsLocation(new Vector3f(0,3,0.5f));
                }
                if (name.equals("Pick") && isPressed) {
                    pickMode();
                }
                // Check if mouse key pressed
                if (name.equals("PickDown")) {
                    if (isPressed) {
                        pick();
                        moveEnabled = true;
                    } else {
                        unpick();
                        moveEnabled = false;
                    }
                }
            }
        };

        addListener(keyMappings);

    }

    private void resetAllPhysics() {
        resetBoxPhysics();
        resetGoalPhysics();
        resetBallPhysics();
    }

    private void reInitiateAllPhysics() {
        setBoxesToPhysics();
        setGoalToPhysics();
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
        explosionNode = new Node();

        boardNode.attachChild(explosionNode);
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
                mouseY = (float) mme.getDY();
                mouseX = (float) mme.getDX();
                if (mme.getDX()!=0 || mme.getDY()!=0) {
                    if (hasPickedObject && moveEnabled) {
                        placeObjAtContactPoint();
                    }
                }
            }
        });

    }

    public void setInitialPhysics() {
        CollisionShape groundCollisionShape = CollisionShapeFactory.createMeshShape(board);
        RigidBodyControl groundControl = new RigidBodyControl(groundCollisionShape, 0);
        board.addControl(groundControl);
        bulletAppState.getPhysicsSpace().add(groundControl);


        CollisionShape sphereCollisionShape = new SphereCollisionShape(RADIUS);
        sphereControl = new RigidBodyControl(sphereCollisionShape, 3);
        ball.addControl(sphereControl);
        bulletAppState.getPhysicsSpace().add(sphereControl);

        /*
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
         */
        //bulletAppState.getPhysicsSpace().add(boxControl);

        bulletAppState.getPhysicsSpace().addCollisionListener(new PhysicsCollisionListener() {
            public void collision(PhysicsCollisionEvent event) {
                Spatial a = event.getNodeA();
                Spatial b = event.getNodeB();
                PhysicsCollisionObject objectA = event.getObjectA();
                PhysicsCollisionObject objectB = event.getObjectB();

                if (((objectA == sphereControl) && (objectB == goalControl))
                        || ((objectA == goalControl) && (objectB == sphereControl))) {
                    moveTarget();
                }

                for (RigidBodyControl oneBoxCtrl : boxControl) {
                    if (((objectA == oneBoxCtrl) && (objectB == sphereControl))) {
                        a.removeFromParent();
                        removeBox(Integer.parseInt(a.getUserData("PositionX")
                                .toString()),
                                Integer.parseInt(a.getUserData("PositionZ")
                                .toString()), oneBoxCtrl);
                        Geometry explosion = makeGeom.explosionSphere(10, 50);
                        explosion.setLocalTranslation(b.getLocalTranslation());
                        boxNode.attachChild(explosion);
                    }
                    if (((objectA == sphereControl) && (objectB == oneBoxCtrl))) {
                        b.removeFromParent();
                        removeBox(Integer.parseInt(b.getUserData("PositionX")
                                .toString()),
                                Integer.parseInt(b.getUserData("PositionZ")
                                .toString()), oneBoxCtrl);

                        Spatial explosion = b.clone();
                        explosion.setMaterial(makeGeom.changeMat());
                        explosionNode.attachChild(explosion);

                    }
                }

            }
        });

    }

    public void setBoxesToPhysics() {
        boxControl = new RigidBodyControl[amount];
        CollisionShape boxCollisionShape = new BoxCollisionShape(new Vector3f(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION));
        for (int i = 0; i < boxControl.length; i++) {
            boxControl[i] = new RigidBodyControl(boxCollisionShape, 0);
            boxes[i].addControl(boxControl[i]);
            bulletAppState.getPhysicsSpace().add(boxControl[i]);
        }
    }

    public void setGoalToPhysics() {
        CollisionShape goalCollisionShape = new SphereCollisionShape(RADIUS / 2);
        goalControl = new RigidBodyControl(goalCollisionShape, 0);
        goal.addControl(goalControl);
        bulletAppState.getPhysicsSpace().add(goalControl);
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
                new Transform(goalControl.getPhysicsLocation(), goalControl.getPhysicsRotation(Quaternion.IDENTITY)));
        mouseY = 0;
        mouseX = 0;
    }

    private void resetBoxPhysics() {
        for (RigidBodyControl oneBoxCtrl : boxControl) {
            oneBoxCtrl.setEnabled(false);
            //resetRigidBodyControl(oneBoxCtrl,new Transform(oneBoxCtrl.getPhysicsLocation(),oneBoxCtrl.getPhysicsRotation(Quaternion.IDENTITY)));
        }
    }

    private void resetGoalPhysics() {
        goalControl.setEnabled(false);
    }

    private void resetBallPhysics() {
        sphereControl.setPhysicsLocation(Vector3f.UNIT_Y);

        sphereControl.setLinearVelocity(Vector3f.ZERO);
        sphereControl.setAngularVelocity(Vector3f.ZERO);
        mouseY = 0;
        mouseX = 0;
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

    public void makeBoxGrid() {
        boxes = null;
        boxes = new Geometry[maxSize * maxSize];
        //custMath.clearBoxVector();
        custMath.clearBoxVector2();
        custMath.removeAllTargets();
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
        //Vector3f[] boxLocations = custMath.getBoxVector();
        Vector3f[][] boxLocations2 = custMath.getBoxVector2();

        if (boardNode.hasChild(boxNode)) {
            boardNode.detachChild(boxNode);
            boxNode.detachAllChildren();
            //boxNode.removeFromParent();
        }

        int boxCounter = 0;
        for (int i = 0; i < custMath.getMaxSize(); i++) {
            for (int j = 0; j < custMath.getMaxSize(); j++) {
                if (boxLocations2[i][j] != null) {
                    //boxes = makeGeom.makeBoxes(16, 0.2f, ColorRGBA.Blue, boxText);

                    Geometry oneGeom = makeGeom.makeBox(0.2f, ColorRGBA.Blue, boxText);
                    oneGeom.setLocalTranslation(boxLocations2[i][j]);
                    oneGeom.setUserData("PositionX", i);
                    oneGeom.setUserData("PositionZ", j);
                    oneGeom.setUserData("topParent", oneGeom);
                    boxes[boxCounter] = oneGeom;

                    System.out.println(boxCounter);
                    if (boxes[boxCounter] != null) {
                        boxNode.attachChild(boxes[boxCounter]);
                    }
                    boxCounter++;
                }
            }
        }
        goal.setLocalTranslation(custMath.getTargetCoords());


        //setGeomToPhysics();

        boardNode.attachChild(boxNode);

    }

    public void moveTarget() {
        score += 50;
        resetGoalPhysics();
        custMath.removeAllTargets();
        custMath.setTarget();
        goal.setLocalTranslation(custMath.getTargetCoords());
        setGoalToPhysics();
    }

    public void removeBox(int x, int z, RigidBodyControl control) {
        score -= 100;
        control.setEnabled(false);
        custMath.removeBoxAt(x, z);
    }

    public void test() {

        //float[] arr = custMath.getBoardDimension(board);

        boxes = null;
        boxes = new Geometry[maxSize * maxSize];
        //custMath.clearBoxVector();
        custMath.clearBoxVector2();
        custMath.removeAllTargets();
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
        //Vector3f[] boxLocations = custMath.getBoxVector();
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
        int boxCounter = 0;
        for (int i = 0; i < custMath.getMaxSize(); i++) {
            for (int j = 0; j < custMath.getMaxSize(); j++) {
                if (boxLocations2[i][j] != null) {
                    //boxes = makeGeom.makeBoxes(16, 0.2f, ColorRGBA.Blue, boxText);

                    Geometry oneGeom = makeGeom.makeBox(0.2f, ColorRGBA.Blue, boxText);
                    oneGeom.setLocalTranslation(boxLocations2[i][j]);
                    oneGeom.setUserData("PositionX", i);
                    oneGeom.setUserData("PositionZ", j);
                    boxes[boxCounter] = oneGeom;

                    System.out.println(boxCounter);
                    if (boxes[boxCounter] != null) {
                        boxNode.attachChild(boxes[boxCounter]);
                    }
                    boxCounter++;
                }
            }
        }
        goal.setLocalTranslation(custMath.getTargetCoords());


        //setGeomToPhysics();

        boardNode.attachChild(boxNode);

    }

    public void makeHud() {
        int width = settings.getWidth() / 3;
        int height = settings.getHeight() / 4;
        Picture hudPic = new Picture("Background");
        hudPic.setImage(assetManager, "Textures/black2.png", true);
        hudPic.setWidth(width);
        hudPic.setHeight(height);
        //hudPic.setPosition(settings.getWidth() / 4, settings.getHeight() / 4);
        hudPic.setPosition(0, settings.getHeight() - height);
        guiNode.attachChild(hudPic);

        int heartWidth = 40;
        int heartHeight = 40;
        Picture[] heartPic = new Picture[4];
        heartPic[0] = new Picture("Heart");
        heartPic[0].setImage(assetManager, "Textures/heart2.png", true);
        heartPic[0].setWidth(heartWidth);
        heartPic[0].setHeight(heartHeight);
        heartPic[1] = (Picture) heartPic[0].clone();
        heartPic[2] = (Picture) heartPic[0].clone();

        //hudPic.setPosition(settings.getWidth() / 4, settings.getHeight() / 4);
        heartPic[0].setPosition(0, settings.getHeight() - 80);
        heartPic[1].setPosition(heartWidth, settings.getHeight() - 80);
        heartPic[2].setPosition(heartWidth * 2, settings.getHeight() - 80);
        guiNode.attachChild(heartPic[0]);
        guiNode.attachChild(heartPic[1]);
        guiNode.attachChild(heartPic[2]);

        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        //hudText.setText("Current Score: "+score);             // the text
        hudText.setText("Current Score: " + String.format("%.2f", score));
        //hudText.setLocalTranslation(300, hudText.getLineHeight(), 0); // position
        hudText.setLocalTranslation(0, settings.getHeight(), 0); // position
        guiNode.attachChild(hudText);
    }

    public void interpolate() {
        // All interpolators use the same alpha object.
        // No delay, increase time of 2 seconds, no ramp, at 1 in 1 second,
        // decrease time of 2 seconds, no ramp.
        Alpha alpha = new Alpha(0, 2f, 0, 1, 2, 0, 1, 0, 0);

        // Color interpolation from red to blue.
        ColorInterpolatorControl colorInterp4 =
                new ColorInterpolatorControl(alpha.clone(), new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f),
                new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f), "Ambient", true, true);
        goal.addControl(colorInterp4);
    }

    public void pickMode() {
        bulletAppState.setEnabled(!(bulletAppState.isEnabled()));
        pickMode = !pickMode;
        inputManager.setCursorVisible(pickMode);
        // All the objects that are pickable are added to this node.
        pickables = new Node("pickables");
        //pickables.attachChild(boxNode);
        //pickables.attachChild(boxNode);
        //rootNode.attachChild(pickables);
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
        boardNode.collideWith(ray, results);

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
            
            try {
                pickedObject.getControl(RigidBodyControl.class).setEnabled(false);
            }catch (Exception e){
                // No Physics found
            };

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
        boxNode.attachChild(pickedObject);
        try {
                pickedObject.getControl(RigidBodyControl.class).setEnabled(true);
            }catch (Exception e){
                // No Physics found
            };
        //pickedObject.getControl(RigidBodyControl.class).setEnabled(true);
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

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        //cameraMove+=cameraMove*tpf;
        //moveCamera();
        
        if (timeCount > 2) {
            explosionNode.detachAllChildren();
            timeCount = 0;
        }

        innerTpf = tpf;
        timeCount += tpf;

        rotY += 4 * tpf;

        goal.setLocalRotation(new Quaternion().normalizeLocal().fromAngleNormalAxis(rotY, Vector3f.UNIT_Y));
        if (!pickMode) {
            inputManager.setCursorVisible(false);
        }
        //hudText.setText("Current Score: "+score); 
        hudText.setText("Current Score: " + String.format("%.2f", score));
        score -= 5 * tpf;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
