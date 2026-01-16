package fr.asdepack.common.radio;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import fr.asdepack.common.capabilities.radios.PlayerRadioStorage;
import fr.asdepack.common.capabilities.radios.PlayerRadioStorageProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class RadioManager {
    private static RadioManager INSTANCE;

    public static RadioManager getInstance() {
        if (INSTANCE == null) INSTANCE = new RadioManager();
        return INSTANCE;
    }

    public RadioManager() {
    }

    public void onMicPacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) return;

        ServerPlayer sender = (ServerPlayer) senderConnection.getPlayer().getPlayer();
        ServerLevel level = sender.serverLevel();
        System.out.println("RadioManager received mic packet from " + sender.getUUID());

        sender.getCapability(PlayerRadioStorageProvider.PLAYER_RADIO_STORAGE_CAPABILITY).ifPresent(freq -> {
            if(freq instanceof PlayerRadioStorage radioStorage) {
                System.out.println("Received radio packet from " + sender.getUUID() + " on frequency " + (radioStorage.getFrequencyObj() != null ? radioStorage.getFrequencyObj().frequency : "null"));
                if(radioStorage.getFrequencyObj() == null) {
                    System.out.println("Radio packet discarded: no frequency set for player " + sender.getUUID());
                    return;
                }
                transmit(level, radioStorage.getFrequencyObj(), sender.getUUID(), sender.position(), event.getPacket().getOpusEncodedData());
                System.out.println("Transmitted radio packet from " + sender.getUUID() + " on frequency " + radioStorage.getFrequencyObj().frequency + " to " + radioStorage.getFrequencyObj().listeners.size() + " listeners.");
            }
        });
    }

    private void transmit(ServerLevel serverLevel, Frequency frequency, UUID sender, Vec3 senderLocation, byte[] opusEncodedData) {
        for (RadioChannel channel : frequency.listeners) {
            if (sender.equals(channel.owner)) {
                continue;
            }
            channel.transmit(sender, senderLocation, opusEncodedData);
        }
    }
}
