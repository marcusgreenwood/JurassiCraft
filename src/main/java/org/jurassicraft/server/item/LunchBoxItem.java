package org.jurassicraft.server.item;

import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.item.Item;

public class LunchBoxItem extends Item {
    public LunchBoxItem() {
        super();
        this.setCreativeTab(TabHandler.ITEMS);
    }
}

