package dev.radon.naruto_universe.client;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.HandleComboC2SPacket;
import dev.radon.naruto_universe.network.packet.HandleHandSignC2SPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AbilityHandler {
    private static int ticksPassed;
    private static long currentCombo;
    private static final long MAX_COMBO_VALUE = 10000000000L;
    private static final int MAX_TICKS = 15;
    private static Ability currentAbility;

    private static boolean isCurrentlyChargingAbility;

    private static final List<AbilityKeyMapping> ABILITY_KEYS = Lists.newArrayList();

    public static void tick() {
        if (currentCombo != 0) {
            ticksPassed++;
        }

        int lastKey = (int) (currentCombo % 10);

        boolean lastKeyIsHeld = false;

        AbilityKeyMapping currentKey = null;

        if (lastKey > 0 && lastKey < ABILITY_KEYS.size()) {
            currentKey = ABILITY_KEYS.get(lastKey - 1);

            if (currentKey.isDown()) {
                lastKeyIsHeld = true;
            }
        }

        boolean possiblyChanneling = currentAbility != null && currentAbility.activationType() == Ability.ActivationType.CHANNELED;

        LocalPlayer player = Minecraft.getInstance().player;

        if (possiblyChanneling) {
            if (lastKeyIsHeld && currentKey.currentTickCount() >= MAX_TICKS) {
                if (!isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new HandleComboC2SPacket(currentCombo));
                }
                isCurrentlyChargingAbility = true;
            } else if (!lastKeyIsHeld) {
                currentKey.consumeReleaseDuration();

                if (isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new HandleComboC2SPacket(currentCombo));
                    resetAbilityCasting();
                }
                else if (ticksPassed > MAX_TICKS) {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                    resetAbilityCasting();
                }
                isCurrentlyChargingAbility = false;
            }
        } else {
            if (ticksPassed > MAX_TICKS) {
                if (currentAbility != null) {
                    PacketHandler.sendToServer(new HandleComboC2SPacket(currentCombo));
                }
                else {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                }
                resetAbilityCasting();
            }
        }

        for (var key : ABILITY_KEYS) {
            key.update();
        }
    }

    public static void handleAbilityKey(int i) {
        if (isCurrentlyChargingAbility || currentCombo > MAX_COMBO_VALUE) {
            return;
        }

        currentCombo *= 10;
        currentCombo += i;
        ticksPassed = 0;

        currentAbility = AbilityRegistry.getAbility(currentCombo);

        PacketHandler.sendToServer(new HandleHandSignC2SPacket());
    }

    private static void resetAbilityCasting() {
        currentCombo = 0;
        ticksPassed = 0;
        currentAbility = null;
        isCurrentlyChargingAbility = false;
    }

    public static void registerKeyMapping(RegisterKeyMappingsEvent event, KeyMapping key, Runnable onClick) {
        event.register(key);

        AbilityKeyMapping abilityKey = new AbilityKeyMapping(key.getName(), key.getKey().getValue(), key.getCategory());
        ABILITY_KEYS.add(abilityKey);

        abilityKey.registerClickConsumer(onClick);
    }
}
