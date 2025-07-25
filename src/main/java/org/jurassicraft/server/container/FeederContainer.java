package org.jurassicraft.server.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.container.slot.CustomSlot;
import org.jurassicraft.server.food.FoodHelper;
import org.jurassicraft.server.food.FoodType;

public class FeederContainer extends MachineContainer {
    private FeederBlockEntity tile;

    public FeederContainer(InventoryPlayer inventory, FeederBlockEntity tile) {
        super(tile);

        this.tile = tile;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlotToContainer(new CustomSlot(tile, row + (column * 3), 26 + row * 18, 18 + column * 18, stack -> FoodHelper.isFoodType(stack.getItem(), FoodType.MEAT) || FoodHelper.isFoodType(stack.getItem(), FoodType.FISH)));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlotToContainer(new CustomSlot(tile, row + (column * 3) + 9, 98 + row * 18, 18 + column * 18, stack -> FoodHelper.isFoodType(stack.getItem(), FoodType.PLANT)));
            }
        }
        
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlotToContainer(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(inventory, column, 8 + column * 18, 142));
        }
        
        
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUsableByPlayer(player);
    }
}
