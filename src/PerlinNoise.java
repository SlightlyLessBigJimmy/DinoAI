import java.util.Random;

public class PerlinNoise {

    private static final int PERMUTATION_SIZE = 256;
    private final int[] p = new int[PERMUTATION_SIZE * 2];

    public PerlinNoise(long seed) {
        int[] permutation = new int[PERMUTATION_SIZE];
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            permutation[i] = i;
        }

        Random rand = new Random(seed);
        for (int i = PERMUTATION_SIZE - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = permutation[i];
            permutation[i] = permutation[index];
            permutation[index] = temp;
        }

        for (int i = 0; i < PERMUTATION_SIZE * 2; i++) {
            p[i] = permutation[i % PERMUTATION_SIZE];
        }
    }

    // Fade curve
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // Linear interpolation
    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    // Gradient function
    private double grad(int hash, double x, double y) {
        switch (hash & 3) {
            case 0: return  x + y;
            case 1: return -x + y;
            case 2: return  x - y;
            case 3: return -x - y;
            default: return 0;
        }
    }

    // 2D Perlin Noise
    public double noise(double x, double y) {
        int X = ((int) Math.floor(x)) & 255;
        int Y = ((int) Math.floor(y)) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int aa = p[p[X] + Y];
        int ab = p[p[X] + Y + 1];
        int ba = p[p[X + 1] + Y];
        int bb = p[p[X + 1] + Y + 1];

        double x1 = lerp(
                grad(aa, x, y),
                grad(ba, x - 1, y),
                u
        );
        double x2 = lerp(
                grad(ab, x, y - 1),
                grad(bb, x - 1, y - 1),
                u
        );

        return lerp(x1, x2, v);
    }
}
