package me.caarson.karmor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;

public class AnvilListener implements Listener {
    private final ConfigManager configManager;

    public AnvilListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (configManager.isBlockRepairs()) {
            // Check for tagged item in anvil
            if (isKArmorItem(event.getInventory().getItem(0))) {
                event.setResult(null); // Block repair
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (configManager.isBlockRepairs()) {
            // Guard against Mending XP in anvil when item is tagged
            if (event.getSlot() == 0 && isKArmorItem(event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isKArmorItem(org.bukkit.inventory.ItemStack item) {
        if (item == null || item.getType() == org.bukkit.Material.AIR) return false;
        
        org.bukkit.persistence.PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        
        // Check for karmor:set tag
        String setName = pdc.get(
            new org.bukkit.NamespacedKey(configManager.getPlugin(), "karmor", "set"),
            org.bukkit.persistence.PersistentDataType.STRING);
            
        return setName != null;
    }
}
