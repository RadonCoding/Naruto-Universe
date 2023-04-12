package radon.naruto_universe.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.BlindessHandler;

@Mixin(GameRenderer.class)
public class MinecraftMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V", shift = At.Shift.BEFORE))
    private void afterRenderPostEffects(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasUnlockedAbility(NarutoAbilities.MANGEKYO.get())) {
                    BlindessHandler.INSTANCE.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
                    BlindessHandler.INSTANCE.render(mc.getFrameTime(), cap.getMangekyoBlindess());
                }
            });
        }
    }
}