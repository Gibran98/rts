/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src;

import com.jogamp.opengl.GL2;
import mikera.vectorz.Vector2;

/**
 *
 * @author rober
 */
//Simple warrior class
public class Warrior extends Unit{
    public static final int RUBY_COST = 50;
    public static final int WARRIOR_WIDTH = 50, WARRIOR_HEIGHT = 50;

    
    public Warrior(){
        super();
    }
    
    public Warrior(Vector2 dimension, Vector2 position, int id, Player owner){
       super(dimension, position, id, owner);
       this.speed = 2.5;
       this.maxHealth = 15;
       this.health = this.maxHealth;
       this.damage = 5;
       this.attackSpeed = 1;
       this.range = regularRange;
       this.texture = Assets.warriorTexture;
       this.buildingAttackRange = 65;
       this.unitAttackRange = 35;
    }
    
    public void tick(GridMap map){
        super.tick(map);
        super.changeAnimationSide();
        
        if (onMoveCommand) {
            super.changeAnimationSide();
            animated = true;
        }
        else if(onAttackCommand) {
            super.changeAttackingDirection();
            animated = true;
        }
        else {
            if(selected)
                texture = Assets.warriorsStandingSelectedTexture[owner.getID()-1];
            else
                texture = Assets.warriorsStandingTexture[owner.getID()-1];
            animated = false;
        }
        
    }
    
    //simple render method
    @Override
    public void render(GL2 gl, Camera cam){
        if (animated){
            if(onMoveCommand){
                if(selected) texture = Assets.warriorsWalkingSelectedTexture[owner.getID()-1];
                else texture = Assets.warriorsWalkingTexture[owner.getID() - 1];
            }
            else{
                if(selected) texture = Assets.warriorsAttackingSelectedTexture[owner.getID()-1];
                else texture = Assets.warriorsAttackingTexture[owner.getID() - 1];
            }
            if(runningTimer.doneWaiting()){
                // cambio
                runningCnt ++;
                runningCnt %= 4;
                this.runningTimer.setUp(0.2);
            }
            super.renderAnimation(gl, cam, runningCnt, direction);
        }
        else
            super.render(gl, cam);
    }

    public boolean isAnimated() {
        return animated;
    }
}
