package radon.naruto_universe.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.entity.BlockAppearanceEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlockAppearanceRenderer extends GeoEntityRenderer<BlockAppearanceEntity> {
    public BlockAppearanceRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, null);
    }

    @Override
    public void render(@NotNull BlockAppearanceEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        BlockState state = entity.getBlockState();

        if (state != null) {
            BlockRenderDispatcher renderer = mc.getBlockRenderer();

            BakedModel model = renderer.getBlockModel(state);
            RandomSource rand = RandomSource.create();
            rand.setSeed(state.getSeed(entity.blockPosition()));

            poseStack.pushPose();
            poseStack.translate(-0.5F, 0.0F, -0.5F);

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
        }
    }
}
