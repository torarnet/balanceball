/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Tor Arne
 */
public class StartScreenState2 extends AbstractAppState implements ScreenController {
 
  private ViewPort viewPort;
  private Node rootNode;
  private Node guiNode;
  private AssetManager assetManager;
  private Node localRootNode = new Node("Start Screen RootNode");
  private Node localGuiNode = new Node("Start Screen GuiNode");
  private final ColorRGBA backgroundColor = ColorRGBA.Gray;  
 
public StartScreenState2(SimpleApplication app){
    this.rootNode     = app.getRootNode();
    this.viewPort     = app.getViewPort();
    this.guiNode      = app.getGuiNode();
    this.assetManager = app.getAssetManager();  
  }
 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
 
    rootNode.attachChild(localRootNode);
    guiNode.attachChild(localGuiNode);
    viewPort.setBackgroundColor(backgroundColor);
 
    /** init the screen */    
  }
 
  @Override
  public void update(float tpf) {
    /** any main loop action happens here */
  }
 
  @Override
  public void cleanup() {
    rootNode.detachChild(localRootNode);
    guiNode.detachChild(localGuiNode);
 
    super.cleanup();
  }

    public void bind(Nifty nifty, Screen screen) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
