package com.openosrs.injector.injectors;

import com.openosrs.injector.injection.InjectData;
import net.runelite.asm.*;
import net.runelite.deob.DeobAnnotations;

import java.io.File;

import static net.runelite.deob.util.JarUtil.load;

public class MapUnethicalPackets extends AbstractInjector {

    ClassGroup unethicalRsc;
    public MapUnethicalPackets(InjectData inject) {
        super(inject);
    }

    public void inject() {
        unethicalRsc = load(new File("./lib/unethical-rsc-203.jar"));

        for (final ClassFile vanillaClass : inject.getVanilla()) {
            if ((vanillaClass.getName().equals("osrs/ClientPacket")) ||
                    (vanillaClass.getName().equals("osrs/ServerPacket")) ||
                    (vanillaClass.getName().equals("osrs/Buffer")))
                mapClass(vanillaClass);
        }
    }

    private void mapClass(ClassFile vanillaClass) {
        Annotation impl = vanillaClass.getAnnotations().get(DeobAnnotations.IMPLEMENTS);
        if (impl != null)
            for (ClassFile unethicalClass : unethicalRsc.getClasses()) {
                Annotation unethicalImpl = unethicalClass.getAnnotations().get(DeobAnnotations.IMPLEMENTS);
                if (unethicalImpl != null) {
                    if (impl.getValueString().equals(unethicalImpl.getValueString())) {
                        for (Field deobField : vanillaClass.getFields())
                            mapUnethicalBufferField(unethicalClass, deobField);
                        for (Method deobMethod : vanillaClass.getMethods())
                            mapUnethicalBufferMethod(unethicalClass, deobMethod);
                    }
                }
            }
    }

    private void mapUnethicalBufferField(ClassFile unethicalClass, Field deobField) {
        Annotation obfName = deobField.getAnnotations().get(DeobAnnotations.OBFUSCATED_NAME);

        for (Field unethicalField: unethicalClass.getFields()) {
            Annotation unethicalObfName = unethicalField.getAnnotations().get(DeobAnnotations.OBFUSCATED_NAME);

            if (unethicalObfName != null && obfName != null)
            {
                if (unethicalObfName.getValueString().equals(obfName.getValueString()))
                {
                    Annotation export = unethicalField.getAnnotations().get(DeobAnnotations.EXPORT);
                    if (export != null)
                        System.out.println(export.getValueString());

                    deobField.getAnnotations().clear();
                    for (Annotation a : unethicalField.getAnnotations().values())
                    {
                        deobField.addAnnotation(a);
                    }
                }
            }
        }
    }

    private void mapUnethicalBufferMethod (ClassFile unethicalClass, Method deobMethod) {
        for (Method unethicalMethod: unethicalClass.getMethods()) {
            Annotation unethicalObfName = unethicalMethod.getAnnotations().get(DeobAnnotations.OBFUSCATED_NAME);
            Annotation obfName = deobMethod.getAnnotations().get(DeobAnnotations.OBFUSCATED_NAME);
            if (unethicalObfName != null && obfName != null)
            {
                if (unethicalObfName.getValueString().equals(obfName.getValueString()))
                {
                    deobMethod.getAnnotations().clear();
                    for (Annotation a : unethicalMethod.getAnnotations().values())
                    {
                        deobMethod.addAnnotation(a);
                    }
                }
            }
        }
    }
}
