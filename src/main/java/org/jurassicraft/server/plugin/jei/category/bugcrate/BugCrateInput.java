package org.jurassicraft.server.plugin.jei.category.bugcrate;

import org.jurassicraft.server.api.BreedableBug;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;

public class BugCrateInput {
    public final ItemStack stack;
    public final BreedableBug bug;

    public BugCrateInput(ItemStack stack) {
    	
        this.stack = stack;
        Item item = stack.getItem();
        if(item instanceof ItemBlock) {
            this.bug = (BreedableBug)((ItemBlock)stack.getItem()).getBlock();
        } else {
            this.bug = (BreedableBug)stack.getItem();
        }
    }
}
