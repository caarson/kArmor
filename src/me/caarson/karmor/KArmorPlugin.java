package me.caarson.karmor;

import org.bukkit.plugin.java.JavaPlugin;
import me.caarson.karmor.config.ConfigManager;
import me.caarson.karmor.cmd.KArmorCommand;
import me.caarson.karmor.cmd.ParticleCommand;
import me.caarson.karmor.cmd.GiveSetCommand;
import me.caarson.karmor.cosmetic.CosmeticManager;

public class KArmorPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private CosmeticManager cosmeticManager;

    @Override
    public void onEnable() {
        // Initialize configuration manager
        configManager = new ConfigManager(this);
        cosmeticManager = new CosmeticManager(configManager, this);
        
        // Register command executors
        ParticleCommand particleCommand = new ParticleCommand(cosmeticManager, configManager);
        GiveSetCommand giveSetCommand = new GiveSetCommand(configManager);
        KArmorCommand kArmorCommand = new KArmorCommand(particleCommand, giveSetCommand);
        
        getCommand("carmor").setExecutor(kArmorCommand);
        
        getLogger().info("KArmor plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("KArmor plugin disabled!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
}
