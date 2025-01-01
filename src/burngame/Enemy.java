package burngame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy {
    private int health;
    private int worldX, worldY; // Enemy's position in the world
    private Image enemyImage;

    public Enemy(int worldX, int worldY, String type) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.health = 30; // Default health

        // Load the enemy image
        try {
            enemyImage = ImageIO.read(new File(type));
        } catch (IOException ex) {
            System.out.println("Failed to load enemy image.");
        }
    }

    // Method to reduce health when hit by a weapon
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            onDeath(); // Trigger death logic
        }
    }

    // What happens when the enemy dies
    private void onDeath() {
        // Remove the enemy from the game
        Main.enemies.remove(this);
    }

    // Check if the enemy is alive
    public boolean isAlive() {
        return health > 0;
    }

    // Draw the enemy on the screen
    public void draw(Graphics g) {
        // Convert the enemy's world position to screen position
        int screenX = worldX - Main.worldX;
        int screenY = worldY - Main.worldY;
        int playerX = 960;  
        int playerY = 540;
        
        for (Wall wall : Main.walls) {
            Line2D lineofsight = new Line2D.Double(playerX, playerY, screenX, screenY);
            g.drawLine(playerX, playerY, screenX, screenY);
            // Get the wall's bounds in absolute world coordinates
            Rectangle bounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            if(lineofsight.intersects(bounds)){
                System.out.println("balls");
            }
        }   

        // Draw the enemy image
        g.drawImage(enemyImage, screenX - enemyImage.getWidth(null) / 2,
                    screenY - enemyImage.getHeight(null) / 2, null);
    }

    // Get the enemy's bounding box for collision detection
    public Rectangle getBounds() {
        return new Rectangle(worldX - enemyImage.getWidth(null) / 2,
                             worldY - enemyImage.getHeight(null) / 2,
                             enemyImage.getWidth(null),
                             enemyImage.getHeight(null));
    }
    
    // Getter for health (optional)
    public int getHealth() {
        return health;
    }
}
