package org.jurassicraft.server.item.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.util.LangUtils;

public class CultivateItemBlock extends ItemBlock {
    public CultivateItemBlock(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int metadata) {
        return metadata;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumDyeColor color = EnumDyeColor.byMetadata(stack.getItemDamage());
        return this.block.getLocalizedName().replace("{color}", LangUtils.translate(LangUtils.COLORS.get(color.getName())));
    }
}
