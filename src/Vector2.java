public class Vector2 {

    double x = 0;
    double y = 0;

    Vector2(double initX, double initY){

        x=initX;
        y=initY;

    }

    public void setX(double newX){
        x = newX;
    }

    public void setY(double newY){
        y = newY;
    }

    public double GetMagnitude(){
        return Math.sqrt(x*x + y*y);
    }

    public Vector2 Lerp(Vector2 v1, Vector2 v2, double t){
        t = Math.clamp(t, 0,1);
        double resultX = v1.x + (v2.x - v1.x) * t;
        double resultY = v1.y + (v2.y - v1.y) * t;

        setX(resultX);
        setY(resultY);
        return new Vector2(resultX,resultY);
    }

    public Vector2 Unit(){
        double resultX = (x/Math.sqrt((x*x)+(y*y)));
        double resultY = (y/Math.sqrt((x*x)+(y*y)));
        return new Vector2(resultX, resultY);
    }

}
