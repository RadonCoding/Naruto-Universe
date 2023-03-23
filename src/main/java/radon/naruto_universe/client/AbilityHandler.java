package radon.naruto_universe.client;

import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.HandleHandSignC2SPacket;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.apache.commons.compress.utils.Lists;
import radon.naruto_universe.network.packet.TriggerAbilityPacket;

import java.util.List;

public class AbilityHandler {
    private static int ticksPassed;
    private static long currentCombo;
    private static final long MAX_COMBO_VALUE = 10;
    private static final int MAX_TICKS = 15;
    private static Ability currentAbility;

    private static boolean isCurrentlyChargingAbility;

    private static final List<AbilityKeyMapping> MOD_KEYS = Lists.newArrayList();

    public static void tick(LocalPlayer player) {
        if (currentCombo != 0) {
            ticksPassed++;
        }

        int lastKey = (int) (currentCombo % 10);

        boolean lastKeyIsHeld = false;

        AbilityKeyMapping currentKey = null;

        if (lastKey > 0 && lastKey < MOD_KEYS.size()) {
            currentKey = MOD_KEYS.get(lastKey - 1);

            if (currentKey.isDown()) {
                lastKeyIsHeld = true;
            }
        }

        boolean possiblyChanneling = currentAbility != null && currentAbility.getActivationType() == Ability.ActivationType.CHANNELED;

        if (possiblyChanneling) {
            if (lastKeyIsHeld && currentKey.currentTickCount() >= MAX_TICKS) {
                if (!isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new TriggerAbilityPacket(currentAbility.getId()));
                }
                isCurrentlyChargingAbility = true;
            } else if (!lastKeyIsHeld) {
                currentKey.consumeReleaseDuration();

                if (isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new TriggerAbilityPacket(currentAbility.getId()));
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
                    PacketHandler.sendToServer(new TriggerAbilityPacket(currentAbility.getId()));
                }
                else {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                }
                resetAbilityCasting();
            }
        }

        for (AbilityKeyMapping key : MOD_KEYS) {
            key.update();
        }
    }

    public static void handleAbilityKey(int i) {
        if (isCurrentlyChargingAbility || (currentCombo % 10) > MAX_COMBO_VALUE) {
            return;
        }

        currentCombo *= 10;
        currentCombo += i;
        ticksPassed = 0;

        LocalPlayer player = Minecraft.getInstance().player;
        currentAbility = AbilityRegistry.getUnlockedAbility(player, currentCombo);

        PacketHandler.sendToServer(new HandleHandSignC2SPacket(i));
    }

    private static void resetAbilityCasting() {
        currentCombo = 0;
        ticksPassed = 0;
        currentAbility = null;
        isCurrentlyChargingAbility = false;
    }
    
    public static void registerListener(KeyMapping key, Runnable onClick) {
        AbilityKeyMapping modKey = new AbilityKeyMapping(key.getName(), key.getKey().getValue(), key.getCategory());
        modKey.registerClickConsumer(onClick);
        MOD_KEYS.add(modKey);
    }

    public static void registerKeyMapping(RegisterKeyMappingsEvent event, KeyMapping key, Runnable onClick) {
        event.register(key);
        registerListener(key, onClick);
    }
}
