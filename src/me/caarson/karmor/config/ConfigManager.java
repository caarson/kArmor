package me.caarson.karmor.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.caarson.karmor.set.ArmorPieceSpec;
import me.caarson.karmor.set.ArmorSet;
import me.caarson.karmor.cosmetic.CosmeticSet;
import me.caarson.karmor.cosmetic.CosmeticEnchant;

public class ConfigManager {
    private final Plugin plugin;
    private YamlConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        // Load from config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Set defaults if needed (based on user's config structure)
        config.addDefault("sets.Veteran.item.helmet", new HashMap<>());
        config.addDefault("sets.Veteran.item.chest", new HashMap<>());
        config.addDefault("sets.Veteran.item.legs", new HashMap<>());
        config.addDefault("sets.Veteran.item.boots", new HashMap<>());

        // Save updated config back to file
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config: " + e.getMessage());
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }
    
public ConfigurationSection getCosmeticsConfig() {
        return config.getConfigurationSection("cosmetics");
    }
    
    // Get cosmetic settings
    public boolean isCosmeticsEnabled() { 
        return getCosmeticsConfig().getBoolean("enabled", true); 
    }
    
    public int getMaxParticlesPerTickPerPlayer() { 
        return getCosmeticsConfig().getInt("maxParticlesPerTickPerPlayer", 150); 
    }
    
    public double getVisibleRange() { 
        return getCosmeticsConfig().getDouble("visibleRange", 32.0); 
    }
    
    public String get(String path, String def) {
        // General getter for config values
        return getConfig().getString(path, def);
    }
    
    public int get(String path, int def) {
        // General getter for config values
        return getConfig().getInt(path, def);
    }
    
    public double get(String path, double def) {
        // General getter for config values
        return getConfig().getDouble(path, def);
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

    // Missing methods identified from compilation errors
    public Plugin getPlugin() {
        return plugin;
    }

    public String getEnchantListHeader() {
        return config.getString("messages.enchant_list_header", "&6=== Your Cosmetic Enchants ===");
    }

    public boolean isAppendLoreInsteadOfReplace() {
        return config.getBoolean("preserve_meta.append_lore_instead_of_replace", false);
    }

    public ArmorSet getArmorSet(String setName) {
        String path = "sets." + setName;
        ConfigurationSection setSection = config.getConfigurationSection(path);
        if (setSection != null) {
            return new ArmorSet(setName, setSection);
        }
        return null;
    }
}
