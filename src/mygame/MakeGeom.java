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
        this.assetManager=assetManager;
    }
    
    public Geometry makeSky(Texture text) { 
        Sphere s = new Sphere(20, 20, 30);
        Box b = new Box(30,30,30);
        Geometry geom = new Geometry("Sphere", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", text);
        
        s.scaleTextureCoordinates(new Vector2f(5,5));

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
    
    public Geometry makeBall(Float radius,ColorRGBA color) {
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
    
    public Geometry[] makeBoxes(int amount,float size,ColorRGBA color,Texture text) {
        Geometry[] boxArray;
        boxArray = new Geometry[amount];
        
        for (int i=0;i<amount;i++) {
             Geometry oneBox = makeBox(size,color,text);
             boxArray[i]=oneBox;
        }
        return boxArray;
    }

    public Geometry makeBox(float size,ColorRGBA color,Texture text) {
        Box b = new Box(size,size,size);
        Geometry geom3 = new Geometry("Box", b);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat2.setBoolean("UseMaterialColors", true);

        mat2.setColor("Diffuse", color);  // minimum material color
        mat2.setColor("Specular", color.mult(0.1f)); // for shininess
        mat2.setFloat("Shininess", 0f); // [1,128] for shininess

        geom3.setMaterial(mat2);
        
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
        mat3.setColor("Color", color);
        
        // To show Vertices
        //mat3.getAdditionalRenderState().setWireframe(true);
        
        mat3.getAdditionalRenderState().
                            setFaceCullMode(RenderState.FaceCullMode.Off);

        geom3.setMaterial(mat2);
        
        return geom3;
    }
    
}
