package me.caarson.karmor.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import me.caarson.karmor.config.ConfigManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class EnchantRemoveCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public EnchantRemoveCommand(ConfigManager configManager) {
        this.configManager = configManager;
        this.plugin = configManager.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission("karmor.admin")) {
            sender.sendMessage("You don't have permission for this command.");
            return false;
        }
        
        // Validate: must hold an item in main hand
        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(configManager.getMessagesPrefix() + "Hold an item in your main hand.");
            return false;
        }
        
        // Parse command args: /karmor enchant remove <cosmeticId>
        if (args.length != 2) {
            sender.sendMessage("Usage: /karmor enchant remove <cosmeticId>");
            return false;
        }

        String cosmeticId = args[1];
        PersistentDataContainer pdc = itemInHand.getItemMeta().getPersistentDataContainer();
        
        // Check for PDC list and remove
        if (pdc.has(new NamespacedKey(plugin, "cosmetic_enchants"), PersistentDataType.STRING)) {
            String enchantIdsString = pdc.get(new NamespacedKey(plugin, "cosmetic_enchants"), PersistentDataType.STRING);
            if (enchantIdsString != null && !enchantIdsString.isEmpty()) {
                List<String> enchantIds = new ArrayList<>(Arrays.asList(enchantIdsString.split(",")));
                
                if (enchantIds.contains(cosmeticId)) {
                    enchantIds.remove(cosmeticId);
                    String newEnchantIdsString = String.join(",", enchantIds);
                    pdc.set(new NamespacedKey(plugin, "cosmetic_enchants"), 
                        PersistentDataType.STRING, newEnchantIdsString);
                    
                    sender.sendMessage(configManager.getMessagesPrefix() + 
                        "Removed &e" + cosmeticId + "&7 from item in hand.");
                    return true;
                }
            }
        }
        
        sender.sendMessage(configManager.getMessagesPrefix() + "Cosmetic enchant '" + cosmeticId + "' not found on item.");
        return false;
    }
}
