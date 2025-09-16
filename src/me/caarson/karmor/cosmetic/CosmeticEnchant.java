package me.caarson.karmor.cosmetic;

import org.bukkit.configuration.ConfigurationSection;
import java.util.Map;

public class CosmeticEnchant {
    private final ConfigurationSection section;

    public CosmeticEnchant(ConfigurationSection section) {
        this.section = section;
    }

    // Get enchant properties
    public String getName() { return section.getString("name", "Cosmetic Enchantment"); }
    public int getMaxLevel() { return section.getInt("max_level", 1); }
    public double getCostMultiplier() { return section.getDouble("cost_multiplier", 0.5); } 
    public boolean isConsumable() { return section.getBoolean("consumable", false); }

    // Get enchant effects
    public String getParticleStyle() { return section.getString("particle_style", "AURA_ARCANE"); }
    public int getParticleDensity() { return section.getInt("particle_density", 6); } 
    public double getParticleRadius() { return section.getDouble("particle_radius", 0.8); }
    public double getParticleScale() { return section.getDouble("particle_scale", 1.0); }

    // Get enchant settings
    public boolean isEnabled() { return section.getBoolean("enabled", true); }
    
    public Map<String, Object> getExtras() {
        return section.getValues(false);
    }

    // Get specific property from extras map by key
    public String getString(String key) { 
        return section.getString(key, ""); 
    } 
    
    public int getInt(String key) { 
        return section.getInt(key, 0); 
    }
    
    public double getDouble(String key) { 
        return section.getDouble(key, 0.0); 
    }

}
