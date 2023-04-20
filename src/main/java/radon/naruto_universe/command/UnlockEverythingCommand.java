package radon.naruto_universe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;

public class UnlockEverythingCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("unlockeverything")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        unlockEverything(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(Commands.literal("unlockeverything").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int unlockEverything(ServerPlayer player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            for (RegistryObject<Ability> obj : NarutoAbilities.ABILITIES.getEntries()) {
                Ability ability = obj.get();

                if (!cap.hasUnlockedAbility(ability)) {
                    cap.unlockAbility(ability);
                }
            }
            PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
        });
        return 1;
    }
}
