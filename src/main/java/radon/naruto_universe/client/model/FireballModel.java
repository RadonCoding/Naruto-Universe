package radon.naruto_universe.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.FireballProjectile;
import software.bernie.geckolib.model.GeoModel;

public class FireballModel extends GeoModel<FireballProjectile> {
    private static final ResourceLocation MODEL = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/fireball.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/fireball.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(NarutoUniverse.MOD_ID, "animations/entity/fireball.animation.json");

    @Override
    public ResourceLocation getModelResource(FireballProjectile animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FireballProjectile animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FireballProjectile animatable) {
        return ANIMATION;
    }
}