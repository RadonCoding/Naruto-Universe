package radon.naruto_universe.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.ChibakuTenseiEntity;
import radon.naruto_universe.entity.FireballProjectile;
import software.bernie.geckolib.model.GeoModel;

public class ChibakuTenseiModel extends GeoModel<ChibakuTenseiEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/chibaku_tensei.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/chibaku_tensei.png");

    @Override
    public ResourceLocation getModelResource(ChibakuTenseiEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ChibakuTenseiEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ChibakuTenseiEntity animatable) {
        return null;
    }
}