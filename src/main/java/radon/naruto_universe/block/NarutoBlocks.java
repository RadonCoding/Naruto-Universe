package radon.naruto_universe.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

public class NarutoBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NarutoUniverse.MOD_ID);

    public static final RegistryObject<Block> AMATERASU = BLOCKS.register("amaterasu", () -> new AmaterasuBlock(BlockBehaviour.Properties.of(NarutoMaterials.AMATERASU, MaterialColor.FIRE)
            .noCollission().strength(100.0F).lightLevel((state) -> 15).randomTicks().sound(SoundType.WOOL)));
}
