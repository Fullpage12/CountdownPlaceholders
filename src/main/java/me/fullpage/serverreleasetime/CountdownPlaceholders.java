package me.fullpage.serverreleasetime;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class CountdownPlaceholders extends JavaPlugin {

    private static CountdownPlaceholders instance;

    public static CountdownPlaceholders getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        File configf = new File(this.getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        final PapiHook papiHook = new PapiHook();
        if (!papiHook.isRegistered() && papiHook.register())
            getLogger().log(Level.INFO, "Successfully registered placeholders!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        final PapiHook papiHook = new PapiHook();
        PlaceholderAPI.unregisterPlaceholderHook(papiHook.getIdentifier());
    }



}
