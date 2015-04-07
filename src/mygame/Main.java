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

    float x = 0;
    float y = 0;
    float z = 0;
    Geometry keyGeom;

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

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0.5f, -1f, -0.5f).normalizeLocal());
        rootNode.addLight(sun);

        flyCam.setMoveSpeed(10);

        Box b = new Box(10, 0.5f, 10);
        Geometry geom = new Geometry("Box", b);
        
        // Makes a new texture object
        Texture text = assetManager.loadTexture("Textures/tile2.png");
        
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        mat.setBoolean("UseMaterialColors", true);
        mat.setTexture("DiffuseMap", text); // with Lighting.j3md
        mat.setTexture("SpecularMap", text); // with Lighting.j3md

        //material.setColor("Ambient", ColorRGBA.Red.mult(0.5f));
        mat.setColor("Diffuse", ColorRGBA.White);  // minimum material color
        mat.setColor("Specular", ColorRGBA.White.mult(0.1f)); // for shininess
        mat.setFloat("Shininess", 0f); // [1,128] for shininess

        
        // .setWrap makes it possible to use the texture several times
        // in the same geometry
        text.setWrap(Texture.WrapMode.Repeat);

        //mat.setTexture("ColorMap",text);

        //Says to use the texture 20 * 20 times to fill the surface
        //b.scaleTextureCoordinates(new Vector2f(20, 20));
        geom.setMaterial(mat);

        // Adjust the position
        geom.setLocalTranslation(new Vector3f(-5f, -4f, 5f));

        // Makes the geometry visible from both sides
        geom.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);


        //geom.setMaterial(mat);

        rootNode.attachChild(geom);

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
