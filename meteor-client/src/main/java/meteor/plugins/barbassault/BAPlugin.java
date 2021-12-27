// 
// Decompiled by Procyon v0.5.36
// 

package meteor.plugins.barbassault;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.barbassault.overlays.BAItemOverlay;
import meteor.plugins.barbassault.overlays.BASceneOverlay;
import meteor.plugins.barbassault.util.Role;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayManager;
import meteor.util.Text;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import meteor.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(name = "BA Additions", description = "", tags = { "barbarian", "assault", "barb", "ba", "attacker", "defender", "collector", "healer", "zhuri/nicole" }, enabledByDefault = false)
public class BAPlugin extends Plugin
{
    private static final Logger log = Logger.Companion.getLogger(BAPlugin.class);
    @Inject
    private Client client;
    @Inject
    private BAItemOverlay itemOverlay;
    @Inject
    private BASceneOverlay sceneOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BAConfig config;
    private int inGame;
    private Role role;
    private final Map<WorldPoint, Integer> redEggs;
    private final Map<WorldPoint, Integer> greenEggs;
    private final Map<WorldPoint, Integer> blueEggs;
    private final Map<WorldPoint, Integer> yellowEggs;
    
    public BAPlugin() {
        this.inGame = 0;
        this.role = null;
        this.redEggs = new HashMap<WorldPoint, Integer>();
        this.greenEggs = new HashMap<WorldPoint, Integer>();
        this.blueEggs = new HashMap<WorldPoint, Integer>();
        this.yellowEggs = new HashMap<WorldPoint, Integer>();
    }
    
    @Provides
    public BAConfig getConfig(final ConfigManager configManager) {
        return (BAConfig)configManager.getConfig((Class)BAConfig.class);
    }
    
    public void startup() {
        this.overlayManager.add((Overlay)this.itemOverlay);
        this.overlayManager.add((Overlay)this.sceneOverlay);
    }
    
    public void shutdown() {
        this.overlayManager.remove((Overlay)this.itemOverlay);
        this.overlayManager.remove((Overlay)this.sceneOverlay);
        this.reset();
    }
    
    private void reset() {
        this.inGame = 0;
        this.role = null;
        this.clearEggMaps();
    }
    
    @Subscribe
    private void onVarbitChanged(final VarbitChanged e) {
        final int inGameVarb = this.client.getVar(Varbits.IN_GAME_BA);
        if (this.inGame != inGameVarb && (this.inGame = inGameVarb) == 0) {
            this.reset();
        }
    }
    
    @Subscribe
    private void onWidgetLoaded(final WidgetLoaded e) {
        switch (e.getGroupId()) {
            case 485: {
                this.role = Role.ATTACKER;
                break;
            }
            case 487: {
                this.role = Role.DEFENDER;
                break;
            }
            case 488: {
                this.role = Role.HEALER;
                break;
            }
            case 486: {
                this.role = Role.COLLECTOR;
                break;
            }
        }
    }
    
