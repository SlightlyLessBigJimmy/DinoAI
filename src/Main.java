import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static GameWindow window;
    public static GameObject cam;
    public static Sprite floor;

    public static ArrayList<GameObject> objects = new ArrayList<>();
    public static ArrayList<Sprite> sprites = new ArrayList<>();
    public static ArrayList<GuiObject> guiObjects = new ArrayList<>();

    public static Random random = new Random();


    public static DinoAgent dinoAgent;
    //public static  NeuralNetwork brain = new NeuralNetwork(8,16,4);

    public static void main(String[] args){
        window = new GameWindow();
        cam = new GameObject();

        floor = new Sprite("Square.png",
                new Vector2(1000,50),
                100,
                new Vector2(0,window.getY()-10));
        floor.setCollision(true);

        dinoAgent = new DinoAgent();
    }


    public static void Update(double deltaTime){
        dinoAgent.update();
        CleanupDestroyed();
    }

    public static void CleanupDestroyed(){

        ArrayList<GameObject> toRemove = new ArrayList<>();

        for (GameObject obj : objects){
            if (obj.isDestroyed()){
                obj.OnDestroy();
                toRemove.add(obj);
            }
        }

        objects.removeAll(toRemove);
    }
}
