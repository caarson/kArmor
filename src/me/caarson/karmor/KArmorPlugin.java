package me.caarson.karmor;

import org.bukkit.plugin.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;

public class KArmorPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private SetTracker setTracker;
    private CosmeticManager cosmeticManager;
    private PhoenixBridge phoenixBridge;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        setTracker = new SetTracker(configManager, this);
        cosmeticManager = new CosmeticManager(configManager, this);
        phoenixBridge = new PhoenixBridge(this);

        getCommand("karmor").setExecutor(new KArmorMainCommandHandler(this));
        
        getServer().getPluginManager().registerEvents(
            new EquipListener(setTracker, cosmeticManager), 
            this
        );
        getServer().getPluginManager().registerEvents(
            new AnvilListener(configManager),
            this
        );

        phoenixBridge.initialize();
    }

    @Override
    public void onDisable() {
        setTracker.cleanup();
        cosmeticManager.stopTasks();
        phoenixBridge.shutdown();
    }
}

// This class handles all subcommands under 'karmor'
class KArmorMainCommandHandler implements CommandExecutor {
    private final KArmorPlugin plugin;

    public KArmorMainCommandHandler(KArmorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false; // Show help message? But no specific handling for empty.
        }

        String subcommand = args[0];
        switch (subcommand.toLowerCase()) {
            case "give":
                new GivePieceCommand(plugin.getConfigManager()).onCommand(sender, command, label, args);
                break;
            case "giveSet":
                new GiveSetCommand(plugin.getConfigManager()).onCommand(sender, command, label, args);
                break;
            case "tagFromHand":
                new ItemTagger(plugin.getConfigManager()).onCommand(sender, command, label, args);
                break;
            case "enchant":
                if (args.length > 1) {
                    String enchantSub = args[1].toLowerCase();
                    switch (enchantSub) {
                        case "add":
                            new EnchantAddCommand(plugin.getConfigManager()).onCommand(sender, command, label, args);
                            break;
                        case "remove":
                            new EnchantRemoveCommand(plugin.getConfigManager()).onCommand(sender, command, label, args);
                            break;
                        case "list":
                            new EnchantListCommand(plugin.getConfigManager()).onCommand(sender, command, label, args);
                            break;
                    }
                }
                break;
            case "reload":
                new ReloadCommand().onCommand(sender, command, label, args);
                break;
            default:
                sender.sendMessage("Invalid subcommand. Use /karmor help for details.");
                return false;
        }
        return true;
    }
}
