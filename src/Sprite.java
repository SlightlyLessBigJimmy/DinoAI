import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sprite extends GameObject{

    Vector2 size = new Vector2(1,1);

    String texture;

    BufferedImage image;

    int zIndex = 0;

    boolean visible = true;

    Sprite(String texturePath, Vector2 newSize, int newZIndex, Vector2 newPos){
        super();
        texture = texturePath;
        size = newSize;
        zIndex = newZIndex;
        Position = newPos;
        Start();
    }

    private void Start(){
        Main.sprites.add(this);

        if (texture == null) {
            return; // Tilemap or procedural sprite
        }

        try {
            image = ImageIO.read(
                    getClass().getResource(texture)
            );
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void setZIndex(int newZIndex){
        zIndex = newZIndex;
    }

    public void setVisible(boolean newVisible){
        visible = newVisible;
    }

    public void setSize(double newX, double newY){
        size = new Vector2(newX, newY);
    }

    public boolean GetVisible(){
        return visible;
    }

    public boolean IsOnCamera(){
        //Vector2 camPos = Main.cam.GetPosition();
        //Vector2 pos = this.GetPosition();
        //Vector2 size = this.GetSize();
        return true;
    }

    public int GetZIndex(){
        return zIndex;
    }

    public BufferedImage GetImage(){
        return image;
    }

    public Vector2 GetSize() {
        return size;
    }
}
