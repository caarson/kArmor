package me.caarson.karmor.cmd;

import me.caarson.karmor.cosmetic.CosmeticManager;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.cosmetic.ParticleManager;
import me.caarson.karmor.cosmetic.ParticleManager.ActiveProfile;
import me.caarson.karmor.cosmetic.ParticleManager.ArmorSlot;
import me.caarson.karmor.cosmetic.ParticleManager.Trigger;
import me.caarson.karmor.cosmetic.ParticleStyle;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleCommand implements CommandExecutor {
    private final CosmeticManager cosmeticManager;
    private final ConfigManager configManager;

    public ParticleCommand(CosmeticManager cosmeticManager, ConfigManager configManager) {
        this.cosmeticManager = cosmeticManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        String subcommand = args.length > 0 ? args[0] : "help";

        switch(subcommand) {
            case "on":
            case "off": handleToggle(player, subcommand);
                break;
            case "set":
            case "clear":
            case "preview":
            case "list":
                handleSubcommand(player, subcommand, Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                sendHelpMessage(player);
                return true;
        }
        return true;
    }

    private void handleToggle(Player player, String toggle) {
        boolean enabled = toggle.equals("on");
        cosmeticManager.setPlayerCosmeticToggle(player, enabled);

        // Message confirmation
        if (enabled) {
            player.sendMessage("Cosmetics toggled ON for your worn armor pieces.");
        } else {
            player.sendMessage("Cosmetics toggled OFF for your worn armor pieces.");
        }
    }

    private void handleSubcommand(Player player, String subcommand, String[] args) {
        switch(subcommand) {
            case "set":
                if (args.length < 3) {
                    sendHelpMessage(player);
                    return;
                }
                
                // Parse arguments: slot, style, color, scale, rateTps, density, radius, triggers
                ArmorSlot slot = parseSlot(args[0]);
                ParticleStyle style = parseStyle(args[1]);
                Color color = parseColor(args[2]);
                double scale = args.length >= 4 ? Double.parseDouble(args[3]) : configManager.get("cosmetics.default.scale", 1.0);
                int rateTps = args.length >= 5 ? Integer.parseInt(args[4]) : configManager.get("cosmetics.default.rateTps", 5);
                int density = args.length >= 6 ? Integer.parseInt(args[5]) : configManager.get("cosmetics.default.density", 6);
                double radius = args.length >= 7 ? Double.parseDouble(args[6]) : configManager.get("cosmetics.default.radius", 0.8);

                String triggersArg = args.length >= 8 ? args[7] : "AURA";
                EnumSet<Trigger> triggers = parseTriggers(triggersArg);
                
                ActiveProfile profile = createProfileFromArgs(slot, style, color, scale, rateTps, density, radius, triggers);
                
                // Get current worn item in the slot (if exists)
                ItemStack armorPiece = getWornItemInSlot(player, slot);
                if (armorPiece == null) {
                    player.sendMessage("No armor piece found for slot: " + slot.name());
                    return;
                }

                cosmeticManager.saveProfile(armorPiece, profile);
                cosmeticManager.getOrLoadProfile(armorPiece);  // refresh cache
                player.sendMessage("Cosmetics profile updated for slot: " + slot.name() + ".");
                break;

            case "clear":
                if (args.length < 1) {
                    sendHelpMessage(player);
                    return;
                }
                
                ArmorSlot slot = parseSlot(args[0]);
                ItemStack armorPiece = getWornItemInSlot(player, slot);
                if (armorPiece == null) {
                    player.sendMessage("No armor piece found for slot: " + slot.name());
                    return;
                }

                cosmeticManager.clearProfileCache(armorPiece);
                player.sendMessage("Cosmetics profile cleared from slot: " + slot.name() + ".");
                break;

            case "preview":
                if (args.length < 1) {
                    sendHelpMessage(player);
                    return;
                }
                
                ParticleStyle style = parseStyle(args[0]);
Color color = args.length >= 2 ? parseColor(args[1]) : Color.parseHex(configManager.get("cosmetics.default.color", "#7F00FF"));
                double scale = args.length >= 3 ? Double.parseDouble(args[2]) : configManager.get("cosmetics.default.scale", 1.0);
                
                ActiveProfile profile = createPreviewProfile(style, color, scale);
                // Spawn temporary particles (not persisted)
                spawnTemporaryParticles(player, profile, style);
                player.sendMessage("Cosmetics preview spawned for style: " + style.name() + ".");
                break;

            case "list":
                sendSupportedStylesList(player);
                break;
        }
    }

    private ArmorSlot parseSlot(String slotName) {
        try {
            return ArmorSlot.valueOf(slotName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ParticleStyle parseStyle(String styleName) {
        try {
            return ParticleStyle.valueOf(styleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Color parseColor(String hexString) {
        // Parse #RRGGBB format
        if (!hexString.startsWith("#")) hexString = "#" + hexString;
        return Color.parseHex(hexString);
    }

    private EnumSet<Trigger> parseTriggers(String triggersArg) {
        String[] triggerNames = triggersArg.split(",");
        EnumSet<Trigger> set = EnumSet.noneOf(Trigger.class);
        for (String name : triggerNames) {
            try {
                Trigger trigger = Trigger.valueOf(name.toUpperCase());
                set.add(trigger);
            } catch (IllegalArgumentException e) {}
        }
        return set;
    }

    private ActiveProfile createProfileFromArgs(ArmorSlot slot, ParticleStyle style, Color color, double scale, int rateTps, int density, double radius, EnumSet<Trigger> triggers) {
        ActiveProfile profile = new ActiveProfile();
            profile.enabled = true; // always enabled for set
        SlotPreset preset = new SlotPreset(style, color, scale, rateTps, density, radius, triggers);
        profile.slots.put(slot, preset);
        return profile;
    }

    private ActiveProfile createPreviewProfile(ParticleStyle style, Color color, double scale) {
        ActiveProfile profile = new ActiveProfile();
        profile.enabled = true; // always enabled for preview
        SlotPreset preset = new SlotPreset(style, color, scale, 5, 6, 0.8, EnumSet.of(Trigger.AURA));
        profile.slots.put(ArmorSlot.HELMET, preset);
        return profile;
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

    private void spawnTemporaryParticles(Player player, ActiveProfile profile, ParticleStyle style) {
        // Spawn particles for 10 seconds without persistence
        long startTime = System.nanoTime();
        BukkitTask task = player.getServer().getScheduler().runTaskLater(player.getServer(), () -> {
            if (System.nanoTime() - startTime > 1e9 * 10) {
                // After 10 seconds, cancel task and stop spawning
                return;
            }
            
            ParticleManager particles = cosmeticManager.particles();
            particles.tickAuras(player, profile, System.nanoTime());
        }, 20);
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("Usage: /carmor particles <subcommand> [args]");
        player.sendMessage("/carmor particles on|off - toggle cosmetics");
        player.sendMessage("/carmor particles set <slot> <style> [#rrggbb] [scale] [rateTps] [density] [radius] [triggers=commaList]");
        player.sendMessage("/carmor particles clear <slot>");
        player.sendMessage("/carmor particles preview <style> [#rrggbb] [scale]");
        player.sendMessage("/carmor particles list - show supported styles");
    }

    private void sendSupportedStylesList(Player player) {
        StringBuilder sb = new StringBuilder("Supported cosmetic styles: ");
        for (ParticleStyle style : ParticleStyle.values()) {
            if (style.isColorable()) {
                sb.append(style.name() + " (colorable), ");
            } else {
                sb.append(style.name() + ", ");
            }
        }
        player.sendMessage(sb.toString());
    }

}
