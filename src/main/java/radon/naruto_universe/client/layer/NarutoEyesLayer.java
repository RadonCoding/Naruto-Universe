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

import java.util.concurrent.atomic.AtomicReference;

public class NarutoEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M> {
    private static final RenderType BACKGROUND = RenderType.entityTranslucent(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/background.png"));
    private static final ResourceLocation SHARINGAN_ONE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_one.png");
    private static final ResourceLocation SHARINGAN_TWO = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_two.png");
    private static final ResourceLocation SHARINGAN_THREE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_three.png");
    private static final ResourceLocation ITACHI_MANGEKYO = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/itachi_mangekyo.png");
    private static final ResourceLocation OBITO_MANGEKYO = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/obito_mangekyo.png");
    private static final ResourceLocation MADARA_MANGEKYO = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/madara_mangekyo.png");
    private static final ResourceLocation RINNEGAN = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/rinnegan.png");

    //private static RenderType SIX_TOMOE_RINNEGAN = EntityRegistry.ModRenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/six_tomoe_rinnegan.png");

    public NarutoEyesLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    private void renderBackground(PoseStack pMatrixStack, MultiBufferSource pBuffer) {
        VertexConsumer consumer = pBuffer.getBuffer(BACKGROUND);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderEyes(PoseStack pMatrixStack, MultiBufferSource pBuffer, RenderType pRenderType) {
        VertexConsumer consumer = pBuffer.getBuffer(pRenderType);
        this.getParentModel().renderToBuffer(pMatrixStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static ResourceLocation getTexture(ToggledEyes eyes, boolean remote) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;
        
        AtomicReference<ResourceLocation> result = new AtomicReference<>();

        mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (remote ? eyes.is(NarutoAbilities.RINNEGAN.get()) : cap.hasToggledAbility(NarutoAbilities.RINNEGAN.get())) {
                result.set(RINNEGAN);
            } else if (remote ? eyes.is(NarutoAbilities.MANGEKYO.get()) : cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())) {
                switch (remote ? eyes.getMangekyoType() : cap.getMangekyoType()) {
                    case ITACHI -> result.set(ITACHI_MANGEKYO);
                    case OBITO -> result.set(OBITO_MANGEKYO);
                    case MADARA -> result.set(MADARA_MANGEKYO);
                }
            } else if (remote ? eyes.is(NarutoAbilities.SHARINGAN.get()) : cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                switch (remote ? eyes.getSharinganLevel() : cap.getSharinganLevel()) {
                    case 1 -> result.set(SHARINGAN_ONE);
                    case 2 -> result.set(SHARINGAN_TWO);
                    default -> result.set(SHARINGAN_THREE);
                }
            }
        });

        return result.get();
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        if (pLivingEntity.isInvisibleTo(mc.player)) return;

        mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            ToggledEyes eyes = cap.getToggledEyes(pLivingEntity.getUUID());

            boolean remote = pLivingEntity != mc.player;

            if (remote && eyes == null) {
                PacketHandler.sendToServer(new EyeStatusRequestC2SPacket(pLivingEntity.getUUID()));
                return;
            }

            ResourceLocation texture = getTexture(eyes, remote);

            if (texture != null) {
                RenderType type = NarutoRenderTypes.eyes(texture);

                if (remote ? eyes.is(NarutoAbilities.RINNEGAN.get()) : cap.hasToggledAbility(NarutoAbilities.RINNEGAN.get())) {
                    this.renderEyes(pMatrixStack, pBuffer, type);
                } else if (remote ? eyes.is(NarutoAbilities.MANGEKYO.get()) : cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())) {
                    this.renderBackground(pMatrixStack, pBuffer);
                    this.renderEyes(pMatrixStack, pBuffer, type);
                } else if (remote ? eyes.is(NarutoAbilities.SHARINGAN.get()) : cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                    this.renderBackground(pMatrixStack, pBuffer);
                    this.renderEyes(pMatrixStack, pBuffer, type);
                }
            }
        });
    }

    @Override
    public @NotNull RenderType renderType() {
        return null;
    }
}
