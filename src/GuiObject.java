import javax.swing.plaf.PanelUI;
import java.awt.image.BufferedImage;
import java.security.PublicKey;

public class GuiObject extends GameObject {
    BufferedImage drawImage;
    Vector2 size;
    boolean visible = true;

    public GuiObject(){
        super();
        size = new Vector2(10,10);
        Main.guiObjects.add(this);
    }

    public boolean getVisible(){
        return visible;
    }

    public BufferedImage getImage(){
        return drawImage;
    }

    public Vector2 getSize(){
        return size;
    }

    public void setSize(Vector2 newSize){
        size = newSize;
    }

    public void setVisible(boolean newVisible){
        visible = newVisible;
    }

}
