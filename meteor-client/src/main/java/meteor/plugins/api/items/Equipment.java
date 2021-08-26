package meteor.plugins.api.items;

import meteor.plugins.api.game.Game;
import meteor.plugins.api.game.GameThread;
import net.runelite.api.*;
import net.runelite.api.widgets.WidgetInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Equipment {

    public static List<Item> getAll(Predicate<Item> filter) {
        List<Item> items = new ArrayList<>();
        ItemContainer container = Game.getClient().getItemContainer(InventoryID.EQUIPMENT);
        if (container == null) {
            return items;
        }

        Inventory.cacheItems(container);

        Item[] containerItems = container.getItems();
        for (int i = 0, containerItemsLength = containerItems.length; i < containerItemsLength; i++) {
            Item item = containerItems[i];
            if (item.getId() != -1 && item.getName() != null && !item.getName().equals("null")) {
                WidgetInfo widgetInfo = getEquipmentWidgetInfo(item.getSlot());
                item.setActionParam(-1);
                item.setSlot(i);

                if (widgetInfo != null) {
                    item.setWidgetId(widgetInfo.getPackedId());

                    if (filter.test(item)) {
                        items.add(item);
                    }
                }
            }
        }

        return items;
    }

    public static List<Item> getAll() {
        return getAll(x -> true);
    }

    public static List<Item> getAll(int... ids) {
        return getAll(x -> {
            for (int id : ids) {
                if (id == x.getId()) {
                    return true;
                }
            }

            return false;
        });
    }

    public static List<Item> getAll(String... names) {
        return getAll(x -> {
            if (x.getName() == null) {
                return false;
            }

            for (String name : names) {
                if (name.equals(x.getName())) {
                    return true;
                }
            }

            return false;
        });
    }

    public static Item getFirst(Predicate<Item> filter) {
        return getAll(filter).stream().findFirst().orElse(null);
    }

    public static Item getFirst(int... ids) {
        return getFirst(x -> {
            for (int id : ids) {
                if (id == x.getId()) {
                    return true;
                }
            }

            return false;
        });
    }

    public static Item getFirst(String... names) {
        return getFirst(x -> {
            if (x.getName() == null) {
                return false;
            }

            for (String name : names) {
                if (name.equals(x.getName())) {
                    return true;
                }
            }

            return false;
        });
    }

    private static WidgetInfo getEquipmentWidgetInfo(int itemIndex) {
        for (EquipmentInventorySlot equipmentInventorySlot : EquipmentInventorySlot.values()) {
            if (equipmentInventorySlot.getSlotIdx() == itemIndex) {
                return equipmentInventorySlot.getWidgetInfo();
            }
        }

        return null;
    }

    public static boolean contains(Predicate<Item> filter) {
        return getFirst(filter) != null;
    }

    public static boolean contains(int... ids) {
        return contains(x -> {
            for (int id : ids) {
                if (id == x.getId()) {
                    return true;
                }
            }

            return false;
        });
    }

    public static boolean contains(String... names) {
        return contains(x -> {
            if (x.getName() == null) {
                return false;
            }

            for (String name : names) {
                if (name.equals(x.getName())) {
                    return true;
                }
            }

            return false;
        });
    }
}
