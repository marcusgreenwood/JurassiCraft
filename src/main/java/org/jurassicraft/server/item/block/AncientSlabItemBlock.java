package org.jurassicraft.server.item.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.block.tree.AncientDoubleSlabBlock;
import org.jurassicraft.server.block.tree.AncientSlabBlock;
import org.jurassicraft.server.block.tree.AncientSlabHalfBlock;

public class AncientSlabItemBlock extends ItemBlock {
    private final BlockSlab singleSlab;
    private final BlockSlab doubleSlab;

    public AncientSlabItemBlock(Block block, AncientSlabHalfBlock singleSlab, AncientDoubleSlabBlock doubleSlab) {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.singleSlab.getUnlocalizedName(stack.getMetadata());
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        if (stack.getCount() != 0 && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == this.singleSlab) {
                AncientSlabBlock.EnumBlockHalf half = state.getValue(BlockSlab.HALF);

                if ((facing == Direction.UP && half == BlockSlab.EnumBlockHalf.BOTTOM || facing == Direction.DOWN && half == BlockSlab.EnumBlockHalf.TOP)) {
                    AxisAlignedBB collisionBox = state.getSelectedBoundingBox(world, pos);
                    IBlockState doubleSlabState = this.doubleSlab.getDefaultState();

                    if (collisionBox != Block.NULL_AABB && world.checkNoEntityCollision(collisionBox.offset(pos)) && world.setBlockState(pos, doubleSlabState, 11)) {
                        SoundType sound = this.doubleSlab.getSoundType();
                        world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                        stack.shrink(1);
                    }

                    return EnumActionResult.SUCCESS;
                }
            }

            return this.tryPlace(player, stack, world, pos.offset(facing)) ? EnumActionResult.SUCCESS : super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        BlockPos placePos = pos;
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == this.singleSlab) {
            boolean flag = state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

            if ((side == Direction.UP && !flag || side == Direction.DOWN && flag)) {
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState iblockstate1 = world.getBlockState(pos);
        return iblockstate1.getBlock() == this.singleSlab || super.canPlaceBlockOnSide(world, placePos, side, player, stack);
    }

    private boolean tryPlace(EntityPlayer player, ItemStack stack, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == this.singleSlab) {
            AxisAlignedBB collisionBounds = state.getSelectedBoundingBox(world, pos);

            if (collisionBounds != Block.NULL_AABB && world.checkNoEntityCollision(collisionBounds.offset(pos)) && world.setBlockState(pos, state, 11)) {
                SoundType soundtype = this.doubleSlab.getSoundType();
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);;
            }

            return true;
        }

        return false;
    }
}
