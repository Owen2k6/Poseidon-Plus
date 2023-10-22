package net.minecraft.server;

public class RecipeIngots {

    private Object[][] a;

    public RecipeIngots() {
        this.a = new Object[][] { { Block.GOLD_BLOCK, new ItemStack(Item.GOLD_INGOT, 9)}, { Block.IRON_BLOCK, new ItemStack(Item.IRON_INGOT, 9)}, { Block.DIAMOND_BLOCK, new ItemStack(Item.DIAMOND, 9)}, { Block.LAPIS_BLOCK, new ItemStack(Item.INK_SACK, 9, 4)}};
    }

    public void a(CraftingManager craftingmanager) {
        for (Object[] objects : this.a)
        {
            Block block = (Block) objects[0];
            ItemStack itemstack = (ItemStack) objects[1];

            craftingmanager.registerShapedRecipe(new ItemStack(block), "###", "###", "###", '#', itemstack);
            craftingmanager.registerShapedRecipe(itemstack, "#", '#', block);
        }
    }
}
