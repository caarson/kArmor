package me.caarson.karmor.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final Plugin plugin;
    private YamlConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        // Load from config.yml
        config = YamlConfiguration.loadConfiguration(plugin.getFile("config.yml"));
        
        // Set defaults if needed (based on user's config structure)
        config.addDefault("sets.Veteran.item.helmet", new HashMap<>());
        config.addDefault("sets.Veteran.item.chest", new HashMap<>());
        config.addDefault("sets.Veteran.item.legs", new HashMap<>());
        config.addDefault("sets.Veteran.item.boots", new HashMap<>());

        // Save updated config back to file
        try {
            config.save(plugin.getFile("config.yml"));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config: " + e.getMessage());
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }
    
    // Get set-specific items, cosmetics, etc.
    public ArmorPieceSpec getArmorPieceSpec(String setName, String slot) {
        // Example: sets.Veteran.item.helmet
        String path = "sets." + setName + ".item." + slot;
        return new ArmorPieceSpec(config.getConfigurationSection(path));
    }
    
    public CosmeticSet getCosmeticSet(String setName) {
        String path = "sets." + setName + ".cosmetics";
        return new CosmeticSet(config.getConfigurationSection(path));
    }

    // Get cosmetic enchant info
    public CosmeticEnchant getCosmeticEnchant(String enchantId) {
        String path = "cosmetic_enchants." + enchantId;
        return new CosmeticEnchant(config.getConfigurationSection(path));
    }
    
    // Config settings for preservation and anvil
    public boolean isMergeVanillaEnchants() {
        return config.getBoolean("preserve_meta.merge_vanilla_enchants", true);
    }

    public boolean isBlockRepairs() {
        return config.getBoolean("anvil.block_repairs", true);
    }

    public String getMessagesPrefix() {
        return config.getString("messages.prefix", "&8[&6kArmor&8]&r ");
    }
}
