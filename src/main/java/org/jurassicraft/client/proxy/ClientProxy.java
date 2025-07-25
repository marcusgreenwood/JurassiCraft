package org.jurassicraft.client.proxy;

import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.server.util.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.gui.*;
import org.jurassicraft.client.model.JurassicraftTabulaModelHandler;
import org.jurassicraft.client.render.RenderingHandler;
import org.jurassicraft.client.render.entity.OverridenEntityRenderer;
import org.jurassicraft.client.sound.SoundHandler;
import org.jurassicraft.client.sound.VehicleSound;
import org.jurassicraft.server.block.entity.*;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.VenomEntity;
import org.jurassicraft.server.entity.particle.VenomParticle;
import org.jurassicraft.server.entity.vehicle.VehicleEntity;
import org.jurassicraft.server.event.KeyBindingHandler;
import org.jurassicraft.server.item.JournalItem;
import org.jurassicraft.server.proxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ClientProxy extends ServerProxy {
	
	public static final Minecraft MC = Minecraft.getMinecraft();
	private static KeyBindingHandler keyHandler = new KeyBindingHandler();
	public static final List<UUID> PATRONS = new ArrayList<>();

	@Override
	public void onPreInit(FMLPreInitializationEvent event) {
		super.onPreInit(event);

		KeyBindingHandler.init();
		try {
			LanguageHandler.INSTANCE.loadRemoteLocalization(JurassiCraft.MODID);
		} catch (Exception e) {
			JurassiCraft.getLogger().error("Failed to load remote localizations", e);
		}
		MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		MinecraftForge.EVENT_BUS.register(RenderingHandler.INSTANCE);
		RenderingHandler.INSTANCE.preInit();
		ModelLoaderRegistry.registerLoader(JurassicraftTabulaModelHandler.INSTANCE);
		JurassicraftTabulaModelHandler.INSTANCE.addDomain(JurassiCraft.MODID);
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);
		FMLInterModComms.sendMessage("waila", "register", "org.jurassicraft.server.plugin.waila.BlockDataProvider.init");
		FMLInterModComms.sendMessage("waila", "register", "org.jurassicraft.server.plugin.waila.EntityDataProvider.init");
		RenderingHandler.INSTANCE.init();
	}

	@Override
	public void onPostInit(FMLPostInitializationEvent event) {
		super.onPostInit(event);

		RenderingHandler.INSTANCE.postInit();

		new Thread(() -> {
			final List<String> patrons = WebUtils.readPastebinAsList("G8AVxw6A");
			if (patrons != null) {
				for (String patron : patrons) {
					PATRONS.add(UUID.fromString(patron));
				}
			}
		}).start();
	}

	@Override
	public EntityPlayer getPlayer() {
		return MC.player;
	}

	@Override
	public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? this.getPlayer() : super.getPlayerEntityFromContext(ctx));
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) {
			if (tile instanceof CleaningStationBlockEntity && id == GUI_CLEANING_STATION_ID) {
				return new CleaningStationGui(player.inventory, (CleaningStationBlockEntity) tile);
			} else if (tile instanceof FossilGrinderBlockEntity && id == GUI_FOSSIL_GRINDER_ID) {
				return new FossilGrinderGui(player.inventory, (FossilGrinderBlockEntity) tile);
			} else if (tile instanceof DNASequencerBlockEntity && id == GUI_DNA_SEQUENCER_ID) {
				return new DNASequencerGui(player.inventory, (DNASequencerBlockEntity) tile);
			} else if (tile instanceof EmbryonicMachineBlockEntity && id == GUI_EMBRYONIC_MACHINE_ID) {
				return new EmbryonicMachineGui(player.inventory, (EmbryonicMachineBlockEntity) tile);
			} else if (tile instanceof EmbryoCalcificationMachineBlockEntity
					&& id == GUI_EMBRYO_CALCIFICATION_MACHINE_ID) {
				return new EmbryoCalcificationMachineGui(player.inventory,
						(EmbryoCalcificationMachineBlockEntity) tile);
			} else if (tile instanceof DNASynthesizerBlockEntity && id == GUI_DNA_SYNTHESIZER_ID) {
				return new DNASynthesizerGui(player.inventory, (DNASynthesizerBlockEntity) tile);
			} else if (tile instanceof IncubatorBlockEntity && id == GUI_INCUBATOR_ID) {
				return new IncubatorGui(player.inventory, (IncubatorBlockEntity) tile);
			} else if (tile instanceof DNACombinatorHybridizerBlockEntity && id == GUI_DNA_COMBINATOR_HYBRIDIZER_ID) {
				return new DNACombinatorHybridizerGui(player.inventory, (DNACombinatorHybridizerBlockEntity) tile);
			} else if (tile instanceof DNAExtractorBlockEntity && id == GUI_DNA_EXTRACTOR_ID) {
				return new DNAExtractorGui(player.inventory, (DNAExtractorBlockEntity) tile);
			} else if (tile instanceof CultivatorBlockEntity && id == GUI_CULTIVATOR_ID) {
				CultivatorBlockEntity cultivator = (CultivatorBlockEntity) tile;
				if (cultivator.isProcessing(0)) {
					return new CultivateProcessGui(player.inventory, cultivator);
				} else {
					return new CultivateGui(player.inventory, cultivator);
				}
			} else if (tile instanceof FeederBlockEntity && id == GUI_FEEDER_ID) {
				return new FeederGui(player.inventory, (FeederBlockEntity) tile);
			} else if (tile instanceof BugCrateBlockEntity && id == GUI_BUG_CRATE) {
				return new BugCrateGui(player.inventory, (BugCrateBlockEntity) tile);
			}
		}
		if (id == GUI_SKELETON_ASSEMBLER) {
			return new SkeletonAssemblyGui(SkeletonAssemblyGui.createContainer(player.inventory, world, pos));
		}
		return null;
	}

	public static KeyBindingHandler getKeyHandler() {
		return keyHandler;
	}

	@Override
	public void openSelectDino(BlockPos pos, EnumFacing facing, EnumHand hand) {
		MC.displayGuiScreen(new SelectDinoGui(pos, facing, hand));
	}

	@Override
	public void openOrder(DinosaurEntity entity) {
		MC.displayGuiScreen(new OrderDinosaurGui(entity));
	}

	@Override
	public void openFieldGuide(DinosaurEntity entity, DinosaurEntity.FieldGuideInfo fieldGuideInfo) {
		MC.displayGuiScreen(new FieldGuideGui(entity, fieldGuideInfo));
	}

	@Override
	public void openJournal(JournalItem.JournalType type) {
		MC.displayGuiScreen(new JournalGui(type));
	}

	public static void playCarSound(VehicleEntity entity) {
		MC.getSoundHandler().playSound(new VehicleSound(entity, SoundHandler.CAR_MOVE));
	}

	public static void playHelicopterSound(VehicleEntity entity) {
		MC.getSoundHandler().playSound(new VehicleSound(entity, SoundHandler.CAR_MOVE));
	}

	public static void stopSound(ISound sound) {
		MC.getSoundHandler().stopSound(sound);
	}

	public static void spawnVenomParticles(VenomEntity entity) {
		ParticleManager particleManager = MC.effectRenderer;
		float size = 0.35F;
		for (int i = 0; i < 16; ++i) {
			particleManager.addEffect(new VenomParticle(entity.world, size * Math.random() - size / 2,
					size * Math.random() - size / 2, size * Math.random() - size / 2, 0.0F, 0.0F, 0.0F, 1.0F, entity));
		}
	}

	private static void registerEntity(Class<? extends Entity> entity, String name) {
		String formattedName = name.toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");
		ResourceLocation registryName = new ResourceLocation("jurassicraft:entities." + formattedName);
		EntityRegistry.registerModEntity(registryName, entity, "jurassicraft." + formattedName, 2055,
				JurassiCraft.INSTANCE, 1024, 1, true);
	}

}
