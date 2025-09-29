package me.caarson.karmor.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        
        // Set defaults for cosmetics
        config.addDefault("cosmetics.enabled", true);
        config.addDefault("cosmetics.maxParticlesPerTickPerPlayer", 150);
        config.addDefault("cosmetics.visibleRange", 32.0);
        config.addDefault("cosmetics.default.style", "AURA_ARCANE");
        config.addDefault("cosmetics.default.color", "#7F00FF");
        config.addDefault("cosmetics.default.rateTps", 5);
        config.addDefault("cosmetics.default.density", 6);
        config.addDefault("cosmetics.default.radius", 0.8);
        config.addDefault("cosmetics.default.scale", 1.0);
        config.addDefault("cosmetics.default.triggers", new String[]{"AURA"});

        // Set defaults for example_set
        Map<String, Object> helmetMap = new HashMap<>();
        helmetMap.put("material", "DIAMOND_HELMET");
        helmetMap.put("name", "&bExample Helmet");
        helmetMap.put("lore", Arrays.asList(
            "&7Part of the Example Set",
            "&7Special ability: Aura particles"
        ));
        
        Map<String, Object> chestplateMap = new HashMap<>();
        chestplateMap.put("material", "DIAMOND_CHESTPLATE");
        chestplateMap.put("name", "&bExample Chestplate");
        chestplateMap.put("lore", Arrays.asList(
            "&7Part of the Example Set",
            "&7Special ability: Wing particles"
        ));
        
        Map<String, Object> legsMap = new HashMap<>();
        legsMap.put("material", "DIAMOND_LEGGINGS");
        legsMap.put("name", "&bExample Leggings");
        legsMap.put("lore", Arrays.asList(
            "&7Part of the Example Set",
            "&7Special ability: Swirl particles"
        ));
        
        Map<String, Object> bootsMap = new HashMap<>();
        bootsMap.put("material", "DIAMOND_BOOTS");
        bootsMap.put("name", "&bExample Boots");
        bootsMap.put("lore", Arrays.asList(
            "&7Part of the Example Set",
            "&7Special ability: Footprint particles"
        ));
        
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("helmet", helmetMap);
        itemMap.put("chestplate", chestplateMap);
        itemMap.put("legs", legsMap);
        itemMap.put("boots", bootsMap);
        
        Map<String, Object> exampleSetMap = new HashMap<>();
        exampleSetMap.put("item", itemMap);
        
        config.addDefault("sets.example_set", exampleSetMap);

        // Set other defaults
        config.addDefault("preserve_meta.merge_vanilla_enchants", true);
        config.addDefault("preserve_meta.append_lore_instead_of_replace", false);
        config.addDefault("preserve_meta.respect_max_levels", true);
        config.addDefault("anvil.block_repairs", true);
        config.addDefault("messages.prefix", "&8[&6kArmor&8]&r ");
        config.addDefault("messages.enchant_list_header", "&6=== Your Cosmetic Enchants ===");

        // Copy defaults and save
        config.options().copyDefaults(true);
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
        plugin.getLogger().info("ConfigManager: Looking for armor set '" + setName + "'");
        
        // Check if sets section exists
        ConfigurationSection setsSection = config.getConfigurationSection("sets");
        if (setsSection == null) {
            plugin.getLogger().warning("ConfigManager: No 'sets' section found in config!");
            return null;
        }
        
        plugin.getLogger().info("ConfigManager: Available sets: " + setsSection.getKeys(false));
        
        // Check if the specific set exists
        if (!setsSection.contains(setName)) {
            plugin.getLogger().warning("ConfigManager: Set '" + setName + "' not found in sets section");
            return null;
        }
        
        // Try to get as ConfigurationSection first
        ConfigurationSection setSection = setsSection.getConfigurationSection(setName);
        if (setSection == null) {
            plugin.getLogger().warning("ConfigManager: Set '" + setName + "' exists but is not a ConfigurationSection, trying to get as object...");
            
            // The set might be stored as a Map instead of a ConfigurationSection
            // Let's try to get the raw object and check its type
            Object setObject = setsSection.get(setName);
            if (setObject instanceof Map) {
                plugin.getLogger().info("ConfigManager: Set '" + setName + "' is a Map, converting to ConfigurationSection");
                // Convert the Map to a ConfigurationSection
                Map<?, ?> setMap = (Map<?, ?>) setObject;
                for (Map.Entry<?, ?> entry : setMap.entrySet()) {
                    setsSection.set(setName + "." + entry.getKey(), entry.getValue());
                }
                // Now try to get it as a ConfigurationSection again
                setSection = setsSection.getConfigurationSection(setName);
            }
        }
        
        if (setSection == null) {
            plugin.getLogger().warning("ConfigManager: Still unable to get set '" + setName + "' as ConfigurationSection");
            return null;
        }
        
        plugin.getLogger().info("ConfigManager: Found configuration section for set '" + setName + "'");
        return new me.caarson.karmor.set.ArmorSet(setName, setSection);
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
