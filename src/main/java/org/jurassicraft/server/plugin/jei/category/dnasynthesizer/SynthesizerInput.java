package org.jurassicraft.server.plugin.jei.category.dnasynthesizer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.api.SynthesizableItem;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.genetics.DinoDNA;
import org.jurassicraft.server.genetics.GeneticsHelper;
import org.jurassicraft.server.genetics.PlantDNA;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;

import java.util.Random;

public class SynthesizerInput {
    public final ItemStack stack;
    public final SynthesizableItem item;

    public SynthesizerInput(ItemStack stack) {
    	
        this.stack = stack;
        Item item = stack.getItem();
        if(item instanceof ItemBlock) {
            this.item = (SynthesizableItem)((ItemBlock)stack.getItem()).getBlock();
        } else {
            this.item = (SynthesizableItem)stack.getItem();
        }
    }
}
