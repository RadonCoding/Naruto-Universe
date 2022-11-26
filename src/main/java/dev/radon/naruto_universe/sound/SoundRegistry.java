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

    public static final RegistryObject<SoundEvent> HAND_SIGN =
            SOUNDS.register("hand_sign", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign")));
    public static final RegistryObject<SoundEvent> ABILITY_ACTIVATE =
            SOUNDS.register("ability_activate", () -> new SoundEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "ability_activate")));
}
