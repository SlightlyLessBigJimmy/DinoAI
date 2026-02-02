import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static GameWindow window;
    public static Sprite player;
    public static GameObject cam;
    public static Tilemap landMap;
    public static Tilemap waterMap;

    public static double speed = 5;

    public static int mapSize = 250;
    public static double mapScale = 0.1;
    public static int mapWaterMargin = 5;
    public static float mapTileSize = 80;

    public static ArrayList<GameObject> objects = new ArrayList<GameObject>();
    public static ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    public static int[][] landLevel = new int[mapSize][mapSize];
    public static int[][] waterLevel = new int[mapSize][mapSize];
    public static BufferedImage tileset;
    public static Random RNG = new Random();

    static {
        try {
            tileset = ImageIO.read(
                    Main.class.getResource("/tileset.png")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        window = new GameWindow();
        cam = new GameObject();
        player = new Sprite("/red.png", new Vector2(70,70), 100, new Vector2(0,0));
        player.GoTo(0, 0);

        int seed = RNG.nextInt(-200000000, 200000000);

        PerlinNoise noise = new PerlinNoise(seed);
        System.out.println(seed);
        int center = mapSize / 2;
        double maxDist = Math.sqrt(2 * center * center);

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                if (y >= mapSize - mapWaterMargin || y <= mapWaterMargin || x >= mapSize - mapWaterMargin || x <= mapWaterMargin){
                    waterLevel[y][x] = 0; // water
                    landLevel[y][x] = -1;
                    continue;
                }

                double n = noise.noise(x * mapScale, y * mapScale);

                double dx = x - center;
                double dy = y - center;

                double dist = Math.sqrt(dx * dx + dy * dy) / maxDist;

                double falloff = dist * 3;

                double islandValue = n - falloff;

                if (islandValue > -0.5) {
                    landLevel[y][x] = 3; // stone
                    waterLevel[y][x] = -1;
                }else if (islandValue > -1.3) {
                    landLevel[y][x] = 1; // grass
                    waterLevel[y][x] = -1;
                } else if (islandValue > -1.5) {
                    landLevel[y][x] = 2; // sand
                    waterLevel[y][x] = -1;
                } else {
                    waterLevel[y][x] = 0; // water
                    landLevel[y][x] = -1;
                }

            }
        }



        landMap = new Tilemap(
                landLevel,
                tileset,
                32,
                mapTileSize,
                new Vector2(0, 0),
                -100

        );

        waterMap = new Tilemap(
                waterLevel,
                tileset,
                32,
                mapTileSize,
                new Vector2(0, 0),
                -100
        );
        waterMap.setCollision(true, "Tilemap");
        player.setCollision(true, "box");
        Sprite smile = new Sprite("smile.png", new Vector2(300,300), 50, new Vector2(500,500));
        smile.setCollision(true, "transparency");
    }

    public static void Update(double deltaTime){
        if (player == null){
            return;
        }
        //System.out.println(player.GetPosition().x + "," + player.GetPosition().y);
        Vector2 direction = new Vector2(0,0);
;
        if (window.upHeld){
            direction.y -= speed;
        }

        if (window.downHeld){
            direction.y += speed;
        }

        if (window.leftHeld){
            direction.x -= speed;
        }

        if (window.rightHeld){
            direction.x += speed;
        }

        if (direction.getMagnitude() > 0){
            player.Move(direction);
        }

        cam.Position.Lerp(cam.Position, player.GetPosition(), 0.1);
    }
}