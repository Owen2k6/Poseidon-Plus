package net.minecraft.server;

public class RecipesCrafting {

    public RecipesCrafting() {}

    public void a(CraftingManager craftingmanager) {
        craftingmanager.registerShapedRecipe(new ItemStack(Block.CHEST), "###", "# #", "###", '#', Block.WOOD);
        craftingmanager.registerShapedRecipe(new ItemStack(Block.FURNACE), "###", "# #", "###", '#', Block.COBBLESTONE);
        craftingmanager.registerShapedRecipe(new ItemStack(Block.WORKBENCH), "##", "##", '#', Block.WOOD);
        craftingmanager.registerShapedRecipe(new ItemStack(Block.SANDSTONE), "##", "##", '#', Block.SAND);
    }
}
