package com.neet.GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.neet.Entity.PlayerSave;
import com.neet.Handlers.Keys;
import com.neet.Main.GamePanel;

public class MenuState extends GameState {

    private BufferedImage head;

    private int currentChoice = 0;

    private String[] options = {
            "New Game",
            "Quit"
    };

    private Color titleColor;
    private Font titleFont;

    private Font font;
    private Font font2;

    public MenuState(GameStateManager gsm) {

        super(gsm);

        try {

            head = ImageIO.read(getClass().getResourceAsStream("/HUD/hud1.gif")).getSubimage(0, 0, 12, 11);
            titleColor = Color.BLUE;
            titleFont = new Font("Arial Rounded MT Bold", Font.PLAIN, 28);
            font = new Font("Arial", Font.PLAIN, 14);
            font2 = new Font("Arial", Font.PLAIN, 10);
        }
        catch(Exception e) {
            System.out.println("Eroare in GameState -> MenuState");
            e.printStackTrace();
        }

    }

    public void init()
    { }

    public void update() {
        handleInput();

    }

    public void draw(Graphics2D g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Aventurile ", 120, 60);
        g.drawString("lui "       ,170, 90);
        g.drawString("Faria"      , 150, 120);

        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("New Game", 150, 185);
        g.drawString("Quit", 170, 205);

        if(currentChoice == 0)
            g.drawImage(head, 130, 175, null);
        else
        if(currentChoice == 1)
            g.drawImage(head, 130, 195, null);

    }

    private void select() {
        if(currentChoice == 0) {
            PlayerSave.init();
            gsm.setState(GameStateManager.LEVEL1ASTATE);
        }
        else if(currentChoice == 1) {
            System.exit(0);
        }
    }

    public void handleInput() {
        if(Keys.isPressed(Keys.ENTER))
            select();
        if(Keys.isPressed(Keys.UP)) {
            if(currentChoice > 0) {
                currentChoice--;
            }
        }
        if(Keys.isPressed(Keys.DOWN)) {
            if(currentChoice < options.length - 1) {
                currentChoice++;
            }
        }
    }

}










