package net.minecraft.server;

public class ItemBlockPistonMoving extends ItemBlock {
    public ItemBlockPistonMoving(int id) {
        super(id);
        this.a(true); // Set hasSubtypes to true
    }

    @Override
    public int filterData(int metadata) {
        return metadata;
    }
}