    @Subscribe
    private void onMenuEntryAdded(final MenuEntryAdded e) {
        if (this.inGame == 0 || this.role == null) {
            return;
        }
        final String target = e.getTarget();
        final String option = e.getOption();
        if (!this.config.shouldLeftClickCall() || !target.endsWith("horn")) {
            final List<MenuEntry> menu = new ArrayList<MenuEntry>();
            final List<MenuEntry> selected = new ArrayList<MenuEntry>();
            final List<MenuEntry> prio = new ArrayList<MenuEntry>();
            MenuEntry walk_here = null;
            boolean prioWalk = false;
            final MenuEntry[] menuEntries;
            final MenuEntry[] entries = menuEntries = this.client.getMenuEntries();
            for (final MenuEntry entry : menuEntries) {
                final String targ = Text.removeTags(entry.getTarget()).toLowerCase();
                final String opt = Text.removeTags(entry.getOption()).toLowerCase();
                Label_0590: {
                    if (opt.equals("walk here")) {
                        walk_here = entry;
                    }
                    else {
                        switch (this.role) {
                            case DEFENDER: {
                                if (!opt.equals("take")) {
                                    break;
                                }
                                if (targ.equals("logs") || targ.equals("hammer")) {
                                    prio.add(entry);
                                    break Label_0590;
                                }
                                if (this.config.shouldDeprioBait() && (targ.equals("tofu") || targ.equals("crackers") || targ.equals("worms"))) {
                                    prioWalk = true;
                                    break;
                                }
                                break;
                            }
                            case COLLECTOR: {
                                if (!opt.equals("take")) {
                                    break;
                                }
                                if (targ.equals("yellow egg")) {
                                    prio.add(entry);
                                    break Label_0590;
                                }
                                if (!this.config.shouldDeprioIncEggs() || (!targ.equals("blue egg") && !targ.equals("green egg") && !targ.equals("red egg"))) {
                                    break;
                                }
                                prioWalk = true;
                                final String listen = this.role.getListen(this.client);
                                if (!Strings.isNullOrEmpty(listen) && listen.substring(0, listen.length() - 1).toLowerCase().equals(targ)) {
                                    selected.add(entry);
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    menu.add(entry);
                }
            }
            if (prioWalk && walk_here != null) {
                menu.remove(walk_here);
                menu.add(walk_here);
            }
            if (!selected.isEmpty()) {
                menu.addAll(selected);
            }
            if (!prio.isEmpty()) {
                menu.addAll(prio);
            }
            try {
                this.client.setMenuEntries((MenuEntry[])menu.toArray(new MenuEntry[0]));
            }
            catch (Exception ex) {
                BAPlugin.log.debug("error setting entries: {}", (Object)ex.getMessage());
            }
            return;
        }
        final String find = this.role.getCallEntry(this.client);
        if (Strings.isNullOrEmpty(find)) {
            return;
        }
        final List<MenuEntry> keep = new ArrayList<MenuEntry>();
        final MenuEntry[] entries2 = this.client.getMenuEntries();
        MenuEntry call = null;
        for (final MenuEntry entry2 : entries2) {
            final String opt2 = entry2.getOption();
            if (opt2.equals(find)) {
                call = entry2;
            }
            else if (!opt2.startsWith("Tell-")) {
                keep.add(entry2);
            }
        }
        if (call != null) {
            keep.add(call);
            this.client.setMenuEntries((MenuEntry[])keep.toArray(new MenuEntry[0]));
        }
    }
    
    @Subscribe
    private void onItemSpawned(final ItemSpawned e) {
        if (this.inGame == 0) {
            return;
        }
        final Map<WorldPoint, Integer> map = this.getEggMap(e.getItem().getId());
        if (map != null) {
            final WorldPoint wp = e.getTile().getWorldLocation();
            final Integer exists = map.putIfAbsent(wp, 1);
            if (exists != null) {
                map.put(wp, exists + 1);
            }
        }
    }
    
    @Subscribe
    private void onItemDespawned(final ItemDespawned e) {
        if (this.inGame == 0) {
            return;
        }
        final int id = e.getItem().getId();
        if (!this.isItemEgg(id)) {
            return;
        }
        final Map<WorldPoint, Integer> map = this.getEggMap(id);
        if (map != null) {
            final WorldPoint wp = e.getTile().getWorldLocation();
            if (map.containsKey(wp)) {
                final int quantity = map.get(wp);
                if (quantity > 1) {
                    map.put(wp, quantity - 1);
                }
                else {
                    map.remove(wp);
                }
            }
        }
    }
    
    private void clearEggMaps() {
        this.greenEggs.clear();
        this.redEggs.clear();
        this.blueEggs.clear();
        this.yellowEggs.clear();
    }
    
    private Map<WorldPoint, Integer> getEggMap(final int id) {
        switch (id) {
            case 10531: {
                return this.greenEggs;
            }
            case 10532: {
                return this.redEggs;
            }
            case 10533: {
                return this.blueEggs;
            }
            case 10534: {
                return this.yellowEggs;
            }
            default: {
                return null;
            }
        }
    }
    
    private boolean isItemEgg(final int id) {
        return id == 10532 || id == 10531 || id == 10533 || id == 10534;
    }
    
    public int getInGame() {
        return this.inGame;
    }
    
    public Role getRole() {
        return this.role;
    }
    
    public Map<WorldPoint, Integer> getRedEggs() {
        return this.redEggs;
    }
    
    public Map<WorldPoint, Integer> getGreenEggs() {
        return this.greenEggs;
    }
    
    public Map<WorldPoint, Integer> getBlueEggs() {
        return this.blueEggs;
    }
    
    public Map<WorldPoint, Integer> getYellowEggs() {
        return this.yellowEggs;
    }
}
