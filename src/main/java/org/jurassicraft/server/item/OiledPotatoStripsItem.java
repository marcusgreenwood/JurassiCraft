package org.jurassicraft.server.item;

import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.item.ItemFood;

public class OiledPotatoStripsItem extends ItemFood {
	public OiledPotatoStripsItem() {
	 super(0, false);
     this.setCreativeTab(TabHandler.FOODS);
	}
}
