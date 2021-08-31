package meteor.plugins.autoclicker;


import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.overlay.OverlayManager;
import meteor.util.HotkeyListener;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginDescriptor(
        name = "Soxs' AutoClicker",
        description = "Auto clicks. Lovely to pair with 1-click plugins.",
        enabledByDefault = false
)
@SuppressWarnings("unused")
@Singleton
public class AutoClickerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private AutoClickerConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private AutoClickerOverlay overlay;
    
    @Inject
    private OverlayManager overlayManager;

    private ExecutorService executorService, clickService;
    private Point savedPoint;
    private Random random;
    public static boolean run;
    private static AutoClickerPlugin instance;

    private static ArrayList<MouseListener> mouseListeners = new ArrayList<>();
    private static ArrayList<MouseMotionListener> mouseMotionListeners = new ArrayList<>();
    private static SMouseMotionListener myMouseMotionListener;
    private static SMouseListener myMouseListener;
    private static boolean setupMouseListener = false;
    private static boolean inputDisabled = false;


    @Provides
    public AutoClickerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoClickerConfig.class);
    }

    @Override
    public void startup() {
        instance = this;
        keyManager.registerKeyListener(hotkeyListener, this.getClass());
        executorService = Executors.newSingleThreadExecutor();
        clickService = Executors.newSingleThreadExecutor();
        random = new Random();
        overlayManager.add(overlay);
    }

    @Override
    public void shutdown() {
        keyManager.unregisterKeyListener(hotkeyListener);
        executorService.shutdown();
        clickService.shutdown();
        run = false;
        inputDisabled = false;
        overlayManager.remove(overlay);
    }

    public static boolean shouldDisableMouseInput() {
        return inputDisabled;
    }

    public Plugin getClickerPlugin() {
        return this;
    }

    void setupMouseListener() {
        if (!setupMouseListener) {
            Canvas c = client.getCanvas();
            MouseListener[] mL = c.getMouseListeners();
            MouseMotionListener[] mML = c.getMouseMotionListeners();

            for (MouseListener mouseListener : mL) {
                mouseListeners.add(mouseListener);
                c.removeMouseListener(mouseListener);
            }
            for (MouseMotionListener mouseMotionListener : mML) {
                mouseMotionListeners.add(mouseMotionListener);
                c.removeMouseMotionListener(mouseMotionListener);
            }
            c.addMouseListener(myMouseListener = new SMouseListener());
            c.addMouseMotionListener(myMouseMotionListener = new SMouseMotionListener());
            setupMouseListener = true;
        }
    }

    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            run = !run;

            if (!run) {
                inputDisabled = false;
                return;
            }

            savedPoint = client.getMouseCanvasPosition();
            if (config.disableRealMouse())
                inputDisabled = true;

            setupMouseListener();

            executorService.submit(() ->
            {

                while (run) {
                    Point mousePoint = client.getMouseCanvasPosition();
                    if (config.mouseOnNPC())
                    {
                        List<NPC> npcs = client.getNpcs();
                        AtomicBoolean shouldContinue = new AtomicBoolean(false);
                        clientThread.invokeLater(() -> {
                            for (NPC npc : npcs)
                            {
                                if (npc == null)
                                    continue;
                                if (npc.getId() == config.mouseOnNPCID())
                                {
                                    if (npc.getConvexHull().contains(mousePoint.getX(), mousePoint.getY()))
                                    {
                                        shouldContinue.set(true);
                                        break;
                                    }
                                }
                            }
                        });

                        if (!shouldContinue.get())
                        {
                            try {
                                Thread.sleep(getBetweenClicksDelay());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                    }

                    if (random.nextInt(100) < config.frequencyAFK()) {
                        try {
                            Thread.sleep(randWeightedInt(config.minDelayAFK(), config.maxDelayAFK()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (client.getGameState() == GameState.LOGGED_IN) {
                        clientThread.invokeLater(() -> {
                            if ((!config.skipOnMoving() || client.getLocalPlayer().getIdlePoseAnimation() == client.getLocalPlayer().getPoseAnimation()) &&
                                    (!config.skipOnInteraction() || client.getLocalPlayer().getInteracting() == null) &&
                                    (!config.skipOnAnimating() || client.getLocalPlayer().getAnimation() == -1)) {
                                if (config.followMouse()) {
                                    clickService.submit(() -> click(client.getMouseCanvasPosition()));
                                } else
                                    clickService.submit(() -> click(savedPoint));
                            }
                        });
                    }

                    try {
                        Thread.sleep(getBetweenClicksDelay());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                inputDisabled = false;
            });
        }
    };

    public long getBetweenClicksDelay() {
        return config.weightedDistribution() ?
                (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * config.deviation() + config.target())
                :
                (long) clamp(Math.round(random.nextGaussian() * config.deviation() + config.target()));
    }

    private double clamp(double val) {
        return Math.max(config.minDelay(), Math.min(config.maxDelay(), val));
    }

    public void click(Point p) {
        assert !client.isClientThread();

        Point original = client.getMouseCanvasPosition();
        boolean moved = false;

        if (client.isStretchedEnabled()) {
            final Dimension stretched = client.getStretchedDimensions();
            final Dimension real = client.getRealDimensions();
            final double width = (stretched.width / real.getWidth());
            final double height = (stretched.height / real.getHeight());

            final Point point = new Point((int) (p.getX() * width), (int) (p.getY() * height));
            original = new Point((int) (original.getX() * width), (int) (original.getY() * height));

            if (original.distanceTo(point) > 0) {
                mouseEvent(MouseEvent.MOUSE_MOVED, point);
                moved = true;
            }
            mouseEvent(MouseEvent.MOUSE_PRESSED, point);
            mouseEvent(MouseEvent.MOUSE_RELEASED, point);
            mouseEvent(MouseEvent.MOUSE_CLICKED, point);
            if (moved) {
                mouseEvent(MouseEvent.MOUSE_MOVED, original);
            }
            return;
        }
        if (original.distanceTo(p) > 0) {
            mouseEvent(MouseEvent.MOUSE_MOVED, p);
            moved = true;
        }
        mouseEvent(MouseEvent.MOUSE_PRESSED, p);
        mouseEvent(MouseEvent.MOUSE_RELEASED, p);
        mouseEvent(MouseEvent.MOUSE_CLICKED, p);
        if (moved) {
            mouseEvent(MouseEvent.MOUSE_MOVED, original);
        }
    }

    private void mouseEvent(int id, @NotNull Point point) {
        MouseEvent e = new SMouseEvent(
                client.getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, 1
        );

        client.getCanvas().dispatchEvent(e);
    }

    private int randWeightedInt(int min, int max) {
        int ra = randBellWeight(min, max);
        int sorted = Math.min(max, Math.max(min, ra));
        if (min >= 0 && max > 0)
            return Math.abs(sorted);
        else
            return sorted;
    }

    private int randBellWeight(int min, int max) {
        if (max <= min)
            max = min + 1;
        return (int) nextSkewedBoundedDouble(min, max, config.weightSkewAFK() / 10d, config.weightBiasAFK() / 10d);
    }

    private double nextSkewedBoundedDouble(double min, double max, double skew, double bias) {
        double range = max - min;
        double mid = min + range / 2.0;
        double unitGaussian = random.nextGaussian();
        double biasFactor = Math.exp(bias);
        return mid + (range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5));
    }


    static class SMouseEvent extends MouseEvent {
        public SMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
            super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
        }
    }

    private static class SMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseMotionListener mouseListener : mouseMotionListeners) {
                    mouseListener.mouseDragged(e);
                }
            } else {
                e.consume();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseMotionListener mouseListener : mouseMotionListeners) {
                    mouseListener.mouseMoved(e);
                }
            } else {
                e.consume();
            }
        }
    }

    private static class SMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseListener mouseListener : mouseListeners) {
                    mouseListener.mouseClicked(e);
                }
            } else
                e.consume();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseListener mouseListener : mouseListeners) {
                    mouseListener.mousePressed(e);
                }
            } else
                e.consume();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseListener mouseListener : mouseListeners) {
                    mouseListener.mouseReleased(e);
                }
            } else
                e.consume();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseListener mouseListener : mouseListeners) {
                    mouseListener.mouseEntered(e);
                }
            } else
                e.consume();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e instanceof SMouseEvent || !shouldDisableMouseInput()) {
                for (MouseListener mouseListener : mouseListeners) {
                    mouseListener.mouseExited(e);
                }
            } else
                e.consume();
        }

    }

}
