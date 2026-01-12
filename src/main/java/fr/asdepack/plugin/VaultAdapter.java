package fr.asdepack.plugin;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public class VaultAdapter {

    private Object economy;
    private ClassLoader bukkitClassLoader;

    public void init() {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
            bukkitClassLoader = bukkitClass.getClassLoader();

            Method getPluginManager = bukkitClass.getMethod("getPluginManager");
            Object pluginManager = getPluginManager.invoke(null);

            Method getPlugin = pluginManager.getClass().getMethod("getPlugin", String.class);
            economy = getPlugin.invoke(pluginManager, "Vault");

            initVault();

            if (economy != null) System.out.println("[Asdepack] Vault Economy lié.");

        } catch (Exception e) {
            System.err.println("[Asdepack] Erreur init adapter : " + e.getMessage());
        }
    }

    private void initVault() {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, bukkitClassLoader);
            Object servicesManager = bukkitClass.getMethod("getServicesManager").invoke(null);

            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy", true, economy.getClass().getClassLoader());
            Method getRegistration = servicesManager.getClass().getMethod("getRegistration", Class.class);
            Object registration = getRegistration.invoke(servicesManager, economyClass);

            if (registration != null) {
                Method getProvider = registration.getClass().getMethod("getProvider");
                economy = getProvider.invoke(registration);
            }
        } catch (Exception e) {
            System.err.println("[Asdepack] Vault non trouvé ou pas de plugin d'économie : " + e.getMessage());
        }
    }

    public double getBalance(Player player) {
        return this.getBalance(player.getUUID());
    }

    public double getBalance(UUID playerUUID) {
        if (economy == null) return 0.0;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, bukkitClassLoader);
            Method getOfflinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class);
            Object offlinePlayer = getOfflinePlayer.invoke(null, playerUUID);

            Method getBalanceMethod = economy.getClass().getMethod("getBalance", Class.forName("org.bukkit.OfflinePlayer", true, bukkitClassLoader));
            return (double) getBalanceMethod.invoke(economy, offlinePlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void deposit(Player player, double amount) {
        deposit(player.getUUID(), amount);
    }

    public void deposit(UUID playerUUID, double amount) {
        if (economy == null) return;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, bukkitClassLoader);
            Object offlinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class).invoke(null, playerUUID);

            Method depositMethod = economy.getClass().getMethod("depositPlayer",
                    Class.forName("org.bukkit.OfflinePlayer", true, bukkitClassLoader), double.class);
            depositMethod.invoke(economy, offlinePlayer, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void withdraw(Player player, double amount) {
        withdraw(player.getUUID(), amount);
    }

    public void withdraw(UUID playerUUID, double amount) {
        if (economy == null) return;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, bukkitClassLoader);
            Object offlinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class).invoke(null, playerUUID);

            Method withdrawMethod = economy.getClass().getMethod("withdrawPlayer",
                    Class.forName("org.bukkit.OfflinePlayer", true, bukkitClassLoader), double.class);
            withdrawMethod.invoke(economy, offlinePlayer, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}