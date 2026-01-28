public class GameObject {

    Vector2 Position = new Vector2(0,0);

    GameObject(){
        Main.objects.add(this);
    }

    public void GoTo(double xPos, double yPos){
        Position.setX(xPos);
        Position.setY(yPos);
    }

    public Vector2 GetPosition(){
        return Position;
    }

}
