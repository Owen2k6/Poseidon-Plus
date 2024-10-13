package net.minecraft.server;

public class ItemBlockPistonExtension extends ItemBlock {
    public ItemBlockPistonExtension(int id) {
        super(id);
        this.a(true); // Set hasSubtypes to true
    }

    @Override
    public int filterData(int metadata) {
        return metadata;
    }
}
