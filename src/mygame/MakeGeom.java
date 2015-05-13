/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Tor Arne
 */
public class MakeGeom {

    private AssetManager assetManager;
    private CustomMesh customMesh;
    private Mesh mesh;

    public MakeGeom(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Geometry makeSky(Texture text) {
        Sphere s = new Sphere(20, 20, 30);
        Box b = new Box(30, 30, 30);
        Geometry geom = new Geometry("Background", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", text);

        s.scaleTextureCoordinates(new Vector2f(5, 5));

        text.setWrap(Texture.WrapMode.Repeat);

        //mat.setColor("Color", ColorRGBA.Blue);

        mat.getAdditionalRenderState().
                setFaceCullMode(RenderState.FaceCullMode.Off);

        geom.setMaterial(mat);

        return geom;
    }

    public Geometry makeBoard(Texture text) {
        Box b = new Box(3, 0.1f, 3);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", text);

        text.setWrap(Texture.WrapMode.Repeat);

        geom.setMaterial(mat);

        return geom;
    }

    public Geometry makeBall(Float radius, ColorRGBA color) {
        Sphere s = new Sphere(10, 10, radius);
        Geometry geom = new Geometry("Sphere", s);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 64f); // [1,128] for shininess

        geom.setMaterial(mat2);

        //geom2.setLocalTransform(sphereStartTransform);

        return geom;
    }

    public Geometry[] makeBoxes(int amount, float size, ColorRGBA color, Texture text) {
        Geometry[] boxArray;
        boxArray = new Geometry[amount];

        for (int i = 0; i < amount; i++) {
            Geometry oneBox = makeBox(size, color, text);
            boxArray[i] = oneBox;
        }
        return boxArray;
    }

    public Geometry makeBox(float size, ColorRGBA color, Texture text) {
        Box b = new Box(size, size, size);
        Geometry geom3 = new Geometry("Box", b);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess

        if (text != null) {
            Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat3.setTexture("ColorMap", text);
            geom3.setMaterial(mat3);
        } else {
            geom3.setMaterial(mat2);
        }

        return geom3;
    }

    public Geometry makeGoal(ColorRGBA color) {
        mesh = new CustomMesh().makeGoal();

        Geometry geom3 = new Geometry("Goal", mesh);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Ambient", color.mult(0.7f));
        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 64f); // [1,128] for shininess

        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat3.setColor("Color", color);
        //mat3.setBoolean("VertexColor", true);

        // To show Vertices
        //mat3.getAdditionalRenderState().setWireframe(true);

        //mat3.getAdditionalRenderState().
        //                  setFaceCullMode(RenderState.FaceCullMode.Off);

        geom3.setMaterial(mat2);

        return geom3;
    }

    // Makes a box and expands it using custom shader for animation.
    public Geometry explosionSphere(float size, float speed) {
        Sphere s = new Sphere(10, 10, 0.5f);
        Geometry geom = new Geometry("Sphere", s);
        // Uses the custom shader called Grow. This will scale the geometry
        Material mat = new Material(assetManager,
                "MatDefs/Grow.j3md");
        
        Texture texture = assetManager.loadTexture("Textures/fire5.png");
        // Set mode to repeat, so only the fractional part is considered when we move the texture
        // coordinates.
        //s.scaleTextureCoordinates(new Vector2f(5,5));
        //texture.setWrap(Texture.WrapMode.Repeat);
        // Using shader variables for setting texture and scale factor
        mat.setTexture("Image", texture);
        //mat.setFloat("Size", 10.0f);
        //mat.setFloat("Speed", 20.0f);
        mat.setFloat("Size", size);
        mat.setFloat("Speed", speed);

        geom.setMaterial(mat);

        return geom;
    }
    
    public Geometry explosionSphere2(float size, float speed) {
        Sphere s = new Sphere(10, 10, 0.5f);
        Geometry geom = new Geometry("Sphere", s);
        // Uses the custom shader called Grow. This will scale the geometry
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        
        Texture texture = assetManager.loadTexture("Textures/fire5.png");
        // Set mode to repeat, so only the fractional part is considered when we move the texture
        // coordinates.
        //s.scaleTextureCoordinates(new Vector2f(5,5));
        //texture.setWrap(Texture.WrapMode.Repeat);
        // Using shader variables for setting texture and scale factor
        mat.setTexture("ColorMap", texture);
        //mat.setFloat("Size", 10.0f);
        //mat.setFloat("Speed", 20.0f);
        //mat.setFloat("Size", size);
        //mat.setFloat("Speed", speed);

        geom.setMaterial(mat);

        return geom;
    }
    
    public Material changeMat() {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        
        Texture texture = assetManager.loadTexture("Textures/explosion2.png");

        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);  // !
        
        mat.setTexture("ColorMap", texture);

        return mat;
    }
    
}
