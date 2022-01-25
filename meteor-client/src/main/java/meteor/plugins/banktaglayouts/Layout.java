package meteor.plugins.banktaglayouts;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Layout {

    // Maps indexes to items.
    private final Map<Integer, Integer> layoutMap = new HashMap<>();

    public static Layout fromString(String layoutString) {
        return fromString(layoutString, false);
    }

    public static Layout fromString(String layoutString, boolean ignoreNfe) {
        Layout layout = Layout.emptyLayout();
        if (layoutString.isEmpty()) {
            return layout;
        }
        for (String s1 : layoutString.split(",")) {
            String[] split = s1.split(":");
            try {
                int itemId = Integer.parseInt(split[0]);
                int index = Integer.parseInt(split[1]);
                if (index >= 0) {
                    layout.putItem(itemId, index);
                } else {
                    //                    log.debug("Removed item " + itemName(itemId) + " (" + itemId + ") due to it having a negative index (" + index + ")");
                    log.debug("Removed item " + itemId + " due to it having a negative index (" + index + ")");
                }
            } catch (NumberFormatException e) {
                if (!ignoreNfe) {
                    throw e;
                }
                log.debug("input string \"" + layoutString + "\"");
            }
        }
        return layout;
    }

    public static Layout emptyLayout() {
        return new Layout();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> integerIntegerEntry : allPairs()) {
            sb.append(integerIntegerEntry.getValue() + ":" + integerIntegerEntry.getKey() + ",");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public void putItem(int itemId, int index) {
        layoutMap.put(index, itemId);
    }

    /**
     * returns -1 if there is no item there.
     */
    public int getItemAtIndex(int index) {
        return layoutMap.getOrDefault(index, -1);
    }

    public Iterator<Map.Entry<Integer, Integer>> allPairsIterator() {
        return layoutMap.entrySet().iterator();
    }

    /**
     * Finds the index for the EXACT itemId. Does not factor in placeholders or variation items. For duplicated items,
     * it returns one of the indexes where the itemId can be found.
     * If there's no index for this itemId, then it returns -1.
     */
    public Integer getIndexForItem(int itemId) {
        return allPairs().stream()
                .filter(e -> e.getValue() == itemId)
                .map(e -> e.getKey())
                .findAny().orElse(-1);
    }

    /**
     * Finds the indexes for the EXACT itemId. Does not factor in placeholders or variation items.
     * If there're no indexes for this itemId, then it returns an empty list.
     */
    private List<Integer> getIndexesForItem(int itemId) {
        return allPairs().stream()
                .filter(e -> e.getValue() == itemId)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public Collection<Integer> getAllUsedItemIds() {
        return new HashSet<>(layoutMap.values());
    }

    public Collection<Integer> getAllUsedIndexes() {
        return layoutMap.keySet();
    }

    public Collection<Map.Entry<Integer, Integer>> allPairs() {
        return layoutMap.entrySet();
    }

    public int getFirstEmptyIndex() {
        return getFirstEmptyIndex(-1);
    }

    public int getFirstEmptyIndex(int afterThisIndex) {
        List<Integer> indexes = new ArrayList<>(getAllUsedIndexes());
        indexes.sort(Integer::compare);
        for (Integer integer : indexes) {
            if (integer < afterThisIndex) {
                continue;
            }

            if (integer - afterThisIndex > 1) {
                break;
            }
            afterThisIndex = integer;
        }
        return afterThisIndex + 1;
    }

    public void clearIndex(int index) {
        layoutMap.remove(index);
    }

    /**
     * @param draggedItemIndex dragged item's original index.
     * @param targetIndex      target location's index.
     * @param draggedItemId    the dragged item widget's item id.
     */
    public void moveItem(int draggedItemIndex, int targetIndex, int draggedItemId) {
        int layoutItemId = getItemAtIndex(draggedItemIndex);
        if (draggedItemId == -1) { // dragging a layout placeholder, or bad input.
            draggedItemId = layoutItemId;
            assert draggedItemId != -1;
        } else if (layoutItemId != draggedItemId) {
            // Modifying a layout should use the real item there, NOT the item id stored in the layout (which can be
            // different due to how variant items are assigned indexes), because the item the user sees themselves
            // moving is the item id in the widget, not the item id in the layout. Therefore, the duplicates must be
            // updated to use that id as well.
            for (Integer index : getIndexesForItem(layoutItemId)) {
                putItem(draggedItemId, index);
            }
        }

        int targetItemId = getItemAtIndex(targetIndex);

        clearIndex(draggedItemIndex);
        clearIndex(targetIndex);
        putItem(draggedItemId, targetIndex);
        if (targetItemId != -1) {
            putItem(targetItemId, draggedItemIndex);
        }
    }

    public boolean isEmpty() {
        return layoutMap.isEmpty();
    }

    public int countItemsWithId(int idAtIndex) {
        int count = 0;
        for (Map.Entry<Integer, Integer> pair : allPairs()) {
            if (pair.getValue() == idAtIndex) {
                count++;
            }
        }
        return count;
    }

    // TODO create test.
    public void duplicateItem(int clickedItemIndex, int itemIdAtIndex) {
        int duplicatedItemIndex = getFirstEmptyIndex(clickedItemIndex);

        int layoutItemId = getItemAtIndex(clickedItemIndex);
        if (itemIdAtIndex == -1) {
            itemIdAtIndex = layoutItemId;
        }
        if (layoutItemId != itemIdAtIndex) {
            // Modifying a layout should always use the real item there, NOT the item id stored in the layout (which can
            // be different due to how variant items are assigned indexes).
            // Therefore, the duplicates must be updated to use that id as well.
            List<Integer> indexesToChange = getIndexesForItem(layoutItemId);
            for (Integer index : indexesToChange) {
                putItem(itemIdAtIndex, index);
            }
        }

        putItem(itemIdAtIndex, duplicatedItemIndex);
    }
}
