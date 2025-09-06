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

public class EnchantListCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public EnchantListCommand(ConfigManager configManager) {
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
        
        // Check for cosmetic enchant list
        PersistentDataContainer pdc = itemInHand.getItemMeta().getPersistentDataContainer();
        if (pdc.hasKey(new NamespacedKey(plugin, "karmor", "cosmetic_enchants"), PersistentDataType.STRING_LIST)) {
            List<String> enchantIds = pdc.get(
                new NamespacedKey(plugin, "karmor", "cosmetic_enchants"),
                PersistentDataType.STRING_LIST
            );
            
            if (enchantIds.isEmpty()) {
                sender.sendMessage(configManager.getMessagesPrefix() + 
                    "No cosmetic enchants on item.");
                return false;
            }
            
            sender.sendMessage(configManager.getMessagesPrefix() + 
                configManager.getEnchantListHeader());
                
            for (String enchantId : enchantIds) {
                CosmeticEnchant cosmeticEnchant = configManager.getCosmeticEnchant(enchantId);
                sender.sendMessage(
                    configManager.getMessagesPrefix() +
                    " &8- &e" + enchantId + "&7(level " + 
                    cosmeticEnchant.getMaxLevel() + ")"
                );
            }
            
            return true;
        } else {
            sender.sendMessage(configManager.getMessagesPrefix() + 
                "No cosmetic enchants on item.");
            return false;
        }
    }
}
