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

    public boolean upHeld = false;
    public boolean downHeld = false;
    public boolean leftHeld = false;
    public boolean rightHeld = false;

    public GameWindow() {
        this.setTitle("Gaem");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 450);
        this.setLocationRelativeTo(null);
        //this.setResizable(false);

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
            }
        });
        setFocusable(true); // Ensures the JPanel can receive key events
        requestFocusInWindow(); // Request focus

    }

    public void StartThread() {
        Thread gameThread = new Thread(() -> {

            while (running) {

                long currentTime = System.nanoTime();
                double deltaTimeSeconds = (currentTime - lastTime) / 1_000_000_000.0;

                lastTime = currentTime;

                Update(deltaTimeSeconds);

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
        Main.Update(deltaTime);
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
