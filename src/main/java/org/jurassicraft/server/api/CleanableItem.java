package org.jurassicraft.server.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public interface CleanableItem extends JurassicIngredientItem {
    static CleanableItem getCleanableItem(ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();

                if (block instanceof CleanableItem) {
                    return (CleanableItem) block;
                }
            } else if (item instanceof CleanableItem) {
                return (CleanableItem) item;
            }
        }

        return null;
    }

    static boolean isCleanableItem(ItemStack stack) {
        return getCleanableItem(stack) != null;
    }

    boolean isCleanable(ItemStack stack);

    ItemStack getCleanedItem(ItemStack stack, Random random);
}
