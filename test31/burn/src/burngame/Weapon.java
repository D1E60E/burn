package burngame;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Weapon {
    private String name;
    private int damage;
    int fireRate; // Bullets per second
    private boolean automatic; // True for automatic weapons (AR), false for semi-automatic (Pistol)
    Image sparkImage;
    String sparkPath = "src/burngame/icons/spark.png";
    int clip;
    boolean reloading = false;    
    private long lastShotTime = 0;

    public Weapon(String name, boolean enemy) {
        this.name = name;
        switch (name) {
            case "Pistol" -> {
                this.damage = 10;
                this.fireRate = 5;
                this.automatic = false;
                this.clip = -1;
            }
            case "Assault Rifle" -> {
                this.damage = 10;
                this.fireRate = 10;
                this.automatic = true;
                this.clip = 24;
            }
            case "Knife" -> {
                this.damage = 100;
                this.fireRate = 10;
                this.automatic = false;
                this.clip = -1;
            }
            default -> {
            }
        }
        try {
            sparkImage = ImageIO.read(new File(sparkPath));
        } catch (IOException ex) {
            System.out.println("Failed to load spark image for " + name);
        }
         if (enemy) fireRate /= 2;
    }

    public boolean canShoot() {
        if (clip == 0){
            reloading = true;
            clip = 24;
            return false;     
        }
        long currentTime = System.currentTimeMillis();
        if (reloading){
         if (currentTime - lastShotTime >= 3500){
             reloading = false;
             return true;
         }else{
             return false;
         }
        }else{
        if (currentTime - lastShotTime >= 1000 / fireRate) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
        }
    }
  
    
    public String getName() {
        return name;
    }
  
   
   public boolean isAutomatic() {
    return automatic;
}
    
public void shoot(int x, int y, int startX, int startY, boolean enemy) throws UnsupportedAudioFileException {
    if (!canShoot()) return;
    clip--;
    if (name.equals("Pistol")) Main.playSound("Pistol");
    if (name.equals("Assault Rifle")) Main.playSound("AR");
    
    // Adjust position to world coordinates
    int adjustedX = x + Main.worldX;
    int adjustedY = y + Main.worldY;

    // Calculate direction vector
    double directionX = adjustedX - (startX + Main.worldX);
    double directionY = adjustedY - (startY + Main.worldY);

    // Normalize direction vector
    double length = Math.sqrt(directionX * directionX + directionY * directionY);
    if (length == 0) return;
    directionX /= length;
    directionY /= length;

    // Variables to track closest intersection
    double closestDistance;
    if(name.equals("Knife")){
        closestDistance = 100;
    }else{
        closestDistance = Double.MAX_VALUE;
    }
    int sparkX = -1, sparkY = -1;
    boolean hitSomething = false;

    // Check ray against walls first
    for (Wall wall : Main.walls) {
        if (wall.hardwall) {
            Rectangle bounds = wall.getBounds(wall.x, wall.y);
            Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

            if (intersection != null && !name.equals("Knife")) {
                double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    sparkX = (int) intersection.getX();
                    sparkY = (int) intersection.getY();
                    hitSomething = true;
                }
            }
        }
    }

    if (enemy) {
        // Check if the ray intersects the player
        Rectangle bounds = Main.getPlayerBounds();
        bounds.x += Main.worldX;
        bounds.y += Main.worldY;
        Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

        if (intersection != null) {
            double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
            if (distance < closestDistance) {
                Main.getPlayer().takeDamage(damage);
                return;
            }
        }
    } else {
        // Check if the ray intersects any enemy
        for (Enemy e : Main.enemies) {
            Rectangle bounds = e.getBounds();
            Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

            if (intersection != null) {
                double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
                if (distance < closestDistance) {
                    e.takeDamage(damage);
                    return;
                }
            }
        }
    }

    // If no enemies or player were hit, create a spark only for wall hits
    if (hitSomething) {
        Main.sparks.add(new Spark(sparkX, sparkY, sparkImage));
    }
}




private Point2D getRayWallIntersection(double startX, double startY, double dirX, double dirY, Rectangle wall) {
    // Wall edges (top, bottom, left, right)
    Line2D[] edges = {
        new Line2D.Double(wall.x, wall.y, wall.x + wall.width, wall.y),              // Top edge
        new Line2D.Double(wall.x, wall.y, wall.x, wall.y + wall.height),            // Left edge
        new Line2D.Double(wall.x + wall.width, wall.y, wall.x + wall.width, wall.y + wall.height), // Right edge
        new Line2D.Double(wall.x, wall.y + wall.height, wall.x + wall.width, wall.y + wall.height) // Bottom edge
    };

    // Track the closest intersection
    Point2D closestIntersection = null;
    double closestDistance = Double.MAX_VALUE;

    for (Line2D edge : edges) {
        Point2D intersection = getLineIntersection(startX, startY, startX + dirX * 10000, startY + dirY * 10000, edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2()); //*10000 to make sure the ray is far enough
        if (intersection != null) {
            // Calculate distance to the intersection point
            double distance = intersection.distance(startX, startY);

            // Update the closest intersection
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIntersection = intersection;
            }
        }
    }

    return closestIntersection; // Return the closest intersection
}


private Point2D getLineIntersection(double x1, double y1, double x2, double y2,
                                    double x3, double y3, double x4, double y4) {
    double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

    // Lines are parallel if denominator is zero
    if (denom == 0) return null;

    double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
    double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom;

    // Check if intersection is within the ray and wall segment
    if (t >= 0 && u >= 0 && u <= 1) {
        double intersectX = x1 + t * (x2 - x1);
        double intersectY = y1 + t * (y2 - y1); 
        return new Point2D.Double(intersectX, intersectY); // Return the intersection point
    }

    return null; // No valid intersection
}

    
}