package radon.naruto_universe.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.client.ability.SpecialAbilityHandler;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MouseHandler {
    private static double accumulatedScroll;

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();

        double delta = event.getScrollDelta();

        if (accumulatedScroll != 0.0D && Math.signum(delta) != Math.signum(accumulatedScroll)) {
            accumulatedScroll = 0.0D;
        }

        accumulatedScroll += delta;

        int i = (int) accumulatedScroll;

        if (i == 0) {
            return;
        }

        accumulatedScroll -= i;

        if (mc.options.keyShift.isDown()) {
            if (SpecialAbilityHandler.scroll(i)) {
                event.setCanceled(true);
            }
        }
    }
}
