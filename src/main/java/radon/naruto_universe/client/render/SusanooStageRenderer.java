package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.entity.SusanooEntity;

public abstract class SusanooStageRenderer<M extends EntityModel<SusanooEntity>> extends LivingEntityRenderer<SusanooEntity, M> {
    public SusanooStageRenderer(EntityRendererProvider.Context pContext, M pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    public void renderSusanoo(@NotNull SusanooEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer,
                              int pPackedLight, float r, float g, float b) {
        pMatrixStack.pushPose();

        LivingEntity owner = pEntity.getOwner();

        if (owner != null) {
            pEntity.swinging = owner.swinging;
            pEntity.swingTime = owner.swingTime;
            pEntity.swingingArm = owner.swingingArm;
            pEntity.attackAnim = owner.attackAnim;
            pEntity.oAttackAnim = owner.oAttackAnim;

            owner.yBodyRot = pEntity.yBodyRot;
            owner.yBodyRotO = pEntity.yBodyRotO;

            owner.animationSpeed = pEntity.animationSpeed;
            owner.animationSpeedOld = pEntity.animationSpeedOld;
            owner.walkDist = pEntity.walkDist;
            owner.walkDistO = pEntity.walkDistO;
        }

        this.model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);

        float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float f2 = f1 - f;

        float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());

        float f7 = this.getBob(pEntity, pPartialTicks);
        this.setupRotations(pEntity, pMatrixStack, f7, f, pPartialTicks);
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pMatrixStack, pPartialTicks);
        pMatrixStack.translate(0.0F, -1.501F, 0.0F);
        float f8 = 0.0F;
        float f5 = 0.0F;

        if (!pEntity.isAlive()) {
            f8 = Mth.lerp(pPartialTicks, pEntity.animationSpeedOld, pEntity.animationSpeed);
            f5 = pEntity.animationPosition - pEntity.animationSpeed * (1.0F - pPartialTicks);

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.prepareMobModel(pEntity, f5, f8, pPartialTicks);
        this.model.setupAnim(pEntity, f5, f8, f7, f2, f6);

        RenderType type = this.getRenderType(pEntity, true, true, false);

        if (type != null) {
            VertexConsumer consumer = pBuffer.getBuffer(type);
            int i = getOverlayCoords(pEntity, this.getWhiteOverlayProgress(pEntity, pPartialTicks));
            this.model.renderToBuffer(pMatrixStack, consumer, pPackedLight, i, r, g ,b, 0.85F);
        }

        for (RenderLayer<SusanooEntity, M> renderlayer : this.layers) {
            renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, f5, f8, pPartialTicks, f7, f2, f6);
        }

        pMatrixStack.popPose();
    }

    @Override
    protected boolean shouldShowName(@NotNull SusanooEntity pEntity) {
        return false;
    }
}
