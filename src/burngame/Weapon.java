/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package burngame;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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

    public Weapon(String name) {
        this.name = name;
        if (name.equals("Pistol")){
            this.damage = 10;
            this.fireRate = 5;
            this.automatic = false;
            this.clip = -1;
        }else if (name.equals("Assault Rifle")){
            this.damage = 10;
            this.fireRate = 10;
            this.automatic = true;
            this.clip = 30;
        }
        try {
            sparkImage = ImageIO.read(new File(sparkPath));
        } catch (IOException ex) {
            System.out.println("Failed to load spark image for " + name);
        }
    }

    public boolean canShoot() {
        if (clip == 0){
            reloading = true;
            clip = 30;
            return false;     
        }
        long currentTime = System.currentTimeMillis();
        if (reloading){
            if(currentTime - lastShotTime >= 3500){
                reloading = false;
                lastShotTime = currentTime;
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
   
   public int getcount(){
       return clip;
   }
    
    
public void shoot(int x, int y, int startX, int startY, boolean enemy) {
    if (!canShoot()) return;
    clip--;
    // Adjust position to world coordinates
    int adjustedX = x + Main.worldX;
    int adjustedY = y + Main.worldY;

    // Calculate direction vector from the player to the adjusted mouse position
    double directionX = adjustedX - (startX + Main.worldX);
    double directionY = adjustedY - (startY + Main.worldY);

    // Normalize the direction vector
    double length = Math.sqrt(directionX * directionX + directionY * directionY);
    if (length == 0) return; // Avoid division by zero
    directionX /= length;
    directionY /= length;

    // Variables to track the closest intersection
    double closestDistance = Double.MAX_VALUE;
    int sparkX = -1, sparkY = -1;

    // Check ray against each wall
    for (Wall wall : Main.walls) {
        if (wall.hardwall){
        // Get the wall's bounds in absolute world coordinates
        Rectangle bounds = wall.getBounds(wall.x, wall.y);

        // Get intersection point with the ray
        Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);
        
        if (intersection != null) {
            // Calculate distance to the intersection point
            double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);

            // Keep track of the closest intersection
            if (distance < closestDistance) {
                closestDistance = distance;
                sparkX = (int) intersection.getX();
                sparkY = (int) intersection.getY();
            
        }}}}

    // If there was a valid intersection, create a spark at that position
    if (sparkX != -1 && sparkY != -1) {
        Main.sparks.add(new Spark(sparkX, sparkY, sparkImage)); // Pass exact world coordinates
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
        Point2D intersection = getLineIntersection(
            startX, startY,
            startX + dirX * 10000, startY + dirY * 10000, // Extend ray far enough
            edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2()
        );

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