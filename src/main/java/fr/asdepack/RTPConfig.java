package fr.asdepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class RTPConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FMLPaths.CONFIGDIR.get().resolve("tacznpcs_positions.json");

    public static Vec3 spawn = null;
    public static int spawnTimer = 15;
    public static int rtpTimer = 3;
    public static List<Vec3> positions = new ArrayList<>();

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }

        try {
            String json = Files.readString(FILE);
            Data data = GSON.fromJson(json, Data.class);

            if (data != null) {
                spawn = data.spawn;
                spawnTimer = data.spawnTimer;
                rtpTimer = data.rtpTimer;
                positions = data != null && data.positions != null
                        ? data.positions
                        : new ArrayList<>();

            }

        } catch (IOException e) {
            e.printStackTrace();
            spawn = null;
            positions = new ArrayList<>();
        }
    }

    public static void save() {
        try {
            Data data = new Data();
            data.spawn = spawn;
            data.spawnTimer = spawnTimer;
            data.rtpTimer = rtpTimer;
            data.positions = positions;
            Files.writeString(FILE, GSON.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(Vec3 pos) {
        positions.add(pos);
        save();
    }

    public static void remove(Vec3 pos) {
        positions.remove(pos);
        save();
    }

    public static void setSpawn(Vec3 pos) {
        spawn = pos;
        save();
    }

    public static boolean hasSpawn() {
        return spawn != null;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        load();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        load();
    }

    private static class Data {
        Vec3 spawn;
        int spawnTimer;
        int rtpTimer;
        List<Vec3> positions = new ArrayList<>();
    }
}
