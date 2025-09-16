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
        String basePath = "sets." + setName + ".item";
        Map<String, ArmorPieceSpec> result = new HashMap<>();
        for (String slot : new String[]{"helmet", "chest", "legs", "boots"}) {
            ConfigurationSection pieceSection = section.getConfigurationSection(basePath + "." + slot);
            if (pieceSection != null) {
                result.put(slot, new ArmorPieceSpec(pieceSection));
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
}
