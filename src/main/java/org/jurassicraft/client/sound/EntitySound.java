package org.jurassicraft.client.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import java.util.function.Predicate;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.proxy.ClientProxy;

public class EntitySound<T extends Entity> extends MovingSound {

    protected final T entity;
    protected final Predicate<T> predicate;

    public EntitySound(T entity, SoundEvent soundEvent, SoundCategory soundCategory, Predicate<T> predicate) {
        super(soundEvent, soundCategory);
        this.entity = entity;
        this.predicate = predicate;
    }

    public EntitySound(T entity, SoundEvent soundEvent, SoundCategory soundCategory) {
        this(entity, soundEvent, soundCategory, t -> true);
    }

    @Override
    public void update() {
        if (this.entity.isRemoved() || !predicate.test(this.entity)) {
            this.donePlaying = true;
        } else {
        	 EntityPlayer player = ClientProxy.MC.player;
             this.xPosF = (float) (entity.getX() + (player.getX() - entity.getX()) / 2);
             this.yPosF = (float) (entity.getY() + (player.getY() - entity.getY()) / 2);
             this.zPosF = (float) (entity.getZ() + (player.getZ() - entity.getZ()) / 2);
        }
    }

    public void setFinished() {
        this.donePlaying = true;
    }


}
