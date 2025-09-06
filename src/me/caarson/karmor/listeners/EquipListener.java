package me.caarson.karmor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;

public class EquipListener implements Listener {
    private final SetTracker setTracker;

    public EquipListener(SetTracker setTracker) {
        this.setTracker = setTracker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        setTracker.onPlayerArmorChange(event);
    }
}
