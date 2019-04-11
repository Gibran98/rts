/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.Rectangle;
import mikera.vectorz.*;

/**
 *
 * @author brobz
 */
public abstract class Entity {
    protected Vector2 dimension, position, velocity;
    protected float opacity;
    protected Texture texture;
    protected Rectangle hitBox;
    
    public Entity(){
        dimension = Vector2.of(0, 0);
        position = Vector2.of(0, 0);
        velocity = Vector2.of(0, 0);
        updateHitBox();
        texture = null;
        opacity = 1;
    }
    
    public Entity(Vector2 dimension, Vector2 position){
        this.dimension = dimension;
        this.position = position;
        this.velocity = Vector2.of(0, 0);
        updateHitBox();
    }
    
    public void tick(){
        position.add(velocity);
        updateHitBox();
    }
    
    public void render(GL2 gl, Camera cam){
        gl.glEnable(GL2.GL_TEXTURE_2D);
        Vector2 pos = cam.projectPosition(position);
        Vector2 dim = cam.projectDimension(dimension);
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());
        
        gl.glColor4f(1, 1, 1, opacity);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2d(pos.x - dim.x / 2, pos.y - dim.y / 2);
        
        gl.glTexCoord2f(0, 1);
        gl.glVertex2d(pos.x - dim.x / 2, pos.y + dim.y / 2);
        
        gl.glTexCoord2f(1, 1);        
        gl.glVertex2d(pos.x + dim.x / 2, pos.y + dim.y / 2);
        
        gl.glTexCoord2f(1, 0);
        gl.glVertex2d(pos.x + dim.x / 2, pos.y - dim.y / 2);
        gl.glEnd();
        gl.glFlush();
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
    
    public void updateHitBox(){
        hitBox = new Rectangle((int)(position.x - dimension.x / 2), (int)(position.y - dimension.y / 2), (int)dimension.x, (int)dimension.y);
    }
    
    public Rectangle getHitBox(){
        return hitBox;
    }
    
    public void setOpacity(float opacity){
        this.opacity = opacity;
    }
}