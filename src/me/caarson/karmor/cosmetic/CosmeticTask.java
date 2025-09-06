package me.caarson.karmor.cosmetic;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CosmeticTask {
    public static class StartEvent extends Event {
        private final Player player;

        public StartEvent(Player player) {
            this.player = player;
        }

        public Player getPlayer() { return player; }
    }

    public static class StopEvent extends Event {
        private final Player player;

        public StopEvent(Player player) {
            this.player = player;
        }

        public Player getPlayer() { return player; }
    }
}
