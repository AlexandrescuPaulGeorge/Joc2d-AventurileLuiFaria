package com.neet.GameState;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.neet.Audio.JukeBox;
import com.neet.Entity.*;
import com.neet.Entity.Explosion;
import com.neet.Entity.Enemy;
import com.neet.Entity.Enemies.Zombie;
import com.neet.Handlers.Keys;
import com.neet.Main.GamePanel;
import com.neet.TileMap.Background;
import com.neet.TileMap.TileMap;

public class Level1AState extends GameState {

    private Background sky;

    private Player player;
    private TileMap tileMap;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;

    private HUD hud;
    private Teleport teleport;

    private boolean blockInput = false;
    private int eventCount = 0;
    private boolean eventStart;
    private ArrayList<Rectangle> tb;
    private boolean eventFinish;
    private boolean eventDead;

    public Level1AState(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public void init() {

        sky = new Background("/Backgrounds/backgr1.gif", 0);

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/level1set.gif");
        tileMap.loadMap("/Maps/level1.map");
        tileMap.setPosition(140, 0);
        tileMap.setBounds(tileMap.getWidth() - tileMap.getTileSize(), tileMap.getHeight() - 2 * tileMap.getTileSize(), 0, 0);
        tileMap.setTween(1);

        player = new Player(tileMap);
        player.setPosition(300, 200);
        player.setHealth(PlayerSave.getHealth());
        player.setLives(PlayerSave.getLives());
        player.setTime(PlayerSave.getTime());

        enemies = new ArrayList<Enemy>();
        populateEnemies();//------------------------------------------------------------------

        player.init(enemies);

        explosions = new ArrayList<Explosion>();

        hud = new HUD(player);

        teleport = new Teleport(tileMap);
        teleport.setPosition(4400, 131);

        eventStart = true;
        tb = new ArrayList<Rectangle>();
        eventStart();

       // JukeBox.load("/Music/another.mp3", "another");
       //JukeBox.loop("another", 600, JukeBox.getFrames("another") - 2200);
    }

    private void populateEnemies() {
        enemies.clear();

        Zombie z;

        z = new Zombie(tileMap, player);
        z.setPosition(1140, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(1240, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(1340, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(2430, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(2530, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(2630, 100);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(3700, 131);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(3800, 131);
        enemies.add(z);

        z = new Zombie(tileMap, player);
        z.setPosition(3900, 131);
        enemies.add(z);


    }

    public void update() {

        handleInput();

        if(player.getHealth() == 0 || player.gety() > tileMap.getHeight()) {
            eventDead = blockInput = true;
        }
        if(teleport.contains(player)) {
            eventFinish = blockInput = true;
        }

        if(eventStart)
            eventStart();
        if(eventDead)
            eventDead();
        if(eventFinish)
            eventFinish();


        player.update();

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


        sky.draw(g);
        tileMap.draw(g);

        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        teleport.draw(g);
        player.draw(g);
        hud.draw(g);

    }

    public void handleInput() {
        if(Keys.isPressed(Keys.ESCAPE))
            gsm.setPaused(true);
        if(blockInput || player.getHealth() == 0)
            return;
        player.setUp(Keys.keyState[Keys.UP]);
        player.setLeft(Keys.keyState[Keys.LEFT]);
        player.setDown(Keys.keyState[Keys.DOWN]);
        player.setRight(Keys.keyState[Keys.RIGHT]);
        player.setJumping(Keys.keyState[Keys.BUTTON1]);
        if(Keys.isPressed(Keys.BUTTON2))
            player.setAttacking();

    }


//////////////////// EVENTS/////////////////////////


    private void reset() {
        player.reset();
        player.setPosition(300, 200);
        populateEnemies();//-----------------------------------------------------------
        blockInput = true;
        eventCount = 0;
        eventStart = true;
        eventStart();

    }

    // level started
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

    // atunci cand playerul a murit
    private void eventDead() {
        eventCount++;
        if(eventCount == 1) {
            player.setDead();
            player.stop();
        }
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
                player.loseLife();
                reset();
            }
        }
    }
    private void eventFinish()
    {
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
            JukeBox.stop("another");
        }
        if(eventCount == 180) {
            PlayerSave.setHealth(player.getHealth());
            PlayerSave.setLives(player.getLives());
            gsm.setState(GameStateManager.LEVEL1BSTATE);
        }

    }

}


