package me.caarson.karmor.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class KArmorCommand implements CommandExecutor {
    private final ParticleCommand particleCommand;
    private final GiveSetCommand giveSetCommand;

    public KArmorCommand(ParticleCommand particleCommand, GiveSetCommand giveSetCommand) {
        this.particleCommand = particleCommand;
        this.giveSetCommand = giveSetCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subcommand) {
            case "help":
                sendHelpMessage(sender);
                break;
            case "particles":
                if (sender instanceof Player) {
                    return particleCommand.onCommand(sender, command, label, subArgs);
                } else {
                    sender.sendMessage("§cThis command can only be used by players.");
                }
                break;
            case "giveset":
                if (sender instanceof Player) {
                    return giveSetCommand.onCommand(sender, command, label, subArgs);
                } else {
                    sender.sendMessage("§cThis command can only be used by players.");
                }
                break;
            default:
                sender.sendMessage("§cUnknown subcommand. Use §e/" + label + " help §cfor available commands.");
                break;
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== KArmor Help ===");
        sender.sendMessage("§b/" + getCommandName(sender) + " help §7- Show this help menu");
        sender.sendMessage("§b/" + getCommandName(sender) + " particles <subcommand> §7- Manage armor cosmetics");
        sender.sendMessage("§b/" + getCommandName(sender) + " giveset <set> §7- Give a full armor set with cosmetics (Admin)");
        sender.sendMessage("§eParticles Subcommands:");
        sender.sendMessage("§d  on|off §7- Toggle cosmetics on/off");
        sender.sendMessage("§d  set <slot> <style> [#color] §7- Set cosmetics for armor slot");
        sender.sendMessage("§d  clear <slot> §7- Clear cosmetics from armor slot");
        sender.sendMessage("§d  preview <style> [#color] §7- Preview a cosmetic style");
        sender.sendMessage("§d  list §7- Show supported cosmetic styles");
        sender.sendMessage("§6Available slots: §aHELMET, CHEST, LEGS, BOOTS");
        sender.sendMessage("§6Use §a/" + getCommandName(sender) + " particles list §6to see available styles");
    }

    private String getCommandName(CommandSender sender) {
        // Determine if the command was used with alias 'karmor' or 'carmor'
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // This is a simple approach; in practice, you might need to track the actual command used
            return "carmor"; // Default to main command name
        }
        return "carmor";
    }
}
