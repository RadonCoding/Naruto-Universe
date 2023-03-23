package radon.naruto_universe.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.network.packet.HandleHandSignC2SPacket;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.network.packet.TriggerAbilityPacket;
import radon.naruto_universe.network.packet.UnlockAbilityC2SPacket;

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
        INSTANCE.messageBuilder(TriggerAbilityPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TriggerAbilityPacket::new)
                .encoder(TriggerAbilityPacket::encode)
                .consumerMainThread(TriggerAbilityPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncNinjaPlayerS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncNinjaPlayerS2CPacket::new)
                .encoder(SyncNinjaPlayerS2CPacket::encode)
                .consumerMainThread(SyncNinjaPlayerS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(UnlockAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UnlockAbilityC2SPacket::new)
                .encoder(UnlockAbilityC2SPacket::encode)
                .consumerMainThread(UnlockAbilityC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
