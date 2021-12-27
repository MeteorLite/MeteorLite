package meteor.util;

import meteor.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class InjectDeobToFatJar {
    private static final File rsClientClassesDir =
            new File("./runescape-client/build/classes/java/main");
    private static final File rsClientClassesNetDir =
            new File("./runescape-client/build/classes/java/main/net/runelite/rs/");
    private static final File fatJar = new File("./meteor-client/build/libs/meteor-client-1.0.1-all.jar");
    private static final File fatJarPacked = new File("./meteor-client/build/libs/meteor-client-1.0.1-min.jar");

    private static final File fatJarTemp =
            new File("./meteor-client/build/libs/fatJarTemp/");
    private static List<File> filesToAdd = new ArrayList<>();
    private static int classesAdded = 0;
    private static Logger log = Logger.Companion.getLogger(InjectDeobToFatJar.class);

    public static void main(String[] args) throws IOException {
        log.debug("unzipping...");
        unzip();
        log.debug("copying...");
        copyDirectory(rsClientClassesDir, fatJarTemp);
        log.debug("zipping...");
        zip();
        log.debug("processed " + classesAdded + " classes");
    }

    public static void zip() throws IOException {
        FileOutputStream fos = new FileOutputStream(fatJarPacked);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        zipDirectory(fatJarTemp, zipOut, "");
        zipOut.close();
        fos.close();
    }

    private static void zipDirectory(File directory, ZipOutputStream zipOut, String dir) throws IOException {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                String s = dir;
                s = s + f.getName() + "/";
                zipDirectory(directory, zipOut, s);
            }
            else {
                zipFile(f, dir + f.getName(), zipOut);
            }
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {
        FileInputStream fis = null;
        ZipEntry zipEntry = null;
        try {
            fis = new FileInputStream(fileToZip);
            zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("duplicate entry : " + zipEntry.getName());
        }

    }

    private static void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private static void unzip() throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fatJar));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(fatJarTemp, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void processFile(File f) {
        filesToAdd.add(f);
        classesAdded++;
    }
}