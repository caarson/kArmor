package me.caarson.karmor.set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class ArmorSet {
    private final String setName;
    private Map<String, ArmorPieceSpec> pieces; // slot -> piece spec

    public ArmorSet(String setName, ConfigurationSection section) {
        this.setName = setName;
        pieces = loadPieces(section);
    }

    private Map<String, ArmorPieceSpec> loadPieces(ConfigurationSection section) {
        Map<String, ArmorPieceSpec> result = new HashMap<>();
        
        // Debug: Log all keys in the set section
        System.out.println("ArmorSet: Set section keys: " + section.getKeys(false));
        
        // The debug shows "item" exists but getConfigurationSection returns null
        // This means "item" is stored as a Map, not a ConfigurationSection
        // Let's handle the Map conversion for the item section too
        
        Object itemObject = section.get("item");
        ConfigurationSection itemSection = null;
        
        if (itemObject instanceof Map) {
            System.out.println("ArmorSet: 'item' is a Map, converting to ConfigurationSection");
            Map<?, ?> itemMap = (Map<?, ?>) itemObject;
            for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                section.set("item." + entry.getKey(), entry.getValue());
            }
            itemSection = section.getConfigurationSection("item");
        } else {
            itemSection = section.getConfigurationSection("item");
        }
        
        if (itemSection == null) {
            System.out.println("ArmorSet: Still unable to get 'item' as ConfigurationSection");
            return result;
        }
        
        System.out.println("ArmorSet: Item section keys: " + itemSection.getKeys(false));
        
        for (String slot : new String[]{"helmet", "chestplate", "legs", "boots"}) {
            ConfigurationSection pieceSection = itemSection.getConfigurationSection(slot);
            if (pieceSection == null) {
                // Try Map conversion for individual pieces too
                Object pieceObject = itemSection.get(slot);
                if (pieceObject instanceof Map) {
                    System.out.println("ArmorSet: Piece '" + slot + "' is a Map, converting to ConfigurationSection");
                    Map<?, ?> pieceMap = (Map<?, ?>) pieceObject;
                    for (Map.Entry<?, ?> entry : pieceMap.entrySet()) {
                        itemSection.set(slot + "." + entry.getKey(), entry.getValue());
                    }
                    pieceSection = itemSection.getConfigurationSection(slot);
                }
            }
            
            if (pieceSection != null) {
                System.out.println("ArmorSet: Found piece for slot '" + slot + "'");
                result.put(slot, new ArmorPieceSpec(pieceSection));
            } else {
                System.out.println("ArmorSet: No piece found for slot '" + slot + "'");
            }
        }
        return result;
    }

    public boolean isFullSetEquipped(Player player) {
        return player.getInventory().getHelmet() != null &&
               player.getInventory().getChestplate() != null &&
               player.getInventory().getLeggings() != null &&
               player.getInventory().getBoots() != null;
    }

    public String getSetName() {
        return setName;
    }

    public ArmorPieceSpec getPiece(String slot) {
        return pieces.get(slot);
    }
}
