package radon.naruto_universe.client.ability;

import net.minecraftforge.common.MinecraftForge;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.ability.event.AbilityTriggerEvent;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.client.NarutoKeyMapping;
import radon.naruto_universe.client.NarutoKeys;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.HandleHandSignC2SPacket;
import radon.naruto_universe.ability.Ability;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import radon.naruto_universe.network.packet.TriggerAbilityC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class ClientAbilityHandler {
    private static int ticksPassed;
    private static long currentCombo;
    private static final long MAX_COMBO_VALUE = 10 * 10; // Max 10 hand signs
    private static final int MAX_TICKS = 15;
    private static Ability currentAbility;
    private static NarutoKeyMapping currentKey;

    private static boolean isCurrentlyChargingAbility;

    private static final List<NarutoKeyMapping> ABILITY_KEYS = new ArrayList<>();

    public static void tick(LocalPlayer player) {
        if (currentKey != null) {
            ticksPassed++;
        }

        boolean possiblyChanneling = currentAbility != null && currentAbility.getActivationType() == Ability.ActivationType.CHANNELED;
        boolean isSpecial = currentKey != null && currentKey.getName().equals(NarutoKeys.KEY_ACTIVATE_SPECIAL.getName());

        if (possiblyChanneling) {
            assert currentKey != null;

            boolean lastKeyIsHeld = currentKey.isDown();

            if (lastKeyIsHeld && (isSpecial || currentKey.currentTickCount() >= MAX_TICKS)) {
                if (!isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    triggerAbility(currentAbility);
                }
                isCurrentlyChargingAbility = true;
            } else if (!lastKeyIsHeld) {
                currentKey.consumeReleaseDuration();

                if (isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    triggerAbility(currentAbility);
                    resetAbilityCasting();
                } else if (ticksPassed > MAX_TICKS) {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                    resetAbilityCasting();
                }
                isCurrentlyChargingAbility = false;
            }
        } else {
            if (isSpecial || ticksPassed > MAX_TICKS) {
                if (currentAbility != null) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    triggerAbility(currentAbility);
                } else {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                }
                resetAbilityCasting();
            }
        }

        for (NarutoKeyMapping key : ABILITY_KEYS) {
            key.update();
        }
    }

    public static void handleSpecialKey() {
        if (isCurrentlyChargingAbility || currentCombo > MAX_COMBO_VALUE) {
            return;
        }

        Ability ability = SpecialAbilityHandler.getSelected();

        if (ability != null) {
            ticksPassed = 0;
            currentKey = ABILITY_KEYS.get(ABILITY_KEYS.size() - 1);
            currentAbility = ability;
        }
    }

    public static void handleAbilityKey(int i) {
        if (isCurrentlyChargingAbility || currentCombo > MAX_COMBO_VALUE) {
            return;
        }

        currentCombo *= 10;
        currentCombo += i;
        ticksPassed = 0;

        int lastKey = (int) (currentCombo % 10);

        if (lastKey > 0 && lastKey < ABILITY_KEYS.size()) {
            currentKey = ABILITY_KEYS.get(lastKey - 1);
        }

        LocalPlayer player = Minecraft.getInstance().player;
        currentAbility = NarutoAbilities.getUnlockedAbility(player, currentCombo);

        assert player != null;

        // Reset the power reset timer on client too
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.setPowerResetTimer(0));
        PacketHandler.sendToServer(new HandleHandSignC2SPacket(i));
    }

    private static void resetAbilityCasting() {
        currentCombo = 0;
        ticksPassed = 0;
        currentAbility = null;
        currentKey = null;
        isCurrentlyChargingAbility = false;
    }

    public static void registerListener(KeyMapping key, Runnable onClick) {
        NarutoKeyMapping abilityKey = new NarutoKeyMapping(key.getName(), key.getKey().getValue(), key.getCategory());
        abilityKey.registerClickConsumer(onClick);
        ABILITY_KEYS.add(abilityKey);
    }

    public static void registerAbilityKey(RegisterKeyMappingsEvent event, KeyMapping key, Runnable onClick) {
        event.register(key);
        registerListener(key, onClick);
    }


    public static void triggerAbility(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        assert owner != null;

        Ability.Status status;

        if (!ability.isUnlocked(owner)) {
            return;
        } else if ((status = ability.checkTriggerable(owner)) != Ability.Status.SUCCESS) {
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                switch (status) {
                    case NO_CHAKRA -> owner.sendSystemMessage(Component.translatable("ability.fail.not_enough_chakra"));
                    case NO_POWER -> owner.sendSystemMessage(Component.translatable("ability.fail.not_enough_power"));
                    case COOLDOWN -> mc.gui.setOverlayMessage(Component.translatable("ability.fail.cooldown",
                            cap.getRemainingCooldown(ability) / 20), false);
                }
            });
            return;
        }

        MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));

        if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
            ability.runClient(owner);

            if (ability.shouldLog(owner)) {
                owner.sendSystemMessage(ability.getChatMessage());
            }
        } else if (ability.getActivationType() == Ability.ActivationType.CHANNELED) {
            NarutoAbilities.setChanneledAbility(owner, ability);
        } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
            NarutoAbilities.setToggledAbility(owner, ability);
        }
    }
}
