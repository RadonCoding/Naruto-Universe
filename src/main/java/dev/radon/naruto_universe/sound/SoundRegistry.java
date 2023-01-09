package dev.radon.naruto_universe.sound;

import dev.radon.naruto_universe.NarutoUniverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<SoundEvent> HAND_SIGN_ONE =
            SOUNDS.register("hand_sign_one", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_one")));

    public static final RegistryObject<SoundEvent> HAND_SIGN_TWO =
            SOUNDS.register("hand_sign_two", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_two")));

    public static final RegistryObject<SoundEvent> HAND_SIGN_THREE =
            SOUNDS.register("hand_sign_three", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_three")));
    public static final RegistryObject<SoundEvent> ABILITY_ACTIVATE =
            SOUNDS.register("ability_activate", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "ability_activate")));

    public static final RegistryObject<SoundEvent> CHAKRA_JUMP =
            SOUNDS.register("chakra_jump", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "chakra_jump")));

    public static final RegistryObject<SoundEvent> GREAT_FIREBALL =
            SOUNDS.register("great_fireball", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "great_fireball")));

    public static final RegistryObject<SoundEvent> PHOENIX_FLOWER =
            SOUNDS.register("phoenix_flower", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "phoenix_flower")));

    public static final RegistryObject<SoundEvent> SHARINGAN_ACTIVATE =
            SOUNDS.register("sharingan_activate", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "sharingan_activate")));

    public static final RegistryObject<SoundEvent> SHARINGAN_DEACTIVATE =
            SOUNDS.register("sharingan_deactivate", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "sharingan_deactivate")));

    public static final RegistryObject<SoundEvent> RINNEGAN_ACTIVATE =
            SOUNDS.register("rinnegan_activate", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "rinnegan_activate")));

    public static final RegistryObject<SoundEvent> KUNAI_THROW =
            SOUNDS.register("kunai_throw", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "kunai_throw")));

    public static final RegistryObject<SoundEvent> KUNAI_HIT =
            SOUNDS.register("kunai_hit", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "kunai_hit")));

    public static final RegistryObject<SoundEvent> KUNAI_HIT_WOOD =
            SOUNDS.register("kunai_hit_wood", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "kunai_hit_wood")));
}
