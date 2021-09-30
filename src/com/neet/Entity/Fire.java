package com.neet.Entity;

import javax.imageio.ImageIO;

import com.neet.Entity.MapObject;
import com.neet.TileMap.TileMap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fire extends MapObject {

    private BufferedImage[] sprites;

    public Fire(TileMap tm) {
        super(tm);
        facingRight = true;
        width = 20;
        height =30;
        cwidth = 20;
        cheight =30;
        try {
            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream("/Sprites/Other/Fire1.gif")
            );
            sprites = new BufferedImage[60];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
            }
            animation.setFrames(sprites);
            animation.setDelay(5);
        }
        catch(Exception e) {
            System.out.println("Eroare in Enitity -> Fire");
            e.printStackTrace();
        }
    }

    public void update() {
        animation.update();
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }

}

