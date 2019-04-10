/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.BitJunkies.RTS.src;

import com.BitJunkies.RTS.src.server.GameClient;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.ArrayList;
import mikera.vectorz.Vector2;

/**
 *
 * @author rober
 */
public class Game {
    private static boolean running = false;
    private static int FPS = 60;
    private static GLWindow window;
    private static Camera camera;
    private static ArrayList<Unit> units;
    
    public static void main(String args[]){
        window = Display.init();
        start();
        //GameClient cl = new GameClient();
    }
    
    public static void start(){
        Thread thread = new Thread(){
            public void run(){
                
                running = true;
                double timeTick = 1000000000 / FPS;
                double delta = 0;
                long now;
                long lastTime = System.nanoTime();
                while(running){
                    now = System.nanoTime();
                    delta += (now - lastTime) / timeTick;
                    lastTime = now;
                    if(delta >= 1){
                        // Input
                        // TODO
                        // Update
                        tick();
                        // Render
                        window.display();
                    }
                }
                window.destroy();
            }
        };
        thread.setName("GameLoop");
        thread.start();
    }
    
    public static void tick(){
        /*/
        for(int i = 0; i < units.size(); i++){
            units.get(i).tick();
        }
        /*/
    }
    
    public static void render(GLAutoDrawable drawable){
       GL2 gl = drawable.getGL().getGL2();
       gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
       
       
       if(units == null){
           System.out.println("aaaaa");
       }

       for(int i = 0; i < units.size(); i++){
           units.get(i).render(gl, camera);   
       }
        
    }
    
    public static void stop(){
        running = false;
    }
    
    public static void init(){
        //inicializar players y map y cosas
        Assets.init();
        camera = new Camera();
        units = new ArrayList<Unit>();
        for(int i = 0; i < 30; i++){
            units.add(new Unit(Vector2.of(30, 30), Vector2.of(i * 50, i * 30)));
        }
    }
    
    public static ArrayList<Unit> getUnits(){
        return units;
    }
    
}
