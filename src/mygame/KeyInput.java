/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.KeyInputEvent;
import java.util.ArrayList;
import java.util.List;
import util.RawInputAdapter;

/**
 *
 * @author Tor Arne
 */
public class KeyInput {

    private InputManager inputManager;
    boolean[] keysPressed;
    List<String> keyMaps;

    public KeyInput(InputManager inputManager, boolean[] keysPressed) {
        this.inputManager = inputManager;
        this.keysPressed = keysPressed;
        keyMaps = new ArrayList<String>();
        //mapKeys();
        initInput();
    }

    public void mapKeys() {
        inputManager.addMapping("MoveLeft", new KeyTrigger(com.jme3.input.KeyInput.KEY_LEFT));
        keyMaps.add("MoveLeft");
        inputManager.addMapping("MoveRight", new KeyTrigger(com.jme3.input.KeyInput.KEY_RIGHT));
        keyMaps.add("MoveRight");
        inputManager.addMapping("Reset", new KeyTrigger(com.jme3.input.KeyInput.KEY_R));
        keyMaps.add("Reset");
    }
    
    public List<String> getMappings() {
        return keyMaps;
    }

    private void initInput() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onKeyEvent(KeyInputEvent kie) {
                keysPressed[kie.getKeyCode() % keysPressed.length] = kie.isPressed() || kie.isRepeating();

                if (kie.isPressed() && kie.getKeyCode() == com.jme3.input.KeyInput.KEY_1) {
                    System.out.println("Pressed Key 1");
                } else if (kie.isPressed() && kie.getKeyCode() == com.jme3.input.KeyInput.KEY_2) {
                    //toggleDebug();
                } 
            }
        });
    }
}
