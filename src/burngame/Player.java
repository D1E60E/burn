package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {
    int x = 960;  //1920
    int y = 540;  //1080
    public int speed = 8;
    double angle; // Angle of rotation in radians
    Image img1;
    Image img;
    private Weapon currentWeapon;
    private int weaponIndex = 0;
    private final Weapon[] weapons;
    Rectangle2D nigga ;


    public Player() {
        try {
            // Load the player's image
            img1 = ImageIO.read(new File("src/burngame/icons/test.png"));
            img = img1.getScaledInstance(100, 33, Image.SCALE_DEFAULT);
        } catch (IOException ex) {
            System.out.println("Image Failed to Load");
        }
        weapons = new Weapon[] {
            new Weapon("Pistol", 10, 5, false, "src/burngame/icons/spark.png"),
            new Weapon("Assault Rifle", 10, 10, true, "src/burngame/icons/spark.png")
        };
        currentWeapon = weapons[weaponIndex];

    }
    public void switchWeapon(int direction) {
        weaponIndex = (weaponIndex + direction + weapons.length) % weapons.length;
        currentWeapon = weapons[weaponIndex];
    }

   public void shoot() {
    if (currentWeapon != null) {
        currentWeapon.shoot(Main.mouseX, Main.mouseY);
    }
}



    public String getWeaponName() {
        return currentWeapon != null ? currentWeapon.getName() : "None";
    }
    public Weapon getCurrentWeapon(){
        return currentWeapon;
    }
   
    public void updateDirection(int mouseX, int mouseY){
      //  angle = Math.atan2(mouseY - y, mouseX-x) +1.57;
        angle = Math.atan2(mouseY - (y + img.getHeight(null) / 2), mouseX - (x + img.getWidth(null) / 2)) +1.57;
        
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

    }
    
   public Rectangle2D getRotatedHitbox() {

        // Create the unrotated rectangle centered at (960, 540)
        Rectangle2D rect = new Rectangle2D.Double(960 - 100 / 2, 540 - 33 / 2, 100, 33);

        // Rotate the rectangle around its center
        AffineTransform transform = AffineTransform.getRotateInstance(angle, 960, 540);
        
        return transform.createTransformedShape(rect).getBounds2D();
    }
    
 
    
}
