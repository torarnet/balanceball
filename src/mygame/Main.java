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
    HudBuilder hudBuilder;
    // Lights
    AmbientLight ambient;
    DirectionalLight directional;
    DirectionalLight directional2;
    // Listeners
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
    Geometry shadowPick;
    Vector3f lastPlace;
    // HUD
    Picture[] heartPic;
    Picture[] boxPic;
    Picture hudPic;
    Picture instructions;
    BitmapText hudText;
    BitmapText pauseText;
    BitmapText hudTextPickActive;
    BitmapText finalScoreText;
    int hearts = 3;
    int picks = 3;
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
    private float lastScore = 0.0f;
    private float explosionSize = 1;
    private float explosionSpeed = 1;
    private float rotY = 0;
    private float innerTpf = 0;
    float timeCount = 0;
    int reduceScore = 5;
    boolean paused = false;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings setting = new AppSettings(true);
        setting.setTitle("Balance Ball");
        setting.setSettingsDialogImage("Interface/logo2.png");
        //setting.setFrameRate(60);
        app.setSettings(setting);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        setDisplayFps(false); // to hide the FPS
        setDisplayStatView(false); // to hide the statistics 

        initVars(8, 4);
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
        interpolate();

        boardNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);
        rootNode.attachChild(goal);

    }

    public void initVars(int i, int j) {
        amount = i;
        maxSize = j;
        boxes = new Geometry[maxSize * maxSize];
    }

    public void initClasses() {
        makeGeom = new MakeGeom(assetManager);
        lights = new Lights();
        mesh = new CustomMesh();
        keyInputs = new KeyInput(inputManager, keysPressed);
        hudBuilder = new HudBuilder(assetManager, settings, guiNode,
                guiFont);
    }

    public void initCamera() {
        moveCamera();
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
    }

    public void moveCamera() {
        float deltaX = (float) Math.cos(cameraMove);
        float deltaZ = (float) Math.sin(cameraMove);

        cam.setLocation(new Vector3f(deltaX * 8, 7, -8 * deltaZ));
        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
    }

    public void initTextures() {
        boardText = assetManager.loadTexture("Textures/board1.jpg");
        sphereText = assetManager.loadTexture("Textures/wall3.png");
        boxText = assetManager.loadTexture("Textures/box2.png");
    }

    public void initGeom() {
        sky = makeGeom.makeSky(sphereText);
        board = makeGeom.makeBoard(boardText);
        ball = makeGeom.makeBall(RADIUS, ColorRGBA.Red);
        goal = makeGeom.makeGoal(ColorRGBA.Yellow);
        shadowPick = makeGeom.makeBox(0.2f, ColorRGBA.White, null);
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

    public void initKeys() {
        keyInputs.mapKeys();
        List<String> keyMappings;
        keyMappings = keyInputs.getMappings();

        // For continuus actions
        analogListener = new AnalogListener() {
            public void onAnalog(String name, float keyPressed, float tpf) {
                if (name.equals("MoveLeft")) {
                    cameraMove -= 3.0f * tpf;
                    moveCamera();
                }
                if (name.equals("MoveRight")) {
                    cameraMove += 3.0f * tpf;
                    moveCamera();
                }
            }
        };

        actionListener = new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Reset") && isPressed) {
                    gameOver();
                }
                if (name.equals("Pick") && isPressed) {
                    pickMode();
                }
                if (name.equals("Pause") && isPressed) {
                    pause();
                }
                if (name.equals("Instructions") && isPressed) {
                    if (!paused) {
                        pause();
                    }
                    showInstructions();
                }
                if (name.equals("Easy") && isPressed) {
                    changeDifficulty(8, 4);
                }
                if (name.equals("Medium") && isPressed) {
                    changeDifficulty(14, 6);
                }
                if (name.equals("Hard") && isPressed) {
                    changeDifficulty(20, 6);
                }
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
    
    private void initMouse() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent mme) {
                mouseY = (float) mme.getDY();
                mouseX = (float) mme.getDX();
                if (mme.getDX() != 0 || mme.getDY() != 0) {
                    if (hasPickedObject && moveEnabled) {
                        placeObjAtContactPoint();
                    }
                }
            }
        });

    }

    private void changeDifficulty(int amount, int grid) {
        initVars(amount, grid);
        initGrid();
        resetAll();
        gameOver();
    }

    private void resetAll() {
        resetAllPhysics();
        makeBoxGrid();
        reInitiateAllPhysics();
        resetLife();
        resetPicks();
        score = 0;
        explosionNode.detachAllChildren();
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

                forceMove.subtractLocal(camLeft.mult(mouseX));
                forceMove.addLocal(camDir.mult(mouseY));

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

    

    public void setInitialPhysics() {
        CollisionShape groundCollisionShape = CollisionShapeFactory.createMeshShape(board);
        RigidBodyControl groundControl = new RigidBodyControl(groundCollisionShape, 0);
        board.addControl(groundControl);
        bulletAppState.getPhysicsSpace().add(groundControl);


        CollisionShape sphereCollisionShape = new SphereCollisionShape(RADIUS);
        sphereControl = new RigidBodyControl(sphereCollisionShape, 3);
        ball.addControl(sphereControl);
        bulletAppState.getPhysicsSpace().add(sphereControl);

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
                        removeLife();
                        a.removeFromParent();
                        removeBox(Integer.parseInt(a.getUserData("PositionX")
                                .toString()),
                                Integer.parseInt(a.getUserData("PositionZ")
                                .toString()), oneBoxCtrl);

                        Spatial explosion = a.clone();
                        explosion.setMaterial(makeGeom.changeMat());
                        explosionNode.attachChild(explosion);
                    }
                    if (((objectA == sphereControl) && (objectB == oneBoxCtrl))) {
                        removeLife();
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
        resetBoxPhysics();
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
        custMath.clearBoxVector2();
        custMath.removeAllTargets();
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
        Vector3f[][] boxLocations2 = custMath.getBoxVector2();

        if (boardNode.hasChild(boxNode)) {
            boardNode.detachChild(boxNode);
            boxNode.detachAllChildren();
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
        //score -= 100;
        control.setEnabled(false);
        custMath.removeBoxAt(x, z);
    }

    public void makeHud() {
        hudPic = hudBuilder.initHudPic();
        heartPic = hudBuilder.initHeartPics();
        pauseText = hudBuilder.initPauseText();
        boxPic = hudBuilder.initBoxText();
        BitmapText hudTextLife = hudBuilder.initHudTextLife();
        BitmapText hudTextPick = hudBuilder.initHudTextPick();
        hudTextPickActive = hudBuilder.initHudTextPickActive();
        BitmapText hudTextInstruct = hudBuilder.initHudTextInstruct();
        hudText = hudBuilder.initHudTextScore();
        finalScoreText = hudBuilder.initFinalScoreText();
        instructions = hudBuilder.initInstructions();

        guiNode.attachChild(hudPic);
        guiNode.attachChild(heartPic[0]);
        guiNode.attachChild(heartPic[1]);
        guiNode.attachChild(heartPic[2]);
        guiNode.attachChild(boxPic[0]);
        guiNode.attachChild(boxPic[1]);
        guiNode.attachChild(boxPic[2]);
        guiNode.attachChild(hudTextLife);
        guiNode.attachChild(hudTextPick);
        guiNode.attachChild(hudTextInstruct);
        guiNode.attachChild(hudText);
        guiNode.attachChild(finalScoreText);
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
        if (!paused) {
            bulletAppState.setEnabled(!(bulletAppState.isEnabled()));
            pickMode = !pickMode;
            inputManager.setCursorVisible(pickMode);
            // All the objects that are pickable are added to this node.
            //pickables = new Node("pickables");
            if (!pickMode) {
                shadowPick.removeFromParent();
            }
        }
    }

    public void pause() {
        if (!pickMode) {
            bulletAppState.setEnabled(!(bulletAppState.isEnabled()));
            paused = !paused;
        }
    }

    public CollisionResult pickIfAny() {
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

        if (result != null && picks > 0) {
            hasPickedObject = true;
            // The actual geometry we collided with, this can be any sub mesh
            // of the model.
            pickedObject = result.getGeometry();
            lastPlace = new Vector3f(pickedObject.getLocalTranslation());
            // Therefore we use the dirty hack, and obtain the handle to the
            // topmost parent for the sub mesh, so that we can manipulate the
            // object as a whole.
            // Here the name given earlier to the geometry is used
            boxNode.attachChild(shadowPick);
            pickedObject = pickedObject.getUserData("topParent");

            //custMath.adjustPos(pickedObject.getLocalTranslation());

            try {
                pickedObject.getControl(RigidBodyControl.class).setEnabled(false);
            } catch (Exception e) {
                // No Physics found
            }

            if (pickedObject == null) {
                hasPickedObject = false;
                return;
            }

            // Remove the picked object from the pickable node, and add it
            // directly to the root of the scene graph.
            // This way it can be moved all around the scene graph.
            pickedObject.removeFromParent();
            custMath.removeBoxAt(Integer.parseInt(pickedObject.getUserData("PositionX")
                    .toString()),
                    Integer.parseInt(pickedObject.getUserData("PositionZ")
                    .toString()));
            rootNode.attachChild(pickedObject);
            //System.out.println("picked " + pickedObject.getName());
        } else {
            //System.out.println("picked nothing");
        }
    }

    private void unpick() {
        // This method removes the picked object from the root of the scene
        // graph, and adds it back to the pickable node.
        if (!hasPickedObject) {
            return;
        }
        // Puts the geometry back in the pickable node for further picking
        //System.out.println("released " + pickedObject.getName());
        pickedObject.removeFromParent();
        boxNode.attachChild(pickedObject);
        Vector3f newPlace = custMath.adjustPos(pickedObject.getLocalTranslation());
        //if (!custMath.isElmTaken(pickedObject.getLocalTranslation())) {
        if (!custMath.isElmTaken(newPlace)) {
            pickedObject.setLocalTranslation(custMath.adjustPos(newPlace));
            //pickedObject.setLocalTranslation(custMath.adjustPos(pickedObject.getLocalTranslation()));
            custMath.addBoxAt(0, 0, pickedObject.getLocalTranslation());
            shadowPick.removeFromParent();
            removeOnePick();
        } else {
            pickedObject.setLocalTranslation(lastPlace);
        }
        try {
            pickedObject.getControl(RigidBodyControl.class).setEnabled(true);
        } catch (Exception e) {
            // No Physics found
        }
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

        shadowPick.setLocalTranslation(custMath.adjustPos(pickedObject.getLocalTranslation()));

        CollisionResult moveToPoint = pickIfAny();
        if (moveToPoint != null) {
            Vector3f loc = moveToPoint.getContactPoint();
            loc.y += objOffsetY;
            pickedObject.setLocalTranslation(loc);
            //custMath.adjustPos(pickedObject.getLocalTranslation());
            //pickedObject.setLocalTranslation(custMath.adjustPos(pickedObject.getLocalTranslation()));
        }
    }

    public void removeLife() {
        if (hearts > 0) {
            hearts--;
            guiNode.detachChild(heartPic[hearts]);
        }
        if (hearts == 0) {
            gameOver();
        }
    }

    public void resetLife() {
        hearts = 3;
        guiNode.attachChild(heartPic[0]);
        guiNode.attachChild(heartPic[1]);
        guiNode.attachChild(heartPic[2]);
    }

    public void removeOnePick() {
        if (picks > 0) {
            picks--;
            guiNode.detachChild(boxPic[picks]);
        } else {
        }
    }

    public void showInstructions() {
        if (guiNode.hasChild(instructions)) {
            guiNode.detachChild(instructions);
        } else {
            guiNode.attachChild(instructions);
        }
    }

    public void resetPicks() {
        picks = 3;
        guiNode.attachChild(boxPic[0]);
        guiNode.attachChild(boxPic[1]);
        guiNode.attachChild(boxPic[2]);
    }

    public void gameOver() {
        lastScore = score;
        pause();
        resetAll();

        finalScoreText.setText("Last Score: " + String.format("%.2f", lastScore));
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

        if (ball.getLocalTranslation().getY() < -10) {
            resetBallPhysics();
            removeLife();
        }

        if (pickMode) {
            guiNode.attachChild(hudTextPickActive);
        } else {
            guiNode.detachChild(hudTextPickActive);
        }

        if (paused) {
            guiNode.attachChild(pauseText);
        } else {
            guiNode.detachChild(pauseText);
        }

        innerTpf = tpf;
        timeCount += tpf;

        rotY += 4 * tpf;

        goal.setLocalRotation(new Quaternion().normalizeLocal().fromAngleNormalAxis(rotY, Vector3f.UNIT_Y));
        if (!pickMode) {
            inputManager.setCursorVisible(false);
            try {
                //shadowPick.removeFromParent();
            } catch (Exception e) {
            }
        }
        //hudText.setText("Current Score: "+score); 
        hudText.setText("Current Score: " + String.format("%.2f", score));
        if (!paused) {
            score -= reduceScore * tpf;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
