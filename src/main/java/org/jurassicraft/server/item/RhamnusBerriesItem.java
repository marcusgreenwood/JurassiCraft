package org.jurassicraft.server.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.init.MobEffects;
import net.minecraft.world.item.ItemFood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.level.Level;

/**
 * Created by Codyr on 01/10/2017.
 */
public class RhamnusBerriesItem extends ItemFood {

    public RhamnusBerriesItem(int i, float v) {
        super(5, 0.6F, false);

    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 1400, 1));
    }
}


