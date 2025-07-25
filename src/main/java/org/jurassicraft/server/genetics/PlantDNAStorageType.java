package org.jurassicraft.server.genetics;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jurassicraft.server.item.ItemHandler;

import java.util.List;

public class PlantDNAStorageType implements StorageType {
    private PlantDNA dna;

    @Override
    public ItemStack createItem() {
        ItemStack output = new ItemStack(ItemHandler.PLANT_DNA, 1, this.getMetadata());
        NBTTagCompound compound = new NBTTagCompound();
        this.dna.writeToNBT(compound);
        output.setTagCompound(compound);
        return output;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        this.dna.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dna = PlantDNA.readFromNBT(nbt);
    }

    @Override
    public void addInformation(ItemStack stack, List<String> tooltip) {
        this.dna.addInformation(stack, tooltip);
    }

    @Override
    public int getMetadata() {
        return this.dna.getMetadata();
    }
}
