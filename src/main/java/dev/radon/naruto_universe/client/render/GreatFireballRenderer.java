package dev.radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.radon.naruto_universe.entity.GreatFireballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class GreatFireballRenderer extends EntityRenderer<GreatFireballEntity> {
    private static final List<Vec3> SPHERE_POINTS = Lists.newArrayList();

    static {
        double radius = 2.0D;

        for (double i = -2.0D * Math.PI; i < 2.0D * (Math.PI / 2); i += 0.1D) {
            for (double j = -Math.PI; j < Math.PI / 2; j += 0.1D) {
                double x = Math.cos(i) * Math.sin(j) * radius;
                double y = Math.sin(i) * Math.sin(j) * radius;
                double z = Math.cos(j) * radius;
                SPHERE_POINTS.add(new Vec3(x, y, z));
            }
        }
    }

    public GreatFireballRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(GreatFireballEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        for (Vec3 point : SPHERE_POINTS) {
            Vec3 movement = pEntity.getDeltaMovement();
            Vec3 pos = point.add(pEntity.getX(), pEntity.getY() + 1.0D, pEntity.getZ());
            pEntity.level.addParticle(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), movement.x(), movement.y(), movement.z());
        }
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GreatFireballEntity pEntity) {
        return null;
    }
}
