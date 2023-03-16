package radon.naruto_universe.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.InteractionHand;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.event.PlayerModelEvent;
import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.client.layer.ModEyesLayer;
import radon.naruto_universe.client.gui.overlay.ChakraBarOverlay;
import radon.naruto_universe.client.model.ThrownKunaiModel;
import radon.naruto_universe.client.particle.ParticleRegistry;
import radon.naruto_universe.client.render.FireballRenderer;
import radon.naruto_universe.client.render.ThrownKunaiRenderer;
import radon.naruto_universe.entity.EntityRegistry;
import radon.naruto_universe.item.ItemRegistry;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.HandleComboC2SPacket;
import radon.naruto_universe.client.particle.VaporParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ItemProperties.register(ItemRegistry.KUNAI.get(), new ResourceLocation("throwing"),
                (pStack,  pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F);
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_ONE, () -> AbilityHandler.handleAbilityKey(1));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_TWO, () -> AbilityHandler.handleAbilityKey(2));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_THREE, () -> AbilityHandler.handleAbilityKey(3));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.OPEN_ABILITY_SCREEN, () ->
                    Minecraft.getInstance().setScreen(new NinjaScreen()));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_CHAKRA_JUMP, () ->
                PacketHandler.sendToServer(new HandleComboC2SPacket(-1)));
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "chakra_bar",  ChakraBarOverlay.HUD_CHAKRA_BAR);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
            event.register(ParticleRegistry.VAPOR.get(), VaporParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.GREAT_FIREBALL.get(), FireballRenderer::new);
            event.registerEntityRenderer(EntityRegistry.THROWN_KUNAI.get(), ThrownKunaiRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ThrownKunaiModel.LAYER_LOCATION, ThrownKunaiModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onAddLayers(final EntityRenderersEvent.AddLayers event) {
            event.getSkins().forEach((skin) -> {
                LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin(skin);
                renderer.addLayer(new ModEyesLayer(renderer));
            });
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (event.phase != TickEvent.Phase.END || player == null) return;

            AbilityHandler.tick(player);
            DoubleJumpHandler.tick(player);
        }

        @SubscribeEvent
        public static void onSetupPlayerAngles(PlayerModelEvent.SetupAngles.Post event) {
            LocalPlayer player = Minecraft.getInstance().player;

            PlayerModel model = event.getModelPlayer();

            if (player.isSprinting() && !player.isSwimming() && !player.getAbilities().flying) {
                model.body.xRot = 0.5F;

                boolean rotateLeftArm = true, rotateRightArm = true;

                if (player.isUsingItem()) {
                    rotateRightArm = player.getUsedItemHand() != InteractionHand.MAIN_HAND;
                    rotateLeftArm = player.getUsedItemHand() != InteractionHand.OFF_HAND;
                } else if (player.swinging) {
                    rotateRightArm = player.swingingArm != InteractionHand.MAIN_HAND;
                    rotateLeftArm = player.swingingArm != InteractionHand.OFF_HAND;
                }

                if (rotateRightArm) {
                    model.rightArm.xRot = 1.6F;
                }
                if (rotateLeftArm) {
                    model.leftArm.xRot = 1.6F;
                }

                model.rightLeg.z = 4.0F;
                model.leftLeg.z = 4.0F;
                model.rightLeg.y = 12.2F;
                model.leftLeg.y = 12.2F;
                model.head.y = 4.2F;
                model.body.y = 3.2F;
                model.rightArm.y = 5.2F;
                model.leftArm.y = 5.2F;
            }

            if (player.isUsingItem() && player.getMainHandItem().is(ItemRegistry.KUNAI.get())) {
                // TODO: Animation for throwing kunai :O
            }

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isChannelingAbility(AbilityRegistry.POWER_CHARGE.get())) {
                    model.leftArm.xRot = -1.0F;
                    model.leftArm.yRot = 0.6F;

                    model.rightArm.xRot = -1.0F;
                    model.rightArm.yRot = -0.6F;
                }
            });
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (event.getAction() == InputConstants.PRESS && event.getKey() == mc.options.keyJump.getKey().getValue()) {
                DoubleJumpHandler.run(mc.player);
            }
        }
    }
}
