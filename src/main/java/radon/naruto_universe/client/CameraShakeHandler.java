package radon.naruto_universe.client;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import java.util.concurrent.LinkedBlockingQueue;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CameraShakeHandler {
    private static final LinkedBlockingQueue<ShakeEvent> shakes = new LinkedBlockingQueue<>();

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakes.isEmpty()) return;

        ShakeEvent shake = shakes.peek();

        float time = (float) (event.getCamera().getEntity().tickCount + event.getPartialTick());
        float shakeX = Mth.cos(time * shake.speed) * shake.intensity;
        float shakeY = Mth.sin(time * shake.speed) * shake.intensity;
        event.setPitch(event.getPitch() + shakeX);
        event.setYaw(event.getYaw() + shakeY);

        shake.duration--;

        if (shake.duration <= 0) {
            shakes.remove();
        }
    }

    public static void shakeCamera(float intensity, float speed, int duration) {
        shakes.add(new ShakeEvent(intensity, speed, duration));
    }

    public static class ShakeEvent {
        public float intensity;
        public float speed;
        public int duration;

        public ShakeEvent(float intensity, float speed, int duration) {
            this.intensity = intensity;
            this.speed = speed;
            this.duration = duration * 10;
        }
    }
}

