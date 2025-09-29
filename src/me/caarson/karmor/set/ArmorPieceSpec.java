package me.caarson.karmor.set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<String> getLore() {
        List<String> loreList = new ArrayList<>();
        if (section.contains("lore")) {
            loreList = section.getStringList("lore");
        }
        return loreList;
    }

    // Set item metadata
    public void setEnchantType(String enchantType) { 
        section.set("enchant_type", enchantType); 
    }
    
    // Update lore text
    public void updateLore(List<String> newLore) {
        section.set("lore", newLore);
    }

    // Create item from specification
    public ItemStack createItem() {
        // Get material from configuration
        String materialName = section.getString("material", "LEATHER_CHESTPLATE");
        org.bukkit.Material material = org.bukkit.Material.getMaterial(materialName);
        if (material == null) {
            material = org.bukkit.Material.LEATHER_CHESTPLATE; // fallback
        }
        
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Translate color codes in display name
            String displayName = section.getString("name", "Armor Piece");
            displayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName);
            meta.setDisplayName(displayName);
            
            // Translate color codes in lore
            List<String> lore = getLore();
            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(translatedLore);
            
            item.setItemMeta(meta);
        }
        return item;
    }

    // Transient helper method to find cosmetic profile if already supported by other classes
    public Optional<ParticleManager.ActiveProfile> findCosmeticProfile(ItemStack stack, CosmeticManager cm) {
        return cm.loadProfile(stack);
    }
}
