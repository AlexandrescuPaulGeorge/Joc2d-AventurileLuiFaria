package com.neet.Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.neet.Audio.JukeBox;
import com.neet.TileMap.TileMap;

public class Player extends MapObject {

    // referinte
    private ArrayList<Enemy> enemies;

    // player stuff
    private int lives;
    private int health;
    private int maxHealth;
    private int damage;
    private boolean knockback;
    private boolean flinching;
    private long flinchCount;
    private boolean doubleJump;
    private boolean alreadyDoubleJump;
    private double doubleJumpStart;
    private long time;

    // actions

    private boolean attacking;
    private boolean teleporting;

    // animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] NUMFRAMES = {
            1,8,5,3,3,1,3,2
    };
    private final int[] FRAMEWIDTHS = {
            40,40,80,40,40,40,40,40
    };
    private final int[] FRAMEHEIGHTS = {
            40, 40, 40, 40, 40, 40 ,40,40
    };
    private final int[] SPRITEDELAYS = {
            -1, 3, 7,13,12, 1 ,1,1
    };

    private Rectangle ar;

    // animatii
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int ATTACKING = 2;
    private static final int JUMPING = 3;
    private static final int FALLING = 4;
    private static final int DEAD = 5;
    private static final int TELEPORTING = 6;
    private static int KNOCKBACK = 7;


    public Player(TileMap tm) {

        super(tm);

        ar = new Rectangle(0, 0, 0, 0);
        ar.width = 50;
        ar.height = 20;

        width =40;
        height = 40;
        cwidth = 8;
        cheight = 38;

        moveSpeed = 1.6;
        maxSpeed = 1.6;
        stopSpeed = 1.6;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;
        doubleJumpStart = -3;

        damage = 2;

        facingRight = true;

        lives = 3;
        health = maxHealth = 5;

        // incarcarea spriteurilor
        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/test.gif"));

            int count = 0;
            sprites = new ArrayList<BufferedImage[]>();

            for(int i = 0; i < NUMFRAMES.length; i++) {
                BufferedImage[] bi = new BufferedImage[NUMFRAMES[i]];
                for(int j = 0; j < NUMFRAMES[i]; j++) {
                    bi[j] = spritesheet.getSubimage(j * FRAMEWIDTHS[i], count, FRAMEWIDTHS[i], FRAMEHEIGHTS[i]);
                }
                sprites.add(bi);
                count += FRAMEHEIGHTS[i];
            }

        }
        catch(Exception e) {
            System.out.println("Eroare in Entity -> Player");
            e.printStackTrace();
        }

        setAnimation(IDLE);

    }

    public void init(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public int getHealth()
    {
        return health;
    }
    public void setTeleporting(boolean b)
    {
        teleporting=b;
        setAnimation(TELEPORTING);
    }


    public void setJumping(boolean b) {
        if(knockback)
            return;
        if(b && !jumping && falling && !alreadyDoubleJump) {
            doubleJump = true;
        }
        jumping = b;
    }
    public void setAttacking() {
        if(knockback)
            return;
        attacking = true;
    }

    public void setDead() {
        health = 0;
        stop();
    }

    public String getTimeToString() {
        int minutes = (int) (time / 3600);
        int seconds = (int) ((time % 3600) / 60);
        return seconds < 10 ? minutes + ":0" + seconds : minutes + ":" + seconds;
    }

    public void setTime(long t) { time = t; }
    public void setHealth(int i) { health = i; }
    public void setLives(int i) { lives = i; }
    public void loseLife() { lives--; }
    public int getLives() { return lives; }



    public void hit(int damage) {
        if(flinching)
            return;
        stop();
        health -= damage;
        if(health < 0)
            health = 0;
        flinching = true;
        flinchCount = 0;
        if(facingRight)
            dx = -1;
        else
            dx = 1;
        dy = -3;
        knockback = true;
        falling = true;
        jumping = false;
    }

    public void reset() {
        health = maxHealth;
        facingRight = true;
        currentAction = -1;
        stop();
    }

    public void stop() {
        left = right = up = down  = jumping = attacking = teleporting= flinching = false;
    }

    private void getNextPosition() {

        if(knockback) {
            dy += fallSpeed * 2;
            if(!falling)
                knockback = false;
            return;
        }

        double maxSpeed = this.maxSpeed;

        // miscarea jucatorului
        if(left) {
            dx -= moveSpeed;
            if(dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        }
        else if(right) {
            dx += moveSpeed;
            if(dx > maxSpeed) {
                dx = maxSpeed;
            }
        }
        else {
            if(dx > 0) {
                dx -= stopSpeed;
                if(dx < 0) {
                    dx = 0;
                }
            }
            else if(dx < 0) {
                dx += stopSpeed;
                if(dx > 0) {
                    dx = 0;
                }
            }
        }

        //nu se poate misca in timp ce ataca ,cu exceptia cand sare
        if((attacking) && !(jumping || falling)) {
            dx = 0;
        }

        // saritura
        if(jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        if(doubleJump) {
            dy = doubleJumpStart;
            alreadyDoubleJump = true;
            doubleJump = false;
        }

        if(!falling)
            alreadyDoubleJump = false;

        // pt cazul cand cade
        if(falling) {
            dy += fallSpeed;
            if(dy < 0 && !jumping)
                dy += stopJumpSpeed;
            if(dy > maxFallSpeed)
                dy = maxFallSpeed;
        }

    }

    private void setAnimation(int i) {
        currentAction = i;
        animation.setFrames(sprites.get(currentAction));
        animation.setDelay(SPRITEDELAYS[currentAction]);
        width = FRAMEWIDTHS[currentAction];
        height = FRAMEHEIGHTS[currentAction];
    }

    public void update() {

        time++;

        //verfica teleportarea
        if(teleporting) {
            setAnimation(TELEPORTING);
        }

        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        if(dx == 0)
            x = (int)x;

        if(flinching) {
            flinchCount++;
           if(flinchCount > 120) {
              flinching = false;
            }
        }

        // verifica daca atacul s-a terminat
        if(currentAction == ATTACKING ) {
            if(animation.hasPlayedOnce()) {
                attacking = false;

            }
        }

        // interactionea cu inamicii
        for(int i = 0; i < enemies.size(); i++) {

            Enemy e = enemies.get(i);

            // check attack
            if (currentAction == ATTACKING && animation.getFrame() == 3 && animation.getCount() == 0) {
                if (e.intersects(ar)) {
                    e.hit(damage);
                }
            }

            if(!e.isDead() && intersects(e)) {
                hit(e.getDamage());
            }

            if(e.isDead()) {
               // JukeBox.play("explode", 2000);
            }

        }

        // animatiile in ordinea prioritatii
        if(teleporting) {
            if(currentAction != TELEPORTING) {
                setAnimation(TELEPORTING);
            }
        }
        else if(knockback) {
            if(currentAction != KNOCKBACK) {
                setAnimation(KNOCKBACK);
            }
        }
        else if (health == 0) {
            if (currentAction != DEAD) {
                setAnimation(DEAD);
            }
        }
        else if(attacking) {
            if(currentAction != ATTACKING) {
                setAnimation(ATTACKING);
                ar.y = (int)y - 6;
                if(facingRight)
                    ar.x = (int)x + 10;
                else
                    ar.x = (int)x - 40;
            }

        }
        else if (dy < 0) {
            if (currentAction != JUMPING) {
                setAnimation(JUMPING);
            }
        } else if (dy > 0) {
            if (currentAction != FALLING) {
                setAnimation(FALLING);
            }
        }
        else if (left || right) {
            if (currentAction != WALKING) {
                setAnimation(WALKING);
            }
        } else if (currentAction != IDLE) {
            setAnimation(IDLE);
        }

        animation.update();

        //directia jucatorului
        if (!attacking) {
            if (right) facingRight = true;
            if (left) facingRight = false;
        }
    }

    public void draw(Graphics2D g) {
        if(flinching) {
            if(flinchCount % 10 < 5) return;
        }
        super.draw(g);

    }

}
