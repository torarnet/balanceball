/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 *
 * @author Tor Arne
 */
public class HudBuilder {
    
    final int WIDTH;
    final int HEIGHT;
    final int ICONDIM = 40;
    AssetManager assetManager;
    Node guiNode;
    BitmapFont guiFont;
    
    public HudBuilder(AssetManager assetManager,AppSettings settings,
            Node guiNode,BitmapFont guiFont) {
        WIDTH = settings.getWidth();
        HEIGHT = settings.getHeight();
        this.assetManager=assetManager;
        this.guiNode=guiNode;
        this.guiFont=guiFont;
    }
    
    public Picture initHudPic() {
        Picture hudPic = new Picture("Background");
        hudPic.setImage(assetManager, "Textures/black2.png", true);
        hudPic.setWidth(WIDTH/3);
        hudPic.setHeight(HEIGHT/3);
        hudPic.setPosition(0, HEIGHT - HEIGHT/3);
        
        return hudPic;
    }
    
    public Picture[] initHeartPics() {
        Picture[] heartPic = new Picture[3];
        heartPic[0] = new Picture("Heart");
        heartPic[0].setImage(assetManager, "Textures/heart2.png", true);
        heartPic[0].setWidth(ICONDIM);
        heartPic[0].setHeight(ICONDIM);
        heartPic[1] = (Picture) heartPic[0].clone();
        heartPic[2] = (Picture) heartPic[0].clone();

        heartPic[0].setPosition(ICONDIM * 2, HEIGHT - 40);
        heartPic[1].setPosition(ICONDIM * 3, HEIGHT - 40);
        heartPic[2].setPosition(ICONDIM * 4, HEIGHT - 40);
        
        return heartPic;
        
    }
    
    public BitmapText initPauseText() {
        BitmapText pauseText = new BitmapText(guiFont, false);
        pauseText.setSize(30);      // font size
        pauseText.setColor(ColorRGBA.Red);
        pauseText.setText("PAUSED");
        pauseText.setLocalTranslation(0, HEIGHT - 80, 0);
        
        return pauseText;
    }
    
    public Picture[] initBoxText() {
        Picture[] boxPic = new Picture[3];
        boxPic[0] = new Picture("BoxPic");
        boxPic[0].setImage(assetManager, "Textures/box2.png", true);
        boxPic[0].setWidth(ICONDIM);
        boxPic[0].setHeight(ICONDIM);
        boxPic[1] = (Picture) boxPic[0].clone();
        boxPic[2] = (Picture) boxPic[0].clone();
        
        boxPic[0].setPosition(ICONDIM * 2, HEIGHT - 80);
        boxPic[1].setPosition(ICONDIM * 3, HEIGHT - 80);
        boxPic[2].setPosition(ICONDIM * 4, HEIGHT - 80);
        
        return boxPic;
    } 
    
    public BitmapText initHudTextLife() {
        BitmapText hudTextLife = new BitmapText(guiFont, false);
        hudTextLife.setSize(30);      // font size
        hudTextLife.setColor(ColorRGBA.Red);
        hudTextLife.setText("Life:");
        hudTextLife.setLocalTranslation(0, HEIGHT, 0);
        
        return hudTextLife;
    }
    
    public BitmapText initHudTextPick() {
        BitmapText hudTextPick = new BitmapText(guiFont, false);
        hudTextPick.setSize(30);      // font size
        hudTextPick.setColor(ColorRGBA.Red);
        hudTextPick.setText("Picks:");
        hudTextPick.setLocalTranslation(0, HEIGHT - 40, 0);
        
        return hudTextPick;
    }
    
    public BitmapText initHudTextPickActive() {
        BitmapText hudTextPickActive = new BitmapText(guiFont, false);
        hudTextPickActive.setSize(30);      // font size
        hudTextPickActive.setColor(ColorRGBA.Red);
        hudTextPickActive.setText("Pick Mode Active");
        hudTextPickActive.setLocalTranslation(WIDTH - 240, HEIGHT - 40, 0);
        
        return hudTextPickActive;
    }
    
    public BitmapText initHudTextInstruct() {
        BitmapText hudTextInstruct = new BitmapText(guiFont, false);
        hudTextInstruct.setSize(30);      // font size
        hudTextInstruct.setColor(ColorRGBA.Red);
        hudTextInstruct.setText("Press \" i \" for instructions");
        hudTextInstruct.setLocalTranslation(WIDTH - 330, 0 + 50, 0);
        
        return hudTextInstruct;
    }
    
    public BitmapText initHudTextScore() {
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setSize(20);
        hudText.setColor(ColorRGBA.Red);
        hudText.setText("Current Score: " + String.format("%.2f", 0.0f));
        hudText.setLocalTranslation(0, HEIGHT - 120, 0);
        
        return hudText;
    }
    
    public BitmapText initFinalScoreText() {
        BitmapText finalScoreText = new BitmapText(guiFont, false);
        finalScoreText.setSize(24);
        finalScoreText.setColor(ColorRGBA.Red);
        finalScoreText.setText("Last Score: " + String.format("%.2f", 0.0f));
        finalScoreText.setLocalTranslation(WIDTH - 220, HEIGHT, 0);
        
        return finalScoreText;
    }
    
    public Picture initInstructions() {        
        Picture instructions = new Picture("Instructions");
        instructions.setImage(assetManager, "Textures/instructions.png", true);
        instructions.setWidth(WIDTH/3 * 2);
        instructions.setHeight(HEIGHT/3 * 2);
        instructions.setPosition(WIDTH / 2 - (WIDTH/3), HEIGHT / 2 - (HEIGHT/3));
        
        return instructions;
    }
    
}
