package com.neet.Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.neet.Entity.Enemies.DarkEnergy;
import com.neet.TileMap.TileMap;
import com.neet.Entity.Enemy;

public class Inferno extends Enemy {

    public BufferedImage[] sprites;
    private Player player;
    private ArrayList<Enemy> enemies;

    private boolean active;


    private int step;
    private int stepCount;

    // patternul de atac
    private int[] steps = {0, 1,  0, 1, 0, 1};

    public Inferno(TileMap tm, Player p, ArrayList<Enemy> enemies)
     {
        super(tm);
        player = p;
        this.enemies = enemies;

        width = 80;
        height = 80;
        cwidth = 80;
        cheight = 80;

        health = maxHealth = 1;

        moveSpeed = 1.4;

        try {
            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/Inferno.gif"));
            sprites = new BufferedImage[10];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
            }
        }
        catch(Exception e) {
            System.out.println("Eroare in Entity -> Inferno");
            e.printStackTrace();
        }

        damage = 1;

        animation.setFrames(sprites);
        animation.setDelay(3);

        step = 0;
        stepCount = 0;

    }

    public void setActive() { active = true; }

    public void update() {

        if(health == 0) return;

        // da restart la attack pattern
        if(step == steps.length) {
            step = 0;
        }

        if(flinching) {
            flinchCount++;
            if(flinchCount == 8) flinching = false;
        }

        x += dx;
        y += dy;

        if(player.getx() < x)
            facingRight = false;
        else
            facingRight = true;

        animation.update();

        if(!active)
            return;
        ////////// attacuri////////////

        // zboara si arunca bombe
        if(steps[step] == 0) {
            stepCount++;
            //System.out.println("step="+stepCount);
            //ystem.out.println("y="+y);
            if(y > 60) {
                dy = -4;
               //System.out.println("y="+y);
            }
            if(y < 60) {
              dy = 0;
              y = 60;
             dx = -1;
            }
          //  System.out.println("dx="+dx);
           // System.out.println("x="+x);
            if(y == 60) {
                if(dx == -1 && x < 60) {
                   // System.out.println("x="+x);
                    dx = 1;
                }
                if(dx == 1 && x > tileMap.getWidth() - 60) {
                    dx = -1;
                }
            }
            if(stepCount % 60 == 0) {
                DarkEnergy de = new DarkEnergy(tileMap);
                de.setType(DarkEnergy.GRAVITY);
                de.setPosition(x, y);
                int dir = Math.random() < 0.5 ? 1 : -1;
                de.setVector(dir, 0);
                enemies.add(de);
            }
            if(stepCount == 559) {
                step++;
                stepCount = 0;
                right = left = false;
            }
        }
        // ataca ,zburand pe langa podea
        else if(steps[step] == 1) {
            stepCount++;
            if(stepCount == 1) {
                x = -9000;
                y = 9000;
                dx = dy = 0;
            }
            if(stepCount == 60) {
                if(player.getx() > tileMap.getWidth() / 2) {
                    x = 30;
                    y = tileMap.getHeight() - 60;
                    dx = 4;
                }
                else {
                    x = tileMap.getWidth() - 30;
                    y = tileMap.getHeight() - 60;
                    dx = -4;
                }
            }
            //System.out.println("stepcount"+stepCount);
            if((dx == -4 && x < 30) || (dx == 4 && x > tileMap.getWidth() - 30)) {
                stepCount = 0;
                step++;
                dx = dy = 0;
            }

        }
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }

}

