package radon.naruto_universe.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.item.armor.AkatsukiCloakItem;
import software.bernie.geckolib.model.GeoModel;

public class AkatsukiCloakModel extends GeoModel<AkatsukiCloakItem> {
    private static final ResourceLocation MODEL = new ResourceLocation(NarutoUniverse.MOD_ID, "geo/item/armor/akatsuki_cloak.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/armor/akatsuki_cloak.png");

    @Override
    public ResourceLocation getModelResource(AkatsukiCloakItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AkatsukiCloakItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AkatsukiCloakItem animatable) {
        return null;
    }
}