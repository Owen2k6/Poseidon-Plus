package org.bukkit.command.plus;

import com.projectposeidon.johnymuffin.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

public class SetUUIDCommand extends VanillaCommand
{

    public SetUUIDCommand()
    {
        super("setuuid");
        this.description = "Change a player's cached UUID";
        this.usageMessage = "/setuuid <username> <uuid> <premium>";
        this.setPermission("bukkit.command.setuuid");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        if (!testPermission(sender))
            return false;
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        String username = args[0];
        String uuid = args[1];
        boolean online = args[2].equals("true");

        try
        {
            long unixTime = (System.currentTimeMillis() / 1000L) + 1382400;
            UUIDManager.getInstance().receivedUUID(username, UUID.fromString(uuid), unixTime, online);
            UUIDManager.getInstance().saveJsonArray();

            sender.sendMessage(ChatColor.GREEN + "Changed UUID of " + username + " to: " + uuid);

            String cachedUUID = UUIDManager.getInstance().getUUIDFromUsername(username).toString();
            sender.sendMessage(ChatColor.GRAY + "Cached UUID for " + username + " is now: " + cachedUUID);
        } catch (IllegalArgumentException ignored) {
            sender.sendMessage(ChatColor.RED + "Invalid UUID specified");
        }

        return true;
    }

    @Override
    public boolean matches(String input)
    {
        return input.startsWith("setuuid ");
    }
}
