/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Tor Arne
 */
public class CustomMath {
    
    float[][] boxGrid;
    boolean[][] active;
    HashMap<Integer,Integer> boxPlacement;
    int maxSize;
    float gridElmSize = 0.2f;

    // Return float[startx,endx,startz,endz]
    public float[] getBoardDimension(Geometry board) {
        Vector3f vector = board.getWorldTranslation();

        System.out.println(vector.getX());
        System.out.println(vector.getY());
        System.out.println(vector.getZ());
        
        BoundingBox box = (BoundingBox) board.getModelBound();
        Vector3f boxSize = new Vector3f();
        boxSize = box.getExtent(boxSize);

        float[] floatArray = new float[4];
        floatArray[0] = (vector.getX()-boxSize.getX());
        floatArray[1] = (vector.getX()+boxSize.getX());
        floatArray[2] = (vector.getZ()-boxSize.getZ());
        floatArray[3] = (vector.getZ()+boxSize.getZ());
        
         

        System.out.println(boxSize.getX());
        System.out.println(boxSize.getY());
        System.out.println(boxSize.getZ());
        
        System.out.println(floatArray[0]);
        System.out.println(floatArray[1]);
        System.out.println(floatArray[2]);
        System.out.println(floatArray[3]);

        return floatArray;
    }
    
    public void makeGrid() {
        
    }
    
}
