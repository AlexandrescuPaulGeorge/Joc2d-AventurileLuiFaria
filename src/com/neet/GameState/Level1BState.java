package com.neet.GameState;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.neet.Audio.JukeBox;
import com.neet.Entity.Enemy;
import com.neet.Entity.Explosion;
import com.neet.Entity.*;
import com.neet.Entity.Enemies.DarkLord;
import com.neet.Handlers.Keys;
import com.neet.Main.GamePanel;
import com.neet.TileMap.Background;
import com.neet.TileMap.TileMap;


public class Level1BState extends GameState {

    private Background temple;

    private Player player;
    private TileMap tileMap;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;

    private HUD hud;
    private Teleport teleport;

    // eventuri
    private boolean blockInput = false;
    private int eventCount = 0;
    private boolean eventStart;
    private ArrayList<Rectangle> tb;
    private boolean eventFinish;
    private boolean eventDead;

    public Level1BState(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        temple = new Background("/Backgrounds/backgr2.gif", 0.5, 0);

        tileMap = new TileMap( 30);
        tileMap.loadTiles("/Tilesets/level2set.gif");
        tileMap.loadMap("/Maps/level2.map");
        tileMap.setPosition(140, 0);
        tileMap.setTween(1);

        player = new Player(tileMap);
        player.setPosition(131, 121);
        player.setHealth(PlayerSave.getHealth());
        player.setLives(PlayerSave.getLives());
        player.setTime(PlayerSave.getTime());

        enemies = new ArrayList<Enemy>();
        populateEnemies();

        player.init(enemies);

        explosions = new ArrayList<Explosion>();

        hud = new HUD(player);

        teleport = new Teleport(tileMap);
        teleport.setPosition(4400, 400);


        eventStart = true;
        tb = new ArrayList<Rectangle>();
        eventStart();

        //JukeBox.load("/Music/billie.mp3", "billie");
        //JukeBox.loop("billie", 600, JukeBox.getFrames("billie") - 2200);

    }

    private void populateEnemies() {
        enemies.clear();
        DarkLord x;

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(1350, 240);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(1740, 240);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(2250, 600);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(2400, 450);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(3480, 480);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(3980, 570);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(4050, 510);
        enemies.add(x);

        x = new DarkLord(tileMap, player,enemies);
        x.setPosition(4130, 450);
        enemies.add(x);
    }

    public void update() {

        handleInput();

        if(teleport.contains(player)) {
            eventFinish = blockInput = true;
        }

        if(eventStart)
            eventStart();
        if(eventDead)
            eventDead();
        if(eventFinish)
            eventFinish();


        temple.setPosition(tileMap.getx(), tileMap.gety());


        player.update();
        if(player.getHealth() == 0 || player.gety() > tileMap.getHeight()) {
            eventDead = blockInput = true;
        }

        tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
        tileMap.update();
        tileMap.fixBounds();

        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.update();
            if(e.isDead()) {
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

        teleport.update();

    }

    public void draw(Graphics2D g) {

        temple.draw(g);

        tileMap.draw(g);

        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        for(int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }

        player.draw(g);

        teleport.draw(g);

        hud.draw(g);

        g.setColor(java.awt.Color.BLACK);
        for(int i = 0; i < tb.size(); i++) {
            g.fill(tb.get(i));
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


//////////////////// EVENTS////////////////////

    private void reset() {
        player.loseLife();
        player.reset();
        player.setPosition(300, 131);
        populateEnemies();
        blockInput = true;
        eventCount = 0;
        eventStart = true;
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
            tb.clear();
        }
    }

    private void eventDead() {
        eventCount++;
        if(eventCount == 1) player.setDead();
        if(eventCount == 60) {
            tb.clear();
            tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
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
                reset();
            }
        }
    }

    private void eventFinish() {
        eventCount++;
        if(eventCount == 1) {
            player.setTeleporting(true);
            player.stop();
        }
        else if(eventCount == 120) {
            tb.clear();
            tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
        }
        else if(eventCount > 120) {
            tb.get(0).x -= 6;
            tb.get(0).y -= 4;
            tb.get(0).width += 12;
            tb.get(0).height += 8;
            JukeBox.stop("billie");
        }
        if(eventCount == 180) {
            PlayerSave.setHealth(player.getHealth());
            PlayerSave.setLives(player.getLives());
            gsm.setState(GameStateManager.LEVEL1CSTATE);
        }
    }
}