/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.java.decompiler.main.decompiler;

import static meteor.Logger.ANSI_YELLOW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.util.InterpreterUtil;
import meteor.Logger;
import meteor.Message;

public class ConsoleDecompiler implements IBytecodeProvider, IResultSaver {

  static Logger logger = Logger.Companion.getLogger(ConsoleDecompiler.class);
  private final File root;
  private final Fernflower fernflower;

  // *******************************************************************
  // Implementation
  // *******************************************************************
  private final Map<String, ZipOutputStream> mapArchiveStreams = new HashMap<>();
  private final Map<String, Set<String>> mapArchiveEntries = new HashMap<>();
  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public ConsoleDecompiler(File destination, Map<String, Object> options) {
    this(destination, options, new PrintStreamLogger(System.out));
  }
  protected ConsoleDecompiler(File destination, Map<String, Object> options,
      IFernflowerLogger logger) {
    root = destination;
    fernflower = new Fernflower(this, this, options, logger);
  }

  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println(
          "Usage: java -jar fernflower.jar [-<option>=<value>]* [<source>]+ <destination>\n" +
              "Example: java -jar fernflower.jar -dgs=true c:\\my\\source\\ c:\\my.jar d:\\decompiled\\");
      return;
    }

    Map<String, Object> mapOptions = new HashMap<>();
    List<File> lstSources = new ArrayList<>();
    List<File> lstLibraries = new ArrayList<>();

    boolean isOption = true;
    for (int i = 0; i < args.length - 1; ++i) { // last parameter - destination
      String arg = args[i];

      if (isOption && arg.length() > 5 && arg.charAt(0) == '-' && arg.charAt(4) == '=') {
        String value = arg.substring(5);
        if ("true".equalsIgnoreCase(value)) {
          value = "1";
        } else if ("false".equalsIgnoreCase(value)) {
          value = "0";
        }

        mapOptions.put(arg.substring(1, 4), value);
      } else {
        isOption = false;

        if (arg.startsWith("-e=")) {
          addPath(lstLibraries, arg.substring(3));
        } else {
          addPath(lstSources, arg);
        }
      }
    }

    if (lstSources.isEmpty()) {
      System.out.println("error: no sources given");
      return;
    }

    File destination = new File(args[args.length - 1]);
    if (!destination.exists()) {
      destination.mkdirs();
    }
    if (!destination.isDirectory()) {
      System.out.println("error: destination '" + destination + "' is not a directory");
      return;
    }

    PrintStreamLogger logger2 = new PrintStreamLogger(System.out);
    ConsoleDecompiler decompiler = new ConsoleDecompiler(destination, mapOptions, logger2);

    for (File source : lstSources) {
      decompiler.addSpace(source, true);
    }
    for (File library : lstLibraries) {
      decompiler.addSpace(library, false);
    }

    decompiler.decompileContext();

    logger.info(Message.Companion.newMessage().add(ANSI_YELLOW, "[Extracting injected-client]").build());
    File jarFile = new File("./build/decompiled/injected-client.jar");
    String srcDest = "../injected-client/src/main/java/";
    File rsDir = new File("../injected-client/src/main/java/net/runelite/rs/");
    File rogueInjected = new File("../injected-client/src/main/java/injected-client.jar");
    File destFolder = new File(srcDest);
    destFolder.mkdirs();
    rsDir.mkdirs();
    new File("../injected-client/src/main/java/osrs/").mkdirs();
    java.util.jar.JarFile jar = null;
    try {
      jar = new java.util.jar.JarFile(jarFile);
      java.util.Enumeration enumEntries = jar.entries();
      while (enumEntries.hasMoreElements()) {
        java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
        java.io.File f = new java.io.File(srcDest + java.io.File.separator + file.getName());
        if (file.isDirectory()) { // if its a directory, create it
          continue;
        }
        InputStream is = null; // get the input stream
        try {
          is = jar.getInputStream(file);
          java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
          while (is.available() > 0) {  // write contents of 'is' to 'fos'
            fos.write(is.read());
          }
          fos.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
      try {
        jar.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    rogueInjected.delete();

    File engineFile = new File("../injected-client/src/main/java/GameEngine.java");

    ArrayList<String> engineLines = new ArrayList<>();
    try {
      BufferedReader br = Files.newBufferedReader(engineFile.toPath(), StandardCharsets.UTF_8);

      String st;
      while ((st = br.readLine()) != null) {
        if (st.contains("final void resizeCanvas() {")) {
          engineLines.add("public void resizeCanvas() {");
          logger.info(Message.Companion.newMessage()
                          .add(ANSI_YELLOW, "[Patched GameEngine resizeCanvas() access]")
              .build());
        } else {
          engineLines.add(st);
        }
      }
      BufferedWriter out = new BufferedWriter
          (new OutputStreamWriter(new FileOutputStream(engineFile), StandardCharsets.UTF_8));
      engineFile.delete();
      for (String s : engineLines) {
        out.write(s);
        out.newLine();
      }
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    File bufferedSinkFile = new File("../injected-client/src/main/java/BufferedSink.java");

    ArrayList<String> bufferedSinkLines = new ArrayList<>();
    try {
      BufferedReader br = Files
          .newBufferedReader(bufferedSinkFile.toPath(), StandardCharsets.UTF_8);

      String st;
      while ((st = br.readLine()) != null) {
        if (st.contains("} catch (InterruptedException var18) {")) {
          bufferedSinkLines.add("} catch (Exception var18) {");
          logger.info(Message.Companion.newMessage()
                          .add(ANSI_YELLOW, "[Patched BufferedSink run() Exception runtime crash]")
              .build());
        } else if (st.contains("this.wait();")) {
          bufferedSinkLines.add("wait();");
        } else {
          bufferedSinkLines.add(st);
        }
      }
      BufferedWriter out = new BufferedWriter
          (new OutputStreamWriter(new FileOutputStream(bufferedSinkFile), StandardCharsets.UTF_8));
      bufferedSinkFile.delete();
      for (String s : bufferedSinkLines) {
        out.write(s);
        out.newLine();
      }
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  private static void addPath(List<File> list, String path) {
    File file = new File(path);
    if (file.exists()) {
      list.add(file);
    } else {
      System.out.println("warn: missing '" + path + "', ignored");
    }
  }

  public void addSpace(File file, boolean isOwn) {
    fernflower.getStructContext().addSpace(file, isOwn);
  }

  public void decompileContext() {
    try {
      fernflower.decompileContext();
    } finally {
      fernflower.clearContext();
    }
  }

  // *******************************************************************
  // Interface IBytecodeProvider
  // *******************************************************************

  @Override
  public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
    File file = new File(externalPath);
    if (internalPath == null) {
      return InterpreterUtil.getBytes(file);
    } else {
      try (ZipFile archive = new ZipFile(file)) {
        ZipEntry entry = archive.getEntry(internalPath);
        if (entry == null) {
          throw new IOException("Entry not found: " + internalPath);
        }
        return InterpreterUtil.getBytes(archive, entry);
      }
    }
  }

  // *******************************************************************
  // Interface IResultSaver
  // *******************************************************************

  private String getAbsolutePath(String path) {
    return new File(root, path).getAbsolutePath();
  }

  @Override
  public void saveFolder(String path) {
    File dir = new File(getAbsolutePath(path));
    if (!(dir.mkdirs() || dir.isDirectory())) {
      throw new RuntimeException("Cannot create directory " + dir);
    }
  }

  @Override
  public void copyFile(String source, String path, String entryName) {
    try {
      InterpreterUtil.copyFile(new File(source), new File(getAbsolutePath(path), entryName));
    } catch (IOException ex) {
      DecompilerContext.getLogger().writeMessage("Cannot copy " + source + " to " + entryName, ex);
    }
  }

  @Override
  public void saveClassFile(String path, String qualifiedName, String entryName, String content,
      int[] mapping) {
    File file = new File(getAbsolutePath(path), entryName);
    try (Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF8")) {
      out.write(content);
    } catch (IOException ex) {
      DecompilerContext.getLogger().writeMessage("Cannot write class file " + file, ex);
    }
  }

  @Override
  public void createArchive(String path, String archiveName, Manifest manifest) {
    File file = new File(getAbsolutePath(path), archiveName);
    try {
      if (!(file.createNewFile() || file.isFile())) {
        throw new IOException("Cannot create file " + file);
      }

      FileOutputStream fileStream = new FileOutputStream(file);
      @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
      ZipOutputStream zipStream = manifest != null ? new JarOutputStream(fileStream, manifest)
          : new ZipOutputStream(fileStream);
      mapArchiveStreams.put(file.getPath(), zipStream);
    } catch (IOException ex) {
      DecompilerContext.getLogger().writeMessage("Cannot create archive " + file, ex);
    }
  }

  @Override
  public void saveDirEntry(String path, String archiveName, String entryName) {
    saveClassEntry(path, archiveName, null, entryName, null);
  }

  @Override
  public void copyEntry(String source, String path, String archiveName, String entryName) {
    String file = new File(getAbsolutePath(path), archiveName).getPath();

    if (!checkEntry(entryName, file)) {
      return;
    }

    try (ZipFile srcArchive = new ZipFile(new File(source))) {
      ZipEntry entry = srcArchive.getEntry(entryName);
      if (entry != null) {
        try (InputStream in = srcArchive.getInputStream(entry)) {
          ZipOutputStream out = mapArchiveStreams.get(file);
          out.putNextEntry(new ZipEntry(entryName));
          InterpreterUtil.copyStream(in, out);
        }
      }
    } catch (IOException ex) {
      String message = "Cannot copy entry " + entryName + " from " + source + " to " + file;
      DecompilerContext.getLogger().writeMessage(message, ex);
    }
  }

  @Override
  public void saveClassEntry(String path, String archiveName, String qualifiedName,
      String entryName, String content) {
    String file = new File(getAbsolutePath(path), archiveName).getPath();

    if (!checkEntry(entryName, file)) {
      return;
    }

    try {
      ZipOutputStream out = mapArchiveStreams.get(file);
      out.putNextEntry(new ZipEntry(entryName));
      if (content != null) {
        out.write(content.getBytes("UTF-8"));
      }
    } catch (IOException ex) {
      String message = "Cannot write entry " + entryName + " to " + file;
      DecompilerContext.getLogger().writeMessage(message, ex);
    }
  }

  private boolean checkEntry(String entryName, String file) {
    Set<String> set = mapArchiveEntries.get(file);
    if (set == null) {
      mapArchiveEntries.put(file, set = new HashSet<>());
    }

    boolean added = set.add(entryName);
    if (!added) {
      String message = "Zip entry " + entryName + " already exists in " + file;
      DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.WARN);
    }
    return added;
  }

  @Override
  public void closeArchive(String path, String archiveName) {
    String file = new File(getAbsolutePath(path), archiveName).getPath();
    try {
      mapArchiveEntries.remove(file);
      mapArchiveStreams.remove(file).close();
    } catch (IOException ex) {
      DecompilerContext.getLogger()
          .writeMessage("Cannot close " + file, IFernflowerLogger.Severity.WARN);
    }
  }
}