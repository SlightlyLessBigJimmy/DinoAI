public class NoiseTest {
    public static void main(String[] args) {
        PerlinNoise noise = new PerlinNoise(0);

        int width = 50;
        int height = 20;
        double scale = 0.1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double n = noise.noise(x * scale, y * scale);
                System.out.print(n > 0 ? "#" : ".");
            }
            System.out.println();
        }
    }
}
