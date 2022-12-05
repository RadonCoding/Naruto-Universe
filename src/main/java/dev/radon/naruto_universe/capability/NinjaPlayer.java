package dev.radon.naruto_universe.capability;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import dev.radon.naruto_universe.util.HelperMethods;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.compress.utils.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class NinjaPlayer implements INinjaPlayer {
    private NinjaClan clan;
    private long currentCombo;
    private float chakra;
    private float maxChakra;
    private float voicePitch;
    private int sharinganLevel;
    private ResourceLocation channeledAbility;

    private final List<ResourceLocation> toggledAbilities = Lists.newArrayList();
    private final List<NinjaTrait> traits = Lists.newArrayList();
    private final List<DelayedTickEvent> delayedTickEvents = Lists.newArrayList();
    private final List<ResourceLocation> unlockedAbilities = Lists.newArrayList();

    public NinjaPlayer() {
        this.chakra = 100.0F;
        this.maxChakra = 100.0F;
        this.voicePitch = 1.0F;
        this.traits.add(NinjaTrait.UNLOCKED_RINNEGAN);
    }

    public void tick(Player player, LogicalSide side) {
        updateChanneledAbilities(player, side);
        updateToggledAbilities(player, side);

        if (side == LogicalSide.SERVER && player instanceof ServerPlayer serverPlayer) {
            updateTickEvents(serverPlayer);
        }

        this.addChakra(0.001F);
    }

    public void generateShinobi(Player player) {
        NinjaClan clan = HelperMethods.randomEnum(NinjaClan.class);
        this.setClan(clan);

        player.sendSystemMessage(Component.translatable("give.clan", this.clan.getIdentifier()));

        List<NinjaTrait> releaseTraits = this.clan.getReleaseTraits();

        for (NinjaTrait trait : releaseTraits) {
            this.addTrait(trait);
            player.sendSystemMessage(Component.translatable("give.release", trait.getIdentifier()));
        }

        List<NinjaTrait> bloodlineTraits = this.clan.getBloodlineTraits();

        for (NinjaTrait trait : bloodlineTraits) {
            this.addTrait(trait);
            player.sendSystemMessage(Component.translatable("give.bloodline", trait.getIdentifier()));
        }
    }

    public void delayTickEvent(Consumer<ServerPlayer> task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    public void updateTickEvents(ServerPlayer player) {
        Iterator<DelayedTickEvent> iter = this.delayedTickEvents.iterator();

        while (iter.hasNext()) {
            DelayedTickEvent event = iter.next();

            event.tick();

            if (event.run(player)) {
                iter.remove();
            }
        }
    }

    public NinjaClan getClan() {
        return this.clan;
    }

    public void setClan(NinjaClan clan) {
        this.clan = clan;
    }

    public int getSharinganLevel() {
        return this.sharinganLevel;
    }

    public boolean levelUpSharingan() {
        if (this.sharinganLevel == 3) {
            return false;
        }
        this.sharinganLevel += 1;
        return true;
    }

    public void addChakra(float amount) {
        float newChakra = this.chakra + amount;
        this.chakra = Math.min(newChakra, this.maxChakra);
    }

    public void setChakra(float chakra) {
        this.chakra = chakra;
    }

    public void useChakra(float amount) {
        this.chakra -= amount;
    }

    public float getChakra() {
        return this.chakra;
    }

    public float getMaxChakra() {
        return this.maxChakra;
    }

    public float getVoicePitch() {
        return this.voicePitch;
    }

    public void setVoicePitch(float voicePitch) {
        this.voicePitch = voicePitch;
    }

    public void addTrait(NinjaTrait trait) {
        this.traits.add(trait);
    }

    public boolean hasTrait(NinjaTrait trait) {
        return this.traits.contains(trait);
    }

    public void unlockAbility(ResourceLocation key) {
        this.unlockedAbilities.add(key);
    }

    public boolean hasUnlockedAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        return this.unlockedAbilities.contains(key);
    }

    public void enableToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        this.toggledAbilities.add(key);
    }

    public void disableToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        this.toggledAbilities.remove(key);
    }

    public boolean hasToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        return this.toggledAbilities.contains(key);
    }

    public void updateToggledAbilities(Player player, LogicalSide side) {
        Iterator<ResourceLocation> iter = this.toggledAbilities.iterator();

        while (iter.hasNext()) {
            Ability toggled = AbilityRegistry.getValue(iter.next());

            if (side == LogicalSide.SERVER) {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!toggled.checkChakra(serverPlayer)) {
                        iter.remove();
                        PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(this.serializeNBT()), serverPlayer);
                    }
                    else {
                        toggled.runServer(serverPlayer);
                    }
                }
            }
            else {
                if (player instanceof LocalPlayer localPlayer) {
                    toggled.runClient(localPlayer);
                }
            }
        }
    }

    public ResourceLocation getChanneledAbility() {
        return this.channeledAbility;
    }

    public void setChanneledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        this.channeledAbility = key;
    }

    public void stopChanneledAbility() {
        this.channeledAbility = null;
    }

    public void updateChanneledAbilities(Player player, LogicalSide side) {
        Ability channeled = AbilityRegistry.getValue(this.channeledAbility);

        if (channeled != null) {
            if (side == LogicalSide.CLIENT) {
                if (player instanceof LocalPlayer localPlayer) {
                    channeled.runClient(localPlayer);
                }
            }
            else {
                if (player instanceof ServerPlayer serverPlayer) {
                    channeled.runServer(serverPlayer);
                }
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("clan", this.clan.name());
        nbt.putLong("current_combo", this.currentCombo);
        nbt.putFloat("chakra", this.chakra);
        nbt.putFloat("max_chakra", this.maxChakra);
        nbt.putFloat("voice_pitch", this.voicePitch);
        nbt.putInt("sharingan_level", this.sharinganLevel);

        if (this.channeledAbility != null) {
            nbt.putString("currently_channeled", this.channeledAbility.toString());
        }

        ListTag toggledAbilitiesTag = new ListTag();

        for (var key : this.toggledAbilities) {
            toggledAbilitiesTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("currently_toggled", toggledAbilitiesTag);

        ListTag unlockedAbilitiesTag = new ListTag();

        for (var key : this.unlockedAbilities) {
            unlockedAbilitiesTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("unlocked", unlockedAbilitiesTag);

        ListTag traitsTag = new ListTag();

        for (var trait : this.traits) {
            traitsTag.add(StringTag.valueOf(trait.name()));
        }
        nbt.put("traits", traitsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.clan = NinjaClan.valueOf(nbt.getString("clan"));
        this.currentCombo = nbt.getLong("current_combo");
        this.chakra = nbt.getFloat("chakra");
        this.maxChakra = nbt.getFloat("max_chakra");
        this.voicePitch = nbt.getFloat("voice_pitch");
        this.sharinganLevel = nbt.getInt("sharingan_level");

        this.channeledAbility = new ResourceLocation(nbt.getString("currently_channeled"));

        this.toggledAbilities.clear();
        this.traits.clear();

        if (nbt.contains("currently_toggled")) {
            for (var key : nbt.getList("currently_toggled", Tag.TAG_STRING)) {
                this.toggledAbilities.add(new ResourceLocation(key.getAsString()));
            }
        }

        if (nbt.contains("unlocked")) {
            for (var key : nbt.getList("unlocked", Tag.TAG_STRING)) {
                this.unlockedAbilities.add(new ResourceLocation(key.getAsString()));
            }
        }

        if (nbt.contains("traits")) {
            for (var trait : nbt.getList("traits", Tag.TAG_STRING)) {
                this.traits.add(NinjaTrait.valueOf(trait.getAsString()));
            }
        }
    }
}
