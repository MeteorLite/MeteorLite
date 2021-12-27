package meteor.plugins.socket;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.socket.packet.*;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import meteor.plugins.socket.hash.AES256;
import meteor.plugins.socket.org.json.JSONObject;
import meteor.Logger;

import javax.inject.Inject;
import java.io.PrintWriter;

@PluginDescriptor(
        name = "Socket",
        description = "Socket connection for broadcasting messages across clients.",
        tags = {"socket", "server", "discord", "connection", "broadcast"},
        enabledByDefault = false
)

public class SocketPlugin extends Plugin {

    private static final Logger log = Logger.getLogger(SocketPlugin.class);
    // Config version changes between updates, hence we use a global variable.
    public static final String CONFIG_VERSION = "Socket Plugin v2.1.0";

    // To help users who decide to use weak passwords.
    public static final String PASSWORD_SALT = "$P@_/gKR`y:mv)6K";

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private Client client;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private EventBus eventBus;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private ClientThread clientThread;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private SocketConfig config;

    @Provides
    public SocketConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketConfig.class);
    }

    // This variables controls the next UNIX epoch time to establish the next connection.
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private long nextConnection;

    // This variables controls the current active connection.
    private SocketConnection connection = null;

    @Override
    public void startup()
    {
        this.nextConnection = 0L;

        eventBus.register(SocketReceivePacket.class);
        eventBus.register(SocketBroadcastPacket.class);

        eventBus.register(SocketPlayerJoin.class);
        eventBus.register(SocketPlayerLeave.class);

        eventBus.register(SocketStartup.class);
        eventBus.register(SocketShutdown.class);

        eventBus.post(new SocketStartup());
    }

    @Override
    public void shutdown()
    {
        eventBus.post(new SocketShutdown());

        eventBus.unregister(SocketReceivePacket.class);
        eventBus.unregister(SocketBroadcastPacket.class);

        eventBus.unregister(SocketPlayerJoin.class);
        eventBus.unregister(SocketPlayerLeave.class);

        eventBus.unregister(SocketStartup.class);
        eventBus.unregister(SocketShutdown.class);

        if (this.connection != null)
            this.connection.terminate(true);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Attempt connecting, or re-establishing connection to the socket server, only when the user is logged in.
        if (client.getGameState() == GameState.LOGGED_IN) {
            if (this.connection != null) { // If an connection is already being established, ignore.
                SocketState state = this.connection.getState();
                if (state == SocketState.CONNECTING || state == SocketState.CONNECTED)
                    return;
            }

            if (System.currentTimeMillis() >= this.nextConnection) { // Create a new connection.
                this.nextConnection = System.currentTimeMillis() + 30000L;
                this.connection = new SocketConnection(this, this.client.getLocalPlayer().getName());
                new Thread(this.connection).start(); // Handler blocks, so run it on a separate thread.
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        // Notify the user to restart the plugin when the config changes.
        if (event.getGroup().equals(CONFIG_VERSION))
            this.clientThread.invoke(() -> this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=b4281e>Configuration changed. Please restart the plugin to see updates.", null));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        // Terminate all connections to the socket server when the user logs out.
        if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            if (this.connection != null)
            {
                this.connection.terminate(false);
            }
        }
    }

    @Subscribe
    public void onSocketBroadcastPacket(SocketBroadcastPacket packet)
    {
        try
        {
            // Handles the packets that alternative plugins broadcasts.
            if (this.connection == null || this.connection.getState() != SocketState.CONNECTED)
                return;

            String data = packet.getPayload().toString();
            log.debug("Deploying packet from client: {}", data);

            String secret = this.config.getPassword() + PASSWORD_SALT;

            JSONObject payload = new JSONObject();
            payload.put("header", SocketPacket.BROADCAST);
            payload.put("payload", AES256.encrypt(secret, data)); // Payload is now an encrypted string.

            PrintWriter outputStream = this.connection.getOutputStream();
            synchronized (outputStream)
            {
                outputStream.println(payload.toString());
            }
        } catch (Exception e) { // Oh no, something went wrong!
            e.printStackTrace();
            log.error("An error has occurred while trying to broadcast a packet.", e);
        }
    }
}
