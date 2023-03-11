package org.bukkit.command.plus;

import com.legacyminecraft.poseidon.PlusConfig;
import com.legacyminecraft.poseidon.PoseidonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

public class RestartCommand extends VanillaCommand {

    String shuttingDownMessage = "&bServer is restarting, please reconnect in a few minutes.";
    public RestartCommand() {
        super("restart");
        this.description = "*Restarts* the server";
        this.usageMessage = "/restart";
        this.setPermission("bukkit.command.stop");
        shuttingDownMessage = PlusConfig.getInstance().getString("messages.kick.restart", "&bServer is restarting, please reconnect in a few minutes.");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Command.broadcastCommandMessage(sender, "Starting Server Restart, Saving Data.");

        ((CraftServer) Bukkit.getServer()).setShuttingdown(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.saveData();
            player.kickPlayer((ChatColor.translateAlternateColorCodes('&', shuttingDownMessage)));;
        }
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(new PoseidonPlugin(), () -> {
            Command.broadcastCommandMessage(sender, "Restarting the server..");
            Bukkit.shutdown();
        }, 100);

        return true;
    }

    @Override
    public boolean matches(String input) {
        return input.equals("restart");
    }
}
