import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

class GamePanel extends JPanel implements KeyListener {
    private int x = 350, y = 400;
    private int health = 100;
    private boolean jumping = false, movingLeft = false, movingRight = false, invincible = false;
    private int velocityY = 0;
    private final int groundY = 400;
    private final Timer movementTimer, enemySpawnTimer, invincibilityTimer;
    private final JFrame parentFrame;
    private final Color characterColor;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final Random random = new Random();
    private int lastDirection = 1; // 1: 오른쪽, -1: 왼쪽

    public GamePanel(JFrame parentFrame, Color characterColor) {
        this.parentFrame = parentFrame;
        this.characterColor = characterColor;

        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        addKeyListener(this);

        movementTimer = new Timer(15, e -> {
            if (jumping) {
                y += velocityY;
                velocityY += 2;
                if (y >= groundY) {
                    y = groundY;
                    jumping = false;
                }
            }
            if (movingLeft) x = Math.max(x - 5, 0);
            if (movingRight) x = Math.min(x + 5, getWidth() - 50);

            moveEnemies();
            moveProjectiles();
            checkCollisions();

            repaint();
        });

        enemySpawnTimer = new Timer(1000 + random.nextInt(2000), e -> spawnEnemy());
        invincibilityTimer = new Timer(2000, e -> invincible = false);

        movementTimer.start();
        enemySpawnTimer.start();
    }

    private void spawnEnemy() {
        int side = random.nextInt(2);
        int enemyX = (side == 0) ? 0 : getWidth() - 30;
        enemies.add(new Enemy(enemyX, groundY, 30));
    }

    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.x < x) enemy.x += 2;
            else if (enemy.x > x) enemy.x -= 2;

            if (enemy.health <= 0) iterator.remove();
        }
    }

    private void moveProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();

            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (new Rectangle(projectile.x, projectile.y, projectile.size, projectile.size)
                        .intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    enemy.health--;
                    hit = true;
                    break;
                }
            }

            if (hit || projectile.isOutOfBounds()) iterator.remove();
        }
    }

    private void checkCollisions() {
        if (!invincible) {
            for (Enemy enemy : enemies) {
                if (new Rectangle(x, y, 50, 50).intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    health -= 10;
                    invincible = true;
                    invincibilityTimer.start();

                    if (health <= 0) {
                        JOptionPane.showMessageDialog(this, "게임 오버!");
                        System.exit(0);
                    }
                    break;
                }
            }
        }
    }

    private void performMeleeAttack() {
        int attackX = (lastDirection == 1) ? x + 50 : x - 50;
        int attackWidth = 50;
        Rectangle attackArea = new Rectangle(attackX, y, attackWidth, 50);

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
            if (attackArea.intersects(enemyRect)) {
                enemy.health--;
                if (enemy.health <= 0) iterator.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 캐릭터 체력바
        g.setColor(Color.RED);
        g.fillRect(x, y - 20, 50, 10);
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 20, health / 2, 10);

        // 캐릭터
        g.setColor(characterColor);
        g.fillRect(x, y, 50, 50);

        // 적
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) g.fillRect(enemy.x, enemy.y, enemy.size, enemy.size);

        // 투사체
        g.setColor(Color.ORANGE);
        for (Projectile projectile : projectiles)
            g.fillRect(projectile.x, projectile.y, projectile.size, projectile.size);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            movingLeft = true;
            lastDirection = -1;
        }
        if (key == KeyEvent.VK_D) {
            movingRight = true;
            lastDirection = 1;
        }
        if (key == KeyEvent.VK_W && !jumping) {
            jumping = true;
            velocityY = -15;
        }
        if (key == KeyEvent.VK_J) performMeleeAttack();
        if (key == KeyEvent.VK_K) fireProjectile();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) movingLeft = false;
        if (key == KeyEvent.VK_D) movingRight = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void fireProjectile() {
        int speed = lastDirection * 10;
        projectiles.add(new Projectile(x + 25, y + 25, speed));
    }
}