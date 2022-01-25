package net.runelite.mixins;

import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSNanoClock;

@Mixin(RSNanoClock.class)
public abstract class NanoClockMixin implements RSNanoClock {
	@Shadow("client")
	private static RSClient client;

	@Inject
	private long tmpNanoTime;

	@Copy("wait")
	@Replace("wait")
	public int copy$wait(int cycleDurationMillis, int var2) {
		if (!client.isUnlockedFps()) {
			return copy$wait(cycleDurationMillis, var2);
		} else {
			long nanoTime = System.nanoTime();

			if (nanoTime >= getLastTimeNano() && nanoTime >= tmpNanoTime) {
				long cycleDuration;
				long diff;

				if (client.getUnlockedFpsTarget() > 0L) {
					cycleDuration = nanoTime - tmpNanoTime;
					diff = client.getUnlockedFpsTarget() - cycleDuration;
					diff /= 1000000L;
					if (diff > 0L) {
						try {
							if (diff % 10L == 0L) {
								Thread.sleep(diff - 1L);
								Thread.sleep(1L);
							} else {
								Thread.sleep(diff);
							}
						} catch (InterruptedException var22) {
						}

						nanoTime = System.nanoTime();
					}
				}

				tmpNanoTime = nanoTime;

				cycleDuration = (long) cycleDurationMillis * 1000000L;
				diff = nanoTime - getLastTimeNano();

				int cycles = (int) (diff / cycleDuration);

				setLastTimeNano(getLastTimeNano() + (long) cycles * cycleDuration);

				if (cycles > 10) {
					cycles = 10;
				}

				return cycles;
			} else {
				setLastTimeNano(tmpNanoTime = nanoTime);

				return 1;
			}
		}
	}
}
