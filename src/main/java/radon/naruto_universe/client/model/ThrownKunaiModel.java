package radon.naruto_universe.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import radon.naruto_universe.NarutoUniverse;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ThrownKunaiModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(NarutoUniverse.MOD_ID, "thrown_kunai"), "main");
	private final ModelPart bb_main;

	public ThrownKunaiModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-14.5F, -13.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(11, 6).addBox(-14.5F, -14.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 9).addBox(-13.5F, -13.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 13).addBox(-13.5F, -12.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 10).addBox(-11.5F, -12.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 12).addBox(-12.5F, -12.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-11.5F, -10.5F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 5).addBox(-11.5F, -11.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 7).addBox(-10.5F, -10.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.5F, -9.5F, -0.5F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-9.5F, -5.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 2).addBox(-5.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(11, 8).addBox(-5.5F, -5.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(13, 12).addBox(-4.5F, -4.5F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 17).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 15).addBox(-3.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(13, 4).addBox(-2.5F, -3.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 0).addBox(-2.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}