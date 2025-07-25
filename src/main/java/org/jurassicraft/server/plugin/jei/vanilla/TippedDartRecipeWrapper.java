package org.jurassicraft.server.plugin.jei.vanilla;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import org.jurassicraft.server.item.ItemHandler;

import java.util.Arrays;
import java.util.List;

public class TippedDartRecipeWrapper implements IShapedCraftingRecipeWrapper {
    private final List<ItemStack> inputs;
    private final ItemStack output;

    public TippedDartRecipeWrapper(PotionType type) {
        ItemStack dartStack = new ItemStack(ItemHandler.DART_TRANQUILIZER);
        ItemStack lingeringPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), type);
        this.inputs = Arrays.asList(
                dartStack, dartStack, dartStack,
                dartStack, lingeringPotion, dartStack,
                dartStack, dartStack, dartStack
        );
        ItemStack outputStack = new ItemStack(ItemHandler.DART_TIPPED_POTION, 8);
        this.output = PotionUtils.addPotionToItemStack(outputStack, type);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, this.inputs);
        ingredients.setOutput(ItemStack.class, this.output);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }
}
