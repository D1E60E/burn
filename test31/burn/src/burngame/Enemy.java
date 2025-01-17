package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Enemy {
    private int health;
    private int x, y; // Enemy's position in the world
    private Image enemyImage;
    public Weapon weapon;
    private boolean canSee;
    Line2D lineofsight;
    private long lastSeenTime = -1; // Tracks when the player was last seen
    private int reactionTime; // Reaction time in milliseconds
    private int level;
    private double accuracy;
    double angle;
    private boolean bloodDraw = false;
    Image imgblood;
    private int speed;

    public Enemy(int x, int y, String type, int level) {
        this.x = x;
        this.y = y;
        this.health = 30; // Default health
        this.weapon = new Weapon(type, true);
        this.level = level;
        lineofsight = new Line2D.Double(960, 540, x-Main.worldX, y-Main.worldY);
        loadEnemyLevel();
        // Load the enemy image
        try {
            Image enemyImag = ImageIO.read(new File("src/burngame/icons/"+type+"guy.png"));
            imgblood = ImageIO.read(new File("src/burngame/icons/blood.png"));
            enemyImage = enemyImag.getScaledInstance(100, 33, Image.SCALE_DEFAULT);
        } catch (IOException ex) {
            System.out.println("Failed to load enemy image.");
        }
    }

    // Method to reduce health when hit by a weapon
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die(); // Trigger death logic
        }
        
         bloodDraw = true;

        // Create a new thread to reset bloodDraw after 0.5 seconds
        new Thread(() -> {
        try {
            Thread.sleep(230); // 500 milliseconds = 0.5 seconds
        } catch (InterruptedException e) {
        }
        bloodDraw = false; // Reset bloodDraw
    }).start();
    }

    // What happens when the enemy dies
    private void die() {
        // Remove the enemy from the game
        Main.enemies.remove(this);
        bloodDraw = true;
    }

    // Check if the enemy is alive
    public boolean isAlive() {
        return health > 0;
    }
    
    private void loadEnemyLevel(){
        if (level == 1){
            reactionTime = 300;
            accuracy = 0.7;
        }
    }
    // Draw the enemy on the screen
    public void draw(Graphics g) {
        if (health <=0)return;
        updateRotation();
        // Convert the enemy's world position to screen position
        int screenX = x - Main.worldX;
        int screenY = y - Main.worldY;
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        // Rotate around the center of the player
        g2d.rotate(angle, screenX, screenY);
        // Draw the enemy image
        g2d.drawImage(enemyImage, screenX - enemyImage.getWidth(null) / 2, screenY - enemyImage.getHeight(null) / 2, null);
        g2d.setTransform(oldTransform);
        checkSight();
        if (canSee){
         long currentTime = System.currentTimeMillis();
         moveToPlayer();
        if (lastSeenTime != -1 && currentTime - lastSeenTime >= reactionTime) {
            shoot(); // Shoot at the player
        }
        if ((currentTime - lastSeenTime)<= 1000){
            moveToPlayer();
        }
        }
        
        if (bloodDraw){
            g2d.drawImage(imgblood,screenX-(imgblood.getWidth(null)/2),screenY-(imgblood.getHeight(null)/2),null);
        }
        updateSpeed();
        resolveCollisions();
    }
    
   
    // Get the enemy's bounding box for collision detection
   public Rectangle getBounds() {
    int width = 100;
    int height = 33;
    AffineTransform transform = AffineTransform.getRotateInstance(angle, x, y);
    Rectangle2D originalRect = new Rectangle2D.Double(x - width / 2.0, y - height / 2.0, width,height);
    Shape rotatedRect = transform.createTransformedShape(originalRect);
    return rotatedRect.getBounds();
}


