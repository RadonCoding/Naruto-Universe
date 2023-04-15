package radon.naruto_universe.ability.special;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.ToggledEyes;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.GenjutsuS2CPacket;
import radon.naruto_universe.util.HelperMethods;

public class Genjutsu extends Ability {
    private static final double RANGE = 30.0D;

    @Override
    public NinjaRank getRank() {
        return NinjaRank.GENIN;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return null;
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);

        if (hit == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public void runServer(LivingEntity owner) {
        //EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);
        //Entity target = hit.getEntity();

        //if (target instanceof Player player) {
        //    int duration = 20 * 20; // TEMPORARY
        //    PacketHandler.sendToClient(new GenjutsuS2CPacket(owner.getId(), duration), (ServerPlayer) player);
        //}

        int duration = 20 * 20;
        PacketHandler.sendToClient(new GenjutsuS2CPacket(owner.getId(), duration), (ServerPlayer) owner);
    }
}
