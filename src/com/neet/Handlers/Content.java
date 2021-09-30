package com.neet.Handlers;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class Content {


    public static BufferedImage[][] Explosion = load("/Sprites/Enemies/Explosion.gif", 30, 30);

    public static BufferedImage[][] Zombie = load("/Sprites/Enemies/Zombie.gif", 40, 40);
    public static BufferedImage[][] DarkLord = load("/Sprites/Enemies/DarkLord.gif", 40, 40);
    public static BufferedImage[][] DarkEnergy = load("/Sprites/Enemies/DarkEnergy.gif", 20, 20);

    public static BufferedImage[][] load(String s, int w, int h) {
        BufferedImage[][] ret;
        try {
            BufferedImage spritesheet = ImageIO.read(Content.class.getResourceAsStream(s));
            int width = spritesheet.getWidth() / w;
            int height = spritesheet.getHeight() / h;
            ret = new BufferedImage[height][width];
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    ret[i][j] = spritesheet.getSubimage(j * w, i * h, w, h);
                }
            }
            return ret;
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Eroare in Handlers -> Content");
            System.exit(0);
        }
        return null;
    }

}
