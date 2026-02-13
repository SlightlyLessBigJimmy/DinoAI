import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GamePanel extends JPanel {

    public GamePanel() {
        setDoubleBuffered(true);
    }

    double gravityScale = 1;

    public void setGravity(double newGravity){
        gravityScale = newGravity;
    }

    private List<Sprite> getRenderSnapshot() {
        synchronized (Main.sprites) {
            return new ArrayList<>(Main.sprites);
        }
    }

    private void calculatePhysics(){
        for (GameObject object : Main.objects) {
            if (object.usesPhysics()){
                object.Move(object.Velocity);

                if (Math.abs(gravityScale) > 0){
                    object.addForce(new Vector2(0, gravityScale));
                }

            }
        }
    }

    private void drawSprites(Graphics g) {
        Vector2 camPos = Main.cam.getPosition();

        List<Sprite> sprites = getRenderSnapshot();

        sprites.sort(Comparator.comparingInt(Sprite::GetZIndex));

        for (Sprite sprite : sprites) {

            if (sprite == null) continue;
            if (!sprite.GetVisible()) continue;

            BufferedImage img = sprite.GetImage();
            if (img == null) continue;

            Vector2 worldPos = sprite.getPosition();
            Vector2 size = sprite.GetSize();

            double screenX = worldPos.x - camPos.x + getWidth() / 2.0;
            double screenY = worldPos.y - camPos.y + getHeight() / 2.0;

            g.drawImage(
                    img,
                    (int)(screenX - size.x / 2),
                    (int)(screenY - size.y / 2),
                    (int)size.x,
                    (int)size.y,
                    this
            );
        }
    }

    private void drawGUI(Graphics g) {
        for (GuiObject object : Main.guiObjects) {

            if (object == null) continue;
            if (!object.getVisible()) continue;

            BufferedImage img = object.getImage();
            if (img == null) continue;

            Vector2 pos = object.getPosition(); // screen-space
            Vector2 size = object.getSize();

            g.drawImage(
                    img,
                    (int) pos.x,
                    (int) pos.y,
                    (int) size.x,
                    (int) size.y,
                    this
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.fillRect(0,0, this.getWidth(), this.getHeight());
        drawSprites(g);
        drawGUI(g);
        calculatePhysics();
    }
}
