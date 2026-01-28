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

}
