package radon.naruto_universe.capability;

import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.sound.SoundRegistry;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.util.HelperMethods;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.compress.utils.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class NinjaPlayer implements INinjaPlayer {
    private NinjaClan clan;
    private long currentCombo;
    private float power;
    private float powerResetTimer;
    private float chakra;
    private float maxChakra;
    private NinjaRank rank;
    private int sharinganLevel;

    private ResourceLocation channeledAbility;
    private final List<ResourceLocation> toggledAbilities = Lists.newArrayList();
    private final List<NinjaTrait> traits = Lists.newArrayList();
    private final List<DelayedTickEvent> delayedTickEvents = Lists.newArrayList();
    private final List<ResourceLocation> unlockedAbilities = Lists.newArrayList();

    public static final float POWER_RESET_TIME = 160.0F;
    public static final float MAX_ABILITY_POWER = 10.0F;

    public NinjaPlayer() {
        this.power = 0.0F;
        this.powerResetTimer = 0.0F;
        this.chakra = 100.0F;
        this.maxChakra = 100.0F;
        this.rank = NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public void tick(Player player, LogicalSide side) {
        updateChanneledAbilities(player, side);
        updateToggledAbilities(player, side);

        if (side == LogicalSide.SERVER) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.updateTickEvents(serverPlayer);
            }
            if (this.power > 0.0F) {
                if (this.powerResetTimer < POWER_RESET_TIME) {
                    this.powerResetTimer++;
                } else {
                    this.power = 0.0F;
                    this.powerResetTimer = 0.0F;
                }
            }
        }
        this.addChakra(0.01F);
    }

    @Override
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

    public void startPowerReset() {
        this.powerResetTimer = 0.0F;
    }

    @Override
    public void delayTickEvent(Consumer<ServerPlayer> task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    private void updateTickEvents(ServerPlayer player) {
        Iterator<DelayedTickEvent> iter = this.delayedTickEvents.iterator();

        while (iter.hasNext()) {
            DelayedTickEvent event = iter.next();

            event.tick();

            if (event.run(player)) {
                iter.remove();
            }
        }
    }

    @Override
    public NinjaClan getClan() {
        return this.clan;
    }

    @Override
    public void setClan(NinjaClan clan) {
        this.clan = clan;
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
        this.power += amount;
    }

    @Override
    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public void addChakra(float amount) {
        float newChakra = this.chakra + amount;
        this.chakra = Math.min(newChakra, this.maxChakra);
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
        return this.maxChakra;
    }

    @Override
    public NinjaRank getRank() {
        return this.rank;
    }

    @Override
    public void setRank(NinjaRank rank) {
        this.rank = rank;
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
        nbt.putString("clan", this.clan.name());
        nbt.putLong("current_combo", this.currentCombo);
        nbt.putFloat("power", this.power);
        nbt.putFloat("chakra", this.chakra);
        nbt.putFloat("max_chakra", this.maxChakra);
        nbt.putString("rank", this.rank.name());
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
        this.power = nbt.getFloat("power");
        this.chakra = nbt.getFloat("chakra");
        this.maxChakra = nbt.getFloat("max_chakra");
        this.rank = NinjaRank.valueOf(nbt.getString("rank"));
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
