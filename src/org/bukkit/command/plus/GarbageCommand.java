package org.bukkit.command.plus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class GarbageCommand extends VanillaCommand {
    public GarbageCommand() {
        super("garbagecollect");
        this.description = "Runs the java garbage collector on maximum cleanup";
        this.usageMessage = "/garbagecollect";
        this.setPermission("bukkit.command.gcl");
        this.setAliases(Arrays.asList("gcl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        sender.sendMessage(ChatColor.RED + "The server will now perform a hard garbage collection.");
        sender.sendMessage(ChatColor.RED + "This clears the memory usage as much as possible.");
        sender.sendMessage(ChatColor.RED + "If memory usage is still high, try restarting the server.");

        Runtime.getRuntime().gc();
        System.gc();

        sender.sendMessage(ChatColor.GREEN + "The server has issued the GC requests to the JVM.");
        sender.sendMessage(ChatColor.GREEN + "Within the next few seconds, the server will begin cleaning up.");

        return true;
    }

    @Override
    public boolean matches(String input) {
        return input.equals("garbagecollect") || input.equals("gcl");
    }
}
