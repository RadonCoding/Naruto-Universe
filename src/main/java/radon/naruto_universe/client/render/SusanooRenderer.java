package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.naruto_universe.capability.MangekyoType;
import radon.naruto_universe.capability.SusanooStage;
import radon.naruto_universe.client.model.SusanooRibcageModel;
import radon.naruto_universe.client.model.SusanooSkeletalModel;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.HashMap;
import java.util.Map;

public class SusanooRenderer extends EntityRenderer<SusanooEntity> {
    private final Map<SusanooStage, SusanooStageRenderer<?>> renderers = new HashMap<>();

    public SusanooRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderers.put(SusanooStage.RIBCAGE, new SusanooRibcageRenderer(pContext, new SusanooRibcageModel<>(pContext
                .bakeLayer(SusanooRibcageModel.LAYER_LOCATION)), 1.0F));
        this.renderers.put(SusanooStage.SKELETAL, new SusanooSkeletalRenderer(pContext, new SusanooSkeletalModel<>(pContext
                .bakeLayer(SusanooSkeletalModel.LAYER_LOCATION)), 1.0F));
    }

    @Override
    public void render(@NotNull SusanooEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        MangekyoType type = pEntity.getVariant();
        Vector3f color = type.getSusanooColor();

        this.renderers.get(pEntity.getStage()).renderSusanoo(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight, color.x(), color.y(), color.z());
    }

    @Override
    protected int getBlockLightLevel(@NotNull SusanooEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SusanooEntity pEntity) {
        return null;
    }
}
