package dev.hoot.api.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import meteor.game.ItemManager;
import org.jetbrains.annotations.NotNull;
import org.sponge.util.Logger;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class Prices {
	private static final Logger logger = new Logger("Prices");
	@Inject
	private static ItemManager itemManager;

	private static final LoadingCache<Integer, Integer> CACHE = CacheBuilder.newBuilder()
					.expireAfterWrite(Duration.ofMinutes(5))
					.build(new CacheLoader<>() {
						@Override
						public Integer load(@NotNull Integer itemId) {
							logger.debug("Caching item {} price", itemId);
							return GameThread.invokeLater(() -> itemManager.getItemPrice(itemId));
						}
					});

	public static int getItemPrice(int id) {
		try {
			return CACHE.get(id);
		} catch (ExecutionException e) {
			return -1;
		}
	}
}
