package org.jurassicraft.server.entity.vehicle;

import org.jurassicraft.client.render.overlay.HelicopterHUDRenderer.HudElementAltimeter;
import org.jurassicraft.client.render.overlay.HelicopterHUDRenderer.HudElementArtificialHorizon;
import org.jurassicraft.client.render.overlay.HelicopterHUDRenderer.HudElementCompass;
import org.jurassicraft.client.render.overlay.HelicopterHUDRenderer.HudElementStatsDisplay;
import org.jurassicraft.client.render.overlay.HelicopterHUDRenderer.HudElementTachometer;
import org.jurassicraft.server.item.ItemHandler;

import net.minecraft.world.level.block.BlockEventData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.BlockEvent;

public class TransportHelicopterEntity extends HelicopterEntity {

	public TransportHelicopterEntity(World worldIn) {
		// super(worldIn, 5, 3.5f, 8, 3992, 300, 6838, 5);
		super(worldIn, 2, 3.5f, 5, 3992, 300, 6838, 5);

		if (this.level().isClientSide) {
			this.addHudOverlayElement(HudElementAltimeter.class);
			this.addHudOverlayElement(HudElementArtificialHorizon.class);
			this.addHudOverlayElement(HudElementTachometer.class);
			this.addHudOverlayElement(HudElementCompass.class);
			this.addHudOverlayElement(HudElementStatsDisplay.class);
		}
	}

	@Override
	protected Seat[] createSeats() {
		Seat middle = new Seat(0F, -0.23F, 1.2F, 0.5F, 0.25F);
		Seat frontLeft = new Seat(-0.55F, -0.34F, 0.1F, 0.5F, 0.25F);
		Seat frontRight = new Seat(0.55F, -0.34F, 0.1F, 0.5F, 0.25F);
		Seat backLeft = new Seat(0.4F, 0.25F, -1F, 0.5F, 0.25F);
		Seat backReft = new Seat(-0.4F, 0.25F, -1F, 0.5F, 0.25F);
		return new Seat[] { middle, frontLeft, frontRight, backLeft, backReft };
	}

	@Override
	protected WheelData createWheels() {
		return new WheelData(1, 2, -1, -2.2);
	}

	@Override
	public void doPlayerRotations(EntityPlayer player, float partialTicks) {
		float rotation = (float) Math.toRadians(player.rotationYaw - this.rotationYaw);
		float offsetZ = seats[getSeatForEntity(player)].getOffsetZ();
		GlStateManager.translate(Math.sin(-rotation) * offsetZ, 0, Math.cos(rotation) * offsetZ);
		GlStateManager.rotate((float) ((Math.sin(rotation) * this.pitch) + (Math.cos(rotation) * this.roll)), 0, 0, 1);
		GlStateManager.rotate((float) ((Math.cos(rotation) * this.pitch) + (Math.sin(-rotation) * this.roll)), 1, 0, 0);
		GlStateManager.translate(-Math.sin(-rotation) * offsetZ, -0, -Math.cos(rotation) * offsetZ);
	}

	@Override
	public void dropItems() {
		this.entityDropItem(new ItemStack(ItemHandler.VEHICLE_ITEM, 1, 2), 0.1f);
	}

}
