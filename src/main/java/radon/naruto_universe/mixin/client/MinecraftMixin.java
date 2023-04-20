package radon.naruto_universe.mixin.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.naruto_universe.client.NarutoPostEffect;
import radon.naruto_universe.client.effects.NarutoPostEffects;

@Mixin(GameRenderer.class)
public class MinecraftMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V", shift = At.Shift.BEFORE))
    private void afterRenderPostEffects(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            Window window = mc.getWindow();

            for (NarutoPostEffect effect : NarutoPostEffects.EFFECTS) {
                if (effect.shouldRender(mc.player)) {
                    effect.resize(window.getWidth(), window.getHeight());
                    effect.render(mc.getFrameTime());
                }
            }
        }
    }
}