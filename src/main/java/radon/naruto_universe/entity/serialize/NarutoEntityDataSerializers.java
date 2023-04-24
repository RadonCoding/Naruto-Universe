package radon.naruto_universe.entity.serialize;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

import java.util.List;

public class NarutoEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<List<Block>>> BLOCK_LIST = SERIALIZERS.register("block_list", BlockListSerializer::new);
}
