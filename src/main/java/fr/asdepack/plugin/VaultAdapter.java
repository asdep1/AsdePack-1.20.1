package fr.asdepack.plugin;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public class VaultAdapter extends Adapter {

    public VaultAdapter() {
        super("Vault");
    }

    @Override
    public void init() {
        try {
            super.init();
            initVault();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVault() {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, this.bukkitClassLoader);
            Object servicesManager = bukkitClass.getMethod("getServicesManager").invoke(null);

            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy", true, this.pluginClassLoader);
            Method getRegistration = servicesManager.getClass().getMethod("getRegistration", Class.class);
            Object registration = getRegistration.invoke(servicesManager, economyClass);

            if (registration != null) {
                Method getProvider = registration.getClass().getMethod("getProvider");
                this.plugin = getProvider.invoke(registration);
            }
        } catch (Exception e) {
            System.err.println("[Asdepack] Vault non trouvé ou pas de plugin d'économie : " + e.getMessage());
        }
    }

    public double getBalance(Player player) {
        return this.getBalance(player.getUUID());
    }

    public double getBalance(UUID playerUUID) {
        if (!isEnabled()) return 0.0;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, this.bukkitClassLoader);
            Method getOfflinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class);
            Object offlinePlayer = getOfflinePlayer.invoke(null, playerUUID);

            Method getBalanceMethod = this.plugin.getClass().getMethod("getBalance", Class.forName("org.bukkit.OfflinePlayer", true, this.bukkitClassLoader));
            return (double) getBalanceMethod.invoke(this.plugin, offlinePlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void deposit(Player player, double amount) {
        deposit(player.getUUID(), amount);
    }

    public void deposit(UUID playerUUID, double amount) {
        if (!isEnabled()) return;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, this.bukkitClassLoader);
            Object offlinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class).invoke(null, playerUUID);

            Method depositMethod = this.plugin.getClass().getMethod("depositPlayer",
                    Class.forName("org.bukkit.OfflinePlayer", true, this.bukkitClassLoader), double.class);
            depositMethod.invoke(this.plugin, offlinePlayer, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void withdraw(Player player, double amount) {
        withdraw(player.getUUID(), amount);
    }

    public void withdraw(UUID playerUUID, double amount) {
        if (!isEnabled()) return;
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit", true, this.bukkitClassLoader);
            Object offlinePlayer = bukkitClass.getMethod("getOfflinePlayer", UUID.class).invoke(null, playerUUID);

            Method withdrawMethod = this.plugin.getClass().getMethod("withdrawPlayer",
                    Class.forName("org.bukkit.OfflinePlayer", true, this.bukkitClassLoader), double.class);
            withdrawMethod.invoke(this.plugin, offlinePlayer, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}