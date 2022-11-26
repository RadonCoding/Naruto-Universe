package dev.radon.naruto_universe.network;

import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.network.packet.HandleComboC2SPacket;
import dev.radon.naruto_universe.network.packet.HandleHandSignC2SPacket;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(NarutoUniverse.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true).simpleChannel();

        INSTANCE.messageBuilder(HandleHandSignC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(HandleHandSignC2SPacket::new)
                .encoder(HandleHandSignC2SPacket::encode)
                .consumerMainThread(HandleHandSignC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(HandleComboC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(HandleComboC2SPacket::new)
                .encoder(HandleComboC2SPacket::encode)
                .consumerMainThread(HandleComboC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncShinobiPlayerS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncShinobiPlayerS2CPacket::new)
                .encoder(SyncShinobiPlayerS2CPacket::encode)
                .consumerMainThread(SyncShinobiPlayerS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
