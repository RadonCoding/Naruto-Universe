package radon.naruto_universe.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.naruto_universe.entity.SusanooEntity;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void wantsToStopRiding(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = ((LivingEntity) (Object) this).getVehicle();

        if (entity instanceof SusanooEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
