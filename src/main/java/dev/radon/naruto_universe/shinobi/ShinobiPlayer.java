package dev.radon.naruto_universe.shinobi;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class ShinobiPlayer {
    private long currentCombo;
    private float chakra;
    private float maxChakra;
    private ResourceLocation channeledAbility;

    private List<ResourceLocation> toggledAbilities = Lists.newArrayList();
    private List<DelayedTickEvent> delayedTickEvents = Lists.newArrayList();

    public ShinobiPlayer() {
        this.chakra = 25.0F;
        this.maxChakra = 25.0F;
    }

    public void scheduleDelayedTickEvent(int delay, Runnable task) {
        DelayedTickEvent event = new DelayedTickEvent(delay, task);
        delayedTickEvents.add(event);
    }

    public void updateDelayedTickEvents() {
        var iter = delayedTickEvents.iterator();

        while (iter.hasNext()) {
            DelayedTickEvent event = iter.next();

            event.tick();

            if (event.run()) {
                iter.remove();
            }
        }
    }

    public void addChakra(float amount) {
        this.chakra += amount;
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

    public List<ResourceLocation> getToggledAbilities() {
        return this.toggledAbilities;
    }

    public void enableToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);
        this.toggledAbilities.add(key);
    }

    public void disableToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);
        this.toggledAbilities.remove(key);
    }

    public boolean hasToggledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);
        return this.toggledAbilities.contains(key);
    }

    public ResourceLocation getChanneledAbility() {
        return this.channeledAbility;
    }

    public void startChanneledAbility(Ability ability) {
        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);
        this.channeledAbility = key;
    }

    public void stopChanneledAbility() {
        this.channeledAbility = null;
    }

    public void copyFrom(ShinobiPlayer src) {
        this.currentCombo = src.currentCombo;
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("current_combo", this.currentCombo);
        nbt.putFloat("chakra", this.chakra);
        nbt.putFloat("max_chakra", this.maxChakra);

        if (this.channeledAbility != null) {
            nbt.putString("currently_channeled", this.channeledAbility.toString());
        }
        if (this.toggledAbilities != null) {
            ListTag toggledAbilitiesTag = new ListTag();

            for (var key : this.toggledAbilities) {
                toggledAbilitiesTag.add(StringTag.valueOf(key.toString()));
            }

            nbt.put("currently_toggled", toggledAbilitiesTag);
        }
        return nbt;
    }

    public void deserialize(CompoundTag nbt) {
        this.currentCombo = nbt.getLong("current_combo");
        this.chakra = nbt.getFloat("chakra");
        this.maxChakra = nbt.getFloat("max_chakra");

        if (nbt.contains("currently_channeled")) {
            this.channeledAbility = new ResourceLocation(nbt.getString("currently_channeled"));
        }
        if (nbt.contains("currently_toggled")) {
            for (var key : nbt.getList("currently_toggled", Tag.TAG_STRING)) {
                this.toggledAbilities.add(new ResourceLocation(key.getAsString()));
            }
        }
    }
}
