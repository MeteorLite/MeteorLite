package dev.hoot.api.game;

import meteor.callback.ClientThread;
import net.runelite.api.Client;
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.util.concurrent.*;

public class GameThread {
    private static final Logger log = new Logger("GameThread");
    private static final long TIMEOUT = 1000;
    @Inject
    private static ClientThread clientThread;

    @Inject
    private static Client client;

    public static void invoke(Runnable runnable) {
        if (client.isClientThread()) {
            runnable.run();
        } else {
            clientThread.invokeLater(runnable);
        }
    }

    public static <T> T invokeLater(Callable<T> callable) {
        if (client.isClientThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FutureTask<T> futureTask = new FutureTask<>(callable);
            clientThread.invokeLater(futureTask);
            return futureTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException("Lookup on client thread timed out after " + TIMEOUT + " ms");
        }
    }
}

