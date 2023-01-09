package dev.radon.naruto_universe.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.client.event.PlayerModelEvent;
import dev.radon.naruto_universe.client.gui.NinjaScreen;
import dev.radon.naruto_universe.client.layer.ModEyesLayer;
import dev.radon.naruto_universe.client.gui.overlay.ChakraBarOverlay;
import dev.radon.naruto_universe.client.model.ThrownKunaiModel;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import dev.radon.naruto_universe.client.render.FireballRenderer;
import dev.radon.naruto_universe.client.render.ThrownKunaiRenderer;
import dev.radon.naruto_universe.entity.EntityRegistry;
import dev.radon.naruto_universe.item.ItemRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.HandleComboC2SPacket;
import dev.radon.naruto_universe.client.particle.VaporParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

public class ClientEvents {
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
            event.register(ParticleRegistry.CHAKRA.get(), VaporParticle.ChakraProvider::new);
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

            if (player.isSprinting() && !player.isSwimming() && !player.getAbilities().flying && !player.swinging) {
                model.leftArm.xRot = 1.6F;
                model.rightArm.xRot = 1.6F;
            }

            if (player.isUsingItem() && player.getMainHandItem().is(ItemRegistry.KUNAI.get())) {
                // TODO: Animation for throwing kunai :O
            }

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isChannelingAbility(AbilityRegistry.CHAKRA_CHARGE.get())) {
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
