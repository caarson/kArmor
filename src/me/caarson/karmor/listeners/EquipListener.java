package me.caarson.karmor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.EnumSet;
import me.caarson.karmor.cosmetic.ParticleManager.ArmorSlot;
import me.caarson.karmor.cosmetic.ParticleManager.Trigger;
import me.caarson.karmor.cosmetic.CosmeticManager;
import me.caarson.karmor.set.SetTracker;

public class EquipListener implements Listener {
    private final CosmeticManager cosmeticManager;
    private final SetTracker setTracker;

    public EquipListener(CosmeticManager cosmeticManager, SetTracker setTracker) {
        this.cosmeticManager = cosmeticManager;
        this.setTracker = setTracker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        // Handle equipping/unequipping armor pieces
        Player player = event.getPlayer();
        
        // Add debug logging
        System.out.println("EquipListener: Armor change detected for player " + player.getName());
        System.out.println("EquipListener: Slot type: " + event.getSlotType());
        System.out.println("EquipListener: Old item: " + event.getOldItem());
        System.out.println("EquipListener: New item: " + event.getNewItem());
        
        // Check if the new item has karmor data
        ItemStack newItem = event.getNewItem();
        if (newItem != null && newItem.hasItemMeta()) {
            org.bukkit.persistence.PersistentDataContainer pdc = newItem.getItemMeta().getPersistentDataContainer();
            if (pdc.has(new org.bukkit.NamespacedKey(cosmeticManager.getPlugin(), "karmor.cosmetic.particles"))) {
                System.out.println("EquipListener: Found karmor cosmetic particles on new item!");
                String profileJson = pdc.get(new org.bukkit.NamespacedKey(cosmeticManager.getPlugin(), "karmor.cosmetic.particles"), org.bukkit.persistence.PersistentDataType.STRING);
                System.out.println("EquipListener: Profile JSON: " + profileJson);
            }
        }

        setTracker.onPlayerArmorChange(event);
    }

    private ArmorSlot parseSlot(String slotName) {
        try {
            return ArmorSlot.valueOf(slotName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
