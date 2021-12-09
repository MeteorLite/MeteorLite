package net.runelite.rs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.runelite.mapping.ObfuscatedName;

/*
  This must be ran every revision for Jagex Reflection checks. (must use vm arg -noverify)
 */
public class UpdateRefmap {

  public static void main(String[] args) throws Exception {
    Map<String, Class<?>> classMap = new HashMap<>();
    Path path = new File("./runescape-client/build/classes/java/main/osrs/")
        .toPath();

    Files.walk(path)
        .filter(Files::isRegularFile)
        .forEach(f ->
        {
          String className = f
              .getName(f.getNameCount() - 1)
              .toString()
              .replace(".class", "");

          try {
            String packageName = className.startsWith("JS") ? "osrs.javascript." : "osrs.";
            Class<?> clazz = Class.forName(packageName + className);

            ObfuscatedName obfuscatedName = clazz
                .getAnnotation(ObfuscatedName.class);

            if (obfuscatedName != null) {
              classMap.put(obfuscatedName.value(), clazz);
            }
          } catch (ClassNotFoundException ignore) {
            ignore.printStackTrace();
          }
        });
    HashMap<String, String> refmap = new HashMap<>();
    for (String s : classMap.keySet()) {
      refmap.put(s, classMap.get(s).getName());
    }
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    try (Writer writer = new FileWriter("./meteor-client/src/main/resources/osrs-refmap.json")) {
      gson.toJson(refmap, writer);
    }
    System.exit(0);
  }
}
