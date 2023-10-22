package com.legacyminecraft.poseidon;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class PlusConfig extends Configuration
{
    private static PlusConfig instance;

    private PlusConfig()
    {
        super(new File("plus.yml"));
        this.reload();
    }

    public void reload()
    {
        this.load();
        this.write();
        this.save();
    }

    private void write()
    {
        generateConfigOption("messages.player.join", "§e%player% joined the game.");
        generateConfigOption("messages.player.leave", "§e%player% left the game.");
        generateConfigOption("messages.kick.ban", "&cYou have been banned from this server.");
        generateConfigOption("messages.kick.whitelist", "&cServer currently whitelisted. Please try again later.");
        generateConfigOption("messages.kick.shutdown", "&cThe server is shutting down.");
        generateConfigOption("messages.kick.restart", "&cThe server is restarting.");
        generateConfigOption("messages.kick.full", "&cThe server is full.");
        generateConfigOption("messages.kick.alternateLocation", "&cYou logged in from another location!");
        generateConfigOption("messages.broadcast.prefix", "&d[Server]");
        generateConfigOption("game.weather.lightning.can-start-fire", true);
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null)
        {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    //Getters Start
    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null)
        {
            value = defaultValue;
        }
        return value;

    }

    public String getConfigString(String key)
    {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key)
    {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key)
    {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key)
    {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key)
    {
        return Boolean.valueOf(getConfigString(key));
    }

    //Getters End

    private boolean convertToNewAddress(String newKey, String oldKey)
    {
        if (this.getString(newKey) != null)
        {
            return false;
        }
        if (this.getString(oldKey) == null)
        {
            return false;
        }
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;
    }

    public static PlusConfig getInstance()
    {
        if (instance == null) instance = new PlusConfig();
        return instance;
    }
}
