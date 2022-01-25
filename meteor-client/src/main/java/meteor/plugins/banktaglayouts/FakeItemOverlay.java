package meteor.plugins.banktaglayouts;

import lombok.extern.slf4j.Slf4j;
import meteor.game.ItemManager;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.tooltip.Tooltip;
import meteor.ui.overlay.tooltip.TooltipManager;
import meteor.util.ColorUtil;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class FakeItemOverlay extends Overlay {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private TooltipManager tooltipManager;

    @Inject
    private BankTagLayoutsPlugin plugin;

    @Inject
    private BankTagLayoutsConfig config;
    private Tooltip tooltip = null;

    FakeItemOverlay() {
        drawAfterLayer(WidgetInfo.BANK_ITEM_CONTAINER);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        BankTagLayoutsPlugin.LayoutableThing currentLayoutableThing = plugin.getCurrentLayoutableThing();
        if (currentLayoutableThing == null) {
            return null;
        }

        Layout layout = plugin.getBankOrder(currentLayoutableThing);
        if (layout == null) {
            return null;
        }

        if (config.showLayoutPlaceholders() && log.isDebugEnabled()) {
            updateTooltip(layout);
        }

        Widget bankItemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankItemContainer == null) {
            return null;
        }
        int scrollY = bankItemContainer.getScrollY();
        Point canvasLocation = bankItemContainer.getCanvasLocation();
        Rectangle bankItemArea = new Rectangle(canvasLocation.getX() + 51 - 6, canvasLocation.getY(), bankItemContainer.getWidth() - 51 + 6, bankItemContainer.getHeight());

        graphics.clip(bankItemArea);

        for (BankTagLayoutsPlugin.FakeItem fakeItem : plugin.fakeItems) {
            if (fakeItem.isLayoutPlaceholder() && !config.showLayoutPlaceholders()) {
                continue;
            }

            int dragDeltaX = 0;
            int dragDeltaY = 0;
            if (fakeItem.index == plugin.draggedItemIndex && plugin.antiDrag.mayDrag()) {
                dragDeltaX = client.getMouseCanvasPosition().getX() - plugin.dragStartX;
                dragDeltaY = client.getMouseCanvasPosition().getY() - plugin.dragStartY;
                dragDeltaY += bankItemContainer.getScrollY() - plugin.dragStartScroll;
            }
            int fakeItemId = fakeItem.getItemId();

            int x = BankTagLayoutsPlugin.getXForIndex(fakeItem.index) + canvasLocation.getX() + dragDeltaX;
            int y = BankTagLayoutsPlugin.getYForIndex(fakeItem.index) + canvasLocation.getY() - scrollY + dragDeltaY;
            if (y + BankTagLayoutsPlugin.BANK_ITEM_HEIGHT > bankItemArea.getMinY() && y < bankItemArea.getMaxY()) {
                if (fakeItem.isLayoutPlaceholder()) {
                    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    BufferedImage image = itemManager.getImage(fakeItemId, 1000, false);
                    graphics.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
                    BufferedImage outline = itemManager.getItemOutline(fakeItemId, 1000, Color.GRAY);
                    graphics.drawImage(outline, x, y, null);
                } else {
                    if (fakeItem.quantity == 0) {
                        // placeholder.
                        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    } else {
                        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    }
                    boolean showQuantity = itemManager.getItemComposition(fakeItemId).isStackable() || fakeItem.quantity != 1;
                    BufferedImage image = itemManager.getImage(fakeItemId, fakeItem.quantity, showQuantity);
                    graphics.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

                    if (log.isDebugEnabled()) {
                        graphics.setColor(Color.PINK);
                        graphics.drawString("Dup", x, y + 33);
                    }
                }
            }
        }

        return null;
    }

    private void updateTooltip(Layout layout) {
        tooltipManager.getTooltips().remove(tooltip);
        tooltip = null;

        int index = plugin.getIndexForMousePosition(true);
        if (!log.isDebugEnabled() && plugin.fakeItems.stream().noneMatch(fakeItem -> fakeItem.index == index)) {
            return;
        }

        if (index != -1) {
            int itemIdForTooltip = layout.getItemAtIndex(index);
            if (itemIdForTooltip != -1 && tooltip == null) {
                String tooltipString = ColorUtil.wrapWithColorTag(plugin.itemName(itemIdForTooltip), BankTagLayoutsPlugin.itemTooltipColor);
                if (log.isDebugEnabled()) {
                    tooltipString += " (" + itemIdForTooltip + (plugin.isPlaceholder(itemIdForTooltip) ? ", ph" : "") + ")";
                }
                tooltip = new Tooltip(tooltipString);
                tooltipManager.add(tooltip);
            }
        }
    }
}
