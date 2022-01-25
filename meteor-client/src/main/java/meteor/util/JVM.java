package meteor.util;

import com.google.common.io.ByteStreams;
import meteor.MeteorLiteClientLauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JVM {
    public static ClassLoader createJarClassLoader(File jar) throws IOException, ClassNotFoundException
    {
        try (JarFile jarFile = new JarFile(jar))
        {
            ClassLoader classLoader = new ClassLoader(MeteorLiteClientLauncher.class.getClassLoader())
            {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException
                {
                    String entryName = name.replace('.', '/').concat(".class");
                    JarEntry jarEntry;

                    try
                    {
                        jarEntry = jarFile.getJarEntry(entryName);
                    }
                    catch (IllegalStateException ex)
                    {
                        throw new ClassNotFoundException(name, ex);
                    }

                    if (jarEntry == null)
                    {
                        throw new ClassNotFoundException(name);
                    }

                    try
                    {
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        if (inputStream == null)
                        {
                            throw new ClassNotFoundException(name);
                        }

                        byte[] bytes = ByteStreams.toByteArray(inputStream);
                        return defineClass(name, bytes, 0, bytes.length);
                    }
                    catch (IOException e)
                    {
                        throw new ClassNotFoundException(null, e);
                    }
                }
            };

            // load all of the classes in this jar; after the jar is closed the classloader
            // will no longer be able to look up classes
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class"))
                {
                    name = name.substring(0, name.length() - 6);
                    classLoader.loadClass(name.replace("/", "."));
                }
            }

            return classLoader;
        }
    }
}
