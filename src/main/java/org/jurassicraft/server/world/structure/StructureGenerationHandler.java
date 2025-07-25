package org.jurassicraft.server.world.structure;

import com.google.common.collect.Lists;
import net.minecraft.init.Biomes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.jurassicraft.server.conf.JurassiCraftConfig;
import org.jurassicraft.server.maps.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public enum StructureGenerationHandler implements IWorldGenerator {
    INSTANCE;

    private static final Map<Biome, List<GeneratorEntry>> GENERATORS = new HashMap<>();
    private static final List<GeneratorEntry> UNIVERSAL_GENERATORS = Lists.newArrayList();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            int blockX = (chunkX << 4) + random.nextInt(16);
            int blockZ = (chunkZ << 4) + random.nextInt(16);
            BlockPos pos = new BlockPos(blockX, 0, blockZ);
            Biome biome = world.getBiomeForCoordsBody(pos);

            StructureUtils.StructureData data = StructureUtils.getStructureData();

            boolean universalGeneratorsGenerated = false;
            for(GeneratorEntry generatorEntry : UNIVERSAL_GENERATORS) {
                if (generatorEntry.predicate.canSpawn(world, pos, random) && generatorEntry.configPredicate.test(data)) {
                    generatorEntry.generatorFunction.apply(random).generate(world, random, pos);
                    universalGeneratorsGenerated = true;
                }
            }

            if(!universalGeneratorsGenerated) {
                List<GeneratorEntry> entries = GENERATORS.get(biome);
                if (entries != null && !entries.isEmpty()) {
                    GeneratorEntry generatorEntry = entries.get(random.nextInt(entries.size()));
                    if (generatorEntry != null) {
                        if (generatorEntry.predicate.canSpawn(world, pos, random)&& generatorEntry.configPredicate.test(data)) {
                            generatorEntry.generatorFunction.apply(random).generate(world, random, pos);
                        }
                    }
                }
            }
        }
    }
    
    public static void registerInit() {
    	GameRegistry.registerWorldGenerator(INSTANCE, 0);
    	register();
    }

    private static void register() {
        
        StructureGenerationHandler.registerGenerator(VisitorCentreGenerator::new, StructureUtils.StructureData::isVisitorCenter,(world, pos, random) -> world.getChunkFromBlockCoords(pos) == world.getChunkFromBlockCoords(MapUtils.getVisitorCenterPosition()));
        StructureGenerationHandler.registerGenerator(RaptorPaddockGenerator::new, StructureUtils.StructureData::isRaptorPaddock, JurassiCraftConfig.STRUCTURE_GENERATION.paddockRarity, Biomes.JUNGLE, Biomes.MUTATED_JUNGLE, Biomes.JUNGLE_EDGE, Biomes.MUTATED_JUNGLE_EDGE, Biomes.SAVANNA, Biomes.MUTATED_SAVANNA);
    }
    
    public static void reloadGenerators() {
    	StructureGenerationHandler.clear();
    	register();
    }

    public static void registerGenerator(Function<Random, StructureGenerator> generatorFunction, Predicate<StructureUtils.StructureData> configPredicate, int weight, Biome... validBiomes) {
        registerGenerator(generatorFunction, configPredicate, (world, pos, random) -> random.nextInt(weight) == 0, validBiomes);
    }

    public static void registerGenerator(Function<Random, StructureGenerator> generatorFunction, Predicate<StructureUtils.StructureData> configPredicate, StructurePredicate predicate, Biome... validBiomes) {
        GeneratorEntry entry = new GeneratorEntry(generatorFunction, configPredicate, predicate);
        for (Biome biome : validBiomes) {
            StructureGenerationHandler.addEntry(biome, entry);
        }
        if(validBiomes.length == 0) {
            UNIVERSAL_GENERATORS.add(entry);
        }
    }

    private static void addEntry(Biome biome, GeneratorEntry generator) {
        GENERATORS.computeIfAbsent(biome, b -> new ArrayList<>()).add(generator);
    }
    
    private static void clear() {
        GENERATORS.clear();
    }

    private static class GeneratorEntry {
        private final Function<Random, StructureGenerator> generatorFunction;
        private final Predicate<StructureUtils.StructureData> configPredicate;
        private final StructurePredicate predicate;

        public GeneratorEntry(Function<Random, StructureGenerator> generatorFunction, Predicate<StructureUtils.StructureData> configPredicate, StructurePredicate predicate) {
            this.generatorFunction = generatorFunction;
            this.configPredicate = configPredicate;
            this.predicate = predicate;
        }
    }

    @FunctionalInterface
    public interface StructurePredicate {
        boolean canSpawn(World world, BlockPos pos, Random random);
    }
}
