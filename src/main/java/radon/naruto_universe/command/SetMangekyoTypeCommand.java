package radon.naruto_universe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;
import radon.naruto_universe.capability.ninja.MangekyoType;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;

public class SetMangekyoTypeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("setmangekyotype")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("mangekyotype", EnumArgument.enumArgument(MangekyoType.class)).executes((ctx) ->
                        setMangekyoType(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("mangekyotype", MangekyoType.class))))));

        dispatcher.register(Commands.literal("setmangekyotype").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setMangekyoType(ServerPlayer player, MangekyoType type) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            cap.setMangekyoType(type);
            PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
        });
        return 1;
    }
}
