package radon.naruto_universe.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;

import java.security.SecureRandom;

public class HelperMethods {
    private static final SecureRandom random = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){
        int x = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static Quaternionf getQuaternion(float x, float y, float z, float w) {
        w *= (float)Math.PI / 180;
        float f = (float) Math.sin(w / 2.0F);
        float i = x * f;
        float j = y * f;
        float k = z * f;
        float r = (float) Math.cos(w / 2.0F);
        return new Quaternionf(i, j, k, r);
    }

    public static void rotateQ(float w, float x, float y, float z, PoseStack matrix) {
        matrix.mulPose(getQuaternion(x, y, z, w));
    }
}
