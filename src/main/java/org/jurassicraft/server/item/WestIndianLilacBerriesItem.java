package org.jurassicraft.server.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.init.MobEffects;
import net.minecraft.world.item.ItemFood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.tab.TabHandler;

public class WestIndianLilacBerriesItem extends ItemFood {

    public WestIndianLilacBerriesItem() {
        super(1, 0.1F, false);
        this.setHasSubtypes(true);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 1400, 1));
    }
}
