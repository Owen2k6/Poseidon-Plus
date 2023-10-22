package net.minecraft.server;

import java.util.Comparator;

class RecipeSorter implements Comparator<CraftingRecipe> {

    final CraftingManager a;

    RecipeSorter(CraftingManager craftingmanager) {
        this.a = craftingmanager;
    }

    public int compare(CraftingRecipe o1, CraftingRecipe o2) {
        return o1 instanceof ShapelessRecipes && o2 instanceof ShapedRecipes ? 1 : (o2 instanceof ShapelessRecipes && o1 instanceof ShapedRecipes ? -1 : (Integer.compare(o2.a(), o1.a())));
    }
}
