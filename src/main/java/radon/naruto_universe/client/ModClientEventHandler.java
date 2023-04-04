package radon.naruto_universe.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.event.PlayerModelEvent;
import radon.naruto_universe.client.gui.DojutsuScreen;
import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.client.layer.ModEyesLayer;
import radon.naruto_universe.client.gui.overlay.ChakraBarOverlay;
import radon.naruto_universe.client.model.ThrownKunaiModel;
import radon.naruto_universe.client.particle.FlameParticle;
import radon.naruto_universe.client.particle.NarutoParticles;
import radon.naruto_universe.client.render.FireballRenderer;
import radon.naruto_universe.client.render.EmptyRenderer;
import radon.naruto_universe.client.render.ThrownKunaiRenderer;
import radon.naruto_universe.entity.NarutoEntities;
import radon.naruto_universe.item.NarutoItems;
import radon.naruto_universe.network.PacketHandler;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import radon.naruto_universe.network.packet.TriggerAbilityPacket;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEventHandler {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ItemProperties.register(NarutoItems.KUNAI.get(), new ResourceLocation("throwing"),
                (pStack,  pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F);
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onCreativeTabRegister(final CreativeModeTabEvent.Register event) {
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
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_ONE, () -> AbilityHandler.handleAbilityKey(1));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_TWO, () -> AbilityHandler.handleAbilityKey(2));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_HAND_SIGN_THREE, () -> AbilityHandler.handleAbilityKey(3));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_CHAKRA_JUMP, () ->
                PacketHandler.sendToServer(new TriggerAbilityPacket(NarutoAbilities.CHAKRA_JUMP.getId())));
            AbilityHandler.registerKeyMapping(event, KeyRegistry.KEY_ACTIVATE_SPECIAL, SpecialAbilityHandler::triggerSelectedAbility);
            KeyRegistry.register(event);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("chakra_bar",  ChakraBarOverlay.HUD_CHAKRA_BAR);
            event.registerAboveAll("special_ability", SpecialAbilityHandler.SPECIAL_ABILITY);
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
            event.register(NarutoParticles.VAPOR.get(), VaporParticle.Provider::new);
            event.register(NarutoParticles.FLAME.get(), FlameParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(NarutoEntities.FIREBALL_JUTSU.get(), FireballRenderer::new);
            event.registerEntityRenderer(NarutoEntities.THROWN_KUNAI.get(), ThrownKunaiRenderer::new);
            event.registerEntityRenderer(NarutoEntities.PARTICLE_SPAWNER.get(), EmptyRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ThrownKunaiModel.LAYER_LOCATION, ThrownKunaiModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onAddLayers(final EntityRenderersEvent.AddLayers event) {
            event.getSkins().forEach((skin) -> {
                LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin(skin);
                assert renderer != null;
                renderer.addLayer(new ModEyesLayer<>(renderer));
            });
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (event.phase != TickEvent.Phase.START || player == null) return;

            AbilityHandler.tick(player);
            DoubleJumpHandler.tick(player);
        }

        @SubscribeEvent
        public static void onSetupPlayerAngles(PlayerModelEvent.SetupAngles.Post event) {
            LocalPlayer player = Minecraft.getInstance().player;

            PlayerModel<Player> model = event.getModelPlayer();

            assert player != null;

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

                model.head.y = 4.2F;
                model.body.y = 3.2F;
                model.rightArm.y = 5.2F;
                model.leftArm.y = 5.2F;
                model.rightLeg.y = 12.2F;
                model.leftLeg.y = 12.2F;
                model.rightLeg.z = 4.0F;
                model.leftLeg.z = 4.0F;
            }

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isChannelingAbility(NarutoAbilities.POWER_CHARGE.get())) {
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
                assert mc.player != null;
                DoubleJumpHandler.run(mc.player);
            }

            if (KeyRegistry.OPEN_NINJA_SCREEN.isDown()) {
                mc.setScreen(new NinjaScreen());
            }

            if (event.getAction() == InputConstants.PRESS && KeyRegistry.SHOW_DOJUTSU_MENU.isDown()) {
                mc.setScreen(new DojutsuScreen());
            } else if (event.getKey() == KeyRegistry.SHOW_DOJUTSU_MENU.getKey().getValue() && event.getAction() == InputConstants.RELEASE && mc.screen instanceof DojutsuScreen) {
                mc.screen.onClose();
            }
        }
    }
}
