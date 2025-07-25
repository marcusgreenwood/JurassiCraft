package org.jurassicraft.server.block.machine;

import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.OrientedBlock;
import org.jurassicraft.server.block.entity.EmbryonicMachineBlockEntity;
import org.jurassicraft.server.proxy.ServerProxy;
import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class EmbryonicMachineBlock extends OrientedBlock {
    public static final PropertyBool PETRI_DISH = PropertyBool.create("dish");
    public static final PropertyBool TEST_TUBES = PropertyBool.create("tubes");

    public EmbryonicMachineBlock() {
        super(Material.IRON);
        this.setUnlocalizedName("embryonic_machine");
        this.setHardness(2.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(TabHandler.BLOCKS);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (stack.hasDisplayName()) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof EmbryonicMachineBlockEntity) {
                ((EmbryonicMachineBlockEntity) tile).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof EmbryonicMachineBlockEntity) {
            InventoryHelper.dropInventoryItems(world, pos, (EmbryonicMachineBlockEntity) tileentity);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else if (!player.isSneaking()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            
            	if (tileEntity instanceof EmbryonicMachineBlockEntity) {
            		EmbryonicMachineBlockEntity embryonicMachine = (EmbryonicMachineBlockEntity) tileEntity;

                if (embryonicMachine.isUsableByPlayer(player)) {
                    player.openGui(JurassiCraft.INSTANCE, ServerProxy.GUI_EMBRYONIC_MACHINE_ID, world, pos.getX(), pos.getY(), pos.getZ());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new EmbryonicMachineBlockEntity();
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

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PETRI_DISH, TEST_TUBES);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        boolean dish = false;
        boolean tubes = false;
        TileEntity tile = access.getTileEntity(pos);
        if (tile instanceof EmbryonicMachineBlockEntity) {
            EmbryonicMachineBlockEntity machine = (EmbryonicMachineBlockEntity) tile;
            tubes = machine.getStackInSlot(1) != ItemStack.EMPTY;
            dish = machine.getStackInSlot(0) != ItemStack.EMPTY;
        }
        return state.withProperty(PETRI_DISH, dish).withProperty(TEST_TUBES, tubes);
    }
}
