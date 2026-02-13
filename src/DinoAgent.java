import java.util.ArrayList;

public class DinoAgent extends Sprite {

    public int score = 0;
    public double speed = -5;

    ArrayList<Sprite> cacti = new ArrayList<Sprite>();

    public DinoAgent(){
        super("Dino.png", new Vector2(50,50), 1, new Vector2(0,0));
        this.setPhysics(true);
        this.setCollision(true, "transparency");
    }

    public void jump(){
        if (isGrounded() == 1) {
            addForce(new Vector2(0, -20));
        }
    }

    public Sprite spawnCactus(){
        speed -= .05;
        speed = Math.max(speed, -15);

        Sprite cactus = new Sprite("Cactus.png", new Vector2(30,60), 100, new Vector2(410,143));

        cacti.add(cactus);

        cactus.setCollision(true, "transparency");
        cactus.addToIgnoreList(Main.floor);
        cactus.addToIgnoreList(this);
        this.addToIgnoreList(cactus);
        for (Sprite other : cacti){
            if (other != cactus){
                other.addToIgnoreList(cactus);
                cactus.addToIgnoreList(other);
            }
        }

        return cactus;
    }

    public int isGrounded(){
        ArrayList<Sprite> ignoreList = new ArrayList<>();
        ignoreList.add(this);

        Raycast groundCheck = new Raycast(
                getPosition(),
                new Vector2(0,1),
                24,
                ignoreList
        );

        if (groundCheck.Hit){
            return 1;
        }
        else {
            return 0;
        }

    }


    public double[] getInputs(){

        return new double[]{
                (getPosition().y - 6.0) / (142.0 - 6.0),//Agent's vertical Position (normalized)
                (getVelocity().y + 15.0) / 32.0,//Agent's vertical Velocity (normalized)
                0,//distance from cacti (normalized)
                0,//distance to 2nd cacti (normalized)
                isGrounded(),//is touching floor?
                (speed + 15.0) / 10.0,//movement speed (normalized)
                0,//distance between cacti (normalized)
                0//bias
        };
    }

    public void update(){
        jump();

        for (Sprite cactus : cacti){

            if (cactus.isDestroyed()) continue;

            if (cactus.getPosition().x < -410){
                cactus.Destroy();
            }
        }
        cacti.removeIf(Sprite::isDestroyed);
    }

}
