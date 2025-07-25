package org.jurassicraft.server.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;

import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.vehicle.FordExplorerEntity;
import org.jurassicraft.server.entity.vehicle.HelicopterEntity;
import org.jurassicraft.server.entity.vehicle.JeepWranglerEntity;
import org.jurassicraft.server.entity.vehicle.TransportHelicopterEntity;
import org.jurassicraft.server.entity.vehicle.VehicleEntity;
import org.jurassicraft.server.tab.TabHandler;
import org.jurassicraft.server.util.LangUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

import static org.jurassicraft.server.item.ItemHandler.VEHICLE_ITEM;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class VehicleItem extends Item {
	
	public static final String[] variants = new String[] { "ford_explorer", "jeep_wrangler", "helicopter" };
	// public static final String[] localized = new String[variants.length];

	public VehicleItem() {

		this.setCreativeTab(TabHandler.VEHICLES);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		// Caching the localized files, is that needed? Don't uncomment as changing the
		// language at the runtime would break the cache
		/*
		 * if(FMLCommonHandler.instance().getSide() == Dist.CLIENT) { for (int i = 0; i
		 * < variants.length; i++) { localized[i] = LangUtils.translate("item." +
		 * variants[i] + ".name"); } }
		 */
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(stack, world, tooltip, tooltipFlag);
		int meta = stack.getMetadata();
		tooltip.add(LangUtils.translate("item.vehicle_item.place.name").replace("{variant}", LangUtils.translate("item." + variants[meta] + ".name") /* localized[meta] */));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		return LangUtils.translate("item." + variants[meta]);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			pos = pos.offset(side);
			VehicleEntity entity = null;
			if (stack.getMetadata() == 0) {
				entity = new FordExplorerEntity(world);
			} else if (stack.getMetadata() == 1) {
				entity = new JeepWranglerEntity(world);
			} else if (stack.getMetadata() == 2) {
				entity = new TransportHelicopterEntity(world);
			}
			if (stack.getMetadata() == 2) {
				entity.setPositionAndRotation(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, player.rotationYaw, 0.0F);
			} else {
				entity.setPositionAndRotation(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, player.rotationYaw, 0.0F);
			}

			world.spawnEntity(entity);

			stack.shrink(1);
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (int i = 0; i < 3; ++i) {
				items.add(new ItemStack(this, 1, i));
			}
		}
	}

}
