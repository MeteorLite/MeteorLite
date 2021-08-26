/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.config;

import static org.sponge.util.Logger.ANSI_GREEN;
import static org.sponge.util.Logger.ANSI_YELLOW;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.NonNull;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ClientShutdown;
import meteor.eventbus.events.ConfigChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.util.ColorUtil;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import org.sponge.util.Logger;
import org.sponge.util.Message;

@Singleton
public class ConfigManager {

  public static final String RSPROFILE_GROUP = "rsprofile";

  private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

  private static final int KEY_SPLITTER_GROUP = 0;
  private static final int KEY_SPLITTER_KEY = 1;

  private final File settingsFileInput;
  private final EventBus eventBus;
  private final ConfigInvocationHandler handler = new ConfigInvocationHandler(this);
  private final Map<String, Consumer<? super Plugin>> consumers = new HashMap<>();
  private final File propertiesFile;
  private final Logger log = new Logger("ConfigManager");
  private Properties properties = new Properties();
  private boolean loaded;
  private Client client;

  @Inject
  public ConfigManager(
      @Named("config") File config,
      ScheduledExecutorService scheduledExecutorService,
      EventBus eventBus,
      Client client) {
    this.settingsFileInput = config;
    this.eventBus = eventBus;
    this.propertiesFile = getPropertiesFile();
    this.client = client;

    scheduledExecutorService.scheduleWithFixedDelay(this::saveProperties, 30, 30, TimeUnit.SECONDS);
    this.eventBus.register(this);
  }

  public void saveProperties() {
    saveProperties(false);
  }

  public static String getWholeKey(String groupName, String key) {
    return groupName + "." + key;
  }

  static Object stringToObject(String str, Class<?> type) {
    if (type == boolean.class || type == Boolean.class) {
      return Boolean.parseBoolean(str);
    }
    if (type == int.class || type == Integer.class) {
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException e) {
        return 1;
      }
    }
    if (type == double.class || type == Double.class) {
      return Double.parseDouble(str);
    }
    if (type == Color.class) {
      return ColorUtil.fromString(str);
    }
    if (type == Dimension.class) {
      String[] splitStr = str.split("x");
      int width = Integer.parseInt(splitStr[0]);
      int height = Integer.parseInt(splitStr[1]);
      return new Dimension(width, height);
    }
    if (type == Point.class) {
      String[] splitStr = str.split(":");
      int width = Integer.parseInt(splitStr[0]);
      int height = Integer.parseInt(splitStr[1]);
      return new Point(width, height);
    }
    if (type == Rectangle.class) {
      String[] splitStr = str.split(":");
      int x = Integer.parseInt(splitStr[0]);
      int y = Integer.parseInt(splitStr[1]);
      int width = Integer.parseInt(splitStr[2]);
      int height = Integer.parseInt(splitStr[3]);
      return new Rectangle(x, y, width, height);
    }
    if (type.isEnum()) {
      return Enum.valueOf((Class<? extends Enum>) type, str);
    }
    if (type == Instant.class) {
      return Instant.parse(str);
    }
    if (type == Keybind.class || type == ModifierlessKeybind.class) {
      String[] splitStr = str.split(":");
      int code = Integer.parseInt(splitStr[0]);
      int mods = Integer.parseInt(splitStr[1]);
      if (type == ModifierlessKeybind.class) {
        return new ModifierlessKeybind(code, mods);
      }
      return new Keybind(code, mods);
    }
    if (type == WorldPoint.class) {
      String[] splitStr = str.split(":");
      int x = Integer.parseInt(splitStr[0]);
      int y = Integer.parseInt(splitStr[1]);
      int plane = Integer.parseInt(splitStr[2]);
      return new WorldPoint(x, y, plane);
    }
    if (type == Duration.class) {
      return Duration.ofMillis(Long.parseLong(str));
    }
    if (type == byte[].class) {
      return Base64.getUrlDecoder().decode(str);
    }
		/*
				if (type == EnumSet.class)
		{
			try
			{
				String substring = str.substring(str.indexOf("{") + 1, str.length() - 1);
				String[] splitStr = substring.split(", ");
				Class<? extends Enum> enumClass = null;
				if (!str.contains("{"))
				{
					return null;
				}

				enumClass = findEnumClass(str, OPRSExternalPluginManager.pluginClassLoaders);

				EnumSet enumSet = EnumSet.noneOf(enumClass);
				for (String s : splitStr)
				{
					try
					{
						enumSet.add(Enum.valueOf(enumClass, s.replace("[", "").replace("]", "")));
					}
					catch (IllegalArgumentException ignore)
					{
						return EnumSet.noneOf(enumClass);
					}
				}
				return enumSet;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		 */

