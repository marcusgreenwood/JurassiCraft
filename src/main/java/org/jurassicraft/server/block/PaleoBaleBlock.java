package org.jurassicraft.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.tab.TabHandler;

import java.util.Locale;

public class PaleoBaleBlock extends BlockRotatedPillar {
    private PaleoBaleBlock.Variant variant;

    public PaleoBaleBlock(PaleoBaleBlock.Variant variant) {
        super(Material.GRASS, MapColor.FOLIAGE);
        this.variant = variant;
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
        this.setCreativeTab(TabHandler.PLANTS);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        entity.fall(fallDistance, 0.2F);
    }

    public PaleoBaleBlock.Variant getVariant() {
        return this.variant;
    }

    public enum Variant implements IStringSerializable {
        CYCADEOIDEA(BlockHandler.CYCADEOIDEA),
        CYCAD(BlockHandler.SMALL_CYCAD),
        FERN(BlockHandler.SMALL_CHAIN_FERN, BlockHandler.SMALL_ROYAL_FERN, BlockHandler.RAPHAELIA, BlockHandler.BRISTLE_FERN, BlockHandler.CINNAMON_FERN, BlockHandler.TEMPSKYA),
        LEAVES(BlockHandler.ANCIENT_LEAVES.values().toArray(new Block[0])),
        OTHER(BlockHandler.AJUGINUCULA_SMITHII, BlockHandler.RHACOPHYTON, BlockHandler.GRAMINIDITES_BAMBUSOIDES, BlockHandler.HELICONIA, BlockHandler.RHAMNUS_SALICIFOLIUS_PLANT, BlockHandler.LARGESTIPULE_LEATHER_ROOT, BlockHandler.RHACOPHYTON, BlockHandler.CRY_PANSY, BlockHandler.DICKSONIA, BlockHandler.DICROIDIUM_ZUBERI, BlockHandler.WILD_ONION, BlockHandler.ZAMITES, BlockHandler.UMALTOLEPIS, BlockHandler.LIRIODENDRITES, BlockHandler.WOOLLY_STALKED_BEGONIA);

        private Item[] ingredients;

        Variant(Block... ingredients) {
            this.ingredients = new Item[ingredients.length];

            for (int i = 0; i < ingredients.length; i++) {
                this.ingredients[i] = Item.getItemFromBlock(ingredients[i]);
            }
        }

        Variant(Item... ingredients) {
            this.ingredients = ingredients;
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }

        public Item[] getIngredients() {
            return this.ingredients;
        }
    }
}
