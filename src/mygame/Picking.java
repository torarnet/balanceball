/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;

/**
 *
 * @author Tor Arne
 */
public class Picking extends Main {
    
    public void placeObjAtContactPoint() {
        // When moving the picked object, we perform picking again, to find
        // which object we collide with, and then we place the picked object
        // on top of it. This gives a easy way to stack objects in the scene,
        // and prevents the user from moving the object into "thin-air".

        // This method follows the mouse using the 
        // CollisionResult.getContactPoint. Then it sets the transformation
        // using these coordinates, one little step at a time

        // This is called each time the mouse moves (from onAnalog), 
        // continually updating the pickedObject location using getContactPoint

        // As long as we have the floor in the pickables variable, 
        // it will register new mouse coordinates as long as we have
        // the mouse over the floor geometry.

        super.shadowPick.setLocalTranslation(super.custMath.adjustPos(super.pickedObject.getLocalTranslation()));

        CollisionResult moveToPoint = super.pickIfAny();
        if (moveToPoint != null) {
            Vector3f loc = moveToPoint.getContactPoint();
            loc.y += objOffsetY;
            pickedObject.setLocalTranslation(loc);
            //custMath.adjustPos(pickedObject.getLocalTranslation());
            //pickedObject.setLocalTranslation(custMath.adjustPos(pickedObject.getLocalTranslation()));
        }
    }
    
}
