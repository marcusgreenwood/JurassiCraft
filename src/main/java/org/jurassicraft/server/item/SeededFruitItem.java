package org.jurassicraft.server.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SeededFruitItem extends ItemFood {
    private Item seed;

    public SeededFruitItem(Item seed, int amount, float saturation) {
        super(amount, saturation, false);
        this.seed = seed;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (!world.isRemote && player.getRNG().nextDouble() < 0.5) {
            player.inventory.addItemStackToInventory(new ItemStack(this.seed));
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 14;
    }
}
