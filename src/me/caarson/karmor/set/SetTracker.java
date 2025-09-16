package me.caarson.karmor.set;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class SetTracker {
    private final ConfigManager configManager;
    private final Plugin plugin;
    private Map<Player, String> activeSets = new HashMap<>();
    private Set<Player> activePlayers = new HashSet<>();

    public SetTracker(ConfigManager configManager, Plugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
    }

    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is wearing armor set (or removing)
        String setName = getSetForPlayer(player);
        if (setName != null && !configManager.getCosmeticSet(setName).isEnabled()) {
            clearPlayerSet(player);
            return;
        }
        
        // Update active players
        if (getActivePlayers().contains(player) && setName == null) {
            clearPlayerSet(player); // player removed armor set
        } else if (!getActivePlayers().contains(player) && setName != null) {
            setActivePlayer(player); // player started wearing armor set
        }
    }

    public String getSetForPlayer(Player player) {
        return activeSets.getOrDefault(player, null);
    }

    public void setActivePlayer(Player player) {
        activePlayers.add(player);
    }

    public void clearPlayerSet(Player player) {
        activeSets.remove(player);
        activePlayers.remove(player);
    }

    public Set<Player> getActivePlayers() {
        return activePlayers;
    }
    
    // Add a helper method to set the player's armor set
    public void setPlayerSet(Player player, String setName) {
        activeSets.put(player, setName);
    }

    // Helper: check if player has armor in slot (if armor piece exists)
    private boolean isArmorInSlot(Player player, String slotName) {
        return player.getInventory().getArmorContents().getItem(slotName).getType() != org.bukkit.Material.AIR;
    }
}
