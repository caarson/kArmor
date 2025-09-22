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
import me.caarson.karmor.set.ArmorPieceSpec;
import me.caarson.karmor.config.ConfigManager;
import java.util.List;

public class GivePieceCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public GivePieceCommand(ConfigManager configManager) {
        this.configManager = configManager;
        this.plugin = configManager.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission("karmor.admin")) {
            sender.sendMessage("You don't have permission for this command.");
            return false;
        }
        
        if (args.length != 3 || !(sender instanceof Player)) {
            sender.sendMessage("Usage: /karmor give <player> <set> <slot>");
            return false;
        }

        Player player = (Player) sender;
        String setName = args[1];
        String slot = args[2];

        ArmorPieceSpec pieceSpec = configManager.getArmorPieceSpec(setName, slot);
        
        if (pieceSpec == null) {
            sender.sendMessage("Invalid set or slot.");
            return false;
        }

        ItemStack item = pieceSpec.createItem();
        
        // Tag the item for kArmor
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        pdc.set(new NamespacedKey("karmor", "set"), PersistentDataType.STRING, setName);
        pdc.set(new NamespacedKey("karmor", "slot"), PersistentDataType.STRING, slot);

        // Append lore if configured
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
        
        sender.sendMessage(configManager.getMessagesPrefix() + 
            "Gave &e" + setName + "&7 &f" + slot + "&7 to %rankColor%"+player.getName()+"&7.");
        
        return true;
    }
}
