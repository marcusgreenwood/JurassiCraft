package org.jurassicraft.server.item;

import java.util.function.BiConsumer;

import org.jurassicraft.server.entity.DinosaurEntity;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.tab.TabHandler;

public class Dart extends Item {
    private final BiConsumer<DinosaurEntity, ItemStack> consumer;
    private final int dartColor;

    public Dart(BiConsumer<DinosaurEntity, ItemStack> consumer, int dartColor) {
	    this.consumer = consumer;
	    this.dartColor = dartColor;
        this.setCreativeTab(TabHandler.ITEMS);
    }

    public int getDartColor(ItemStack stack) {
        return dartColor;
    }

    public BiConsumer<DinosaurEntity, ItemStack> getConsumer() {
        return consumer;
    }
}