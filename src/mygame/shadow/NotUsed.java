/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.shadow;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Tor Arne
 */
public class NotUsed {
    
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
    
}
