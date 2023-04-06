package radon.naruto_universe.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NinjaPlayer implements INinjaPlayer {
    private long currentCombo;
    private float power;
    private int powerResetTimer;
    private float chakra;
    private float experience;
    private int sharinganLevel;
    private Vec3 oldPlayerPos;

    // Used for storing the player's movement speed from client for some abilities
    private double movementSpeed;

    private Ability channeledAbility;
    private final List<Ability> toggledAbilities = Lists.newArrayList();
    private final List<NinjaTrait> traits = Lists.newArrayList();
    private final List<DelayedTickEvent> delayedTickEvents = Lists.newArrayList();
    private final List<Ability> unlockedAbilities = Lists.newArrayList();
    private final List<Ability> specialAbilities = Lists.newArrayList();

    // Used for checking if the player's experience has changed
    private float oldExperience;

    private static final UUID MOVEMEMENT_SPEED_UUID = UUID.fromString("E8A3EE4A-B07F-48E4-A072-DAB79F4C35F1");
    public static final int POWER_RESET_TIME = 20;
    public static final float CHAKRA_REGEN_AMOUNT = 0.05F;
    public static final float NINJA_SPEED = 0.15F;
    public static final float POWER_AMOUNT = 10.0F;
    public static final float MAX_POWER = 30.0F;
    public static final float POWER_CHARGE_AMOUNT = 0.01F;
    public static final float CHAKRA_AMOUNT = 100.0F;

    public NinjaPlayer() {
        this.power = 0.0F;
        this.powerResetTimer = 0;
        this.chakra = 100.0F;
    }

    @Override
    public void tick(LivingEntity entity, boolean isClientSide) {
        updateChanneledAbilities(entity, isClientSide);
        updateToggledAbilities(entity, isClientSide);

        if (!isClientSide) {
            this.updateTickEvents(entity);
        }

        if (this.power > 0.0F) {
            if (this.powerResetTimer > POWER_RESET_TIME) {
                if (isClientSide) {
                    System.out.println("RESETTING POWER ON CLIENT");
                }
                else {
                    System.out.println("RESETTING POWER ON SERVER");
                }
                this.power = 0.0F;
                this.powerResetTimer = 0;
            }
            this.powerResetTimer++;
        }

        Vec3 currentPlayerPos = entity.position();

        if (this.oldPlayerPos == currentPlayerPos) {
            this.addChakra(Math.max(CHAKRA_REGEN_AMOUNT, this.getRank().ordinal() * CHAKRA_REGEN_AMOUNT));
        } else {
            this.oldPlayerPos = currentPlayerPos;
        }
        this.updateNinjaStats(entity);
    }

    private void updateNinjaStats(LivingEntity entity) {
        AttributeInstance speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier speedModifier = new AttributeModifier(MOVEMEMENT_SPEED_UUID, "Movement speed",
                this.getRank().ordinal() * NINJA_SPEED, AttributeModifier.Operation.ADDITION);

        if (this.oldExperience != this.experience) {
            this.oldExperience = this.experience;

            assert speedAttr != null;
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
    public void delayTickEvent(Consumer<LivingEntity> task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    private void updateTickEvents(LivingEntity entity) {
        final List<DelayedTickEvent> events = new ArrayList<>(this.delayedTickEvents);

        for (DelayedTickEvent event : events) {
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
    public void enableToggledAbility(LivingEntity entity, Ability ability) {
        this.toggledAbilities.add(ability);

        if (ability instanceof Ability.IToggled toggled) {
            entity.level.playSound(null, entity.blockPosition(),
                    toggled.getActivationSound(), SoundSource.PLAYERS, 10.0F, 1.0F);

            if (ability.shouldLog(entity)) {
                entity.sendSystemMessage(toggled.getEnableMessage());
            }
        }
        this.updateSpecialAbilities();
    }

    @Override
    public void disableToggledAbility(LivingEntity entity, Ability ability) {
        this.toggledAbilities.remove(ability);

        if (ability instanceof Ability.IToggled toggled) {
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
        List<Ability> remove = Lists.newArrayList();

        for (Ability toggled : this.toggledAbilities) {
            if (toggled != exclude && toggled.isDojutsu()) {
                remove.add(toggled);
            }
        }
        remove.forEach(x -> this.disableToggledAbility(entity, x));
    }

    private void updateSpecialAbilities() {
        this.specialAbilities.clear();

        for (Ability toggled : this.toggledAbilities) {
            if (toggled instanceof Ability.ISpecial special) {
                this.specialAbilities.addAll(special.getSpecialAbilities());
            }
        }
    }

    @Override
    public List<Ability> getSpecialAbilities() {
        return this.specialAbilities;
    }

    private void updateToggledAbilities(LivingEntity entity, boolean isClientSide) {
        Iterator<Ability> iter = this.toggledAbilities.iterator();

        while (iter.hasNext()) {
            Ability toggled = iter.next();
            if (toggled.checkChakra(entity) != Ability.FailStatus.SUCCESS) {
                iter.remove();
            } else {
                if (isClientSide) {
                    toggled.runClient(entity);
                }
                else {
                    toggled.runServer(entity);
                }
            }
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

    private void updateChanneledAbilities(LivingEntity entity, boolean isClientSide) {
        if (this.channeledAbility != null) {
            if (isClientSide) {
                this.channeledAbility.runClient(entity);
            }
            else {
                this.channeledAbility.runServer(entity);
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

        if (this.channeledAbility != null) {
            nbt.putString("channeled", NarutoAbilities.getKey(this.channeledAbility).toString());
        }

        ListTag specialAbilitiesTag = new ListTag();

        for (Ability ability : this.specialAbilities) {
            specialAbilitiesTag.add(StringTag.valueOf(NarutoAbilities.getKey(ability).toString()));
        }
        nbt.put("special", specialAbilitiesTag);

        ListTag toggledAbilitiesTag = new ListTag();

        for (Ability ability : this.toggledAbilities) {
            toggledAbilitiesTag.add(StringTag.valueOf(NarutoAbilities.getKey(ability).toString()));
        }
        nbt.put("toggled", toggledAbilitiesTag);

        ListTag unlockedAbilitiesTag = new ListTag();

        for (Ability ability : this.unlockedAbilities) {
            unlockedAbilitiesTag.add(StringTag.valueOf(NarutoAbilities.getKey(ability).toString()));
        }
        nbt.put("unlocked", unlockedAbilitiesTag);

        ListTag traitsTag = new ListTag();

        for (NinjaTrait trait : this.traits) {
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

        if (nbt.contains("currently_channeled")) {
            this.channeledAbility = NarutoAbilities.getValue(new ResourceLocation(nbt.getString("channeled")));
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

        for (Tag trait : nbt.getList("traits", Tag.TAG_STRING)) {
            this.traits.add(NinjaTrait.valueOf(trait.getAsString()));
        }
    }
}
