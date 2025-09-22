package me.caarson.karmor.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import me.caarson.karmor.cosmetic.CosmeticManager.CosmeticSet;

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
        ConfigurationSection cosmeticsConfig = getCosmeticsConfig();
        return cosmeticsConfig != null && cosmeticsConfig.getBoolean("enabled", true); 
    }
    
    public int getMaxParticlesPerTickPerPlayer() { 
        ConfigurationSection cosmeticsConfig = getCosmeticsConfig();
        return cosmeticsConfig != null ? cosmeticsConfig.getInt("maxParticlesPerTickPerPlayer", 150) : 150; 
    }
    
    public double getVisibleRange() { 
        ConfigurationSection cosmeticsConfig = getCosmeticsConfig();
        return cosmeticsConfig != null ? cosmeticsConfig.getDouble("visibleRange", 32.0) : 32.0; 
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

    // Get armor piece specification
    public me.caarson.karmor.set.ArmorPieceSpec getArmorPieceSpec(String setName, String slot) {
        ConfigurationSection setSection = config.getConfigurationSection("sets." + setName);
        if (setSection != null) {
            ConfigurationSection pieceSection = setSection.getConfigurationSection("item." + slot);
            if (pieceSection != null) {
                return new me.caarson.karmor.set.ArmorPieceSpec(pieceSection);
            }
        }
        return null;
    }

    // Get cosmetic enchantment
    public me.caarson.karmor.cosmetic.CosmeticEnchant getCosmeticEnchant(String enchantId) {
        ConfigurationSection enchantSection = config.getConfigurationSection("cosmetics.enchants." + enchantId);
        if (enchantSection != null) {
            return new me.caarson.karmor.cosmetic.CosmeticEnchant(enchantSection);
        }
        return null;
    }

    // Get armor set
    public me.caarson.karmor.set.ArmorSet getArmorSet(String setName) {
        ConfigurationSection setSection = config.getConfigurationSection("sets." + setName);
        if (setSection != null) {
            return new me.caarson.karmor.set.ArmorSet(setName, setSection);
        }
        return null;
    }

    // Get cosmetic set
    public CosmeticSet getCosmeticSet(String setName) {
        ConfigurationSection setSection = config.getConfigurationSection("sets." + setName);
        if (setSection != null) {
            return new CosmeticSet(setSection);
        }
        return null;
    }

    public boolean isRespectMaxLevels() {
        return config.getBoolean("preserve_meta.respect_max_levels", true);
    }
}
