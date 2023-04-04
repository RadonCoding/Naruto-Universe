package radon.naruto_universe.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class AkatsukiCloakModel extends HumanoidModel<LivingEntity> {
	public AkatsukiCloakModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition armor_head = head.addOrReplaceChild("armor_head", CubeListBuilder.create().texOffs(14, 15).addBox(-5.0F, -26.3F, -5.0F, 1.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(34, 4).addBox(-4.0F, -26.3F, -5.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(34, 0).addBox(-4.0F, -26.3F, 4.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 0).addBox(4.0F, -26.3F, -5.0F, 1.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -24.3F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition armor_body = body.addOrReplaceChild("armor_body", CubeListBuilder.create().texOffs(0, 9).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition armor_right_arm = right_arm.addOrReplaceChild("armor_right_arm", CubeListBuilder.create().texOffs(16, 28).addBox(-8.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition armor_left_arm = left_arm.addOrReplaceChild("armor_left_arm", CubeListBuilder.create().texOffs(0, 25).addBox(4.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition armor_right_leg = right_leg.addOrReplaceChild("armor_right_leg", CubeListBuilder.create().texOffs(32, 25).addBox(-4.0F, -11.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition armor_left_leg = left_leg.addOrReplaceChild("armor_left_leg", CubeListBuilder.create().texOffs(26, 13).addBox(0.0F, -11.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		this.leftLeg.visible = true;
		this.rightLeg.visible = true;
		this.head.visible = true;
		super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}
}