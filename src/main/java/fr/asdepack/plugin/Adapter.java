package fr.asdepack.plugin;

import lombok.Getter;

import java.lang.reflect.Method;

public abstract class Adapter {

    @Getter
    protected boolean enabled = false;
    protected Object plugin;
    protected ClassLoader bukkitClassLoader;
    protected ClassLoader pluginClassLoader;
    @Getter
    private String name;

    protected Adapter() {
    }

    protected Adapter(String name) {
        this.name = name;
    }

    public void init() {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
            this.bukkitClassLoader = bukkitClass.getClassLoader();

            Method getPluginManager = bukkitClass.getMethod("getPluginManager");
            Object pluginManager = getPluginManager.invoke(null);

            Method getPlugin = pluginManager.getClass().getMethod("getPlugin", String.class);
            this.plugin = getPlugin.invoke(pluginManager, name);

            if (this.plugin != null) {
                this.pluginClassLoader = this.plugin.getClass().getClassLoader();
                this.enable();
            }
        } catch (Exception e) {
            System.err.printf("[Asdepack] Error initializing %s adapter: %s%n", name, e.getMessage());
        }
    }

    protected void enable() {
        System.out.printf("[Asdepack] %s adapter enabled.%n", name);
        enabled = true;
    }

    protected void disable() {
        System.out.printf("[Asdepack] %s adapter disabled.%n", name);
        enabled = false;
    }

    @Override
    public String toString() {
        return "Adapter{" +
                "enabled=" + enabled +
                ", name='" + name + '\'' +
                ", plugin=" + plugin +
                ", bukkitClassLoader=" + bukkitClassLoader +
                '}';
    }
}
