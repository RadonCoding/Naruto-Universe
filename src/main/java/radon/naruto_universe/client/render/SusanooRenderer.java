package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.ninja.SusanooStage;
import radon.naruto_universe.client.model.SusanooHumanoidModel;
import radon.naruto_universe.client.model.SusanooRibcageModel;
import radon.naruto_universe.client.model.SusanooSkeletalModel;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.HashMap;
import java.util.Map;

public class SusanooRenderer extends EntityRenderer<SusanooEntity> {
    private final Map<SusanooStage, SusanooStageRenderer> renderers = new HashMap<>();

    public SusanooRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderers.put(SusanooStage.RIBCAGE, new SusanooStageRenderer(pContext, new SusanooRibcageModel()));
        this.renderers.put(SusanooStage.SKELETAL, new SusanooSkeletalRenderer(pContext, new SusanooSkeletalModel()));
        this.renderers.put(SusanooStage.HUMANOID, new SusanooHumanoidRenderer(pContext, new SusanooHumanoidModel()));
    }

    @Override
    public void render(@NotNull SusanooEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.renderers.get(pEntity.getStage()).render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SusanooEntity pEntity) {
        return this.renderers.get(pEntity.getStage()).getTextureLocation(pEntity);
    }
}
