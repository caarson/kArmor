package me.caarson.karmor.integrate;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredListener;
import java.util.HashMap;
import java.util.Map;

public class PhoenixBridge {
    private final Plugin plugin;
    private boolean isPhoenixAPIAvailable = false;
    private Map<String, String> rankColorsMap = new HashMap<>();

    public PhoenixBridge(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            // Check for Phoenix-API availability (via soft-depend)
            Class.forName("com.github.phoenixapi.PhoenixAPI");
            isPhoenixAPIAvailable = true;
            
            // Initialize rank colors from config
            ConfigurationSection messagesConfig = plugin.getConfig().getConfigurationSection("messages");
            String prefix = messagesConfig.getString("prefix", "&8[&6kArmor&8]&r ");
            // Example: if Phoenix-API, then %rankColor% is replaced by actual color code.
            
            // We assume that Phoenix-API provides a method to get rank colors
            // For example in PhoenixAPI: `PhoenixAPI.getRankColor(player)`
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("Phoenix-API not available; using fallback white.");
            isPhoenixAPIAvailable = false;
        }
    }

    public void shutdown() {
        // Cleanup if needed
    }

    public String getRankColor(Player player, boolean useFallback) {
        if (!isPhoenixAPIAvailable && useFallback) {
            return ChatColor.WHITE.toString();
        } else {
            try {
                // Phoenix-API method: assume it exists
                String colorCode = com.github.phoenixapi.PhoenixAPI.getRankColor(player);
                return colorCode;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to get rank color via Phoenix-API; using fallback white.");
                return ChatColor.WHITE.toString();
            }
        }
    }
}
