package org.jurassicraft.server.item;

import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.item.Item;

public class StampSetItem extends Item {
    public StampSetItem() {
        super();
        this.setCreativeTab(TabHandler.ITEMS);
    }
}
