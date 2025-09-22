package me.caarson.karmor;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.set.ArmorPieceSpec;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ItemTagger implements CommandExecutor {
    private final ConfigManager configManager;
    private final Plugin plugin;

    public ItemTagger(ConfigManager configManager) {
        this.configManager = configManager;
        this.plugin = configManager.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("karmor.admin")) {
            sender.sendMessage("You don't have permission for this command.");
            return false;
        }

        if (args.length != 3 || !(sender instanceof Player)) {
            sender.sendMessage("Usage: /karmor tagFromHand <player> <set> <slot>");
            return false;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            sender.sendMessage("Hold an item in your main hand.");
            return false;
        }

        String setName = args[1];
        String slot = args[2];
        // ArmorPieceSpec pieceSpec = configManager.getArmorPieceSpec(setName, slot); // Temporarily commented out

        // Tag the item
        PersistentDataContainer pdc = itemInHand.getItemMeta().getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "karmor_set"), PersistentDataType.STRING, setName);
        pdc.set(new NamespacedKey(plugin, "karmor_slot"), PersistentDataType.STRING, slot);

        // Append lore (temporarily disabled due to compilation issues)
        // if (configManager.isAppendLoreInsteadOfReplace()) {
        //     List<String> existingLore = itemInHand.getItemMeta().getLore();
        //     List<String> newLore = pieceSpec.getLore();
        //     itemInHand.setItemMeta(itemInHand.getItemMeta().setLore(existingLore + newLore));
        // }

        // Merge enchants (temporarily disabled due to compilation issues)
        // if (configManager.isMergeVanillaEnchants()) {
        //     Map<String, Integer> pieceEnchants = pieceSpec.getEnchants();
        //     for (Map.Entry<String, Integer> entry : pieceEnchants.entrySet()) {
        //         Enchantment enchant = Enchantment.getByKey(entry.getKey());
        //         int level = entry.getValue();
        //         int currentLevel = itemInHand.getEnchantLevel(enchant);

        //         if (!itemInHand.hasEnchant(enchant)) {
        //             itemInHand.addEnchant(enchant, level, true);
        //         } else {
        //             if (configManager.isRespectMaxLevels() && currentLevel >= level) {
        //                 continue;
        //             }
        //             itemInHand.addEnchant(enchant, level, true);
        //         }
        //     }
        // }

        sender.sendMessage("Tagged the item in hand as " + setName + " " + slot + ".");
        return true;
    }
}
