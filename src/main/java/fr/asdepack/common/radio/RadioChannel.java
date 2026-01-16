package fr.asdepack.common.radio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import fr.asdepack.Asdepack;
import fr.asdepack.AsdepackVCPlugin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RadioChannel implements Supplier<short[]> {
    public UUID owner;
    public AudioPlayer audioPlayer;
    private final Map<UUID, List<short[]>> packetBuffer;
    private final Map<UUID, OpusDecoder> decoders;
    private final HighPassAudioEffect effect;

    public static final Boolean IS_AUDIO_PLAYING_AROUND_ENTITY = false;

    public RadioChannel(Player owner) {
        this(owner.getUUID());
    }

    public RadioChannel(UUID owner) {
        this.owner = owner;

        packetBuffer = new HashMap<>();
        decoders = new HashMap<>();
        effect = new HighPassAudioEffect(1500, 48000);
    }

    @Override
    public short[] get() {
        short[] audio = generatePacket();
        if (audio == null) {
            if (audioPlayer != null) {
                audioPlayer.stopPlaying();
            }
            audioPlayer = null;
            return null;
        }
        return audio;
    }

    public short[] generatePacket() {
        List<short[]> packetsToCombine = new ArrayList<>();
        for (Map.Entry<UUID, List<short[]>> packets : packetBuffer.entrySet()) {
            if (packets.getValue().isEmpty()) {
                continue;
            }
            short[] audio = packets.getValue().remove(0);
            packetsToCombine.add(audio);
        }
        packetBuffer.values().removeIf(List::isEmpty);

        if (packetsToCombine.isEmpty()) {
            return null;
        }

        short[] combinedAudio = AsdepackVCPlugin.combineAudio(packetsToCombine);

        return effect.apply(combinedAudio);
    }

    public void transmit(UUID sender, Vec3 senderLocation, byte[] data) {
        List<short[]> microphonePackets = packetBuffer.computeIfAbsent(sender, k -> new ArrayList<>());

        if (microphonePackets.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                microphonePackets.add(null);
            }
        }

        OpusDecoder decoder = getDecoder(sender);
        if (data == null || data.length == 0) {
            decoder.resetState();
            return;
        }
        microphonePackets.add(decoder.decode(data));

        if (audioPlayer == null) {
            getAudioPlayer().startPlaying();
        }
    }

    private OpusDecoder getDecoder(UUID sender) {
        return decoders.computeIfAbsent(sender, uuid -> {
            assert AsdepackVCPlugin.serverApi != null;
            return AsdepackVCPlugin.serverApi.createDecoder();
        });
    }

    private AudioPlayer getAudioPlayer() {
        if (audioPlayer == null) {
            assert AsdepackVCPlugin.serverApi != null;
            VoicechatConnection connection = AsdepackVCPlugin.serverApi.getConnectionOf(owner);
            if(IS_AUDIO_PLAYING_AROUND_ENTITY) {
                assert connection != null;
                EntityAudioChannel channel = AsdepackVCPlugin.serverApi.createEntityAudioChannel(this.owner, connection.getPlayer());
                assert channel != null;
                channel.setDistance(8);
                audioPlayer = AsdepackVCPlugin.serverApi.createAudioPlayer(channel, AsdepackVCPlugin.serverApi.createEncoder(), this);
            } else {
                //static voice channel
                assert connection != null;
                StaticAudioChannel channel = AsdepackVCPlugin.serverApi.createStaticAudioChannel(this.owner, connection.getPlayer().getServerLevel(), connection);
                audioPlayer = AsdepackVCPlugin.serverApi.createAudioPlayer(channel, AsdepackVCPlugin.serverApi.createEncoder(), this);
            }
        }
        return audioPlayer;
    }
}