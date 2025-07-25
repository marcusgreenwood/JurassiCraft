package org.jurassicraft.server.item.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.block.NestFossilBlock;
import org.jurassicraft.server.util.LangUtils;

public class NestFossilItemBlock extends ItemBlock {
    private boolean encased;

    public NestFossilItemBlock(Block block, boolean encased) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.encased = encased;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return LangUtils.translate(this.encased ? "tile.encased_nest_fossil.name" : "tile.nest_fossil.name");
    }

    private static NestFossilBlock.Variant getVariant(ItemStack stack) {
        NestFossilBlock.Variant[] values = NestFossilBlock.Variant.values();
        return values[stack.getItemDamage() % values.length];
    }

    @Override
    public int getMetadata(int metadata) {
        return metadata;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + getVariant(stack).getName();
    }
}
