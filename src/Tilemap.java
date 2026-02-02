import java.awt.*;
import java.awt.image.BufferedImage;

public class Tilemap extends Sprite {

    private int[][] tiles;

    private BufferedImage tileset;
    private int tilePixelSize;
    private int tilesetColumns;

    private double tileWorldSize;

    public Tilemap(int[][] tileData, BufferedImage tilesetImage, int tilePixelSize, double tileWorldSize, Vector2 worldPos, int zIndex){
        super(null, new Vector2(1, 1), zIndex, worldPos);

        this.tiles = tileData;
        this.tileset = tilesetImage;
        this.tilePixelSize = tilePixelSize;
        this.tileWorldSize = tileWorldSize;

        this.tilesetColumns = tileset.getWidth() / tilePixelSize;

        buildTilemapImage();
    }

    private void buildTilemapImage() {
        int rows = tiles.length;
        int cols = tiles[0].length;

        double mapWorldWidth = cols * tileWorldSize;
        double mapWorldHeight = rows * tileWorldSize;

        int mapPixelWidth = (int) Math.round(cols * tilePixelSize);
        int mapPixelHeight = (int) Math.round(rows * tilePixelSize);

        BufferedImage mapImage = new BufferedImage(
                mapPixelWidth,
                mapPixelHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = mapImage.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < cols; x++)
            {

                int tileId = tiles[y][x];
                if (tileId < 0) {
                    continue;
                }

                int srcX = (tileId % tilesetColumns) * tilePixelSize;
                int srcY = (tileId / tilesetColumns) * tilePixelSize;

                int dstX = x * tilePixelSize;
                int dstY = y * tilePixelSize;

                g.drawImage(
                        tileset,
                        dstX,
                        dstY,
                        dstX + tilePixelSize,
                        dstY + tilePixelSize,
                        srcX,
                        srcY,
                        srcX + tilePixelSize,
                        srcY + tilePixelSize,
                        null
                );
            }
        }

        g.dispose();

        this.image = mapImage;
        this.size = new Vector2(mapWorldWidth, mapWorldHeight);
    }

    public int getTilePixelSize(){
        return tilePixelSize;
    }

    public double getTileWorldSize(){
        return tileWorldSize;
    }

    public Vector2 worldToTile(Vector2 worldPos){

        double mapLeft = Position.x - size.x / 2;
        double mapTop  = Position.y - size.y / 2;

        return new Vector2(
                Math.floor((worldPos.x - mapLeft) / tileWorldSize),
                Math.floor((worldPos.y - mapTop) / tileWorldSize)
        );
    }


    public int getTileAtWorldPos(Vector2 worldPos){
        Vector2 t = worldToTile(worldPos);
        int x = (int) t.x;
        int y = (int) t.y;

        if (y < 0 || y >= tiles.length || x < 0 || x >= tiles[0].length) {
            return -1;
        }
        return tiles[y][x];
    }
}
