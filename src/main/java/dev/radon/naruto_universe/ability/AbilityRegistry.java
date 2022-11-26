package dev.radon.naruto_universe.ability;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.jutsu.GreatFireballJutsu;
import dev.radon.naruto_universe.ability.utility.ChakraCharge;
import dev.radon.naruto_universe.ability.utility.ChakraControl;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

public class AbilityRegistry {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(NarutoUniverse.MOD_ID, "ability"), NarutoUniverse.MOD_ID);
    public static final Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Ability> CHAKRA_CONTROL =
            ABILITIES.register("chakra_control", ChakraControl::new);

    public static final RegistryObject<Ability> CHAKRA_CHARGE =
            ABILITIES.register("chakra_charge", ChakraCharge::new);

    public static final RegistryObject<Ability> GREAT_FIREBALL =
            ABILITIES.register("great_fireball_jutsu", GreatFireballJutsu::new);

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
}
