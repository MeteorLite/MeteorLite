package meteor.plugins.resourcepacks;

import com.google.inject.Provides;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.inject.Inject;
import lombok.Setter;
import meteor.MeteorLiteClientLauncher;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.config.MeteorLiteConfig;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import okhttp3.HttpUrl;
import org.apache.commons.io.FileUtils;
import meteor.Logger;

@PluginDescriptor(
		name = "Resource Packs",
		description = "Change the look of the client"
)
public class ResourcePacksPlugin extends Plugin
{
	public static final File RESOURCEPACKS_DIR = new File(
			MeteorLiteClientLauncher.METEOR_DIR.getPath() + File.separator + "resource-packs-repository");
	public static final File NOTICE_FILE = new File(RESOURCEPACKS_DIR.getPath() + File.separator + "DO_NOT_EDIT_CHANGES_WILL_BE_OVERWRITTEN");
	public static final String BRANCH = "github-actions";
	public static final String OVERLAY_COLOR_CONFIG = "overlayBackgroundColor";
	public static final HttpUrl GITHUB = HttpUrl.parse("https://github.com/melkypie/resource-packs");
	public static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/melkypie/resource-packs");
	public static final HttpUrl API_GITHUB = HttpUrl.parse("https://api.github.com/repos/melkypie/resource-packs");

	@Setter
	private static boolean ignoreOverlayConfig = false;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ResourcePacksConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ResourcePacksManager resourcePacksManager;

	@Inject
	private ScheduledExecutorService executor;


	@Provides
	public ResourcePacksConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ResourcePacksConfig.class);
	}

	@Override
	public void startup()
	{
		if (!RESOURCEPACKS_DIR.exists())
		{
			RESOURCEPACKS_DIR.mkdirs();
			unpackMeteorLiteTheme();
		}

		if (!NOTICE_FILE.exists())
		{
			try {
				NOTICE_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		executor.submit(() -> {
			resourcePacksManager.refreshPlugins();
			clientThread.invokeLater(resourcePacksManager::updateAllOverrides);
		});
	}

	@Override
	public void shutdown()
	{
		clientThread.invokeLater(() ->
		{
			resourcePacksManager.adjustWidgetDimensions(false);
			resourcePacksManager.removeGameframe();
			resourcePacksManager.resetWidgetOverrides();
			resourcePacksManager.resetCrossSprites();
		});
		if (config.allowLoginScreen())
		{
			resourcePacksManager.resetLoginScreen();
		}
		if (config.allowOverlayColor())
		{
			resourcePacksManager.resetOverlayColor();
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		resourcePacksManager.adjustWidgetDimensions(true);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(ResourcePacksConfig.GROUP_NAME))
		{
			switch (event.getKey())
			{
				case "allowSpellsPrayers":
				case "allowColorPack":
				case "colorPackOverlay":
				case "colorPack":
				case "resourcePack":
					clientThread.invokeLater(resourcePacksManager::updateAllOverrides);
					break;
				case "allowOverlayColor":
					if (config.allowOverlayColor())
					{
						clientThread.invokeLater(resourcePacksManager::updateAllOverrides);
					}
					else
					{
						resourcePacksManager.resetOverlayColor();
					}
					break;
				case "allowCrossSprites":
					if (config.allowCrossSprites())
					{
						clientThread.invokeLater(resourcePacksManager::changeCrossSprites);
					}
					else
					{
						resourcePacksManager.resetCrossSprites();
					}
					break;
				case "allowLoginScreen":
					if (config.allowLoginScreen())
					{
						clientThread.invokeLater(resourcePacksManager::updateAllOverrides);
					}
					else
					{
						resourcePacksManager.resetLoginScreen();
					}
					break;
			}
		}
		else if (event.getGroup().equals("banktags") && event.getKey().equals("useTabs"))
		{
			clientThread.invoke(resourcePacksManager::reloadBankTagSprites);
		}
		else if (config.allowOverlayColor() && !ignoreOverlayConfig &&
			event.getGroup().equals(MeteorLiteConfig.GROUP_NAME) && event.getKey().equals(OVERLAY_COLOR_CONFIG))
		{
			configManager.setConfiguration(ResourcePacksConfig.GROUP_NAME, ResourcePacksConfig.ORIGINAL_OVERLAY_COLOR,
				event.getNewValue());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGIN_SCREEN)
		{
			return;
		}

		resourcePacksManager.changeCrossSprites();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (!resourcePacksManager.getColorProperties().isEmpty() && WidgetOverride.scriptWidgetOverrides.containsKey(event.getScriptId()))
		{
			for (WidgetOverride widgetOverride : WidgetOverride.scriptWidgetOverrides.get(event.getScriptId()))
			{
				resourcePacksManager.addPropertyToWidget(widgetOverride);
			}
		}
	}

	private void unpackMeteorLiteTheme() {
		Logger log = Logger.getLogger(getClass());
		log.info("Unpacking theme");
		File themeZip = new File(MeteorLiteClientLauncher.METEOR_DIR, "theme.zip");
		File themeDir = new File(RESOURCEPACKS_DIR, "MeteorLite");
		try {
			FileUtils.copyInputStreamToFile(ClassLoader.getSystemClassLoader().getResourceAsStream("MeteorLite-Theme.zip"), themeZip);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try(ZipFile file = new ZipFile(themeZip))
		{
			FileSystem fileSystem = FileSystems.getDefault();
			Enumeration<? extends ZipEntry> entries = file.entries();
			Files.createDirectory(themeDir.toPath());
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory())
				{
					Files.createDirectories(fileSystem.getPath(RESOURCEPACKS_DIR + "/" + entry.getName()));
				}
				else
				{
					InputStream is = file.getInputStream(entry);
					BufferedInputStream bis = new BufferedInputStream(is);
					String uncompressedFileName = RESOURCEPACKS_DIR + "/" + entry.getName();
					Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
					Files.createFile(uncompressedFilePath);
					FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
					while (bis.available() > 0)
					{
						fileOutput.write(bis.read());
					}
					fileOutput.close();
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		log.info("Theme unpacked successfully");
	}
}
