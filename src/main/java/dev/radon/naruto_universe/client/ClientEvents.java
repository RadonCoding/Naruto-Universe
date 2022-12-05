package dev.radon.naruto_universe.client;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.client.gui.NinjaScreen;
import dev.radon.naruto_universe.client.layer.ModEyesLayer;
import dev.radon.naruto_universe.client.overlay.ChakraBarOverlay;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import dev.radon.naruto_universe.client.particle.VaporParticle;
import dev.radon.naruto_universe.client.render.GreatFireballRenderer;
import dev.radon.naruto_universe.entity.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_ONE, () -> AbilityHandler.handleAbilityKey(1));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_TWO, () -> AbilityHandler.handleAbilityKey(2));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_THREE, () -> AbilityHandler.handleAbilityKey(3));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.OPEN_ABILITY_SCREEN, () ->
                    Minecraft.getInstance().setScreen(new NinjaScreen()));
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
            event.registerEntityRenderer(EntityRegistry.GREAT_FIREBALL.get(), GreatFireballRenderer::new);
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
            AbilityHandler.tick();
        }
    }
}
