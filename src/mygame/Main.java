package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
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
    Lights lights;
    Mesh mesh;

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
        rootNode.attachChild(board);

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
    }

    public void initGeom() {
        boxes = makeBoxes();
        ball = makeBall();
        board = makeBoard();
        sky = makeSky();
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
        return null;
    }

    public Geometry[] makeBoxes() {
        return null;
    }

    public Geometry makeBox() {
        return null;
    }

    public Geometry makeBall() {
        return null;
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
