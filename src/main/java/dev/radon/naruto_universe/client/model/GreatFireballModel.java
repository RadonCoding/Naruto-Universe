package dev.radon.naruto_universe.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.entity.GreatFireballEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.checkerframework.checker.units.qual.A;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GreatFireballModel extends AnimatedGeoModel {
	public static final ResourceLocation MODEL_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/great_fireball.geo.json");
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/models/entity/great_fireball.png");
	public static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "animations/great_fireball.animation.json");

	@Override
	public ResourceLocation getModelResource(Object object) {
		return MODEL_LOCATION;
	}

	@Override
	public ResourceLocation getTextureResource(Object object) {
		return TEXTURE_LOCATION;
	}

	@Override
	public ResourceLocation getAnimationResource(Object animatable) {
		return ANIMATION_LOCATION;
	}
}