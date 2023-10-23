package org.bukkit.craftbukkit.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Iterator;

public class CraftThreadManager {

    final HashSet<CraftWorker> workers = new HashSet<CraftWorker>();

    void executeTask(Runnable task, Plugin owner, int taskId) {

        CraftWorker craftWorker = new CraftWorker(this, task, owner, taskId);
        synchronized (workers) {
            workers.add(craftWorker);
        }

    }

    void interruptTask(int taskId) {
        synchronized (workers) {
            for (CraftWorker craftWorker : workers) {
                if (craftWorker.getTaskId() == taskId) {
                    craftWorker.interrupt();
                }
            }
        }
    }

    void interruptTasks(Plugin owner) {
        synchronized (workers) {
            for (CraftWorker craftWorker : workers) {
                if (craftWorker.getOwner().equals(owner)) {
                    craftWorker.interrupt();
                }
            }
        }
    }

    void interruptAllTasks() {
        synchronized (workers) {
            for (CraftWorker craftWorker : workers) {
                craftWorker.interrupt();
            }
        }
    }

    boolean isAlive(int taskId) {
        synchronized (workers) {
            for (CraftWorker craftWorker : workers) {
                if (craftWorker.getTaskId() == taskId) {
                    return craftWorker.isAlive();
                }
            }
            // didn't find it, so it must have been removed
            return false;
        }
    }
}
