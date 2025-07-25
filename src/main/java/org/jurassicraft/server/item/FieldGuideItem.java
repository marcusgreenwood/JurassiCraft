package org.jurassicraft.server.item;

import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerMP;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.message.OpenFieldGuideGuiMessage;
import org.jurassicraft.server.tab.TabHandler;

public class FieldGuideItem extends Item {
    public FieldGuideItem() {
        super();
        this.setCreativeTab(TabHandler.ITEMS);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (target instanceof DinosaurEntity) {
            if (!player.level().isClientSide) {
                JurassiCraft.NETWORK_WRAPPER.sendTo(new OpenFieldGuideGuiMessage((DinosaurEntity) target), (EntityPlayerMP) player);
            }
            return true;
        }
        return false;
    }
}
