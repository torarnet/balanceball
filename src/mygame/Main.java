package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

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
    Texture boardText;
    Texture sphereText;
    MakeGeom makeGeom;
    Lights lights;
    CustomMesh mesh;
    AmbientLight ambient;
    DirectionalLight directional;
    DirectionalLight directional2;
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;
    float cameraMove = 0;

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
        rootNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);
        rootNode.attachChild(boxes[0]);
        rootNode.attachChild(boxes[1]);
        rootNode.attachChild(boxes[2]);

    }

    public void initClasses() {
        makeGeom = new MakeGeom(assetManager);
        lights = new Lights();
        mesh = new CustomMesh();
        
    }

    public void initCamera() {
        cam.setLocation(new Vector3f(0, 7, -8));
        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
    }
    
    public void moveCamera() {
        //cam.setRotation(new Quaternion().fromAngleNormalAxis(cameraMove, Vector3f.UNIT_Y));
        //cam.setLocation(new Vector3f((float)Math.sin(cameraMove),7,-8));
        //cam.setLocation(new Vector3f((float)Math.cos(cameraMove),7,-8));
        
        float deltaX = (float)Math.cos(cameraMove);
        float deltaZ = (float)Math.sin(cameraMove);
        
        cam.lookAt(new Vector3f(0,1,0), Vector3f.UNIT_Y);

        cam.setLocation(new Vector3f(deltaX*8,7,-8*deltaZ));

    }

    public void initTextures() {
        boardText = assetManager.loadTexture("Textures/board1.jpg");
        sphereText = assetManager.loadTexture("Textures/abs1.png");
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
    }

    

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        cameraMove+=1.0f*tpf;
        moveCamera();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
