package com.neet.GameState;

import java.awt.*;

import com.neet.Handlers.Keys;
import com.neet.Main.GamePanel;

public class FinishState extends GameState {

    private Font font;

    public FinishState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {}

    public void update() {
        handleInput();
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Felicitari ati castigat!", 130, 140);
    }

    public void handleInput() {
        if(Keys.isPressed(Keys.ESCAPE)) gsm.setState(GameStateManager.MENUSTATE);
    }

}