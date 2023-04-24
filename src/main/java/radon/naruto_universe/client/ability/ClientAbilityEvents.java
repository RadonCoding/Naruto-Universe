package radon.naruto_universe.client.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.effect.NarutoEffects;
import radon.naruto_universe.entity.SusanooEntity;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.ChangeSusanooStageC2SPacket;
import radon.naruto_universe.network.packet.SusanooControlC2SPacket;
import radon.naruto_universe.util.HelperMethods;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientAbilityEvents {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (mc.player.hasEffect(NarutoEffects.STUN.get())) {
            mc.player.input.forwardImpulse = 0.0F;
            mc.player.input.leftImpulse = 0.0F;
            mc.player.input.jumping = false;
            mc.player.input.shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            if (mc.player.hasEffect(NarutoEffects.STUN.get())) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
            else if (mc.player.getVehicle() instanceof SusanooEntity susanoo) {
                if (event.isAttack()) {
                    PacketHandler.sendToServer(new SusanooControlC2SPacket(SusanooControlC2SPacket.LEFT));
                    susanoo.onLeftClick();

                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
                else if (event.isUseItem() && mc.player.getMainHandItem().isEmpty() && mc.player.getOffhandItem().isEmpty()) {
                    PacketHandler.sendToServer(new SusanooControlC2SPacket(SusanooControlC2SPacket.RIGHT));
                    susanoo.onRightClick();

                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (!mc.options.keyShift.isDown()) {
            if (mc.player.getVehicle() instanceof SusanooEntity susanoo) {
                if (event.getAction() == InputConstants.PRESS) {
                    if (event.getKey() == InputConstants.KEY_UP) {
                        PacketHandler.sendToServer(new ChangeSusanooStageC2SPacket(ChangeSusanooStageC2SPacket.UP));
                        susanoo.incrementStage();
                    } else if (event.getKey() == InputConstants.KEY_DOWN) {
                        PacketHandler.sendToServer(new ChangeSusanooStageC2SPacket(ChangeSusanooStageC2SPacket.DOWN));
                        susanoo.decrementStage();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                ItemStack stack = mc.player.getUseItem();
                Item item = stack.getItem();

                if (item.getUseAnimation(stack) == UseAnim.BOW || item.getUseAnimation(stack) == UseAnim.CROSSBOW ||
                        item.getUseAnimation(stack) == UseAnim.SPEAR) {
                    EntityHitResult hit = HelperMethods.getLivingEntityLookAt(mc.player, 100.0F, 5.0F);

                    if (hit != null) {
                        Entity target = hit.getEntity();
                        mc.player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    }
                }
            }
        });
    }
}
