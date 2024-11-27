import javax.swing.Timer;  // Add this line
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    private final int WIDTH = 600, HEIGHT = 600;
    private final int DOT_SIZE = 20, ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 100;

    private int snakeLength;
    private int[] x, y;
    private int foodX, foodY;
    private boolean running = false;
    private char direction = 'R';
    private Timer timer;
    
    private ArrayList<PowerUp> powerUps;
    private boolean speedBoostActive = false;
    private long speedBoostTimer = 0;
    private boolean magnetActive = false;
    private long magnetTimer = 0;
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);

        x = new int[ALL_DOTS];
        y = new int[ALL_DOTS];
        powerUps = new ArrayList<>();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                changeDirection(e);
            }
        });
        startGame();
    }

    public void startGame() {
        snakeLength = 3;
        x[0] = 100;
        y[0] = 100;

        timer = new Timer(DELAY, this);
        timer.start();
        spawnFood();
        spawnPowerUp();
        running = true;
    }

    public void spawnFood() {
        foodX = (int) (Math.random() * RAND_POS) * DOT_SIZE;
        foodY = (int) (Math.random() * RAND_POS) * DOT_SIZE;
    }

    public void spawnPowerUp() {
        if (Math.random() < 0.5) {
            int powerUpX = (int) (Math.random() * RAND_POS) * DOT_SIZE;
            int powerUpY = (int) (Math.random() * RAND_POS) * DOT_SIZE;
            PowerUp newPowerUp = new PowerUp(powerUpX, powerUpY);
            powerUps.add(newPowerUp);
        }
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= DOT_SIZE; break;
            case 'D': y[0] += DOT_SIZE; break;
            case 'L': x[0] -= DOT_SIZE; break;
            case 'R': x[0] += DOT_SIZE; break;
        }
    }

    public void checkCollisions() {
        // Check if snake runs into itself
        for (int i = snakeLength; i > 0; i--) {
            if (i > 3 && x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Check if snake hits the wall
        if (x[0] >= WIDTH || x[0] < 0 || y[0] >= HEIGHT || y[0] < 0) {
            running = false;
        }
    }

    public void checkPowerUps() {
        for (Iterator<PowerUp> it = powerUps.iterator(); it.hasNext(); ) {
            PowerUp powerUp = it.next();
            if (x[0] == powerUp.getX() && y[0] == powerUp.getY()) {
                // Apply power-up effects
                applyPowerUp(powerUp);
                it.remove();
                spawnPowerUp(); // respawn power-up
            }
        }
    }

    public void applyPowerUp(PowerUp powerUp) {
        // Example Power-Up Effects
        if (powerUp.getType() == PowerUp.Type.SPEED_BOOST) {
            speedBoostActive = true;
            speedBoostTimer = System.currentTimeMillis();
        } else if (powerUp.getType() == PowerUp.Type.SHRINK) {
            if (snakeLength > 3) snakeLength--;  // Shrink snake length
        } else if (powerUp.getType() == PowerUp.Type.MAGNET) {
            magnetActive = true;
            magnetTimer = System.currentTimeMillis();
        }
    }

    public void checkFoodCollision() {
        // Check if snake's head collides with food
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++; // Increase snake length
            spawnFood();   // Spawn new food
        }
    }



    public void updateGameState() {
        if (speedBoostActive && System.currentTimeMillis() - speedBoostTimer > 5000) {
            speedBoostActive = false; // Deactivate after 5 seconds
        }
        if (magnetActive && System.currentTimeMillis() - magnetTimer > 5000) {
            magnetActive = false; // Deactivate after 5 seconds
        }

        if (speedBoostActive) {
            timer.setDelay(50); // Increase speed when boost is active
        } else {
            timer.setDelay(DELAY);
        }
        checkFoodCollision();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
            checkPowerUps();
            updateGameState();
            repaint();
        }
    }

    public void changeDirection(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && direction != 'R') {
            direction = 'L';
        } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
            direction = 'R';
        } else if (key == KeyEvent.VK_UP && direction != 'D') {
            direction = 'U';
        } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
            direction = 'D';
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSnake(g);
        drawFood(g);
        drawPowerUps(g);
    }

    public void drawSnake(Graphics g) {
        for (int i = 0; i < snakeLength; i++) {
            if (i == 0) {
                g.setColor(Color.green); // Snake head
            } else {
                g.setColor(Color.white); // Snake body
            }
            g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
        }
    }

    public void drawFood(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(foodX, foodY, DOT_SIZE, DOT_SIZE);
    }

    public void drawPowerUps(Graphics g) {
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
    }
}
