package radon.naruto_universe.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.jutsu.fire.*;
import radon.naruto_universe.ability.jutsu.lightning.Lariat;
import radon.naruto_universe.ability.special.Amaterasu;
import radon.naruto_universe.ability.special.Genjutsu;
import radon.naruto_universe.ability.special.Susanoo;
import radon.naruto_universe.ability.utility.*;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.KeyRegistry;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class NarutoAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(NarutoUniverse.MOD_ID, "ability"), NarutoUniverse.MOD_ID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<PowerCharge> POWER_CHARGE =
            ABILITIES.register("power_charge", PowerCharge::new);
    public static RegistryObject<ChakraControl> CHAKRA_CONTROL =
            ABILITIES.register("chakra_control", ChakraControl::new);
    public static RegistryObject<ChakraJump> CHAKRA_JUMP =
            ABILITIES.register("chakra_jump", ChakraJump::new);
    public static RegistryObject<GreatFireball> GREAT_FIREBALL =
            ABILITIES.register("great_fireball", GreatFireball::new);
    public static RegistryObject<PhoenixSageFire> PHOENIX_SAGE_FIRE =
            ABILITIES.register("phoenix_sage_fire", PhoenixSageFire::new);
    public static RegistryObject<HidingInAsh> HIDING_IN_ASH =
            ABILITIES.register("hiding_in_ash", HidingInAsh::new);
    public static RegistryObject<GreatFlame> GREAT_FLAME =
            ABILITIES.register("great_flame", GreatFlame::new);
    public static RegistryObject<GreatAnnihilation> GREAT_ANNIHILATION =
            ABILITIES.register("great_annihilation", GreatAnnihilation::new);
    public static RegistryObject<Ability> SHARINGAN =
            ABILITIES.register("sharingan", Sharingan::new);
    public static RegistryObject<Ability> MANGEKYO =
            ABILITIES.register("mangekyo", Mangekyo::new);
    public static RegistryObject<Ability> RINNEGAN =
            ABILITIES.register("rinnegan", Rinnegan::new);
    public static RegistryObject<Ability> GENJUTSU =
            ABILITIES.register("genjutsu", Genjutsu::new);
    public static RegistryObject<Ability> AMATERASU =
            ABILITIES.register("amaterasu", Amaterasu::new);
    public static RegistryObject<Ability> SUSANOO =
            ABILITIES.register("susanoo", Susanoo::new);
    public static RegistryObject<Ability> LARIAT =
            ABILITIES.register("lariat", Lariat::new);

    private static final HashMap<Long, ResourceLocation> COMBO_MAP = new HashMap<>();

    public static class ComboGenerator implements Iterator<Long> {
        private final List<Long> elements;
        private final int[] currentIndices;

        public ComboGenerator(List<Long> elements) {
            this.elements = elements;
            this.currentIndices = new int[elements.size()];
            Arrays.fill(this.currentIndices, -1);
            next();
        }

        @Override
        public boolean hasNext() {
            for (int currentIndex : this.currentIndices) {
                if (currentIndex < this.elements.size() - 1) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Long next() {
            long combo = 0;

            for (int i = this.currentIndices.length - 1; i >= 0; i--) {
                if (this.currentIndices[i] >= 0) {
                    combo *= 10;
                    combo += this.elements.get(this.currentIndices[i]);
                }
            }

            for (int i = 0; i < this.currentIndices.length; i++) {
                if (this.currentIndices[i] < this.elements.size() - 1) {
                    this.currentIndices[i]++;
                    break;
                } else {
                    this.currentIndices[i] = 0;
                }
            }
            return combo;
        }
    }


    public static void registerCombos() {
        List<Long> keys = Arrays.asList(1L, 2L, 3L);
        ComboGenerator gen = new ComboGenerator(keys);

        for (RegistryObject<Ability> entry : ABILITIES.getEntries()) {
            Ability ability = entry.get();

            if (!ability.hasCombo()) {
                continue;
            }

            assert entry.getKey() != null;

            long nxt = gen.next();
            COMBO_MAP.put(nxt, entry.getKey().location());
        }
    }

    public static long getCombo(Ability ability) {
        for (Map.Entry<Long, ResourceLocation> entry : COMBO_MAP.entrySet()) {
            if (entry.getValue() == ability.getId()) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public static Ability getAbility(long combo) {
        ResourceLocation key = COMBO_MAP.get(combo);

        if (key != null) {
            return ABILITY_REGISTRY.get().getValue(key);
        }
        return null;
    }

    public static Ability getUnlockedAbility(Player player, long combo) {
        Ability ability = getAbility(combo);

        if (ability != null && ability.isUnlocked(player)) {
            return ability;
        }
        return null;
    }


    public static boolean checkRequirements(Player player, Ability ability) {
        AtomicBoolean result = new AtomicBoolean(true);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getRank().ordinal() >= ability.getRank().ordinal()) {
                List<NinjaTrait> requirements = ability.getRequirements();

                for (NinjaTrait requirement : requirements) {
                    if (!cap.hasTrait(requirement)) {
                        result.set(false);
                    }
                }
            }
            else {
                result.set(false);
            }
        });

        return result.get();
    }

    public static void setChanneledAbility(LivingEntity owner, Ability ability) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (ability instanceof Ability.IChanneled) {
                if (cap.getChanneledAbility() != ability) {
                    cap.setChanneledAbility(owner, ability);
                } else {
                    cap.stopChanneledAbility(owner);
                }
            }
        });
    }

    public static void setToggledAbility(LivingEntity owner, Ability ability) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (ability instanceof Ability.IToggled) {
                if (!cap.hasToggledAbility(ability)) {
                    cap.enableToggledAbility(owner, ability);
                } else {
                    cap.disableToggledAbility(owner, ability);
                }

                if (ability.isDojutsu()) {
                    cap.clearToggledDojutsus(owner, ability);
                }
            }
        });
    }

    private static void collectDigits(long num, List<Integer> digits) {
        if (num / 10 > 0) {
            collectDigits(num / 10, digits);
        }
        digits.add((int)(num % 10));
    }

    public static String getStringFromCombo(long combo) {
        StringBuilder result = new StringBuilder();

        List<Integer> digits = new ArrayList<>();

        collectDigits(combo, digits);

        for (int digit : digits) {
            switch (digit) {
                case 1 -> result.append((char) KeyRegistry.KEY_HAND_SIGN_ONE.getKey().getValue());
                case 2 -> result.append((char) KeyRegistry.KEY_HAND_SIGN_TWO.getKey().getValue());
                case 3 -> result.append((char) KeyRegistry.KEY_HAND_SIGN_THREE.getKey().getValue());
            }
        }
        return result.toString();
    }

    public static ResourceLocation getKey(Ability ability) {
        return NarutoAbilities.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return NarutoAbilities.ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean isUnlocked(LivingEntity owner, Ability ability) {
        AtomicBoolean result = new AtomicBoolean(false);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasUnlockedAbility(ability)) {
                result.set(true);
            }
        });
        return result.get();
    }

    public static List<Ability> getDojutsuAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();

        for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            if (ability.get().isDojutsu() && ability.get().isUnlocked(player)) {
                abilities.add(ability.get());
            }
        }
        return abilities;
    }
}
