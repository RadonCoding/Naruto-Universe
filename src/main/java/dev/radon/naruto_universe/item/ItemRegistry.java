package dev.radon.naruto_universe.item;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.item.armor.AnbuArmorItem;
import dev.radon.naruto_universe.item.armor.ModArmorMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<AnbuArmorItem> ANBU_CHEST = ITEMS.register("anbu_chestplate",
            () -> new AnbuArmorItem(ArmorTiers.ANBU, EquipmentSlot.CHEST, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<AnbuArmorItem> ANBU_LEGGINGS = ITEMS.register("anbu_leggings",
            () -> new AnbuArmorItem(ArmorTiers.ANBU, EquipmentSlot.LEGS, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<AnbuArmorItem> ANBU_BOOTS = ITEMS.register("anbu_boots",
            () -> new AnbuArmorItem(ArmorTiers.ANBU, EquipmentSlot.FEET, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<AnbuArmorItem> DOG_ANBU_MASK = ITEMS.register("dog_anbu_mask",
            () -> new AnbuArmorItem(ArmorTiers.DOG_ANBU_MASK, EquipmentSlot.HEAD, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));


    public static class ArmorTiers {
        public static final ArmorMaterial ANBU = new ModArmorMaterial("anbu", 37, new int[] { 3, 6, 8, 3 },
                15, SoundEvents.ARMOR_EQUIP_GENERIC, 3.0F, 0.1F,
                () -> Ingredient.of(Items.IRON_INGOT));

        public static final ArmorMaterial DOG_ANBU_MASK = new ModArmorMaterial("dog_anbu_mask", 37, new int[] { 3, 6, 8, 3 },
                15, SoundEvents.ARMOR_EQUIP_GENERIC, 3.0F, 0.1F,
                () -> Ingredient.of(Items.IRON_INGOT));

    }
}
