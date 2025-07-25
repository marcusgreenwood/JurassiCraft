package org.jurassicraft.server.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.vecmath.Vector2d;
import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.api.Hybrid;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.entity.DisplayBlockEntity;
import org.jurassicraft.server.block.entity.SkullDisplayEntity;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.dinosaur.TyrannosaurusDinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.dinosaur.TyrannosaurusEntity;
import org.jurassicraft.server.plant.PlantHandler;
import org.jurassicraft.server.tab.TabHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerMP;
import net.minecraft.init.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.util.LangUtils;

public class FossilItem extends Item implements GrindableItem {
    public static Map<String, List<Dinosaur>> fossilDinosaurs = new HashMap<>();
    private String type;
    private boolean fresh;

    public FossilItem(String type, boolean fresh) {
        this.type = type.toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");
        this.fresh = fresh;
        this.setHasSubtypes(true);

        this.setCreativeTab(TabHandler.FOSSILS);
    }

    public static void init() {
        for (Dinosaur dinosaur : EntityHandler.getDinosaurs().values()) {
        	String[] boneTypes = dinosaur.getMetadata().getBones();

            for (String boneType : boneTypes) {
            	
                List<Dinosaur> dinosaursWithType = fossilDinosaurs.get(boneType);

                if (dinosaursWithType == null) {
                    dinosaursWithType = new ArrayList<>();
                }

                dinosaursWithType.add(dinosaur);
        
                fossilDinosaurs.put(boneType, dinosaursWithType);
            	
            }
        }
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Dinosaur dinosaur = getDinosaur(stack);

        if (dinosaur != null) {
            return LangUtils.translate(this.getUnlocalizedName() + ".name").replace("{dino}", LangUtils.getDinoName(dinosaur));
        }

        return super.getItemStackDisplayName(stack);
    }

    public static Dinosaur getDinosaur(ItemStack stack) {
        return EntityHandler.getDinosaurById(stack.getItemDamage());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subtypes) {
        List<Dinosaur> dinosaurs = new ArrayList<>(EntityHandler.getRegisteredDinosaurs());

        Collections.sort(dinosaurs);

        List<Dinosaur> dinosaursForType = fossilDinosaurs.get(this.type);
        if(this.isInCreativeTab(tab))
        for (Dinosaur dinosaur : dinosaurs) {
           if (dinosaursForType.contains(dinosaur) && !(!this.fresh && dinosaur instanceof Hybrid)) {
                subtypes.add(new ItemStack(this, 1, EntityHandler.getDinosaurId(dinosaur)));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> lore, ITooltipFlag flagIn) {
        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt != null && nbt.hasKey("Genetics") && nbt.hasKey("DNAQuality")) {
            int quality = nbt.getInteger("DNAQuality");

            TextFormatting colour;

            if (quality > 75) {
                colour = TextFormatting.GREEN;
            } else if (quality > 50) {
                colour = TextFormatting.YELLOW;
            } else if (quality > 25) {
                colour = TextFormatting.GOLD;
            } else {
                colour = TextFormatting.RED;
            }


            lore.add(colour + LangUtils.translate(LangUtils.LORE.get("dna_quality")).replace("{quality}", LangUtils.getFormattedQuality(quality)));
            lore.add(TextFormatting.BLUE + LangUtils.translate(LangUtils.LORE.get("genetic_code")).replace("{code}", LangUtils.getFormattedGenetics(nbt.getString("Genetics"))));
        }
        
        if(((FossilItem) stack.getItem()).getBoneType().equals("skull") && ((FossilItem) stack.getItem()).getDinosaur(stack).getClass() == TyrannosaurusDinosaur.class) {
        	lore.add(TextFormatting.GOLD + LangUtils.translate("pose.name") + ": " + LangUtils.getStandType(getHasStand(stack)));
			lore.add(TextFormatting.WHITE + LangUtils.translate("lore.change_variant.name"));
        }
    }

    @Override
    public boolean isGrindable(ItemStack stack) {
        return true;
    }
    
    public boolean isFresh() {
        return this.fresh;
    }
    
    public String getBoneType(){
        return type;
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	IBlockState blockstate = world.getBlockState(pos);
    	Block worldBlock = blockstate.getBlock();
    	
    	if (worldBlock.isReplaceable(world, pos))
        {
            side = Direction.UP;
            pos = pos.down();
        }
    	worldBlock = world.getBlockState(pos).getBlock();

        if (!worldBlock.isReplaceable(world, pos))
        {
            if (!world.getBlockState(pos).getMaterial().isSolid() || !world.isSideSolid(pos, side, true))
            {
                return EnumActionResult.FAIL;
            }

            pos = pos.offset(side);
        }

        ItemStack stack = player.getHeldItem(hand);
        Block block = BlockHandler.SKULL_DISPLAY;
        if (!player.level().isClientSide && player.canPlayerEdit(pos, side, stack) && world.mayPlace(block, pos, false, side, (Entity)null) && ((FossilItem) stack.getItem()).getBoneType().equals("skull") && ((FossilItem) stack.getItem()).getDinosaur(stack).getClass() == TyrannosaurusDinosaur.class) {
        	
        	if (side == Direction.DOWN)
            {
                return EnumActionResult.FAIL;
            }
            
            if (block.canPlaceBlockAt(world, pos)) {
            	IBlockState blockstatePlacement = block.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, /*meta*/ 0, player, hand);
            	if (!world.setBlockState(pos, blockstatePlacement, 11))
            		return EnumActionResult.FAIL;

                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() == block)
                {
                    ItemBlock.setTileEntityNBT(world, player, pos, stack);
                    block.onBlockPlacedBy(world, pos, state, player, stack);

                    if (player instanceof EntityPlayerMP)
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
                }
                
                world.playSound(null, pos, SoundType.STONE.getPlaceSound(), SoundCategory.BLOCKS, (SoundType.STONE.getVolume() + 1.0F) / 2.0F, SoundType.STONE.getPitch() * 0.8F);
                SkullDisplayEntity tile = (SkullDisplayEntity) world.getTileEntity(pos);
                
                if (tile != null) {
                	tile.setModel(stack.getItemDamage(), !this.isFresh(), getHasStand(stack));
                	EnumFacing.Axis axis = side.getAxis();
                	if (axis == EnumFacing.Axis.Y) {
                		tile.setAngle(angleToPlayer(pos, new Vector2d(player.getX(), player.getZ())));
                	}else if(axis == EnumFacing.Axis.X) {
                		tile.setAngle((short) side.getHorizontalAngle());
                	}else if(axis == EnumFacing.Axis.Z) {
                		tile.setAngle((short) (180 + side.getHorizontalAngle()));
                	}
                    world.notifyBlockUpdate(pos, state, state, 0);
                    tile.markDirty();
                    stack.shrink(1);

                }

            }
        }

