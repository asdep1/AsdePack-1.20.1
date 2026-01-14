package fr.asdepack.client;

import fr.asdepack.Asdepack;
import fr.asdepack.network.PacketHelper;
import fr.asdepack.network.packets.STestPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if(Keybindings.INSTANCE.demoKeybind.consumeClick() && minecraft.player != null) {
            PacketHelper.sendToServer(new STestPacket());
        }
    }
}
