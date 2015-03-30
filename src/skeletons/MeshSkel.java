/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletons;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.awt.Transparency;

/**
 * This is a demonstration on how to make own custom meshes.
 * There are one non indexed and one indexed mesh representing a 2d House
 * There also is a house made in 3D, having a cube as body and a triangular roof
 * 
 * @author Tor Arne using IFE Examples
 */
public class MeshSkel extends SimpleApplication {

    public static void main(String[] args) {
        MeshSkel app = new MeshSkel();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        flyCam.setRotationSpeed(5);
        flyCam.setMoveSpeed(10);

        // True parameter for indexed, False for non-indexed
        // Mesh myHouse = oneHouse(true);
        
        // Make it in 3D
        Mesh myHouse = oneHouse3D();

        Geometry houseGeom = new Geometry("House", myHouse);

        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
                
        // Set either custom colors or a static color
        mat.setColor("Color", new ColorRGBA(0.5f,0f,0.2f,1));
        //mat.setBoolean("VertexColor", true);

        houseGeom.setMaterial(mat);

        rootNode.attachChild(houseGeom);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public Mesh oneHouse(boolean indexed) {

        Vector3f[] triangleMesh = null;
        int[] indices = null;

        // Not indexed
        if (!indexed) {
            // A House built with Three Triangles
            triangleMesh = new Vector3f[]{
                // Lower left part body of house
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                // Roof of house
                new Vector3f(1, 1, 0),
                new Vector3f(0.5f, 1.5f, 0),
                new Vector3f(0, 1, 0),
                // Upper right part body of house
                new Vector3f(1, 0, 0),
                new Vector3f(1, 1, 0),
                new Vector3f(0, 1, 0)
            };
        }

        // Indexed
        if (indexed) {
            // A House built with Three Triangles
            triangleMesh = new Vector3f[]{
                // House
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                // Roof
                new Vector3f(0.5f, 1.5f, 0),
                new Vector3f(1, 1, 0),};

            // Each number corresponds to a vertice number in the same order 
            // as they were created in the above triangleMesh
            indices = new int[]{
                // Lower Left
                2, 0, 1,
                // Upper Right
                1, 4, 2,
                // Roof
                2, 4, 3
            };
        }

        // Empty mesh object.
        Mesh houseMesh = new Mesh();

        // Assign buffers. 3 points for a triangle, 4 for square etc.
        houseMesh.setBuffer(VertexBuffer.Type.Position, 3,
                BufferUtils.createFloatBuffer(triangleMesh));

        // Per vertex coloring. RGBA.
        float[] colors = new float[]{
            1, 0, 0, 1,
            0, 1, 0, 1,
            0, 0, 1, 1,
            1, 1, 0, 1,
            0, 0, 1, 1,
            1, 1, 0, 1,
            0, 0, 1, 1,
            1, 1, 0, 1,
            0, 0, 1, 1
        };

        // More one-colored
        float[] colors2 = new float[]{
            0.5f, 0, 0, 1,
            0.5f, 0, 0, 1,
            1, 0, 0, 1,
            1, 0, 0, 1,
            0, 0, 1, 1,
            1, 0, 0, 1,
            1, 0, 0, 1,
            0.5f, 0, 0, 1,
            0.5f, 0, 0, 1
        };

        if (indexed && indices != null) {
            houseMesh.setBuffer(VertexBuffer.Type.Index, 3,
                    BufferUtils.createIntBuffer(indices));

            houseMesh.setBuffer(VertexBuffer.Type.Color, 4,
                    BufferUtils.createFloatBuffer(colors2));
        }

        if (!indexed) {
            houseMesh.setBuffer(VertexBuffer.Type.Color, 4,
                    BufferUtils.createFloatBuffer(colors2));
        }

        // Update the bounds of the mesh, so that bounding box is correctly
        // recalcualted internally by jME.
        houseMesh.updateBound();

        return houseMesh;

    }

    // House drawn in 3D, triangle by triangle
    public Mesh oneHouse3D() {

        Vector3f[] triangleMesh;
        int[] indices;

        // A House built of Triangles in 3D
        triangleMesh = new Vector3f[]{
            // House
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(0, 1, 0),
            // Roof
            new Vector3f(0.5f, 1.5f, 0),
            new Vector3f(1, 1, 0),
            // Z Direction, doing everything backwards, Z being -1
            new Vector3f(1, 1, -1),
            new Vector3f(0.5f, 1.5f, -1),
            new Vector3f(0, 1, -1),
            new Vector3f(0, 0, -1),
            new Vector3f(1, 0, -1)
        };

        // Each number corresponds to a vertice number in the same order 
        // as they were created in the above triangleMesh
        indices = new int[]{
            // Lower Left
            2, 0, 1,
            // Upper Right
            1, 4, 2,
            // Roof
            2, 4, 3,
            // SideRight
            4, 1, 9,
            9, 5, 4,
            // Back wall, Lower Left
            5, 9, 8,
            // Back Wall, Upper Right
            5, 8, 7,
            // Side Left
            0, 2, 8,
            8, 2, 7,
            // Side Left, Roof
            7, 2, 6,
            6, 2, 3,
            // Back Side, Roof
            5, 7, 6,
            // Side Right, Roof
            4, 5, 6,
            4, 6, 3,
            // Bottom, use reverse order, clockwise so backside gets drawn
            9, 1, 0,
            0, 8, 9
        };

        // Empty mesh object.
        Mesh houseMesh = new Mesh();

        // Assign buffers.
        houseMesh.setBuffer(VertexBuffer.Type.Position, 3,
                BufferUtils.createFloatBuffer(triangleMesh));

        // Per vertex coloring. RGBA. No need to independently color 
        // Each triangle because color is reused for the same vertexes
        float[] colors = new float[]{
            1, 0, 0, 1
        };

        houseMesh.setBuffer(VertexBuffer.Type.Index, 3,
                BufferUtils.createIntBuffer(indices));

        // To use this, set mat.setBoolean("VertexColor", true); in
        // material method
        houseMesh.setBuffer(VertexBuffer.Type.Color, 4,
                BufferUtils.createFloatBuffer(colors));

        // Update the bounds of the mesh, so that bounding box is correctly
        // recalcualted internally by jME.
        houseMesh.updateBound();

        return houseMesh;
    }
}