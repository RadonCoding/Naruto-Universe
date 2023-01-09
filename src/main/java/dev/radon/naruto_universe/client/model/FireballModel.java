package dev.radon.naruto_universe.client.model;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.entity.FireballEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FireballModel extends AnimatedGeoModel<FireballEntity> {
	public static final ResourceLocation MODEL_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/fireball.geo.json");
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/models/entity/fireball.png");
	public static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "animations/fireball.animation.json");

	@Override
	public ResourceLocation getModelResource(FireballEntity object) {
		return MODEL_LOCATION;
	}

	@Override
	public ResourceLocation getTextureResource(FireballEntity object) {
		return TEXTURE_LOCATION;
	}

	@Override
	public ResourceLocation getAnimationResource(FireballEntity animatable) {
		return ANIMATION_LOCATION;
	}
}