package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

public class Player {
    public boolean dead = false;
    int x = 960;  //1920
    int y = 540;  //1080
    public int speed;
    double angle; // Angle of rotation in radians
    int maxHealth = 50;
    int health = maxHealth;
    BufferedImage strip;
    private Weapon currentWeapon;
    private int weaponIndex = 0;
    private final Weapon[] weapons;
    Rectangle2D hitboxdraw;
    int iter = 80;
    Image img;
    Image imgblood;
    boolean bloodDraw = false;
    private void aniframe(BufferedImage strip) {
        try {
            // Load the player's image
            strip = ImageIO.read(new File("src/burngame/icons/carter" + weaponIndex + ".png"));
        } catch (IOException ex) {
            System.out.println("Image Failed to Load");
        }
        Rectangle spotlight = new Rectangle(0, iter, 40, 40);
        Image frame = strip.getSubimage(0, iter, 40, 40);
        img = frame.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
       // img =  frame; 
    }

    public Player() {
        try {
            // Load the player's image
            strip = ImageIO.read(new File("src/burngame/icons/carter" + weaponIndex + ".png"));
            imgblood = ImageIO.read(new File("src/burngame/icons/blood.png"));
            aniframe(strip);
        } catch (IOException ex) {
            System.out.println("Image Failed to Load");
        }
        weapons = new Weapon[] {
            new Weapon("Knife", false),
            new Weapon("Pistol", false),
            new Weapon("Assault Rifle", false)
        };
        currentWeapon = weapons[weaponIndex];
    }
    
    public void switchWeapon(int direction) {
        weaponIndex = (weaponIndex + direction + weapons.length) % weapons.length;
        currentWeapon = weapons[weaponIndex];
        iter = 0;
        if(currentWeapon.getName().equals("Knife")){
            iter = 80;
        }
    }

    public void shoot() {
        if (currentWeapon != null) {
            try {
                currentWeapon.shoot(Main.mouseX, Main.mouseY, 960, 540, false);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (currentWeapon.getName().equals("Knife")){
            System.out.println("balls");
            iter = 0;
            
        }
    }
   
    public String getWeaponName() {
        return currentWeapon != null ? currentWeapon.getName() : "None";
    }
    public Weapon getCurrentWeapon(){
        return currentWeapon;
    }
    public int getAmmo(){
        return currentWeapon.clip;
    }
   
    public void updateDirection(int mouseX, int mouseY){
      //  angle = Math.atan2(mouseY - y, mouseX-x) +1.57;
        angle = Math.atan2(mouseY - (y + img.getHeight(null) / 2), mouseX - (x + img.getWidth(null) / 2)) +1.57;
        
    }
    
    public void updatePlayerPosition(int worldX, int worldY,  Set<Integer> pressedKeys, int counter){
        if (currentWeapon.getName().equals("Knife")){
            speed = 12;
        }
        else{
            speed = 8;
        }
        if(pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_D)){
            if (pressedKeys.contains(KeyEvent.VK_W)){
                Main.worldY -= speed;
            }
            if (pressedKeys.contains(KeyEvent.VK_A)){
                Main.worldX -= speed;
            }
            if (pressedKeys.contains(KeyEvent.VK_S)){
                Main.worldY += speed;
            }
            if (pressedKeys.contains(KeyEvent.VK_D)){
                Main.worldX += speed;
            }
            if(iter == 360 && weaponIndex == 0){
                iter = 80;
            }
            if(iter == 120 && weaponIndex == 1){
                iter = 0;
            }
            if(iter == 120 && weaponIndex == 2){
                iter = 0;
            }
            
            if(counter == 3 || counter == 6){
                if(weaponIndex == 0){
                  iter += 40;
                }
                if(!(weaponIndex == 0) && counter == 6){
                    iter += 40;
                }
            }
        }  
        // Resolve any collisions
        aniframe(strip);
        resolveCollisions();
    }
        
    public void resolveCollisions() {
        Rectangle2D playerHitbox = this.getRotatedHitbox();

         for (Enemy e : Main.enemies){
            Rectangle enemyBounds = e.getBounds();
            enemyBounds.x -= Main.worldX;
            enemyBounds.y -= Main.worldY;
            pushOut(playerHitbox,enemyBounds);
        }
        
        
        for (Wall wall : Main.walls) {
            Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            pushOut(playerHitbox, wallBounds);
        }
        
    }
    private void pushOut(Rectangle2D hitbox, Rectangle2D wallBounds){
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
                    Main.worldX += disX;
                } else {
                    Main.worldY += disY;
                }
            }
 }
    public void draw(Graphics g) {
        x -= img.getWidth(null)/2;
        y -= img.getHeight(null)/2;
        updateDirection(Main.mouseX,Main.mouseY);
        Graphics2D g2d = (Graphics2D) g;
        
        // Calculate the center of the image for rotation
        int centerX = x + img.getWidth(null) / 2;
        int centerY = y + img.getHeight(null) / 2;

        // Save the current transform
        AffineTransform oldTransform = g2d.getTransform();

        // Rotate around the center of the player
        g2d.rotate(angle, centerX, centerY);

        // Draw the image, adjusted to keep it centered
        g2d.drawImage(img, x, y, null);

        // Restore the original transform
        g2d.setTransform(oldTransform);
        //g2d.draw(hitboxdraw);
        
        if (bloodDraw){
            g2d.drawImage(imgblood,960-(imgblood.getWidth(null)/2),540-(imgblood.getHeight(null)/2),null);
        }
    }
    
   public Rectangle2D getRotatedHitbox() {

        // Create the unrotated rectangle centered at (960, 540)
        Rectangle2D rect = new Rectangle2D.Double(960 - 100 / 2, 540 - 33 / 2, 100, 33);
        
        // Rotate the rectangle around its center
        AffineTransform transform = AffineTransform.getRotateInstance(angle, 960, 540);
        hitboxdraw = transform.createTransformedShape(rect).getBounds2D();
        return transform.createTransformedShape(rect).getBounds2D();
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            dead = true; // Trigger death logic
        }

    // Set bloodDraw to true
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
}   
 
    

