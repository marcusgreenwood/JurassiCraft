package org.jurassicraft.server.plugin.jei.category.dnasequencer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.api.SequencableItem;

public class SequencerInput {
    public final ItemStack stack;
    public final SequencableItem item;

    public SequencerInput(ItemStack stack) {
        this.stack = stack;
        Item item = stack.getItem();
        if(item instanceof ItemBlock) {
            this.item = (SequencableItem)((ItemBlock)stack.getItem()).getBlock();
        } else {
            this.item = (SequencableItem)stack.getItem();
        }
    }
}
