package radon.naruto_universe.capability;

import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.ClearEyeStatusS2CPacket;
import radon.naruto_universe.network.packet.UpdateEyeStatusS2CPacket;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.*;
import java.util.function.Consumer;

public class NinjaPlayer implements INinjaPlayer {
    private long currentCombo;
    private float power;
    private int powerResetTimer;
    private float chakra;
    private float experience;
    private int sharinganLevel;
    private MangekyoType mangekyoType;
    private float mangekyoBlindess;
    private boolean initialized;

    // Used for storing the player's movement speed from client for some abilities
    private double movementSpeed;

    private Ability channeledAbility;
    private final List<Ability> toggledAbilities = new ArrayList<>();
    private final List<NinjaTrait> traits = new ArrayList<>();
    private final List<DelayedTickEvent> delayedTickEvents = new ArrayList<>();
    private final List<Ability> unlockedAbilities = new ArrayList<>();
    private final List<Ability> specialAbilities = new ArrayList<>();
    private final Map<Ability, Integer> cooldowns = new HashMap<>();
    private final Map<UUID, ToggledEyes> cachedEyes = new HashMap<>();

    // Used for checking if the player's experience has changed
    private float oldExperience;

    private static final UUID MOVEMEMENT_SPEED_UUID = UUID.fromString("E8A3EE4A-B07F-48E4-A072-DAB79F4C35F1");
    public static int POWER_RESET_TIME = 20;
    public static float CHAKRA_REGEN_AMOUNT = 0.05F;
    public static float NINJA_SPEED = 0.15F;
    public static float POWER_AMOUNT = 10.0F;
    public static float MAX_POWER = 30.0F;
    public static float POWER_CHARGE_AMOUNT = 0.01F;
    public static float CHAKRA_AMOUNT = 100.0F;

    public NinjaPlayer() {
        this.power = 0.0F;
        this.powerResetTimer = 0;
        this.chakra = 100.0F;
    }

    @Override
    public void tick(LivingEntity owner, boolean isClientSide) {
        Vec3 currPos = owner.position();
        Vec3 oldPos = new Vec3(owner.xOld, owner.yOld, owner.zOld);

        if (oldPos.equals(currPos)) {
            this.addChakra(Math.max(CHAKRA_REGEN_AMOUNT, this.getRank().ordinal() * CHAKRA_REGEN_AMOUNT));
        }

        this.updateChanneledAbilities(owner, isClientSide);
        this.updateToggledAbilities(owner, isClientSide);
        this.updateCooldowns();

        this.updateTickEvents(owner, isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);

        if (this.power > 0.0F) {
            if (this.powerResetTimer > POWER_RESET_TIME) {
                this.power = 0.0F;
                this.powerResetTimer = 0;
            }
            this.powerResetTimer++;
        }
        this.updateNinjaStats(owner);
    }

    @Override
    public void generateNinja() {
        this.mangekyoType = HelperMethods.randomEnum(MangekyoType.class);
    }

