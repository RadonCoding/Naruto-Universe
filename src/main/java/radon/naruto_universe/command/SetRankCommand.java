package radon.naruto_universe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;

public class SetRankCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("setrank")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("rank", EnumArgument.enumArgument(NinjaRank.class)).executes((ctx) ->
                        setPlayerRank(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("rank", NinjaRank.class))))));

        dispatcher.register(Commands.literal("setrank").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setPlayerRank(ServerPlayer player, NinjaRank rank) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            cap.setRank(rank);
            PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
        });
        return 1;
    }
}
