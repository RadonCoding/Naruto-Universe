package radon.naruto_universe.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.ThrownKunaiEntity;
import software.bernie.geckolib.model.GeoModel;

public class ThrownKunaiModel extends GeoModel<ThrownKunaiEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/entity/thrown_kunai.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/thrown_kunai.png");

    @Override
    public ResourceLocation getModelResource(ThrownKunaiEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ThrownKunaiEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ThrownKunaiEntity animatable) {
        return null;
    }
}
