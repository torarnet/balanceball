package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
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
    Lights lights;
    Mesh mesh;
    AmbientLight ambient;
    DirectionalLight directional;
    DirectionalLight directional2;
    final float RADIUS = 0.2f;
    final float BOXDIMENSION = 0.2f;

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
MakeGeom make = new MakeGeom(assetManager);
        initClasses();
        initCamera();
        initTextures();
        initGeom();
        addLights();
        doTranslations();
        rootNode.attachChild(board);
        rootNode.attachChild(sky);
        rootNode.attachChild(ball);

    }

    public void initClasses() {
        lights = new Lights();
        mesh = new Mesh();
    }

    public void initCamera() {
        cam.setLocation(new Vector3f(0, 7, -8));
        cam.lookAt(new Vector3f(0, 1, 0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
    }

    public void initTextures() {
        boardText = assetManager.loadTexture("Textures/board1.jpg");
        sphereText = assetManager.loadTexture("Textures/abs1.png");
    }

    public void initGeom() {
        boxes = makeBoxes();
        ball = makeBall();
        board = makeBoard();
        sky = makeSky();
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
    }

    public Geometry makeBoard() {
        Box b = new Box(3, 0.1f, 3);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", boardText);

        boardText.setWrap(Texture.WrapMode.Repeat);

        geom.setMaterial(mat);

        return geom;
    }

    public Geometry makeSky() {
        Sphere s = new Sphere(20, 20, 30);
        Geometry geom = new Geometry("Sphere", s);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", sphereText);
        
        s.scaleTextureCoordinates(new Vector2f(20,20));

        sphereText.setWrap(Texture.WrapMode.Repeat);
        
        //mat.setColor("Color", ColorRGBA.Blue);
        
        mat.getAdditionalRenderState().
                            setFaceCullMode(RenderState.FaceCullMode.Off);

        geom.setMaterial(mat);

        return geom;
    }

    public Geometry[] makeBoxes() {
        return null;
    }

    public Geometry makeBox() {
        return null;
    }

    public Geometry makeBall() {
        Sphere s = new Sphere(10, 10, RADIUS);
        Geometry geom = new Geometry("Sphere", s);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", ColorRGBA.Gray);  // minimum material color
        mat2.setColor("Specular", ColorRGBA.Gray.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 64f); // [1,128] for shininess

        geom.setMaterial(mat2);

        //geom2.setLocalTransform(sphereStartTransform);

        return geom;
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
