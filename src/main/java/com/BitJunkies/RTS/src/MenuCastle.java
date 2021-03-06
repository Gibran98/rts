/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src;

import com.BitJunkies.RTS.input.MouseInput;
import com.jogamp.opengl.GL2;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;
import mikera.vectorz.Vector2;

/**
 *
 * @author rober
 */
public class MenuCastle extends Menu{
    //Basic variables holding menu settings
    private boolean creatingWorker;
    private AtomicInteger castleCount;
    private double spacingLeft, spacingTop, widthItem, heightItem;
    private Rectangle workerHitBox;
    private Timer animationTimer;
    private int animationIdx;
    
    public MenuCastle(){
        super();
    }
    
    public MenuCastle(Vector2 dimension, Vector2 position, AtomicInteger castleCount){
        super(dimension, position);
        this.castleCount = castleCount;
        this.spacingLeft = 20;
        this.spacingTop = 10;
        this.widthItem = 80;
        this.heightItem = 80;
        this.creatingWorker = false;
        this.animationTimer = new Timer(Game.getFPS());
        this.animationTimer.setUp(0.4);
        this.animationIdx = 0;
    }
    
    public void tick(){
        super.tick();
    }
    
    public void render(GL2 gl, Camera cam){
        Display.drawImageStatic(gl, cam, Assets.menuSingleTexture, position.x,position.y, 300, dimension.y, 1f);
        Vector2 pos = position;
        //We draw every item from the menu in case it exists
        int currSpacing = 0;
        if(!castleCount.equals(0)){
            float opac = 0.15f;
            if(Game.currPlayer.hasRubys(Worker.RUBY_COST)) opac = 1f;
            //Display.drawImageStatic(gl, cam, Assets.workerTexture, pos.x + spacingLeft + currSpacing, pos.y + spacingTop, widthItem, heightItem, (float)opac);
            Display.drawAnimationStatic(gl, cam, Assets.workersWalkingTexture[Game.currPlayer.getID() - 1], pos.x + spacingLeft + currSpacing, pos.y + spacingTop, widthItem, heightItem, (float)opac, animationIdx, 1);
            workerHitBox = new Rectangle((int)(pos.x + spacingLeft + currSpacing), (int)(pos.y + spacingTop), (int)widthItem, (int)heightItem);
            currSpacing += spacingLeft + widthItem;
            
            if(animationTimer.doneWaiting()){
                animationIdx ++;
                animationIdx %= 4;
                animationTimer.setUp(0.4);
            }
        }
    }
    
    //method that checks if an item in the menu was pressed
    public boolean checkPress(Rectangle mouseHitBox){
        //each of this checks individually for every item if it was pressed and
        //if so then it activates a creating mode for that specific item
        if(!castleCount.equals(0)){
            if(workerHitBox.intersects(mouseHitBox) && Game.currPlayer.hasRubys(Worker.RUBY_COST)){
                creatingWorker = true;
                return true;
            }
        }
        return false;
    }
    public boolean isCreatingWorker() {
        return creatingWorker;
    }
    
    //method to stop the creatin mode of casttle
    public void stopCreatingWorker(){
        creatingWorker = false;
    }
}
