package net.runelite.deob.updater;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.runelite.asm.ClassFile;
import net.runelite.asm.ClassGroup;
import net.runelite.asm.pool.Field;
import net.runelite.asm.pool.Method;
import net.runelite.deob.DeobProperties;
import net.runelite.deob.updater.mappingdumper.MappedClass;
import net.runelite.deob.updater.mappingdumper.MappedField;
import net.runelite.deob.updater.mappingdumper.MappedMethod;
import net.runelite.deob.updater.mappingdumper.MappingDump;

public class MappingDumper {

  private static final Map<ClassFile, MappedClass> classMap = new HashMap<>();
  private static final Map<Field, MappedField> fieldMap = new HashMap<>();
  private static final Map<Method, MappedMethod> methodMap = new HashMap<>();
  private static ClassGroup group;

  public MappingDumper(ClassGroup group) {
    MappingDumper.group = group;
  }

  public static ClassGroup getGroup() {
    return group;
  }

  public static void putMap(ClassFile clazz, MappedClass mc) {
    classMap.put(clazz, mc);
  }

  public static MappedClass getMap(ClassFile clazz) {
    return classMap.get(clazz);
  }

  public static void putMap(Field field, MappedField mf) {
    fieldMap.put(field, mf);
  }

  public static MappedField getMap(Field field) {
    return fieldMap.get(field);
  }

  public static void putMap(Method method, MappedMethod mm) {
    methodMap.put(method, mm);
  }

  public static MappedMethod getMap(Method method) {
    return methodMap.get(method);
  }

  public void dump(final File outputFile) {
    Stopwatch st = Stopwatch.createStarted();
    group.buildClassGraph();

    // MappingDump.of(ClassGroup) dumps everything completely
    final MappingDump dump = new MappingDump().visitGroup(group);
    dump.revision = Integer.parseInt(DeobProperties.getRevision());

    writeJson(dump, outputFile);
  }

  // Without this stack'll overflow :P
  private void writeJson(MappingDump dump, File outputFile) {
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    try (JsonWriter writer = new JsonWriter(
        new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
      writer.setIndent("  ");
      writer.beginObject();
      writer.name("revision").value(dump.revision);
      writer.name("totalClasses").value(dump.totalClasses);
      writer.name("totalNamedClasses").value(dump.totalNamedClasses);
      writer.name("totalFields").value(dump.totalFields);
      writer.name("totalNamedFields").value(dump.totalNamedFields);
      writer.name("totalNonStaticFields").value(dump.totalNonStaticFields);
      writer.name("totalNamedNonStaticFields").value(dump.totalNamedNonStaticFields);
      writer.name("totalStaticFields").value(dump.totalStaticFields);
      writer.name("totalNamedStaticFields").value(dump.totalNamedStaticFields);
      writer.name("totalMethods").value(dump.totalMethods);
      writer.name("totalNamedMethods").value(dump.totalNamedMethods);
      writer.name("totalNonStaticMethods").value(dump.totalNonStaticMethods);
      writer.name("totalNamedNonStaticMethods").value(dump.totalNamedNonStaticMethods);
      writer.name("totalStaticMethods").value(dump.totalStaticMethods);
      writer.name("totalNamedStaticMethods").value(dump.totalNamedStaticMethods);
      writer.name("mappedClasses");
      writer.beginArray();
      for (MappedClass mc : dump.classes) {
        gson.toJson(mc, MappedClass.class, writer);
      }
      writer.endArray();
      writer.endObject();
    } catch (IOException e) {
    }
  }

}
