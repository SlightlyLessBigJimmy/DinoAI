import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameWindow extends JFrame {

    private long lastTime = System.nanoTime();
    private boolean running = true;

    private GamePanel panel;

    private int fps;
    private int frames;
    private long fpsTimer = System.nanoTime();

    public int FPS = 0;
    public int frameTime = 16;

    public boolean upHeld = false;
    public boolean downHeld = false;
    public boolean leftHeld = false;
    public boolean rightHeld = false;

    public boolean upArrowHeld = false;
    public boolean downArrowHeld = false;

    public GameWindow() {
        this.setTitle("Dino Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 450);
        this.setLocationRelativeTo(null);

        panel = new GamePanel();
        this.add(panel);

        this.setVisible(true);

        System.out.println("Starting game thread now...");
        StartThread();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    upHeld = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    downHeld = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftHeld = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightHeld = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upArrowHeld = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downArrowHeld = true;
                }

                //keybinds
                if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
                    System.out.println("saving current weights...");
                    Main.saveBrain(Main.population[0].brain, "saved_network.json");
                }
                if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET && !Main.generationRunning) {
                    NeuralNetwork loadedBrain = Main.loadBrain("saved_network.json");
                    System.out.println("Starting training from saved brain...");
                    Main.generation = loadedBrain.generationNumber;
                    Main.startGeneration(loadedBrain);
                }
                if (e.getKeyCode() == KeyEvent.VK_P && !Main.generationRunning) {
                    System.out.println("Starting training...");
                    Main.startGeneration(new NeuralNetwork(10, 16, 2));
                }
                if (e.getKeyCode() == KeyEvent.VK_O && Main.generationRunning) {
                    System.out.println("Toggled lesser agent visibility.");
                    Main.showLesserDinos = !Main.showLesserDinos;
                }
                if (e.getKeyCode() == KeyEvent.VK_I && !Main.generationRunning) {
                    System.out.println("Starting manual mode...");
                    Main.populationSize = 1;
                    Main.manualOvveride = true;
                    Main.startGeneration(new NeuralNetwork(10, 16, 2));
                }
                if (e.getKeyCode() == KeyEvent.VK_F && Main.generationRunning) {
                    System.out.println("Teleported Camera to best dino");
                    Main.cam.GoTo(Main.currentBest.getPosition());
                }
                if (e.getKeyCode() == KeyEvent.VK_0) {
                    frameTime++;
                    System.out.println("FrameTime is now: " + frameTime);
                }

                if (e.getKeyCode() == KeyEvent.VK_9) {
                    frameTime = Math.max(frameTime - 1, 1);
                    System.out.println("FrameTime is now: " + frameTime);
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    upHeld = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    downHeld = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftHeld = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightHeld = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upArrowHeld = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downArrowHeld = false;
                }
            }
        });
        setFocusable(true); // Ensures the JPanel can receive key events
        requestFocusInWindow(); // Request focus

    }


    public void StartThread() {
        Thread gameThread = new Thread(() -> {
            int numberOfLoops = 0;
            while (running) {
                numberOfLoops += 1;

                long currentTime = System.nanoTime();
                double deltaTimeSeconds = (currentTime - lastTime) / 1_000_000_000.0;

                lastTime = currentTime;

                if (numberOfLoops > 10){
                    numberOfLoops = 100;
                    Main.Update(deltaTimeSeconds);
                    Update(deltaTimeSeconds);
                }

                try {
                    Thread.sleep(frameTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        gameThread.start();
    }

    public void Update(double deltaTime) {
        panel.repaint();
        frames++;
        long now = System.nanoTime();

        if (now - fpsTimer >= 1_000_000_000L) {
            fps = frames;
            frames = 0;
            fpsTimer = now;
            FPS = fps;
            //System.out.println("FPS: " + fps);
        }
    }
}
