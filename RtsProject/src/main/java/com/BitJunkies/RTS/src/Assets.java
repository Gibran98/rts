package com.BitJunkies.RTS.src;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
public class Assets {
    //class that contains game resources

    //Images
    public static Texture backgroundTexture;
    public static BufferedImage background;
    
    public static Texture rockTexture;
    public static BufferedImage rock;
    
    public static Texture rockTextureD1;
    public static BufferedImage rockD1;
    
    public static Texture rockTextureD2;
    public static BufferedImage rockD2;

    //Sound clips
    public static SoundClip explosionSound;
    public static SoundClip otherExplosionSound;

    // init creates obejects so that they are avbailable to our game
    public static void init(){
        background = ImageLoader.loadImage("/Images/background.jpg");
        backgroundTexture = AWTTextureIO.newTexture(Display.getProfile(), background, true);
        
        rock = ImageLoader.loadImage("/Images/Rock.png");
        rockTexture = AWTTextureIO.newTexture(Display.getProfile(), rock, true);

        rockD1 = ImageLoader.loadImage("/Images/RockDamage1.png");
        rockTextureD1 = AWTTextureIO.newTexture(Display.getProfile(), rockD1, true);

        rockD2 = ImageLoader.loadImage("/Images/RockDamage2.png");
        rockTextureD2 = AWTTextureIO.newTexture(Display.getProfile(), rockD2, true);

        //Sounds
        otherExplosionSound = new SoundClip("/Sounds/explosion2.wav");
        otherExplosionSound.setLooping(false);
        otherExplosionSound.prePlayLoad();
    }
}