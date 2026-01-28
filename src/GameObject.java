public class GameObject {

    Vector2 Position = new Vector2(0,0);

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

    public Vector2 GetPosition(){
        return Position;
    }

}
