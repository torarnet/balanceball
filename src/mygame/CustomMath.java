/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.Random;

/**
 *
 * @author Tor Arne
 */
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
