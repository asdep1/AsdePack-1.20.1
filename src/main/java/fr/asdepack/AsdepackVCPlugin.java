package fr.asdepack;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import fr.asdepack.common.radio.RadioManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@ForgeVoicechatPlugin
public class AsdepackVCPlugin implements VoicechatPlugin {

    public static Logger logger = Logger.getLogger(AsdepackVCPlugin.class.getName());

    @Nullable
    public static VoicechatServerApi serverApi;
    @Nullable
    public static VolumeCategory radios;

    private ExecutorService executor;

    public AsdepackVCPlugin() {
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("Asdepack Voicechat Radio Thread");
            thread.setUncaughtExceptionHandler((t, e) -> logger.severe("Uncaught exception in thread " + t.getName() + ": " + e.getMessage()));
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public String getPluginId() {
        return Asdepack.MODID + "-voicechat-plugin";
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();

        radios = serverApi.volumeCategoryBuilder()
                .setId("radios")
                .setName("Radios")
                .setDescription("The volume of radios")
                .setIcon(getIcon("radio_icon.png"))
                .build();

        serverApi.registerVolumeCategory(radios);
    }

    @Override
    public void registerEvents(EventRegistration reg) {
        reg.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        reg.registerEvent(MicrophonePacketEvent.class, microphonePacketEvent -> {
            executor.submit(() -> RadioManager.getInstance().onMicPacket(microphonePacketEvent));
        });
    }

    public static short[] combineAudio(List<short[]> audioParts) {
        short[] result = new short[960];
        int sample;
        for (int i = 0; i < result.length; i++) {
            sample = 0;
            for (short[] audio : audioParts) {
                if (audio == null) {
                    sample += 0;
                } else {
                    sample += audio[i];
                }
            }
            if (sample > Short.MAX_VALUE) {
                result[i] = Short.MAX_VALUE;
            } else if (sample < Short.MIN_VALUE) {
                result[i] = Short.MIN_VALUE;
            } else {
                result[i] = (short) sample;
            }
        }
        return result;
    }


    private int[][] getIcon(String path) {
        try {
            Enumeration<URL> resources = AsdepackVCPlugin.class.getClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                BufferedImage bufferedImage = ImageIO.read(resources.nextElement().openStream());
                if (bufferedImage.getWidth() != 16) {
                    continue;
                }
                if (bufferedImage.getHeight() != 16) {
                    continue;
                }
                int[][] image = new int[16][16];
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        image[x][y] = bufferedImage.getRGB(x, y);
                    }
                }
                return image;
            }

        } catch (Exception e) {
            logger.warning("Could not load icon " + path + ": " + e.getMessage());
        }
        return null;
    }
}
