package radon.naruto_universe.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.naruto_universe.client.model.ChibakuTenseiModel;
import radon.naruto_universe.entity.ChibakuTenseiEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChibakuTenseiRenderer extends GeoEntityRenderer<ChibakuTenseiEntity> {
    public ChibakuTenseiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChibakuTenseiModel());
    }
}
