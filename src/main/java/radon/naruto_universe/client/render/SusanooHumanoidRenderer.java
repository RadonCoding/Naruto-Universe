package radon.naruto_universe.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.naruto_universe.client.layer.SusanooHumanoidEyesLayer;
import radon.naruto_universe.client.layer.SusanooSkeletalEyesLayer;
import radon.naruto_universe.entity.SusanooEntity;
import software.bernie.geckolib.model.GeoModel;

public class SusanooHumanoidRenderer extends SusanooStageRenderer{
    public SusanooHumanoidRenderer(EntityRendererProvider.Context renderManager, GeoModel<SusanooEntity> model) {
        super(renderManager, model);

        this.addRenderLayer(new SusanooHumanoidEyesLayer(this));
    }
}
