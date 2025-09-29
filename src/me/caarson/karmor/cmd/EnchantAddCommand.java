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
public class EnchantAddCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public EnchantAddCommand(ConfigManager configManager) {
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
        
        // Parse command args: /karmor enchant add <cosmeticId> [level]
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage("Usage: /karmor enchant add <cosmeticId> [level]");
            return false;
        }

        String cosmeticId = args[1];
        int level = 0;
        if (args.length == 3 && !args[2].isEmpty()) {
            try {
                level = Integer.parseInt(args[2]);
                if (level <= 0 || level > configManager.getCosmeticEnchant(cosmeticId).getMaxLevel()) {
                    sender.sendMessage(configManager.getMessagesPrefix() + 
                        "Invalid level for cosmetic enchant '" + cosmeticId + "'. Max level is " + 
                        configManager.getCosmeticEnchant(cosmeticId).getMaxLevel());
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Level must be a valid integer.");
                return false;
            }
        }

        // Add to PDC list
        PersistentDataContainer pdc = itemInHand.getItemMeta().getPersistentDataContainer();
        List<String> enchantIds = new ArrayList<>();
if (pdc.has(new NamespacedKey(plugin, "karmor.cosmetic_enchants"), PersistentDataType.STRING)) {
            String enchantIdsString = pdc.get(new NamespacedKey(plugin, "karmor.cosmetic_enchants"), PersistentDataType.STRING);
            if (enchantIdsString != null && !enchantIdsString.isEmpty()) {
                enchantIds = new ArrayList<>(Arrays.asList(enchantIdsString.split(",")));
            }
        }

        // Ensure the cosmeticId isn't already present (if needed)
        if (!enchantIds.contains(cosmeticId)) {
            enchantIds.add(cosmeticId);
            String enchantIdsString = String.join(",", enchantIds);
            pdc.set(new NamespacedKey(plugin, "karmor.cosmetic_enchants"), 
                PersistentDataType.STRING, enchantIdsString);
        }

        sender.sendMessage(configManager.getMessagesPrefix() + 
            "Added &e" + cosmeticId + "&7 to item in hand.");
        
        return true;
    }
}
