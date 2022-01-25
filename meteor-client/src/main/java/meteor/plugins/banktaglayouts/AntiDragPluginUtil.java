package meteor.plugins.banktaglayouts;

import lombok.RequiredArgsConstructor;
import meteor.input.KeyListener;
import net.runelite.api.events.FocusChanged;

import java.awt.event.KeyEvent;

/**
 * Utilities for simulating the AntiDrag plugin.
 */
@RequiredArgsConstructor
public class AntiDragPluginUtil implements KeyListener {

    private final BankTagLayoutsPlugin plugin;

    private boolean ctrlDown = false;
    private boolean shiftDown = false;

    /**
     * necessary to prevent the item snapping back if you start a drag and then tap control.
     */
    private boolean earlyAllowDrag = false;
    private long dragStart = -1;

    public boolean isAntiDragPluginEnabled() {
        return Boolean.parseBoolean(plugin.configManager.getConfiguration("runelite", "antidragplugin"));
    }

    /**
     * @return drag delay in client ticks (20ms increments).
     */
    public int getDragDelay() {
        int dragDelay;
        try {
            dragDelay = Integer.parseInt(plugin.configManager.getConfiguration("antiDrag", "dragDelay"));
        } catch (NumberFormatException e) {
            return 30; // default value.
        }
        return dragDelay;
    }

    public boolean disableOnCtrl() {
        return Boolean.parseBoolean(plugin.configManager.getConfiguration("antiDrag", "disableOnCtrl"));
    }

    public boolean onShiftOnly() {
        String configuration = plugin.configManager.getConfiguration("antiDrag", "onShiftOnly");
        if (configuration == null) {
            return true; // default value is true for this setting.
        }
        return Boolean.parseBoolean(configuration);
    }

    /**
     * Factors in plugin enabled, and keyboard key state.
     */
    public int getCurrentDragDelay() {
        if (!isAntiDragPluginEnabled()) {
            return 5;
        }
        if (onShiftOnly()) {
            return shiftDown ? getDragDelay() : 5;
        }
        if (disableOnCtrl() && ctrlDown) {
            return 5;
        }
        return getDragDelay();
    }

    public boolean mayDrag() {
        if (dragStart == -1) {
            return true;
        }
        if (earlyAllowDrag) {
            return true;
        }
        return System.currentTimeMillis() - dragStart > getCurrentDragDelay() * 20;
    }

    public void startDrag() {
        dragStart = System.currentTimeMillis();
        earlyAllowDrag = false;
    }

    public void endDrag() {
        dragStart = -1;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlDown = true;
            earlyAllowDrag = true;
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlDown = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftDown = false;
        }
    }

    public void focusChanged(FocusChanged focusChanged) {
        if (!focusChanged.isFocused()) {
            ctrlDown = false;
            shiftDown = false;
        }
    }
}
