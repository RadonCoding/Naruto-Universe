package radon.naruto_universe.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.ToggledEyes;
import radon.naruto_universe.client.NarutoRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.EyeStatusRequestC2SPacket;

public class ModEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M> {
    private static final RenderType BACKGROUND = RenderType.entityTranslucent(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/background.png"));
    private static final RenderType SHARINGAN_ONE = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_one.png"));
    private static final RenderType SHARINGAN_TWO = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_two.png"));
    private static final RenderType SHARINGAN_THREE = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_three.png"));
    private static final RenderType ITACHI_MANGEKYO = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/itachi_mangekyo.png"));
    private static final RenderType OBITO_MANGEKYO = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/obito_mangekyo.png"));
    private static final RenderType MADARA_MANGEKYO = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/madara_mangekyo.png"));
    private static final RenderType RINNEGAN = NarutoRenderTypes.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/rinnegan.png"));
    //private static RenderType SIX_TOMOE_RINNEGAN = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/six_tomoe_rinnegan.png"));

    public ModEyesLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    private void renderBackground(PoseStack pMatrixStack, MultiBufferSource pBuffer) {
        VertexConsumer consumer = pBuffer.getBuffer(BACKGROUND);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderEyes(PoseStack pMatrixStack, MultiBufferSource pBuffer, RenderType pRenderType) {
        VertexConsumer consumer = pBuffer.getBuffer(pRenderType);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            ToggledEyes eyes = cap.getToggledEyes(pLivingEntity.getUUID());

            boolean remote = pLivingEntity != mc.player;

            if (remote && eyes == null) {
                PacketHandler.sendToServer(new EyeStatusRequestC2SPacket(pLivingEntity.getUUID()));
                return;
            }

            if (remote ? eyes.is(NarutoAbilities.RINNEGAN.get()) : cap.hasToggledAbility(NarutoAbilities.RINNEGAN.get())) {
                this.renderEyes(pMatrixStack, pBuffer, RINNEGAN);
            }
            else if (remote ? eyes.is(NarutoAbilities.MANGEKYO.get()) : cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())) {
                this.renderBackground(pMatrixStack, pBuffer);

                switch (remote ? eyes.getMangekyoType() : cap.getMangekyoType()) {
                    case ITACHI -> this.renderEyes(pMatrixStack, pBuffer, ITACHI_MANGEKYO);
                    case OBITO -> this.renderEyes(pMatrixStack, pBuffer, OBITO_MANGEKYO);
                    case MADARA -> this.renderEyes(pMatrixStack, pBuffer, MADARA_MANGEKYO);
                }
            }
            else if (remote ? eyes.is(NarutoAbilities.SHARINGAN.get()) : cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                this.renderBackground(pMatrixStack, pBuffer);

                switch (remote ? eyes.getSharinganLevel() : cap.getSharinganLevel()) {
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
