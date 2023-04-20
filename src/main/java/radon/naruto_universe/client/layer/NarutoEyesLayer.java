package radon.naruto_universe.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.data.NarutoDataHandler;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.ToggledEyes;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.RequestNarutoDataC2SPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class NarutoEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M> {
    public static final int TEXTURE_SIZE = 1664;

    public static final RenderType BACKGROUND = RenderType.entityTranslucent(new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/background.png"));
    private static final ResourceLocation SHARINGAN_ONE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_one.png");
    private static final ResourceLocation SHARINGAN_TWO = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_two.png");
    private static final ResourceLocation SHARINGAN_THREE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/eyes/sharingan_three.png");
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

    public static boolean hasBackground(ToggledEyes eyes, boolean remote) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        AtomicBoolean result = new AtomicBoolean(false);

        mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (remote ? eyes.is(NarutoAbilities.MANGEKYO.get()) : cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())) {
                result.set(true);
            } else if (remote ? eyes.is(NarutoAbilities.SHARINGAN.get()) : cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                result.set(true);
            }
        });

        return result.get();
    }

    public static ResourceLocation getTexture(ToggledEyes eyes) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        ResourceLocation texture = null;

        if (eyes.is(NarutoAbilities.RINNEGAN.get())) {
            texture = RINNEGAN;
        } else if (eyes.is(NarutoAbilities.MANGEKYO.get())) {
            return new ResourceLocation(NarutoUniverse.MOD_ID, String.format("textures/eyes/%s.png", eyes.mangekyoType.getIconName()));
        } else if (eyes.is(NarutoAbilities.SHARINGAN.get())) {
            switch (eyes.sharinganLevel) {
                case 1 -> texture = SHARINGAN_ONE;
                case 2 -> texture = SHARINGAN_TWO;
                default -> texture = SHARINGAN_THREE;
            }
        }
        return texture;
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        if (pLivingEntity.isInvisibleTo(mc.player)) return;

        mc.player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> {
            boolean remote = pLivingEntity != mc.player;

            if (remote && !cap.isSynced(pLivingEntity.getUUID())) {
                PacketHandler.sendToServer(new RequestNarutoDataC2SPacket(pLivingEntity.getUUID()));
                return;
            }

            ToggledEyes eyes = remote ? cap.getRemoteEyes(pLivingEntity.getUUID()) : cap.getLocalEyes();

            if (eyes != null) {
                ResourceLocation texture = getTexture(eyes);

                if (texture != null) {
                    RenderType type = NarutoRenderTypes.eyes(texture);

                    if (hasBackground(eyes, remote)) {
                        this.renderBackground(pMatrixStack, pBuffer);
                    }
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
