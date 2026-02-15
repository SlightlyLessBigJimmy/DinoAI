import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork implements Serializable {

    private static final long serialVersionUID = 1L;

    double mutationRate = 0.05;

    int generationNumber = 1;

    int c1 = 8;
    int c2 = 16;
    int c3 = 4;

    double[][] w1;
    double[][] w2;

    public NeuralNetwork(int c1Size, int c2Size, int c3Size){
        c1 = c1Size;
        c2 = c2Size;
        c3 = c3Size;

        w1 = new double[c1][c2];
        w2 = new double[c2][c3];

        for (int i = 0; i < c1; i++) {
            for (int j = 0; j < c2; j++) {
                w1[i][j] = Main.random.nextGaussian();
            }
        }

        for (int i = 0; i < c2; i++) {
            for (int j = 0; j < c3; j++) {
                w2[i][j] = Main.random.nextGaussian();
            }
        }

    }

    public double[] forward(double[] x) {
        double[] hidden = new double[c2];

        for (int j = 0; j < c2; j++) {
            double sum = 0;
            for (int i = 0; i < c1; i++) {
                sum += x[i] * w1[i][j];
            }
            hidden[j] = Math.tanh(sum);
        }

        double[] output = new double[c3];

        for (int k = 0; k < c3; k++) {
            double sum = 0;
            for (int j = 0; j < c2; j++) {
                sum += hidden[j] * w2[j][k];
            }
            output[k] = Math.tanh(sum);
        }

        return output;
    }

    public NeuralNetwork clone(){
        NeuralNetwork newNetwork = new NeuralNetwork(c1, c2, c3);
        newNetwork.setWeights(w1, w2);
        return newNetwork;
    }

    public void mutate() {
        for (int i = 0; i < w1.length; i++) {
            for (int j = 0; j < w1[i].length; j++) {
                if (Main.random.nextDouble() < mutationRate) {
                    w1[i][j] += Main.random.nextGaussian() * 0.3;
                }
            }
        }

        for (int i = 0; i < w2.length; i++) {
            for (int j = 0; j < w2[i].length; j++) {
                if (Main.random.nextDouble() < mutationRate) {
                    w2[i][j] += Main.random.nextGaussian() * 0.3;
                }
            }
        }
    }

    public void setWeights(double[][] w1New, double[][] w2New){
        w1 = new double[w1New.length][w1New[0].length];
        w2 = new double[w2New.length][w2New[0].length];

        for (int i = 0; i < w1New.length; i++)
            System.arraycopy(w1New[i], 0, w1[i], 0, w1New[i].length);

        for (int i = 0; i < w2New.length; i++)
            System.arraycopy(w2New[i], 0, w2[i], 0, w2New[i].length);
    }

    public double[][] getWeights(int wNumber){

        if (wNumber == 1){
            return w1;
        }
        else if (wNumber == 2){
            return w2;
        }

        return w1;
    }

    public String toString(){
        return Arrays.deepToString(w1) + "\n" + Arrays.deepToString(w2);
    }

}
