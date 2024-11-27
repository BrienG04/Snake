import java.awt.*;

public class PowerUp {
    private int x, y;
    private Type type;

    public enum Type {
        SPEED_BOOST, SHRINK, MAGNET
    }

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = Type.values()[(int)(Math.random() * Type.values().length)];
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
            case MAGNET:
                g.setColor(Color.orange);
                break;
        }
        g.fillRect(x, y, 20, 20);
    }
}