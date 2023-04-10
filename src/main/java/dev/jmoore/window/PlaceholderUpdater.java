package dev.jmoore.window;

import javafx.scene.layout.StackPane;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlaceholderUpdater implements Runnable {

    private final StackPane parent;
    private final AtomicInteger x;
    private final AtomicInteger y;
    private final AtomicInteger lastX = new AtomicInteger(0);
    private final AtomicInteger lastY = new AtomicInteger(0);
    private final AtomicLong lastUpdateX = new AtomicLong(0);
    private final AtomicLong lastUpdateY = new AtomicLong(0);

    public PlaceholderUpdater(StackPane parent, int x, int y) {
        this.parent = parent;
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
    }

    @Override
    public void run() {
        while (parent.isVisible()) {
            //#region Sleeper
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //#endregion

            // Check last update difference
            final var currentTime = System.currentTimeMillis();
            final var threshold = 500;
            if ((currentTime - lastUpdateX.get() > threshold) && (currentTime - lastUpdateY.get() > threshold)) {

                // Update the placeholder if the values have changed
                if (lastX.get() != x.get() || lastY.get() != y.get()) {
                    parent.setBackground(JFXBackgroundPlaceholderImage.get(x.get(), y.get()));
                    lastX.set(x.get());
                    lastY.set(y.get());
                    System.out.println("Placeholder updated");
                }
            }
        }
    }

    public void updateX(double x) {
        this.x.set((int) x);
        this.lastUpdateX.set(System.currentTimeMillis());
    }

    public void updateY(double y) {
        this.y.set((int) y);
        this.lastUpdateY.set(System.currentTimeMillis());
    }
}
