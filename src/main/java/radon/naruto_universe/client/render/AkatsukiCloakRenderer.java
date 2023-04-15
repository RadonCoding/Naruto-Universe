package radon.naruto_universe.client.render;

import net.minecraft.world.entity.EquipmentSlot;
import radon.naruto_universe.client.model.AkatsukiCloakModel;
import radon.naruto_universe.item.armor.AkatsukiCloakItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AkatsukiCloakRenderer extends GeoArmorRenderer<AkatsukiCloakItem> {
    public AkatsukiCloakRenderer() {
        super(new AkatsukiCloakModel());
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        this.setBoneVisible(this.head, true);
        this.setBoneVisible(this.body, true);
        this.setBoneVisible(this.rightArm, true);
        this.setBoneVisible(this.leftArm, true);
        this.setBoneVisible(this.rightLeg, true);
        this.setBoneVisible(this.leftLeg, true);
    }
}
