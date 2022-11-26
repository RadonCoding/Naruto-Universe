package dev.radon.naruto_universe.client;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.client.overlay.ChakraBarOverlay;
import dev.radon.naruto_universe.client.render.GreatFireballRenderer;
import dev.radon.naruto_universe.client.screen.AbilityScreen;
import dev.radon.naruto_universe.entity.EntityRegistry;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_ONE, () -> AbilityHandler.handleAbilityKey(1));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_TWO, () -> AbilityHandler.handleAbilityKey(2));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_THREE, () -> AbilityHandler.handleAbilityKey(3));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.OPEN_ABILITY_SCREEN, () -> Minecraft.getInstance().setScreen(new AbilityScreen()));
        }

        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        }

        @SubscribeEvent
        public static void onRegisterLayerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.GREAT_FIREBALL.get(), GreatFireballRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "chakra_bar",  ChakraBarOverlay.HUD_CHAKRA_BAR);
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            AbilityHandler.tick();
        }
    }
}
