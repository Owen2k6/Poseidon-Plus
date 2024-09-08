package net.oldschoolminecraft.poseidon;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockEvent;

public class RedstoneTNTIgnitionEvent extends BlockEvent implements Cancellable
{
    private boolean cancelled;

    public RedstoneTNTIgnitionEvent(Block theBlock)
    {
        super(Type.EXPLOSION_PRIME, theBlock);
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }
}
