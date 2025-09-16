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
import me.caarson.karmor.config.ConfigManager;
import java.util.List;

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
            sender.sendMessage("You don't have permission for this command.");
            return false;
        }
        
        if (args.length != 2 || !(sender instanceof Player)) {
            sender.sendMessage("Usage: /karmor giveSet <player> <set>");
            return false;
        }

        Player player = (Player) sender;
        String setName = args[1];

        ArmorSet set = configManager.getArmorSet(setName);
        
        if (set == null) {
            sender.sendMessage("Invalid set.");
            return false;
        }

        // Create and tag all pieces
        for (String slot : new String[]{"helmet", "chestplate", "legs", "boots"}) {
            ArmorPieceSpec pieceSpec = set.getPiece(slot);
            
            ItemStack item = pieceSpec.createItem();
            
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "karmor", "set"), PersistentDataType.STRING, setName);
            pdc.set(new NamespacedKey(plugin, "karmor", "slot"), PersistentDataType.STRING, slot);

            if (configManager.isAppendLoreInsteadOfReplace()) {
                List<String> newLore = pieceSpec.getLore();
                item.setItemMeta(item.getItemMeta().setLore(item.getItemMeta().getLore() + newLore));
            }

            player.getInventory().addItem(item);
        }
        
        sender.sendMessage(configManager.getMessagesPrefix() +
            "Gave &e" + setName + "&7 full set to %rankColor%"+player.getName()+"&7.");
        
        return true;
    }
}
