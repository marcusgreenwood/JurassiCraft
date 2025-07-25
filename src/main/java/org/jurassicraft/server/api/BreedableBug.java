package org.jurassicraft.server.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;

public interface BreedableBug extends JurassicIngredientItem {
    static BreedableBug getBug(ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof BreedableBug) {
                    return (BreedableBug) block;
                }
            } else if (item instanceof BreedableBug) {
                return (BreedableBug) item;
            }
        }
        return null;
    }

    static boolean isBug(ItemStack stack) {
        return getBug(stack) != null;
    }

    int getBreedings(ItemStack stack);
}
