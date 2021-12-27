package meteor.plugins.playerindicatorsextended;

import com.google.inject.Provides;
import meteor.PluginManager;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.game.ChatIconManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.socket.packet.SocketMembersUpdate;
import meteor.plugins.socket.packet.SocketPlayerJoin;
import meteor.plugins.socket.packet.SocketShutdown;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import meteor.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Objects;

@PluginDescriptor(
        name = "Socket - Player Indicator",
        description = "Shows you players who are in your socket",
        tags = {"indicator, socket, player, highlight"},
        enabledByDefault = false
)
public class PlayerIndicatorsExtendedPlugin extends Plugin {
    private static final Logger log = Logger.Companion.getLogger(PlayerIndicatorsExtendedPlugin.class);

    @Inject
    private PlayerIndicatorsExtendedConfig config;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private PlayerIndicatorsExtendedOverlay overlay;

    @Inject
    private PlayerIndicatorsExtendedMinimapOverlay overlayMinimap;

    @Inject
    private ChatIconManager chatIconManager;

    private ArrayList<Actor> players;

    private ArrayList<String> names;

    @Provides
    public PlayerIndicatorsExtendedConfig getConfig(ConfigManager configManager) {
        return (PlayerIndicatorsExtendedConfig)configManager.getConfig(PlayerIndicatorsExtendedConfig.class);
    }

    public ArrayList<Actor> getPlayers() {
        return this.players;
    }

    int activeTick = 0;

    boolean cleared = false;

    public void startup() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.overlayMinimap);
        this.players = new ArrayList<>();
        this.names = new ArrayList<>();
    }

    public void shutdown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.overlayMinimap);
    }

    @Subscribe
    public void onSocketPlayerJoin(SocketPlayerJoin event) {
        this.names.add(event.getPlayerName());
        if (event.getPlayerName().equals(Objects.requireNonNull(this.client.getLocalPlayer()).getName()))
            this.names.clear();
    }

    @Subscribe
    public void onSocketMembersUpdate(SocketMembersUpdate event)
    {
        this.names.clear();
        for(String s : event.getMembers())
        {
            if(!s.equals(client.getLocalPlayer().getName()))
            {
                names.add(s);
            }
        }
    }

    @Subscribe
    public void onSocketShutdown(SocketShutdown event)
    {
        names.clear();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        players.clear();
        loop: for(Player p : client.getPlayers())
        {
            for(String name : names)
            {
                if(name.equals(p.getName()))
                {
                    players.add(p);
                    continue loop;
                }
            }
        }
    }
}
