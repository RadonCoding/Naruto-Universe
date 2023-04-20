package radon.naruto_universe.capability.data;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.NarutoDamageSource;
import radon.naruto_universe.ability.special.Amaterasu;
import radon.naruto_universe.capability.ninja.MangekyoType;
import radon.naruto_universe.capability.ninja.ToggledEyes;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNarutoDataRemoteS2CPacket;

import java.util.*;

public class NarutoData implements INarutoData {
    private boolean burning;
    private ToggledEyes eyes;

    private final Set<UUID> cache = Sets.newLinkedHashSet();
    private final Set<UUID> cachedBurning = new LinkedHashSet<>();
    private final Map<UUID, ToggledEyes> cachedEyes = new HashMap<>();

    @Override
    public void tick(LivingEntity entity, boolean isClientSide) {
        if (this.burning) {
            entity.hurt(NarutoDamageSource.AMATERASU, Amaterasu.DAMAGE);
        }
    }

    @Override
    public boolean isRemoteBurning(UUID uuid) {
        return this.cachedBurning.contains(uuid);
    }

    @Override
    public boolean isLocalBurning() {
        return this.burning;
    }

    @Override
    public void setLocalBurning(LivingEntity owner, boolean burning) {
        this.burning = burning;

        if (!owner.level.isClientSide) {
            this.sync(owner);
        }
    }

    @Override
    public ToggledEyes getRemoteEyes(UUID uuid) {
        return this.cachedEyes.get(uuid);
    }

    @Override
    public ToggledEyes getLocalEyes() {
        return this.eyes;
    }

    @Override
    public void setLocalEyes(LivingEntity owner, ToggledEyes eyes) {
        this.eyes = eyes;

        if (!owner.level.isClientSide) {
            this.sync(owner);
        }
    }

    @Override
    public boolean isSynced(UUID uuid) {
        return this.cache.contains(uuid);
    }

    public void sync(LivingEntity owner) {
        PacketHandler.broadcast(new SyncNarutoDataRemoteS2CPacket(owner.getUUID(), this.serializeNBT()));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("burning", this.burning);

        boolean hasEyes = this.eyes != null;
        nbt.putBoolean("has_eyes", hasEyes);

        if (hasEyes) {
            nbt.putString("identifier", this.eyes.identifier.toString());
            nbt.putInt("sharingan_level", this.eyes.sharinganLevel);
            nbt.putInt("mangekyo_type", this.eyes.mangekyoType.ordinal());
        }
        return nbt;
    }

    @Override
    public void deserializeLocalNBT(CompoundTag nbt) {
        this.burning = nbt.getBoolean("burning");

        if (nbt.getBoolean("has_eyes")) {
            this.eyes = new ToggledEyes(new ResourceLocation(nbt.getString("identifier")), nbt.getInt("sharingan_level"), MangekyoType.values()[nbt.getInt("mangekyo_type")]);
        }
    }

    @Override
    public void deserializeRemoteNBT(UUID uuid, CompoundTag nbt) {
        this.cache.add(uuid);

        if (nbt.getBoolean("burning")) {
            this.cachedBurning.add(uuid);
        } else {
            this.cachedBurning.remove(uuid);
        }

        if (nbt.getBoolean("has_eyes")) {
            this.cachedEyes.put(uuid, new ToggledEyes(new ResourceLocation(nbt.getString("identifier")), nbt.getInt("sharingan_level"), MangekyoType.values()[nbt.getInt("mangekyo_type")]));
        } else {
            this.cachedEyes.remove(uuid);
        }
    }
}
