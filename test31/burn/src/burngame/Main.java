
package burngame;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

/**
 *
 * @author Only Adam Shafronsky
 */
public class Main extends javax.swing.JFrame {
    
    //VARIABLE CREATION
    static Player player = new Player();
    static ArrayList<Spark> sparks = new ArrayList<>();
    Set<Integer> pressedKeys = new HashSet<>(); // Track currently pressed keys to make movement smoother than just a key listener
    Timer gameLoop;
    int counter = 0;
    private boolean isShooting = false;
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static int worldX;
    public static int worldY;
    private Image crosshair;
    private Image testBackground;
    private Image pistolImg;
    private Image arImg;
    private Image knifeImg;
    private Image weaponImg;
    static ArrayList<Wall> walls = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    Image scaled;
    Image scaledPistol;
    Image scaledAR;
    Image scaledKnife;
    
    //DEVELOPER VARIABLES
    private boolean showWalls = false; // Toggle visibility of walls for testing
    private boolean editMode = false; //Toggle edit mode for world building
    
    
    
    public Main() {
        initComponents();
        initGameLoop();
        initWalls();
        initEnemies();
        try {
            crosshair = ImageIO.read(new File("src/burngame/icons/crosshair.png"));
            testBackground = ImageIO.read(new File("src/burngame/backgrounds/1ap.png"));
            pistolImg = ImageIO.read(new File("src/burngame/icons/pistol.png"));
            arImg = ImageIO.read(new File("src/burngame/icons/rifle.png"));
            knifeImg = ImageIO.read(new File("src/burngame/icons/knife.png"));
            scaled = testBackground.getScaledInstance(2250, 2250, Image.SCALE_DEFAULT);
            scaledAR = arImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            scaledPistol = pistolImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            scaledKnife = knifeImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            weaponImg = scaledKnife;
        } catch (IOException ex) {
            System.out.println("Crosshair Image Failed");
        }

        // Set the custom cursor to hide the default cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor blankCursor = toolkit.createCustomCursor(toolkit.getImage(""), new java.awt.Point(0, 0), "blank cursor");
        this.setCursor(blankCursor); 
        if (editMode) this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); //this is for easier wall building, do not use for game
        worldX += 350;
        worldY +=1600;
    }
        @SuppressWarnings("unchecked")
        
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panDraw = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                draw(g);
            }

        };
        ammocount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panDraw.setBackground(new java.awt.Color(0, 0, 0));
        panDraw.setMaximumSize(new java.awt.Dimension(1920, 1080));
        panDraw.setMinimumSize(new java.awt.Dimension(1920, 1080));
        panDraw.setPreferredSize(new java.awt.Dimension(1920, 1080));
        panDraw.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panDrawMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                panDrawMouseMoved(evt);
            }
        });
        panDraw.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                panDrawMouseWheelMoved(evt);
            }
        });
        panDraw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panDrawMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panDrawMouseReleased(evt);
            }
        });
        panDraw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panDrawKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                panDrawKeyReleased(evt);
            }
        });
        panDraw.setFocusable(true);
        panDraw.requestFocusInWindow();

        ammocount.setFont(new java.awt.Font("Liberation Serif", 3, 48)); // NOI18N
        ammocount.setForeground(new java.awt.Color(255, 0, 0));
        ammocount.setText("jLabel1");

        javax.swing.GroupLayout panDrawLayout = new javax.swing.GroupLayout(panDraw);
        panDraw.setLayout(panDrawLayout);
        panDrawLayout.setHorizontalGroup(
            panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                .addContainerGap(1678, Short.MAX_VALUE)
                .addComponent(ammocount, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
        );
        panDrawLayout.setVerticalGroup(
            panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDrawLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ammocount)
                .addContainerGap(994, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panDraw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(panDraw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
     private void initGameLoop() {
        // Game loop updates player movement every 16 ms (~60 FPS)
        gameLoop = new Timer(16, e -> {
            counter++;
            if(counter == 7){
                counter = 0;
            }
            player.updatePlayerPosition(worldX, worldY, pressedKeys, counter);    
            int count = player.getAmmo();
            switch (player.getWeaponName()) {
                case "Assault Rifle" -> ammocount.setText(String.valueOf(count));
                case "Pistol" -> ammocount.setText("∞");
                default -> ammocount.setText("");
            }
            panDraw.repaint();
            if (isShooting && player.getCurrentWeapon().isAutomatic()) {
                player.shoot();
            }
        });
        gameLoop.start();
    }
     
     
     
     //LIST OF WALLS, ADD ANY WALLS OVER HERE (x,y,width,height,is it hard wall?)Please use the wall maker to auto generate the wall
     private void initWalls(){
        addWall(1126,275,808,265,true);
        addWall(1048,2235,503,15,true);
        addWall(1552,1827,698,422,true);
        addWall(-1,1805,1048,445,true);
        addWall(-1,1538,14,267,true);
        addWall(-1,1265,1048,275,true);
        addWall(0,1,14,1262,true);
        addWall(14,1,2236,14,true);
        addWall(2237,15,13,1426,true);
        addWall(1895,1441,356,119,true);
        addWall(2245,1560,4,268,true);
        addWall(2025,941,211,221,true);
        addWall(2052,631,182,191,true);
        addWall(489,873,15,391,true);
        addWall(489,1,15,740,true);
        addWall(85,91,305,191,true);
        addWall(-2,1238,15,32,true);
        addWall(1611,153,260,120,false);
        addWall(544,1144,261,121,false);
        addWall(2029,1562,223,266,false);    
        addWall(503,1145,39,118,false);
     }
     private void initEnemies(){
         addEnemy(100,400,"Pistol");
         addEnemy(112,1675,"Pistol");
         addEnemy(1954,1697,"Pistol");
         addEnemy(2164,1309,"Pistol");
         addEnemy(2174,68,"Knife");
         addEnemy(2172,568,"Knife");
         addEnemy(1541,205,"Knife");
         addEnemy(564,64,"Knife");
         addEnemy(1082,407,"Knife");
         addEnemy(665,1115,"Knife");
         addEnemy(444,1190,"Knife");
         addEnemy(45,1208,"Knife");
     }
     private void addWall(int x, int y, int width, int height, boolean bool){
         walls.add(new Wall(x,y,width,height,bool));
     }
     private void addEnemy(int x, int y, String weapon){
         enemies.add(new Enemy(x,y,weapon,1));
     }
    
   private void printMethod(int x1, int x2, int y1, int y2) {
    // Calculate the differences between the coordinates
    int xdiff = Math.abs(x1 - x2);
    int ydiff = Math.abs(y1 - y2);

    // Ensure x1, y1 is the leftmost/topmost point
    if (x2 < x1) {
        x1 = x2;
    }

    if (y2 < y1) {
        y1 = y2;
    }
        System.out.println("addWall(" + x1 + "," + y1 + "," + xdiff + ","+ydiff+",true);");
}
        public static Rectangle getPlayerBounds(){
           Rectangle2D rect = player.getRotatedHitbox();
           return rect.getBounds();
       }

       public static Player getPlayer(){
           return player;
       }
   
       public static void playSound(String sound) throws UnsupportedAudioFileException{
        try {
            File f = new File("src/burngame/sounds/"+sound+".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (LineUnavailableException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
    //EVENT METHODS
    private int k = 0;
    private int xs[] = new int[2];
    private int ys[] = new int[2];
    private void panDrawKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panDrawKeyPressed
        // System.out.println(evt.getKeyCode());  
           pressedKeys.add(evt.getKeyCode());
           if (editMode){
           if (evt.getKeyCode() == 75){
               xs[k]=mouseX + worldX;
               ys[k]=mouseY + worldY;
               k++;
               if (k>1){
                   printMethod(xs[0],xs[1],ys[0],ys[1]);
                   k=0;   
               }
           }
        }      
    }//GEN-LAST:event_panDrawKeyPressed

    private void panDrawKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panDrawKeyReleased
         pressedKeys.remove(evt.getKeyCode());
    }//GEN-LAST:event_panDrawKeyReleased

    private void panDrawMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseMoved
       mouseX = evt.getX();
       mouseY = evt.getY();
        player.updateDirection(mouseX,mouseY);
        
    }//GEN-LAST:event_panDrawMouseMoved

    private void panDrawMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseDragged
        panDrawMouseMoved(evt);
    }//GEN-LAST:event_panDrawMouseDragged

    private void panDrawMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_panDrawMouseWheelMoved
        int direction = evt.getWheelRotation() > 0 ? 1 : -1;
        player.switchWeapon(direction);
        if (player.getWeaponName().equals("Pistol")) weaponImg = scaledPistol;
        else if (player.getWeaponName().equals("Assault Rifle")) weaponImg = scaledAR;
        else if (player.getWeaponName().equals("Knife")) weaponImg = scaledKnife;
    }//GEN-LAST:event_panDrawMouseWheelMoved

    private void panDrawMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMousePressed
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1){
            if (player.getWeaponName().equals("Assault Rifle")) {
                // Continuous shooting for Assault Rifle
                isShooting = true;
            }else{
                // Single shot for Pistol or Knife on mouse press
                player.shoot(); // Call shoot once for Pistol
            } 
        }
    }//GEN-LAST:event_panDrawMousePressed

    private void panDrawMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseReleased
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            isShooting = false;
        }
    }//GEN-LAST:event_panDrawMouseReleased
    
    
    
    
    //Please add any methods above this, below is just main and draw methods
    
    private void draw(Graphics g) {
    // Draw player in the center of the screen
     g.drawImage(scaled, 0-worldX, 0-worldY, null);
        for (Enemy enemy : enemies){
        enemy.draw(g);
    }
    player.x = 1920 / 2;
    player.y = 1080 / 2;
    player.draw(g);
    if (!editMode){
        g.drawImage(crosshair, mouseX - crosshair.getWidth(null) / 2, mouseY - crosshair.getHeight(null) / 2, null);
    }
   

    // Draw the walls (considering world movement)
    if (showWalls) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
    }
    
 
    
    //Draw the bullet hitting animations
    sparks.removeIf(Spark::isExpired);
    for (Spark spark : sparks) {
        spark.draw(g);
    }
    
    g.drawImage(weaponImg, 1600, 10, null);
}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
        
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ammocount;
    private static javax.swing.JPanel panDraw;
    // End of variables declaration//GEN-END:variables
}
