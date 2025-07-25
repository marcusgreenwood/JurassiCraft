package org.jurassicraft.server.item;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.block.tree.TreeType;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;
import org.jurassicraft.server.tab.TabHandler;

import java.util.List;
import java.util.Random;

public class PlantFossilItem extends Item implements GrindableItem {
    public PlantFossilItem() {
        super();
        this.setCreativeTab(TabHandler.PLANTS);
    }

    @Override
    public boolean isGrindable(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getGroundItem(ItemStack stack, Random random) {
        NBTTagCompound tag = stack.getTagCompound();

        int outputType = random.nextInt(4);

        if (outputType == 3) {
            List<Plant> prehistoricPlants = PlantHandler.getPrehistoricPlants();
            ItemStack output = new ItemStack(ItemHandler.PLANT_SOFT_TISSUE, 1, PlantHandler.getPlantId(prehistoricPlants.get(random.nextInt(prehistoricPlants.size()))));
            output.setTagCompound(tag);
            return output;
        } else if (outputType < 2) {
            return new ItemStack(Items.DYE, 1, 15);
        }

        return new ItemStack(Items.FLINT);
    }

    @Override
    public List<Pair<Float, ItemStack>> getChancedOutputs(ItemStack inputItem) {
        List<Pair<Float, ItemStack>> list = Lists.newArrayList();
        List<Plant> prehistoricPlants = PlantHandler.getPrehistoricPlants();
        NBTTagCompound tag = inputItem.getTagCompound();
        float single = 100F/4F;
        float plantSingle = single / prehistoricPlants.size();
        for(Plant plant : prehistoricPlants) {
            ItemStack output = new ItemStack(ItemHandler.PLANT_SOFT_TISSUE, 1, PlantHandler.getPlantId(plant));
            output.setTagCompound(tag);
            list.add(Pair.of(plantSingle, output));
        }
        list.add(Pair.of(50f, new ItemStack(Items.DYE, 1, 15)));
        list.add(Pair.of(single, new ItemStack(Items.FLINT)));
        return list;
    }
}
