package org.jurassicraft.server.plugin.jei.category.fossilgrinder;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.api.GrindableItem;

public class GrinderInput {
    public final ItemStack stack;
    public final GrindableItem grind;

    public GrinderInput(ItemStack stack) {
    	
        this.stack = stack;
        Item item = stack.getItem();
        if(item instanceof ItemBlock) {
            this.grind = (GrindableItem)((ItemBlock)stack.getItem()).getBlock();
        } else {
            this.grind = (GrindableItem)stack.getItem();
        }
    }
}
