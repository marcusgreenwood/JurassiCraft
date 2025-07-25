package org.jurassicraft.server.plugin.jei.category.fossilgrinder;

import com.google.common.collect.Lists;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.text.TextFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FossilGrinderRecipeWrapper implements IRecipeWrapper {
    private final GrinderInput input;

    public FossilGrinderRecipeWrapper(GrinderInput input) {
        this.input = input;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, input.stack);
        List<ItemStack> list = Lists.newArrayList();
        input.grind.getChancedOutputs(input.stack).forEach(pair -> {
            ItemStack stack = pair.getRight();
            stack.getOrCreateSubCompound("jei_rendering_info").setFloat("Chance", Math.round(pair.getLeft() * 10F) / 10F);
            list.add(stack);
        });

        List<List<ItemStack>> outputs = new ArrayList<>();
        outputs.add(list);
        ingredients.setOutputLists(ItemStack.class, outputs);
    }


    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
