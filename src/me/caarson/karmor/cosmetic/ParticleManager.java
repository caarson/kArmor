package me.caarson.karmor.cosmetic;

import org.bukkit.Color;
import java.util.EnumSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumSet;
import me.caarson.karmor.config.ConfigManager;

public class ParticleManager {
    private final ConfigManager configManager;

    public ParticleManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    // Public API methods
    public void tickAuras(Player p, ActiveProfile profile, long nowNanos) {
        if (!profile.enabled || !configManager.getCosmeticsConfig().isEnabled()) return;
        
        for (Map.Entry<ArmorSlot, SlotPreset> entry : profile.slots.entrySet()) {
            ArmorSlot slot = entry.getKey();
            SlotPreset preset = entry.getValue();

            // Check triggers
            if (!preset.triggers.contains(Trigger.AURA)) continue;

            // Rate limiting
            long lastNanos = preset.lastAuraNanos;
            int rateTps = preset.rateTps;
            if (nowNanos - lastNanos < 1e9 / rateTps) continue;
            
            // Culling distance check
            double visibleRange = preset.radius + preset.visibleRange;
            boolean withinVisibleRange = isWithinVisibleRange(p, p.getWorld(), visibleRange);
            if (!withinVisibleRange) continue;

            // Particle spawning logic per slot
            spawnAuraParticles(p, slot, preset.style, preset.color, preset.scale, preset.density);
            
            // Update timestamp
            preset.lastAuraNanos = nowNanos;
        }
    }

    public void tickTrails(Player p, ActiveProfile profile, long nowNanos) {
        if (!profile.enabled || !configManager.getCosmeticsConfig().isEnabled()) return;

        for (Map.Entry<ArmorSlot, SlotPreset> entry : profile.slots.entrySet()) {
            ArmorSlot slot = entry.getKey();
            SlotPreset preset = entry.getValue();

            // Check triggers
            if (!preset.triggers.contains(Trigger.TRAIL)) continue;

            // Rate limiting
            long lastNanos = preset.lastTrailNanos;
            int rateTps = preset.rateTps;
            if (nowNanos - lastNanos < 1e9 / rateTps) continue;

            // Culling distance check
            double visibleRange = preset.radius + preset.visibleRange;
            boolean withinVisibleRange = isWithinVisibleRange(p, p.getWorld(), visibleRange);
            if (!withinVisibleRange) continue;

            // Movement detection with last location cache (simple)
            Vector velocity = p.getVelocity();
            double speed = velocity.length();
            
            if (speed > 0.08) {
                spawnTrailParticles(p, slot, preset.style, preset.color, preset.scale, preset.density);
            }
            
            // Update timestamp
            preset.lastTrailNanos = nowNanos;
        }
    }

    public void triggerImpact(Player p, ActiveProfile profile, ImpactType type) {
        if (!profile.enabled || !configManager.getCosmeticsConfig().isEnabled()) return;

        for (Map.Entry<ArmorSlot, SlotPreset> entry : profile.slots.entrySet()) {
            ArmorSlot slot = entry.getKey();
            SlotPreset preset = entry.getValue();

            // Check triggers
            EnumSet<Trigger> triggers = preset.triggers;
            switch(type) {
                case HIT:
                    if (!triggers.contains(Trigger.IMPACT_HIT)) continue;
                    spawnImpactParticles(p, preset.style, preset.color, preset.scale);
                    break;
                case KILL:
                    if (!triggers.contains(Trigger.IMPACT_KILL)) continue;
                    spawnImpactParticles(p, preset.style, preset.color, preset.scale);
                    break;
                case BLOCK:
                    if (!triggers.contains(Trigger.IMPACT_BLOCK)) continue;
                    spawnImpactParticles(p, preset.style, preset.color, preset.scale);
                    break;
            }
        }
    }

    public ActiveProfile getOrLoadProfile(ItemStack armorPiece) {
        PersistentDataContainer pdc = armorPiece.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(configManager.getPlugin(), "karmor", "cosmetic:particles");
        
        if (pdc.hasKey(key, PersistentDataType.STRING)) {
            String json = pdc.get(key, PersistentDataType.STRING);
            return parseJson(json);
        }
        
        return null;
    }

