package me.caarson.karmor.cosmetic;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import java.util.HashMap;
import java.util.Map;

public class CosmeticManager {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private Map<Player, BukkitTask> activeTasks = new HashMap<>();

    public CosmeticManager(ConfigManager configManager, Plugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
    }

    public void startTasks() {
        for (Player player : SetTracker.getActivePlayers()) {
            String setName = SetTracker.getSetForPlayer(player);
            if (configManager.getCosmeticSet(setName).isEnabled()) {
                int tickInterval = configManager.getCosmeticSet(setName).getTickInterval();
                BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                    applyCosmeticsToPlayer(player, setName);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand != null) {
                        applyCosmeticsToItem(itemInHand, player);
                    }
                }, 0, tickInterval);
                activeTasks.put(player, task);
            }
        }
    }

    public void stopTasks() {
        for (BukkitTask task : activeTasks.values()) {
            task.cancel();
        }
        activeTasks.clear();
    }

    private void applyCosmeticsToPlayer(Player player, String setName) {
        CosmeticSet cosmetics = configManager.getCosmeticSet(setName);
        
        // Apply particles
        if (cosmetics.isParticleEnabled()) {
            for (int i = 0; i < cosmetics.getParticleCount(); i++) {
                player.getWorld().spawnParticle(
                    cosmetics.getParticle(),
                    player.getLocation().add(cosmetics.getParticleOffset(), 0, 0),
                    cosmetics.getParticleSpeed()
                );
            }
        }
        
        // Apply sound
        if (cosmetics.isSoundEnabled()) {
            player.playSound(player.getLocation(), cosmetics.getSoundType(), cosmetics.getVolume(), cosmetics.getPitch());
        }
    }

    private void applyCosmeticsToItem(ItemStack item, Player player) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (pdc.hasKey(new NamespacedKey(plugin, "karmor", "cosmetic_enchants"), PersistentDataType.STRING_LIST)) {
            List<String> enchantIds = pdc.get(new NamespacedKey(plugin, "karmor", "cosmetic_enchants"), PersistentDataType.STRING_LIST);
            
            for (String enchantId : enchantIds) {
                CosmeticEnchant cosmeticEnchant = configManager.getCosmeticEnchant(enchantId);
                
                // Apply sound from cosmetic enchant
                if (cosmeticEnchant.isSoundEnabled()) {
                    player.playSound(player.getLocation(), cosmeticEnchant.getSoundType(), cosmeticEnchant.getVolume(), cosmeticEnchant.getPitch());
                }
            }
        }
    }

    public boolean isCosmeticsEnabled(String setName) {
        return configManager.getCosmeticSet(setName).isEnabled();
    }
}

// CosmeticSet class (from ConfigManager)
class CosmeticSet {
    private final ConfigurationSection section;

    public CosmeticSet(ConfigurationSection section) {
        this.section = section;
    }

    public boolean isEnabled() { return section.getBoolean("enabled", true); }
    public int getTickInterval() { return section.getInt("tick_interval", 10); }
    public String getParticle() { return section.getString("particle", "FLAME"); }
    public int getParticleCount() { return section.getInt("particle_count", 8); }
    public double getParticleOffset() { return section.getDouble("particle_offset", 0.2); }
    public double getParticleSpeed() { return section.getDouble("particle_speed", 0.01); }
    public boolean isSoundEnabled() { return section.getBoolean("sound.enabled", false); }
    public String getSoundType() { return section.getString("sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP"); }
    public float getVolume() { return (float) section.getDouble("sound.volume", 0.5); }
    public float getPitch() { return (float) section.getDouble("sound.pitch", 1.2); }
}
