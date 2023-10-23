package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class ReloadCommand extends Command {
    public ReloadCommand(String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Collections.singletonList("rl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        if(args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            Bukkit.reload();
            sender.sendMessage(ChatColor.GREEN + "Reload complete.");
            sender.sendMessage(ChatColor.RED + "Remember, Restarting the server is much more efficient and stops your server from commiting die.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/reload is a bad idea. Please restart the server instead.");
        sender.sendMessage(ChatColor.RED + "This function is intended for development of a single plugin.");
        sender.sendMessage(ChatColor.RED + "To continue, type /reload confirm");



        return true;
    }
}
