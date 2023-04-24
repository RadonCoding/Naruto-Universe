package radon.naruto_universe.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Quaternionf;

import java.security.SecureRandom;
import java.util.Optional;

public class HelperMethods {
    public static final SecureRandom RANDOM = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    public static HitResult getHitResult(Level level, Entity entity, Vec3 start, Vec3 end, double radius) {
        double d0 = Double.MAX_VALUE;
        Entity entityHit = null;

        for (Entity target : level.getEntities(entity, entity.getBoundingBox().expandTowards(end).inflate(radius))) {
            AABB box = target.getBoundingBox();
            Optional<Vec3> optional = box.clip(start, end);

            if (optional.isPresent()) {
                double d1 = start.distanceToSqr(optional.get());

                if (d1 < d0) {
                    entityHit = target;
                    d0 = d1;
                }
            }
        }

        if (entityHit != null) {
            return new EntityHitResult(entityHit);
        }

        BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return blockHit;
        }
        return null;
    }

    public static HitResult getHitResult(Entity entity, double range, double radius) {
        Vec3 look = entity.getLookAngle().normalize().scale(range);
        Vec3 start = new Vec3(entity.getX(), entity.getEyeY() - 0.2D, entity.getZ());
        Vec3 end = start.add(look);
        return getHitResult(entity.level, entity, start, end, radius);
    }

    public static EntityHitResult getLivingEntityLookAt(Entity entity, double range, double radius) {
        Vec3 start = entity.getEyePosition();
        Vec3 view = entity.getViewVector(1.0F);
        Vec3 end = start.add(view.scale(range));
        return ProjectileUtil.getEntityHitResult(entity.level, entity, start, end, entity.getBoundingBox().expandTowards(end).inflate(radius),
                target -> !target.isSpectator() && target.isPickable());
    }

    public static EntityHitResult getEntityEyesConnect(Entity entity, double range) {
        Vec3 sourceEyePos = new Vec3(entity.getX(), entity.getEyeY() - 0.2D, entity.getZ());
        Vec3 look = entity.getLookAngle().normalize().scale(range);
        Vec3 end = sourceEyePos.add(look);

        AABB box = entity.getBoundingBox().expandTowards(end).inflate(1.0F);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(entity.level, entity, sourceEyePos, end, box, target -> true);

        if (hit != null) {
            Entity target = hit.getEntity();
            Vec3 targetEyePos = new Vec3(target.getX(), target.getEyeY() - 0.2D, target.getZ());

            Vec3 targetToSource = sourceEyePos.subtract(targetEyePos).normalize();
            Vec3 targetLookVector = target.getLookAngle().normalize();

            if (targetLookVector.dot(targetToSource) > Math.cos(Math.toRadians(15))) {
                return hit;
            }
        }
        return null;
    }

    public static int toRGB24(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF));
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
