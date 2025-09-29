package me.caarson.karmor.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import me.caarson.karmor.set.ArmorSet;
import me.caarson.karmor.set.ArmorPieceSpec;
import me.caarson.karmor.config.ConfigManager;
import java.util.List;
import java.util.EnumSet;
import me.caarson.karmor.cosmetic.ParticleManager;

public class GiveSetCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public GiveSetCommand(ConfigManager configManager) {
        this.configManager = configManager;
        this.plugin = configManager.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission("karmor.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return false;
        }
        
        if (args.length != 1 || !(sender instanceof Player)) {
            sender.sendMessage("§cUsage: §7/" + label + " giveset <set>");
            return false;
        }

        Player player = (Player) sender;
        String setName = args[0];

        plugin.getLogger().info("GiveSetCommand: Looking for armor set '" + setName + "'");
        ArmorSet set = configManager.getArmorSet(setName);
        
        if (set == null) {
            plugin.getLogger().warning("GiveSetCommand: Armor set '" + setName + "' not found in configuration");
            sender.sendMessage("§cInvalid armor set. Please check the set name and try again.");
            return false;
        }
        
        plugin.getLogger().info("GiveSetCommand: Found armor set '" + setName + "', creating pieces...");

        // Create and tag all pieces
        for (String slot : new String[]{"helmet", "chestplate", "legs", "boots"}) {
            ArmorPieceSpec pieceSpec = set.getPiece(slot);
            
            if (pieceSpec == null) {
                plugin.getLogger().warning("GiveSetCommand: No piece found for slot '" + slot + "' in set '" + setName + "'");
                continue;
            }
            
            plugin.getLogger().info("GiveSetCommand: Creating item for slot '" + slot + "'");
            ItemStack item = pieceSpec.createItem();
            
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "karmor_set"), PersistentDataType.STRING, setName);
            pdc.set(new NamespacedKey(plugin, "karmor_slot"), PersistentDataType.STRING, slot);

            // Apply cosmetic profile for testing particles
            String style = getDefaultStyleForSlot(slot);
            String profileJson = createCosmeticProfile(style, "#FFFFFF", 5, 6, 0.8, 1.0, new String[]{"AURA"});
            pdc.set(new NamespacedKey(plugin, "karmor.cosmetic.particles"), PersistentDataType.STRING, profileJson);

            if (configManager.isAppendLoreInsteadOfReplace()) {
                List<String> currentLore = item.getItemMeta().getLore();
                List<String> newLore = pieceSpec.getLore();
                if (currentLore == null) {
                    currentLore = new java.util.ArrayList<>();
                }
                currentLore.addAll(newLore);
                item.getItemMeta().setLore(currentLore);
            }

            player.getInventory().addItem(item);
        }
        
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', 
            configManager.getMessagesPrefix() + "&aSuccessfully granted &e" + setName + " &aarmor set with cosmetic particles to &b" + player.getName() + "&a."));
        
        return true;
    }

    private String getDefaultStyleForSlot(String slot) {
        switch (slot) {
            case "helmet": return "HELMET_HALO";
            case "chestplate": return "WINGS_FLAME";
            case "legs": return "LEGS_SWIRL";
            case "boots": return "BOOTS_FOOTPRINTS";
            default: return "AURA_ARCANE";
        }
    }

    private String createCosmeticProfile(String style, String color, int rateTps, int density, double radius, double scale, String[] triggers) {
        return String.format("{\"style\":\"%s\",\"color\":\"%s\",\"rateTps\":%d,\"density\":%d,\"radius\":%.1f,\"scale\":%.1f,\"triggers\":[\"%s\"]}",
            style, color, rateTps, density, radius, scale, String.join("\",\"", triggers));
    }
}
