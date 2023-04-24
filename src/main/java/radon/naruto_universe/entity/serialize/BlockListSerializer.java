package radon.naruto_universe.entity.serialize;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockListSerializer implements EntityDataSerializer<List<Block>>{
    @Override
    public void write(FriendlyByteBuf buf, List<Block> blocks) {
        buf.writeVarInt(blocks.size());

        for (Block block : blocks) {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);

            if (key != null) {
                buf.writeResourceLocation(key);
            }
        }
    }

    @Override
    public @NotNull List<Block> read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Block> blocks = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ResourceLocation key = buf.readResourceLocation();
            Block block = ForgeRegistries.BLOCKS.getValue(key);

            if (block != null) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    @Override
    public @NotNull List<Block> copy(@NotNull List<Block> blocks) {
        return new ArrayList<>(blocks);
    }
}
