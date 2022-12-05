package dev.radon.naruto_universe.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.entity.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModEyesLayer<T extends LivingEntity, M extends PlayerModel<T>> extends EyesLayer<T, M> {
    private static final RenderType BACKGROUND = EntityRegistry.ModRenderType.eyesBackground(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/background.png"));
    private static final RenderType SHARINGAN_ONE = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_one.png"));
    private static final RenderType SHARINGAN_TWO = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_two.png"));
    private static final RenderType SHARINGAN_THREE = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_three.png"));
    private static final RenderType RINNEGAN = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/rinnegan.png"));

    public ModEyesLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    private void renderBackground(PoseStack pMatrixStack, MultiBufferSource pBuffer) {
        VertexConsumer consumer = pBuffer.getBuffer(BACKGROUND);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderEyes(PoseStack pMatrixStack, MultiBufferSource pBuffer, RenderType pRenderType) {
        VertexConsumer consumer = pBuffer.getBuffer(pRenderType);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        LocalPlayer player = Minecraft.getInstance().player;

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggledAbility(AbilityRegistry.SHARINGAN.get())) {
                int level = cap.getSharinganLevel();

                this.renderBackground(pMatrixStack, pBuffer);

                switch (level) {
                    case 1:
                        this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_ONE);
                        break;
                    case 2:
                        this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_TWO);
                        break;
                    case 3:
                        this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_THREE);
                        break;
                }
            }

            if (cap.hasToggledAbility(AbilityRegistry.RINNEGAN.get())) {
                this.renderEyes(pMatrixStack, pBuffer, RINNEGAN);
            }
        });
    }

    @Override
    public RenderType renderType() {
        return null;
    }
}
