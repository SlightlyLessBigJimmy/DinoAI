public class GameObject {

    Vector2 Position = new Vector2(0,0);
    Vector2 Velocity = new Vector2(0,0);

    boolean usePhysics = false;
    private boolean destroyed = false;

    GameObject(){
        Main.objects.add(this);
    }

    public void GoTo(double xPos, double yPos){
        Position.setX(xPos);
        Position.setY(yPos);
    }

    public void GoTo(Vector2 newPos){
        Position.setX(newPos.x);
        Position.setY(newPos.y);
    }

    public void Move(Vector2 direction){
        Position.x += direction.x;
        Position.y += direction.y;
    }

    public void setVelocity(Vector2 newVel){
        Velocity.setX(newVel.x);
        Velocity.setY(newVel.y);
    }

    public void addForce(Vector2 force){
        Velocity.Add(force);
    }

    public void setPhysics(boolean newBool){
        usePhysics = newBool;
    }

    public boolean usesPhysics(){
        return usePhysics;
    }

    public Vector2 getVelocity(){
        return Velocity;
    }

    public Vector2 getPosition(){
        return Position;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public void Destroy(){
        destroyed = true;
    }

    // Called during cleanup phase
    public void OnDestroy(){
        // Override in subclasses if needed
    }
}
