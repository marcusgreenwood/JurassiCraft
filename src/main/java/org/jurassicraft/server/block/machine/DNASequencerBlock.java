package org.jurassicraft.server.block.machine;

import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.OrientedBlock;
import org.jurassicraft.server.block.entity.DNASequencerBlockEntity;
import org.jurassicraft.server.proxy.ServerProxy;
import org.jurassicraft.server.tab.TabHandler;

public class DNASequencerBlock extends OrientedBlock {
    public DNASequencerBlock() {
        super(Material.IRON);
        this.setUnlocalizedName("dna_sequencer");
        this.setHardness(2.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(TabHandler.BLOCKS);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasDisplayName()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof DNASequencerBlockEntity) {
                ((DNASequencerBlockEntity) tile).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof DNASequencerBlockEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (DNASequencerBlockEntity) tile);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof DNASequencerBlockEntity) {
                DNASequencerBlockEntity entity = (DNASequencerBlockEntity) tile;
                if (entity.isUsableByPlayer(player)) {
                    player.openGui(JurassiCraft.INSTANCE, ServerProxy.GUI_DNA_SEQUENCER_ID, world, pos.getX(), pos.getY(), pos.getZ());
                    return true;
                }
            }
        }
        return false;
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
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new DNASequencerBlockEntity();
    }
}
