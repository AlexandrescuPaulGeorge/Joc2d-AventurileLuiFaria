package com.neet.TileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.neet.Main.GamePanel;

public class Background {

    private BufferedImage image;

    private double x;
    private double y;
    private double dx;
    private double dy;

    private int width;
    private int height;

    private double xscale;
    private double yscale;

    public Background(String s) {
        this(s, 0.1);
    }

    public Background(String s, double d) {
        this(s, d, d);
    }

    public Background(String s, double d1, double d2) {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(s));
            width = image.getWidth();
            height = image.getHeight();
            xscale = d1;
            yscale = d2;
        }
        catch(Exception e) {
            System.out.println("Eroare in TileMap -> Background");
            e.printStackTrace();
        }
    }

    public void setPosition(double x, double y) {
        this.x = (x * xscale) % width;
        this.y = (y * yscale) % height;
    }
    public void update() {
        x += dx;
        while(x <= -width)
            x += width;
        while(x >= width)
            x -= width;
        y += dy;
        while(y <= -height)
            y += height;
        while(y >= height)
            y -= height;
    }

    public void draw(Graphics2D g) {

        g.drawImage(image, (int)x, (int)y, null);

        if(x < 0) {
            g.drawImage(image, (int)x + GamePanel.WIDTH, (int)y, null);
        }
        if(x > 0) {
            g.drawImage(image, (int)x - GamePanel.WIDTH, (int)y, null);
        }
        if(y < 0) {
            g.drawImage(image, (int)x, (int)y + GamePanel.HEIGHT, null);
        }
        if(y > 0) {
            g.drawImage(image, (int)x, (int)y - GamePanel.HEIGHT, null);
        }
    }

}