        return EnumActionResult.SUCCESS;
    }
    
    private static short angleToPlayer(BlockPos block, Vector2d player) {
    	return (short) (90 - Math.toDegrees(Math.atan2(((double) block.getZ() + 0.5 - player.y), ((double) block.getX() + 0.5 - player.x))));
    }
    
    public static boolean getHasStand(ItemStack stack) {
    	
    	if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Type")) {
    		return stack.getTagCompound().getBoolean("Type");
    	}else {
    		return true;
    	}
    }
    
    public static void setHasStand(ItemStack stack, boolean hasStand) {
    	NBTTagCompound nbt = stack.getTagCompound();
    	if(nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
    	}
    	nbt.setBoolean("Type", hasStand);
    }
    
    public static boolean changeStandType(ItemStack stack) {
    	
        boolean newType = !getHasStand(stack);
        
        setHasStand(stack, newType);
        return newType;
    }
    
    @Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
			if (player.isSneaking() && ((FossilItem) stack.getItem()).getBoneType().equals("skull") && ((FossilItem) stack.getItem()).getDinosaur(stack).getClass() == TyrannosaurusDinosaur.class) {
				boolean oldType = getHasStand(stack);
				boolean type = changeStandType(stack);
				if (type != oldType && world.isRemote) {
					TextComponentString change = new TextComponentString(LangUtils.translate(LangUtils.STAND_CHANGE.get("type")).replace("{mode}", LangUtils.getStandType(type)));
					change.getStyle().setColor(TextFormatting.YELLOW);
					ClientProxy.MC.ingameGUI.addChatMessage(ChatType.GAME_INFO, change);
				}
			}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
}
    
    @Override
    public ItemStack getGroundItem(ItemStack stack, Random random) {
        NBTTagCompound tag = stack.getTagCompound();

        int outputType = random.nextInt(6);

        if (outputType == 5 || this.fresh) {
            ItemStack output = new ItemStack(ItemHandler.SOFT_TISSUE, 1, stack.getItemDamage());
            output.setTagCompound(tag);
            return output;
        } else if (outputType < 3) {
            return new ItemStack(Items.DYE, 1, 15);
        }

        return new ItemStack(Items.FLINT);
    }

    @Override
    public List<ItemStack> getJEIRecipeTypes() {
        List<ItemStack> list = Lists.newArrayList();
        fossilDinosaurs.get(this.type).forEach(dino -> list.add(new ItemStack(this, 1, EntityHandler.getDinosaurId(dino))));
        return list;
    }

    @Override
    public List<Pair<Float, ItemStack>> getChancedOutputs(ItemStack inputItem) {
        float single = 100F/6F;
        NBTTagCompound tag = inputItem.getTagCompound();
        ItemStack output = new ItemStack(ItemHandler.SOFT_TISSUE, 1, inputItem.getItemDamage());
        output.setTagCompound(tag);
        if(this.fresh) {
            return Lists.newArrayList(Pair.of(100F, output));
        }
        return Lists.newArrayList(Pair.of(single, output), Pair.of(50f, new ItemStack(Items.DYE, 1, 15)), Pair.of(single*2f, new ItemStack(Items.FLINT)));
    }
}
