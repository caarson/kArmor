package me.caarson.karmor;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.cosmetic.CosmeticManager;
import me.caarson.karmor.cosmetic.ParticleManager;
import me.caarson.karmor.cosmetic.CosmeticTask;
import me.caarson.karmor.listeners.HitListener;
import me.caarson.karmor.listeners.EquipListener;
import me.caarson.karmor.cmd.ParticleCommand;
import me.caarson.karmor.set.SetTracker;

public class KArmorPlugin extends JavaPlugin {
    private final ConfigManager configManager;
    private final CosmeticManager cosmeticManager;
    private final ParticleManager particleManager;
    private final CosmeticsTask cosmeticsTask;
    private final HitListener hitListener;
    private final EquipListener equipListener;
    private final ParticleCommand particleCommand;
    private final SetTracker setTracker;

@Override
    public void onEnable() {
        // Initialize configuration manager (already exists)
        configManager = new ConfigManager(this);

        // Bootstrap cosmetic manager
        cosmeticManager = new CosmeticManager(configManager, this);
        cosmeticsTask = new CosmeticsTask(cosmeticManager, this);
        particleManager = new ParticleManager(configManager);
        cosmeticManager.setParticleManager(particleManager);

        // Initialize tracker for set detection
        setTracker = new SetTracker(configManager, this); // Now added

        // Register listeners for equip/unequip events
        equipListener = new EquipListener(cosmeticManager, setTracker);
        getServer().getPluginManager().registerEvents(equipListener, this);

        // Register hit listener (new)
        hitListener = new HitListener(this);
        getServer().getPluginManager().registerEvents(hitListener, this);

        // Start cosmetics task
        cosmeticsTask.onEnable();

        // Register command executor for /carmor particles
        particleCommand = new ParticleCommand(cosmeticManager, configManager);
        if (hasCommand("carmor")) {
            getCommand("carmor").setExecutor(particleCommand);
        } else {
            registerCommand("carmor", particleCommand);
        }
    }

    @Override
    public void onDisable() {
        // Stop cosmetics task and clear listeners
        cosmeticsTask.onDisable();
        getServer().getPluginManager().unregisterEvents(equipListener, this);
        getServer().getPluginManager().unregisterEvents(hitListener, this);
    }

    private boolean hasCommand(String name) {
        return getCommand(name) != null;
    }

    private void registerCommand(String name, CommandExecutor executor) {
        if (name.equals("carmor")) {
            // Register command as root
            getServer().getPluginManager().registerCommand(new PluginCommand(this), new String[]{name});
        } else {
            // Otherwise, register the subcommand via a main handler class
            // This assumes you have an existing command dispatcher that routes /carmor to other commands
            // (already implemented in KArmorPlugin)
        }
    }

    public ConfigManager getConfigManager() { return configManager; }
}
