package radon.naruto_universe.capability;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.compress.utils.Lists;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.sound.SoundRegistry;

import java.util.*;
import java.util.function.Consumer;

public class NinjaPlayer implements INinjaPlayer {
    private long currentCombo;
    private float power;
    private int powerResetTimer;
    private float chakra;
    private float experience;
    private int sharinganLevel;
    private Vec3 oldPlayerPos;

    private ResourceLocation channeledAbility;
    private final List<ResourceLocation> toggledAbilities = Lists.newArrayList();
    private final List<NinjaTrait> traits = Lists.newArrayList();
    private final List<DelayedTickEvent> delayedTickEvents = Lists.newArrayList();
    private final List<ResourceLocation> unlockedAbilities = Lists.newArrayList();

    // Used for checking if the player's experience has changed
    private float oldExperience;

    public static final int POWER_RESET_TIME = 15;
    public static final float CHAKRA_CHARGE_AMOUNT = 0.05F;
    public static final float POWER_CHARGE_AMOUNT = 0.01F;
    public static final float CHAKRA_MINIMUM = 100.0F;
    public static final float CHAKRA_MULTIPLIER = 0.5F;
    public static final float MINIMUM_ABILITY_POWER = 10.0F;

    public static final double NINJA_SPEED_MULTIPLIER = 0.1D;

    private static final UUID MOVEMEMENT_SPEED_UUID = UUID.fromString("E8A3EE4A-B07F-48E4-A072-DAB79F4C35F1");

    public NinjaPlayer() {
        this.power = 0.0F;
        this.powerResetTimer = 0;
        this.chakra = 100.0F;
    }

    @Override
    public void tick(Player player, LogicalSide side, TickEvent.Phase phase) {
        updateChanneledAbilities(player, side);
        updateToggledAbilities(player, side);

        if (side == LogicalSide.SERVER && player instanceof ServerPlayer serverPlayer) {
            if (phase == TickEvent.Phase.START) {
                this.updateTickEvents(serverPlayer);

                if (this.power > 0.0F) {
                    if (this.powerResetTimer > POWER_RESET_TIME) {
                        this.power = 0.0F;
                        this.powerResetTimer = 0;
                    }
                    this.powerResetTimer++;
                }
            }
        }

        if (phase == TickEvent.Phase.START) {
            Vec3 currentPlayerPos = player.position();

            if (this.oldPlayerPos == currentPlayerPos) {
                this.addChakra(Math.max(CHAKRA_CHARGE_AMOUNT, this.getRank().ordinal() * CHAKRA_CHARGE_AMOUNT));
            } else {
                this.oldPlayerPos = currentPlayerPos;
            }
        }

        this.updateNinjaStats(player);
    }

    private void updateNinjaStats(Player player) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier speedModifier = new AttributeModifier(MOVEMEMENT_SPEED_UUID, "Movement speed", Math.max(1, this.getRank().ordinal()) * NINJA_SPEED_MULTIPLIER, AttributeModifier.Operation.ADDITION);

