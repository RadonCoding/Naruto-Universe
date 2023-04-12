package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.client.layer.SusanooSkeletalEyesLayer;
import radon.naruto_universe.client.model.SusanooSkeletalModel;
import radon.naruto_universe.entity.SusanooEntity;

public class SusanooSkeletalRenderer extends SusanooStageRenderer<SusanooSkeletalModel<SusanooEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/susanoo_skeletal.png");
    private static final RenderType RENDER_TYPE = NarutoRenderTypes.susanoo(TEXTURE);

    public SusanooSkeletalRenderer(EntityRendererProvider.Context pContext, SusanooSkeletalModel<SusanooEntity> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);

        this.addLayer(new SusanooSkeletalEyesLayer<>(this));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(@NotNull SusanooEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RENDER_TYPE;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SusanooEntity pEntity) {
        return TEXTURE;
    }
}
