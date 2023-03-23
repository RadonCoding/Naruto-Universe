package radon.naruto_universe.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.entity.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ModEyesLayer<T extends LivingEntity, M extends PlayerModel<T>> extends EyesLayer<T, M> {
    private static final RenderType BACKGROUND = EntityRegistry.ModRenderType.eyesBackground(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/background.png"));
    private static final RenderType SHARINGAN_ONE = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_one.png"));
    private static final RenderType SHARINGAN_TWO = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_two.png"));
    private static final RenderType SHARINGAN_THREE = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_three.png"));
    private static final RenderType RINNEGAN = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/rinnegan.png"));
    private static final RenderType SIX_TOMOE_RINNEGAN = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/six_tomoe_rinnegan.png"));

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
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        assert player != null;

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggledAbility(AbilityRegistry.RINNEGAN.get())) {
                if (cap.hasToggledAbility(AbilityRegistry.SHARINGAN.get())) {
                    this.renderEyes(pMatrixStack, pBuffer, SIX_TOMOE_RINNEGAN);
                }
                else {
                    this.renderEyes(pMatrixStack, pBuffer, RINNEGAN);
                }
            }
            else if (cap.hasToggledAbility(AbilityRegistry.SHARINGAN.get())) {
                int level = cap.getSharinganLevel();

                this.renderBackground(pMatrixStack, pBuffer);

                switch (level) {
                    case 1 -> this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_ONE);
                    case 2 -> this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_TWO);
                    default -> this.renderEyes(pMatrixStack, pBuffer, SHARINGAN_THREE);
                }
            }
        });
    }

    @Override
    public @NotNull RenderType renderType() {
        return null;
    }
}
