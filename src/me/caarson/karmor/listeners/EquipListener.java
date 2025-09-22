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
import me.caarson.karmor.cosmetic.CosmeticManager.ActiveProfile;
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
        
        // Temporarily commented out due to API changes - methods like getItem(), getSlot(), isEquipping() don't exist
        // ItemStack item = event.getItem();
        // if (item != null && !item.getType().equals(Material.AIR)) {
        //     ArmorSlot slot = parseSlot(event.getSlot());
        //     
        //     if (event.isEquipping()) {
        //         // Load cosmetic profile into cache when equipping
        //         ActiveProfile profile = cosmeticManager.loadProfile(item);
        //         if (profile != null) {
        //             // If player has global toggle OFF, don't spawn particles
        //             if (!cosmeticManager.isCosmeticsEnabled(player)) return;
        //             // Warm cache for future use
        //             cosmeticManager.getOrLoadProfile(item); 
        //         }
        //     } else {
        //         // Clear profile cache when unequipping
        //         cosmeticManager.clearProfileCache(item);
        //     }
        // }

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
