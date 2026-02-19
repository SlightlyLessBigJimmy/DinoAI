import java.io.Serializable;
import java.util.Arrays;

public class NeuralNetwork implements Serializable {

    private static final long serialVersionUID = 1L;

    public double mutationRate = 0.05;
    public int generationNumber = 1;

    public int[] layers;          // layer sizes
    public double[][][] weights;  // weights[layer][from][to]

    public NeuralNetwork(int... layerSizes) {
        layers = layerSizes.clone();

        weights = new double[layers.length - 1][][];

        for (int l = 0; l < weights.length; l++) {
            int from = layers[l];
            int to = layers[l + 1];

            weights[l] = new double[from][to];

            for (int i = 0; i < from; i++) {
                for (int j = 0; j < to; j++) {
                    weights[l][i][j] = Main.random.nextGaussian();
                }
            }
        }
    }

    public double[] forward(double[] input) {

        double[] activations = input;

        for (int l = 0; l < weights.length; l++) {

            int nextSize = layers[l + 1];
            double[] next = new double[nextSize];

            for (int j = 0; j < nextSize; j++) {
                double sum = 0;

                for (int i = 0; i < activations.length; i++) {
                    sum += activations[i] * weights[l][i][j];
                }

                next[j] = Math.tanh(sum);
            }

            activations = next;
        }

        return activations;
    }

    public NeuralNetwork clone() {
        NeuralNetwork copy = new NeuralNetwork(layers);

        for (int l = 0; l < weights.length; l++) {
            for (int i = 0; i < weights[l].length; i++) {
                System.arraycopy(weights[l][i], 0, copy.weights[l][i], 0, weights[l][i].length);
            }
        }

        return copy;
    }

    public void mutate() {

        for (int l = 0; l < weights.length; l++) {
            for (int i = 0; i < weights[l].length; i++) {
                for (int j = 0; j < weights[l][i].length; j++) {

                    if (Main.random.nextDouble() < mutationRate) {
                        weights[l][i][j] += Main.random.nextGaussian() * 0.3;
                    }

                }
            }
        }
    }

    public double[][] getWeights(int layerIndex) {
        return weights[layerIndex];
    }

    public void setWeights(double[][][] newWeights) {

        weights = new double[newWeights.length][][];

        for (int l = 0; l < newWeights.length; l++) {

            weights[l] = new double[newWeights[l].length][newWeights[l][0].length];

            for (int i = 0; i < newWeights[l].length; i++) {
                System.arraycopy(newWeights[l][i], 0, weights[l][i], 0, newWeights[l][i].length);
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.deepToString(weights);
    }
}
