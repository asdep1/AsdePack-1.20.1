package fr.asdepack.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.asdepack.Asdepack;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.common.network.packets.STestPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
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

//    @SubscribeEvent
//    public static void onKeyInput(InputEvent.Key event) {
//        if(Minecraft.getInstance().getConnection() != null) {
//            if(event.getAction() == InputConstants.PRESS) {
//                PacketHelper.sendToServer(new RadioTransmitPacket(true, RadioTransmitPacket.PacketContext.KEYBIND));
//            } else if(event.getAction() == InputConstants.RELEASE) {
//                PacketHelper.sendToServer(new RadioTransmitPacket(false, RadioTransmitPacket.PacketContext.KEYBIND));
//            }
//        }
//    }

}
