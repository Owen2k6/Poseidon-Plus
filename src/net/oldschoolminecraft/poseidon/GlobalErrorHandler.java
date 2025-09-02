package net.oldschoolminecraft.poseidon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class GlobalErrorHandler implements Thread.UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        if (e instanceof StackOverflowError)
        {
            System.out.println("[Poseidon Watchdog] Critical stack overflow detected in thread: " + t.getName() + " (" + t.getId() + "/" + t.getState() + ")");
            System.out.println("[Poseidon Watchdog] A world save is being forced to prevent data loss...");
            Bukkit.broadcastMessage(ChatColor.RED + "[SEVERE] Critical error. Forcing world save.");

            try
            {
                Bukkit.getServer().savePlayers();
                Bukkit.getServer().getWorlds().forEach(World::save);
                System.out.println("[Poseidon Watchdog] World save completed. A stacktrace will now be produced.");
                Bukkit.broadcastMessage(ChatColor.RED + "[SEVERE] World save completed.");
            } catch (Exception ex) {
                System.out.println("[Poseidon Watchdog] World save FAILED. A stacktrace will now be produced.");
                Bukkit.broadcastMessage(ChatColor.RED + "[SEVERE] World save FAILED.");
            }

            System.out.println("[Poseidon Watchdog] ----- Stack Trace -----");
            Arrays.stream(t.getStackTrace())
                  .forEach(stackTraceElement -> System.out.println("[Poseidon Watchdog] " + stackTraceElement));
            System.out.println("[Poseidon Watchdog] ----- End of Stack Trace -----");

            ObjectLogger.logObjectOverride("stackoverflow-log." + System.currentTimeMillis() + ".json", t, "SKIP_STATIC");

            System.out.println("[Poseidon Watchdog] Shutdown imminent!");
            Bukkit.broadcastMessage(ChatColor.RED + "[SEVERE] Stack overflow detected. Server shutdown imminent.");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));

            for (Player ply : Bukkit.getOnlinePlayers())
                ply.kickPlayer(ChatColor.RED + "Critical error. Server shutting down.");

            Bukkit.shutdown();
        }
    }
}
