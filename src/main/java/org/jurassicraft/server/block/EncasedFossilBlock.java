package org.jurassicraft.server.block;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.server.api.CleanableItem;
import org.jurassicraft.server.api.SubBlocksBlock;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.item.block.EncasedFossilItemBlock;
import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.Explosion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class EncasedFossilBlock extends Block implements SubBlocksBlock, CleanableItem {
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);

    private int start;

    public EncasedFossilBlock(int start) {
        super(Material.ROCK);
        this.setHardness(2.0F);
        this.setResistance(8.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(TabHandler.FOSSILS);

        this.start = start;

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        Map<Integer, Dinosaur> dinosaurs = EntityHandler.getDinosaurs();

        for (int i = 0; i < 16; i++) {
            Dinosaur dinosaur = dinosaurs.get(i + this.start);

            if (dinosaur != null && dinosaur.shouldRegister()) {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    public Dinosaur getDinosaur(int metadata) {
        return EntityHandler.getDinosaurById(this.start + metadata);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new EncasedFossilItemBlock(this);
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isCleanable(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCleanedItem(ItemStack stack, Random random) {
        int dinosaurId = BlockHandler.getDinosaurId((EncasedFossilBlock) Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
        String[] bones = EntityHandler.getDinosaurById(dinosaurId).getMetadata().getBones();
        return new ItemStack(ItemHandler.FOSSILS.get(bones[random.nextInt(bones.length)]), 1, dinosaurId);
    }

    @Override
    public List<ItemStack> getJEIRecipeTypes() {
        List<ItemStack> list = Lists.newArrayList();
        EntityHandler.getDinosaurs().values().forEach(dino -> list.add(new ItemStack(BlockHandler.getEncasedFossil(dino), 1, EntityHandler.getDinosaurId(dino)%16)));
        return list;
    }

    @Override
    public List<Pair<Float, ItemStack>> getChancedOutputs(ItemStack inputItem) {
        int dinosaurId = BlockHandler.getDinosaurId((EncasedFossilBlock) Block.getBlockFromItem(inputItem.getItem()), inputItem.getItemDamage());
        String[] bones = EntityHandler.getDinosaurById(dinosaurId).getMetadata().getBones();
        float single = 100F / bones.length;

        List<Pair<Float, ItemStack>> list = Lists.newArrayList();
        for(String bone : bones) {
            list.add(Pair.of(single, new ItemStack(ItemHandler.FOSSILS.get(bone), 1, dinosaurId)));
        }
        return list;
    }
}
