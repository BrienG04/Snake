import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

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
    private Timer powerUpTimer;
    private boolean powerUpSpawned = false;

    private ArrayList<PowerUp> powerUps;
    private boolean speedBoostActive = false;
    private long speedBoostTimer = 0;
    private boolean doubleSizeActive = false; // For double size power-up
    private long doubleSizeTimer = 0; // Timer for double size duration
    private boolean pinkPowerUpActive = false; // For PINK power-up
    private long pinkPowerUpTimer = 0; // Timer for PINK power-up duration

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
        powerUpTimer = new Timer(9000, e -> spawnPowerUp());  // Spawn power-ups every 9 seconds
        powerUpTimer.start();
        spawnFood();

        running = true;
    }

    public void spawnFood() {
        foodX = (int) (Math.random() * RAND_POS) * DOT_SIZE;
        foodY = (int) (Math.random() * RAND_POS) * DOT_SIZE;
    }

    public void spawnPowerUp() {
        if (!powerUpSpawned) {
            int powerUpX = (int) (Math.random() * RAND_POS) * DOT_SIZE;
            int powerUpY = (int) (Math.random() * RAND_POS) * DOT_SIZE;

            // Make sure the power-up doesn't spawn on the snake's head or body
            if (powerUpX != x[0] || powerUpY != y[0]) {
                PowerUp newPowerUp = new PowerUp(powerUpX, powerUpY);
                powerUps.add(newPowerUp);  // Add power-up to the list
                powerUpSpawned = true;
                System.out.println("Spawned PowerUp at: (" + powerUpX + ", " + powerUpY + ")");
            }
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
        for (int i = snakeLength; i > 0; i--) {
            if (i > 3 && x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] >= WIDTH || x[0] < 0 || y[0] >= HEIGHT || y[0] < 0) {
            running = false;
        }
    }

    public void checkPowerUps() {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);

            if (x[0] == powerUp.getX() && y[0] == powerUp.getY()) {
                applyPowerUp(powerUp);
                powerUps.remove(i);
                powerUpSpawned = false;
                System.out.println("PowerUp collected!");
                break;
            }
        }
    }

    public void applyPowerUp(PowerUp powerUp) {
        if (powerUp.getType() == PowerUp.Type.SPEED_BOOST) {
            speedBoostActive = true;
            speedBoostTimer = System.currentTimeMillis();
        } else if (powerUp.getType() == PowerUp.Type.SHRINK) {
            if (snakeLength > 3) snakeLength -= 2;
        } else if (powerUp.getType() == PowerUp.Type.DOUBLE_SIZE) {
            doubleSizeActive = true; // Activate double size effect
            doubleSizeTimer = System.currentTimeMillis(); // Start the timer
            snakeLength *= 2;  // Double the snake's length
            System.out.println("Double Size Power-Up Activated!");
        } else if (powerUp.getType() == PowerUp.Type.PINK) {
            pinkPowerUpActive = true;  // Activate the pink power-up
            pinkPowerUpTimer = System.currentTimeMillis();  // Start the timer
            System.out.println("Pink Power-Up Activated! Pellet count *3");
        }
    }

    public void updateGameState() {
        if (speedBoostActive && System.currentTimeMillis() - speedBoostTimer > 5000) {
            speedBoostActive = false;
        }

        // Check if double size power-up has expired (after 10 seconds)
        if (doubleSizeActive && System.currentTimeMillis() - doubleSizeTimer > 10000) {
            doubleSizeActive = false;  // Reset the flag
            snakeLength /= 2;  // Revert the snake length to normal
            System.out.println("Double Size Power-Up Deactivated!");
        }

        // Check if pink power-up has expired (after 15 seconds)
        if (pinkPowerUpActive && System.currentTimeMillis() - pinkPowerUpTimer > 15000) {
            pinkPowerUpActive = false;  // Reset the flag
            System.out.println("Pink Power-Up Deactivated!");
        }

        if (speedBoostActive) {
            timer.setDelay(50); // Speed up the game when Speed Boost is active
        } else {
            timer.setDelay(DELAY); // Normal game speed
        }

        checkFoodCollision();
    }

    public void checkFoodCollision() {
        if (x[0] == foodX && y[0] == foodY) {
            if (pinkPowerUpActive) {
                snakeLength += 3;  // Triple the length when Pink Power-Up is active
                System.out.println("Food collected! Current length: " + snakeLength);
            } else {
                snakeLength++;  // Normal food collection
            }
            spawnFood();
        }
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.green);
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.white);
                } else {
                    g.setColor(Color.green);
                }
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
            }

            g.setColor(Color.red);
            g.fillRect(foodX, foodY, DOT_SIZE, DOT_SIZE);

            for (PowerUp powerUp : powerUps) {
                powerUp.draw(g);
            }

            g.setColor(Color.white);
            g.drawString("Length: " + snakeLength, 10, 10);
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        String message = "Game Over!";
        Font small = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (WIDTH - metr.stringWidth(message)) / 2, HEIGHT / 2);
    }
}
