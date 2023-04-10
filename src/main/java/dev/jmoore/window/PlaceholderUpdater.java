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
    }
}
