import java.awt.*;
import java.awt.image.BufferedImage;

public class TextLabel extends GuiObject {

    String text = "";
    int fontSize = 16;
    boolean autoScale = false;
    Color textColor = Color.WHITE;
    String fontName = "Arial";
    int fontStyle = Font.PLAIN;

    public TextLabel(String newText) {
        super();
        text = newText;
        updateTexture();
    }

    public void setText(String newText) {
        text = newText;
        updateTexture();
    }

    public void setFontSize(int size) {
        fontSize = size;
        updateTexture();
    }

    public void setAutoScale(boolean value) {
        autoScale = value;
        updateTexture();
    }

    private void updateTexture() {
        Font font = new Font(fontName, fontStyle, fontSize);

        // --- Measure text ---
        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = temp.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.dispose();

        if (textWidth <= 0 || textHeight <= 0) {
            this.drawImage = null;
            return;
        }

        // --- Render text at native resolution ---
        BufferedImage textImage = new BufferedImage(
                textWidth,
                textHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        g2d = textImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(textColor);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        // --- Auto-scale if needed ---
        if (autoScale) {
            Vector2 size = getSize(); // GUI object size

            BufferedImage scaled = new BufferedImage(
                    (int) size.x,
                    (int) size.y,
                    BufferedImage.TYPE_INT_ARGB
            );

            g2d = scaled.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.drawImage(
                    textImage,
                    0, 0,
                    (int) size.x,
                    (int) size.y,
                    null
            );
            g2d.dispose();

            this.drawImage = scaled;
        } else {
            // Natural size = text size
            this.drawImage = textImage;
            setSize(new Vector2(textWidth, textHeight));
        }
    }
}