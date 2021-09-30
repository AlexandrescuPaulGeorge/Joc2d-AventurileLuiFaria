package com.neet.GameState;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.neet.Audio.JukeBox;
import com.neet.Entity.Enemy;
import com.neet.Entity.Explosion;
import com.neet.Entity.*;
import com.neet.Entity.Enemies.DarkEnergy;
import com.neet.Handlers.Keys;
import com.neet.Main.GamePanel;
import com.neet.TileMap.Background;
import com.neet.TileMap.TileMap;

public class Level1CState extends GameState {

    private Background temple;

    private Player player;
    private TileMap tileMap;
    private Fire fire;
    private Fire fire1;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;

    private HUD hud;

    private Inferno inferno;

    private boolean blockInput = false;
    private int eventCount = 0;
    private boolean eventStart;
    private ArrayList<Rectangle> tb;
    private boolean eventFinish;
    private boolean eventDead;
    private boolean eventPortal;
    private boolean flash;
    private boolean eventBossDead;

    public Level1CState(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        temple = new Background("/Backgrounds/backgr3.gif", 0.5, 0);

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/lavaset.gif");
        tileMap.loadMap("/Maps/level3.map");
        tileMap.setPosition(140, 0);
        tileMap.setTween(1);

        player = new Player(tileMap);
        player.setPosition(50, 190);
        player.setHealth(PlayerSave.getHealth());
        player.setLives(PlayerSave.getLives());
        player.setTime(PlayerSave.getTime());

        explosions = new ArrayList<Explosion>();

        enemies = new ArrayList<Enemy>();
        populateEnemies();

        player.init(enemies);

        hud = new HUD(player);

        fire = new Fire(tileMap);
        fire.setPosition(15, 75);

        fire1 = new Fire(tileMap);
        fire1.setPosition(375, 75);

        eventStart = blockInput = true;
        tb = new ArrayList<Rectangle>();
        eventStart();

        //JukeBox.load("/Music/beat.mp3", "beat");
        //JukeBox.loop("beat", 600, JukeBox.getFrames("beat") - 2200);
    }

    private void populateEnemies() {
        enemies.clear();
        inferno = new Inferno(tileMap, player, enemies);//, explosions);
        inferno.setPosition(0, 0);
        enemies.add(inferno);
    }

    public void update() {

        handleInput();

        if(!eventFinish && inferno.isDead()) {
            eventBossDead = blockInput = true;
        }

        if(player.getHealth() == 0 || player.gety() > tileMap.getHeight()) {
            eventDead = blockInput = true;
        }

        if(eventStart) eventStart();
        if(eventDead) eventDead();
        if(eventFinish) eventFinish();
        if(eventPortal) eventPortal();
        if(eventBossDead) eventBossDead();

        temple.setPosition(tileMap.getx(), tileMap.gety());

        player.update();

        tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
        tileMap.update();
        tileMap.fixBounds();
        fire.update();
        fire1.update();

        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.update();
            if(e.isDead() || e.shouldRemove()) {
                enemies.remove(i);
                i--;
                explosions.add(new Explosion(tileMap, e.getx(), e.gety()));
            }
        }

        for(int i = 0; i < explosions.size(); i++) {
            explosions.get(i).update();
            if(explosions.get(i).shouldRemove()) {
                explosions.remove(i);
                i--;
            }
        }
    }

    public void draw(Graphics2D g) {

        temple.draw(g);

        tileMap.draw(g);
        fire.draw(g);
        fire1.draw(g);

        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        for(int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }

        player.draw(g);

        hud.draw(g);

        g.setColor(java.awt.Color.BLACK);
        for(int i = 0; i < tb.size(); i++) {
            g.fill(tb.get(i));
        }

        if(flash) {
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }

    }

    public void handleInput() {
        if(Keys.isPressed(Keys.ESCAPE)) gsm.setPaused(true);
        if(blockInput || player.getHealth() == 0) return;
        player.setUp(Keys.keyState[Keys.UP]);
        player.setLeft(Keys.keyState[Keys.LEFT]);
        player.setDown(Keys.keyState[Keys.DOWN]);
        player.setRight(Keys.keyState[Keys.RIGHT]);
        player.setJumping(Keys.keyState[Keys.BUTTON1]);
        if(Keys.isPressed(Keys.BUTTON2))
            player.setAttacking();

    }


//////////////////// EVENTS////////////////////////

    private void reset() {
        player.reset();
        player.setPosition(50, 190);
        populateEnemies();
        eventStart = blockInput = true;
        eventCount = 0;
        eventStart();
    }

    private void eventStart() {
        eventCount++;
        if(eventCount == 1) {
            tb.clear();
            tb.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
            tb.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
            tb.add(new Rectangle(0, GamePanel.HEIGHT / 2, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
            tb.add(new Rectangle(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
        }
        if(eventCount > 1 && eventCount < 60) {
            tb.get(0).height -= 4;
            tb.get(1).width -= 6;
            tb.get(2).y += 4;
            tb.get(3).x += 6;
        }
        if(eventCount == 60) {
            eventStart = blockInput = false;
            eventCount = 0;
            eventPortal = blockInput = true;
            tb.clear();

        }
    }

    private void eventDead() {
        eventCount++;
        if(eventCount == 1) {
            player.setDead();
            player.stop();
        }
        if(eventCount == 60) {
            tb.clear();
            tb.add(new Rectangle(
                    GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
        }
        else if(eventCount > 60) {
            tb.get(0).x -= 6;
            tb.get(0).y -= 4;
            tb.get(0).width += 12;
            tb.get(0).height += 8;
        }
        if(eventCount >= 120) {
            if(player.getLives() == 0) {
                gsm.setState(GameStateManager.MENUSTATE);
            }
            else {
                eventDead = blockInput = false;
                eventCount = 0;
                player.loseLife();
                reset();
            }
        }
    }

    private void eventFinish() {
        eventCount++;
        if(eventCount == 1) {
            tb.clear();
            tb.add(new Rectangle(
                    GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
        }
        else if(eventCount > 1) {
            tb.get(0).x -= 6;
            tb.get(0).y -= 4;
            tb.get(0).width += 12;
            tb.get(0).height += 8;
        }
        if(eventCount == 60) {
            PlayerSave.setHealth(player.getHealth());
            PlayerSave.setLives(player.getLives());
            gsm.setState(GameStateManager.FINISHSTATE);
        }

    }

    private void eventPortal() {

        eventCount++;
        if(eventCount == 1) {
            eventCount = 60;
        }
        if(eventCount == 60)
        {
            flash = true;
            inferno.setPosition(160, 160);
            DarkEnergy de;
            for(int i = 0; i < 20; i++) {
                de = new DarkEnergy(tileMap);
                de.setPosition(160, 160);
                de.setVector(Math.random() * 10 - 5, Math.random() * -2 - 3);
                enemies.add(de);
            }
        }
        if(eventCount == 162) {
            flash = false;
        }

        if(eventCount == 420) {
            eventPortal = blockInput = false;
            eventCount = 0;
            inferno.setActive();
        }

    }

    public void eventBossDead() {
        eventCount++;
        if(eventCount == 1) {
            player.stop();
            enemies.clear();
        }
        if(eventCount <= 120 && eventCount % 15 == 0) {
            explosions.add(new Explosion(tileMap, inferno.getx(), inferno.gety()));
        }
        if(eventCount == 390) {
            eventBossDead = false;
            eventCount = 0;
            eventFinish = true;
        }
    }

}

