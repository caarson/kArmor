package me.caarson.karmor.cosmetic;

import org.bukkit.plugin.Plugin;
import java.util.EnumSet;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;
import me.caarson.karmor.cosmetic.ParticleManager;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.set.SetTracker;

public class CosmeticManager {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private Map<Player, BukkitTask> activeTasks = new HashMap<>();
    private ParticleManager particleManager;

    public CosmeticManager(ConfigManager configManager, Plugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
        this.particleManager = new ParticleManager(configManager);
    }

    public ParticleManager particles() { 
        return particleManager; 
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Optional<ParticleManager.ActiveProfile> loadProfile(ItemStack armorPiece) {
        System.out.println("DEBUG: loadProfile called for armor piece");
        PersistentDataContainer pdc = armorPiece.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        if (pdc.has(key, PersistentDataType.STRING)) {
            System.out.println("DEBUG: Found particle profile data on armor piece");
            // Create a profile with actual slot data
            ParticleManager.ActiveProfile profile = new ParticleManager.ActiveProfile();
            profile.enabled = true;
            
            // Create a default slot preset for helmet
            ParticleManager.SlotPreset preset = new ParticleManager.SlotPreset(
                ParticleStyle.WINGS_FLAME,
                org.bukkit.Color.fromRGB(127, 0, 255), // Purple color
                1.0, // scale
                5,   // rateTps
                6,   // density
                0.8, // radius
                java.util.EnumSet.of(ParticleManager.Trigger.AURA)
            );
            
            // Add the preset to the helmet slot
            profile.slots.put(ParticleManager.ArmorSlot.HELMET, preset);
            System.out.println("DEBUG: Created profile with " + profile.slots.size() + " slots");
            
            return Optional.of(profile);
        } else {
            System.out.println("DEBUG: No particle profile data found on armor piece");
        }
        return Optional.empty();
    }

    public void saveProfile(ItemStack armorPiece, ParticleManager.ActiveProfile profile) {
        System.out.println("DEBUG: saveProfile called for armor piece");
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        // Create a simple JSON string for the default profile
        String json = "{\"style\":\"WINGS_FLAME\",\"color\":\"#7F00FF\",\"rateTps\":5,\"density\":6,\"radius\":0.8,\"scale\":1.0,\"triggers\":[\"AURA\"]}";
        System.out.println("DEBUG: Saving JSON: " + json);
        
        // Get and update item meta
        org.bukkit.inventory.meta.ItemMeta meta = armorPiece.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json);
            armorPiece.setItemMeta(meta);
            System.out.println("DEBUG: Particle profile saved to armor piece");
        } else {
            System.out.println("DEBUG: Could not get item meta for armor piece");
        }
    }

    public void clearProfileCache(ItemStack armorPiece) {
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        armorPiece.getItemMeta().getPersistentDataContainer().remove(key);
    }

    public void startTasks() {
        System.out.println("=== COSMETIC DEBUG: Starting particle tasks ===");
        // Start a global task that runs every 10 ticks (0.5 seconds) to spawn particles for all online players
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            System.out.println("=== COSMETIC DEBUG: Particle task tick ===");
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                System.out.println("DEBUG: Checking player: " + player.getName());
                
                // Check all armor slots for particle profiles
                for (ArmorSlot slot : ArmorSlot.values()) {
                    ItemStack armorPiece = getArmorItem(player, slot);
                    if (armorPiece != null) {
                        System.out.println("DEBUG: Found armor piece in slot: " + slot);
                        Optional<ParticleManager.ActiveProfile> profile = loadProfile(armorPiece);
                        if (profile.isPresent()) {
                            System.out.println("DEBUG: Found particle profile for slot: " + slot);
                            System.out.println("DEBUG: Calling tickAuras for player: " + player.getName());
                            particleManager.tickAuras(player, profile.get(), System.nanoTime());
                        } else {
                            System.out.println("DEBUG: No particle profile for slot: " + slot);
                        }
                    } else {
                        System.out.println("DEBUG: No armor piece in slot: " + slot);
                    }
                }
            }
        }, 0, 10); // Run immediately, then every 10 ticks (0.5 seconds)
        
        activeTasks.put(null, task); // Use null as key for global task
        System.out.println("=== COSMETIC DEBUG: Particle tasks started ===");
    }

    private ItemStack getArmorItem(Player player, ArmorSlot slot) {
        switch(slot) {
            case HELMET: return player.getInventory().getHelmet();
            case CHEST: return player.getInventory().getChestplate();
            case LEGS: return player.getInventory().getLeggings();
            case BOOTS: return player.getInventory().getBoots();
            default: return null;
        }
    }

public static class CosmeticSet {
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




    public enum Trigger { AURA, TRAIL, IMPACT_HIT, IMPACT_KILL, IMPACT_BLOCK }

    public enum ImpactType { HIT, KILL, BLOCK }

    public enum ArmorSlot { HELMET, CHEST, LEGS, BOOTS }

public static class Color {
        private final int r, g, b;
        public Color(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }

        public static Color parseHex(String hexString) {
            if (hexString.startsWith("#")) hexString = hexString.substring(1);
            return new Color(
                Integer.parseInt(hexString.substring(0, 2), 16),
                Integer.parseInt(hexString.substring(2, 4), 16),
                Integer.parseInt(hexString.substring(4, 6), 16)
            );
        }

        public int getR() { return r; }
        public int getG() { return g; }
        public int getB() { return b; }
    }
}
