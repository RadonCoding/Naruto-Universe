package dev.radon.naruto_universe.ability;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.jutsu.GreatFireball;
import dev.radon.naruto_universe.ability.utility.ChakraCharge;
import dev.radon.naruto_universe.ability.utility.Rinnegan;
import dev.radon.naruto_universe.ability.utility.Sharingan;
import dev.radon.naruto_universe.ability.utility.WaterWalking;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.KeyRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AbilityRegistry {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(NarutoUniverse.MOD_ID, "ability"), NarutoUniverse.MOD_ID);
    public static final Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Ability> WATER_WALKING =
            ABILITIES.register("water_walking", WaterWalking::new);

    public static final RegistryObject<Ability> CHAKRA_CHARGE =
            ABILITIES.register("chakra_charge", ChakraCharge::new);

    public static final RegistryObject<Ability> GREAT_FIREBALL =
            ABILITIES.register("great_fireball", GreatFireball::new);
    public static final RegistryObject<Ability> SHARINGAN =
            ABILITIES.register("sharingan", Sharingan::new);
    public static final RegistryObject<Ability> RINNEGAN =
            ABILITIES.register("rinnegan", Rinnegan::new);

    private static HashMap<Long, ResourceLocation> COMBO_MAP = new HashMap<>();

    public static void registerCombos() {
        for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            ABILITY_REGISTRY.get().getResourceKey(ability.get()).ifPresent(key ->
                COMBO_MAP.put(ability.get().getCombo(), key.location()));
        }
    }

    public static Ability getAbility(long combo) {
        ResourceLocation key = COMBO_MAP.get(combo);

        if (key != null) {
            return ABILITY_REGISTRY.get().getValue(key);
        }
        return null;
    }

    public static float getProgress(Player player, Ability ability) {
        AtomicReference<Float> progress = new AtomicReference(0.0F);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            List<NinjaTrait> requirements = ability.getRequirements();

            int completed = 0;

            for (NinjaTrait requirement : requirements) {
                if (cap.hasTrait(requirement)) {
                    completed++;
                }
            }

            progress.set(requirements.size() > 0 ? (float) completed / (float) requirements.size() : 1.0F);
        });

        return progress.get();
    }

    private static void collectDigits(long num, List<Integer> digits) {
        if (num / 10 > 0) {
            collectDigits(num / 10, digits);
        }
        digits.add((int)(num % 10));
    }

    public static String getStringFromCombo(long combo) {
        StringBuilder result = new StringBuilder();

        List<Integer> digits = Lists.newArrayList();

        collectDigits(combo, digits);

        for (int digit : digits) {
            switch (digit) {
                case 1:
                    result.append((char) KeyRegistry.KEY_HAND_SIGN_ONE.getKey().getValue());
                    break;
                case 2:
                    result.append((char) KeyRegistry.KEY_HAND_SIGN_TWO.getKey().getValue());
                    break;
                case 3:
                    result.append((char) KeyRegistry.KEY_HAND_SIGN_THREE.getKey().getValue());
                    break;
            }
        }
        return result.toString();
    }

    public static ResourceLocation getKey(Ability ability) {
        return AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return AbilityRegistry.ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean isUnlocked(Player player, Ability ability) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasUnlockedAbility(ability)) {
                result.set(true);
            }
        });
        return result.get();
    }
}
