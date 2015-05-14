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
 * Generate a chosen grid with chosen amount of boxes to be placed on the board.
 * Placement of boxes is random
 *
 * @author Tor Arne
 */
public class CustomMath {

    float[][] boxGrid;
    // Sets the active places on grid
    boolean[][] active;
    // Finds an empty place on grid at random and place target there
    boolean[][] target;
    int maxSize;
    int amount;
    float gridElmSize;
    float[] boardDims;
    Vector3f worldTrans;
    Vector3f targetCoords;
    Vector3f[] boxLocations;
    Vector3f[][] boxLocations2;

    public CustomMath(Geometry board) {
        this.maxSize = 4;
        this.amount = 4;
        boxGrid = new float[maxSize][maxSize];
        active = new boolean[maxSize][maxSize];
        target = new boolean[maxSize][maxSize];
        setBoardDimension(board);
    }

    /*
     * maxSize is how big the board grid will be in one direction, 
     * board is the surface itself,
     * amount is number of boxes to be created
     */
    public CustomMath(int maxSize, Geometry board, int amount) {
        this.maxSize = maxSize;
        this.amount = amount;
        boxGrid = new float[maxSize][maxSize];
        active = new boolean[maxSize][maxSize];
        target = new boolean[maxSize][maxSize];
        setBoardDimension(board);
    }

    public void setVars(int maxSize, Geometry board, int amount) {
        this.maxSize = maxSize;
        this.amount = amount;
        boxGrid = new float[maxSize][maxSize];
        active = new boolean[maxSize][maxSize];
        target = new boolean[maxSize][maxSize];
        setBoardDimension(board);
    }

    // Make sure the amount is not to many for the grid
    public boolean isAmountValid() {
        if (amount < maxSize * maxSize) {
            return true;
        } else {
            return false;
        }
    }

    public void setActive() {
        active = new boolean[maxSize][maxSize];
        // counter restart the box placement if all points on grid are filled
        // stops the placement after max amount of boxes is reached
        int counter = 0;
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (counter < maxSize * maxSize && counter < amount) {
                    active[i][j] = new Random().nextBoolean();
                    if (active[i][j] == true) {
                        counter++;
                    }
                    System.out.println("place: " + i + " and " + j + " is " + active[i][j]);
                }
            }
        }
        if (counter < amount && (isAmountValid())) {
            setActive();
        }
    }

    public void setTarget() {
        int targetX;
        int targetZ;
        targetX = new Random().nextInt(maxSize);
        targetZ = new Random().nextInt(maxSize);
        if (active[targetX][targetZ] == false) {
            target[targetX][targetZ] = true;
            setTargetCoords(targetX, targetZ);
            System.out.println(targetX + " and " + targetZ + " is " + target[targetX][targetZ]);
        } else {
            if (isAmountValid()) {
                setTarget();
            }
        }
    }

    private void setTargetCoords(int targetX, int targetZ) {
        float lengthX = boardDims[1] - boardDims[0];
        float lengthZ = boardDims[3] - boardDims[2];
        System.out.println(lengthX);
        System.out.println(lengthZ);
        gridElmSize = lengthX / maxSize;
        System.out.println(gridElmSize);

        float radiusX = lengthX / 2;
        float radiusZ = lengthZ / 2;

        float factorX = gridElmSize * (targetX + 1) - (radiusX) - (radiusX / maxSize);
        float factorZ = gridElmSize * (targetZ + 1) - (radiusZ) - (radiusZ / maxSize);
        targetCoords = new Vector3f(factorX, worldTrans.getY() + 0.4f, factorZ);
    }

    // set boardDims as float[startx,endx,startz,endz]
    private void setBoardDimension(Geometry board) {
        worldTrans = board.getWorldTranslation();

        BoundingBox box = (BoundingBox) board.getModelBound();
        Vector3f boxSize = new Vector3f();
        boxSize = box.getExtent(boxSize);

        float[] floatArray = new float[4];
        floatArray[0] = (worldTrans.getX() - boxSize.getX());
        floatArray[1] = (worldTrans.getX() + boxSize.getX());
        floatArray[2] = (worldTrans.getZ() - boxSize.getZ());
        floatArray[3] = (worldTrans.getZ() + boxSize.getZ());

        boardDims = floatArray;
    }

    public void makeGrid() {
        boxLocations2 = new Vector3f[maxSize][maxSize];
        boxLocations = new Vector3f[maxSize * maxSize];
        //boxGrid = new float[4][4];
        //maxSize = 16;
        float lengthX = boardDims[1] - boardDims[0];
        float lengthZ = boardDims[3] - boardDims[2];
        System.out.println(lengthX);
        System.out.println(lengthZ);
        gridElmSize = lengthX / maxSize;
        System.out.println(gridElmSize);

        float radiusX = lengthX / 2;
        float radiusZ = lengthZ / 2;

        int gridCounter = 0;
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (active[i][j] == true) {
                    float factorX = gridElmSize * (i + 1) - (radiusX) - (radiusX / maxSize);
                    float factorZ = gridElmSize * (j + 1) - (radiusZ) - (radiusZ / maxSize);
                    boxLocations[gridCounter] = new Vector3f(factorX, worldTrans.getY(), factorZ);
                    gridCounter++;

                    boxLocations2[i][j] = new Vector3f(factorX, worldTrans.getY() + 0.3f, factorZ);
                }
                if (target[i][j] == true) {
                    float factorX = gridElmSize * (i + 1) - (radiusX) - (radiusX / maxSize);
                    float factorZ = gridElmSize * (j + 1) - (radiusZ) - (radiusZ / maxSize);
                    targetCoords = new Vector3f(factorX, worldTrans.getY() + 0.4f, factorZ);
                }
            }
        }
        for (Vector3f oneVector : boxLocations) {
            if (oneVector != null) {
                System.out.println(oneVector.getX() + " : " + oneVector.getZ());
            }
        }
    }

    public Vector3f getTargetCoords() {
        return targetCoords;
    }

    public Vector3f[] getBoxVector() {
        return boxLocations;
    }

    public void clearBoxVector() {
        boxLocations = null;
    }

    public Vector3f[][] getBoxVector2() {
        return boxLocations2;
    }

    public void clearBoxVector2() {
        boxLocations2 = null;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void removeTargetAt(int x, int z) {
        if (target[x][z] = true) {
            target[x][z] = false;
        } else {
            System.out.println("Target not found at removeTargetAt");
        }
    }

    public void removeAllTargets() {
        target = null;
        target = new boolean[maxSize][maxSize];
    }

    public void removeBoxAt(int x, int z) {
        if (active[x][z] = true) {
            active[x][z] = false;
        } else {
            System.out.println("Box not found at removeBoxAt");
        }
    }
}
