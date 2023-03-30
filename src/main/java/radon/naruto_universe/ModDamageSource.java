package radon.naruto_universe;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class ModDamageSource {
    public static DamageSource kunai(Entity pSource, @Nullable Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource("kunai", pSource, pIndirectEntity)).setProjectile();
    }

    public static DamageSource jutsu(Entity pSource, @Nullable Entity pIndirectEntity) {
        return (new IndirectEntityDamageSource("jutsu", pSource, pIndirectEntity)).setProjectile();
    }
}
