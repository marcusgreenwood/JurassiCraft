package org.jurassicraft.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.block.entity.DisplayBlockEntity;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.dinosaur.DinosaurMetadata;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.DisplayBlockItem;
import org.jurassicraft.server.item.ItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisplayBlock extends BlockContainer {
	
    public DisplayBlock() {
        super(Material.WOOD);
        
        this.setSoundType(SoundType.WOOD);
        this.setTickRandomly(false);
        this.setHardness(0.0F);
        this.setResistance(0.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        return getBounds(blockAccess, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getBounds(world, pos).offset(pos);
    }

    private static AxisAlignedBB getBounds(IBlockAccess world, BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof DisplayBlockEntity) {
            DisplayBlockEntity displayEntity = (DisplayBlockEntity) entity;
            Dinosaur dinosaur = displayEntity.getEntity().getDinosaur();
            if (dinosaur != null && !displayEntity.isSkeleton()) {
                DinosaurMetadata metadata = dinosaur.getMetadata();
                float width = MathHelper.clamp(metadata.getAdultSizeX() * 0.25F, 0.1F, 1.0F);
                float height = MathHelper.clamp(metadata.getAdultSizeY() * 0.25F, 0.1F, 1.0F);
                float halfWidth = width / 2.0F;
                return new AxisAlignedBB(0.5 - halfWidth, 0, 0.5 - halfWidth, halfWidth + 0.5, height, halfWidth + 0.5);
            }
        }
        return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && canBlockStay(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        this.checkAndDropBlock(world, pos, world.getBlockState(pos));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(world, pos, state);
    }

    private void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
        if (!canBlockStay(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    private static boolean canBlockStay(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOpaqueCube();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemHandler.DISPLAY_BLOCK_ITEM;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getItemFromTile(getTile(world, pos));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new DisplayBlockEntity();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    private static DisplayBlockEntity getTile(IBlockAccess world, BlockPos pos) {
        return (DisplayBlockEntity) world.getTileEntity(pos);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            this.dropBlockAsItem(world, pos, state, 0);
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    private static ItemStack getItemFromTile(DisplayBlockEntity tile) {
        int metadata = DisplayBlockItem.getMetadata(EntityHandler.getDinosaurId(tile.getEntity().getDinosaur()), tile.isFossile(), tile.isSkeleton());
        ItemStack stack = new ItemStack(ItemHandler.DISPLAY_BLOCK_ITEM, 1, metadata);
        NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setByte("Type", tile.getVariant());
		nbt.setByte("Gender", (byte) (tile.isMale() ? 1 : 2));
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>(1);

        DisplayBlockEntity tile = getTile(world, pos);

        if (tile != null) {
            drops.add(getItemFromTile(tile));
        }

        return drops;
    }
}
