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

    public boolean jumpHeld = false;

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
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jumpHeld = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jumpHeld = false;
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
                    Thread.sleep(16);
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

            //System.out.println("FPS: " + fps);
        }
    }
}
