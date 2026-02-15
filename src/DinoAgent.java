import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class DinoAgent extends Sprite {

    public NeuralNetwork brain;

    public double score = 0;
    public double speed = -5;
    public boolean alive = true;

    private boolean isDucking = false;

    private int minSpawnTime = 30;
    private int maxSpawnTime = 120;

    private int spawnTimer = 0;
    private int nextSpawnTime = 60; // initial delay
    ArrayList<Sprite> cacti = new ArrayList<Sprite>();

    public DinoAgent(NeuralNetwork initBrain){
        brain = initBrain;
        super("Dino.png", new Vector2(50,50), 1, new Vector2(0,0));
        setPhysics(true);
        setIgnoreListMode("whitelist");
        addToIgnoreList(Main.floor);
        setCollision(true, "box");
        brain.generationNumber = Main.generation;
    }

    //Game Mechanics
    private void jump(){
        if (isGrounded() == 1) {
            addForce(new Vector2(0, -17));
        }
    }

    private void handleSpawning() {

        spawnTimer++;

        if (spawnTimer >= nextSpawnTime) {

            spawnCactus();

            spawnTimer = 0;

            // Random interval between 60–120 frames
            nextSpawnTime = Main.random.nextInt(minSpawnTime, maxSpawnTime);
        }
    }

    private void handleMoving(){
        for (Sprite cactus : cacti){
            if (cactus.isDestroyed()) continue;

            cactus.Move(new Vector2(speed,0));

            if (cactus.getPosition().x < -410){
                cactus.Destroy();
            }
        }
        cacti.removeIf(Sprite::isDestroyed);
    }

    public int isGrounded(){
        ArrayList<Sprite> ignoreList = new ArrayList<>();
        ignoreList.add(this);
        ignoreList.add(Main.floor);

        Raycast groundCheck = new Raycast(
                getPosition(),
                new Vector2(0,1),
                28,
                ignoreList,
                "whitelist"
        );

        if (groundCheck.Hit){
            return 1;
        }
        else {
            return 0;
        }

    }

    //Getting Input Values
    private ArrayList<Sprite> getCactiInFront() {

        ArrayList<Sprite> front = new ArrayList<>();

        for (Sprite cactus : cacti) {
            if (!cactus.isDestroyed() &&
                    cactus.getPosition().x > getPosition().x) {

                front.add(cactus);
            }
        }

        front.sort((a, b) -> Double.compare(a.getPosition().x, b.getPosition().x));

        return front;
    }

    public Sprite spawnCactus(){
        String spritePath = "Cactus.png";
        Vector2 cactusSize = new Vector2(30,60);
        double yPos = 150;
        if (Main.random.nextInt(0,5) == 1){
            spritePath = "bird.png";
            yPos = Main.random.nextDouble(70,130);
            cactusSize = new Vector2(60,40);
        }

        Sprite cactus = new Sprite(spritePath, cactusSize, 100, new Vector2(410,yPos));
        cactus.setIgnoreListMode("whitelist");
        cacti.add(cactus);

        cactus.setCollision(true, "box");

        if (!Main.showLesserDinos && Main.currentBest != this){
            cactus.setVisible(false);
        }

        return cactus;
    }

    public double[] getInputs(){

        double distance1 = 1.0; // default normalized
        double distance2 = 1.0;
        double height1 = 0;
        double height2 = 0;
        double between = 1.0;

        ArrayList<Sprite> front = getCactiInFront();

        if (front.size() > 0){

            distance1 = (front.get(0).getPosition().x - getPosition().x) / 410.0;
            height1 = (130 - front.get(0).getPosition().y) / (130 - 70);
            if (front.size() > 1){

                distance2 = (front.get(1).getPosition().x - getPosition().x) / 410.0;
                height2 = (130 - front.get(1).getPosition().y) / (130 - 70);

                between = (front.get(1).getPosition().x -
                        front.get(0).getPosition().x) / 410.0;
            }
        }

        return new double[]{
                (getPosition().y - 6.0) / (142.0 - 6.0),//Agent's vertical Position (normalized)
                (getVelocity().y + 15.0) / 32.0,//Agent's vertical Velocity (normalized)
                distance1,//distance from closest cacti (normalized)
                distance2,//distance to 2nd cacti (normalized)
                isGrounded(),//is touching floor?
                (speed + 15.0) / 10.0,//movement speed (normalized)
                between,//distance between cacti (normalized)
                height1,//closest cactus y position (normalized)
                height2,//2nd cactus y position(normalized)
                1.0 // bias
        };
    }

    public void update(double deltaTime){
        for (Sprite cactus : cacti){
            if (isCollidingAt(getPosition().x, getPosition().y, cactus)){
                setVisible(false);
                for (Sprite cactus1 : cacti){
                    if (isCollidingAt(getPosition().x, getPosition().y, cactus)){
                        cactus1.Destroy();
                    }
                }
                Destroy();
                alive = false;
            }
        }

        if (!alive){
            return;
        }

        speed -= 0.25 * deltaTime;
        speed = Math.max(speed, -15);
        if (speed < -10){
            minSpawnTime = 15;
        }

        score+=deltaTime;
        if (Main.highScore < score){
            Main.highScore = score;
        }
        double[] inputs = getInputs();
        double[] outputs = brain.forward(inputs);

        if (Main.manualOvveride){
            if(Main.window.upArrowHeld){
                outputs[0] = 1;
            }
            else {
                outputs[0] = -1;
            }

            if(Main.window.downArrowHeld){
                outputs[1] = 1;
            }
            else {
                outputs[1] = -1;
            }

        }

        boolean willJump = outputs[0] > 0;
        if (willJump && !Objects.equals(texture, "duckDino.png")){
            jump();
        }

        boolean willDuck = outputs[1] > 0;
        if (willDuck){
            isDucking = true;
        }
        else {
            isDucking = false;
        }

        if (isDucking){
            if (isGrounded() == 1){
                setTexture("duckDino.png");
                setSize(50,20);
                GoTo(new Vector2(0,165));
            }
            else{
                addForce(new Vector2(0, 2));
            }
        }
        else{
            setTexture("dino.png");
            setSize(50,50);
            if (isGrounded() == 1){
                GoTo(new Vector2(0,150));
            }
        }

        handleSpawning();
        handleMoving();
    }

    //Fitness
    public double fitness(){
        return score;
    }
}
