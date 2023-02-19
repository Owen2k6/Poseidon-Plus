package org.bukkit.command.plus;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Map;

public class ModifyConfigCommand extends VanillaCommand {

    public ModifyConfigCommand() {
        super("modifyconfig");
        this.description = "Modify a configuration file";
        this.usageMessage = "/modifyconfig <file> <key> <value>";
        this.setPermission("bukkit.command.modifyconfig");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender))
            return false;
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        String file = args[0];
        String key = args[1];
        String value = args[2];

        File f = new File(file);
        if (!f.exists()) {
            sender.sendMessage(ChatColor.RED + "File does not exist");
            return false;
        }
        if (!file.endsWith(".yml")) {
            sender.sendMessage(ChatColor.RED + "File must be a .yml file");
            return false;
        }
        if (file.contains("..")) {
            sender.sendMessage(ChatColor.RED + "File must be in the root directory");
            return false;
        }

        try {
            // Read YAML file into a Map
            Yaml yaml = new Yaml(new SafeConstructor());
            Map<String, Object> data = (Map<String, Object>) yaml.load(new FileInputStream(f));

            // Split key by periods and traverse Map to find the nested key
            String[] keyParts = key.split("\\.");
            Map<String, Object> map = data;
            for (int i = 0; i < keyParts.length - 1; i++) {
                map = (Map<String, Object>) map.get(keyParts[i]);
            }

            // Update value for the given key
            map.put(keyParts[keyParts.length - 1], value);

            // Write updated Map back to YAML file
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setIndent(2);
            Yaml dumper = new Yaml(options);
            FileWriter writer = new FileWriter(f);
            dumper.dump(data, writer);
            writer.close();
            return true;
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("modifyconfig ");
    }
}