    return str;
  }

  @Nullable
  static String objectToString(Object object) {
    if (object instanceof Color) {
      return String.valueOf(((Color) object).getRGB());
    }
    if (object instanceof Enum) {
      return ((Enum) object).name();
    }
    if (object instanceof Dimension) {
      Dimension d = (Dimension) object;
      return d.width + "x" + d.height;
    }
    if (object instanceof Point) {
      Point p = (Point) object;
      return p.x + ":" + p.y;
    }
    if (object instanceof Rectangle) {
      Rectangle r = (Rectangle) object;
      return r.x + ":" + r.y + ":" + r.width + ":" + r.height;
    }
    if (object instanceof Instant) {
      return ((Instant) object).toString();
    }
    if (object instanceof Keybind) {
      Keybind k = (Keybind) object;
      return k.getKeyCode() + ":" + k.getModifiers();
    }
    if (object instanceof WorldPoint) {
      WorldPoint wp = (WorldPoint) object;
      return wp.getX() + ":" + wp.getY() + ":" + wp.getPlane();
    }
    if (object instanceof Duration) {
      return Long.toString(((Duration) object).toMillis());
    }
    if (object instanceof byte[]) {
      return Base64.getUrlEncoder().encodeToString((byte[]) object);
    }
    if (object instanceof EnumSet) {
      if (((EnumSet) object).size() == 0) {
        return getElementType((EnumSet) object).getCanonicalName() + "{}";
      }

      return ((EnumSet) object).toArray()[0].getClass().getCanonicalName() + "{" + object
          + "}";
    }

    return object == null ? null : object.toString();
  }

  public static <T extends Enum<T>> Class<T> getElementType(EnumSet<T> enumSet) {
    if (enumSet.isEmpty()) {
      enumSet = EnumSet.complementOf(enumSet);
    }
    return enumSet.iterator().next().getDeclaringClass();
  }

  public static Class<? extends Enum> findEnumClass(String clasz,
      ArrayList<ClassLoader> classLoaders) {
    StringBuilder transformedString = new StringBuilder();
    for (ClassLoader cl : classLoaders) {
      try {
        String[] strings = clasz.substring(0, clasz.indexOf("{")).split("\\.");
        int i = 0;
        while (i != strings.length) {
          if (i == 0) {
            transformedString.append(strings[i]);
          } else if (i == strings.length - 1) {
            transformedString.append("$").append(strings[i]);
          } else {
            transformedString.append(".").append(strings[i]);
          }
          i++;
        }
        return (Class<? extends Enum>) cl.loadClass(transformedString.toString());
      } catch (Exception e2) {
        // Will likely fail a lot
      }
      try {
        return (Class<? extends Enum>) cl.loadClass(clasz.substring(0, clasz.indexOf("{")));
      } catch (Exception e) {
        // Will likely fail a lot
      }
      transformedString = new StringBuilder();
    }
    throw new RuntimeException("Failed to find Enum for " + clasz.substring(0, clasz.indexOf("{")));
  }

  /**
   * Split a config key into (group, profile, key)
   *
   * @param key in form group.(rsprofile.profile.)?key
   * @return an array of {group, profile, key}
   */
  @VisibleForTesting
  @Nullable
  static String[] splitKey(String key) {
    int i = key.indexOf('.');
    if (i == -1) {
      // all keys must have a group and key
      return null;
    }
    String group = key.substring(0, i);
    key = key.substring(i + 1);
    return new String[]{group, key};
  }

  private File getLocalPropertiesFile() {
    return settingsFileInput;
  }

  private File getPropertiesFile() {
    return getLocalPropertiesFile();
  }

  public void load() {
    loadFromFile();
  }

  private void swapProperties(Properties newProperties, boolean saveToServer) {
    Set<Object> newKeys = new HashSet<>(newProperties.keySet());
    Set<Object> oldKeys = new HashSet<>(properties.keySet());
    synchronized (this) {
      handler.invalidate();
    }

    for (Object wholeKey : oldKeys) {
      String[] split = splitKey((String) wholeKey);
      if (split == null) {
        continue;
      }

      String groupName = split[KEY_SPLITTER_GROUP];
      String key = split[KEY_SPLITTER_KEY];
      String newValue = (String) properties.get(wholeKey);
      setConfiguration(groupName, key, newValue);
    }

    for (Object wholeKey : newKeys) {
      String[] split = splitKey((String) wholeKey);
      if (split == null) {
        continue;
      }

      String groupName = split[KEY_SPLITTER_GROUP];
      String key = split[KEY_SPLITTER_KEY];
      String newValue = (String) newProperties.get(wholeKey);
      setConfiguration(groupName, key, newValue);
    }

    migrateConfig();
  }

  private synchronized void loadFromFile() {
    loaded = false;
    consumers.clear();

    Properties newProperties = new Properties();
    try (FileInputStream in = new FileInputStream(propertiesFile)) {
      newProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
      loaded = true;
    } catch (Exception e) {
      log.warn("No properties file found - Starting fresh config");
      loaded = false;
    }

    swapProperties(newProperties, false);
    if (!loaded) {
      saveProperties(true);
      loaded = true;
    }

    log.debug("Configuration loaded");
  }

  public void saveProperties(boolean forced) {
    try {
      if (loaded || forced)
      {
        File parent = propertiesFile.getParentFile();

        parent.mkdirs();

        File tempFile = File.createTempFile("runelite", null, parent);

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
          out.getChannel().lock();
          properties
              .store(new OutputStreamWriter(out, StandardCharsets.UTF_8), "RuneLite configuration");
          // FileOutputStream.close() closes the associated channel, which frees the lock
        }

        try {
          Files.move(tempFile.toPath(), propertiesFile.toPath(), StandardCopyOption.REPLACE_EXISTING,
              StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception ex) {
          Files.move(tempFile.toPath(), propertiesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } catch (Exception e) {
      //ignore
    }
  }

  public <T extends Config> T getConfig(Class<T> clazz) {
    if (!Modifier.isPublic(clazz.getModifiers())) {
      throw new RuntimeException(
          "Non-public configuration classes can't have default methods invoked");
    }

    T t = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]
        {
            clazz
        }, handler);

    return t;
  }

  public List<String> getConfigurationKeys(String prefix) {
    return properties.keySet().stream().filter(v -> ((String) v).startsWith(prefix))
        .map(String.class::cast).collect(Collectors.toList());
  }

  public String getConfiguration(String groupName, String key) {
    return getConfiguration(groupName, null, key);
  }

  public String getConfiguration(String groupName, String profile, String key) {
    return properties.getProperty(getWholeKey(groupName, key));
  }

  public <T> T getConfiguration(String groupName, String key, Class<T> clazz) {
    return getConfiguration(groupName, null, key, clazz);
  }

  public <T> T getConfiguration(String groupName, String profile, String key, Class<T> clazz) {
    String value = getConfiguration(groupName, profile, key);
    if (!Strings.isNullOrEmpty(value)) {
      try {
        return (T) stringToObject(value, clazz);
      } catch (Exception e) {
        e.printStackTrace();
        //log.warn("Unable to unmarshal {} ", getWholeKey(groupName, profile, key), e);
      }
    }
    return null;
  }

  public void setConfiguration(String groupName, String key, String value) {
    setConfiguration(groupName, null, key, value);
  }

  public void setConfiguration(String groupName, String profile, String key,
      @NonNull String value) {
    if (Strings.isNullOrEmpty(groupName) || Strings.isNullOrEmpty(key) || key.indexOf(':') != -1) {
      throw new IllegalArgumentException();
    }

    String wholeKey = getWholeKey(groupName, key);
    String oldValue;
    synchronized (this) {
      oldValue = (String) properties.setProperty(wholeKey, value);
    }

    log.debug("set config {}-{} ", wholeKey, value);
    handler.invalidate();

    ConfigChanged configChanged = new ConfigChanged();
    configChanged.setGroup(groupName);
    configChanged.setProfile(profile);
    configChanged.setKey(key);
    configChanged.setOldValue(oldValue);
    configChanged.setNewValue(value);

    eventBus.post(configChanged);

    if (client.getGameState() == GameState.LOGGED_IN)
    saveProperties();
  }

  public void setConfiguration(String groupName, String profile, String key, Object value) {
    setConfiguration(groupName, profile, key, objectToString(value));
  }

  public void setConfiguration(String groupName, String key, Object value) {
    // do not save consumers for buttons, they cannot be changed anyway
    if (value instanceof Consumer) {
      return;
    }

    setConfiguration(groupName, null, key, value);
  }

  public void unsetConfiguration(String groupName, String key) {
    unsetConfiguration(groupName, null, key);
  }

  public void unsetConfiguration(String groupName, String profile, String key) {
    assert !key.startsWith(RSPROFILE_GROUP + ".");
    String wholeKey = getWholeKey(groupName, key);
    String oldValue;
    synchronized (this) {
      oldValue = (String) properties.remove(wholeKey);
    }

    if (oldValue == null) {
      return;
    }

    //log.debug("Unsetting configuration value for {}", wholeKey);
    handler.invalidate();

    ConfigChanged configChanged = new ConfigChanged();
    configChanged.setGroup(groupName);
    configChanged.setProfile(profile);
    configChanged.setKey(key);
    configChanged.setOldValue(oldValue);

    eventBus.post(configChanged);
  }

  public ConfigDescriptor getConfigDescriptor(Config configurationProxy) {
    Class<?> inter = configurationProxy.getClass().getInterfaces()[0];
    ConfigGroup group = inter.getAnnotation(ConfigGroup.class);

    if (group == null) {
      throw new IllegalArgumentException("Not a config group");
    }

    final List<ConfigSectionDescriptor> sections = getAllDeclaredInterfaceFields(inter).stream()
        .filter(m -> m.isAnnotationPresent(ConfigSection.class) && m.getType() == String.class)
        .map(m ->
        {
          try {
            return new ConfigSectionDescriptor(
                String.valueOf(m.get(inter)),
                m.getDeclaredAnnotation(ConfigSection.class)
            );
          } catch (IllegalAccessException e) {
            //log.warn("Unable to load section {}::{}", inter.getSimpleName(), m.getName());
            return null;
          }
        })
        .filter(Objects::nonNull)
        .sorted((a, b) -> ComparisonChain.start()
            .compare(a.getSection().position(), b.getSection().position())
            .compare(a.getSection().name(), b.getSection().name())
            .result())
        .collect(Collectors.toList());

    final List<ConfigTitleDescriptor> titles = getAllDeclaredInterfaceFields(inter).stream()
        .filter(m -> m.isAnnotationPresent(ConfigTitle.class) && m.getType() == String.class)
        .map(m ->
        {
          try {
            return new ConfigTitleDescriptor(
                String.valueOf(m.get(inter)),
                m.getDeclaredAnnotation(ConfigTitle.class)
            );
          } catch (IllegalAccessException e) {
            //log.warn("Unable to load title {}::{}", inter.getSimpleName(), m.getName());
            return null;
          }
        })
        .filter(Objects::nonNull)
        .sorted((a, b) -> ComparisonChain.start()
            .compare(a.getTitle().position(), b.getTitle().position())
            .compare(a.getTitle().name(), b.getTitle().name())
            .result())
        .collect(Collectors.toList());

    final List<ConfigItemDescriptor> items = Arrays.stream(inter.getMethods())
        .filter(m -> m.getParameterCount() == 0 && m.isAnnotationPresent(ConfigItem.class))
        .map(m -> new ConfigItemDescriptor(
            m.getDeclaredAnnotation(ConfigItem.class),
            m.getReturnType(),
            m.getDeclaredAnnotation(Range.class),
            m.getDeclaredAnnotation(Alpha.class),
            m.getDeclaredAnnotation(Units.class),
            m.getDeclaredAnnotation(Icon.class)
        ))
        .sorted((a, b) -> ComparisonChain.start()
            .compare(a.getItem().position(), b.getItem().position())
            .compare(a.getItem().name(), b.getItem().name())
            .result())
        .collect(Collectors.toList());

    return new ConfigDescriptor(group, sections, titles, items);
  }

  /**
   * Initialize the configuration from the default settings
   *
   * @param config
   */
  public void setDefaultConfiguration(Plugin plugin, Object config, boolean override) {
    Class<?> clazz = config.getClass().getInterfaces()[0];
    ConfigGroup group = clazz.getAnnotation(ConfigGroup.class);

    if (group == null) {
      return;
    }
    if (getConfiguration(group.value(), "pluginEnabled") == null) {
      if (plugin.getClass().getAnnotation(PluginDescriptor.class) != null) {
        log.debug("should have set default");
        setConfiguration(group.value(), "pluginEnabled", plugin.getClass().getAnnotation(PluginDescriptor.class).enabledByDefault());
      }
    }

    for (Method method : getAllDeclaredInterfaceMethods(clazz)) {
      ConfigItem item = method.getAnnotation(ConfigItem.class);

      // only apply default configuration for methods which read configuration (0 args)
      if (item == null || method.getParameterCount() != 0) {
        continue;
      }

      if (method.getReturnType().isAssignableFrom(Consumer.class)) {
        Object defaultValue;
        try {
          defaultValue = ConfigInvocationHandler.callDefaultMethod(config, method, null);
        } catch (Throwable ex) {
          //log.warn(null, ex);
          continue;
        }

        //log.debug("Registered consumer: {}.{}", group.value(), item.keyName());
        consumers.put(group.value() + "." + item.keyName(), (Consumer) defaultValue);
      } else {
        if (!method.isDefault()) {
          if (override) {
            String current = getConfiguration(group.value(), item.keyName());
            // only unset if already set
            if (current != null) {
              unsetConfiguration(group.value(), item.keyName());
            }
          }
          continue;
        }

        if (!override) {
          // This checks if it is set and is also unmarshallable to the correct type; so
          // we will overwrite invalid config values with the default
          Object current = getConfiguration(group.value(), item.keyName(), method.getReturnType());
          if (current != null) {
            continue; // something else is already set
          }
        }

        Object defaultValue;
        try {
          defaultValue = ConfigInvocationHandler.callDefaultMethod(config, method, null);
        } catch (Throwable ex) {
          //log.warn(null, ex);
          continue;
        }

        String current = getConfiguration(group.value(), item.keyName());
        String valueString = objectToString(defaultValue);
        // null and the empty string are treated identically in sendConfig and treated as an unset
        // If a config value defaults to "" and the current value is null, it will cause an extra
        // unset to be sent, so treat them as equal
        if (Objects.equals(current, valueString) || (Strings.isNullOrEmpty(current) && Strings
            .isNullOrEmpty(valueString))) {
          continue; // already set to the default value
        }

        //log.debug("Setting default configuration value for {}.{} to {}", group.value(), item.keyName(), defaultValue);

        setConfiguration(group.value(), item.keyName(), valueString);
      }
    }
  }

  /**
   * Does DFS on a class's interfaces to find all of its implemented fields.
   */
  private Collection<Field> getAllDeclaredInterfaceFields(Class<?> clazz) {
    Collection<Field> methods = new HashSet<>();
    Stack<Class<?>> interfaces = new Stack<>();
    interfaces.push(clazz);

    while (!interfaces.isEmpty()) {
      Class<?> interfaze = interfaces.pop();
      Collections.addAll(methods, interfaze.getDeclaredFields());
      Collections.addAll(interfaces, interfaze.getInterfaces());
    }

    return methods;
  }

  /**
   * Does DFS on a class's interfaces to find all of its implemented methods.
   */
  private Collection<Method> getAllDeclaredInterfaceMethods(Class<?> clazz) {
    Collection<Method> methods = new HashSet<>();
    Stack<Class<?>> interfaces = new Stack<>();
    interfaces.push(clazz);

    while (!interfaces.isEmpty()) {
      Class<?> interfaze = interfaces.pop();
      Collections.addAll(methods, interfaze.getDeclaredMethods());
      Collections.addAll(interfaces, interfaze.getInterfaces());
    }

    return methods;
  }

  @Subscribe(priority = 100)
  private void onClientShutdown(ClientShutdown e) {
    saveProperties(false);
  }

  private synchronized void migrateConfig() {
    String migrationKey = "profileMigrationDone";
    if (getConfiguration("runelite", migrationKey) != null) {
      return;
    }

    AtomicInteger changes = new AtomicInteger();
    List<Predicate<String>> migrators = new ArrayList<>();
    for (String[] tpl : new String[][]
        {
            {"(grandexchange)\\.buylimit_(%)\\.(#)", "$1.buylimit.$3"},
            {"(timetracking)\\.(%)\\.(autoweed|contract)", "$1.$3"},
            {"(timetracking)\\.(%)\\.(#\\.#)", "$1.$3"},
            {"(timetracking)\\.(%)\\.(birdhouse)\\.(#)", "$1.$3.$4"},
            {"(killcount|personalbest)\\.(%)\\.([^.]+)", "$1.$3"},
            {"(geoffer)\\.(%)\\.(#)", "$1.$3"},
        }) {
      String replace = tpl[1];
      String pat = ("^" + tpl[0] + "$")
          .replace("#", "-?[0-9]+")
          .replace("(%)", "(?<login>.*)");
      Pattern p = Pattern.compile(pat);

      migrators.add(oldkey ->
      {
        Matcher m = p.matcher(oldkey);
        if (!m.find()) {
          return false;
        }

        String newKey = m.replaceFirst(replace);

        String[] oldKeySplit = splitKey(oldkey);
        if (oldKeySplit == null) {
          //log.warn("skipping migration of invalid key \"{}\"", oldkey);
          return false;
        }

        String[] newKeySplit = splitKey(newKey);
        if (newKeySplit == null) {
          //log.warn("migration produced a bad key: \"{}\" -> \"{}\"", oldkey, newKey);
          return false;
        }

        if (changes.getAndAdd(1) <= 0) {
          File file = new File(propertiesFile.getParent(),
              propertiesFile.getName() + "." + TIME_FORMAT.format(new Date()));
          //log.info("backing up pre-migration config to {}", file);
          saveProperties(false);
        }

        String oldGroup = oldKeySplit[KEY_SPLITTER_GROUP];
        String oldKeyPart = oldKeySplit[KEY_SPLITTER_KEY];
        String value = getConfiguration(oldGroup, oldKeyPart);
        setConfiguration(newKeySplit[KEY_SPLITTER_GROUP], newKeySplit[KEY_SPLITTER_KEY],
            value);
        unsetConfiguration(oldGroup, oldKeyPart);
        return true;
      });
    }

    ImmutableSet<Object> keys = ImmutableSet.copyOf(properties.keySet());
    keys:
    for (Object o : keys) {
      if (o instanceof String)
      for (Predicate<String> mig : migrators) {
        if (mig.test((String) o)) {
          continue keys;
        }
      }
    }

    if (changes.get() > 0) {
      //log.info("migrated {} config keys", changes);
    }
    setConfiguration("runelite", migrationKey, 1);
  }

  /**
   * Retrieves a consumer from config group and key name
   */
  public Consumer<? super Plugin> getConsumer(final String configGroup, final String keyName) {
    return consumers
        .getOrDefault(configGroup + "." + keyName, (p) -> log.error("Failed to retrieve consumer"));
  }
}