private void updateSpeed(){
    if (this.weapon.getName().equals("Knife")){
        speed = 7;
    }else{
        speed = 0;
    }
}
    
    private void updateRotation(){
        if (canSee){
             int enemyCenterX = x + enemyImage.getWidth(null) / 2;
               int enemyCenterY = y + enemyImage.getHeight(null) / 2;

        // Player is always at (960, 540) in screen coordinates
        angle = Math.atan2(540 - (enemyCenterY - Main.worldY), 960 - (enemyCenterX - Main.worldX))+1.57;
        }
    }
    
    // Getter for health (optional)
    public int getHealth() {
        return health;
    }
    
    public void shoot() {
    // Base target is the player at the center of the screen
    int targetX = 960; // Player's screen center X
    int targetY = 540; // Player's screen center Y

    // Apply accuracy offset
    double maxOffset = (1.0 - accuracy) * 200; // Maximum offset based on accuracy
    targetX += (int) ((Math.random() * 2 - 1) * maxOffset); // Random offset between -maxOffset and +maxOffset
    targetY += (int) ((Math.random() * 2 - 1) * maxOffset);

        try {
            // Call weapon's shoot method with adjusted target
            weapon.shoot(targetX, targetY, x - Main.worldX, y - Main.worldY, true);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Enemy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkSight() {
        lineofsight = new Line2D.Double(960, 540, x - Main.worldX, y - Main.worldY);
        canSee = true; // Assume clear sight unless proven otherwise
        Rectangle enemyBounds = this.getBounds();
        enemyBounds.x -= Main.worldX;
        enemyBounds.y -= Main.worldY;

        // Check if enemy is within the visible screen bounds
        if (!enemyBounds.intersects(new Rectangle(0, 0, 1920, 1080))) {
            canSee = false;
        }

        // Check if any walls block the line of sight
        for (Wall wall : Main.walls) {
            if (wall.hardwall){
            Rectangle bounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);

            if (lineofsight.intersects(bounds)) {
                canSee = false; // Line of sight blocked
                break;
            }
        }
        }
        // Update hostility and reaction time
        if (canSee) {
            if (lastSeenTime == -1) {
                lastSeenTime = System.currentTimeMillis(); // Record first sighting time
            }
        } else {
            lastSeenTime = -1; // Reset if the player is not visible
        }
        
    }

    public void moveToPlayer() {
    // Calculate the direction vector from the enemy to the player
    double deltaX = (960 + Main.worldX) - x; // Player's global X position
    double deltaY = (540 + Main.worldY) - y; // Player's global Y position

    // Calculate the distance to normalize the direction
    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Avoid division by zero
    if (distance == 0) return;

    // Normalize the direction and scale by speed
    double moveX = (deltaX / distance) * speed;
    double moveY = (deltaY / distance) * speed;

    // Update the enemy's position
    x += moveX;
    y += moveY;
    resolveCollisions();
}
 public void resolveCollisions() {
     Rectangle hitbox = this.getBounds();
     hitbox.x -= Main.worldX;
     hitbox.y -= Main.worldY;
     Rectangle playerHitbox = Main.getPlayerBounds();
     pushOut(hitbox,playerHitbox);
        
        for (Enemy e : Main.enemies){
            if (e != this){
                
            Rectangle enemyBounds = e.getBounds();
            enemyBounds.x -= Main.worldX;
            enemyBounds.y -= Main.worldY;
            pushOut(hitbox,enemyBounds);
        }
        }
        
        for (Wall wall : Main.walls) {
            Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            pushOut(hitbox, wallBounds);
        }
    }

 
 private void pushOut(Rectangle hitbox, Rectangle2D wallBounds){
     if (hitbox.intersects(wallBounds)) {
                double disX, disY;

                // Horizontal collision
                if (hitbox.getCenterX() < wallBounds.getCenterX()) {
                    disX = wallBounds.getMinX() - hitbox.getMaxX();
                } else {
                    disX = wallBounds.getMaxX() - hitbox.getMinX();
                }

                // Vertical collision
                if (hitbox.getCenterY() < wallBounds.getCenterY()) {
                    disY = wallBounds.getMinY() - hitbox.getMaxY();
                } else {
                    disY = wallBounds.getMaxY() - hitbox.getMinY();
                }

                // Apply the smaller push-out value
                if (Math.abs(disX) < Math.abs(disY)) {
                    x += disX;
                } else {
                    y += disY;
                }
            }
 }
}

