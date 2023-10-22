package net.minecraft.server;

public class RecipesFood {

    public RecipesFood() {}

    public void a(CraftingManager craftingmanager) {
        craftingmanager.registerShapedRecipe(new ItemStack(Item.MUSHROOM_SOUP), "Y", "X", "#", 'X', Block.BROWN_MUSHROOM, 'Y', Block.RED_MUSHROOM, '#', Item.BOWL);
        craftingmanager.registerShapedRecipe(new ItemStack(Item.MUSHROOM_SOUP), "Y", "X", "#", 'X', Block.RED_MUSHROOM, 'Y', Block.BROWN_MUSHROOM, '#', Item.BOWL);
        craftingmanager.registerShapedRecipe(new ItemStack(Item.COOKIE, 8), "#X#", 'X', new ItemStack(Item.INK_SACK, 1, 3), '#', Item.WHEAT);
    }
}
