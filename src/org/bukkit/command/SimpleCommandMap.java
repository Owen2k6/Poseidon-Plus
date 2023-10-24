package org.bukkit.command;

import org.bukkit.Server;
import org.bukkit.command.defaults.*;
import org.bukkit.command.plus.FakeOpCommand;
import org.bukkit.command.plus.GarbageCommand;
import org.bukkit.command.plus.ModifyConfigCommand;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.command.plus.RestartCommand;

import java.util.*;

import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;

public class SimpleCommandMap implements CommandMap {
    protected final Map<String, Command> knownCommands = new HashMap<String, Command>();
    protected final Set<String> aliases = new HashSet<String>();
    private final Server server;
    protected static final Set<VanillaCommand> fallbackCommands = new HashSet<VanillaCommand>();

    static {
        fallbackCommands.add(new ListCommand());
        fallbackCommands.add(new StopCommand());
        fallbackCommands.add(new SaveCommand());
        fallbackCommands.add(new SaveOnCommand());
        fallbackCommands.add(new SaveOffCommand());
        fallbackCommands.add(new OpCommand());
        fallbackCommands.add(new FakeOpCommand());
        fallbackCommands.add(new DeopCommand());
        fallbackCommands.add(new BanIpCommand());
        fallbackCommands.add(new PardonIpCommand());
        fallbackCommands.add(new BanCommand());
        fallbackCommands.add(new PardonCommand());
        fallbackCommands.add(new KickCommand());
        fallbackCommands.add(new TeleportCommand());
        fallbackCommands.add(new GiveCommand());
        fallbackCommands.add(new TimeCommand());
        fallbackCommands.add(new SayCommand());
        fallbackCommands.add(new WhitelistCommand());
        fallbackCommands.add(new TellCommand());
        fallbackCommands.add(new MeCommand());
        fallbackCommands.add(new KillCommand());
        fallbackCommands.add(new HelpCommand());
        fallbackCommands.add(new GarbageCommand());
        fallbackCommands.add(new ModifyConfigCommand());
        fallbackCommands.add(new RestartCommand());
    }

    public SimpleCommandMap(final Server server) {
        this.server = server;
        setDefaultCommands(server);
    }

    private void setDefaultCommands(final Server server) {
        register("poseidon", new PoseidonCommand("poseidon"));
        register("bukkit", new VersionCommand("version"));
        register("bukkit", new ReloadCommand("reload"));
        register("bukkit", new PluginsCommand("plugins"));
    }

    /**
     * {@inheritDoc}
     */
    public void registerAll(String fallbackPrefix, List<Command> commands) {
        if (commands != null) {
            for (Command c : commands) {
                register(fallbackPrefix, c);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(String fallbackPrefix, Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(String label, String fallbackPrefix, Command command) {
        boolean registeredPassedLabel = register(label, fallbackPrefix, command, false);

        command.getAliases().removeIf(o -> !register((String) o, fallbackPrefix, command, true));

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registeredPassedLabel;
    }

    private synchronized boolean register(String label, String fallbackPrefix, Command command, boolean isAlias) {
        String lowerLabel = label.trim().toLowerCase();

        if (isAlias && knownCommands.containsKey(lowerLabel)) {
            // Request is for an alias and it conflicts with a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        String lowerPrefix = fallbackPrefix.trim().toLowerCase();
        boolean registerdPassedLabel = true;

        // If the command exists but is an alias we overwrite it, otherwise we rename it based on the fallbackPrefix
        while (knownCommands.containsKey(lowerLabel) && !aliases.contains(lowerLabel)) {
            lowerLabel = lowerPrefix + ":" + lowerLabel;
            registerdPassedLabel = false;
        }

        if (isAlias) {
            aliases.add(lowerLabel);
        } else {
            // Ensure lowerLabel isn't listed as a alias anymore and update the commands registered name
            aliases.remove(lowerLabel);
            command.setLabel(lowerLabel);
        }
        knownCommands.put(lowerLabel, command);

        return registerdPassedLabel;
    }

    protected Command getFallback(String label) {
        for (VanillaCommand cmd : fallbackCommands) {
            if (cmd.matches(label)) {
                return cmd;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] args = commandLine.split(" ");

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        Command target = getCommand(sentCommandLabel);
        if (target == null) {
            target = getFallback(commandLine.toLowerCase());
        }
        if (target == null) {
            return false;
        }

        try {
            // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }

    public synchronized void clearCommands() {
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            entry.getValue().unregister(this);
        }
        knownCommands.clear();
        aliases.clear();
        setDefaultCommands(server);
    }

    public Command getCommand(String name) {
        return knownCommands.get(name.toLowerCase());
    }

    public void registerServerAliases() {
        Map<String, String[]> values = server.getCommandAliases();

        for (String alias : values.keySet()) {
            String[] targetNames = values.get(alias);
            List<Command> targets = new ArrayList<Command>();
            StringBuilder bad = new StringBuilder();

            for (String name : targetNames) {
                Command command = getCommand(name);

                if (command == null) {
                    bad.append(name).append(", ");
                } else {
                    targets.add(command);
                }
            }

            // We register these as commands so they have absolute priority.

            if (!targets.isEmpty()) {
                knownCommands.put(alias.toLowerCase(), new MultipleCommandAlias(alias.toLowerCase(), targets.toArray(new Command[0])));
            } else {
                knownCommands.remove(alias.toLowerCase());
            }

            if (bad.length() > 0) {
                bad = new StringBuilder(bad.substring(0, bad.length() - 2));
                server.getLogger().warning("The following command(s) could not be aliased under '" + alias + "' because they do not exist: " + bad);
            }
        }
    }
}
