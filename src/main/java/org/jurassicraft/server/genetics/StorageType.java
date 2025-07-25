package org.jurassicraft.server.genetics;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public interface StorageType {
    ItemStack createItem();

    void writeToNBT(NBTTagCompound nbt);

    void readFromNBT(NBTTagCompound nbt);

    void addInformation(ItemStack stack, List<String> tooltip);

    int getMetadata();
}