        if (this.oldExperience != this.experience) {
            this.oldExperience = this.experience;

            speedAttr.removeModifier(MOVEMEMENT_SPEED_UUID);
            speedAttr.addTransientModifier(speedModifier);
        }
    }

    @Override
    public void generateShinobi(Player player) {

    }

    public void setPowerResetTimer(int value) {
        this.powerResetTimer = value;
    }

    @Override
    public void delayTickEvent(Consumer<ServerPlayer> task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    private void updateTickEvents(ServerPlayer player) {
        final List<DelayedTickEvent> events = new ArrayList<>(this.delayedTickEvents);

        for (DelayedTickEvent event : events) {
            event.tick();

            if (event.run(player)) {
                this.delayedTickEvents.remove(event);
            }
        }
    }

    @Override
    public int getSharinganLevel() {
        return this.sharinganLevel;
    }

    @Override
    public void levelUpSharingan() {
        if (this.sharinganLevel == 3) {
            return;
        }
        this.sharinganLevel += 1;
    }

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    public void addPower(float amount) {
        float newPower = this.power + amount;
        this.power = Math.min(newPower, this.getMaxPower());
    }

    @Override
    public float getMaxPower() {
        return Math.max(MINIMUM_ABILITY_POWER, this.getRank().getExperience() / 100.0F);
    }

    @Override
    public void addChakra(float amount) {
        float newChakra = this.chakra + amount;
        this.chakra = Math.min(newChakra, this.getMaxChakra());
    }

    @Override
    public void setChakra(float chakra) {
        this.chakra = chakra;
    }

    @Override
    public void useChakra(float amount) {
        this.chakra -= amount;
    }

    @Override
    public float getChakra() {
        return this.chakra;
    }

    @Override
    public float getMaxChakra() {
        return Math.max(CHAKRA_MINIMUM, this.experience * CHAKRA_MULTIPLIER);
    }

    @Override
    public void addExperience(float amount) {
        this.experience += amount;
    }

    @Override
    public float getExperience() {
        return this.experience;
    }

    @Override
    public void setExperience(float experience) {
        this.experience = experience;
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.getRank(this.experience);
    }

    @Override
    public void setRank(NinjaRank rank) {
        this.experience = rank.getExperience();
    }

    @Override
    public void addTrait(NinjaTrait trait) {
        this.traits.add(trait);
    }

    @Override
    public boolean hasTrait(NinjaTrait trait) {
        return this.traits.contains(trait);
    }

    @Override
    public void unlockAbility(ResourceLocation key) {
        this.unlockedAbilities.add(key);
    }

    @Override
    public boolean hasUnlockedAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        return this.unlockedAbilities.contains(key);
    }

    @Override
    public void enableToggledAbility(Player player, Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        this.toggledAbilities.add(key);

        if (ability instanceof Ability.Toggled toggled) {
            player.level.playSound(null, player.blockPosition(),
                    toggled.getActivationSound(), SoundSource.PLAYERS, 10.0F, 1.0F);

            if (ability.shouldLog()) {
                player.sendSystemMessage(toggled.getEnableMessage());
            }
        }
    }

    @Override
    public void disableToggledAbility(Player player, Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        this.toggledAbilities.remove(key);

        if (ability instanceof Ability.Toggled toggled) {
            if (toggled.getDectivationSound() != null) {
                player.level.playSound(null, player.blockPosition(),
                        toggled.getDectivationSound(), SoundSource.PLAYERS, 10.0F, 1.0F);
            }

            if (ability.shouldLog()) {
                player.sendSystemMessage(toggled.getDisableMessage());
            }
        }
    }

    @Override
    public boolean hasToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.getKey(ability);
        return this.toggledAbilities.contains(key);
    }

    @Override
    public void clearToggledAbilities() {
        this.toggledAbilities.clear();
    }

    private void updateToggledAbilities(Player player, LogicalSide side) {
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

    @Override
    public ResourceLocation getChanneledAbility() {
        return this.channeledAbility;
    }

    @Override
    public void setChanneledAbility(Player player, Ability ability) {
        this.channeledAbility = AbilityRegistry.getKey(ability);

        if (ability instanceof Ability.Channeled channeled) {
            player.level.playSound(null, player.blockPosition(),
                    SoundRegistry.ABILITY_ACTIVATE.get(), SoundSource.PLAYERS, 10.0F, 1.0F);

            if (ability.shouldLog()) {
                player.sendSystemMessage(channeled.getStartMessage());
            }
        }
    }

    @Override
    public void stopChanneledAbility(Player player) {
        Ability ability = AbilityRegistry.getValue(this.channeledAbility);

        if (ability.shouldLog() && ability instanceof Ability.Channeled channeled) {
            player.sendSystemMessage(channeled.getStopMessage());
        }
        this.channeledAbility = null;
    }

    @Override
    public boolean isChannelingAbility(Ability ability) {
        if (this.channeledAbility == null) {
            return false;
        }
        ResourceLocation key = AbilityRegistry.getKey(ability);
        return this.channeledAbility.equals(key);
    }

    private void updateChanneledAbilities(Player player, LogicalSide side) {
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
        nbt.putLong("current_combo", this.currentCombo);
        nbt.putFloat("power", this.power);
        nbt.putFloat("power_reset_timer", this.powerResetTimer);
        nbt.putFloat("chakra", this.chakra);
        nbt.putFloat("experience", this.experience);
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
        this.currentCombo = nbt.getLong("current_combo");
        this.power = nbt.getFloat("power");
        this.powerResetTimer = nbt.getInt("power_reset_timer");
        this.chakra = nbt.getFloat("chakra");
        this.experience = nbt.getFloat("experience");
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
