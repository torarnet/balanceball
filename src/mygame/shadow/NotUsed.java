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
/*
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
*/

/*
public class CustomMath {

    int difficulty = 1;
    float[][] boxGrid = new float[4][4];
    boolean[][] active = new boolean[4][4];
    boolean[][] target = new boolean[4][4];
    int maxSize = 4;
    float gridElmSize = 0.2f;
    float[] boardDims;
    Vector3f worldTrans;
    Vector3f[] boxLocations;

    public void setActive() {
        int counter = 0;
        active = new boolean[4][4];
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (active[i][j] == true) {
                    counter++;
                    if (counter >= maxSize * maxSize) {
                        setActive();
                    }
                }
                active[i][j] = new Random().nextBoolean();
                System.out.println("place: " + i + " and " + j + "is " + active[i][j]);
            }
        }
    }

    public void setTarget() {
        int targetX = 0;
        int targetZ = 0;
        targetX = new Random().nextInt(4);
        targetZ = new Random().nextInt(4);
        if (active[targetX][targetZ] == false) {
            target[targetX][targetZ] = true;
            System.out.println(targetX + " and " + targetZ + " is " + target[targetX][targetZ]);
        } else {
            setTarget();
        }
    }

    // Return float[startx,endx,startz,endz]
    public float[] getBoardDimension(Geometry board) {
        worldTrans = board.getWorldTranslation();

        System.out.println(worldTrans.getX());
        System.out.println(worldTrans.getY());
        System.out.println(worldTrans.getZ());

        BoundingBox box = (BoundingBox) board.getModelBound();
        Vector3f boxSize = new Vector3f();
        boxSize = box.getExtent(boxSize);

        float[] floatArray = new float[4];
        floatArray[0] = (worldTrans.getX() - boxSize.getX());
        floatArray[1] = (worldTrans.getX() + boxSize.getX());
        floatArray[2] = (worldTrans.getZ() - boxSize.getZ());
        floatArray[3] = (worldTrans.getZ() + boxSize.getZ());



        System.out.println(boxSize.getX());
        System.out.println(boxSize.getY());
        System.out.println(boxSize.getZ());

        System.out.println(floatArray[0]);
        System.out.println(floatArray[1]);
        System.out.println(floatArray[2]);
        System.out.println(floatArray[3]);

        boardDims = floatArray;

        return floatArray;
    }

    public void makeGrid() {
        boxLocations = new Vector3f[maxSize*maxSize];
        //boxGrid = new float[4][4];
        //maxSize = 16;
        float lengthX = boardDims[1] - boardDims[0];
        float lengthZ = boardDims[3] - boardDims[2];
        System.out.println(lengthX);
        System.out.println(lengthZ);
        gridElmSize = lengthX / maxSize;
        System.out.println(gridElmSize);

        float radiusBoardX=lengthX/2;
        float radiusBoardZ=lengthZ/2;
        
        int gridCounter = 0;
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (active[i][j] == true) {
                            float factorX = gridElmSize*(i+1)-(lengthX/2)-(lengthX/maxSize/2);
                            float factorZ = gridElmSize*(j+1)-(lengthX/2)-(lengthX/maxSize/2);
                    boxLocations[gridCounter] = new Vector3f
                            (factorX,worldTrans.getY()+0.5f,factorZ);
                    gridCounter++;
                }
                //boxGrid[i][j] = 
            }
        }
        for (Vector3f oneVector : boxLocations) {
            if(oneVector!=null)
            System.out.println(oneVector.getX()+" : "+oneVector.getZ());
        }
    }
    
    public Vector3f[] getBoxVector() {
        return boxLocations;
    }
    
    public void clearBoxVector() {
        //boxLocations = new Vector3f[maxSize*maxSize];
        boxLocations = null;
    }
    
}

*/