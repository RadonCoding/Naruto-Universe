package radon.naruto_universe.client.layer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.client.model.SusanooSkeletalModel;
import radon.naruto_universe.entity.SusanooEntity;

public class SusanooSkeletalEyesLayer<T extends SusanooEntity, M extends SusanooSkeletalModel<T>> extends EyesLayer<T, M> {
    private static final RenderType SUSANOO_SKELETAL_EYES = RenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID,
            "textures/entity/susanoo_skeletal_eyes.png"));

    public SusanooSkeletalEyesLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public @NotNull RenderType renderType() {
        return SUSANOO_SKELETAL_EYES;
    }
}
