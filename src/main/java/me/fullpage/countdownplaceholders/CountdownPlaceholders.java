package me.fullpage.countdownplaceholders;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CountdownPlaceholders extends JavaPlugin implements CommandExecutor {

    private static CountdownPlaceholders instance;
    private static PapiHook papiHook;

    public static CountdownPlaceholders getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        this.getCommand("countdownplaceholders").setExecutor(this);

        papiHook = new PapiHook();
        if (!papiHook.isRegistered() && papiHook.register()) {
            getLogger().log(Level.INFO, "Successfully registered placeholders!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlaceholderAPI.unregisterPlaceholderHook(papiHook.getIdentifier());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp() || sender.hasPermission("countdownplaceholders.admin")) {
            if (args.length == 0) {
                sendMessage(sender, "&cIncorrect usage, try /countdownplaceholders <reload, placeholders>");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                reloadConfig();
                PlaceholderAPI.unregisterPlaceholderHook(papiHook.getIdentifier());
                papiHook.register();

                sendMessage(sender, "&aSuccessfully reloaded the plugin!");
            } else if (args[0].equalsIgnoreCase("placeholders")) {
                sendMessage(sender, "&6Countdown Placeholders:");
                for (String key : getConfig().getKeys(false)) {
                    String placeholder = "%countdown_" + key + "%";
                    sendMessage(sender, "&e" + placeholder + (sender instanceof OfflinePlayer ? " (" + PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, placeholder) + ")" : ""));
                }

            }
        } else {
            sendMessage(sender, "&cInsufficient Permission.");
            return true;
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

}
