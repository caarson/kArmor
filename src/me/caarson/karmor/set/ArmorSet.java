package me.caarson.karmor.set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.stream.Collectors;

public class ArmorSet {
    private final String setName;
    private Map<String, ArmorPieceSpec> pieces; // slot -> piece spec

    public ArmorSet(String setName, ConfigurationSection section) {
        this.setName = setName;
        pieces = loadPieces(section);
    }

    private Map<String, ArmorPieceSpec> loadPieces(ConfigurationSection section) {
        String basePath = "sets." + setName + ".item";
        return Stream.of("helmet", "chest", "legs", "boots")
            .map(slot -> new ArmorPieceSpec(section.getConfigurationSection(basePath + "." + slot)))
            .collect(Collectors.toMap(
                slot -> slot,
                spec -> spec
            );
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
