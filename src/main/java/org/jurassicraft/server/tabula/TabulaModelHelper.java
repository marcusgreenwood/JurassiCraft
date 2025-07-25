package org.jurassicraft.server.tabula;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.minecraft.resources.ResourceLocation;

import org.jurassicraft.JurassiCraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TabulaModelHelper {
	
    public static TabulaCubeContainer getCubeByName(String name, TabulaModelContainer model) {
        List<TabulaCubeContainer> allCubes = getAllCubes(model);

        for (TabulaCubeContainer cube : allCubes) {
            if (cube.getName().equals(name)) {
                return cube;
            }
        }

        return null;
    }

    public static TabulaCubeContainer getCubeByIdentifier(String identifier, TabulaModelContainer model) {
        List<TabulaCubeContainer> allCubes = getAllCubes(model);

        for (TabulaCubeContainer cube : allCubes) {
            if (cube.getIdentifier().equals(identifier)) {
                return cube;
            }
        }

        return null;
    }

    public static List<TabulaCubeContainer> getAllCubes(TabulaModelContainer model) {
        List<TabulaCubeContainer> cubes = new ArrayList<>();

        try {
            for (TabulaCubeGroupContainer cubeGroup : model.getCubeGroups()) {
                cubes.addAll(traverse(cubeGroup));
            }

            for (TabulaCubeContainer cube : model.getCubes()) {
                cubes.addAll(traverse(cube));
            }
        } catch (Exception e) {
            JurassiCraft.getLogger().error("Failed to load all cubes for model {} by {}", model.getName(), model.getAuthor(), e);
        }

        return cubes;
    }

    private static List<TabulaCubeContainer> traverse(TabulaCubeGroupContainer group) {
        List<TabulaCubeContainer> retCubes = new ArrayList<>();

        for (TabulaCubeContainer child : group.getCubes()) {
            retCubes.addAll(traverse(child));
        }

        for (TabulaCubeGroupContainer child : group.getCubeGroups()) {
            retCubes.addAll(traverse(child));
        }

        return retCubes;
    }

    private static List<TabulaCubeContainer> traverse(TabulaCubeContainer cube) {
        List<TabulaCubeContainer> retCubes = new ArrayList<>();

        retCubes.add(cube);

        for (TabulaCubeContainer child : cube.getChildren()) {
            retCubes.addAll(traverse(child));
        }

        return retCubes;
    }

    public static TabulaModelContainer loadTabulaModel(ResourceLocation location) throws IOException {
    	
        String path = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath() + ".tbl";
        try (InputStream stream = TabulaModelHelper.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("Could not find tabula model at " + location);
            }
            return TabulaModelHelper.loadTabulaModel(getModelJsonStream(path, stream));
        }
    }

    public static TabulaModelContainer loadTabulaModel(InputStream stream) {
        return new Gson().fromJson(new InputStreamReader(stream), TabulaModelContainer.class);
    }

    private static InputStream getModelJsonStream(String name, InputStream file) throws IOException {
        ZipInputStream zip = new ZipInputStream(file);
        ZipEntry entry;

        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().equals("model.json")) {
                return zip;
            }
        }

        throw new RuntimeException("No model.json present in " + name);
    }
}