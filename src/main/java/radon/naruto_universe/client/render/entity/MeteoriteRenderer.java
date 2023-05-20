package radon.naruto_universe.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.entity.MeteoriteEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class MeteoriteRenderer extends GeoEntityRenderer<MeteoriteEntity> {
    public MeteoriteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, null);
    }

    @Override
    public void render(@NotNull MeteoriteEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.getSize() > 0) {
            Minecraft mc = Minecraft.getInstance();

            assert mc.level != null;

            int radius = entity.getSize() / 2;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockRenderDispatcher renderer = mc.getBlockRenderer();

            int index = 0;

            poseStack.pushPose();
            poseStack.translate(-0.5D, (entity.getBbHeight() / 2.0) - 0.5D, -0.5D);

            List<Block> blocks = entity.getBlocks();

            ModelBlockRenderer.enableCaching();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        double distance = Math.sqrt(x * x + y * y + z * z);

                        if (distance < radius && distance >= radius - 1) {
                            pos.set(entity.blockPosition().offset(x, y, z));

                            Block block = blocks.get(index);
                            BlockState state = block.defaultBlockState();
                            BakedModel model = renderer.getBlockModel(state);
                            RandomSource rand = RandomSource.create();
                            rand.setSeed(state.getSeed(pos));

                            poseStack.pushPose();
                            poseStack.translate(x, y, z);

                            for (RenderType type : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
                                renderer.renderSingleBlock(state,
                                        poseStack,
                                        bufferSource,
                                        packedLight,
                                        OverlayTexture.NO_OVERLAY,
                                        ModelData.EMPTY,
                                        type);
                            }
                            poseStack.popPose();
                            index = (index + 1) % blocks.size();
                        }
                    }
                }
            }

            ModelBlockRenderer.clearCache();

            poseStack.popPose();
        }
    }
}