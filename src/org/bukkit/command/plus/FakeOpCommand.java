package org.bukkit.command.plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class FakeOpCommand extends VanillaCommand {
    public FakeOpCommand() {
        super("fakeop");
        this.description = "Gives the specified player operator status... probably not. lol";
        this.usageMessage = "/fakeop <player>";
        this.setPermission("bukkit.command.op.give");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length != 1)  {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Command.broadcastCommandMessage(sender, "Totally Oping " + args[0]);

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (player instanceof Player) {
            ((Player)player).sendMessage(ChatColor.YELLOW + "You are now op!");
        }

        return true;
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("fakeop ");
    }
}
