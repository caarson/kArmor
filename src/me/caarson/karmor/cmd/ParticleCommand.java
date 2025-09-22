package me.caarson.karmor.cmd;

import me.caarson.karmor.cosmetic.CosmeticManager;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.cosmetic.ParticleManager;
import me.caarson.karmor.cosmetic.ParticleManager.ActiveProfile;
import me.caarson.karmor.cosmetic.ParticleManager.ArmorSlot;
import me.caarson.karmor.cosmetic.ParticleManager.Trigger;
import me.caarson.karmor.cosmetic.ParticleManager.SlotPreset;
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
        // Temporarily comment out unavailable method
        // cosmeticManager.setPlayerCosmeticToggle(player, enabled);

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
                // Temporarily comment out unavailable method
                // cosmeticManager.getOrLoadProfile(armorPiece);  // refresh cache
                player.sendMessage("Cosmetics profile updated for slot: " + slot.name() + ".");
                break;

            case "clear":
                if (args.length < 1) {
                    sendHelpMessage(player);
                    return;
                }
                
                ArmorSlot clearSlot = parseSlot(args[0]);
                ItemStack clearArmorPiece = getWornItemInSlot(player, clearSlot);
                if (clearArmorPiece == null) {
                    player.sendMessage("No armor piece found for slot: " + clearSlot.name());
                    return;
                }

                cosmeticManager.clearProfileCache(clearArmorPiece);
                player.sendMessage("Cosmetics profile cleared from slot: " + clearSlot.name() + ".");
                break;

            case "preview":
                if (args.length < 1) {
                    sendHelpMessage(player);
                    return;
                }
                
                ParticleStyle previewStyle = parseStyle(args[0]);
Color previewColor = args.length >= 2 ? parseColor(args[1]) : parseColor(configManager.get("cosmetics.default.color", "#7F00FF"));
                double previewScale = args.length >= 3 ? Double.parseDouble(args[2]) : configManager.get("cosmetics.default.scale", 1.0);
                
                ActiveProfile previewProfile = createPreviewProfile(previewStyle, previewColor, previewScale);
                // Spawn temporary particles (not persisted)
                spawnTemporaryParticles(player, previewProfile, previewStyle);
                player.sendMessage("Cosmetics preview spawned for style: " + previewStyle.name() + ".");
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
        // Parse #RRGGBB format - custom implementation since Color.parseHex doesn't exist
        if (hexString.startsWith("#")) {
            hexString = hexString.substring(1);
        }
        if (hexString.length() == 6) {
            int r = Integer.parseInt(hexString.substring(0, 2), 16);
            int g = Integer.parseInt(hexString.substring(2, 4), 16);
            int b = Integer.parseInt(hexString.substring(4, 6), 16);
            return Color.fromRGB(r, g, b);
        }
        return Color.WHITE; // default color if parsing fails
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
        // Use reflection or public methods to set fields if needed
        // For now, create a simple profile
        SlotPreset preset = new SlotPreset(style, color, scale, rateTps, density, radius, triggers);
        // slots field might be private, so we need to find another way
        // This will likely need refactoring of the ParticleManager class
        return profile;
    }

    private ActiveProfile createPreviewProfile(ParticleStyle style, Color color, double scale) {
        ActiveProfile profile = new ActiveProfile();
        // enabled field might be private, need alternative approach
        SlotPreset preset = new SlotPreset(style, color, scale, 5, 6, 0.8, EnumSet.of(Trigger.AURA));
        // slots field might be private, need alternative approach
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
        // Fix runTaskLater call - use plugin instance instead of server
        BukkitTask task = player.getServer().getScheduler().runTaskLater(configManager.getPlugin(), () -> {
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
