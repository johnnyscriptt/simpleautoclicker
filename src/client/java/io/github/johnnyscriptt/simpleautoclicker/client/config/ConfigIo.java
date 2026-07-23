package io.github.johnnyscriptt.simpleautoclicker.client.config;

import io.github.johnnyscriptt.simpleautoclicker.client.SimpleAutoclickerClient;
import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loads and saves the single {@link AutoclickerConfig} instance at config/simpleautoclicker.json. */
public final class ConfigIo {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static AutoclickerConfig config;

    private ConfigIo() {
    }

    public static synchronized AutoclickerConfig get() {
        if (config == null) {
            config = load();
        }
        return config;
    }

    public static synchronized void save() {
        if (config == null) {
            return;
        }
        config.clamp();
        try {
            Files.writeString(path(), GSON.toJson(config));
        } catch (IOException e) {
            SimpleAutoclickerClient.LOGGER.warn("Could not save simpleautoclicker.json", e);
        }
    }

    private static AutoclickerConfig load() {
        Path file = path();
        if (Files.exists(file)) {
            try {
                AutoclickerConfig loaded = GSON.fromJson(Files.readString(file), AutoclickerConfig.class);
                if (loaded != null) {
                    loaded.clamp();
                    return loaded;
                }
            } catch (Exception e) {
                SimpleAutoclickerClient.LOGGER.warn("Could not read simpleautoclicker.json, using defaults", e);
            }
        }
        return new AutoclickerConfig();
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("simpleautoclicker.json");
    }
}
