package radon.naruto_universe.sound;

import radon.naruto_universe.NarutoUniverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<SoundEvent> HAND_SIGN_ONE =
            SOUNDS.register("hand_sign_one", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_one")));

    public static final RegistryObject<SoundEvent> HAND_SIGN_TWO =
            SOUNDS.register("hand_sign_two", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_two")));

    public static final RegistryObject<SoundEvent> HAND_SIGN_THREE =
            SOUNDS.register("hand_sign_three", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hand_sign_three")));
    public static final RegistryObject<SoundEvent> ABILITY_ACTIVATE =
            SOUNDS.register("ability_activate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "ability_activate")));

    public static final RegistryObject<SoundEvent> CHAKRA_JUMP =
            SOUNDS.register("chakra_jump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "chakra_jump")));

    public static final RegistryObject<SoundEvent> GREAT_FIREBALL =
            SOUNDS.register("great_fireball", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "great_fireball")));

    public static final RegistryObject<SoundEvent> SAGE_FIRE =
            SOUNDS.register("sage_fire", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "sage_fire")));

    public static final RegistryObject<SoundEvent> HIDING_IN_ASH =
            SOUNDS.register("hiding_in_ash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "hiding_in_ash")));

    public static final RegistryObject<SoundEvent> GREAT_FLAME =
            SOUNDS.register("great_flame", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "great_flame")));

    public static final RegistryObject<SoundEvent> GREAT_ANNIHILATION =
            SOUNDS.register("great_annihilation", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "great_annihilation")));

    public static final RegistryObject<SoundEvent> SHARINGAN_ACTIVATE =
            SOUNDS.register("sharingan_activate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "sharingan_activate")));

    public static final RegistryObject<SoundEvent> SHARINGAN_DEACTIVATE =
            SOUNDS.register("sharingan_deactivate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "sharingan_deactivate")));

    public static final RegistryObject<SoundEvent> RINNEGAN_ACTIVATE =
            SOUNDS.register("rinnegan_activate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "rinnegan_activate")));

    public static final RegistryObject<SoundEvent> KUNAI_THROW =
            SOUNDS.register("kunai_throw", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "kunai_throw")));

    public static final RegistryObject<SoundEvent> KUNAI_HIT =
            SOUNDS.register("kunai_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NarutoUniverse.MOD_ID,
                    "kunai_hit")));
}
