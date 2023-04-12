package radon.naruto_universe.client.ability;

import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.NarutoKeyMapping;
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

    private static boolean isCurrentlyChargingAbility;

    private static final List<NarutoKeyMapping> MOD_KEYS = new ArrayList<>();

    public static void tick(LocalPlayer player) {
        if (currentCombo != 0) {
            ticksPassed++;
        }

        int lastKey = (int) (currentCombo % 10);

        boolean lastKeyIsHeld = false;

        NarutoKeyMapping currentKey = null;

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
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    ClientAbilityHandler.triggerAbility(currentAbility);
                }
                isCurrentlyChargingAbility = true;
            } else if (!lastKeyIsHeld) {
                assert currentKey != null;
                currentKey.consumeReleaseDuration();

                if (isCurrentlyChargingAbility) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    ClientAbilityHandler.triggerAbility(currentAbility);
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
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(currentAbility.getId()));
                    ClientAbilityHandler.triggerAbility(currentAbility);
                }
                else {
                    player.sendSystemMessage(Component.translatable("ability.fail.not_found"));
                }
                resetAbilityCasting();
            }
        }

        for (NarutoKeyMapping key : MOD_KEYS) {
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
        isCurrentlyChargingAbility = false;
    }
    
    public static void registerListener(KeyMapping key, Runnable onClick) {
        NarutoKeyMapping modKey = new NarutoKeyMapping(key.getName(), key.getKey().getValue(), key.getCategory());
        modKey.registerClickConsumer(onClick);
        MOD_KEYS.add(modKey);
    }

    public static void registerKeyMapping(RegisterKeyMappingsEvent event, KeyMapping key, Runnable onClick) {
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
        } else if ((status = ability.checkChakra(owner)) != Ability.Status.SUCCESS) {
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
