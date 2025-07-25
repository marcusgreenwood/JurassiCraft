package org.jurassicraft.server.block;

import java.util.Locale;

import org.jurassicraft.server.api.SubBlocksBlock;
import org.jurassicraft.server.item.block.FossilizedTrackwayItemBlock;
import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockHorizontal;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class FossilizedTrackwayBlock extends Block implements SubBlocksBlock {
    public static final PropertyEnum<TrackwayType> VARIANT = PropertyEnum.create("variant", TrackwayType.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public FossilizedTrackwayBlock() {
        super(Material.ROCK);
        this.setHardness(1.5F);
        this.setCreativeTab(TabHandler.FOSSILS);
        this.setUnlocalizedName("fossilized_trackway");
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, TrackwayType.BIPED_MEDIUM).withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VARIANT, TrackwayType.values()[meta]);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(meta >> 2 & 3);

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, facing).withProperty(VARIANT, TrackwayType.values()[meta & 3]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING).getIndex() & 3) << 2 | (state.getValue(VARIANT).ordinal() & 3);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, VARIANT);
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (TrackwayType type : TrackwayType.values()) {
            if(this.getCreativeTabToDisplayOn().equals(TabHandler.FOSSILS))
                list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public ItemBlock getItemBlock() {
        return new FossilizedTrackwayItemBlock(this);
    }

    public enum TrackwayType implements IStringSerializable {
        BIPED_MEDIUM, BIPED_SMALL, RAPTOR;

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }
    }
}
