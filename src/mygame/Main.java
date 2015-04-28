package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.List;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    Geometry[] boxes;
    Geometry ball;
    Geometry board;
    Geometry sky;
    Geometry goal;
    Texture boardText;
    Texture sphereText;
    MakeGeom makeGeom;
    Lights lights;
    CustomMesh mesh;
    KeyInput keyInput;
    AmbientLight ambient;
    DirectionalLight directional;
    DirectionalLight directional2;
    AnalogListener analogListener;
    
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;
    float cameraMove = 0;
    private boolean[] keysPressed = new boolean[0xff];

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings setting = new AppSettings(true);
        setting.setTitle("BalanceBall");
        setting.setSettingsDialogImage("Interface/piratelogo.jpg");
        app.setSettings(setting);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        initClasses();
        initCamera();
        initTextures();
        initGeom();
        addLights();
        doTranslations();
        initKeys();
        rootNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);
        rootNode.attachChild(boxes[0]);
        rootNode.attachChild(boxes[1]);
        rootNode.attachChild(boxes[2]);
        rootNode.attachChild(goal);
        
        test();

    }

    public void initClasses() {
        makeGeom = new MakeGeom(assetManager);
        lights = new Lights();
        mesh = new CustomMesh();
        keyInput = new KeyInput(inputManager,keysPressed);
        
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
        
        float deltaX = (float)Math.cos(cameraMove);
        float deltaZ = (float)Math.sin(cameraMove);
 
        cam.setLocation(new Vector3f(deltaX*8,7,-8*deltaZ));
        
        cam.lookAt(new Vector3f(0,1,0), Vector3f.UNIT_Y);

    }

    public void initTextures() {
        boardText = assetManager.loadTexture("Textures/board1.jpg");
        //sphereText = assetManager.loadTexture("Textures/abs1.png");
        sphereText = assetManager.loadTexture("Textures/fire5.png");
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
                new Vector3f(1,1,0));
        directional2 = lights.getDirectionalLight(ColorRGBA.White, 
                new Vector3f(-1,-1,0));
        rootNode.addLight(ambient);
        rootNode.addLight(directional);
        rootNode.addLight(directional2);
    }
    
    public void doTranslations() {
        ball.setLocalTranslation(0, 2, 0);
        boxes[0].setLocalTranslation(-1, 1, 0);
        boxes[1].setLocalTranslation(1, 1, 0);
        boxes[2].setLocalTranslation(2, 1, 0);
        goal.setLocalTranslation(0, 1, 0); 
    }
    
    public void doScaling() {
        goal.setLocalScale(0.3f);
    }
    
    public void initKeys() {
        List<String> keyMappings = keyInput.getMappings();
        
        // For continuus actions
        analogListener = new AnalogListener() {
            public void onAnalog(String name, float keyPressed, float tpf) {
                /** TODO: test for mapping names and implement actions */
                
                // Does commands based on keymapped names
                // Increases and decreases x,y,z values if keys are pressed
                if (name.equals("MoveLeft")) {         // test?
                    cameraMove-=3.0f*tpf;
                    moveCamera();
                } 
                if (name.equals("MoveRight")) {         // test?
                    cameraMove+=3.0f*tpf;
                    moveCamera();
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
        
        inputManager.addListener(analogListener, stringArray);
    }
    
    public void test() {
        CustomMath custMath = new CustomMath();
        float[] arr = custMath.getBoardDimension(board);
        Geometry geom1 = makeGeom.makeBox(0.3f, ColorRGBA.Green, boardText);
        Geometry geom2 = makeGeom.makeBox(0.3f, ColorRGBA.Green, boardText);
        Geometry geom3 = makeGeom.makeBox(0.3f, ColorRGBA.Green, boardText);
        Geometry geom4 = makeGeom.makeBox(0.3f, ColorRGBA.Green, boardText);
        
        geom1.setLocalTranslation(arr[0], 0.5f, arr[2]);
        geom2.setLocalTranslation(arr[0], 0.5f, arr[3]);
        geom3.setLocalTranslation(arr[1], 0.5f, arr[2]);
        geom4.setLocalTranslation(arr[1], 0.5f, arr[3]);
        
        rootNode.attachChild(geom1);
        rootNode.attachChild(geom2);
        rootNode.attachChild(geom3);
        rootNode.attachChild(geom4);
        
        custMath.setActive();
        custMath.setTarget();
        custMath.makeGrid();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        //cameraMove+=cameraMove*tpf;
        //moveCamera();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
