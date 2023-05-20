package radon.naruto_universe.client;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CameraShakeHandler {
    private static ShakeEvent current;

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (current == null) return;

        float time = (float) (event.getCamera().getEntity().tickCount + event.getPartialTick());
        float shakeX = Mth.cos(time * current.speed) * current.intensity;
        float shakeY = Mth.sin(time * current.speed) * current.intensity;
        event.setPitch(event.getPitch() + shakeX);
        event.setYaw(event.getYaw() + shakeY);

        current.duration--;

        if (current.duration <= 0) {
            current = null;
        }
    }

    public static void shakeCamera(float intensity, float speed, int duration) {
        current = new ShakeEvent(intensity, speed, duration);
    }

    public static class ShakeEvent {
        public float intensity;
        public float speed;
        public int duration;

        public ShakeEvent(float intensity, float speed, int duration) {
            this.intensity = intensity;
            this.speed = speed;
            this.duration = duration;
        }
    }
}

