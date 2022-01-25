package meteor.plugins.banktaglayouts.invsetupsstuff;

import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import meteor.game.ItemVariationMapping;
import meteor.plugins.banktaglayouts.BankTagLayoutsPlugin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class InventorySetupsAdapter {

    public static final String CONFIG_GROUP = "inventorysetups";
    public static final String CONFIG_KEY = "setups";

    private final BankTagLayoutsPlugin plugin;

    public InventorySetup getInventorySetup(String name) {
        final String storedSetups = plugin.configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);
        if (Strings.isNullOrEmpty(storedSetups)) {
            return null;
        }
        try {
            Type type = new TypeToken<ArrayList<InventorySetup>>() {

            }.getType();

            return ((List<InventorySetup>) plugin.gson.fromJson(storedSetups, type)).stream().filter(s -> s.getName().equals(name)).findAny().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setupContainsItem(final InventorySetup setup, int itemID) {
        // So place holders will show up in the bank.
        itemID = plugin.itemManager.canonicalize(itemID);

        // Check if this item (inc. placeholder) is in the additional filtered items
        if (additionalFilteredItemsHasItem(itemID, setup.getAdditionalFilteredItems())) {
            return true;
        }

        // check the rune pouch to see if it has the item (runes in this case)
        if (setup.getRune_pouch() != null) {
            if (checkIfContainerContainsItem(itemID, setup.getRune_pouch())) {
                return true;
            }
        }

        return checkIfContainerContainsItem(itemID, setup.getInventory()) ||
                checkIfContainerContainsItem(itemID, setup.getEquipment());
    }

    private boolean additionalFilteredItemsHasItem(int itemId,
                                                   final HashMap<Integer, InventorySetupsItem> additionalFilteredItems) {
        final int canonicalizedId = plugin.itemManager.canonicalize(itemId);
        for (final Integer additionalItemKey : additionalFilteredItems.keySet()) {
            boolean isFuzzy = additionalFilteredItems.get(additionalItemKey).isFuzzy();
            int addItemId = getProcessedID(isFuzzy, additionalFilteredItems.get(additionalItemKey).getId());
            int finalItemId = getProcessedID(isFuzzy, canonicalizedId);
            if (addItemId == finalItemId) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfContainerContainsItem(int itemID, final ArrayList<InventorySetupsItem> setupContainer) {
        // So place holders will show up in the bank.
        itemID = plugin.itemManager.canonicalize(itemID);

        for (final InventorySetupsItem item : setupContainer) {
            // For equipped weight reducing items or noted items in the inventory
            int setupItemId = plugin.itemManager.canonicalize(item.getId());
            if (getProcessedID(item.isFuzzy(), itemID) == getProcessedID(item.isFuzzy(), setupItemId)) {
                return true;
            }
        }

        return false;
    }

    private int getProcessedID(boolean isFuzzy, int itemId) {
        // use fuzzy mapping if needed
        if (isFuzzy) {
            return ItemVariationMapping.map(itemId);
        }

        return itemId;
    }

}
