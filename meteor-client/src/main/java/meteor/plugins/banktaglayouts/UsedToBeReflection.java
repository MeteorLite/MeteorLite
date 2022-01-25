package meteor.plugins.banktaglayouts;


import meteor.game.ItemVariationMapping;
import meteor.plugins.banktags.BankTagsPlugin;
import meteor.util.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static meteor.plugins.banktaglayouts.BankTagLayoutsPlugin.CONFIG_GROUP;
import static meteor.plugins.banktags.BankTagsPlugin.ICON_SEARCH;
import static meteor.plugins.banktags.BankTagsPlugin.TAG_TABS_CONFIG;


/**
 * Class I made when I changed this plugin from using reflection to access private parts of Bank Tags.
 */
public class UsedToBeReflection {

    static final String ITEM_KEY_PREFIX = "item_";

    private final BankTagLayoutsPlugin plugin;

    public UsedToBeReflection(BankTagLayoutsPlugin plugin) {
        this.plugin = plugin;
    }

    void setIcon(final String tag, final String icon) {
        plugin.configManager.setConfiguration(CONFIG_GROUP, ICON_SEARCH + Text.standardize(tag), icon);
    }

    boolean findTag(int itemId, String bankTagName) {
        Collection<String> tags = getTags(itemId, false);
        tags.addAll(getTags(itemId, true));
        return tags.stream().anyMatch(tag -> tag.startsWith(Text.standardize(bankTagName)));
    }

    Collection<String> getTags(int itemId, boolean variation) {
        return new LinkedHashSet<>(Text.fromCSV(getTagString(itemId, variation).toLowerCase()));
    }

    String getTagString(int itemId, boolean variation) {
        itemId = getItemId(itemId, variation);

        String config = plugin.configManager.getConfiguration(CONFIG_GROUP, ITEM_KEY_PREFIX + itemId);
        if (config == null) {
            return "";
        }

        return config;
    }

    private int getItemId(int itemId, boolean variation) {
        itemId = Math.abs(itemId);
        itemId = plugin.itemManager.canonicalize(itemId);

        if (variation) {
            itemId = ItemVariationMapping.map(itemId) * -1;
        }

        return itemId;
    }

    public void saveNewTab(String newTabName) {
        List<String> tabs = new ArrayList<>(Text.fromCSV(plugin.configManager.getConfiguration(BankTagsPlugin.CONFIG_GROUP, TAG_TABS_CONFIG)));
        tabs.add(newTabName);
        String tags = Text.toCSV(tabs);
        plugin.configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, TAG_TABS_CONFIG, tags);
    }

    public void loadTab(String name) {
        plugin.configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, "useTabs", false);
        plugin.configManager.setConfiguration(BankTagsPlugin.CONFIG_GROUP, "useTabs", true);
    }

    public void openTag(String tag) {
    }

    public void scrollTab(int i) {
    }
}
