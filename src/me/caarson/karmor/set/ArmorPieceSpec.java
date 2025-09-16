package me.caarson.karmor.set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.caarson.karmor.cosmetic.CosmeticManager;
import me.caarson.karmor.cosmetic.ParticleManager;

public class ArmorPieceSpec {
    private final ConfigurationSection section;
    private Map<String, String> lore = new HashMap<>();

    public ArmorPieceSpec(ConfigurationSection section) {
        this.section = section;
    }

    // Get piece type
    public String getType() { return section.getString("type", "armor"); }
    
    // Get item metadata (e.g., enchantment)
    public String getEnchantType() { return section.getString("enchant_type", ""); }
    
    // Get lore text (for display purposes)
    public Map<String, String> getLore() {
        if (!section.contains("lore")) return lore;
        
        for (String key : section.getKeys(false)) {
            lore.put(key, section.getString(key));
        }
        return lore;
    }

    // Set item metadata
    public void setEnchantType(String enchantType) { 
        section.set("enchant_type", enchantType); 
    }
    
    // Update lore text
    public void updateLore(Map<String, String> newLore) {
        lore = newLore;
        for (String key : newLore.keySet()) {
            section.set(key, newLore.get(key));
        }
    }

    // Transient helper method to find cosmetic profile if already supported by other classes
public Optional<ParticleManager.ActiveProfile> findCosmeticProfile(ItemStack stack, CosmeticManager cm) {
        return cm.loadProfile(stack);
    }
}
