package radon.naruto_universe.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.client.ability.ClientAbilityHandler;
import radon.naruto_universe.client.ability.DoubleJumpHandler;
import radon.naruto_universe.client.ability.SpecialAbilityHandler;
import radon.naruto_universe.client.animation.AnimationHandler;
import radon.naruto_universe.client.event.PlayerModelEvent;
import radon.naruto_universe.client.genjutsu.ClientGenjutsuHandler;
import radon.naruto_universe.client.gui.DojutsuScreen;
import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.client.gui.overlay.ChakraBarOverlay;
import radon.naruto_universe.client.layer.NarutoEyesLayer;
import radon.naruto_universe.client.particle.FireParticle;
import radon.naruto_universe.client.particle.NarutoParticles;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.client.render.*;
import radon.naruto_universe.entity.NarutoEntities;
import radon.naruto_universe.item.NarutoItems;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.TriggerAbilityC2SPacket;

public class ClientEventHandler {
    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onCreativeTabRegister(CreativeModeTabEvent.Register event) {
            event.registerCreativeModeTab(new ResourceLocation(NarutoUniverse.MOD_ID, "tab"), builder -> {
                builder.title(Component.translatable(String.format("item_group.%s.%s", NarutoUniverse.MOD_ID, "tab")))
                        .icon(() -> new ItemStack(NarutoItems.KUNAI.get()))
                        .displayItems((pEnabledFeatures, pOutput, pDisplayOperatorCreativeTab) -> {
                            pOutput.accept(new ItemStack(NarutoItems.KUNAI.get()));
                            pOutput.accept(new ItemStack(NarutoItems.AKATSUKI_CLOAK.get()));
                        });
            });
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            ClientAbilityHandler.registerAbilityKey(event, NarutoKeys.KEY_HAND_SIGN_ONE, () -> ClientAbilityHandler.handleAbilityKey(1));
            ClientAbilityHandler.registerAbilityKey(event, NarutoKeys.KEY_HAND_SIGN_TWO, () -> ClientAbilityHandler.handleAbilityKey(2));
            ClientAbilityHandler.registerAbilityKey(event, NarutoKeys.KEY_HAND_SIGN_THREE, () -> ClientAbilityHandler.handleAbilityKey(3));
            ClientAbilityHandler.registerAbilityKey(event, NarutoKeys.KEY_ACTIVATE_SPECIAL, ClientAbilityHandler::handleSpecialKey);
            NarutoKeys.register(event);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelowAll("chakra_bar",  ChakraBarOverlay.HUD_CHAKRA_BAR);
            event.registerAboveAll("special_ability", SpecialAbilityHandler.SPECIAL_ABILITY);
            event.registerAboveAll("genjutsu_overlay", ClientGenjutsuHandler.GENJUTSU_OVERLAY);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
            event.register(NarutoParticles.VAPOR.get(), VaporParticle.Provider::new);
            event.register(NarutoParticles.FIRE.get(), FireParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(NarutoEntities.FIREBALL.get(), FireballRenderer::new);
            event.registerEntityRenderer(NarutoEntities.THROWN_KUNAI.get(), ThrownKunaiRenderer::new);
            event.registerEntityRenderer(NarutoEntities.PARTICLE_SPAWNER.get(), EmptyRenderer::new);
            event.registerEntityRenderer(NarutoEntities.HIDING_IN_ASH.get(), EmptyRenderer::new);
            event.registerEntityRenderer(NarutoEntities.SUSANOO.get(), SusanooRenderer::new);
            event.registerEntityRenderer(NarutoEntities.METEORITE.get(), MeteoriteRenderer::new);
            event.registerEntityRenderer(NarutoEntities.CHIBAKU_TENSEI.get(), ChibakuTenseiRenderer::new);
            event.registerEntityRenderer(NarutoEntities.BLOCK_APPEARANCE.get(), BlockAppearanceRenderer::new);
        }

        @SubscribeEvent
        public static void registerPlayerLayers(EntityRenderersEvent.AddLayers event) {
            if (event.getSkin("default") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new NarutoEyesLayer<>(renderer));
            }
            if (event.getSkin("slim") instanceof PlayerRenderer renderer) {
                renderer.addLayer(new NarutoEyesLayer<>(renderer));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player == null) return;

            ClientAbilityHandler.tick(player);
            DoubleJumpHandler.tick(player);
        }

        @SubscribeEvent
        public static void onSetupPlayerAngles(PlayerModelEvent.SetupAngles.Post event) {
            AnimationHandler.animate(event.getEntity(), event.getModelPlayer());
        }

        @SubscribeEvent
        public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
            LivingEntity entity = event.getEntity();

            if (event.getRenderer().getModel() instanceof HumanoidModel<?> model) {
                AnimationHandler.animate(entity, model);
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getAction() == InputConstants.PRESS) {
                if (NarutoKeys.KEY_CHAKRA_JUMP.isDown()) {
                    Ability ability = NarutoAbilities.CHAKRA_JUMP.get();
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(ability.getId()));
                    ClientAbilityHandler.triggerAbility(ability);
                }

                if (NarutoKeys.OPEN_NINJA_SCREEN.isDown()) {
                   mc.setScreen(new NinjaScreen());
                }

                if (mc.options.keyJump.isDown()) {
                    DoubleJumpHandler.run(mc.player);
                }

                if (mc.options.keyShift.isDown()) {
                    if (event.getKey() == InputConstants.KEY_UP) {
                        SpecialAbilityHandler.scroll(1);
                    } else if (event.getKey() == InputConstants.KEY_DOWN) {
                        SpecialAbilityHandler.scroll(-1);
                    }
                }

                if (NarutoKeys.SHOW_DOJUTSU_MENU.isDown()) {
                    mc.setScreen(new DojutsuScreen());
                }
            } else if (event.getAction() == InputConstants.RELEASE) {
                if (event.getKey() == NarutoKeys.SHOW_DOJUTSU_MENU.getKey().getValue() && mc.screen instanceof DojutsuScreen) {
                    mc.screen.onClose();
                }
            }
        }
    }
}
