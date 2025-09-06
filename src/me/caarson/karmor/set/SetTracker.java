package me.caarson.karmor.set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorChangeEvent;
import org.bukkit.plugin.Plugin;
import java.util.Map;
import java.util.HashMap;

public class SetTracker {
    private final ConfigManager configManager;
    private final Plugin plugin;
    private Map<Player, String> playerSetMap = new HashMap<>(); // Player -> set name

    public SetTracker(ConfigManager configManager, Plugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
    }

    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        String setName = getSetForPlayer(player);

        if (event.getSlot().equals("helmet") || event.getSlot().equals("chestplate") ||
            event.getSlot().equals("legs") || event.getSlot().equals("boots")) {

            // Check for full set
            boolean isFullSet = configManager.getArmorSet(setName).isFullSetEquipped(player);
            
            if (isFullSet) {
                playerSetMap.put(player, setName);
                // Start cosmetics tasks if enabled
                plugin.getServer().getPluginManager().callEvent(new CosmeticTask.StartEvent(player));
            } else {
                playerSetMap.remove(player);
                // Stop cosmetics tasks
                plugin.getServer().getPluginManager().callEvent(new CosmeticTask.StopEvent(player));
            }
        }
    }

    public String getSetForPlayer(Player player) {
        if (playerSetMap.containsKey(player)) {
            return playerSetMap.get(player);
        } else {
            for (String setName : configManager.getConfig().getKeys("sets")) {
                if (configManager.getArmorSet(setName).isFullSetEquipped(player)) {
                    return setName;
                }
            }
            return null; // Not wearing any set
        }
    }

    public boolean isPlayerWearingFullSet(Player player) {
        return getSetForPlayer(player) != null;
    }

    public void cleanup() {
        playerSetMap.clear();
    }

    public Map<Player, String> getActivePlayers() {
        return new HashMap<>(playerSetMap);
    }
}
