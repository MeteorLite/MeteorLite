/*
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * All rights reserved.
 *
 * This code is licensed under GPL3, see the complete license in
 * the LICENSE file in the root directory of this submodule.
 */
package com.openosrs.injector;

import static net.runelite.deob.util.JarUtil.addReflection;
import static net.runelite.deob.util.JarUtil.load;
import static org.sponge.util.Logger.ANSI_GREEN;
import static org.sponge.util.Logger.ANSI_RESET;
import static org.sponge.util.Logger.ANSI_YELLOW;

import com.openosrs.injector.injection.InjectData;
import com.openosrs.injector.injection.InjectTaskHandler;
import com.openosrs.injector.injectors.CreateAnnotations;
import com.openosrs.injector.injectors.InjectConstruct;
import com.openosrs.injector.injectors.InterfaceInjector;
import com.openosrs.injector.injectors.MixinInjector;
import com.openosrs.injector.injectors.RSApiInjector;
import com.openosrs.injector.injectors.raw.*;
import com.openosrs.injector.rsapi.RSApi;
import com.openosrs.injector.transformers.InjectTransformer;
import com.openosrs.injector.transformers.Java8Ifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.EnumConverter;
import net.runelite.asm.ClassFile;
import net.runelite.asm.ClassGroup;
import net.runelite.deob.util.JarUtil;
import org.sponge.util.Logger;
import org.sponge.util.Message;

public class Injector extends InjectData implements InjectTaskHandler {

  static final Logger log = new Logger("Injector");
  static Injector injector = new Injector();
  static String oprsVer;

  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    ArgumentAcceptingOptionSpec<OutputMode> outModeOption =
        parser.accepts("outmode")
            .withRequiredArg().ofType(OutputMode.class)
            .withValuesConvertedBy(new EnumConverter<OutputMode>(OutputMode.class) {
              @Override
              public OutputMode convert(String value) {
                return super.convert(value.toUpperCase());
              }
            });

    OptionSet options = parser.parse(args);
    oprsVer = "1.0-SNAPSHOT";

    File clientMixins = new File("../runelite-mixins/build/libs/runelite-mixins-" + oprsVer + ".jar");
    if (clientMixins.exists()) {
      log.info("Injecting Client");

      injector.vanilla = load(
              new File("../runescape-client/build/libs/runescape-client-" + oprsVer + ".jar"));
      injector.deobfuscated = load(
              new File("../runescape-client/build/libs/runescape-client-" + oprsVer + ".jar"));
      injector.rsApi = new RSApi(Objects.requireNonNull(
              new File("../runescape-api/build/classes/java/main/net/runelite/rs/api/")
                      .listFiles()));
      injector.mixins = load(clientMixins);

      injector.initToVanilla();
      injector.injectVanilla();
      save(injector.getVanilla(), new File("./build/injected/injected-client.jar"),
              options.valueOf(outModeOption));
    }

    File klientMixins = new File("../klient-mixins/build/libs/klient-mixins-" + oprsVer + ".jar");
    if (klientMixins.exists()) {
      log.info("Injecting Klient");
      HOOKS = "meteor/Hooks";

      injector.vanilla = load(
              new File("../runescape-client/build/libs/runescape-client-" + oprsVer + ".jar"));
      injector.deobfuscated = load(
              new File("../runescape-client/build/libs/runescape-client-" + oprsVer + ".jar"));
      injector.rsApi = new RSApi(Objects.requireNonNull(
              new File("../runescape-api/build/classes/java/main/net/runelite/rs/api/")
                      .listFiles()));
      injector.mixins = load(klientMixins);

      injector.initToVanilla();
      injector.injectVanilla();
      save(injector.getVanilla(), new File("./build/injected/injected-klient.jar"),
              options.valueOf(outModeOption));
    }
  }

  private static void save(ClassGroup group, File output, OutputMode mode) {
    if (output.exists()) {
      try {
        Files.walk(output.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        log.info("Failed to delete output directory contents.");
        throw new RuntimeException(e);
      }
    }

    output.getParentFile().mkdirs();
    JarUtil.save(group, output);
  }

  private static void saveFiles(ClassGroup group, File outDir) {
    try {
      outDir.mkdirs();

      for (ClassFile cf : group.getClasses()) {
        File f = new File(outDir, cf.getName() + ".class");
        byte[] data = JarUtil.writeClass(group, cf);
        Files.write(f.toPath(), data);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void injectVanilla() {
    log.debug(ANSI_YELLOW + "[Starting injection]" + ANSI_RESET);

    transform(new Java8Ifier(this));

    inject(new CreateAnnotations(this));

    inject(new GraphicsObject(this));

    inject(new CopyRuneLiteClasses(this));

    inject(new RuneLiteIterableHashTable(this));

    inject(new RuneliteObject(this));

    //Injects initial RSAPI
    inject(new InterfaceInjector(this));

    inject(new RasterizerAlpha(this));

    inject(new MixinInjector(this));

    // This is where field hooks runs

    // This is where method hooks runs

    inject(new InjectConstruct(this));

    //Requires InterfaceInjector
    inject(new RSApiInjector(this));

    //Some annotations are still nice to have such as ObfName and ObfSig for Reflection checks
    //inject(new RemoveAnnotations(this));
    //The Reflection class is skipped during load because the asm doesnt support invokedynamic, ez fix to just put
    //it back in after doing everything
    addReflection(vanilla,
        new File("../runescape-client/build/libs/runescape-client-" + oprsVer + ".jar"));
    //inject(new DrawAfterWidgets(this));

    inject(new ScriptVM(this));

    // All GPU raw injectors should probably be combined, especially RenderDraw and Occluder
    inject(new ClearColorBuffer(this, HOOKS));

    inject(new RenderDraw(this, HOOKS));

    //inject(new Occluder(this));

    inject(new DrawMenu(this, HOOKS));



    //inject(new AddPlayerToMenu(this));

    //validate(new InjectorValidator(this));

    //transform(new SourceChanger(this));
  }

  private void inject(com.openosrs.injector.injectors.Injector injector) {
    final String name = injector.getName();

    //log.info(ANSI_YELLOW + "[Starting " + name + "]" + ANSI_RESET);

    injector.start();

    injector.inject();

    log.info(Message.newMessage()
                    .add(ANSI_YELLOW, name + " ")
                    .add(ANSI_GREEN, injector.getCompletionMsg())
                    .build());

    if (injector instanceof Validator) {
      validate((Validator) injector);
    }
  }

  private void validate(Validator validator) {
    final String name = validator.getName();

    if (!validator.validate()) {
      throw new InjectException(name + " failed validation");
    }
  }

  private void transform(InjectTransformer transformer) {
    final String name = transformer.getName();

    //log.info(ANSI_YELLOW + "Starting " + name + ANSI_RESET);

    transformer.transform();

    log.info(Message.newMessage()
                    .add(ANSI_YELLOW, name + " ")
                    .addDefault(transformer.getCompletionMsg())
        .build());
  }

  public void runChildInjector(com.openosrs.injector.injectors.Injector injector) {
    inject(injector);
  }
}
