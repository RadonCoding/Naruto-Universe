package dev.radon.naruto_universe.item.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AnbuArmorItem extends ArmorItem {
    public AnbuArmorItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Item.Properties pProperties) {
        super(pMaterial, pSlot, pProperties);
    }
}
