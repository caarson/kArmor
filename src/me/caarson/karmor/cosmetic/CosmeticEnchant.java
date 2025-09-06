package me.caarson.karmor.cosmetic;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class CosmeticEnchant {
    private final String enchantId;
    private final String display;
    private final String loreLine;
    private final int maxLevel;
    private final Map<String, Integer> particles;
    private final boolean soundEnabled;
    
    public CosmeticEnchant(ConfigurationSection section) {
        this.enchantId = section.getName();
        this.display = section.getString("display", "&cAura: Flame");
        this.loreLine = section.getString("lore_line", "&7Cosmetic: &cFlame Aura");
        this.maxLevel = section.getInt("max_level", 1);
        
        // Particles config
        ConfigurationSection particlesSection = section.getConfigurationSection("particles");
        if (particlesSection != null) {
            this.particles = Map.of(
                "particle", particlesSection.getString("particle", "FLAME"),
                "count_per_tick", particlesSection.getInt("count_per_tick", 4),
                "offset", particlesSection.getDouble("offset", 0.15),
                "speed", particlesSection.getDouble("speed", 0.0)
            );
        } else {
            this.particles = Map.of();
        }
        
        // Sound config
        ConfigurationSection soundSection = section.getConfigurationSection("sound");
        if (soundSection != null) {
            this.soundEnabled = soundSection.getBoolean("enabled", false);
        } else {
            this.soundEnabled = false;
        }
    }

    public String getEnchantId() { return enchantId; }
    public String getDisplay() { return display; }
    public String getLoreLine() { return loreLine; }
    public int getMaxLevel() { return maxLevel; }
    public Map<String, Integer> getParticles() { return particles; }
    public boolean isSoundEnabled() { return soundEnabled; }

    // Additional helper methods for cosmetic effects
    public void applyToItem(ItemStack item) {
        // Store in PDC: karmor:cosmetic_enchants = list of IDs (not the actual enchant)
        // For visual effect, use particles/sound as per config.
        // Note: This doesn't affect gameplay; only applies cosmetics for display.
    }
}
