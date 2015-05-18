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

    // Sets the active places on grid, this is the indexes
    boolean[][] active;
    // Finds an empty place on grid at random and place target there, 
    // this is index of target
    boolean[][] target;
    // Size of Grid in one direction
    int maxSize;
    // Number of boxes
    int amount;
    // Size of one element of grid
    float gridElmSize;
    // Length of board
    float[] boardDims;
    float lengthX;
    float lengthZ;
    // Where is board in world space
    Vector3f worldTrans;
    // Location of target in coordinates
    Vector3f targetCoords;
    // Locations in Scene Graph for each box
    Vector3f[][] boxLocations2;
    // Locations in scene graph for each grid element
    Vector3f[][] gridCoords;

    // Simplified constructor. For more detailed see below
    public CustomMath(Geometry board) {
        this.maxSize = 4;
        this.amount = 4;
        active = new boolean[maxSize][maxSize];
        target = new boolean[maxSize][maxSize];
        setBoardDimension(board);
    }

    /* Sets the grid, box amount, and board dimensions
     * 
     * maxSize is how big the board grid will be in one direction, 
     * board is the surface itself,
     * amount is number of boxes to be created
     * Also inits active and target indexes 
     */
    public CustomMath(int maxSize, Geometry board, int amount) {
        this.maxSize = maxSize;
        this.amount = amount;
        active = new boolean[maxSize][maxSize];
        target = new boolean[maxSize][maxSize];
        setBoardDimension(board);
    }

    // Sets new values for grid and amount of boxes
    public void setVars(int maxSize, Geometry board, int amount) {
        this.maxSize = maxSize;
        this.amount = amount;
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

    // Sets the box locations indexes in a [][] array to be used later on when 
    // deciding the vectors.
    // Sets the boxes in a random place. If not the whole amount is used,
    // it restarts the method until the whole amount is set in array
    public void setActive() {
        active = new boolean[maxSize][maxSize];

        int counter = 0;

        for (int i = 0; i < amount; i++) {
            int i1 = 0;
            int j1 = 0;
            Random rand = new Random();
            // Gets a random location and sets true if not already filled
            i1 = rand.nextInt(maxSize);
            j1 = rand.nextInt(maxSize);
            if (active[i1][j1] == false) {
                active[i1][j1] = true;
                counter++;
            }
        }
        // If not whole amount is set in the grid, it restarts
        if (counter < amount && (isAmountValid())) {
            setActive();
        }
    }

    // Does the same for target. Sets the target in an array
    public void setTarget() {
        int targetX;
        int targetZ;
        targetX = new Random().nextInt(maxSize);
        targetZ = new Random().nextInt(maxSize);
        // If there is not already a box there, sets the target there
        if (active[targetX][targetZ] == false) {
            target[targetX][targetZ] = true;
            setTargetCoords(targetX, targetZ);
            //System.out.println("target: i: "+targetX+" j: "+targetZ+ " is true");
        } else {
            if (isAmountValid()) {
                setTarget();
            }
        }
    }

    // Sets the target vector based on placement in target[][] array
    private void setTargetCoords(int targetX, int targetZ) {
        float lengthX = boardDims[1] - boardDims[0];
        float lengthZ = boardDims[3] - boardDims[2];
        gridElmSize = lengthX / maxSize;

        float radiusX = lengthX / 2;
        float radiusZ = lengthZ / 2;

        // Calculates the exact position of one target coordinate in scene graph
        float factorX = gridElmSize * (targetX + 1) - (radiusX) - (radiusX / maxSize);
        float factorZ = gridElmSize * (targetZ + 1) - (radiusZ) - (radiusZ / maxSize);
        // Puts in the coordinate in vector
        targetCoords = new Vector3f(factorX, worldTrans.getY() + 0.4f, factorZ);
    }

    // set boardDims as float[startx,endx,startz,endz]
    // Is used to get the location of board in scene graph
    private void setBoardDimension(Geometry board) {
        // Get location of board
        worldTrans = board.getWorldTranslation();

        // Get size of board
        BoundingBox box = (BoundingBox) board.getModelBound();
        Vector3f boxSize = new Vector3f();
        boxSize = box.getExtent(boxSize);

        // Use this to get start and end coords
        float[] floatArray = new float[4];
        floatArray[0] = (worldTrans.getX() - boxSize.getX());
        floatArray[1] = (worldTrans.getX() + boxSize.getX());
        floatArray[2] = (worldTrans.getZ() - boxSize.getZ());
        floatArray[3] = (worldTrans.getZ() + boxSize.getZ());

        boardDims = floatArray;
    }

    // This is where the grid itself is made, with all the vectors for the
    // Boxes, target and all the vectors for the whole grid
    public void makeGrid() {
        // reset the boxLocations vector and gridcoords vector
        boxLocations2 = new Vector3f[maxSize][maxSize];
        gridCoords = new Vector3f[maxSize][maxSize];
        // Length of board
        lengthX = boardDims[1] - boardDims[0];
        lengthZ = boardDims[3] - boardDims[2];
        // Size of one element accoding to max size of grid
        gridElmSize = lengthX / maxSize;

        float radiusX = lengthX / 2;
        float radiusZ = lengthZ / 2;

        // For each element according to maxsize. Matches the size of array[][]
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                // Use maths to get vector coordinate for this location
                // based on several factors. This will get the exact 
                // coordinate position for a element in active[][] array
                float factorX = gridElmSize * (i + 1) - (radiusX) - (radiusX / maxSize);
                float factorZ = gridElmSize * (j + 1) - (radiusZ) - (radiusZ / maxSize);
                // Add all vectors for the whole grid to the gridCoord array
                gridCoords[i][j] = new Vector3f(factorX, worldTrans.getY() + 0.3f, factorZ);
                // Place box in vector if active on this location is set
                if (active[i][j] == true) {
                    // Add vector to array
                    //System.out.println("i: "+i+" j: "+j+ " is true");
                    boxLocations2[i][j] = new Vector3f(factorX, worldTrans.getY() + 0.3f, factorZ);
                }
                // Do the same for target.
                if (target[i][j] == true) {
                    targetCoords = new Vector3f(factorX, worldTrans.getY() + 0.4f, factorZ);
                }
            }
        }
    }

    // This is used with picking to make sure the picked object is placed
    // in the same coordinate system. It adjusts the position so it fits 
    // with grid when the object is released. Takes input vector and 
    // transforms it to fit with grid
    public Vector3f adjustPos(Vector3f vectorIn) {
        Vector3f vectorOut = new Vector3f();
        // Round the input to nearest grid vector according to grid element size
        float adjustedX = round(vectorIn.getX(), gridElmSize);
        float adjustedZ = round(vectorIn.getZ(), gridElmSize);

        float radiusX = lengthX / 2;
        float radiusZ = lengthZ / 2;

        // Sets the new position according to the max size of grid and the 
        // radius of board. 
        // adjusted is the round to the nearest grid element.
        // then radius for one element is calculated and subtracted from
        // position to make the box come in center
        vectorOut.setX(adjustedX - radiusX / maxSize);
        vectorOut.setY(vectorIn.getY());
        vectorOut.setZ(adjustedZ - radiusZ / maxSize);

        return vectorOut;
    }

    // Round to nearest factor
    float round(float i, float v) {
        return Math.round(i / v) * v;
    }

    // Returns target vector
    public Vector3f getTargetCoords() {
        return targetCoords;
    }

    // Returns box array vector with the coordinates
    public Vector3f[][] getBoxVector2() {
        return boxLocations2;
    }

    // Resets box location vector, the one with coordinates
    public void clearBoxVector2() {
        boxLocations2 = null;
    }

    // Size of grid
    public int getMaxSize() {
        return maxSize;
    }

    // Remove target from target array
    public void removeTargetAt(int x, int z) {
        if (target[x][z] = true) {
            target[x][z] = false;
        } else {
            System.out.println("Target not found at removeTargetAt");
        }
    }

    // Remove all targets , resets the array
    public void removeAllTargets() {
        target = null;
        target = new boolean[maxSize][maxSize];
    }

    // Remove box place from active grid, this is just the index, not coords
    public void removeBoxAt(int x, int z) {
        if (active[x][z] = true) {
            active[x][z] = false;
        } else {
            System.out.println("Box not found at removeBoxAt");
        }
    }

    // adds the box to active[][] grid, so the system knows there should be a 
    // box there.
    // x and y coords are not in world space, they are for active[i][j] indexes
    public void addBoxAt(int x, int z, Vector3f vector) {

        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (vector != null) {
                    // Checks input vector with gridCoords array to se if there
                    // alrady is something there. Uses difference to have some
                    // space in coordinates. This means that the coords does not
                    // have to be exact, but in a range very close
                    Vector3f diff = gridCoords[i][j].subtract(vector);
                    if (Math.abs(diff.getX()) < 0.1 && Math.abs(diff.getZ()) < 0.1) {
                        if (!isThereBox(i, j)) {
                            active[i][j] = true;
                        }
                        //System.out.println("Active at X: " + i + " Z: " + j);
                    }
                }
            }
        }

    }

    // Checks to see if there already is something at a location coordinates
    public boolean isElmTaken(Vector3f vector) {
        for (int i = 0; i < maxSize; i++) {
            for (int j = 0; j < maxSize; j++) {
                if (vector != null) {
                    // Same as above
                    Vector3f diff = gridCoords[i][j].subtract(vector);
                    if (Math.abs(diff.getX()) < 0.1 && Math.abs(diff.getZ()) < 0.1) {
                        if (isThereBox(i, j) || isThereTarget(i, j)) {
                            //System.out.println("True");
                            return true;
                        }
                    }
                }
            }
        }
        //System.out.println("False");
        return false;
    }

    // Checks the active array to see if element is occupied
    public boolean isThereBox(int x, int z) {
        if (active[x][z] == true) {
            return true;
        } else {
            return false;
        }
    }

    // Checks the target array to see if element is occupied
    public boolean isThereTarget(int x, int z) {
        if (target[x][z] == true) {
            return true;
        } else {
            return false;
        }
    }
}
