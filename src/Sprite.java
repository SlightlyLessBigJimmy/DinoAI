import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sprite extends GameObject {

    Vector2 size = new Vector2(1, 1);

    String texture;
    BufferedImage image;

    int zIndex = 0;



    boolean visible = true;
    boolean canCollide = false;
    String collisionMode = "box";

    Sprite(String texturePath, Vector2 newSize, int newZIndex, Vector2 newPos) {
        super();
        texture = texturePath;
        size = newSize;
        zIndex = newZIndex;
        Position = newPos;
        Start();
    }

    private void Start() {
        Main.sprites.add(this);

        if (texture == null) return;

        try {
            image = ImageIO.read(getClass().getResource(texture));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setZIndex(int newZIndex) {
        zIndex = newZIndex;
    }

    public void setVisible(boolean newVisible) {
        visible = newVisible;
    }

    public void setSize(double newX, double newY) {
        size = new Vector2(newX, newY);
    }

    public void setCollision(boolean enabled, String mode) {
        canCollide = enabled;
        collisionMode = mode.toLowerCase();
    }

    public void setCollision(boolean enabled) {
        canCollide = enabled;
    }

    public boolean GetVisible() {
        return visible;
    }

    public boolean getCanCollide() {
        return canCollide;
    }

    public String getCollisionMode() {
        return collisionMode;
    }

    public int GetZIndex() {
        return zIndex;
    }

    public BufferedImage GetImage() {
        return image;
    }

    public Vector2 GetSize() {
        return size;
    }

    private boolean boxCollision(double newX, double newY, Sprite other) {
        double ax1 = newX - size.x/2;
        double ay1 = newY - size.y/2;
        double ax2 = newX + size.x/2;
        double ay2 = newY + size.y/2;

        double bx1 = other.Position.x - other.size.x/2;
        double by1 = other.Position.y - other.size.y/2;
        double bx2 = other.Position.x + other.size.x/2;
        double by2 = other.Position.y + other.size.y/2;

        return ax1 < bx2 && ax2 > bx1 &&
                ay1 < by2 && ay2 > by1;
    }

    private boolean transparencyCollision(double newX, double newY, Sprite other) {
        if (image == null || other.image == null) return false;

        double ax1 = newX - size.x/2;
        double ay1 = newY - size.y/2;
        double ax2 = newX + size.x/2;
        double ay2 = newY + size.y/2;

        double bx1 = other.Position.x - other.size.x/2;
        double by1 = other.Position.y - other.size.y/2;
        double bx2 = other.Position.x + other.size.x/2;
        double by2 = other.Position.y + other.size.y/2;

        int ox1 = (int) Math.max(ax1, bx1);
        int oy1 = (int) Math.max(ay1, by1);
        int ox2 = (int) Math.min(ax2, bx2);
        int oy2 = (int) Math.min(ay2, by2);

        if (ox1 >= ox2 || oy1 >= oy2) return false;

        for (int y = oy1; y < oy2; y++) {
            for (int x = ox1; x < ox2; x++) {

                int ax = (int) ((x - ax1) * image.getWidth() / size.x);
                int ay = (int) ((y - ay1) * image.getHeight() / size.y);

                int bx = (int) ((x - bx1) * other.image.getWidth() / other.size.x);
                int by = (int) ((y - by1) * other.image.getHeight() / other.size.y);

                if (ax < 0 || ay < 0 || bx < 0 || by < 0) continue;
                if (ax >= image.getWidth() || ay >= image.getHeight()) continue;
                if (bx >= other.image.getWidth() || by >= other.image.getHeight()) continue;

                if (((image.getRGB(ax, ay) >>> 24) != 0) &&
                        ((other.image.getRGB(bx, by) >>> 24) != 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean tilemapCollision(double newX, double newY, Tilemap map) {

        double ax1 = newX - size.x / 2;
        double ay1 = newY - size.y / 2;
        double ax2 = newX + size.x / 2;
        double ay2 = newY + size.y / 2;

        double tileSize = map.getTileWorldSize();

        double mapLeft = map.Position.x - map.size.x / 2;
        double mapTop  = map.Position.y - map.size.y / 2;

        int startX = (int) Math.floor((ax1 - mapLeft) / tileSize);
        int endX   = (int) Math.floor((ax2 - mapLeft - 0.0001) / tileSize);
        int startY = (int) Math.floor((ay1 - mapTop) / tileSize);
        int endY   = (int) Math.floor((ay2 - mapTop - 0.0001) / tileSize);

        for (int ty = startY; ty <= endY; ty++) {
            for (int tx = startX; tx <= endX; tx++) {

                Vector2 worldPos = new Vector2(
                        mapLeft + tx * tileSize + tileSize / 2,
                        mapTop  + ty * tileSize + tileSize / 2
                );

                if (map.getTileAtWorldPos(worldPos) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean isCollidingAt(double newX, double newY, Sprite other) {

        if (!other.canCollide) return false;

        if (other instanceof Tilemap) {
            if (!other.collisionMode.equals("tilemap")) return false;
            return tilemapCollision(newX, newY, (Tilemap) other);
        }

        if (collisionMode.equals("box") &&
                other.collisionMode.equals("box")) {
            return boxCollision(newX, newY, other);
        }

        if (collisionMode.equals("transparency") ||
                other.collisionMode.equals("transparency")) {
            return transparencyCollision(newX, newY, other);
        }

        return false;
    }

    public void Move(Vector2 direction) {
        double targetX = Position.x + direction.x;
        double targetY = Position.y + direction.y;

        if (!canCollide) {
            Position.x = targetX;
            Position.y = targetY;
            return;
        }

        // ---- X AXIS ----
        double newX = targetX;
        for (Sprite other : Main.sprites) {
            if (other == this) continue;

            if (isCollidingAt(newX, Position.y, other)) {
                newX = Position.x; // cancel X only
                break;
            }
        }
        Position.x = newX;

        // ---- Y AXIS ----
        double newY = targetY;
        for (Sprite other : Main.sprites) {
            if (other == this) continue;

            if (isCollidingAt(Position.x, newY, other)) {
                newY = Position.y; // cancel Y only
                break;
            }
        }
        Position.y = newY;
    }

}
