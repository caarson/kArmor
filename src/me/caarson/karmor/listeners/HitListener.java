package me.caarson.karmor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.EnumSet;
import me.caarson.karmor.cosmetic.CosmeticManager.ActiveProfile;
import me.caarson.karmor.cosmetic.ParticleManager.Trigger;

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
        checkAndTriggerImpact(player, ImpactType.HIT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // Check if the victim was wearing armor with impact triggers (kill)
        checkAndTriggerImpact(player, ImpactType.KILL);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Check if the breaker is wearing armor with impact triggers (block break)
        checkAndTriggerImpact(player, ImpactType.BLOCK);
    }
    
    private void checkAndTriggerImpact(Player p, ImpactType type) {
        // Get current worn items
        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = getWornItemInSlot(p, slot);
            if (item != null && !item.getType().equals(Material.AIR)) {
                ActiveProfile profile = getProfileFromItem(item);
                if (profile != null) {
                    // Check triggers for this type
                    boolean shouldTrigger = false;
                    switch(type) {
                        case HIT:
                            shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_HIT); break;
                        case KILL:
                            shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_KILL); break;
                        case BLOCK:
                            shouldTrigger = profile.slots.get(slot).triggers.contains(Trigger.IMPACT_BLOCK); break;
                    }
                    
                    if (shouldTrigger) {
                        // Trigger impact particle effect
                        ParticleManager particles = plugin.getServer().getPluginManager().getPlugin("karmor").getPluginMeta().getCustomField("particles");
                        particles.triggerImpact(p, profile, type);
                    }
                }
            }
        }
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

    private ActiveProfile getProfileFromItem(ItemStack item) {
        // Get the profile from persistent data container
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "karmor", "cosmetic:particles");
        
        if (pdc.hasKey(key, PersistentDataType.STRING)) {
            String json = pdc.get(key, PersistentDataType.STRING);
            return parseJson(json);
        }
        return null;
    }

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

}
