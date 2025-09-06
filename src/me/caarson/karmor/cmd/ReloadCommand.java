package me.caarson.karmor.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final Plugin plugin;

    public ReloadCommand() {
        this.plugin = null; // Will be set from KArmorPlugin
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission("karmor.admin")) {
            sender.sendMessage("You don't have permission for this command.");
            return false;
        }
        
        // Reload config and reset state
        ConfigManager configManager = new ConfigManager(plugin);
        plugin.reloadConfig();
        
        sender.sendMessage(configManager.getMessagesPrefix() + "Reloaded configuration successfully.");
        
        return true;
    }
}
