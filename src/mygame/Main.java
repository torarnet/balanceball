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
    //Node pickables;
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
    boolean isPaused;

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

        // Init all methods needed to build the game. Names are self-explanatory
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

    /////////////////////////
    ///// Initial Setup /////
    /////////////////////////
    // Vars needed for the grid
    public void initVars(int i, int j) {
        amount = i;
        maxSize = j;
        boxes = new Geometry[maxSize * maxSize];
    }

    public void initClasses() {
        // Geometry class
        makeGeom = new MakeGeom(assetManager);
        lights = new Lights();
        // Custom mesh for target
        mesh = new CustomMesh();
        // Extra key input class
        keyInputs = new KeyInput(inputManager, keysPressed);
        // The HUD and Instruction class
        hudBuilder = new HudBuilder(assetManager, settings, guiNode,
                guiFont);
    }

    public void initCamera() {
        moveCamera();
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
    }

    // Moves the camera around the board based on sine and cosine waves
    // and the unity circle. cameraMove is altered by key left and key right
    // is attached to analogListeners to be called each time a key is being held
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
        // shadowPick is used to let the player see where the box is placed
        // during pick mode before he releases the button
        shadowPick = makeGeom.makeBox(0.2f, ColorRGBA.White, null);
    }

    // Have two lights to make a better shiny ball, and ambient as supplementary
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

    // Ball in center, goal in center before it is moved to another location
    public void doTranslations() {
        ball.setLocalTranslation(0, 2, 0);
        goal.setLocalTranslation(0, 1, 0);
        goal.setLocalScale(0.3f);
    }

    // Inits the custom math class where all the logic goes for the boxes and
    // target goal. 
    public void initGrid() {
        custMath = new CustomMath(maxSize, board, amount);
    }

    // Makes some nodes
    public void makeScene() {
        boardNode = new Node();
        // All the boxes go here
        boxNode = new Node();
        // ExplosionNode is used to trigger the explosion effect. This is then
        // displayed at the length of a timer given in onUpdate()
        explosionNode = new Node();

        boardNode.attachChild(explosionNode);
        boardNode.attachChild(boxNode);
        rootNode.attachChild(boardNode);
    }

    // Makes the grid for boxes and goal
    public void makeBoxGrid() {
        boxes = null;
        boxes = new Geometry[maxSize * maxSize];
        // Resets everything in CustomMath class
        custMath.clearBoxVector2();
        custMath.removeAllTargets();
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
        // Get all the vector locations for boxes
        Vector3f[][] boxLocations2 = custMath.getBoxVector2();

        // detaches the box node to be rearranged
        if (boardNode.hasChild(boxNode)) {
            boardNode.detachChild(boxNode);
            boxNode.detachAllChildren();
        }

        //Make one box for each loop and add to boxes array
        int boxCounter = 0;
        for (int i = 0; i < custMath.getMaxSize(); i++) {
            for (int j = 0; j < custMath.getMaxSize(); j++) {
                // if there should be a box
                if (boxLocations2[i][j] != null) {
                    //boxes = makeGeom.makeBoxes(16, 0.2f, ColorRGBA.Blue, boxText);
                    Geometry oneGeom = makeGeom.makeBox(0.2f, ColorRGBA.Blue, boxText);
                    // Put it where the vector ants it to be
                    oneGeom.setLocalTranslation(boxLocations2[i][j]);
                    // Add some custom data. Here it is index of positions
                    oneGeom.setUserData("PositionX", i);
                    oneGeom.setUserData("PositionZ", j);
                    // For use with picking
                    oneGeom.setUserData("topParent", oneGeom);
                    // Add to array
                    boxes[boxCounter] = oneGeom;

                    // Add to node
                    if (boxes[boxCounter] != null) {
                        boxNode.attachChild(boxes[boxCounter]);
                    }
                    boxCounter++;
                }
            }
        }

        // Move the goal
        goal.setLocalTranslation(custMath.getTargetCoords());
        // Reattach to boardNode
        boardNode.attachChild(boxNode);
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

    ///////////////////////////////
    ///// Key/Mouse Listeners /////
    ///////////////////////////////
    public void initKeys() {
        // Gets all key mappings from KeyInput class which do not have direct
        // dependency on main class
        keyInputs.mapKeys();
        // Makes a list of strings connected to each key
        List<String> keyMappings;
        keyMappings = keyInputs.getMappings();

        // For continuus actions
        analogListener = new AnalogListener() {
            public void onAnalog(String name, float keyPressed, float tpf) {
                // Rotates the camera around the board based on keyleft/keyright
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
                    if (!isPaused) {
                        pause();
                    }
                    showInstructions();
                }
                // Difficulties, takes amount and maxSize as params
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

    // RawMouseListener to constantly update mouse coordinates.
    // Also changes box location when in picking mode
    private void initMouse() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent mme) {
                mouseY = (float) mme.getDY();
                mouseX = (float) mme.getDX();
                // Change location of box if it is picked and enabled
                if (mme.getDX() != 0 || mme.getDY() != 0) {
                    if (hasPickedObject && moveEnabled) {
                        placeObjAtContactPoint();
                    }
                }
            }
        });

    }

    public void addListener(List<String> keyMappings) {
        // All the strings from KeyInput class
        String[] stringArray = new String[keyMappings.size()];
        stringArray = keyMappings.toArray(stringArray);

        inputManager.addListener(actionListener, stringArray);
        inputManager.addListener(analogListener, stringArray);
    }

    ///////////////////
    ///// Physics /////
    ///////////////////
    private void initPhysics() {
        bulletAppState = new BulletAppState();
        // add the bullet app state to the state manager, so that bullet is
        // udpated during each rendering.
        stateManager.attach(bulletAppState);

        // Used to join the mouse coordinates with cam direction to set the 
        // force to be applied to get the ball to move
        bulletAppState.getPhysicsSpace().addTickListener(new PhysicsTickListener() {
            @Override
            public void prePhysicsTick(PhysicsSpace ps, float f) {
                // the force to apply to the sphere in each axis for moving it.
                Vector3f forceMove = new Vector3f();
                // movement of the sphere accounts for camera rotation.
                Vector3f camDir = cam.getDirection().clone();
                Vector3f camLeft = cam.getLeft();

                // Gets the force to be added in one direction
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
        // Adds collisionshapes and controls to board and ball. 
        // ball has 3 movement, board has 0 movement
        CollisionShape groundCollisionShape = CollisionShapeFactory.createMeshShape(board);
        RigidBodyControl groundControl = new RigidBodyControl(groundCollisionShape, 0);
        board.addControl(groundControl);
        bulletAppState.getPhysicsSpace().add(groundControl);

        CollisionShape sphereCollisionShape = new SphereCollisionShape(RADIUS);
        sphereControl = new RigidBodyControl(sphereCollisionShape, 3);
        ball.addControl(sphereControl);
        bulletAppState.getPhysicsSpace().add(sphereControl);

        // Collision Listener
        bulletAppState.getPhysicsSpace().addCollisionListener(new PhysicsCollisionListener() {
            public void collision(PhysicsCollisionEvent event) {
                // Get geometry and object that collided
                Spatial a = event.getNodeA();
                Spatial b = event.getNodeB();
                PhysicsCollisionObject objectA = event.getObjectA();
                PhysicsCollisionObject objectB = event.getObjectB();

                // If ball crash with goal, call moveTarget() to relocate goal
                if (((objectA == sphereControl) && (objectB == goalControl))
                        || ((objectA == goalControl) && (objectB == sphereControl))) {
                    moveTarget();
                }

                // For each box control, check if this collides with ball
                for (RigidBodyControl oneBoxCtrl : boxControl) {
                    if (((objectA == oneBoxCtrl) && (objectB == sphereControl))) {
                        // Crashed into box, remove one life and remove the box
                        removeLife();
                        a.removeFromParent();
                        // let customMath class know there is no longer a 
                        // box there
                        removeBox(Integer.parseInt(a.getUserData("PositionX")
                                .toString()),
                                Integer.parseInt(a.getUserData("PositionZ")
                                .toString()), oneBoxCtrl);

                        // Makes a box explosion in a simple way and put it
                        // in same place as box collided with. Is there till
                        // a timer resets.
                        // NOTE: Tried to use my own custom shader, which is 
                        // included in source code. Got problems where all 
                        // instances of boxes used same animation in sync.
                        // Therefore had to do it this way instead. 
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

    // Adds physics control to each box made
    public void setBoxesToPhysics() {
        boxControl = new RigidBodyControl[amount];
        CollisionShape boxCollisionShape = new BoxCollisionShape(new Vector3f(BOXDIMENSION, BOXDIMENSION, BOXDIMENSION));
        for (int i = 0; i < boxControl.length; i++) {
            boxControl[i] = new RigidBodyControl(boxCollisionShape, 0);
            boxes[i].addControl(boxControl[i]);
            bulletAppState.getPhysicsSpace().add(boxControl[i]);
        }
    }

    // Same with goal
    public void setGoalToPhysics() {
        CollisionShape goalCollisionShape = new SphereCollisionShape(RADIUS / 2);
        goalControl = new RigidBodyControl(goalCollisionShape, 0);
        goal.addControl(goalControl);
        bulletAppState.getPhysicsSpace().add(goalControl);
    }

    // Resets the physics scene
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

    ///////////////////
    ///// Picking /////
    ///////////////////
    // Toggles pickmode
    public void pickMode() {
        // If game is not in pause mode
        if (!isPaused) {
            // Toggle physics
            bulletAppState.setEnabled(!(bulletAppState.isEnabled()));
            // Toggle this picking mode
            pickMode = !pickMode;
            // Show cursor so we can see what we pick
            inputManager.setCursorVisible(pickMode);
            // All the objects that are pickable are added to this node.
            //pickables = new Node("pickables");
            // Remove shadow of box when pickmode is exited
            if (!pickMode) {
                shadowPick.removeFromParent();
            }
        }
    }

    // Is there an object where we pick
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

        // If there is something to be picked and we have pick opurtunities left
        if (result != null && picks > 0) {
            hasPickedObject = true;
            // The actual geometry we collided with, this can be any sub mesh
            // of the model.
            pickedObject = result.getGeometry();
            // Gets the current place of geometry, to be used incase
            // the place where we drop the box is not available, it puts the
            // box back where it came from
            lastPlace = new Vector3f(pickedObject.getLocalTranslation());
            // Place a shadow for easier picking
            boxNode.attachChild(shadowPick);
            // Therefore we use the dirty hack, and obtain the handle to the
            // topmost parent for the sub mesh, so that we can manipulate the
            // object as a whole.
            // Here the name given earlier to the geometry is used
            pickedObject = pickedObject.getUserData("topParent");

            //custMath.adjustPos(pickedObject.getLocalTranslation());

            // Disable physics for the picked object
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
            // Remove box from index when it is picked
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
        // Puts the geometry back in the box node
        //System.out.println("released " + pickedObject.getName());
        pickedObject.removeFromParent();
        boxNode.attachChild(pickedObject);
        // Adjusts the position so it fits with the grid
        Vector3f newPlace = custMath.adjustPos(pickedObject.getLocalTranslation());
        // If the place is unoccupied
        if (!custMath.isElmTaken(newPlace)) {
            // Put at new position
            pickedObject.setLocalTranslation(newPlace);
            //pickedObject.setLocalTranslation(custMath.adjustPos(newPlace));
            // Add to index
            custMath.addBoxAt(0, 0, pickedObject.getLocalTranslation());
            // Remove Shadow
            shadowPick.removeFromParent();
            // Remove one pick opportunity
            removeOnePick();
        } else {
            // If place taken, put box at last place and also add the old index
            pickedObject.setLocalTranslation(lastPlace);
            custMath.addBoxAt(0, 0, pickedObject.getLocalTranslation());
        }
        // Try to start the physics again
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
        // This method follows the mouse using the 
        // CollisionResult.getContactPoint. Then it sets the transformation
        // using these coordinates, one little step at a time

        // This is called each time the mouse moves (from onAnalog), 
        // continually updating the pickedObject location using getContactPoint

        // As long as we have the floor in the same node, 
        // it will register new mouse coordinates as long as we have
        // the mouse over the board geometry.

        // Always update the shadowpick to the nearest grid location
        shadowPick.setLocalTranslation(custMath.adjustPos(pickedObject.getLocalTranslation()));

        CollisionResult moveToPoint = pickIfAny();
        if (moveToPoint != null) {
            Vector3f loc = moveToPoint.getContactPoint();
            loc.y += objOffsetY;
            pickedObject.setLocalTranslation(loc);
        }
    }

    ///////////////
    ///// HUD /////
    ///////////////
    // Makes a simple HUD. Tried with appstates but would then have to redo
    // the whole application and divide everything into states. 
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

    /////////////////
    ///// Other /////
    /////////////////
    // Reset all physics
    private void resetAllPhysics() {
        resetBoxPhysics();
        resetGoalPhysics();
        resetBallPhysics();
    }

    // Restart physics
    private void reInitiateAllPhysics() {
        setBoxesToPhysics();
        setGoalToPhysics();
    }

    // Does a full reset, with new grid and new physics. Reset score, life and
    // pick opportunities.
    private void resetAll() {
        resetAllPhysics();
        makeBoxGrid();
        reInitiateAllPhysics();
        resetLife();
        resetPicks();
        score = 0;
        explosionNode.detachAllChildren();
    }

    // Sets a new amount of boxes and grid size. Then resets
    private void changeDifficulty(int amount, int grid) {
        initVars(amount, grid);
        initGrid();
        resetAll();
        gameOver();
    }

    // Pause game
    public void pause() {
        if (!pickMode) {
            bulletAppState.setEnabled(!(bulletAppState.isEnabled()));
            isPaused = !isPaused;
        }
    }

    // If target is hit, this method is called to set target at a new location
    // and give the player som points
    public void moveTarget() {
        score += 50;
        resetGoalPhysics();
        // Removes current target and set a new one
        custMath.removeAllTargets();
        custMath.setTarget();
        goal.setLocalTranslation(custMath.getTargetCoords());
        setGoalToPhysics();
    }

    // Remove box at index while it also disables the physics
    public void removeBox(int x, int z, RigidBodyControl control) {
        //score -= 100;
        control.setEnabled(false);
        custMath.removeBoxAt(x, z);
    }

    // One life, One heart
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

    // Show instructions on how to play
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

    // Starts a new game and updates the last score
    public void gameOver() {
        lastScore = score;
        pause();
        resetAll();

        finalScoreText.setText("Last Score: " + String.format("%.2f", lastScore));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code

        // Removes all explosion nodes after 2 seconds
        if (timeCount > 2) {
            explosionNode.detachAllChildren();
            timeCount = 0;
        }

        // Resets the ball if it drops off board
        if (ball.getLocalTranslation().getY() < -10) {
            resetBallPhysics();
            removeLife();
        }

        // Show if pickmode is active or not
        if (pickMode) {
            guiNode.attachChild(hudTextPickActive);
        } else {
            guiNode.detachChild(hudTextPickActive);
        }

        // Show if game is paused
        if (isPaused) {
            guiNode.attachChild(pauseText);
        } else {
            guiNode.detachChild(pauseText);
        }

        // Always update the scroe
        hudText.setText("Current Score: " + String.format("%.2f", score));

        innerTpf = tpf;
        timeCount += tpf;

        // Rotate goal
        rotY += 4 * tpf;
        goal.setLocalRotation(new Quaternion().normalizeLocal().fromAngleNormalAxis(rotY, Vector3f.UNIT_Y));

        // Never show the cursor unless in pick mode
        if (!pickMode) {
            inputManager.setCursorVisible(false);
        }

        // Dont steal points while having a break
        if (!isPaused) {
            score -= reduceScore * tpf;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
