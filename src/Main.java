import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static GameWindow window;
    public static GameObject cam;
    public static Sprite floor;

    public static TextLabel infoBox;

    public static ArrayList<GameObject> objects = new ArrayList<>();
    public static ArrayList<Sprite> sprites = new ArrayList<>();
    public static ArrayList<GuiObject> guiObjects = new ArrayList<>();

    public static Random random = new Random();

    public static int populationSize = 100;
    public static int elitePercent = 1;
    public static int generation = 1;
    public static double highScore = 0;
    public static boolean showLesserDinos = true;
    public static boolean manualOvveride = false;
    public static DinoAgent currentBest;

    public static ArrayList<Sprite> DinoSprites;
    public static DinoAgent[] population;
    public static boolean generationRunning = false;

    public static void startGeneration(NeuralNetwork base) {
        if (population == null) {
            population = createPopulationFromBrain(base);
        }

        generationRunning = true;
    }

    public static void nextGeneration() {
        generation++;
        population = evolve(population);
        generationRunning = true;
    }

    public static void main(String[] args){
        window = new GameWindow();
        cam = new GameObject();
        infoBox = new TextLabel(" ");
        infoBox.setTextColor(Color.BLACK);

        floor = new Sprite("Square.png", new Vector2(1000,50), 100, new Vector2(0,200));
        floor.setCollision(true);
    }

    public static void Update(double deltaTime){

        if (generationRunning) {

            int alive = 0;

            for (DinoAgent agent : population) {
                agent.update(deltaTime);
                if (agent.alive) alive++;
            }

            if (alive == 0) {
                nextGeneration();
            }
            infoBox.setText("Generation: "+generation+" HighScore: "+Math.round(highScore)+" LiveAgents: "+alive+" FPS: "+window.FPS+" LesserAgentsVisible: "+showLesserDinos);
            updateDinoSprites(population);
        }

        Vector2 direction = new Vector2(0,0);

        if (window.upHeld){
            direction.y -= 5;
        }

        if (window.downHeld){
            direction.y += 5;
        }

        if (window.leftHeld){
            direction.x -= 5;
        }

        if (window.rightHeld){
            direction.x += 5;
        }

        direction.Unit();
        cam.Move(direction);

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

    // Evolution
    public static void updateDinoSprites(DinoAgent[] currentPopulation) {
        DinoSprites = new ArrayList<>();

        double bestFitness = Double.NEGATIVE_INFINITY;

        for (DinoAgent agent : currentPopulation) {
            if (agent.fitness() > bestFitness) {
                bestFitness = agent.fitness();
                currentBest = agent;
            }
        }

        for (DinoAgent agent : currentPopulation) {
            if (!showLesserDinos) {
                agent.setVisible(agent == currentBest);
            } else {
                agent.setVisible(true);
            }
            DinoSprites.add(agent);
        }
    }


    public static DinoAgent[] createPopulationFromBrain(NeuralNetwork sampleBrain){
        DinoAgent[] population = new DinoAgent[populationSize];

        for (int i = 0; i < populationSize; i++){
            DinoAgent agent = new DinoAgent(sampleBrain.clone());
            agent.brain.mutate();
            population[i] = agent;
        }

        updateDinoSprites(population);

        return population;
    }

    public static DinoAgent[] evolve(DinoAgent[] currentPopulation){

        Arrays.sort(currentPopulation, (a, b) -> Double.compare(b.fitness(), a.fitness()));
        DinoAgent[] nextGeneration = new DinoAgent[currentPopulation.length];

        int eliteCount = (elitePercent * currentPopulation.length) /100;

        for (int i = 0; i < eliteCount; i++){
            nextGeneration[i] = new DinoAgent(currentPopulation[i].brain.clone());
        }

        for (int i = eliteCount; i < nextGeneration.length; i++){
            DinoAgent parent = currentPopulation[Main.random.nextInt(Math.min(50, currentPopulation.length))];
            NeuralNetwork childBrain = parent.brain.clone();
            childBrain.mutate();
            nextGeneration[i] = new DinoAgent(childBrain);
        }

        updateDinoSprites(nextGeneration);

        return nextGeneration;
    }

    //Save/Load
    public static void saveBrain(NeuralNetwork brain, String filename) {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(brain);
            System.out.println("Neural network saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NeuralNetwork loadBrain(String filename) {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            NeuralNetwork nn = (NeuralNetwork) in.readObject();
            System.out.println("Neural network loaded from " + filename);
            return nn;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
