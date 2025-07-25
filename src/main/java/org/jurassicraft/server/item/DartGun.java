package org.jurassicraft.server.item;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.entity.TranquilizerDartEntity;
import org.jurassicraft.server.tab.TabHandler;

import java.util.Collections;
import java.util.List;

public class DartGun extends Item {

    public DartGun() {
        this.setCreativeTab(TabHandler.ITEMS);
        this.setMaxStackSize(1);
    }

    private static final int MAX_CARRY_SIZE = 12; //TODO config ?
    
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        SoundEvent event = null;
        
        ItemStack dartItem = getDartItem(itemstack);
        if(dartItem.isEmpty()) {
            List<Slot> list = Lists.newArrayList(playerIn.inventoryContainer.inventorySlots);
            Collections.reverse(list);
            if(setDartItem(itemstack, 
        	    list.stream()
        	    .map(Slot::getStack)
        	    .filter(stack -> stack.getItem() instanceof Dart)
        	    .findFirst()
        	    .orElse(ItemStack.EMPTY))) {
        	event = SoundEvents.ENTITY_ITEM_PICKUP;
            } else {
        	event = SoundEvents.BLOCK_COMPARATOR_CLICK;
            }
        } else if (!worldIn.isRemote) {
            TranquilizerDartEntity dart = new TranquilizerDartEntity(worldIn, playerIn, dartItem);
            dart.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 2.5F, 0.5F);
            worldIn.spawnEntity(dart);
            dartItem.shrink(1);
            setDartItem(itemstack, dartItem);
            event = SoundEvents.ENTITY_SNOWBALL_THROW;
        }
        
        if(event != null) {
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), event, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }


        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }
    
    public static ItemStack getDartItem(ItemStack dartGun) {
        NBTTagCompound nbt = dartGun.getOrCreateSubCompound("dart_gun");
        ItemStack stack = new ItemStack(nbt.getCompoundTag("itemstack"));
        stack.setCount(Math.min(stack.getCount(), MAX_CARRY_SIZE));
        return stack;
    }
    
    public static boolean setDartItem(ItemStack dartGun, ItemStack dartItem) {
        boolean hadItem = !dartItem.isEmpty();
        ItemStack dartItem2 = dartItem.splitStack(MAX_CARRY_SIZE);
        dartGun.getOrCreateSubCompound("dart_gun").setTag("itemstack", dartItem2.serializeNBT());
        return hadItem;
    }
}