import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Image extends GuiObject {
    String texture;

    public Image(String texturePath){
        super();
        texture = texturePath;

        try {
            this.drawImage = ImageIO.read(getClass().getResource(texture));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
