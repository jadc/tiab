package red.jad.tiab.old_config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

/*
Thank you for the code, user11681
https://github.com/user11681/fixscale/blob/1.16/src/main/java/user11681/scale/ScaleConfig.java
(I'm allowed to just yoink this, right...?)
 */
public class OldConfig {
    @Expose
    public ConfigGameplay gameplay;
    @Expose
    public ConfigEffects effects;

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    private static final JsonParser PARSER = new JsonParser();
    private final File file;

    public OldConfig(final String path) {
        this.file = new File(FabricLoader.getInstance().getConfigDir().toFile(), path);
        // Initialization
        this.gameplay = new ConfigGameplay();
        this.effects = new ConfigEffects();
    }

    public void write() throws Throwable {
        ((OutputStream) new FileOutputStream(this.file)).write(GSON.toJson(this).getBytes());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void read() throws Throwable {
        if (this.file.createNewFile()) {
            this.write();
        } else {
            final InputStream input = new FileInputStream(this.file);
            final byte[] content = new byte[input.available()];
            while (input.read(content) > -1);
            final JsonObject element = (JsonObject) PARSER.parse(new String(content));

            // Reading
            this.gameplay = GSON.fromJson(element.get("gameplay"), ConfigGameplay.class);
            this.effects = GSON.fromJson(element.get("effects"), ConfigEffects.class);
        }
    }
}
