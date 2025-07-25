package org.jurassicraft.server.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public interface GrindableItem extends JurassicIngredientItem {
    static GrindableItem getGrindableItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();

            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();

                if (block instanceof GrindableItem) {
                    return (GrindableItem) block;
                }
            } else if (item instanceof GrindableItem) {
                return (GrindableItem) item;
            }
        }

        return null;
    }

    static boolean isGrindableItem(ItemStack stack) {
        return getGrindableItem(stack) != null;
    }

    boolean isGrindable(ItemStack stack);

    ItemStack getGroundItem(ItemStack stack, Random random);
}
