package meteor.util.bootstrap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import meteor.Logger;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Bootstrapper {
    private static final File HOSTING_UPDATE = new File("./build/update.json");
    private static final String HOSTING_BASE = "https://raw.githubusercontent.com/MeteorLite/Hosting/main/";
    private static File shadowJar;
    private static File outputDir = new File("./build/bootstrap/");
    private static File updateOutput = new File("./build/bootstrap/bootstrap-meteorlite.json");
    private static final File bootstrapShadowJar = new File("./build/bootstrap/meteorlite.jar");
    private static final File libsDir = new File("./build/libs/");

    private static Logger log = new Logger("Bootstrapper");

    private static MessageDigest md5Digest;
    private static Update update;
    private static String currentVer;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        outputDir.mkdirs();
        fetchCurrentRevision();
        Reader reader = Files.newBufferedReader(HOSTING_UPDATE.toPath());
        Gson gson = new Gson();
        update = gson.fromJson(reader, Update.class);;
        String[] vers = update.version.split("\\.");
        int patch = Integer.parseInt(vers[2]);
        currentVer = vers[0] + "." + vers[1] + "." + patch;
        update.version = vers[0] + "." + vers[1] + "." + (patch + 1);
        for (File f : Objects.requireNonNull(libsDir.listFiles()))
            if (f.getName().contains("-all"))
                shadowJar = f;
        md5Digest = MessageDigest.getInstance("MD5");
        processFile(shadowJar);
        saveBootstrap();
        copyShadowJar();
    }

    private static void fetchCurrentRevision() {
        if (HOSTING_UPDATE.exists()) {
            HOSTING_UPDATE.delete();
        }

        URL website = null;
        try {
            website = new URL(HOSTING_BASE + "bootstrap-meteorlite.json");
            String filepath = HOSTING_UPDATE.getAbsolutePath();

            ReadableByteChannel channel = Channels.newChannel(website.openStream());
            HOSTING_UPDATE.createNewFile();
            FileOutputStream stream = new FileOutputStream(filepath);

            stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void copyShadowJar() {
        try {
            if (bootstrapShadowJar.exists())
                bootstrapShadowJar.delete();
            Files.copy(shadowJar.toPath(), bootstrapShadowJar.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveBootstrap() {
        if (update.size < 100)
            throw new RuntimeException("Bad bootstrap, bump meteor-client version");
        outputDir.mkdirs();
        if (!updateOutput.exists()) {
            try {
                updateOutput.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!updateOutput.exists()) {
            try {
                updateOutput.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new FileWriter(updateOutput)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(update, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(File f) {
        if (!f.getName().endsWith(".msi")) {
            update.size = f.length();
            update.hash = getFileChecksum(md5Digest, f);
        }
    }

    private static String getFileChecksum(MessageDigest digest, File file)
    {
        //Get file input stream for reading the file content
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            };

            //close the stream; We don't need it now.
            fis.close();

            //Get the hash's bytes
            byte[] bytes = digest.digest();

            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //return complete hash
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
