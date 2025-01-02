package burngame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Wall {
    int x, y, width, height;
   public boolean hardwall;

    public Wall(int x, int y, int width, int height, boolean hard) {
        this.x = x +Main.worldX;
        this.y = y+ Main.worldY;
        this.width = width;
        this.height = height;
        this.hardwall = hard;
    }

    // Method to get the wall's bounds
   public void draw(Graphics g) {
    g.setColor(Color.BLACK); // Wall color
    g.fillRect(x - Main.worldX, y - Main.worldY, width, height); // Adjusted position for world movement
}

// Update getBounds method for collision detection
public Rectangle getBounds(int x, int y) {
    return new Rectangle(x, y, width, height); // Adjusted position for world movement
}
}

