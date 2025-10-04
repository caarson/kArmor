package me.caarson.karmor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import java.util.EnumSet;
import me.caarson.karmor.cosmetic.ParticleManager.Trigger;
import me.caarson.karmor.cosmetic.ParticleManager.ArmorSlot;
import me.caarson.karmor.cosmetic.ParticleStyle;

public class HitListener implements Listener {
    private final Plugin plugin;

    public HitListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // Listen to EntityDamageByEntityEvent, PlayerDeathEvent, BlockBreakEvent
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        // Check if the damager is wearing armor with impact triggers
        checkAndTriggerImpact(player, "HIT");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // Check if the victim was wearing armor with impact triggers (kill)
        checkAndTriggerImpact(player, "KILL");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Check if the breaker is wearing armor with impact triggers (block break)
        checkAndTriggerImpact(player, "BLOCK");
    }
    
    private void checkAndTriggerImpact(Player p, String type) {
        // Get current worn items
        // Temporarily commented out due to compilation issues
        // for (ArmorSlot slot : ArmorSlot.values()) {
        //     ItemStack item = getWornItemInSlot(p, slot);
        //     if (item != null && !item.getType().equals(Material.AIR)) {
        //         ActiveProfile profile = getProfileFromItem(item);
        //         if (profile != null) {
        //             // Check triggers for this type
        //             boolean shouldTrigger = false;
        //             switch(type) {
        //                 case "HIT":
        //                     shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_HIT); break;
        //                 case "KILL":
        //                     shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_KILL); break;
        //                 case "BLOCK":
        //                     shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_BLOCK); break;
        //             }
        //             
        //             if (shouldTrigger) {
        //                 // Trigger impact particle effect
        //                 // ParticleManager particles = plugin.getServer().getPluginManager().getPlugin("karmor").getPluginMeta().getCustomField("particles");
        //                 // particles.triggerImpact(p, profile, type);
        //             }
        //         }
        //     }
        // }
    }

    private ItemStack getWornItemInSlot(Player player, ArmorSlot slot) {
        // Get item in specific armor slot
        switch(slot) {
            case HELMET: return player.getInventory().getHelmet();
            case CHEST: return player.getInventory().getChestplate();
            case LEGS: return player.getInventory().getLeggings();
            case BOOTS: return player.getInventory().getBoots();
        }
        return null;
    }


}
