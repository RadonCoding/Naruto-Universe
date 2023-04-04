package radon.naruto_universe.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.item.armor.AkatsukiCloakItem;
import radon.naruto_universe.item.armor.ModArmorMaterial;

public class NarutoItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<KunaiItem> KUNAI = ITEMS.register("kunai",
            () -> new KunaiItem(new Item.Properties().durability(250)));
    public static final RegistryObject<ArmorItem> AKATSUKI_CLOAK = ITEMS.register("akatsuki_cloak",
            () -> new AkatsukiCloakItem(ModArmorMaterials.AKATSUKI, EquipmentSlot.CHEST, new Item.Properties()));

    public static class ModArmorMaterials {
        public static final ModArmorMaterial AKATSUKI = new ModArmorMaterial("akatsuki", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE,
                3.0F, 0.1F, () -> Ingredient.of(Items.NETHERITE_INGOT));
    }
}
