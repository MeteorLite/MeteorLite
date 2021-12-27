package com.owain.chinmanager.tasks;

import com.owain.chinmanager.ChinManager;
import com.owain.chinmanager.ChinManagerPlugin;
import com.owain.chinmanager.ChinManagerState;
import com.owain.chinmanager.ChinManagerStates;
import com.owain.chinmanager.utils.IntRandomNumberGenerator;
import com.owain.chintasks.Task;
import io.reactivex.rxjava3.core.ObservableEmitter;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.KEY_TYPED;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import meteor.callback.ClientThread;
import meteor.Logger;


public class SetupTask implements Task<Void>
{
	private final ChinManager chinManager;
	private final ChinManagerPlugin chinManagerPlugin;
	private final Client client;
	private final ClientThread clientThread;
	private Logger log = new Logger("Chin-SetupTask");

	@Inject
	SetupTask(ChinManager chinManager, ChinManagerPlugin chinManagerPlugin)
	{
		this.chinManager = chinManager;
		this.chinManagerPlugin = chinManagerPlugin;
		this.client = chinManagerPlugin.getClient();
		this.clientThread = chinManagerPlugin.getClientThread();
	}

	@Override
	public void routine(ObservableEmitter<Void> emitter)
	{
		chinManagerPlugin.getExecutorService().submit(() ->
		{
			ChinManagerPlugin.shouldSetup = false;

			try
			{
				keyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_UP);

				int j = 0;

				do
				{
					Thread.sleep(new IntRandomNumberGenerator(120, 240).nextInt());
					j++;
				}
				while (j < 10);

				keyEvent(KeyEvent.KEY_RELEASED, KeyEvent.VK_UP);

				clientThread.invoke(() -> {
					ChinManagerState.stateMachine.accept(ChinManagerStates.IDLE);
					if (client.getVar(VarClientInt.INVENTORY_TAB) != 3)
					{
						client.runScript(915, 3);
					}
					chinManager.setCurrentlyActive(chinManager.getActivePlugins().stream().findFirst().orElse(null));
				});
			}
			catch (InterruptedException e)
			{
				log.error("Oops!", e);
			}
		});
	}

	public void keyEvent(int id, int key)
	{
		KeyEvent e = new KeyEvent(
			client.getCanvas(),
			id,
			System.currentTimeMillis(),
			0,
			id == KEY_TYPED ? KeyEvent.VK_UNDEFINED : key,
			id == KEY_TYPED ? (char) key : KeyEvent.CHAR_UNDEFINED
		);

		client.getCanvas().dispatchEvent(e);
	}
}
