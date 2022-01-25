package meteor.plugins.banktaglayouts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meteor.game.ItemVariationMapping;
import meteor.plugins.banktaglayouts.invsetupsstuff.InventorySetup;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class LayoutGenerator {

    private final BankTagLayoutsPlugin plugin;

    private static int toZigZagIndex(int inventoryIndex, int row, int col) {
        if (inventoryIndex < 0 || row < 0 || col < 0) {
            throw new IllegalArgumentException();
        }

        row += (inventoryIndex / 16) * 2; // Does this cover multiple pairs of rows?
        inventoryIndex -= (inventoryIndex / 16) * 16;
        int index = 0;
        index += inventoryIndex % 2 == 0 ? 0 : 8; // top or bottom row?
        index += inventoryIndex / 2; // column.
        index += row * 8 + col; // offset.
        return index;
    }

    public Layout basicLayout(List<Integer> equippedItems,
                              List<Integer> inventory,
                              Layout currentLayout,
                              int duplicateLimit) {
        return basicLayout(equippedItems, inventory, Collections.emptyList(), Collections.emptyList(), currentLayout, duplicateLimit);
    }

    public Layout basicLayout(List<Integer> equippedItems,
                              List<Integer> inventory,
                              List<Integer> runePouch,
                              List<Integer> additionalItems,
                              Layout currentLayout,
                              int duplicateLimit) {
        Layout previewLayout = Layout.emptyLayout();
        List<Integer> displacedItems = new ArrayList<>();

        log.debug("generate layout");
        log.debug("equipped gear is " + equippedItems);
        log.debug("inventory is " + inventory);

        int i = 0;

        // lay out equipped items.
        equippedItems = equippedItems.stream()
                .map(itemId -> plugin.itemManager.canonicalize(itemId)) // Weight reducing items have different ids when equipped; this fixes that.
                .collect(Collectors.toList());
        i = layoutItems(equippedItems, currentLayout, previewLayout, displacedItems, i, true);

        inventory = inventory.stream().filter(integer -> integer != -1).collect(Collectors.toList());

        // lay out the inventory items.
        if (duplicateLimit <= 0) {
            // distinct leaves the first duplicate it encounters and removes only duplicates coming after the first.
            inventory = inventory.stream().distinct().collect(Collectors.toList());
        } else {
            inventory = limitDuplicates(inventory, duplicateLimit);
        }

        i = layoutItems(inventory, currentLayout, previewLayout, displacedItems, i, true);

        i = layoutItems(runePouch, currentLayout, previewLayout, displacedItems, i, false);

        i = layoutItems(additionalItems, currentLayout, previewLayout, displacedItems, i, false);

        int displacedItemsStart = i;

        // copy items from current layout into the empty spots.
        for (Map.Entry<Integer, Integer> itemPosition : currentLayout.allPairs()) {
            int index = itemPosition.getKey();
            int currentItemAtIndex = itemPosition.getValue();
            int previewItemAtIndex = previewLayout.getItemAtIndex(index);

            if (currentItemAtIndex != -1 && previewItemAtIndex == -1) {
                previewLayout.putItem(currentItemAtIndex, index);
            }
        }

        // Remove items that were placed as part of the gear or inventory.
        displacedItems = displacedItems.stream().filter(id -> !layoutContainsItem(id, previewLayout)).collect(Collectors.toList());

        int j = displacedItemsStart;
        while (displacedItems.size() > 0 && j < 2000 / 38 * 8) {
            int currentItemAtIndex = currentLayout.getItemAtIndex(j);
            if (currentItemAtIndex == -1) {
                Integer itemId = displacedItems.remove(0);
                log.debug(itemId + " goes to " + j);
                previewLayout.putItem(itemId, j);
            }

            j++;
        }

        return previewLayout;
    }

    private List<Integer> limitDuplicates(List<Integer> inventory, int duplicateLimit) {
        List<Map.Entry<Integer, Integer>> groupedInventory = new ArrayList<>();

        int inARow = 0;
        int lastItemId = -1;
        for (Integer itemId : inventory) {
            if (lastItemId != itemId) {
                int quantity = inARow > duplicateLimit ? 1 : inARow;
                groupedInventory.add(new AbstractMap.SimpleEntry<>(lastItemId, quantity));
                inARow = 0;
            }
            inARow++;

            lastItemId = itemId;
        }
        int quantity = inARow > duplicateLimit ? 1 : inARow;
        if (quantity > 0) {
            groupedInventory.add(new AbstractMap.SimpleEntry<>(lastItemId, quantity));
        }

        inventory = groupedInventory.stream().flatMap(entry -> Collections.nCopies(entry.getValue(), entry.getKey()).stream()).collect(Collectors.toList());
        return inventory;
    }

    private int layoutItems(List<Integer> inventory,
                            Layout currentLayout,
                            Layout previewLayout,
                            List<Integer> displacedItems,
                            int i,
                            boolean useZigZag) {
        for (Integer itemId : inventory) {
            if (itemId == -1) {
                continue;
            }
            int index = useZigZag ? toZigZagIndex(i, 0, 0) : i;
            previewLayout.putItem(itemId, index);
            int currentLayoutItem = currentLayout.getItemAtIndex(index);
            if (currentLayoutItem != -1) {
                displacedItems.add(currentLayoutItem);
            }
            i++;
        }
        if (!inventory.isEmpty()) {
            Optional<Integer> highestUsedIndex = previewLayout.getAllUsedIndexes().stream().max(Integer::compare);
            if (highestUsedIndex.isPresent()) {
                if (useZigZag) {
                    i = (highestUsedIndex.get() / 16 * 2 + 2) * 8;
                } else {
                    i = (highestUsedIndex.get() / 8 + 1) * 8;
                }
            }
        }
        return i;
    }

    private boolean listContainsItem(int id, List<Integer> items) {
        int nonPlaceholderId = plugin.getNonPlaceholderId(id);
        for (Integer item : items) {
            if (nonPlaceholderId == plugin.getNonPlaceholderId(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean layoutContainsItem(int id, Layout previewLayout) {
        int baseId = ItemVariationMapping.map(plugin.getNonPlaceholderId(id));
        for (Integer item : previewLayout.getAllUsedItemIds()) {
            if (baseId == ItemVariationMapping.map(plugin.getNonPlaceholderId(item))) {
                return true;
            }
        }
        return false;
    }

    // TODO opening inventory setups panel closes auto-layout preview.

    public Layout basicInventorySetupsLayout(InventorySetup inventorySetup, Layout currentLayout, int duplicateLimit) {
        List<Integer> equippedGear = inventorySetup.getEquipment() == null ? Collections.emptyList() : inventorySetup.getEquipment().stream().map(isi -> isi.getId()).filter(id -> id != -1).collect(Collectors.toList());
        List<Integer> inventory = inventorySetup.getInventory() == null ? Collections.emptyList() : inventorySetup.getInventory().stream().map(isi -> isi.getId()).filter(id -> id != -1).collect(Collectors.toList());
        List<Integer> runePouchRunes = inventorySetup.getRune_pouch() == null ? Collections.emptyList() : inventorySetup.getRune_pouch().stream().map(isi -> isi.getId()).filter(id -> id != -1).collect(Collectors.toList());
        List<Integer> additionalItems = inventorySetup.getAdditionalFilteredItems() == null ? Collections.emptyList() : inventorySetup.getAdditionalFilteredItems().entrySet().stream().map(isi -> isi.getValue().getId()).filter(id -> id != -1).collect(Collectors.toList());

        return basicLayout(equippedGear, inventory, runePouchRunes, additionalItems, currentLayout, duplicateLimit);
    }
}
