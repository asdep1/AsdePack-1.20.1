package fr.asdepack.server.bridges;

import fr.asdepack.Asdepack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldGuardAdapter extends Adapter {

    public WorldGuardAdapter() {
        super("WorldGuard");
    }

    public List<ProtectedRegion> getApplicableRegions(String worldName, Vec3 location) {
        List<ProtectedRegion> regions = new ArrayList<>();
        if (!isEnabled()) return regions;

        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, this.bukkitClassLoader);
            Method getWorldMethod = bukkitClass.getMethod("getWorld", String.class);
            Object bukkitWorld = getWorldMethod.invoke(null, worldName);
            if (bukkitWorld == null) return regions;

            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter", true, this.pluginClassLoader);
            Method adaptWorld = bukkitAdapterClass.getMethod("adapt", Class.forName("org.bukkit.World", true, this.bukkitClassLoader));
            Object wgWorld = adaptWorld.invoke(null, bukkitWorld);

            Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard", true, this.pluginClassLoader);
            Object wgInstance = wgClass.getMethod("getInstance").invoke(null);
            Object platform = wgInstance.getClass().getMethod("getPlatform").invoke(wgInstance);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);

            Method getManager = container.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World", true, this.pluginClassLoader));
            Object regionManager = getManager.invoke(container, wgWorld);

            if (regionManager != null) {
                Class<?> bv3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3", true, this.pluginClassLoader);
                Method at = bv3Class.getMethod("at", double.class, double.class, double.class);
                Object vector = at.invoke(null, location.x, location.y, location.z);

                Method getApplicable = regionManager.getClass().getMethod("getApplicableRegions", bv3Class);
                Object applicableSet = getApplicable.invoke(regionManager, vector);

                Method getRegions = applicableSet.getClass().getMethod("getRegions");
                Set<?> set = (Set<?>) getRegions.invoke(applicableSet);

                for (Object pr : set) {
                    String id = (String) pr.getClass().getMethod("getId").invoke(pr);
                    Object min = pr.getClass().getMethod("getMinimumPoint").invoke(pr);
                    Object max = pr.getClass().getMethod("getMaximumPoint").invoke(pr);

                    regions.add(new ProtectedRegion(id, toBlockPos(min), toBlockPos(max)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regions;
    }

    public boolean isPlayerInRegion(Player player, String region) {
        if (!isEnabled()) return false;
        String worldName = player.level().dimension().location().getPath();
        if (worldName.equals("overworld")) worldName = "world";

        List<ProtectedRegion> regions = Asdepack.WG_ADAPTER.getApplicableRegions(worldName, player.position());
        for (ProtectedRegion r : regions) {
            if (r.getId().equals(region)) return true;
        }
        return false;
    }

    private BlockPos toBlockPos(Object bv3) throws Exception {
        int x = (int) bv3.getClass().getMethod("getX").invoke(bv3);
        int y = (int) bv3.getClass().getMethod("getY").invoke(bv3);
        int z = (int) bv3.getClass().getMethod("getZ").invoke(bv3);
        return new BlockPos(x, y, z);
    }
}