    private void updateNinjaStats(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed != null) {
            if (this.toggledAbilities.contains(NarutoAbilities.CHAKRA_CONTROL.get())) {
                AttributeModifier speedModifier = new AttributeModifier(MOVEMEMENT_SPEED_UUID, "Movement speed",
                        this.getRank().ordinal() * NINJA_SPEED, AttributeModifier.Operation.ADDITION);

                if (!speed.hasModifier(speedModifier)) {
                    speed.addTransientModifier(speedModifier);
                }
                else if (this.oldExperience != this.experience) {
                    this.oldExperience = this.experience;
                    speed.removeModifier(MOVEMEMENT_SPEED_UUID);
                    speed.addTransientModifier(speedModifier);
                }
            } else {
                speed.removeModifier(MOVEMEMENT_SPEED_UUID);
            }
        }
    }

    public void setPowerResetTimer(int value) {
        this.powerResetTimer = value;
    }

    @Override
    public void delayTickEvent(Consumer<LivingEntity> task, int delay, LogicalSide side) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay, side));
    }

    @Override
    public float getMangekyoBlindess() {
        return this.mangekyoBlindess;
    }

    @Override
    public void increaseMangekyoBlindess(float amount) {
        this.mangekyoBlindess += amount;
    }

    @Override
    public ToggledEyes getToggledEyes(UUID uuid) {
        return this.cachedEyes.get(uuid);
    }

    @Override
    public void saveToggledEyes(UUID uuid, ToggledEyes eyes) {
        this.cachedEyes.put(uuid, eyes);
    }

    @Override
    public void removeToggledEyes(UUID uuid) {
        this.cachedEyes.remove(uuid);
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    private void updateTickEvents(LivingEntity entity, LogicalSide side) {
        List<DelayedTickEvent> events = new ArrayList<>(this.delayedTickEvents);

        for (DelayedTickEvent event : events) {
            if (event.getSide() != side) {
                continue;
            }

            event.tick();

            if (event.run(entity)) {
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
    public MangekyoType getMangekyoType() {
        return this.mangekyoType;
    }

    @Override
    public void setMangekyoType(MangekyoType type) {
        this.mangekyoType = type;
    }

    private void updateCooldowns() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.cooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.cooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    @Override
    public void addCooldown(Ability ability) {
        this.cooldowns.put(ability, ability.getCooldown());
    }

    @Override
    public int getRemainingCooldown(Ability ability) {
        return this.cooldowns.get(ability);
    }

    @Override
    public boolean isCooldownDone(Ability ability) {
        return !this.cooldowns.containsKey(ability);
    }

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    public void resetPower() {
        this.power = 0.0F;
    }

    @Override
    public void addPower(float amount) {
        float newPower = this.power + amount;
        this.power = Math.min(newPower, this.getMaxPower());
    }

    @Override
    public float getMaxPower() {
        return Math.min(MAX_POWER, Math.max(POWER_AMOUNT, this.experience / 100.0F));
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
        return Math.max(CHAKRA_AMOUNT, CHAKRA_AMOUNT * (this.experience / 100.0F));
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
    public void unlockAbility(Ability ability) {
        this.unlockedAbilities.add(ability);
    }

    @Override
    public boolean hasUnlockedAbility(Ability ability) {
        return this.unlockedAbilities.contains(ability);
    }

    @Override
    public Ability getCurrentEyes() {
        return this.toggledAbilities.stream().filter(Ability::isDojutsu).findFirst().orElse(null);
    }

    @Override
    public void enableToggledAbility(LivingEntity owner, Ability ability) {
        this.toggledAbilities.add(ability);

        if (!owner.level.isClientSide && ability.isDojutsu()) {
            PacketHandler.broadcast(new UpdateEyeStatusS2CPacket(owner.getUUID(), new ToggledEyes(ability.getId(), this.sharinganLevel, this.mangekyoType)));
        }

        if (ability instanceof Ability.IToggled toggled) {
            toggled.onToggled(owner, owner.level.isClientSide);

            owner.level.playSound(null, owner.blockPosition(),
                    toggled.getActivationSound(), SoundSource.PLAYERS, 10.0F, 1.0F);

            if (ability.shouldLog(owner)) {
                owner.sendSystemMessage(toggled.getEnableMessage());
            }
        }
        this.updateSpecialAbilities();
    }

    @Override
    public void disableToggledAbility(LivingEntity entity, Ability ability) {
        this.toggledAbilities.remove(ability);

        if (!entity.level.isClientSide && ability.isDojutsu()) {
            PacketHandler.broadcast(new ClearEyeStatusS2CPacket(entity.getUUID()));
        }

        if (ability instanceof Ability.IToggled toggled) {
            toggled.onDisabled(entity, entity.level.isClientSide);

            if (toggled.getDectivationSound() != null) {
                entity.level.playSound(null, entity.blockPosition(),
                        toggled.getDectivationSound(), SoundSource.PLAYERS, 10.0F, 1.0F);
            }

            if (ability.shouldLog(entity)) {
                entity.sendSystemMessage(toggled.getDisableMessage());
            }
        }
        this.updateSpecialAbilities();
    }

    @Override
    public boolean hasToggledAbility(Ability ability) {
        return this.toggledAbilities.contains(ability);
    }

    @Override
    public void clearToggledAbilities() {
        this.toggledAbilities.clear();
    }

    @Override
    public void clearToggledDojutsus(LivingEntity entity, Ability exclude) {
        this.toggledAbilities.removeIf(toggled -> toggled != exclude && toggled.isDojutsu());
    }

    private void updateSpecialAbilities() {
        this.specialAbilities.clear();

        for (Ability toggled : this.toggledAbilities) {
            if (toggled instanceof Ability.ISpecial special) {
                this.specialAbilities.addAll(special.getSpecialAbilities());
            }
        }

        if (!this.specialAbilities.contains(NarutoAbilities.SUSANOO.get()) && this.unlockedAbilities.contains(NarutoAbilities.MANGEKYO.get())) {
            this.specialAbilities.add(NarutoAbilities.SUSANOO.get());
        }
    }

    @Override
    public List<Ability> getSpecialAbilities() {
        return this.specialAbilities;
    }

    private void updateToggledAbilities(LivingEntity owner, boolean isClientSide) {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : this.toggledAbilities) {
            if (ability.checkStatus(owner) != Ability.Status.SUCCESS) {
                remove.add(ability);
            } else {
                if (isClientSide) {
                    ability.runClient(owner);
                }
                else {
                    ability.runServer(owner);
                }
            }
        }

        for (Ability ability : remove) {
            disableToggledAbility(owner, ability);
        }
    }

    @Override
    public Ability getChanneledAbility() {
        return this.channeledAbility;
    }

    @Override
    public void setChanneledAbility(LivingEntity entity, Ability ability) {
        this.channeledAbility = ability;

        if (this.channeledAbility instanceof Ability.IChanneled channeled) {
            entity.level.playSound(null, entity.blockPosition(),
                    NarutoSounds.ABILITY_ACTIVATE.get(), SoundSource.PLAYERS, 10.0F, 1.0F);

            if (this.channeledAbility.shouldLog(entity)) {
                entity.sendSystemMessage(channeled.getStartMessage());
            }
        }
    }

    @Override
    public void stopChanneledAbility(LivingEntity entity) {
        if (this.channeledAbility.shouldLog(entity) && this.channeledAbility instanceof Ability.IChanneled channeled) {
            entity.sendSystemMessage(channeled.getStopMessage());
        }
        this.channeledAbility = null;
    }

    @Override
    public boolean isChannelingAbility(Ability ability) {
        if (this.channeledAbility == null) {
            return false;
        }
        return this.channeledAbility == ability;
    }

    @Override
    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    @Override
    public double getMovementSpeed() {
        return this.movementSpeed;
    }

    private void updateChanneledAbilities(LivingEntity owner, boolean isClientSide) {
        if (this.channeledAbility != null) {
            if (this.channeledAbility.checkStatus(owner) != Ability.Status.SUCCESS) {
                this.stopChanneledAbility(owner);
            }
            else {
                if (isClientSide) {
                    this.channeledAbility.runClient(owner);
                } else {
                    this.channeledAbility.runServer(owner);
                }
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("current_combo", this.currentCombo);
        nbt.putFloat("power", this.power);
        nbt.putInt("power_reset_timer", this.powerResetTimer);
        nbt.putFloat("chakra", this.chakra);
        nbt.putFloat("experience", this.experience);
        nbt.putInt("sharingan_level", this.sharinganLevel);
        nbt.putFloat("mangekyo_blindness", this.mangekyoBlindess);
        nbt.putBoolean("initialized", this.initialized);

        if (this.mangekyoType != null) {
            nbt.putInt("mangekyo_type", this.mangekyoType.ordinal());
        }

        ListTag specialAbilitiesTag = new ListTag();

        for (Ability ability : this.specialAbilities) {
            specialAbilitiesTag.add(StringTag.valueOf(ability.getId().toString()));
        }
        nbt.put("special", specialAbilitiesTag);

        ListTag toggledAbilitiesTag = new ListTag();

        for (Ability ability : this.toggledAbilities) {
            toggledAbilitiesTag.add(StringTag.valueOf(ability.getId().toString()));
        }
        nbt.put("toggled", toggledAbilitiesTag);

        ListTag unlockedAbilitiesTag = new ListTag();

        for (Ability ability : this.unlockedAbilities) {
            unlockedAbilitiesTag.add(StringTag.valueOf(ability.getId().toString()));
        }
        nbt.put("unlocked", unlockedAbilitiesTag);

        ListTag traitsTag = new ListTag();

        for (NinjaTrait trait : this.traits) {
            traitsTag.add(IntTag.valueOf(trait.ordinal()));
        }
        nbt.put("traits", traitsTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            CompoundTag cooldown = new CompoundTag();
            cooldown.putString("identifier", entry.getKey().getId().toString());
            cooldown.putInt("cooldown", entry.getValue());
            cooldownsTag.add(cooldown);
        }
        nbt.put("cooldowns", cooldownsTag);

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
        this.mangekyoBlindess = nbt.getFloat("mangekyo_blindness");
        this.initialized = nbt.getBoolean("initialized");

        if (nbt.contains("mangekyo_type")) {
            this.mangekyoType = MangekyoType.values()[nbt.getInt("mangekyo_type")];
        }

        this.specialAbilities.clear();
        this.toggledAbilities.clear();
        this.traits.clear();

        for (Tag key : nbt.getList("special", Tag.TAG_STRING)) {
            this.specialAbilities.add(NarutoAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggledAbilities.add(NarutoAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        for (Tag key : nbt.getList("unlocked", Tag.TAG_STRING)) {
            this.unlockedAbilities.add(NarutoAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        ListTag traits = nbt.getList("traits", Tag.TAG_INT);

        for (int i = 0; i < traits.size(); i++) {
            this.traits.add(NinjaTrait.values()[traits.getInt(i)]);
        }

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag cooldown = (CompoundTag) key;
            this.cooldowns.put(NarutoAbilities.getValue(new ResourceLocation(cooldown.getString("identifier"))), cooldown.getInt("cooldown"));
        }
    }
}
