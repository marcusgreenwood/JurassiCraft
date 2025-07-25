package org.jurassicraft.client.sound;

// TODO: This entire sound system needs rewrite for 1.21
// The old MovingSound and related APIs have changed significantly

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import java.util.function.Predicate;

// TODO: Reimplement this class with modern sound APIs
public class EntitySound<T extends Entity> {
    // Temporarily disabled - needs complete rewrite for modern sound system
    /*
    private final T trackedEntity;
    private final Predicate<T> predicate;

    public EntitySound(T entity, SoundEvent soundEvent, SoundSource soundCategory, Predicate<T> predicate) {
        // TODO: Implement with new sound APIs
    }

    public EntitySound(T entity, SoundEvent soundEvent, SoundSource soundCategory) {
        this(entity, soundEvent, soundCategory, entity1 -> true);
    }

    @Override
    public void tick() {
        // TODO: Implement sound positioning and lifecycle
    }
    */
}
