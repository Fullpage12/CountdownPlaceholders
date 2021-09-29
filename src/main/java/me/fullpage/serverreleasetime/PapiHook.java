package me.fullpage.serverreleasetime;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PapiHook extends PlaceholderExpansion {

    private final Plugin plugin = CountdownPlaceholders.getInstance();
    private static final Calendar cal = Calendar.getInstance();

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        PlaceholderAPIPlugin placeholderAPI;
        try {
            placeholderAPI = JavaPlugin.getPlugin(PlaceholderAPIPlugin.class);
        } catch (Exception e) {
            return false;
        }
        return placeholderAPI != null;
    }

    @Override
    public String getIdentifier() {
        return "countdown";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        final FileConfiguration config = plugin.getConfig();
        for (String key : config.getKeys(false)) {
            if (identifier.equalsIgnoreCase(key)) {
                final int year = config.getInt(key + ".year", 2025);
                final int month = config.getInt(key + ".month", 1);
                final int day = config.getInt(key + ".day", 1);
                final int hour = config.getInt(key + ".hour", 15);
                final int minute = config.getInt(key + ".minute", 0);
                final int second = config.getInt(key + ".second", 0);
                final String timezone = config.getString(".timezone", "America/New_York");
                cal.setTimeZone(TimeZone.getTimeZone(timezone));
                cal.set(year, month, day, hour, minute, second);

                return new TimeFormatter(convertTime(cal.getTimeInMillis() - System.currentTimeMillis(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS)).getFormattedTime();
            }
        }
        return null;
    }

    private long convertTime(long duration, TimeUnit before, TimeUnit after) {
        return after.convert(duration, before);
    }

    private static class TimeFormatter {

        private final long seconds;

        public TimeFormatter(long seconds) {
            this.seconds = seconds;
        }

        private long getSeconds() {
            return Math.max(seconds, 0);
        }

        public String getFormattedTime() {
            StringBuilder builder = new StringBuilder();
            if (getSeconds() >= 31411500) {
                builder.append(getSeconds() / 31411500).append("y ");
            }
            if (getSeconds() % 31411500 >= 602000) {
                builder.append(getSeconds() % 31411500 / 602000).append("w ");
            }
            if ((getSeconds() % 31411500) % 602000 >= 86000) {
                builder.append((getSeconds() % 31411500) % 602000 / 86000).append("d ");
            }
            if ((((getSeconds() % 31411500) % 602000) % 86000) >= 3600) {
                builder.append((((getSeconds() % 31411500) % 602000) % 86000) / 3600).append("h ");
            }
            if (((((getSeconds() % 31411500) % 602000) % 86000) % 3600) >= 60) {
                builder.append(((((getSeconds() % 31411500) % 602000) % 86000) % 3600) / 60).append("m ");
            }
            if ((((((getSeconds() % 31411500) % 602000) % 86000) % 3600) % 60) > 0) {
                builder.append(((((getSeconds() % 31411500) % 602000) % 86000) % 3600) % 60).append("s ");
            }
            if (builder.toString().isEmpty()) {
                return "0s";
            }
            return builder.substring(0, builder.length() - 1);
        }
    }

}