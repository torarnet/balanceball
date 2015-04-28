/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Tor Arne
 */
public class CustomMesh {

    Vector3f[] triangleMesh;
    int[] indices;

    public Mesh makeGoal() {

        Mesh mesh = new Mesh();
        
        triangleMesh = new Vector3f[]{
           
            new Vector3f(0,0,0), 
            new Vector3f(0.5f,-1,0.5f),
            new Vector3f(1,0,0),
            
            new Vector3f(0.5f,-1,0.5f),
            new Vector3f(1,0,1),
            
            new Vector3f(0.5f,-1,0.5f),
            new Vector3f(0,0,1),
            
            new Vector3f(0.5f,-1,0.5f),
            
            new Vector3f(0.5f,1,0.5f),
            
        };
        
        indices = new int[]{
                // Front
                /*0, 1, 2,
                2, 3, 4,
                4, 5, 6,
                6, 7, 0,
                0, 8, 2,
                2, 4, 8,
                4, 6, 8,
                6, 0, 8,*/
                
                // Reverse
                2, 1, 0,
                4, 3, 2,
                6, 5, 4,
                0, 7, 6,
                0, 8, 2,
                8, 4, 2,
                8, 6, 4,
                8, 0, 6
            };
        
        // Assign buffers.
        mesh.setBuffer(VertexBuffer.Type.Position, 3,
                BufferUtils.createFloatBuffer(triangleMesh));
        
        // Per vertex coloring. RGBA. No need to independently color 
        // Each triangle because color is reused for the same vertexes
        float[] colors = new float[]{
            1, 0, 0, 1,
            1, 1, 0, 1,
            0, 1, 1, 1,
            0, 0, 1, 1,
            1, 0, 0, 1,
            1, 1, 0, 1,
            0, 1, 1, 1,
            0, 0, 1, 1,
            1, 0, 0, 1,
            1, 1, 0, 1,
            0, 1, 1, 1,
            0, 0, 1, 1,
            1, 0, 0, 1,
            0, 0, 1, 1,
            0, 0, 1, 1,
        };
        
         mesh.setBuffer(VertexBuffer.Type.Index, 3,
                BufferUtils.createIntBuffer(indices));

        // To use this, set mat.setBoolean("VertexColor", true); in
        // material method
        mesh.setBuffer(VertexBuffer.Type.Color, 4,
                BufferUtils.createFloatBuffer(colors));

        // Update the bounds of the mesh, so that bounding box is correctly
        // recalcualted internally by jME.
        mesh.updateBound();

        return mesh;
        
    }
}
