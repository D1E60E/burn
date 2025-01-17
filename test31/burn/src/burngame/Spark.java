package burngame;

import java.awt.Graphics;
import java.awt.Image;

public class Spark {
    int worldX, worldY; // Spark's static world position
    Image img;
    long createTime;
    int lifetime = 300; // Spark lasts 300 ms

    public Spark(int worldX, int worldY, Image img) {
        this.worldX = worldX; // Save the exact world coordinates at creation
        this.worldY = worldY;
        this.img = img;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > lifetime;
    }

    public void draw(Graphics g) {
        // Convert the spark's world position to screen position
        int screenX = worldX - Main.worldX;
        int screenY = worldY - Main.worldY;
        // Center the spark image and draw it
        g.drawImage(img, screenX - img.getWidth(null) / 2, screenY - img.getHeight(null) / 2, null);
    }
}
