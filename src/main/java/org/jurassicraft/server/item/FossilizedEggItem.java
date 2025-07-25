package org.jurassicraft.server.item;

import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.NonNullList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.block.NestFossilBlock;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.tab.TabHandler;

import java.util.List;
import java.util.Random;

public class FossilizedEggItem extends Item implements GrindableItem {
    public FossilizedEggItem() {
        super();
        this.setCreativeTab(TabHandler.FOSSILS);
        this.setHasSubtypes(true);
    }

//    @Override
//    public String getItemStackDisplayName(ItemStack stack) {
//        return LangUtils.translate(this.getUnlocalizedName());
//    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if(this.isInCreativeTab(tab))
        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            subItems.add(new ItemStack(this, 1, variant.ordinal()));
        }
    }

    @Override
    public boolean isGrindable(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getGroundItem(ItemStack stack, Random random) {
        NBTTagCompound tag = stack.getTagCompound();

        int outputType = random.nextInt(3);

        if (outputType == 0) {
            List<Dinosaur> dinosaurs = EntityHandler.getPrehistoricDinosaurs();

            ItemStack output = new ItemStack(ItemHandler.SOFT_TISSUE, 1, EntityHandler.getDinosaurId(dinosaurs.get(random.nextInt(dinosaurs.size()))));
            output.setTagCompound(tag);
            return output;
        } else if (outputType == 1) {
            return new ItemStack(Items.DYE, 1, 15);
        }

        return new ItemStack(Items.FLINT);
    }

    @Override
    public List<Pair<Float, ItemStack>> getChancedOutputs(ItemStack inputItem) {
        List<Pair<Float, ItemStack>> list = Lists.newArrayList();
        NBTTagCompound tag = inputItem.getTagCompound();
        List<Dinosaur> dinosaurs = EntityHandler.getPrehistoricDinosaurs();
        float single = 100F/3F;
        float dinoSingle = single / dinosaurs.size();
        for(Dinosaur dinosaur : dinosaurs) {
            ItemStack output = new ItemStack(ItemHandler.SOFT_TISSUE, 1, EntityHandler.getDinosaurId(dinosaur));
            output.setTagCompound(tag);
            list.add(Pair.of(dinoSingle, output));
        }
        list.add(Pair.of(single, new ItemStack(Items.DYE, 1, 15)));
        list.add(Pair.of(single, new ItemStack(Items.FLINT)));

        return list;
    }
}
