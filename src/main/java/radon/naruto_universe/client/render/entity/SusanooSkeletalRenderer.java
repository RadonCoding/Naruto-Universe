package radon.naruto_universe.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.naruto_universe.client.layer.SusanooSkeletalEyesLayer;
import radon.naruto_universe.client.render.entity.SusanooStageRenderer;
import radon.naruto_universe.entity.SusanooEntity;
import software.bernie.geckolib.model.GeoModel;

public class SusanooSkeletalRenderer extends SusanooStageRenderer {
    public SusanooSkeletalRenderer(EntityRendererProvider.Context renderManager, GeoModel<SusanooEntity> model) {
        super(renderManager, model);

        this.addRenderLayer(new SusanooSkeletalEyesLayer(this));
    }
}
