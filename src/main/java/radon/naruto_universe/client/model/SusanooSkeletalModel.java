package radon.naruto_universe.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.NarutoUniverse;

public class SusanooSkeletalModel<T extends LivingEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(NarutoUniverse.MOD_ID, "susanooo_skeletal"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart left_arm;

	public SusanooSkeletalModel(ModelPart root) {
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.right_arm = this.body.getChild("right_arm");
		this.left_arm = this.body.getChild("left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -52.0F, -8.0F, 32.0F, 36.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(84, 40).addBox(-12.0F, -16.0F, -6.0F, 24.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(88, 120).addBox(-18.74F, -0.2952F, -8.3694F, 6.0F, 26.0F, 8.0F, new CubeDeformation(-0.01F)), PartPose.offset(-11.0F, -41.0F, 3.0F));

		PartDefinition cube_r1 = right_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(96, 94).addBox(-22.75F, -16.75F, -18.0F, 14.0F, 14.0F, 12.0F, new CubeDeformation(-0.02F))
		.texOffs(36, 94).addBox(-18.75F, -12.75F, -6.0F, 6.0F, 6.0F, 24.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 40.0021F, -13.2018F, 0.48F, 0.0F, 0.0F));

		PartDefinition cube_r2 = right_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(120, 60).addBox(-14.0F, -25.0F, 5.0F, 18.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.105F, 15.9968F, -13.3594F, 0.0F, 0.0F, -0.2618F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 118).addBox(12.74F, 8.7048F, -3.3694F, 6.0F, 26.0F, 8.0F, new CubeDeformation(-0.01F)), PartPose.offset(11.0F, -50.0F, -1.0F));

		PartDefinition cube_r3 = left_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(96, 0).addBox(8.75F, -16.75F, -18.0F, 14.0F, 14.0F, 12.0F, new CubeDeformation(-0.02F))
		.texOffs(0, 88).addBox(12.75F, -12.75F, -6.0F, 6.0F, 6.0F, 24.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 49.0021F, -8.2018F, 0.48F, 0.0F, 0.0F));

		PartDefinition cube_r4 = left_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(96, 26).addBox(-4.0F, -25.0F, 5.0F, 18.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.105F, 24.9968F, -8.3594F, 0.0F, 0.0F, 0.2618F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 52).addBox(-11.0F, -16.0F, -20.0F, 22.0F, 16.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -52.0F, 8.0F));

		PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(66, 70).addBox(-15.5F, -2.0F, -3.5F, 18.0F, 6.0F, 18.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(6.51F, 0.8484F, -16.0731F, 0.3054F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);

		this.body.yRot = 0.0F;
		this.right_arm.z = 3.0F;
		this.right_arm.x = -11.0F;
		this.left_arm.z = -1.0F;
		this.left_arm.x = 11.0F;
		float f = 1.0F;

		this.right_arm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F / f;
		this.left_arm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F / f;
		this.right_arm.zRot = 0.0F;
		this.left_arm.zRot = 0.0F;
		this.right_arm.yRot = 0.0F;
		this.left_arm.yRot = 0.0F;

		this.setupAttackAnimation(pEntity);
	}

	protected void setupAttackAnimation(T pLivingEntity) {
		if (!(this.attackTime <= 0.0F)) {
			HumanoidArm humanoidarm = this.getAttackArm(pLivingEntity);
			ModelPart modelpart = this.getArm(humanoidarm);
			float f = this.attackTime;
			this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;

			if (humanoidarm == HumanoidArm.LEFT) {
				this.body.yRot *= -1.0F;
			}

			this.right_arm.z = Mth.sin(this.body.yRot) * 5.0F;
			this.right_arm.x = -Mth.cos(this.body.yRot) * 5.0F;
			this.left_arm.z = -Mth.sin(this.body.yRot) * 5.0F;
			this.left_arm.x = Mth.cos(this.body.yRot) * 5.0F;
			this.right_arm.yRot += this.body.yRot;
			this.left_arm.yRot += this.body.yRot;
			this.left_arm.xRot += this.body.yRot;
			f = 1.0F - this.attackTime;
			f *= f;
			f *= f;
			f = 1.0F - f;
			float f1 = Mth.sin(f * (float)Math.PI);
			float f2 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
			modelpart.xRot -= f1 * 1.2F + f2;
			modelpart.yRot += this.body.yRot * 2.0F;
			modelpart.zRot += Mth.sin(this.attackTime * (float)Math.PI) * -0.4F;
		}
	}

	private ModelPart getArm(HumanoidArm pSide) {
		return pSide == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
	}

	private HumanoidArm getAttackArm(T pEntity) {
		HumanoidArm humanoidarm = pEntity.getMainArm();
		return pEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}