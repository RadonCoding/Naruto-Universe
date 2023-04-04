package radon.naruto_universe.ability;

import com.google.common.collect.Maps;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.jutsu.fire.*;
import radon.naruto_universe.ability.special.Amaterasu;
import radon.naruto_universe.ability.special.Genjutsu;
import radon.naruto_universe.ability.special.Susanoo;
import radon.naruto_universe.ability.utility.*;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.KeyRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NarutoAbilities {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(NarutoUniverse.MOD_ID, "ability"), NarutoUniverse.MOD_ID);
    public static final Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<PowerCharge> POWER_CHARGE =
            ABILITIES.register("power_charge", PowerCharge::new);
    public static final RegistryObject<ChakraControl> CHAKRA_CONTROL =
            ABILITIES.register("chakra_control", ChakraControl::new);
    public static final RegistryObject<ChakraJump> CHAKRA_JUMP =
            ABILITIES.register("chakra_jump", ChakraJump::new);
    public static final RegistryObject<GreatFireball> GREAT_FIREBALL =
            ABILITIES.register("great_fireball", GreatFireball::new);
    public static final RegistryObject<PhoenixSageFire> PHOENIX_SAGE_FIRE =
            ABILITIES.register("phoenix_sage_fire", PhoenixSageFire::new);
    public static final RegistryObject<HidingInAsh> HIDING_IN_ASH =
            ABILITIES.register("hiding_in_ash", HidingInAsh::new);
    public static final RegistryObject<GreatFlame> GREAT_FLAME =
            ABILITIES.register("great_flame", GreatFlame::new);
    public static final RegistryObject<GreatAnnihilation> GREAT_ANNIHILATION =
            ABILITIES.register("great_annihilation", GreatAnnihilation::new);
    public static final RegistryObject<Ability> SHARINGAN =
            ABILITIES.register("sharingan", Sharingan::new);
    public static final RegistryObject<Ability> RINNEGAN =
            ABILITIES.register("rinnegan", Rinnegan::new);
    public static final RegistryObject<Ability> GENJUTSU =
            ABILITIES.register("genjutsu", Genjutsu::new);
    public static final RegistryObject<Ability> AMATERASU =
            ABILITIES.register("amaterasu", Amaterasu::new);
    public static final RegistryObject<Ability> SUSANOO =
            ABILITIES.register("susanoo", Susanoo::new);

    private static final HashMap<Long, ResourceLocation> COMBO_MAP = Maps.newLinkedHashMap();

    public static class ComboGenerator implements Iterator<Long> {
        private final List<Long> elements;
        private int[] currentIndices;

        public ComboGenerator(List<Long> elements) {
            this.elements = elements;
            this.currentIndices = new int[elements.size()];
            Arrays.fill(this.currentIndices, -1);
            next();
        }

        @Override
        public boolean hasNext() {
            for (int i = 0; i < this.currentIndices.length; i++) {
                if (this.currentIndices[i] < this.elements.size() - 1) {
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
            System.out.println(nxt);

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

    public static boolean isUnlocked(Player player, Ability ability) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasUnlockedAbility(ability)) {
                result.set(true);
            }
        });
        return result.get();
    }

    public static List<Ability> getDojutsuAbilities(Player player) {
        final List<Ability> abilities = Lists.newArrayList();

        for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            if (ability.get().isDojutsu() && ability.get().isUnlocked(player)) {
                abilities.add(ability.get());
            }
        }
        return abilities;
    }
}
