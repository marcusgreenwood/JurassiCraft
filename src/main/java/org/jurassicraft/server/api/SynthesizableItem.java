package org.jurassicraft.server.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public interface SynthesizableItem extends JurassicIngredientItem {
    static SynthesizableItem getSynthesizableItem(ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();

                if (block instanceof SynthesizableItem) {
                    return (SynthesizableItem) block;
                }
            } else if (item instanceof SynthesizableItem) {
                return (SynthesizableItem) item;
            }
        }

        return null;
    }

    static boolean isSynthesizableItem(ItemStack stack) {
        return getSynthesizableItem(stack) != null;
    }

    boolean isSynthesizable(ItemStack stack);

    ItemStack getSynthesizedItem(ItemStack stack, Random random);
}
