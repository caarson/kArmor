package me.caarson.karmor.set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArmorPieceSpec {
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final Map<String, Integer> enchants;

    public ArmorPieceSpec(ConfigurationSection section) {
        this.material = Material.valueOf(section.getString("material", "NETHERITE_HELMET"));
        this.name = section.getString("name", "&6&lVeteran Helm");
        this.lore = section.getStringList("lore", List.of("&7A helm awarded to seasoned fighters."));
        this.enchants = parseEnchants(section.getStringList("enchants"));
    }

    private Map<String, Integer> parseEnchants(List<String> enchantStrings) {
        return enchantStrings.stream().map(s -> s.split(":"))
            .collect(Collectors.toMap(
                parts -> parts[0],
                parts -> Integer.parseInt(parts[1])
            ));
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        item.setDisplayName(name);
        item.setLore(lore);
        
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            item.addEnchant(org.bukkit.enchantments.Enchantment.getByKey(entry.getKey()), entry.getValue(), true);
        }
        
        return item;
    }

    public String getSlot() {
        // Slot is derived from configuration path, e.g., "helmet" in section
        // This method may not be necessary if slot is handled externally (via ArmorSet)
        return null; // Placeholder for future implementation
    }
}
