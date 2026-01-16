package fr.asdepack.common.network;

import fr.asdepack.Asdepack;
import fr.asdepack.common.network.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHelper {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                ResourceLocation.fromNamespaceAndPath(Asdepack.MODID, "main"))
            .serverAcceptedVersions((status) -> true)
            .clientAcceptedVersions((status) -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(STestPacket.class, 1)
                .encoder(STestPacket::encode)
                .decoder(STestPacket::new)
                .consumerMainThread(STestPacket::handle)
                .add();

        INSTANCE.messageBuilder(CSyncKitStorage.class, 2)
                .encoder(CSyncKitStorage::encode)
                .decoder(CSyncKitStorage::new)
                .consumerMainThread(CSyncKitStorage::handle)
                .add();

        INSTANCE.messageBuilder(SRequestKitPacket.class, 3)
                .encoder(SRequestKitPacket::encode)
                .decoder(SRequestKitPacket::new)
                .consumerMainThread(SRequestKitPacket::handle)
                .add();

        INSTANCE.messageBuilder(CSyncRegion.class, 4)
                .encoder(CSyncRegion::encode)
                .decoder(CSyncRegion::new)
                .consumerMainThread(CSyncRegion::handle)
                .add();

        INSTANCE.messageBuilder(SPacketTransmitPacket.class, 5)
                .encoder(SPacketTransmitPacket::encode)
                .decoder(SPacketTransmitPacket::new)
                .consumerMainThread(SPacketTransmitPacket::handle)
                .add();

        INSTANCE.messageBuilder(SPacketChangeRadioFreq.class, 6)
                .encoder(SPacketChangeRadioFreq::encode)
                .decoder(SPacketChangeRadioFreq::new)
                .consumerMainThread(SPacketChangeRadioFreq::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