    public void clearProfileCache(ItemStack armorPiece) {
        PersistentDataContainer pdc = armorPiece.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(configManager.getPlugin(), "karmor", "cosmetic:particles");
        pdc.remove(key);
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

    // Persistence key: NamespacedKey("karmor","cosmetic:particles"); JSON schema:
    // {
    //   "style":"WINGS_FLAME",
    //   "color":"#FF6A00",
    //   "rateTps":6,
    //   "density":8,
    //   "radius":0.9,
    //   "scale":1.0,
    //   "triggers":["AURA","TRAIL","IMPACT_HIT"]
    // }

    private ActiveProfile parseJson(String json) {
        // Simple manual JSON parser - just enough for this schema
        try {
            String styleName = extractValue(json, "style", "\"");
            ParticleStyle style = ParticleStyle.valueOf(styleName.toUpperCase());
            
            String colorHex = extractValue(json, "color", "\"");
            Color color = Color.parseHex(colorHex);
            
            int rateTps = extractIntValue(json, "rateTps");
            int density = extractIntValue(json, "density");
            double radius = extractDoubleValue(json, "radius");
            double scale = extractDoubleValue(json, "scale");
            
            String triggersStr = extractValue(json, "triggers", "[");
            EnumSet<Trigger> triggers = parseTriggers(triggersStr);

            ActiveProfile profile = new ActiveProfile();
            SlotPreset preset = new SlotPreset(style, color, scale, rateTps, density, radius, triggers);
            // Assume single slot for now (simplified)
            profile.slots.put(ArmorSlot.HELMET, preset); // dummy slot
            return profile;
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
        StringBuilder sb = new StringBuilder("{");
        
        if (!profile.slots.isEmpty()) {
            SlotPreset preset = profile.slots.values().iterator().next(); // first slot
            
            sb.append("\"style\":\"" + preset.style.name() + "\",");
            sb.append("\"color\":\"#" + String.format("%02X%02X%02X", preset.color.getRed(), preset.color.getGreen(), preset.color.getBlue()) + "\",");
            sb.append("\"rateTps\":" + preset.rateTps + ",");
            sb.append("\"density\":" + preset.density + ",");
            sb.append("\"radius\":" + preset.radius + ",");
            sb.append("\"scale\":" + preset.scale + ",");
            
            // Serialize triggers
            StringBuilder triggerSb = new StringBuilder("[");
            for (Trigger trigger : preset.triggers) {
                triggerSb.append("\"" + trigger.name() + "\",");
            }
            if (triggerSb.length() > 1) {
                triggerSb.delete(triggerSb.length() - 1, triggerSb.length()); // remove last comma
            }
            triggerSb.append("]");
            
            sb.append("\"triggers\":" + triggerSb.toString());
        } else {
            sb.append("\"style\":\"AURA_ARCANE\",");
            sb.append("\"color\":\"#7F00FF\",");
            sb.append("\"rateTps\":5,");
            sb.append("\"density\":6,");
            sb.append("\"radius\":0.8,");
            sb.append("\"scale\":1.0,");
            sb.append("\"triggers\":[\"AURA\"]");
        }
        
        sb.append("}");
        return sb.toString();
    }

    private boolean isWithinVisibleRange(Player p, World world, double range) {
        // Check nearby players within visibleRange
        for (Player player : world.getPlayers()) {
            if (!player.equals(p)) { 
                Vector distance = player.getLocation().toVector().subtract(p.getLocation().toVector());
                double dist = distance.length();
                if (dist <= range) return true;
            }
        }
        return false; // no nearby players within visibleRange
    }

    private void spawnAuraParticles(Player p, ArmorSlot slot, ParticleStyle style, Color color, double scale, int density) {
        // Implementation per style
        switch(style) {
            case HELMET_HALO:
                // Spawn small circle of REDSTONE at head height +0.4 with radius around yaw
                Vector playerHead = p.getLocation().toVector().add(new Vector(0, 0.4, 0));
                double radius = scale * style.getRadius();
                spawnRedstoneParticles(playerHead, color, radius);
                break;
            case WINGS_FLAME:
                // 2 bezier-ish curves offset on the back (left/right), particles: FLAME
                spawnFlameParticles(p.getLocation().toVector(), color, density);
                break;
            case WINGS_FROST:
                // particles: SOUL_FIRE_FLAME/CLOUD for frost
                spawnSoulFireParticles(p.getLocation().toVector(), color, density);
                break;
            case LEGS_SWIRL:
                // vertical spiral from hips to knees using SPELL_WITCH or colored dust
                spawnSpellWitchParticles(p.getLocation().toVector(), color, density);
                break;
            case BOOTS_FOOTPRINTS:
                // spawn 2 short-lived CLOUD/CRIT at foot positions when moving
                spawnCloudCritParticles(p.getLocation().toVector(), color, density);
                break;
            default: // AURA_ARCANE, AURA_VOID, AURA_NATURE (standard)
                spawnRedstoneParticles(p.getLocation().toVector(), color, style.getRadius());
        }
    }

    private void spawnTrailParticles(Player p, ArmorSlot slot, ParticleStyle style, Color color, double scale, int density) {
        // Implementation per style
        switch(style) {
            case TRAIL_SPARK:
                // emit particles behind player along velocity vector
                Vector velocity = p.getVelocity();
                if (vel.length() > 0.08) {
                    spawnSparkParticles(p.getLocation().toVector(), vel, color, density);
                }
                break;
            case TRAIL_NOTE:
                // emit particles behind player along velocity vector
                Vector vel = p.getVelocity();
                if (vel.length() > 0.08) {
                    spawnNoteParticles(p.getLocation().toVector(), vel, color, density);
                }
                break;
            case TRAIL_HEART:
                // emit particles behind player along velocity vector
                Vector vel = p.getVelocity();
                if (vel.length() > 0.08) {
                    spawnHeartParticles(p.getLocation().toVector(), vel, color, density);
                }
                break;
            default: // standard trail - simple dust or spark
                spawnSparkParticles(p.getLocation().toVector(), p.getVelocity(), color, density);
        }
    }

    private void spawnImpactParticles(Player p, ParticleStyle style, Color color, double scale) {
        // Implementation per style
        switch(style) {
            case IMPACT_HIT_BURST:
                // radial burst of CRIT + (optional) colored dust at victim location
                Vector location = p.getLocation().toVector();
                spawnCritParticles(loc, color, 10);
                break;
            case IMPACT_KILL_BURST:
                // larger burst with SOUL + ENCHANTMENT_TABLE particles
                Vector loc = p.getLocation().toVector();
                spawnSoulParticles(loc, color, 15);
                spawnEnchantmentTableParticles(loc, color, 8);
                break;
            case IMPACT_BLOCK_BURST:
                // small burst of BLOCK_CRACK using the broken block’s material
                Vector loc = p.getLocation().toVector();
                spawnBlockCrackParticles(loc, color, 8);
                break;
            default: // standard impact - simple dust or crit
                spawnCritParticles(p.getLocation().toVector(), color, 10);
        }
    }

    private void spawnRedstoneParticles(Vector location, Color color, double radius) {
        // use Bukkit Particle.REDSTONE + DustOptions(color, scale)
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnFlameParticles(Vector location, Color color, int density) {
        // particles: FLAME
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnSoulFireParticles(Vector location, Color color, int density) {
        // particles: SOUL_FIRE_FLAME/CLOUD for frost
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnSpellWitchParticles(Vector location, Color color, int density) {
        // SPELL_WITCH or colored dust
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnCloudCritParticles(Vector location, Color color, int density) {
        // CLOUD/CRIT at foot positions when moving
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnSparkParticles(Vector location, Vector velocity, Color color, int density) {
        // emit TRAIL_* styles behind player along velocity vector
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnNoteParticles(Vector location, Vector velocity, Color color, int density) {
        // emit particles behind player along velocity vector
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnHeartParticles(Vector location, Vector velocity, Color color, int density) {
        // emit particles behind player along velocity vector
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnCritParticles(Vector location, Color color, int count) {
        // CRIT + colored dust at victim location
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnSoulParticles(Vector location, Color color, int count) {
        // SOUL particles for kill burst
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnEnchantmentTableParticles(Vector location, Color color, int count) {
        // ENCHANTMENT_TABLE particles for kill burst
        // (in actual implementation we'd need to call particle API here)
    }

    private void spawnBlockCrackParticles(Vector location, Color color, int count) {
        // BLOCK_CRACK using the broken block’s material
        // (in actual implementation we'd need to call particle API here)
    }
}
