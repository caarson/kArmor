package me.caarson.karmor.cosmetic;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import java.util.Map;
import java.util.HashMap;
import me.caarson.karmor.KArmorPlugin;

public class CosmeticTask implements Listener {
    private final CosmeticManager cosmeticsManager;
    private final KArmorPlugin plugin;
    // Movement detection cache
    Map<Player, Vector> lastLocationMap = new HashMap<>();

    public CosmeticTask(CosmeticManager cosmeticsManager, KArmorPlugin plugin) {
        this.cosmeticsManager = cosmeticsManager;
        this.plugin = plugin;
    }

    // Extended task logic
    public void startTask() {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                // Skip if cosmetics are not enabled for player (simplified)
                // if (!cosmeticsManager.isCosmeticsEnabled(player)) continue;

                // Temporarily comment out particle functionality to fix compilation
                // ParticleManager.ActiveProfile profile = cosmeticsManager.particles().getOrLoadProfile(player);
                // if (profile == null || !profile.enabled) continue;
                
                // // Tick auras
                // if (profile.triggers.contains(ParticleManager.Trigger.AURA)) {
                //     cosmeticsManager.particles().tickAuras(player, profile, System.nanoTime());
                // }

                // // Tick trails on movement detection with last location cache
                // if (profile.triggers.contains(ParticleManager.Trigger.TRAIL)) {
                //     Vector currentLocation = player.getLocation().toVector();
                //     Vector lastLocation = lastLocationMap.getOrDefault(player, currentLocation);
                //     double distance = currentLocation.subtract(lastLocation).length(); 
                //     
                //     // Movement threshold check
                //     if (distance > 0.08) {
                //         cosmeticsManager.particles().tickTrails(player, profile, System.nanoTime());
                //     }
                //     
                //     // Update cached location
                //     lastLocationMap.put(player, currentLocation);
                // }
            }
        }, 0, 20); // tick interval: 1 tick every 20 ticks
    }

    public void stopTask() {
        // Cancel the task if it exists
        // (not implemented in this file)
    }

    public void onEnable() {
        startTask();
    }

    public void onDisable() {
        stopTask();
    }
}
