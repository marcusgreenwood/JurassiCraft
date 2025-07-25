package org.jurassicraft.server.world.loot;

import net.minecraft.world.level.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.conf.JurassiCraftConfig;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.FlyingDinosaurEntity;
import org.jurassicraft.server.genetics.DinoDNA;
import org.jurassicraft.server.genetics.GeneticsHelper;
import org.jurassicraft.server.item.DisplayBlockItem;
import org.jurassicraft.server.item.FossilItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Loot {

	public static final ResourceLocation GENETICIST_HOUSE_CHEST = new ResourceLocation(JurassiCraft.MODID, "structure/geneticist_house");
	public static final ResourceLocation VISITOR_GROUND_STORAGE = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/ground_storage");
	public static final ResourceLocation VISITOR_CONTROL_ROOM = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/control_room");
	public static final ResourceLocation VISITOR_LABORATORY = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/laboratory");
	public static final ResourceLocation VISITOR_CRYONICS = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/cryonics");
	public static final ResourceLocation VISITOR_INFIRMARY = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/infirmary");
	public static final ResourceLocation VISITOR_GARAGE = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/garage");
	public static final ResourceLocation VISITOR_STAFF_DORMS = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/staff_dorms");
	public static final ResourceLocation VISITOR_KITCHEN = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/kitchen");
	public static final ResourceLocation VISITOR_DORM_TOWER = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/dorm_tower");
	public static final ResourceLocation VISITOR_DINING_HALL = new ResourceLocation(JurassiCraft.MODID, "structure/visitor_centre/dining_hall");
	public static final ResourceLocation FOSSIL_DIGSITE_LOOT = new ResourceLocation(JurassiCraft.MODID, "structure/fossil_digsite");

	public static final DinosaurData DINOSAUR_DATA = new DinosaurData();
	public static final PlantData PLANT_DATA = new PlantData();
	public static final RandomDNA RANDOM_DNA = new RandomDNA();
	public static final PotionData POTION_DATA = new PotionData();
	public static final RandomDNA FULL_DNA = new RandomDNA(true);

	private static long tableID = 0;

	public static PoolBuilder pool(String name) {
		return new PoolBuilder(name);
	}

	public static EntryBuilder entry(Item item) {
		return new EntryBuilder(item);
	}

	public static EntryBuilder entry(Block block) {
		return new EntryBuilder(Item.getItemFromBlock(block));
	}

	public static MultiEntryBuilder entries(Item... items) {
		return new MultiEntryBuilder(items);
	}

	public static LootEntry[] entries(Object... entryData) {
		LootEntry[] builders = new LootEntry[entryData.length / 3];
		for (int i = 0; i < entryData.length; i += 3) {
			Object itemData = entryData[i];
			Item item = itemData instanceof Block ? Item.getItemFromBlock((Block) itemData) : (Item) itemData;
			EntryBuilder entry = Loot.entry(item).count((int) entryData[i + 1], (int) entryData[i + 2]);
			builders[i / 3] = entry.build();
		}
		return builders;
	}

	public static void handleTable(LootTable table, ResourceLocation name) {
		boolean frozen = false;

		if (table.isFrozen()) {// Some mods like to replace the loot tables which ends up with the loot table
								// being frozen.
			frozen = true;
			ReflectionHelper.setPrivateValue(LootTable.class, table, false, "isFrozen");
			for (LootPool lootPool : ((List<LootPool>) ReflectionHelper.getPrivateValue(LootTable.class, table, "pools",
					"field_186466_c"))) { // Unfreeze all the pools
				ReflectionHelper.setPrivateValue(LootPool.class, lootPool, false, "isFrozen");
			}
		}

		if (name == LootTableList.GAMEPLAY_FISHING) {
			LootEntry gracilaria = Loot.entry(ItemHandler.GRACILARIA).weight(25).build();
			table.addPool(Loot.pool("gracilaria").rolls(1, 1).chance(0.1F).entry(gracilaria).build());
		} else if (name == LootTableList.CHESTS_VILLAGE_BLACKSMITH || name == LootTableList.CHESTS_NETHER_BRIDGE
				|| name == LootTableList.CHESTS_SIMPLE_DUNGEON || name == LootTableList.CHESTS_STRONGHOLD_CORRIDOR
				|| name == LootTableList.CHESTS_DESERT_PYRAMID || name == LootTableList.CHESTS_ABANDONED_MINESHAFT) {
			float percentage = JurassiCraftConfig.ITEMS.lootSpawnrate / 100F;
			
			LootEntry plantFossil = Loot.entry(ItemHandler.PLANT_FOSSIL).weight(5).count((int) percentage, (int) (3 * percentage)).build();
			LootEntry twig = Loot.entry(ItemHandler.TWIG_FOSSIL).weight(5).count((int) percentage, (int) (3 * percentage)).build();
			LootEntry amber = Loot.entry(ItemHandler.AMBER).weight(2).count(0, (int) percentage).data(0, 1).build();
			LootEntry skull = Loot.entry(ItemHandler.FOSSILS.get("skull")).weight(2).function(DINOSAUR_DATA).count((int) percentage, (int) (2 * percentage))
					.build();

			table.addPool(Loot.pool("fossils").rolls(1, 2).entries(plantFossil, twig, amber, skull).build());

			LootEntry[] records = Loot.entries(ItemHandler.JURASSICRAFT_THEME_DISC, ItemHandler.DONT_MOVE_A_MUSCLE_DISC,
					ItemHandler.TROODONS_AND_RAPTORS_DISC).buildEntries();

			table.addPool(Loot.pool("records").rolls(0, (int) (2 * percentage)).entries(records).build());
		}
		if (name.getResourceDomain().equals(JurassiCraft.MODID)) {
			if (name.getResourcePath().equals(Loot.VISITOR_GROUND_STORAGE.getResourcePath())) {
				LootEntry amber = Loot.entry(ItemHandler.AMBER).data(0, 1).count(0, 3).build();
				LootEntry wool = Loot.entry(Blocks.WOOL).data(0, 15).count(0, 64).build();
				table.addPool(Loot.pool("items").rolls(5, 6).entries(amber, wool).build());
			} else if (name.getResourcePath().equals(Loot.VISITOR_LABORATORY.getResourcePath())) {
				LootEntry softTissue = Loot.entry(ItemHandler.SOFT_TISSUE).count(0, 3).function(DINOSAUR_DATA).build();
				LootEntry plantSoftTissue = Loot.entry(ItemHandler.PLANT_SOFT_TISSUE).count(0, 3).function(PLANT_DATA)
						.build();
				LootEntry amber = Loot.entry(ItemHandler.AMBER).data(0, 1).count(0, 5).build();
				LootEntry dna = Loot.entry(ItemHandler.DNA).function(DINOSAUR_DATA).function(RANDOM_DNA).build();
				table.addPool(Loot.pool("items").rolls(3, 4).entries(dna, softTissue, plantSoftTissue, amber).build());
			} else if (name.getResourcePath().equals(Loot.VISITOR_DINING_HALL.getResourcePath())) {
				LootEntry amber = Loot.entry(ItemHandler.AMBER).weight(2).count(0, 1).data(0, 1).build();
				LootEntry tooth = Loot.entry(ItemHandler.FOSSILS.get("tooth")).weight(2).function(DINOSAUR_DATA)
						.count(1, 2).build();
				LootEntry actionFigure = Loot.entry(ItemHandler.DISPLAY_BLOCK_ITEM).function(DINOSAUR_DATA).weight(1)
						.build();
				table.addPool(Loot.pool("items").rolls(8, 11).entries(amber, tooth, actionFigure).build());
			} else if (name.getResourcePath().equals(Loot.VISITOR_KITCHEN.getResourcePath())) {
				LootEntry waterBottle = Loot.entry(Items.POTIONITEM).count(0, 1).function(POTION_DATA).build();
				table.addPool(Loot.pool("items").rolls(3, 4).entries(waterBottle).build());
			} else if (name.getResourcePath().equals(Loot.FOSSIL_DIGSITE_LOOT.getResourcePath())) {
				LootEntry fossilPart = Loot.entry(ItemHandler.FOSSIL_DUMMY).weight(1).function(DINOSAUR_DATA).count(1, 1).build();
				LootEntry fossilPart2 = Loot.entry(ItemHandler.FOSSIL_DUMMY).weight(2).function(DINOSAUR_DATA).count(1, 2).build();
				table.addPool(Loot.pool("items").rolls(6, 9).entries(fossilPart, fossilPart2).build());
			}

		}
		if (frozen) { // If the table was originally frozen, then freeze it.
			table.freeze();
		}
	}

	public static class PoolBuilder {
		private String name;
		private int minRolls;
		private int maxRolls = 1;
		private int minBonusRolls;
		private int maxBonusRolls;
		private List<LootCondition> conditions = new ArrayList<>();
		private List<LootEntry> entries = new ArrayList<>();

		private PoolBuilder(String name) {
			this.name = name;
		}

		public PoolBuilder rolls(int min, int max) {
			this.minRolls = min;
			this.maxRolls = max;
			return this;
		}

		public PoolBuilder bonusRolls(int min, int max) {
			this.minBonusRolls = min;
			this.maxBonusRolls = max;
			return this;
		}

		public PoolBuilder condition(LootCondition condition) {
			this.conditions.add(condition);
			return this;
		}

		public PoolBuilder chance(float chance) {
			return this.condition(new RandomChance(chance));
		}

		public PoolBuilder entry(LootEntry entry) {
			this.entries.add(entry);
			return this;
		}

		public PoolBuilder entries(LootEntry... entries) {
			for (LootEntry entry : entries) {
				this.entry(entry);
			}
			return this;
		}

		public LootPool build() {
			LootEntry[] entries = this.entries.toArray(new LootEntry[0]);
			LootCondition[] conditions = this.conditions.toArray(new LootCondition[0]);
			return new LootPool(entries, conditions, new RandomValueRange(this.minRolls, this.maxRolls),
					new RandomValueRange(this.minBonusRolls, this.maxBonusRolls), this.name);
		}
	}

	public static class EntryBuilder {
		protected Item item;
		protected int weight = 1;
		protected int quality = 0;
		protected List<LootCondition> conditions = new ArrayList<>();
		protected List<LootFunction> functions = new ArrayList<>();

		private EntryBuilder(Item item) {
			this.item = item;
		}

		public EntryBuilder weight(int weight) {
			this.weight = weight;
			return this;
		}

		public EntryBuilder quality(int quality) {
			this.quality = quality;
			return this;
		}

		public EntryBuilder condition(LootCondition condition) {
			this.conditions.add(condition);
			return this;
		}

		public EntryBuilder tag(NBTTagCompound compound) {
			return this.function(new SetNBT(new LootCondition[0], compound));
		}

		public EntryBuilder function(LootFunction function) {
			this.functions.add(function);
			return this;
		}

		public EntryBuilder count(int min, int max) {
			return this.function(new SetCount(new LootCondition[0], new RandomValueRange(min, max)));
		}

		public EntryBuilder data(int min, int max) {
			return this.function(new SetMetadata(new LootCondition[0], new RandomValueRange(min, max)));
		}

		public EntryBuilder data(int data) {
			return this.data(data, data);
		}

		public LootEntry build() {
			LootCondition[] conditions = this.conditions.toArray(new LootCondition[0]);
			LootFunction[] functions = this.functions.toArray(new LootFunction[0]);
			return new LootEntryItem(this.item, this.weight, this.quality, functions, conditions,
					this.item.getUnlocalizedName() + "_" + tableID++);
		}
	}

	public static class MultiEntryBuilder extends EntryBuilder {
		protected Item[] items;

		private MultiEntryBuilder(Item... items) {
			super(null);
			this.items = items;
		}

		public LootEntry[] buildEntries() {
			LootCondition[] conditions = this.conditions.toArray(new LootCondition[0]);
			LootFunction[] functions = this.functions.toArray(new LootFunction[0]);
			LootEntry[] entries = new LootEntry[this.items.length];
			for (int i = 0; i < this.items.length; i++) {
				Item item = this.items[i];
				entries[i] = new LootEntryItem(item, this.weight, this.quality, functions, conditions,
						item.getUnlocalizedName() + "_" + tableID++);
			}
			return entries;
		}
	}

	public static class DinosaurData extends LootFunction {
		public DinosaurData() {
			super(new LootCondition[0]);
		}

		public DinosaurData(LootCondition[] conditions) {
			super(conditions);
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			List<Dinosaur> dinosaurs = EntityHandler.getRegisteredDinosaurs();
				Dinosaur dinosaur = dinosaurs.get(rand.nextInt(dinosaurs.size()));
				if (stack.getItem() instanceof FossilItem) {
					String boneName = dinosaur.getMetadata().getBones()[rand.nextInt(dinosaur.getMetadata().getBones().length)];
					stack = new ItemStack(ItemHandler.FOSSILS.get(boneName), 1, EntityHandler.getDinosaurId(dinosaur));
					return stack;
				} else if (stack.getItem() instanceof DisplayBlockItem) {
					DisplayBlockItem s = (DisplayBlockItem) stack.getItem();
					stack.setItemDamage(s.getMetadata(EntityHandler.getDinosaurId(dinosaur), false, false));
					return stack;
				} else {
					stack.setItemDamage(EntityHandler.getDinosaurId(dinosaur));
					return stack;
				}

		}
	}

	public static class PotionData extends LootFunction {
		public PotionData() {
			super(new LootCondition[0]);
		}

		public PotionData(LootCondition[] conditions) {
			super(conditions);
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			stack = PotionUtils.addPotionToItemStack(stack, PotionTypes.WATER);
			stack.setItemDamage(0);
			return stack;
		}
	}

	public static class PlantData extends LootFunction {
		public PlantData() {
			super(new LootCondition[0]);
		}

		public PlantData(LootCondition[] conditions) {
			super(conditions);
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			List<Plant> plants = PlantHandler.getPrehistoricPlants();
			Plant plant = plants.get(rand.nextInt(plants.size()));
			stack.setItemDamage(PlantHandler.getPlantId(plant));
			return stack;
		}
	}

	public static class RandomDNA extends LootFunction {
		private boolean full;

		public RandomDNA() {
			super(new LootCondition[0]);
		}

		public RandomDNA(LootCondition[] conditions) {
			super(conditions);
		}

		public RandomDNA(boolean full) {
			this();
			this.full = full;
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			int quality = (rand.nextInt(10) + 1) * 5;
			if (this.full) {
				quality = 100;
			}
			DinoDNA dna = new DinoDNA(EntityHandler.getDinosaurById(stack.getItemDamage()), quality, GeneticsHelper.randomGenetics(rand));
			NBTTagCompound compound = new NBTTagCompound();
			dna.writeToNBT(compound);
			stack.setTagCompound(compound);
			return stack;
		}
	}

	public static class WrittenBook extends LootFunction {
		private final String title;
		private final String author;
		private final String[] pages;

		public WrittenBook(LootCondition[] conditions, String title, String author, String[] pages) {
			super(conditions);
			this.title = title;
			this.author = author;
			this.pages = pages;
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) {
				compound = new NBTTagCompound();
				stack.setTagCompound(compound);
			}
			compound.setBoolean("resolved", false);
			compound.setInteger("generation", 0);
			compound.setString("title", this.title);
			compound.setString("author", this.author);
			NBTTagList pages = new NBTTagList();
			for (String page : this.pages) {
				pages.appendTag(new NBTTagString(page));
			}
			compound.setTag("pages", pages);
			return stack;
		}
	}
}
