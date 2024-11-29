import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class PowerUp {
    private int x, y;
    private Type type;

    public enum Type {
        SPEED_BOOST,
        SHRINK,
        DOUBLE_SIZE,
        PINK  // Add the PINK power-up type
    }

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    public void draw(Graphics g) {
        switch (type) {
            case SPEED_BOOST:
                g.setColor(Color.blue);
                break;
            case SHRINK:
                g.setColor(Color.pink);
                break;
            case DOUBLE_SIZE:
                g.setColor(Color.yellow);  // Yellow for Double Size
                break;
            case PINK:
                g.setColor(Color.cyan);  // Magenta for PINK power-up
                break;
        }
        g.fillRect(x, y, 20, 20);
    }
}
