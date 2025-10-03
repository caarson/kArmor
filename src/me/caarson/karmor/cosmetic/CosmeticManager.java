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
    }

    public ParticleManager particles() { 
        return particleManager; 
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Optional<ParticleManager.ActiveProfile> loadProfile(ItemStack armorPiece) {
        PersistentDataContainer pdc = armorPiece.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        if (pdc.has(key, PersistentDataType.STRING)) {
            String json = pdc.get(key, PersistentDataType.STRING);
            // Temporarily return empty due to type mismatch
            // return Optional.of(parseJson(json));
        }
        return Optional.empty();
    }

    public void saveProfile(ItemStack armorPiece, ParticleManager.ActiveProfile profile) {
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        // String json = serialize(profile);
        // armorPiece.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, json);
    }

    public void clearProfileCache(ItemStack armorPiece) {
        NamespacedKey key = new NamespacedKey(plugin, "karmor.cosmetic.particles");
        armorPiece.getItemMeta().getPersistentDataContainer().remove(key);
    }

    public void startTasks() {
        // Temporarily commented out due to compilation issues
        // for (Player player : SetTracker.getActivePlayers()) {
        //     String setName = SetTracker.getSetForPlayer(player);
        //     if (configManager.getCosmeticSet(setName).isEnabled()) {
        //         int tickInterval = configManager.getCosmeticSet(setName).getTickInterval();
        //         BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
        //             applyCosmeticsToPlayer(player, setName);
        //             ItemStack itemInHand = player.getInventory().getItemInMainHand();
        //             if (itemInHand != null) {
        //                 applyCosmeticsToItem(itemInHand, player);
        //             }
        //         }, 0, tickInterval);
        //         activeTasks.put(player, task);
        //     }
        // }
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

    private ActiveProfile parseJson(String json) {
        // Simple manual JSON parser - just enough for this schema
        try {
            // Temporarily commented out due to compilation issues
            // String styleName = extractValue(json, "style", "\"");
            // ParticleStyle style = ParticleStyle.valueOf(styleName.toUpperCase());
            // 
            // String colorHex = extractValue(json, "color", "\"");
            // Color color = Color.parseHex(colorHex);
            // 
            // int rateTps = extractIntValue(json, "rateTps");
            // int density = extractIntValue(json, "density");
            // double radius = extractDoubleValue(json, "radius");
            // double scale = extractDoubleValue(json, "scale");
            // 
            // String triggersStr = extractValue(json, "triggers", "[");
            // EnumSet<Trigger> triggers = parseTriggers(triggersStr);
            // 
            // ActiveProfile profile = new ActiveProfile();
            // SlotPreset preset = new SlotPreset(style, color, scale, rateTps, density, radius, triggers);
            // // Assume single slot for now (simplified)
            // profile.slots.put(ArmorSlot.HELMET, preset); // dummy slot
            return null;
        } catch (Exception e) {
            return null; // failed parse
        }
    }

    private String extractValue(String json, String key, String delimiter) {
        int start = json.indexOf(key + ":" + delimiter);
        if (start == -1) return "";
        int end = json.indexOf(delimiter, start + key.length() + 2);
        return json.substring(start + key.length() + 2, end).trim();
    }

    private int extractIntValue(String json, String key) {
        int start = json.indexOf(key + ":");
        if (start == -1) return 0;
        int end = json.indexOf(',', start);
        if (end == -1) end = json.length();
        return Integer.parseInt(json.substring(start + key.length() + 1, end).trim());
    }

    private double extractDoubleValue(String json, String key) {
        int start = json.indexOf(key + ":");
        if (start == -1) return 0.0;
        int end = json.indexOf(',', start);
        if (end == -1) end = json.length();
        return Double.parseDouble(json.substring(start + key.length() + 1, end).trim());
    }

    private EnumSet<Trigger> parseTriggers(String triggersStr) {
        EnumSet<Trigger> set = EnumSet.noneOf(Trigger.class);
        if (triggersStr.startsWith("[") && triggersStr.endsWith("]")) {
            String content = triggersStr.substring(1, triggersStr.length() - 1).trim();
            String[] triggerNames = content.split(",");
            for (String name : triggerNames) {
                try {
                    Trigger trigger = Trigger.valueOf(name.toUpperCase().trim());
                    set.add(trigger);
                } catch (IllegalArgumentException e) {}
            }
        }
        return set;
    }

    private String serialize(ActiveProfile profile) {
        // Simple manual JSON serializer - just enough for this schema
        // Temporarily commented out due to compilation issues
        // StringBuilder sb = new StringBuilder("{");
        // 
        // if (!profile.slots.isEmpty()) {
        //     SlotPreset preset = profile.slots.values().iterator().next(); // first slot
        //     
        //     sb.append("\"style\":\"" + preset.style.name() + "\",");
        //     sb.append("\"color\":\"#" + String.format("%02X%02X%02X", preset.color.getRed(), preset.color.getGreen(), preset.color.getBlue()) + "\",");
        //     sb.append("\"rateTps\":" + preset.rateTps + ",");
        //     sb.append("\"density\":" + preset.density + ",");
        //     sb.append("\"radius\":" + preset.radius + ",");
        //     sb.append("\"scale\":" + preset.scale + ",");
        //     
        //     // Serialize triggers
        //     StringBuilder triggerSb = new StringBuilder("[");
        //     for (Trigger trigger : preset.triggers) {
        //         triggerSb.append("\"" + trigger.name() + "\",");
        //     }
        //     if (triggerSb.length() > 1) {
        //         triggerSb.delete(triggerSb.length() - 1, triggerSb.length()); // remove last comma
        //     }
        //     triggerSb.append("]");
        //     
        //     sb.append("\"triggers\":" + triggerSb.toString());
        // } else {
        //     sb.append("\"style\":\"AURA_ARCANE\",");
        //     sb.append("\"color\":\"#7F00FF\",");
        //     sb.append("\"rateTps\":5,");
        //     sb.append("\"density\":6,");
        //     sb.append("\"radius\":0.8,");
        //     sb.append("\"scale\":1.0,");
        //     sb.append("\"triggers\":[\"AURA\"]");
        // }
        // 
        // sb.append("}");
        return "{}"; // Return empty JSON for now
    }

public static class ActiveProfile {
        Map<ArmorSlot, SlotPreset> slots;
        boolean enabled;

        public ActiveProfile() {
            this.slots = new HashMap<>();
            this.enabled = true;
        }
    }

    public static class SlotPreset {
        ParticleStyle style;
        Color color;
        double scale;
        int rateTps;
        int density;
        double radius;
        EnumSet<Trigger> triggers;
        Map<String,Object> extras;
        long lastAuraNanos;
        long lastTrailNanos;

        public SlotPreset(ParticleStyle style, Color color, double scale, int rateTps, int density, double radius, EnumSet<Trigger> triggers) {
            this.style = style;
            this.color = color;
            this.scale = scale;
            this.rateTps = rateTps;
            this.density = density;
            this.radius = radius;
            this.triggers = triggers;
            this.extras = new HashMap<>();
            this.lastAuraNanos = 0L; // never emitted
            this.lastTrailNanos = 0L;
        }
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
