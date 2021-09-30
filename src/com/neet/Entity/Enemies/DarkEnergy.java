package com.neet.Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.neet.Entity.Enemy;
import com.neet.Handlers.Content;
import com.neet.TileMap.TileMap;


public class DarkEnergy extends Enemy {

    private BufferedImage[] startSprites;
    private BufferedImage[] sprites;

    private boolean start;

    private int type = 0;
    public static int VECTOR = 0;
    public static int GRAVITY = 1;

    public DarkEnergy(TileMap tm) {

        super(tm);

        health = maxHealth = 1;

        width = 20;
        height = 20;
        cwidth = 12;
        cheight = 12;

        damage = 1;
        moveSpeed = 5;

        startSprites = Content.DarkEnergy[0];
        sprites = Content.DarkEnergy[1];

        animation.setFrames(startSprites);
        animation.setDelay(2);

        start = true;
       // flinching = true;
    }

    public void setType(int i) { type = i; }

    public void update() {

        if(start) {
            if(animation.hasPlayedOnce()) {
                animation.setFrames(sprites);
                animation.setNumFrames(3);
                animation.setDelay(2);
                start = false;
            }
        }

        if(type == VECTOR) {
            x += dx;
            y += dy;
        }
        else if(type == GRAVITY) {
            dy += 0.2;
            x += dx;
           y += dy;
        }
        animation.update();
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }

}